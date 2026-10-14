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

package com.tencent.kuikly.a2ui.render.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.util.SizeF
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tencent.kuikly.a2ui.bridge.A2UICoreEntry
import com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod
import com.tencent.kuikly.a2ui.bridge.A2UINativeMethod
import com.tencent.kuikly.core.render.android.IKuiklyRenderContext
import com.tencent.kuikly.core.render.android.IKuiklyRenderExport
import com.tencent.kuikly.core.render.android.IKuiklyRenderLifecycleCallback
import com.tencent.kuikly.core.render.android.IKuiklyRenderView
import com.tencent.kuikly.core.render.android.KuiklyContextParams
import com.tencent.kuikly.core.render.android.KuiklyRenderExport
import com.tencent.kuikly.core.render.android.KuiklyRenderViewContext
import com.tencent.kuikly.core.render.android.adapter.IKRColorParserAdapter
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager
import com.tencent.kuikly.core.render.android.context.KuiklyRenderCoreExecuteModeBase
import com.tencent.kuikly.core.render.android.expand.component.KRActivityIndicatorView
import com.tencent.kuikly.core.render.android.expand.component.KRCanvasView
import com.tencent.kuikly.core.render.android.expand.component.KRHoverView
import com.tencent.kuikly.core.render.android.expand.component.KRImageView
import com.tencent.kuikly.core.render.android.expand.component.KRMaskView
import com.tencent.kuikly.core.render.android.expand.component.KRModalView
import com.tencent.kuikly.core.render.android.expand.component.KRRichTextView
import com.tencent.kuikly.core.render.android.expand.component.KRRichTextShadow
import com.tencent.kuikly.core.render.android.expand.component.KRTextAreaView
import com.tencent.kuikly.core.render.android.expand.component.KRTextFieldView
import com.tencent.kuikly.core.render.android.expand.component.KRAudioView
import com.tencent.kuikly.core.render.android.expand.component.KRVideoView
import com.tencent.kuikly.core.render.android.expand.component.KRView
import com.tencent.kuikly.core.render.android.expand.component.KRWrapperImageView
import com.tencent.kuikly.core.render.android.expand.component.blur.KRBlurView
import com.tencent.kuikly.core.render.android.expand.component.list.KRRecyclerContentView
import com.tencent.kuikly.core.render.android.expand.component.list.KRRecyclerView
import com.tencent.kuikly.core.render.android.expand.component.pag.KRPAGView
import com.tencent.kuikly.core.render.android.expand.module.KRMemoryCacheModule
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderShadowExport
import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.layer.KuiklyRenderLayerHandler
import com.tencent.kuikly.core.render.android.scheduler.KuiklyRenderCoreContextScheduler
import com.tencent.kuikly.core.render.android.scheduler.KuiklyRenderCoreTask
import com.tencent.tdf.module.TDFBaseModule
import java.util.UUID

/**
 * Listener interface for A2UI surface events.
 * Allows the hosting Activity/Fragment to receive action callbacks.
 */
interface A2UISurfaceViewListener {
    /** Called when a new surface is created */
    fun onSurfaceCreated(surfaceId: String) {}
    /** Called when a surface is destroyed */
    fun onSurfaceDestroyed(surfaceId: String) {}
    /** Called when the message stream is finished */
    fun onStreamFinished() {}
    /** Called when an error occurs */
    fun onError(message: String) {}
    /** Called when an action is dispatched from a user interaction */
    fun onAction(surfaceId: String, tag: Int, action: String, params: String?) {}
    /** Called when the data model changes due to user interaction (two-way binding).
     *  @param dataModelJson Full JSON snapshot of the current data model
     *  @param changedKey The specific key that was changed */
    fun onDataModelChanged(dataModelJson: String, changedKey: String) {}
    /** Called when a Markdown link inside a Text component is tapped.
     *  @param url The link destination URL */
    fun onOpenUrl(url: String) {}
}

/**
 * Android A2UI Surface View.
 * This is the main entry point for embedding A2UI rendered content in an Android application.
 *
 * Architecture (aligned with Kuikly DSL rendering flow):
 * - Uses [A2UICoreEntry] as the bridge between Native and Kotlin A2UI logic.
 * - Native → Kotlin: via [A2UICoreEntry.callKotlinMethod] (INIT, WRITE_STREAM, FINISH_STREAM, DESTROY)
 * - Kotlin → Native: via [NativeBridge.NativeBridgeDelegate.callNative] (CREATE_VIEW, SET_PROP, etc.)
 * - The delegate dispatches render commands to [KuiklyRenderLayerHandler].
 *
 * Threading model:
 * - [writeStream] and [finishStream] schedule work on the Context Thread via
 *   [KuiklyRenderCoreContextScheduler], where Kotlin logic executes.
 * - Kotlin sends render commands back via NativeBridge, which are collected as pending UI tasks.
 * - UI tasks are batch-flushed to the Main Thread when FLUSH_UI_TASKS is received.
 *
 * Usage:
 * ```kotlin
 * val surfaceView = A2UISurfaceView(context)
 * surfaceView.init()
 * // Feed A2UI envelope messages one by one, aligned with the A2UI protocol
 * // stream unit (each envelope is a JSON object with exactly one of
 * // createSurface / updateComponents / updateDataModel / deleteSurface).
 * surfaceView.writeStream("""{"version":"v0.9","createSurface":{...}}""")
 * surfaceView.writeStream("""{"version":"v0.9","updateComponents":{...}}""")
 * surfaceView.writeStream("""{"version":"v0.9","updateDataModel":{...}}""")
 * surfaceView.finishStream()
 * ```
 *
 * Note: A2UI protocol server-to-client is a stream of JSON objects, NOT a JSON
 * array. If your source data comes packaged as a JSON array (e.g. demo assets
 * that bundle multiple envelopes into a single file), the caller is responsible
 * for unpacking the array and feeding each envelope object individually via
 * [writeStream]. See [A2UI Protocol v0.9 §Envelope message structure].
 */
@SuppressLint("ViewConstructor")
class A2UISurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IKuiklyRenderView {

    companion object {
        /**
         * Return type token for a custom A2UI function's handler.
         * Must match the wire tokens accepted by
         * `A2UIFunctionReturnType.fromWire` in commonMain.
         */
        const val RETURN_TYPE_STRING = "string"
        const val RETURN_TYPE_BOOLEAN = "boolean"
        const val RETURN_TYPE_NUMBER = "number"
        const val RETURN_TYPE_ARRAY = "array"
        const val RETURN_TYPE_VOID = "void"

        const val INVOKE_MODE_SYNC = "sync"
        const val INVOKE_MODE_ASYNC = "async"

        /**
         * A user-supplied handler for a custom A2UI function. Receives the
         * resolved arguments as a [JSONObject] and must return one of:
         *  - `String` for return type `"string"`,
         *  - `Boolean` for `"boolean"`,
         *  - `Number` for `"number"`,
         *  - `String` (JSON) or `List<*>` for `"array"`,
         *  - anything (including `Unit`/`null`) for `"void"`.
         *
         * The handler is invoked on the thread the framework selects based on
         * the registered [invokeMode]. For `"sync"` the framework calls the
         * handler on the Kuikly Context Thread and blocks on the return value;
         * SYNC handlers **must return quickly** to avoid stalling rendering.
         * For `"async"` (only valid for `"void"` handlers), the framework
         * dispatches to the Android main thread and returns immediately.
         */
        fun interface Handler {
            fun invoke(args: org.json.JSONObject): Any?
        }

        /**
         * Register a custom A2UI function **globally**. Once registered, the
         * function is available to every [A2UISurfaceView] instance in this
         * process, both existing and future ones. Registering with an
         * already-registered name replaces the previous handler and
         * descriptor.
         *
         * The handler table itself lives in [A2UIAndroidCustomFunctionRegistry]
         * so that the embedded-mode host (`A2UISurfaceDSLView` + `A2UIAndroidNativeMethodHandler`)
         * shares the exact same registration table.
         *
         * Threading contract for the [handler]:
         *  - When `invokeMode = "sync"`, the handler runs on the Kuikly Context
         *    Thread (never the Android main thread). Keep the work brief; do
         *    not block on network or disk I/O — offload those to a background
         *    thread and return a cached value, or use the `"void"` +
         *    `"async"` mode when a side-effect-only handler is enough.
         *  - When `invokeMode = "async"` (void only), the handler runs on the
         *    Android main thread and its return value is ignored.
         *
         * Reserved names (the 14 built-in A2UI v0.9 functions) cannot be
         * overridden and will be silently ignored by the Kotlin registry.
         *
         * @param name        Function name as it appears in the A2UI catalog
         *                    and in `functionCall.call` on the wire.
         * @param returnType  One of `RETURN_TYPE_STRING` / `RETURN_TYPE_BOOLEAN`
         *                    / `RETURN_TYPE_NUMBER` / `RETURN_TYPE_ARRAY` /
         *                    `RETURN_TYPE_VOID`.
         * @param invokeMode  One of `INVOKE_MODE_SYNC` / `INVOKE_MODE_ASYNC`.
         *                    Non-void functions are always treated as SYNC
         *                    regardless of this argument.
         * @param handler     The callback that performs the actual work.
         */
        @JvmStatic
        @JvmOverloads
        fun registerCustomFunction(
            name: String,
            returnType: String,
            invokeMode: String = INVOKE_MODE_SYNC,
            handler: Handler
        ) {
            if (name.isEmpty()) return
            // Delegate to the shared registry. The registry's ChangeListener
            // (installed lazily below) fans-out register/unregister events to
            // every currently-live surface so its Kotlin core learns about the
            // new function without waiting for its own init cycle.
            ensureCustomFunctionListenerInstalled()
            A2UIAndroidCustomFunctionRegistry.register(
                name,
                returnType,
                invokeMode,
                A2UIAndroidCustomFunctionRegistry.Handler { args -> handler.invoke(args) }
            )
        }

        /**
         * Reverse of [registerCustomFunction]. Safe to call for names that
         * were never registered.
         */
        @JvmStatic
        fun unregisterCustomFunction(name: String) {
            if (name.isEmpty()) return
            ensureCustomFunctionListenerInstalled()
            A2UIAndroidCustomFunctionRegistry.unregister(name)
        }

        /**
         * Bridge the process-global [A2UIAndroidCustomFunctionRegistry] change
         * events into the per-instance replay path so that every already-live
         * surface picks up the new function immediately.
         *
         * Installed lazily to keep the ordering of static initialisers simple.
         */
        @Volatile
        private var customFunctionListenerInstalled: Boolean = false

        @Synchronized
        private fun ensureCustomFunctionListenerInstalled() {
            if (customFunctionListenerInstalled) return
            customFunctionListenerInstalled = true
            A2UIAndroidCustomFunctionRegistry.addChangeListener(
                object : A2UIAndroidCustomFunctionRegistry.ChangeListener {
                    override fun onRegister(name: String, spec: String) {
                        for (surface in liveSurfaces) {
                            surface.dispatchRegisterCustomFunction(name, spec)
                        }
                    }
                    override fun onUnregister(name: String) {
                        for (surface in liveSurfaces) {
                            surface.dispatchUnregisterCustomFunction(name)
                        }
                    }
                }
            )
        }

        /**
         * Weak set of all currently-live surface views. Used to broadcast
         * register/unregister calls that arrive after some surfaces have
         * already initialised. WeakHashMap-backed to avoid leaking the view.
         */
        private val liveSurfaces: MutableSet<A2UISurfaceView> =
            java.util.Collections.newSetFromMap(java.util.WeakHashMap())

        /**
         * Shared KRMemoryCacheModule instance used by all A2UISurfaceView instances.
         * This avoids duplicating base64 image cache per card and allows cross-card
         * cache sharing.
         */
        private val sharedMemoryCacheModule = KRMemoryCacheModule()

        // region Custom Render View Registration

        /**
         * Register a custom render view **globally**. Once registered, the
         * view is available to every A2UI host in this process (both existing
         * and future [A2UISurfaceView] instances *and* every Kuikly page
         * hosting an `A2UISurfaceDSLView`).
         *
         * This API is aligned with the Kuikly DSL custom component
         * registration flow. The same [IKuiklyRenderViewExport] implementation
         * used in Kuikly DSL can be reused here without modification.
         *
         * The handler table itself lives in [A2UIAndroidCustomViewRegistry] so
         * that the embedded-mode host (`A2UISurfaceDSLView` +
         * [A2UIKuiklyBaseViewSupport]) shares the exact same registration
         * table.
         *
         * @param viewName The view name as it appears in the A2UI protocol
         *                 (e.g. "KRMyCustomView"). Must match the viewName
         *                 used in createView commands.
         * @param creator  A factory lambda that creates the view instance.
         * @param shadowCreator Optional factory for the corresponding shadow.
         */
        @JvmStatic
        @JvmOverloads
        fun registerCustomRenderView(
            viewName: String,
            creator: (Context) -> IKuiklyRenderViewExport,
            shadowCreator: (() -> IKuiklyRenderShadowExport)? = null
        ) {
            if (viewName.isEmpty()) return
            // Delegate to the shared registry. The registry's ChangeListener
            // (installed lazily below) fans-out register/unregister events to
            // every currently-live surface so its Kotlin core learns about
            // the new viewName without waiting for its own init cycle.
            ensureCustomViewListenerInstalled()
            A2UIAndroidCustomViewRegistry.register(viewName, creator, shadowCreator)
        }

        /**
         * Unregister a previously registered custom render view.
         * Safe to call for names that were never registered.
         */
        @JvmStatic
        fun unregisterCustomRenderView(viewName: String) {
            if (viewName.isEmpty()) return
            ensureCustomViewListenerInstalled()
            A2UIAndroidCustomViewRegistry.unregister(viewName)
        }

        /**
         * Bridge the process-global [A2UIAndroidCustomViewRegistry] change
         * events into the per-instance replay path so that every already-live
         * surface picks up the new viewName immediately.
         */
        @Volatile
        private var customViewListenerInstalled: Boolean = false

        @Synchronized
        private fun ensureCustomViewListenerInstalled() {
            if (customViewListenerInstalled) return
            customViewListenerInstalled = true
            A2UIAndroidCustomViewRegistry.addChangeListener(
                object : A2UIAndroidCustomViewRegistry.ChangeListener {
                    override fun onRegister(
                        viewName: String,
                        entry: A2UIAndroidCustomViewRegistry.Entry
                    ) {
                        for (surface in liveSurfaces) {
                            surface.dispatchRegisterCustomView(viewName, entry)
                        }
                    }

                    override fun onUnregister(viewName: String) {
                        for (surface in liveSurfaces) {
                            surface.dispatchUnregisterCustomView(viewName)
                        }
                    }
                }
            )
        }

        // endregion
    }

    /** Unique instance ID for this A2UI surface */
    private val instanceId = "a2ui_${UUID.randomUUID()}"

    /** A2UI Core Entry - bridge to Kotlin logic */
    private var coreEntry: A2UICoreEntry? = null

    /** Whether the surface has been initialized */
    private var isInitialized = false

    /** Kuikly render layer handler - manages native view creation, prop setting, etc. */
    private var renderLayerHandler: KuiklyRenderLayerHandler? = null

    /** Kuikly render context */
    private val kuiklyRenderViewContext = KuiklyRenderViewContext(
        context, this, enableContextReplace = false, useHostDisplayMetrics = false
    )

    /** Kuikly render export - registers all native components */
    private val renderExportInstance = KuiklyRenderExport(kuiklyRenderContext)

    /** Main thread handler for dispatching UI tasks */
    private val mainHandler = Handler(Looper.getMainLooper())

    /** Pending UI tasks collected from Kotlin→Native render commands */
    private val pendingUITasks = mutableListOf<() -> Unit>()

    /** Listener for surface events (action callbacks, etc.) */
    private var surfaceListener: A2UISurfaceViewListener? = null

    /** Tags that have touchEnable set (need click listeners) */
    private val clickableTags = mutableSetOf<Int>()

    /**
     * Memory cache module for base64 image caching (required by KRImageView).
     * Shared across all A2UISurfaceView instances so that cached images are
     * available globally and not duplicated per card.
     *
     * In the normal Kuikly DSL flow, ImageAttr.src() pre-stores the base64 string
     * in the cache under a generated key, then sends only the key to native.
     * In A2UI, the full base64 string is passed directly as the src prop.
     * KRImageView retrieves the original base64 via this module's get() method.
     */
    private val memoryCacheModule get() = sharedMemoryCacheModule

    init {
        clipChildren = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && isInitialized) {
            val density = resources.displayMetrics.density
            val widthDp = w / density
            // Determine heightDp based on the layout mode:
            // - Fixed height (EXACTLY): use the actual height from the parent container.
            // - WRAP_CONTENT / AT_MOST: use 10000 as a sentinel so Kotlin layer sets
            //   styleHeight = NaN, allowing content-driven sizing.
            val heightDp = if (isFixedHeightMode && h > 0) {
                // Fixed height container: pass actual height so flex engine uses it as constraint
                h / density
            } else {
                // WRAP_CONTENT mode: always use sentinel value so Kotlin layer keeps
                // styleHeight = NaN (content-driven). The actual content height is
                // determined by the flex engine and reported back via CONTENT_HEIGHT_CHANGED.
                // Previously this branch passed `h / density` when contentHeightPx > 0,
                // which caused the Kotlin layer to lock the root height to the current
                // content height, preventing it from shrinking when children resize
                // (e.g. Image after loadResolution callback adjusts its dimensions).
                10000f
            }

            // In content-driven mode, skip re-sending SET_SURFACE_SIZE if only the height
            // changed (due to contentHeightPx update). The Kotlin layer already has
            // styleHeight = NaN and will recalculate content height on its own.
            // Only notify when width changes or when there are pending stream chunks.
            val widthChanged = (widthDp != lastKnownWidthDp)
            val heightConstraintChanged = (isFixedHeightMode && heightDp != lastKnownHeightDp)
            lastKnownWidthDp = widthDp
            lastKnownHeightDp = heightDp

            // If there are buffered stream chunks waiting for valid dimensions, send them now
            if (pendingStreamChunks.isNotEmpty() || pendingFinishStream) {
                val chunks = pendingStreamChunks.toList()
                pendingStreamChunks.clear()
                val shouldFinish = pendingFinishStream
                pendingFinishStream = false
                KuiklyRenderCoreContextScheduler.scheduleTask {
                    val effectiveHeight = if (heightDp > 0f) heightDp else 10000f
                    coreEntry?.callKotlinMethod(
                        A2UIKotlinMethod.SET_SURFACE_SIZE,
                        instanceId,
                        "$widthDp,$effectiveHeight",
                        null
                    )
                    for (chunk in chunks) {
                        coreEntry?.callKotlinMethod(A2UIKotlinMethod.WRITE_STREAM, instanceId, chunk, null)
                    }
                    if (shouldFinish) {
                        coreEntry?.callKotlinMethod(A2UIKotlinMethod.FINISH_STREAM, instanceId, null, null)
                    }
                }
            } else if (widthChanged || heightConstraintChanged) {
                // Only re-send SET_SURFACE_SIZE when the width or fixed-height constraint
                // actually changed. In content-driven mode, height changes from
                // contentHeightPx updates should NOT trigger a re-send (the Kotlin layer
                // already has styleHeight = NaN and manages content height internally).
                KuiklyRenderCoreContextScheduler.scheduleTask {
                    coreEntry?.callKotlinMethod(
                        A2UIKotlinMethod.SET_SURFACE_SIZE,
                        instanceId,
                        "$widthDp,$heightDp",
                        null
                    )
                    // If stream was already finished, trigger a re-layout with proper dimensions
                    if (streamFinished) {
                        coreEntry?.callKotlinMethod(A2UIKotlinMethod.FINISH_STREAM, instanceId, null, null)
                    }
                }
            }
        }
    }

    /** Last known view dimensions in dp (for passing to Kotlin layer) */
    private var lastKnownWidthDp: Float = 0f
    private var lastKnownHeightDp: Float = 0f

    /** Whether finishStream has been called (to trigger re-layout on size change) */
    private var streamFinished = false

    /** Buffered stream chunks waiting for valid dimensions before being sent */
    private val pendingStreamChunks = mutableListOf<String>()

    /** Whether finishStream was called before dimensions were available */
    private var pendingFinishStream = false

    /** Content height in pixels, updated when root view frame is set */
    private var contentHeightPx: Int = 0

    /** Whether the parent has assigned a fixed height (MeasureSpec.EXACTLY) */
    private var isFixedHeightMode: Boolean = false

    /** Tag of the root view (first child inserted into platform root) */
    private var rootViewTag: Int = -1

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        // Track whether the parent assigns a fixed height
        isFixedHeightMode = (heightMode == MeasureSpec.EXACTLY)
        if (heightMode == MeasureSpec.EXACTLY) {
            // Fixed height from parent - use it directly
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else if (contentHeightPx > 0) {
            // WRAP_CONTENT or AT_MOST: use content height as our measured height.
            // We still need to call super.onMeasure to ensure all children get measured
            // (FrameLayout.onMeasure measures children based on their layoutParams).
            // Then override our own measured dimension with the content-driven height.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            val finalHeight = if (heightMode == MeasureSpec.AT_MOST) {
                minOf(contentHeightPx, heightSize)
            } else {
                contentHeightPx
            }
            setMeasuredDimension(widthSize, finalHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    // region IKuiklyRenderView implementation

    override val view: ViewGroup get() = this

    override val kuiklyRenderContext: IKuiklyRenderContext get() = kuiklyRenderViewContext

    override val kuiklyRenderExport: IKuiklyRenderExport get() = renderExportInstance

    override fun init(
        contextCode: String, pageName: String, params: Map<String, Any>,
        size: Size?, assetsPath: String?
    ) { /* Not used for A2UI */ }

    /**
     * `IKuiklyRenderView` lifecycle hook — normally invoked by
     * `KuiklyRenderViewBaseDelegator` after `renderCore` finishes initialising,
     * to broadcast `didInitCore` and drive the layer-init lifecycle states.
     *
     * A2UI's standalone surface bypasses `renderCore` entirely (see the
     * top-of-file architecture note): initialisation is driven by
     * `A2UICoreEntry.callKotlinMethod(INIT, …)` inside [init], and the
     * Kuikly DSL delegator is never involved. There is nothing to broadcast
     * here, so this override is intentionally empty.
     */
    override fun didCreateRenderView() { /* Not used for A2UI */ }

    override fun sendEvent(event: String, data: Map<String, Any>) {}
    @Suppress("UNCHECKED_CAST")
    override fun <T : KuiklyRenderBaseModule> module(name: String): T? {
        return when (name) {
            KRMemoryCacheModule.MODULE_NAME -> memoryCacheModule as? T
            else -> null
        }
    }
    override fun <T : TDFBaseModule> getTDFModule(name: String): T? = null
    override fun getView(tag: Int): View? = renderLayerHandler?.getView(tag)
    override fun resume() {}
    override fun pause() {}

    override fun destroy() {
        if (!isInitialized) return
        isInitialized = false
        // Note: memoryCacheModule is shared across all surfaces, do NOT call onDestroy() here.
        // It will be cleaned up when the process exits.
        synchronized(liveSurfaces) {
            liveSurfaces.remove(this)
        }
        KuiklyRenderCoreContextScheduler.scheduleTask {
            coreEntry?.callKotlinMethod(A2UIKotlinMethod.DESTROY, instanceId, null, null)
            coreEntry?.destroy()
            coreEntry = null
        }
    }

    override fun syncFlushAllRenderTasks() {}
    override fun performWhenViewDidLoad(task: KuiklyRenderCoreTask) { task() }
    override fun addKuiklyRenderLifecycleCallback(callback: IKuiklyRenderLifecycleCallback) {}
    override fun removeKuiklyRenderLifeCycleCallback(callback: IKuiklyRenderLifecycleCallback) {}
    override fun dispatchOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    override fun isDebugLogEnable(): Boolean = false

    // endregion

    // region A2UI public API

    /**
     * Initialize the A2UI surface view.
     * Sets up the Kuikly render layer, registers native components, and creates the A2UICoreEntry.
     *
     * @param configJson Optional JSON configuration string
     */
    fun init(configJson: String? = null) {
        if (isInitialized) return
        isInitialized = true

        // Capture current view dimensions if available
        if (width > 0) {
            val density = resources.displayMetrics.density
            lastKnownWidthDp = width / density
            // For fixed height mode, use actual height; for WRAP_CONTENT, use sentinel
            lastKnownHeightDp = if (isFixedHeightMode && height > 0) {
                height / density
            } else if (height > 0 && contentHeightPx > 0) {
                height / density
            } else {
                10000f
            }
        }

        ensureColorParseAdapterRegistered()

        // Initialize context params so that getImageLoader() works properly.
        // KRImageView depends on contextParams to create KRImageLoader for fetching images.
        kuiklyRenderViewContext.initContextParams(
            KuiklyContextParams(
                executeMode = KuiklyRenderCoreExecuteModeBase.JVM,
                pageUrl = instanceId,
                pageData = null,
                assetsPath = null
            )
        )

        registerDefaultRenderViews()

        // Initialize Kuikly render layer handler
        renderLayerHandler = KuiklyRenderLayerHandler().apply {
            init(this@A2UISurfaceView)
        }

        // Create A2UICoreEntry and set up the NativeBridge delegate
        val entry = A2UICoreEntry()
        entry.delegate = createNativeBridgeDelegate()
        coreEntry = entry

        // Track this surface so subsequent global register/unregister calls
        // can be forwarded here too.
        synchronized(liveSurfaces) {
            liveSurfaces.add(this)
        }

        // Augment the config with the real screen size (in dp) so full-screen overlays
        // (e.g. Modal/KRModalView) can size their Kuikly flex frame to the whole screen
        // rather than the self-sizing card. The surface itself stays content-driven.
        val effectiveConfig = injectDeviceSize(configJson)

        // Initialize the Kotlin A2UI instance on context thread
        KuiklyRenderCoreContextScheduler.scheduleTask {
            entry.callKotlinMethod(A2UIKotlinMethod.INIT, instanceId, effectiveConfig, null)
            // Replay every already-registered custom function so this fresh
            // instance's Kotlin core learns about them. The registry itself is
            // process-global so a single call would technically suffice, but
            // registering per-init keeps behaviour identical whether the app
            // registers before or after creating the surface.
            val snapshot: List<Pair<String, String>> = A2UIAndroidCustomFunctionRegistry.snapshot()
            for ((name, spec) in snapshot) {
                entry.callKotlinMethod(
                    A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION,
                    instanceId,
                    name,
                    spec
                )
            }
            // Replay every globally-registered custom render view viewName so
            // this fresh instance's Kotlin core (A2UIApplier.createDSLView)
            // can distinguish custom components from unknown / malformed names.
            val viewNames = A2UIAndroidCustomViewRegistry.viewNames()
            for (name in viewNames) {
                entry.callKotlinMethod(
                    A2UIKotlinMethod.REGISTER_CUSTOM_VIEW,
                    instanceId,
                    name,
                    null
                )
            }
        }
    }

    /**
     * Add `deviceWidth`/`deviceHeight` (in dp) to the config JSON passed to the core,
     * so full-screen components can be laid out against the real screen size.
     *
     * Uses the real (physical) display metrics including system bars. The app-usable
     * `resources.displayMetrics` under-reports on devices that exclude the navigation
     * bar (e.g. MIUI), which would leave the modal backdrop short at the bottom. The
     * native KRModalView container is full-screen, so we must match the full screen.
     */
    private fun injectDeviceSize(configJson: String?): String {
        return try {
            val metrics = resources.displayMetrics
            val density = if (metrics.density > 0f) metrics.density else 1f
            val realMetrics = android.util.DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay?.getRealMetrics(realMetrics)
            // Fall back to usable metrics if the real metrics are unavailable.
            val widthPx = if (realMetrics.widthPixels > 0) realMetrics.widthPixels else metrics.widthPixels
            val heightPx = if (realMetrics.heightPixels > 0) realMetrics.heightPixels else metrics.heightPixels
            val deviceWidthDp = widthPx / density
            val deviceHeightDp = heightPx / density
            val obj = if (configJson.isNullOrEmpty()) {
                org.json.JSONObject()
            } else {
                org.json.JSONObject(configJson)
            }
            obj.put("deviceWidth", deviceWidthDp.toDouble())
            obj.put("deviceHeight", deviceHeightDp.toDouble())
            obj.toString()
        } catch (e: Exception) {
            configJson ?: "{}"
        }
    }

    /**
     * Feed one A2UI envelope message into the surface's message stream.
     *
     * ### Contract (aligned with A2UI protocol v0.9 §Envelope message structure)
     * A2UI's server-to-client channel is defined as **a stream of JSON objects**,
     * where each object is a single envelope containing exactly one of:
     * `createSurface`, `updateComponents`, `updateDataModel`, or `deleteSurface`.
     *
     * The recommended way to call this method is once per protocol message:
     * ```
     * surfaceView.writeStream("""{"version":"v0.9","createSurface":{...}}""")
     * surfaceView.writeStream("""{"version":"v0.9","updateComponents":{...}}""")
     * surfaceView.writeStream("""{"version":"v0.9","updateDataModel":{...}}""")
     * ```
     *
     * ### Accepted input
     * - A single, complete envelope object as a JSON string (e.g. `{"version":"v0.9","createSurface":{...}}`).
     * - A JSONL / newline-delimited concatenation of envelope objects (also permitted
     *   by the protocol as a valid transport framing) — the underlying parser will
     *   split them by top-level `{ ... }` boundaries.
     * - A partial JSON string that will be completed by subsequent [writeStream] calls;
     *   the parser buffers characters until a top-level `{ ... }` is closed.
     *
     * ### NOT accepted
     * - A JSON array wrapping multiple envelopes (`[ {...}, {...} ]`).
     *   The A2UI protocol does **not** define an array wrapper; it is a
     *   convenience packaging that some demo assets use to store multiple
     *   messages in a single file. Unpacking such an array is the responsibility
     *   of the caller (demo / integration layer), which should iterate the array
     *   and invoke [writeStream] once per envelope object.
     *
     *   If the input starts with `[`, this method logs an error and returns
     *   without dispatching anything. It will never crash on this input.
     *
     * If the view has not yet been measured (width = 0), the call is buffered
     * and will be dispatched once valid dimensions are available via [onSizeChanged].
     *
     * @param chunk A single A2UI envelope JSON object (or a JSONL run of them,
     *              or a continuation of a partial envelope from a previous call).
     */
    fun writeStream(chunk: String) {
        ensureInitialized()
        // Reject non-protocol JSON-array wrapping. Per A2UI v0.9, server-to-client
        // is a stream of JSON objects; an outer array is not part of the protocol.
        // The caller (demo / integration layer) must unpack such arrays and feed
        // each envelope object individually.
        val trimmedStart = chunk.indexOfFirst { !it.isWhitespace() }
        if (trimmedStart >= 0 && chunk[trimmedStart] == '[') {
            Log.e(
                "A2UISurfaceView",
                "writeStream: input starts with '[' — a JSON-array wrapper is NOT part of " +
                    "the A2UI protocol. Please unpack the array on the caller side and feed " +
                    "each envelope object via writeStream() individually. Input dropped."
            )
            return
        }
        val widthDp = lastKnownWidthDp
        val heightDp = lastKnownHeightDp
        if (widthDp <= 0f) {
            // Buffer the chunk until we have valid dimensions
            pendingStreamChunks.add(chunk)
            return
        }
        KuiklyRenderCoreContextScheduler.scheduleTask {
            // Set surface size first so surfaces created from this stream get proper dimensions
            val effectiveHeight = if (heightDp > 0f) heightDp else 10000f
            coreEntry?.callKotlinMethod(
                A2UIKotlinMethod.SET_SURFACE_SIZE,
                instanceId,
                "$widthDp,$effectiveHeight",
                null
            )
            coreEntry?.callKotlinMethod(A2UIKotlinMethod.WRITE_STREAM, instanceId, chunk, null)
        }
    }

    /**
     * Mark the message stream as finished.
     *
     * If the view has not yet been measured (width = 0), the finish is deferred
     * until valid dimensions are available via onSizeChanged.
     */
    fun finishStream() {
        streamFinished = true
        val widthDp = lastKnownWidthDp
        val heightDp = lastKnownHeightDp
        if (widthDp <= 0f) {
            // Defer finish until we have valid dimensions
            pendingFinishStream = true
            return
        }
        KuiklyRenderCoreContextScheduler.scheduleTask {
            val effectiveHeight = if (heightDp > 0f) heightDp else 10000f
            coreEntry?.callKotlinMethod(
                A2UIKotlinMethod.SET_SURFACE_SIZE,
                instanceId,
                "$widthDp,$effectiveHeight",
                null
            )
            coreEntry?.callKotlinMethod(A2UIKotlinMethod.FINISH_STREAM, instanceId, null, null)
        }
    }

    /**
     * Set a listener for surface events (action callbacks, etc.).
     */
    fun setListener(listener: A2UISurfaceViewListener) {
        this.surfaceListener = listener
    }

    /**
     * Get the native View for a specific tag.
     */
    fun getViewByTag(tag: Int): View? = renderLayerHandler?.getView(tag)

    // endregion

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isInitialized) {
            destroy()
        }
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            init()
        }
    }

    // region NativeBridge Delegate - handles Kotlin → Native render commands

    /**
     * Create the NativeBridge delegate that receives render commands from Kotlin
     * and dispatches them to KuiklyRenderLayerHandler.
     *
     * Shadow operations execute directly (called on context thread).
     * View operations are collected as pending UI tasks and batch-flushed to main thread.
     */
    private fun createNativeBridgeDelegate(): A2UICoreEntry.Delegate {
        return object : A2UICoreEntry.Delegate {
            override fun callNative(
                methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?,
                arg3: Any?, arg4: Any?, arg5: Any?
            ): Any? {
                val handler = renderLayerHandler ?: return null

                when (methodId) {
                    A2UINativeMethod.CREATE_VIEW -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val viewName = arg2 as? String ?: return null
                        postToMainThread { handler.createRenderView(tag, viewName) }
                    }
                    A2UINativeMethod.REMOVE_VIEW -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        postToMainThread { handler.removeRenderView(tag) }
                    }
                    A2UINativeMethod.INSERT_SUB_VIEW -> {
                        val parentTag = (arg1 as? Number)?.toInt() ?: return null
                        val childTag = (arg2 as? Number)?.toInt() ?: return null
                        val index = (arg3 as? Number)?.toInt() ?: return null
                        // Map A2UI's PLATFORM_ROOT_TAG (0) to Kuikly's ROOT_VIEW_TAG (-1)
                        val effectiveParentTag = if (parentTag == 0) -1 else parentTag
                        // Track root view tag (first child inserted into platform root)
                        if (parentTag == 0 && rootViewTag == -1) {
                            rootViewTag = childTag
                        }
                        postToMainThread { handler.insertSubRenderView(effectiveParentTag, childTag, index) }
                    }
                    A2UINativeMethod.SET_PROP -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val key = arg2 as? String ?: return null
                        val value = arg3 ?: return null
                        // Check if this is an event registration (arg4 == 1 means event)
                        val isEvent = (arg4 as? Number)?.toInt() == 1
                        if (isEvent) {
                            // Create a KuiklyRenderCallback wrapper for event props (e.g. "pan", "progressDidChanged")
                            // This matches KuiklyRenderCore.setViewProp() behavior.
                            // When the native layer triggers the event, callback is invoked with event data.
                            val eventName = key
                            val callback: (Any?) -> Unit = { result ->
                                val resultStr = when (result) {
                                    is Map<*, *> -> org.json.JSONObject(result as Map<*, *>).toString()
                                    is String -> result
                                    null -> null
                                    else -> result.toString()
                                }
                                // Dispatch event back to Kotlin Core on context thread
                                KuiklyRenderCoreContextScheduler.scheduleTask {
                                    coreEntry?.callKotlinMethod(
                                        A2UIKotlinMethod.VIEW_EVENT,
                                        instanceId,
                                        "$tag|$eventName",
                                        resultStr
                                    )
                                }
                            }
                            postToMainThread { handler.setProp(tag, key, callback) }
                        } else if (key == "touchEnable" && value.toString() == "1") {
                            // When touchEnable is set to 1, just set the prop.
                            // Click handling is done via Kuikly Core's standard click event
                            // registered in A2UIApplier (cross-platform, works on all platforms).
                            clickableTags.add(tag)
                            postToMainThread { handler.setProp(tag, key, value) }
                        } else {
                            postToMainThread { handler.setProp(tag, key, value) }
                        }
                    }
                    A2UINativeMethod.SET_FRAME -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        // Core sends: callNativeMethod(SET_RENDER_VIEW_FRAME, instanceId, tag, x, y, width, height)
                        // So arg2=x, arg3=y, arg4=width, arg5=height (all as Float/Number)
                        val x = (arg2 as? Number)?.toFloat() ?: 0f
                        val y = (arg3 as? Number)?.toFloat() ?: 0f
                        val width = (arg4 as? Number)?.toFloat() ?: 0f
                        val height = (arg5 as? Number)?.toFloat() ?: 0f
                        val frame = RectF(x, y, width, height)
                        // If this is the root view frame, update content height for WRAP_CONTENT support
                        if (tag == rootViewTag) {
                            val density = resources.displayMetrics.density
                            val newContentHeight = (height * density).toInt()
                            if (newContentHeight != contentHeightPx && newContentHeight > 0) {
                                contentHeightPx = newContentHeight
                                postToMainThread {
                                    handler.setRenderViewFrame(tag, frame)
                                    requestLayout()
                                }
                                return null
                            }
                        }
                        postToMainThread { handler.setRenderViewFrame(tag, frame) }
                    }
                    A2UINativeMethod.CALL_VIEW_METHOD -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val method = arg2 as? String ?: return null
                        val params = arg3 as? String
                        postToMainThread { handler.callViewMethod(tag, method, params, null) }
                    }
                    A2UINativeMethod.CREATE_SHADOW -> {
                        // Shadow operations run directly on context thread
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val viewName = arg2 as? String ?: return null
                        handler.createShadow(tag, viewName)
                    }
                    A2UINativeMethod.SET_SHADOW_PROP -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val key = arg2 as? String ?: return null
                        val value = arg3 ?: return null
                        handler.setShadowProp(tag, key, value)
                    }
                    A2UINativeMethod.SET_SHADOW_FOR_VIEW -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val shadow = handler.shadow(tag)
                        shadow?.let {
                            postToMainThread { handler.setShadow(tag, it) }
                        }
                    }
                    A2UINativeMethod.MEASURE_SHADOW_SIZE -> {
                        // Synchronous measurement - runs on context thread
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val constraintWidth = (arg2 as? Number)?.toFloat() ?: return null
                        val constraintHeight = (arg3 as? Number)?.toFloat() ?: return null
                        val size = handler.calculateRenderViewSize(tag, SizeF(constraintWidth, constraintHeight))
                        return "${size.width},${size.height}"
                    }
                    A2UINativeMethod.REMOVE_SHADOW -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        handler.removeShadow(tag)
                    }
                    A2UINativeMethod.CALCULATE_RENDER_VIEW_SIZE -> {
                        // Core sends this for view size calculation (e.g., text measurement)
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val constraintWidth = (arg2 as? Number)?.toFloat() ?: return null
                        val constraintHeight = (arg3 as? Number)?.toFloat() ?: return null
                        val size = handler.calculateRenderViewSize(tag, SizeF(constraintWidth, constraintHeight))
                        return "${size.width}|${size.height}"
                    }
                    A2UINativeMethod.CALL_MODULE_METHOD -> {
                        // args: instanceId, moduleName, method, params
                        val moduleName = arg1 as? String ?: return null
                        val method = arg2 as? String ?: return null
                        val params = arg3 as? String
                        if (moduleName == KRMemoryCacheModule.MODULE_NAME) {
                            // Forward to our custom memoryCacheModule so that
                            // base64 image data stored by Kotlin Core (via setMemoryCache)
                            // is available when KRImageView.getBase64Image() retrieves it.
                            return memoryCacheModule.call(method, params, null)
                        }
                    }
                    A2UINativeMethod.SET_TIMEOUT -> {
                        // Timeout handling - not yet needed in A2UI
                    }
                    A2UINativeMethod.CALL_SHADOW_METHOD -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val method = arg2 as? String ?: return null
                        val params = arg3 as? String ?: ""
                        return handler.callShadowMethod(tag, method, params)
                    }
                    A2UINativeMethod.FIRE_FATAL_EXCEPTION -> {
                        // Fatal exception from Core - log it
                        val message = arg1 as? String ?: "unknown"
                        println("[A2UI] FATAL: $message")
                    }
                    A2UINativeMethod.CALL_TDF_MODULE_METHOD -> {
                        // TDF module method calls - not yet supported in A2UI demo
                    }
                    A2UINativeMethod.DESTROY -> {
                        postToMainThread { handler.onDestroy() }
                    }
                    A2UINativeMethod.FLUSH_UI_TASKS -> {
                        // Before flushing UI tasks, trigger layout recalculation.
                        // This matches KuiklyRenderCore's KuiklyRenderCoreUIScheduler behavior
                        // which calls LayoutView before each UI flush.
                        coreEntry?.callKotlinMethod(
                            A2UIKotlinMethod.LAYOUT_VIEW,
                            instanceId,
                            null,
                            null
                        )
                        flushUITasks()
                    }
                    A2UINativeMethod.ACTION_CALLBACK -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val actionJson = arg2 as? String ?: return null
                        val surfaceId = arg3 as? String ?: ""
                        // Dispatch action to listener on main thread
                        mainHandler.post {
                            surfaceListener?.onAction(surfaceId, tag, actionJson, null)
                        }
                    }
                    A2UINativeMethod.CONTENT_HEIGHT_CHANGED -> {
                        val height = (arg1 as? Number)?.toFloat() ?: return null
                        val density = resources.displayMetrics.density
                        val newContentHeightPx = (height * density).toInt()
                        if (newContentHeightPx != contentHeightPx && newContentHeightPx > 0) {
                            contentHeightPx = newContentHeightPx
                            postToMainThread { requestLayout() }
                        }
                    }
                    A2UINativeMethod.SHOW_DATE_TIME_PICKER -> {
                        val tag = (arg1 as? Number)?.toInt() ?: return null
                        val configJson = arg2 as? String ?: return null
                        // Delegate to the shared utility so behaviour stays
                        // identical between native-page (this) and Kuikly-page-
                        // embedded (A2UIAndroidNativeMethodHandler) modes. It
                        // handles the main-thread hop internally.
                        A2UIAndroidNativeMethodHandler.showDateTimePickerDialog(
                            context, instanceId, tag, configJson
                        )
                    }
                    A2UINativeMethod.DATA_MODEL_CHANGED -> {
                        val dataModelJson = arg1 as? String ?: return null
                        val changedKey = arg2 as? String ?: ""
                        // Dispatch data model change to listener on main thread
                        mainHandler.post {
                            surfaceListener?.onDataModelChanged(dataModelJson, changedKey)
                        }
                    }
                    A2UINativeMethod.OPEN_URL -> {
                        val url = arg1 as? String ?: return null
                        // Dispatch link tap to listener on main thread
                        mainHandler.post {
                            surfaceListener?.onOpenUrl(url)
                        }
                    }
                    A2UINativeMethod.EXECUTE_FUNCTION_CALL -> {
                        val functionName = arg1 as? String ?: return null
                        val argsJson = arg2 as? String ?: "{}"
                        // Shared utility handles the main-thread hop internally.
                        A2UIAndroidNativeMethodHandler.executeFunctionCall(
                            context, functionName, argsJson
                        )
                    }
                    A2UINativeMethod.INVOKE_CUSTOM_FUNCTION -> {
                        // Custom user function callback. Called on the Kuikly Context Thread.
                        // arg1: name, arg2: argsJson, arg3: "returnType|invokeMode"
                        val name = arg1 as? String ?: return null
                        val argsJson = arg2 as? String ?: "{}"
                        val spec = arg3 as? String ?: return null
                        return invokeCustomFunctionLocally(name, argsJson, spec)
                    }
                }
                return null
            }
        }
    }

    // endregion

    // region UI Task Batching

    /**
     * Flush all pending UI tasks to main thread in one batch.
     */
    private fun flushUITasks() {
        val tasks: List<() -> Unit>
        synchronized(pendingUITasks) {
            if (pendingUITasks.isEmpty()) return
            tasks = pendingUITasks.toList()
            pendingUITasks.clear()
        }
        mainHandler.post {
            for (task in tasks) {
                task()
            }
        }
    }

    /**
     * Add a UI task to the pending batch.
     */
    private fun postToMainThread(task: () -> Unit) {
        synchronized(pendingUITasks) {
            pendingUITasks.add(task)
        }
    }

    // endregion

    // region FunctionCall Execution

    // endregion

    // region Custom Function Execution

    /**
     * Forward a global `registerCustomFunction` call into this surface's Kotlin
     * core. Called by the companion object's [registerCustomFunction] for every
     * live surface, so the Kotlin core doesn't need to poll for new functions.
     */
    private fun dispatchRegisterCustomFunction(name: String, spec: String) {
        val entry = coreEntry ?: return
        KuiklyRenderCoreContextScheduler.scheduleTask {
            entry.callKotlinMethod(
                A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION,
                instanceId,
                name,
                spec
            )
        }
    }

    /** Companion to [dispatchRegisterCustomFunction]. */
    private fun dispatchUnregisterCustomFunction(name: String) {
        val entry = coreEntry ?: return
        KuiklyRenderCoreContextScheduler.scheduleTask {
            entry.callKotlinMethod(
                A2UIKotlinMethod.UNREGISTER_CUSTOM_FUNCTION,
                instanceId,
                name,
                null
            )
        }
    }

    /**
     * Forward a global `registerCustomRenderView` into this surface's Kotlin core.
     * The DSL applier uses this to decide between A2UICustomComponentView and a
     * DivView fallback for unknown/malformed viewNames.
     *
     * Also installs the native factory into this surface's own
     * [IKuiklyRenderExport] so subsequent `createRenderView(viewName, …)`
     * commands can locate it. Called from the shared registry's ChangeListener
     * so already-initialised surfaces immediately pick up newly-registered
     * custom views without waiting for the app to re-init them.
     */
    private fun dispatchRegisterCustomView(
        viewName: String,
        entry: A2UIAndroidCustomViewRegistry.Entry
    ) {
        // Install into this surface's own render export. `renderViewExport`
        // is a plain map.put; safe to call after init.
        renderExportInstance.renderViewExport(viewName, entry.creator, entry.shadowCreator)
        val core = coreEntry ?: return
        KuiklyRenderCoreContextScheduler.scheduleTask {
            core.callKotlinMethod(
                A2UIKotlinMethod.REGISTER_CUSTOM_VIEW,
                instanceId,
                viewName,
                null
            )
        }
    }

    /**
     * Companion to [dispatchRegisterCustomView]. Note: the native
     * [IKuiklyRenderExport] does not expose a public "remove" operation, so
     * factories previously installed via [dispatchRegisterCustomView] stay in
     * the map (unregister-after-use is best-effort). We still forward the
     * KMP-side unregister so newly-created surfaces treat the name as unknown
     * again.
     */
    private fun dispatchUnregisterCustomView(viewName: String) {
        val core = coreEntry ?: return
        KuiklyRenderCoreContextScheduler.scheduleTask {
            core.callKotlinMethod(
                A2UIKotlinMethod.UNREGISTER_CUSTOM_VIEW,
                instanceId,
                viewName,
                null
            )
        }
    }

    /**
     * Execute a registered custom function on this surface's behalf.
     *
     * Threading: this method is called by the NativeBridge delegate on the
     * Kuikly Context Thread. For SYNC handlers we invoke inline and return
     * the raw value to Kotlin; for ASYNC void handlers we post to the
     * Android main thread and return null immediately.
     *
     * @param name     Registered function name.
     * @param argsJson JSON string of arguments (already resolved).
     * @param spec     Wire descriptor "returnType|invokeMode".
     * @return The value to hand back to Kotlin, or `null` for VOID / when no
     *         matching handler is registered.
     */
    private fun invokeCustomFunctionLocally(name: String, argsJson: String, spec: String): Any? {
        // Delegate to the shared registry so the embedded-mode handler and
        // standalone surface view resolve custom functions from the same table.
        return A2UIAndroidCustomFunctionRegistry.invokeLocally(name, argsJson, spec)
    }

    // endregion

    // endregion

    // region Component Registration

    private fun registerDefaultRenderViews() {
        with(renderExportInstance) {
            renderViewExport(KRView.VIEW_NAME, { context -> KRView(context) })
            renderViewExport(KRRichTextView.VIEW_NAME, { context -> KRRichTextView(context) }, { KRRichTextShadow() })
            renderViewExport(KRRichTextView.GRADIENT_RICH_TEXT_VIEW, { context -> KRRichTextView(context) }, { KRRichTextShadow() })
            renderViewExport(KRImageView.VIEW_NAME, { context -> KRImageView(context) })
            renderViewExport(KRWrapperImageView.VIEW_NAME, { context -> KRWrapperImageView(context) })
            renderViewExport(KRRecyclerView.VIEW_NAME, { context -> KRRecyclerView(context) })
            renderViewExport(KRRecyclerView.VIEW_NAME_SCROLL_VIEW, { context -> KRRecyclerView(context) })
            renderViewExport(KRRecyclerContentView.VIEW_NAME, { context -> KRRecyclerContentView(context) })
            renderViewExport(KRTextFieldView.VIEW_NAME, { context -> KRTextFieldView(context, android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) })
            renderViewExport(KRTextAreaView.VIEW_NAME, { context -> KRTextAreaView(context, android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) })
            renderViewExport(KRCanvasView.VIEW_NAME, { context -> KRCanvasView(context) })
            renderViewExport(KRHoverView.VIEW_NAME, { context -> KRHoverView(context) })
            renderViewExport(KRActivityIndicatorView.VIEW_NAME, { context -> KRActivityIndicatorView(context) })
            renderViewExport(KRBlurView.VIEW_NAME, { context -> KRBlurView(context) })
            renderViewExport(KRPAGView.VIEW_NAME, { context -> KRPAGView(context) })
            renderViewExport(KRMaskView.VIEW_NAME, { context -> KRMaskView(context) })
            renderViewExport(KRVideoView.VIEW_NAME, { context -> KRVideoView(context) })
            renderViewExport(KRAudioView.VIEW_NAME, { context -> KRAudioView(context) })
            renderViewExport(KRModalView.VIEW_NAME, { context -> KRModalView(context) })
            // Register custom render views from the process-global registry.
            // Uses the shared A2UIAndroidCustomViewRegistry so that native-page
            // and Kuikly-page-embedded modes see the same table.
            for ((name, entry) in A2UIAndroidCustomViewRegistry.snapshot()) {
                renderViewExport(name, entry.creator, entry.shadowCreator)
            }
        }
    }

    // endregion

    // region Color Adapter

    private fun ensureColorParseAdapterRegistered() {
        if (KuiklyRenderAdapterManager.krColorParseAdapter == null) {
            KuiklyRenderAdapterManager.krColorParseAdapter = object : IKRColorParserAdapter {
                override fun toColor(colorStr: String): Int? {
                    return try {
                        if (colorStr.startsWith("#")) Color.parseColor(colorStr)
                        else colorStr.toLongOrNull()?.toInt()
                    } catch (e: Exception) { null }
                }
            }
        }
    }

    // endregion
}
