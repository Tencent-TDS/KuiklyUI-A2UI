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
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class A2UIInteractionArchitectureTest {
    private fun create(adapter: A2UIProtocolAdapter) {
        adapter.convertMessage(mapOf(
            "version" to "0.9",
            "createSurface" to mapOf(
                "surfaceId" to "s", "catalogId" to "catalog.json",
            ),
        ))
    }

    private fun components(adapter: A2UIProtocolAdapter, vararg definitions: Map<String, Any?>): List<A2UIMessage> =
        adapter.convertMessage(mapOf("updateComponents" to mapOf(
            "surfaceId" to "s", "components" to definitions.toList(),
        )))

    @Test
    fun choiceToggleAndFilterKeepModelAndVisualUpdatesSeparate() {
        val adapter = A2UIProtocolAdapter()
        create(adapter)
        adapter.convertMessage(mapOf("updateDataModel" to mapOf(
            "surfaceId" to "s", "value" to mapOf("selected" to emptyList<String>()),
        )))
        val created = components(adapter,
            mapOf("id" to "root", "component" to "Column", "children" to listOf("picker")),
            mapOf("id" to "picker", "component" to "ChoicePicker", "variant" to "mutuallyExclusive",
                "value" to mapOf("path" to "/selected"), "filterable" to true,
                "options" to listOf(mapOf("label" to "Alpha", "value" to "a"),
                    mapOf("label" to "Beta", "value" to "b"))),
        )
        val tag = created.first { adapter.getChoiceOptionValueForChipTag(it.tag) == "a" }.tag
        assertEquals("picker", adapter.getChoicePickerIdForChipTag(tag))
        assertEquals("selected", adapter.getChoicePickerBindingPath("picker"))
        assertTrue(adapter.updateChoicePickerSelection("picker", "a").isNotEmpty())
        assertTrue(adapter.getDataModelSnapshot().contains("\"selected\":[\"a\"]"))
        val beforeFilter = adapter.getDataModelSnapshot()
        assertTrue(adapter.filterChoicePickerOptions("picker", "Alpha").isNotEmpty())
        assertEquals(beforeFilter, adapter.getDataModelSnapshot())
        adapter.updateChoicePickerSelection("picker", "a")
        assertTrue(adapter.getDataModelSnapshot().contains("\"selected\":[]"))
    }

    @Test
    fun tabSelectionDoesNotMutateProtocolData() {
        val adapter = A2UIProtocolAdapter()
        create(adapter)
        val created = components(adapter,
            mapOf("id" to "root", "component" to "Column", "children" to listOf("tabs")),
            mapOf("id" to "tabs", "component" to "Tabs", "tabs" to listOf(
                mapOf("title" to "One", "child" to "one"), mapOf("title" to "Two", "child" to "two"))),
            mapOf("id" to "one", "component" to "Text", "text" to "First"),
            mapOf("id" to "two", "component" to "Text", "text" to "Second"),
        )
        val second = created.first { adapter.getTabsIdForHeaderTag(it.tag) == "tabs" &&
            adapter.getTabIndexForHeaderTag(it.tag) == 1 }
        assertNotNull(second)
        val data = adapter.getDataModelSnapshot()
        val updates = adapter.switchTab("tabs", 1)
        assertTrue(updates.isNotEmpty())
        assertTrue(updates.all { it.type == A2UIMessageType.UPDATE_COMPONENT })
        assertEquals(data, adapter.getDataModelSnapshot())
        assertTrue(adapter.switchTab("tabs", 1).isEmpty())
        assertTrue(adapter.switchTab("tabs", 99).isEmpty())
    }

    @Test
    fun modalMaterializationAndReverseRemovalAreRepeatable() {
        val adapter = A2UIProtocolAdapter()
        create(adapter)
        val inline = components(adapter,
            mapOf("id" to "root", "component" to "Column", "children" to listOf("modal")),
            mapOf("id" to "modal", "component" to "Modal", "trigger" to "trigger", "content" to "body"),
            mapOf("id" to "trigger", "component" to "Text", "text" to "Open"),
            mapOf("id" to "body", "component" to "Text", "text" to "Body"),
        )
        assertTrue(inline.any { adapter.getModalIdForTriggerTag(it.tag) == "modal" })
        val opened = adapter.openModal("modal")
        assertTrue(opened.any { it.type == A2UIMessageType.CREATE_COMPONENT && it.props?.get("text") == "Body" })
        assertTrue(adapter.openModal("modal").isEmpty())
        val removed = adapter.closeModal("modal")
        assertEquals(opened.filter { it.type == A2UIMessageType.CREATE_COMPONENT }.map { it.tag }.reversed(),
            removed.map { it.tag })
        assertTrue(removed.all { it.type == A2UIMessageType.REMOVE_COMPONENT })
        assertTrue(adapter.closeModal("modal").isEmpty())
        assertTrue(adapter.openModal("modal").isNotEmpty())
    }
}
