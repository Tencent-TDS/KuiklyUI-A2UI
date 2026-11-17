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

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.rpc.A2UIRpcError
import com.tencent.kuikly.a2ui.rpc.A2UIRpcRequest
import com.tencent.kuikly.a2ui.rpc.A2UIRpcResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class A2UIAdapterExtractionTest {
    private fun createSurface(adapter: A2UIProtocolAdapter, id: String) {
        adapter.setExpectedProtocolVersion(A2UIProtocolVersion.V1_0)
        adapter.strictV10 = true
        A2UICustomFunctionRegistry.register("hostFunction", """{"catalogId":"extraction_test_catalog_v1_0.json","returnType":"string","allowedCallers":"rendererOrAgent"}""")
        A2UICustomFunctionRegistry.register("remote", """{"catalogId":"extraction_test_catalog_v1_0.json","returnType":"object","allowedCallers":"agentOnly"}""")
        adapter.convertMessage(mapOf(
            "version" to "1.0",
            "createSurface" to mapOf(
                "surfaceId" to id,
                "catalogId" to "extraction_test_catalog_v1_0.json",
            ),
        ))
    }

    private fun incoming(adapter: A2UIProtocolAdapter, surface: String, request: String) {
        adapter.convertMessage(mapOf(
            "version" to "1.0",
            "callRendererFunction" to mapOf(
                "surfaceId" to surface,
                "functionCallId" to request,
                "callFunction" to mapOf(
                    "@call" to "hostFunction",
                    "catalogId" to "extraction_test_catalog_v1_0.json",
                    "args" to emptyMap<String, Any?>(),
                ),
            ),
        ))
    }

    @Test
    fun publicCompanionStillExists() {
        assertSame(A2UIProtocolAdapter.Companion, A2UIProtocolAdapter.Companion)
    }

    @Test
    fun dateAndIsoWeekBoundariesArePreserved() {
        assertEquals("2027 2026 53 Fri", A2UIValueFormatting.formatDateString("2027-01-01", "yyyy YYYY ww EEE"))
        assertEquals("2024 2025 01", A2UIValueFormatting.formatDateString("2024-12-30", "yyyy YYYY ww"))
        assertEquals("Thursday, February 29 at 12:05:06 AM",
            A2UIValueFormatting.formatDateString("2024-02-29T00:05:06Z", "EEEE, MMMM d 'at' hh:mm:ss a"))
        assertEquals("unparsed", A2UIValueFormatting.formatDateString("unparsed", "yyyy"))
    }

    @Test
    fun numericFormattingAndCoercionArePreserved() {
        assertEquals("850000000000.00", A2UIValueFormatting.formatDouble(850000000000.0, 2))
        assertEquals("-1234.50", A2UIValueFormatting.formatDouble(-1234.5, 2))
        assertEquals("0.0001", A2UIValueFormatting.doubleToPlainString(0.0001))
        assertEquals("-850000000000", A2UIValueFormatting.doubleToPlainString(-850000000000.0))
        assertEquals("-1,234.50", A2UIValueFormatting.addThousandsSeparator("-1234.50"))
        assertEquals(3, A2UIValueFormatting.coerceToInt(" 3.9 "))
        assertEquals(false, A2UIValueFormatting.coerceToBool(" FALSE "))
        assertNull(A2UIValueFormatting.coerceToBool(1))
    }

    @Test
    fun interpolationLexingKeepsNestedExpressionsAndQuotedCommas() {
        val args = "value: \${formatNumber(value: \${/value}, decimals: 2)}, label: 'a,b', suffix: \"c,d\""
        assertEquals(listOf(
            "value: \${formatNumber(value: \${/value}, decimals: 2)}",
            " label: 'a,b'", " suffix: \"c,d\"",
        ), A2UIValueFormatting.splitArguments(args))
        val template = "\${outer(\${/value})}"
        assertEquals(template.lastIndex, A2UIValueFormatting.findMatchingBrace(template, 2))
        assertEquals(-1, A2UIValueFormatting.findMatchingBrace("\${unclosed", 2))
    }

    @Test
    fun dataPathMaintainsExactKeyPrecedenceAndNullDeletion() {
        val model = mutableMapOf<String, Any?>(
            "chart" to mutableMapOf("items" to mutableListOf(mutableMapOf("label" to "nested"))),
            "chart.items[0].label" to "exact",
        )
        assertEquals("exact", A2UIDataPath.resolveNestedDataPath(model, "chart.items[0].label"))
        model.remove("chart.items[0].label")
        A2UIDataPath.setNestedDataPath(model, "chart/items/0/label", "updated")
        assertEquals("updated", A2UIDataPath.resolveNestedDataPath(model, "chart.items[0].label"))
        A2UIDataPath.setNestedDataPath(model, "chart/items/0/label", null)
        assertNull(A2UIDataPath.resolveNestedDataPath(model, "chart.items[0].label"))
        assertEquals("chart", A2UIDataPath.getRootDataKey("chart.items[0].label"))
        assertEquals(listOf("a", "b", "[unclosed"), A2UIDataPath.parsePathSegments("a/b[unclosed"))
    }

    @Test
    fun gradientClearsOnlyDefaultBorderAndShadow() {
        val defaults = mutableMapOf<String, Any?>(
            "backgroundLinearGradient" to "gradient",
            "backgroundColor" to A2UIColorSupport.CARD_DEFAULT_BACKGROUND_COLOR,
            "border" to A2UIColorSupport.CARD_DEFAULT_BORDER_RAW,
            "boxShadow" to A2UIColorSupport.CARD_DEFAULT_BOX_SHADOW_RAW,
            "wrapperBoxShadowView" to 1,
        )
        A2UIColorSupport.normalizeColorFieldsInPlace(defaults)
        A2UIColorSupport.clearDefaultsIfGradient(defaults)
        assertEquals(mapOf<String, Any?>("backgroundLinearGradient" to "gradient"), defaults)
        val overrides = mutableMapOf<String, Any?>(
            "backgroundLinearGradient" to "gradient", "border" to "2 solid #123456",
            "boxShadow" to "0 2 4 #44000000 0",
        )
        A2UIColorSupport.clearDefaultsIfGradient(overrides)
        assertEquals("2 solid #123456", overrides["border"])
        assertEquals("0 2 4 #44000000 0", overrides["boxShadow"])
        assertEquals(0x33000000L.toString(), A2UIColorSupport.hexToKuiklyColor("#33000000"))
        assertEquals("0", A2UIColorSupport.hexToKuiklyColor("TRANSPARENT"))
        val noColors = mapOf<String, Any?>("width" to 10)
        assertSame(noColors, A2UIColorSupport.withNormalizedColors(noColors))
    }

    @Test
    fun rpcUsesReplacedCallbacksAndSupportsReentrantCompletion() {
        val adapter = A2UIProtocolAdapter()
        createSurface(adapter, "s1")
        val firstWire = mutableListOf<String>()
        val secondWire = mutableListOf<String>()
        val firstCalls = mutableListOf<A2UIRpcRequest>()
        adapter.emitWireMessage = { firstWire += it }
        adapter.onIncomingAgentCall = { req ->
            firstCalls += req
            adapter.respondToAgent(req.surfaceId, req.requestId, null)
        }
        incoming(adapter, "s1", "first")
        assertEquals(1, firstCalls.size)
        assertEquals(1, firstWire.size)
        assertTrue(firstWire.single().contains("\"functionCallId\":\"first\""))
        assertTrue(firstWire.single().contains("\"value\":null"))
        assertFalse(firstWire.single().contains("surfaceId"))
        assertFalse(firstWire.single().contains("requestId"))
        assertFalse(firstWire.single().contains("result"))
        assertFalse(adapter.rpcDispatcher.hasPending("first"))
        adapter.emitWireMessage = { secondWire += it }
        adapter.onIncomingAgentCall = { error("host failed") }
        incoming(adapter, "s1", "second")
        assertEquals(1, firstWire.size)
        assertEquals(1, secondWire.size)
        assertTrue(secondWire.single().contains(A2UIRpcError.Codes.INTERNAL_ERROR))
        assertFalse(adapter.rpcDispatcher.hasPending("second"))
    }

    @Test
    fun rpcResetCancelsOriginalDispatcherAndReadsNewSurface() {
        val adapter = A2UIProtocolAdapter()
        createSurface(adapter, "s1")
        val dispatcher = adapter.rpcDispatcher
        val completed = mutableListOf<A2UIRpcResponse>()
        adapter.invokeAgentFunction("s1", "remote", emptyMap(), null) { completed += it }
        assertEquals(1, dispatcher.pendingCountFor())
        adapter.reset()
        assertSame(dispatcher, adapter.rpcDispatcher)
        assertEquals(1, completed.size)
        assertEquals(A2UIRpcError.Codes.SURFACE_CLOSED, completed.single().error?.code)
        createSurface(adapter, "s2")
        val calls = mutableListOf<A2UIRpcRequest>()
        adapter.onIncomingAgentCall = { calls += it }
        incoming(adapter, "s1", "old-surface")
        incoming(adapter, "s2", "new-surface")
        assertEquals(listOf("new-surface"), calls.map { it.requestId })
        assertNotNull(calls.single())
        adapter.reset()
        assertEquals(0, dispatcher.pendingCountFor())
    }
}
