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

/**
 * Global registry that tracks which viewNames the host app has declared as
 * A2UI custom render views. The actual UIView/View factory closure lives on
 * the native (platform) side; this Kotlin side only needs to know *whether*
 * a given viewName is a registered custom component so the DSL applier can
 * decide between:
 *
 *   - route to [com.tencent.kuikly.a2ui.views.A2UICustomComponentView]
 *     (viewName is a registered custom component)
 *   - fall back to a plain [com.tencent.kuikly.core.views.DivView]
 *     (viewName is not registered — treat as unknown / placeholder to avoid
 *     hitting the native `KRClassFromString` / `createRenderShadow` assert
 *     paths that would crash)
 *
 * Population happens automatically from the native layer: when the host
 * calls `A2UISurfaceView.registerCustomRenderView(viewName, creator)`, the
 * platform bridge forwards the viewName here via
 * [com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod.REGISTER_CUSTOM_VIEW].
 *
 * The registry is process-global to mirror the platform-side global creator
 * table, so a single surface view can render any registered custom component
 * regardless of which surface's init triggered the registration.
 */
object A2UICustomViewRegistry {

    /**
     * Set of currently-registered custom viewNames. Uses a plain [MutableSet]
     * without extra locking; the surface-view register/unregister calls are
     * serialised through the Kuikly Context Thread when they hit
     * [com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager.callKotlinMethod].
     */
    private val viewNames: MutableSet<String> = mutableSetOf()

    /**
     * Register a custom viewName. No-op if [viewName] is blank.
     */
    fun register(viewName: String) {
        if (viewName.isEmpty()) return
        viewNames.add(viewName)
    }

    /**
     * Remove a previously registered viewName. No-op if it was never registered.
     */
    fun unregister(viewName: String) {
        if (viewName.isEmpty()) return
        viewNames.remove(viewName)
    }

    /**
     * Test whether [viewName] has been registered as a custom render view by
     * the host app.
     */
    fun isRegistered(viewName: String): Boolean {
        if (viewName.isEmpty()) return false
        return viewName in viewNames
    }

    /**
     * Snapshot of all currently registered viewNames. Mainly for diagnostics.
     */
    fun registeredNames(): Set<String> = viewNames.toSet()

    /**
     * Clear the registry. Primarily used by tests.
     */
    fun clearAll() {
        viewNames.clear()
    }
}
