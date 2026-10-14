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

package com.tencent.kuikly.a2ui.views

import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ContainerAttr
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.VirtualView
import com.tencent.kuikly.core.base.event.Event
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * DateTimeInputView - A2UI DateTimeInput component implementation.
 *
 * Architecture (Method B - Native Picker):
 * - Inherits VirtualView (no render node itself)
 * - Display area: Shows formatted date/time text with a border
 * - On click: Delegates to native layer to show a full-screen picker dialog
 *   (BottomSheetDialog on Android, UIDatePicker sheet on iOS)
 * - Native picker result is received via A2UIKotlinMethod.PICKER_RESULT
 *   and routed back to update this view's displayed value
 *
 * This design ensures the picker popup is always displayed at the window level,
 * completely independent of the A2UI card's clipping boundaries.
 */
class DateTimeInputView : VirtualView<DateTimeInputAttr, DateTimeInputEvent>() {

    // Whether a value has been set (false = show placeholder)
    internal var hasValue by observable(false)

    // Selected date/time values (observable for reactive UI updates)
    internal var selectedYear by observable(2025)
    internal var selectedMonth by observable(1)
    internal var selectedDay by observable(1)
    internal var selectedHour by observable(0)
    internal var selectedMinute by observable(0)

    // Click listener set by A2UIApplier to trigger native picker
    private var clickListener: (() -> Unit)? = null

    // Min/max constraints (ISO 8601 strings)
    private var minValue: String = ""
    private var maxValue: String = ""

    override fun createAttr() = DateTimeInputAttr()
    override fun createEvent() = DateTimeInputEvent()

    /**
     * Configure whether date selection is enabled.
     */
    fun setEnableDate(enable: Boolean) {
        attr.enableDate = enable
    }

    /**
     * Configure whether time selection is enabled.
     */
    fun setEnableTime(enable: Boolean) {
        attr.enableTime = enable
    }

    /**
     * Set the display label.
     */
    fun setLabel(label: String) {
        attr.label = label
    }

    /**
     * Get whether date selection is enabled.
     */
    fun getEnableDate(): Boolean = attr.enableDate

    /**
     * Get whether time selection is enabled.
     */
    fun getEnableTime(): Boolean = attr.enableTime

    /**
     * Get the display label.
     */
    fun getLabel(): String = attr.label

    /**
     * Set the minimum allowed date/time (ISO 8601 string).
     */
    fun setMinValue(value: String) {
        minValue = value
    }

    /**
     * Set the maximum allowed date/time (ISO 8601 string).
     */
    fun setMaxValue(value: String) {
        maxValue = value
    }

    /**
     * Get the minimum allowed date/time (ISO 8601 string).
     */
    fun getMinValue(): String = minValue

    /**
     * Get the maximum allowed date/time (ISO 8601 string).
     */
    fun getMaxValue(): String = maxValue

    /**
     * Set a click listener that will be invoked when the display area is tapped.
     * Used by A2UIApplier to trigger the native picker dialog.
     */
    fun setOnClickListener(listener: () -> Unit) {
        clickListener = listener
    }

    override fun didInit() {
        super.didInit()
        body()()
    }

    /**
     * Set the date/time from an ISO 8601 string.
     * Format: "2025-12-15T17:00:00Z"
     * Empty/blank string means no value is set (show placeholder).
     */
    fun setDateTime(isoString: String) {
        if (isoString.isBlank()) {
            hasValue = false
            return
        }
        hasValue = true
        parseISO8601(isoString)
    }

    /**
     * Get the current selection as ISO 8601 string.
     * Returns empty string if no value has been set.
     */
    fun getISOString(): String {
        if (!hasValue) return ""
        return formatISO8601()
    }

    /**
     * Format the current selection for display.
     * Returns placeholder text if no value has been set.
     */
    internal fun formatDisplay(): String {
        if (!hasValue) {
            // Show label as placeholder, or a generic prompt
            return attr.label.ifBlank { "Select..." }
        }
        val parts = mutableListOf<String>()
        if (attr.enableDate) {
            val monthName = MONTH_NAMES.getOrElse(selectedMonth - 1) { "Jan" }
            parts.add("$monthName $selectedDay, $selectedYear")
        }
        if (attr.enableTime) {
            parts.add("${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}")
        }
        return parts.joinToString(" ")
    }

    private fun parseISO8601(iso: String) {
        try {
            if (iso.contains("T")) {
                // Full ISO 8601 format: "2025-12-15T17:00:00Z"
                val datePart = iso.substringBefore("T")
                val parts = datePart.split("-")
                if (parts.size >= 3) {
                    selectedYear = parts[0].toIntOrNull() ?: 2025
                    selectedMonth = parts[1].toIntOrNull() ?: 1
                    selectedDay = parts[2].toIntOrNull() ?: 1
                }
                val timePart = iso.substringAfter("T").substringBefore("Z").substringBefore("+")
                val cleanTimePart = if (timePart.contains(":")) {
                    timePart.split(":").take(3).joinToString(":")
                } else {
                    timePart
                }
                val timeParts = cleanTimePart.split(":")
                if (timeParts.size >= 2) {
                    selectedHour = timeParts[0].toIntOrNull() ?: 0
                    selectedMinute = timeParts[1].toIntOrNull() ?: 0
                }
            } else if (iso.contains(":")) {
                // Pure time format: "09:00:00" or "09:00"
                val timeParts = iso.split(":")
                if (timeParts.size >= 2) {
                    selectedHour = timeParts[0].toIntOrNull() ?: 0
                    selectedMinute = timeParts[1].toIntOrNull() ?: 0
                }
            } else if (iso.contains("-")) {
                // Pure date format: "2025-12-15"
                val parts = iso.split("-")
                if (parts.size >= 3) {
                    selectedYear = parts[0].toIntOrNull() ?: 2025
                    selectedMonth = parts[1].toIntOrNull() ?: 1
                    selectedDay = parts[2].toIntOrNull() ?: 1
                }
            }
        } catch (_: Exception) {
            // Keep defaults on parse failure
        }
    }

    private fun formatISO8601(): String {
        val date = "${selectedYear}-${selectedMonth.toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
        val time = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}:00Z"
        return "${date}T${time}"
    }

    private fun body(): ViewBuilder {
        val ctx = this
        return {
            // Display area: formatted text with border
            View {
                attr {
                    allCenter()
                    height(44f)
                    alignSelf(FlexAlign.STRETCH)
                    borderRadius(4f)
                    border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC.toInt())))
                    padding(8f)
                }
                event {
                    click {
                        ctx.clickListener?.invoke()
                    }
                }
                Text {
                    attr {
                        fontSize(14f)
                        // Use muted color for placeholder, normal color for actual value
                        color(if (ctx.hasValue) Color(0xFF333333.toInt()) else Color(0xFF999999.toInt()))
                        text("\uD83D\uDCC5 ${ctx.formatDisplay()}")
                    }
                }
            }
        }
    }

    companion object {
        private val MONTH_NAMES = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
    }
}

/**
 * DateTimeInput attributes.
 */
class DateTimeInputAttr : ContainerAttr() {
    /** Whether date selection is enabled */
    var enableDate: Boolean = true
    /** Whether time selection is enabled */
    var enableTime: Boolean = false
    /** Display label (shown as prefix in display area and as picker dialog title) */
    var label: String = ""
}

/**
 * DateTimeInput events.
 */
class DateTimeInputEvent : Event()

/**
 * DSL function to create a DateTimeInputView.
 */
fun ViewContainer<*, *>.DateTimeInput(init: DateTimeInputView.() -> Unit) {
    addChild(DateTimeInputView(), init)
}
