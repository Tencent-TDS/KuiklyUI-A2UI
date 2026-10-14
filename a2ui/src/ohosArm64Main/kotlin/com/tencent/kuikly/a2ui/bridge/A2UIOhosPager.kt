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
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.utils.toKRRenderCValue
import com.tencent.kuikly.core.utils.toAny
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

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
            return memScoped {
                // 与 KSP 生成的 initKuikly 保持一致：直接在 arena 上 alloc + 就地填充，
                // 避免 cValue<T> 产生的中间 ByteArray；额外 alloc 一个 result slot 承接
                // 新版 com_tencent_kuikly_CallNative 的出参。
                val cv0 = alloc<ohos.KRRenderCValue>(); arg0.toKRRenderCValue(this, cv0)
                val cv1 = alloc<ohos.KRRenderCValue>(); arg1.toKRRenderCValue(this, cv1)
                val cv2 = alloc<ohos.KRRenderCValue>(); arg2.toKRRenderCValue(this, cv2)
                val cv3 = alloc<ohos.KRRenderCValue>(); arg3.toKRRenderCValue(this, cv3)
                val cv4 = alloc<ohos.KRRenderCValue>(); arg4.toKRRenderCValue(this, cv4)
                val cv5 = alloc<ohos.KRRenderCValue>(); arg5.toKRRenderCValue(this, cv5)
                val result = alloc<ohos.KRRenderCValue>()
                ohos.com_tencent_kuikly_CallNative(
                    methodId,
                    cv0.ptr,
                    cv1.ptr,
                    cv2.ptr,
                    cv3.ptr,
                    cv4.ptr,
                    cv5.ptr,
                    result.ptr
                )
                result.toAny()
            }
        }
    }
}