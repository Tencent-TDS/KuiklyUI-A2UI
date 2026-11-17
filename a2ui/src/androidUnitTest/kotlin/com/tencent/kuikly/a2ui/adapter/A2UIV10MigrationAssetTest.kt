package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.parser.A2UIParseError
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class A2UIV10MigrationAssetTest {
    private data class Replay(val parser: A2UIStreamParser, val messages: List<A2UIMessage>) {
        val adapter get() = parser.protocolAdapter
        val creates get() = messages.filter { it.type == A2UIMessageType.CREATE_COMPONENT }
        fun finalProps(tag: Int): Map<String, Any?> = buildMap {
            messages.filter { it.tag == tag }.forEach { putAll(it.props.orEmpty()) }
        }
    }

    private fun replay(relative: String): Replay {
        val roots = listOf(File("androidAppForTest/src/main/assets/v1_0"), File("../androidAppForTest/src/main/assets/v1_0"))
        val asset = roots.map { File(it, relative) }.first { it.isFile }
        val envelopes = JSONObject(asset.readText()).optJSONArray("messages")!!
        val output = mutableListOf<A2UIMessage>()
        val parser = A2UIStreamParser()
        parser.protocolAdapter.strictV10 = true
        parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) { output.add(message) }
            override fun onError(error: A2UIParseError) { error(error.message) }
            override fun onStreamEnd() = Unit
        })
        for (index in 0 until envelopes.length()) parser.feed(envelopes.optJSONObject(index)!!.toString())
        parser.finish()
        return Replay(parser, output)
    }

    @Test
    fun bodyAndDefaultTextRemainVisible() {
        val replay = replay("a2ui_components/text/4_text_body.json")
        val texts = replay.creates.mapNotNull { it.props?.get("text")?.toString() }
        assertEquals(3, texts.size)
        assertTrue(texts[0].startsWith("This is body text"))
        assertTrue(texts[1].contains("WITHOUT variant"))
        assertTrue(texts[2].startsWith("Long body text"))
    }

    @Test
    fun markdownTitleStaysReactiveAfterInitialDataArrives() {
        val replay = replay("a2ui_components/text/6_text_path_binding.json")
        val title = replay.creates.single { replay.finalProps(it.tag)["text"] == "### Bound Title from Data Model" }
        val updates = replay.adapter.updateDataModelValue("title", "Updated title")
        assertTrue(updates.any { it.tag == title.tag && it.props?.get("text") == "### Updated title" })
        assertFalse(updates.any { it.type == A2UIMessageType.CREATE_COMPONENT })
    }

    @Test
    fun bothEmailValidationFunctionsReturnLiveValidationResults() {
        val replay = replay("a2ui_components/textfield/6_textfield_email_checks_vs_legacy_regexp.json")
        assertTrue(replay.adapter.evaluateChecksForComponent("tf_checks").isValid)
        assertTrue(replay.adapter.evaluateChecksForComponent("tf").isValid)
        replay.adapter.updateDataModelValue("email", "user@example.com")
        replay.adapter.updateDataModelValue("emailRegexp", "user@example.com")
        assertTrue(replay.adapter.evaluateChecksForComponent("tf_checks").isValid)
        assertTrue(replay.adapter.evaluateChecksForComponent("tf").isValid)
        replay.adapter.updateDataModelValue("email", "not-an-email")
        replay.adapter.updateDataModelValue("emailRegexp", "not-an-email")
        assertFalse(replay.adapter.evaluateChecksForComponent("tf_checks").isValid)
        assertFalse(replay.adapter.evaluateChecksForComponent("tf").isValid)
        assertTrue(replay.adapter.getDataModelSnapshot().contains("not-an-email"))
    }

    @Test
    fun verticalListPreservesSixOrderedChildren() {
        val replay = replay("a2ui_components/list/1_list_direction_vertical.json")
        val list = replay.creates.single { it.viewName == "KRScrollView" }
        val children = replay.creates.filter { it.parentTag == list.tag }
        assertEquals((1..6).map { "Vertical Item $it" }, children.map { replay.finalProps(it.tag)["text"] })
        assertEquals((0..5).toList(), children.map { it.index })
    }

    @Test
    fun childListExpandsRelativeValuesWithoutChangingCollectionPath() {
        val replay = replay("a2ui_databinding_tests/03_child_list_template/1_column_template.json")
        val texts = replay.creates.mapNotNull { replay.finalProps(it.tag)["text"]?.toString() }
        assertTrue(texts.contains("• 阅读 A2UI 协议规范"))
        assertTrue(texts.contains("• 补齐数据绑定测试用例"))
        assertTrue(texts.contains("• 跑通 Android / iOS 双端"))
    }

    @Test
    fun weatherPreservesFiveForecastInstancesAndRootDependencies() {
        val replay = replay("a2ui_examples/04_weather-current.json")
        val creates = replay.creates
        val forecastParent = creates.single { parent -> creates.count { it.parentTag == parent.tag && it.viewName == "KRView" } == 5 }
        val days = creates.filter { it.parentTag == forecastParent.tag }
        assertEquals(5, days.size)
        days.forEach { day -> assertEquals(3, creates.count { it.parentTag == day.tag }) }
        val texts = creates.mapNotNull { replay.finalProps(it.tag)["text"]?.toString() }
        assertTrue(texts.containsAll(listOf("# 72°", "## 58°", "### Austin, TX", "74°", "76°", "71°", "73°", "75°")))
        val temperature = assertNotNull(creates.find { replay.finalProps(it.tag)["text"] == "# 72°" })
        assertTrue(replay.adapter.updateDataModelValue("tempHigh", 80).any { it.tag == temperature.tag && it.props?.get("text") == "# 80°" })
    }
}
