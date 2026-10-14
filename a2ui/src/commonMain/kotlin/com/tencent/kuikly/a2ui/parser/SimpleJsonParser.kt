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

package com.tencent.kuikly.a2ui.parser

/**
 * A minimal JSON parser that converts JSON strings to Kotlin maps/lists.
 * This avoids external dependencies for basic JSON parsing in commonMain.
 */
object SimpleJsonParser {

    fun parse(json: String): Map<String, Any?>? {
        val trimmed = json.trim()
        if (!trimmed.startsWith("{")) return null
        val result = parseValue(trimmed, 0)
        @Suppress("UNCHECKED_CAST")
        return result.first as? Map<String, Any?>
    }

    private fun parseValue(json: String, startIndex: Int): Pair<Any?, Int> {
        var i = skipWhitespace(json, startIndex)
        if (i >= json.length) return Pair(null, i)

        return when (json[i]) {
            '{' -> parseObject(json, i)
            '[' -> parseArray(json, i)
            '"' -> parseString(json, i)
            't', 'f' -> parseBoolean(json, i)
            'n' -> parseNull(json, i)
            else -> parseNumber(json, i)
        }
    }

    private fun parseObject(json: String, startIndex: Int): Pair<Map<String, Any?>, Int> {
        val map = mutableMapOf<String, Any?>()
        var i = startIndex + 1 // skip '{'

        i = skipWhitespace(json, i)
        if (i < json.length && json[i] == '}') return Pair(map, i + 1)

        while (i < json.length) {
            i = skipWhitespace(json, i)
            if (i >= json.length || json[i] == '}') break

            // Parse key
            val keyResult = parseString(json, i)
            val key = keyResult.first as String
            i = keyResult.second

            // Skip colon
            i = skipWhitespace(json, i)
            if (i < json.length && json[i] == ':') i++

            // Parse value
            val valueResult = parseValue(json, i)
            map[key] = valueResult.first
            i = valueResult.second

            // Skip comma
            i = skipWhitespace(json, i)
            if (i < json.length && json[i] == ',') i++
        }

        if (i < json.length && json[i] == '}') i++
        return Pair(map, i)
    }

    private fun parseArray(json: String, startIndex: Int): Pair<List<Any?>, Int> {
        val list = mutableListOf<Any?>()
        var i = startIndex + 1 // skip '['

        i = skipWhitespace(json, i)
        if (i < json.length && json[i] == ']') return Pair(list, i + 1)

        while (i < json.length) {
            i = skipWhitespace(json, i)
            if (i >= json.length || json[i] == ']') break

            val valueResult = parseValue(json, i)
            list.add(valueResult.first)
            i = valueResult.second

            i = skipWhitespace(json, i)
            if (i < json.length && json[i] == ',') i++
        }

        if (i < json.length && json[i] == ']') i++
        return Pair(list, i)
    }

    private fun parseString(json: String, startIndex: Int): Pair<String, Int> {
        val sb = StringBuilder()
        var i = startIndex + 1 // skip opening quote
        var escaped = false

        while (i < json.length) {
            val c = json[i]
            when {
                escaped -> {
                    when (c) {
                        '"' -> sb.append('"')
                        '\\' -> sb.append('\\')
                        '/' -> sb.append('/')
                        'b' -> sb.append('\b')
                        'f' -> sb.append('\u000C')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        'u' -> {
                            if (i + 4 < json.length) {
                                val hex = json.substring(i + 1, i + 5)
                                sb.append(hex.toInt(16).toChar())
                                i += 4
                            }
                        }
                        else -> sb.append(c)
                    }
                    escaped = false
                }
                c == '\\' -> escaped = true
                c == '"' -> return Pair(sb.toString(), i + 1)
                else -> sb.append(c)
            }
            i++
        }
        return Pair(sb.toString(), i)
    }

    private fun parseNumber(json: String, startIndex: Int): Pair<Number, Int> {
        var i = startIndex
        val sb = StringBuilder()
        var isFloat = false

        while (i < json.length) {
            val c = json[i]
            if (c == '.' || c == 'e' || c == 'E') isFloat = true
            if (c.isDigit() || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E') {
                sb.append(c)
                i++
            } else {
                break
            }
        }

        val numStr = sb.toString()
        val number: Number = if (isFloat) {
            numStr.toDouble()
        } else {
            val longVal = numStr.toLong()
            if (longVal in Int.MIN_VALUE..Int.MAX_VALUE) longVal.toInt() else longVal
        }
        return Pair(number, i)
    }

    private fun parseBoolean(json: String, startIndex: Int): Pair<Boolean, Int> {
        return if (json.startsWith("true", startIndex)) {
            Pair(true, startIndex + 4)
        } else {
            Pair(false, startIndex + 5)
        }
    }

    private fun parseNull(json: String, startIndex: Int): Pair<Any?, Int> {
        return Pair(null, startIndex + 4)
    }

    private fun skipWhitespace(json: String, startIndex: Int): Int {
        var i = startIndex
        while (i < json.length && json[i].isWhitespace()) i++
        return i
    }
}
