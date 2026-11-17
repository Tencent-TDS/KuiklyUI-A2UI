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

import com.tencent.kuikly.a2ui.bridge.a2uiRespondToRpc as respondToRpc
import com.tencent.kuikly.a2ui.bridge.a2uiFailRpc as failRpc
import com.tencent.kuikly.a2ui.bridge.a2uiInvokeAgentFunction as invokeAgentFunction
import com.tencent.kuikly.a2ui.bridge.a2uiSetRendererFunctionHandler as setRendererFunctionHandler
import com.tencent.kuikly.a2ui.bridge.a2uiRemoveRendererFunctionHandler as removeRendererFunctionHandler
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

/** Root-package ABI exported by the full demo OHOS shared library. */
public fun a2uiRespondToRpc(instanceId: String, surfaceId: String, requestId: String, resultJson: String) {
    respondToRpc(instanceId, surfaceId, requestId, resultJson)
}

public fun a2uiFailRpc(
    instanceId: String, surfaceId: String, requestId: String,
    code: String, message: String, detailsJson: String
) {
    failRpc(instanceId, surfaceId, requestId, code, message, detailsJson)
}

@OptIn(ExperimentalForeignApi::class)
public fun a2uiInvokeAgentFunction(
    instanceId: String, surfaceId: String, name: String, argsJson: String,
    timeoutMs: Long, callbackCtx: COpaquePointer?,
    onComplete: CPointer<CFunction<(COpaquePointer?, CPointer<ByteVar>?) -> Unit>>?
): String? = invokeAgentFunction(instanceId, surfaceId, name, argsJson, timeoutMs, callbackCtx, onComplete)

@OptIn(ExperimentalForeignApi::class)
public fun a2uiSetRendererFunctionHandler(
    instanceId: String, callbackCtx: COpaquePointer?,
    onCall: CPointer<CFunction<(COpaquePointer?, CPointer<ByteVar>?) -> Unit>>?
) {
    setRendererFunctionHandler(instanceId, callbackCtx, onCall)
}

public fun a2uiRemoveRendererFunctionHandler(instanceId: String) {
    removeRendererFunctionHandler(instanceId)
}
