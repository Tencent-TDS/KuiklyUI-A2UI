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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class A2UIV10TemplateRenderingTest {
    private val catalog = "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
    private val items = listOf(
        mapOf("name" to "Espresso", "price" to 3.5),
        mapOf("name" to "Croissant", "price" to 2.8),
        mapOf("name" to "Tea latte", "price" to 4.2),
    )
    private val data = mapOf("cart" to mapOf("title" to "Your cart", "items" to items))
    private val components = listOf(
        mapOf("id" to "root", "component" to "Column", "children" to listOf("title", "items_list")),
        mapOf("id" to "title", "component" to "Text", "text" to mapOf("@path" to "/cart/title")),
        mapOf("id" to "items_list", "component" to "List", "children" to mapOf("componentId" to "item_row", "path" to "/cart/items")),
        mapOf("id" to "item_row", "component" to "Row", "children" to listOf("item_index", "item_name", "item_price")),
        mapOf("id" to "item_index", "component" to "Text", "text" to mapOf("@call" to "@index", "args" to mapOf("offset" to 1))),
        mapOf("id" to "item_name", "component" to "Text", "text" to mapOf("@path" to "name")),
        mapOf("id" to "item_price", "component" to "Text", "text" to mapOf("@path" to "price")),
    )
    private fun adapter() = A2UIProtocolAdapter().apply { strictV10 = true }
    private fun create(adapter: A2UIProtocolAdapter, initial: Map<String, Any?>? = data, inline: List<Map<String, Any?>>? = null): List<A2UIMessage> {
        val payload = mutableMapOf<String, Any?>("surfaceId" to "cart", "catalogId" to catalog)
        if (initial != null) payload["dataModel"] = initial
        if (inline != null) payload["components"] = inline
        return adapter.convertMessage(mapOf("version" to "1.0", "createSurface" to payload))
    }
    private fun update(adapter: A2UIProtocolAdapter, definitions: List<Map<String, Any?>> = components) =
        adapter.convertMessage(mapOf("version" to "1.0", "updateComponents" to mapOf("surfaceId" to "cart", "components" to definitions)))
    private fun dataUpdate(adapter: A2UIProtocolAdapter, values: List<Map<String, Any>> = items) =
        adapter.convertMessage(mapOf("version" to "1.0", "updateDataModel" to mapOf("surfaceId" to "cart", "path" to "/cart/items", "value" to values)))
    private fun creates(messages: List<A2UIMessage>) = messages.filter { it.type == A2UIMessageType.CREATE_COMPONENT }
    private fun assertCart(messages: List<A2UIMessage>) {
        val created = creates(messages)
        val list = created.single { it.viewName == "KRScrollView" }
        val rows = created.filter { it.parentTag == list.tag }
        assertEquals(3, rows.size)
        assertEquals(listOf(0, 1, 2), rows.map { it.index })
        val expected = listOf(listOf("1", "Espresso", "3.5"), listOf("2", "Croissant", "2.8"), listOf("3", "Tea latte", "4.2"))
        rows.forEachIndexed { index, row ->
            val children = created.filter { it.parentTag == row.tag }
            assertEquals(expected[index], children.map { it.props?.get("text")?.toString() })
            assertTrue(created.indexOf(row) < created.indexOf(children.first()))
        }
        assertTrue(created.indexOf(list) < created.indexOf(rows.first()))
        assertEquals(15, created.size)
    }

    @Test
    fun dataBeforeComponentsExpandsWithoutAnotherDataNotification() {
        val adapter = adapter()
        create(adapter)
        val result = update(adapter)
        assertCart(result)
        assertEquals("Your cart", creates(result).single { it.tag == 2 }.props?.get("text"))
        assertTrue(creates(dataUpdate(adapter)).isEmpty())
        assertFalse(dataUpdate(adapter).any { it.type == A2UIMessageType.REMOVE_COMPONENT })
    }

    @Test
    fun inlineDataIsResolvedBeforeFirstComponentProperties() {
        val result = create(adapter(), inline = components)
        assertCart(result)
        assertEquals("Your cart", creates(result).single { it.tag == 2 }.props?.get("text"))
        assertFalse(result.any { it.type == A2UIMessageType.UPDATE_COMPONENT && it.tag == 2 })
        assertTrue(result.indexOfFirst { it.type == A2UIMessageType.UPDATE_DATA } < result.indexOfFirst { it.type == A2UIMessageType.CREATE_COMPONENT })
    }

    @Test
    fun componentsBeforeDataStillExpandOnDataArrival() {
        val adapter = adapter()
        create(adapter, initial = null)
        val initial = update(adapter)
        assertEquals(3, creates(initial).size)
        val generated = dataUpdate(adapter)
        assertEquals(12, creates(generated).size)
        assertEquals(3, creates(generated).count { it.parentTag == 3 })
    }

    @Test
    fun templateAndChildrenCanArriveInSeparateBatches() {
        val adapter = adapter()
        create(adapter)
        val parent = update(adapter, components.take(3))
        val template = update(adapter, listOf(components[3]))
        assertTrue(creates(template).isEmpty())
        val children = update(adapter, components.drop(4))
        assertCart(parent + template + children)
        assertTrue(creates(update(adapter, listOf(components[2]))).isEmpty())
        assertTrue(creates(dataUpdate(adapter)).isEmpty())
    }

    @Test
    fun closedModalListsWaitForOverlayMount() {
        val adapter = adapter()
        create(adapter)
        val definitions = listOf(
            mapOf("id" to "root", "component" to "Column", "children" to listOf("modal")),
            mapOf("id" to "modal", "component" to "Modal", "trigger" to "trigger", "content" to "items_list"),
            mapOf("id" to "trigger", "component" to "Text", "text" to "Open"),
        ) + components.drop(2)
        val initial = update(adapter, definitions)
        assertTrue(creates(initial).none { it.viewName == "KRScrollView" })
        assertTrue(creates(dataUpdate(adapter)).isEmpty())
        val opened = adapter.openModal("modal")
        val list = creates(opened).single { it.viewName == "KRScrollView" }
        assertEquals(3, creates(opened).count { it.parentTag == list.tag })
        assertEquals(listOf("1", "Espresso", "3.5", "2", "Croissant", "2.8", "3", "Tea latte", "4.2"),
            creates(opened).filter { message -> creates(opened).any { row -> row.parentTag == list.tag && message.parentTag == row.tag } }
                .mapNotNull { it.props?.get("text")?.toString() })
    }

    @Test
    fun sameEnvelopeComponentsAndDataRespectPendingParents() {
        for (initial in listOf(null, data)) {
            val adapter = adapter()
            create(adapter, initial = initial)
            val result = adapter.convertMessage(mapOf(
                "version" to "1.0",
                "updateComponents" to mapOf("surfaceId" to "cart", "components" to components),
                "updateDataModel" to mapOf("surfaceId" to "cart", "path" to "/", "value" to data),
            ))
            assertCart(result)
            assertTrue(creates(dataUpdate(adapter)).isEmpty())
        }
    }

    @Test
    fun closedModalContentAcrossBatchesStaysDeferredAndReopensLists() {
        val adapter = adapter()
        create(adapter)
        update(adapter, listOf(
            mapOf("id" to "root", "component" to "Column", "children" to listOf("modal")),
            mapOf("id" to "modal", "component" to "Modal", "trigger" to "trigger", "content" to "content"),
            mapOf("id" to "trigger", "component" to "Text", "text" to "Open"),
            mapOf("id" to "content", "component" to "Column", "children" to listOf("items_list")),
        ))
        assertTrue(creates(update(adapter, components.drop(2))).isEmpty())
        fun assertOpened(messages: List<A2UIMessage>) {
            val list = creates(messages).single { it.viewName == "KRScrollView" }
            assertEquals(3, creates(messages).count { it.parentTag == list.tag })
            assertEquals(9, creates(messages).count { message -> creates(messages).any { row -> row.parentTag == list.tag && message.parentTag == row.tag } })
        }
        assertOpened(adapter.openModal("modal"))
        adapter.closeModal("modal")
        assertTrue(creates(dataUpdate(adapter)).isEmpty())
        assertOpened(adapter.openModal("modal"))
    }

    @Test
    fun changedTemplateAndRootOffsetInvalidateOnlyRelevantInputs() {
        val adapter = adapter()
        val initial = data + mapOf("offset" to 1, "label" to "initial")
        create(adapter, initial = initial)
        val definitions = components.map { definition ->
            when (definition["id"]) {
                "item_index" -> definition + mapOf("text" to mapOf("@call" to "@index", "args" to mapOf("offset" to mapOf("@path" to "/offset"))))
                "item_price" -> definition + mapOf("text" to mapOf("@path" to "/label"))
                else -> definition
            }
        }
        update(adapter, definitions)
        val changed = adapter.convertMessage(mapOf("version" to "1.0", "updateDataModel" to mapOf("surfaceId" to "cart", "path" to "/offset", "value" to 5)))
        assertEquals(listOf("5", "6", "7"), creates(changed).mapNotNull { it.props?.get("text")?.toString() }.filter { it.toIntOrNull() != null })
        val labels = adapter.convertMessage(mapOf("version" to "1.0", "updateDataModel" to mapOf("surfaceId" to "cart", "path" to "/label", "value" to "updated")))
        assertEquals(3, creates(labels).count { it.props?.get("text") == "updated" })
        val unrelated = adapter.convertMessage(mapOf("version" to "1.0", "updateDataModel" to mapOf("surfaceId" to "cart", "path" to "/unrelated", "value" to 1)))
        assertTrue(creates(unrelated).isEmpty())
        val changedTemplate = update(adapter, listOf(mapOf("id" to "item_name", "component" to "Text", "text" to "changed")))
        assertEquals(3, creates(changedTemplate).count { it.props?.get("text") == "changed" })
        assertTrue(creates(dataUpdate(adapter)).isEmpty())
    }

    @Test
    fun strictAbsolutePathsNeverFallbackToItemButLegacyAndLenientRemainCompatible() {
        val decoder = A2UIProtocolDecoder { "" }
        val values = A2UIValueEngine(decoder)
        val context = A2UIEvaluationContext(emptyMap(), item = mapOf("name" to "item"))
        assertEquals("item", values.evaluate(mapOf("path" to "/name"), context, false).value)
        decoder.decode(mapOf("version" to "v1.0"))
        decoder.strictV10 = true
        assertEquals("", values.evaluate(mapOf("@path" to "/name"), context, false).value)
        assertEquals("", values.evaluate(A2UIValue.PathBinding("/name"), context, false).value)
        assertEquals("item", values.evaluate(mapOf("@path" to "name"), context, false).value)
        assertEquals("root", values.evaluate(mapOf("@path" to "/name"), context.copy(data = mapOf("name" to "root")), false).value)
        decoder.strictV10 = false
        assertEquals("item", values.evaluate(mapOf("@path" to "/name"), context, false).value)
    }

    @Test
    fun indexOffsetSupportsNumericRelativeAndRootBindingsWithoutLeakingScope() {
        val decoder = A2UIProtocolDecoder { "" }
        decoder.decode(mapOf("version" to "v1.0"))
        decoder.strictV10 = true
        val values = A2UIValueEngine(decoder)
        val context = A2UIEvaluationContext(mapOf("offset" to 10), item = mapOf("offset" to 2), index = 3)
        fun index(offset: Any?): Any? = values.evaluate(mapOf("@call" to "@index", "args" to mapOf("offset" to offset)), context, false).value
        assertEquals("4", index(1))
        assertEquals("3.5", index(0.5))
        assertEquals("5", index(mapOf("@path" to "offset")))
        assertEquals("13", index(mapOf("@path" to "/offset")))
        assertEquals("3", values.evaluate(mapOf("@call" to "@index"), context, false).value)
        assertEquals("4", values.evaluate(mapOf("@call" to "@index", "args" to mapOf("offset" to 1)), context.copy(item = null), false).value)
        val missing = values.evaluate(mapOf("@call" to "@index", "args" to mapOf("offset" to 1)), context.copy(index = null), false)
        assertEquals("", missing.value)
        assertEquals(1, missing.diagnostics.size)
    }

    @Test
    fun nestedTemplatesUseInnermostIndexAndRelativeCollectionScope() {
        val adapter = adapter()
        val initial = mapOf("groups" to listOf(mapOf("items" to listOf(mapOf("name" to "A"), mapOf("name" to "B")))))
        create(adapter, initial = initial)
        val result = update(adapter, listOf(
            mapOf("id" to "root", "component" to "Column", "children" to mapOf("componentId" to "group", "path" to "/groups")),
            mapOf("id" to "group", "component" to "Column", "children" to mapOf("componentId" to "entry", "path" to "items")),
            mapOf("id" to "entry", "component" to "Row", "children" to listOf("index", "name")),
            mapOf("id" to "index", "component" to "Text", "text" to mapOf("@call" to "@index", "args" to mapOf("offset" to 1))),
            mapOf("id" to "name", "component" to "Text", "text" to mapOf("@path" to "name")),
        ))
        assertEquals(listOf("1", "A", "2", "B"), creates(result).mapNotNull { it.props?.get("text")?.toString() })
    }
}
