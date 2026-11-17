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

package com.tencent.kuikly.a2ui.rpc

/**
 * Terminal response for an [A2UIRpcRequest].
 *
 * Exactly ONE of [result] / [error] must be non-null — the invariant is
 * enforced by the [success] / [failure] factory methods and MUST be
 * preserved by every code path that constructs a response (wire
 * decoders, timeout handlers, dispatcher internals). Call sites that
 * need to inspect which arm is populated should prefer [isSuccess] over
 * raw null checks so future additions (e.g. partial / streaming) stay
 * easy to spot.
 *
 * [direction] echoes the originating request's direction so a single
 * `onComplete` callback can be reused across both sides.
 */
data class A2UIRpcResponse(
    val direction: A2UIRpcRequest.Direction,
    val surfaceId: String,
    val requestId: String,
    /**
     * Successful return value. `null` is allowed and means "the function
     * returned `null` / `undefined`"; use [isSuccess] / [error] to tell
     * success-with-null-result from a failure.
     */
    val result: Any?,
    /**
     * Non-null iff the call failed. See [A2UIRpcError.Codes] for the
     * built-in code vocabulary and `evolution_guide.md` §Errors for the
     * host-defined extension rules (`x-` prefix).
     */
    val error: A2UIRpcError?,
) {
    /** Short-hand for `error == null`. */
    val isSuccess: Boolean
        get() = error == null

    companion object {
        /** Success case helper. [result] may be `null`. */
        fun success(
            direction: A2UIRpcRequest.Direction,
            surfaceId: String,
            requestId: String,
            result: Any? = null,
        ): A2UIRpcResponse = A2UIRpcResponse(
            direction = direction,
            surfaceId = surfaceId,
            requestId = requestId,
            result = result,
            error = null,
        )

        /** Failure case helper. */
        fun failure(
            direction: A2UIRpcRequest.Direction,
            surfaceId: String,
            requestId: String,
            error: A2UIRpcError,
        ): A2UIRpcResponse = A2UIRpcResponse(
            direction = direction,
            surfaceId = surfaceId,
            requestId = requestId,
            result = null,
            error = error,
        )
    }
}
