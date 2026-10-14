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

import com.tencent.kuikly.a2ui.adapter.A2UIProtocolAdapter
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.protocol.A2UISurfaceConfig

/**
 * Progressive JSON stream parser for A2UI protocol messages.
 * Supports incremental parsing of chunked JSON data arriving over a stream.
 *
 * Supports two message formats:
 * 1. Standard A2UI v0.9 protocol: [{"version":"v0.9","createSurface":{...}}, {"version":"v0.9","updateComponents":{...}}, ...]
 * 2. Legacy internal format: [{"type":"createSurface",...}, {"type":"createComponent",...}, ...]
 *
 * The format is auto-detected from the first message. Standard v0.9 messages
 * are converted to internal format via [A2UIProtocolAdapter].
 *
 * Messages are emitted as soon as they are fully parsed, enabling
 * progressive/incremental rendering.
 */
class A2UIStreamParser {

    private val buffer = StringBuilder()
    private var listener: A2UIStreamListener? = null
    private var braceDepth = 0
    private var inString = false
    private var escaped = false
    private var messageStartIndex = -1
    private var streamStarted = false

    /** Protocol adapter for standard v0.9 format */
    val protocolAdapter = A2UIProtocolAdapter()

    /** Whether we've detected the message format */
    private var formatDetected = false

    /** Whether the stream uses standard v0.9 format */
    private var isStandardFormat = false

    /**
     * Set the listener for parsed messages.
     */
    fun setListener(listener: A2UIStreamListener) {
        this.listener = listener
    }

    /**
     * Feed a chunk of data into the parser.
     * Messages will be emitted via the listener as they are fully parsed.
     *
     * @param chunk A partial or complete JSON string chunk
     */
    fun feed(chunk: String) {
        for (char in chunk) {
            buffer.append(char)
            processChar(char)
        }
    }

    /**
     * Mark the stream as finished.
     * Any remaining buffered data will attempt to be parsed.
     */
    fun finish() {
        // Try to parse any remaining content
        if (messageStartIndex >= 0 && braceDepth == 0) {
            tryParseMessage()
        }
        // For standard format, emit a streamEnd message if we have a surface
        if (isStandardFormat) {
            // The protocol adapter handles streamEnd internally when convertMessages is used,
            // but for streaming mode we need to emit it here via the listener's onStreamEnd
        }
        listener?.onStreamEnd()
        // Only reset parser state, NOT the protocol adapter state.
        // The protocol adapter retains tagToAction, idToTag etc. for the lifetime of the surface.
        resetParserState()
    }

    /**
     * Reset only the parser's internal parsing state (buffer, brace tracking, etc.).
     * Does NOT reset the protocol adapter, preserving tagToAction, idToTag, etc.
     */
    private fun resetParserState() {
        buffer.clear()
        braceDepth = 0
        inString = false
        escaped = false
        messageStartIndex = -1
        streamStarted = false
        formatDetected = false
        isStandardFormat = false
    }

    /**
     * Fully reset the parser state including the protocol adapter.
     * Should only be called on destroy.
     */
    fun reset() {
        resetParserState()
        protocolAdapter.reset()
    }

    private fun processChar(char: Char) {
        if (escaped) {
            escaped = false
            return
        }

        when {
            char == '\\' && inString -> {
                escaped = true
            }
            char == '"' -> {
                inString = !inString
            }
            inString -> {
                // Inside a string, ignore structural characters
            }
            char == '[' && !streamStarted -> {
                streamStarted = true
            }
            char == '{' -> {
                if (braceDepth == 0) {
                    messageStartIndex = buffer.length - 1
                }
                braceDepth++
            }
            char == '}' -> {
                braceDepth--
                if (braceDepth == 0 && messageStartIndex >= 0) {
                    tryParseMessage()
                }
            }
        }
    }

    private fun tryParseMessage() {
        val jsonStr = buffer.substring(messageStartIndex, buffer.length)
        messageStartIndex = -1

        try {
            val map = parseJsonObject(jsonStr)
            if (map != null) {
                // Auto-detect format from first message
                if (!formatDetected) {
                    formatDetected = true
                    isStandardFormat = map.containsKey("version") ||
                            map.containsKey("createSurface") ||
                            map.containsKey("updateComponents") ||
                            map.containsKey("updateDataModel")
                }

                if (isStandardFormat) {
                    // Standard A2UI v0.9 protocol - convert via adapter
                    val messages = protocolAdapter.convertMessage(map)
                    for (message in messages) {
                        listener?.onMessageParsed(message)
                    }
                } else {
                    // Legacy internal format
                    val message = parseMessageJson(map)
                    if (message != null) {
                        listener?.onMessageParsed(message)
                    }
                }
            }
        } catch (e: Exception) {
            listener?.onError(A2UIParseError("Failed to parse message: ${e.message}", jsonStr))
        }

        // Clear processed content from buffer
        buffer.clear()
    }

    /**
     * Parse a single JSON message object into an A2UIMessage (legacy internal format).
     */
    private fun parseMessageJson(map: Map<String, Any?>): A2UIMessage? {
        val typeStr = map["type"] as? String ?: return null
        val type = A2UIMessageType.fromValue(typeStr)
        val surfaceId = map["surfaceId"] as? String ?: ""

        return A2UIMessage(
            type = type,
            surfaceId = surfaceId,
            tag = (map["tag"] as? Number)?.toInt() ?: -1,
            viewName = map["viewName"] as? String,
            parentTag = (map["parentTag"] as? Number)?.toInt() ?: -1,
            index = (map["index"] as? Number)?.toInt() ?: -1,
            props = parseProps(map["props"]),
            frame = parseFrame(map["frame"]),
            bindings = parseBindings(map["bindings"]),
            method = map["method"] as? String,
            params = map["params"] as? String,
            data = parseProps(map["data"]),
            config = parseSurfaceConfig(map["config"])
        )
    }

    private fun parseProps(value: Any?): Map<String, Any?>? {
        if (value == null) return null
        @Suppress("UNCHECKED_CAST")
        return value as? Map<String, Any?>
    }

    private fun parseFrame(value: Any?): FloatArray? {
        if (value == null) return null
        val list = value as? List<*> ?: return null
        if (list.size < 4) return null
        return floatArrayOf(
            (list[0] as? Number)?.toFloat() ?: 0f,
            (list[1] as? Number)?.toFloat() ?: 0f,
            (list[2] as? Number)?.toFloat() ?: 0f,
            (list[3] as? Number)?.toFloat() ?: 0f
        )
    }

    private fun parseBindings(value: Any?): Map<String, String>? {
        if (value == null) return null
        @Suppress("UNCHECKED_CAST")
        val map = value as? Map<String, Any?> ?: return null
        return map.mapValues { it.value?.toString() ?: "" }
    }

    private fun parseSurfaceConfig(value: Any?): A2UISurfaceConfig? {
        if (value == null) return null
        @Suppress("UNCHECKED_CAST")
        val map = value as? Map<String, Any?> ?: return null
        return A2UISurfaceConfig(
            width = (map["width"] as? Number)?.toFloat() ?: 0f,
            height = (map["height"] as? Number)?.toFloat() ?: 0f,
            backgroundColor = map["backgroundColor"] as? String,
            incrementalRender = map["incrementalRender"] as? Boolean ?: true
        )
    }

    /**
     * Minimal JSON object parser.
     * Parses a JSON object string into a Map<String, Any?>.
     */
    private fun parseJsonObject(json: String): Map<String, Any?>? {
        return try {
            SimpleJsonParser.parse(json)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Listener interface for stream parsing events.
     */
    interface A2UIStreamListener {
        /** Called when a complete message has been parsed */
        fun onMessageParsed(message: A2UIMessage)
        /** Called when a parse error occurs */
        fun onError(error: A2UIParseError)
        /** Called when the stream has ended */
        fun onStreamEnd()
    }
}

/**
 * Parse error information.
 */
data class A2UIParseError(
    val message: String,
    val rawContent: String? = null
)
