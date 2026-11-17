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

package com.tencent.kuikly.a2ui.dsl

import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.layout.FlexPositionType
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.ImageView
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.TextAreaView
import com.tencent.kuikly.core.views.RichTextView
import com.tencent.kuikly.core.views.AudioView
import com.tencent.kuikly.core.views.BlurView
import com.tencent.kuikly.core.views.HoverView
import com.tencent.kuikly.core.views.PAGView
import com.tencent.kuikly.core.views.APNGVView
import com.tencent.kuikly.core.views.ActivityIndicatorView
import com.tencent.kuikly.core.views.ScrollerView
import com.tencent.kuikly.core.views.VideoView
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.a2ui.views.DateTimeInputView

import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toFloat
import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toColor

@Suppress("DEPRECATION")
internal class A2UIPropertyPipeline(private val text: A2UITextRenderer, private val controls: A2UIControlRenderer, private val media: A2UIMediaRenderer, private val bindings: A2UIEventBindings) {
    /** Keys already handled by applyCommonProps (layout/style/container props) - not forwarded to native for custom components */
    private val COMMON_HANDLED_KEYS = setOf(
        // Size
        "width", "height", "minWidth", "minHeight", "maxWidth", "maxHeight",
        // Flex
        "flex", "flexGrow", "flexShrink", "flexBasis",
        "flexDirection", "justifyContent", "alignItems", "alignContent", "flexWrap",
        // Margin
        "margin", "marginTop", "marginBottom", "marginLeft", "marginRight",
        // Padding
        "padding", "paddingTop", "paddingBottom", "paddingLeft", "paddingRight",
        // Visual
        "backgroundColor", "backgroundLinearGradient",
        "borderRadius",
        "opacity", "overflow", "visibility", "zIndex", "transform",
        "border", "borderWidth", "borderColor", "borderStyle",
        "boxShadow",
        // Position
        "positionType", "top", "left", "bottom", "right",
        // Alignment
        "alignSelf",
        // Touch
        "touchEnable",
        // Accessibility
        "accessibility"
    )

    fun apply(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        applyCommonProps(view, props)
        when (view) {
            is RichTextView -> text.apply(view, props)
            is InputView -> controls.applyTextFieldProps(view, props)
            is TextAreaView -> controls.applyTextAreaProps(view, props)
            is ImageView -> media.applyImageProps(view, props)
            is VideoView -> media.applyVideoProps(view, props)
            is AudioView -> media.applyAudioProps(view, props)
            is A2UISliderView -> controls.applySliderProps(view, props)
            is CheckBoxView -> controls.applyCheckBoxProps(view, props)
            is DateTimeInputView -> controls.applyDateTimeInputProps(view, props)
            is BlurView -> applyBlurProps(view, props)
            is HoverView -> applyHoverProps(view, props)
            is ActivityIndicatorView -> applyActivityIndicatorProps(view, props)
            is PAGView -> media.applyPAGProps(view, props)
            is APNGVView -> media.applyAPNGProps(view, props)
            is ScrollerView<*, *> -> applyScrollerProps(view, props)
            else -> {
                // For custom components (DivView fallback), forward unknown props
                // to the native render view via setProp. This enables custom component
                // props like "source", "text", "textColor" to reach the native layer.
                applyCustomComponentProps(view, props)
            }
        }
        text.enqueueCorrections(view, props)
        bindings.applyComposite(view, props)
    }
    private fun applyCommonProps(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        view.attr {
            for ((key, value) in props) {
                if (value == null) continue
                when (key) {
                    // Size
                    "width" -> width(toFloat(value))
                    "height" -> height(toFloat(value))
                    "minWidth" -> minWidth(toFloat(value))
                    "minHeight" -> minHeight(toFloat(value))
                    "maxWidth" -> maxWidth(toFloat(value))
                    "maxHeight" -> maxHeight(toFloat(value))

                    // Flex layout
                    "flex" -> flex(toFloat(value))
                    "flexGrow" -> "flexGrow" with toFloat(value).toString()
                    "flexShrink" -> "flexShrink" with toFloat(value).toString()
                    "flexBasis" -> "flexBasis" with toFloat(value).toString()

                    // Margin
                    "margin" -> margin(toFloat(value))
                    "marginTop" -> marginTop(toFloat(value))
                    "marginBottom" -> marginBottom(toFloat(value))
                    "marginLeft" -> marginLeft(toFloat(value))
                    "marginRight" -> marginRight(toFloat(value))

                    // Visual
                    "backgroundColor" -> backgroundColor(toColor(value))
                    // NOTE: The Kuikly Core DSL exposes this as
                    // `backgroundLinearGradient(Direction, vararg ColorStop)`, but under
                    // the hood it stringifies to CSS and writes the prop with the key
                    // `backgroundImage` (see StyleConst.BACKGROUND_IMAGE = "backgroundImage").
                    // The KR render layer's when-branch only matches "backgroundImage"
                    // (KRCssConst.BACKGROUND_IMAGE) — writing "backgroundLinearGradient"
                    // silently drops the prop and no gradient ever renders. We keep
                    // "backgroundLinearGradient" as our public A2UI/style key (matches
                    // the KRCommonProps catalog surface) and translate here.
                    "backgroundLinearGradient" -> "backgroundImage" with value.toString()
                    "borderRadius" -> borderRadius(toFloat(value))
                    "opacity" -> opacity(toFloat(value))
                    "overflow" -> overflow(value.toString() == "hidden")
                    "visibility" -> {
                        val visible = when (value) {
                            is Boolean -> value
                            is Number -> value.toInt() != 0
                            is String -> value != "false" && value != "0" && value != "hidden"
                            else -> true
                        }
                        if (!visible) opacity(0f)
                    }
                    "zIndex" -> "zIndex" with value.toString()
                    "transform" -> "transform" with value.toString()

                    // Border (pass as raw string prop to native layer).
                    // Kuikly Core's border API only supports four-side uniform width/color;
                    // per-side borderTop*/borderBottom*/borderLeft*/borderRight* are NOT
                    // implemented in any KR render layer and are therefore not exposed.
                    "border" -> "border" with value.toString()
                    "borderWidth" -> "borderWidth" with value.toString()
                    "borderColor" -> "borderColor" with value.toString()
                    "borderStyle" -> "borderStyle" with value.toString()

                    // Box shadow (pass as raw string prop to native layer)
                    "boxShadow" -> "boxShadow" with value.toString()

                    // Accessibility
                    "accessibility" -> accessibility(value.toString())

                    // PR-9: `__accessibility` is the internal resolved form of a
                    // v1.0 `@accessibility.description` DynamicString (Adapter
                    // produces it via injectAccessibilityProps). On Image this
                    // key is consumed again in applyImageProps (see applyImageProps
                    // comment); on every OTHER component the common path owns it.
                    // Guard against empty strings (resolveValue returns "" for
                    // path bindings that haven't been resolved yet during initial
                    // paint) so we don't overwrite a prior label with "".
                    "__accessibility" ->
                        (value as? String)?.takeIf { it.isNotEmpty() }?.let { accessibility(it) }

                    // PR-9: v1.0 `@accessibility.live` (off / polite / assertive) and
                    // `@accessibility.hidden` (boolean) — injected by the Adapter's
                    // `injectAccessibilityProps` as resolved internal keys. Kuikly's
                    // common attr API does not surface accessibilityLiveRegion /
                    // accessibilityElementsHidden as first-class setters, so we
                    // forward them as generic native props. A host render view that
                    // knows how to consume them (e.g. android uses ViewCompat
                    // .setAccessibilityLiveRegion, iOS maps to
                    // accessibilityElementsHidden) picks them up via setProp.
                    // Hosts that don't consume them are unaffected.
                    "__accessibilityLive" -> value?.toString()?.lowercase()?.let { lv ->
                        if (lv == "off" || lv == "polite" || lv == "assertive") {
                            "accessibilityLive" with lv
                        }
                    }
                    "__accessibilityHidden" -> {
                        val hidden = when (value) {
                            is Boolean -> value
                            is Number -> value.toInt() != 0
                            is String -> value == "true" || value == "1"
                            else -> false
                        }
                        "accessibilityHidden" with hidden
                    }

                    // Position
                    "positionType" -> {
                        when (value.toString().lowercase()) {
                            "absolute" -> positionType(FlexPositionType.ABSOLUTE)
                            "relative" -> positionType(FlexPositionType.RELATIVE)
                        }
                    }
                    "top" -> top(toFloat(value))
                    "left" -> left(toFloat(value))
                    "bottom" -> bottom(toFloat(value))
                    "right" -> right(toFloat(value))

                    // Alignment
                    "alignSelf" -> {
                        when (value.toString().lowercase()) {
                            "center" -> alignSelf(FlexAlign.CENTER)
                            "flex-start", "flexstart" -> alignSelf(FlexAlign.FLEX_START)
                            "flex-end", "flexend" -> alignSelf(FlexAlign.FLEX_END)
                            "stretch" -> alignSelf(FlexAlign.STRETCH)
                            "auto" -> alignSelf(FlexAlign.AUTO)
                        }
                    }

                    // Touch
                    "touchEnable" -> {
                        val enable = when (value) {
                            is Number -> value.toInt() == 1
                            is Boolean -> value
                            is String -> value == "1" || value == "true"
                            else -> false
                        }
                        touchEnable(enable)
                    }

                    // Checks: Button disabled state
                    "__disabled" -> {
                        val disabled = when (value) {
                            is Boolean -> value
                            is Number -> value.toInt() != 0
                            is String -> value == "true"
                            else -> false
                        }
                        if (disabled) {
                            opacity(0.4f)
                            touchEnable(false)
                        } else {
                            opacity(1.0f)
                            touchEnable(true)
                        }
                    }
                }
            }
        }

        // Container-specific properties (padding, flexDirection, etc.)
        if (view is ViewContainer<*, *>) {
            view.attr {
                for ((key, value) in props) {
                    if (value == null) continue
                    when (key) {
                        // Padding
                        "padding" -> padding(toFloat(value))
                        "paddingTop" -> paddingTop(toFloat(value))
                        "paddingBottom" -> paddingBottom(toFloat(value))
                        "paddingLeft" -> paddingLeft(toFloat(value))
                        "paddingRight" -> paddingRight(toFloat(value))

                        // Flex container
                        "flexDirection" -> {
                            when (value.toString().lowercase()) {
                                "row" -> flexDirection(FlexDirection.ROW)
                                "column" -> flexDirection(FlexDirection.COLUMN)
                                "row-reverse", "rowreverse" -> flexDirection(FlexDirection.ROW_REVERSE)
                                "column-reverse", "columnreverse" -> flexDirection(FlexDirection.COLUMN_REVERSE)
                            }
                        }
                        "justifyContent" -> {
                            when (value.toString().lowercase()) {
                                "center" -> justifyContent(FlexJustifyContent.CENTER)
                                "flex-start", "flexstart" -> justifyContent(FlexJustifyContent.FLEX_START)
                                "flex-end", "flexend" -> justifyContent(FlexJustifyContent.FLEX_END)
                                "space-between", "spacebetween" -> justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                                "space-around", "spacearound" -> justifyContent(FlexJustifyContent.SPACE_AROUND)
                                "space-evenly", "spaceevenly" -> justifyContent(FlexJustifyContent.SPACE_EVENLY)
                            }
                        }
                        "alignItems" -> {
                            when (value.toString().lowercase()) {
                                "center" -> alignItems(FlexAlign.CENTER)
                                "flex-start", "flexstart" -> alignItems(FlexAlign.FLEX_START)
                                "flex-end", "flexend" -> alignItems(FlexAlign.FLEX_END)
                                "stretch" -> alignItems(FlexAlign.STRETCH)
                            }
                        }
                        "alignContent" -> {
                            when (value.toString().lowercase()) {
                                "center" -> alignContent(FlexAlign.CENTER)
                                "flex-start", "flexstart" -> alignContent(FlexAlign.FLEX_START)
                                "flex-end", "flexend" -> alignContent(FlexAlign.FLEX_END)
                                "stretch" -> alignContent(FlexAlign.STRETCH)
                            }
                        }
                        "flexWrap" -> {
                            when (value.toString().lowercase()) {
                                "wrap" -> flexWrap(FlexWrap.WRAP)
                                "nowrap" -> flexWrap(FlexWrap.NOWRAP)
                            }
                        }
                    }
                }
            }
        }
        bindings.applyGenericClick(view, props)
    }
    /**
     * Forward custom component props to the native render view via setProp.
     * Only forwards props that are NOT already handled by applyCommonProps (layout/style props).
     * This enables custom component-specific props (e.g., "source", "text", "textColor")
     * to reach the native IKuiklyRenderViewExport.setProp() method.
     */
    private fun applyCustomComponentProps(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        val customProps = props.filter { (key, value) ->
            value != null && !key.startsWith("__") && key !in COMMON_HANDLED_KEYS
        }
        if (customProps.isEmpty()) return
        view.attr {
            for ((key, value) in customProps) {
                key with value.toString()
            }
        }
    }
    /**
     * Apply blur-specific properties to a BlurView.
     */
    private fun applyBlurProps(view: BlurView, props: Map<String, Any?>) {
        view.attr {
            props["blurRadius"]?.let { blurRadius(toFloat(it)) }
        }
    }

    /**
     * Apply hover-specific properties to a HoverView.
     */
    private fun applyHoverProps(view: HoverView, props: Map<String, Any?>) {
        view.attr {
            props["bringIndex"]?.let { "bringIndex" with toFloat(it).toInt() }
            props["hoverMarginTop"]?.let { "hoverMarginTop" with toFloat(it) }
        }
    }

    /**
     * Apply activity-indicator-specific properties to an ActivityIndicatorView.
     */
    private fun applyActivityIndicatorProps(view: ActivityIndicatorView, props: Map<String, Any?>) {
        view.attr {
            props["isGrayStyle"]?.let {
                val isGray = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                if (isGray) "isGrayStyle" with "1"
            }
        }
    }
    /**
     * Apply scroller-specific properties to a ScrollerView (KRScrollView/KRListView).
     * Handles scroll behavior configuration like scrollEnable, bouncesEnable, etc.
     */
    private fun applyScrollerProps(view: ScrollerView<*, *>, props: Map<String, Any?>) {
        view.attr {
            props["scrollEnable"]?.let {
                val enable = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                scrollEnable(enable)
            }
            props["bouncesEnable"]?.let {
                val enable = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                bouncesEnable(enable)
            }
            props["showScrollerIndicator"]?.let {
                val show = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                showScrollerIndicator(show)
            }
            props["pagingEnable"]?.let {
                val enable = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                pagingEnable(enable)
            }
            props["flingEnable"]?.let {
                val enable = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                flingEnable(enable)
            }
            props["nestedScroll"]?.let { nested ->
                if (nested is Map<*, *>) {
                    // Pass nested scroll config as raw prop for native layer
                    "nestedScroll" with nested.toString()
                }
            }
        }
    }
}
