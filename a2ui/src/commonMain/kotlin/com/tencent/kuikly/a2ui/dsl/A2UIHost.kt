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

import com.tencent.kuikly.core.base.ViewContainer

/**
 * A2UIHost - The abstraction that [A2UIApplier] depends on to attach A2UI-generated
 * DSL views into a Kuikly Pager environment.
 *
 * There are two implementations:
 * 1. [A2UIPager] — the legacy "self-contained Pager" host used by the native
 *    A2UISurfaceView entry point (each surface has its own Pager).
 * 2. `A2UISurfaceDSLView` — the "embedded" host used when A2UI is nested inside a
 *    user-written Kuikly DSL / Compose page (the surface *is* a DSL ViewContainer
 *    living directly in the host Pager's component tree; there is no inner Pager).
 *
 * By depending only on this interface, [A2UIApplier] no longer needs a full Pager
 * instance and works transparently in either mode.
 *
 * Threading: all methods must be invoked on the Kuikly Context Thread.
 */
interface A2UIHost {

    /**
     * The Kuikly Pager id that owns this host's component tree.
     *
     * For [A2UIPager] this is its own `pagerId`. For an embedded DSL surface, this
     * is the *host* Kuikly Pager id (the page the user wrote in DSL/Compose), so
     * that [com.tencent.kuikly.core.manager.BridgeManager.currentPageId] resolves
     * to the correct ReactiveObserver / NativeBridge for the surrounding page.
     */
    val hostPagerId: String

    /**
     * The root ViewContainer under which A2UI-generated components (tag=0) are
     * inserted. For [A2UIPager] this is the pager itself; for a DSL surface it
     * is the surface view (a `ViewContainer` subclass) sitting in the host page.
     */
    val rootContainer: ViewContainer<*, *>

    /**
     * The A2UI tag ↔ Kuikly DSL view registry.
     */
    val registry: A2UIViewRegistry

    /**
     * Current width constraint in dp for the surface. Used by:
     * - Slider width bootstrapping (see [A2UIApplier.createComponent]).
     * - Modal full-screen sizing fallback.
     *
     * For [A2UIPager] this is the pager's `surfaceWidth` (set via setSurfaceWidth).
     * For a DSL surface this is the surface view's laid-out width (or the host
     * page's rootViewWidth before the first layout pass).
     */
    val surfaceWidth: Float

    /**
     * Best-effort size (in dp) for a full-screen Modal window rendered by A2UI.
     * The Kuikly `ModalView(inWindow = true)` renders a native OS-window-level
     * container; this method returns the (width, height) that should be applied
     * to the Kuikly flex frame so backdrop / children lay out against the full
     * screen area.
     */
    fun fullScreenModalSize(): Pair<Float, Float>

    /**
     * Enqueue [task] to be run *after* the next Kuikly Pager layout pass on this
     * host's owning Pager completes. Used by [A2UIApplier] for post-layout fixes
     * (Text width in Row, vertical divider height, Slider actual width, etc.).
     */
    fun addTaskWhenLayoutFinish(task: () -> Unit)

    /**
     * Trigger a re-layout of this host's owning Pager and, if the host tracks
     * content height (native-embedded mode), report the new content height back
     * to the platform layer.
     *
     * For a DSL-embedded surface, this is typically a no-op because the host
     * Kuikly Core Flex engine re-lays out automatically when child attrs change.
     */
    fun performLayoutAndReportHeight()
}
