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

internal object A2UIValueFormatting {
    /** Simple date formatter for the adapter's existing ISO 8601 / TR35 subset. */
    fun formatDateString(isoDate: String, pattern: String): String {
        // Parse ISO 8601: "2025-12-15" or "2025-12-15T10:15:00Z"
        val datePart: String
        val timePart: String?

        if (isoDate.contains("T")) {
            val parts = isoDate.split("T")
            datePart = parts[0]
            timePart = parts.getOrNull(1)?.removeSuffix("Z")?.removeSuffix("+00:00")
        } else {
            datePart = isoDate
            timePart = null
        }

        val dateParts = datePart.split("-")
        if (dateParts.size < 3) return isoDate

        val year = dateParts[0].toIntOrNull() ?: return isoDate
        val month = dateParts[1].toIntOrNull() ?: return isoDate
        val day = dateParts[2].toIntOrNull() ?: return isoDate

        var hour = 0
        var minute = 0
        var second = 0
        if (timePart != null) {
            val timeParts = timePart.split(":")
            hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            second = timeParts.getOrNull(2)?.split(".")?.get(0)?.toIntOrNull() ?: 0
        }

        val monthNames = listOf("", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val monthShort = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayShort = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Calculate day of week
        val dow = calculateDayOfWeek(year, month, day)

        // Calculate ISO 8601 week-year & week-of-year (used by TR-35 tokens Y/w).
        // These may differ from the calendar year at year boundaries: e.g. 2027-01-01
        // is a Friday, so its ISO week is 2026-W53 (week-year=2026), while the calendar
        // year is 2027.
        val (weekYear, weekOfYear) = calculateIsoWeek(year, month, day)

        // Time formatting
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val amPm = if (hour < 12) "AM" else "PM"

        // Token-based replacement to avoid conflicts
        // Parse the pattern into tokens and replace each token with its value
        val result = StringBuilder()
        var i = 0
        while (i < pattern.length) {
            val remaining = pattern.substring(i)
            val consumed = when {
                remaining.startsWith("EEEE") -> { result.append(dayNames[dow]); 4 }
                remaining.startsWith("EEE") -> { result.append(dayShort[dow]); 3 }
                remaining.startsWith("E") -> { result.append(dayShort[dow]); 1 }
                remaining.startsWith("MMMM") -> { result.append(monthNames[month]); 4 }
                remaining.startsWith("MMM") -> { result.append(monthShort[month]); 3 }
                remaining.startsWith("MM") -> { result.append(month.toString().padStart(2, '0')); 2 }
                remaining.startsWith("M") -> { result.append(month.toString()); 1 }
                remaining.startsWith("yyyy") -> { result.append(year.toString()); 4 }
                remaining.startsWith("yy") -> { result.append((year % 100).toString().padStart(2, '0')); 2 }
                // TR-35 `Y` = week-based year (ISO 8601). Different from `y` on
                // year-boundary weeks. YYYY is 4-digit, YY is last-2-digit, Y is unpadded.
                remaining.startsWith("YYYY") -> { result.append(weekYear.toString().padStart(4, '0')); 4 }
                remaining.startsWith("YY") -> { result.append((weekYear % 100).toString().padStart(2, '0')); 2 }
                remaining.startsWith("Y") -> { result.append(weekYear.toString()); 1 }
                remaining.startsWith("dd") -> { result.append(day.toString().padStart(2, '0')); 2 }
                remaining.startsWith("d") -> { result.append(day.toString()); 1 }
                remaining.startsWith("HH") -> { result.append(hour.toString().padStart(2, '0')); 2 }
                remaining.startsWith("H") -> { result.append(hour.toString()); 1 }
                remaining.startsWith("hh") -> { result.append(hour12.toString().padStart(2, '0')); 2 }
                remaining.startsWith("h") -> { result.append(hour12.toString()); 1 }
                remaining.startsWith("mm") -> { result.append(minute.toString().padStart(2, '0')); 2 }
                remaining.startsWith("ss") -> { result.append(second.toString().padStart(2, '0')); 2 }
                // TR-35 `w` = ISO 8601 week-of-year (1..53). ww is 2-digit, w is unpadded.
                remaining.startsWith("ww") -> { result.append(weekOfYear.toString().padStart(2, '0')); 2 }
                remaining.startsWith("w") -> { result.append(weekOfYear.toString()); 1 }
                remaining.startsWith("a") -> { result.append(amPm); 1 }
                remaining.startsWith("'") -> {
                    // Quoted literal text
                    val endQuote = remaining.indexOf("'", 1)
                    if (endQuote > 0) {
                        result.append(remaining.substring(1, endQuote))
                        endQuote + 1
                    } else {
                        result.append("'")
                        1
                    }
                }
                else -> { result.append(pattern[i]); 1 }
            }
            i += consumed
        }

        return result.toString()
    }

    /** Calculate day of week (0=Sunday) using Tomohiko Sakamoto's algorithm. */
    private fun calculateDayOfWeek(year: Int, month: Int, day: Int): Int {
        val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        var y = year
        if (month < 3) y -= 1
        return (y + y / 4 - y / 100 + y / 400 + t[month - 1] + day) % 7
    }

    /** ISO 8601 week-year and week-of-year, including year-boundary weeks. */
    private fun calculateIsoWeek(year: Int, month: Int, day: Int): Pair<Int, Int> {
        val ordinal = dayOfYear(year, month, day)
        val isoDow = isoWeekday(year, month, day) // 1=Mon..7=Sun
        // Week number formula (from ISO 8601):
        //   week = (ordinal - isoDow + 10) / 7
        val week = (ordinal - isoDow + 10) / 7
        return when {
            week < 1 -> Pair(year - 1, lastIsoWeekOfYear(year - 1))
            week > lastIsoWeekOfYear(year) -> Pair(year + 1, 1)
            else -> Pair(year, week)
        }
    }

    /** ISO weekday: Monday=1 .. Sunday=7. */
    private fun isoWeekday(year: Int, month: Int, day: Int): Int {
        // calculateDayOfWeek returns 0=Sun..6=Sat; convert to 1=Mon..7=Sun.
        val dow = calculateDayOfWeek(year, month, day)
        return if (dow == 0) 7 else dow
    }

    /** Day-of-year (1-based). */
    private fun dayOfYear(year: Int, month: Int, day: Int): Int {
        val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var d = day
        for (m in 1 until month) d += daysInMonth[m - 1]
        if (month > 2 && isLeapYear(year)) d += 1
        return d
    }

    private fun isLeapYear(y: Int): Boolean =
        (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

    /** Number of ISO weeks in a given year (52 or 53). */
    private fun lastIsoWeekOfYear(year: Int): Int {
        val jan1 = isoWeekday(year, 1, 1)
        val dec31 = isoWeekday(year, 12, 31)
        return if (jan1 == 4 || dec31 == 4) 53 else 52
    }

    /** Format a double with specified decimal places (Kotlin Multiplatform compatible). */
    fun formatDouble(value: Double, decimals: Int): String {
        // Split the integer and fractional parts via Long arithmetic instead of
        // Double.toString(). Double.toString() emits scientific notation for large
        // magnitudes (e.g. 850000000000.0 -> "8.5E11"), which the previous substring
        // logic mangled into "8.5E". Long.toString() never uses scientific notation.
        val negative = value < 0
        val abs = kotlin.math.abs(value)
        var factor = 1L
        repeat(decimals) { factor *= 10L }
        val scaled = kotlin.math.round(abs * factor).toLong()
        val intPart = scaled / factor
        val fracPart = scaled % factor
        val sign = if (negative) "-" else ""
        return if (decimals > 0) {
            "$sign$intPart.${fracPart.toString().padStart(decimals, '0')}"
        } else {
            "$sign$intPart"
        }
    }

    /** Expand scientific notation to plain decimals, preserving existing non-exponent forms. */
    fun doubleToPlainString(num: Double): String {
        val s = num.toString()
        if (!s.contains('E') && !s.contains('e')) return s

        val negative = s.startsWith("-")
        val body = if (negative) s.substring(1) else s
        val eIndex = body.indexOfFirst { it == 'E' || it == 'e' }
        val mantissa = body.substring(0, eIndex)
        val exp = body.substring(eIndex + 1).toInt()
        val dot = mantissa.indexOf('.')
        val digits = mantissa.replace(".", "")
        // Decimal point position within `digits` after applying the exponent.
        val pointPos = (if (dot < 0) mantissa.length else dot) + exp

        val sb = StringBuilder()
        when {
            pointPos <= 0 -> {
                sb.append("0.")
                repeat(-pointPos) { sb.append('0') }
                sb.append(digits)
            }
            pointPos >= digits.length -> {
                sb.append(digits)
                repeat(pointPos - digits.length) { sb.append('0') }
            }
            else -> {
                sb.append(digits.substring(0, pointPos))
                sb.append('.')
                sb.append(digits.substring(pointPos))
            }
        }

        var result = sb.toString()
        if (result.contains('.')) {
            result = result.trimEnd('0').trimEnd('.')
        }
        return (if (negative) "-" else "") + result
    }

    /** Coerce Number/String to Int (accepts "3", 3.0, etc.); returns null on failure. */
    fun coerceToInt(v: Any?): Int? = when (v) {
        null -> null
        is Number -> v.toInt()
        is String -> v.trim().toDoubleOrNull()?.toInt()
        else -> null
    }

    /** Coerce Boolean/String ("true"/"false") to Boolean; returns null on failure. */
    fun coerceToBool(v: Any?): Boolean? = when (v) {
        null -> null
        is Boolean -> v
        is String -> when (v.trim().lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }
        else -> null
    }

    /** Add thousands separator (comma) to a formatted number string. */
    fun addThousandsSeparator(numStr: String): String {
        val parts = numStr.split(".")
        val intPart = parts[0]
        val isNegative = intPart.startsWith("-")
        val digits = if (isNegative) intPart.substring(1) else intPart
        val result = StringBuilder()
        for ((i, c) in digits.reversed().withIndex()) {
            if (i > 0 && i % 3 == 0) {
                result.append(',')
            }
            result.append(c)
        }
        val intFormatted = (if (isNegative) "-" else "") + result.reverse().toString()
        return if (parts.size > 1) "$intFormatted.${parts[1]}" else intFormatted
    }

    /** Find a closing brace while accounting for nested interpolation pairs. */
    fun findMatchingBrace(s: String, start: Int): Int {
        var depth = 1
        var i = start
        while (i < s.length) {
            if (i < s.length - 1 && s[i] == '$' && s[i + 1] == '{') {
                depth++
                i += 2
            } else if (s[i] == '}') {
                depth--
                if (depth == 0) return i
                i++
            } else {
                i++
            }
        }
        return -1
    }

    /** Split argument string by top-level commas, respecting nested interpolation and quotes. */
    fun splitArguments(argsStr: String): List<String> {
        val parts = mutableListOf<String>()
        var depth = 0
        var inSingleQuote = false
        var inDoubleQuote = false
        var start = 0
        var i = 0
        while (i < argsStr.length) {
            val c = argsStr[i]
            when {
                c == '\'' && !inDoubleQuote -> inSingleQuote = !inSingleQuote
                c == '"' && !inSingleQuote -> inDoubleQuote = !inDoubleQuote
                !inSingleQuote && !inDoubleQuote -> {
                    if (i < argsStr.length - 1 && c == '$' && argsStr[i + 1] == '{') {
                        depth++
                        i += 2
                        continue
                    } else if (c == '}' && depth > 0) {
                        depth--
                    } else if (c == ',' && depth == 0) {
                        parts.add(argsStr.substring(start, i))
                        start = i + 1
                    }
                }
            }
            i++
        }
        if (start < argsStr.length) {
            parts.add(argsStr.substring(start))
        }
        return parts
    }
}
