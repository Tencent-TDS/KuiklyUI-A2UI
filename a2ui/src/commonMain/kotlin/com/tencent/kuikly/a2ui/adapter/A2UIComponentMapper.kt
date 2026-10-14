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

/**
 * Maps standard A2UI v0.9 protocol component names and properties
 * to Kuikly internal component names and properties.
 *
 * Standard A2UI 0.9 components (18 total):
 * - Text, Image, Icon, Video, AudioPlayer
 * - Row, Column, List, Card
 * - Tabs, Modal, Divider, Button
 * - TextField, CheckBox, ChoicePicker, Slider, DateTimeInput
 */
object A2UIComponentMapper {

    /**
     * Mapping status for components that cannot be directly mapped to Kuikly components.
     * These are listed here for documentation purposes.
     *
     * Components with full mapping:
     * - Text -> KRRichTextView (text rendering)
     * - Image -> KRImageView (image display)
     * - Row -> KRView (flexDirection=row)
     * - Column -> KRView (flexDirection=column)
     * - List -> KRScrollView (scrollable list)
     * - Card -> KRView (container with styling)
     * - Divider -> KRView (thin line)
     * - Button -> KRView (clickable container)
     * - TextField -> KRTextFieldView (text input)
     *
     * Components with partial/placeholder mapping:
     * - Icon -> KRRichTextView (renders icon name as text placeholder)
     * - Video -> KRVideoView (native video player; host must register krVideoViewAdapter)
     * - AudioPlayer -> KRAudioView (native audio player; host must register krAudioViewAdapter)
     * - Tabs -> KRView (placeholder, needs custom implementation)
     * - Modal -> handled by the adapter: the Modal node renders nothing inline; its
     *   trigger is shown in place and tapping it opens a full-screen KRModalView overlay
     *   (built on demand) containing the modal's content. See A2UIProtocolAdapter.openModal().
     * - CheckBox -> KRCheckBoxView (native checkbox component with click toggle)
     * - ChoicePicker -> KRView (placeholder, needs custom implementation)
     * - Slider -> KRSliderView (native slider component with pan gesture support)
     * - DateTimeInput -> KRDateTimeInputView (VirtualView with Modal + DatePicker + ScrollPicker)
     */

    /**
     * Map standard A2UI 0.9 component name to Kuikly internal view name.
     * For unknown component names (custom components), returns the original name
     * so that createDSLView can create an A2UICustomComponentView.
     *
     * ## Icon routing
     * The [A2UIIconRegistry] acts as a global all-or-nothing switch for Icons:
     * - When the registry is empty, every Icon renders as a text glyph on
     *   `KRRichTextView` (default, backwards compatible).
     * - When the registry is non-empty, every Icon renders as an image on
     *   `KRImageView`, regardless of whether the specific icon name is registered
     *   (unregistered names fall back to [A2UIIconRegistry.FALLBACK_KEY]).
     *
     * The routing decision depends solely on the global registry state, so no
     * per-instance component definition is needed here.
     */
    fun mapComponentName(standardName: String): String {
        if (standardName == "Icon" && !A2UIIconRegistry.isEmpty()) {
            return "KRImageView"
        }
        return COMPONENT_NAME_MAP[standardName] ?: standardName
    }

    /**
     * True when the current Icon routing resolves to `KRImageView` for every Icon
     * (i.e. the host app has registered at least one custom icon image). Callers
     * that need to emit image-friendly layout props (e.g. width/height/resizeMode
     * instead of fontSize/textAlign) consult this helper.
     */
    fun isIconRenderedAsImage(): Boolean = !A2UIIconRegistry.isEmpty()

    /**
     * Resolve a static Icon `name` string to its registered image URI, applying
     * the fallback semantics of [A2UIIconRegistry.resolveIconUriOrFallback].
     * Returns `null` when [iconNameValue] is not a static string (data binding).
     */
    fun resolveIconImageUri(iconNameValue: Any?): String? {
        val staticName = iconNameValue as? String ?: return null
        return A2UIIconRegistry.resolveIconUriOrFallback(staticName)
    }

    /**
     * Map TextField component name to Kuikly internal view name based on variant.
     * Per A2UI protocol:
     * - variant="shortText" (default) / "number" / "obscured" -> KRTextFieldView (single-line)
     * - variant="longText" -> KRTextAreaView (multi-line text area)
     */
    fun mapTextFieldViewName(variant: String?): String {
        return when (variant) {
            "longText" -> "KRTextAreaView"
            else -> "KRTextFieldView"
        }
    }

    /**
     * Map standard A2UI 0.9 property name to Kuikly internal property name
     * for a given component type.
     * @return Pair of (kuiklyPropName, convertedValue) or null if the prop should be skipped.
     */
    fun mapProp(componentName: String, propKey: String, propValue: Any?): Pair<String, Any?>? {
        if (propValue == null) return null

        // Component-specific property mapping
        val componentResult = when (componentName) {
            "Text" -> mapTextProp(propKey, propValue)
            "Image" -> mapImageProp(propKey, propValue)
            "Icon" -> mapIconProp(propKey, propValue)
            "Row" -> mapRowProp(propKey, propValue)
            "Column" -> mapColumnProp(propKey, propValue)
            "List" -> mapListProp(propKey, propValue)
            "Card" -> mapCardProp(propKey, propValue)
            "Divider" -> mapDividerProp(propKey, propValue)
            "Button" -> mapButtonProp(propKey, propValue)
            "TextField" -> mapTextFieldProp(propKey, propValue)
            "Slider" -> mapSliderProp(propKey, propValue)
            "CheckBox" -> mapCheckBoxProp(propKey, propValue)
            "Video" -> mapVideoProp(propKey, propValue)
            "AudioPlayer" -> mapAudioPlayerProp(propKey, propValue)
            "DateTimeInput" -> mapDateTimeInputProp(propKey, propValue)
            else -> null
        }

        // If component-specific mapping handled it, use that result
        if (componentResult != null) return componentResult

        // Fallback to generic property mapping (layout props are handled separately)
        return GENERIC_PROP_MAP[propKey]?.let { it to propValue } ?: (propKey to propValue)
    }

    /**
     * Check if a standard component is a layout container (Row, Column, List, Card, etc.)
     */
    fun isLayoutContainer(componentName: String): Boolean {
        return componentName in LAYOUT_CONTAINERS
    }

    /**
     * Check if a standard component needs shadow (text measurement).
     */
    fun needsShadow(componentName: String): Boolean {
        return componentName in SHADOW_COMPONENTS
    }

    /**
     * Get the Kuikly view name that needs shadow support.
     */
    fun getShadowViewName(componentName: String): String? {
        return SHADOW_VIEW_NAME_MAP[componentName]
    }

    // ---- Private mappings ----

    private val COMPONENT_NAME_MAP = mapOf(
        "Text" to "KRRichTextView",
        "Image" to "KRImageView",
        "Icon" to "KRRichTextView",  // Render icon name as text for now
        "Video" to "KRVideoView",      // Native video player (requires host krVideoViewAdapter)
        "AudioPlayer" to "KRAudioView", // Native audio player (requires host krAudioViewAdapter)
        "Row" to "KRView",
        "Column" to "KRView",
        "List" to "KRScrollView",
        "Card" to "KRView",
        "Tabs" to "KRView",          // Placeholder
        "Modal" to "KRView",           // Node itself is skipped; overlay uses KRModalView (see adapter.openModal)
        "Divider" to "KRView",
        "Button" to "KRView",
        "TextField" to "KRTextFieldView",
        "CheckBox" to "KRCheckBoxView",  // Native CheckBox component
        "ChoicePicker" to "KRView",  // Placeholder
        "Slider" to "KRSliderView",    // Native Slider component
        "DateTimeInput" to "KRDateTimeInputView"  // DateTimeInput with picker popup
    )

    private val LAYOUT_CONTAINERS = setOf(
        "Row", "Column", "List", "Card", "Tabs", "Modal", "Button", "ChoicePicker"
    )

    private val SHADOW_COMPONENTS = setOf("Text", "Icon")

    private val SHADOW_VIEW_NAME_MAP = mapOf(
        "Text" to "KRRichTextView",
        "Icon" to "KRRichTextView"
    )

    private val GENERIC_PROP_MAP = mapOf(
        "backgroundColor" to "backgroundColor",
        "borderRadius" to "borderRadius",
        "borderColor" to "borderColor",
        "borderWidth" to "borderWidth",
        "opacity" to "opacity",
        "overflow" to "overflow"
    )

    // region ---- Kuikly `styles` extension (kuikly_a2ui_catalog.json) ----

    /**
     * Whitelist of common style keys accepted inside `styles` on every A2UI component.
     * Sourced from `kuikly_components_catalog.json → $defs.KRCommonProps`, minus fields
     * whose semantics are owned by the A2UI protocol layer (`id`, `action`,
     * `accessibility`, `touchEnable`).
     */
    private val COMMON_STYLE_KEYS = setOf(
        // sizing
        "width", "height", "minWidth", "minHeight", "maxWidth", "maxHeight",
        // flex
        "flex", "flexGrow", "flexShrink", "flexBasis", "alignSelf",
        // spacing
        "margin", "marginTop", "marginBottom", "marginLeft", "marginRight",
        "padding", "paddingTop", "paddingBottom", "paddingLeft", "paddingRight",
        // background
        "backgroundColor", "backgroundLinearGradient",
        // corners
        "borderRadius",
        // borders
        "borderWidth", "borderColor", "borderStyle",
        // visual
        "opacity", "visibility", "overflow", "zIndex", "boxShadow",
        // transform + absolute positioning
        "transform", "positionType", "top", "left", "bottom", "right"
    )

    /**
     * Whitelist of text-only style keys accepted inside `styles`, in addition to the
     * common set. Sourced from `kuikly_components_catalog.json → components.KRRichTextView`,
     * minus content fields (`text`, `component`).
     *
     * Per the current design only `Text` opts in — `Icon` intentionally does NOT accept
     * text styles because in image-mode (registry non-empty) it renders as `KRImageView`
     * and text properties would be silently dropped, and even in text-mode we prefer to
     * keep Icon styling deterministic (glyph size derives from surrounding layout).
     */
    private val TEXT_STYLE_KEYS = setOf(
        "fontSize", "color", "fontWeight", "fontStyle", "fontFamily",
        "textAlign", "textDecoration",
        "lineHeight", "lineSpacing", "letterSpacing",
        "numberOfLines", "firstLineHeadIndent", "textShadow"
    )

    /** Standard A2UI components that accept `TEXT_STYLE_KEYS` inside `styles`. */
    private val TEXT_STYLE_COMPONENTS = setOf("Text")

    /**
     * Expand a `styles` map on a standard A2UI component into a list of raw
     * `(kuiklyPropKey, kuiklyPropValue)` pairs, filtering by the component-specific
     * whitelist.
     *
     * Value handling:
     * - primitives (String / Number / Boolean) pass through unchanged;
     * - a Map value shaped like `{"path": ...}` or `{"call": ...}` is wrapped into
     *   [A2UIValue] via [resolveTextValue] so the caller's `resolveValue` /
     *   `resolveValueWithScope` can dereference it against the live data model
     *   (making styles fully data-bindable);
     * - structural Map values for `backgroundLinearGradient`, `boxShadow` and
     *   `transform` are converted here into the space/pipe-separated strings
     *   that Kuikly's KR layer expects (see [gradientMapToCssString],
     *   [boxShadowMapToCssString], [transformMapToCssString]); malformed maps
     *   yield null and are silently dropped so a bad style cannot poison the
     *   view with an invalid value;
     * - `textShadow` intentionally passes through as a Map — the DSL applier
     *   layer ([A2UIApplier.applyTextProps]) consumes the Map directly via the
     *   4-arg `textShadow(offsetX, offsetY, radius, color)` Kuikly Core API;
     * - other Map values pass through verbatim.
     *
     * Fields not present in the whitelist are silently dropped. The caller is
     * responsible for applying `resolveValue` on each returned value and for merging
     * the pairs into its `props` map.
     */
    fun expandStyles(componentName: String, styles: Map<String, Any?>): List<Pair<String, Any?>> {
        val allowText = componentName in TEXT_STYLE_COMPONENTS
        val out = mutableListOf<Pair<String, Any?>>()
        for ((key, rawValue) in styles) {
            if (rawValue == null) continue
            val allowed = key in COMMON_STYLE_KEYS || (allowText && key in TEXT_STYLE_KEYS)
            if (!allowed) continue

            val prepared: Any? = when (rawValue) {
                is Map<*, *> -> {
                    // Detect data-binding shapes and lift them into A2UIValue so that
                    // callers' resolveValue()/resolveValueWithScope() can dereference.
                    val hasPath = rawValue["path"] is String
                    val hasCall = rawValue["call"] is String
                    when {
                        hasPath || hasCall -> resolveTextValue(rawValue)
                        // Structured backgroundLinearGradient →  CSS linear-gradient string
                        // expected by KRCSSBackgroundDrawable.parseBackgroundImage().
                        // Input shape (per kuikly_a2ui_catalog.json):
                        //   { "direction": "to-bottom" | "to-top" | "to-right" | "to-left",
                        //     "colorStops": [ {"color": "#RRGGBB", "stop": 0.0..1.0 }, ... ] }
                        // Output shape:  "linear-gradient(<dir_int>, <color1> <stop1>, ...)"
                        //   dir_int: 0=BOTTOM_TOP (to-top), 1=TOP_BOTTOM (to-bottom),
                        //            2=RIGHT_LEFT (to-left), 3=LEFT_RIGHT (to-right)
                        key == "backgroundLinearGradient" -> gradientMapToCssString(rawValue)
                        // Structured boxShadow → KR's space-separated shorthand.
                        // Input shape:
                        //   { "offsetX": 0, "offsetY": 2, "radius": 8,
                        //     "color": "#33000000", "fill": false }
                        // Output shape: "offsetX offsetY radius color [fill]"
                        //   (fill is 0 by default; "1" produces an inner shadow).
                        // Matches KRViewDecoration.updateBgShadowDrawable() parser.
                        key == "boxShadow" -> boxShadowMapToCssString(rawValue)
                        // Structured transform → KR's pipe-separated shorthand.
                        // Input shape (all fields optional):
                        //   { "rotate": 45, "scale": 1.2 | [1.2, 0.8],
                        //     "translate": [0.1, 0.2] | { "x": 0.1, "y": 0.2 },
                        //     "anchor":    [0.5, 0.5] | { "x": 0.5, "y": 0.5 },
                        //     "skew":      [0, 0],
                        //     "rotateXY":  [0, 0] }
                        // Output shape (fixed order, KR parses positionally):
                        //   "rotate|scaleX scaleY|translateX translateY|pivotX pivotY[|skewX skewY][|rotateX rotateY]"
                        // Note: translate values are fractions of the view's frame
                        // (KR multiplies by frameWidth/frameHeight internally), NOT dp.
                        key == "transform" -> transformMapToCssString(rawValue)
                        else -> rawValue
                    }
                }
                else -> rawValue
            }
            if (prepared != null) {
                out += key to prepared
            }
        }
        return out
    }

    /**
     * Convert a structured `backgroundLinearGradient` map into the CSS-ish string
     * that Kuikly's KRCSSBackgroundDrawable expects. Returns null if the map is
     * malformed (missing/empty colorStops), so the caller can skip the prop
     * instead of poisoning the view with an invalid gradient.
     *
     * Colors inside `colorStops[i].color` are normalized to decimal ARGB long
     * strings via [normalizeColorToArgbDecimal] before being emitted. Rationale:
     * three-way parity between the KR render layers. Android's `String.toColor()`
     * falls back to `toLong().toInt()`; iOS's `[KRConvertUtil UIColor:@(...
     * longLongValue)]`; ohos's `ConvertToHexColor` calls `std::stol`. All three
     * are the naive base-10 parsers used when no KRColorParseAdapter is
     * registered — none of them accepts a leading `#`, so writing `"#3A6DF0"`
     * silently degrades to 0 (transparent black) on iOS/ohos and to a
     * NumberFormatException catch-all on Android, making the gradient
     * completely invisible on iOS/ohos and looking like a plain dark card on
     * Android. Pre-converting to `"4282063092"` etc. makes the string
     * portable and defensive against unconfigured hosts.
     */
    private fun gradientMapToCssString(map: Map<*, *>): String? {
        val stopsAny = map["colorStops"] as? List<*> ?: return null
        if (stopsAny.isEmpty()) return null
        val dir = when ((map["direction"] as? String)?.lowercase()) {
            "to-top" -> 0
            "to-right" -> 3
            "to-left" -> 2
            // Default & explicit "to-bottom" both map to TOP_BOTTOM (1).
            else -> 1
        }
        val sb = StringBuilder("linear-gradient(")
        sb.append(dir)
        for (item in stopsAny) {
            val stopMap = item as? Map<*, *> ?: continue
            val rawColor = stopMap["color"] as? String ?: continue
            val color = normalizeColorToArgbDecimal(rawColor)
            val stop = when (val s = stopMap["stop"]) {
                is Number -> s.toDouble()
                is String -> s.toDoubleOrNull() ?: continue
                else -> continue
            }
            // NOTE: use "," (no trailing space) between stops. The ohos KR
            // parser ([KRLinearGradientParser::ParseFromCssLinearGradient])
            // splits by "," via a raw first-of scan that does NOT trim tokens,
            // so a leading space would turn the color into "" and crash
            // downstream on std::stof(). Android/iOS parsers .trim() each
            // token before parsing, so the missing space is a no-op there.
            sb.append(',').append(color).append(' ').append(stop)
        }
        sb.append(')')
        return sb.toString()
    }

    /**
     * Convert a structured `boxShadow` map into the space-separated shorthand
     * that KR's [KRViewDecoration] expects:
     *   "offsetX offsetY radius color [fill]"
     *
     * Every field except `color` is optional. Returns null when `color` is
     * missing or unparsable so the caller can skip the prop instead of
     * emitting a malformed value that KR would silently reject.
     *
     * Colors are normalized to decimal ARGB long strings via
     * [normalizeColorToArgbDecimal] for the same reason as gradients: KR's
     * shadow color parsers on iOS/ohos/Android all default to base-10 int
     * parsing when no KRColorParseAdapter is registered, so `"#33000000"`
     * silently degrades to 0. `fill=true` produces an inner shadow; false or
     * absent yields the standard outer drop shadow.
     */
    private fun boxShadowMapToCssString(map: Map<*, *>): String? {
        val rawColor = map["color"] as? String ?: return null
        if (rawColor.isEmpty()) return null
        val color = normalizeColorToArgbDecimal(rawColor)
        val offsetX = numberToPlainString(map["offsetX"]) ?: "0"
        val offsetY = numberToPlainString(map["offsetY"]) ?: "0"
        val radius = numberToPlainString(map["radius"]) ?: "0"
        val fill = when (val f = map["fill"]) {
            is Boolean -> if (f) "1" else "0"
            is Number -> if (f.toInt() != 0) "1" else "0"
            is String -> if (f == "1" || f.equals("true", ignoreCase = true)) "1" else "0"
            null -> null
            else -> "0"
        }
        val sb = StringBuilder()
        sb.append(offsetX).append(' ').append(offsetY).append(' ')
            .append(radius).append(' ').append(color)
        if (fill != null) sb.append(' ').append(fill)
        return sb.toString()
    }

    /**
     * Normalize a color literal into the **decimal ARGB long** string form
     * that the KR render layers on all three platforms understand without
     * needing a `KRColorParseAdapter` registered.
     *
     * Accepted inputs:
     *  - `"#RGB"`      → expanded to `#RRGGBB`, alpha defaulted to `FF`
     *  - `"#ARGB"`     → expanded to `#AARRGGBB`
     *  - `"#RRGGBB"`   → alpha defaulted to `FF`
     *  - `"#AARRGGBB"` → used verbatim
     *  - a decimal integer string (already the target form) → returned as-is
     *  - anything else → returned verbatim (best-effort, downstream will
     *    likely fall back to 0)
     *
     * The result is the unsigned 32-bit ARGB packed as decimal, e.g.
     * `#3A6DF0` → `"4282063088"`. Using unsigned width matters: on some
     * hosts `Int.toLong()` of a negative Kotlin Int yields a sign-extended
     * 64-bit value that JSON/text parsers on the other side may mis-parse.
     * We produce a canonical positive decimal here so all three parsers
     * (`std::stol`, `longLongValue`, `String.toLong`) agree.
     */
    private fun normalizeColorToArgbDecimal(raw: String): String {
        val s = raw.trim()
        if (s.isEmpty()) return raw
        // CSS keyword: fully-transparent. KR's renderers use base-10
        // parsers (`Long.parseLong` / `stol` / `longLongValue`) which
        // crash on any non-decimal token. `transparent` is the only CSS
        // keyword without a compact hex alias, so we handle it here.
        if (s.equals("transparent", ignoreCase = true)) return "0"
        // Already-decimal fast-path (e.g. seeded by upstream normalization
        // in A2UIProtocolAdapter): hand through unchanged.
        if (isDecimalColorLiteral(s)) return s
        if (s[0] != '#') {
            // A non-hex, non-decimal, non-keyword literal is a **defect**:
            // catalog's `pattern` requires `^#[0-9a-fA-F]{3,8}$` but is
            // not enforced at runtime. Downgrade to fully-transparent
            // black rather than let it flow into the KR-facing shorthand
            // (e.g. "linear-gradient(1, red 0.0, ...)") where the render
            // layer's base-10 parser would crash.
            println("[A2UI-Mapper] Invalid color literal '$raw' — downgraded to transparent. Use CSS hex ('#RRGGBB' / '#AARRGGBB') or 'transparent'.")
            return "0"
        }
        val hex = s.substring(1)
        val expanded: String = when (hex.length) {
            3 -> {  // #RGB → #FFRRGGBB
                val r = hex[0]; val g = hex[1]; val b = hex[2]
                "FF$r$r$g$g$b$b"
            }
            4 -> {  // #ARGB → #AARRGGBB
                val a = hex[0]; val r = hex[1]; val g = hex[2]; val b = hex[3]
                "$a$a$r$r$g$g$b$b"
            }
            6 -> "FF$hex"          // #RRGGBB → alpha=FF
            8 -> hex               // #AARRGGBB
            else -> {
                // Malformed length (#12, #12345, #123456789, ...).
                println("[A2UI-Mapper] Malformed hex color '$raw' (length ${hex.length}) — downgraded to transparent.")
                return "0"
            }
        }
        // Parse as unsigned 32-bit hex; the high bit may make it negative if
        // we went through toInt(), so we go via Long and mask.
        val argbLong: Long = try {
            expanded.toLong(16) and 0xFFFFFFFFL
        } catch (_: NumberFormatException) {
            // Non-hex characters in body (e.g. "#GG0000").
            println("[A2UI-Mapper] Non-hex characters in color '$raw' — downgraded to transparent.")
            return "0"
        }
        return argbLong.toString()
    }

    /**
     * True iff [s] is a non-empty run of decimal digits (optionally with a
     * single leading minus). Kuikly Render's color parsers accept exactly
     * this shape.
     */
    private fun isDecimalColorLiteral(s: String): Boolean {
        if (s.isEmpty()) return false
        val start = if (s[0] == '-') 1 else 0
        if (start >= s.length) return false
        for (i in start until s.length) {
            if (s[i] !in '0'..'9') return false
        }
        return true
    }

    /**
     * Convert a structured `transform` map into the pipe-separated shorthand
     * that KR's [KRCSSAnimation.initTransformFromStr] parses positionally:
     *   "rotate|scaleX scaleY|translateX translateY|pivotX pivotY[|skewX skewY][|rotateX rotateY]"
     *
     * Callers may specify any subset of keys; missing ones fall back to the
     * neutral identity values (rotate=0, scale=1, translate=0, anchor=0.5).
     * The KR parser is strictly positional and requires the first 4 groups,
     * so we always emit them. skew / rotateXY groups are only emitted when
     * the caller opts in — omitting them keeps the string forward-compatible
     * with older KR versions.
     *
     * Accepted value shapes:
     *  - `scale`: Number (uniform) OR [x, y] OR { "x": _, "y": _ }
     *  - `translate` / `anchor` / `skew` / `rotateXY`: [x, y] OR { "x": _, "y": _ }
     *
     * Note: KR multiplies translate/anchor by frameWidth/frameHeight, so the
     * values here must be **fractions of the view's own frame**, not dp.
     */
    private fun transformMapToCssString(map: Map<*, *>): String? {
        if (map.isEmpty()) return null
        val rotate = numberToPlainString(map["rotate"]) ?: "0"
        val (scaleX, scaleY) = when (val s = map["scale"]) {
            is Number -> {
                val v = numberToPlainString(s)!!
                v to v
            }
            else -> readPointPair(s, "1", "1")
        }
        val (tx, ty) = readPointPair(map["translate"], "0", "0")
        val (ax, ay) = readPointPair(map["anchor"], "0.5", "0.5")
        val sb = StringBuilder()
        sb.append(rotate).append('|')
            .append(scaleX).append(' ').append(scaleY).append('|')
            .append(tx).append(' ').append(ty).append('|')
            .append(ax).append(' ').append(ay)
        val skew = map["skew"]
        if (skew != null) {
            val (sx, sy) = readPointPair(skew, "0", "0")
            sb.append('|').append(sx).append(' ').append(sy)
        }
        val rotateXY = map["rotateXY"]
        if (rotateXY != null) {
            // skew group must be present before rotateXY per positional format.
            if (skew == null) sb.append('|').append("0 0")
            val (rx, ry) = readPointPair(rotateXY, "0", "0")
            sb.append('|').append(rx).append(' ').append(ry)
        }
        return sb.toString()
    }

    /**
     * Read an (x, y) pair from either a two-element list or a `{ "x": _, "y": _ }`
     * map, falling back to [defaultX] / [defaultY] when the value is null or
     * malformed. Non-numeric entries are silently coerced to the default so
     * that a single bad field cannot poison the entire transform string.
     */
    private fun readPointPair(
        value: Any?,
        defaultX: String,
        defaultY: String
    ): Pair<String, String> {
        return when (value) {
            is List<*> -> {
                val x = numberToPlainString(value.getOrNull(0)) ?: defaultX
                val y = numberToPlainString(value.getOrNull(1)) ?: defaultY
                x to y
            }
            is Map<*, *> -> {
                val x = numberToPlainString(value["x"]) ?: defaultX
                val y = numberToPlainString(value["y"]) ?: defaultY
                x to y
            }
            null -> defaultX to defaultY
            else -> defaultX to defaultY
        }
    }

    /**
     * Format a Number as its plain decimal string (no scientific notation, no
     * trailing ".0" for integers). Strings that already look numeric pass
     * through verbatim. Returns null for other types so the caller can decide
     * on a default.
     */
    private fun numberToPlainString(value: Any?): String? {
        return when (value) {
            is Int, is Long, is Short, is Byte -> value.toString()
            is Float -> {
                if (value.isFinite() && value == value.toInt().toFloat()) value.toInt().toString()
                else value.toString()
            }
            is Double -> {
                if (value.isFinite() && value == value.toInt().toDouble()) value.toInt().toString()
                else value.toString()
            }
            is Number -> value.toString()
            is String -> if (value.toDoubleOrNull() != null) value else null
            else -> null
        }
    }

    // endregion

    // ---- Text component property mapping ----

    private fun mapTextProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "text" -> "text" to resolveTextValue(value)
            "variant" -> mapTextVariant(value.toString())
            else -> null // Skip unknown text props
        }
    }

    /**
     * Map text variant to fontSize + fontWeight props.
     * Returns a special marker that the adapter will expand into multiple props.
     */
    private fun mapTextVariant(variant: String): Pair<String, Any?>? {
        // Variant is handled at the adapter level to generate multiple props
        return "__variant" to variant
    }

    /**
     * Get font size for a text variant.
     * Per implementation guide: base font size = 16dp
     * h1: 2.5x=40, h2: 2x=32, h3: 1.75x=28, h4: 1.5x=24, h5: 1.25x=20
     * caption: 0.8x=13, body: 1x=16
     */
    fun getVariantFontSize(variant: String): Float {
        return when (variant) {
            "h1" -> 40f
            "h2" -> 32f
            "h3" -> 28f
            "h4" -> 24f
            "h5" -> 20f
            "caption" -> 13f
            "body" -> 16f
            else -> 16f
        }
    }

    /**
     * Get font weight for a text variant.
     * Headings (h1-h5) use bold weight.
     */
    fun getVariantFontWeight(variant: String): String? {
        return when (variant) {
            "h1", "h2", "h3", "h4", "h5" -> "bold"
            else -> null
        }
    }

    /**
     * Get text color for a text variant.
     * Per guide: caption uses muted/lighter color, others inherit.
     * Leaf components should never hardcode colors unless explicitly instructed.
     * We use a default dark color for readability.
     */
    fun getVariantColor(variant: String): String {
        return when (variant) {
            "caption" -> "#666666"
            else -> "#1A1A1A"
        }
    }

    // ---- Image component property mapping ----

    private fun mapImageProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "url" -> "src" to resolveTextValue(value)
            // A2UI protocol: `Image.description` is a DynamicString carrying the image's
            // accessibility text (alt text). It's non-visual — it feeds screen readers
            // (Android contentDescription / iOS accessibilityLabel), mirroring the
            // reference Lit renderer that emits `<img alt=${description}>`.
            //
            // We surface it as an internal `__accessibility` prop so the adapter's
            // generic prop loop (see A2UIProtocolAdapter.buildProps) will run it through
            // resolveValue and flatten every DynamicString form:
            //   - literal string          → A2UIValue.Literal("alt text")
            //   - {"path": "/alt"}        → A2UIValue.PathBinding → dataModel value
            //   - {"reference": {...}} / {"call": ...} → A2UIValue.FunctionCall
            // A2UIApplier.applyImageProps then reads this key and invokes Kuikly's
            // `accessibility(...)` on the ImageAttr. The `__` prefix keeps it out of any
            // conflict with the top-level `accessibility` component field (which is
            // present in skipKeys) and with native Kuikly style attr names.
            "description" -> "__accessibility" to resolveTextValue(value)
            "fit" -> "resizeMode" to mapImageFit(value.toString())
            "variant" -> "" to null // Handled by adapter in buildProps for sizing
            else -> null
        }
    }

    private fun mapImageFit(fit: String): String {
        // A2UI fit  ->  Kuikly ImageAttr resizeMode
        // ------------------------------------------------------------------
        // A2UI catalog defines fit ∈ {contain, cover, fill, none, scaleDown},
        // with "fill" as the documented default. It maps to CSS `object-fit`
        // semantics. Kuikly's ImageAttr only exposes three modes:
        //   resizeCover()   -- object-fit: cover
        //   resizeContain() -- object-fit: contain
        //   resizeStretch() -- object-fit: fill  (stretch to fill, may distort)
        //
        // Two enum values have NO exact native equivalent and are downgraded
        // here. This is an intentional approximation:
        //
        //   * "none"      : CSS "render at intrinsic size, no scaling, centered".
        //                   Kuikly has no center-without-scale mode, so we fall
        //                   back to "contain" — image stays fully visible but is
        //                   scaled to fit the container (large images will be
        //                   shrunk instead of clipped).
        //
        //   * "scaleDown" : CSS "max(none, contain)" — never enlarge, only
        //                   shrink oversized images. Kuikly has no such mode,
        //                   so we fall back to plain "contain", which WILL
        //                   enlarge small images to fill (semantic drift).
        //                   A precise implementation would require inspecting
        //                   the image's intrinsic size (via loadResolution)
        //                   and choosing contain-vs-center at runtime.
        return when (fit) {
            "contain" -> "contain"
            "cover" -> "cover"
            "fill" -> "stretch"
            "none" -> "contain"        // downgrade: no native "center" mode
            "scaleDown" -> "contain"   // downgrade: no native "scale-down" mode
            else -> "stretch" // default: fill -> stretch
        }
    }

    // ---- Icon component property mapping ----

    /**
     * Map the Icon `name` field to either an image `src` or a text glyph, depending
     * on the global [A2UIIconRegistry] state:
     *
     * ### Registry non-empty (image mode)
     * The Icon renders as `KRImageView` — the target key is `src` for every case:
     * - **Static name**: resolve via [A2UIIconRegistry.resolveIconUriOrFallback] and
     *   emit `src = A2UIValue.Literal(uri)`. Unregistered names fall back to the
     *   registered `_fallback` entry (or an empty URI when no fallback exists).
     * - **Data-bound name** (`{"path": "/iconName"}`): emit `src` as an
     *   [A2UIValue.IconPathBinding] that wraps the underlying [A2UIValue.PathBinding];
     *   the adapter's value resolvers detect this subtype, resolve the underlying path
     *   to obtain the icon name string, then translate it into an image URI via
     *   [A2UIIconRegistry.resolveIconUriOrFallback] before applying to `KRImageView.src`.
     * - **SVG path Icon** (`Map` without `path`): emit `src = <fallback URI>` — no
     *   unicode placeholder can be shown on an image view, and the fallback (if any)
     *   covers this case consistently.
     *
     * ### Registry empty (text mode)
     * The Icon renders as `KRRichTextView` — the target key is `text`:
     * - **Static name**: emit `text = A2UIValue.Literal(<unicode glyph>)`.
     * - **Data-bound name**: emit `text = A2UIValue.PathBinding(...)` untouched;
     *   the adapter's binding evaluator will later convert the resolved name to
     *   the unicode glyph via [resolveIconNameToChar].
     * - **SVG path Icon**: emit `text = "●"` placeholder.
     */
    private fun mapIconProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "name" -> {
                if (isIconRenderedAsImage()) {
                    // ---- image mode ----
                    if (value is Map<*, *>) {
                        val path = value["path"] as? String
                        if (path != null) {
                            // Data-bound name: emit an IconPathBinding so the adapter's value
                            // resolvers know to (1) resolve the underlying path to a name
                            // string, then (2) translate that name into an image URI via
                            // A2UIIconRegistry.resolveIconUriOrFallback before writing to `src`.
                            val isAbsolute = path.startsWith("/")
                            return "src" to A2UIValue.IconPathBinding(
                                A2UIValue.PathBinding(path, isAbsolute)
                            )
                        }
                        // SVG path Icon: no image representation available, emit fallback URI
                        // (which KRImageView will render as the registered `_fallback` image,
                        // or a blank slot when no fallback is registered).
                        return "src" to A2UIValue.Literal(A2UIIconRegistry.resolveIconUriOrFallback(null))
                    }
                    // Static name: resolve now (may return fallback URI or "").
                    val uri = resolveIconImageUri(value) ?: A2UIIconRegistry.resolveIconUriOrFallback(null)
                    return "src" to A2UIValue.Literal(uri)
                }

                // ---- text mode (registry empty) ----
                if (value is Map<*, *>) {
                    val path = value["path"] as? String
                    if (path != null) {
                        return "text" to resolveTextValue(value)
                    }
                    return "text" to A2UIValue.Literal("●")
                }
                "text" to A2UIValue.Literal(mapIconName(value))
            }
            else -> null
        }
    }

    private fun mapIconName(value: Any?): String {
        // Map known icon names to emoji/unicode equivalents.
        // Normalize snake_case aliases (e.g. "arrow_upward") to camelCase before lookup
        // so callers using either style stay compatible.
        return ICON_MAP[normalizeIconName(value.toString())] ?: "●"
    }

    /**
     * Resolve an icon name string to its unicode/emoji representation.
     * Used by the adapter when re-evaluating data-bound icon names.
     */
    fun resolveIconNameToChar(name: String): String {
        return ICON_MAP[normalizeIconName(name)] ?: "●"
    }

    /**
     * Convert snake_case icon names to camelCase to match the A2UI protocol enum.
     * Examples: "arrow_upward" -> "arrowUpward", "priority_high" -> "priorityHigh".
     * Already-camelCase names pass through unchanged.
     */
    private fun normalizeIconName(raw: String): String {
        if (!raw.contains('_')) return raw
        val sb = StringBuilder(raw.length)
        var upperNext = false
        for (c in raw) {
            if (c == '_') {
                upperNext = true
            } else if (upperNext) {
                sb.append(c.uppercaseChar())
                upperNext = false
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    // ---- Row component property mapping ----

    private fun mapRowProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "justify" -> "justifyContent" to mapJustify(value.toString())
            "align" -> "alignItems" to mapAlign(value.toString())
            "children" -> "" to null // Handled by adapter (tree structure)
            else -> null
        }
    }

    // ---- Column component property mapping ----

    private fun mapColumnProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "justify" -> "justifyContent" to mapJustify(value.toString())
            "align" -> "alignItems" to mapAlign(value.toString())
            "children" -> "" to null // Handled by adapter (tree structure)
            else -> null
        }
    }

    // ---- List component property mapping ----

    private fun mapListProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "direction" -> "" to null // Handled by adapter in buildProps
            "align" -> "alignItems" to mapAlign(value.toString())
            "children" -> "" to null // Handled by adapter
            else -> null
        }
    }

    // ---- Card component property mapping ----

    private fun mapCardProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "child" -> "" to null // Handled by adapter (tree structure)
            else -> null
        }
    }

    // ---- Divider component property mapping ----

    private fun mapDividerProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "axis" -> "" to null // Handled by adapter to set width/height in buildProps
            else -> null
        }
    }

    // ---- Button component property mapping ----

    private fun mapButtonProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "child" -> null // Handled by adapter (tree structure)
            "variant" -> "" to null // Handled by adapter in buildProps, skip
            "action" -> "" to null // Event handling, skip for now
            "checks" -> "" to null // Handled by adapter checks evaluation
            else -> null
        }
    }

    // ---- TextField component property mapping ----

    private fun mapTextFieldProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            // Per A2UI reference renderer, `label` is a persistent text shown ABOVE the
            // input (not an in-field placeholder). It is rendered as a separate Text in
            // buildComponentTree, so it is intentionally not mapped to an input prop here.
            "label" -> "" to null
            "value" -> "text" to resolveTextValue(value)
            "variant" -> "variant" to value // Input type hint, handled by A2UIApplier
            "validationRegexp" -> "" to null // Validation, skip for now
            "checks" -> "" to null // Handled by adapter checks evaluation
            else -> null
        }
    }

    // ---- Slider component property mapping ----

    private fun mapSliderProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "value" -> "value" to resolveTextValue(value)
            "min" -> "min" to value
            "max" -> "max" to value
            "label" -> "" to null // Label is handled by adapter (separate Text component above slider)
            "progressColor" -> "progressColor" to value
            "trackColor" -> "trackColor" to value
            "thumbColor" -> "thumbColor" to value
            "trackThickness" -> "trackThickness" to value
            "thumbSize" -> "thumbSize" to value
            else -> null
        }
    }

    // ---- CheckBox component property mapping ----

    private fun mapCheckBoxProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "value" -> "checked" to resolveTextValue(value)
            "label" -> "" to null // Label is handled by adapter (separate Text component or ignored)
            "disable" -> "disable" to value
            else -> null
        }
    }

    // ---- Video component property mapping ----

    private fun mapVideoProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "url" -> "src" to resolveTextValue(value)
            else -> null
        }
    }

    // ---- AudioPlayer component property mapping ----

    private fun mapAudioPlayerProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "url" -> "src" to resolveTextValue(value)
            // Per A2UI reference renderer, `description` is a persistent title shown ABOVE
            // the player. It is rendered as a separate Text in buildComponentTree, so it is
            // intentionally not mapped to a player prop here.
            "description" -> "" to null
            else -> null
        }
    }

    // ---- DateTimeInput component property mapping ----

    private fun mapDateTimeInputProp(key: String, value: Any?): Pair<String, Any?>? {
        return when (key) {
            "value" -> "value" to resolveTextValue(value)
            "enableDate" -> "enableDate" to value
            "enableTime" -> "enableTime" to value
            "label" -> "label" to value
            "min" -> "min" to resolveTextValue(value)
            "max" -> "max" to resolveTextValue(value)
            else -> null
        }
    }

    // ---- Shared mapping helpers ----

    private fun mapJustify(value: String): String {
        return when (value) {
            "start" -> "flex-start"
            "center" -> "center"
            "end" -> "flex-end"
            "spaceBetween" -> "space-between"
            "spaceAround" -> "space-around"
            "spaceEvenly" -> "space-evenly"
            "stretch" -> "flex-start" // stretch not valid for justifyContent
            else -> "flex-start"
        }
    }

    private fun mapAlign(value: String): String {
        return when (value) {
            "start" -> "flex-start"
            "center" -> "center"
            "end" -> "flex-end"
            "stretch" -> "stretch"
            else -> "stretch"
        }
    }

    /**
     * Resolve a dynamic value into a type-safe [A2UIValue] representation.
     *
     * Instead of returning raw strings (where path references are indistinguishable
     * from literal text), this returns a sealed class that preserves semantic type:
     * - Plain strings/values → [A2UIValue.Literal]
     * - Data binding {"path": "/key"} → [A2UIValue.PathBinding]
     * - Function calls {"call": "...", "args": {...}} → [A2UIValue.FunctionCall]
     *
     * This eliminates the fragile `startsWith("/")` heuristic.
     */
    fun resolveTextValue(value: Any?): Any? {
        if (value == null) return null
        if (value is String) return A2UIValue.Literal(value)
        if (value is Number) return A2UIValue.Literal(value)
        if (value is Map<*, *>) {
            // Data binding: {"path": "/key"}
            val path = value["path"] as? String
            if (path != null) {
                val isAbsolute = path.startsWith("/")
                return A2UIValue.PathBinding(path, isAbsolute)
            }
            // Function call: {"call": "formatDate", "args": {...}}
            val call = value["call"] as? String
            if (call != null) {
                return A2UIValue.FunctionCall(value)
            }
        }
        return A2UIValue.Literal(value.toString())
    }

    /**
     * Icon name to unicode/emoji mapping for common icons.
     */
    private val ICON_MAP = mapOf(
        "accountCircle" to "👤",
        "add" to "+",
        "arrowBack" to "←",
        "arrowForward" to "→",
        "arrowUpward" to "↑",
        "attachFile" to "📎",
        "calendarToday" to "📅",
        "call" to "📞",
        "camera" to "📷",
        "check" to "✓",
        "close" to "✕",
        "delete" to "🗑",
        "directionsRun" to "🏃",
        "download" to "⬇",
        "edit" to "✏",
        "event" to "📅",
        "error" to "⚠",
        "fastForward" to "⏩",
        "favorite" to "❤",
        "favoriteOff" to "♡",
        "folder" to "📁",
        "help" to "?",
        "home" to "🏠",
        "info" to "ℹ",
        "locationOn" to "📍",
        "lock" to "🔒",
        "lockOpen" to "🔓",
        "mail" to "✉",
        "menu" to "☰",
        "moreVert" to "⋮",
        "moreHoriz" to "⋯",
        "notificationsOff" to "🔕",
        "notifications" to "🔔",
        "pause" to "⏸",
        "payment" to "💳",
        "person" to "👨",
        "priorityHigh" to "❗",
        "phone" to "📱",
        "photo" to "🖼",
        "play" to "▶",
        "print" to "🖨",
        "refresh" to "↻",
        "rewind" to "⏪",
        "search" to "🔍",
        "send" to "✈",
        "settings" to "⚙",
        "share" to "🔗",
        "shoppingCart" to "🛒",
        "skipNext" to "⏭",
        "skipPrevious" to "⏮",
        "star" to "★",
        "starHalf" to "⯨",
        "starOff" to "☆",
        "stop" to "⏹",
        "trendingUp" to "📈",
        "upload" to "⬆",
        "visibility" to "👁",
        "visibilityOff" to "🙈",
        "volumeDown" to "🔉",
        "volumeMute" to "🔇",
        "volumeOff" to "🔇",
        "volumeUp" to "🔊",
        "warning" to "⚠"
    )
}
