/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  Kuikly,
  IKuiklyViewDelegate,
  KRNativeRenderController,
  KRRenderViewExportCreator,
  KRRenderModuleExportCreator,
  KRRecord,
  KuiklyRenderBaseModule,
  KRAny,
  KuiklyRenderCallback
} from '@kuikly-open/render';
import { a2uiNativeManager } from './A2UINativeManager';
import { promptAction, display } from '@kit.ArkUI';
import { common, Want } from '@kit.AbilityKit';

/**
 * Global registry mapping instanceId → contentHeight callback.
 * This is needed because KRNativeInstance.moduleCreatorRegistry is global (static),
 * so all A2UISurfaceView instances share the same A2UIBridgeModule creator.
 * When multiple A2UISurfaceView instances exist, the Module uses its instanceId
 * (set via init()) to look up the correct callback for each instance.
 */
const a2uiHeightCallbackRegistry = new Map<string, (height: number) => void>();

/**
 * Global registry mapping instanceId → action callback.
 * Used to dispatch action events from A2UIBridgeModule to the correct A2UISurfaceView instance.
 */
const a2uiActionCallbackRegistry = new Map<string, (tag: number, action: string) => void>();

/**
 * Global registry mapping instanceId → picker callback.
 * Used to dispatch showDateTimePicker requests from A2UIBridgeModule to the correct
 * A2UISurfaceViewController instance, which then shows the native picker dialog.
 */
const a2uiPickerCallbackRegistry = new Map<string, (configJson: string) => void>();

/**
 * Handler type for a custom A2UI function.
 *
 * - `args`  : Resolved argument object from the A2UI stream.
 * - returns : One of:
 *     - string   for return type "string"
 *     - boolean  for "boolean"
 *     - number   for "number"
 *     - array    for "array" (also accepted as a JSON string)
 *     - anything for "void"
 *
 * Threading:
 *   * SYNC handlers run on the Kuikly Context Thread. Keep the work brief.
 *   * ASYNC handlers (void only) run on the ArkTS main event loop and their
 *     return value is ignored.
 */
export type A2UICustomFunctionHandler = (args: Record<string, Object>) => Object | null;

/**
 * Global handler table. Registrations are process-wide and survive across
 * A2UISurfaceView instances. See [registerA2UICustomFunction] below.
 */
const a2uiCustomFunctionHandlers = new Map<string, A2UICustomFunctionHandler>();

/**
 * Global descriptor table. Keyed by function name → "returnType|invokeMode".
 * Used to replay registrations to the Kotlin core when a new surface boots up.
 */
const a2uiCustomFunctionDescriptors = new Map<string, string>();

// region Custom Render View Registration

/**
 * Global custom render view creator table.
 * Keyed by viewName → creator function.
 */
const a2uiCustomViewCreators = new Map<string, KRRenderViewExportCreator>();

/**
 * Register a custom render view **globally**. Once registered, the view is
 * available to every A2UISurfaceView instance in this process, both existing
 * and future ones.
 *
 * This API is aligned with the Kuikly DSL custom component registration flow.
 * The same KuiklyRenderBaseView implementation used in Kuikly DSL can be
 * reused here without modification.
 *
 * @param viewName The view name as it appears in the A2UI protocol
 *                 (e.g. "KRMyCustomView"). Must match the viewName used in
 *                 createView commands.
 * @param creator  A factory function that creates the view instance.
 */
export function registerA2UICustomRenderView(
  viewName: string,
  creator: KRRenderViewExportCreator
): void {
  if (viewName.length === 0) {
    return;
  }
  a2uiCustomViewCreators.set(viewName, creator);
  // Push into the KMP-side A2UICustomViewRegistry directly so Kuikly-page
  // hosts (whose business Pagers don't override onReceivePagerEvent) also
  // see the registration. Idempotent w.r.t. native-mode which registers
  // via `dispatchRegisterCustomView` below.
  a2uiNativeManager.registerA2UICustomViewInKmp(viewName);
  // Broadcast to already-live surfaces so their Kotlin cores learn about it
  // immediately (also handled by replay on future surfaces via ready-hook).
  for (const c of a2uiLiveControllers) {
    c.dispatchRegisterCustomView(viewName);
  }
}

/**
 * Unregister a previously registered custom render view.
 * Safe to call for names that were never registered.
 */
export function unregisterA2UICustomRenderView(viewName: string): void {
  if (viewName.length === 0) {
    return;
  }
  a2uiCustomViewCreators.delete(viewName);
  a2uiNativeManager.unregisterA2UICustomViewInKmp(viewName);
  for (const c of a2uiLiveControllers) {
    c.dispatchUnregisterCustomView(viewName);
  }
}

// endregion

/**
 * Currently-live controllers. Used to broadcast register/unregister calls
 * that arrive after some surfaces have already booted.
 */
const a2uiLiveControllers: A2UISurfaceViewController[] = [];

/**
 * Register a custom A2UI function **globally**. Once registered, every
 * A2UISurfaceView in this process — created before or after the call —
 * will be able to invoke it via the standard functionCall mechanism.
 *
 * Reserved names (the 14 built-in A2UI v0.9 functions) are ignored by the
 * Kotlin registry.
 *
 * @param name       Function name as it appears in the A2UI catalog.
 * @param returnType One of "string" | "boolean" | "number" | "array" | "void".
 * @param invokeMode One of "sync" | "async". Non-void functions are treated as SYNC.
 * @param handler    Callback that performs the work.
 */
export function registerA2UICustomFunction(
  name: string,
  returnType: string,
  invokeMode: string,
  handler: A2UICustomFunctionHandler
): void {
  if (name.length === 0) {
    return;
  }
  const spec = `${returnType}|${invokeMode}`;
  a2uiCustomFunctionHandlers.set(name, handler);
  a2uiCustomFunctionDescriptors.set(name, spec);
  // Push descriptor into the KMP-side A2UICustomFunctionRegistry directly.
  // Native mode also registers via `dispatchRegisterCustomFunction` below
  // (through A2UIPager.onReceivePagerEvent); the KMP register call is
  // idempotent so double-write is safe.
  a2uiNativeManager.registerA2UICustomFunctionInKmp(name, spec);
  for (const c of a2uiLiveControllers) {
    c.dispatchRegisterCustomFunction(name, spec);
  }
}

/**
 * Reverse of [registerA2UICustomFunction]. Safe to call for unknown names.
 */
export function unregisterA2UICustomFunction(name: string): void {
  if (name.length === 0) {
    return;
  }
  a2uiCustomFunctionHandlers.delete(name);
  a2uiCustomFunctionDescriptors.delete(name);
  a2uiNativeManager.unregisterA2UICustomFunctionInKmp(name);
  for (const c of a2uiLiveControllers) {
    c.dispatchUnregisterCustomFunction(name);
  }
}

// region Icon Registry

/**
 * Reserved icon name used to look up the fallback image when a runtime icon
 * name (typically arriving via data binding) is not registered. Business apps
 * should include this key when calling [registerA2UIIcons] so unregistered
 * names have a predictable rendering.
 *
 * Must stay in sync with `A2UIIconRegistry.FALLBACK_KEY` on the Kotlin side.
 */
export const A2UI_ICON_FALLBACK_KEY: string = '_fallback';

/**
 * Global icon URI table. Kept as a source of truth on the ArkTS side so that
 * newly-created A2UISurfaceView controllers can replay every previously
 * registered icon into their freshly-booted Kotlin core.
 */
const a2uiIconRegistry = new Map<string, string>();

/**
 * Register a batch of business-supplied Icon images **globally**. Once at
 * least one icon has been registered, EVERY A2UI Icon component (including
 * those whose `name` is data-bound at runtime) is rendered via KRImageView
 * instead of the built-in unicode/emoji glyph.
 *
 * URI value contract: any URI understood by the host's `IKRImageAdapter`
 * implementation, e.g. `assets://a2ui_icons/home.png` (rawfile), `file://…`,
 * `https://…`, or a base64 data URI.
 *
 * If an [A2UI_ICON_FALLBACK_KEY] entry is provided, unregistered icon names
 * fall back to it; otherwise the underlying Kotlin registry falls back to
 * the first registered image, then to an empty URI.
 *
 * Uses `Map<string, string>` (not a plain object literal) because ArkTS
 * strict mode forbids untyped object literals and computed property names,
 * which would make constructing a table like `{[FALLBACK_KEY]: ...}` illegal.
 */
export function registerA2UIIcons(icons: Map<string, string>): void {
  icons.forEach((uri: string, name: string) => {
    registerA2UIIcon(name, uri);
  });
}

/**
 * Register or overwrite a single custom Icon image **globally**.
 *
 * @param name Icon `name` as defined by the A2UI protocol (camelCase or
 *             snake_case), or [A2UI_ICON_FALLBACK_KEY].
 * @param uri  Image URI (e.g. `assets://a2ui_icons/home.png`).
 */
export function registerA2UIIcon(name: string, uri: string): void {
  if (name.length === 0 || uri.length === 0) {
    return;
  }
  a2uiIconRegistry.set(name, uri);
  a2uiNativeManager.registerA2UIIconInKmp(name, uri);
  for (const c of a2uiLiveControllers) {
    c.dispatchRegisterIcon(name, uri);
  }
}

/**
 * Reverse of [registerA2UIIcon]. Safe to call for names that were never
 * registered.
 */
export function unregisterA2UIIcon(name: string): void {
  if (name.length === 0) {
    return;
  }
  a2uiIconRegistry.delete(name);
  a2uiNativeManager.unregisterA2UIIconInKmp(name);
  for (const c of a2uiLiveControllers) {
    c.dispatchUnregisterIcon(name);
  }
}

// endregion

/**
 * Internal: invoke a registered custom function.
 *
 * Void ASYNC handlers are dispatched via setTimeout(0) so the caller sees
 * a synchronous null return; every other mode executes inline on the
 * calling thread and returns the raw value (or its JSON encoding for
 * arrays) to the Kotlin coercer.
 */
function invokeCustomFunctionLocally(name: string, argsJson: string, spec: string): Object | null {
  const handler = a2uiCustomFunctionHandlers.get(name);
  if (!handler) {
    return null;
  }
  let args: Record<string, Object> = {};
  try {
    args = JSON.parse(argsJson) as Record<string, Object>;
  } catch (_e) {
    args = {};
  }
  const parts = spec.split('|');
  const returnType = (parts[0] ?? 'string').toLowerCase();
  const invokeMode = (parts[1] ?? 'sync').toLowerCase();

  if (returnType === 'void' && invokeMode === 'async') {
    setTimeout(() => {
      try {
        handler(args);
      } catch (e) {
        console.error(`[A2UI] custom function '${name}' threw: ${e}`);
      }
    }, 0);
    return null;
  }

  try {
    const raw = handler(args);
    if (raw === null || raw === undefined) {
      return null;
    }
    if (Array.isArray(raw)) {
      return JSON.stringify(raw);
    }
    return raw;
  } catch (e) {
    console.error(`[A2UI] custom function '${name}' threw: ${e}`);
    return null;
  }
}

/**
 * Listener interface for A2UI surface events.
 * Allows the hosting page to receive action callbacks from A2UI protocol interactions.
 */
export interface A2UISurfaceViewListener {
  /** Called when a surface is created and ready */
  onSurfaceCreated?: (surfaceId: string) => void;
  /** Called when a surface is destroyed */
  onSurfaceDestroyed?: (surfaceId: string) => void;
  /** Called when the message stream is finished */
  onStreamFinished?: () => void;
  /** Called when an error occurs */
  onError?: (message: string) => void;
  /** Called when an action is dispatched from a user interaction */
  onAction?: (surfaceId: string, tag: number, action: string, params: string | null) => void;
  /** Called when the data model changes due to user interaction (two-way binding) */
  onDataModelChanged?: (dataModelJson: string, changedKey: string) => void;
  /** Called when a Markdown link inside a Text component is tapped */
  onOpenUrl?: (url: string) => void;
}

// region Snapshots exposed to A2UIKuiklyRenderViewSupport
//
// The Kuikly-page hosting bridge (see A2UIKuiklyRenderViewSupport.ets) needs
// to read back everything registered via the top-level `registerA2UI…` APIs
// so it can (a) contribute the correct custom-view creator map to the host
// Kuikly page's IKuiklyViewDelegate and (b) replay custom-function / custom-
// view / icon registrations into the KMP layer via a fresh
// `KRNativeRenderController` — the same replay `onRenderControllerReady`
// does for a native `A2UISurfaceView`. These helpers are intentionally kept
// out of the module's public Index.ets exports; they are `export`ed only
// because ArkTS forbids cross-file access to file-scope `const`s.

/**
 * Snapshot every currently-registered custom render view creator.
 * The bridge merges this into the host Kuikly page's
 * `getCustomRenderViewCreatorRegisterMap()` so that any A2UISurface embedded
 * inside a business Pager can resolve its custom viewNames.
 */
export function snapshotA2UICustomViewCreators(): Map<string, KRRenderViewExportCreator> {
  return new Map(a2uiCustomViewCreators);
}

/**
 * Snapshot every currently-registered custom function descriptor
 * (`name → "returnType|invokeMode"`). The bridge replays these entries
 * into a fresh Kuikly-page render controller via
 * `REGISTER_CUSTOM_FUNCTION` events so the enclosed A2UISurfaceDSLView
 * sees exactly the same registrations as a legacy native surface.
 */
export function snapshotA2UICustomFunctionDescriptors(): Map<string, string> {
  return new Map(a2uiCustomFunctionDescriptors);
}

/**
 * Snapshot every currently-registered icon (`name → uri`). See
 * [snapshotA2UICustomFunctionDescriptors] for the replay contract.
 */
export function snapshotA2UIIconRegistry(): Map<string, string> {
  return new Map(a2uiIconRegistry);
}

/**
 * Local invocation entry for a registered custom function. Exported so the
 * Kuikly-page-side A2UIBridgeModule (bundled with the host page delegate by
 * A2UIKuiklyRenderViewSupport) can dispatch `invokeCustomFunction` calls
 * exactly like the native-mode module does.
 *
 * Signature and semantics are 1:1 with the file-scope `invokeCustomFunctionLocally`.
 */
export function invokeA2UICustomFunctionLocally(
  name: string,
  argsJson: string,
  spec: string
): Object | null {
  return invokeCustomFunctionLocally(name, argsJson, spec);
}

// region DateTime picker helpers (module-scope)
//
// These helpers are shared between two hosting paths:
//
//  1) Legacy Native-page (`A2UISurfaceView`): `A2UISurfaceViewController`
//     registers a per-instance callback into `a2uiPickerCallbackRegistry`,
//     which then calls into `A2UISurfaceView.showDateTimePicker` (the view
//     method retained for backward compatibility). That path bounces
//     through the controller for `notifyPickerResult`.
//
//  2) Kuikly-page embedded (`A2UISurfaceDSLView`): the outer host page's
//     `A2UIBridgeModule.call('showDateTimePicker')` uses these helpers
//     directly and pushes the picker result back through the KMP
//     top-level bridge (`a2uiNotifyPickerResult`), bypassing
//     `A2UISurfaceViewController` (which does not exist in this mode).
//
// Both paths converge here to keep the picker UX identical.

interface A2UIPickerConfig {
  tag: number;
  enableDate: boolean;
  enableTime: boolean;
  value: string;
  label: string;
  /**
   * Present ONLY when the picker request comes from a Kuikly-page hosted
   * A2UISurface (embedded mode). Callers use this to echo the picker
   * result back into the correct KMP DSL bridge via
   * `a2uiNativeManager.notifyPickerResultInKmp(...)`.
   */
  dslInstanceId: string;
}

/**
 * Parse the JSON payload delivered by KMP `A2UIDSLBridge.showDateTimePicker`.
 * Returns null on malformed input; callers should silently skip in that case.
 */
function parseA2UIPickerConfig(configJson: string): A2UIPickerConfig | null {
  try {
    const c = JSON.parse(configJson) as Record<string, Object>;
    return {
      tag: (c['tag'] as number) ?? 0,
      enableDate: (c['enableDate'] as boolean) ?? true,
      enableTime: (c['enableTime'] as boolean) ?? false,
      value: (c['value'] as string) ?? '',
      label: (c['label'] as string) ?? '',
      dslInstanceId: (c['dslInstanceId'] as string) ?? ''
    };
  } catch (_e) {
    return null;
  }
}

/** Split an ISO 8601 string into its Y/M/D/H/m components, filling defaults. */
function parseISO8601Components(value: string): [number, number, number, number, number] {
  let year: number = 2025;
  let month: number = 1;
  let day: number = 1;
  let hour: number = 0;
  let minute: number = 0;
  if (value.length === 0) return [year, month, day, hour, minute];
  try {
    const datePart = value.split('T')[0];
    const parts = datePart.split('-');
    if (parts.length >= 3) {
      year = parseInt(parts[0]) || 2025;
      month = parseInt(parts[1]) || 1;
      day = parseInt(parts[2]) || 1;
    }
    if (value.includes('T')) {
      const timePart = value.split('T')[1].split('Z')[0].split('+')[0];
      const timeParts = timePart.split(':');
      if (timeParts.length >= 2) {
        hour = parseInt(timeParts[0]) || 0;
        minute = parseInt(timeParts[1]) || 0;
      }
    }
  } catch (_e) {
    // Keep defaults on parse failure
  }
  return [year, month, day, hour, minute];
}

/** Format Y/M/D H:m into ISO 8601 (UTC 'Z', seconds always 00). */
function formatA2UIISO8601(year: number, month: number, day: number, hour: number, minute: number): string {
  const datePart = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  const timePart = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:00Z`;
  return `${datePart}T${timePart}`;
}

/** Wrap ArkUI `DatePickerDialog.show` with the same defaults used by A2UI. */
function showA2UIDatePicker(
  year: number, month: number, day: number,
  onResult: (year: number, month: number, day: number) => void
): void {
  DatePickerDialog.show({
    start: new Date('1900-1-1'),
    end: new Date('2100-12-31'),
    selected: new Date(`${year}-${month}-${day}`),
    lunar: false,
    onDateAccept: (value: Date) => {
      onResult(value.getFullYear(), value.getMonth() + 1, value.getDate());
    }
  });
}

/** Wrap ArkUI `TimePickerDialog.show` with the same defaults used by A2UI. */
function showA2UITimePicker(
  hour: number, minute: number,
  onResult: (hour: number, minute: number) => void
): void {
  TimePickerDialog.show({
    selected: new Date(`2025-1-1 ${hour}:${minute}:0`),
    useMilitaryTime: true,
    onAccept: (value: TimePickerResult) => {
      onResult(value.hour ?? 0, value.minute ?? 0);
    }
  });
}

/**
 * Show the appropriate DatePicker / TimePicker combination for the given
 * config and hand the ISO 8601 result to [onResult]. `enableDate=false &&
 * enableTime=false` is treated as a no-op.
 */
function showA2UIDateTimePickerFromConfig(
  config: A2UIPickerConfig,
  onResult: (tag: number, isoString: string) => void
): void {
  const parsed = parseISO8601Components(config.value);
  const y0: number = parsed[0];
  const m0: number = parsed[1];
  const d0: number = parsed[2];
  const h0: number = parsed[3];
  const mm0: number = parsed[4];
  const tag: number = config.tag;

  if (config.enableDate && config.enableTime) {
    // Show date first, then time — matches Android's two-step flow.
    showA2UIDatePicker(y0, m0, d0, (selYear: number, selMonth: number, selDay: number): void => {
      showA2UITimePicker(h0, mm0, (selHour: number, selMinute: number): void => {
        onResult(tag, formatA2UIISO8601(selYear, selMonth, selDay, selHour, selMinute));
      });
    });
  } else if (config.enableDate) {
    showA2UIDatePicker(y0, m0, d0, (selYear: number, selMonth: number, selDay: number): void => {
      onResult(tag, formatA2UIISO8601(selYear, selMonth, selDay, h0, mm0));
    });
  } else if (config.enableTime) {
    showA2UITimePicker(h0, mm0, (selHour: number, selMinute: number): void => {
      onResult(tag, formatA2UIISO8601(y0, m0, d0, selHour, selMinute));
    });
  }
}

// endregion

/**
 * The A2UIBridgeModule class as a callable module creator. The bridge
 * installs one instance of this module per Kuikly-page delegate so the
 * KMP core can call back for `contentHeightChanged` / `actionCallback` /
 * `showDateTimePicker` / `invokeCustomFunction` / `executeFunctionCall`
 * / `openUrl` / `debugLog` exactly as it does in native mode.
 *
 * Note: `contentHeightChanged` and `showDateTimePicker` in Kuikly-page
 * mode are largely no-ops from the host page's perspective — the DSL
 * surface manages its own height via Flex layout, and the picker path is
 * only exercised by the native surface. They are wired up for parity so
 * KMP-side call sites don't need to distinguish between modes.
 */
export function createA2UIBridgeModule(): A2UIBridgeModule {
  return new A2UIBridgeModule();
}

/**
 * Register the given KRNativeRenderController's `instanceId` with the
 * global action-dispatch registry so `A2UIBridgeModule.actionCallback`
 * has a target. Used only in Kuikly-page mode (native mode routes actions
 * through `A2UISurfaceView.onReady`).
 */
export function registerA2UIActionCallback(
  instanceId: string,
  callback: (tag: number, action: string) => void
): void {
  if (instanceId.length > 0) {
    a2uiActionCallbackRegistry.set(instanceId, callback);
  }
}

/** Reverse of [registerA2UIActionCallback]. */
export function unregisterA2UIActionCallback(instanceId: string): void {
  if (instanceId.length > 0) {
    a2uiActionCallbackRegistry.delete(instanceId);
  }
}

// endregion

/**
 * A2UI Event names for communication with Kotlin layer via render.sendEvent().
 * These must match the constants in A2UICoreEntry.kt (ohosArm64Main).
 */
class A2UIEvent {
  static readonly WRITE_STREAM: string = 'a2ui_writeStream';
  static readonly FINISH_STREAM: string = 'a2ui_finishStream';
  static readonly SET_SURFACE_SIZE: string = 'a2ui_setSurfaceSize';
  static readonly VIEW_CLICK: string = 'a2ui_viewClick';
  static readonly VIEW_EVENT: string = 'a2ui_viewEvent';
  static readonly PICKER_RESULT: string = 'a2ui_pickerResult';
  static readonly DESTROY: string = 'a2ui_destroy';
  /** Register a custom A2UI function's metadata. Data payload: "name\nspec". */
  static readonly REGISTER_CUSTOM_FUNCTION: string = 'a2ui_registerCustomFunction';
  /** Unregister a custom function. Data payload: "name". */
  static readonly UNREGISTER_CUSTOM_FUNCTION: string = 'a2ui_unregisterCustomFunction';
  /** Register a custom A2UI render view. Data payload: "viewName". */
  static readonly REGISTER_CUSTOM_VIEW: string = 'a2ui_registerCustomView';
  /** Unregister a custom render view. Data payload: "viewName". */
  static readonly UNREGISTER_CUSTOM_VIEW: string = 'a2ui_unregisterCustomView';
  /** Register a business-supplied custom Icon image. Data payload: "name\nuri". */
  static readonly REGISTER_ICON: string = 'a2ui_registerIcon';
  /** Unregister a custom Icon. Data payload: "name". */
  static readonly UNREGISTER_ICON: string = 'a2ui_unregisterIcon';
}

/**
 * A2UISurfaceView - HarmonyOS A2UI Surface View Component.
 *
 * This is the main entry point for embedding A2UI rendered content in a HarmonyOS application.
 *
 * Architecture (aligned with Android/iOS):
 * - Uses Kuikly's KRNativeRender (Kuikly component) as the rendering engine
 * - Kotlin A2UI core logic (A2UIBridgeManager, A2UIDSLBridge, A2UIPager) runs via Kotlin/Native
 * - Communication: ArkTS → Kotlin via render.sendEvent(); Kotlin → ArkTS via NativeBridge
 *
 * Rendering flow:
 * 1. KRNativeRender initializes → C++ creates Kotlin A2UIOhosPager
 * 2. A2UIOhosPager.onCreatePager() → initializes A2UIBridgeManager
 * 3. ArkTS sends writeStream/finishStream via render.sendEvent()
 * 4. Kotlin parses JSON, builds DSL tree, calculates layout
 * 5. Kotlin sends render commands (CREATE_VIEW, SET_PROP, SET_FRAME) via NativeBridge
 * 6. C++ KRRenderLayerHandler creates ArkUI C-API nodes
 *
 * Usage:
 * ```ets
 * A2UISurfaceView({
 *   listener: myListener,
 *   onReady: (controller: A2UISurfaceViewController) => {
 *     controller.writeStream('{"version":"v0.9","createSurface":{...}}');
 *     controller.writeStream('{"version":"v0.9","updateComponents":{...}}');
 *     controller.finishStream();
 *   }
 * })
 * ```
 */
@Component
export struct A2UISurfaceView {
  /** Listener for surface events */
  listener?: A2UISurfaceViewListener;
  /** Callback when the surface view controller is ready */
  onReady?: (controller: A2UISurfaceViewController) => void;

  // Internal state
  private controller: A2UISurfaceViewController | null = null;
  private delegate: A2UIViewDelegate | null = null;
  @State private showKuikly: boolean = false;
  @State private contentHeight: number = 0;
  /** Last width reported to Kotlin via onAreaChange, to avoid redundant calls */
  private lastReportedWidth: number = 0;

  aboutToAppear(): void {
    // Create the controller and delegate
    this.controller = new A2UISurfaceViewController(this.listener);
    this.delegate = new A2UIViewDelegate(this.controller!);
    this.showKuikly = true;
  }

  aboutToDisappear(): void {
    // Unregister callbacks for this instance
    if (this.controller) {
      const instanceId = this.controller.getInstanceId();
      if (instanceId) {
        a2uiHeightCallbackRegistry.delete(instanceId);
        a2uiActionCallbackRegistry.delete(instanceId);
        a2uiPickerCallbackRegistry.delete(instanceId);
      }
    }
    this.controller?.destroy();
    this.controller = null;
    this.delegate = null;
  }

  build() {
    Column() {
      if (this.showKuikly && this.delegate) {
        Kuikly({
          pageName: 'a2ui',
          pageData: {},
          nativeManager: a2uiNativeManager,
          delegate: this.delegate,
          onControllerReadyCallback: (renderController: KRNativeRenderController) => {
            if (this.controller) {
              this.controller.onRenderControllerReady(renderController);
              // Register height callback with the instanceId from the render controller
              const instanceId = renderController.instanceId;
              if (instanceId) {
                a2uiHeightCallbackRegistry.set(instanceId, (height: number): void => {
                  this.updateContentHeight(height);
                });
                // Register action callback
                a2uiActionCallbackRegistry.set(instanceId, (tag: number, action: string): void => {
                  if (this.listener?.onAction) {
                    this.listener.onAction('a2ui', tag, action, null);
                  } else {
                    // Default behavior: show a toast with the action info
                    promptAction.showToast({ message: `Action: ${action}` });
                  }
                });
                // Register picker callback
                a2uiPickerCallbackRegistry.set(instanceId, (configJson: string): void => {
                  this.showDateTimePicker(configJson);
                });
              }
              // Notify caller that the controller is ready
              if (this.onReady) {
                this.onReady(this.controller);
              }
            }
          }
        })
          .width('100%')
          .height(this.contentHeight > 0 ? this.contentHeight : undefined)
      }
    }
    .width('100%')
    .height(this.contentHeight > 0 ? this.contentHeight : undefined)
    .clip(true)
    .onAreaChange((_: Area, newValue: Area) => {
      const width = newValue.width as number;
      // Only send setSurfaceSize when width actually changes.
      // Height changes (due to contentHeight updates) should NOT trigger
      // a new setSurfaceSize, as that would cause a layout loop.
      if (width > 0 && this.controller && width !== this.lastReportedWidth) {
        this.lastReportedWidth = width;
        this.controller.setSurfaceSize(width, 10000);
      }
    })
  }

  /**
   * Called by A2UIBridgeModule when content height changes.
   */
  updateContentHeight(height: number): void {
    if (height > 0 && height !== this.contentHeight) {
      this.contentHeight = height;
    }
  }

  /**
   * Show a native date/time picker dialog.
   * Parses the config JSON and shows the appropriate picker(s).
   *
   * @param configJson JSON config: {"tag":N,"enableDate":bool,"enableTime":bool,"value":"ISO8601","label":"..."}
   */
  private showDateTimePicker(configJson: string): void {
    try {
      const config = JSON.parse(configJson) as Record<string, Object>;
      const tag = config['tag'] as number;
      const enableDate = config['enableDate'] as boolean ?? true;
      const enableTime = config['enableTime'] as boolean ?? false;
      const value = (config['value'] as string) ?? '';
      const label = (config['label'] as string) ?? '';

      // Parse current value
      let year = 2025;
      let month = 1;
      let day = 1;
      let hour = 0;
      let minute = 0;
      if (value.length > 0) {
        try {
          const datePart = value.split('T')[0];
          const parts = datePart.split('-');
          if (parts.length >= 3) {
            year = parseInt(parts[0]) || 2025;
            month = parseInt(parts[1]) || 1;
            day = parseInt(parts[2]) || 1;
          }
          if (value.includes('T')) {
            const timePart = value.split('T')[1].split('Z')[0].split('+')[0];
            const timeParts = timePart.split(':');
            if (timeParts.length >= 2) {
              hour = parseInt(timeParts[0]) || 0;
              minute = parseInt(timeParts[1]) || 0;
            }
          }
        } catch (_e) {
          // Keep defaults on parse failure
        }
      }

      if (enableDate && enableTime) {
        // Show date picker first, then time picker
        this.showDatePickerDialog(year, month, day, label, (selYear: number, selMonth: number, selDay: number) => {
          this.showTimePickerDialog(hour, minute, label, (selHour: number, selMinute: number) => {
            const isoString = this.formatISO8601(selYear, selMonth, selDay, selHour, selMinute);
            this.controller?.notifyPickerResult(tag, isoString);
          });
        });
      } else if (enableDate) {
        this.showDatePickerDialog(year, month, day, label, (selYear: number, selMonth: number, selDay: number) => {
          const isoString = this.formatISO8601(selYear, selMonth, selDay, hour, minute);
          this.controller?.notifyPickerResult(tag, isoString);
        });
      } else if (enableTime) {
        this.showTimePickerDialog(hour, minute, label, (selHour: number, selMinute: number) => {
          const isoString = this.formatISO8601(year, month, day, selHour, selMinute);
          this.controller?.notifyPickerResult(tag, isoString);
        });
      }
    } catch (e) {
      console.error(`[A2UISurfaceView] Error showing date/time picker: ${e}`);
    }
  }

  /**
   * Show a date picker dialog using ArkUI DatePickerDialog.
   */
  private showDatePickerDialog(
    year: number, month: number, day: number, label: string,
    onResult: (year: number, month: number, day: number) => void
  ): void {
    DatePickerDialog.show({
      start: new Date('1900-1-1'),
      end: new Date('2100-12-31'),
      selected: new Date(`${year}-${month}-${day}`),
      lunar: false,
      onDateAccept: (value: Date) => {
        onResult(value.getFullYear(), value.getMonth() + 1, value.getDate());
      }
    });
  }

  /**
   * Show a time picker dialog using ArkUI TimePickerDialog.
   */
  private showTimePickerDialog(
    hour: number, minute: number, label: string,
    onResult: (hour: number, minute: number) => void
  ): void {
    TimePickerDialog.show({
      selected: new Date(`2025-1-1 ${hour}:${minute}:0`),
      useMilitaryTime: true,
      onAccept: (value: TimePickerResult) => {
        onResult(value.hour ?? 0, value.minute ?? 0);
      }
    });
  }

  /**
   * Format date/time components into ISO 8601 string.
   */
  private formatISO8601(year: number, month: number, day: number, hour: number, minute: number): string {
    const datePart = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
    const timePart = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:00Z`;
    return `${datePart}T${timePart}`;
  }
}

/**
 * A2UISurfaceViewController - Controls the A2UI rendering lifecycle.
 *
 * This class manages the communication between the ArkTS layer and the
 * Kotlin A2UI core logic via Kuikly's native rendering pipeline.
 *
 * It mirrors the role of:
 * - Android: A2UISurfaceView (the FrameLayout that hosts Kuikly render views)
 * - iOS: A2UISurfaceView (the UIView that hosts Kuikly render views)
 */
export class A2UISurfaceViewController {
  /** Kuikly render controller (set when KRNativeRender is ready) */
  private renderController: KRNativeRenderController | null = null;
  /** Whether the render controller is ready */
  private isReady: boolean = false;
  /** Whether finishStream has been called */
  private streamFinished: boolean = false;
  /** Buffered stream chunks waiting for render controller to be ready */
  private pendingStreamChunks: string[] = [];
  /** Whether finishStream was called before controller was ready */
  private pendingFinishStream: boolean = false;
  /** Last known surface width in vp */
  private lastKnownWidth: number = 0;
  /** Last known surface height in number */
  private lastKnownHeight: number = 0;
  /** Listener for surface events */
  private listener?: A2UISurfaceViewListener;

  constructor(listener?: A2UISurfaceViewListener) {
    this.listener = listener;
  }

  getPageName(): string {
    return 'a2ui';
  }

  /**
   * Get the instanceId of the underlying Kuikly render controller.
   * Returns empty string if controller is not ready yet.
   */
  getInstanceId(): string {
    return this.renderController?.instanceId ?? '';
  }

  /**
   * Called when the Kuikly render controller is ready.
   * At this point, the Kotlin A2UIOhosPager has been created and initialized.
   */
  onRenderControllerReady(controller: KRNativeRenderController): void {
    this.renderController = controller;
    this.isReady = true;

    // Add ourselves to the live controllers list so future
    // registerA2UICustomFunction calls are forwarded here too.
    a2uiLiveControllers.push(this);

    // Always send surface size first if available
    if (this.lastKnownWidth > 0) {
      this.sendSurfaceSize();
    }

    // Flush pending operations
    if (this.pendingStreamChunks.length > 0 || this.pendingFinishStream) {
      // Send buffered stream chunks
      const chunks = [...this.pendingStreamChunks];
      this.pendingStreamChunks = [];
      for (const chunk of chunks) {
        this.sendEvent(A2UIEvent.WRITE_STREAM, chunk);
      }

      // Send finish if pending
      if (this.pendingFinishStream) {
        this.pendingFinishStream = false;
        this.sendEvent(A2UIEvent.FINISH_STREAM, '');
      }
    }

    // Replay every globally-registered custom function so this fresh
    // instance's Kotlin core learns about them.
    a2uiCustomFunctionDescriptors.forEach((spec: string, name: string) => {
      this.sendEvent(A2UIEvent.REGISTER_CUSTOM_FUNCTION, `${name}\n${spec}`);
    });

    // Replay every globally-registered custom render view name so this fresh
    // instance's Kotlin core also learns about them (mirrors the function
    // replay above). See A2UICustomViewRegistry for how these names are used
    // by A2UIApplier.createDSLView to route unknown viewNames.
    a2uiCustomViewCreators.forEach((_creator: KRRenderViewExportCreator, viewName: string) => {
      this.sendEvent(A2UIEvent.REGISTER_CUSTOM_VIEW, viewName);
    });

    // Replay every globally-registered custom Icon image so this fresh
    // instance's Kotlin core (A2UIIconRegistry) can route Icon components to
    // KRImageView with the correct URI. Payload format: "name\nuri".
    a2uiIconRegistry.forEach((uri: string, name: string) => {
      this.sendEvent(A2UIEvent.REGISTER_ICON, `${name}\n${uri}`);
    });

    // Notify listener
    if (this.listener?.onSurfaceCreated) {
      this.listener.onSurfaceCreated('a2ui');
    }
  }

  /**
   * Forward a global `registerA2UICustomFunction` into this surface's Kotlin core.
   * Called by the top-level [registerA2UICustomFunction] for every live
   * controller.
   */
  dispatchRegisterCustomFunction(name: string, spec: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.REGISTER_CUSTOM_FUNCTION, `${name}\n${spec}`);
  }

  /** Companion to [dispatchRegisterCustomFunction]. */
  dispatchUnregisterCustomFunction(name: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.UNREGISTER_CUSTOM_FUNCTION, name);
  }

  /**
   * Forward a global `registerA2UICustomRenderView` into this surface's
   * Kotlin core. Called by the top-level [registerA2UICustomRenderView] for
   * every live controller. The Kotlin side uses this to populate
   * A2UICustomViewRegistry so that A2UIApplier.createDSLView can route
   * unknown viewNames correctly (custom view vs. DivView fallback).
   */
  dispatchRegisterCustomView(viewName: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.REGISTER_CUSTOM_VIEW, viewName);
  }

  /** Companion to [dispatchRegisterCustomView]. */
  dispatchUnregisterCustomView(viewName: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.UNREGISTER_CUSTOM_VIEW, viewName);
  }

  /**
   * Forward a global `registerA2UIIcon` into this surface's Kotlin core so
   * that the Icon component mapper picks up the new URI. Called by the
   * top-level [registerA2UIIcon] for every live controller.
   */
  dispatchRegisterIcon(name: string, uri: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.REGISTER_ICON, `${name}\n${uri}`);
  }

  /** Companion to [dispatchRegisterIcon]. */
  dispatchUnregisterIcon(name: string): void {
    if (!this.isReady) {
      return;
    }
    this.sendEvent(A2UIEvent.UNREGISTER_ICON, name);
  }

  /**
   * Feed one A2UI envelope message into the surface's message stream.
   *
   * Per A2UI protocol v0.9: server-to-client is a stream of JSON objects,
   * each containing exactly one of: createSurface, updateComponents,
   * updateDataModel, or deleteSurface.
   *
   * @param chunk A single A2UI envelope JSON object
   */
  writeStream(chunk: string): void {
    // Reject JSON array wrapper (not part of A2UI protocol)
    const trimmed = chunk.trimStart();
    if (trimmed.length > 0 && trimmed.charAt(0) === '[') {
      console.error('[A2UISurfaceView] writeStream: input starts with "[" — a JSON-array wrapper is NOT part of the A2UI protocol. Please unpack the array and feed each envelope individually.');
      return;
    }

    if (!this.isReady) {
      // Buffer until render controller is ready
      this.pendingStreamChunks.push(chunk);
      return;
    }

    // Send to Kotlin via render.sendEvent()
    this.sendEvent(A2UIEvent.WRITE_STREAM, chunk);
  }

  /**
   * Mark the message stream as finished.
   * This triggers final layout calculation and rendering.
   */
  finishStream(): void {
    this.streamFinished = true;

    if (!this.isReady) {
      this.pendingFinishStream = true;
      return;
    }

    this.sendEvent(A2UIEvent.FINISH_STREAM, '');

    if (this.listener?.onStreamFinished) {
      this.listener.onStreamFinished();
    }
  }

  /**
   * Set the surface size (called when the component's layout changes).
   */
  setSurfaceSize(width: number, height: number): void {
    if (width <= 0) return;
    const newHeight = height > 0 ? height : 10000;
    // Skip if size hasn't changed
    if (width === this.lastKnownWidth && newHeight === this.lastKnownHeight) return;
    this.lastKnownWidth = width;
    this.lastKnownHeight = newHeight;

    if (!this.isReady) return;
    this.sendSurfaceSize();

    // If there are pending streams waiting for size, flush them now
    if (this.pendingStreamChunks.length > 0 || this.pendingFinishStream) {
      const chunks = [...this.pendingStreamChunks];
      this.pendingStreamChunks = [];
      for (const chunk of chunks) {
        this.sendEvent(A2UIEvent.WRITE_STREAM, chunk);
      }
      if (this.pendingFinishStream) {
        this.pendingFinishStream = false;
        this.sendEvent(A2UIEvent.FINISH_STREAM, '');
      }
    }
  }

  /**
   * Notify a view click event (for action dispatch).
   */
  notifyViewClick(tag: number): void {
    if (!this.isReady) return;
    this.sendEvent(A2UIEvent.VIEW_CLICK, `${tag}`);
  }

  /**
   * Notify a view event (e.g. pan gesture, progress change).
   * @param tag The native ref of the view
   * @param eventName The event name
   * @param data Optional event data as JSON string
   */
  notifyViewEvent(tag: number, eventName: string, data?: string): void {
    if (!this.isReady) return;
    const payload = data ? `${tag}|${eventName}\n${data}` : `${tag}|${eventName}`;
    this.sendEvent(A2UIEvent.VIEW_EVENT, payload);
  }

  /**
   * Notify picker result.
   * @param tag The tag of the picker component
   * @param isoString The selected date/time in ISO format
   */
  notifyPickerResult(tag: number, isoString: string): void {
    if (!this.isReady) return;
    this.sendEvent(A2UIEvent.PICKER_RESULT, `${tag}|${isoString}`);
  }

  /**
   * Destroy the surface and clean up resources.
   */
  destroy(): void {
    if (this.isReady) {
      this.sendEvent(A2UIEvent.DESTROY, '');
    }
    this.renderController = null;
    this.isReady = false;

    // Remove ourselves from the live controllers list.
    const idx = a2uiLiveControllers.indexOf(this);
    if (idx >= 0) {
      a2uiLiveControllers.splice(idx, 1);
    }

    if (this.listener?.onSurfaceDestroyed) {
      this.listener.onSurfaceDestroyed('a2ui');
    }
  }

  /**
   * Send an event to the Kotlin layer via render.sendEvent().
   */
  private sendEvent(event: string, data: string): void {
    if (!this.renderController) return;
    // Use KRNativeRenderController's sendEvent method
    const eventData: KRRecord = { 'data': data };
    this.renderController.sendEvent(event, eventData);
  }

  /**
   * Send the current surface size to Kotlin.
   * Format: "width,height,deviceWidth,deviceHeight"
   * deviceWidth/deviceHeight are the real screen dimensions in vp,
   * used by Modal to size its full-screen overlay correctly.
   */
  private sendSurfaceSize(): void {
    let sizeStr = `${this.lastKnownWidth},${this.lastKnownHeight}`;
    // Append real screen size so Modal overlay can cover the full screen
    // even when the A2UISurfaceView is narrower (e.g. inside a padded container).
    try {
      const defaultDisplay = display.getDefaultDisplaySync();
      const deviceWidthVp = px2vp(defaultDisplay.width);
      const deviceHeightVp = px2vp(defaultDisplay.height);
      sizeStr += `,${deviceWidthVp},${deviceHeightVp}`;
    } catch (e) {
      // Fallback: no device size appended; Kotlin will use surfaceWidth as fallback.
    }
    this.sendEvent(A2UIEvent.SET_SURFACE_SIZE, sizeStr);
  }

}

/**
 * A2UIViewDelegate - IKuiklyViewDelegate implementation for A2UI.
 *
 * This delegate is passed to the Kuikly component to configure the rendering.
 * It registers A2UI-specific custom views and modules.
 */
class A2UIViewDelegate extends IKuiklyViewDelegate {
  private controller: A2UISurfaceViewController;

  constructor(controller: A2UISurfaceViewController) {
    super();
    this.controller = controller;
  }

  getPageName(): string {
    return 'a2ui';
  }

  getCustomRenderViewCreatorRegisterMap(): Map<string, KRRenderViewExportCreator> {
    // Return all globally-registered custom render views.
    // Standard Kuikly views (KRView, KRImageView, KRRichTextView, etc.) are
    // registered by the C++ layer automatically; only custom views need to be
    // registered here via registerA2UICustomRenderView().
    return new Map(a2uiCustomViewCreators);
  }

  getCustomRenderModuleCreatorRegisterMap(): Map<string, KRRenderModuleExportCreator> {
    const moduleMap = new Map<string, KRRenderModuleExportCreator>();
    moduleMap.set('A2UIBridgeModule', (): A2UIBridgeModule => {
      return new A2UIBridgeModule();
    });
    return moduleMap;
  }
}

/**
 * A2UIBridgeModule - Receives callbacks from Kotlin A2UI layer via callModuleMethod.
 *
 * This module handles:
 * - contentHeightChanged: Updates the surface view height when content layout changes
 * - actionCallback: Dispatches action events to the listener
 * - dataModelChanged: Notifies data model changes
 * - openUrl: Handles URL opening
 */
class A2UIBridgeModule extends KuiklyRenderBaseModule {
  constructor() {
    super();
  }

  call(method: string, params: KRAny, callback: KuiklyRenderCallback | null): KRAny {
    if (method === 'contentHeightChanged') {
      const height = params as number;
      if (height > 0) {
        // Use the instanceId (set by KRNativeInstance.getOrCreateModule → init())
        // to look up the correct height callback for this specific A2UISurfaceView instance.
        const heightCallback = a2uiHeightCallbackRegistry.get(this.instanceId);
        if (heightCallback) {
          heightCallback(height);
        }
      }
    } else if (method === 'executeFunctionCall') {
      // Format: "functionName\nargsJson"
      const paramsStr = params as string;
      const separatorIndex = paramsStr.indexOf('\n');
      if (separatorIndex >= 0) {
        const functionName = paramsStr.substring(0, separatorIndex);
        const argsJson = paramsStr.substring(separatorIndex + 1);
        this.handleFunctionCall(functionName, argsJson);
      }
    } else if (method === 'actionCallback') {
      // Format: "tag\nactionJson"
      const paramsStr = params as string;
      const separatorIndex = paramsStr.indexOf('\n');
      if (separatorIndex >= 0) {
        const tagStr = paramsStr.substring(0, separatorIndex);
        const actionJson = paramsStr.substring(separatorIndex + 1);
        const tag = parseInt(tagStr);
        const actionCallback = a2uiActionCallbackRegistry.get(this.instanceId);
        if (actionCallback) {
          actionCallback(tag, actionJson);
        }
      }
    } else if (method === 'showDateTimePicker') {
      // Params is the configJson string. Two hosting modes exist and we
      // route by *actual ArkTS state* rather than by any KMP-side hint:
      //   • Native-page  — A2UISurfaceView is mounted, so it has already
      //                    populated `a2uiPickerCallbackRegistry` under
      //                    its own `instanceId` (see the `onControllerReadyCallback`
      //                    block). Legacy `A2UISurfaceViewController.
      //                    notifyPickerResult(...)` path stays in charge
      //                    and dispatches through `sendEvent(a2ui_pickerResult)`.
      //   • Kuikly-page  — no A2UISurfaceView, no controller, so nothing
      //     (embedded)    ever registered a callback into the registry.
      //                    Fall through to the NAPI path which pushes
      //                    the result straight into KMP by `dslInstanceId`.
      //
      // Historical note: this used to key off `configJson.dslInstanceId.length
      // > 0`, but KMP now *always* bakes `dslInstanceId` into the config
      // (native side ignores it via this exact branch). See
      // `A2UIDSLBridge.showDateTimePicker`.
      const configJson = params as string;
      const pickerCallback = a2uiPickerCallbackRegistry.get(this.instanceId);
      if (pickerCallback) {
        pickerCallback(configJson);
      } else {
        const config = parseA2UIPickerConfig(configJson);
        if (config !== null && config.dslInstanceId.length > 0) {
          showA2UIDateTimePickerFromConfig(config, (tag: number, isoString: string): void => {
            a2uiNativeManager.notifyPickerResultInKmp(config.dslInstanceId, tag, isoString);
          });
        }
      }
    } else if (method === 'invokeCustomFunction') {
      // Custom user function callback. Called synchronously from Kotlin via
      // BridgeManager.callModuleMethod(..., syncCall = 1).
      // Payload format: "name\nspec\nargsJson"
      const paramsStr = params as string;
      const firstNewline = paramsStr.indexOf('\n');
      const secondNewline = firstNewline >= 0 ? paramsStr.indexOf('\n', firstNewline + 1) : -1;
      if (firstNewline >= 0 && secondNewline >= 0) {
        const name = paramsStr.substring(0, firstNewline);
        const spec = paramsStr.substring(firstNewline + 1, secondNewline);
        const argsJson = paramsStr.substring(secondNewline + 1);
        return invokeCustomFunctionLocally(name, argsJson, spec);
      }
      return null;
    } else if (method === 'debugLog') {
      // Debug log from Kotlin layer (println doesn't output to hilog on OHOS)
      const msg = params as string;
      console.log(msg);
    }
    return null;
  }

  /**
   * Execute a client-side function call per A2UI protocol.
   * Handles standard functions like openUrl.
   *
   * `showAlert` was removed from the built-in set: per A2UI v0.9 spec only
   * `openUrl` is a standard `action.functionCall` intent, so hosts that
   * want an alert must emit an `action.event` (e.g. `event.name = "toast"`)
   * and pop the dialog themselves in their `onAction` listener.
   */
  private handleFunctionCall(functionName: string, argsJson: string): void {
    try {
      const args = JSON.parse(argsJson) as Record<string, Object>;
      switch (functionName) {
        case 'openUrl': {
          const url = (args['url'] as string) ?? '';
          if (url.length > 0) {
            this.openUrl(url);
          }
          break;
        }
        default: {
          // Unknown function: show a toast as fallback
          promptAction.showToast({ message: `FunctionCall: ${functionName}(${argsJson})` });
          break;
        }
      }
    } catch (e) {
      console.error(`[A2UIBridgeModule] Failed to execute functionCall '${functionName}': ${e}`);
    }
  }

  /**
   * Open a URL using the system browser or handler.
   */
  private openUrl(url: string): void {
    try {
      const context = getContext() as common.UIAbilityContext;
      const want: Want = {
        action: 'ohos.want.action.viewData',
        uri: url
      };
      context.startAbility(want);
    } catch (e) {
      console.error(`[A2UIBridgeModule] Failed to open URL: ${url}, error: ${e}`);
    }
  }

  syncMode(): boolean {
    return false;
  }

  onDestroy(): void {
  }
}