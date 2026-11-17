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

import com.tencent.kuikly.a2ui.parser.A2UIParseError
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import java.io.File
import com.tencent.kuikly.core.base.CrossPlatFeature
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.KotlinMethod
import com.tencent.kuikly.core.manager.NativeMethod
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.DivView
import com.tencent.kuikly.a2ui.views.DateTimeInputView
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@Suppress("DEPRECATION")
class A2UIApplierCoreTest {
    private class RecordingNative : NativeBridge.NativeBridgeDelegate {
        val calls = mutableListOf<Pair<Int, List<Any?>>>()
        override fun callNative(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?, arg4: Any?, arg5: Any?): Any? {
            calls.add(methodId to listOf(arg0, arg1, arg2, arg3, arg4, arg5))
            return when (methodId) {
                NativeMethod.CALCULATE_RENDER_VIEW_SIZE -> "40.0|20.0"
                NativeMethod.FIRE_FATAL_EXCEPTION -> error("Core fatal: $arg1")
                else -> null
            }
        }
    }

    private fun withPager(block: (A2UIPager, A2UIApplier, RecordingNative) -> Unit) {
        val id = "applier-core-test"
        val oldPage = BridgeManager.currentPageId
        val oldCatch = BridgeManager.catchException
        val oldFlat = CrossPlatFeature.isIgnoreRenderViewForFlatLayer
        val oldContext = NativeBridge.isContextThread
        val native = RecordingNative()
        BridgeManager.init(false)
        NativeBridge.isContextThread = true
        CrossPlatFeature.isIgnoreRenderViewForFlatLayer = true
        BridgeManager.registerNativeBridge(id, NativeBridge().apply { delegate = native })
        PagerManager.registerPageRouter(id) { A2UIPager() }
        BridgeManager.currentPageId = id
        try {
            PagerManager.createPager(id, id, """{"rootViewWidth":375.0,"rootViewHeight":10000.0,"nativeBuild":2}""")
            val pager = PagerManager.getPager(id) as A2UIPager
            block(pager, A2UIApplier(pager), native)
        } finally {
            BridgeManager.callKotlinMethod(KotlinMethod.DESTROY_INSTANCE, id)
            BridgeManager.currentPageId = oldPage
            BridgeManager.init(oldCatch)
            CrossPlatFeature.isIgnoreRenderViewForFlatLayer = oldFlat
            NativeBridge.isContextThread = oldContext
        }
    }

    @Test
    fun publicJvmSignaturesRemainCompatibleWithLegacy() {
        fun signatures(type: Class<*>): Set<String> = type.declaredMethods
            .filter {
                java.lang.reflect.Modifier.isPublic(it.modifiers) &&
                    !(it.isSynthetic && it.name.startsWith("access\$"))
            }
            .map { method ->
                val parameters = method.parameterTypes.joinToString { if (it == type) "Self" else it.name }
                "${method.name}($parameters):${method.returnType.name}"
            }.toSet()
        assertEquals(signatures(A2UILegacyApplier::class.java), signatures(A2UIApplier::class.java))
        assertEquals(listOf(A2UIHost::class.java), A2UIApplier::class.java.constructors.single().parameterTypes.toList())
    }

    @Test
    fun realCoreCreatesUpdatesAndRemovesTheSameView() = withPager { pager, applier, native ->
        applier.createComponent(1, "KRView", 0, 0, mapOf("width" to 120, "height" to 40, "backgroundColor" to "#FF0000"))
        val view = assertNotNull(pager.registry.getView(1))
        assertTrue(view is DivView)
        assertTrue(pager.getSubviews().contains(view))
        assertEquals(120f, view.flexNode.styleWidth)
        applier.updateComponent(1, "Text", mapOf("width" to 80))
        assertSame(view, pager.registry.getView(1))
        assertEquals(80f, view.flexNode.styleWidth)
        applier.performLayout()
        assertTrue(native.calls.any { it.first == NativeMethod.CREATE_RENDER_VIEW })
        applier.removeComponent(1, 0)
        assertNull(pager.registry.getView(1))
        assertFalse(pager.getSubviews().contains(view))
        assertNull(pager.registry.getTagByNativeRef(view.nativeRef))
    }

    @Test
    fun composeControlsReceivePropertiesBeforeCoreBodyInitialization() = withPager { pager, applier, _ ->
        applier.createComponent(1, "CheckBox", 0, 0, mapOf("width" to 24, "height" to 24, "checked" to true, "__primaryColor" to "#FF0000"))
        val check = pager.registry.getView(1) as CheckBoxView
        assertEquals(24f, check.flexNode.styleWidth)
        assertTrue(check.getSubviews().isNotEmpty())
        applier.createComponent(2, "DateTimeInput", 0, 1, mapOf("width" to 200, "height" to 40, "enableDate" to true, "enableTime" to false, "value" to "2026-11-23", "__dateTimeBindingPath" to "date"))
        val date = pager.registry.getView(2) as DateTimeInputView
        assertTrue(date.getEnableDate())
        assertFalse(date.getEnableTime())
        assertEquals("2026-11-23T00:00:00Z", date.getISOString())
        assertEquals("date", applier.getDateTimeBindingPath(2))
        assertTrue(date.getSubviews().isNotEmpty())
    }

    @Test
    fun inlineCartAssetCreatesThreeRowsInRealScrollerContent() = withPager { pager, applier, _ ->
        val roots = listOf(File("androidAppForTest/src/main/assets"), File("../androidAppForTest/src/main/assets"))
        val asset = roots.map { File(it, "v1_0/a2ui_v10_tests/02_pr3_createsurface_inline_datamodel.json") }.first { it.isFile }
        val envelopes = JSONObject(asset.readText()).optJSONArray("messages")!!
        val parsed = mutableListOf<A2UIMessage>()
        val parser = A2UIStreamParser()
        parser.protocolAdapter.strictV10 = true
        parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) {
                parsed.add(message)
                when (message.type) {
                    A2UIMessageType.CREATE_COMPONENT -> applier.createComponent(message.tag, message.viewName ?: "KRView", message.parentTag, message.index, message.props)
                    A2UIMessageType.UPDATE_COMPONENT -> message.props?.let { applier.updateComponent(message.tag, message.viewName ?: "KRView", it) }
                    A2UIMessageType.REMOVE_COMPONENT -> applier.removeComponent(message.tag, message.parentTag)
                    A2UIMessageType.STREAM_END -> applier.performLayout()
                    else -> Unit
                }
            }
            override fun onError(error: A2UIParseError) { error(error.message) }
            override fun onStreamEnd() = Unit
        })
        for (index in 0 until envelopes.length()) parser.feed(envelopes.optJSONObject(index)!!.toString())
        parser.finish()
        val creates = parsed.filter { it.type == A2UIMessageType.CREATE_COMPONENT }
        val list = creates.single { it.viewName == "KRScrollView" }
        val rows = creates.filter { it.parentTag == list.tag }
        val container = assertNotNull(pager.registry.getContainer(list.tag)).realContainerView()
        assertEquals(3, rows.size)
        assertEquals(3, container.getSubviews().size)
        rows.forEach { row ->
            assertTrue(container.getSubviews().contains(pager.registry.getView(row.tag)))
            assertEquals(3, assertNotNull(pager.registry.getContainer(row.tag)).getSubviews().size)
        }
        assertEquals(listOf("Your cart", "1", "Espresso", "3.5", "2", "Croissant", "2.8", "3", "Tea latte", "4.2"),
            creates.mapNotNull { it.props?.get("text")?.toString() })
    }

    private fun withMigrationAsset(relative: String, check: (A2UIPager, List<A2UIMessage>) -> Unit) = withPager { pager, applier, _ ->
        val roots = listOf(File("androidAppForTest/src/main/assets/v1_0"), File("../androidAppForTest/src/main/assets/v1_0"))
        val asset = roots.map { File(it, relative) }.first { it.isFile }
        val envelopes = JSONObject(asset.readText()).optJSONArray("messages")!!
        val output = mutableListOf<A2UIMessage>()
        val parser = A2UIStreamParser()
        parser.protocolAdapter.strictV10 = true
        parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) {
                output.add(message)
                when (message.type) {
                    A2UIMessageType.CREATE_COMPONENT -> applier.createComponent(message.tag, message.viewName ?: "KRView", message.parentTag, message.index, message.props)
                    A2UIMessageType.UPDATE_COMPONENT -> message.props?.let { applier.updateComponent(message.tag, message.viewName ?: "KRView", it) }
                    A2UIMessageType.REMOVE_COMPONENT -> applier.removeComponent(message.tag, message.parentTag)
                    A2UIMessageType.STREAM_END -> applier.performLayout()
                    else -> Unit
                }
            }
            override fun onError(error: A2UIParseError) { error(error.message) }
            override fun onStreamEnd() = Unit
        })
        for (index in 0 until envelopes.length()) parser.feed(envelopes.optJSONObject(index)!!.toString())
        parser.finish()
        check(pager, output.filter { it.type == A2UIMessageType.CREATE_COMPONENT })
    }

    @Test
    fun migratedListMountsSixChildrenInTheRealScrollContent() = withMigrationAsset("a2ui_components/list/1_list_direction_vertical.json") { pager, creates ->
        val list = creates.single { it.viewName == "KRScrollView" }
        val children = creates.filter { it.parentTag == list.tag }
        val container = assertNotNull(pager.registry.getContainer(list.tag)).realContainerView()
        assertEquals(6, children.size)
        assertEquals(6, container.getSubviews().size)
        children.forEach { assertTrue(container.getSubviews().contains(pager.registry.getView(it.tag))) }
    }

    @Test
    fun migratedWeatherMountsAllFiveForecastRowsAndFifteenTexts() = withMigrationAsset("a2ui_examples/04_weather-current.json") { pager, creates ->
        val forecast = creates.single { parent -> creates.count { it.parentTag == parent.tag && it.viewName == "KRView" } == 5 }
        val days = creates.filter { it.parentTag == forecast.tag }
        val parent = assertNotNull(pager.registry.getContainer(forecast.tag)).realContainerView()
        assertEquals(5, parent.getSubviews().size)
        days.forEach { day ->
            val children = creates.filter { it.parentTag == day.tag }
            val container = assertNotNull(pager.registry.getContainer(day.tag)).realContainerView()
            assertTrue(parent.getSubviews().contains(pager.registry.getView(day.tag)))
            assertEquals(3, children.size)
            assertEquals(3, container.getSubviews().size)
            children.forEach { assertTrue(container.getSubviews().contains(pager.registry.getView(it.tag))) }
        }
    }

    @Test
    fun scrollerChildrenUseTheRealContentContainer() = withPager { pager, applier, _ ->
        applier.createComponent(1, "List", 0, 0, mapOf("width" to 200, "height" to 120))
        applier.createComponent(2, "View", 1, 0, mapOf("width" to 80, "height" to 30))
        val container = assertNotNull(pager.registry.getContainer(1)).realContainerView()
        val child = assertNotNull(pager.registry.getView(2))
        assertTrue(container.getSubviews().contains(child))
        applier.removeComponent(2, 1)
        assertFalse(container.getSubviews().contains(child))
        assertNull(pager.registry.getView(2))
    }
}
