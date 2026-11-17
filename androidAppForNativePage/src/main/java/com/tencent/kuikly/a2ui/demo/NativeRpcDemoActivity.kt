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

package com.tencent.kuikly.a2ui.demo

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import org.json.JSONObject

/**
 * A2UI v1.0 **RPC round-trip demo**, Native-page edition.
 *
 * Counterpart to `A2UIRpcDemoPager` (Kuikly-embedded mode). The core
 * difference is that this activity hosts A2UI through a plain
 * [A2UISurfaceView] inside an AppCompatActivity view tree — i.e. it
 * exercises the **`nativeBridgeMap` → `SEND_RPC_ENVELOPE (methodId=108)`**
 * outbound path added in PR-6d, not the Kuikly-embedded
 * `A2UIBridgeModule.rpcEnvelope` module path.
 *
 * ### What this proves
 *
 *  1. **Outbound**: calling
 *     [A2UIBridgeManager.invokeAgentFunctionJson] with the surface view's
 *     [A2UISurfaceView.instanceId] mints a `callAgentFunction` wire
 *     envelope and drives it through the Kotlin core's `emitRpcWire(…)` →
 *     `A2UISurfaceView.callNative(SEND_RPC_ENVELOPE, …)` path, which hands
 *     it to [A2UISurfaceView.rpcEnvelopeOutboundHandler] (set in
 *     [onCreate]). The handler just logs it + synthesises the matching
 *     `agentFunctionResponse` envelope and feeds it back via
 *     [A2UISurfaceView.writeStream] — the Kotlin dispatcher resolves our
 *     `onComplete` lambda, closing the loop.
 *
 *  2. **Inbound**: tapping the mock-agent button synthesises a
 *     `callRendererFunction("getGreeting", {"path":"Ada"})` envelope and
 *     feeds it into [A2UISurfaceView.writeStream]; the Kotlin core splits
 *     it off from the stream, routes it to the handler we installed via
 *     [A2UIBridgeManager.setRendererFunctionJsonHandler], and we respond
 *     with [A2UIBridgeManager.respondToAgentJson]. That response travels
 *     outbound through the same `SEND_RPC_ENVELOPE` path captured above,
 *     so you'll see **two** outbound log lines per inbound call: the
 *     handler's response + any follow-up ack.
 *
 * ### Why there's no real agent transport
 *
 * The demo deliberately short-circuits the "real" network by echoing
 * every outbound envelope into a local mock agent (see
 * [onOutboundEnvelope]). A real integration would do
 * `webSocket.send(envelopeJson)` here and feed inbound frames back to
 * [A2UISurfaceView.writeStream] from the socket's `onMessage` callback.
 */
class NativeRpcDemoActivity : AppCompatActivity() {

    // ---------------------------------------------------------------
    //  UI handles
    // ---------------------------------------------------------------

    private lateinit var surfaceView: A2UISurfaceView
    private lateinit var statusText: TextView
    private lateinit var logText: TextView
    private lateinit var scrollView: ScrollView
    private val mainHandler = Handler(Looper.getMainLooper())

    /** Stable surfaceId used in every envelope on this page. */
    private val surfaceId: String = "rpc-demo-native"

    /** Monotonic counter for the mock agent's inbound requestIds. */
    private var mockReqCounter: Int = 0

    /** Rolling log — newest at top, capped at [MAX_LOG_LINES]. */
    private val eventLog = ArrayDeque<String>()

    // ---------------------------------------------------------------
    //  Activity lifecycle
    // ---------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildUi())

        // 1) Wire the outbound v1.0 RPC envelope sink BEFORE init(),
        //    otherwise the handler would miss any envelope produced
        //    between `init()` and our subscription.
        surfaceView.rpcEnvelopeOutboundHandler = { envelopeJson ->
            // The sink is called on the Kuikly Context Thread; hop back
            // to main for logging + mock-agent simulation.
            mainHandler.post { onOutboundEnvelope(envelopeJson) }
        }

        // 2) Boot the surface and install the inbound RPC handler. The
        //    JSON handler stays alive for the whole activity.
        surfaceView.init("{\"protocolVersion\":\"1.0\",\"strictV10\":true}")
        A2UIBridgeManager.setRendererFunctionJsonHandler(surfaceView.instanceId) { requestJson ->
            // Same thread note as above.
            mainHandler.post { onInboundRendererFunction(requestJson) }
        }

        // 3) Seed the surface with a tiny createSurface envelope so the
        //    user sees *something* while using the demo. Must follow
        //    init() so the writeStream dispatch has a valid coreEntry.
        surfaceView.writeStream(INITIAL_CREATE_SURFACE_ENVELOPE)
        // Intentionally no finishStream() — the stream stays open so
        // subsequent writeStream() calls deliver more envelopes.
    }

    override fun onDestroy() {
        // Detach the handler so the Kotlin core does not try to call
        // into a torn-down activity. Harmless if the surface is already
        // destroyed (the bridge map lookup will be a no-op).
        A2UIBridgeManager.setRendererFunctionJsonHandler(surfaceView.instanceId, null)
        super.onDestroy()
    }

    // ---------------------------------------------------------------
    //  Inbound: mock agent → renderer
    // ---------------------------------------------------------------

    private fun simulateAgentCallRendererFunction() {
        val requestId = "req-${++mockReqCounter}"
        val envelope = JSONObject().apply {
            put("version", "1.0")
            put(
                "callRendererFunction",
                JSONObject().apply {
                    put("surfaceId", surfaceId)
                    put("functionCallId", requestId)
                    put("callFunction", JSONObject().apply {
                        put("@call", "getGreeting")
                        put("catalogId", "custom_functions_catalog_v1_0.json")
                        put("args", JSONObject().apply { put("name", "Ada") })
                    })
                }
            )
        }.toString()
        setStatus("Mock agent -> callRendererFunction getGreeting")
        log("agent → $envelope")
        surfaceView.writeStream(envelope)
    }

    /**
     * Called by [A2UIBridgeManager] for every `callRendererFunction`
     * arriving on this surface's instance. Host callbacks retain the internal
     * requestId/name/args representation; the stream uses official 1.0 fields.
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
        val instanceId = surfaceView.instanceId
        when (name) {
            "getGreeting" -> {
                val recipient = args?.optString("name").orEmpty()
                setStatus("Inbound getGreeting(name=\"$recipient\") → ok")
                log("→ host responding ok for requestId=$requestId")
                A2UIBridgeManager.respondToAgentJson(
                    instanceId = instanceId,
                    surfaceId = reqSurfaceId,
                    requestId = requestId,
                    resultJson = JSONObject.quote("Hello, $recipient!"),
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

    // ---------------------------------------------------------------
    //  Outbound: renderer → agent
    // ---------------------------------------------------------------

    private fun invokeAgentFetchUser() {
        val instanceId = surfaceView.instanceId
        val argsJson = "{\"id\":42}"
        setStatus("Host -> invokeAgentFunction fetchUser")
        val reqId = A2UIBridgeManager.invokeAgentFunctionJson(
            instanceId = instanceId,
            surfaceId = surfaceId,
            name = "fetchUser",
            argsJson = argsJson,
            timeoutMs = 5_000L,
        ) { responseJson ->
            // Fires on the dispatcher's thread (Kuikly Context Thread).
            // Hop to main for UI work.
            mainHandler.post { onAgentFunctionResponse(responseJson) }
        }
        if (reqId == null) {
            log("! invokeAgentFunctionJson returned null (instance unknown or stream not v1.0 yet)")
            setStatus("invokeAgentFunction failed (null reqId)")
            return
        }
        log("→ host invokeAgentFunction reqId=$reqId args=$argsJson")
        // Note: the outbound envelope itself is emitted by Kotlin core
        // via SEND_RPC_ENVELOPE (methodId=108), which lands in
        // onOutboundEnvelope() below. That function also contains the
        // mock-agent that synthesises the matching agentFunctionResponse
        // and feeds it back through writeStream(). This closes the loop
        // without needing a real network transport.
    }

    private fun onAgentFunctionResponse(responseJson: String) {
        log("← onComplete: $responseJson")
        val resp = try {
            JSONObject(responseJson)
        } catch (_: Throwable) {
            null
        }
        val result = resp?.optJSONObject("result")
        val error = resp?.optJSONObject("error")
        val status = when {
            result != null -> "fetchUser result: $result"
            error != null -> "fetchUser error: ${error.optString("code")}"
            else -> "fetchUser: (empty response)"
        }
        setStatus(status)
    }

    // ---------------------------------------------------------------
    //  SEND_RPC_ENVELOPE outbound handler + mock agent
    // ---------------------------------------------------------------

    /**
     * Called when the Kotlin core hands us an outbound v1.0 RPC envelope
     * via `A2UISurfaceView.rpcEnvelopeOutboundHandler` — the demo's
     * stand-in for "ship this to the agent over the wire".
     *
     * Two envelope shapes arrive here:
     *  - `{"version":"1.0","callAgentFunction":{…}}` — emitted by
     *    [invokeAgentFetchUser]. We mock-reply with
     *    `agentFunctionResponse` on the same stream.
     *  - `{"version":"1.0","rendererFunctionResponse":{…}}` — emitted by
     *    our own inbound handler's [A2UIBridgeManager.respondToAgentJson]
     *    call. In a real integration this would be forwarded to the
     *    agent transport; here we just log it.
     */
    private fun onOutboundEnvelope(envelopeJson: String) {
        log("agent-wire ← $envelopeJson")
        val env = try {
            JSONObject(envelopeJson)
        } catch (_: Throwable) {
            return
        }
        val call = env.optJSONObject("callAgentFunction") ?: return
        // Mock backend: pretend we processed fetchUser(id=42) and reply.
        val requestId = call.optString("functionCallId")
        val response = JSONObject().apply {
            put("version", "1.0")
            put(
                "agentFunctionResponse",
                JSONObject().apply {
                    put("functionCallId", requestId)
                    put(
                        "value",
                        JSONObject().apply {
                            put("id", 42)
                            put("name", "Alice")
                            put("role", "admin")
                        }
                    )
                }
            )
        }.toString()
        log("mock-agent → $response")
        surfaceView.writeStream(response)
    }

    // ---------------------------------------------------------------
    //  UI construction
    // ---------------------------------------------------------------

    private fun buildUi(): ViewGroup {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        root.addView(TextView(this).apply {
            text = "A2UI v1.0 RPC Demo (Native Page)"
            setTextColor(Color.BLACK)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
        })

        statusText = TextView(this).apply {
            text = "Status: Tap a button below to trigger an RPC."
            setTextColor(Color.parseColor("#666666"))
            textSize = 12f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = dp(4)
            lp.bottomMargin = dp(10)
            layoutParams = lp
        }
        root.addView(statusText)

        surfaceView = A2UISurfaceView(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#F2F6FB"))
                cornerRadius = dp(6).toFloat()
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = dp(10)
            layoutParams = lp
        }
        root.addView(surfaceView)

        root.addView(buildButton("Mock agent → getGreeting(\"Ada\")", "#1A73E8") {
            simulateAgentCallRendererFunction()
        })
        root.addView(buildButton("Fetch user from agent (id=42)", "#34A853") {
            invokeAgentFetchUser()
        })
        root.addView(buildButton("Clear event log", "#BDBDBD") {
            eventLog.clear()
            renderLog()
        })

        root.addView(TextView(this).apply {
            text = "Event log (newest first)"
            setTextColor(Color.parseColor("#444444"))
            textSize = 13f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = dp(6)
            lp.bottomMargin = dp(2)
            layoutParams = lp
        })

        scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#FAFAFA"))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutParams = lp
            setPadding(dp(6), dp(6), dp(6), dp(6))
        }
        logText = TextView(this).apply {
            setTextColor(Color.parseColor("#333333"))
            textSize = 11f
        }
        scrollView.addView(logText)
        root.addView(scrollView)

        return root
    }

    private fun buildButton(title: String, hex: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = title
            setTextColor(Color.WHITE)
            isAllCaps = false
            background = GradientDrawable().apply {
                setColor(Color.parseColor(hex))
                cornerRadius = dp(6).toFloat()
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(42)
            )
            lp.bottomMargin = dp(8)
            layoutParams = lp
            gravity = Gravity.CENTER
            setOnClickListener { onClick() }
        }
    }

    // ---------------------------------------------------------------
    //  Log helpers
    // ---------------------------------------------------------------

    private fun log(line: String) {
        eventLog.addFirst(line)
        while (eventLog.size > MAX_LOG_LINES) {
            eventLog.removeLast()
        }
        renderLog()
    }

    private fun renderLog() {
        logText.text = eventLog.joinToString(separator = "\n")
    }

    private fun setStatus(line: String) {
        statusText.text = "Status: $line"
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val MAX_LOG_LINES: Int = 200

        /**
         * Minimal v1.0 `createSurface` envelope that paints a single Text
         * component. We keep it tiny because the demo's point is the RPC
         * roundtrip, not the UI tree.
         */
        private const val INITIAL_CREATE_SURFACE_ENVELOPE: String =
            """{"version":"1.0","createSurface":{"surfaceId":"rpc-demo-native","catalogId":"custom_functions_catalog_v1_0.json"}}
{"version":"1.0","updateComponents":{"surfaceId":"rpc-demo-native","components":[{"id":"root","component":"Column","children":["hint"]},{"id":"hint","component":"Text","text":"Tap a button below to trigger an RPC."}]}}
"""
    }
}
