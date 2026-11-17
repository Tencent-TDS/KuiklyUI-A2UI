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

import com.tencent.kuikly.core.base.Size
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.StyleSpace
import com.tencent.kuikly.core.views.ImageView
import com.tencent.kuikly.core.views.AudioView
import com.tencent.kuikly.core.views.AudioPlayControl
import com.tencent.kuikly.core.views.PAGView
import com.tencent.kuikly.core.views.APNGVView
import com.tencent.kuikly.core.views.VideoView
import com.tencent.kuikly.core.views.VideoPlayControl

import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toFloat
import com.tencent.kuikly.a2ui.dsl.A2UIRenderValues.toColor

@Suppress("DEPRECATION")
internal class A2UIMediaRenderer(private val registry: A2UIViewRegistry, private val layout: A2UILayoutEffects) {
    private val imageAutoSizeRegistered = mutableSetOf<Int>()
    fun remove(tag: Int) { imageAutoSizeRegistered.remove(tag) }
    /**
     * Apply image-specific properties to an ImageView.
     */
    fun applyImageProps(view: ImageView, props: Map<String, Any?>) {
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
            val tag = registry.getTag(view)
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
        layout.performLayout()
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
        layout.performLayout()
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
    fun applyVideoProps(view: VideoView, props: Map<String, Any?>) {
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
    fun applyAudioProps(view: AudioView, props: Map<String, Any?>) {
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
     * Apply PAG-specific properties to a PAGView.
     */
    fun applyPAGProps(view: PAGView, props: Map<String, Any?>) {
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
    fun applyAPNGProps(view: APNGVView, props: Map<String, Any?>) {
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
}
