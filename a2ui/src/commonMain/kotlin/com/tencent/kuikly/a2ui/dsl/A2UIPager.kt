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

import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod
import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager

/**
 * A2UIPager - The core container that bridges A2UI JSON protocol to Kuikly DSL component tree.
 *
 * This class extends Kuikly's [Pager] to leverage the full Kuikly Core rendering pipeline:
 * - FlexNode layout calculation (no custom layout logic needed)
 * - RenderView creation and management
 * - Text measurement (via RichTextView's built-in shadow)
 * - Component lifecycle management
 *
 * Design principles:
 * - Width: constrained by external container (set via pageViewWidth in PageData)
 * - Height: content-driven (WRAP_CONTENT mode per A2UI v0.9 protocol)
 *   The pageViewHeight is set to a large value (10000) so the flex engine
 *   can freely determine content height. After layout, the actual content
 *   height is reported back to the native container.
 * - Components are dynamically added/removed via [A2UIApplier]
 *
 * Threading: All operations must run on the Kuikly Context Thread.
 */
@Suppress("DEPRECATION")
internal class A2UIPager : Pager(), A2UIHost {

    /**
     * Registry that maps A2UI protocol tags ↔ Kuikly DSL views.
     * Delegated to A2UIViewRegistry so the mapping logic is shared with
     * the DSL-embedded surface (A2UISurfaceDSLView).
     */
    override val registry: A2UIViewRegistry = A2UIViewRegistry()

    /** Callback when layout calculation completes - reports content height */
    var onContentHeightChanged: ((Float) -> Unit)? = null

    /** Current surface width constraint */
    override var surfaceWidth: Float = 0f
        private set

    /** Real device screen width in dp (set by platform layer) */
    internal var deviceScreenWidth: Float = 0f
        private set

    /** Real device screen height in dp (set by platform layer) */
    internal var deviceScreenHeight: Float = 0f
        private set

    /** Last reported content height (to avoid duplicate callbacks) */
    private var lastReportedHeight: Float = 0f

    /**
     * Handle events sent from the platform layer (e.g. ArkTS render.sendEvent()).
     *
     * On HarmonyOS, events from ArkTS are routed through:
     *   ArkTS render.sendEvent() → C++ CallKotlinMethod(UPDATE_INSTANCE) →
     *   PagerManager.firePagerEvent() → this method.
     *
     * This maps the event name to the corresponding A2UIKotlinMethod and forwards
     * to A2UIBridgeManager for processing.
     *
     * On Android/iOS, this method is not called because A2UICoreEntry directly
     * calls A2UIBridgeManager.callKotlinMethod().
     */
    override fun onReceivePagerEvent(pagerEvent: String, eventData: JSONObject) {
        super.onReceivePagerEvent(pagerEvent, eventData)
        // After super handles "rootViewSizeDidChanged", it calls setupRootViewSizeStyle()
        // which sets flexNode.styleHeight to the container's actual height (e.g. 703vp).
        // A2UI uses content-driven height (wrap content), so we must reset styleHeight to NaN
        // to prevent the container height from overriding the content-driven layout.
        if (pagerEvent == Pager.PAGER_EVENT_ROOT_VIEW_SIZE_CHANGED) {
            flexNode.styleHeight = Float.NaN
            return
        }
        val data = eventData.opt("data") as? String
        val methodId = when (pagerEvent) {
            "a2ui_writeStream" -> A2UIKotlinMethod.WRITE_STREAM
            "a2ui_finishStream" -> A2UIKotlinMethod.FINISH_STREAM
            "a2ui_setSurfaceSize" -> A2UIKotlinMethod.SET_SURFACE_SIZE
            "a2ui_viewClick" -> A2UIKotlinMethod.VIEW_CLICK
            "a2ui_viewEvent" -> A2UIKotlinMethod.VIEW_EVENT
            "a2ui_pickerResult" -> A2UIKotlinMethod.PICKER_RESULT
            "a2ui_destroy" -> A2UIKotlinMethod.DESTROY
            "a2ui_registerCustomFunction" -> A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION
            "a2ui_unregisterCustomFunction" -> A2UIKotlinMethod.UNREGISTER_CUSTOM_FUNCTION
            "a2ui_registerCustomView" -> A2UIKotlinMethod.REGISTER_CUSTOM_VIEW
            "a2ui_unregisterCustomView" -> A2UIKotlinMethod.UNREGISTER_CUSTOM_VIEW
            "a2ui_registerIcon" -> A2UIKotlinMethod.REGISTER_ICON
            "a2ui_unregisterIcon" -> A2UIKotlinMethod.UNREGISTER_ICON
            else -> return
        }
        // For VIEW_EVENT, data format is "tag|eventName\ndata"
        // Split into arg1 (tagAndEvent) and arg2 (eventData)
        if (methodId == A2UIKotlinMethod.VIEW_EVENT && data != null) {
            val newlineIdx = data.indexOf('\n')
            if (newlineIdx >= 0) {
                val tagAndEvent = data.substring(0, newlineIdx)
                val eventDataStr = data.substring(newlineIdx + 1)
                A2UIBridgeManager.callKotlinMethod(methodId, pagerId, tagAndEvent, eventDataStr)
            } else {
                A2UIBridgeManager.callKotlinMethod(methodId, pagerId, data, null)
            }
        } else if (methodId == A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION && data != null) {
            // Payload format: "name\nspec"
            val newlineIdx = data.indexOf('\n')
            if (newlineIdx >= 0) {
                val name = data.substring(0, newlineIdx)
                val spec = data.substring(newlineIdx + 1)
                A2UIBridgeManager.callKotlinMethod(methodId, pagerId, name, spec)
            }
        } else if (methodId == A2UIKotlinMethod.REGISTER_ICON && data != null) {
            // Payload format: "name\nuri"
            val newlineIdx = data.indexOf('\n')
            if (newlineIdx >= 0) {
                val name = data.substring(0, newlineIdx)
                val uri = data.substring(newlineIdx + 1)
                A2UIBridgeManager.callKotlinMethod(methodId, pagerId, name, uri)
            }
        } else {
            A2UIBridgeManager.callKotlinMethod(methodId, pagerId, data, null)
        }
    }

    override fun body(): ViewBuilder {
        return {
            // Root container: column direction, width fills parent, height wraps content
            attr {
                flexDirection(FlexDirection.COLUMN)
                // Width is set by pageViewWidth from PageData
                // Height is NOT explicitly set here - the large pageViewHeight (10000)
                // acts as a max constraint, and flex engine calculates actual content height
            }
        }
    }

    /**
     * Set the surface width constraint from the platform layer.
     * Height is intentionally NOT set - it's determined by content layout.
     *
     * @param width Width in dp from the native container
     */
    fun setSurfaceWidth(width: Float) {
        if (width <= 0f || width == surfaceWidth) return
        surfaceWidth = width
        // Ensure currentPageId is set for ReactiveObserver lookup
        BridgeManager.currentPageId = pagerId
        attr {
            width(width)
        }
        // Set styleHeight to NaN (undefined) so the flex engine calculates
        // height based on content (WRAP_CONTENT / content-driven mode).
        // This overrides the fixed height set by Pager.setupRootViewSizeStyle().
        flexNode.styleHeight = Float.NaN
    }

    /**
     * Set both width and height constraints.
     * Use this when the container has a fixed height (non-WRAP_CONTENT mode).
     *
     * @param width Width in dp
     * @param height Height in dp (>= 10000 means content-driven mode)
     */
    fun setSurfaceSize(width: Float, height: Float) {
        if (width <= 0f) return
        surfaceWidth = width
        // Ensure currentPageId is set for ReactiveObserver lookup
        BridgeManager.currentPageId = pagerId
        attr {
            width(width)
            if (height > 0f && height < 10000f) {
                height(height)
            }
        }
        // If height >= 10000 or <= 0, set styleHeight to NaN (undefined)
        // so the flex engine calculates height based on content (WRAP_CONTENT mode).
        // This overrides the fixed height set by Pager.setupRootViewSizeStyle().
        if (height >= 10000f || height <= 0f) {
            flexNode.styleHeight = Float.NaN
        }
    }

    /**
     * Best-effort size (in dp) for a full-screen Modal window.
     *
     * The embedded A2UI surface is content-driven (no fixed height) and its pageData
     * may not carry device dimensions, so we prefer the real device size when present
     * and otherwise fall back to the surface width times a typical phone aspect ratio.
     * The native KRModalView container is full-screen regardless; this only sizes the
     * Kuikly flex frame used to lay out the modal's children.
     */
    internal fun fullScreenModalSizeInternal(): Pair<Float, Float> {
        val deviceWidth = if (pageData.deviceWidth > 0f) pageData.deviceWidth else deviceScreenWidth
        val deviceHeight = if (pageData.deviceHeight > 0f) pageData.deviceHeight else deviceScreenHeight
        // Prefer the real screen size; the card width (surfaceWidth) is smaller than the
        // screen and would leave the modal off-center.
        val width = when {
            deviceWidth > 0f -> deviceWidth
            surfaceWidth > 0f -> surfaceWidth
            else -> 375f
        }
        val height = if (deviceHeight > 0f) deviceHeight else width * 2.4f
        return width to height
    }

    /**
     * Update the real device screen dimensions.
     * Called by A2UIDSLBridge when the platform layer provides screen size info.
     */
    internal fun updateDeviceSize(width: Float, height: Float) {
        if (width > 0f) deviceScreenWidth = width
        if (height > 0f) deviceScreenHeight = height
    }

    /**
     * Register a view with its A2UI tag for lookup.
     * Also builds the reverse mapping from nativeRef to protocol tag.
     */
    internal fun registerView(tag: Int, view: DeclarativeBaseView<*, *>) {
        registry.register(tag, view)
    }

    /**
     * Unregister a view by tag.
     */
    internal fun unregisterView(tag: Int) {
        registry.unregister(tag)
    }

    /**
     * Get a registered view by its A2UI tag.
     */
    internal fun getViewByTag(tag: Int): DeclarativeBaseView<*, *>? {
        return registry.getView(tag)
    }

    /**
     * Get a registered container view by its A2UI tag.
     */
    internal fun getContainerByTag(tag: Int): ViewContainer<*, *>? {
        return registry.getContainer(tag)
    }

    /**
     * Get the A2UI protocol tag for a given view instance.
     * Used for two-way binding registration.
     */
    internal fun getTagByView(view: DeclarativeBaseView<*, *>): Int? {
        return registry.getTag(view)
    }

    /**
     * Look up the A2UI protocol tag from a Kuikly Core nativeRef (render view tag).
     * This is needed because the native click handler reports nativeRef,
     * but the action system uses protocol tags.
     *
     * @param nativeRef The render view tag reported by the native layer
     * @return The A2UI protocol tag, or null if not found
     */
    internal fun getProtocolTagByNativeRef(nativeRef: Int): Int? {
        return registry.getTagByNativeRef(nativeRef)
    }

    // region A2UIHost implementation

    /**
     * A2UIHost#hostPagerId — for the legacy native path, the pager itself owns
     * the surface, so we return the pager's own id.
     */
    override val hostPagerId: String
        get() = pagerId

    /**
     * A2UIHost#rootContainer — the pager acts as its own root container.
     * Tag=0 in A2UIApplier resolves to this.
     */
    override val rootContainer: ViewContainer<*, *>
        get() = this

    /**
     * A2UIHost#fullScreenModalSize — delegates to the existing internal helper.
     */
    override fun fullScreenModalSize(): Pair<Float, Float> = fullScreenModalSizeInternal()

    /**
     * A2UIHost#addTaskWhenLayoutFinish — forwards to Pager's post-layout queue.
     */
    override fun addTaskWhenLayoutFinish(task: () -> Unit) {
        addTaskWhenPagerUpdateLayoutFinish(task)
    }

    // endregion

    /**
     * Override onLayoutView to report content height after every layout pass.
     *
     * The base Pager.onLayoutView() calls layoutIfNeed() which recalculates
     * FlexNode layout and synchronizes frames to native views. We additionally
     * check if the content height changed and report it to the native container.
     *
     * This is critical for dynamic content (e.g. TextAreaView auto-growing):
     * when text input triggers markDirty() → setNeedDirtyCallback → native calls
     * onLayoutView(), the re-layout updates child frames but the native container
     * (A2UISurfaceView) also needs its height constraint updated.
     */
    override fun onLayoutView() {
        super.onLayoutView()
        reportContentHeightIfChanged()
    }

    /**
     * Trigger layout calculation and report content height.
     * Called after all component tree operations are complete.
     *
     * The layout is performed by Kuikly Core's standard layoutIfNeed(),
     * which calculates FlexNode layout and synchronizes frames to native views.
     * After layout, we read the root FlexNode's computed height as content height.
     *
     * This also fulfills [A2UIHost.performLayoutAndReportHeight].
     */
    override fun performLayoutAndReportHeight() {
        // Trigger Kuikly Core's standard layout calculation
        // This will: calculate FlexNode layout → set frames on native views
        onLayoutView()
    }

    /**
     * Check if content height changed and notify the native container.
     */
    private fun reportContentHeightIfChanged() {
        val contentHeight = flexNode.layoutFrame.height
        // Filter out the "magic" large height (>= 10000) which indicates
        // the root node hasn't been properly constrained yet (content-driven mode
        // initial value). Only report real content heights.
        if (contentHeight > 0f && contentHeight < 10000f && contentHeight != lastReportedHeight) {
            lastReportedHeight = contentHeight
            onContentHeightChanged?.invoke(contentHeight)
        }
    }

    /**
     * Get the computed content height after layout.
     */
    fun getContentHeight(): Float {
        return flexNode.layoutFrame.height
    }

    override fun onDestroyPager() {
        registry.clear()
        onContentHeightChanged = null
        lastReportedHeight = 0f
        super.onDestroyPager()
    }
}
