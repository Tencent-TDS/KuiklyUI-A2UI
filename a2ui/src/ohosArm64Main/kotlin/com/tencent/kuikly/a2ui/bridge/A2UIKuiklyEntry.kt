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
    // Mirrors the KSP-generated callNative (see
    // kuikly core-ksp/.../impl/OhOsTargetEntryBuilder.kt). Each argument is
    // encoded via `toKRRenderCValue()` (returns a KRRenderCValue by value),
    // handed to the 7-arg `com_tencent_kuikly_CallNative`, and released in
    // `finally` with `KRJSONRelease` to prevent native leaks on exception.
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
