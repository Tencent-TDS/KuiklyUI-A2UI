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

package com.tencent.kuikly.a2ui.rpc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the PR-6 design-spec test matrix for [A2UIRpcDispatcher]
 * (cases 1-6 + cancellation paths). Each test is deterministic:
 *
 *   * `clock` is injected as a mutable `Ref<Long>` so timeout flows
 *     are driven exactly by the test.
 *   * `rendererIdPrefix` is pinned so renderer-minted requestIds are
 *     byte-stable (`"renderer-TEST-1"`, `"renderer-TEST-2"`, \u2026).
 *
 * The tests intentionally touch only the dispatcher's public /
 * internal API. The adapter-level wire parsing is covered by a
 * separate test suite (see `A2UIProtocolAdapterRpcTest`).
 */
class A2UIRpcDispatcherTest {

    private class Clock {
        var now: Long = 0L
    }

    private fun newDispatcher(
        defaultTimeoutMs: Long = 1000L,
        prefix: String = "TEST",
    ): Pair<A2UIRpcDispatcher, Clock> {
        val clock = Clock()
        val dispatcher = A2UIRpcDispatcher(
            defaultTimeoutMs = defaultTimeoutMs,
            clock = { clock.now },
            rendererIdPrefix = prefix,
        )
        return dispatcher to clock
    }

    // Test 1: agent-initiated RPC happy path (host completes synchronously).
    @Test
    fun agentInitiated_completeSuccess_returnsResponse() {
        val (dispatcher, _) = newDispatcher()
        val req = A2UIRpcRequest(
            direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
            surfaceId = "s1",
            requestId = "r-1",
            name = "foo",
            args = mapOf("x" to 1),
            timeoutMs = null,
        )
        dispatcher.onIncomingAgentCall(req)
        assertTrue(dispatcher.hasPending("r-1"))

        val resp = dispatcher.completeAgentCall("s1", "r-1", result = "ok")
        assertNotNull(resp)
        assertTrue(resp.isSuccess)
        assertEquals("ok", resp.result)
        assertEquals("r-1", resp.requestId)
        assertFalse(dispatcher.hasPending("r-1"))
    }

    // Test 2: agent-initiated RPC failure path.
    @Test
    fun agentInitiated_failWithError_returnsErrorResponse() {
        val (dispatcher, _) = newDispatcher()
        dispatcher.onIncomingAgentCall(makeAgentRequest("r-2"))

        val resp = dispatcher.failAgentCall(
            surfaceId = "s1",
            requestId = "r-2",
            error = A2UIRpcError.invalidArgs("missing x"),
        )
        assertNotNull(resp)
        assertFalse(resp.isSuccess)
        assertEquals(A2UIRpcError.Codes.INVALID_ARGS, resp.error!!.code)
        assertEquals("missing x", resp.error!!.message)
        assertFalse(dispatcher.hasPending("r-2"))
    }

    // Test 3: completeAgentCall for an unknown requestId is a no-op
    // (defensive against double-completion / late-arrival after timeout).
    @Test
    fun completeAgentCall_unknownRequestId_returnsNull() {
        val (dispatcher, _) = newDispatcher()
        val resp = dispatcher.completeAgentCall("s1", "unknown", result = "ok")
        assertNull(resp)
    }

    // Test 4: renderer-initiated RPC happy path.
    @Test
    fun rendererInitiated_agentResponse_invokesCallback() {
        val (dispatcher, _) = newDispatcher()
        val callbackValues = mutableListOf<A2UIRpcResponse>()

        val req = dispatcher.beginRendererCall(
            surfaceId = "s1",
            name = "fetchUser",
            args = mapOf("id" to 42),
            timeoutMs = null,
            onComplete = { callbackValues += it },
        )
        assertEquals("renderer-TEST-1", req.requestId)
        assertTrue(dispatcher.hasPending(req.requestId))

        val fakeAgentReply = A2UIRpcResponse.success(
            direction = A2UIRpcRequest.Direction.RENDERER_TO_AGENT,
            surfaceId = "s1",
            requestId = req.requestId,
            result = mapOf("name" to "alice"),
        )
        val entry = dispatcher.onIncomingAgentResponse(fakeAgentReply)
        assertNotNull(entry)
        entry.onComplete?.invoke(fakeAgentReply)

        assertEquals(1, callbackValues.size)
        assertTrue(callbackValues[0].isSuccess)
        assertFalse(dispatcher.hasPending(req.requestId))
    }

    // Test 5: timeout expiry for renderer-initiated RPC invokes the
    // onComplete callback with Codes.TIMEOUT.
    @Test
    fun rendererInitiated_timeout_invokesCallbackWithTimeoutError() {
        val (dispatcher, clock) = newDispatcher(defaultTimeoutMs = 500L)
        var received: A2UIRpcResponse? = null
        val req = dispatcher.beginRendererCall(
            surfaceId = "s1",
            name = "slow",
            args = emptyMap(),
            onComplete = { received = it },
        )

        // Just before deadline: no expiry.
        clock.now = 499L
        assertEquals(0, dispatcher.tick().size)
        assertNull(received)
        assertTrue(dispatcher.hasPending(req.requestId))

        // At deadline: expiry fires.
        clock.now = 500L
        val expired = dispatcher.tick()
        assertEquals(listOf(req.requestId), expired)
        assertNotNull(received)
        assertFalse(received!!.isSuccess)
        assertEquals(A2UIRpcError.Codes.TIMEOUT, received!!.error!!.code)
        assertFalse(dispatcher.hasPending(req.requestId))
    }

    // Test 5b: timeout for agent-initiated RPC silently cleans up
    // (no callback to invoke per PR-6 spec).
    @Test
    fun agentInitiated_timeout_cleansPendingSilently() {
        val (dispatcher, clock) = newDispatcher(defaultTimeoutMs = 300L)
        dispatcher.onIncomingAgentCall(makeAgentRequest("r-ag"))

        clock.now = 299L
        assertEquals(0, dispatcher.tick().size)
        assertTrue(dispatcher.hasPending("r-ag"))

        clock.now = 300L
        val expired = dispatcher.tick()
        assertEquals(listOf("r-ag"), expired)
        assertFalse(dispatcher.hasPending("r-ag"))
    }

    // Test 6: cancelAll(surfaceId=null) clears every pending and
    // invokes renderer-side callbacks with SURFACE_CLOSED.
    @Test
    fun cancelAll_global_firesRendererCallbacks() {
        val (dispatcher, _) = newDispatcher()
        var cb1: A2UIRpcResponse? = null
        var cb2: A2UIRpcResponse? = null

        dispatcher.onIncomingAgentCall(makeAgentRequest("ag"))
        dispatcher.beginRendererCall("s1", "a", emptyMap()) { cb1 = it }
        dispatcher.beginRendererCall("s1", "b", emptyMap()) { cb2 = it }
        assertEquals(3, dispatcher.pendingCountFor())

        dispatcher.cancelAll(surfaceId = null)

        assertEquals(0, dispatcher.pendingCountFor())
        assertEquals(A2UIRpcError.Codes.SURFACE_CLOSED, cb1!!.error!!.code)
        assertEquals(A2UIRpcError.Codes.SURFACE_CLOSED, cb2!!.error!!.code)
    }

    // Test 6b: cancelAll(surfaceId) only affects that surface.
    @Test
    fun cancelAll_perSurface_isolatesOtherSurfaces() {
        val (dispatcher, _) = newDispatcher()
        var cbA: A2UIRpcResponse? = null
        var cbB: A2UIRpcResponse? = null

        dispatcher.beginRendererCall("sA", "a", emptyMap()) { cbA = it }
        dispatcher.beginRendererCall("sB", "b", emptyMap()) { cbB = it }

        dispatcher.cancelAll(surfaceId = "sA")

        assertEquals(1, dispatcher.pendingCountFor())
        assertNotNull(cbA)
        assertNull(cbB)
    }

    // Test 7: renderer-initiated requestIds are unique and monotonic
    // within one dispatcher.
    @Test
    fun rendererIds_areUniqueAndMonotonic() {
        val (dispatcher, _) = newDispatcher(prefix = "P")
        val ids = (1..5).map {
            dispatcher.beginRendererCall("s", "f", emptyMap()) { }.requestId
        }
        assertEquals(
            listOf(
                "renderer-P-1", "renderer-P-2", "renderer-P-3",
                "renderer-P-4", "renderer-P-5",
            ),
            ids,
        )
    }

    // Test 8: timeoutMs = 0 disables timeout (infinite wait).
    @Test
    fun timeoutZero_disablesExpiry() {
        val (dispatcher, clock) = newDispatcher(defaultTimeoutMs = 0L)
        var received: A2UIRpcResponse? = null
        val req = dispatcher.beginRendererCall(
            surfaceId = "s1",
            name = "forever",
            args = emptyMap(),
            timeoutMs = 0L,
            onComplete = { received = it },
        )

        clock.now = Long.MAX_VALUE / 2
        dispatcher.tick()
        assertNull(received)
        assertTrue(dispatcher.hasPending(req.requestId))
    }

    // Test 9: duplicate requestIds \u2014 "last writer wins" per PR-6 spec.
    @Test
    fun duplicateRequestId_overwritesPreviousPending() {
        val (dispatcher, _) = newDispatcher()
        dispatcher.onIncomingAgentCall(makeAgentRequest("dup", surfaceId = "s1"))
        // Second incoming with same requestId replaces the first.
        dispatcher.onIncomingAgentCall(makeAgentRequest("dup", surfaceId = "s1"))
        assertEquals(1, dispatcher.pendingCountFor())
    }

    // Test 10: callback that throws does NOT leak the exception back
    // to the dispatcher.
    @Test
    fun callbackThrows_isSwallowedByDispatcher() {
        val (dispatcher, clock) = newDispatcher(defaultTimeoutMs = 10L)
        dispatcher.beginRendererCall(
            surfaceId = "s1",
            name = "boom",
            args = emptyMap(),
            onComplete = { error("boom") },
        )
        clock.now = 10L
        // Would rethrow if dispatcher didn't catch.
        dispatcher.tick()
        assertEquals(0, dispatcher.pendingCountFor())
    }

    private fun makeAgentRequest(
        requestId: String,
        surfaceId: String = "s1",
        name: String = "foo",
    ) = A2UIRpcRequest(
        direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
        surfaceId = surfaceId,
        requestId = requestId,
        name = name,
        args = emptyMap(),
        timeoutMs = null,
    )
}
