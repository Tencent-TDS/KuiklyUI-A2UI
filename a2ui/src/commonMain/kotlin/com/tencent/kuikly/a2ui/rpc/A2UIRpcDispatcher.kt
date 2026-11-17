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

/**
 * In-process coordinator for A2UI v1.0 bidirectional RPC.
 *
 * Responsibilities (see PR-6 design spec §4):
 *
 *  * Track pending requests (both agent→renderer and renderer→agent).
 *  * Mint renderer-originated [A2UIRpcRequest.requestId] values with a
 *    per-dispatcher namespace so multi-adapter / multi-surface setups
 *    never collide.
 *  * Surface timeout expiries via [tick] (host-driven; no background
 *    timer — see spec §8-3).
 *  * Cancel all outstanding pending entries on surface close / adapter
 *    reset.
 *
 * Explicitly NON-responsibilities:
 *
 *  * Execution. Host code does the actual work; dispatcher only tracks
 *    correlations.
 *  * I/O. The adapter serialises/deserialises the wire envelope.
 *  * Thread safety. Dispatcher is intended to be driven from a single
 *    thread — the same thread the adapter runs on, which is the Kuikly
 *    context thread. If a host needs cross-thread delivery, it is
 *    responsible for hopping into that thread before calling in.
 */
class A2UIRpcDispatcher(
    /**
     * Default timeout applied when [A2UIRpcRequest.timeoutMs] is `null`.
     * `0` or negative disables the default (pending entries never time
     * out on their own).
     */
    private val defaultTimeoutMs: Long = 30_000L,
    /**
     * Monotonic clock in milliseconds. Injected so unit tests can drive
     * time deterministically without pulling in `kotlinx.coroutines` or
     * relying on `System.currentTimeMillis()` (which is JVM-only in
     * commonMain).
     */
    private val clock: () -> Long = { 0L },
    /**
     * Opaque per-dispatcher prefix used when minting renderer-originated
     * [A2UIRpcRequest.requestId]s. Stable for the lifetime of this
     * dispatcher. Tests pin it so expected ids are deterministic.
     */
    private val rendererIdPrefix: String = defaultRendererPrefix(),
) {

    /**
     * Snapshot of an in-flight request. Internal; exposed only through
     * [pendingCountFor]/[hasPending] for tests.
     */
    internal class PendingRpc(
        val direction: A2UIRpcRequest.Direction,
        val surfaceId: String,
        val requestId: String,
        val startedAtMs: Long,
        val timeoutMs: Long,
        val onComplete: ((A2UIRpcResponse) -> Unit)?,
    )

    /**
     * Pending table keyed by requestId. The two directions SHARE this
     * map because [A2UIRpcRequest.requestId] is globally unique within
     * a dispatcher (agent-originated ids are opaque strings picked by
     * the agent, renderer-originated ids are minted by us; collisions
     * across the two sides are not possible in practice).
     */
    private val pending = mutableMapOf<String, PendingRpc>()

    /**
     * Monotonically increasing sequence for renderer-originated ids.
     * We intentionally do NOT use `AtomicLong` — the dispatcher is
     * single-threaded by contract; introducing atomics would mislead
     * callers about re-entrancy guarantees.
     */
    private var rendererSeq: Long = 0L

    // ---------------------------------------------------------------
    //  Incoming wire traffic (adapter -> dispatcher)
    // ---------------------------------------------------------------

    /**
     * Called by the adapter when a `callRendererFunction` envelope is
     * decoded. Registers the pending entry and returns the request as-is
     * for the adapter to forward to the host via BridgeManager.
     *
     * If an entry with the same [A2UIRpcRequest.requestId] already
     * exists (duplicate delivery or agent bug) the newer request wins
     * and the old pending is silently replaced — mirrors the "last
     * writer wins" behaviour of most RPC frameworks.
     */
    fun onIncomingAgentCall(req: A2UIRpcRequest): A2UIRpcRequest {
        val effectiveTimeout = (req.timeoutMs ?: defaultTimeoutMs)
            .coerceAtLeast(0L)
        pending[req.requestId] = PendingRpc(
            direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
            surfaceId = req.surfaceId,
            requestId = req.requestId,
            startedAtMs = clock(),
            timeoutMs = effectiveTimeout,
            onComplete = null,
        )
        return req
    }

    /**
     * Called by the adapter when an `agentFunctionResponse` envelope is
     * decoded. Removes the matching pending entry and returns it so the
     * adapter can invoke its `onComplete` callback (or logs + drops the
     * response when no pending entry matches — a late / duplicate).
     */
    internal fun onIncomingAgentResponse(resp: A2UIRpcResponse): PendingRpc? {
        val entry = pending.remove(resp.requestId)
        if (entry == null) {
            println(
                "[A2UI] warn: dropped agentFunctionResponse requestId=" +
                    "'${resp.requestId}' (no pending entry; late or duplicate)."
            )
            return null
        }
        // Defensive: the pending entry must be the RENDERER_TO_AGENT
        // side; receiving an agentFunctionResponse that correlates to
        // an AGENT_TO_RENDERER pending is a wire-format violation.
        if (entry.direction != A2UIRpcRequest.Direction.RENDERER_TO_AGENT) {
            println(
                "[A2UI] warn: agentFunctionResponse requestId=" +
                    "'${resp.requestId}' matched a pending entry in the wrong " +
                    "direction (${entry.direction}); treating as success anyway."
            )
        }
        return entry
    }

    // ---------------------------------------------------------------
    //  Host-originated completions / invocations
    // ---------------------------------------------------------------

    /**
     * Called by the host AFTER it has executed a renderer-side function
     * requested by the agent. Produces the response envelope the
     * adapter will serialise back to the wire. Returns `null` when the
     * request is no longer known (already timed out or cancelled).
     */
    fun completeAgentCall(
        surfaceId: String,
        requestId: String,
        result: Any?,
    ): A2UIRpcResponse? {
        val entry = pending.remove(requestId) ?: run {
            println(
                "[A2UI] warn: completeAgentCall for unknown requestId=" +
                    "'$requestId' (already timed out?); dropping."
            )
            return null
        }
        return A2UIRpcResponse.success(
            direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
            surfaceId = surfaceId,
            requestId = requestId,
            result = result,
        )
    }

    /**
     * Host-driven failure path for a renderer-side function. Same
     * semantics as [completeAgentCall] but emits an error response.
     */
    fun failAgentCall(
        surfaceId: String,
        requestId: String,
        error: A2UIRpcError,
    ): A2UIRpcResponse? {
        val entry = pending.remove(requestId) ?: run {
            println(
                "[A2UI] warn: failAgentCall for unknown requestId=" +
                    "'$requestId' (already timed out?); dropping."
            )
            return null
        }
        return A2UIRpcResponse.failure(
            direction = A2UIRpcRequest.Direction.AGENT_TO_RENDERER,
            surfaceId = surfaceId,
            requestId = requestId,
            error = error,
        )
    }

    /**
     * Host-driven renderer→agent RPC. Mints a fresh [A2UIRpcRequest]
     * and stashes the [onComplete] callback so a later
     * `agentFunctionResponse` can invoke it. Returns the request for
     * the adapter to serialise.
     */
    fun beginRendererCall(
        surfaceId: String,
        name: String,
        args: Map<String, Any?>,
        timeoutMs: Long? = null,
        onComplete: (A2UIRpcResponse) -> Unit,
    ): A2UIRpcRequest {
        val requestId = newRendererRequestId()
        val req = A2UIRpcRequest(
            direction = A2UIRpcRequest.Direction.RENDERER_TO_AGENT,
            surfaceId = surfaceId,
            requestId = requestId,
            name = name,
            args = args,
            timeoutMs = timeoutMs,
        )
        val effectiveTimeout = (timeoutMs ?: defaultTimeoutMs).coerceAtLeast(0L)
        pending[requestId] = PendingRpc(
            direction = A2UIRpcRequest.Direction.RENDERER_TO_AGENT,
            surfaceId = surfaceId,
            requestId = requestId,
            startedAtMs = clock(),
            timeoutMs = effectiveTimeout,
            onComplete = onComplete,
        )
        return req
    }

    // ---------------------------------------------------------------
    //  Housekeeping
    // ---------------------------------------------------------------

    /**
     * Cancel all pending requests. If [surfaceId] is `null`, every
     * entry across all surfaces is cancelled (used by adapter.reset()).
     * Otherwise only entries for that surface are cancelled (used when
     * a single surface is destroyed while others remain open).
     *
     * Renderer-originated pendings' `onComplete` is invoked with
     * [reason]; agent-originated pendings are silently dropped (the
     * agent is not notified of cancellation — mirrors Google's spec).
     */
    fun cancelAll(
        surfaceId: String?,
        reason: A2UIRpcError = A2UIRpcError.surfaceClosed(),
    ) {
        if (pending.isEmpty()) return
        val toCancel = if (surfaceId == null) {
            pending.values.toList().also { pending.clear() }
        } else {
            val matching = pending.values.filter { it.surfaceId == surfaceId }
            matching.forEach { pending.remove(it.requestId) }
            matching
        }
        for (entry in toCancel) {
            if (entry.direction == A2UIRpcRequest.Direction.RENDERER_TO_AGENT) {
                val resp = A2UIRpcResponse.failure(
                    direction = entry.direction,
                    surfaceId = entry.surfaceId,
                    requestId = entry.requestId,
                    error = reason,
                )
                safeInvoke(entry.onComplete, resp)
            }
        }
    }

    /**
     * Drive timeout expiry. Call this from the adapter after every
     * batch of wire-in/out activity (and optionally from a host-owned
     * timer). Expired entries are removed from the pending table and,
     * for renderer-originated pendings, their `onComplete` callback is
     * invoked with [A2UIRpcError.Codes.TIMEOUT].
     *
     * Returns the list of request ids that just expired, for logging
     * / telemetry convenience.
     */
    fun tick(nowMs: Long = clock()): List<String> {
        if (pending.isEmpty()) return emptyList()
        val expired = mutableListOf<PendingRpc>()
        val it = pending.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next().value
            val deadline = entry.timeoutMs
            if (deadline <= 0L) continue
            if (nowMs - entry.startedAtMs >= deadline) {
                expired += entry
                it.remove()
            }
        }
        for (entry in expired) {
            if (entry.direction == A2UIRpcRequest.Direction.RENDERER_TO_AGENT) {
                val resp = A2UIRpcResponse.failure(
                    direction = entry.direction,
                    surfaceId = entry.surfaceId,
                    requestId = entry.requestId,
                    error = A2UIRpcError.timeout(entry.timeoutMs),
                )
                safeInvoke(entry.onComplete, resp)
            }
        }
        return expired.map { it.requestId }
    }

    // ---------------------------------------------------------------
    //  Test / introspection helpers
    // ---------------------------------------------------------------

    /** Number of pending entries for [surfaceId], or total when `null`. */
    fun pendingCountFor(surfaceId: String? = null): Int =
        if (surfaceId == null) pending.size
        else pending.values.count { it.surfaceId == surfaceId }

    /** Whether a pending entry exists for the given [requestId]. */
    fun hasPending(requestId: String): Boolean = pending.containsKey(requestId)

    /**
     * Mint a new renderer-originated requestId.
     *
     * Format: `"renderer-<prefix>-<seq>"` where `<prefix>` is stable
     * per-dispatcher and `<seq>` is monotonic. Enough for in-process
     * uniqueness across any sane number of concurrent dispatchers.
     */
    private fun newRendererRequestId(): String {
        rendererSeq += 1
        return "renderer-$rendererIdPrefix-$rendererSeq"
    }

    private fun safeInvoke(
        cb: ((A2UIRpcResponse) -> Unit)?,
        resp: A2UIRpcResponse,
    ) {
        if (cb == null) return
        try {
            cb(resp)
        } catch (t: Throwable) {
            println(
                "[A2UI] error: RPC onComplete callback threw for requestId=" +
                    "'${resp.requestId}': ${t.message}"
            )
        }
    }

    companion object {
        /**
         * Generate a short random-ish prefix for renderer-side request
         * ids. We intentionally avoid pulling in a cryptographic RNG
         * from commonMain; the prefix just needs to be unique across
         * dispatchers in the same process, which a timestamp-derived
         * value handles fine. Tests inject their own prefix instead of
         * relying on this.
         */
        private fun defaultRendererPrefix(): String {
            // Use the lower 32 bits of a nanosecond-ish value. We can't
            // call `System.nanoTime()` in commonMain, but
            // `kotlin.random.Random` is available and already seeded
            // off the platform clock on every target.
            val raw = kotlin.random.Random.nextInt().toLong() and 0xFFFFFFFFL
            // Base36 keeps the string short and URL-safe without any
            // platform-specific conversion helpers.
            return raw.toString(radix = 36).padStart(6, '0')
        }
    }
}
