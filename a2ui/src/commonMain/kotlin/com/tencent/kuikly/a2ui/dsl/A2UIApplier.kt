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

/** Public DSL execution facade; Core owns Flex, native render and view lifecycle. */
class A2UIApplier(private val host: A2UIHost) {
    private val callbacks = A2UIEventCallbacks()
    private val layout = A2UILayoutEffects(host)
    private val runtime = A2UIViewRuntime(host, layout)
    private val bindings = A2UIEventBindings(host, callbacks)
    private val text = A2UITextRenderer(host.registry, layout, callbacks)
    private val controls = A2UIControlRenderer(host.registry, bindings)
    private val media = A2UIMediaRenderer(host.registry, layout)
    private val properties = A2UIPropertyPipeline(text, controls, media, bindings)

    var onSliderValueChanged: ((Int, String, Float) -> Unit)?
        get() = callbacks.onSliderValueChanged
        set(value) { callbacks.onSliderValueChanged = value }
    var onCheckBoxValueChanged: ((Int, String, Boolean) -> Unit)?
        get() = callbacks.onCheckBoxValueChanged
        set(value) { callbacks.onCheckBoxValueChanged = value }
    var onTextFieldValueChanged: ((Int, String, String) -> Unit)?
        get() = callbacks.onTextFieldValueChanged
        set(value) { callbacks.onTextFieldValueChanged = value }
    var onShowDateTimePicker: ((Int, Boolean, Boolean, String, String, String, String) -> Unit)?
        get() = callbacks.onShowDateTimePicker
        set(value) { callbacks.onShowDateTimePicker = value }
    var onChoicePickerValueChanged: ((Int, String, String) -> Unit)?
        get() = callbacks.onChoicePickerValueChanged
        set(value) { callbacks.onChoicePickerValueChanged = value }
    var onChoicePickerFilter: ((String, String) -> Unit)?
        get() = callbacks.onChoicePickerFilter
        set(value) { callbacks.onChoicePickerFilter = value }
    var onTabSelected: ((String, Int) -> Unit)?
        get() = callbacks.onTabSelected
        set(value) { callbacks.onTabSelected = value }
    var onModalOpen: ((String) -> Unit)?
        get() = callbacks.onModalOpen
        set(value) { callbacks.onModalOpen = value }
    var onModalClose: ((String) -> Unit)?
        get() = callbacks.onModalClose
        set(value) { callbacks.onModalClose = value }
    var onMarkdownLinkClick: ((String) -> Unit)?
        get() = callbacks.onMarkdownLinkClick
        set(value) { callbacks.onMarkdownLinkClick = value }
    var onViewClick: ((Int) -> Unit)?
        get() = callbacks.onViewClick
        set(value) { callbacks.onViewClick = value }

    fun createComponent(tag: Int, viewName: String, parentTag: Int, index: Int, props: Map<String, Any?>? = null) {
        val mount = runtime.prepareMount(tag, viewName, parentTag, index, !props.isNullOrEmpty()) { view ->
            properties.apply(view, props.orEmpty())
        }
        runtime.commitNativeMount(mount, props)
    }

    fun updateComponent(tag: Int, viewName: String, props: Map<String, Any?>) {
        val view = runtime.viewForUpdate(tag) ?: return
        properties.apply(view, props)
    }

    fun removeComponent(tag: Int, parentTag: Int) {
        if (!runtime.remove(tag, parentTag)) return
        controls.remove(tag)
        bindings.remove(tag)
        text.remove(tag)
        media.remove(tag)
    }

    fun performLayout() = runtime.performLayout()

    fun updateDateTimeInputValue(tag: Int, isoString: String) = controls.updateDateTimeInputValue(tag, isoString)
    fun getDateTimeBindingPath(tag: Int): String? = bindings.getDateTimeBindingPath(tag)

    fun destroy() {
        // Pager cleanup remains owned by the host/bridge lifecycle.
    }
}
