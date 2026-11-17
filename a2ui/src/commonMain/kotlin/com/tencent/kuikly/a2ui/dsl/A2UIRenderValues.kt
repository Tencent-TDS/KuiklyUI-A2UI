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

package com.tencent.kuikly.a2ui.dsl

import com.tencent.kuikly.core.base.Color

internal object A2UIRenderValues {
    fun toFloat(value: Any?): Float {
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull() ?: 0f
            else -> 0f
        }
    }

    /**
     * Convert a color value to a Color object.
     * Supports:
     * - "#RRGGBB" -> Color with alpha 0xFF
     * - "#AARRGGBB" -> Color with explicit alpha
     * - Integer/Long ARGB value (as Number or numeric String)
     */
    fun toColor(value: Any?): Color {
        if (value == null) return Color.BLACK
        val str = value.toString()
        if (str.startsWith("#")) {
            val hex = str.removePrefix("#")
            return when (hex.length) {
                6 -> {
                    // #RRGGBB -> add FF alpha
                    val rgb = hex.toLongOrNull(16) ?: 0L
                    Color(0xFF000000L or rgb)
                }
                8 -> {
                    // #AARRGGBB
                    val argb = hex.toLongOrNull(16) ?: 0L
                    Color(argb)
                }
                else -> Color.BLACK
            }
        }
        // Try as numeric ARGB value
        val longVal = str.toLongOrNull()
        if (longVal != null) {
            return Color(longVal)
        }
        // Fallback: pass as color string (for token-based colors)
        return Color(str)
    }
    fun snapSliderValue(value: Float, min: Float, max: Float, steps: Int?): Float {
        if (steps == null || steps <= 0 || max <= min) return value
        val stepValue = (max - min) / steps.toFloat()
        if (stepValue <= 0f) return value
        val clamped = value.coerceIn(min, max)
        val n = kotlin.math.round((clamped - min) / stepValue)
        return (min + n * stepValue).coerceIn(min, max)
    }
}
