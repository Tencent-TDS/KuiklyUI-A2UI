package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.parser.A2UIParseError
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class A2UIFormalV10AssetsTest {
    private val root: File
        get() = listOf(File("androidAppForKuiklyPage/src/main/assets"), File("../androidAppForKuiklyPage/src/main/assets")).first { it.isDirectory }

    private data class Replay(val parser: A2UIStreamParser, val output: MutableList<A2UIMessage>) {
        fun texts(): List<String> {
            val properties = linkedMapOf<Int, MutableMap<String, Any?>>()
            output.forEach { message -> message.props?.let { properties.getOrPut(message.tag) { linkedMapOf() }.putAll(it) } }
            return properties.values.mapNotNull { it["text"] as? String }
        }
    }

    private fun replay(asset: File): Replay {
        val envelopes = JSONObject(asset.readText()).optJSONArray("messages")!!
        val output = mutableListOf<A2UIMessage>()
        val parser = A2UIStreamParser()
        var ended = false
        parser.protocolAdapter.strictV10 = true
        parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) { output.add(message) }
            override fun onError(error: A2UIParseError) { error("${asset.name}: ${error.message}") }
            override fun onStreamEnd() { ended = true }
        })
        for (index in 0 until envelopes.length()) {
            val envelope = envelopes.optJSONObject(index)!!
            assertEquals("1.0", envelope.optString("version"), asset.name)
            parser.feed(envelope.toString())
        }
        parser.finish()
        assertTrue(ended, asset.path)
        return Replay(parser, output)
    }

    @Test
    fun allOneHundredOfficialBasicAssetsCreateRealProjectedContent() {
        val assets = File(root, "a2ui_components").walkTopDown().filter { it.extension == "json" }.toList()
        assertEquals(100, assets.size)
        for (asset in assets) {
            val output = replay(asset).output
            assertTrue(output.count { it.type == A2UIMessageType.CREATE_COMPONENT } > 1, asset.path)
        }
    }

    @Test
    fun dynamicDateHeadingAndPlainDateRemainReactive() {
        val result = replay(File(root, "a2ui_components/text/8_text_function_formatDate.json"))
        assertTrue(result.texts().contains("##### Dec 15, 2025"), result.texts().toString())
        assertTrue(result.texts().contains("2025/12/15"), result.texts().toString())
        result.output.addAll(result.parser.protocolAdapter.updateDataModelValue("publishedAt", "2026-11-23T17:30:00Z"))
        assertTrue(result.texts().contains("##### Nov 23, 2026"), result.texts().toString())
        assertTrue(result.texts().contains("2026/11/23"), result.texts().toString())
    }

    @Test
    fun booleanDemoPreservesRealFunctionValuesAndOfficialValidationThreshold() {
        val original = A2UICustomFunctionRegistry.lookup("isVip")
        A2UICustomFunctionRegistry.register("isVip", "boolean|sync")
        try {
            val asset = File(root, "a2ui_custom_functions/02_boolean_return/1_custom_is_vip.json")
            val envelopes = JSONObject(asset.readText()).optJSONArray("messages")!!
            val output = mutableListOf<A2UIMessage>()
            val parser = A2UIStreamParser()
            parser.protocolAdapter.strictV10 = true
            parser.protocolAdapter.customFunctionInvoker = { name, args, _ ->
                check(name == "isVip")
                JSONObject(args).optDouble("level", 0.0) >= 3
            }
            parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
                override fun onMessageParsed(message: A2UIMessage) { output.add(message) }
                override fun onError(error: A2UIParseError) { error(error.message) }
                override fun onStreamEnd() = Unit
            })
            for (index in 0 until envelopes.length()) parser.feed(envelopes.optJSONObject(index)!!.toString())
            parser.finish()
            val result = Replay(parser, output)
            assertTrue(result.texts().contains("当前 userLevel = 5，isVip 返回 true"), result.texts().toString())
            assertTrue(parser.protocolAdapter.evaluateChecksForComponent("vip_btn").isValid)
            output.addAll(parser.protocolAdapter.updateDataModelValue("userLevel", 2))
            assertTrue(result.texts().contains("当前 userLevel = 2，isVip 返回 false"), result.texts().toString())
            assertFalse(parser.protocolAdapter.evaluateChecksForComponent("vip_btn").isValid)
        } finally {
            A2UICustomFunctionRegistry.unregister("isVip")
            if (original != null) A2UICustomFunctionRegistry.register(original)
        }
    }
}
