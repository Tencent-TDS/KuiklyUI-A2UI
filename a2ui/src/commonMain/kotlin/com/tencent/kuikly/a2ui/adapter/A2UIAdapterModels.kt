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

/**
 * Represents a ChildList template binding.
 * When a container's "children" is {"path": "/items", "componentId": "template-id"},
 * this binding is stored and expanded when the data at the path becomes available.
 */
data class ChildListBinding(
    /** The data path pointing to an array in the data model (e.g., "/events") */
    val dataPath: String,
    /** The component ID of the template to instantiate for each array item */
    val templateId: String,
    /** IDs of currently generated instances (for cleanup on re-expansion) */
    val generatedInstanceIds: MutableList<String> = mutableListOf(),
    /**
     * Per-index instance IDs from the last expansion, used by the diff-based
     * updater to REMOVE only the subtree of indices whose data has actually
     * changed (or that have been removed), while leaving unchanged indices
     * fully intact (no REMOVE/INSTANTIATE, no visual flicker). Rebuilt on
     * every expand call.
     */
    val lastInstancesByIndex: MutableMap<Int, MutableList<String>> = mutableMapOf(),
    /**
     * Per-index snapshot of the resolved item data from the last expansion.
     * Compared with the current array to decide whether a given index can
     * be reused as-is. Primitive maps / lists / scalars are compared with
     * structural equality.
     */
    val lastItemSnapshots: MutableMap<Int, Any?> = mutableMapOf()
) {
    internal var templateInputSnapshot: String? = null
}

/**
 * Represents a single tab definition within a Tabs component.
 */
data class TabDefinition(
    /** The display title of the tab (resolved at creation time) */
    val title: String,
    /** The component ID of the tab's content */
    val childId: String,
    /** The original title spec from the protocol (String or Map with path binding) for re-resolution on data update */
    val titleSpec: Any? = null
)

/**
 * Represents a Modal component definition.
 *
 * Per the A2UI spec, a Modal is a "Modal Entry Point": only the [triggerId]
 * component is shown inline; tapping it opens a dialog displaying [contentId].
 */
data class ModalDefinition(
    /** Component ID of the trigger (rendered inline; opens the modal when tapped) */
    val triggerId: String,
    /** Component ID of the content shown inside the modal when open */
    val contentId: String
)
