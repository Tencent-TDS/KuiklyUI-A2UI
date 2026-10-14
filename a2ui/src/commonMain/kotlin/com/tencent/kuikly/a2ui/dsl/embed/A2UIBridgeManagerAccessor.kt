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

import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.dsl.A2UIDSLBridge

/**
 * Thin adapter so [A2UISurfaceDSLView] can (un)register its bridge with
 * [A2UIBridgeManager] without directly leaking the manager type across the
 * `dsl.embed` package.
 *
 * This exists so that platform-side callbacks routed by instance id (native
 * DateTimePicker result, custom-function invocation, custom-view registration,
 * etc.) can find the embedded surface's bridge exactly the same way they find
 * a legacy native-embedded surface's bridge.
 */
internal object A2UIBridgeManagerAccessor {

    /**
     * Publish [bridge] under [instanceId] so platform callbacks can route to it.
     * Called from [A2UISurfaceDSLView.didInit] after the bridge is initialized.
     */
    fun registerDslBridge(instanceId: String, bridge: A2UIDSLBridge) {
        A2UIBridgeManager.registerDslBridge(instanceId, bridge)
    }

    /**
     * Withdraw the previously-registered bridge. Called from
     * [A2UISurfaceDSLView.willRemoveFromParentView] and [A2UISurfaceDSLView.resetStream].
     */
    fun unregisterDslBridge(instanceId: String) {
        A2UIBridgeManager.unregisterDslBridge(instanceId)
    }

    /**
     * Intercept an A2UI `action.functionCall` and execute it locally via the
     * shared [A2UIBridgeManager] path (INVOKE_CUSTOM_FUNCTION or
     * EXECUTE_FUNCTION_CALL), so DSL-embedded surfaces get exactly the same
     * behaviour as the legacy native-embedded surfaces. Returns `true` iff the
     * action was handled here — the caller must NOT forward to the host
     * `.action { }` callback in that case.
     */
    fun tryHandleFunctionCall(instanceId: String, actionJson: String): Boolean {
        return A2UIBridgeManager.tryHandleFunctionCall(instanceId, actionJson)
    }
}
