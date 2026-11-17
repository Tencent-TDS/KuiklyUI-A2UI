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
 * A2UI v1.0 bidirectional-RPC request envelope.
 *
 * Models BOTH directions of the RPC channel introduced by the v1.0
 * protocol:
 *
 *   * [Direction.AGENT_TO_RENDERER] — carries the payload of an
 *     incoming `callRendererFunction` wire message; the renderer is
 *     expected to execute [name] on its side and emit a
 *     `rendererFunctionResponse` referring back to [requestId].
 *   * [Direction.RENDERER_TO_AGENT] — the renderer-originated version
 *     (`callAgentFunction` on the wire). [requestId] is minted locally
 *     by [A2UIRpcDispatcher.beginRendererCall] so renderer-side and
 *     agent-side ids never collide.
 *
 * The data class is intentionally transport-agnostic: platform bridges
 * (Android / iOS / HarmonyOS) receive and emit this value as-is, with
 * the surrounding wire framing produced by the adapter layer.
 *
 * All fields are protocol-level — see `evolution_guide.md`
 * §"Bidirectional function calls" and `a2ui_protocol.md` §RPC for the
 * canonical semantics.
 */
data class A2UIRpcRequest(
    /** Which side originated the call. */
    val direction: Direction,
    /** Surface this RPC is scoped to. Must match the adapter's current surface. */
    val surfaceId: String,
    /**
     * Opaque correlation id. Guaranteed unique within a process for the
     * lifetime of the enclosing [A2UIRpcDispatcher] on the originating
     * side; the receiving side merely echoes it back on the response
     * envelope.
     */
    val requestId: String,
    /** Function identifier. Resolution is the receiving side's responsibility. */
    val name: String,
    /**
     * Function arguments. An empty map is allowed and means "no args";
     * `null` arg VALUES inside the map are preserved.
     */
    val args: Map<String, Any?>,
    /**
     * Caller-specified timeout in milliseconds. `null` means "use the
     * dispatcher default" (see `A2UIRpcDispatcher.defaultTimeoutMs`).
     *
     * Semantics: for [Direction.AGENT_TO_RENDERER], this is the agent's
     * declared tolerance for waiting on the renderer; the renderer tracks
     * it so it can drop stale pending entries. For
     * [Direction.RENDERER_TO_AGENT], it bounds the host's wait on its
     * `onComplete` callback.
     */
    val timeoutMs: Long?,
) {
    /**
     * Which side initiated the RPC. Encoded onto the wire by the choice
     * of envelope key (`callRendererFunction` vs `callAgentFunction`);
     * tracked here so routing code can branch without parsing JSON again.
     */
    enum class Direction {
        /** `callRendererFunction` — agent asks renderer to run a function. */
        AGENT_TO_RENDERER,

        /** `callAgentFunction` — renderer asks agent to run a function. */
        RENDERER_TO_AGENT,
    }
}
