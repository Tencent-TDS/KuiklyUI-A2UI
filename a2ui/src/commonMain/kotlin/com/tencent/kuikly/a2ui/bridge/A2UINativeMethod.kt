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

/**
 * Method IDs for Kotlin → Native communication (render commands).
 * These are sent via NativeBridge.toNative(methodId, ...) to the native render layer.
 *
 * Aligned with Kuikly DSL's NativeMethod pattern but specific to A2UI.
 */
object A2UINativeMethod {
    /** Create a native render view. args: instanceId, tag, viewName */
    const val CREATE_VIEW = 1
    /** Remove a native render view. args: instanceId, tag */
    const val REMOVE_VIEW = 2
    /** Insert a child view into parent. args: instanceId, parentTag, childTag, index */
    const val INSERT_SUB_VIEW = 3
    /** Set a property on a view. args: instanceId, tag, key, value */
    const val SET_PROP = 4
    /** Set the frame of a view. args: instanceId, tag, frameString("x,y,w,h") */
    const val SET_FRAME = 5
    /** Calculate render view size. args: instanceId, tag, constraintWidth, constraintHeight */
    const val CALCULATE_RENDER_VIEW_SIZE = 6
    /** Call a method on a view. args: instanceId, tag, method, params */
    const val CALL_VIEW_METHOD = 7
    /** Call a module method. args: instanceId, moduleName, method, params */
    const val CALL_MODULE_METHOD = 8
    /** Create a shadow for text measurement. args: instanceId, tag, viewName */
    const val CREATE_SHADOW = 9
    /** Remove a shadow. args: instanceId, tag */
    const val REMOVE_SHADOW = 10
    /** Set a shadow property. args: instanceId, tag, key, value */
    const val SET_SHADOW_PROP = 11
    /** Associate shadow with view. args: instanceId, tag */
    const val SET_SHADOW_FOR_VIEW = 12
    /** Set timeout. args: instanceId, delay */
    const val SET_TIMEOUT = 13
    /** Call shadow method. args: instanceId, tag, method, params */
    const val CALL_SHADOW_METHOD = 14
    /** Fire fatal exception. args: instanceId, message */
    const val FIRE_FATAL_EXCEPTION = 15
    /** Sync flush UI tasks. args: instanceId */
    const val SYNC_FLUSH_UI = 16
    /** Call TDF module method. args: instanceId, moduleName, method, params */
    const val CALL_TDF_MODULE_METHOD = 17

    // A2UI-specific methods (start from 100 to avoid conflicts with Core NativeMethod)
    /** Measure shadow size synchronously. args: instanceId, tag, constraintWidth, constraintHeight. Returns "width,height" */
    const val MEASURE_SHADOW_SIZE = 100
    /** Action callback from Kotlin to Native. args: instanceId, tag, actionJson */
    const val ACTION_CALLBACK = 101
    /** Content height changed (for self-sizing containers). args: instanceId, height */
    const val CONTENT_HEIGHT_CHANGED = 102
    /** Show a native date/time picker dialog. args: instanceId, tag, configJson (enableDate, enableTime, value, label) */
    const val SHOW_DATE_TIME_PICKER = 103
    /** Notify host that data model has changed (two-way binding). args: instanceId, dataModelJson, changedKey */
    const val DATA_MODEL_CHANGED = 104
    /** Ask the host to open a URL (Markdown link tap). args: instanceId, url */
    const val OPEN_URL = 105
    /** Execute a client-side functionCall action. args: instanceId, functionName, argsJson */
    const val EXECUTE_FUNCTION_CALL = 106
    /**
     * Invoke a previously-registered custom A2UI function on the native (platform) side.
     *
     * Args: instanceId, name, argsJson, spec ("returnType|invokeMode", e.g. "string|sync", "void|async").
     *
     * Return semantics:
     *   - void        : null (fire-and-forget; ASYNC mode dispatches on native main thread)
     *   - string      : String value
     *   - boolean     : "true"/"false" (String) — coerced back to Boolean by the Kotlin layer
     *   - number      : String representation of a number (e.g. "3.14") — coerced back to Double
     *   - array       : JSON-encoded array String (e.g. "[1,2,3]") — parsed back into List
     *
     * Must return synchronously when spec's invokeMode is "sync". The Kotlin
     * expression evaluator will block on the return value.
     */
    const val INVOKE_CUSTOM_FUNCTION = 107

    // Convenience aliases
    /** Sync flush UI tasks (alias for readability). args: instanceId */
    const val FLUSH_UI_TASKS = SYNC_FLUSH_UI
    /** Destroy render layer (legacy, not sent by DSL path but handled by native layer). */
    const val DESTROY = 200
}

/**
 * Method IDs for Native → Kotlin communication (control commands).
 * These are received via A2UICoreEntry.callKotlinMethod(methodId, ...).
 */
object A2UIKotlinMethod {
    /** Initialize an A2UI instance. args: instanceId, configJson */
    const val INIT = 1
    /** Write a JSON stream chunk. args: instanceId, chunk */
    const val WRITE_STREAM = 2
    /** Finish the stream. args: instanceId */
    const val FINISH_STREAM = 3
    /** Destroy an A2UI instance. args: instanceId */
    const val DESTROY = 4
    /** Set surface size from platform container. args: instanceId, "width,height" */
    const val SET_SURFACE_SIZE = 5
    /** View click event from native. args: instanceId, tag (as String) */
    const val VIEW_CLICK = 6
    /** View event from native (e.g. pan, progressDidChanged). args: instanceId, tag (as String), "eventName|eventData" */
    const val VIEW_EVENT = 7
    /** Trigger layout recalculation. args: instanceId */
    const val LAYOUT_VIEW = 8
    /** Date/time picker result from native. args: instanceId, "tag|isoString" */
    const val PICKER_RESULT = 9
    /**
     * Register a custom A2UI function's metadata (implementation stays on native side).
     * Registration is GLOBAL: once registered from any surface view, the function is
     * visible to every A2UI instance in the process.
     *
     * args: instanceId (unused but kept for signature symmetry), name, spec ("returnType|invokeMode").
     */
    const val REGISTER_CUSTOM_FUNCTION = 10

    /**
     * Unregister a previously registered custom function.
     * args: instanceId (unused), name
     */
    const val UNREGISTER_CUSTOM_FUNCTION = 11

    /**
     * Register a custom A2UI render view's viewName.
     *
     * The actual UIView/View factory closure lives on the native (platform)
     * side; Kotlin only needs to know *whether* a viewName is a declared
     * custom component so that [com.tencent.kuikly.a2ui.dsl.A2UIApplier]
     * can decide between routing to [com.tencent.kuikly.a2ui.views.A2UICustomComponentView]
     * (registered) and falling back to a plain DivView (unregistered/unknown).
     *
     * Registration is GLOBAL: once registered from any surface view, the
     * viewName is visible to every A2UI instance in the process.
     *
     * args: instanceId (unused but kept for signature symmetry), viewName.
     */
    const val REGISTER_CUSTOM_VIEW = 12

    /**
     * Unregister a previously registered custom render view.
     * args: instanceId (unused), viewName.
     */
    const val UNREGISTER_CUSTOM_VIEW = 13

    /**
     * Register a business-supplied custom Icon image.
     *
     * The A2UI `Icon` component ships with a built-in unicode/emoji `ICON_MAP` that
     * renders each icon name via `KRRichTextView`. Some business scenarios need
     * to replace those glyphs with brand images. Once at least one icon has been
     * registered (via this method), EVERY A2UI Icon component — including those
     * whose `name` is data-bound at runtime — is routed to `KRImageView` and looks
     * up its image URI from
     * [com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry].
     *
     * Registration is GLOBAL: once registered from any surface view, the icon is
     * visible to every A2UI instance in the process. `arg0` (instanceId) is kept
     * for signature symmetry but is not used.
     *
     * args: instanceId (unused), name, uri.
     */
    const val REGISTER_ICON = 14

    /**
     * Unregister a previously registered custom icon.
     * args: instanceId (unused), name.
     */
    const val UNREGISTER_ICON = 15
}
