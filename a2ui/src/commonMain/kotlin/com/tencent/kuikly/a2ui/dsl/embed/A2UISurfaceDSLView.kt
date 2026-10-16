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

package com.tencent.kuikly.a2ui.dsl.embed

import com.tencent.kuikly.a2ui.dsl.A2UIDSLBridge
import com.tencent.kuikly.a2ui.dsl.A2UIHost
import com.tencent.kuikly.a2ui.dsl.A2UIViewRegistry
import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.domChildren
import com.tencent.kuikly.core.layout.Frame
import com.tencent.kuikly.core.layout.undefined
import com.tencent.kuikly.core.views.DivAttr
import com.tencent.kuikly.core.views.DivEvent
import com.tencent.kuikly.core.views.internal.GroupView

/**
 * A2UISurfaceDSLView — the DSL/Compose entry point for embedding A2UI content
 * *inside* a user-written Kuikly page.
 *
 * Unlike the native A2UISurfaceView on Android/iOS/OHOS, this class is a plain
 * Kuikly DSL [DivView] that lives directly in the surrounding Kuikly component
 * tree. It implements [A2UIHost] and delegates its own DSL subtree to
 * [A2UIDSLBridge] in "embedded" mode (no inner Pager, no separate NativeBridge).
 *
 * Threading: all interactions must be issued on the Kuikly Context Thread, as
 * with any other DSL view.
 *
 * ### DSL usage
 * ```kotlin
 * // inside a Kuikly page body(): ViewBuilder
 * lateinit var surface: A2UISurfaceDSLView
 * A2UISurface {
 *     surface = this  // capture the view instance for imperative stream writes
 *     attr {
 *         flex(1f)
 *     }
 *     event {
 *         action { surfaceId, tag, actionJson ->
 *             vm.handleAction(actionJson)
 *         }
 *         openUrl { url -> AppRouter.open(url) }
 *         dataModelChanged { snapshotJson, changedKey ->
 *             vm.syncDataModel(snapshotJson)
 *         }
 *     }
 * }
 * // later, from a coroutine / callback:
 * surface.writeStream(chunk)
 * surface.finishStream()
 * ```
 */
// NOTE on class hierarchy:
//   We intentionally extend `GroupView<A2UISurfaceAttr, A2UISurfaceEvent>` here
//   INSTEAD OF the more convenient `DivView`. Reason: `DivView` hard-codes its
//   generic parameters to `<DivAttr, DivEvent>`, which means the DSL builder
//   `event { ... }` receives a `DivEvent` receiver. That would make the A2UI-
//   specific callbacks (`action { ... }`, `openUrl { ... }`, `dataModelChanged
//   { ... }`) unresolvable inside `event { }`. By subclassing `GroupView`
//   directly with our own type parameters, the DSL builder sees
//   `A2UISurfaceEvent` as the receiver and the callbacks work as documented.
//
//   We still mimic DivView's rendering behavior by returning ViewConst.TYPE_VIEW
//   from viewName() and reusing the same flat-layer heuristic.
class A2UISurfaceDSLView
    : GroupView<A2UISurfaceAttr, A2UISurfaceEvent>(), A2UIHost {

    // === Bridge state ===

    /**
     * Unique id for this surface (used as the A2UIDSLBridge instanceId key,
     * NOT as a Kuikly pagerId — the pagerId is the host page's own).
     * This id is also what platform picker/custom-view callbacks route to via
     * A2UIBridgeManager.dslBridgeMap, so it must be stable for the lifetime of
     * this view instance.
     */
    val instanceId: String = "a2ui_embed_${instanceCounter++}"

    private var dslBridge: A2UIDSLBridge? = null

    /** Registry of tag ↔ view for the A2UI-generated DSL subtree of this surface. */
    override val registry: A2UIViewRegistry = A2UIViewRegistry()

    /** Buffered stream chunks if written before the surface is ready to process them. */
    private var pendingStream: StringBuilder? = null

    /** True once finishStream() has been called before the surface is ready to process it. */
    private var pendingFinished: Boolean = false

    /**
     * True once this view's own FlexNode has been attached to its parent's flex tree by
     * the enclosing container (via [insertDomSubView]). Until this is true, our own
     * `didCreateFlexNode` (in the base [ViewContainer]) is still false, so calling
     * `insertDomSubView` on children would early-return and silently drop the sub-tree.
     * We therefore delay stream processing until this flag flips true.
     */
    private var flexNodeReady: Boolean = false

    // === A2UIHost implementation ===

    override val hostPagerId: String
        get() = pagerId

    override val rootContainer: ViewContainer<*, *>
        get() = this

    override val surfaceWidth: Float
        get() {
            val laidOutWidth = flexNode.layoutFrame.width
            if (laidOutWidth > 0f && !laidOutWidth.isNaN()) return laidOutWidth
            // Fallback: host page's rootView width (dp).
            return getPager().pageData.pageViewWidth
        }

    override fun fullScreenModalSize(): Pair<Float, Float> {
        val pageData = getPager().pageData
        // Prefer activity/window size when available (mirrors Kuikly ModalView),
        // otherwise fall back to device size.
        val width = when {
            pageData.activityWidth > 0f -> pageData.activityWidth
            pageData.deviceWidth > 0f -> pageData.deviceWidth
            surfaceWidth > 0f -> surfaceWidth
            else -> 375f
        }
        val height = when {
            pageData.activityHeight > 0f -> pageData.activityHeight
            pageData.deviceHeight > 0f -> pageData.deviceHeight
            else -> width * 2.4f
        }
        return width to height
    }

    override fun addTaskWhenLayoutFinish(task: () -> Unit) {
        getPager().addTaskWhenPagerUpdateLayoutFinish(task)
    }

    override fun performLayoutAndReportHeight() {
        // In embedded mode, the host Kuikly Pager's Flex engine automatically
        // re-lays out when child attrs change. We still explicitly nudge the
        // host layout when A2UI issues an out-of-band geometry change (e.g.
        // Image feature-auto-size after a network image loads).
        getPager().onLayoutView()
        // Notify any Compose wrapper (or other external host) that the A2UI
        // subtree has just finished a layout pass — content height may have
        // changed and the wrapper should re-measure.
        onContentSizeChanged?.invoke()
    }

    // === Compose interop hooks ===
    // These are used exclusively by Compose wrappers (see the demo's
    // A2UISurfaceComposable) to drive host-side wrap-content measurement.
    // They have no effect when this surface is embedded directly inside a
    // hand-written Kuikly DSL page.

    /**
     * Fires after every A2UI stream write that ended in a fresh Kuikly-side
     * layout pass. Compose wrappers plug their `mutableStateOf` bump here to
     * trigger a Compose remeasure. Always invoked on the Kuikly Context Thread.
     */
    var onContentSizeChanged: (() -> Unit)? = null

    /**
     * Measure this surface's current A2UI subtree in a **wrap-content** manner
     * under the given width, and return the resulting content height (in dp).
     *
     * Implementation: temporarily swap this view's own flexNode style to
     * `(width=availableWidth, height=Undefined)`, ask the Kuikly Flex engine
     * to recompute layout for this subtree in isolation, read back the
     * resulting `layoutHeight`, then restore the previous style so the host
     * page's next full layout pass sees the original constraints.
     *
     * This is a **read-only measurement** — it does not push frames to
     * `renderView` (that only happens during the host page's own layout pass).
     *
     * Only safe to call on the Kuikly Context Thread.
     *
     * @param availableWidth Width constraint in dp for the wrap-content measurement.
     * @return The A2UI subtree's natural content height in dp. Returns 0f if
     *   the surface currently has no A2UI children (e.g. before the first
     *   stream chunk arrives).
     */
    fun measureContentHeight(availableWidth: Float): Float {
        if (availableWidth <= 0f || availableWidth.isNaN()) {
            return 0f
        }
        if (flexNode.childCount == 0) {
            return 0f
        }

        val node = flexNode
        val savedWidth = node.styleWidth
        val savedHeight = node.styleHeight
        return try {
            node.styleWidth = availableWidth
            node.styleHeight = Float.undefined
            node.calculateLayout(null)
            val h = node.layoutFrame.height
            val safe = if (h.isNaN() || h < 0f) 0f else h
            safe
        } finally {
            node.styleWidth = savedWidth
            node.styleHeight = savedHeight
            // Leave the node dirty so the host page's next real layout pass
            // recomputes with the true (Compose-driven) constraints.
        }
    }

    // === Kuikly DSL boilerplate ===

    override fun createAttr(): A2UISurfaceAttr = A2UISurfaceAttr()

    override fun createEvent(): A2UISurfaceEvent = A2UISurfaceEvent()

    // NOTE: viewName() and isRenderView() are intentionally NOT overridden here.
    // GroupView already provides sensible defaults:
    //   - viewName()      -> ViewConst.TYPE_VIEW
    //   - isRenderView()  -> isRenderViewForFlatLayer()  (internal helper,
    //                        only reachable from the core module)
    // These are exactly what DivView does. Overriding them again from an
    // out-of-module subclass would require calling the internal helper, which
    // is not accessible here — and would be redundant anyway.

    override fun didInit() {
        super.didInit()

        // Kuikly-page-embedded mode: this surface lives inside a host
        // Kuikly page. The DSL bridge no longer needs a per-surface
        // NativeBridge because all native dispatch (picker dialogs, action
        // callbacks, custom-function invocation, …) now flows through the
        // standard Kuikly Module channel (`A2UIBridgeModule` installed on
        // the host page's delegate — see A2UIKuiklyBaseViewSupport on
        // Android, A2UIKuiklyRenderViewSupport on OHOS, and
        // A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting on iOS).
        // Passing `nativeBridge = null` tells A2UIDSLBridge to use the
        // Module channel exclusively.
        val bridge = A2UIDSLBridge(
            instanceId = instanceId,
            nativeBridge = null,
            externalHost = this
        )
        wireBridgeCallbacks(bridge)
        bridge.initialize()
        dslBridge = bridge

        // Register in A2UIBridgeManager so that platform callbacks (picker result,
        // custom function invocation, etc.) can route by instanceId back to this bridge.
        A2UIBridgeManagerAccessor.registerDslBridge(instanceId, bridge)

        // NOTE: We intentionally do NOT flush pendingStream/pendingFinished here.
        // At the time `didInit()` runs, this view's own FlexNode has not been attached
        // to its parent's flex tree yet (the enclosing container calls
        // `insertDomSubView(this)` only AFTER `didInit()` returns). Base
        // `ViewContainer.insertDomSubView` early-returns when `didCreateFlexNode == false`,
        // so if we flushed the stream here, every child would be silently dropped from
        // the flex tree and layout would collapse to 0×0. We defer the flush to
        // [createFlexNode] below, which is the exact hook the parent invokes to build
        // our flex node — by then it is safe to add children.
    }

    /**
     * Called by our enclosing container as part of `insertDomSubView(this, index)`.
     * At the end of `super.createFlexNode()` the base `ViewContainer` sets its own
     * private `didCreateFlexNode = true`, meaning it is now safe for us to accept
     * child insertions via `insertDomSubView`. This is therefore the correct place
     * to flush any buffered A2UI stream data.
     */
    override fun createFlexNode() {
        super.createFlexNode()
        flexNodeReady = true

        // Flush any stream data written before the surface was ready to process it.
        val bridge = dslBridge
        if (bridge != null) {
            pendingStream?.let { buf ->
                if (buf.isNotEmpty()) bridge.writeStream(buf.toString())
            }
            pendingStream = null
            if (pendingFinished) {
                bridge.finishStream()
                pendingFinished = false
            }
        }
    }

    /**
     * Last width (in dp) pushed down from an external host (e.g. Compose) via
     * [setFrameToRenderView]. Used to detect real changes and avoid marking the
     * flex tree dirty on every no-op frame update.
     */
    private var lastHostWidth: Float = Float.NaN

    /**
     * When embedded inside Compose (via KNode), the enclosing Compose LayoutNode
     * decides this view's final frame and pushes it down through the standard
     * [DeclarativeBaseView.setFrameToRenderView] pipeline every layout pass.
     * The base implementation only forwards the frame to the native RenderView,
     * leaving [flexNode.styleWidth] / [flexNode.styleHeight] untouched — so the
     * Kuikly Flex engine, when it later lays out this surface's own subtree,
     * has no idea what width the outer world assigned to us and every
     * descendant collapses to 0×0.
     *
     * We fix that here by mirroring the Compose-assigned width onto our own
     * `flexNode.styleWidth` (kept as [Float.NaN] for height to preserve
     * content-driven wrap-content behaviour), then marking the node dirty so
     * the next host layout pass propagates the new width down to children.
     * This mirrors what [com.tencent.kuikly.a2ui.dsl.A2UIPager.setSurfaceWidth]
     * does for the platform-native (non-DSL) embedding path.
     */
    override fun setFrameToRenderView(frame: Frame) {
        super.setFrameToRenderView(frame)
        val w = frame.width
        val changed = w > 0f && w != lastHostWidth
        if (changed) {
            lastHostWidth = w
            // Mirror the Compose-assigned width into the flex style so children
            // get a real containing-block width. Keep height NaN so the surface
            // still wraps its content in the vertical axis (Compose already
            // told us the height via `frame.height`, but the A2UI subtree may
            // want less — the caller is expected to give a matching height).
            flexNode.styleWidth = w
            flexNode.styleHeight = Float.NaN

            // In a normal Kuikly page the Pager's own `layoutIfNeed` would
            // pick up our dirty flag and run `calculateLayout` on the root
            // node, whose callbacks would then propagate the new frames down
            // to every descendant's RenderView. In a Compose host, however,
            // the Kuikly root flexNode is never dirty (Compose owns layout),
            // so `getPager().onLayoutView()` is a no-op for our subtree.
            //
            // Bypass that: run `calculateLayout` on our own flexNode to
            // compute every descendant's layoutFrame under the new width,
            // then manually DFS-push every descendant's fresh layoutFrame
            // to its own native RenderView.
            flexNode.calculateLayout(null)
            pushFramesRecursively(this)
        }
    }

    /**
     * Walk this A2UI subtree depth-first and push every [DeclarativeBaseView]'s
     * freshly-computed [layoutFrame] to its own native RenderView. Used by the
     * Compose-host code path where the Kuikly Pager's own layout loop cannot
     * pick up dirty subtrees.
     */
    private fun pushFramesRecursively(container: ViewContainer<*, *>) {
        container.domChildren().forEach { child ->
            @Suppress("UNCHECKED_CAST")
            (child as DeclarativeBaseView<*, *>).setFrameToRenderView(child.flexNode.layoutFrame)
            if (child is ViewContainer<*, *>) {
                pushFramesRecursively(child)
            }
        }
    }

    override fun willRemoveFromParentView() {
        super.willRemoveFromParentView()
        val bridge = dslBridge
        if (bridge != null) {
            A2UIBridgeManagerAccessor.unregisterDslBridge(instanceId)
            bridge.destroy()
        }
        dslBridge = null
        registry.clear()
    }

    // === Imperative API ===

    /**
     * Write a chunk of A2UI JSON protocol stream. Safe to call before [didInit]
     * completes — chunks are buffered and flushed on init.
     */
    fun writeStream(chunk: String) {
        if (chunk.isEmpty()) return
        val bridge = dslBridge
        // Even if the bridge is already initialized, we MUST buffer until our own flex node
        // has been attached to the parent's flex tree. Otherwise child inserts triggered by
        // stream processing would be dropped by `ViewContainer.insertDomSubView`'s
        // `!didCreateFlexNode → return` early exit.
        if (bridge != null && flexNodeReady) {
            bridge.writeStream(chunk)
        } else {
            val buf = pendingStream ?: StringBuilder().also { pendingStream = it }
            buf.append(chunk)
        }
    }

    /**
     * Mark the A2UI protocol stream as finished. Triggers final layout.
     */
    fun finishStream() {
        val bridge = dslBridge
        if (bridge != null && flexNodeReady) {
            bridge.finishStream()
        } else {
            pendingFinished = true
        }
    }

    /**
     * Restart the surface with a fresh empty stream. All previously-rendered
     * A2UI content is removed and the parser state is reset. Useful when the
     * business layer wants to replace the surface content instead of appending.
     */
    fun resetStream() {
        val old = dslBridge
        if (old != null) {
            A2UIBridgeManagerAccessor.unregisterDslBridge(instanceId)
            old.destroy()
        }
        dslBridge = null
        registry.clear()
        pendingStream = null
        pendingFinished = false

        // Re-create only if the view is still attached (didInit ran once).
        // Same reasoning as in `didInit`: embedded mode now goes exclusively
        // through the Module channel, so no per-surface NativeBridge is
        // needed.
        val bridge = A2UIDSLBridge(
            instanceId = instanceId,
            nativeBridge = null,
            externalHost = this
        )
        wireBridgeCallbacks(bridge)
        bridge.initialize()
        A2UIBridgeManagerAccessor.registerDslBridge(instanceId, bridge)
        dslBridge = bridge
    }

    // === Callback wiring ===

    private fun wireBridgeCallbacks(bridge: A2UIDSLBridge) {
        val evt = getViewEvent() as? A2UISurfaceEvent ?: return
        bridge.onAction = { surfaceId, tag, actionJson ->
            // A2UI protocol: action.functionCall means "invoke a client-side
            // function locally" (e.g. openUrl / a user-registered custom void
            // function like `logEvent`). Route those through the
            // shared bridge manager so DSL-embedded surfaces get identical
            // dispatch behaviour to legacy native-embedded surfaces. Only
            // forward to the host `.action { }` callback for non-functionCall
            // (or unhandled) actions.
            if (!A2UIBridgeManagerAccessor.tryHandleFunctionCall(instanceId, actionJson)) {
                evt.onActionCallback?.invoke(surfaceId, tag, actionJson)
            }
        }
        bridge.onOpenUrl = { url ->
            evt.onOpenUrlCallback?.invoke(url)
        }
        bridge.onDataModelChanged = { dataModelJson, changedKey ->
            evt.onDataModelChangedCallback?.invoke(dataModelJson, changedKey)
        }
        // Content height is intentionally not exposed via a callback: the host
        // Kuikly Pager's Flex engine will size this surface automatically based
        // on its A2UI-generated children (unless the caller explicitly set a
        // fixed width/height via attr).
    }

    companion object {
        private var instanceCounter: Long = 0L
    }
}

// ===================================================================
// A2UISurfaceAttr / A2UISurfaceEvent
// ===================================================================

/**
 * Attributes for [A2UISurfaceDSLView]. Inherits all layout / visual attrs
 * (width/height/flex/backgroundColor/…) from [DivAttr]. No A2UI-specific attrs
 * are needed for v1 — the surface content is driven via imperative
 * writeStream() / finishStream() calls on the view instance.
 */
class A2UISurfaceAttr : DivAttr()

/**
 * Events for [A2UISurfaceDSLView].
 *
 * All callbacks are optional. They fire on the Kuikly Context Thread.
 */
class A2UISurfaceEvent : DivEvent() {

    internal var onActionCallback: ((surfaceId: String, tag: Int, actionJson: String) -> Unit)? = null
    internal var onOpenUrlCallback: ((url: String) -> Unit)? = null
    internal var onDataModelChangedCallback: ((dataModelJson: String, changedKey: String) -> Unit)? = null

    /**
     * Called when the A2UI protocol emits an action (e.g. `Action.functionCall`
     * or a raw JSON action payload) from a user gesture. The host page is
     * expected to interpret the action according to the surrounding app's
     * routing / business logic.
     */
    fun action(cb: (surfaceId: String, tag: Int, actionJson: String) -> Unit) {
        onActionCallback = cb
    }

    /**
     * Called when the user taps a Markdown link inside a Text.
     */
    fun openUrl(cb: (url: String) -> Unit) {
        onOpenUrlCallback = cb
    }

    /**
     * Called when two-way binding updates the A2UI data model (slider drag,
     * checkbox toggle, text-field input, picker result, etc.). Only fires if
     * the protocol / adapter has enabled sendDataModel.
     */
    fun dataModelChanged(cb: (dataModelJson: String, changedKey: String) -> Unit) {
        onDataModelChangedCallback = cb
    }
}

// ===================================================================
// DSL entry point
// ===================================================================

/**
 * Embed an A2UI-rendered surface inside a Kuikly DSL page.
 *
 * @param init A [A2UISurfaceDSLView] builder lambda where you configure `attr {}`
 *             and `event {}` blocks. Capture `this` into a caller-owned variable
 *             to drive the surface imperatively via writeStream()/finishStream().
 *             See class-level KDoc for an example.
 */
fun ViewContainer<*, *>.A2UISurface(init: A2UISurfaceDSLView.() -> Unit) {
    addChild(A2UISurfaceDSLView(), init)
}
