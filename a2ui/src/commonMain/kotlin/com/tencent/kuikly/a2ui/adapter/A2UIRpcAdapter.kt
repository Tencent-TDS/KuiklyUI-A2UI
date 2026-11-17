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

package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.rpc.A2UIRpcDispatcher
import com.tencent.kuikly.a2ui.rpc.A2UIRpcError
import com.tencent.kuikly.a2ui.rpc.A2UIRpcRequest
import com.tencent.kuikly.a2ui.rpc.A2UIRpcResponse

/** Wire adaptation only; the parent adapter retains dispatcher and stream lifecycle ownership. */
internal class A2UIRpcAdapter(
    private val rpcDispatcher: A2UIRpcDispatcher,
    private val getSurfaceId: () -> String,
    private val getProtocolVersion: () -> A2UIProtocolVersion,
    private val getStrictV10: () -> Boolean,
    private val getIncomingAgentCall: () -> ((A2UIRpcRequest) -> Unit)?,
    private val getEmitWireMessage: () -> ((String) -> Unit)?,
    private val serializeToJson: (Any?) -> String?,
    private val getCatalogId: () -> String? = { null },
) {
    private val currentSurfaceId: String get() = getSurfaceId()
    private val _protocolVersion: A2UIProtocolVersion get() = getProtocolVersion()
    private val strictV10: Boolean get() = getStrictV10()
    private val onIncomingAgentCall: ((A2UIRpcRequest) -> Unit)? get() = getIncomingAgentCall()
    private val emitWireMessage: ((String) -> Unit)? get() = getEmitWireMessage()

    /** Handle an incoming `callRendererFunction` envelope (agent → renderer). */
    fun handleCallRendererFunction(payload: Map<String, Any?>?) {
        val parsed = parseRpcRequest(
            payload = payload,
            direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
            envelopeKey = "callRendererFunction",
        ) ?: return

        // Record the pending entry (for timeout bookkeeping). The host
        // may call `respondToAgent` / `failAgentRequest` later — those
        // paths will look up this requestId again.
        rpcDispatcher.onIncomingAgentCall(parsed)

        val handler = onIncomingAgentCall
        if (handler == null) {
            // No host handler attached → immediately fail back so the
            // agent isn't left waiting. Log once, loudly.
            println(
                "[A2UI] warn: callRendererFunction '${parsed.name}' received " +
                    "but no host handler is installed. Replying with " +
                    "UNKNOWN_FUNCTION. Call " +
                    "A2UIBridgeManager.setRendererFunctionHandler to opt in."
            )
            val resp = rpcDispatcher.failAgentCall(
                surfaceId = parsed.surfaceId,
                requestId = parsed.requestId,
                error = A2UIRpcError.unknownFunction(parsed.name),
            )
            if (resp != null) emitRendererFunctionResponse(resp)
            return
        }
        // Invoke the host asynchronously from the adapter's POV — the
        // host will call `respondToAgent` / `failAgentRequest` when
        // ready. We swallow handler exceptions here so a buggy host
        // can't take the whole stream down; the pending entry still
        // exists and will eventually time out if the host never
        // responds.
        try {
            handler(parsed)
        } catch (t: Throwable) {
            println(
                "[A2UI] error: rendererFunctionHandler for '${parsed.name}' " +
                    "threw: ${t.message}. Failing RPC with INTERNAL_ERROR."
            )
            val resp = rpcDispatcher.failAgentCall(
                surfaceId = parsed.surfaceId,
                requestId = parsed.requestId,
                error = A2UIRpcError.internalError(t.message ?: "handler threw"),
            )
            if (resp != null) emitRendererFunctionResponse(resp)
        }
    }

    /** Handle an incoming `agentFunctionResponse` envelope (reply to renderer-originated call). */
    fun handleAgentFunctionResponse(payload: Map<String, Any?>?) {
        val resp = parseRpcResponse(
            payload = payload,
            direction = A2UIRpcRequest.Direction.RENDERER_TO_AGENT,
            envelopeKey = "agentFunctionResponse",
        ) ?: return

        val entry = rpcDispatcher.onIncomingAgentResponse(resp) ?: return
        try {
            entry.onComplete?.invoke(resp)
        } catch (t: Throwable) {
            println(
                "[A2UI] error: onComplete for requestId='${resp.requestId}' " +
                    "threw: ${t.message}"
            )
        }
    }

    /** Ignore outbound-only envelope keys received on the inbound path. */
    fun handleRendererFunctionResponse(payload: Map<String, Any?>?) {
        if (payload == null) return
        println(
            "[A2UI] warn: envelope key 'rendererFunctionResponse' is an " +
                "outbound-only message from the renderer side; ignoring " +
                "it on the inbound path."
        )
    }

    fun handleCallAgentFunctionIncoming(payload: Map<String, Any?>?) {
        if (payload == null) return
        println(
            "[A2UI] warn: envelope key 'callAgentFunction' is an " +
                "outbound-only message from the renderer side; ignoring " +
                "it on the inbound path."
        )
    }

    /** Emit a host result for an earlier request; no-op after timeout. */
    fun respondToAgent(surfaceId: String, requestId: String, result: Any?) {
        val resp = rpcDispatcher.completeAgentCall(surfaceId, requestId, result)
            ?: return
        emitRendererFunctionResponse(resp)
    }

    /** Failure variant of [respondToAgent]. */
    fun failAgentRequest(
        surfaceId: String,
        requestId: String,
        error: A2UIRpcError,
    ) {
        val resp = rpcDispatcher.failAgentCall(surfaceId, requestId, error) ?: return
        emitRendererFunctionResponse(resp)
    }

    /** Initiate a renderer-originated call and return its opaque requestId. */
    fun invokeAgentFunction(
        surfaceId: String,
        name: String,
        args: Map<String, Any?>,
        timeoutMs: Long?,
        onComplete: (A2UIRpcResponse) -> Unit,
    ): String {
        val req = rpcDispatcher.beginRendererCall(
            surfaceId = surfaceId,
            name = name,
            args = args,
            timeoutMs = timeoutMs,
            onComplete = onComplete,
        )
        if (strictV10) {
            val error = if (surfaceId.isEmpty() || surfaceId != currentSurfaceId) {
                A2UIRpcError.invalidArgs("RPC surfaceId must match the current surface")
            } else validateCatalogCall(name, getCatalogId(), args, A2UIRpcRequest.Direction.RENDERER_TO_AGENT)
            if (error != null) {
                val response = A2UIRpcResponse.failure(req.direction, surfaceId, req.requestId, error)
                val callback = rpcDispatcher.onIncomingAgentResponse(response)?.onComplete
                try {
                    callback?.invoke(response)
                } catch (t: Throwable) {
                    println("[A2UI] error: rejected RPC callback threw: ${t.message}")
                }
                return req.requestId
            }
        }
        emitCallAgentFunction(req)
        return req.requestId
    }

    /** Validate a request; malformed envelopes are diagnosed and dropped. */
    private fun parseRpcRequest(
        payload: Map<String, Any?>?,
        direction: A2UIRpcRequest.Direction,
        envelopeKey: String,
    ): A2UIRpcRequest? {
        if (payload == null) {
            rpcWireError(envelopeKey, "payload is not a Map")
            return null
        }
        if (payload.containsKey("functionCallId") || payload.containsKey("callFunction")) {
            val allowed = setOf("surfaceId", "functionCallId", "callFunction")
            if (strictV10 && payload.keys.any { it !in allowed }) {
                rpcWireError(envelopeKey, "unsupported request properties")
                return null
            }
            val requestId = payload["functionCallId"] as? String
            val call = payload["callFunction"] as? Map<*, *>
            val name = call?.get("@call") as? String
            val catalogId = call?.get("catalogId") as? String
            if (requestId.isNullOrEmpty() || name.isNullOrEmpty() || catalogId.isNullOrEmpty()) {
                rpcWireError(envelopeKey, "functionCallId, @call and catalogId must be strings")
                return null
            }
            if (strictV10 && call.keys.any { it !in setOf("@call", "catalogId", "args") }) {
                rpcWireError(envelopeKey, "unsupported callFunction properties")
                return null
            }
            val surfaceId = if (payload.containsKey("surfaceId")) {
                payload["surfaceId"] as? String ?: run {
                    rpcWireError(envelopeKey, "surfaceId must be a string when present")
                    return null
                }
            } else ""
            if (surfaceId.isNotEmpty() && currentSurfaceId.isNotEmpty() && surfaceId != currentSurfaceId) {
                rpcWireError(envelopeKey, "surfaceId does not match current surface")
                return null
            }
            val args = call["args"]
            if (call.containsKey("args") && args !is Map<*, *>) {
                rpcWireError(envelopeKey, "callFunction.args must be an object")
                return null
            }
            @Suppress("UNCHECKED_CAST")
            val arguments = (args as? Map<String, Any?>) ?: emptyMap()
            if (strictV10) {
                val error = validateCatalogCall(name, catalogId, arguments, direction)
                if (error != null) {
                    emitRendererFunctionResponse(A2UIRpcResponse.failure(direction, surfaceId, requestId, error))
                    return null
                }
            }
            return A2UIRpcRequest(direction, surfaceId, requestId, name, arguments, null)
        }
        if (strictV10) {
            rpcWireError(envelopeKey, "official functionCallId/callFunction request required")
            return null
        }
        val surfaceId = payload["surfaceId"] as? String
        if (surfaceId.isNullOrEmpty()) {
            rpcWireError(envelopeKey, "missing or non-string 'surfaceId'")
            return null
        }
        // Cross-check against adapter's current surface. Mismatch is a
        // strict-mode reject because the dispatcher's pending table is
        // keyed by requestId only; accepting mismatched surfaceIds would
        // make cancel-by-surface unreliable.
        if (currentSurfaceId.isNotEmpty() && surfaceId != currentSurfaceId) {
            rpcWireError(
                envelopeKey,
                "surfaceId '$surfaceId' does not match current '$currentSurfaceId'",
            )
            return null
        }
        val requestId = payload["requestId"] as? String
        if (requestId.isNullOrEmpty()) {
            rpcWireError(envelopeKey, "missing or non-string 'requestId'")
            return null
        }
        val name = payload["name"] as? String
        if (name.isNullOrEmpty()) {
            rpcWireError(envelopeKey, "missing or non-string 'name'")
            return null
        }
        val argsRaw = payload["args"]
        val args: Map<String, Any?> = when (argsRaw) {
            null -> emptyMap()
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                argsRaw as Map<String, Any?>
            }
            else -> {
                rpcWireError(envelopeKey, "'args' must be a Map when present")
                return null
            }
        }
        val timeoutMs = when (val t = payload["timeoutMs"]) {
            null -> null
            is Number -> {
                val v = t.toLong()
                if (v < 0L) {
                    rpcWireError(envelopeKey, "'timeoutMs' must be non-negative")
                    return null
                }
                v
            }
            else -> {
                rpcWireError(envelopeKey, "'timeoutMs' must be a Number")
                return null
            }
        }
        return A2UIRpcRequest(
            direction = direction,
            surfaceId = surfaceId,
            requestId = requestId,
            name = name,
            args = args,
            timeoutMs = timeoutMs,
        )
    }

    /** Malformed result/error combinations complete the call with WIRE_FORMAT_ERROR. */
    private fun parseRpcResponse(
        payload: Map<String, Any?>?,
        direction: A2UIRpcRequest.Direction,
        envelopeKey: String,
    ): A2UIRpcResponse? {
        if (payload == null) {
            rpcWireError(envelopeKey, "payload is not a Map")
            return null
        }
        val official = payload.containsKey("functionCallId")
        if (strictV10 && !official) {
            rpcWireError(envelopeKey, "official functionCallId/value response required")
            return null
        }
        val surfaceId = if (official) currentSurfaceId else payload["surfaceId"] as? String
        if (surfaceId == null || (!official && surfaceId.isEmpty())) {
            rpcWireError(envelopeKey, "missing or non-string 'surfaceId'")
            return null
        }
        val requestId = payload[if (official) "functionCallId" else "requestId"] as? String
        if (requestId.isNullOrEmpty()) {
            rpcWireError(envelopeKey, "missing or non-string 'requestId'")
            return null
        }
        val hasResult = payload.containsKey(if (official) "value" else "result")
        val errorRaw = payload["error"]
        val errorMap = errorRaw as? Map<*, *>
        val unsupported = strictV10 && (payload.keys.any { it !in setOf("functionCallId", "value", "error") } ||
            (errorRaw != null && (errorMap == null || errorMap["code"] !is String ||
                errorMap["message"] !is String || errorMap.keys.any { it !in setOf("code", "message") })))
        if (unsupported || hasResult == payload.containsKey("error")) {
            // Both present or both absent → wire-format violation.
            rpcWireError(
                envelopeKey,
                "exactly one of 'result' / 'error' must be present",
            )
            return A2UIRpcResponse.failure(
                direction = direction,
                surfaceId = surfaceId,
                requestId = requestId,
                error = A2UIRpcError.wireFormat(
                    "$envelopeKey envelope had both or neither of result/error",
                ),
            )
        }
        return if (errorRaw != null) {
            val errMap = errorRaw as? Map<*, *>
            if (errMap == null) {
                rpcWireError(envelopeKey, "'error' must be a Map")
                return A2UIRpcResponse.failure(
                    direction = direction,
                    surfaceId = surfaceId,
                    requestId = requestId,
                    error = A2UIRpcError.wireFormat("error field was not a Map"),
                )
            }
            val code = (errMap["code"] as? String)?.takeIf { it.isNotEmpty() }
                ?: A2UIRpcError.Codes.INTERNAL_ERROR
            val message = errMap["message"] as? String
            @Suppress("UNCHECKED_CAST")
            val details = errMap["details"] as? Map<String, Any?>
            A2UIRpcResponse.failure(
                direction = direction,
                surfaceId = surfaceId,
                requestId = requestId,
                error = A2UIRpcError(code, message, details),
            )
        } else {
            A2UIRpcResponse.success(
                direction = direction,
                surfaceId = surfaceId,
                requestId = requestId,
                result = payload[if (official) "value" else "result"],
            )
        }
    }

    private fun validateCatalogCall(
        name: String,
        catalogId: String?,
        args: Map<String, Any?>,
        direction: A2UIRpcRequest.Direction,
    ): A2UIRpcError? {
        if (catalogId.isNullOrBlank() || !A2UICustomFunctionRegistry.hasCatalog(catalogId)) {
            return A2UIRpcError("UNKNOWN_CATALOG", "RPC catalog '$catalogId' is not registered")
        }
        val descriptor = A2UICustomFunctionRegistry.lookupExact(name, catalogId)
            ?: return A2UIRpcError.unknownFunction(name)
        val allowed = when (direction) {
            A2UIRpcRequest.Direction.AGENT_TO_RENDERER -> descriptor.allowedCallers == "rendererOrAgent"
            A2UIRpcRequest.Direction.RENDERER_TO_AGENT -> descriptor.allowedCallers in setOf("agentOnly", "rendererOrAgent")
        }
        if (!allowed) return A2UIRpcError("FUNCTION_NOT_ALLOWED", "Function '$name' is not allowed in this RPC direction")
        if (descriptor.requiresUserActivation) {
            return A2UIRpcError("USER_ACTIVATION_REQUIRED", "RPC cannot provide trusted user activation for '$name'")
        }
        return descriptor.validateArguments(args)?.let { A2UIRpcError.invalidArgs(it) }
    }

    private fun rpcWireError(envelopeKey: String, detail: String) {
        val prefix = if (strictV10 && _protocolVersion == A2UIProtocolVersion.V1_0) {
            "[A2UI] error"
        } else {
            "[A2UI] warn"
        }
        println("$prefix: v1.0 RPC '$envelopeKey' envelope invalid — $detail")
    }

    private fun emitRendererFunctionResponse(resp: A2UIRpcResponse) {
        val json = serializeToJson(mapOf(
            "version" to A2UIProtocolVersion.V1_0.wireValue,
            "rendererFunctionResponse" to buildResponsePayload(resp),
        )) ?: return
        (emitWireMessage ?: return).invoke(json)
    }

    private fun emitCallAgentFunction(req: A2UIRpcRequest) {
        val payload = mutableMapOf<String, Any?>(
            "surfaceId" to req.surfaceId,
            "requestId" to req.requestId,
            "name" to req.name,
            "args" to req.args,
        )
        req.timeoutMs?.let { payload["timeoutMs"] = it }
        if (strictV10) {
            payload.clear()
            payload["surfaceId"] = req.surfaceId
            payload["functionCallId"] = req.requestId
            val call = mutableMapOf<String, Any?>("@call" to req.name, "args" to req.args)
            getCatalogId()?.let { call["catalogId"] = it }
            payload["callFunction"] = call
        }
        val json = serializeToJson(mapOf(
            "version" to A2UIProtocolVersion.V1_0.wireValue,
            "callAgentFunction" to payload,
        )) ?: return
        (emitWireMessage ?: return).invoke(json)
    }

    private fun buildResponsePayload(resp: A2UIRpcResponse): Map<String, Any?> {
        val base = mutableMapOf<String, Any?>(
            "surfaceId" to resp.surfaceId,
            "requestId" to resp.requestId,
        )
        if (strictV10) {
            base.clear()
            base["functionCallId"] = resp.requestId
        }
        if (resp.isSuccess) {
            base[if (strictV10) "value" else "result"] = resp.result
        } else {
            val err = resp.error!!
            val errMap = mutableMapOf<String, Any?>("code" to err.code)
            if (strictV10) {
                errMap["message"] = err.message ?: err.code
            } else {
                err.message?.let { errMap["message"] = it }
                err.details?.let { errMap["details"] = it }
            }
            base["error"] = errMap
        }
        return base
    }
}
