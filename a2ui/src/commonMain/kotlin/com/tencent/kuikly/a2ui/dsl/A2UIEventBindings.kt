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

import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.DivView
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.TextAreaView
import com.tencent.kuikly.core.views.ModalView
import com.tencent.kuikly.core.views.willDismiss
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.a2ui.views.DateTimeInputView

import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.snapSliderValue

@Suppress("DEPRECATION")
internal class A2UIEventBindings(private val host: A2UIHost, private val callbacks: A2UIEventCallbacks) {
    /** Map from slider view tag to its bound data key (for two-way binding) */
    private val sliderBindingPaths = mutableMapOf<Int, SliderBindingInfo>()

    /** Map from checkbox view tag to its bound data key (for two-way binding) */
    private val checkBoxBindingPaths = mutableMapOf<Int, String>()
    /** Map from textField view tag to its bound data key (for two-way binding) */
    private val textFieldBindingPaths = mutableMapOf<Int, String>()

    /** Map from dateTimeInput view tag to its bound data key (for two-way binding) */
    private val dateTimeBindingPaths = mutableMapOf<Int, String>()

    /** Set of static (non-bound) DateTimeInput tags that have registered click handlers */
    private val dateTimeStaticTags = mutableSetOf<Int>()

    /** Set of chip tags that have already registered click handlers (to avoid duplicates) */
    private val choiceChipRegistered = mutableSetOf<Int>()

    /** Set of filter input tags that have already been registered for text change events */
    private val choiceFilterRegistered = mutableSetOf<Int>()

    /** Set of tab header tags that have already registered click handlers (to avoid duplicates) */
    private val tabHeaderRegistered = mutableSetOf<Int>()

    /** Set of tags that have already registered Modal-related click handlers (to avoid duplicates) */
    private val modalClickRegistered = mutableSetOf<Int>()

    fun sliderInfo(tag: Int): SliderBindingInfo? = sliderBindingPaths[tag]
    fun remove(tag: Int) {
        checkBoxBindingPaths.remove(tag)
        sliderBindingPaths.remove(tag)
        textFieldBindingPaths.remove(tag)
        dateTimeBindingPaths.remove(tag)
        dateTimeStaticTags.remove(tag)
    }
    /**
     * Get the binding path for a DateTimeInput component.
     */
    fun getDateTimeBindingPath(tag: Int): String? {
        return dateTimeBindingPaths[tag]
    }

    fun bindTextField(view: InputView, props: Map<String, Any?>) {
        val bindingPath = props["__textFieldBindingPath"] as? String
        if (bindingPath != null) {
            val tag = host.registry.getTag(view) ?: return
            // Only register event once (first time)
            if (!textFieldBindingPaths.containsKey(tag)) {
                view.event {
                    textDidChange { params ->
                        val dataKey = textFieldBindingPaths[tag] ?: return@textDidChange
                        callbacks.onTextFieldValueChanged?.invoke(tag, dataKey, params.text)
                    }
                }
            }
            textFieldBindingPaths[tag] = bindingPath
        }
    }
    fun bindTextField(view: TextAreaView, props: Map<String, Any?>) {
        val bindingPath = props["__textFieldBindingPath"] as? String
        if (bindingPath != null) {
            val tag = host.registry.getTag(view) ?: return
            // Only register event once (first time)
            if (!textFieldBindingPaths.containsKey(tag)) {
                view.event {
                    textDidChange { params ->
                        val dataKey = textFieldBindingPaths[tag] ?: return@textDidChange
                        callbacks.onTextFieldValueChanged?.invoke(tag, dataKey, params.text)
                    }
                }
            }
            textFieldBindingPaths[tag] = bindingPath
        }
    }
    fun bindSlider(view: A2UISliderView, tag: Int?, props: Map<String, Any?>, min: Float, max: Float, steps: Int?) {
        val bindingPath = props["__sliderBindingPath"] as? String
        if (bindingPath != null) {
            val effectiveTag = tag ?: (host.registry.getTag(view) ?: return)
            val bindingInfo = SliderBindingInfo(
                dataKey = bindingPath,
                min = min,
                max = max,
                steps = steps
            )
            // Only register event once (first time)
            if (!sliderBindingPaths.containsKey(effectiveTag)) {
                view.event {
                    progressDidChanged { progress ->
                        // Denormalize: convert [0,1] back to [min, max]
                        val info = sliderBindingPaths[effectiveTag] ?: return@progressDidChanged
                        val denormalized = info.min + progress * (info.max - info.min)
                        // PR-9: snap the user-driven value to the nearest legal
                        // step before reporting back to the host. This keeps the
                        // bound value quantised regardless of how fine-grained
                        // the pan gesture was.
                        val snapped = snapSliderValue(denormalized, info.min, info.max, info.steps)
                        callbacks.onSliderValueChanged?.invoke(effectiveTag, info.dataKey, snapped)
                    }
                }
            }
            sliderBindingPaths[effectiveTag] = bindingInfo
        }
    }
    fun bindCheckBox(view: CheckBoxView, tag: Int?, props: Map<String, Any?>) {
        val bindingPath = props["__checkBoxBindingPath"] as? String
        if (bindingPath != null && tag != null) {
            // Only register event once (first time)
            if (!checkBoxBindingPaths.containsKey(tag)) {
                view.event {
                    checkedDidChanged { newChecked ->
                        val dataKey = checkBoxBindingPaths[tag] ?: return@checkedDidChanged
                        callbacks.onCheckBoxValueChanged?.invoke(tag, dataKey, newChecked)
                    }
                }
            }
            checkBoxBindingPaths[tag] = bindingPath
        }
    }
    fun bindDateTime(view: DateTimeInputView, props: Map<String, Any?>) {
        val bindingPath = props["__dateTimeBindingPath"] as? String
        val tag = host.registry.getTag(view) ?: return
        // Only register click handler once (first time)
        if (!dateTimeBindingPaths.containsKey(tag) && !dateTimeStaticTags.contains(tag)) {
            view.setOnClickListener {
                // Request native picker dialog with min/max constraints
                callbacks.onShowDateTimePicker?.invoke(
                    tag,
                    view.getEnableDate(),
                    view.getEnableTime(),
                    view.getISOString(),
                    view.getLabel(),
                    view.getMinValue(),
                    view.getMaxValue()
                )
            }
        }
        if (bindingPath != null) {
            dateTimeBindingPaths[tag] = bindingPath
        } else {
            // Track static (non-bound) DateTimeInput tags to avoid re-registering
            dateTimeStaticTags.add(tag)
        }
    }
    fun applyGenericClick(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        val touchEnableValue = props["touchEnable"]
        val isTouchEnabled = when (touchEnableValue) {
            is Number -> touchEnableValue.toInt() == 1
            is Boolean -> touchEnableValue
            is String -> touchEnableValue == "1" || touchEnableValue == "true"
            else -> false
        }
        if (isTouchEnabled) {
            val tag = host.registry.getTag(view)
            if (tag != null) {
                view.event {
                    click {
                        callbacks.onViewClick?.invoke(tag)
                    }
                }
            }
        }
    }
    fun applyComposite(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        // Handle ChoicePicker chip click registration
        val choiceParentId = props["__choicePickerParentId"] as? String
        val choiceOptionValue = props["__choiceOptionValue"] as? String
        if (choiceParentId != null && choiceOptionValue != null && view is DivView) {
            val tag = host.registry.getTag(view) ?: return
            // Only register click handler once
            if (!choiceChipRegistered.contains(tag)) {
                choiceChipRegistered.add(tag)
                view.event {
                    click {
                        callbacks.onChoicePickerValueChanged?.invoke(tag, choiceParentId, choiceOptionValue)
                    }
                }
            }
        }

        // Handle ChoicePicker filter input text change
        val choiceFilterId = props["__choicePickerFilterId"] as? String
        if (choiceFilterId != null && view is InputView) {
            val tag = host.registry.getTag(view) ?: return
            if (!choiceFilterRegistered.contains(tag)) {
                choiceFilterRegistered.add(tag)
                view.event {
                    textDidChange { params ->
                        callbacks.onChoicePickerFilter?.invoke(choiceFilterId, params.text)
                    }
                }
            }
        }

        // Handle Tabs header click registration
        val tabsParentId = props["__tabsParentId"] as? String
        val tabIndex = props["__tabIndex"] as? Int
        if (tabsParentId != null && tabIndex != null && view is DivView) {
            val tag = host.registry.getTag(view) ?: return
            if (!tabHeaderRegistered.contains(tag)) {
                tabHeaderRegistered.add(tag)
                view.event {
                    click {
                        callbacks.onTabSelected?.invoke(tabsParentId, tabIndex)
                    }
                }
            }
        }

        // Handle Modal interactions (trigger to open, close button/backdrop to close,
        // dialog to absorb taps, and modal window back-press dismissal).
        applyModalProps(view, props)
    }
    /**
     * Wire Modal-related click handlers based on the marker props injected by the adapter:
     * - `__modalTriggerId`: tapping this view opens the modal.
     * - `__modalClose`: tapping this view (close button / backdrop) closes the modal.
     * - `__modalAbsorb`: this view swallows taps so they don't reach the backdrop.
     * - `__modalId` (on the ModalView itself): wire system back-press to close.
     */
    private fun applyModalProps(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        val triggerModalId = props["__modalTriggerId"] as? String
        val closeModalId = props["__modalClose"] as? String
        val absorb = props["__modalAbsorb"] != null
        val modalId = props["__modalId"] as? String

        if (triggerModalId == null && closeModalId == null && !absorb && modalId == null) return

        val tag = host.registry.getTag(view) ?: return
        if (modalClickRegistered.contains(tag)) return
        modalClickRegistered.add(tag)

        // Click handlers are wired on container (DivView) targets, matching how the
        // adapter builds the trigger, backdrop, dialog, and close row.
        val divView = view as? DivView
        if (divView != null) {
            if (triggerModalId != null) {
                divView.event { click { callbacks.onModalOpen?.invoke(triggerModalId) } }
            }
            if (closeModalId != null) {
                divView.event { click { callbacks.onModalClose?.invoke(closeModalId) } }
            }
            if (absorb) {
                // No-op click to keep the tap from bubbling to the backdrop close handler.
                divView.event { click { } }
            }
        }
        if (modalId != null && view is ModalView) {
            // Size the Kuikly flex frame so the backdrop fills the (natively full-screen)
            // modal window and child layout has real bounds to work with.
            val (modalWidth, modalHeight) = host.fullScreenModalSize()
            view.attr {
                width(modalWidth)
                height(modalHeight)
            }
            // System back-press dismisses the modal.
            view.event { willDismiss { callbacks.onModalClose?.invoke(modalId) } }
        }
    }
}

    internal data class SliderBindingInfo(
        val dataKey: String,
        val min: Float,
        val max: Float,
        val steps: Int? = null
    )
