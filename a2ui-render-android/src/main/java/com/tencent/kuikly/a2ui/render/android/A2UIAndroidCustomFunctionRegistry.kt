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

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Process-global registry for user-supplied A2UI custom function handlers on Android.
 *
 * This registry is shared by:
 *   - [A2UISurfaceView] — the standalone native-page host.
 *   - [A2UIAndroidNativeMethodHandler] — the handler installed on the Kuikly
 *     render pipeline when A2UI is embedded inside a Kuikly page via
 *     `A2UISurfaceDSLView`.
 *
 * Registration is process-global and idempotent; both hosts see the same
 * function table so an app only needs to register a custom function once.
 *
 * Threading contract for [Handler.invoke]:
 *  - When `invokeMode = "sync"` the handler runs on the Kuikly Context Thread.
 *    Keep the work brief; do NOT block on network or disk I/O.
 *  - When `invokeMode = "async"` (VOID only) the handler runs on the Android
 *    main thread and its return value is ignored.
 */
object A2UIAndroidCustomFunctionRegistry {

    /**
     * Return type token for a custom A2UI function's handler.
     * Must match the wire tokens accepted by `A2UIFunctionReturnType.fromWire`
     * in commonMain.
     */
    const val RETURN_TYPE_STRING = "string"
    const val RETURN_TYPE_BOOLEAN = "boolean"
    const val RETURN_TYPE_NUMBER = "number"
    const val RETURN_TYPE_ARRAY = "array"
    const val RETURN_TYPE_VOID = "void"

    const val INVOKE_MODE_SYNC = "sync"
    const val INVOKE_MODE_ASYNC = "async"

    /**
     * A user-supplied handler for a custom A2UI function.
     * See the class KDoc for threading semantics and accepted return types.
     */
    fun interface Handler {
        fun invoke(args: org.json.JSONObject): Any?
    }

    /** name → handler. */
    private val handlers: MutableMap<String, Handler> = ConcurrentHashMap()

    /** name → "returnType|invokeMode". */
    private val descriptors: MutableMap<String, String> = ConcurrentHashMap()

    /**
     * Listener notified when the process-global registry changes. Used by
     * [A2UISurfaceView] to forward the new function into every live surface's
     * Kotlin core so it can be resolved during rendering.
     */
    interface ChangeListener {
        fun onRegister(name: String, spec: String)
        fun onUnregister(name: String)
    }

    private val listeners: MutableSet<ChangeListener> =
        java.util.Collections.synchronizedSet(mutableSetOf())

    /** Snapshot of all currently-registered "name → spec" pairs. */
    fun snapshot(): List<Pair<String, String>> =
        descriptors.entries.map { it.key to it.value }

    fun addChangeListener(listener: ChangeListener) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: ChangeListener) {
        listeners.remove(listener)
    }

    /**
     * Register a custom A2UI function. See [A2UISurfaceView.registerCustomFunction]
     * for the full contract; this method is equivalent and shared across hosts.
     */
    fun register(name: String, returnType: String, invokeMode: String, handler: Handler) {
        if (name.isEmpty()) return
        val spec = "$returnType|$invokeMode"
        handlers[name] = handler
        descriptors[name] = spec
        val snapshot = synchronized(listeners) { listeners.toList() }
        for (l in snapshot) l.onRegister(name, spec)
    }

    fun unregister(name: String) {
        if (name.isEmpty()) return
        handlers.remove(name)
        descriptors.remove(name)
        val snapshot = synchronized(listeners) { listeners.toList() }
        for (l in snapshot) l.onUnregister(name)
    }

    /**
     * Look up and execute the handler registered for [name].
     *
     * On the SYNC path (or VOID|SYNC) this method runs the handler inline on
     * the current (Kuikly Context) thread and returns the coerced value.
     *
     * On the ASYNC path (VOID|ASYNC) this method posts to the Android main
     * thread and returns `null` immediately.
     *
     * @return coerced return value for SYNC; `null` for ASYNC / missing / VOID.
     */
    fun invokeLocally(name: String, argsJson: String, spec: String): Any? {
        val handler = handlers[name] ?: return null
        val args = try {
            org.json.JSONObject(argsJson)
        } catch (e: Exception) {
            org.json.JSONObject()
        }
        val parts = spec.split("|")
        val returnType = parts.getOrNull(0)?.lowercase() ?: RETURN_TYPE_STRING
        val invokeMode = parts.getOrNull(1)?.lowercase() ?: INVOKE_MODE_SYNC

        if (returnType == RETURN_TYPE_VOID && invokeMode == INVOKE_MODE_ASYNC) {
            mainHandler.post {
                try {
                    handler.invoke(args)
                } catch (e: Exception) {
                    Log.e(TAG, "custom function '$name' threw", e)
                }
            }
            return null
        }

        return try {
            when (val raw = handler.invoke(args)) {
                null -> null
                is Unit -> null
                is String -> raw
                is Boolean -> raw
                is Number -> raw
                is List<*> -> {
                    // Encode to JSON so the Kotlin coercion can treat it as an array literal.
                    org.json.JSONArray(raw).toString()
                }
                else -> raw.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "custom function '$name' threw", e)
            null
        }
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private const val TAG = "A2UICustomFn"
}
