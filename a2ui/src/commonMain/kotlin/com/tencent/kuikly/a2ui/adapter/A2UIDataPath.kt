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

internal object A2UIDataPath {
    /**
     * Split a data-model path expression into its atomic segments.
     *
     * A2UI JSON Pointer paths use `/` as the segment separator, but this project also
     * accepts an extended flatten form where `.` acts as a field separator and `[N]`
     * denotes a numeric array index (e.g. `chart.items[0].label`). Both forms may be
     * mixed. Empty segments (produced by leading slashes such as `/foo/bar`) are
     * dropped so callers can pass the raw path with or without a leading `/`.
     *
     * Examples:
     *   "stat1/value"           -> ["stat1", "value"]
     *   "chart.items[0].label"  -> ["chart", "items", "0", "label"]
     *   "/event/location/0"     -> ["event", "location", "0"]
     */
    fun parsePathSegments(path: String): List<String> {
        if (path.isEmpty()) return emptyList()
        val out = mutableListOf<String>()
        val buf = StringBuilder()
        val flush = {
            if (buf.isNotEmpty()) {
                out.add(buf.toString())
                buf.clear()
            }
        }
        var i = 0
        while (i < path.length) {
            when (val c = path[i]) {
                '/', '.' -> {
                    flush()
                    i++
                }
                '[' -> {
                    flush()
                    val end = path.indexOf(']', i + 1)
                    if (end < 0) {
                        // Malformed - treat rest as literal to avoid data loss
                        buf.append(path.substring(i))
                        i = path.length
                    } else {
                        val idx = path.substring(i + 1, end).trim()
                        if (idx.isNotEmpty()) out.add(idx)
                        i = end + 1
                    }
                }
                else -> {
                    buf.append(c)
                    i++
                }
            }
        }
        flush()
        return out
    }

    /**
     * Resolve a nested data path from the data model.
     * Supports multi-segment paths like "stat1/value" or "chart.items[0].label" by
     * traversing nested Maps and Lists. Numeric segments index into Lists (per A2UI
     * path syntax, e.g. "/event/location/0" or "chart.items[0]").
     *
     * As a fallback for backward compatibility, if the raw [key] exists as-is in the
     * top-level data model (e.g. legacy flatten storage), it is returned directly.
     */
    @Suppress("UNCHECKED_CAST")
    fun resolveNestedDataPath(dataModel: Map<String, Any?>, key: String): Any? {
        // Fast path & backward compatibility: exact top-level key hit (e.g. flatten
        // key "chart.items[0].label" that was stored verbatim before normalization).
        if (dataModel.containsKey(key)) return dataModel[key]

        val segments = parsePathSegments(key)
        if (segments.isEmpty()) return null
        var current: Any? = dataModel[segments[0]]
        for (i in 1 until segments.size) {
            when (current) {
                null -> return null
                is Map<*, *> -> current = current[segments[i]]
                is List<*> -> {
                    val index = segments[i].toIntOrNull() ?: return null
                    current = current.getOrNull(index)
                }
                else -> return null
            }
        }
        return current
    }

    /**
     * Write [value] into the data model at [key], creating/traversing nested Maps
     * and Lists as needed. Supports both slash-separated JSON Pointer paths (e.g.
     * "formData/email") and the extended flatten form with `.`/`[N]` (e.g.
     * "chart.items[0].label"). This is the inverse of [resolveNestedDataPath]: a
     * value written here can be read back through it.
     *
     * Intermediate Maps parsed from JSON may be read-only, so they are copied into
     * MutableMaps along the path. A null [value] removes the leaf key.
     */
    fun setNestedDataPath(dataModel: MutableMap<String, Any?>, key: String, value: Any?) {
        val segments = parsePathSegments(key)
        if (segments.isEmpty()) return
        if (segments.size == 1) {
            val seg = segments[0]
            if (value != null) dataModel[seg] = value else dataModel.remove(seg)
            return
        }

        // Walk/create the path down to the parent of the leaf segment. At each
        // step the container's shape is decided by the *next* segment: a numeric
        // segment implies a List, otherwise a Map.
        var parentContainer: Any = dataModel
        var parentKey: Any = segments[0] // Either String (Map key) or Int (List index)
        for (i in 0 until segments.size - 1) {
            val currentSeg = segments[i]
            val nextSeg = segments[i + 1]
            val nextIsIndex = nextSeg.toIntOrNull() != null
            val currentIndex = currentSeg.toIntOrNull()

            @Suppress("UNCHECKED_CAST")
            val existing: Any? = when (parentContainer) {
                is MutableMap<*, *> -> (parentContainer as MutableMap<String, Any?>)[currentSeg]
                is MutableList<*> -> {
                    val idx = currentIndex ?: return
                    (parentContainer as MutableList<Any?>).getOrNull(idx)
                }
                else -> return
            }

            val child: Any = if (nextIsIndex) {
                // Ensure child is a MutableList
                when (existing) {
                    is MutableList<*> -> existing
                    is List<*> -> existing.toMutableList()
                    else -> mutableListOf<Any?>()
                }
            } else {
                // Ensure child is a MutableMap
                @Suppress("UNCHECKED_CAST")
                when (existing) {
                    is MutableMap<*, *> -> existing as MutableMap<String, Any?>
                    is Map<*, *> -> (existing as Map<String, Any?>).toMutableMap()
                    else -> mutableMapOf<String, Any?>()
                }
            }

            // Store child back into parent (may replace read-only or absent value)
            @Suppress("UNCHECKED_CAST")
            when (parentContainer) {
                is MutableMap<*, *> -> (parentContainer as MutableMap<String, Any?>)[currentSeg] = child
                is MutableList<*> -> {
                    val list = parentContainer as MutableList<Any?>
                    val idx = currentIndex ?: return
                    while (list.size <= idx) list.add(null)
                    list[idx] = child
                }
            }

            parentContainer = child
            parentKey = if (nextIsIndex) nextSeg.toInt() else nextSeg
        }

        // Assign the leaf
        @Suppress("UNCHECKED_CAST")
        when (parentContainer) {
            is MutableMap<*, *> -> {
                val m = parentContainer as MutableMap<String, Any?>
                val leafKey = parentKey as String
                if (value != null) m[leafKey] = value else m.remove(leafKey)
            }
            is MutableList<*> -> {
                val list = parentContainer as MutableList<Any?>
                val idx = parentKey as Int
                while (list.size <= idx) list.add(null)
                list[idx] = value
            }
        }
    }

    /**
     * Get the root key (first segment) from a potentially nested path.
     * Used for dependency tracking - when "stat1" changes, "stat1/value" also changes.
     * Recognises `/`, `.` and `[` as segment boundaries so flatten paths like
     * `chart.items[0].label` still map back to the root key `chart`.
     */
    fun getRootDataKey(key: String): String {
        var end = key.length
        for (i in key.indices) {
            val c = key[i]
            if (c == '/' || c == '.' || c == '[') {
                end = i
                break
            }
        }
        return if (end == 0) key else key.substring(0, end)
    }
}
