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

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped

/** Shared OHOS RPC transport implementation used by each library's root ABI. */
public fun a2uiRespondToRpc(
    instanceId: String,
    surfaceId: String,
    requestId: String,
    resultJson: String
) {
    A2UIBridgeManager.respondToAgentJson(
        instanceId = instanceId,
        surfaceId = surfaceId,
        requestId = requestId,
        resultJson = resultJson.takeIf { it.isNotEmpty() }
    )
}

public fun a2uiFailRpc(
    instanceId: String,
    surfaceId: String,
    requestId: String,
    code: String,
    message: String,
    detailsJson: String
) {
    A2UIBridgeManager.failAgentRequestJson(
        instanceId = instanceId,
        surfaceId = surfaceId,
        requestId = requestId,
        code = code,
        message = message.takeIf { it.isNotEmpty() },
        detailsJson = detailsJson.takeIf { it.isNotEmpty() }
    )
}

/**
 * The callback may run on the RPC completion thread. Its JSON buffer is borrowed
 * for the duration of the call; the NAPI shim must copy it and dispatch to ArkTS.
 * The shim owns callbackCtx. A null return means no request was dispatched.
 */
@OptIn(ExperimentalForeignApi::class)
public fun a2uiInvokeAgentFunction(
    instanceId: String,
    surfaceId: String,
    name: String,
    argsJson: String,
    timeoutMs: Long,
    callbackCtx: COpaquePointer?,
    onComplete: CPointer<CFunction<(COpaquePointer?, CPointer<ByteVar>?) -> Unit>>?
): String? {
    return A2UIBridgeManager.invokeAgentFunctionJson(
        instanceId = instanceId,
        surfaceId = surfaceId,
        name = name,
        argsJson = argsJson.takeIf { it.isNotEmpty() },
        timeoutMs = timeoutMs.takeIf { it > 0L }
    ) { responseJson ->
        if (onComplete != null) {
            memScoped {
                val cstr = responseJson.cstr.ptr
                onComplete.invoke(callbackCtx, cstr)
            }
        }
    }
}

/** Shares the callback thread, borrowed-buffer and context ownership contract above. */
@OptIn(ExperimentalForeignApi::class)
public fun a2uiSetRendererFunctionHandler(
    instanceId: String,
    callbackCtx: COpaquePointer?,
    onCall: CPointer<CFunction<(COpaquePointer?, CPointer<ByteVar>?) -> Unit>>?
) {
    if (onCall == null) {
        A2UIBridgeManager.setRendererFunctionJsonHandler(instanceId, null)
        return
    }
    A2UIBridgeManager.setRendererFunctionJsonHandler(instanceId) { requestJson ->
        memScoped {
            val cstr = requestJson.cstr.ptr
            onCall.invoke(callbackCtx, cstr)
        }
    }
}

public fun a2uiRemoveRendererFunctionHandler(instanceId: String) {
    A2UIBridgeManager.setRendererFunctionJsonHandler(instanceId, null)
}
