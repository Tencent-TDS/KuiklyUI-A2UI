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

// No package declaration - this must be in the root package so that
// C++ can call it via api->kotlin.root.initKuikly()

import com.tencent.kuikly.a2ui.bridge.A2UICoreEntry
import com.tencent.kuikly.a2ui.bridge.A2UIOhosPager
import com.tencent.kuikly.core.exception.ExceptionTracker
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.KotlinMethod
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.utils.toKRRenderCValue
import com.tencent.kuikly.core.utils.toAny
import com.tencent.kuikly.core.utils.asString
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.staticCFunction
import kotlin.experimental.ExperimentalNativeApi

/**
 * A2UI Kuikly Entry for HarmonyOS (OHOS).
 *
 * This file provides the `initKuikly` function that is called by the C++ layer
 * (via libshared_api.h) during app initialization. It mirrors the KSP-generated
 * entry code for standard Kuikly DSL pages.
 *
 * Flow:
 * 1. C++ napi_init.cpp: InitKuikly() → api->kotlin.root.initKuikly()
 * 2. This function: BridgeManager.init() + register "a2ui" page route
 * 3. com_tencent_kuikly_SetCallKotlin registers the callKotlin callback
 * 4. When KRNativeRender(pageName="a2ui") is created:
 *    - C++ calls callKotlin(CREATE_INSTANCE, pagerId, "a2ui", pageData, ...)
 *    - callKotlin creates NativeBridge and registers it
 *    - BridgeManager.callKotlinMethod → PagerManager.createPager("a2ui") → A2UIOhosPager
 */

@OptIn(ExperimentalForeignApi::class)
private fun callNative(
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

/**
 * Entry point called by C++ layer to initialize Kuikly for A2UI.
 *
 * This function:
 * 1. Initializes BridgeManager (if not already initialized)
 * 2. Registers the "a2ui" page route pointing to A2UIOhosPager
 * 3. Registers the callKotlin callback with the C++ render core
 *
 * @return The handler ID from com_tencent_kuikly_SetCallKotlin
 */
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("initKuikly")
fun initKuikly(): Int {
    if (!BridgeManager.isDidInit()) {
        BridgeManager.init(true)
        BridgeManager.registerPageRouter(A2UICoreEntry.PAGE_NAME) { A2UIOhosPager() }
    }

    return ohos.com_tencent_kuikly_SetCallKotlin(staticCFunction { methodId, arg0, arg1, arg2, arg3, arg4, arg5 ->
        val callKotlinClosure = {
            if (methodId == KotlinMethod.CREATE_INSTANCE) {
                val nativeBridge = NativeBridge()
                nativeBridge.callNativeCallback = { methodId, arg0, arg1, arg2, arg3, arg4, arg5 ->
                    callNative(methodId, arg0, arg1, arg2, arg3, arg4, arg5)
                }
                BridgeManager.registerNativeBridge(arg0.asString(), nativeBridge)
            }
            BridgeManager.callKotlinMethod(
                methodId,
                arg0.toAny(),
                arg1.toAny(),
                arg2.toAny(),
                arg3.toAny(),
                arg4.toAny(),
                arg5.toAny()
            )
        }

        if (BridgeManager.catchException) {
            try {
                callKotlinClosure()
            } catch (t: Throwable) {
                ExceptionTracker.notifyKuiklyException(t)
            }
        } else {
            callKotlinClosure()
        }
    })
}
