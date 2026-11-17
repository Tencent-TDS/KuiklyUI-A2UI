package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class A2UIFormalRpcWireTest {
    @Test
    fun dumpActualFormalRpcWireMessages() {
        val catalog = "custom_functions_catalog_v1_0.json"
        val previous = listOf("getGreeting", "fetchUser").associateWith { A2UICustomFunctionRegistry.lookupExact(it, catalog) }
        try {
            A2UICustomFunctionRegistry.register("getGreeting", """{"catalogId":"custom_functions_catalog_v1_0.json","returnType":"string","allowedCallers":"rendererOrAgent","args":{"properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false}}""")
            A2UICustomFunctionRegistry.register("fetchUser", """{"catalogId":"custom_functions_catalog_v1_0.json","returnType":"object","allowedCallers":"agentOnly","args":{"properties":{"id":{"type":"number"}},"required":["id"],"additionalProperties":false}}""")
            val adapter = A2UIProtocolAdapter()
            adapter.setExpectedProtocolVersion(A2UIProtocolVersion.V1_0)
            adapter.strictV10 = true
            adapter.convertMessage(mapOf("version" to "1.0", "createSurface" to mapOf("surfaceId" to "rpc-demo", "catalogId" to catalog)))
            val emitted = mutableListOf<String>()
            adapter.emitWireMessage = { emitted += it }
            adapter.invokeAgentFunction("rpc-demo", "fetchUser", mapOf("id" to 42), null) {}
            adapter.onIncomingAgentCall = { request ->
                adapter.respondToAgent(request.surfaceId, request.requestId, "Hello, ${request.args["name"]}!")
            }
            adapter.convertMessage(mapOf("version" to "1.0", "callRendererFunction" to mapOf(
                "surfaceId" to "rpc-demo", "functionCallId" to "agent-call-1",
                "callFunction" to mapOf("@call" to "getGreeting", "catalogId" to catalog, "args" to mapOf("name" to "Ada")),
            )))
            adapter.convertMessage(mapOf("version" to "1.0", "callRendererFunction" to mapOf(
                "functionCallId" to "agent-call-2",
                "callFunction" to mapOf("@call" to "fetchUser", "catalogId" to catalog, "args" to mapOf("id" to 42)),
            )))
            assertEquals(3, emitted.size)
            assertTrue(emitted[0].contains("callAgentFunction"))
            assertTrue(emitted[1].contains("Hello, Ada!"))
            assertTrue(emitted[2].contains("FUNCTION_NOT_ALLOWED"))
            emitted.forEach { println("A2UI_FORMAL_RPC_WIRE\t$it") }
        } finally {
            previous.forEach { (name, descriptor) ->
                A2UICustomFunctionRegistry.unregister(name, catalog)
                descriptor?.let(A2UICustomFunctionRegistry::register)
            }
        }
    }
}
