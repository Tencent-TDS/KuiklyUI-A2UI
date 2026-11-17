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

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.base.Size
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.layout.FlexPositionType
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.layout.StyleSpace
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.DivView
import com.tencent.kuikly.core.views.ImageView
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.TextAreaView
import com.tencent.kuikly.core.views.ModalView
import com.tencent.kuikly.core.views.ISpan
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.RichTextView
import com.tencent.kuikly.core.views.Span
import com.tencent.kuikly.core.views.AudioView
import com.tencent.kuikly.core.views.AudioPlayControl
import com.tencent.kuikly.core.views.BlurView
import com.tencent.kuikly.core.views.CanvasView
import com.tencent.kuikly.core.views.HoverView
import com.tencent.kuikly.core.views.PAGView
import com.tencent.kuikly.core.views.APNGVView
import com.tencent.kuikly.core.views.ActivityIndicatorView
import com.tencent.kuikly.core.views.ScrollerView
import com.tencent.kuikly.core.views.VideoView
import com.tencent.kuikly.core.views.VideoPlayControl
import com.tencent.kuikly.core.views.willDismiss
import com.tencent.kuikly.a2ui.views.A2UIScrollerView
import com.tencent.kuikly.a2ui.views.A2UICustomComponentView
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.a2ui.views.DateTimeInputView
import com.tencent.kuikly.a2ui.markdown.MarkdownParser

/**
 * Frozen pre-extraction reference for isolated Core replay tests; never used in production.
 * The algorithms below intentionally preserve the pre-extraction behavior.
 *
 * This operates on the Kuikly DSL component tree, letting Kuikly Core handle:
 * - FlexNode creation and layout calculation automatically
 * - RenderView creation and frame synchronization
 * - Text measurement via RichTextView's built-in shadow
 *
 * Usage:
 * ```kotlin
 * val host: A2UIHost = ... // e.g. an A2UIPager, or an A2UISurfaceDSLView
 * val applier = A2UIApplier(host)
 * applier.createComponent(tag = 1, viewName = "KRView", parentTag = 0, index = 0)
 * applier.updateComponent(tag = 1, "KRView", props = mapOf("backgroundColor" to "#FF0000"))
 * applier.performLayout()
 * ```
 */
@Suppress("DEPRECATION")
internal class A2UILegacyApplier(private val host: A2UIHost) {

    /** Root tag (the host's rootContainer acts as the A2UI protocol root) */
    private val ROOT_TAG = 0

    /** Callback for two-way binding: slider value changed (tag, dataKey, newValue) */
    var onSliderValueChanged: ((Int, String, Float) -> Unit)? = null

    /** Callback for two-way binding: checkbox value changed (tag, dataKey, newValue) */
    var onCheckBoxValueChanged: ((Int, String, Boolean) -> Unit)? = null

    /** Callback for two-way binding: textField value changed (tag, dataKey, newValue) */
    var onTextFieldValueChanged: ((Int, String, String) -> Unit)? = null

    /** Callback for showing native date/time picker (tag, enableDate, enableTime, currentValue, label, min, max) */
    var onShowDateTimePicker: ((Int, Boolean, Boolean, String, String, String, String) -> Unit)? = null

    /** Callback for two-way binding: choicePicker chip tapped (chipTag, parentComponentId, optionValue) */
    var onChoicePickerValueChanged: ((Int, String, String) -> Unit)? = null

    /** Callback for choicePicker filter input text changed (parentComponentId, filterText) */
    var onChoicePickerFilter: ((String, String) -> Unit)? = null

    /** Callback for tab selection: tab header tapped (tabsComponentId, tabIndex) */
    var onTabSelected: ((String, Int) -> Unit)? = null

    /** Callback for Modal open: trigger tapped (modalComponentId) */
    var onModalOpen: ((String) -> Unit)? = null

    /** Callback for Modal close: close button / backdrop tapped, or back pressed (modalComponentId) */
    var onModalClose: ((String) -> Unit)? = null

    /** Callback for a Markdown link tap inside a Text component (the link URL) */
    var onMarkdownLinkClick: ((String) -> Unit)? = null

    /** Callback for view click (action dispatch): invoked when a touchEnable=1 view is tapped */
    var onViewClick: ((Int) -> Unit)? = null

    /** Map from slider view tag to its bound data key (for two-way binding) */
    private val sliderBindingPaths = mutableMapOf<Int, SliderBindingInfo>()

    /** Map from checkbox view tag to its bound data key (for two-way binding) */
    private val checkBoxBindingPaths = mutableMapOf<Int, String>()

    /**
     * Set of CheckBox tags whose checked/default view creators have already been registered.
     * The creators encode theme-derived colors (primaryColor) and static glyphs,
     * so they only need to be installed on first apply; subsequent incremental updates
     * (e.g. updateDataModel diff carrying only `checked`) must NOT overwrite them or
     * the theme color would be lost (fallback #00BFFF).
     */
    private val checkBoxCreatorRegistered = mutableSetOf<Int>()

    /** Map from textField view tag to its bound data key (for two-way binding) */
    private val textFieldBindingPaths = mutableMapOf<Int, String>()

    /** Map from dateTimeInput view tag to its bound data key (for two-way binding) */
    private val dateTimeBindingPaths = mutableMapOf<Int, String>()

    /** Set of static (non-bound) DateTimeInput tags that have registered click handlers */
    private val dateTimeStaticTags = mutableSetOf<Int>()

    /** Set of chip tags that have already registered click handlers (to avoid duplicates) */
    private val choiceChipRegistered = mutableSetOf<Int>()

    /** Set of filter input tags that have already been registered for text change events */
    private val choiceFilterRegistered = mutableSetOf<Int>()

    /** Set of tab header tags that have already registered click handlers (to avoid duplicates) */
    private val tabHeaderRegistered = mutableSetOf<Int>()

    /** Set of tags that have already registered Modal-related click handlers (to avoid duplicates) */
    private val modalClickRegistered = mutableSetOf<Int>()

    /**
     * Set of Image tags that have already registered a loadResolution callback for the
     * `__featureAutoSizeMaxHeightDp` sizing rule. See [applyImageProps] for the sizing spec.
     */
    private val imageAutoSizeRegistered = mutableSetOf<Int>()

    /**
     * Tags of Row-hosted Text views for which `applyRowTextWidth` has already
     * run successfully at least once (i.e. parent Row had non-zero width and
     * the width constraint was applied). Used by the first-attach flicker
     * suppression path in [applyTextProps] / [applyMarkdownText]:
     *
     *  - On FIRST applyProps for a given tag, if the Row isn't laid out yet
     *    (rowMaxW == null), we draw the Text with `opacity = 0f` so the
     *    "no-width single-line overflow" intermediate frame is invisible.
     *  - When the one-shot post-layout callback runs, it applies the real
     *    width AND restores opacity to 1f atomically.
     *  - After that the tag is added to this set, so subsequent content
     *    updates (streaming data refresh) never re-hide the Text even if
     *    some edge case causes rowMaxW to momentarily come back as null.
     */
    private val rowTextWidthAppliedTags = mutableSetOf<Int>()

    /**
     * Tags of Row-hosted Texts that are CURRENTLY rendered with opacity=0
     * by the first-attach flicker suppression. We need this because the
     * `shouldEverApplyRowTextMaxWidth` predicate is optimistic when the
     * Text hasn't been attached to its parent Row yet (view.flexNode.parent
     * == null) — it still votes HIDE so that review-row bodies get hidden
     * on their very first applyTextProps call (which happens before attach).
     * Later, when the one-shot post-layout callback fires and
     * `computeRowTextMaxWidth` still returns null (meaning the optimistic
     * guess was wrong — e.g. parent Row is nested-Row width-dependent),
     * applyRowTextWidth consults this set and restores opacity so the Text
     * never gets stuck invisible.
     */
    private val rowTextHiddenTags = mutableSetOf<Int>()

    /**
     * Slider binding info: stores the data key, min/max for denormalization,
     * and (PR-9, v1.0) an optional `steps` count for value snapping.
     *
     * `steps` is the number of intervals, i.e. `steps = 4` over `min=0, max=1`
     * produces 5 legal values {0.0, 0.25, 0.5, 0.75, 1.0}. A `null` or
     * non-positive value disables snapping and keeps the pre-PR-9 continuous
     * behaviour.
     */
    private data class SliderBindingInfo(
        val dataKey: String,
        val min: Float,
        val max: Float,
        val steps: Int? = null
    )

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

    init {
        // Register the host's root container as the A2UI root (tag 0)
        host.registry.register(ROOT_TAG, host.rootContainer)
    }

    /**
     * Create a DSL component and register it with the given tag.
     *
     * @param tag Unique tag for this component
     * @param viewName The component type name (e.g., "KRView", "KRRichTextView", "KRImageView")
     * @param parentTag Parent component tag (for tree insertion)
     * @param index Position within parent's children
     * @param props Initial properties to apply
     */
    fun createComponent(
        tag: Int,
        viewName: String,
        parentTag: Int,
        index: Int,
        props: Map<String, Any?>? = null
    ) {
        // Ensure currentPageId is set for ReactiveObserver lookup in attr{}
        BridgeManager.currentPageId = host.hostPagerId
        val view = createDSLView(viewName)
        host.registry.register(tag, view)

        // Insert into parent FIRST so that pagerId is set via internalAddChild.
        // This is critical because attr{} requires pagerId to be non-empty.
        val effectiveParentTag = if (parentTag < 0) ROOT_TAG else parentTag
        val parent = host.registry.getContainer(effectiveParentTag) ?: host.rootContainer

        // For ComposeView subclasses (e.g. CheckBoxView, DateTimeInputView), didInit() is called during
        // addChild and may require certain props (like width/height, or enableDate/enableTime/value) to
        // be set beforehand. We pass props in the init lambda so they are applied before didInit().
        //
        // Slider is intentionally NOT special-cased here: a2ui's A2UISliderView wraps kuikly's SliderView
        // and defers creating the inner SliderView until its own layoutFrameDidChanged observes a real
        // width/height, so didInit()/body() do not need a seed size.
        if (view is CheckBoxView && props != null && props.isNotEmpty()) {
            // CheckBoxView.didInit() requires styleWidth and styleHeight to be set.
            // CheckBox uses fixed size (24x24 per protocol), so just apply props in init lambda.
            parent.addChild(view, {
                applyProps(view, viewName, props)
            }, if (index >= 0) index else -1)
        } else if (view is DateTimeInputView && props != null && props.isNotEmpty()) {
            // DateTimeInputView.didInit() calls body() which needs enableDate/enableTime/value
            // to be set beforehand. Apply props in init lambda so they are available.
            parent.addChild(view, {
                applyProps(view, viewName, props)
            }, if (index >= 0) index else -1)
        } else {
            parent.addChild(view, {}, if (index >= 0) index else -1)

            // Apply props BEFORE insertDomSubView, because insertDomSubView calls
            // insertSubRenderView which checks isRenderView(). The flat-layer optimization
            // returns false for isRenderView() if attr is empty (no backgroundColor, etc.).
            // We need props applied so the view is recognized as a RenderView.
            if (props != null && props.isNotEmpty()) {
                applyProps(view, viewName, props)
            }
        }

        // After addChild and applyProps, insert the FlexNode into the parent's FlexNode tree.
        // This also creates the RenderView and inserts it into the native view hierarchy.
        //
        // For ScrollerView parents: ScrollerView.addChild() delegates to contentView.addChild(),
        // so the child lives in contentView's logical tree. We must also insert the child's
        // FlexNode into contentView's flex tree (not scrollerView's). Use realContainerView()
        // which returns contentView for ScrollerView, or self for plain containers.
        val domContainer = parent.realContainerView()
        val domIndex = if (index >= 0) index else domContainer.getSubviews().size - 1
        domContainer.insertDomSubView(view, domIndex.coerceAtLeast(0))

        // Vertical Divider: after the initial layout, resize the divider's height to
        // match the tallest sibling (i.e. the parent Row's actual content height) so
        // it truly "spans the height of the container" (per A2UI catalog spec).
        //
        // Why we don't use alignSelf=stretch: Kuikly's flex layout cannot stretch a
        // node whose intrinsic cross-axis size is undefined when the parent's cross
        // axis is content-driven. In such cases the undefined layoutDim propagates
        // through max()/boundAxis() as NaN, collapsing the parent's height to 0.
        //
        // Strategy: buildProps assigns a sensible default height (40dp) so the first
        // layout pass is stable and the Row's height is the max of its non-divider
        // siblings. After that layout completes we read the tallest sibling's
        // computed height and resize the divider to match. If the divider is already
        // >= that height (e.g. the Row is short), we leave it as-is.
        if (props != null && props["__verticalDividerFollowParent"] == true) {
            host.addTaskWhenLayoutFinish {
                var maxSiblingHeight = 0f
                for (sibling in parent.getSubviews()) {
                    if (sibling === view) continue
                    val h = sibling.flexNode.layoutFrame.height
                    if (!h.isNaN() && h > maxSiblingHeight) {
                        maxSiblingHeight = h
                    }
                }
                if (maxSiblingHeight > 0f) {
                    val current = view.flexNode.layoutFrame.height
                    // Only resize if the target height is meaningfully different from
                    // the current one to avoid an unnecessary re-layout loop.
                    if (current.isNaN() || kotlin.math.abs(current - maxSiblingHeight) > 0.5f) {
                        view.attr { height(maxSiblingHeight) }
                    }
                }
            }
        }
    }

    /**
     * Update properties on an existing component.
     *
     * @param tag The component tag
     * @param viewName The component type name (for type-specific prop handling)
     * @param props Properties to update
     */
    fun updateComponent(tag: Int, viewName: String, props: Map<String, Any?>) {
        // Ensure currentPageId is set for ReactiveObserver lookup in attr{}
        BridgeManager.currentPageId = host.hostPagerId
        val view = host.registry.getView(tag) ?: return
        applyProps(view, viewName, props)
    }

    /**
     * Remove a component from the tree and unregister it.
     *
     * @param tag The component tag to remove
     * @param parentTag The parent component tag
     */
    fun removeComponent(tag: Int, parentTag: Int) {
        val view = host.registry.getView(tag) ?: return
        val effectiveParentTag = if (parentTag < 0) ROOT_TAG else parentTag
        val parent = host.registry.getContainer(effectiveParentTag)
        if (parent != null) {
            // Mirror createComponent (which calls both addChild AND insertDomSubView):
            // removeDomSubView tears down the native render view, removeChild detaches the
            // DSL node. removeChild alone only nulls the renderView reference and leaves the
            // native view on screen (this is the canonical Kuikly removal order, see e.g.
            // ModalView.didRemoveFromParentView / LoopDirectivesView).
            //
            // For ScrollerView parents: children live in contentView (via realContainerView()),
            // so we must removeDomSubView/removeChild from contentView, not scrollerView.
            val domContainer = parent.realContainerView()
            domContainer.removeDomSubView(view)
            domContainer.removeChild(view)
        }
        host.registry.unregister(tag)
        // Clean up tag-scoped state to avoid stale entries when the same protocol id
        // (and therefore the same tag) is re-created later (e.g. Modal reopen).
        // In particular, checkBoxCreatorRegistered controls whether creators are
        // registered; leaking it causes the new CheckBoxView to skip creator setup
        // and fall back to the default empty-src Image renderer.
        checkBoxCreatorRegistered.remove(tag)
        checkBoxBindingPaths.remove(tag)
        sliderBindingPaths.remove(tag)
        textFieldBindingPaths.remove(tag)
        dateTimeBindingPaths.remove(tag)
        dateTimeStaticTags.remove(tag)
        // When a Row-hosted Text is removed and later re-created at the same
        // tag (e.g. a ChildList index whose data changed enough to force a
        // REMOVE + INSTANTIATE), we must clear the "already applied" marker
        // so the first-attach flicker suppression in applyTextProps /
        // applyMarkdownText (opacity=0 while parent Row isn't laid out)
        // re-engages for the fresh instance.
        rowTextWidthAppliedTags.remove(tag)
        rowTextHiddenTags.remove(tag)
        imageAutoSizeRegistered.remove(tag)
    }

    /**
     * Trigger layout calculation.
     * After this, content height can be retrieved from the host.
     */
    fun performLayout() {
        // Ensure currentPageId is set for layout operations
        BridgeManager.currentPageId = host.hostPagerId
        host.performLayoutAndReportHeight()
    }

    /**
     * Destroy the applier and clean up.
     */
    fun destroy() {
        // Pager cleanup is handled by A2UIPager.onDestroyPager()
    }

    // region Private - View Factory

    /**
     * Create a Kuikly DSL view instance based on the component type name.
     */
    private fun createDSLView(viewName: String): DeclarativeBaseView<*, *> {
        return when (viewName) {
            "KRView", "View" -> DivView()
            "KRRichTextView", "KRGradientRichTextView", "RichText", "Text" -> RichTextView()
            "KRImageView", "KRWrapperImageView", "Image" -> ImageView()
            "KRVideoView", "Video" -> VideoView()
            "KRAudioView", "AudioPlayer" -> AudioView()
            "KRTextFieldView", "TextField" -> InputView()
            "KRTextAreaView" -> TextAreaView()
            "KRSliderView", "Slider" -> A2UISliderView()
            "KRCheckBoxView", "CheckBox" -> CheckBoxView()
            "KRDateTimeInputView", "DateTimeInput" -> DateTimeInputView()
            "KRModalView", "Modal" -> ModalView().apply { inWindow = true }
            // A2UI List -> A2UIScrollerView (extends ScrollerView with cross-axis
            // size backfill to fix content-driven layout height collapse).
            "KRScrollView", "KRListView", "Scroller", "List" -> A2UIScrollerView()
            "KRBlurView" -> BlurView()
            "KRActivityIndicatorView" -> ActivityIndicatorView()
            "KRHoverView" -> HoverView()
            "KRPAGView" -> PAGView()
            "KRAPNGView" -> APNGVView()
            "KRCanvasView" -> CanvasView()
            // Custom component branch:
            //  - If the host app has registered `viewName` via
            //    `A2UISurfaceView.registerCustomRenderView(...)`, route it to
            //    A2UICustomComponentView so the native render layer creates the
            //    correct registered custom view and the flex engine can call the
            //    native Shadow to determine intrinsic size.
            //  - Otherwise (viewName is unknown / malformed protocol data), fall
            //    back to a plain DivView. This avoids the fatal native asserts
            //    that would otherwise fire on iOS/Android when the runtime cannot
            //    resolve a corresponding view class (`KRClassFromString(...)`
            //    returns nil / `createRenderShadow` throws).
            else -> if (A2UICustomViewRegistry.isRegistered(viewName)) {
                A2UICustomComponentView(viewName)
            } else {
                DivView()
            }
        }
    }

    // endregion

    // region Private - Property Application

    /**
     * Apply properties to a DSL view.
     */
    private fun applyProps(view: DeclarativeBaseView<*, *>, viewName: String, props: Map<String, Any?>) {
        // Apply common layout/style properties
        applyCommonProps(view, props)

        // Apply type-specific properties
        when (view) {
            is RichTextView -> applyTextProps(view, props)
            is InputView -> applyTextFieldProps(view, props)
            is TextAreaView -> applyTextAreaProps(view, props)
            is ImageView -> applyImageProps(view, props)
            is VideoView -> applyVideoProps(view, props)
            is AudioView -> applyAudioProps(view, props)
            is A2UISliderView -> applySliderProps(view, props)
            is CheckBoxView -> applyCheckBoxProps(view, props)
            is DateTimeInputView -> applyDateTimeInputProps(view, props)
            is BlurView -> applyBlurProps(view, props)
            is HoverView -> applyHoverProps(view, props)
            is ActivityIndicatorView -> applyActivityIndicatorProps(view, props)
            is PAGView -> applyPAGProps(view, props)
            is APNGVView -> applyAPNGProps(view, props)
            is ScrollerView<*, *> -> applyScrollerProps(view, props)
            else -> {
                // For custom components (DivView fallback), forward unknown props
                // to the native render view via setProp. This enables custom component
                // props like "source", "text", "textColor" to reach the native layer.
                applyCustomComponentProps(view, props)
            }
        }

        // Post-layout alignment fix for Text in Column with align=center/end.
        // During the first layout pass, Text keeps alignSelf=stretch to get a width
        // constraint (needed when parentMaxWidth is undefined in the flex chain).
        // After layout, we know the parent's actual width, so we can:
        //  1. Set maxWidth = parent's content width (provides width constraint for wrapping)
        //  2. Remove alignSelf=stretch (lets alignItems position the text)
        //  3. Trigger re-layout so the text is correctly centered/end-aligned
        val textAlignPostLayout = props["__textAlignPostLayout"] as? String
        if (textAlignPostLayout != null && view is RichTextView) {
            host.addTaskWhenLayoutFinish {
                val parentNode = view.flexNode.parent ?: return@addTaskWhenLayoutFinish
                val parentWidth = parentNode.layoutFrame.width
                if (parentWidth > 0f && !parentWidth.isNaN()) {
                    // Parent's content width = layoutWidth - paddingLeft - paddingRight
                    val padLeft = parentNode.getPadding(StyleSpace.Type.LEFT)
                    val padRight = parentNode.getPadding(StyleSpace.Type.RIGHT)
                    val contentWidth = parentWidth - (if (padLeft.isNaN()) 0f else padLeft) - (if (padRight.isNaN()) 0f else padRight)
                    val maxW = if (contentWidth > 0f) contentWidth else parentWidth
                    view.attr {
                        maxWidth(maxW)
                        alignSelf(FlexAlign.AUTO)  // inherit from parent's alignItems
                    }
                }
            }
        }

        // Post-layout width fix for Text inside a Row.
        //
        // Kuikly's Flex engine passes `parentMaxWidth=undefined` from a Row to
        // its children, so a Row-hosted Text measures at its natural single-
        // line width and gets clipped at the Row's right edge instead of
        // wrapping. We work around this by hard-setting the Text's own
        // `width` to the Row's content width minus the Text's horizontal
        // margins; this produces an EXACTLY-mode measurement and triggers
        // real multi-line wrapping.
        //
        // The width is normally applied synchronously inside the same
        // `view.attr { ... }` block that writes the text content (see
        // [applyTextProps] / [applyMarkdownText]) — that merged path is
        // flicker-free because the render layer only ever sees the final
        // state. The one-shot callback registered below is the FIRST-attach
        // fallback: when a Text is attached during streaming its parent Row
        // may not yet be laid out, so [computeRowTextMaxWidth] returns null
        // and [applyRowTextWidth] can't run from the synchronous path. This
        // callback fires after the first full layout pass and gives
        // `parentWidth > 0` a chance so the real width (and visibility
        // restore for the opacity=0 flicker-suppression path) can finally be
        // applied.
        val textInRowPostLayout = props["__textInRowPostLayout"] as? String
        if (textInRowPostLayout != null && view is RichTextView) {
            host.addTaskWhenLayoutFinish {
                applyRowTextWidth(view, props)
            }
        }

        // Handle ChoicePicker chip click registration
        val choiceParentId = props["__choicePickerParentId"] as? String
        val choiceOptionValue = props["__choiceOptionValue"] as? String
        if (choiceParentId != null && choiceOptionValue != null && view is DivView) {
            val tag = host.registry.getTag(view) ?: return
            // Only register click handler once
            if (!choiceChipRegistered.contains(tag)) {
                choiceChipRegistered.add(tag)
                view.event {
                    click {
                        onChoicePickerValueChanged?.invoke(tag, choiceParentId, choiceOptionValue)
                    }
                }
            }
        }

        // Handle ChoicePicker filter input text change
        val choiceFilterId = props["__choicePickerFilterId"] as? String
        if (choiceFilterId != null && view is InputView) {
            val tag = host.registry.getTag(view) ?: return
            if (!choiceFilterRegistered.contains(tag)) {
                choiceFilterRegistered.add(tag)
                view.event {
                    textDidChange { params ->
                        onChoicePickerFilter?.invoke(choiceFilterId, params.text)
                    }
                }
            }
        }

        // Handle Tabs header click registration
        val tabsParentId = props["__tabsParentId"] as? String
        val tabIndex = props["__tabIndex"] as? Int
        if (tabsParentId != null && tabIndex != null && view is DivView) {
            val tag = host.registry.getTag(view) ?: return
            if (!tabHeaderRegistered.contains(tag)) {
                tabHeaderRegistered.add(tag)
                view.event {
                    click {
                        onTabSelected?.invoke(tabsParentId, tabIndex)
                    }
                }
            }
        }

        // Handle Modal interactions (trigger to open, close button/backdrop to close,
        // dialog to absorb taps, and modal window back-press dismissal).
        applyModalProps(view, props)
    }

    /**
     * Wire Modal-related click handlers based on the marker props injected by the adapter:
     * - `__modalTriggerId`: tapping this view opens the modal.
     * - `__modalClose`: tapping this view (close button / backdrop) closes the modal.
     * - `__modalAbsorb`: this view swallows taps so they don't reach the backdrop.
     * - `__modalId` (on the ModalView itself): wire system back-press to close.
     */
    private fun applyModalProps(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        val triggerModalId = props["__modalTriggerId"] as? String
        val closeModalId = props["__modalClose"] as? String
        val absorb = props["__modalAbsorb"] != null
        val modalId = props["__modalId"] as? String

        if (triggerModalId == null && closeModalId == null && !absorb && modalId == null) return

        val tag = host.registry.getTag(view) ?: return
        if (modalClickRegistered.contains(tag)) return
        modalClickRegistered.add(tag)

        // Click handlers are wired on container (DivView) targets, matching how the
        // adapter builds the trigger, backdrop, dialog, and close row.
        val divView = view as? DivView
        if (divView != null) {
            if (triggerModalId != null) {
                divView.event { click { onModalOpen?.invoke(triggerModalId) } }
            }
            if (closeModalId != null) {
                divView.event { click { onModalClose?.invoke(closeModalId) } }
            }
            if (absorb) {
                // No-op click to keep the tap from bubbling to the backdrop close handler.
                divView.event { click { } }
            }
        }
        if (modalId != null && view is ModalView) {
            // Size the Kuikly flex frame so the backdrop fills the (natively full-screen)
            // modal window and child layout has real bounds to work with.
            val (modalWidth, modalHeight) = host.fullScreenModalSize()
            view.attr {
                width(modalWidth)
                height(modalHeight)
            }
            // System back-press dismisses the modal.
            view.event { willDismiss { onModalClose?.invoke(modalId) } }
        }
    }

    /**
     * Apply common properties that all views support.
     * These map directly to Kuikly DSL attr methods.
     */
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

        // Register click event handler for views with touchEnable=1.
        // On Android, the native layer (A2UISurfaceView.kt) registers an OnClickListener
        // when SET_PROP(touchEnable=1) is received, and sends VIEW_CLICK to Kotlin on tap.
        // On HarmonyOS, the C++ render layer only sets HitTest mode for touchEnable
        // but does NOT register a click callback. So we register via Kuikly Core's
        // standard click event mechanism, which works cross-platform.
        val touchEnableValue = props["touchEnable"]
        val isTouchEnabled = when (touchEnableValue) {
            is Number -> touchEnableValue.toInt() == 1
            is Boolean -> touchEnableValue
            is String -> touchEnableValue == "1" || touchEnableValue == "true"
            else -> false
        }
        if (isTouchEnabled) {
        val tag = host.registry.getTag(view)
            if (tag != null) {
                view.event {
                    click {
                        onViewClick?.invoke(tag)
                    }
                }
            }
        }

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
     * Apply text-specific properties to a RichTextView.
     *
     * For Row-hosted Text (marked with `__textInRowPostLayout`) this function
     * merges the row-text width constraint into the same `view.attr {}` block
     * as the text/font/color settings, so the render pipeline never observes
     * an intermediate "content updated but still at natural single-line width"
     * state (which would otherwise cause a visible single-line→wrapped flicker
     * when a newly instantiated review row first receives its body text).
     */
    private fun applyTextProps(view: RichTextView, props: Map<String, Any?>) {
        val rawText = props["text"]?.toString()

        // Per the A2UI v0.9 protocol, a Text's content is "simple Markdown" by
        // default (no opt-in flag). When markers are present we render it as
        // styled spans; otherwise we keep the cheaper single-string path.
        if (rawText != null && MarkdownParser.containsMarkdown(rawText)) {
            applyMarkdownText(view, rawText, props)
            return
        }

        // Compute the row-text width constraint up-front (null when either the
        // marker is absent or the parent Row hasn't been laid out yet). We
        // only need to markDirty + relayout when we actually got a width.
        val rowMaxW = computeRowTextMaxWidth(view, props)
        val isRowText = props["__textInRowPostLayout"] != null
        val tag = if (isRowText) host.registry.getTag(view) else null
        // Flicker suppression: whenever this is a Row-hosted Text and the
        // parent Row isn't currently laid out (rowMaxW == null), draw this
        // frame with opacity=0 so the inevitable "no-width single-line
        // overflow" intermediate state is never visible. The one-shot
        // post-layout callback registered by applyProps will later invoke
        // applyRowTextWidth which both applies the real width AND restores
        // opacity to 1 atomically, so the user only ever sees the final
        // wrapped state.
        //
        // NOTE: we intentionally DO NOT gate on `tag !in
        // rowTextWidthAppliedTags`. In streaming scenarios the same Text may
        // be re-applied many times; if a later re-apply happens while the
        // parent Row is momentarily unlaid-out (e.g. its subtree was just
        // rebuilt), rowMaxW is again null. Without this guard we would
        // render a single "wide single-line" frame before the next layout
        // pass, which the user perceives as a flicker between wrapped and
        // unwrapped states. applyProps registers a fresh one-shot post-
        // layout callback on every pass, so re-hiding is always safe.
        //
        // Gated by shouldEverApplyRowTextMaxWidth: we must only hide a Text
        // when we are sure that applyRowTextWidth WILL later restore its
        // opacity. For Texts whose parent Row width is NOT independent of
        // the Text's own natural width (e.g. nested Row, "OS 87" inside
        // header-left → header-row), applyRowTextWidth will permanently
        // return early and the opacity would stay at 0 forever.
        val needsFirstAttachHide = isRowText && rowMaxW == null &&
            shouldEverApplyRowTextMaxWidth(view, props)
        if (rowMaxW != null) {
            // Invalidate TextShadow's size cache BEFORE the attr block so the
            // single re-measure triggered by the batched attr-flush uses the
            // new content + width together (otherwise a cached single-line
            // result from the previous content would be used).
            view.shadow?.markDirty()
        }

        view.attr {
            rawText?.let { text(it) }
            props["fontSize"]?.let { fontSize(toFloat(it)) }
            props["color"]?.let { color(toColor(it)) }
            props["fontWeight"]?.let {
                when (it.toString().lowercase()) {
                    "bold", "700" -> fontWeightBold()
                    "medium", "500" -> fontWeightMedium()
                    "semibold", "600" -> fontWeightSemiBold()
                    "light", "300" -> fontWeightLight()
                    "normal", "400" -> fontWeightNormal()
                    "800" -> fontWeightExtraBold()
                    "900" -> fontWeightBlack()
                    else -> fontWeightNormal()
                }
            }
            props["fontStyle"]?.let {
                if (it.toString().lowercase() == "italic") {
                    fontStyleItalic()
                }
            }
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "left" -> textAlignLeft()
                    "right" -> textAlignRight()
                }
            }
            props["numberOfLines"]?.let { "numberOfLines" with toFloat(it).toInt() }
            props["lineHeight"]?.let { lineHeight(toFloat(it)) }
            props["lineSpacing"]?.let { lineSpacing(toFloat(it)) }
            props["letterSpacing"]?.let { letterSpacing(toFloat(it)) }
            props["textDecoration"]?.let {
                when (it.toString().lowercase()) {
                    "underline" -> textDecorationUnderLine()
                    "line-through", "linethrough" -> textDecorationLineThrough()
                }
            }
            props["fontFamily"]?.let { fontFamily(it.toString()) }
            props["textShadow"]?.let { shadow ->
                if (shadow is Map<*, *>) {
                    val offsetX = toFloat(shadow["offsetX"])
                    val offsetY = toFloat(shadow["offsetY"])
                    val radius = toFloat(shadow["radius"])
                    val color = toColor(shadow["color"])
                    textShadow(offsetX, offsetY, radius, color)
                }
            }
            props["firstLineHeadIndent"]?.let { firstLineHeadIndent(toFloat(it)) }
            // Row-text width constraint (merged in the same attr block so the
            // render never flickers through a "wide single-line" intermediate
            // state). We use maxWidth (AT-MOST) rather than width (EXACTLY)
            // so sibling Texts in the same Row can still share the row
            // horizontally at their natural widths; only overlong text
            // actually wraps.
            if (rowMaxW != null) {
                maxWidth(rowMaxW)
                alignSelf(FlexAlign.AUTO)
                opacity(1f)
            } else if (needsFirstAttachHide) {
                opacity(0f)
            }
        }
        if (rowMaxW != null) {
            host.performLayoutAndReportHeight()
            view.setFrameToRenderView(view.flexNode.layoutFrame)
            view.updateShadow()
            if (tag != null) {
                rowTextWidthAppliedTags.add(tag)
                rowTextHiddenTags.remove(tag)
            }
        } else if (needsFirstAttachHide && tag != null) {
            // Record that this tag is now hiding, so the post-layout
            // callback (applyRowTextWidth) can restore opacity even when
            // its own maxW is still null (e.g. parent Row turned out to be
            // nested-Row width-dependent).
            rowTextHiddenTags.add(tag)
        }
    }

    /**
     * Compute the horizontal width budget that a Row-hosted Text should use
     * when wrapping, or null when the computation isn't yet possible (marker
     * missing, parent not laid out, zero/negative budget after margins).
     *
     * Kuikly's Flex engine passes parentMaxWidth=undefined from a Row to its
     * children, so Text.measure() returns the single-line natural width and
     * the Row then clips the Text at its right edge. We work around this by
     * setting the Text's own `maxWidth` to the Row's content width, giving
     * long text an AT-MOST bound that triggers multi-line wrapping while
     * short sibling Texts are unaffected and can still share the row
     * horizontally at their natural widths.
     */
    private fun computeRowTextMaxWidth(view: RichTextView, props: Map<String, Any?>): Float? {
        if (!shouldEverApplyRowTextMaxWidth(view, props)) return null
        val parentNode = view.flexNode.parent ?: return null
        val parentWidth = parentNode.layoutFrame.width
        if (parentWidth <= 0f || parentWidth.isNaN()) return null
        val padLeft = parentNode.getPadding(StyleSpace.Type.LEFT)
        val padRight = parentNode.getPadding(StyleSpace.Type.RIGHT)
        val contentWidth = parentWidth -
            (if (padLeft.isNaN()) 0f else padLeft) -
            (if (padRight.isNaN()) 0f else padRight)
        val marginLeft = view.flexNode.getMargin(StyleSpace.Type.LEFT)
        val marginRight = view.flexNode.getMargin(StyleSpace.Type.RIGHT)
        val textMargin = (if (marginLeft.isNaN()) 0f else marginLeft) +
            (if (marginRight.isNaN()) 0f else marginRight)
        val maxW = (if (contentWidth > 0f) contentWidth else parentWidth) - textMargin
        return if (maxW > 0f) maxW else null
    }

    /**
     * Whether a Row-hosted Text is ever going to receive a maxWidth bound
     * from this applier. This is distinct from [computeRowTextMaxWidth]
     * returning non-null: the width may not be computable YET (parent Row
     * not laid out), but it eventually will. This predicate controls the
     * first-attach opacity-hide optimization — we only dare hide a Text
     * when we know a post-layout callback will later restore its opacity.
     * For a nested-Row Text (parent Row's width depends on this Text's
     * natural width), no maxWidth will ever be applied, so we must NOT
     * hide it or it would stay invisible forever.
     */
    private fun shouldEverApplyRowTextMaxWidth(view: RichTextView, props: Map<String, Any?>): Boolean {
        if (props["__textInRowPostLayout"] == null) return false
        // Optimistic path: when the Text hasn't been attached to its parent
        // Row yet (typical for a freshly-instantiated row-template subtree
        // whose children are pre-applied before being attached), we have no
        // way to judge whether the parent Row's width is self-determined.
        // Returning `true` here lets applyTextProps / applyMarkdownText go
        // ahead and set opacity=0 for this first frame, suppressing the
        // single-line flicker. If the optimistic guess turns out to be wrong
        // (parent Row is actually nested-Row width-dependent), the post-
        // layout callback in applyRowTextWidth consults `rowTextHiddenTags`
        // and restores opacity=1 on its maxW==null short-circuit — so the
        // Text never gets stuck invisible.
        val parentNode = view.flexNode.parent ?: return true
        return isParentRowWidthIndependent(parentNode)
    }

    /**
     * Decide whether [parentRow]'s width can be used safely as a maxWidth
     * bound for a Text child, i.e. whether that width is determined by
     * something OTHER than the Text's own natural width.
     *
     * Rules (walking up the flex tree):
     * - Explicit `styleWidth` set on the Row → independent ✓
     * - `alignSelf = STRETCH` and the grandparent is a Column → the Row is
     *   stretched to the column's cross-axis extent, independent ✓
     * - Grandparent is a Column (vertical container) → the Row is stretched
     *   by default (Column's default alignItems is stretch unless the Row
     *   explicitly opts out with FLEX_START/CENTER/FLEX_END), independent ✓
     * - Grandparent is a Row (nested horizontal Row) without any of the
     *   above → wrap-content, NOT independent ✗
     * - No grandparent (root) → independent ✓
     */
    private fun isParentRowWidthIndependent(parentRow: com.tencent.kuikly.core.layout.FlexNode): Boolean {
        // Explicit width on the Row itself → always safe.
        if (!parentRow.styleWidth.isNaN()) return true
        val grandparent = parentRow.parent ?: return true
        // Grandparent is a horizontal container → the Row is wrap-content
        // along the main axis unless its own alignSelf stretches it, which
        // doesn't apply on the main axis anyway. Treat as not independent.
        if (grandparent.flexDirection == FlexDirection.ROW ||
            grandparent.flexDirection == FlexDirection.ROW_REVERSE) {
            return false
        }
        // Grandparent is vertical (Column/Card). By default its cross-axis
        // alignItems is STRETCH, which pulls the Row to the column's width.
        // The Row stays stretched unless it explicitly opts out.
        return parentRow.alignSelf != FlexAlign.FLEX_START &&
            parentRow.alignSelf != FlexAlign.CENTER &&
            parentRow.alignSelf != FlexAlign.FLEX_END
    }

    /**
     * Apply the hard width constraint to a Row-hosted Text so wrapping works,
     * invoked ONLY from the one-shot post-layout callback registered in
     * [applyProps]. The synchronous path inside [applyTextProps] /
     * [applyMarkdownText] already merges the width into the same `view.attr`
     * block that writes the content — this standalone call exists solely to
     * cover the first-attach case where the parent Row hadn't been laid out
     * yet and [computeRowTextMaxWidth] returned null.
     *
     * In addition to the width + shape-change (alignSelf STRETCH→AUTO) that
     * forces a real re-measure, this also restores `opacity` to 1 so any
     * first-attach flicker suppression (opacity=0 set by the synchronous
     * path when rowMaxW was unknown) is lifted atomically with the final
     * wrapped layout — the user never sees the single-line intermediate
     * state.
     */
    private fun applyRowTextWidth(view: RichTextView, props: Map<String, Any?>) {
        val maxW = computeRowTextMaxWidth(view, props)
        if (maxW == null) {
            // If the Text was optimistically hidden by applyTextProps /
            // applyMarkdownText (because `view.flexNode.parent` was null and
            // `shouldEverApplyRowTextMaxWidth` returned the optimistic
            // `true`), we now know no maxWidth will ever be applied — the
            // parent Row is actually nested-Row width-dependent. We MUST
            // restore opacity here, otherwise the Text would stay invisible
            // forever.
            val tag = host.registry.getTag(view)
            if (tag != null && tag in rowTextHiddenTags) {
                view.attr { opacity(1f) }
                rowTextHiddenTags.remove(tag)
            }
            return
        }
        // Invalidate TextShadow's size cache so the next calculateRenderViewSize
        // performs a real native multi-line layout instead of returning the
        // cached single-line result.
        view.shadow?.markDirty()
        view.attr {
            maxWidth(maxW)
            alignSelf(FlexAlign.AUTO)
            // Restore visibility in case applyTextProps / applyMarkdownText set
            // opacity=0 for this first attach frame while waiting for the Row
            // to be laid out. Setting opacity=1 unconditionally here is safe
            // because any explicit opacity from protocol props is applied
            // elsewhere (applyCommonProps) and this call wins by order.
            opacity(1f)
        }
        host.performLayoutAndReportHeight()
        view.setFrameToRenderView(view.flexNode.layoutFrame)
        view.updateShadow()
        host.registry.getTag(view)?.let { rowTextWidthAppliedTags.add(it) }
    }

    /** Color used for rendered Markdown links. */
    private val MARKDOWN_LINK_COLOR = Color(0xFF1A73E8L)
    /** Color used for inline `code` runs. */
    private val MARKDOWN_CODE_COLOR = Color(0xFFC7254EL)

    /**
     * Render a Markdown string as a set of styled [RichTextView] spans.
     *
     * The Text's base style (variant font size, color, weight) is used as the
     * default for every run; inline emphasis and headings layer on top of it.
     * Heading levels override the base font size so a leading `#` renders as a
     * heading rather than showing the literal marker.
     */
    private fun applyMarkdownText(view: RichTextView, rawText: String, props: Map<String, Any?>) {
        val spans = MarkdownParser.parse(rawText)
        if (spans.isEmpty()) {
            view.attr { text(rawText) }
            return
        }

        val baseFontSize = props["fontSize"]?.let { toFloat(it) }
        val baseColor = props["color"]?.let { toColor(it) }
        val baseBold = props["fontWeight"]?.toString()?.lowercase()?.let {
            it == "bold" || it == "700" || it == "800" || it == "900" || it == "semibold" || it == "600"
        } ?: false

        // Replace any previously-applied spans (e.g. when the text updates on a
        // data-model change) and trigger a relayout in one call.
        view.getViewAttr().spans(arrayListOf<ISpan>())

        for (span in spans) {
            val link = span.link
            view.Span {
                text(span.text)

                val size = headingFontSize(span.headingLevel) ?: baseFontSize
                size?.let { fontSize(it) }

                if (link != null) {
                    color(MARKDOWN_LINK_COLOR)
                } else if (span.code) {
                    color(MARKDOWN_CODE_COLOR)
                } else {
                    baseColor?.let { color(it) }
                }

                if (span.bold || baseBold) fontWeightBold()
                if (span.italic) fontStyleItalic()

                when {
                    span.strikethrough -> textDecorationLineThrough()
                    link != null -> textDecorationUnderLine()
                }

                if (link != null) {
                    click { onMarkdownLinkClick?.invoke(link) }
                }
            }
        }

        // Compute the row-text width constraint up-front so we can merge it
        // into the single trailing `view.attr {}` block below. This avoids a
        // visible flicker where the Markdown spans appear at their natural
        // single-line width, get clipped by the Row, and then relayout to
        // the wrapped width only on a subsequent flush.
        val rowMaxW = computeRowTextMaxWidth(view, props)
        val isRowText = props["__textInRowPostLayout"] != null
        val tag = if (isRowText) host.registry.getTag(view) else null
        // Same flicker suppression as applyTextProps: whenever a Row-hosted
        // markdown Text has no computable maxWidth yet (parent Row not laid
        // out), draw it invisible for this frame; the post-layout callback
        // (via applyRowTextWidth) will restore opacity atomically when it
        // applies the real width. See applyTextProps for why we do NOT gate
        // on `tag !in rowTextWidthAppliedTags` — streaming re-applies can
        // observe a transient null-maxW state after the first apply and
        // would otherwise flash a wide single-line frame.
        //
        // Gated by shouldEverApplyRowTextMaxWidth (see applyTextProps for
        // the rationale): never hide a Text whose parent Row width is NOT
        // independent of this Text's natural width, because applyRowTextWidth
        // will never run for it and the opacity would stay at 0 forever.
        val needsFirstAttachHide = isRowText && rowMaxW == null &&
            shouldEverApplyRowTextMaxWidth(view, props)
        if (rowMaxW != null) {
            // Invalidate TextShadow's size cache so the single re-measure
            // triggered by the batched attr-flush uses the new spans + width
            // together.
            view.shadow?.markDirty()
        }

        // Paragraph-level attributes still apply to the whole block.
        view.attr {
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "left" -> textAlignLeft()
                    "right" -> textAlignRight()
                }
            }
            props["numberOfLines"]?.let { "numberOfLines" with toFloat(it).toInt() }
            props["lineHeight"]?.let { lineHeight(toFloat(it)) }
            props["lineSpacing"]?.let { lineSpacing(toFloat(it)) }
            props["letterSpacing"]?.let { letterSpacing(toFloat(it)) }
            if (rowMaxW != null) {
                maxWidth(rowMaxW)
                alignSelf(FlexAlign.AUTO)
                opacity(1f)
            } else if (needsFirstAttachHide) {
                opacity(0f)
            }
        }
        if (rowMaxW != null) {
            host.performLayoutAndReportHeight()
            view.setFrameToRenderView(view.flexNode.layoutFrame)
            view.updateShadow()
            if (tag != null) {
                rowTextWidthAppliedTags.add(tag)
                rowTextHiddenTags.remove(tag)
            }
        } else if (needsFirstAttachHide && tag != null) {
            // See applyTextProps for why we record this in rowTextHiddenTags.
            rowTextHiddenTags.add(tag)
        }
    }

    /** Map a Markdown heading level (1..6) to a font size; null when not a heading. */
    private fun headingFontSize(level: Int): Float? = when (level) {
        1 -> 40f
        2 -> 32f
        3 -> 28f
        4 -> 24f
        5 -> 20f
        6 -> 18f
        else -> null
    }

    /**
     * Apply text-field-specific properties to an InputView.
     * Handles value/placeholder/variant and two-way binding.
     */
    private fun applyTextFieldProps(view: InputView, props: Map<String, Any?>) {
        view.attr {
            props["text"]?.let { text(it.toString()) }
            props["placeholder"]?.let { placeholder(it.toString()) }
            props["placeholderColor"]?.let { placeholderColor(toColor(it)) }
            props["fontSize"]?.let { fontSize(toFloat(it)) }
            props["color"]?.let { color(toColor(it)) }
            props["fontWeight"]?.let {
                when (it.toString().lowercase()) {
                    "bold", "700" -> fontWeightBold()
                    else -> {} // normal is default
                }
            }
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "right" -> textAlignRight()
                    else -> textAlignLeft()
                }
            }
            props["editable"]?.let {
                val editableValue = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                editable(editableValue)
            }
            props["keyboardType"]?.let {
                when (it.toString().lowercase()) {
                    "password" -> keyboardTypePassword()
                    "number", "numeric" -> keyboardTypeNumber()
                    "email" -> keyboardTypeEmail()
                }
            }
            props["variant"]?.let {
                when (it.toString().lowercase()) {
                    "obscured", "password" -> keyboardTypePassword()
                    "email" -> keyboardTypeEmail()
                    "number", "numeric" -> keyboardTypeNumber()
                }
            }
            props["returnKeyType"]?.let { "returnKeyType" with it.toString() }
            props["maxTextLength"]?.let { maxTextLength(toFloat(it).toInt()) }
            props["autofocus"]?.let {
                val autoFocus = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                if (autoFocus) autofocus(true)
            }
            props["tintColor"]?.let { tintColor(toColor(it)) }
        }

        // Register two-way binding: store binding path and register event
        val bindingPath = props["__textFieldBindingPath"] as? String
        if (bindingPath != null) {
            val tag = host.registry.getTag(view) ?: return
            // Only register event once (first time)
            if (!textFieldBindingPaths.containsKey(tag)) {
                view.event {
                    textDidChange { params ->
                        val dataKey = textFieldBindingPaths[tag] ?: return@textDidChange
                        onTextFieldValueChanged?.invoke(tag, dataKey, params.text)
                    }
                }
            }
            textFieldBindingPaths[tag] = bindingPath
        }
    }

    /**
     * Apply text-area-specific properties to a TextAreaView (multi-line input).
     * Handles value/placeholder and two-way binding, similar to applyTextFieldProps
     * but for the multi-line variant (variant="longText").
     */
    private fun applyTextAreaProps(view: TextAreaView, props: Map<String, Any?>) {
        view.attr {
            props["text"]?.let { text(it.toString()) }
            props["placeholder"]?.let { placeholder(it.toString()) }
            props["placeholderColor"]?.let { placeholderColor(toColor(it)) }
            props["fontSize"]?.let { fontSize(toFloat(it)) }
            props["color"]?.let { color(toColor(it)) }
            props["fontWeight"]?.let {
                when (it.toString().lowercase()) {
                    "bold", "700" -> fontWeightBold()
                    else -> {} // normal is default
                }
            }
            props["textAlign"]?.let {
                when (it.toString().lowercase()) {
                    "center" -> textAlignCenter()
                    "right" -> textAlignRight()
                    else -> textAlignLeft()
                }
            }
            props["lineHeight"]?.let { lineHeight(toFloat(it)) }
            props["editable"]?.let {
                val editableValue = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                editable(editableValue)
            }
            props["keyboardType"]?.let {
                when (it.toString().lowercase()) {
                    "password" -> keyboardTypePassword()
                    "number", "numeric" -> keyboardTypeNumber()
                    "email" -> keyboardTypeEmail()
                }
            }
            props["returnKeyType"]?.let { "returnKeyType" with it.toString() }
            props["maxTextLength"]?.let { maxTextLength(toFloat(it).toInt()) }
            props["autofocus"]?.let {
                val autoFocus = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                if (autoFocus) autofocus(true)
            }
            props["tintColor"]?.let { tintColor(toColor(it)) }
        }

        // Register two-way binding: store binding path and register event
        val bindingPath = props["__textFieldBindingPath"] as? String
        if (bindingPath != null) {
            val tag = host.registry.getTag(view) ?: return
            // Only register event once (first time)
            if (!textFieldBindingPaths.containsKey(tag)) {
                view.event {
                    textDidChange { params ->
                        val dataKey = textFieldBindingPaths[tag] ?: return@textDidChange
                        onTextFieldValueChanged?.invoke(tag, dataKey, params.text)
                    }
                }
            }
            textFieldBindingPaths[tag] = bindingPath
        }
    }

    /**
     * Apply slider-specific properties to an [A2UISliderView] (which wraps kuikly's SliderView
     * and owns "stretch width + measured-size backfill" so kuikly's SliderView stays untouched).
     * Handles value normalization (protocol value range [min,max] → slider [0,1]).
     * Also registers two-way binding if a binding path is provided.
     */
    private fun applySliderProps(view: A2UISliderView, props: Map<String, Any?>) {
        val tag = host.registry.getTag(view)

        // When called from updateDataModel re-evaluation, props may only contain "value"
        // without min/max. Fall back to stored binding info for correct normalization.
        val storedInfo = if (tag != null) sliderBindingPaths[tag] else null
        val max = toFloat(props["max"] ?: storedInfo?.max ?: 1f)
        val min = toFloat(props["min"] ?: storedInfo?.min ?: 0f)
        val rawValue = toFloat(props["value"] ?: 0f)

        // PR-9: v1.0 `steps` support. We treat `steps` as the number of intervals
        // (same convention as HTML `<input type="range" step>` when combined with
        // min/max/value): `steps = N` produces `N + 1` legal stops evenly spaced
        // in [min, max]. Non-positive or absent -> continuous slider (pre-PR-9).
        // We parse once here, keep it on SliderBindingInfo so later data-driven
        // re-applies (updateDataModel path) and the drag-time progressDidChanged
        // handler can both snap against the same number.
        val steps: Int? = (props["steps"] ?: storedInfo?.steps)?.let { v ->
            when (v) {
                is Number -> v.toInt().takeIf { it > 0 }
                is String -> v.toIntOrNull()?.takeIf { it > 0 }
                else -> null
            }
        }
        val value = snapSliderValue(rawValue, min, max, steps)

        view.attr {
            // Normalize value from [min, max] to [0, 1]
            val normalizedProgress = if (max > min) (value - min) / (max - min) else 0f
            currentProgress(normalizedProgress.coerceIn(0f, 1f))

            // Color customization
            props["progressColor"]?.let { progressColor(toColor(it)) }
            props["trackColor"]?.let { trackColor(toColor(it)) }
            props["thumbColor"]?.let { thumbColor(toColor(it)) }

            // Track thickness
            props["trackThickness"]?.let { trackThickness(toFloat(it)) }

            // Thumb size
            props["thumbSize"]?.let {
                val size = toFloat(it)
                thumbSize(Size(size, size))
            }
        }

        // Register two-way binding: store binding path and register event
        val bindingPath = props["__sliderBindingPath"] as? String
        if (bindingPath != null) {
            val effectiveTag = tag ?: (host.registry.getTag(view) ?: return)
            val bindingInfo = SliderBindingInfo(
                dataKey = bindingPath,
                min = min,
                max = max,
                steps = steps
            )
            // Only register event once (first time)
            if (!sliderBindingPaths.containsKey(effectiveTag)) {
                view.event {
                    progressDidChanged { progress ->
                        // Denormalize: convert [0,1] back to [min, max]
                        val info = sliderBindingPaths[effectiveTag] ?: return@progressDidChanged
                        val denormalized = info.min + progress * (info.max - info.min)
                        // PR-9: snap the user-driven value to the nearest legal
                        // step before reporting back to the host. This keeps the
                        // bound value quantised regardless of how fine-grained
                        // the pan gesture was.
                        val snapped = snapSliderValue(denormalized, info.min, info.max, info.steps)
                        onSliderValueChanged?.invoke(effectiveTag, info.dataKey, snapped)
                    }
                }
            }
            sliderBindingPaths[effectiveTag] = bindingInfo
        }
    }

    /**
     * PR-9: Snap `value` to the nearest step of a v1.0 Slider.
     *
     *   stepValue = (max - min) / steps
     *   snapped   = min + round((value - min) / stepValue) * stepValue
     *
     * The result is clamped to [min, max] so floating-point drift at the
     * boundaries can never escape the legal range. When `steps` is null,
     * zero, or negative, the function is a pass-through (continuous slider
     * behaviour matches pre-PR-9).
     */
    private fun snapSliderValue(value: Float, min: Float, max: Float, steps: Int?): Float {
        if (steps == null || steps <= 0 || max <= min) return value
        val stepValue = (max - min) / steps.toFloat()
        if (stepValue <= 0f) return value
        val clamped = value.coerceIn(min, max)
        val n = kotlin.math.round((clamped - min) / stepValue)
        return (min + n * stepValue).coerceIn(min, max)
    }

    /**
     * Apply checkbox-specific properties to a CheckBoxView.
     * Handles boolean value binding and checked state.
     * Also registers two-way binding if a binding path is provided.
     *
     * Since A2UI protocol doesn't provide image URLs for checkbox states,
     * we use checkedViewCreator/defaultViewCreator with unicode characters (☑/☐).
     */
    private fun applyCheckBoxProps(view: CheckBoxView, props: Map<String, Any?>) {
        val checked = when (val v = props["checked"]) {
            is Boolean -> v
            is Number -> v.toInt() != 0
            is String -> v == "true" || v == "1"
            else -> false
        }

        val tag = host.registry.getTag(view)
        val firstApply = tag != null && !checkBoxCreatorRegistered.contains(tag)

        if (firstApply) {
            // First-time setup: register glyph creators with theme-derived colors.
            // Incremental updates (updateDataModel diff) only carry `checked` and must
            // not re-run this block, otherwise the captured primaryColor would fall
            // back to the default #00BFFF (theme color would be lost after every diff).
            val primaryColorHex = props["__primaryColor"] as? String ?: "#00BFFF"
            val primaryColor = toColor(primaryColorHex)
            checkBoxCreatorRegistered.add(tag!!)

            view.attr {
                checked(checked)

                // Provide unicode-based view creators for visual display
                checkedViewCreator {
                    RichText {
                        attr {
                            flex(1f)
                            text("☑")
                            fontSize(20f)
                            textAlignCenter()
                            color(primaryColor)
                        }
                    }
                }
                defaultViewCreator {
                    RichText {
                        attr {
                            flex(1f)
                            text("□")
                            fontSize(20f)
                            textAlignCenter()
                            color(Color(0xFF757575.toInt()))
                        }
                    }
                }
            }
        } else {
            // Incremental update: only sync the checked state, keep the previously
            // registered creators (and their captured theme color) intact.
            view.attr {
                checked(checked)
            }
        }

        // Register two-way binding: store binding path and register event.
        // Reuse the `tag` captured above (may be null if the view hasn't been mounted yet;
        // in that case binding registration is skipped, matching the previous behavior).
        val bindingPath = props["__checkBoxBindingPath"] as? String
        if (bindingPath != null && tag != null) {
            // Only register event once (first time)
            if (!checkBoxBindingPaths.containsKey(tag)) {
                view.event {
                    checkedDidChanged { newChecked ->
                        val dataKey = checkBoxBindingPaths[tag] ?: return@checkedDidChanged
                        onCheckBoxValueChanged?.invoke(tag, dataKey, newChecked)
                    }
                }
            }
            checkBoxBindingPaths[tag] = bindingPath
        }
    }

    /**
     * Apply DateTimeInput-specific properties to a DateTimeInputView.
     * Handles ISO 8601 value parsing, enableDate/enableTime flags, and native picker trigger.
     */
    private fun applyDateTimeInputProps(view: DateTimeInputView, props: Map<String, Any?>) {
        // Set enableDate/enableTime flags via public methods
        props["enableDate"]?.let {
            view.setEnableDate(when (it) {
                is Boolean -> it
                is String -> it == "true"
                else -> true
            })
        }
        props["enableTime"]?.let {
            view.setEnableTime(when (it) {
                is Boolean -> it
                is String -> it == "true"
                else -> false
            })
        }
        props["label"]?.let {
            view.setLabel(it.toString())
        }

        // Store min/max constraints
        props["min"]?.let { view.setMinValue(it.toString()) }
        props["max"]?.let { view.setMaxValue(it.toString()) }

        // Set the date/time value from ISO 8601 string
        props["value"]?.let {
            view.setDateTime(it.toString())
        }

        // Register click handler to trigger native picker (regardless of binding)
        val bindingPath = props["__dateTimeBindingPath"] as? String
        val tag = host.registry.getTag(view) ?: return
        // Only register click handler once (first time)
        if (!dateTimeBindingPaths.containsKey(tag) && !dateTimeStaticTags.contains(tag)) {
            view.setOnClickListener {
                // Request native picker dialog with min/max constraints
                onShowDateTimePicker?.invoke(
                    tag,
                    view.getEnableDate(),
                    view.getEnableTime(),
                    view.getISOString(),
                    view.getLabel(),
                    view.getMinValue(),
                    view.getMaxValue()
                )
            }
        }
        if (bindingPath != null) {
            dateTimeBindingPaths[tag] = bindingPath
        } else {
            // Track static (non-bound) DateTimeInput tags to avoid re-registering
            dateTimeStaticTags.add(tag)
        }
    }

    /**
     * Update a DateTimeInputView's value from native picker result.
     * Called by A2UIDSLBridge when the native picker returns a result.
     */
    fun updateDateTimeInputValue(tag: Int, isoString: String) {
        val view = host.registry.getView(tag) as? DateTimeInputView ?: return
        view.setDateTime(isoString)
    }

    /**
     * Get the binding path for a DateTimeInput component.
     */
    fun getDateTimeBindingPath(tag: Int): String? {
        return dateTimeBindingPaths[tag]
    }

    /**
     * Apply image-specific properties to an ImageView.
     */
    private fun applyImageProps(view: ImageView, props: Map<String, Any?>) {
        view.attr {
            props["src"]?.let { src(it.toString()) }
            // A2UI `Image.description` is the accessibility text (equivalent to HTML
            // <img alt>). The mapper emits it under `__accessibility`; the adapter's
            // generic prop loop has already flattened any DynamicString (path/reference/
            // function-call) form into a plain String via resolveValue. Empty strings
            // (which resolveValue also returns for a not-yet-resolved PathBinding) are
            // skipped so we don't overwrite a previously set value with "" on data-model
            // updates during initial paint. Kuikly's `accessibility(...)` maps to
            // Android contentDescription and iOS accessibilityLabel.
            (props["__accessibility"] as? String)?.takeIf { it.isNotEmpty() }?.let {
                accessibility(it)
            }
            // Read "resizeMode" — this is the key emitted by A2UIComponentMapper.mapImageProp
            // ("fit" -> "resizeMode") and also written directly by A2UIProtocolAdapter for
            // variant sizing (mediumFeature/largeFeature/header). Kept aligned with
            // applyVideoProps which also reads "resizeMode". (Historically this branch read
            // "resize" and silently no-op'd for all JSON-driven fit values.)
            props["resizeMode"]?.let {
                when (it.toString().lowercase()) {
                    "cover" -> resizeCover()
                    "contain" -> resizeContain()
                    "stretch", "fill" -> resizeStretch()
                    // "center" would correspond to CSS object-fit: none (render at intrinsic
                    // size, no scaling, centered). Kuikly Core's ImageAttr only exposes
                    // cover/contain/stretch, so there is no native center mode — the mapper
                    // (see A2UIComponentMapper.mapImageFit) downgrades A2UI fit=none to
                    // "contain" instead, and this branch should never be hit from that path.
                    // Keep an explicit fall-through to contain as a defensive default.
                    "center" -> resizeContain()
                }
            }
            props["placeholder"]?.let { "placeholder" with it.toString() }
            props["placeholderSrc"]?.let { "placeholder" with it.toString() }
            props["blurRadius"]?.let { blurRadius(toFloat(it)) }
            props["tintColor"]?.let { tintColor(toColor(it)) }
            props["capInsets"]?.let { insets ->
                if (insets is Map<*, *>) {
                    val top = toFloat(insets["top"])
                    val left = toFloat(insets["left"])
                    val bottom = toFloat(insets["bottom"])
                    val right = toFloat(insets["right"])
                    capInsets(top, left, bottom, right)
                }
            }
            props["maskLinearGradient"]?.let { mask ->
                if (mask is Map<*, *>) {
                    // Pass as raw prop string for native layer to parse
                    "maskLinearGradient" with mask.toString()
                }
            }
        }

        // Feature-variant auto-size rule (variant=mediumFeature|largeFeature, non-Row parent):
        //   width  = min(image.intrinsicWidth, MAX_W dp)   — MAX_W is optional
        //   height = width * (imgH / imgW), clamped to MAX_H dp (excess CROPPED via cover)
        //
        // Two behaviors depending on whether the width cap is specified:
        //   - With `__featureAutoSizeMaxWidthDp` (mediumFeature, cap=300):
        //       image is clamped to that width and CENTERED on the parent's cross axis
        //       (alignSelf=CENTER), so when the parent is wider than MAX_W it sits in
        //       the middle.
        //   - Without `__featureAutoSizeMaxWidthDp` (largeFeature):
        //       image renders at its intrinsic width — but still clamped to the parent's
        //       inner content width (parent.width - parent.paddingH - self.marginH) so
        //       it never overflows the container. Leading-aligned (alignSelf=AUTO), i.e.
        //       no centering. Height is still clamped to MAX_H.
        //
        // The adapter can't know the intrinsic size at buildProps time, so it emits a
        // safe placeholder (alignSelf=stretch, height=MAX_H, resize=cover) plus the
        // marker prop(s):
        //   `__featureAutoSizeMaxHeightDp=<height cap in dp>` (200 mediumFeature / 400 largeFeature)
        //   `__featureAutoSizeMaxWidthDp=<width cap in dp>`  (optional; 300 for mediumFeature)
        // We wire loadResolution once and rewrite the geometry when the real size is known.
        //
        // Three sizing modes share this marker set:
        //   - header (__featureAutoSizeHeader=true): keep alignSelf=stretch, set
        //     height = min(availableWidth * imgH/imgW, maxHeightDp). Full-width banner.
        //   - mediumFeature (__featureAutoSizeMaxWidthDp set, no header flag):
        //     clamp width to min(imgW, cap, availW), center on cross axis (alignSelf=CENTER).
        //   - largeFeature (no width cap, no header flag):
        //     width = min(imgW, availW), leading-aligned (alignSelf=AUTO).
        //
        // smallFeature reuses the mediumFeature path with cap=100×100.
        val autoSizeMaxHeight = (props["__featureAutoSizeMaxHeightDp"] as? Number)?.toFloat()
        if (autoSizeMaxHeight != null && autoSizeMaxHeight > 0f) {
            val isHeader = props["__featureAutoSizeHeader"] == true
            // Width cap is optional. Present  -> clamp + center (mediumFeature/smallFeature).
            //                       Absent   -> no width cap, no centering (largeFeature).
            val autoSizeMaxWidth =
                (props["__featureAutoSizeMaxWidthDp"] as? Number)?.toFloat()
            val tag = host.registry.getTag(view)
            if (tag != null && !imageAutoSizeRegistered.contains(tag)) {
                imageAutoSizeRegistered.add(tag)
                view.event {
                    loadResolution { params ->
                        if (isHeader) {
                            applyHeaderAutoSize(
                                view,
                                params.width,
                                params.height,
                                autoSizeMaxHeight
                            )
                        } else {
                            applyFeatureAutoSize(
                                view,
                                params.width,
                                params.height,
                                autoSizeMaxWidth,
                                autoSizeMaxHeight
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Grow a `variant=header` Image's height from its natural resolution once loaded,
     * while keeping `alignSelf=stretch` so the banner always fills the parent's
     * cross-axis width (minus the image's own horizontal margin).
     *
     * Rules (in dp):
     *   - width: unchanged (alignSelf=stretch still fills the parent's available width)
     *   - height = availableWidth * (imgH / imgW), clamped to [maxHeightDp]
     *     (the spec recommends 200dp for `header`). If the parent's available width
     *     can't be resolved (parent not yet laid out), fall back to the max height
     *     so the banner still becomes visible instead of staying at 0dp.
     *   - alignSelf stays `stretch` (banner style). We do NOT switch to AUTO/CENTER
     *     here, that's the mediumFeature/largeFeature behavior.
     *
     * This path exists so that the adapter can emit a `height=0` placeholder for
     * `variant=header` (streaming-friendly: no vertical real estate reserved before
     * the image resolves) without the layout "jumping" when the image lands \u2014 instead
     * of using a fixed 200dp placeholder which would reserve space and then look fine,
     * `height=0` makes the banner invisible until it has real content, and this method
     * is what grows it to the correct height in a single layout pass.
     *
     * @param imageWidth  natural image width  from loadResolution callback (treated as dp)
     * @param imageHeight natural image height from loadResolution callback (treated as dp)
     * @param maxHeightDp height cap in dp (typically 200 for `header`)
     */
    private fun applyHeaderAutoSize(
        view: ImageView,
        imageWidth: Int,
        imageHeight: Int,
        maxHeightDp: Float
    ) {
        if (imageWidth <= 0 || imageHeight <= 0) return

        val intrinsicWidthDp = imageWidth.toFloat()
        val intrinsicHeightDp = imageHeight.toFloat()
        val aspectRatio = intrinsicHeightDp / intrinsicWidthDp

        // Stretch mode: width is dictated by the parent, so compute the natural height
        // the image would take at the parent's content width. If unknown, fall back to
        // the cap so the banner still shows up at a sensible size.
        val availableWidthDp = computeParentAvailableWidthDp(view)
        val naturalHeightDp = if (availableWidthDp != null && availableWidthDp > 0f) {
            availableWidthDp * aspectRatio
        } else {
            maxHeightDp
        }
        val targetHeightDp = kotlin.math.min(naturalHeightDp, maxHeightDp)

        view.attr {
            alignSelf(FlexAlign.STRETCH)
            height(targetHeightDp)
        }

        // Same rationale as applyFeatureAutoSize: without an in-flight animation the
        // DSL's attr path marks the flex tree dirty but doesn't re-run layout, so the
        // parent Card keeps its 0-height-derived size. Kick a layout pass so the
        // banner's new height propagates to the host's reported content height.
        host.performLayoutAndReportHeight()
    }

    /**
     * Recompute an Image's geometry from its natural resolution once loaded, for the
     * `mediumFeature` / `largeFeature` variant sizing spec (see [applyImageProps]).
     *
     * Rules (in dp):
     *   - When [maxWidthDp] is non-null (mediumFeature, cap=300):
     *       `w = min(imgWidth, maxWidthDp, availableWidth)` — width prefers the image's
     *       intrinsic width but is capped at the fixed maximum AND the parent's available
     *       content width, so the image never overflows a narrow container. The image is
     *       centered on the parent's cross axis (`alignSelf=CENTER`), so when the parent
     *       container is wider than the computed width, the image sits in the middle
     *       rather than at the leading edge.
     *   - When [maxWidthDp] is null (largeFeature):
     *       `w = min(imgWidth, availableWidth)` — no fixed cap, but clamp to the parent's
     *       inner content width (parent.layoutFrame.width - parent.paddingH - self.marginH)
     *       so a wide asset never overflows a padded Card. When the parent isn't laid
     *       out or the available width can't be resolved, fall back to `imgWidth`. The
     *       image is leading-aligned (`alignSelf=AUTO`), i.e. no centering.
     *   - In both cases: `h = w * (imgH / imgW)`; if `h > maxHeightDp`, clamp to
     *     `maxHeightDp` (the excess is cropped by resize=cover, which is already set
     *     by the adapter). The cap differs per variant: 200dp for mediumFeature, 400dp
     *     for largeFeature.
     *
     * Note on units: the render layer's `loadResolution` callback reports width/height in
     * platform-native units (iOS: point count for network images with UIImage.scale=1;
     * Android: bitmap pixel count). Both platforms treat these values as the image's
     * "natural size" without applying screen density scaling — we follow the same
     * convention here and treat the incoming numbers as dp directly, so the width/height
     * cap comparisons apply consistently against the image's own coordinate space.
     *
     * @param imageWidth  natural image width  from loadResolution callback (treated as dp)
     * @param imageHeight natural image height from loadResolution callback (treated as dp)
     * @param maxWidthDp  optional width cap in dp; null means no cap and no centering
     *                    (e.g. 300 for mediumFeature; null for largeFeature)
     * @param maxHeightDp variant-specific height cap in dp (e.g. 200 for mediumFeature,
     *                    400 for largeFeature)
     */
    private fun applyFeatureAutoSize(
        view: ImageView,
        imageWidth: Int,
        imageHeight: Int,
        maxWidthDp: Float?,
        maxHeightDp: Float
    ) {
        if (imageWidth <= 0 || imageHeight <= 0) return

        val intrinsicWidthDp = imageWidth.toFloat()
        val intrinsicHeightDp = imageHeight.toFloat()

        // With a width cap  -> clamp to it (mediumFeature), but also clamp to the
        //                      parent's available width so the image never overflows
        //                      a narrow container (e.g. a Column inside a Row with
        //                      weight=1 on a phone screen may be < 300dp wide).
        // Without           -> use the image's own width, but clamp to the parent's
        //                      inner content width minus this image's own horizontal
        //                      margin (largeFeature). This prevents overflow when the
        //                      image is naturally wider than the parent's usable area
        //                      (e.g. a Card with padding=16 wrapping an Image with
        //                      margin=8 on a 375dp-wide surface: available = 375 - 32
        //                      - 16 = 327dp, so a 600px-wide asset must shrink to fit).
        val targetWidthDp = if (maxWidthDp != null) {
            val cappedWidth = kotlin.math.min(intrinsicWidthDp, maxWidthDp)
            // Also clamp to parent's available width to prevent overflow in narrow containers
            val availableWidthDp = computeParentAvailableWidthDp(view)
            if (availableWidthDp != null && availableWidthDp > 0f) {
                kotlin.math.min(cappedWidth, availableWidthDp)
            } else {
                cappedWidth
            }
        } else {
            val availableWidthDp = computeParentAvailableWidthDp(view)
            if (availableWidthDp != null && availableWidthDp > 0f) {
                kotlin.math.min(intrinsicWidthDp, availableWidthDp)
            } else {
                intrinsicWidthDp
            }
        }
        val aspectRatio = intrinsicHeightDp / intrinsicWidthDp
        val naturalHeightDp = targetWidthDp * aspectRatio
        val targetHeightDp = kotlin.math.min(naturalHeightDp, maxHeightDp)

        // Overwrite the placeholder geometry:
        //   - alignSelf: replace the placeholder's `stretch`.
        //       CENTER when a width cap is set (mediumFeature) — center on cross axis.
        //       AUTO   when no cap is set (largeFeature) — leading-aligned, no centering.
        //   - width/height: lock in the computed dp size (natural size, capped).
        val targetAlign = if (maxWidthDp != null) FlexAlign.CENTER else FlexAlign.AUTO
        view.attr {
            alignSelf(targetAlign)
            width(targetWidthDp)
            height(targetHeightDp)
        }

        // The above `view.attr {}` reaches Attr.width/height, which set flexNode.styleWidth/
        // Height and mark the node dirty — but the DSL's normal attr path only re-runs the
        // pager layout pass when an animation is in flight (see Attr.endApplyAttrProperty).
        // Without an animation, the flex tree stays dirty and ancestors never re-measure,
        // so the parent (e.g. Card) keeps the placeholder-derived height instead of
        // shrinking to hug the newly sized image. Explicitly kick a layout pass here so
        // Card and any wrapping containers re-collapse to the image's real height, and
        // the pager reports the updated content height back to the native host.
        host.performLayoutAndReportHeight()
    }

    /**
     * Compute the horizontal space available to [view] inside its parent flex node, in
     * dp. Used by [applyFeatureAutoSize] (largeFeature branch) to keep the image from
     * overflowing when its intrinsic width is wider than the parent's usable content
     * area.
     *
     * Formula:
     *   available = parent.layoutFrame.width
     *             - parent.padding(LEFT + RIGHT)
     *             - view.margin(LEFT + RIGHT)
     *
     * Returns `null` when the parent is unavailable or hasn't been laid out yet
     * (layoutFrame.width == 0), so callers can fall back to using the image's own
     * intrinsic width instead of clamping to 0.
     *
     * Note: this reads dp values because loadResolution runs after the initial layout
     * pass, so the parent's layoutFrame is already populated with the placeholder
     * geometry. Padding/margin are read from the flex style directly (they don't
     * depend on layout timing).
     */
    private fun computeParentAvailableWidthDp(view: ImageView): Float? {
        val parentNode = view.flexNode.parent ?: return null
        val parentWidth = parentNode.layoutFrame.width
        if (parentWidth <= 0f) return null
        val parentPaddingH = parentNode.getPadding(StyleSpace.Type.LEFT) +
            parentNode.getPadding(StyleSpace.Type.RIGHT)
        val selfMarginH = view.flexNode.getMargin(StyleSpace.Type.LEFT) +
            view.flexNode.getMargin(StyleSpace.Type.RIGHT)
        return parentWidth - parentPaddingH - selfMarginH
    }

    /**
     * Apply video-specific properties to a VideoView.
     *
     * The A2UI v0.9 Video component only carries a `url` (mapped to `src` by
     * A2UIComponentMapper). The native KRVideoView lazily creates the underlying
     * player via the host-registered krVideoViewAdapter once `src` is set and the
     * view has non-zero bounds (height comes from buildProps' default of 200dp).
     */
    private fun applyVideoProps(view: VideoView, props: Map<String, Any?>) {
        view.attr {
            props["src"]?.let { src(it.toString()) }
            when (props["resizeMode"]?.toString()?.lowercase()) {
                "cover" -> resizeModeToCover()
                "contain" -> resizeModeToContain()
                "stretch", "fill" -> resizeModeToStretch()
                // Per A2UI guide: render full-width responsively; default to cover.
                else -> resizeModeToCover()
            }
            props["muted"]?.let {
                val isMuted = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> false
                }
                muted(isMuted)
            }
            props["playControl"]?.let {
                when (it.toString().lowercase()) {
                    "play" -> playControl(VideoPlayControl.PLAY)
                    "pause" -> playControl(VideoPlayControl.PAUSE)
                    "stop" -> playControl(VideoPlayControl.STOP)
                }
            }
            props["rate"]?.let { rate(toFloat(it)) }
            // PR-9: v1.0 `posterUrl` — forward as a generic native prop so a
            // host-registered krVideoViewAdapter can pick it up via
            // setProp("posterUrl", ...). Hosts that don't consume it see no
            // behaviour change (setProp returns false; Kuikly drops it).
            props["posterUrl"]?.toString()?.takeIf { it.isNotEmpty() }?.let {
                "posterUrl" with it
            }
        }
    }

    /**
     * Apply audio-specific properties to an AudioView.
     *
     * The A2UI v0.9 AudioPlayer component carries a `url` (mapped to `src` by
     * A2UIComponentMapper) and an optional `description` (rendered as a separate Text
     * above the player in buildComponentTree). The native KRAudioView lazily creates
     * the underlying player via the host-registered krAudioViewAdapter once `src` is
     * set and the view has non-zero bounds (height comes from buildProps' default).
     * Playback transport (play/pause/seek) is provided by the host adapter's native
     * controls; no playControl is auto-issued so the audio does not auto-play.
     */
    private fun applyAudioProps(view: AudioView, props: Map<String, Any?>) {
        view.attr {
            props["src"]?.let { src(it.toString()) }
            props["playControl"]?.let {
                when (it.toString().lowercase()) {
                    "play" -> playControl(AudioPlayControl.PLAY)
                    "pause" -> playControl(AudioPlayControl.PAUSE)
                    "stop" -> playControl(AudioPlayControl.STOP)
                }
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
     * Apply PAG-specific properties to a PAGView.
     */
    private fun applyPAGProps(view: PAGView, props: Map<String, Any?>) {
        view.attr {
            props["src"]?.let { src(it.toString()) }
            props["repeatCount"]?.let { repeatCount(toFloat(it).toInt()) }
            props["scaleMode"]?.let {
                when (it.toString().lowercase()) {
                    "none" -> scaleModeNone()
                    "stretch" -> scaleModeStretch()
                    "letterbox" -> scaleModeLetterBox()
                    "zoom" -> scaleModeZoom()
                }
            }
            props["autoPlay"]?.let {
                val auto = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                autoPlay(auto)
            }
        }
    }

    /**
     * Apply APNG-specific properties to an APNGVView.
     */
    private fun applyAPNGProps(view: APNGVView, props: Map<String, Any?>) {
        view.attr {
            props["src"]?.let { src(it.toString()) }
            props["repeatCount"]?.let { repeatCount(toFloat(it).toInt()) }
            props["autoPlay"]?.let {
                val auto = when (it) {
                    is Boolean -> it
                    is Number -> it.toInt() != 0
                    is String -> it == "true" || it == "1"
                    else -> true
                }
                autoPlay(auto)
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

    // endregion

    // region Private - Utility

    private fun toFloat(value: Any?): Float {
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull() ?: 0f
            else -> 0f
        }
    }

    /**
     * Convert a color value to a Color object.
     * Supports:
     * - "#RRGGBB" -> Color with alpha 0xFF
     * - "#AARRGGBB" -> Color with explicit alpha
     * - Integer/Long ARGB value (as Number or numeric String)
     */
    private fun toColor(value: Any?): Color {
        if (value == null) return Color.BLACK
        val str = value.toString()
        if (str.startsWith("#")) {
            val hex = str.removePrefix("#")
            return when (hex.length) {
                6 -> {
                    // #RRGGBB -> add FF alpha
                    val rgb = hex.toLongOrNull(16) ?: 0L
                    Color(0xFF000000L or rgb)
                }
                8 -> {
                    // #AARRGGBB
                    val argb = hex.toLongOrNull(16) ?: 0L
                    Color(argb)
                }
                else -> Color.BLACK
            }
        }
        // Try as numeric ARGB value
        val longVal = str.toLongOrNull()
        if (longVal != null) {
            return Color(longVal)
        }
        // Fallback: pass as color string (for token-based colors)
        return Color(str)
    }

    // endregion
}
