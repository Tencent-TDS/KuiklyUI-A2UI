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

package com.tencent.kuikly.a2ui.views

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.Size
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.event.PanGestureParams
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.layout.Frame
import com.tencent.kuikly.core.layout.undefined
import com.tencent.kuikly.core.layout.valueEquals
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Slider

/**
 * A2UISliderView - a2ui's wrapper around kuikly's [SliderView].
 *
 * Why this wrapper exists:
 * - kuikly's [SliderView] contract requires the caller to explicitly set width()/height()
 *   via `attr { size(...) }` before `didInit()` runs, otherwise it throws. It also sizes
 *   its internal progress / thumb strictly from `attr.sliderWidth` / `attr.sliderHeight`,
 *   and `handlePanGesture()` uses `flexNode.styleWidth` / `styleHeight` to convert gesture
 *   coordinates to progress. The implicit assumption is: "the caller sets the exact size,
 *   and the view faithfully renders at that size".
 *
 * - In the A2UI protocol most Sliders don't carry an explicit width — they are supposed to
 *   `alignSelf=stretch` and fill the parent (just like Video / AudioPlayer / full-width
 *   TextField). The real width is only known after the host Pager completes a layout pass.
 *
 * Rather than teaching kuikly's [SliderView] about "stretch + backfill" (which pollutes
 * its single-source-of-truth size contract), we let a2ui own that responsibility:
 *   1. This wrapper itself is `alignSelf=stretch`-friendly (it does not set its own width
 *      by default) and listens to its own [layoutFrameDidChanged].
 *   2. The real laid-out width/height is written into observable [measuredWidth] /
 *      [measuredHeight].
 *   3. The inner [SliderView] is created *only after* [measuredWidth] > 0 (via `vif`),
 *      so `didInit()` sees a legitimate size and the whole "temporary width + reset to
 *      NaN + backfill" dance in [com.tencent.kuikly.a2ui.dsl.A2UIApplier] goes away.
 *   4. The inner [SliderView]'s width()/height() are set from [measuredWidth] /
 *      [measuredHeight], so any subsequent re-layout automatically keeps it in sync.
 *
 * All A2UI slider props (currentProgress / colors / trackThickness / thumbSize / direction)
 * and all events (progressDidChanged / beginDragSlider / endDragSlider) are 1:1 forwarded
 * to the inner [SliderView].
 */
class A2UISliderView : ComposeView<A2UISliderAttr, A2UISliderEvent>() {

    override fun createAttr(): A2UISliderAttr = A2UISliderAttr()
    override fun createEvent(): A2UISliderEvent = A2UISliderEvent()

    /**
     * The actual laid-out width/height (dp) of this wrapper, as reported by the
     * flex engine. These start at 0 and become > 0 after the first layout pass.
     * They are `observable`, so the `vif { measuredWidth > 0 }` branch below
     * mounts/unmounts the inner [SliderView] automatically, and the inner
     * attr { width(...) height(...) } block re-runs when the measured size changes.
     */
    internal var measuredWidth: Float by observable(0f)
    internal var measuredHeight: Float by observable(0f)

    /**
     * Called by the flex engine every time this wrapper's frame is (re)computed.
     * We mirror the real size into [measuredWidth] / [measuredHeight] so the inner
     * [SliderView] can be sized against the parent-driven layout result.
     */
    override fun layoutFrameDidChanged(frame: Frame) {
        super.layoutFrameDidChanged(frame)
        if (frame.width > 0f && !frame.width.valueEquals(Float.undefined)
            && !frame.width.valueEquals(measuredWidth)) {
            measuredWidth = frame.width
        }
        if (frame.height > 0f && !frame.height.valueEquals(Float.undefined)
            && !frame.height.valueEquals(measuredHeight)) {
            measuredHeight = frame.height
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            // Defer the inner SliderView until we have a real laid-out size.
            // This keeps SliderView.didInit()'s width/height precondition happy
            // without any temporary-width tricks in a2ui's A2UIApplier.
            vif({ ctx.measuredWidth > 0f && ctx.measuredHeight > 0f }) {
                Slider {
                    attr {
                        // Real, parent-driven size.
                        width(ctx.measuredWidth)
                        height(ctx.measuredHeight)

                        // Forward all slider-shape props.
                        currentProgress(ctx.attr.currentProgress)
                        progressColor(ctx.attr.progressColor)
                        trackColor(ctx.attr.trackColor)
                        thumbColor(ctx.attr.thumbColor)
                        trackThickness(ctx.attr.trackThickness)
                        thumbSize(ctx.attr.thumbSize)
                        sliderDirection(ctx.attr.directionHorizontal)
                        enableGlassEffect(ctx.attr.enableGlassEffect)
                    }
                    event {
                        progressDidChanged { progress ->
                            // Keep wrapper attr in sync so external bindings always see
                            // the latest value even if they re-read ctx.attr.currentProgress.
                            ctx.attr.currentProgress = progress
                            ctx.event.progressDidChangedHandlerFn?.invoke(progress)
                        }
                        beginDragSlider { params ->
                            ctx.event.beginDragSliderHandlerFn?.invoke(params)
                        }
                        endDragSlider { params ->
                            ctx.event.endDragSliderHandlerFn?.invoke(params)
                        }
                    }
                }
            }
        }
    }

    /**
     * Convenience accessor mirroring kuikly [SliderView.getCurrentProgress].
     */
    fun getCurrentProgress(): Float = attr.currentProgress
}

/**
 * Mirrors [com.tencent.kuikly.core.views.SliderAttr] but owned by the a2ui wrapper so
 * callers (A2UIApplier.applySliderProps) can keep using the same DSL API they used to
 * use against SliderView directly.
 *
 * Note: width()/height() are deliberately *not* overridden to seed sliderWidth/sliderHeight
 * (unlike kuikly's SliderAttr) — the wrapper's measuredWidth/measuredHeight is driven
 * purely by the flex engine. If a caller explicitly supplies width/height here, the
 * normal Attr.width()/height() path still produces a sized wrapper, and the measured
 * values will converge to that same number after layout.
 */
class A2UISliderAttr : ComposeAttr() {
    internal var progressColor: Color by observable(Color.BLUE)
    internal var trackColor: Color by observable(Color.GRAY)
    internal var thumbColor: Color by observable(Color.WHITE)
    internal var trackThickness: Float by observable(5f)
    internal var thumbSize: Size by observable(Size(10f, 10f))
    internal var currentProgress: Float by observable(0f)
    internal var directionHorizontal: Boolean = true
    internal var enableGlassEffect: Boolean by observable(false)

    /** Set current progress percentage, range [0, 1]. */
    fun currentProgress(progress01: Float) {
        currentProgress = progress01
    }

    /** Progress-bar fill color. */
    fun progressColor(color: Color) {
        progressColor = color
    }

    /** Background track color. */
    fun trackColor(color: Color) {
        trackColor = color
    }

    /** Thumb (knob) color. */
    fun thumbColor(color: Color) {
        thumbColor = color
    }

    /** Track thickness in dp. */
    fun trackThickness(thickness: Float) {
        trackThickness = thickness
    }

    /** Thumb size (width, height). */
    fun thumbSize(size: Size) {
        thumbSize = size
    }

    /** Slider orientation. Horizontal by default. */
    fun sliderDirection(horizontal: Boolean) {
        directionHorizontal = horizontal
    }

    /**
     * Enable iOS liquid-glass effect (only effective on iOS 26.0+).
     * Forwarded to the inner [SliderView].
     */
    fun enableGlassEffect(enabled: Boolean) {
        enableGlassEffect = enabled
    }
}

/**
 * Mirrors [com.tencent.kuikly.core.views.SliderEvent].
 */
class A2UISliderEvent : ComposeEvent() {
    internal var progressDidChangedHandlerFn: ((progress: Float) -> Unit)? = null
    internal var beginDragSliderHandlerFn: ((params: PanGestureParams) -> Unit)? = null
    internal var endDragSliderHandlerFn: ((params: PanGestureParams) -> Unit)? = null

    /** Fired whenever the progress value changes. */
    fun progressDidChanged(handlerFn: (progress: Float) -> Unit) {
        progressDidChangedHandlerFn = handlerFn
    }

    /** Fired when the user starts dragging the thumb. */
    fun beginDragSlider(handlerFn: (params: PanGestureParams) -> Unit) {
        beginDragSliderHandlerFn = handlerFn
    }

    /** Fired when the user ends dragging the thumb. */
    fun endDragSlider(handlerFn: (params: PanGestureParams) -> Unit) {
        endDragSliderHandlerFn = handlerFn
    }
}

/**
 * DSL function to create an [A2UISliderView].
 */
fun ViewContainer<*, *>.A2UISlider(init: A2UISliderView.() -> Unit) {
    addChild(A2UISliderView(), init)
}
