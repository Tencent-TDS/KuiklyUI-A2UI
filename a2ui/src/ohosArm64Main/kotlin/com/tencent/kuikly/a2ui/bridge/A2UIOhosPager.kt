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

package com.tencent.kuikly.a2ui.bridge

import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.utils.toKRRenderCValue
import com.tencent.kuikly.core.utils.toAny
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * A2UIOhosPager - HarmonyOS-specific A2UI bootstrap Pager.
 *
 * This Pager is the initial entry point on HarmonyOS. When KRNativeRender creates this Pager
 * (via PagerManager.createPager), it bootstraps the A2UI rendering system.
 *
 * Lifecycle:
 * 1. KRNativeRender initializes → C++ creates this Pager via BridgeManager.callKotlinMethod(CREATE_INSTANCE)
 * 2. onCreatePager() → registers NativeBridge with A2UIBridgeManager → calls INIT
 * 3. A2UIBridgeManager.initInstance() → A2UIDSLBridge.initialize() → creates inner A2UIPager
 * 4. PagerManager.createPager(pagerId, ...) REPLACES this Pager in pagerMap with A2UIPager
 * 5. All subsequent events (UPDATE_INSTANCE) go to A2UIPager.onReceivePagerEvent()
 * 6. A2UIPager forwards events to A2UIBridgeManager for processing
 *
 * After step 4, this bootstrap Pager is no longer referenced by PagerManager.
 * The inner A2UIPager takes over all rendering and event handling.
 *
 * NativeBridge sharing:
 * - The NativeBridge created here uses the SAME callNativeCallback as the one
 *   registered by C++ for this Pager instance (com_tencent_kuikly_CallNative).
 * - When A2UIDSLBridge registers this NativeBridge with Core's BridgeManager,
 *   it overwrites the original registration. This is intentional - the inner
 *   A2UIPager's render commands need to route through the same C++ instance.
 *
 * Marked `open` so downstream modules can supply a `@Page("a2ui")`-annotated
 * subclass that gets registered by KSP into their KuiklyCoreEntry.kt. This
 * matters for the `:demo` KMP output (used by ohosAppForKuiklyPage and
 * ohosAppForBothPages) which needs an explicit `"a2ui"` page route so that
 * `A2UISurfaceView.ets` (which starts a Kuikly page with `pageName:'a2ui'`)
 * can still find a Pager creator. The `:a2ui` output continues to
 * register `"a2ui"` directly via `A2UIKuiklyEntry.kt` for legacy standalone
 * apps (ohosAppForNativePage, ohosAppForTest).
 */
open class A2UIOhosPager : Pager() {

    override fun body(): ViewBuilder = {
        // Empty body - this bootstrap Pager is replaced by A2UIPager during initA2UI()
    }

    override fun onCreatePager(pagerId: String, pagerData: JSONObject) {
        super.onCreatePager(pagerId, pagerData)
        initA2UI(pagerId)
    }

    /**
     * Initialize the A2UI bridge system.
     *
     * Creates a NativeBridge that shares the same callNativeCallback as the one
     * C++ set up for this Pager instance, then bootstraps A2UIBridgeManager.
     */
    private fun initA2UI(pagerId: String) {
        // Ensure A2UIBridgeManager is initialized
        if (!A2UIBridgeManager.isDidInit()) {
            A2UIBridgeManager.init()
        }

        // Create a NativeBridge for A2UIBridgeManager.
        // On OHOS, NativeBridge.callNativeCallback is a function pointer to
        // com_tencent_kuikly_CallNative (set by C++ during KRRenderCore initialization).
        // We create a new NativeBridge and set its callNativeCallback to the same
        // global C function. The C++ layer routes calls by instanceId (arg0).
        val a2uiNativeBridge = NativeBridge()
        a2uiNativeBridge.callNativeCallback = getCallNativeCallback()

        // Register with A2UIBridgeManager
        A2UIBridgeManager.registerNativeBridge(pagerId, a2uiNativeBridge)

        // Initialize the A2UI instance.
        // This will:
        // 1. Create A2UIDSLBridge
        // 2. Register NativeBridge with Core's BridgeManager (overwrites ours)
        // 3. Create inner A2UIPager via PagerManager.createPager (replaces this Pager in pagerMap)
        // After this, all events go to A2UIPager.onReceivePagerEvent()
        // Pass device screen size from this Pager's pageData (set by KRNativeRenderController.createPageParams)
        // so that A2UIPager can size full-screen overlays (Modal) correctly.
        val configJson = if (pageData.deviceWidth > 0f || pageData.deviceHeight > 0f) {
            "{\"deviceWidth\":${pageData.deviceWidth},\"deviceHeight\":${pageData.deviceHeight}}"
        } else {
            null
        }
        A2UIBridgeManager.callKotlinMethod(A2UIKotlinMethod.INIT, pagerId, configJson, null)
    }

    companion object {
        /**
         * Get the callNativeCallback function that routes to C++'s com_tencent_kuikly_CallNative.
         *
         * On OHOS/Kotlin-Native, this is the global C function exported by core-render-ohos.
         * All NativeBridge instances on OHOS use this same callback - the C++ layer
         * distinguishes instances by the instanceId in arg0.
         */
        internal fun getCallNativeCallback(): (Int, Any?, Any?, Any?, Any?, Any?, Any?) -> Any? {
            return { methodId, arg0, arg1, arg2, arg3, arg4, arg5 ->
                callNativeImpl(methodId, arg0, arg1, arg2, arg3, arg4, arg5)
            }
        }

        /**
         * Call the C++ native function com_tencent_kuikly_CallNative.
         * This is the Kotlin/Native → C++ bridge for render commands.
         *
         * On OHOS, Kuikly's native (C++/NAPI) layer does NOT dispatch
         * A2UI-specific methodIds (`SHOW_DATE_TIME_PICKER = 103`,
         * `EXECUTE_FUNCTION_CALL = 106`, `INVOKE_CUSTOM_FUNCTION = 107`)
         * — unlike Android/iOS which intercept them inside each
         * `A2UISurfaceView.callNative(…)`. Those requests would otherwise
         * reach `com_tencent_kuikly_CallNative` with no handler and
         * silently return `null` (which was the root cause of
         * `getGreeting / formatPhone / isVip / calculateDiscount /
         * buildSummary` custom functions rendering as empty text in the
         * OHOS Native-page demo).
         *
         * Fix: pre-dispatch those methodIds here through the standard
         * Kuikly Module channel (`BridgeManager.callModuleMethod`,
         * `syncCall = 1`), targeting the `A2UIBridgeModule` that
         * `A2UISurfaceView.ets`'s own `getCustomRenderModuleCreatorRegisterMap`
         * registers against the enclosed Kuikly delegate (same module
         * `A2UIKuiklyRenderViewSupport.wrap(...)` uses for Kuikly-page-
         * embedded mode). This mirrors — in KMP space — what Android/iOS
         * do inside `A2UISurfaceView.callNative(…)` and is OHOS's single
         * canonical dispatch point for the three methodIds.
         */
        @OptIn(ExperimentalForeignApi::class)
        private fun callNativeImpl(
            methodId: Int,
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?
        ): Any? {
            // ------------------------------------------------------------------
            // OHOS-only: route A2UI-extended methodIds through the Module bus.
            // ------------------------------------------------------------------
            when (methodId) {
                A2UINativeMethod.SHOW_DATE_TIME_PICKER -> {
                    // arg0=instanceId, arg1=tag(Int), arg2=configJson(String)
                    val instanceId = arg0 as? String ?: return null
                    val configJson = arg2 as? String ?: return null
                    return try {
                        BridgeManager.callModuleMethod(
                            instanceId,
                            "A2UIBridgeModule",
                            "showDateTimePicker",
                            configJson,
                            null,
                            1
                        )
                    } catch (_: Throwable) {
                        null
                    }
                }
                A2UINativeMethod.EXECUTE_FUNCTION_CALL -> {
                    // arg0=instanceId, arg1=name, arg2=argsJson
                    val instanceId = arg0 as? String ?: return null
                    val name = arg1 as? String ?: return null
                    val argsJson = arg2 as? String ?: "{}"
                    return try {
                        BridgeManager.callModuleMethod(
                            instanceId,
                            "A2UIBridgeModule",
                            "executeFunctionCall",
                            "$name\n$argsJson",
                            null,
                            1
                        )
                    } catch (_: Throwable) {
                        null
                    }
                }
                A2UINativeMethod.INVOKE_CUSTOM_FUNCTION -> {
                    // arg0=instanceId, arg1=name, arg2=argsJson, arg3="returnType|invokeMode"
                    val instanceId = arg0 as? String ?: return null
                    val name = arg1 as? String ?: return null
                    val argsJson = arg2 as? String ?: "{}"
                    val spec = arg3 as? String ?: return null
                    return try {
                        BridgeManager.callModuleMethod(
                            instanceId,
                            "A2UIBridgeModule",
                            "invokeCustomFunction",
                            // Payload format identical to the Native-page
                            // Kuikly-page module path on all three platforms:
                            // "name\nspec\nargsJson".
                            "$name\n$spec\n$argsJson",
                            null,
                            1
                        )
                    } catch (_: Throwable) {
                        null
                    }
                }
            }
            // Mirrors the KSP-generated callNative (see kuikly
            // core-ksp/.../impl/OhOsTargetEntryBuilder.kt). Each argument is
            // encoded via `toKRRenderCValue()` (returns a KRRenderCValue by
            // value), handed to the 7-arg `com_tencent_kuikly_CallNative`, and
            // released in `finally` with `KRJSONRelease` to prevent native
            // leaks on exception.
            var cv0 = ohos.KRJSON_INVALID
            var cv1 = ohos.KRJSON_INVALID
            var cv2 = ohos.KRJSON_INVALID
            var cv3 = ohos.KRJSON_INVALID
            var cv4 = ohos.KRJSON_INVALID
            var cv5 = ohos.KRJSON_INVALID
            return try {
                cv0 = arg0.toKRRenderCValue()
                cv1 = arg1.toKRRenderCValue()
                cv2 = arg2.toKRRenderCValue()
                cv3 = arg3.toKRRenderCValue()
                cv4 = arg4.toKRRenderCValue()
                cv5 = arg5.toKRRenderCValue()
                ohos.com_tencent_kuikly_CallNative(
                    methodId, cv0, cv1, cv2, cv3, cv4, cv5
                ).toAny()
            } finally {
                ohos.KRJSONRelease(cv0)
                ohos.KRJSONRelease(cv1)
                ohos.KRJSONRelease(cv2)
                ohos.KRJSONRelease(cv3)
                ohos.KRJSONRelease(cv4)
                ohos.KRJSONRelease(cv5)
            }
        }
    }
}