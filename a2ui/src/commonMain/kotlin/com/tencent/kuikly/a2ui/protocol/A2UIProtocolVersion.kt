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

package com.tencent.kuikly.a2ui.protocol

/**
 * Supported A2UI wire protocol versions.
 *
 * The version of a given message stream is determined by the top-level
 * `"version"` field of the first envelope in that stream. Streams that do not
 * carry an explicit `"version"` field fall back to [DEFAULT_VERSION] for
 * backwards compatibility with legacy agents that pre-date v0.9.1.
 *
 * - [V0_9]   : Initial A2UI release used by the Kuikly A2UI stack today.
 * - [V0_9_1] : Point release of v0.9 (treated identically to v0.9 by the
 *              adapter; declared as a distinct value so future divergence
 *              can be expressed without another enum migration).
 * - [V1_0]   : First GA release of the A2UI protocol. Introduces the `@`
 *              prefixed mixin keys (`@path`, `@call`, `@accessibility`,
 *              `@checks`, `@catalogId`, `@metadata`, `@index`), bidirectional
 *              RPC messages (`callRendererFunction`, `rendererFunctionResponse`,
 *              `callAgentFunction`, `agentFunctionResponse`), inline components
 *              and data model on `createSurface`, `ValidationResult`-style
 *              check results, and several smaller additions. See
 *              `docs/A2UI_V10_MIGRATION.md` for the full migration guide.
 */
enum class A2UIProtocolVersion(val wireValue: String) {
    V0_9("v0.9"),
    V0_9_1("v0.9.1"),
    V1_0("1.0");

    /**
     * Whether this version uses the v1.0 `@`-prefixed mixin key convention
     * (`@path`, `@call`, `@accessibility`, `@checks`, ...).
     */
    val usesAtPrefixedKeys: Boolean
        get() = this == V1_0

    companion object {
        /**
         * Default protocol version assumed when an incoming envelope does not
         * carry an explicit `"version"` field. This matches the behaviour of
         * the Kuikly A2UI stack prior to multi-version support.
         */
        val DEFAULT_VERSION: A2UIProtocolVersion = V0_9

        /**
         * Inspect the top-level `"version"` field of a raw envelope [map] and
         * resolve it to one of the known [A2UIProtocolVersion] values.
         *
         * Accepted inputs (case-insensitive, leading/trailing whitespace is
         * trimmed):
         * - `"1.0"`, `"v1.0"`, `"v1"`               -> [V1_0]
         * - `"v0.9.1"`, `"0.9.1"`                   -> [V0_9_1]
         * - `"v0.9"`, `"0.9"`, missing, blank, other -> [V0_9] (default)
         *
         * This function intentionally never throws: the strict/lenient policy
         * for unknown versions is applied at the adapter layer, not here.
         */
        fun detectVersion(map: Map<String, Any?>?): A2UIProtocolVersion {
            if (map == null) return DEFAULT_VERSION
            val raw = (map["version"] as? String)?.trim()?.lowercase()
                ?: return DEFAULT_VERSION
            if (raw.isEmpty()) return DEFAULT_VERSION
            val normalized = if (raw.startsWith("v")) raw.substring(1) else raw
            return when (normalized) {
                "1", "1.0", "1.0.0" -> V1_0
                "0.9.1" -> V0_9_1
                "0.9", "0.9.0" -> V0_9
                else -> DEFAULT_VERSION
            }
        }

        /**
         * Resolve a raw string into an [A2UIProtocolVersion]. Returns
         * [DEFAULT_VERSION] when the input is null or unrecognised. Used by
         * configuration paths (e.g. `A2UIBridgeManager.initInstance` reading
         * `configJson.protocolVersion`).
         */
        fun fromWireString(value: String?): A2UIProtocolVersion =
            detectVersion(mapOf("version" to value))
    }
}

/**
 * Centralised holder for protocol-wide defaults. Kept as a dedicated object so
 * callers can reference `A2UIProtocolDefaults.DEFAULT_VERSION` without pulling
 * in the enum's companion directly, which keeps call-sites easier to grep.
 */
object A2UIProtocolDefaults {
    val DEFAULT_VERSION: A2UIProtocolVersion = A2UIProtocolVersion.DEFAULT_VERSION

    /**
     * Default value for the strict-mode switch applied to v1.0 streams. The
     * Kuikly A2UI stack opts into strict mode out of the box so that any
     * non-conformant v1.0 payload fails fast during development.
     */
    const val STRICT_V10_BY_DEFAULT: Boolean = true
}
