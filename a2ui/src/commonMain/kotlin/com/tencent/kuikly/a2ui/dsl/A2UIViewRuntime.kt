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

import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.a2ui.views.DateTimeInputView

@Suppress("DEPRECATION")
internal class A2UIViewRuntime(private val host: A2UIHost, private val layout: A2UILayoutEffects) {
    private val rootTag = 0

    init { host.registry.register(rootTag, host.rootContainer) }

    internal data class Mount(val view: DeclarativeBaseView<*, *>, val parent: ViewContainer<*, *>, val index: Int)

    fun prepareMount(tag: Int, viewName: String, parentTag: Int, index: Int, hasProps: Boolean, applyInitial: (DeclarativeBaseView<*, *>) -> Unit): Mount {
        BridgeManager.currentPageId = host.hostPagerId
        val view = A2UIViewFactory.create(viewName)
        host.registry.register(tag, view)
        val parent = host.registry.getContainer(if (parentTag < 0) rootTag else parentTag) ?: host.rootContainer
        val childIndex = if (index >= 0) index else -1
        if (hasProps && (view is CheckBoxView || view is DateTimeInputView)) {
            parent.addChild(view, { applyInitial(view) }, childIndex)
        } else {
            parent.addChild(view, {}, childIndex)
            if (hasProps) applyInitial(view)
        }
        return Mount(view, parent, index)
    }

    fun commitNativeMount(mount: Mount, props: Map<String, Any?>?) {
        val (view, parent, index) = mount
        val domContainer = parent.realContainerView()
        val domIndex = if (index >= 0) index else domContainer.getSubviews().size - 1
        domContainer.insertDomSubView(view, domIndex.coerceAtLeast(0))
        if (props?.get("__verticalDividerFollowParent") == true) {
            layout.addTaskWhenLayoutFinish {
                var maxSiblingHeight = 0f
                for (sibling in parent.getSubviews()) {
                    if (sibling === view) continue
                    val height = sibling.flexNode.layoutFrame.height
                    if (!height.isNaN() && height > maxSiblingHeight) maxSiblingHeight = height
                }
                if (maxSiblingHeight > 0f) {
                    val current = view.flexNode.layoutFrame.height
                    if (current.isNaN() || kotlin.math.abs(current - maxSiblingHeight) > 0.5f) {
                        view.attr { height(maxSiblingHeight) }
                    }
                }
            }
        }
    }

    fun viewForUpdate(tag: Int): DeclarativeBaseView<*, *>? {
        BridgeManager.currentPageId = host.hostPagerId
        return host.registry.getView(tag)
    }

    fun remove(tag: Int, parentTag: Int): Boolean {
        val view = host.registry.getView(tag) ?: return false
        val parent = host.registry.getContainer(if (parentTag < 0) rootTag else parentTag)
        if (parent != null) {
            val container = parent.realContainerView()
            container.removeDomSubView(view)
            container.removeChild(view)
        }
        host.registry.unregister(tag)
        return true
    }

    fun performLayout() {
        BridgeManager.currentPageId = host.hostPagerId
        layout.performLayout()
    }
}
