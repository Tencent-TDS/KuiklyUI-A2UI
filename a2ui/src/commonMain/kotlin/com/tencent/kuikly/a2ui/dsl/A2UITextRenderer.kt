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
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.StyleSpace
import com.tencent.kuikly.core.views.ISpan
import com.tencent.kuikly.core.views.RichTextView
import com.tencent.kuikly.core.views.Span
import com.tencent.kuikly.a2ui.markdown.MarkdownParser

import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toFloat
import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toColor

@Suppress("DEPRECATION")
internal class A2UITextRenderer(private val registry: A2UIViewRegistry, private val layout: A2UILayoutEffects, private val callbacks: A2UIEventCallbacks) {
    private val rowTextWidthAppliedTags = mutableSetOf<Int>()
    private val rowTextHiddenTags = mutableSetOf<Int>()
    fun remove(tag: Int) {
        rowTextWidthAppliedTags.remove(tag)
        rowTextHiddenTags.remove(tag)
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
    fun apply(view: RichTextView, props: Map<String, Any?>) {
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
        val tag = if (isRowText) registry.getTag(view) else null
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
            layout.performLayout()
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
            val tag = registry.getTag(view)
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
        layout.performLayout()
        view.setFrameToRenderView(view.flexNode.layoutFrame)
        view.updateShadow()
        registry.getTag(view)?.let { rowTextWidthAppliedTags.add(it) }
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
                    click { callbacks.onMarkdownLinkClick?.invoke(link) }
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
        val tag = if (isRowText) registry.getTag(view) else null
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
            layout.performLayout()
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

    fun enqueueCorrections(view: DeclarativeBaseView<*, *>, props: Map<String, Any?>) {
        val textAlignPostLayout = props["__textAlignPostLayout"] as? String
        if (textAlignPostLayout != null && view is RichTextView) {
            layout.addTaskWhenLayoutFinish {
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
            layout.addTaskWhenLayoutFinish {
                applyRowTextWidth(view, props)
            }
        }
    }
}
