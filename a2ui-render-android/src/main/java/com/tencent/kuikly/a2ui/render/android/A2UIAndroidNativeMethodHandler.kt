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

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod
import com.tencent.kuikly.core.render.android.scheduler.KuiklyRenderCoreContextScheduler
import java.lang.ref.WeakReference

/**
 * Placeholder instanceId used when forwarding a process-global custom-function
 * registration into the KMP-side [A2UIBridgeManager]. The KMP handler for
 * REGISTER_CUSTOM_FUNCTION intentionally ignores arg0 (see the KDoc there:
 * "registration is process-global"), so any non-null string is fine — we just
 * want a stable, human-readable token to make logs easier to reason about.
 */
private const val PROCESS_GLOBAL_INSTANCE_ID = "__a2ui_process_global__"

/**
 * Native-side handler for the A2UI protocol methodIds that live *outside* the
 * built-in Kuikly render enum (SHOW_DATE_TIME_PICKER, EXECUTE_FUNCTION_CALL,
 * INVOKE_CUSTOM_FUNCTION, …).
 *
 * There are two ways A2UI content can be rendered on Android:
 *
 * 1. **Native-page mode** — the app hosts an [A2UISurfaceView] directly. That
 *    view owns its own `A2UICoreEntry.Delegate`, so it dispatches every A2UI
 *    methodId inline. It re-uses the [showDateTimePickerDialog] /
 *    [executeFunctionCall] / [sendPickerResult] utility methods below so the
 *    behaviour is byte-for-byte identical with the embedded path.
 *
 * 2. **Kuikly-page-embedded mode** — the app hosts a Kuikly page that includes
 *    `A2UISurfaceDSLView`, and A2UI reuses the enclosing page's render core.
 *    The host page's render core has no receiver for A2UI-specific methodIds,
 *    so the KMP core instead routes those requests through
 *    `BridgeManager.callModuleMethod("A2UIBridgeModule", …)`. The
 *    [A2UIBridgeModule] endpoint is installed on the host page's delegate by
 *    [A2UIKuiklyBaseViewSupport.wrap] and reuses the same shared utilities
 *    exposed by this class.
 *
 * Install once during application startup so Activity tracking (for picker
 * dialog contexts) and custom-function/view KMP bridges are wired up:
 * ```kotlin
 * override fun onCreate() {
 *     super.onCreate()
 *     A2UIAndroidNativeMethodHandler.install(this)
 * }
 * ```
 */
object A2UIAndroidNativeMethodHandler {

    private const val TAG = "A2UIExtHandler"

    private val mainHandler = Handler(Looper.getMainLooper())

    /** Weak reference to the currently-resumed Activity for popping dialogs. */
    private var topActivityRef: WeakReference<Activity>? = null

    /** Fallback context for cases when no Activity is resumed. */
    private var appContextRef: WeakReference<Context>? = null

    @Volatile
    private var installed = false

    /**
     * Wire up Activity tracking (for picker dialog contexts) and the KMP-
     * side bridges for A2UI custom-function / custom-view registries.
     * Idempotent; safe to call from every app's `Application.onCreate`.
     *
     * In Kuikly-page-embedded mode, the actual A2UI-specific methodIds are
     * delivered through [A2UIBridgeModule] on the host page; this call does
     * NOT install that module (that happens via [A2UIKuiklyBaseViewSupport.wrap]
     * per Kuikly page). It only installs the once-per-process state.
     */
    @JvmStatic
    fun install(application: Application) {
        if (installed) return
        installed = true
        appContextRef = WeakReference(application)
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                topActivityRef = WeakReference(activity)
            }
            override fun onActivityPaused(activity: Activity) {
                if (topActivityRef?.get() === activity) topActivityRef = null
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (topActivityRef?.get() === activity) topActivityRef = null
            }
        })

        // Bridge every custom-function registration into the KMP-side
        // A2UICustomFunctionRegistry so evaluation-time lookups
        // (A2UIProtocolAdapter.evaluateCustomFunctionAsString /
        // evaluateCustomFunctionAsBoolean) can see them in the embedded mode
        // (A2UISurfaceDSLView).
        //
        // In native-page mode (standalone A2UISurfaceView) the surface's own
        // KotlinCoreEntry replays the registry snapshot during its INIT and a
        // separate ChangeListener fan-outs subsequent updates to every live
        // surface — see A2UISurfaceView.ensureCustomFunctionListenerInstalled.
        //
        // Embedded mode has no such per-surface KotlinCoreEntry (A2UI reuses
        // the host page's), so we register a second, process-global listener
        // here that routes register/unregister directly to A2UIBridgeManager.
        // The KMP `A2UICustomFunctionRegistry` is a JVM-singleton `object`, so
        // even if both listeners fire (native page + embedded page both alive
        // in the same app), the resulting registrations are idempotent.
        installCustomFunctionRegistryBridge()

        // Bridge every custom-view registration into the KMP-side
        // A2UICustomViewRegistry so A2UIApplier.createDSLView can distinguish
        // user-supplied custom components (A2UICustomComponentView) from
        // unknown/malformed viewNames (DivView fallback) in embedded mode.
        //
        // The native factory itself is installed into the host page's
        // IKuiklyRenderExport by [A2UIKuiklyBaseViewSupport.wrap]; this
        // bridge is only responsible for forwarding the viewName metadata
        // to KMP so the DSL applier sees the same set of custom components
        // as the native page mode.
        installCustomViewRegistryBridge()
    }

    /**
     * Install [customFunctionRegistryListener] (idempotent) and replay every
     * function that has already been registered on
     * [A2UIAndroidCustomFunctionRegistry] into the KMP-side registry.
     *
     * Callers are expected to hold the same [installed] guard used by [install].
     */
    private fun installCustomFunctionRegistryBridge() {
        if (customFunctionListenerInstalled) return
        customFunctionListenerInstalled = true
        // 1) Replay existing entries so a function registered *before*
        //    A2UIAndroidNativeMethodHandler.install() still reaches the KMP
        //    registry. This mirrors the replay loop in
        //    A2UISurfaceView.registerNativeBridge.
        val snapshot: List<Pair<String, String>> = A2UIAndroidCustomFunctionRegistry.snapshot()
        for ((name, spec) in snapshot) {
            forwardRegister(name, spec)
        }
        // 2) Forward future changes.
        A2UIAndroidCustomFunctionRegistry.addChangeListener(customFunctionRegistryListener)
    }

    @Volatile
    private var customFunctionListenerInstalled: Boolean = false

    private val customFunctionRegistryListener =
        object : A2UIAndroidCustomFunctionRegistry.ChangeListener {
            override fun onRegister(name: String, spec: String) {
                forwardRegister(name, spec)
            }

            override fun onUnregister(name: String) {
                forwardUnregister(name)
            }
        }

    /**
     * Forward a custom-function registration event to the KMP-side
     * [A2UIBridgeManager]. The KMP handler ignores arg0 (registration is
     * process-global), so we pass a placeholder instanceId. The forwarding is
     * scheduled onto the Kuikly Context Thread — same discipline used by
     * [sendPickerResult] — to satisfy the "Kuikly Context Thread only" rule
     * for KMP-side mutations.
     */
    private fun forwardRegister(name: String, spec: String) {
        KuiklyRenderCoreContextScheduler.scheduleTask {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.REGISTER_CUSTOM_FUNCTION,
                PROCESS_GLOBAL_INSTANCE_ID,
                name,
                spec
            )
        }
    }

    private fun forwardUnregister(name: String) {
        KuiklyRenderCoreContextScheduler.scheduleTask {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.UNREGISTER_CUSTOM_FUNCTION,
                PROCESS_GLOBAL_INSTANCE_ID,
                name,
                null
            )
        }
    }

    // region Custom-view registry bridge

    @Volatile
    private var customViewListenerInstalled: Boolean = false

    private val customViewRegistryListener =
        object : A2UIAndroidCustomViewRegistry.ChangeListener {
            override fun onRegister(
                viewName: String,
                entry: A2UIAndroidCustomViewRegistry.Entry
            ) {
                forwardViewRegister(viewName)
            }

            override fun onUnregister(viewName: String) {
                forwardViewUnregister(viewName)
            }
        }

    /**
     * Install [customViewRegistryListener] (idempotent) and replay every
     * view that has already been registered on [A2UIAndroidCustomViewRegistry]
     * into the KMP-side [com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry].
     *
     * Callers are expected to hold the same [installed] guard used by [install].
     */
    private fun installCustomViewRegistryBridge() {
        if (customViewListenerInstalled) return
        customViewListenerInstalled = true
        // 1) Replay existing entries so a view registered *before*
        //    A2UIAndroidNativeMethodHandler.install() still reaches KMP.
        for ((viewName, _) in A2UIAndroidCustomViewRegistry.snapshot()) {
            forwardViewRegister(viewName)
        }
        // 2) Forward future changes.
        A2UIAndroidCustomViewRegistry.addChangeListener(customViewRegistryListener)
    }

    /**
     * Forward a custom-view registration event to the KMP-side
     * [com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager]. The KMP handler
     * ignores arg0 (registration is process-global), so we pass a placeholder
     * instanceId. Scheduled onto the Kuikly Context Thread to satisfy the
     * "KMP-side mutations only from the Kuikly Context Thread" rule.
     */
    private fun forwardViewRegister(viewName: String) {
        KuiklyRenderCoreContextScheduler.scheduleTask {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.REGISTER_CUSTOM_VIEW,
                PROCESS_GLOBAL_INSTANCE_ID,
                viewName,
                null
            )
        }
    }

    private fun forwardViewUnregister(viewName: String) {
        KuiklyRenderCoreContextScheduler.scheduleTask {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.UNREGISTER_CUSTOM_VIEW,
                PROCESS_GLOBAL_INSTANCE_ID,
                viewName,
                null
            )
        }
    }

    // endregion

    /**
     * Reverse of [install]. Typically only useful in tests.
     */
    @JvmStatic
    fun uninstall() {
        if (!installed) return
        installed = false
        if (customFunctionListenerInstalled) {
            customFunctionListenerInstalled = false
            A2UIAndroidCustomFunctionRegistry.removeChangeListener(customFunctionRegistryListener)
        }
        if (customViewListenerInstalled) {
            customViewListenerInstalled = false
            A2UIAndroidCustomViewRegistry.removeChangeListener(customViewRegistryListener)
        }
        topActivityRef = null
        appContextRef = null
    }

    // region Shared utilities — reused by both A2UISurfaceView (native-page mode) and
    //                          the A2UIBridgeModule module endpoint above
    //                          (Kuikly-page-embedded mode).

    /**
     * Resolve a usable Context. Callers supply their own if they have one
     * (e.g. [A2UISurfaceView] passes its view context so dialogs get the
     * correct theme); embedded-mode dispatchers pass `null` to fall back to
     * the most recently resumed Activity.
     *
     * The returned Context is preferred to be an [Activity] because
     * `AlertDialog.Builder` and `DatePickerDialog` behave better with one; if
     * only a bare Context is available (application), the caller must add
     * `Intent.FLAG_ACTIVITY_NEW_TASK` where relevant — this is handled inside
     * [executeFunctionCall].
     */
    private fun resolveContext(callerContext: Context?): Context? {
        callerContext?.let { unwrapActivity(it) }?.let { return it }
        topActivityRef?.get()?.let { return it }
        return callerContext ?: appContextRef?.get()
    }

    private fun unwrapActivity(ctx: Context): Activity? {
        var c: Context? = ctx
        while (c is ContextWrapper) {
            if (c is Activity) return c
            c = c.baseContext
        }
        return null
    }

    /**
     * Pop a date and/or time picker for the A2UI `DateTimeInput` component.
     *
     * Executes on the caller thread; internally posts UI operations to the
     * Android main thread when needed.
     *
     * @param callerContext Preferred Context (e.g. hosting View's context). Pass
     *   `null` to fall back to the last-resumed Activity tracked by [install].
     * @param instanceId    A2UI surface instance id. Used to route the picker
     *                      result back to the correct `A2UIDSLBridge` via
     *                      [A2UIBridgeManager].
     * @param tag           A2UI tag of the requesting DateTimeInput component.
     * @param configJson    JSON: {"enableDate":bool,"enableTime":bool,"value":"ISO8601","label":"..."}
     */
    @JvmStatic
    fun showDateTimePickerDialog(
        callerContext: Context?,
        instanceId: String,
        tag: Int,
        configJson: String
    ) {
        mainHandler.post {
            val ctx = resolveContext(callerContext) ?: return@post
            try {
                val config = org.json.JSONObject(configJson)
                val enableDate = config.optBoolean("enableDate", true)
                val enableTime = config.optBoolean("enableTime", false)
                val value = config.optString("value", "")
                val label = config.optString("label", "")

                var year = 2025; var month = 1; var day = 1
                var hour = 0; var minute = 0
                if (value.isNotEmpty()) {
                    try {
                        val datePart = value.substringBefore("T")
                        val parts = datePart.split("-")
                        if (parts.size >= 3) {
                            year = parts[0].toIntOrNull() ?: 2025
                            month = parts[1].toIntOrNull() ?: 1
                            day = parts[2].toIntOrNull() ?: 1
                        }
                        if (value.contains("T")) {
                            val timePart = value.substringAfter("T").substringBefore("Z").substringBefore("+")
                            val timeParts = timePart.split(":")
                            if (timeParts.size >= 2) {
                                hour = timeParts[0].toIntOrNull() ?: 0
                                minute = timeParts[1].toIntOrNull() ?: 0
                            }
                        }
                    } catch (_: Exception) {}
                }

                when {
                    enableDate && enableTime -> showDatePicker(ctx, year, month, day, label) { y, m, d ->
                        showTimePicker(ctx, hour, minute, label) { h, mm ->
                            sendPickerResult(instanceId, tag, formatISO8601(y, m, d, h, mm))
                        }
                    }
                    enableDate -> showDatePicker(ctx, year, month, day, label) { y, m, d ->
                        sendPickerResult(instanceId, tag, formatISO8601(y, m, d, hour, minute))
                    }
                    enableTime -> showTimePicker(ctx, hour, minute, label) { h, mm ->
                        sendPickerResult(instanceId, tag, formatISO8601(year, month, day, h, mm))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "showDateTimePickerDialog failed: ${e.message}", e)
            }
        }
    }

    private fun showDatePicker(
        ctx: Context, year: Int, month: Int, day: Int, label: String,
        onResult: (Int, Int, Int) -> Unit
    ) {
        val dialog = DatePickerDialog(
            ctx,
            // DatePickerDialog months are 0-based; convert back to 1-based.
            { _, y, m, d -> onResult(y, m + 1, d) },
            year, month - 1, day
        )
        if (label.isNotEmpty()) dialog.setTitle(label)
        dialog.show()
    }

    private fun showTimePicker(
        ctx: Context, hour: Int, minute: Int, label: String,
        onResult: (Int, Int) -> Unit
    ) {
        val dialog = TimePickerDialog(
            ctx,
            { _, h, mm -> onResult(h, mm) },
            hour, minute, true
        )
        if (label.isNotEmpty()) dialog.setTitle(label)
        dialog.show()
    }

    private fun formatISO8601(year: Int, month: Int, day: Int, hour: Int, minute: Int): String {
        val date = "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        val time = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:00Z"
        return "${date}T${time}"
    }

    /**
     * Push a date/time picker result back into the [A2UIDSLBridge] identified
     * by [instanceId]. Works for both hosting modes because
     * [A2UIBridgeManager.callKotlinMethod] resolves the correct bridge via its
     * internal `dslBridgeMap` — which is populated whether the surface is
     * hosted by [A2UISurfaceView] (native page) or an `A2UISurfaceDSLView`
     * (embedded in a Kuikly page).
     */
    @JvmStatic
    fun sendPickerResult(instanceId: String, tag: Int, isoString: String) {
        KuiklyRenderCoreContextScheduler.scheduleTask {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.PICKER_RESULT,
                instanceId,
                "$tag|$isoString",
                null
            )
        }
    }

    /**
     * Execute a client-side function call from the A2UI action protocol.
     * Supports the standard `openUrl` intent and falls back to a Toast for
     * anything else (matches the previous inline implementation in
     * `A2UISurfaceView`).
     *
     * `showAlert` was removed from the built-in set: per A2UI v0.9 spec
     * only `openUrl` is a standard `action.functionCall` intent, so hosts
     * that want an alert must emit an `action.event` (e.g. `event.name =
     * "toast"`) and pop the dialog themselves in their `onAction` handler.
     *
     * @param callerContext Preferred Context. Pass `null` for embedded mode.
     * @param functionName  Function name (e.g. "openUrl").
     * @param argsJson      JSON string with function arguments.
     */
    @JvmStatic
    fun executeFunctionCall(callerContext: Context?, functionName: String, argsJson: String) {
        mainHandler.post {
            val ctx = resolveContext(callerContext) ?: return@post
            val activity = unwrapActivity(ctx)
            try {
                val args = org.json.JSONObject(argsJson)
                when (functionName) {
                    "openUrl" -> {
                        val url = args.optString("url", "")
                        if (url.isNotEmpty()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                if (activity == null) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e(TAG, "openUrl failed: $url, ${e.message}")
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(ctx, "FunctionCall: $functionName($argsJson)", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "executeFunctionCall '$functionName' threw: ${e.message}", e)
            }
        }
    }

    // endregion
}
