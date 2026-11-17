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

package com.tencent.kuikly.a2ui.demo.pages

import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.dsl.embed.A2UISurface
import com.tencent.kuikly.a2ui.dsl.embed.A2UISurfaceDSLView
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Column

/**
 * A2UIRpcDemoPager — a minimal, self-contained Kuikly page that demonstrates
 * the **A2UI v1.0 bidirectional RPC roundtrip** entirely on-device, no real
 * agent / backend needed.
 *
 * Scenarios covered (both directions of `evolution_guide.md §Bidirectional
 * function calls`):
 *
 *  1. **inbound `callRendererFunction` → local handler → `rendererFunctionResponse`**
 *     The "mock agent" button below synthesises a `callRendererFunction`
 *     wire envelope and feeds it into the surface via `writeStream`, exactly
 *     the way a real agent transport would deliver it. The host-registered
 *     JSON handler (installed via
 *     [A2UIBridgeManager.setRendererFunctionJsonHandler]) intercepts the
 *     request, pattern-matches on `name == "getGreeting"`, and calls
 *     [A2UIBridgeManager.respondToAgentJson] to close the loop. The response
 *     envelope is picked up by the Kotlin core and emitted back through
 *     `methodId=108 SEND_RPC_ENVELOPE` (which, in Kuikly-embedded mode,
 *     routes through `A2UIBridgeModule.rpcEnvelope` → `agentOutbox` below).
 *
 *  2. **outbound `callAgentFunction` → mock agent reply → `onComplete`**
 *     The "Fetch user from agent" button calls
 *     [A2UIBridgeManager.invokeAgentFunctionJson], which mints a requestId
 *     and emits a `callAgentFunction` envelope through the same
 *     `SEND_RPC_ENVELOPE` channel. We capture the envelope in the `agentOutbox`
 *     sink below, feed a fake `agentFunctionResponse` back through
 *     [A2UISurfaceDSLView.writeStream], and the Kotlin RPC dispatcher resolves
 *     the `onComplete` callback we passed in.
 *
 * ### UI layout
 *
 * ```
 *   ┌────────────────────────────────────────┐
 *   │ A2UI v1.0 RPC Demo                     │
 *   │ Status: (waiting for user)             │
 *   ├────────────────────────────────────────┤
 *   │ ╭─  v1.0 Surface  ────────────────╮   │
 *   │ │  "Tap a button below ..."       │   │  ← rendered from createSurface
 *   │ ╰─────────────────────────────────╯   │
 *   ├────────────────────────────────────────┤
 *   │ [ Mock agent -> getGreeting(Ada) ]   │  ← inbound RPC
 *   │ [ Fetch user from agent (id=42)    ]   │  ← outbound RPC
 *   ├────────────────────────────────────────┤
 *   │ Event log                              │
 *   │  - host: received callRendererFunction │
 *   │  - host: responded ok                  │
 *   │  - host: invokeAgentFunction req-xxx   │
 *   │  - mock agent: agentFunctionResponse   │
 *   │  - host: onComplete result={...}       │
 *   └────────────────────────────────────────┘
 * ```
 *
 * ### Why this lives in `:demo`
 *
 * The RPC commonMain APIs (`setRendererFunctionJsonHandler`,
 * `respondToAgentJson`, `invokeAgentFunctionJson`) have been PR-6a/6b landed
 * on the Kotlin core, and the DSL-embedded [A2UISurfaceDSLView] registers
 * itself with [A2UIBridgeManager.dslBridgeMap] under its own [A2UISurfaceDSLView.instanceId],
 * so the three APIs just work from any common-main Pager without touching
 * platform-specific host code. The mock agent lives inside this page too —
 * no real WebSocket / SSE connection is required for a demo roundtrip.
 */
@Page("A2UIRpcDemo")
class A2UIRpcDemoPager : Pager() {

    // ------------------------------------------------------------------
    //  Observable UI state
    // ------------------------------------------------------------------

    /** One-line status shown directly under the title. */
    private var statusLine: String by observable("Tap a button below to trigger an RPC.")

    /** Rolling event log printed under the buttons. Newest at the top. */
    private val eventLog by observableList<String>()

    // ------------------------------------------------------------------
    //  Live surface + wire-protocol state
    // ------------------------------------------------------------------

    /** Captured by the A2UISurface init {} lambda; stable after first render. */
    private var surface: A2UISurfaceDSLView? = null

    /** v1.0 surfaceId we use for every envelope on this page. */
    private val surfaceId: String = "rpc-demo-surface"

    // ------------------------------------------------------------------
    //  Pager lifecycle
    // ------------------------------------------------------------------

    override fun created() {
        super.created()
        A2UICustomFunctionRegistry.register("getGreeting", """{"catalogId":"custom_functions_catalog_v1_0.json","returnType":"string","allowedCallers":"rendererOrAgent","args":{"properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false}}""")
        A2UICustomFunctionRegistry.register("fetchUser", """{"catalogId":"custom_functions_catalog_v1_0.json","returnType":"object","allowedCallers":"agentOnly","args":{"properties":{"id":{"type":"number"}},"required":["id"],"additionalProperties":false}}""")
        // The RPC handler is installed on the A2UI
        // surface *after* its didInit completes, because
        // setRendererFunctionJsonHandler looks up the dslBridgeMap by
        // instanceId — which only exists once the DSL view has registered
        // itself. See the A2UISurface init {} block below.
    }

    override fun pageWillDestroy() {
        // Clean up the handler so the demo does not leak the Kuikly context
        // thread capture across hot reloads.
        surface?.instanceId?.let {
            A2UIBridgeManager.setRendererFunctionJsonHandler(it, null)
        }
        super.pageWillDestroy()
    }

    // ------------------------------------------------------------------
    //  UI body
    // ------------------------------------------------------------------

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
                flexDirectionColumn()
                padding(16f)
            }

            // --- Header -----------------------------------------------
            RichText {
                attr {
                    text("A2UI v1.0 RPC Demo")
                    fontSize(18f)
                    color(Color.BLACK)
                    marginBottom(4f)
                }
            }
            RichText {
                attr {
                    text("Status: ${ctx.statusLine}")
                    fontSize(12f)
                    color(Color(0xFF666666L))
                    marginBottom(10f)
                }
            }

            // --- The A2UI v1.0 surface --------------------------------
            A2UISurface {
                attr {
                    protocolConfig(A2UIProtocolVersion.V1_0, strict = true)
                    marginBottom(10f)
                    backgroundColor(Color(0xFFF2F6FBL))
                    borderRadius(6f)
                    padding(8f)
                }
                event {
                    // We only use `action` / `openUrl` for informational
                    // logging — all RPC behaviour is driven below.
                    action { _, _, actionJson ->
                        ctx.log("surface action: $actionJson")
                    }
                    openUrl { url ->
                        ctx.log("surface openUrl: $url")
                    }
                }

                // Capture `this` (= A2UISurfaceDSLView) so later button
                // callbacks can feed envelopes into this surface.
                val self = this
                ctx.surface = self

                // Seed the surface with a v1.0 createSurface envelope +
                // a single Text component so the user sees *something*.
                self.writeStream(INITIAL_CREATE_SURFACE_ENVELOPE)
                // Do NOT finishStream() — we keep the stream open for the
                // lifetime of the page so subsequent writeStream() calls
                // (mock-agent `callRendererFunction` / `agentFunctionResponse`)
                // are parsed as additional messages.

                // Install the inbound-RPC handler now that the surface has
                // been added to the view tree. `didInit` of the DSL view
                // runs synchronously inside `addChild`, which registers the
                // bridge under instanceId in A2UIBridgeManager.dslBridgeMap.
                // It is therefore safe to install the handler right here.
                A2UIBridgeManager.setRendererFunctionJsonHandler(self.instanceId) { requestJson ->
                    ctx.onInboundRendererFunction(requestJson)
                }
            }

            // --- Button row ------------------------------------------
            Column {
                attr {
                    flexDirectionColumn()
                    marginTop(4f)
                }

                Button {
                    attr {
                        height(40f)
                        marginBottom(8f)
                        backgroundColor(Color(0xFF1A73E8L))
                        borderRadius(6f)
                        titleAttr {
                            text("Mock agent → getGreeting(\"Ada\")")
                            color(Color.WHITE)
                            fontSize(14f)
                        }
                    }
                    event {
                        click { ctx.simulateAgentCallRendererFunction() }
                    }
                }

                Button {
                    attr {
                        height(40f)
                        marginBottom(8f)
                        backgroundColor(Color(0xFF34A853L))
                        borderRadius(6f)
                        titleAttr {
                            text("Fetch user from agent (id=42)")
                            color(Color.WHITE)
                            fontSize(14f)
                        }
                    }
                    event {
                        click { ctx.invokeAgentFetchUser() }
                    }
                }

                Button {
                    attr {
                        height(36f)
                        marginBottom(8f)
                        backgroundColor(Color(0xFFBDBDBDL))
                        borderRadius(6f)
                        titleAttr {
                            text("Clear event log")
                            color(Color.WHITE)
                            fontSize(12f)
                        }
                    }
                    event {
                        click { ctx.eventLog.clear() }
                    }
                }
            }

            // --- Event log -------------------------------------------
            RichText {
                attr {
                    text("Event log (newest first)")
                    fontSize(13f)
                    color(Color(0xFF444444L))
                    marginTop(6f)
                    marginBottom(2f)
                }
            }
            Scroller {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFFAFAFAL))
                    borderRadius(6f)
                    padding(6f)
                }
                vfor({ ctx.eventLog }) { line ->
                    RichText {
                        attr {
                            text(line)
                            fontSize(11f)
                            color(Color(0xFF333333L))
                            marginBottom(2f)
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    //  Inbound (agent → renderer) handler
    // ------------------------------------------------------------------

    /**
     * Called by [A2UIBridgeManager] for every `callRendererFunction`
     * arriving on this surface's instance. This library host callback retains
     * the internal requestId/name/args API, not the official wire envelope:
     * ```
     *   { "surfaceId":"...", "requestId":"...", "name":"getGreeting",
     *     "args":{"name":"Ada"} }
     * ```
     */
    private fun onInboundRendererFunction(requestJson: String) {
        log("← agent callRendererFunction: $requestJson")
        val req = try {
            JSONObject(requestJson)
        } catch (t: Throwable) {
            log("! malformed inbound JSON: ${t.message}")
            return
        }
        val reqSurfaceId = req.optString("surfaceId")
        val requestId = req.optString("requestId")
        val name = req.optString("name")
        val args = req.optJSONObject("args")
        val instanceId = surface?.instanceId
        if (instanceId == null) {
            log("! no surface attached; dropping inbound RPC")
            return
        }
        when (name) {
            "getGreeting" -> {
                val recipient = args?.optString("name").orEmpty()
                statusLine = "Inbound getGreeting(name=\"$recipient\") → ok"
                log("→ host responding ok for requestId=$requestId")
                A2UIBridgeManager.respondToAgentJson(
                    instanceId = instanceId,
                    surfaceId = reqSurfaceId,
                    requestId = requestId,
                    resultJson = JSONArray().apply { put("Hello, $recipient!") }
                        .toString().removePrefix("[").removeSuffix("]"),
                )
            }
            else -> {
                log("→ host failing (UNKNOWN_FUNCTION) requestId=$requestId")
                A2UIBridgeManager.failAgentRequestJson(
                    instanceId = instanceId,
                    surfaceId = reqSurfaceId,
                    requestId = requestId,
                    code = "UNKNOWN_FUNCTION",
                    message = "renderer does not know function '$name'",
                    detailsJson = null,
                )
            }
        }
    }

    // ------------------------------------------------------------------
    //  Mock agent: inbound callRendererFunction
    // ------------------------------------------------------------------

    /**
     * Simulates an agent sending a `callRendererFunction("getGreeting", {...})`
     * over the wire. We build the wire envelope by hand and feed it into the
     * DSL surface via [A2UISurfaceDSLView.writeStream], which routes it to
     * the Kotlin core's stream parser exactly like a real WebSocket chunk
     * would.
     */
    private fun simulateAgentCallRendererFunction() {
        val s = surface ?: run {
            log("! surface not ready")
            return
        }
        val requestId = "req-${++mockReqCounter}"
        val envelope = JSONObject().apply {
            put("version", "1.0")
            put("callRendererFunction", JSONObject().apply {
                put("surfaceId", surfaceId)
                put("functionCallId", requestId)
                put("callFunction", JSONObject().apply {
                    put("@call", "getGreeting")
                    put("catalogId", "custom_functions_catalog_v1_0.json")
                    put("args", JSONObject().apply { put("name", "Ada") })
                })
            })
        }.toString()
        statusLine = "Mock agent -> callRendererFunction getGreeting"
        log("agent → $envelope")
        s.writeStream(envelope)
    }

    // ------------------------------------------------------------------
    //  Outbound: renderer → agent
    // ------------------------------------------------------------------

    /**
     * Fires `callAgentFunction("fetchUser", {"id":42})`. The Kotlin core
     * mints a requestId, emits the wire envelope through
     * `SEND_RPC_ENVELOPE`, and stores our `onComplete` lambda in the
     * dispatcher's pending map. Our mock agent (below) intercepts the
     * emitted envelope and injects an `agentFunctionResponse` on the same
     * stream, closing the loop.
     *
     * Because this demo runs purely in Kotlin we do not actually hook the
     * `SEND_RPC_ENVELOPE` native dispatch; instead we simulate the agent's
     * reply with a direct [A2UISurfaceDSLView.writeStream] call *after* we
     * know the dispatcher has stored the pending callback (i.e. as part of
     * the same tick — `invokeAgentFunctionJson` returns synchronously after
     * registration). The response envelope travels back through the core's
     * normal stream-parser path and resolves the pending callback.
     */
    private fun invokeAgentFetchUser() {
        val s = surface ?: run {
            log("! surface not ready")
            return
        }
        val instanceId = s.instanceId
        val args = "{\"id\":42}"
        statusLine = "Host -> invokeAgentFunction fetchUser"
        val reqId = A2UIBridgeManager.invokeAgentFunctionJson(
            instanceId = instanceId,
            surfaceId = surfaceId,
            name = "fetchUser",
            argsJson = args,
            timeoutMs = 5_000L,
        ) { responseJson ->
            // Fires on the dispatcher's thread when the mock agent replies
            // (or on timeout / surface close).
            log("← onComplete: $responseJson")
            val resp = try {
                JSONObject(responseJson)
            } catch (_: Throwable) {
                null
            }
            val result = resp?.optJSONObject("result")
            val error = resp?.optJSONObject("error")
            statusLine = when {
                result != null -> "fetchUser result: $result"
                error != null -> "fetchUser error: ${error.optString("code")}"
                else -> "fetchUser: (empty response)"
            }
        }
        if (reqId == null) {
            log("! invokeAgentFunctionJson returned null (instance unknown or stream not v1.0 yet)")
            statusLine = "invokeAgentFunction failed (null reqId)"
            return
        }
        log("→ host invokeAgentFunction reqId=$reqId args=$args")

        // Mock agent: pretend the backend processed the call and reply on
        // the same stream. In a real integration the response would come
        // in via your agent transport (WebSocket / SSE) and the host would
        // forward it via `s.writeStream(frame)`.
        val response = JSONObject().apply {
            put("version", "1.0")
            put("agentFunctionResponse", JSONObject().apply {
                put("functionCallId", reqId)
                put("value", JSONObject().apply {
                    put("id", 42)
                    put("name", "Alice")
                    put("role", "admin")
                })
            })
        }.toString()
        log("mock-agent → $response")
        s.writeStream(response)
    }

    // ------------------------------------------------------------------
    //  Internal helpers
    // ------------------------------------------------------------------

    private var mockReqCounter: Int = 0

    private fun log(line: String) {
        // Prepend so newest entries are on top in the Scroller.
        eventLog.add(0, line)
        if (eventLog.size > MAX_LOG_LINES) {
            while (eventLog.size > MAX_LOG_LINES) {
                eventLog.removeAt(eventLog.size - 1)
            }
        }
    }

    companion object {
        private const val MAX_LOG_LINES: Int = 200

        /**
         * Minimal v1.0 `createSurface` envelope that paints a single Text
         * component. We keep it tiny because the demo's point is the RPC
         * roundtrip, not the UI tree.
         */
        private const val INITIAL_CREATE_SURFACE_ENVELOPE: String =
            """{"version":"1.0","createSurface":{"surfaceId":"rpc-demo-surface","catalogId":"custom_functions_catalog_v1_0.json"}}
{"version":"1.0","updateComponents":{"surfaceId":"rpc-demo-surface","components":[{"id":"root","component":"Column","children":["hint"]},{"id":"hint","component":"Text","text":"Tap a button below to trigger an RPC."}]}}
"""
    }
}
