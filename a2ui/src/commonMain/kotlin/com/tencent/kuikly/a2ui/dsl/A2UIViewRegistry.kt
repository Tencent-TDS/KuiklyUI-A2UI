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

/**
 * A2UIViewRegistry - Manages the A2UI protocol-tag ↔ Kuikly DSL view mapping.
 *
 * Extracted from A2UIPager so that both the legacy native-embedded rendering path
 * (via [A2UIPager]) and the new DSL-embedded rendering path (via A2UISurfaceDSLView)
 * can share the same lookup semantics without depending on a full Kuikly Pager instance.
 *
 * Threading: all operations must run on the Kuikly Context Thread.
 */
class A2UIViewRegistry {

    /** Forward mapping: A2UI protocol tag → Kuikly DSL view. */
    private val tagToView = mutableMapOf<Int, DeclarativeBaseView<*, *>>()

    /** Reverse mapping: Kuikly Core nativeRef (render view tag) → A2UI protocol tag. */
    private val nativeRefToProtocolTag = mutableMapOf<Int, Int>()

    /**
     * Register a view with its A2UI tag for lookup.
     * Also builds the reverse mapping from nativeRef to protocol tag so that
     * native click events (which report nativeRef) can be routed to the correct
     * protocol tag for action dispatch.
     */
    fun register(tag: Int, view: DeclarativeBaseView<*, *>) {
        tagToView[tag] = view
        // nativeRef is the render view tag used by the native layer for click events.
        nativeRefToProtocolTag[view.nativeRef] = tag
    }

    /**
     * Unregister a view by tag. Removes both mappings.
     */
    fun unregister(tag: Int) {
        val view = tagToView.remove(tag)
        if (view != null) {
            nativeRefToProtocolTag.remove(view.nativeRef)
        }
    }

    /**
     * Get a registered view by its A2UI tag.
     */
    fun getView(tag: Int): DeclarativeBaseView<*, *>? = tagToView[tag]

    /**
     * Get a registered container view by its A2UI tag.
     * Returns null if the view is not a ViewContainer.
     */
    fun getContainer(tag: Int): ViewContainer<*, *>? = tagToView[tag] as? ViewContainer<*, *>

    /**
     * Get the A2UI protocol tag for a given view instance.
     * Used for two-way binding registration (event handler needs the tag).
     */
    fun getTag(view: DeclarativeBaseView<*, *>): Int? {
        return tagToView.entries.firstOrNull { it.value === view }?.key
    }

    /**
     * Look up the A2UI protocol tag from a Kuikly Core nativeRef (render view tag).
     * Used when the native layer reports a click event by nativeRef and we need
     * the protocol tag to look up the associated action.
     */
    fun getTagByNativeRef(nativeRef: Int): Int? = nativeRefToProtocolTag[nativeRef]

    /**
     * Clear all mappings. Called during host destroy.
     */
    fun clear() {
        tagToView.clear()
        nativeRefToProtocolTag.clear()
    }
}
