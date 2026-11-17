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

internal object A2UIColorSupport {
    private const val CARD_DEFAULT_BORDER_COLOR = "#E0E0E0"
    private const val CARD_DEFAULT_SHADOW_COLOR = "#1A000000"
    const val CARD_DEFAULT_BACKGROUND_COLOR = "#FFFFFF"
    const val CARD_DEFAULT_BORDER_RAW = "1 solid $CARD_DEFAULT_BORDER_COLOR"
    const val CARD_DEFAULT_BOX_SHADOW_RAW = "0 1 2 $CARD_DEFAULT_SHADOW_COLOR 0"

    /**
     * Convert a CSS hex color literal into a decimal AARRGGBB string that
     * Kuikly's shorthand parsers (`border`, `boxShadow`, ...) can consume.
     * Eight-digit colors retain the adapter's Android AARRGGBB convention.
     */
    fun hexToKuiklyColor(hex: String): String {
        // CSS keyword: fully-transparent. KR's `toColor` uses
        // `Long.parseLong` which crashes on any non-decimal token, so we
        // pre-translate the one keyword A2UI JSON authors reasonably
        // expect to work. Additional named colors could join this list,
        // but "transparent" is the only one with no compact hex alias.
        if (hex.equals("transparent", ignoreCase = true)) return "0"
        // Defensive fast-path: an already-decimal string (e.g. seeded by
        // earlier passes of this same helper, or emitted directly by
        // A2UIComponentMapper.normalizeColorToArgbDecimal) is safe to hand
        // straight to KR. Skip the fall-through crash-shield below.
        if (isDecimalColorLiteral(hex)) return hex
        if (!hex.startsWith("#")) {
            // Any non-hex, non-decimal, non-keyword literal (e.g. a typo
            // like "reed", or a legacy CSS keyword like "black" that
            // slipped past schema validation) is a **defect** in the
            // source JSON — but the catalog's `pattern` is not enforced
            // at runtime, so we cannot rely on schema rejection. Rather
            // than let the token crash KR's `Long.parseLong`, downgrade
            // to fully-transparent black ("0") and log once for triage.
            reportInvalidColorLiteral(hex)
            return "0"
        }
        val body = hex.substring(1)
        // Expand to 8-digit AARRGGBB
        val aarrggbb: String = when (body.length) {
            3 -> {
                // #RGB -> #FFRRGGBB (each nibble doubled)
                val r = body[0]; val g = body[1]; val b = body[2]
                "FF$r$r$g$g$b$b"
            }
            6 -> "FF$body"                             // #RRGGBB
            8 -> {
                // Ambiguity: "#RRGGBBAA" (web/CSS) vs "#AARRGGBB" (Android).
                // The A2UI codebase uses the Android convention (#33000000
                // = 20% opaque black shadow), so treat 8-digit as AARRGGBB.
                body
            }
            else -> {
                // Malformed hex length (#12, #1234, #123456789, ...). Same
                // downgrade as above — never let it reach KR unparsed.
                reportInvalidColorLiteral(hex)
                return "0"
            }
        }
        val value = aarrggbb.toLongOrNull(16)
        if (value == null) {
            // Hex body has non-hex characters (e.g. "#GG0000"). Downgrade.
            reportInvalidColorLiteral(hex)
            return "0"
        }
        return value.toString()
    }

    private fun isDecimalColorLiteral(s: String): Boolean {
        if (s.isEmpty()) return false
        val start = if (s[0] == '-') 1 else 0
        if (start >= s.length) return false
        for (i in start until s.length) {
            if (s[i] !in '0'..'9') return false
        }
        return true
    }

    private fun reportInvalidColorLiteral(raw: String) {
        // Match A2UIDSLBridge's `[A2UI-DSL] ...` prefix convention so log
        // scrapers can pick this up alongside other adapter diagnostics.
        println("[A2UI-Adapter] Invalid color literal '$raw' — downgraded to transparent. Use CSS hex ('#RRGGBB' / '#AARRGGBB') or 'transparent'.")
    }

    /** Remove gradient-conflicting defaults without removing explicit border/shadow overrides. */
    fun clearDefaultsIfGradient(props: MutableMap<String, Any?>) {
        val g = props["backgroundLinearGradient"]
        if (g !is String || g.isEmpty()) return
        // Always drop the solid color paint — it wipes the gradient.
        props.remove("backgroundColor")
        // Drop only the *default* light border/shadow. A caller who wants a
        // border/shadow on a gradient card can set `styles.border` /
        // `styles.boxShadow` explicitly; expandStyles runs before this
        // helper is invoked, so those overrides are already in `props` and
        // won't match the sentinel values below.
        //
        // Derive both the pre- and post-normalization sentinel from the
        // same source constants used by buildProps("Card"). props may hold
        // either form depending on whether hex→decimal normalization has
        // already run, so we compare against both.
        val borderNormalized = normalizeHexInShorthand(CARD_DEFAULT_BORDER_RAW)
        if (props["border"] == borderNormalized ||
            props["border"] == CARD_DEFAULT_BORDER_RAW) {
            props.remove("border")
        }
        val shadowNormalized = normalizeHexInShorthand(CARD_DEFAULT_BOX_SHADOW_RAW)
        if (props["boxShadow"] == shadowNormalized ||
            props["boxShadow"] == CARD_DEFAULT_BOX_SHADOW_RAW) {
            props.remove("boxShadow")
        }
        props.remove("wrapperBoxShadowView")
    }

    /** Replace hex tokens in CSS shorthands with decimal ARGB values. */
    fun normalizeHexInShorthand(shorthand: String): String {
        if (!shorthand.contains('#')) return shorthand
        return shorthand.split(' ').joinToString(" ") { token ->
            if (token.startsWith("#")) hexToKuiklyColor(token) else token
        }
    }

    private fun isColorPropKey(key: String): Boolean {
        if (key == "color") return true
        return key.length > "Color".length && key.endsWith("Color", ignoreCase = true)
    }

    /** Normalize color-valued properties in place at the final props boundary. */
    fun normalizeColorFieldsInPlace(props: MutableMap<String, Any?>) {
        // Snapshot the key set so we can safely rewrite values in-place even
        // on JVM/Android/Native where MutableMap iterators may not tolerate
        // put() on the *same* key during iteration on all impls.
        val keys = props.keys.toList()
        for (key in keys) {
            if (!isColorPropKey(key)) continue
            val v = props[key]
            // Pipe every String color value through hexToKuiklyColor so
            // its crash-shield can catch invalid literals ("red",
            // "rgb(...)", typos, ...) and downgrade them to "0". The
            // helper's own decimal fast-path keeps already-normalized
            // values (e.g. seeded defaults like "4294967295") cheap.
            if (v is String) {
                props[key] = hexToKuiklyColor(v)
            }
        }
    }

    /** Copy only maps that carry string color values; otherwise preserve identity. */
    fun withNormalizedColors(props: Map<String, Any?>): Map<String, Any?> {
        // Fast path: no color-shaped key at all.
        var needs = false
        for (key in props.keys) {
            if (isColorPropKey(key)) {
                val v = props[key]
                // Any String color value must be piped through the
                // helper so invalid literals get downgraded. Fast-path
                // preservation for already-decimal values lives inside
                // hexToKuiklyColor itself.
                if (v is String) {
                    needs = true
                    break
                }
            }
        }
        if (!needs) return props
        val mutable = props.toMutableMap()
        normalizeColorFieldsInPlace(mutable)
        return mutable
    }
}
