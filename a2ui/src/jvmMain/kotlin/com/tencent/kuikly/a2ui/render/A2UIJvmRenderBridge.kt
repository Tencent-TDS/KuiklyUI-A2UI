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

package com.tencent.kuikly.a2ui.render

/**
 * JVM platform render bridge implementation.
 * Used for testing and as a base for OHOS integration via JNI/NAPI bridge.
 *
 * This implementation uses callback-based delegation, allowing the platform
 * layer to inject its own rendering logic.
 */
class A2UIJvmRenderBridge {

    private var isDestroyed = false

    // Delegate callbacks
    var onCreateView: ((Int, String) -> Unit)? = null
    var onRemoveView: ((Int) -> Unit)? = null
    var onInsertSubView: ((Int, Int, Int) -> Unit)? = null
    var onSetProp: ((Int, String, Any) -> Unit)? = null
    var onSetFrame: ((Int, Float, Float, Float, Float) -> Unit)? = null
    var onCallViewMethod: ((Int, String, String?) -> Unit)? = null
    var onDestroy: (() -> Unit)? = null

    fun createView(tag: Int, viewName: String) {
        if (isDestroyed) return
        onCreateView?.invoke(tag, viewName)
    }

    fun removeView(tag: Int) {
        if (isDestroyed) return
        onRemoveView?.invoke(tag)
    }

    fun insertSubView(parentTag: Int, childTag: Int, index: Int) {
        if (isDestroyed) return
        onInsertSubView?.invoke(parentTag, childTag, index)
    }

    fun setProp(tag: Int, key: String, value: Any) {
        if (isDestroyed) return
        onSetProp?.invoke(tag, key, value)
    }

    fun setFrame(tag: Int, x: Float, y: Float, width: Float, height: Float) {
        if (isDestroyed) return
        onSetFrame?.invoke(tag, x, y, width, height)
    }

    fun callViewMethod(tag: Int, method: String, params: String?) {
        if (isDestroyed) return
        onCallViewMethod?.invoke(tag, method, params)
    }

    fun destroy() {
        if (isDestroyed) return
        isDestroyed = true
        onDestroy?.invoke()
        onCreateView = null
        onRemoveView = null
        onInsertSubView = null
        onSetProp = null
        onSetFrame = null
        onCallViewMethod = null
        onDestroy = null
    }
}
