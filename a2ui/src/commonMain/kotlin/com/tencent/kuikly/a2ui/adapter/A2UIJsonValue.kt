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

internal object A2UIJsonValue {
    fun serializeToJson(obj: Any?): String? {
        if (obj == null) return null
        return try { buildJsonString(obj) } catch (_: Exception) { null }
    }

    fun buildJsonString(obj: Any?): String = when (obj) {
        null -> "null"
        is String -> "\"${escapeJsonString(obj)}\""
        is Number -> obj.toString()
        is Boolean -> obj.toString()
        is Map<*, *> -> obj.entries.joinToString(",", "{", "}") { (key, value) ->
            "\"${escapeJsonString(key.toString())}\":${buildJsonString(value)}"
        }
        is List<*> -> obj.joinToString(",", "[", "]") { buildJsonString(it) }
        else -> "\"${escapeJsonString(obj.toString())}\""
    }

    private fun escapeJsonString(str: String): String = str.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}
