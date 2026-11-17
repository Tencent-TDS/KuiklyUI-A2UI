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

import com.tencent.kuikly.a2ui.protocol.A2UIProtocolDefaults
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.protocol.ProtocolKeys

internal enum class A2UICommandKind { CREATE, COMPONENTS, DATA, CALL_RENDERER, RENDERER_RESPONSE, CALL_AGENT, AGENT_RESPONSE }
internal data class A2UICommand(val kind: A2UICommandKind, val payload: Map<String, Any?>?)
internal data class A2UIDecodedEnvelope(val commands: List<A2UICommand>)

internal class A2UIProtocolDecoder(private val getSurfaceId: () -> String) {
    private var _protocolVersion = A2UIProtocolDefaults.DEFAULT_VERSION
    val protocolVersion get() = _protocolVersion
    var strictV10 = A2UIProtocolDefaults.STRICT_V10_BY_DEFAULT
    private var expectedProtocolVersion: A2UIProtocolVersion? = null
    private val currentSurfaceId get() = getSurfaceId()
    fun setExpectedProtocolVersion(version: A2UIProtocolVersion?) { expectedProtocolVersion = version }
    fun reset() { _protocolVersion = A2UIProtocolDefaults.DEFAULT_VERSION; expectedProtocolVersion = null; reportedBareKeys.clear() }
    internal fun readPathKey(map: Map<*, *>): String? = readMixinKey(map, "path")

    internal fun readCallKey(map: Map<*, *>): String? = readMixinKey(map, "call")

    internal fun readAccessibilityKey(map: Map<*, *>): Map<String, Any?>? {
        @Suppress("UNCHECKED_CAST")
        return readMixinKeyRaw(map, "accessibility") as? Map<String, Any?>
    }

    internal fun readChecksKey(map: Map<*, *>): List<*>? =
        readMixinKeyRaw(map, "checks") as? List<*>

    internal fun readMixinKey(map: Map<*, *>, bareKey: String): String? =
        readMixinKeyRaw(map, bareKey) as? String

    /**
     * Core mixin-key lookup. Returns the value stored under either the
     * `@`-prefixed key (v1.0, preferred) or the bare key (v0.9 compat),
     * applying strict-mode diagnostics along the way.
     */
    internal fun readMixinKeyRaw(map: Map<*, *>, bareKey: String): Any? {
        return when (_protocolVersion) {
            A2UIProtocolVersion.V0_9, A2UIProtocolVersion.V0_9_1 ->
                map[bareKey]
            A2UIProtocolVersion.V1_0 -> {
                val atKey = "@$bareKey"
                val atValue = map[atKey]
                if (atValue != null) {
                    atValue
                } else if (map[bareKey] != null) {
                    // Legacy bare key present in a v1.0 payload: honour it
                    // (lenient) but surface the violation to the host.
                    reportBareKeyUnderV10(bareKey)
                    map[bareKey]
                } else {
                    null
                }
            }
        }
    }

    /**
     * One-shot diagnostic emitter for v1.0 payloads that still carry a
     * deprecated bare mixin key. Collapsed into a Set so a single bad
     * payload does not spam the log 1000 times.
     */
    private val reportedBareKeys = mutableSetOf<String>()

    internal fun reportBareKeyUnderV10(bareKey: String) {
        if (!reportedBareKeys.add(bareKey)) return
        if (strictV10) {
            println(
                "[A2UI] error: v1.0 payload uses deprecated bare mixin key " +
                    "'$bareKey'; expected '@$bareKey'. Accepting under lenient " +
                    "fallback — fix your producer."
            )
        } else {
            println(
                "[A2UI] warn: v1.0 payload uses deprecated bare mixin key " +
                    "'$bareKey'; expected '@$bareKey'."
            )
        }
    }

    /**
     * skipKeys set used by the adapter's "copy-all-other-props" style
     * loops (there are 5 such sites in this file). Must include the
     * version-appropriate mixin keys so they are NOT propagated to the
     * output map as plain props.
     *
     * Under v1.0 both the `@`-prefixed and the bare form are skipped,
     * because lenient mode may still accept bare form from legacy
     * producers (see [readMixinKeyRaw]).
     */
    internal fun bindingSkipKeys(extra: Set<String> = emptySet()): Set<String> {
        val base = setOf("id", "component", "children", "child", "action", "tabs")
        return when (_protocolVersion) {
            A2UIProtocolVersion.V0_9, A2UIProtocolVersion.V0_9_1 ->
                base + setOf("accessibility", "checks") + extra
            A2UIProtocolVersion.V1_0 ->
                base + setOf(
                    "accessibility", "@accessibility",
                    "checks", "@checks",
                    // PR-8: `@catalogId` (and its lenient bare form) is a
                    // protocol-level routing hint, not a render prop.
                    // Strip it before the compMap is iterated into
                    // Kuikly `attr{}` calls so it never reaches
                    // native setProp as an unknown CSS attribute.
                    "catalogId", "@catalogId"
                ) + extra
        }
    }

    /**
     * Apply the v1.0 `@@xxx` → `@xxx` key unescape rule to every nested
     * [Map] / [List] inside [value].
     *
     * v0.9 passes [value] through verbatim (returned by identity-equality
     * when no transformation is needed).
     *
     * Semantics:
     *   * On a [Map], each key is routed through [ProtocolKeys.unescapeAtKey]
     *     so that `"@@path"` is written to the Kuikly data model as the
     *     literal string `"@path"`.
     *   * Values are recursed into so nested dictionaries / arrays in
     *     user-authored JSON all get unescaped.
     *   * Non-collection values (primitives, strings) are returned as-is.
     *   * The input is NEVER mutated — a fresh Map/List is allocated
     *     when any key needs rewriting; otherwise the original is
     *     returned by reference.
     *
     * Only called from the v1.0 path (see [handleUpdateDataModel]); callers
     * on the v0.9 path should bypass this helper entirely.
     */
    internal fun unescapeAtKeysDeep(value: Any?): Any? {
        if (_protocolVersion != A2UIProtocolVersion.V1_0) return value
        return when (value) {
            is Map<*, *> -> {
                var changed = false
                val out = LinkedHashMap<String, Any?>(value.size)
                for ((k, v) in value) {
                    val keyStr = k as? String ?: continue
                    val newKey = ProtocolKeys.unescapeAtKey(keyStr)
                    val newVal = unescapeAtKeysDeep(v)
                    if (newKey !== keyStr || newVal !== v) changed = true
                    out[newKey] = newVal
                }
                if (changed) out else {
                    @Suppress("UNCHECKED_CAST")
                    value as Map<String, Any?>
                }
            }
            is List<*> -> {
                var changed = false
                val out = ArrayList<Any?>(value.size)
                for (item in value) {
                    val v2 = unescapeAtKeysDeep(item)
                    if (v2 !== item) changed = true
                    out.add(v2)
                }
                if (changed) out else value
            }
            else -> value
        }
    }
    fun decode(rawMessage: Map<String, Any?>): A2UIDecodedEnvelope? {
        // ---- Version negotiation ------------------------------------
        // Only trust an explicit "version" field; a missing field inherits
        // the version already pinned for this stream (defaulting to v0.9
        // on the very first envelope via [A2UIProtocolDefaults.DEFAULT_VERSION]).
        val declared = (rawMessage["version"] as? String)?.takeIf { it.isNotBlank() }
        if (declared != null) {
            val detected = A2UIProtocolVersion.detectVersion(rawMessage)
            // Host-declared expectation: under v1.0 strict mode, a host that
            // declared "I only accept V1_0" must see the stream also declare
            // v1.0; anything else is rejected before any A2UI message is
            // emitted. Lenient mode treats this as a warning.
            val expected = expectedProtocolVersion
            if (expected != null && expected != detected) {
                if (strictV10 && expected == A2UIProtocolVersion.V1_0) {
                    println(
                        "[A2UI] error: stream declared version=" +
                            "${detected.wireValue} but host expected " +
                            "${expected.wireValue} under v1.0 strict mode; " +
                            "rejecting envelope"
                    )
                    return null
                } else {
                    println(
                        "[A2UI] warn: stream declared version=" +
                            "${detected.wireValue} but host expected " +
                            "${expected.wireValue} (lenient mode: accepting)"
                    )
                }
            }
            if (_protocolVersion != detected && currentSurfaceId.isNotEmpty()) {
                // Mid-stream version switch is a protocol violation under
                // v1.0 strict mode; under lenient mode we still honour the
                // first version seen.
                if (strictV10 && (detected == A2UIProtocolVersion.V1_0 ||
                        _protocolVersion == A2UIProtocolVersion.V1_0)) {
                    println(
                        "[A2UI] error: mid-stream version switch " +
                            "${_protocolVersion.wireValue} -> ${detected.wireValue} " +
                            "rejected under v1.0 strict mode"
                    )
                    return null
                }
            } else {
                _protocolVersion = detected
            }
        }

        // ---- Version-specific dispatch ------------------------------
        val out = when (_protocolVersion) {
            A2UIProtocolVersion.V1_0 -> convertMessageV10(rawMessage)
            A2UIProtocolVersion.V0_9, A2UIProtocolVersion.V0_9_1 ->
                convertMessageV09(rawMessage)
        }
        return A2UIDecodedEnvelope(out)
    }

    private fun convertMessageV09(raw: Map<String, Any?>): List<A2UICommand> {
        val commands = mutableListOf<A2UICommand>()
        for ((key, kind) in listOf(
            "createSurface" to A2UICommandKind.CREATE,
            "updateComponents" to A2UICommandKind.COMPONENTS,
            "updateDataModel" to A2UICommandKind.DATA,
        )) {
            val payload = raw[key]
            if (payload is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                commands += A2UICommand(kind, payload as Map<String, Any?>)
            }
        }
        return commands
    }

    private fun convertMessageV10(raw: Map<String, Any?>): List<A2UICommand> {
        if (strictV10) {
            val offendingKey = findUnknownAtKey(raw)
            if (offendingKey != null) {
                println("[A2UI] error: v1.0 strict: envelope contains unknown " +
                    "reserved key '$offendingKey'. Reject envelope. " +
                    "(Use '@@${offendingKey.removePrefix("@")}' if you " +
                    "meant a literal '$offendingKey' map key.)")
                return emptyList()
            }
            val create = raw["createSurface"] as? Map<*, *>
            if (create != null) {
                if (create.containsKey("theme")) {
                    println("[A2UI] error: v1.0 strict: createSurface.theme was " +
                        "removed in v1.0. Reject envelope.")
                    return emptyList()
                }
                val components = create["components"]
                if (components != null && components !is List<*>) {
                    println("[A2UI] error: v1.0 strict: createSurface.components " +
                        "must be a List; got ${components::class.simpleName}. Reject envelope.")
                    return emptyList()
                }
                val data = create["dataModel"]
                if (data != null && data !is Map<*, *>) {
                    println("[A2UI] error: v1.0 strict: createSurface.dataModel " +
                        "must be a Map at the root; got ${data::class.simpleName}. Reject envelope.")
                    return emptyList()
                }
            }
        }
        val commands = mutableListOf<A2UICommand>()
        for ((key, kind) in listOf(
            "callRendererFunction" to A2UICommandKind.CALL_RENDERER,
            "rendererFunctionResponse" to A2UICommandKind.RENDERER_RESPONSE,
            "callAgentFunction" to A2UICommandKind.CALL_AGENT,
            "agentFunctionResponse" to A2UICommandKind.AGENT_RESPONSE,
        )) {
            raw[key]?.let {
                @Suppress("UNCHECKED_CAST")
                commands += A2UICommand(kind, it as? Map<String, Any?>)
            }
        }
        commands.addAll(convertMessageV09(raw))
        return commands
    }

    private fun findUnknownAtKey(root: Map<String, Any?>): String? {
        val stack = ArrayDeque<Any?>()
        stack.addLast(root)
        while (stack.isNotEmpty()) {
            when (val node = stack.removeLast()) {
                is Map<*, *> -> for ((key, value) in node) {
                    if (key is String && ProtocolKeys.isUnknownAtKey(key)) return key
                    if (value != null) stack.addLast(value)
                }
                is List<*> -> node.forEach { if (it != null) stack.addLast(it) }
                else -> Unit
            }
        }
        return null
    }
}
