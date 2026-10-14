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

import com.tencent.kuikly.core.nvi.NativeBridge

/**
 * Android A2UI Core Entry class.
 *
 * This is the entry point for Native (Android) to call into Kotlin A2UI logic.
 * It mirrors the pattern of KSP-generated KuiklyCoreEntry in Kuikly DSL,
 * but is hand-written since A2UI doesn't need @Page annotation scanning.
 *
 * Each A2UICoreEntry instance represents one A2UI rendering instance.
 * The native layer (A2UISurfaceView) creates one entry per surface view.
 *
 * Usage from Android native:
 * ```kotlin
 * val entry = A2UICoreEntry()
 * entry.delegate = object : A2UICoreEntry.Delegate { ... }
 * entry.callKotlinMethod(A2UIKotlinMethod.INIT, instanceId, configJson, null)
 * entry.callKotlinMethod(A2UIKotlinMethod.WRITE_STREAM, instanceId, chunk, null)
 * entry.callKotlinMethod(A2UIKotlinMethod.FINISH_STREAM, instanceId, null, null)
 * entry.callKotlinMethod(A2UIKotlinMethod.DESTROY, instanceId, null, null)
 * ```
 */
class A2UICoreEntry {

    /**
     * Delegate interface for Kotlin → Native communication.
     * This is the public API that a2ui-render-android implements.
     * It wraps NativeBridge.NativeBridgeDelegate internally.
     */
    interface Delegate {
        fun callNative(
            methodId: Int,
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?
        ): Any?
    }

    /**
     * Delegate for Kotlin → Native communication.
     * Must be set before calling [callKotlinMethod].
     */
    var delegate: Delegate? = null

    private var instanceId: String? = null
    private var hadRegisterNativeBridge = false

    /**
     * Call a Kotlin method from native.
     *
     * @param methodId One of [A2UIKotlinMethod] constants
     * @param arg0 First argument (typically instanceId)
     * @param arg1 Second argument (varies by method)
     * @param arg2 Third argument (varies by method)
     */
    fun callKotlinMethod(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?) {
        // Initialize BridgeManager on first call
        if (!A2UIBridgeManager.isDidInit()) {
            A2UIBridgeManager.init()
        }

        // Register NativeBridge on first call for this instance
        val currentInstanceId = arg0 as? String ?: return
        if (!hadRegisterNativeBridge) {
            hadRegisterNativeBridge = true
            instanceId = currentInstanceId

            val nativeBridge = NativeBridge()
            val entryDelegate = delegate
            nativeBridge.delegate = object : NativeBridge.NativeBridgeDelegate {
                override fun callNative(
                    methodId: Int,
                    arg0: Any?,
                    arg1: Any?,
                    arg2: Any?,
                    arg3: Any?,
                    arg4: Any?,
                    arg5: Any?
                ): Any? {
                    return entryDelegate?.callNative(methodId, arg0, arg1, arg2, arg3, arg4, arg5)
                }
            }
            A2UIBridgeManager.registerNativeBridge(currentInstanceId, nativeBridge)
        }

        // Dispatch to BridgeManager
        A2UIBridgeManager.callKotlinMethod(methodId, arg0, arg1, arg2)
    }

    /**
     * Destroy this entry and clean up.
     */
    fun destroy() {
        instanceId?.let { id ->
            A2UIBridgeManager.callKotlinMethod(A2UIKotlinMethod.DESTROY, id, null, null)
        }
        delegate = null
        instanceId = null
        hadRegisterNativeBridge = false
    }
}
