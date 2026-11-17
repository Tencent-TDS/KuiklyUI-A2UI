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

internal class A2UIEventCallbacks {
    /** Callback for two-way binding: slider value changed (tag, dataKey, newValue) */
    var onSliderValueChanged: ((Int, String, Float) -> Unit)? = null

    /** Callback for two-way binding: checkbox value changed (tag, dataKey, newValue) */
    var onCheckBoxValueChanged: ((Int, String, Boolean) -> Unit)? = null

    /** Callback for two-way binding: textField value changed (tag, dataKey, newValue) */
    var onTextFieldValueChanged: ((Int, String, String) -> Unit)? = null

    /** Callback for showing native date/time picker (tag, enableDate, enableTime, currentValue, label, min, max) */
    var onShowDateTimePicker: ((Int, Boolean, Boolean, String, String, String, String) -> Unit)? = null

    /** Callback for two-way binding: choicePicker chip tapped (chipTag, parentComponentId, optionValue) */
    var onChoicePickerValueChanged: ((Int, String, String) -> Unit)? = null

    /** Callback for choicePicker filter input text changed (parentComponentId, filterText) */
    var onChoicePickerFilter: ((String, String) -> Unit)? = null

    /** Callback for tab selection: tab header tapped (tabsComponentId, tabIndex) */
    var onTabSelected: ((String, Int) -> Unit)? = null

    /** Callback for Modal open: trigger tapped (modalComponentId) */
    var onModalOpen: ((String) -> Unit)? = null

    /** Callback for Modal close: close button / backdrop tapped, or back pressed (modalComponentId) */
    var onModalClose: ((String) -> Unit)? = null

    /** Callback for a Markdown link tap inside a Text component (the link URL) */
    var onMarkdownLinkClick: ((String) -> Unit)? = null

    /** Callback for view click (action dispatch): invoked when a touchEnable=1 view is tapped */
    var onViewClick: ((Int) -> Unit)? = null
}
