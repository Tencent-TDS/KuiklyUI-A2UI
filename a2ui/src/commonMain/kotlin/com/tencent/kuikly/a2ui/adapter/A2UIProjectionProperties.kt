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

import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BACKGROUND_COLOR
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BORDER_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BOX_SHADOW_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.clearDefaultsIfGradient
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.hexToKuiklyColor
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeColorFieldsInPlace
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeHexInShorthand
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.withNormalizedColors
import com.tencent.kuikly.a2ui.adapter.A2UIDataPath.parsePathSegments
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.addThousandsSeparator
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToBool
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToInt
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.doubleToPlainString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.findMatchingBrace
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDateString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDouble
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.splitArguments
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolDefaults
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.protocol.A2UISurfaceConfig
import com.tencent.kuikly.a2ui.protocol.ProtocolKeys
import com.tencent.kuikly.a2ui.protocol.ValidationResult
import com.tencent.kuikly.a2ui.rpc.A2UIRpcDispatcher
import com.tencent.kuikly.a2ui.rpc.A2UIRpcError
import com.tencent.kuikly.a2ui.rpc.A2UIRpcRequest
import com.tencent.kuikly.a2ui.rpc.A2UIRpcResponse

internal fun A2UIComponentProjection.injectAccessibilityProps(
        compMap: Map<String, Any?>,
        props: MutableMap<String, Any?>
    ) {
        val a11y = readAccessibilityKey(compMap) ?: return
        val componentId = compMap["id"] as? String

        // description -> __accessibility (only if component mapper didn't already set it)
        if (!props.containsKey("__accessibility")) {
            val description = a11y["description"]
            if (description != null) {
                val resolved = resolveValue(
                    A2UIComponentMapper.resolveTextValue(description),
                    componentId
                )?.toString()
                if (!resolved.isNullOrEmpty()) {
                    props["__accessibility"] = resolved
                }
            }
        }

        // live -> __accessibilityLive (enum string, resolved via the same
        // DynamicString pipeline so bindings work)
        a11y["live"]?.let { live ->
            val resolved = resolveValue(
                A2UIComponentMapper.resolveTextValue(live),
                componentId
            )?.toString()?.lowercase()
            if (resolved == "off" || resolved == "polite" || resolved == "assertive") {
                props["__accessibilityLive"] = resolved
            }
        }

        // hidden -> __accessibilityHidden (DynamicBoolean)
        a11y["hidden"]?.let { hidden ->
            val resolved = resolveValue(hidden, componentId)
            val bool = when (resolved) {
                is Boolean -> resolved
                is Number -> resolved.toInt() != 0
                is String -> resolved == "true" || resolved == "1"
                else -> null
            }
            if (bool != null) {
                props["__accessibilityHidden"] = bool
            }
        }
    }
internal fun A2UIComponentProjection.checkErrorTextProps(errorMessage: String?): Map<String, Any?> {
        return if (errorMessage != null) {
            mapOf("text" to errorMessage, "height" to Float.NaN, "opacity" to 1f)
        } else {
            mapOf("text" to "", "height" to 0f, "opacity" to 0f)
        }
    }
internal fun A2UIComponentProjection.computeKuiklyProperties(componentType: String, compMap: Map<String, Any?>): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()
        val id = compMap["id"] as? String

        // Apply default props (currently empty for Kuikly components)
        props.putAll(KuiklyComponentMapper.buildDefaultProps(componentType))

        // Pass through all non-structural properties directly
        for ((key, value) in compMap) {
            val mapped = KuiklyComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                props[propKey] = resolveValue(propValue, id)
            }
        }

        // Merge borderWidth + borderColor into "border" property (Kuikly DSL format: "width style color")
        val borderWidth = props.remove("borderWidth")
        val borderColor = props.remove("borderColor")
        if (borderWidth != null || borderColor != null) {
            val width = borderWidth?.toString() ?: "1"
            val rawColor = borderColor?.toString() ?: "#000000"
            // iOS renderer parses the shorthand color via [NSString longLongValue]
            // (decimal only) - hex literals collapse to transparent. Normalize here.
            val color = hexToKuiklyColor(rawColor)
            props["border"] = "$width solid $color"
        }

        // Ensure borderRadius is String (Kuikly native expects String)
        val borderRadius = props["borderRadius"]
        if (borderRadius is Number) {
            props["borderRadius"] = borderRadius.toString()
        }

        // Store original definition for re-evaluation on data model updates
        if (id != null) {
            planDefinition(id, compMap)
        }

        // Extract and store action definition, set touchEnable for clickable Kuikly components
        val action = compMap["action"]
        if (action != null && id != null) {
            val tag = idToTag[id]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                planAction(tag, action)
                props["touchEnable"] = 1
            }
        }

        normalizeColorFieldsInPlace(props)
        return props
    }
internal fun A2UIComponentProjection.computeProperties(componentType: String, compMap: Map<String, Any?>): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()

        // Determine if this is the root component (no parent in childToParent)
        val id = compMap["id"] as? String
        val isRoot = id != null && !childToParentCache.containsKey(id)

        // Apply component-specific default layout props per implementation guide
        when (componentType) {
            "Row" -> {
                // Row: horizontal flex container, fills available width
                // Per guide: Row/Column have ZERO internal padding and ZERO external margins
                props["flexDirection"] = "row"
                // Per catalog: default align is "stretch"
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
                // Auto-wrap children onto new lines when they don't fit on one line.
                // The engine has no flex-shrink, so without wrapping a row whose children
                // exceed its width simply overflows the parent. Wrapping keeps the content
                // inside the parent's bounds (note: a single child that is itself wider than
                // the whole row still can't be split by the engine and stays on its own line).
                props["flexWrap"] = "wrap"
                // Stack wrapped lines cleanly from the top so they don't overlap on the
                // cross axis (same fix applied to ChoicePicker's chip row).
                props["alignContent"] = "flex-start"
            }
            "Column" -> {
                // Column: vertical flex container
                // Per guide: Row/Column have ZERO internal padding and ZERO external margins
                props["flexDirection"] = "column"
                // Per catalog: default align is "stretch" — make it explicit so that the
                // engine default cannot silently drift away from the protocol contract.
                // If the source JSON provides an `align`, the mapProp loop later overwrites
                // this with mapAlign(compMap["align"]).
                if (compMap["align"] == null) {
                    props["alignItems"] = "stretch"
                }
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
                // When Column is inside a Row and has no explicit weight, it should
                // flex to fill remaining space. Without this, Column's width is determined
                // by content (e.g. text natural width), causing text overflow without wrapping.
                if (id != null && compMap["weight"] == null) {
                    val parentId = childToParentCache[id]
                    if (parentId != null) {
                        val parentComp = idToTag[parentId]?.let { tagToComponentName[it] }
                        if (parentComp == "Row") {
                            props["flex"] = 1
                        }
                    }
                }
            }
            "Card" -> {
                // Per basic_catalog_implementation_guide.md (Card): rounded corners
                // (8-12dp), inner padding (16dp), a background distinct from the surface,
                // and a subtle shadow/elevation, plus a 1dp outline for nesting clarity.
                // Card carries the standard 8dp external margin (Leaf-Margin Strategy).
                //
                // NOTE: buildProps (unlike buildKuiklyProps) does NOT merge
                // borderWidth/borderColor into the `border` shorthand, and the renderer
                // only honors `border` + `boxShadow`. The previous borderWidth/borderColor
                // props were therefore silently dropped (no outline, no shadow).
                props["flexDirection"] = "column"
                props["borderRadius"] = "12"
                props["border"] = normalizeHexInShorthand(CARD_DEFAULT_BORDER_RAW)
                if (props["backgroundColor"] == null) {
                    props["backgroundColor"] = CARD_DEFAULT_BACKGROUND_COLOR
                }
                // boxShadow format: "offsetX offsetY radius color [fill]" (dp). A non-zero
                // offset and a positive radius are required for the shadow to render;
                // fill=0 draws a clean outer drop shadow (view region clipped out).
                // Keep blur small (2dp) so the shadow doesn't extend far beyond the
                // rounded corners — avoids grey residue at corners on Android.
                props["boxShadow"] = normalizeHexInShorthand(CARD_DEFAULT_BOX_SHADOW_RAW)
                // iOS-only escape hatch: when a view has both `borderRadius` and
                // `boxShadow`, the rounded-corner mask on CALayer also clips the
                // drop shadow, making it invisible. Setting `wrapperBoxShadowView=1`
                // tells the iOS renderer to wrap this view in a KRBoxShadowView so
                // the shadow is drawn on an outer layer that isn't masked.
                // Android/Web/HarmonyOS ignore this prop; Core auto-sets it inside
                // View.boxShadow(...) but A2UI writes props directly, so we opt in
                // here explicitly. See core/base/Props.kt WRAPPER_BOX_SHADOW_VIEW.
                props["wrapperBoxShadowView"] = 1
                props["padding"] = 16
                props["margin"] = 8
                // Per v0.9 protocol: Card fills width, height is content-driven
                props["alignSelf"] = "stretch"
                if (isRoot) {
                    props["margin"] = 0
                }
            }
            "Divider" -> {
                // Per A2UI catalog: 1dp line spanning 100% of the parent's cross axis,
                // with a subtle border color. Divider is a leaf, carries 8dp margin.
                //
                // Height/width strategy:
                //   - horizontal: fixed height=1, alignSelf=stretch to fill parent width.
                //   - vertical:   fixed width=1 + explicit visible height fallback.
                //
                // Why vertical Divider does NOT rely on alignSelf=stretch alone:
                // The Kuikly flex engine cannot stretch a node whose intrinsic cross-axis
                // size is undefined when the parent's cross-axis is content-driven (Row
                // height derived from siblings). In that scenario the Divider's undefined
                // layoutDim propagates through max()/boundAxis() as NaN, which makes both
                // the Row's height AND the divider's stretched height collapse to 0.
                //
                // Solution: give the vertical divider a concrete fallback height so the
                // rule is always visible AND does not poison the Row's cross-axis
                // measurement. The value 40 matches the natural height of a body Text
                // (~24 line-height + 8+8 margin), which is the most common sibling in a
                // Row that uses a vertical divider ("Left | Right"). The applier layer
                // can further stretch this to the actual Row height after layout via the
                // __verticalDividerFollowParent hook.
                val axis = compMap["axis"] as? String ?: "horizontal"
                props["backgroundColor"] = "#E0E0E0"
                if (axis == "horizontal") {
                    props["height"] = 1
                    props["alignSelf"] = "stretch"
                    props["marginTop"] = 8
                    props["marginBottom"] = 8
                } else {
                    props["width"] = 1
                    props["height"] = 40
                    props["marginLeft"] = 8
                    props["marginRight"] = 8
                    // Marker consumed by A2UIApplier: after the initial layout, resize the
                    // divider's height to match the parent Row's actual height so it truly
                    // "spans the height of the container" for tall siblings (images, etc.).
                    props["__verticalDividerFollowParent"] = true
                }
            }
            "Button" -> {
                // Per guide: Button is a visually outlined container, carries 8dp margin
                // variant handling: default=subtle bg+border, primary=primaryColor bg, borderless=no bg/border
                val variant = compMap["variant"] as? String ?: "default"
                props["flexDirection"] = "row"
                props["alignItems"] = "center"
                props["justifyContent"] = "center"
                props["borderRadius"] = "6"
                props["padding"] = 10
                props["paddingLeft"] = 16
                props["paddingRight"] = 16
                props["margin"] = 8
                when (variant) {
                    "primary" -> {
                        props["backgroundColor"] = primaryColor
                    }
                    "borderless" -> {
                        // No background, no border - looks like clickable text
                    }
                    else -> {
                        // "default": subtle background + border
                        props["backgroundColor"] = "#F5F5F5"
                        props["borderWidth"] = "1"
                        // Raw hex kept here; buildKuiklyProps() collapses borderWidth+
                        // borderColor into the `border` shorthand and converts the hex
                        // to a decimal AARRGGBB value the iOS renderer can parse.
                        props["borderColor"] = "#CCCCCC"
                    }
                }
            }
            "List" -> {
                // Per A2UI v0.9 protocol: List is a scrollable, invisible container with
                // ZERO spacing. It fills the cross axis of its parent; the main-axis size
                // is bounded by the parent, and content exceeding the main axis MUST be
                // scrollable (not clipped).
                //
                // The DSL side (A2UIApplier.createDSLView) maps "KRScrollView" to
                // A2UIScrollerView, which extends ScrollerView with cross-axis size
                // backfill. This guarantees:
                //   * A KRScrollContentView is auto-inserted so iOS KRScrollView.contentSize
                //     stays in sync with children;
                //   * ScrollerAttr.flexDirection override auto-emits `directionRow` prop;
                //   * ScrollerView.willInit defaults overflow=true — do NOT forward
                //     `overflow` here (applyCommonProps maps non-"hidden" to overflow(false),
                //     which would disable scrolling);
                //   * A2UIScrollerView.backfillCrossAxisSize() sets ScrollerView's height
                //     after contentView layout, so parent Card can correctly size itself.
                val direction = compMap["direction"] as? String ?: "vertical"
                val isHorizontal = direction == "horizontal"
                props["flexDirection"] = if (isHorizontal) "row" else "column"
                // Fill the cross-axis of the parent
                props["alignSelf"] = "stretch"
            }
            "Image" -> {
                // Per guide: Image is a leaf component, carries 8dp margin
                // Per guide: defaults to flexible width to fill container
                val variant = compMap["variant"] as? String ?: "mediumFeature"
                props["margin"] = 8

                // Check if parent is a Row (horizontal layout) to adjust sizing strategy
                val parentId = if (id != null) childToParentCache[id] else null
                val parentDef = if (parentId != null) idToOriginalDef[parentId] else null
                val parentIsRow = parentDef?.get("component") == "Row"

                when (variant) {
                    "icon" -> {
                        props["width"] = 24
                        props["height"] = 24
                        props["margin"] = 0 // Icon-sized images don't need margin
                    }
                    "avatar" -> {
                        props["width"] = 40
                        props["height"] = 40
                        props["borderRadius"] = "20" // Fully rounded
                    }
                    "smallFeature" -> {
                        if (parentIsRow) {
                            // In a Row, keep the legacy fixed 100×100 slot: inside a horizontal
                            // layout cross-axis is height, main-axis is width, so alignSelf=stretch
                            // can't be used to let the parent dictate the image's width.
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Spec: "Render as a small rectangle (e.g., 100x100dp)."
                            // Streaming-friendly placeholder: near-zero footprint until the
                            // real resolution arrives. We can't use 0×0 because the native
                            // ImageView skips decoding on empty bounds, so loadResolution
                            // would never fire and the image would stay invisible. Use a 1dp
                            // seed to guarantee the decoder runs; the applier then rewrites
                            // width/height from loadResolution via __featureAutoSizeMax*Dp.
                            props["width"] = 1
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 100
                            props["__featureAutoSizeMaxHeightDp"] = 100
                        }
                    }
                    "mediumFeature" -> {
                        if (parentIsRow) {
                            // In a Row, use fixed width since alignSelf=stretch only affects cross-axis
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Sizing rule (spec):
                            //   width  = min(image.intrinsicWidth, 300dp)  — fixed cap
                            //   height = width * (imgH / imgW), but capped at 200dp
                            //            (excess is CROPPED via cover, not letter-boxed).
                            //   The image is centered horizontally in the parent when the
                            //   parent's content width is greater than the image width.
                            //
                            // Streaming-friendly placeholder: height=1dp means the image
                            // reserves almost NO vertical space until its real resolution
                            // arrives, avoiding the "big placeholder → smaller real image"
                            // upward collapse. We MUST keep height > 0 because the native
                            // ImageView skips image decoding when its bounds have zero
                            // height, which would prevent loadResolution from ever firing.
                            // The applier wires a loadResolution callback via the
                            // __featureAutoSizeMax(Width|Height)Dp markers that rewrites
                            // width/height/alignSelf once the intrinsic size is known.
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                    "largeFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 150
                            props["height"] = 150
                        } else {
                            // Same auto-size mechanism as mediumFeature but with:
                            //   - NO width cap: image renders at its intrinsic width.
                            //   - Taller max height (400dp).
                            //   - No horizontal centering (alignSelf=AUTO after load),
                            //     since without a width cap the image just occupies its
                            //     own natural width at the leading edge.
                            // We intentionally do NOT emit __featureAutoSizeMaxWidthDp
                            // here; the applier treats its absence as "no width cap +
                            // no centering".
                            // Streaming-friendly placeholder: height=1dp so almost no
                            // vertical space is reserved before the real resolution lands
                            // (prevents the "placeholder 400dp → real ~210dp" upward
                            // collapse). height MUST be > 0 — a 0-height ImageView won't
                            // decode the image and loadResolution will never fire.
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxHeightDp"] = 400
                        }
                    }
                    "header" -> {
                        // Banner style (spec): 100% width (respecting margins), max height
                        // 200dp, cover. alignSelf=stretch lets the image fill parent's
                        // available width (parent content width minus this image's own
                        // margins).
                        //
                        // Streaming-friendly placeholder: height=1dp (not 0 — see other
                        // variants for why 0 breaks image decoding), grown by a
                        // loadResolution callback that keeps alignSelf=stretch (banner
                        // stays full-width) and sets height = min(availableWidth *
                        // imgH/imgW, 200dp). The marker __featureAutoSizeHeader=true
                        // tells the applier to use the header sizing rule
                        // (stretch-preserving) instead of the default
                        // mediumFeature/largeFeature rule.
                        props["alignSelf"] = "stretch"
                        props["height"] = 1
                        props["resizeMode"] = "cover"
                        props["__featureAutoSizeHeader"] = true
                        props["__featureAutoSizeMaxHeightDp"] = 200
                    }
                    else -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Same behavior as mediumFeature (the default variant).
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                }
            }
            "Icon" -> {
                // All Icons share the same 24dp x 24dp footprint plus an 8dp margin.
                // Which prop family we emit here depends on the global routing decided by
                // A2UIComponentMapper.isIconRenderedAsImage() (backed by A2UIIconRegistry):
                // - image mode  → KRImageView layout props (size + resizeMode)
                // - text mode   → KRRichTextView typography props (fontSize + textAlign)
                // The `src` / `text` prop itself is produced by mapIconProp in the
                // generic prop loop; here we only supply layout defaults.
                props["width"] = 24
                props["margin"] = 8
                if (A2UIComponentMapper.isIconRenderedAsImage()) {
                    // Match the default Icon footprint (24dp x 24dp) so registered images
                    // occupy the same slot as the unicode glyph they replace.
                    props["height"] = 24
                    props["resizeMode"] = "contain"
                } else {
                    // Per guide: Icon is a leaf component, 24dp width, no fixed height
                    // so text naturally sizes vertically for proper centering in Button
                    props["fontSize"] = 20f
                    props["textAlign"] = "center"
                }
            }
            "Text" -> {
                // Per guide: Text is a leaf component, carries 8dp margin
                props["margin"] = 8
                // Per A2UI catalog v0.9: Text.variant defaults to "body" (16dp base font,
                // normal weight, standard foreground color). When the JSON omits the
                // `variant` field we must still apply body's visual defaults, otherwise
                // the native Text falls back to platform default sizing which does not
                // match the protocol contract. If a variant IS provided, the __variant
                // expansion further below (mapProp result) will overwrite these values.
                if (compMap["variant"] == null) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize("body")
                    props["color"] = A2UIComponentMapper.getVariantColor("body")
                }
                // If parent is a Button, inherit button's text color
                if (id != null) {
                    val parentId = childToParentCache[id]
                    if (parentId != null) {
                        val parentComp = idToTag[parentId]?.let { tagToComponentName[it] }
                        if (parentComp == "Button") {
                            // Per guide section 4: text inside primary button should contrast
                            val buttonVariant = getParentButtonVariant(parentId)
                            if (buttonVariant == "primary") {
                                props["color"] = "#FFFFFF"
                            }
                            // Text inside button doesn't need its own margin
                            props["margin"] = 0
                        } else if (parentComp == "Row") {
                            // Inside a horizontal container, Text has no width constraint
                            // from the engine (parentMaxWidth is undefined for Row children).
                            // When flexWrap=wrap causes the Text to move to a new line, its
                            // natural width may exceed the Row's width, causing clipping.
                            // Mark for post-layout correction: set maxWidth = Row's width
                            // so the text wraps within bounds.
                            //
                            // Pre-set alignSelf=stretch here so that the applier's post-
                            // layout callback can flip it STRETCH → AUTO together with the
                            // maxWidth update. That alignSelf change is the shape-change
                            // trigger that actually forces the RichTextView to re-measure
                            // with the new maxWidth — otherwise the Kuikly engine would
                            // keep the previously measured (over-wide) natural width and
                            // the text would still clip at the Row's right edge. Row's
                            // crossAxis is vertical, so alignSelf=stretch on a Text inside
                            // a Row has no visual side effect (Row height is driven by its
                            // children, so there is nothing to stretch *to*).
                            props["__textInRowPostLayout"] = "true"
                            props["alignSelf"] = "stretch"
                        } else if (parentComp == "Column" || parentComp == "Card") {
                            // Inside a vertical container, stretch the text so the Flexbox
                            // engine's Loop A pre-fill provides a width constraint via
                            // node.getLayoutDimension(crossAxis) — this is critical when
                            // parentMaxWidth is undefined (Row → flex Column chain).
                            //
                            // NOTE: For parent Column with align=center/end, the end-of-
                            // function logic keeps this stretch but adds a
                            // __textAlignPostLayout hint. The applier uses a post-layout
                            // callback to set maxWidth and remove stretch, enabling
                            // alignItems to position the text correctly on re-layout.
                            props["alignSelf"] = "stretch"
                        }
                    }
                }
            }
            "Video" -> {
                // Per guide: Video is a leaf, spans full width, carries 8dp margin
                props["alignSelf"] = "stretch"
                props["height"] = 200
                props["margin"] = 8
            }
            "AudioPlayer" -> {
                // Per guide: AudioPlayer is a leaf, spans full width, carries 8dp margin.
                // Height accommodates the native transport bar (play/pause + scrubber + time).
                props["alignSelf"] = "stretch"
                props["height"] = 64
                props["margin"] = 8
            }
            "TextField" -> {
                // Per guide: TextField is a visually outlined container, carries 8dp margin
                props["alignSelf"] = "stretch"
                props["margin"] = 8
                val variant = compMap["variant"] as? String ?: "shortText"
                if (variant == "longText") {
                    // Multi-line text area: use minHeight instead of fixed height
                    props["minHeight"] = 88
                } else {
                    // Single-line input: fixed height
                    props["height"] = 44
                }
                props["border"] = normalizeHexInShorthand("1 solid #CCCCCC")
                props["borderRadius"] = "4"
                props["padding"] = 8
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = readPathKey(valueSpec)
                    if (path != null) {
                        props["__textFieldBindingPath"] = path.removePrefix("/")
                    }
                } else if (compMap["validationRegexp"] != null && id != null) {
                    // validationRegexp requires a binding path to trigger re-validation
                    // on text change. Create an internal binding path if none exists.
                    val internalPath = "__vr_$id"
                    props["__textFieldBindingPath"] = internalPath
                    // Initialize the internal data model entry
                    planInitialValue(internalPath, valueSpec?.toString() ?: "")
                }
            }
            "CheckBox" -> {
                // Per A2UI protocol: CheckBox is a leaf interactive component
                // CheckBoxView requires explicit width and height (checked in didInit())
                props["width"] = 24
                props["height"] = 24
                props["margin"] = 8
                // Pass the current theme primaryColor so the applier can tint the checked glyph.
                // Aligned with Button variant=primary / Slider progressColor / ChoicePicker selection.
                props["__primaryColor"] = primaryColor
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = readPathKey(valueSpec)
                    if (path != null) {
                        props["__checkBoxBindingPath"] = path.removePrefix("/")
                    }
                }
            }
            "ChoicePicker" -> {
                // Per guide: ChoicePicker is a visually outlined container, carries 8dp margin
                val displayStyle = compMap["displayStyle"] as? String ?: "chips"
                props["margin"] = 8
                // Fill the available width so the container has a defined main-axis size.
                props["alignSelf"] = "stretch"

                if (displayStyle == "checkbox") {
                    // Checkbox display: vertical list of rows, each row has icon + label
                    props["flexDirection"] = "column"
                    props["alignItems"] = "stretch"
                } else {
                    // Chips display (default): wrapping Row of pill-shaped chips
                    props["flexDirection"] = "row"
                    props["flexWrap"] = "wrap"
                    // Use flex-start (not center) on the cross axis. With flexWrap the row's
                    // cross size (height) is content-driven/undefined while lines are laid out,
                    // and the center calculation depends on that height; it mis-positions the
                    // wrapped lines so they overlap. flex-start stacks the lines cleanly.
                    props["alignItems"] = "flex-start"
                    props["alignContent"] = "flex-start"
                }

                // Extract binding path for two-way binding, or direct initial selection
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = readPathKey(valueSpec)
                    if (path != null) {
                        props["__choicePickerBindingPath"] = path.removePrefix("/")
                    }
                } else if (valueSpec is List<*>) {
                    // Direct array value: pass as initial selection
                    props["__choicePickerDirectValue"] = valueSpec
                } else if (valueSpec is String) {
                    props["__choicePickerDirectValue"] = listOf(valueSpec)
                }

                // Store options, variant, displayStyle, filterable for chip/checkbox generation
                val options = compMap["options"] as? List<*>
                val variant = compMap["variant"] as? String ?: "mutuallyExclusive"
                props["__choicePickerOptions"] = options
                props["__choicePickerVariant"] = variant
                props["__choicePickerDisplayStyle"] = displayStyle
                props["__choicePickerFilterable"] = compMap["filterable"] == true
                // Top-level `label` is rendered as a persistent Text ABOVE the options
                // (resolved + rendered during expansion in buildComponentTree).
                compMap["label"]?.let { props["__choicePickerLabel"] = it }
            }
            "Slider" -> {
                // Per guide: Slider is a leaf component, carries 8dp margin
                // Per A2UI protocol: Slider should span full width of parent (like Video/AudioPlayer)
                // No explicit width - alignSelf=stretch handles it in Column context.
                // The a2ui [A2UISliderView] wrapper observes its own laid-out size via
                // layoutFrameDidChanged and only mounts the inner kuikly SliderView once the
                // real width/height are known, so no bootstrap width is needed here.
                props["alignSelf"] = "stretch"
                props["height"] = 40
                props["margin"] = 8
                // Set default progressColor to theme primary color (overridable by explicit prop)
                if (!compMap.containsKey("progressColor")) {
                    props["progressColor"] = primaryColor
                }
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = readPathKey(valueSpec)
                    if (path != null) {
                        props["__sliderBindingPath"] = path.removePrefix("/")
                    }
                }
                // Note: A2UIValue.PathBinding is handled via resolveValue in the generic prop loop
                // Pass min/max directly for normalization
                compMap["max"]?.let { props["max"] = it }
                compMap["min"]?.let { props["min"] = it }
            }
            "DateTimeInput" -> {
                // Per guide: DateTimeInput is a visually outlined container, carries 8dp margin
                // DateTimeInputView is a VirtualView that handles its own rendering
                props["margin"] = 8
                props["alignSelf"] = "stretch"
                // Pass enableDate/enableTime/label to the view
                compMap["enableDate"]?.let { props["enableDate"] = it }
                compMap["enableTime"]?.let { props["enableTime"] = it }
                compMap["label"]?.let { props["label"] = it }
                // Pass min/max constraints to the view
                compMap["min"]?.let { props["min"] = it }
                compMap["max"]?.let { props["max"] = it }
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = readPathKey(valueSpec)
                    if (path != null) {
                        props["__dateTimeBindingPath"] = path.removePrefix("/")
                    }
                }
            }
            "Tabs" -> {
                // Tabs: invisible container with tab headers
                props["flexDirection"] = "column"
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
            }
            "Modal" -> {
                // Modal: acts as a trigger entry point
                props["flexDirection"] = "column"
            }
        }

        // Process component-specific properties
            val skipKeys = bindingSkipKeys()
        for ((key, value) in compMap) {
            if (key in skipKeys) continue

            // Kuikly `styles` extension (kuikly_a2ui_catalog.json):
            // expand the styles map into individual whitelisted Kuikly props.
            // Placed after semantic defaults (applyDefaultLayoutProps and the
            // per-component blocks above) so that user-specified styles win.
            if (key == "styles" && value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                for ((sk, sv) in A2UIComponentMapper.expandStyles(componentType, value as Map<String, Any?>)) {
                    props[sk] = resolveValue(sv, id)
                }
                clearDefaultsIfGradient(props)
                continue
            }

            val mapped = A2UIComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                if (propKey == "__variant" && propValue is String) {
                    // Expand variant into multiple props
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize(propValue)
                    val fontWeight = A2UIComponentMapper.getVariantFontWeight(propValue)
                    if (fontWeight != null) {
                        props["fontWeight"] = fontWeight
                    }
                    props["color"] = A2UIComponentMapper.getVariantColor(propValue)
                } else if (propKey.isNotEmpty()) {
                    val resolved = resolveValue(propValue, id)
                    // For Icon component with data-bound name (PathBinding),
                    // map the resolved name to unicode char.
                    // Static icon names are already mapped by mapIconProp (wrapped in Literal).
                    if (componentType == "Icon" && propKey == "text" && propValue is A2UIValue.PathBinding && resolved is String && resolved.isNotEmpty()) {
                        props[propKey] = A2UIComponentMapper.resolveIconNameToChar(resolved)
                    } else {
                        props[propKey] = resolved
                    }
                }
            }
        }

        // Store original definition for re-evaluation on data model updates
        if (id != null) {
            planDefinition(id, compMap)
        }

        // Extract and store action definition, set touchEnable for clickable components
        val action = compMap["action"]
        if (action != null && id != null) {
            val tag = idToTag[id]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                planAction(tag, action)
                props["touchEnable"] = 1
            }
        }

        // Register and evaluate checks for this component
        val checks = readChecksKey(compMap)
        if (checks != null && id != null) {
            registerChecks(id, checks)
            // PR-5: ValidationResult -> String? for downstream prop storage.
            val errorMessage = evaluateChecksForComponent(id).errorMessage
            if (componentType == "Button") {
                // Button: disabled when any check fails. We make the disabled state
                // effective on every render path: (1) tagToActionDisabled guards the
                // dispatch, (2) touchEnable=0 makes the native view truly inert (no
                // tap/ripple), (3) opacity dims it. The message is stashed so
                // buildComponentTree can render an inline hint below the button.
                val disabled = errorMessage != null
                idToTag[id]?.let { planDisabled(it, disabled) }
                props["opacity"] = if (disabled) 0.4f else 1.0f
                props["touchEnable"] = if (disabled) 0 else 1
                props["__checkError"] = errorMessage
            } else {
                // TextField/CheckBox: store error for wrapper error text component
                // (handled by buildComponentTree which creates a separate error text view)
                props["__checkError"] = errorMessage
                // Set border color based on check result
                if (errorMessage != null) {
                    props["border"] = normalizeHexInShorthand("1 solid #D32F2F")
                }
            }
        }

        // Handle validationRegexp for TextField (legacy validation, converted to checks)
        val validationRegexp = compMap["validationRegexp"] as? String
        if (validationRegexp != null && id != null && componentType == "TextField" && checks == null) {
            // Determine the binding path for the regex check's value reference
            val valueSpec = compMap["value"]
            val bindingPath = if (valueSpec is Map<*, *>) {
                (readPathKey(valueSpec) ?: "/__vr_$id")
            } else {
                "/__vr_$id"
            }
            // Convert validationRegexp to an equivalent regex check
            val syntheticChecks = listOf(
                mapOf(
                    "condition" to mapOf(
                        "call" to "regex",
                        "args" to mapOf(
                            "value" to mapOf("path" to bindingPath),
                            "pattern" to validationRegexp
                        ),
                        "returnType" to "boolean"
                    ),
                    "message" to "Input does not match the required format"
                )
            )
            registerChecks(id, syntheticChecks)
            // PR-5: ValidationResult -> String? for downstream prop storage.
            val errorMessage = evaluateChecksForComponent(id).errorMessage
            // TextField: store error for wrapper error text component
            props["__checkError"] = errorMessage
            // Set border color based on check result
            if (errorMessage != null) {
                props["border"] = normalizeHexInShorthand("1 solid #D32F2F")
            }
        }

        // Handle "weight" property (flex-grow in Row/Column children)
        val weight = compMap["weight"]
        if (weight != null) {
            props["flex"] = weight
        }

        // Parent-align-aware alignSelf override.
        //
        // Many leaf/container branches above set `alignSelf=stretch` so the child fills
        // the parent's cross axis (needed for Text re-measure/wrap, Image banner width,
        // Divider full-width, TextField/Slider/etc. full-width look).
        //
        // However, A2UI's Row/Column `align` (start/center/end) is expressed on the
        // parent as `alignItems`, and in Flexbox a child's `alignSelf` OVERRIDES the
        // parent's `alignItems`. So an unconditional `alignSelf=stretch` on the child
        // silently defeats `align:center/end/start` — every case renders as a stretched
        // block anchored at the cross-axis start, regardless of the parent's align.
        //
        // Fix: when the parent has an explicit non-stretch `align`, DROP the child's
        // `alignSelf=stretch` so the parent's `alignItems` can position the child's box
        // by its content size on the cross axis. The `stretch` semantics are preserved
        // when the parent's align is absent or explicitly "stretch" (the protocol
        // default), which is where the wrap/full-cross-axis behaviors matter.
        //
        // Cross-axis mapping (Yoga):
        //  - Parent Column (main=vertical)  → cross axis is horizontal → `width`  is the explicit cross-axis size
        //  - Parent Row    (main=horizontal) → cross axis is vertical   → `height` is the explicit cross-axis size
        //
        // Text handling asymmetry:
        //  - In a Column parent with center/end align, Text needs first-pass stretch so
        //    it can wrap correctly (parentMaxWidth is often undefined), then a
        //    post-layout correction (`__textAlignPostLayout`) fixes horizontal alignment.
        //  - In a Row parent, cross-axis alignment (vertical) is handled directly by
        //    Yoga's alignItems on a content-sized cross axis. No post-layout hint
        //    is needed; simply keep stretch (Row Text alignSelf=stretch is compatible
        //    with alignItems=center because Text height is content-driven).
        if (id != null) {
            val parentId = childToParentCache[id]
            val parentDef = if (parentId != null) idToOriginalDef[parentId] else null
            val parentComp = parentDef?.get("component") as? String
            if (parentComp == "Column" || parentComp == "Row") {
                val parentAlign = parentDef["align"] as? String
                if (parentAlign == "start" || parentAlign == "center" || parentAlign == "end") {
                    // Explicit cross-axis size key differs by parent orientation.
                    val explicitCrossSizeKey = if (parentComp == "Column") "width" else "height"
                    val removeStretch = when {
                        // Text is special-cased FIRST: even though a Text may carry an
                        // explicit width (unusual but legal), the Column-parent case
                        // needs the post-layout hint. Handling Text here avoids the
                        // explicit-size branch below stealing this case.
                        componentType == "Text" -> {
                            // Only Column parent needs post-layout correction for horizontal
                            // alignment. In a Row parent, Text keeps stretch and Yoga's
                            // alignItems handles vertical positioning cleanly.
                            if (parentComp == "Column" &&
                                (parentAlign == "center" || parentAlign == "end")
                            ) {
                                props["__textAlignPostLayout"] = parentAlign
                            }
                            false  // keep stretch
                        }
                        // Explicit cross-axis size present → user pinned the cross-axis
                        // dimension, stretch is redundant and would defeat alignItems.
                        // This branch INTENTIONALLY precedes the Row/Column container
                        // branch below so that decorative Row/Column nodes with explicit
                        // cross-axis size (e.g. an 8×8 dot Column, a fixed-height Row
                        // badge) get their stretch removed and the parent's alignItems
                        // can position them.
                        props.containsKey(explicitCrossSizeKey) -> true
                        // Row/Column containers WITHOUT explicit cross-axis size are
                        // content-driven nested layouts and need stretch for internal
                        // re-measure / child stretch propagation.
                        componentType == "Row" || componentType == "Column" -> false
                        // Button/Icon are content-sized on both axes; safe to remove.
                        componentType == "Button" || componentType == "Icon" -> true
                        // Slider/Image/Video/etc. depend on stretch for their main-axis
                        // width in Column context. In Row context this branch rarely
                        // fires (those components aren't typical Row siblings), but
                        // keeping stretch is the conservative default either way.
                        else -> false
                    }
                    if (removeStretch && props["alignSelf"] == "stretch") {
                        props.remove("alignSelf")
                    }
                }
            }
        }

        // PR-9: resolve `@accessibility` sub-map and expose its fields as
        // __accessibility* props for the Applier to forward to native.
        // No-op when the component has no accessibility metadata.
        injectAccessibilityProps(compMap, props)

        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Resolve a component's `label` (DynamicString) into a display string.
     * Supports literal strings and data-binding maps ({"path": "/key"}).
     */
internal fun A2UIComponentProjection.resolveLabelString(labelSpec: Any?, componentId: String? = null): String {
        return when (labelSpec) {
            null -> ""
            is String -> labelSpec
            is Map<*, *> -> resolveValue(A2UIComponentMapper.resolveTextValue(labelSpec), componentId)?.toString() ?: ""
            else -> labelSpec.toString()
        }
    }
internal fun A2UIComponentProjection.computeScopedProperties(
        componentType: String,
        compMap: Map<String, Any?>,
        instanceId: String,
        itemData: Map<String, Any?>,
        scopePath: String
    ): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()
        val id = compMap["id"] as? String

        // Apply component-specific default layout props (same as buildProps)
        applyDefaultLayoutProps(componentType, props, instanceId)

        // Process component-specific properties with scope-aware resolution
            val skipKeys = bindingSkipKeys()
        for ((key, value) in compMap) {
            if (key in skipKeys) continue

            // Kuikly `styles` extension: expand per-scope so binding values inside
            // styles (e.g. {"color": {"path": "textColor"}}) resolve against itemData.
            // We walk the original styles map (`value`) directly, filter via the
            // catalog whitelist, and hand each raw sub-value to resolveValueWithScope
            // so both absolute `/…` paths and relative item-scoped paths work.
            if (key == "styles" && value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val stylesMap = value as Map<String, Any?>
                for ((sk, _) in A2UIComponentMapper.expandStyles(componentType, stylesMap)) {
                    val rawSub = stylesMap[sk]
                    props[sk] = resolveValueWithScope(rawSub, itemData, instanceId)
                }
                clearDefaultsIfGradient(props)
                continue
            }

            val mapped = A2UIComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                if (propKey == "__variant" && propValue is String) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize(propValue)
                    val fontWeight = A2UIComponentMapper.getVariantFontWeight(propValue)
                    if (fontWeight != null) {
                        props["fontWeight"] = fontWeight
                    }
                    props["color"] = A2UIComponentMapper.getVariantColor(propValue)
                } else if (propKey.isNotEmpty()) {
                    // For data bindings, resolve using the ORIGINAL value (before mapProp processing)
                    // because mapProp's resolveTextValue may have extracted the path string,
                    // losing the Map structure needed for scope-aware resolution.
                    val resolved = resolveValueWithScope(value, itemData, instanceId)
                    // For Icon component, map resolved name to unicode char
                    if (componentType == "Icon" && propKey == "text" && resolved is String && resolved.isNotEmpty()) {
                        props[propKey] = A2UIComponentMapper.resolveIconNameToChar(resolved)
                    } else {
                        props[propKey] = resolved
                    }
                }
            }
        }

        // Handle action
        val action = compMap["action"]
        if (action != null) {
            val tag = idToTag[instanceId]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                planAction(tag, action)
                props["touchEnable"] = 1
            }
        }

        // Handle weight
        val weight = compMap["weight"]
        if (weight != null) {
            props["flex"] = weight
        }

        // Parent-align-aware alignSelf override (template path). See the matching block
        // in buildProps for the rationale and cross-axis mapping. Handles both Column
        // (cross=width) and Row (cross=height) parents symmetrically.
        val parentIdForAlign = getOriginalParentId(instanceId)
        val parentDefForAlign = if (parentIdForAlign != null) idToOriginalDef[parentIdForAlign] else null
        val parentCompForAlign = parentDefForAlign?.get("component") as? String
        if (parentCompForAlign == "Column" || parentCompForAlign == "Row") {
            val parentAlign = parentDefForAlign["align"] as? String
            if (parentAlign == "start" || parentAlign == "center" || parentAlign == "end") {
                val originalId = getOriginalComponentId(instanceId)
                val originalComp = originalId?.let { idToOriginalDef[it]?.get("component") as? String }
                val explicitCrossSizeKey = if (parentCompForAlign == "Column") "width" else "height"
                val removeStretch = when {
                    // Text special-cased first (see buildProps for rationale).
                    originalComp == "Text" -> {
                        if (parentCompForAlign == "Column" &&
                            (parentAlign == "center" || parentAlign == "end")
                        ) {
                            props["__textAlignPostLayout"] = parentAlign
                        }
                        false  // keep stretch
                    }
                    // Explicit cross-axis size wins over Row/Column container heuristic
                    // (see buildProps for the decorative-Row/Column rationale).
                    props.containsKey(explicitCrossSizeKey) -> true
                    originalComp == "Row" || originalComp == "Column" -> false
                    originalComp == "Button" || originalComp == "Icon" -> true
                    else -> false
                }
                if (removeStretch && props["alignSelf"] == "stretch") {
                    props.remove("alignSelf")
                }
            }
        }

        normalizeColorFieldsInPlace(props)
        return props
    }
internal fun A2UIComponentProjection.applyDefaultLayoutProps(componentType: String, props: MutableMap<String, Any?>, instanceId: String) {
        when (componentType) {
            "Row" -> {
                props["flexDirection"] = "row"
                props["alignSelf"] = "stretch"
                // Mirror buildProps: auto-wrap overflowing children onto new lines.
                props["flexWrap"] = "wrap"
                props["alignContent"] = "flex-start"
            }
            "Column" -> {
                props["flexDirection"] = "column"
                props["alignSelf"] = "stretch"
                // Per catalog: default align is \"stretch\" — explicit for template path too.
                // The subsequent mapProp loop in buildPropsWithScope will overwrite this if
                // the original definition declares `align`.
                val originalId = getOriginalComponentId(instanceId)
                val originalHasAlign = idToOriginalDef[originalId]?.containsKey("align") == true
                if (!originalHasAlign) {
                    props["alignItems"] = "stretch"
                }                // When Column is inside a Row, flex to fill remaining space
                val parentIsRow = isParentRow(instanceId)
                if (parentIsRow) {
                    props["flex"] = 1
                }
            }
            "Icon" -> {
                // Mirror buildProps("Icon"): leaf component, 24dp width, 8dp margin.
                // The image-vs-text branch is a global routing decision that never changes
                // once the host app has finished registering icons, so we consult the same
                // helper the initial-create path uses — no per-instance state needed.
                props["width"] = 24
                props["margin"] = 8
                if (A2UIComponentMapper.isIconRenderedAsImage()) {
                    props["height"] = 24
                    props["resizeMode"] = "contain"
                } else {
                    props["fontSize"] = 20f
                    props["textAlign"] = "center"
                }
            }
            "Text" -> {
                props["margin"] = 8
                // Mirror buildProps: apply body-variant defaults when the source JSON
                // did not specify a variant, so template-instantiated Text nodes match
                // the A2UI catalog spec (variant="body" is the documented default).
                val originalId = getOriginalComponentId(instanceId)
                val originalVariant = originalId?.let { idToOriginalDef[it]?.get("variant") as? String }
                if (originalVariant == null) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize("body")
                    props["color"] = A2UIComponentMapper.getVariantColor("body")
                }
                // Check if parent is a Button
                val parentComp = getParentComponentType(instanceId)
                if (parentComp == "Button") {
                    val parentId = childToParentCache[instanceId] ?: getOriginalParentId(instanceId)
                    if (parentId != null) {
                        val buttonVariant = getParentButtonVariant(parentId)
                        if (buttonVariant == "primary") {
                            props["color"] = "#FFFFFF"
                        }
                    }
                    props["margin"] = 0
                } else if (parentComp == "Row") {
                    // Mirror buildProps: Text in Row needs post-layout maxWidth fix.
                    // See adapter line ~2536 for why alignSelf=stretch is pre-set
                    // alongside __textInRowPostLayout (needed as a shape-change
                    // trigger so the applier's STRETCH → AUTO flip forces the
                    // RichTextView to re-measure with the new maxWidth).
                    props["__textInRowPostLayout"] = "true"
                    props["alignSelf"] = "stretch"
                } else if (parentComp == "Column" || parentComp == "Card") {
                    // Mirror buildProps: stretch text so the engine's Loop A pre-fill
                    // provides a width constraint via node.getLayoutDimension(crossAxis).
                    // For parent Column with align=center/end, the end-of-function logic
                    // adds __textAlignPostLayout hint; the applier fixes alignment post-layout.
                    props["alignSelf"] = "stretch"
                }
            }
            "Image" -> {
                props["margin"] = 8
                // Look up variant from original component definition
                val originalId = getOriginalComponentId(instanceId)
                val originalDef = idToOriginalDef[originalId]
                val variant = originalDef?.get("variant") as? String ?: "mediumFeature"
                val parentIsRow = isParentRow(instanceId)

                when (variant) {
                    "icon" -> {
                        props["width"] = 24
                        props["height"] = 24
                        props["margin"] = 0
                    }
                    "avatar" -> {
                        props["width"] = 40
                        props["height"] = 40
                        props["borderRadius"] = "20"
                    }
                    "smallFeature" -> {
                        if (parentIsRow) {
                            // In a Row, keep the legacy fixed 100×100 slot.
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Mirror buildProps("Image", smallFeature, non-Row):
                            // 1dp seed placeholder (not 0 — 0 bounds suppresses image
                            // decoding, loadResolution would never fire). Applier grows
                            // into a 100×100dp box via __featureAutoSizeMax(Width|Height)Dp.
                            props["width"] = 1
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 100
                            props["__featureAutoSizeMaxHeightDp"] = 100
                        }
                    }
                    "mediumFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Mirror buildProps("Image", mediumFeature, non-Row):
                            // 1dp-height placeholder (not 0 — 0 bounds suppresses image
                            // decoding, loadResolution would never fire); A2UIApplier
                            // refines via loadResolution (see __featureAutoSizeMax*Dp).
                            // 1dp avoids the "big placeholder → smaller real image"
                            // layout collapse.
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                    "largeFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 150
                            props["height"] = 150
                        } else {
                            // Mirror buildProps("Image", largeFeature, non-Row):
                            // no width cap (image renders at intrinsic width, leading-
                            // aligned), max height 400dp. 1dp-height placeholder (not 0 —
                            // 0 bounds suppresses image decoding) avoids the
                            // "placeholder 400dp → real ~210dp" collapse.
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxHeightDp"] = 400
                        }
                    }
                    "header" -> {
                        // Banner style: 100% width (respecting margins), max height 200dp,
                        // cover. 1dp-height placeholder (not 0 — 0 bounds suppresses
                        // image decoding); applier grows height via the header-specific
                        // loadResolution path (keeps alignSelf=stretch).
                        props["alignSelf"] = "stretch"
                        props["height"] = 1
                        props["resizeMode"] = "cover"
                        props["__featureAutoSizeHeader"] = true
                        props["__featureAutoSizeMaxHeightDp"] = 200
                    }
                    else -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Same as mediumFeature (default variant).
                            props["alignSelf"] = "stretch"
                            props["height"] = 1
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                }
            }
            "Divider" -> {
                // Mirror buildProps("Divider"): vertical divider uses an explicit height
                // fallback (40dp) rather than alignSelf=stretch, because Kuikly's flex
                // engine cannot stretch a NaN cross-axis dim when the parent Row's height
                // is content-driven. The applier layer will resize this divider to match
                // the parent Row's actual height after layout via the marker prop.
                val originalId = getOriginalComponentId(instanceId)
                val axis = idToOriginalDef[originalId]?.get("axis") as? String ?: "horizontal"
                props["backgroundColor"] = "#E0E0E0"
                if (axis == "horizontal") {
                    props["height"] = 1
                    props["alignSelf"] = "stretch"
                    props["marginTop"] = 8
                    props["marginBottom"] = 8
                } else {
                    props["width"] = 1
                    props["height"] = 40
                    props["marginLeft"] = 8
                    props["marginRight"] = 8
                    props["__verticalDividerFollowParent"] = true
                }
            }
            "CheckBox" -> {
                // Mirror buildProps("CheckBox"): Kuikly's CheckBoxView.didInit() throws a
                // fatal RuntimeException when width/height are unset, so template instances
                // need the same fixed 24x24 size as directly declared CheckBoxes.
                props["width"] = 24
                props["height"] = 24
                props["margin"] = 8
                props["__primaryColor"] = primaryColor
            }
            "Slider" -> {
                // Mirror buildProps("Slider"): the A2UISliderView wrapper needs a height
                // (and alignSelf=stretch for full-width) to lay out correctly before it
                // mounts the inner kuikly SliderView.
                props["alignSelf"] = "stretch"
                props["height"] = 40
                props["margin"] = 8
                props["progressColor"] = primaryColor
            }
        }
    }
