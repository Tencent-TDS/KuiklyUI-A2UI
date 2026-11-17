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

package com.tencent.kuikly.a2ui.golden

import com.tencent.kuikly.a2ui.protocol.A2UIMessage

/**
 * 把一条消息流渲染成稳定的文本快照。
 *
 * 为什么不直接用 `assertEquals(expected, actual)` 比较 `List<A2UIMessage>`：
 * [A2UIMessage.equals] 只比较 `type` / `surfaceId` / `tag` 三个字段
 * （见 A2UIMessage.kt:52-56），`props`、`data`、`frame`、`bindings` 全都
 * 不参与比较。拿它做回归断言，等于把"属性解析是否正确"这件最该守的事
 * 整个漏掉——buildProps 把颜色解析错了、把绑定丢了，断言照样通过。
 *
 * 所以这里自己序列化全字段。
 *
 * 稳定性要求（否则快照会无意义地抖动）：
 *  - Map 的 key 必须排序：Kotlin 的 Map 迭代序在不同平台上没有保证
 *  - 数值保留精度，不把 Double 缩为 Float，不把 Float 截为 Long
 *  - 不打印对象 hash / 身份，只打印值
 */
internal object A2UIMessageSnapshot {

    fun renderState(label: String, state: Any?): String =
        "#state $label ${value(state)}\n"

    fun render(messages: List<A2UIMessage>, startIndex: Int = 0): String {
        val sb = StringBuilder()
        messages.forEachIndexed { index, msg ->
            sb.append('#').append(startIndex + index).append(' ')
            renderMessage(msg, sb, indent = "")
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun renderMessage(msg: A2UIMessage, sb: StringBuilder, indent: String) {
        sb.append(msg.type.value)
        sb.append(" surface=").append(quote(msg.surfaceId))
        if (msg.tag != -1) sb.append(" tag=").append(msg.tag)
        if (msg.parentTag != -1) sb.append(" parent=").append(msg.parentTag)
        if (msg.index != -1) sb.append(" index=").append(msg.index)
        msg.viewName?.let { sb.append(" view=").append(quote(it)) }
        msg.method?.let { sb.append(" method=").append(quote(it)) }
        msg.params?.let { sb.append(" params=").append(quote(it)) }

        msg.config?.let { cfg ->
            sb.append("\n").append(indent).append("  config: ")
            sb.append("w=").append(num(cfg.width))
            sb.append(" h=").append(num(cfg.height))
            sb.append(" bg=").append(value(cfg.backgroundColor))
            sb.append(" incremental=").append(cfg.incrementalRender)
            sb.append(" catalogId=").append(value(cfg.catalogId))
        }

        msg.frame?.let { f ->
            sb.append("\n").append(indent).append("  frame: [")
            sb.append(f.joinToString(", ") { num(it) })
            sb.append(']')
        }

        msg.props?.let { renderMap("props", it, sb, indent) }
        msg.data?.let { renderMap("data", it, sb, indent) }
        msg.bindings?.let { b ->
            renderMap("bindings", b.mapValues { it.value as Any? }, sb, indent)
        }

        msg.messages?.let { nested ->
            sb.append("\n").append(indent).append("  messages:")
            nested.forEachIndexed { i, child ->
                sb.append("\n").append(indent).append("    #").append(i).append(' ')
                renderMessage(child, sb, "$indent    ")
            }
        }
    }

    private fun renderMap(
        label: String,
        map: Map<String, Any?>,
        sb: StringBuilder,
        indent: String,
    ) {
        if (map.isEmpty()) {
            sb.append("\n").append(indent).append("  ").append(label).append(": {}")
            return
        }
        sb.append("\n").append(indent).append("  ").append(label).append(':')
        for (key in map.keys.sorted()) {
            sb.append("\n").append(indent).append("    ").append(quote(key)).append(" = ")
            sb.append(value(map[key]))
        }
    }

    /** 递归渲染任意值，容器一律排序后内联 */
    private fun value(v: Any?): String = when (v) {
        null -> "null"
        is String -> quote(v)
        is Boolean, is Byte, is Short, is Int, is Long -> v.toString()
        is Float -> num(v)
        is Double -> v.toString()
        is Map<*, *> -> v.entries
            .map { (k, item) -> (k as? String ?: error("快照 Map 的键不是字符串")) to item }
            .sortedBy { it.first }
            .joinToString(prefix = "{", postfix = "}") { "${quote(it.first)}: ${value(it.second)}" }
        is List<*> -> v.joinToString(prefix = "[", postfix = "]") { value(it) }
        is FloatArray -> v.joinToString(prefix = "[", postfix = "]") { num(it) }
        else -> error("快照不支持类型：${v::class.simpleName}")
    }

    fun quote(text: String): String = buildString {
        append('"')
        for (ch in text) {
            when (ch) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (ch.code < 32) {
                    append("\\u").append(ch.code.toString(16).padStart(4, '0'))
                } else {
                    append(ch)
                }
            }
        }
        append('"')
    }

    private fun num(f: Float): String = f.toString()
}
