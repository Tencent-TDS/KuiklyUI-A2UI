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
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionNativeInvoker
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
     * methodId extension point. Supported on both Android (installed via
     * `A2UIKuiklyBaseViewSupport.wrap`) and HarmonyOS (installed via
     * `A2UIKuiklyRenderViewSupport.getCustomRenderModuleCreatorRegisterMap`).
     *
     * We must *not* call `BridgeManager.callModuleMethod("A2UIBridgeModule", …)`
     * on platforms that don't register the module:
     *
     * * On **iOS**, `KuiklyRenderCore` raises an **Objective-C `NSException`**
     *   ("创建Module失败 … 找不到对应Module实现") through `NSAssert` — this
     *   is *not* a Kotlin `Throwable`, so a `catch (_: Throwable)` in K/N
     *   cannot catch it. The exception then crosses the K/N boundary and
     *   trips `TerminateHandler::kotlinHandler()`, aborting the process.
     * * On **JVM** (unit tests, server renderer), no Kuikly render export
     *   is wired up at all.
     *
     * The only safe design is therefore "guard first, dispatch only on
     * platforms that register the module". [isModuleBasedPlatform] resolves
     * at compile-time via per-source-set `actual` so the JIT / K/N optimizer
     * can typically fold the check away entirely on non-module targets.
     *
     * A defensive `try/catch(Throwable)` is still kept so a misconfigured
     * host app (module name typo, register-order bug) degrades to a no-op
     * instead of taking down the render pipeline.
     */
    internal fun dispatchToA2UIBridgeModule(
        instanceId: String,
        method: String,
        payload: Any?,
        syncCall: Int
    ): Any? {
        // Hard gate: `A2UIBridgeModule` is only registered on Android / OHOS.
        // Never even attempt the call on iOS / JVM — see KDoc above.
        if (!isModuleBasedPlatform) return null
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
     * Get the NativeBridge for an instance.
     */
    internal fun getNativeBridge(instanceId: String): NativeBridge? {
        return nativeBridgeMap[instanceId]
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
     * routed to [A2UINativeMethod.INVOKE_CUSTOM_FUNCTION]; built-in like
     * `openUrl` routed to [A2UINativeMethod.EXECUTE_FUNCTION_CALL]).
     *
     * Returns `true` if the action was handled here (caller must NOT forward to
     * its host `.action { }` callback). Returns `false` for non-functionCall
     * actions and for cases the manager cannot reach the native bridge — the
     * caller should then forward the raw JSON to the host as before.
     */
    internal fun tryHandleFunctionCall(instanceId: String, actionJson: String): Boolean {
        // Legacy native-embedded path registers the NativeBridge via INIT →
        // initInstance; DSL-embedded path does NOT (it owns the bridge itself),
        // so we fall back to the DSL bridge's own NativeBridge when the map is empty.
        val nativeBridge = nativeBridgeMap[instanceId]
            ?: dslBridgeMap[instanceId]?.getNativeBridge()
            ?: return false
        return executeFunctionCallIfNeeded(instanceId, nativeBridge, actionJson)
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
        if (executeFunctionCallIfNeeded(instanceId, nativeBridge, actionJson)) {
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
            // Per A2UI protocol: intercept functionCall actions and execute locally
            if (!executeFunctionCallIfNeeded(instanceId, nativeBridge, action)) {
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
     * @return true if the action was a functionCall and was handled (caller should
     *         NOT forward to ACTION_CALLBACK); false otherwise.
     */
    private fun executeFunctionCallIfNeeded(
        instanceId: String,
        nativeBridge: NativeBridge,
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

            // In embedded mode (Kuikly page hosts an A2UISurface DSL), the DSL
            // creates its own child pager with `instanceId="a2ui_embed_N"`,
            // which has no ArkTS delegate → `callModuleMethod` by that id
            // finds no `A2UIBridgeModule` and returns null. The `A2UIBridgeModule`
            // is actually installed on the *host* Kuikly page's delegate
            // (see `A2UIKuiklyRenderViewSupport.getCustomRenderModuleCreatorRegisterMap`).
            // So we route by the DSL bridge's host pagerId when present.
            // Legacy Native-page mode returns null, and we fall back to
            // `instanceId` (which equals the child pager's own pagerId).
            val moduleInstanceId = dslBridgeMap[instanceId]?.getHostPagerId() ?: instanceId

            // First, check if this is a user-registered custom void function.
            // Non-void custom functions must not appear in an `action.functionCall`
            // slot (they are only valid as expressions), so we only intercept VOID here.
            val custom = A2UICustomFunctionRegistry.lookup(call)
            if (custom != null && custom.returnType == A2UIFunctionReturnType.VOID) {
                val spec = custom.toSpec()
                // iOS (Kuikly-Page mode): the host has installed a direct in-process
                // hook via `A2UICustomFunctionNativeInvoker`. Prefer it — it delivers
                // the invocation straight to the Swift-side global handler without
                // going through `nativeBridge.toNative(107, …)` (whose methodId is
                // unknown to Kuikly's iOS render core when A2UI is embedded inside a
                // Kuikly page) nor through the `A2UIBridgeModule` module bus (which
                // is HarmonyOS-only). If the hook is not installed (Android/OHOS,
                // or iOS native-page mode where liveSurfaces broadcasting already
                // covers registration), we fall through to the platform paths below.
                val hook = A2UICustomFunctionNativeInvoker.hook
                if (hook != null) {
                    try {
                        hook(call, argsJson, spec)
                    } catch (_: Exception) {
                        // Void actions are fire-and-forget: swallow any thrown
                        // exception so a mis-behaving custom handler cannot bring
                        // down the render pipeline.
                    }
                    nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
                    return true
                }
                nativeBridge.toNative(
                    A2UINativeMethod.INVOKE_CUSTOM_FUNCTION,
                    instanceId,
                    call,
                    argsJson,
                    spec,
                    null,
                    null
                )
                // Module-based platforms (Android & OHOS): also dispatch via
                // callModuleMethod. ASYNC void functions use syncCall=0
                // (fire-and-forget); SYNC void functions use syncCall=1 for
                // ordered execution. iOS/JVM no-op via [dispatchToA2UIBridgeModule].
                val syncCallFlag = if (custom.invokeMode == A2UIFunctionInvokeMode.SYNC) 1 else 0
                dispatchToA2UIBridgeModule(
                    moduleInstanceId,
                    "invokeCustomFunction",
                    "$call\n$spec\n$argsJson",
                    syncCallFlag
                )
                nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
                return true
            }

            // Dispatch to native layer via EXECUTE_FUNCTION_CALL for known built-in functions.
            // The native render layer (A2UISurfaceView) handles the actual UI
            // (e.g. opening a browser for openUrl).
            //
            // Per A2UI v0.9 spec the only standard `action.functionCall` intent
            // is `openUrl`. If the JSON tries to invoke a name that is neither a
            // registered custom function nor `openUrl`, log a warning so it is
            // easy to catch misspelled or non-spec `call` values in demos and
            // production JSON alike. We still forward the call to the platform
            // layer so existing platform-specific hooks (if any) keep working;
            // hosts that want strict behaviour should reject the payload
            // themselves in the platform-side executeFunctionCall handler.
            if (!A2UICustomFunctionRegistry.isReserved(call)) {
                println(
                    "[A2UI] warn: action.functionCall '$call' is not a registered custom " +
                        "function nor a reserved A2UI v0.9 built-in (only 'openUrl' is). " +
                        "Consider emitting action.event instead — see A2UI_INTEGRATION_*.md."
                )
            }
            nativeBridge.toNative(
                A2UINativeMethod.EXECUTE_FUNCTION_CALL,
                instanceId,
                call,
                argsJson,
                null,
                null,
                null
            )
            // Also dispatch via callModuleMethod for module-based platforms
            // (Android & OHOS). iOS/JVM no-op via [dispatchToA2UIBridgeModule].
            dispatchToA2UIBridgeModule(
                moduleInstanceId,
                "executeFunctionCall",
                "$call\n$argsJson",
                1
            )
            nativeBridge.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // endregion
}
