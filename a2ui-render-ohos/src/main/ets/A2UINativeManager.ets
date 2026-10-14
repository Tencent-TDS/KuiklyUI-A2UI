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
}

// Export a global singleton instance
export const a2uiNativeManager = new A2UINativeManager();
