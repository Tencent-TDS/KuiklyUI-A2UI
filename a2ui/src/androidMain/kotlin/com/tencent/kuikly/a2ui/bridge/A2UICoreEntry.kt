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

/**
 * Java-friendly facade over the v1.0 bidirectional-RPC APIs exposed on
 * [A2UIBridgeManager] (PR-6b).
 *
 * Why a separate class? `A2UIBridgeManager` is a Kotlin `object` whose
 * JSON-flavored RPC methods accept Kotlin function types
 * (`(String) -> Unit`) \u2014 these are callable from Java but awkward,
 * requiring verbose `Function1<...>` lambdas. This facade re-exposes
 * the four entry points through a pair of explicit SAM interfaces
 * ([Handler], [Completion]) that Java callers can implement with
 * ordinary `::methodRef` syntax or Java 8 lambdas.
 *
 * Idiomatic Java usage:
 * ```java
 * A2UIRpcBridge.get().setHandler(instanceId, envelopeJson -> {
 *     MyHostFunctions.dispatch(envelopeJson);
 * });
 *
 * String reqId = A2UIRpcBridge.get().invoke(
 *     instanceId, surfaceId, "fetchUser", "{\"id\":42}", 5_000L,
 *     responseJson -> MyHost.onAgentReply(responseJson)
 * );
 * ```
 *
 * All methods are thread-safe wrt. their delegate on the KMP side: the
 * underlying `A2UIBridgeManager` is a stateful `object` that assumes
 * the Kuikly context thread; callers are expected to invoke us on
 * that thread (the same thread that drives
 * [A2UICoreEntry.callKotlinMethod]). Hosts that run on a different
 * thread must hop themselves before calling in.
 */
class A2UIRpcBridge private constructor() {

    /** SAM interface for inbound agent-to-renderer RPC envelopes. */
    fun interface Handler {
        /** Called with the JSON envelope. Never `null`. */
        fun onCall(requestJson: String)
    }

    /** SAM interface for the response of a renderer-to-agent RPC. */
    fun interface Completion {
        /** Called with the JSON response envelope exactly once. */
        fun onComplete(responseJson: String)
    }

    fun setHandler(instanceId: String, handler: Handler) {
        A2UIBridgeManager.setRendererFunctionJsonHandler(instanceId) { json ->
            handler.onCall(json)
        }
    }

    fun removeHandler(instanceId: String) {
        A2UIBridgeManager.setRendererFunctionJsonHandler(instanceId, null)
    }

    /**
     * Close an inbound RPC successfully. [resultJson] may be `null` or
     * empty when the function has no return value (void).
     */
    fun respond(
        instanceId: String,
        surfaceId: String,
        requestId: String,
        resultJson: String?,
    ) {
        A2UIBridgeManager.respondToAgentJson(
            instanceId = instanceId,
            surfaceId = surfaceId,
            requestId = requestId,
            resultJson = resultJson,
        )
    }

    /** Close an inbound RPC with an error. */
    @JvmOverloads
    fun fail(
        instanceId: String,
        surfaceId: String,
        requestId: String,
        code: String,
        message: String? = null,
        detailsJson: String? = null,
    ) {
        A2UIBridgeManager.failAgentRequestJson(
            instanceId = instanceId,
            surfaceId = surfaceId,
            requestId = requestId,
            code = code,
            message = message,
            detailsJson = detailsJson,
        )
    }

    /**
     * Fire a renderer-to-agent RPC. Returns the minted requestId (or
     * `null` when the instance is unknown / not yet on v1.0). A
     * non-positive [timeoutMs] means "use the dispatcher default".
     */
    @JvmOverloads
    fun invoke(
        instanceId: String,
        surfaceId: String,
        name: String,
        argsJson: String?,
        timeoutMs: Long = 0L,
        onComplete: Completion,
    ): String? {
        return A2UIBridgeManager.invokeAgentFunctionJson(
            instanceId = instanceId,
            surfaceId = surfaceId,
            name = name,
            argsJson = argsJson,
            timeoutMs = timeoutMs.takeIf { it > 0L },
        ) { responseJson ->
            onComplete.onComplete(responseJson)
        }
    }

    companion object {
        private val INSTANCE: A2UIRpcBridge = A2UIRpcBridge()

        @JvmStatic
        fun get(): A2UIRpcBridge = INSTANCE
    }
}
