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

import com.tencent.kuikly.a2ui.parser.SimpleJsonParser

/**
 * Small JSON codec used by the RPC bridge boundary (PR-6b).
 *
 * Why not reuse the private serialiser inside `A2UIProtocolAdapter`?
 *
 *  * The adapter's writer is `private` and lives in a 7k-line file we
 *    do NOT want BridgeManager (and three platform CoreEntry files)
 *    to reach into.
 *  * The RPC boundary only needs a tiny Map<->JSON pair and never sees
 *    circular or custom-typed values.
 *  * Keeping the codec in `com.tencent.kuikly.a2ui.rpc` keeps all RPC
 *    source in one package so future removal / rework is a single
 *    directory delete.
 *
 * Deliberately minimal \u2014 covers only the subset actually produced by
 * host-facing RPC JSON (primitives, strings, maps, lists, nulls).
 * Unknown types fall back to `toString()` wrapped in quotes which is
 * the same conservative behaviour used by the adapter.
 */
internal object A2UIRpcJson {

    /**
     * Encode a Kotlin value graph (typically `Map<String, Any?>`) as
     * a JSON string. Returns `null` only when [value] is `null` to
     * distinguish "nothing to send" from an empty-object/empty-array
     * payload (both of which encode to a non-null string).
     */
    fun stringify(value: Any?): String? {
        if (value == null) return null
        return try {
            buildJsonString(value)
        } catch (e: Throwable) {
            // We intentionally swallow here: the caller is on a
            // bridging hot path (every RPC envelope) and a malformed
            // input should never take the whole stream down. The
            // caller observes failure as `null` and will log it.
            null
        }
    }

    /**
     * Parse a JSON object string into a map. Returns `null` on any
     * failure \u2014 malformed input, non-object root, or unsupported
     * shape \u2014 matching the contract of [SimpleJsonParser.parse].
     *
     * Deliberately delegates to the already-battle-tested parser used
     * by [com.tencent.kuikly.a2ui.parser.A2UIStreamParser] so we do not
     * maintain a second lenient-JSON reader.
     */
    fun parseObject(json: String?): Map<String, Any?>? {
        if (json.isNullOrEmpty()) return null
        return try {
            SimpleJsonParser.parse(json)
        } catch (e: Throwable) {
            null
        }
    }

    private fun buildJsonString(obj: Any?): String = when (obj) {
        null -> "null"
        is String -> "\"${escape(obj)}\""
        is Number -> obj.toString()
        is Boolean -> obj.toString()
        is Map<*, *> -> obj.entries.joinToString(
            separator = ",",
            prefix = "{",
            postfix = "}",
        ) { (k, v) -> "\"${escape(k.toString())}\":${buildJsonString(v)}" }
        is List<*> -> obj.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]",
        ) { buildJsonString(it) }
        else -> "\"${escape(obj.toString())}\""
    }

    private fun escape(str: String): String = str
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}
