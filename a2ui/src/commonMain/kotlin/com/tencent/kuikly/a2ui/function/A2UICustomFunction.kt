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

package com.tencent.kuikly.a2ui.function

/**
 * Return type of a custom A2UI function.
 *
 * Per A2UI v0.9 catalog semantics, functions used as expressions must be
 * synchronous and return one of: STRING, BOOLEAN, NUMBER, ARRAY. Functions
 * used as actions (functionCall inside `action`) return VOID.
 */
enum class A2UIFunctionReturnType {
    STRING,
    BOOLEAN,
    NUMBER,
    ARRAY,
    VOID;

    fun toWire(): String = name.lowercase()

    companion object {
        fun fromWire(s: String?): A2UIFunctionReturnType? = when (s?.lowercase()) {
            "string" -> STRING
            "boolean", "bool" -> BOOLEAN
            "number", "int", "float", "double" -> NUMBER
            "array", "list" -> ARRAY
            "void", "unit", "null", "none" -> VOID
            else -> null
        }
    }
}

/**
 * Invocation mode for a custom function.
 *
 * Only applies to VOID functions:
 *   - [SYNC]: Handler is executed on the calling thread (Kuikly Context Thread).
 *             Callers must return quickly to avoid stalling render/parse. See
 *             the A2UI custom function integration docs for details.
 *   - [ASYNC]: Handler is dispatched to the platform's main thread and fires
 *              without blocking the caller.
 *
 * For non-VOID return types (STRING/BOOLEAN/NUMBER/ARRAY), the mode is
 * silently coerced to [SYNC] because expression evaluation requires an
 * immediate value.
 */
enum class A2UIFunctionInvokeMode {
    SYNC,
    ASYNC;

    fun toWire(): String = name.lowercase()

    companion object {
        fun fromWire(s: String?): A2UIFunctionInvokeMode = when (s?.lowercase()) {
            "async" -> ASYNC
            else -> SYNC
        }
    }
}

/**
 * Metadata describing a custom A2UI function. The actual implementation
 * (the handler closure) lives on the native (platform) side; this Kotlin
 * side only knows the signature so it can (a) accept/route the call and
 * (b) coerce the returned value into the expected type.
 */
data class A2UICustomFunctionDescriptor(
    val name: String,
    val returnType: A2UIFunctionReturnType,
    val invokeMode: A2UIFunctionInvokeMode
) {
    /**
     * Encode the metadata into the wire format used across the Native ↔ Kotlin
     * boundary: `"returnType|invokeMode"`, e.g. `"string|sync"`, `"void|async"`.
     */
    fun toSpec(): String = "${returnType.toWire()}|${invokeMode.toWire()}"

    companion object {
        /**
         * Parse a wire-format spec back into a descriptor. Unknown/absent tokens
         * fall back to sensible defaults (`SYNC` for mode). Returns `null` if
         * the return type token is invalid.
         */
        fun parseSpec(name: String, spec: String): A2UICustomFunctionDescriptor? {
            val parts = spec.split("|", limit = 2)
            val rt = A2UIFunctionReturnType.fromWire(parts.getOrNull(0)) ?: return null
            var mode = A2UIFunctionInvokeMode.fromWire(parts.getOrNull(1))
            // Non-void functions must be synchronous (expression evaluation is
            // synchronous). We silently coerce here — no assertion required.
            if (rt != A2UIFunctionReturnType.VOID) {
                mode = A2UIFunctionInvokeMode.SYNC
            }
            return A2UICustomFunctionDescriptor(name, rt, mode)
        }
    }
}

/**
 * Global registry of custom A2UI function metadata.
 *
 * Global scope means: once a function is registered from any native surface
 * view, it is available to every A2UI instance in the process. This mirrors
 * the platform's expectation that a "product-level" function library is
 * shared across all A2UI cards the app renders.
 *
 * The registry only stores the [A2UICustomFunctionDescriptor] metadata.
 * The actual handler (which knows how to execute the function on the
 * platform side) is held by the native layer registry — see the platform
 * implementations of `A2UISurfaceView`. When a function is invoked, the
 * Kotlin layer looks up the descriptor here (to know the return type and
 * invoke mode) and then calls back into native via
 * [com.tencent.kuikly.a2ui.bridge.A2UINativeMethod.INVOKE_CUSTOM_FUNCTION].
 *
 * Names of the 14 built-in A2UI v0.9 functions are reserved and cannot be
 * overridden here; the framework's built-in evaluators take precedence for
 * those names.
 */
object A2UICustomFunctionRegistry {

    /**
     * Names of A2UI v0.9 built-in functions that are handled directly by the
     * framework (see [com.tencent.kuikly.a2ui.adapter.A2UIProtocolAdapter]).
     * These names are reserved: attempts to register/override them are ignored.
     */
    private val RESERVED_NAMES: Set<String> = setOf(
        // Check functions
        "required", "regex", "length", "numeric", "email",
        "and", "or", "not",
        // Format functions
        "formatString", "formatNumber", "formatCurrency", "formatDate",
        "pluralize",
        // Action functions
        "openUrl"
    )

    /**
     * Global function table. Keyed by function name.
     *
     * Uses a plain [MutableMap]; access is expected to happen on the Kuikly
     * Context Thread or the Android/iOS main thread when the surface view
     * calls register/unregister. Both patterns end up serialized through the
     * same platform's UI thread in practice, so no extra locking is added
     * here — keeping this KMP-friendly (no `synchronized`).
     */
    private val functions: MutableMap<String, A2UICustomFunctionDescriptor> = mutableMapOf()

    /**
     * Register (or replace) a custom function's metadata. Reserved names are
     * silently ignored so servers cannot shadow built-ins by accident.
     */
    fun register(descriptor: A2UICustomFunctionDescriptor) {
        if (descriptor.name in RESERVED_NAMES) {
            return
        }
        functions[descriptor.name] = descriptor
    }

    /**
     * Convenience overload: parse the wire format then register.
     * Returns `true` if the descriptor was accepted.
     */
    fun register(name: String, spec: String): Boolean {
        if (name in RESERVED_NAMES) {
            return false
        }
        val desc = A2UICustomFunctionDescriptor.parseSpec(name, spec)
        if (desc == null) {
            return false
        }
        functions[name] = desc
        return true
    }

    /**
     * Remove a previously registered function. No-op if the name is not
     * registered or is reserved.
     */
    fun unregister(name: String) {
        if (name in RESERVED_NAMES) return
        functions.remove(name)
    }

    /**
     * Look up a registered function by name. Returns `null` for unknown or
     * reserved names.
     */
    fun lookup(name: String): A2UICustomFunctionDescriptor? {
        if (name in RESERVED_NAMES) {
            return null
        }
        return functions[name]
    }

    /**
     * Test whether the given name refers to a reserved (built-in) A2UI function.
     * Framework code uses this to short-circuit custom-function fallback for
     * built-ins.
     */
    fun isReserved(name: String): Boolean = name in RESERVED_NAMES

    /**
     * Snapshot of all registered function names — mainly for diagnostics.
     */
    fun registeredNames(): Set<String> = functions.keys.toSet()

    /**
     * Clear all registered functions. Primarily used by tests.
     */
    fun clearAll() {
        functions.clear()
    }
}

/**
 * Process-wide hook that lets platform-specific code (currently only the
 * iOS render pod, via `A2UICustomFunctionInvokerBridge`) provide a
 * synchronous invoker for custom A2UI functions.
 *
 * Why this exists
 * ---------------
 * `A2UIDSLBridge.invokeCustomFunctionOnNative` normally routes the call
 * through `nativeBridge.toNative(INVOKE_CUSTOM_FUNCTION, …)`. That works
 * in Native-page mode (standalone `A2UISurfaceView` with its own
 * `A2UICoreEntry`) because the surface's own `perform(...)` receives
 * methodId 107. It does **not** work in Kuikly-page embedded mode, where
 * `nativeBridge` is the enclosing Kuikly page's bridge and no receiver is
 * installed for methodId 107 — the call silently returns `null` and the
 * DSL applier reports `invoker-returned-null`.
 *
 * How this fixes it
 * -----------------
 * The iOS render pod installs a delegate at app boot (idempotent). The
 * DSL bridge consults this hook *before* falling through to
 * `nativeBridge.toNative`. If the hook returns a non-null value it is
 * used verbatim; if the hook is null or returns null, we fall back to
 * the classic transport (preserving Native-page semantics untouched).
 *
 * Kept in `commonMain` (rather than `iosMain`) so that
 * `A2UIDSLBridge.invokeCustomFunctionOnNative` — itself commonMain code
 * — can reference it without needing an `expect/actual` split.
 *
 * Android and OHOS deliberately don't install this hook: they route the
 * same `(name, argsJson, spec)` payload through the Kuikly Module bus
 * (`A2UIBridgeModule.invokeCustomFunction`) in Kuikly-page-embedded mode,
 * and through `nativeBridge.toNative(107, …)` in Native-page mode — both
 * of which are already wired up by the respective render pods (see
 * `A2UIAndroidNativeMethodHandler.install` on Android and
 * `A2UIKuiklyRenderViewSupport` on OHOS). Installing the hook on those
 * platforms would only add a redundant path.
 */
object A2UICustomFunctionNativeInvoker {
    /**
     * Optional synchronous invoker. Signature matches
     * `A2UIDSLBridge.invokeCustomFunctionOnNative`:
     *   (name, argsJson, spec) → raw platform return value (String / Boolean
     *   / Number / JSON-encoded array String / null).
     */
    var hook: ((name: String, argsJson: String, spec: String) -> Any?)? = null
}
