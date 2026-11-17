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

import com.tencent.kuikly.a2ui.protocol.A2UIMessage

internal class A2UIInteractionState {
    private val actions = mutableMapOf<Int, Any?>()
    private val disabledActions = mutableMapOf<Int, Boolean>()
    private val selectedTabs = mutableMapOf<String, Int>()
    private val openModals = mutableSetOf<String>()

    fun registerAction(tag: Int, action: Any?) { actions[tag] = action }
    fun setDisabled(tag: Int, disabled: Boolean) { disabledActions[tag] = disabled }
    fun action(tag: Int): Any? = if (disabledActions[tag] == true) null else actions[tag]
    fun actionTags(): Set<Int> = actions.keys
    fun selectedTab(id: String): Int = selectedTabs[id] ?: 0
    fun initializeTab(id: String) { selectedTabs.getOrPut(id) { 0 } }
    fun selectTab(id: String, index: Int) { selectedTabs[id] = index }
    fun isModalOpen(id: String): Boolean = id in openModals
    fun setModalOpen(id: String, open: Boolean) {
        if (open) openModals.add(id) else openModals.remove(id)
    }
    fun reset() {
        actions.clear()
        disabledActions.clear()
    }
}

/** User intent and local state; view identities and visual messages belong to projection. */
internal class A2UIInteractionController(
    private val model: A2UISurfaceModel,
    private val decoder: A2UIProtocolDecoder,
    private val values: A2UIValueEngine,
    private val projection: A2UIComponentProjection,
    private val state: A2UIInteractionState,
) {
    private fun commit(messages: List<A2UIMessage>): List<A2UIMessage> =
        projection.commitBatch(A2UIProjectionBatch(messages))

    fun reset() = state.reset()

    fun getActionForTag(tag: Int): String? {
        val action = state.action(tag) ?: return null
        val context = A2UIEvaluationContext(model.data, catalogId = model.catalogId)
        return A2UIJsonValue.serializeToJson(values.action(action, context))
    }

    fun getActionTags(): Set<Int> = state.actionTags()

    fun updateDataModelValue(key: String, value: Any?): List<A2UIMessage> {
        val change = model.write(key, value, A2UIUpdateOrigin.BINDING)
        return commit(projection.refreshBindingValue(change))
    }

    fun updateChoicePickerSelection(componentId: String, selectedValue: String): List<A2UIMessage> {
        val definition = model.definitions[componentId] ?: return emptyList()
        val variant = definition["variant"] as? String ?: "mutuallyExclusive"
        val valueSpec = definition["value"]
        val bindingPath = (valueSpec as? Map<*, *>)?.let { decoder.readPathKey(it)?.removePrefix("/") }
        val effectivePath = bindingPath ?: "__cp_internal_${componentId}"
        if (bindingPath == null && model.read(effectivePath) == null) {
            val initial = when (valueSpec) {
                is List<*> -> valueSpec.map { it.toString() }
                is String -> listOf(valueSpec)
                else -> emptyList<String>()
            }
            model.write(effectivePath, initial, A2UIUpdateOrigin.CHOICE)
        }
        val selection = when (val raw = model.read(effectivePath)) {
            is List<*> -> raw.map { it.toString() }
            is String -> listOf(raw)
            else -> emptyList()
        }
        val updated = when (variant) {
            "mutuallyExclusive" -> if (selectedValue in selection) emptyList() else listOf(selectedValue)
            "multipleSelection" -> if (selectedValue in selection) selection.filter { it != selectedValue }
                else selection + selectedValue
            else -> listOf(selectedValue)
        }
        model.write(effectivePath, updated, A2UIUpdateOrigin.CHOICE)
        return commit(projection.renderChoiceSelection(componentId, updated, bindingPath, variant))
    }

    fun filterChoicePickerOptions(componentId: String, filterText: String): List<A2UIMessage> =
        commit(projection.filterChoicePickerOptions(componentId, filterText))

    fun switchTab(tabsId: String, newIndex: Int): List<A2UIMessage> {
        val count = projection.tabCount(tabsId) ?: return emptyList()
        if (newIndex == state.selectedTab(tabsId) || newIndex !in 0 until count) return emptyList()
        state.selectTab(tabsId, newIndex)
        return commit(projection.renderTabSelection(tabsId, newIndex))
    }

    fun openModal(modalId: String): List<A2UIMessage> {
        val contentId = projection.modalContentId(modalId) ?: return emptyList()
        if (state.isModalOpen(modalId) || model.definitions[contentId] == null) return emptyList()
        state.setModalOpen(modalId, true)
        return commit(projection.renderModalOpen(modalId))
    }

    fun closeModal(modalId: String): List<A2UIMessage> {
        if (!state.isModalOpen(modalId)) return emptyList()
        state.setModalOpen(modalId, false)
        return commit(projection.renderModalClose(modalId))
    }

    fun getChoicePickerIdForChipTag(chipTag: Int): String? = projection.getChoicePickerIdForChipTag(chipTag)
    fun getChoiceOptionValueForChipTag(chipTag: Int): String? = projection.getChoiceOptionValueForChipTag(chipTag)
    fun getChoicePickerBindingPath(componentId: String): String? = projection.getChoicePickerBindingPath(componentId)
    fun getTabsIdForHeaderTag(headerTag: Int): String? = projection.getTabsIdForHeaderTag(headerTag)
    fun getTabIndexForHeaderTag(headerTag: Int): Int = projection.getTabIndexForHeaderTag(headerTag)
    fun getModalIdForTriggerTag(triggerTag: Int): String? = projection.getModalIdForTriggerTag(triggerTag)
}
