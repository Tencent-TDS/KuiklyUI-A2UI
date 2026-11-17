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
import com.tencent.kuikly.core.base.Size
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.TextAreaView
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.a2ui.views.DateTimeInputView

import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toFloat
import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toColor
import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.snapSliderValue

@Suppress("DEPRECATION")
internal class A2UIControlRenderer(private val registry: A2UIViewRegistry, private val bindings: A2UIEventBindings) {
    private val checkBoxCreatorRegistered = mutableSetOf<Int>()
    fun remove(tag: Int) { checkBoxCreatorRegistered.remove(tag) }
    /**
     * Apply text-field-specific properties to an InputView.
     * Handles value/placeholder/variant and two-way binding.
     */
    fun applyTextFieldProps(view: InputView, props: Map<String, Any?>) {
        view.attr {
            props["text"]?.let { text(it.toString()) }
            props["placeholder"]?.let { placeholder(it.toString()) }
            props["placeholderColor"]?.let { placeholderColor(toColor(it)) }
            props["fontSize"]?.let { fontSize(toFloat(it)) }
            props["color"]?.let { color(toColor(it)) }
            props["fontWeight"]?.let {
                when (it.toString().lowercase()) {
                    "bold", "700" -> fontWeightBold()
                    else -> {} // normal is default
                }
            }
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "right" -> textAlignRight()
                    else -> textAlignLeft()
                }
            }
            props["editable"]?.let {
                val editableValue = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                editable(editableValue)
            }
            props["keyboardType"]?.let {
                when (it.toString().lowercase()) {
                    "password" -> keyboardTypePassword()
                    "number", "numeric" -> keyboardTypeNumber()
                    "email" -> keyboardTypeEmail()
                }
            }
            props["variant"]?.let {
                when (it.toString().lowercase()) {
                    "obscured", "password" -> keyboardTypePassword()
                    "email" -> keyboardTypeEmail()
                    "number", "numeric" -> keyboardTypeNumber()
                }
            }
            props["returnKeyType"]?.let { "returnKeyType" with it.toString() }
            props["maxTextLength"]?.let { maxTextLength(toFloat(it).toInt()) }
            props["autofocus"]?.let {
                val autoFocus = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                if (autoFocus) autofocus(true)
            }
            props["tintColor"]?.let { tintColor(toColor(it)) }
        }

        // Register two-way binding: store binding path and register event
        bindings.bindTextField(view, props)
    }

    /**
     * Apply text-area-specific properties to a TextAreaView (multi-line input).
     * Handles value/placeholder and two-way binding, similar to applyTextFieldProps
     * but for the multi-line variant (variant="longText").
     */
    fun applyTextAreaProps(view: TextAreaView, props: Map<String, Any?>) {
        view.attr {
            props["text"]?.let { text(it.toString()) }
            props["placeholder"]?.let { placeholder(it.toString()) }
            props["placeholderColor"]?.let { placeholderColor(toColor(it)) }
            props["fontSize"]?.let { fontSize(toFloat(it)) }
            props["color"]?.let { color(toColor(it)) }
            props["fontWeight"]?.let {
                when (it.toString().lowercase()) {
                    "bold", "700" -> fontWeightBold()
                    else -> {} // normal is default
                }
            }
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "right" -> textAlignRight()
                    else -> textAlignLeft()
                }
            }
            props["lineHeight"]?.let { lineHeight(toFloat(it)) }
            props["editable"]?.let {
                val editableValue = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                editable(editableValue)
            }
            props["keyboardType"]?.let {
                when (it.toString().lowercase()) {
                    "password" -> keyboardTypePassword()
                    "number", "numeric" -> keyboardTypeNumber()
                    "email" -> keyboardTypeEmail()
                }
            }
            props["returnKeyType"]?.let { "returnKeyType" with it.toString() }
            props["maxTextLength"]?.let { maxTextLength(toFloat(it).toInt()) }
            props["autofocus"]?.let {
                val autoFocus = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                if (autoFocus) autofocus(true)
            }
            props["tintColor"]?.let { tintColor(toColor(it)) }
        }

        // Register two-way binding: store binding path and register event
        bindings.bindTextField(view, props)
    }

    /**
     * Apply slider-specific properties to an [A2UISliderView] (which wraps kuikly's SliderView
     * and owns "stretch width + measured-size backfill" so kuikly's SliderView stays untouched).
     * Handles value normalization (protocol value range [min,max] → slider [0,1]).
     * Also registers two-way binding if a binding path is provided.
     */
    fun applySliderProps(view: A2UISliderView, props: Map<String, Any?>) {
        val tag = registry.getTag(view)

        // When called from updateDataModel re-evaluation, props may only contain "value"
        // without min/max. Fall back to stored binding info for correct normalization.
        val storedInfo = if (tag != null) bindings.sliderInfo(tag) else null
        val max = toFloat(props["max"] ?: storedInfo?.max ?: 1f)
        val min = toFloat(props["min"] ?: storedInfo?.min ?: 0f)
        val rawValue = toFloat(props["value"] ?: 0f)

        // PR-9: v1.0 `steps` support. We treat `steps` as the number of intervals
        // (same convention as HTML `<input type="range" step>` when combined with
        // min/max/value): `steps = N` produces `N + 1` legal stops evenly spaced
        // in [min, max]. Non-positive or absent -> continuous slider (pre-PR-9).
        // We parse once here, keep it on SliderBindingInfo so later data-driven
        // re-applies (updateDataModel path) and the drag-time progressDidChanged
        // handler can both snap against the same number.
        val steps: Int? = (props["steps"] ?: storedInfo?.steps)?.let { v ->
            when (v) {
                is Number -> v.toInt().takeIf { it > 0 }
                is String -> v.toIntOrNull()?.takeIf { it > 0 }
                else -> null
            }
        }
        val value = snapSliderValue(rawValue, min, max, steps)

        view.attr {
            // Normalize value from [min, max] to [0, 1]
            val normalizedProgress = if (max > min) (value - min) / (max - min) else 0f
            currentProgress(normalizedProgress.coerceIn(0f, 1f))

            // Color customization
            props["progressColor"]?.let { progressColor(toColor(it)) }
            props["trackColor"]?.let { trackColor(toColor(it)) }
            props["thumbColor"]?.let { thumbColor(toColor(it)) }

            // Track thickness
            props["trackThickness"]?.let { trackThickness(toFloat(it)) }

            // Thumb size
            props["thumbSize"]?.let {
                val size = toFloat(it)
                thumbSize(Size(size, size))
            }
        }

        // Register two-way binding: store binding path and register event
        bindings.bindSlider(view, tag, props, min, max, steps)
    }
    /**
     * Apply checkbox-specific properties to a CheckBoxView.
     * Handles boolean value binding and checked state.
     * Also registers two-way binding if a binding path is provided.
     *
     * Since A2UI protocol doesn't provide image URLs for checkbox states,
     * we use checkedViewCreator/defaultViewCreator with unicode characters (☑/☐).
     */
    fun applyCheckBoxProps(view: CheckBoxView, props: Map<String, Any?>) {
        val checked = when (val v = props["checked"]) {
            is Boolean -> v
            is Number -> v.toInt() != 0
            is String -> v == "true" || v == "1"
            else -> false
        }

        val tag = registry.getTag(view)
        val firstApply = tag != null && !checkBoxCreatorRegistered.contains(tag)

        if (firstApply) {
            // First-time setup: register glyph creators with theme-derived colors.
            // Incremental updates (updateDataModel diff) only carry `checked` and must
            // not re-run this block, otherwise the captured primaryColor would fall
            // back to the default #00BFFF (theme color would be lost after every diff).
            val primaryColorHex = props["__primaryColor"] as? String ?: "#00BFFF"
            val primaryColor = toColor(primaryColorHex)
            checkBoxCreatorRegistered.add(tag!!)

            view.attr {
                checked(checked)

                // Provide unicode-based view creators for visual display
                checkedViewCreator {
                    RichText {
                        attr {
                            flex(1f)
                            text("☑")
                            fontSize(20f)
                            textAlignCenter()
                            color(primaryColor)
                        }
                    }
                }
                defaultViewCreator {
                    RichText {
                        attr {
                            flex(1f)
                            text("□")
                            fontSize(20f)
                            textAlignCenter()
                            color(Color(0xFF757575.toInt()))
                        }
                    }
                }
            }
        } else {
            // Incremental update: only sync the checked state, keep the previously
            // registered creators (and their captured theme color) intact.
            view.attr {
                checked(checked)
            }
        }

        // Register two-way binding: store binding path and register event.
        // Reuse the `tag` captured above (may be null if the view hasn't been mounted yet;
        // in that case binding registration is skipped, matching the previous behavior).
        bindings.bindCheckBox(view, tag, props)
    }

    /**
     * Apply DateTimeInput-specific properties to a DateTimeInputView.
     * Handles ISO 8601 value parsing, enableDate/enableTime flags, and native picker trigger.
     */
    fun applyDateTimeInputProps(view: DateTimeInputView, props: Map<String, Any?>) {
        // Set enableDate/enableTime flags via public methods
        props["enableDate"]?.let {
            view.setEnableDate(when (it) {
                is Boolean -> it
                is String -> it == "true"
                else -> true
            })
        }
        props["enableTime"]?.let {
            view.setEnableTime(when (it) {
                is Boolean -> it
                is String -> it == "true"
                else -> false
            })
        }
        props["label"]?.let {
            view.setLabel(it.toString())
        }

        // Store min/max constraints
        props["min"]?.let { view.setMinValue(it.toString()) }
        props["max"]?.let { view.setMaxValue(it.toString()) }

        // Set the date/time value from ISO 8601 string
        props["value"]?.let {
            view.setDateTime(it.toString())
        }

        // Register click handler to trigger native picker (regardless of binding)
        bindings.bindDateTime(view, props)
    }

    /**
     * Update a DateTimeInputView's value from native picker result.
     * Called by A2UIDSLBridge when the native picker returns a result.
     */
    fun updateDateTimeInputValue(tag: Int, isoString: String) {
        val view = registry.getView(tag) as? DateTimeInputView ?: return
        view.setDateTime(isoString)
    }
}
