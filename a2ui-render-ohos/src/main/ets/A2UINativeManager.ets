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

import { KuiklyNativeManager } from '@kuikly-open/render';
import Napi from 'libkuikly_entry.so';

/**
 * A2UI Native Manager for HarmonyOS.
 *
 * This class is responsible for loading the Kotlin/Native library (libshared.so)
 * and calling initKuikly() to register the "a2ui" page route.
 *
 * It also exposes the six unidirectional writers that push A2UI registrations
 * (custom function / custom view / icon) straight into the Kotlin-side
 * global registries via NAPI. See [A2UIKuiklyRenderViewSupport] for the
 * rationale — Kuikly-page mode cannot reach those registries through the
 * usual `sendEvent` path because business pagers extend the plain `Pager`
 * class rather than `A2UIPager`, so we go through NAPI + `@CName` in
 * A2UIKuiklyEntry.kt instead.
 *
 * All six methods are safe no-ops on older `libkuikly_entry.so` builds that
 * were compiled before the new NAPI methods were added — every call is
 * guarded by an existence check so the ArkTS side does not have to know
 * which build variant of `libshared.so` is loaded.
 *
 * It is passed to the Kuikly component via the nativeManager property.
 */
class A2UINativeManager extends KuiklyNativeManager {
  loadNative(): number {
    return Napi.initKuikly();
  }

  // --- Direct KMP registry writers ------------------------------------

  registerA2UICustomFunctionInKmp(name: string, spec: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiRegisterCustomFunction'];
    if (typeof fn === 'function') {
      (fn as (name: string, spec: string) => void)(name, spec);
    }
  }

  unregisterA2UICustomFunctionInKmp(name: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiUnregisterCustomFunction'];
    if (typeof fn === 'function') {
      (fn as (name: string) => void)(name);
    }
  }

  registerA2UICustomViewInKmp(viewName: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiRegisterCustomView'];
    if (typeof fn === 'function') {
      (fn as (viewName: string) => void)(viewName);
    }
  }

  unregisterA2UICustomViewInKmp(viewName: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiUnregisterCustomView'];
    if (typeof fn === 'function') {
      (fn as (viewName: string) => void)(viewName);
    }
  }

  registerA2UIIconInKmp(name: string, uri: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiRegisterIcon'];
    if (typeof fn === 'function') {
      (fn as (name: string, uri: string) => void)(name, uri);
    }
  }

  unregisterA2UIIconInKmp(name: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiUnregisterIcon'];
    if (typeof fn === 'function') {
      (fn as (name: string) => void)(name);
    }
  }

  /**
   * Deliver a DateTimeInput picker result (from an ArkUI DatePicker/
   * TimePicker) straight into the KMP-side A2UIDSLBridge, bypassing the
   * legacy `sendEvent("a2ui_pickerResult", …)` path which requires an
   * `A2UISurfaceViewController` that does not exist in Kuikly-page mode.
   *
   * See `A2UIRegistryBridge.a2uiNotifyPickerResult` on the KMP side for
   * the routing rationale.
   *
   * @param dslInstanceId The DSL-side instance id echoed from the
   *                      original picker config (`configJson.dslInstanceId`).
   * @param tag           A2UI protocol tag of the DateTimeInput component.
   * @param isoString     Selected date/time in ISO 8601 format.
   */
  notifyPickerResultInKmp(dslInstanceId: string, tag: number, isoString: string): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiNotifyPickerResult'];
    if (typeof fn === 'function') {
      (fn as (dslInstanceId: string, tag: number, isoString: string) => void)(
        dslInstanceId, tag, isoString
      );
    }
  }

  /**
   * Deliver an async "custom view size changed" notification into the KMP
   * layer, so `A2UICustomComponentView.handleNativeResize(w, h)` can update
   * its `flexNode.styleWidth` / `styleHeight` on the Kuikly Context Thread.
   *
   * See `A2UIRegistryBridge.a2uiNotifyCustomViewResize` on the KMP side
   * for the thread-hop rationale (mirrors `a2uiNotifyPickerResult`:
   * ArkTS main thread → schedule onto Kuikly Context Thread → mutate
   * Flex tree).
   *
   * @param pagerId   Pager id that owns the target custom view (either the
   *                  A2UI DSL instance id in native-page mode or the host
   *                  Kuikly pager id in Kuikly-page-embedded mode).
   * @param nativeRef Kuikly Core render-view tag of the custom view.
   * @param width     New intrinsic width in vp, or `0` to keep the current
   *                  width unchanged.
   * @param height    New intrinsic height in vp, or `0` to keep the current
   *                  height unchanged.
   */
  notifyCustomViewResizeInKmp(pagerId: string, nativeRef: number, width: number, height: number): void {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiNotifyCustomViewResize'];
    if (typeof fn === 'function') {
      (fn as (pagerId: string, nativeRef: number, width: number, height: number) => void)(
        pagerId, nativeRef, width, height
      );
    }
  }

  // --- A2UI v1.0 RPC facade -------------------------------------------
  //
  // The following six wrappers mirror the ones above but target the RPC
  // NAPI exports added in PR-6a / PR-6e. They are deliberately safe
  // no-ops / `false`-returns on older `libkuikly_entry.so` builds that
  // predate those exports, so host apps can version-probe cheaply.
  //
  // Hosts should prefer the higher-level `A2UISurfaceViewController`
  // instance methods (`setRendererFunctionHandler`, `respondToRpc`,
  // `failRpc`, `invokeAgentFunction`) over calling these wrappers
  // directly; the controller takes care of instanceId plumbing and
  // deferred handler flushing on controller-recreate.

  /**
   * Register an inbound `callRendererFunction` handler for `instanceId`.
   * Returns `true` if the NAPI export exists (and therefore the call was
   * forwarded into the napi shim); `false` otherwise.
   *
   * See `a2uiSetRendererFunctionHandler` in `napi_init.cpp` for the
   * tsfn-based thread-hop semantics.
   */
  setRendererFunctionHandlerInKmp(
    instanceId: string,
    onCall: (requestJson: string) => void
  ): boolean {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiSetRendererFunctionHandler'];
    if (typeof fn !== 'function') {
      return false;
    }
    (fn as (instanceId: string, onCall: (requestJson: string) => void) => void)(
      instanceId, onCall
    );
    return true;
  }

  /**
   * Cheap existence probe for the `a2uiSetRendererFunctionHandler` /
   * `a2uiRemoveRendererFunctionHandler` pair. Lets the controller tell
   * its caller `false` on older `libkuikly_entry.so` builds without
   * importing the `.so` module itself.
   */
  isRendererFunctionHandlerSupportedInKmp(): boolean {
    const napi = Napi as Record<string, Object>;
    return typeof napi['a2uiSetRendererFunctionHandler'] === 'function'
        && typeof napi['a2uiRemoveRendererFunctionHandler'] === 'function';
  }

  /**
   * Detach the inbound `callRendererFunction` handler previously
   * registered via [setRendererFunctionHandlerInKmp]. Idempotent on both
   * sides: calling this on an instance without a registered handler is a
   * no-op. Returns `true` if the NAPI export exists.
   */
  removeRendererFunctionHandlerInKmp(instanceId: string): boolean {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiRemoveRendererFunctionHandler'];
    if (typeof fn !== 'function') {
      return false;
    }
    (fn as (instanceId: string) => void)(instanceId);
    return true;
  }

  /**
   * Ack an inbound `callRendererFunction` with a success payload. See
   * `A2UIBridgeManager.respondToAgentJson` on the KMP side.
   */
  respondToRpcInKmp(
    instanceId: string, surfaceId: string, requestId: string, resultJson: string
  ): boolean {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiRespondToRpc'];
    if (typeof fn !== 'function') {
      return false;
    }
    (fn as (instanceId: string, surfaceId: string, requestId: string, resultJson: string) => void)(
      instanceId, surfaceId, requestId, resultJson
    );
    return true;
  }

  /**
   * Fail an inbound `callRendererFunction` with an error code + message.
   * See `A2UIBridgeManager.failAgentRequestJson` on the KMP side.
   */
  failRpcInKmp(
    instanceId: string, surfaceId: string, requestId: string,
    code: string, message: string, detailsJson: string
  ): boolean {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiFailRpc'];
    if (typeof fn !== 'function') {
      return false;
    }
    (fn as (
      instanceId: string, surfaceId: string, requestId: string,
      code: string, message: string, detailsJson: string
    ) => void)(instanceId, surfaceId, requestId, code, message, detailsJson);
    return true;
  }

  /**
   * Issue an outbound `callAgentFunction` and get the agent's completion
   * JSON back via `onComplete`. Returns the minted requestId on success,
   * `null` if the NAPI export does not exist or the KMP side refused to
   * mint one (no transport bound).
   *
   * See `a2uiInvokeAgentFunction` in `napi_init.cpp` for the tsfn-based
   * completion plumbing.
   */
  invokeAgentFunctionInKmp(
    instanceId: string, surfaceId: string, name: string,
    argsJson: string, timeoutMs: number,
    onComplete: (responseJson: string) => void
  ): string | null {
    const napi = Napi as Record<string, Object>;
    const fn = napi['a2uiInvokeAgentFunction'];
    if (typeof fn !== 'function') {
      return null;
    }
    const minted = (fn as (
      instanceId: string, surfaceId: string, name: string,
      argsJson: string, timeoutMs: number,
      onComplete: (responseJson: string) => void
    ) => string)(instanceId, surfaceId, name, argsJson, timeoutMs, onComplete);
    return (typeof minted === 'string' && minted.length > 0) ? minted : null;
  }
}

// Export a global singleton instance
export const a2uiNativeManager = new A2UINativeManager();
