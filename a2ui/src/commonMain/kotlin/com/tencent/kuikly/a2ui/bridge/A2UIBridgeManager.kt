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

package com.tencent.kuikly.a2ui.bridge

import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.dsl.A2UIDSLBridge
import com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import com.tencent.kuikly.a2ui.function.A2UIFunctionInvokeMode
import com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.nvi.NativeBridge

/**
 * A2UI Bridge Manager - central dispatcher for A2UI instances.
 *
 * This is the Kotlin Common layer entry point for A2UI's JSON-driven rendering.
 * It now uses the DSL rendering path:
 *   JSON → A2UIDSLBridge → A2UIApplier → Kuikly DSL Component Tree → Kuikly Core (auto layout + render)
 *
 * Architecture:
 * - Native → Kotlin: via [callKotlinMethod] (INIT, WRITE_STREAM, FINISH_STREAM, SET_SURFACE_SIZE, VIEW_CLICK, DESTROY)
 * - Kotlin → Native: via Kuikly Core's standard NativeBridge (CREATE_RENDER_VIEW, SET_PROP, etc.)
 *
 * Each A2UI instance (identified by instanceId) has:
 * - A [NativeBridge] for Kuikly Core to send render commands to native
 * - An [A2UIDSLBridge] that manages the Kuikly DSL component tree
 */
object A2UIBridgeManager {

    private val nativeBridgeMap = mutableMapOf<String, NativeBridge>()
    private val dslBridgeMap = mutableMapOf<String, A2UIDSLBridge>()
    private val surfaceSizeMap = mutableMapOf<String, Pair<Float, Float>>()
    private val deviceSizeMap = mutableMapOf<String, Pair<Float, Float>>()
    private var isDidInit = false

    /**
     * Initialize the A2UIBridgeManager (called once on first use).
     */
    fun init() {
        if (isDidInit) return
        isDidInit = true
    }

    fun isDidInit(): Boolean = isDidInit

    /**
     * Route a call to the host-page-installed `A2UIBridgeModule`.
     *
     * The `A2UIBridgeModule` is a standard Kuikly module that A2UI render
     * pods register against the enclosing Kuikly page's delegate so the KMP
     * core can dispatch A2UI-specific requests (showDateTimePicker,
     * executeFunctionCall, invokeCustomFunction, contentHeightChanged,
     * actionCallback, openUrl, …) through the standard
     * `BridgeManager.callModuleMethod` bus instead of inventing a parallel
     * methodId extension point.
     *
     * All three target platforms participate in this channel:
     *   * Android: installed via `A2UIKuiklyBaseViewSupport.wrap`.
     *   * OHOS:    installed via `A2UIKuiklyRenderViewSupport
     *                            .getCustomRenderModuleCreatorRegisterMap`.
     *   * iOS:     resolved by `KuiklyRenderLayerHandler` via
     *              `NSClassFromString("A2UIBridgeModule")` — the render pod
     *              ships the class and keeps it alive with `-ObjC` /
     *              `-force_load`.
     *
     * iOS carries a sharp edge that drives the two-layer gate below:
     * if `A2UIBridgeModule` is somehow NOT linked into the process (host
     * app forgot the pod, dead-stripping surprise), `KuiklyRenderCore`
     * raises an **Objective-C `NSException`** ("创建Module失败 … 找不到
     * 对应Module实现") through `NSAssert`. That exception is NOT a Kotlin
     * `Throwable`, so a `catch (_: Throwable)` in K/N cannot catch it —
     * it crosses the K/N boundary and aborts the process. We therefore
     * `objc_lookUpClass`-check availability *before* calling the module
     * dispatcher on iOS; on Android/OHOS/JVM the check is a cheap
     * constant.
     *
     * [isModuleBasedPlatform] still resolves at compile-time via
     * per-source-set `actual` so JVM / future non-render targets fold
     * the whole function to a no-op.
     *
     * A defensive `try/catch(Throwable)` is kept so a misconfigured host
     * app (module name typo, register-order bug) still degrades to a
     * no-op instead of taking down the render pipeline.
     */
    internal fun dispatchToA2UIBridgeModule(
        instanceId: String,
        method: String,
        payload: Any?,
        syncCall: Int
    ): Any? {
        // Hard gate 1: compile-time, folds away on JVM.
        if (!isModuleBasedPlatform) return null
        // Hard gate 2: runtime availability check (iOS: objc_lookUpClass).
        //   On Android/OHOS this is a constant `true`; on iOS it catches
        //   the "pod not linked" case before Kuikly's own NSAssert fires.
        if (!isA2UIBridgeModuleAvailable()) return null
        return try {
            BridgeManager.callModuleMethod(
                instanceId,
                "A2UIBridgeModule",
                method,
                payload,
                null,
                syncCall
            )
        } catch (_: Throwable) {
            // Host app hasn't registered A2UIBridgeModule yet —
            // treat as no-op so the render pipeline stays alive.
            null
        }
    }

    /**
     * Register a NativeBridge for an instance.
     * Must be called before callKotlinMethod for that instance.
     */
    fun registerNativeBridge(instanceId: String, nativeBridge: NativeBridge) {
        nativeBridgeMap[instanceId] = nativeBridge
    }

    /**
     * Dispatch a method call from Native to Kotlin.
     *
     * @param methodId One of [A2UIKotlinMethod] constants
     * @param arg0 First argument (typically instanceId)
     * @param arg1 Second argument (varies by method)
     * @param arg2 Third argument (varies by method)
     */
    fun callKotlinMethod(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?) {
        when (methodId) {
            A2UIKotlinMethod.INIT -> {
                val instanceId = arg0 as? String ?: return
                val configJson = arg1 as? String
                initInstance(instanceId, configJson)
            }
            A2UIKotlinMethod.WRITE_STREAM -> {
                val instanceId = arg0 as? String ?: return
                val chunk = arg1 as? String ?: return
                writeStream(instanceId, chunk)
            }
            A2UIKotlinMethod.FINISH_STREAM -> {
                val instanceId = arg0 as? String ?: return
                finishStream(instanceId)
            }
            A2UIKotlinMethod.DESTROY -> {
                val instanceId = arg0 as? String ?: return
                destroyInstance(instanceId)
            }
            A2UIKotlinMethod.SET_SURFACE_SIZE -> {
                val instanceId = arg0 as? String ?: return
                val sizeStr = arg1 as? String ?: return
                setSurfaceSize(instanceId, sizeStr)
            }
            A2UIKotlinMethod.VIEW_CLICK -> {
                val instanceId = arg0 as? String ?: return
                val tagStr = arg1 as? String ?: return
                val tag = tagStr.toIntOrNull() ?: return
                handleViewClick(instanceId, tag)
            }
            A2UIKotlinMethod.VIEW_EVENT -> {
                val instanceId = arg0 as? String ?: return
                val tagAndEvent = arg1 as? String ?: return
                val data = arg2 as? String
                handleViewEvent(instanceId, tagAndEvent, data)
            }
            A2UIKotlinMethod.LAYOUT_VIEW -> {
                val instanceId = arg0 as? String ?: return
                val dslBridge = dslBridgeMap[instanceId] ?: return
                val pager = dslBridge.getPager() ?: return
                pager.onLayoutView()
            }
            A2UIKotlinMethod.PICKER_RESULT -> {
                val instanceId = arg0 as? String ?: return
                val tagAndValue = arg1 as? String ?: return
                handlePickerResult(instanceId, tagAndValue)
            }
            A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION -> {
                // instanceId (arg0) is ignored: registration is process-global.
                val name = arg1 as? String ?: return
                val spec = arg2 as? String ?: return
                A2UICustomFunctionRegistry.register(name, spec)
            }
            A2UIKotlinMethod.UNREGISTER_CUSTOM_FUNCTION -> {
                val name = arg1 as? String ?: return
                A2UICustomFunctionRegistry.unregister(name)
            }
            A2UIKotlinMethod.REGISTER_CUSTOM_VIEW -> {
                // instanceId (arg0) is ignored: registration is process-global.
                val viewName = arg1 as? String ?: return
                A2UICustomViewRegistry.register(viewName)
            }
            A2UIKotlinMethod.UNREGISTER_CUSTOM_VIEW -> {
                val viewName = arg1 as? String ?: return
                A2UICustomViewRegistry.unregister(viewName)
            }
            A2UIKotlinMethod.REGISTER_ICON -> {
                // instanceId (arg0) is ignored: registration is process-global.
                val name = arg1 as? String ?: return
                val uri = arg2 as? String ?: return
                A2UIIconRegistry.registerIcon(name, uri)
            }
            A2UIKotlinMethod.UNREGISTER_ICON -> {
                // instanceId (arg0) is ignored: registration is process-global.
                val name = arg1 as? String ?: return
                A2UIIconRegistry.unregisterIcon(name)
            }
        }
    }

    /**
     * Get the DSL bridge for an instance.
     */
    internal fun getDSLBridge(instanceId: String): A2UIDSLBridge? {
        return dslBridgeMap[instanceId]
    }

    /**
     * Resolve the host pager id (i.e. the pager id of the enclosing Kuikly
     * page in Kuikly-page-embedded hosting mode) for a given A2UI DSL
     * instance id. Returns `null` when the bridge is unknown or when the
     * bridge is running in legacy native-page mode (no external host).
     *
     * Exposed as `public` so that platform-side entry points (currently the
     * HarmonyOS `a2uiNotifyPickerResult` NAPI writer) can look up the pager
     * whose Kuikly Context Thread they need to schedule onto before touching
     * the DSL tree.
     */
    fun getHostPagerIdForDsl(instanceId: String): String? {
        return dslBridgeMap[instanceId]?.getHostPagerId()
    }

    /**
     * Register an externally-created [A2UIDSLBridge] under [instanceId].
     *
     * Used by the DSL-embedded surface (`A2UISurfaceDSLView`) so that platform
     * callbacks routed by instance id (picker result, custom-function invocation,
     * custom-view registration, …) can find the surface's bridge.
     *
     * Unlike the legacy native path which enters via [callKotlinMethod] +
     * INIT → [initInstance], the DSL-embedded surface owns the bridge lifecycle
     * itself and only needs to publish the instance id → bridge mapping.
     */
    fun registerDslBridge(instanceId: String, bridge: A2UIDSLBridge) {
        dslBridgeMap[instanceId] = bridge
    }

    /**
     * Unregister a previously [registerDslBridge]-ed bridge. Called when the
     * DSL-embedded surface is detached (willRemoveFromParentView).
     *
     * NOTE: This does NOT destroy the bridge; that is the caller's responsibility.
     */
    fun unregisterDslBridge(instanceId: String) {
        dslBridgeMap.remove(instanceId)
    }

    /**
     * Attempt to intercept an A2UI `action.functionCall` and execute it via the
     * native/module bridge locally, so DSL-embedded surfaces get the same
     * behaviour as the legacy native-embedded surfaces (custom void functions
     * routed to [A2UINativeMethod.INVOKE_CUSTOM_FUNCTION] / Module channel;
     * built-in like `openUrl` routed to [A2UINativeMethod.EXECUTE_FUNCTION_CALL]
     * / Module channel).
     *
     * Returns `true` if the action was handled here (caller must NOT forward to
     * its host `.action { }` callback). Returns `false` for non-functionCall
     * actions.
     *
     * Dispatch channel is picked by hosting mode:
     *   * Native-page mode (`nativeBridgeMap[instanceId]` is set): go through
     *     the per-surface `NativeBridge.toNative(methodId=106/107, …)`.
     *   * Kuikly-page-embedded mode (`dslBridgeMap[instanceId]` has a non-null
     *     `getHostPagerId()`): go through
     *     [dispatchToA2UIBridgeModule] with the host page's pagerId.
     */
    internal fun tryHandleFunctionCall(instanceId: String, actionJson: String): Boolean {
        // Native-page mode is identified by the presence of a per-surface
        // NativeBridge in `nativeBridgeMap` (populated by `initInstance`).
        // Embedded mode leaves the map empty and uses the Module channel
        // instead (resolved inside executeFunctionCallIfNeeded by looking
        // up the host pagerId through the DSL bridge).
        if (dslBridgeMap[instanceId] == null && nativeBridgeMap[instanceId] == null) {
            return false
        }
        return executeFunctionCallIfNeeded(instanceId, actionJson)
    }

    /**
     * Ask the host-platform side (currently ArkTS on OHOS) to compute the
     * intrinsic size of a custom component via the standard Kuikly Module
     * channel. Returns `null` when:
     *
     *   * the current platform does not route A2UI through the Module channel
     *     (`isModuleBasedPlatform == false` on JVM / unknown targets),
     *   * no `A2UIBridgeModule` is linked into the process
     *     (`isA2UIBridgeModuleAvailable() == false` on iOS when the pod is
     *     missing — a safety net; iOS actually goes through its native
     *     Shadow path and never calls this),
     *   * the business side has not registered a `measurer` for `viewName`,
     *     in which case the ArkTS module returns an empty string,
     *   * the Module call itself threw.
     *
     * On a successful return the payload is `"width|height"` in the same
     * unit the native renderer expects (vp on OHOS). We parse it into
     * `(width, height)` and hand it back to [A2UICustomComponentView.measure].
     *
     * Called synchronously from the Kuikly layout thread; the Module call is
     * dispatched with `syncCall = 1` so the ArkTS measurer runs inline on
     * the same thread and the return value lands here without a thread hop.
     *
     * @param instanceId Pager id that owns the custom view — typically this
     *     is the A2UI DSL instance id in native-page mode or the host Kuikly
     *     pager id in embedded mode. We pass it straight through to
     *     [BridgeManager.callModuleMethod] so the module registry resolves
     *     against the correct `KRNativeInstance`.
     * @param viewName   The registered custom component name (e.g. "A2UIWeb").
     * @param nativeRef  The Kuikly Core render-view tag — passed so the
     *     ArkTS measurer can locate the live view instance it has already
     *     `setProp`-populated and read any state that influences size.
     * @param constraintWidth  Cross-axis constraint handed down by the Flex
     *     engine, in the renderer's native unit.
     * @param constraintHeight Main-axis constraint handed down by the Flex
     *     engine; `-1` when unconstrained.
     */
    internal fun calculateCustomViewSize(
        instanceId: String,
        viewName: String,
        nativeRef: Int,
        constraintWidth: Float,
        constraintHeight: Float
    ): Pair<Float, Float>? {
        // Payload format mirrors the other A2UIBridgeModule methods — plain
        // "\n"-separated strings so ArkTS can split and parse without a
        // JSON dependency. See `A2UIBridgeModule.call('calculateCustomViewSize')`
        // in `a2ui-render-ohos/.../A2UISurfaceView.ets`.
        val payload = "$viewName\n$nativeRef\n$constraintWidth\n$constraintHeight"
        val raw = dispatchToA2UIBridgeModule(instanceId, "calculateCustomViewSize", payload, 1)
        val str = raw as? String
        if (str.isNullOrEmpty()) return null
        val parts = str.split("|", limit = 2)
        if (parts.size < 2) return null
        val w = parts[0].toFloatOrNull() ?: return null
        val h = parts[1].toFloatOrNull() ?: return null
        return w to h
    }

    /**
     * Entry point for the host-platform (ArkTS on OHOS) to report that a
     * custom component's intrinsic size has changed asynchronously — e.g. a
     * WebView finished loading and now knows its content height.
     *
     * Locates the [A2UICustomComponentView] registered under
     * `(pagerId, nativeRef)` and delegates to its [A2UICustomComponentView
     * .handleNativeResize], which no-ops when the dimensions did not
     * actually change (so the business side can be noisy without triggering
     * a redundant second layout pass).
     *
     * Must be invoked on the Kuikly Context Thread of the owning pager —
     * the ArkTS entry point in `A2UIRegistryBridge.kt` hops onto that
     * thread via `com_tencent_kuikly_ScheduleContextTask` before calling
     * this function, mirroring the existing [a2uiNotifyPickerResult] path.
     *
     * @param pagerId   Pager id that owns the target custom view (either the
     *     A2UI DSL instance id in native-page mode or the host Kuikly pager
     *     id in embedded mode — whichever was passed to the measurer call
     *     that originally made the component appear in the registry).
     * @param nativeRef Kuikly Core render-view tag of the custom view.
     * @param newWidth  New intrinsic width in the renderer's native unit, or
     *     a non-positive value to keep the current width.
     * @param newHeight New intrinsic height in the renderer's native unit, or
     *     a non-positive value to keep the current height.
     */
    fun notifyCustomViewResize(
        pagerId: String,
        nativeRef: Int,
        newWidth: Float,
        newHeight: Float
    ) {
        val view = com.tencent.kuikly.a2ui.views
            .A2UICustomComponentViewRegistry.lookup(pagerId, nativeRef) ?: return
        view.handleNativeResize(newWidth, newHeight)
    }

    // region Private Methods

    /**
     * Handle a native view event (e.g. pan gesture, progress change).
     * Dispatches the event to the view's event handler via Pager.onViewEvent.
     *
     * @param instanceId The A2UI instance ID
     * @param tagAndEvent Format: "nativeRef|eventName"
     * @param data Event data as string (may be null)
     */
    private fun handleViewEvent(instanceId: String, tagAndEvent: String, data: String?) {
        val parts = tagAndEvent.split("|", limit = 2)
        if (parts.size < 2) return
        val nativeRef = parts[0].toIntOrNull() ?: return
        val eventName = parts[1]

        val dslBridge = dslBridgeMap[instanceId] ?: return
        val pager = dslBridge.getPager() ?: return

        // Dispatch event to the view's event handler
        // Pager.onViewEvent uses nativeRefViewMap to find the view and calls onFireEvent
        val dataJson = if (data != null) {
            try {
                com.tencent.kuikly.core.nvi.serialization.json.JSONObject(data)
            } catch (e: Exception) {
                // If data is not valid JSON, pass null
                null
            }
        } else {
            null
        }
        // Ensure currentPageId is set for ReactiveObserver to work correctly
        com.tencent.kuikly.core.manager.BridgeManager.currentPageId = pager.pagerId
        pager.onViewEvent(nativeRef, eventName, dataJson)

        // After event handling, trigger layout recalculation and UI flush.
        // This is needed because event handlers (e.g. SliderView.handlePanGesture)
        // may update observable properties that trigger reactive updates and markDirty().
        // In normal Kuikly, KuiklyRenderCoreUIScheduler handles this automatically.
        pager.onLayoutView()
        pager.syncFlushUI()
    }

    private fun handleViewClick(instanceId: String, tag: Int) {
        val dslBridge = dslBridgeMap[instanceId] ?: return
        val adapter = dslBridge.protocolAdapter ?: return

        // The native layer reports the Kuikly Core nativeRef (render view tag),
        // but tagToAction uses A2UI protocol tags. Convert nativeRef → protocol tag.
        val pager = dslBridge.getPager()
        val protocolTag = pager?.getProtocolTagByNativeRef(tag) ?: return

        val actionJson = adapter.getActionForTag(protocolTag) ?: return

        // Send action callback to native layer
        val nativeBridge = nativeBridgeMap[instanceId] ?: return

        // Per A2UI protocol: action.functionCall invokes a client-side function.
        // Execute known functions locally via EXECUTE_FUNCTION_CALL native method
        // (e.g. openUrl) instead of just passing the raw JSON to the host.
        // `executeFunctionCallIfNeeded` resolves the transport internally
        // (Native-page NativeBridge from `nativeBridgeMap` vs. Module channel
        // via `dslBridgeMap[...].getHostPagerId()`), so we no longer need to
        // pass a bridge here.
        if (executeFunctionCallIfNeeded(instanceId, actionJson)) {
            return
        }

        nativeBridge.toNative(
            A2UINativeMethod.ACTION_CALLBACK,
            instanceId,
            protocolTag,
            actionJson,
            "dsl-surface",
            null,
            null
        )
        // Also dispatch via callModuleMethod for module-based platforms
        // (Android & OHOS). On iOS/JVM this module isn't registered — the
        // helper short-circuits via [isModuleBasedPlatform] so the call is
        // a safe no-op there. See [dispatchToA2UIBridgeModule].
        dispatchToA2UIBridgeModule(
            instanceId,
            "actionCallback",
            "$protocolTag\n$actionJson",
            1
        )
        nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
    }

    private fun initInstance(instanceId: String, configJson: String?) {
        val nativeBridge = nativeBridgeMap[instanceId] ?: return

        // Create DSL bridge with the NativeBridge
        val dslBridge = A2UIDSLBridge(instanceId, nativeBridge)
        dslBridgeMap[instanceId] = dslBridge

        // Parse real screen size from the config (used to size full-screen overlays like Modal).
        var deviceWidth = 0f
        var deviceHeight = 0f
        if (!configJson.isNullOrEmpty()) {
            try {
                val cfg = com.tencent.kuikly.core.nvi.serialization.json.JSONObject(configJson)
                deviceWidth = cfg.optDouble("deviceWidth", 0.0).toFloat()
                deviceHeight = cfg.optDouble("deviceHeight", 0.0).toFloat()
            } catch (e: Exception) {
                // Ignore malformed config; overlays fall back to heuristic sizing.
            }
        }

        // Set up content height reporting via NativeBridge
        dslBridge.onContentHeightChanged = { height ->
            // Report content height to native layer for self-sizing
            nativeBridge.toNative(
                A2UINativeMethod.CONTENT_HEIGHT_CHANGED,
                instanceId,
                height,
                null,
                null,
                null,
                null
            )
            // Also report via callModuleMethod for module-based platforms
            // (Android & OHOS). Use syncCall=1 to ensure the Module is called
            // synchronously on the main thread before FLUSH_UI_TASKS returns,
            // so contentHeight is updated immediately. On iOS/JVM where the
            // module isn't registered the helper no-ops via
            // [isModuleBasedPlatform]. See [dispatchToA2UIBridgeModule].
            dispatchToA2UIBridgeModule(
                instanceId,
                "contentHeightChanged",
                height,
                1
            )
            nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
        }

        // Set up action callback
        dslBridge.onAction = { surfaceId, tag, action ->
            // Per A2UI protocol: intercept functionCall actions and execute locally.
            // `executeFunctionCallIfNeeded` picks the Native-page transport
            // (NativeBridge from `nativeBridgeMap`) itself when the action
            // is a known functionCall; this closure's `nativeBridge` capture
            // is still used below for the non-functionCall ACTION_CALLBACK
            // fallback.
            if (!executeFunctionCallIfNeeded(instanceId, action)) {
                nativeBridge.toNative(
                    A2UINativeMethod.ACTION_CALLBACK,
                    instanceId,
                    tag,
                    action,
                    surfaceId,
                    null,
                    null
                )
                // Also dispatch via callModuleMethod for module-based platforms
                // (Android & OHOS). No-op on iOS/JVM via [dispatchToA2UIBridgeModule].
                dispatchToA2UIBridgeModule(
                    instanceId,
                    "actionCallback",
                    "$tag\n$action",
                    1
                )
                nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
            }
        }

        // Forward Markdown link taps to the host so it can open the URL.
        dslBridge.onOpenUrl = { url ->
            nativeBridge.toNative(
                A2UINativeMethod.OPEN_URL,
                instanceId,
                url,
                null,
                null,
                null,
                null
            )
            // Also dispatch via callModuleMethod for module-based platforms
            // (Android & OHOS). We reuse the existing executeFunctionCall/openUrl
            // branch in A2UIBridgeModule by wrapping the URL as a functionCall
            // payload. On iOS/JVM this module isn't registered so the helper
            // silently no-ops. See [dispatchToA2UIBridgeModule].
            val safeUrl = url.replace("\\", "\\\\").replace("\"", "\\\"")
            dispatchToA2UIBridgeModule(
                instanceId,
                "executeFunctionCall",
                "openUrl\n{\"url\":\"$safeUrl\"}",
                1
            )
            nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
        }

        // Set up data model change notification
        dslBridge.onDataModelChanged = { dataModelJson, changedKey ->
            nativeBridge.toNative(
                A2UINativeMethod.DATA_MODEL_CHANGED,
                instanceId,
                dataModelJson,
                changedKey,
                null,
                null,
                null
            )
            nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
        }

        // Initialize with stored surface size if available
        val storedSize = surfaceSizeMap[instanceId]
        // If configJson didn't provide device size, use stored device size from setSurfaceSize
        val storedDeviceSize = deviceSizeMap[instanceId]
        val effectiveDeviceWidth = if (deviceWidth > 0f) deviceWidth else (storedDeviceSize?.first ?: 0f)
        val effectiveDeviceHeight = if (deviceHeight > 0f) deviceHeight else (storedDeviceSize?.second ?: 0f)
        if (storedSize != null) {
            dslBridge.initialize(storedSize.first, effectiveDeviceWidth, effectiveDeviceHeight)
        } else {
            dslBridge.initialize(0f, effectiveDeviceWidth, effectiveDeviceHeight)
        }
    }

    private fun writeStream(instanceId: String, chunk: String) {
        val dslBridge = dslBridgeMap[instanceId] ?: return
        dslBridge.writeStream(chunk)
        // Flush pending UI tasks to main thread after processing stream messages.
        // Without this, UI operations (e.g. setCss_src for data-bound images) remain
        // in the pendingUITasks buffer until an unrelated event triggers a flush,
        // causing images/views to not render until user interaction.
        val nativeBridge = nativeBridgeMap[instanceId] ?: return
        nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
    }

    private fun finishStream(instanceId: String) {
        val dslBridge = dslBridgeMap[instanceId] ?: return
        dslBridge.finishStream()
        // Flush pending UI tasks after stream finish (layout + frame updates).
        val nativeBridge = nativeBridgeMap[instanceId] ?: return
        nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
    }

    private fun setSurfaceSize(instanceId: String, sizeStr: String) {
        val parts = sizeStr.split(",")
        if (parts.size >= 2) {
            val width = parts[0].toFloatOrNull() ?: return
            val height = parts[1].toFloatOrNull() ?: return
            // Parse optional device dimensions (appended by HarmonyOS ArkTS layer)
            // Format: "width,height,deviceWidth,deviceHeight"
            val deviceWidth = if (parts.size >= 3) parts[2].toFloatOrNull() ?: 0f else 0f
            val deviceHeight = if (parts.size >= 4) parts[3].toFloatOrNull() ?: 0f else 0f
            // Store the size for this instance
            surfaceSizeMap[instanceId] = Pair(width, height)
            if (deviceWidth > 0f && deviceHeight > 0f) {
                deviceSizeMap[instanceId] = Pair(deviceWidth, deviceHeight)
            }
            // Apply to DSL bridge if it exists
            val dslBridge = dslBridgeMap[instanceId]
            if (dslBridge != null) {
                dslBridge.setSurfaceSize(width, height)
                // Update device dimensions for full-screen overlays (Modal)
                if (deviceWidth > 0f && deviceHeight > 0f) {
                    dslBridge.setDeviceSize(deviceWidth, deviceHeight)
                }
            }
        }
    }

    private fun destroyInstance(instanceId: String) {
        val dslBridge = dslBridgeMap.remove(instanceId)
        dslBridge?.destroy()
        val nativeBridge = nativeBridgeMap.remove(instanceId)
        nativeBridge?.destroy()
        surfaceSizeMap.remove(instanceId)
        deviceSizeMap.remove(instanceId)
    }

    /**
     * Handle date/time picker result from native layer.
     * Format: "tag|isoString" (e.g. "5|2025-12-15T17:30:00Z")
     */
    private fun handlePickerResult(instanceId: String, tagAndValue: String) {
        val parts = tagAndValue.split("|", limit = 2)
        if (parts.size < 2) return
        val tag = parts[0].toIntOrNull() ?: return
        val isoString = parts[1]

        val dslBridge = dslBridgeMap[instanceId] ?: return
        dslBridge.handlePickerResult(tag, isoString)

        // After handling, trigger layout recalculation and UI flush
        val pager = dslBridge.getPager() ?: return
        pager.onLayoutView()
        pager.syncFlushUI()
    }

    /**
     * Check if the action JSON contains a "functionCall" and execute it locally.
     * Per A2UI protocol, action.functionCall invokes a client-side function
     * (e.g. openUrl) that should be executed by the framework rather
     * than forwarded to the host as a raw event.
     *
     * Dispatch strategy (symmetric with [A2UIDSLBridge.showDateTimePicker] /
     * [A2UIDSLBridge.invokeCustomFunctionOnNative]):
     *
     * * **Native-page mode** (`nativeBridgeMap[instanceId]` is set): send
     *   methodId 106/107 through the per-surface NativeBridge; the surface's
     *   `callNative(…)` on each platform handles it directly. Follow with
     *   `FLUSH_UI_TASKS` to keep the host's render queue warm.
     * * **Kuikly-page-embedded mode** (the DSL bridge reports a non-null
     *   `getHostPagerId()`): dispatch through the Module channel keyed by
     *   the host pager id so the enclosing page's `A2UIBridgeModule` picks
     *   it up. The host page's own render loop flushes UI tasks after we
     *   return, so no separate `FLUSH_UI_TASKS` is needed here.
     *
     * @return true if the action was a functionCall and was handled (caller should
     *         NOT forward to ACTION_CALLBACK); false otherwise.
     */
    private fun executeFunctionCallIfNeeded(
        instanceId: String,
        actionJson: String
    ): Boolean {
        // Quick check to avoid parsing non-functionCall actions
        if (!actionJson.contains("\"functionCall\"")) return false

        try {
            val actionObj = com.tencent.kuikly.core.nvi.serialization.json.JSONObject(actionJson)
            val functionCall = actionObj.optJSONObject("functionCall") ?: return false

            val call = functionCall.optString("call", "")
            val args = functionCall.optJSONObject("args")
            val argsJson = args?.toString() ?: "{}"

            // Decide which transport to use.
            //   * Native-page mode: `nativeBridgeMap[instanceId]` was populated
            //     by `initInstance`, so we can hit `nativeBridge.toNative(…)`.
            //   * Kuikly-page-embedded mode: the DSL-embedded surface never
            //     registered a NativeBridge under its own instanceId, but it
            //     did `registerDslBridge`, and that bridge knows the enclosing
            //     Kuikly page's pagerId. We route the Module call through that
            //     host pagerId so `A2UIBridgeModule` (installed on the host's
            //     delegate) actually receives it.
            val transport = nativeBridgeMap[instanceId]
            val hostPagerId = dslBridgeMap[instanceId]?.getHostPagerId()

            // First, check if this is a user-registered custom void function.
            // Non-void custom functions must not appear in an `action.functionCall`
            // slot (they are only valid as expressions), so we only intercept VOID here.
            val custom = A2UICustomFunctionRegistry.lookup(call)
            if (custom != null && custom.returnType == A2UIFunctionReturnType.VOID) {
                val spec = custom.toSpec()
                val syncCallFlag = if (custom.invokeMode == A2UIFunctionInvokeMode.SYNC) 1 else 0
                if (transport != null) {
                    // Native-page: directly invoke via NativeBridge; the
                    // per-surface A2UISurfaceView handles methodId=107.
                    transport.toNative(
                        A2UINativeMethod.INVOKE_CUSTOM_FUNCTION,
                        instanceId,
                        call,
                        argsJson,
                        spec,
                        null,
                        null
                    )
                    transport.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
                } else if (hostPagerId != null) {
                    // Kuikly-page-embedded: Module channel to the host page's
                    // A2UIBridgeModule. ASYNC void functions use syncCall=0
                    // (fire-and-forget); SYNC void functions use syncCall=1
                    // for ordered execution.
                    dispatchToA2UIBridgeModule(
                        hostPagerId,
                        "invokeCustomFunction",
                        "$call\n$spec\n$argsJson",
                        syncCallFlag
                    )
                }
                return true
            }

            // Built-in function (per A2UI v0.9 only `openUrl` is reserved).
            if (!A2UICustomFunctionRegistry.isReserved(call)) {
                println(
                    "[A2UI] warn: action.functionCall '$call' is not a registered custom " +
                        "function nor a reserved A2UI v0.9 built-in (only 'openUrl' is). " +
                        "Consider emitting action.event instead — see A2UI_INTEGRATION_*.md."
                )
            }
            if (transport != null) {
                // Native-page: EXECUTE_FUNCTION_CALL methodId=106 handled
                // directly by A2UISurfaceView.callNative on each platform.
                transport.toNative(
                    A2UINativeMethod.EXECUTE_FUNCTION_CALL,
                    instanceId,
                    call,
                    argsJson,
                    null,
                    null,
                    null
                )
                transport.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
            } else if (hostPagerId != null) {
                // Kuikly-page-embedded: Module channel.
                dispatchToA2UIBridgeModule(
                    hostPagerId,
                    "executeFunctionCall",
                    "$call\n$argsJson",
                    1
                )
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // endregion
}
