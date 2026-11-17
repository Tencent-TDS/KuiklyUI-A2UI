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

import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion.V1_0

/**
 * Centralised source of truth for protocol-level mixin key names that differ
 * between A2UI v0.9 and v1.0.
 *
 * In v1.0 a set of "reserved" keys moved to the `@`-prefixed namespace in
 * order to disambiguate them from arbitrary component/function property
 * names. For example, the dynamic value selector that used to be written as
 *
 * ```json
 * { "path": "user.name" }
 * ```
 *
 * is now written as
 *
 * ```json
 * { "@path": "user.name" }
 * ```
 *
 * Call-sites that need to read these fields must do so via [ProtocolKeys]
 * rather than hard-coding the literal string, so that both v0.9 and v1.0
 * envelopes are supported from the same code path:
 *
 * ```kotlin
 * val path = obj[ProtocolKeys.path(protocolVersion)] as? String
 * ```
 *
 * Keys that did NOT move in v1.0 (e.g. the `path` field inside
 * `updateDataModel`, or the `children` key inside a ChildList template) are
 * intentionally omitted here and should continue to be referenced as plain
 * string literals.
 */
object ProtocolKeys {

    // ----- Keys whose name differs between v0.9 and v1.0 -----

    /** Dynamic value selector key: `"path"` (v0.9) / `"@path"` (v1.0). */
    fun path(v: A2UIProtocolVersion): String =
        if (v == V1_0) AT_PATH else BARE_PATH

    /** Function call key: `"call"` (v0.9) / `"@call"` (v1.0). */
    fun call(v: A2UIProtocolVersion): String =
        if (v == V1_0) AT_CALL else BARE_CALL

    /**
     * Accessibility attributes bag on a component:
     * `"accessibility"` (v0.9) / `"@accessibility"` (v1.0).
     */
    fun accessibility(v: A2UIProtocolVersion): String =
        if (v == V1_0) AT_ACCESSIBILITY else BARE_ACCESSIBILITY

    /**
     * Validation rules bag on a component:
     * `"checks"` (v0.9) / `"@checks"` (v1.0).
     */
    fun checks(v: A2UIProtocolVersion): String =
        if (v == V1_0) AT_CHECKS else BARE_CHECKS

    // ----- Keys that only exist in v1.0 (no v0.9 equivalent) -----

    /** Optional free-form metadata envelope. v1.0 only. */
    const val META: String = "@metadata"

    /** Per-component catalog override. v1.0 only. */
    const val CATALOG_ID: String = "@catalogId"

    /** Reserved key that resolves to the current loop index inside a
     *  ChildList template. v1.0 only. */
    const val INDEX: String = "@index"

    // ----- String literals (exposed for grep-ability and tests) -----

    const val AT_PATH: String = "@path"
    const val AT_CALL: String = "@call"
    const val AT_ACCESSIBILITY: String = "@accessibility"
    const val AT_CHECKS: String = "@checks"

    const val BARE_PATH: String = "path"
    const val BARE_CALL: String = "call"
    const val BARE_ACCESSIBILITY: String = "accessibility"
    const val BARE_CHECKS: String = "checks"

    /**
     * The exhaustive set of `@`-prefixed reserved keys recognised by v1.0.
     * Strict-mode parsing uses this to reject unknown `@foo` keys at the
     * root of a dynamic value.
     */
    val RESERVED_AT_KEYS: Set<String> = setOf(
        AT_PATH,
        AT_CALL,
        AT_ACCESSIBILITY,
        AT_CHECKS,
        META,
        CATALOG_ID,
        INDEX,
    )

    /**
     * Convert a user-escaped key into its literal form.
     *
     * In v1.0 a key that must render as a literal `@xxx` string (i.e. NOT be
     * interpreted as a reserved directive) is escaped by doubling the leading
     * `@`, so the wire value `"@@path"` means "a map key whose literal name
     * is `@path`". This helper strips exactly one leading `@` when it detects
     * the double-at escape sequence; all other inputs are returned unchanged.
     *
     * Examples:
     * - `unescapeAtKey("@@path")` -> `"@path"`
     * - `unescapeAtKey("@@@foo")` -> `"@@foo"`
     * - `unescapeAtKey("@path")`  -> `"@path"` (reserved, not touched here)
     * - `unescapeAtKey("foo")`    -> `"foo"`
     */
    fun unescapeAtKey(key: String): String =
        if (key.startsWith("@@")) key.substring(1) else key

    /**
     * Whether [key] is an `@`-prefixed key that v1.0 reserves for protocol
     * directives (as opposed to a user-escaped literal like `"@@foo"` or a
     * plain property name).
     */
    fun isReservedAtKey(key: String): Boolean =
        key.startsWith("@") && !key.startsWith("@@") && key in RESERVED_AT_KEYS

    /**
     * Whether [key] starts with a single `@` but is NOT one of the recognised
     * reserved keys. Strict v1.0 mode should reject such keys at parse time;
     * lenient mode may surface them as warnings.
     */
    fun isUnknownAtKey(key: String): Boolean =
        key.startsWith("@") && !key.startsWith("@@") && key !in RESERVED_AT_KEYS
}
