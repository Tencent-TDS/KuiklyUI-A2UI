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

package com.tencent.kuikly.a2ui.render.android

import android.util.Log
import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.export.KuiklyRenderCallback

/**
 * Kuikly module that receives A2UI-specific requests from the KMP core via
 * the standard `BridgeManager.callModuleMethod` channel. Mirror of the
 * HarmonyOS `A2UIBridgeModule` (see `a2ui-render-ohos/.../A2UISurfaceView.ets`).
 *
 * Hosting modes
 * -------------
 * - **Native-page mode** (standalone [A2UISurfaceView]): The surface owns
 *   its own `A2UICoreEntry.Delegate` and dispatches methodId=103/106/107
 *   directly in `callNative(…)`. This module is NOT exercised there.
 * - **Kuikly-page-embedded mode** (`A2UISurfaceDSLView` inside a host
 *   `KuiklyBaseView`): The host page's render core has no receiver for
 *   methodId=103/106/107, so the KMP core additionally routes those
 *   requests through `callModuleMethod("A2UIBridgeModule", …)`. This
 *   module is installed on the host page's delegate by
 *   [A2UIKuiklyBaseViewSupport.wrap].
 *
 * Wire format
 * -----------
 * All payloads are plain strings so they can cross the Kuikly module bus
 * unchanged. Multi-field payloads use `'\n'` as separator — identical to
 * the format used by the OHOS counterpart (see
 * `a2ui-render-ohos/src/main/ets/A2UISurfaceView.ets:A2UIBridgeModule`):
 *
 * | method                 | params                                   | sync |
 * |------------------------|------------------------------------------|------|
 * | contentHeightChanged   | height (Number)                          | 1    |
 * | actionCallback         | "<tag>\n<actionJson>"                    | 1    |
 * | showDateTimePicker     | configJson (contains tag / dslInstanceId)| 1    |
 * | executeFunctionCall    | "<functionName>\n<argsJson>"             | 1    |
 * | invokeCustomFunction   | "<name>\n<spec>\n<argsJson>"             | 0/1  |
 * | debugLog               | message (String)                         | 0    |
 *
 * Threading
 * ---------
 * `call(…)` runs on the Kuikly Context Thread. UI work (dialogs,
 * Intents, Toasts, …) is deferred to the Android main thread by the
 * shared utility methods on [A2UIAndroidNativeMethodHandler].
 */
class A2UIBridgeModule : KuiklyRenderBaseModule() {

    override fun call(method: String, params: Any?, callback: KuiklyRenderCallback?): Any? {
        return when (method) {
            METHOD_CONTENT_HEIGHT_CHANGED -> {
                // No-op on Android: the host Kuikly page already drives the
                // A2UISurfaceDSLView's intrinsic height through Flex layout,
                // and standalone surfaces listen to CONTENT_HEIGHT_CHANGED
                // (methodId=102) directly in their own callNative switch.
                // Still accepted here so the KMP-side call site doesn't need
                // to distinguish between modes.
                null
            }
            METHOD_ACTION_CALLBACK -> {
                // No-op on Android: in Kuikly-page-embedded mode the host
                // forwards action callbacks via A2UISurfaceDSLView's own
                // listener wiring (see A2UISurfaceDSLView.wireBridgeCallbacks).
                null
            }
            METHOD_SHOW_DATE_TIME_PICKER -> {
                val configJson = params as? String ?: return null
                handleShowDateTimePicker(configJson)
                null
            }
            METHOD_EXECUTE_FUNCTION_CALL -> {
                val payload = params as? String ?: return null
                handleExecuteFunctionCall(payload)
                null
            }
            METHOD_INVOKE_CUSTOM_FUNCTION -> {
                val payload = params as? String ?: return null
                handleInvokeCustomFunction(payload)
            }
            METHOD_DEBUG_LOG -> {
                val msg = params as? String ?: return null
                Log.d(TAG, msg)
                null
            }
            else -> super.call(method, params, callback)
        }
    }

    /**
     * Parse the picker config JSON and pop the native dialog. The config
     * includes `tag` and `dslInstanceId` so the picker result can be
     * routed back to the correct DSL bridge (see
     * [A2UIAndroidNativeMethodHandler.sendPickerResult]).
     */
    private fun handleShowDateTimePicker(configJson: String) {
        try {
            val obj = org.json.JSONObject(configJson)
            val tag = obj.optInt("tag", -1)
            val dslInstanceId = obj.optString("dslInstanceId", "")
            if (tag < 0 || dslInstanceId.isEmpty()) {
                Log.w(TAG, "showDateTimePicker: missing tag / dslInstanceId in $configJson")
                return
            }
            // No per-module Context: fall back to the resumed Activity
            // tracked by A2UIAndroidNativeMethodHandler.install.
            A2UIAndroidNativeMethodHandler.showDateTimePickerDialog(
                callerContext = null,
                instanceId = dslInstanceId,
                tag = tag,
                configJson = configJson
            )
        } catch (e: Exception) {
            Log.e(TAG, "showDateTimePicker failed: ${e.message}", e)
        }
    }

    /** Payload: `"<functionName>\n<argsJson>"`. */
    private fun handleExecuteFunctionCall(payload: String) {
        val sep = payload.indexOf('\n')
        if (sep < 0) {
            Log.w(TAG, "executeFunctionCall: malformed payload (no '\\n')")
            return
        }
        val functionName = payload.substring(0, sep)
        val argsJson = payload.substring(sep + 1)
        A2UIAndroidNativeMethodHandler.executeFunctionCall(
            callerContext = null,
            functionName = functionName,
            argsJson = argsJson
        )
    }

    /**
     * Payload: `"<name>\n<spec>\n<argsJson>"`.
     *
     * Returns the raw platform value (String / Boolean / Number / JSON-
     * encoded array String / null) so the DSL applier can coerce it to the
     * descriptor's declared return type.
     */
    private fun handleInvokeCustomFunction(payload: String): Any? {
        val first = payload.indexOf('\n')
        val second = if (first >= 0) payload.indexOf('\n', first + 1) else -1
        if (first < 0 || second < 0) {
            Log.w(TAG, "invokeCustomFunction: malformed payload (need two '\\n')")
            return null
        }
        val name = payload.substring(0, first)
        val spec = payload.substring(first + 1, second)
        val argsJson = payload.substring(second + 1)
        return A2UIAndroidCustomFunctionRegistry.invokeLocally(name, argsJson, spec)
    }

    companion object {
        private const val TAG = "A2UIBridgeModule"

        const val MODULE_NAME = "A2UIBridgeModule"

        private const val METHOD_CONTENT_HEIGHT_CHANGED = "contentHeightChanged"
        private const val METHOD_ACTION_CALLBACK = "actionCallback"
        private const val METHOD_SHOW_DATE_TIME_PICKER = "showDateTimePicker"
        private const val METHOD_EXECUTE_FUNCTION_CALL = "executeFunctionCall"
        private const val METHOD_INVOKE_CUSTOM_FUNCTION = "invokeCustomFunction"
        private const val METHOD_DEBUG_LOG = "debugLog"
    }
}
