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
 * Structured error attached to a failed [A2UIRpcResponse].
 *
 * The [code] vocabulary is intentionally minimal — see [Codes] for the
 * built-in whitelist — but hosts MAY introduce extra codes using the
 * `x-` prefix (e.g. `x-auth-required`). The dispatcher does not reject
 * unknown codes; it simply forwards them, so adopters can evolve their
 * error vocabulary without needing a protocol change.
 *
 * [message] is a human-readable hint and SHOULD be safe to log. Keep
 * user-visible strings on the host side; this field exists mainly for
 * diagnostics. [details] is a free-form map for extra structured
 * context (stack frame hints, upstream error codes, …) that the host
 * wants to pass through.
 */
data class A2UIRpcError(
    val code: String,
    val message: String?,
    val details: Map<String, Any?>? = null,
) {
    companion object {
        /** Factory for a canonical [Codes.UNKNOWN_FUNCTION] error. */
        fun unknownFunction(name: String): A2UIRpcError = A2UIRpcError(
            code = Codes.UNKNOWN_FUNCTION,
            message = "Function '$name' is not registered on the renderer side.",
        )

        /** Factory for a canonical [Codes.TIMEOUT] error. */
        fun timeout(timeoutMs: Long): A2UIRpcError = A2UIRpcError(
            code = Codes.TIMEOUT,
            message = "No response received within ${timeoutMs}ms.",
        )

        /** Factory for a canonical [Codes.SURFACE_CLOSED] error. */
        fun surfaceClosed(): A2UIRpcError = A2UIRpcError(
            code = Codes.SURFACE_CLOSED,
            message = "Surface was destroyed before the RPC completed.",
        )

        /** Factory for a canonical [Codes.WIRE_FORMAT_ERROR] error. */
        fun wireFormat(detail: String): A2UIRpcError = A2UIRpcError(
            code = Codes.WIRE_FORMAT_ERROR,
            message = detail,
        )

        /** Factory for a canonical [Codes.INVALID_ARGS] error. */
        fun invalidArgs(detail: String): A2UIRpcError = A2UIRpcError(
            code = Codes.INVALID_ARGS,
            message = detail,
        )

        /** Factory for a canonical [Codes.INTERNAL_ERROR] error. */
        fun internalError(detail: String): A2UIRpcError = A2UIRpcError(
            code = Codes.INTERNAL_ERROR,
            message = detail,
        )

        /** Factory for a canonical [Codes.USER_CANCELLED] error. */
        fun userCancelled(detail: String? = null): A2UIRpcError = A2UIRpcError(
            code = Codes.USER_CANCELLED,
            message = detail ?: "User cancelled the operation.",
        )
    }

    /**
     * Built-in error codes. Mirrors the vocabulary defined in the
     * `evolution_guide.md` §Errors section. Hosts may introduce extra
     * codes using the `x-` prefix (dispatcher does NOT whitelist, so
     * these constants are only for convenience / IDE autocompletion).
     */
    object Codes {
        /** `name` not registered on the receiving side. */
        const val UNKNOWN_FUNCTION = "UNKNOWN_FUNCTION"

        /** Arguments failed schema / type validation. */
        const val INVALID_ARGS = "INVALID_ARGS"

        /** End user explicitly cancelled a renderer-side interaction. */
        const val USER_CANCELLED = "USER_CANCELLED"

        /** Deadline elapsed without a response. */
        const val TIMEOUT = "TIMEOUT"

        /** Unexpected exception on the receiving side. */
        const val INTERNAL_ERROR = "INTERNAL_ERROR"

        /** Envelope was rejected before dispatch (missing field / shape mismatch). */
        const val WIRE_FORMAT_ERROR = "WIRE_FORMAT_ERROR"

        /** Surface was torn down before the RPC completed. */
        const val SURFACE_CLOSED = "SURFACE_CLOSED"
    }
}
