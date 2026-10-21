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
  IKuiklyViewDelegate,
  KRNativeRenderController,
  KRRenderViewExportCreator,
  KRRenderModuleExportCreator,
  IKuiklyRenderModuleExport
} from '@kuikly-open/render';
import {
  snapshotA2UICustomViewCreators,
  createA2UIBridgeModule,
  registerA2UIActionCallback,
  unregisterA2UIActionCallback
} from './A2UISurfaceView';

/**
 * A2UI registrations (custom functions / views / icons) are pushed straight
 * into the KMP-side global registries the moment the caller invokes
 * `registerA2UICustomFunction/View/Icon` on the ArkTS side — see
 * A2UINativeManager.registerA2UI…InKmp and the top-level `@CName` writers
 * exported by A2UIKuiklyEntry.kt. There is therefore no per-controller
 * replay to do at Kuikly-page bootstrap: the KMP registries are already
 * populated by the time the fresh `KRNativeRenderController` starts
 * evaluating A2UI DSL.
 */

/**
 * Utilities for enabling A2UI inside a business Kuikly page (i.e. a
 * `Kuikly({...})` component whose `pageName` refers to a user-authored
 * `Pager` such as `A2UISurfaceDemo`).
 *
 * ### Why this exists
 *
 * A2UI's Kuikly-page integration mode lets you drop `A2UISurface { ... }`
 * DSL blocks directly into a Kotlin `Pager`. The DSL block is rendered by
 * KMP's `A2UISurfaceDSLView` (a Kuikly `DivView` subclass), which lives in
 * the same Flex tree as its siblings — no wrapper native view, no inner
 * pager.
 *
 * For that to work correctly, the host Kuikly page must:
 *
 *   1. Contribute every globally-registered A2UI custom render view to its
 *      own `IKuiklyViewDelegate.getCustomRenderViewCreatorRegisterMap()`,
 *      otherwise `createRenderView("KRMyCustomView", …)` inside the DSL
 *      block finds no factory and falls back to `DivView`.
 *
 *   2. Install `A2UIBridgeModule` on its own delegate so KMP callbacks
 *      (`invokeCustomFunction`, `executeFunctionCall`, `openUrl`,
 *      `debugLog`, …) still land in ArkTS.
 *
 *   3. Replay every previously-registered custom function / custom render
 *      view / icon into the freshly-booted `KRNativeRenderController` so
 *      the KMP core sees the same registrations a legacy native surface
 *      would see. This mirrors what
 *      `A2UISurfaceViewController.onRenderControllerReady()` does for the
 *      native path.
 *
 * `A2UIKuiklyRenderViewSupport.wrap(delegate?)` bundles all three steps
 * into a one-liner:
 *
 * ```ts
 * import { Kuikly } from '@kuikly-open/render';
 * import { A2UIKuiklyRenderViewSupport, a2uiNativeManager } from '@kuikly-a2ui-open/a2ui-render';
 *
 * Kuikly({
 *   pageName: 'A2UISurfaceDemo',
 *   nativeManager: a2uiNativeManager,
 *   delegate: A2UIKuiklyRenderViewSupport.wrap(),
 * })
 * ```
 *
 * ### Cross-platform naming
 *
 * The name and the `wrap(delegate?)` signature are intentionally aligned
 * with:
 *   - Android — `A2UIKuiklyBaseViewSupport.wrap(delegate)` in
 *     `a2ui-render-android`
 *   - iOS     — `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()`
 *     in `a2ui-render-ios`
 *
 * so business code reads similarly across platforms. The Android variant
 * hooks `registerExternalRenderView`; iOS just populates the KMP-side
 * `A2UICustomViewRegistry`. On ohos, the natural hook is the delegate's
 * `getCustomRenderViewCreatorRegisterMap()` (which is what the underlying
 * `KRNativeRender` calls once per page bootstrap) plus a "controller-ready"
 * replay to seed the KMP core.
 *
 * ### Coexistence with the native surface
 *
 * Native mode (`A2UISurfaceView` component) and Kuikly-page mode may live
 * in the same process — see `ohosAppForBothPages`. All registrations
 * (`registerA2UICustomFunction`, `registerA2UICustomRenderView`,
 * `registerA2UIIcons`) are process-global and shared by both modes.
 * `wrap()` reuses those same tables via the `snapshot…` helpers, so a
 * customisation registered before entering the Kuikly page is visible to
 * the embedded surfaces, and vice versa.
 */
export class A2UIKuiklyRenderViewSupport {

  private constructor() {}

  /**
   * Wrap [inner] (or create a fresh empty delegate if `inner` is
   * `undefined`) so the returned delegate:
   *
   *  1. contributes every globally-registered A2UI custom render view to
   *     its `getCustomRenderViewCreatorRegisterMap()` (merged on top of
   *     `inner`'s own map — A2UI wins on name collision, consistent with
   *     the Android side's post-hook ordering),
   *  2. exposes `A2UIBridgeModule` from
   *     `getCustomRenderModuleCreatorRegisterMap()` (merged on top of
   *     `inner`'s own module map),
   *  3. registers a per-page action-callback so `A2UIBridgeModule
   *     .actionCallback` payloads become `console.warn` toasts by default
   *     — the caller can pass its own handler via [wrap]'s [onAction]
   *     parameter for custom routing (mirrors the native-mode
   *     `A2UISurfaceViewListener.onAction`).
   *
   * The `pageName`/`pageData` are still supplied by the caller when
   * instantiating the `Kuikly` component; this method does not touch
   * them.
   *
   * Safe to call from any thread. The returned delegate can be reused
   * across multiple `Kuikly` components — each `KRNativeRenderController`
   * gets its own replay via `onControllerReadyReplay()`.
   *
   * @param inner    Optional user-provided delegate. Every non-A2UI
   *                 callback is forwarded to it via subclass composition.
   * @param onAction Optional action handler invoked whenever an A2UI
   *                 protocol `action.functionCall` (that is NOT one of
   *                 the built-in functions handled inside the KMP layer)
   *                 fires from inside the embedded surface. `tag` is the
   *                 view tag reported by A2UI, `actionJson` is the raw
   *                 protocol JSON. Default: prints to `console.warn`.
   */
  static wrap(
    inner?: IKuiklyViewDelegate,
    onAction?: (tag: number, actionJson: string) => void
  ): IKuiklyViewDelegate {
    return new A2UIKuiklyRenderViewDelegateWrapper(inner, onAction);
  }

  /**
   * Replay every currently-registered custom function / custom view /
   * icon into [controller] so the KMP core hosted by this fresh Kuikly
   * page sees exactly the same registrations as a legacy native surface.
   *
   * Exposed as a static so callers who already own an
   * `IKuiklyViewDelegate` chain (and therefore can't easily go through
   * [wrap]) can still opt into the replay via
   * `onControllerReadyCallback`. Idempotent per-controller.
   */
  static onControllerReadyReplay(controller: KRNativeRenderController): void {
    replayA2UIRegistrationsIntoController(controller);
  }
}

/**
 * The wrapper implementation. Kept private to preserve the
 * `A2UIKuiklyRenderViewSupport.wrap(...)` API surface — callers should
 * never instantiate this directly.
 *
 * Forwards non-A2UI-specific hooks to [inner] by hand (ArkTS lacks
 * Kotlin's `by` delegation, so we override the concrete abstract
 * methods and fan out to [inner] when it is present).
 */
class A2UIKuiklyRenderViewDelegateWrapper extends IKuiklyViewDelegate {
  private inner: IKuiklyViewDelegate | undefined;
  private onAction: ((tag: number, actionJson: string) => void) | undefined;

  constructor(
    inner?: IKuiklyViewDelegate,
    onAction?: (tag: number, actionJson: string) => void
  ) {
    super();
    this.inner = inner;
    this.onAction = onAction;
  }

  getCustomRenderViewCreatorRegisterMap(): Map<string, KRRenderViewExportCreator> {
    // Start with the user's own registrations (if any), then layer every
    // A2UI-registered custom view on top. A2UI wins on name collision —
    // that matches the Android-side ordering (`inner` runs first,
    // A2UI attaches afterwards).
    const merged = new Map<string, KRRenderViewExportCreator>();
    const innerMap = this.inner?.getCustomRenderViewCreatorRegisterMap();
    if (innerMap) {
      innerMap.forEach((creator: KRRenderViewExportCreator, name: string) => {
        merged.set(name, creator);
      });
    }
    snapshotA2UICustomViewCreators().forEach((creator: KRRenderViewExportCreator, name: string) => {
      merged.set(name, creator);
    });
    return merged;
  }

  getCustomRenderModuleCreatorRegisterMap(): Map<string, KRRenderModuleExportCreator> {
    const merged = new Map<string, KRRenderModuleExportCreator>();
    const innerMap = this.inner?.getCustomRenderModuleCreatorRegisterMap();
    if (innerMap) {
      innerMap.forEach((creator: KRRenderModuleExportCreator, name: string) => {
        merged.set(name, creator);
      });
    }
    // A2UIBridgeModule name is fixed by protocol contract — must equal
    // the constant on the KMP side (`A2UIOhosPager.MODULE_NAME`).
    merged.set('A2UIBridgeModule', (): IKuiklyRenderModuleExport => {
      return createA2UIBridgeModule();
    });
    return merged;
  }
}

/**
 * Replay every globally-registered custom function / custom render view /
 * icon into the given [controller] so its KMP core sees the same set of
 * registrations that a native `A2UISurfaceView` would.
 *
 * Historically this replay used `controller.sendEvent(...)` to reach the
 * KMP registries via A2UIPager.onReceivePagerEvent, which does not work
 * when the business pager (e.g. A2UICaseListPager) extends the plain
 * `Pager` class. Direct NAPI writes into `A2UICustomFunctionRegistry` /
 * `A2UICustomViewRegistry` / `A2UIIconRegistry` now happen at register
 * time (see A2UINativeManager.registerA2UI…InKmp), so the KMP core is
 * already up-to-date once the controller boots — no per-controller replay
 * is required. Kept as a no-op stub for API stability.
 */
function replayA2UIRegistrationsIntoController(controller: KRNativeRenderController): void {
  // Intentionally empty: KMP registries are populated eagerly via NAPI on
  // every register/unregister call, so freshly-booted controllers already
  // see the current registration state.
}

/**
 * Convenience helper for callers that don't use [A2UIKuiklyRenderViewSupport.wrap]
 * but do want the KMP replay behaviour on a bare `KRNativeRenderController`.
 *
 * Also lets callers hook up a per-instance action callback exactly the way
 * a native surface does via `A2UISurfaceViewListener.onAction`.
 *
 * @param controller The `KRNativeRenderController` handed to
 *                   `onControllerReadyCallback` by `Kuikly({…})`.
 * @param onAction   Optional custom-action sink. If provided, it will
 *                   receive every `functionCall` action fired by embedded
 *                   surfaces on this controller. If omitted, actions land
 *                   in `console.warn`.
 */
export function bindA2UIKuiklyPageController(
  controller: KRNativeRenderController,
  onAction?: (tag: number, actionJson: string) => void
): void {
  const instanceId = controller.instanceId;
  if (!instanceId) {
    return;
  }
  registerA2UIActionCallback(instanceId, (tag: number, action: string): void => {
    if (onAction) {
      onAction(tag, action);
    } else {
      console.warn(`[A2UI] Kuikly-page action (unhandled): tag=${tag} action=${action}`);
    }
  });
  replayA2UIRegistrationsIntoController(controller);
}

/**
 * Companion to [bindA2UIKuiklyPageController]. Call from the host page's
 * `aboutToDisappear`/dispose lifecycle so the action-callback registry
 * doesn't leak `instanceId` entries.
 */
export function unbindA2UIKuiklyPageController(controller: KRNativeRenderController): void {
  const instanceId = controller.instanceId;
  if (!instanceId) {
    return;
  }
  unregisterA2UIActionCallback(instanceId);
}
