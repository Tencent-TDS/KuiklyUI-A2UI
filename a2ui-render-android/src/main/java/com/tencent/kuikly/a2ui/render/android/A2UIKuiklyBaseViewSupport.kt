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

import com.tencent.kuikly.core.render.android.IKuiklyRenderExport
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegatorDelegate

/**
 * Utilities for enabling A2UI in a Kuikly page host (e.g. `KuiklyBaseView` or a
 * custom subclass of `KuiklyRenderViewDelegator`).
 *
 * ### Why this exists
 * A2UI needs two things injected onto the host Kuikly page's
 * [IKuiklyRenderExport]:
 *
 *  1. **Custom render views** registered via `registerA2UICustomRenderView`,
 *     so an `A2UISurfaceDSLView` that receives
 *     `createRenderView("KRMyCustomView", …)` finds its factory.
 *  2. **The A2UIBridgeModule**, so the KMP core can dispatch A2UI-specific
 *     requests (showDateTimePicker / executeFunctionCall /
 *     invokeCustomFunction / …) through the standard Kuikly module bus in
 *     Kuikly-page-embedded mode.
 *
 * The core framework calls back into the app-supplied
 * [KuiklyRenderViewBaseDelegatorDelegate.registerExternalRenderView] once per
 * page initialisation with that export — this is the natural hook point, but
 * hosting apps typically own their own delegate and won't route calls to A2UI
 * on their own.
 *
 * [wrap] gives the app a one-liner "wrap my delegate before handing it to
 * `KuiklyBaseView`" workflow that:
 *
 * 1. Delegates every other callback to the original delegate untouched.
 * 2. Additionally attaches the export to
 *    [A2UIAndroidCustomViewRegistry] on `registerExternalRenderView`, which
 *    both:
 *      - installs every already-registered A2UI custom view factory onto it, and
 *      - keeps a weak reference so subsequent register/unregister calls fan
 *        out to this page too.
 * 3. Registers [A2UIBridgeModule] on the host page so the KMP core can
 *    reach back into Android for A2UI-specific requests.
 *
 * ### Usage
 * ```kotlin
 * class MyKuiklyActivity : Activity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         val myDelegate = MyKuiklyDelegate() // your own delegate
 *         val kuiklyView = KuiklyBaseView(
 *             this,
 *             A2UIKuiklyBaseViewSupport.wrap(myDelegate)
 *         )
 *         kuiklyView.onAttach(contextCode, pageName, pageData)
 *         setContentView(kuiklyView)
 *     }
 * }
 * ```
 *
 * Apps that do not use A2UI custom render views can skip [wrap] entirely.
 * Apps that do not host A2UI inside a Kuikly page at all (native-page mode
 * only) can also skip [wrap] — the standalone `A2UISurfaceView` installs A2UI
 * custom views onto its own private [IKuiklyRenderExport] directly.
 */
object A2UIKuiklyBaseViewSupport {

    /**
     * Wrap [delegate] so the resulting delegate additionally installs every
     * A2UI-registered custom render view onto the host page's
     * [IKuiklyRenderExport].
     *
     * Passing `null` returns a delegate that does nothing except the A2UI
     * hooking (useful for hosting apps that don't need their own delegate).
     * When a non-null [delegate] is passed, every callback except
     * `registerExternalRenderView` is forwarded unchanged via Kotlin's
     * `by` delegation, so any current or future methods added to the
     * [KuiklyRenderViewBaseDelegatorDelegate] interface are automatically
     * kept in sync.
     *
     * Safe to call from any thread. The returned delegate can be reused
     * across multiple `KuiklyBaseView` instances — each `registerExternalRenderView`
     * callback receives a fresh export, and the registry tracks them
     * independently via a WeakHashMap.
     */
    @JvmStatic
    @JvmOverloads
    fun wrap(delegate: KuiklyRenderViewBaseDelegatorDelegate? = null): KuiklyRenderViewBaseDelegatorDelegate {
        // Use an inner "identity" delegate when the caller has none; it inherits
        // every default from the interface, which is exactly what a
        // "no-op" delegate would do.
        val inner: KuiklyRenderViewBaseDelegatorDelegate =
            delegate ?: object : KuiklyRenderViewBaseDelegatorDelegate {}
        return A2UIDelegateWrapper(inner)
    }
}

/**
 * Wrapper that forwards every [KuiklyRenderViewBaseDelegatorDelegate] method
 * to [inner] via Kotlin's `by` delegation and additionally hooks
 * `registerExternalRenderView` to install A2UI custom views.
 *
 * Kept out of the public [A2UIKuiklyBaseViewSupport] API surface so hosting
 * apps see only [A2UIKuiklyBaseViewSupport.wrap].
 */
private class A2UIDelegateWrapper(
    private val inner: KuiklyRenderViewBaseDelegatorDelegate
) : KuiklyRenderViewBaseDelegatorDelegate by inner {

    override fun registerExternalRenderView(kuiklyRenderExport: IKuiklyRenderExport) {
        // 1) Let the app-supplied delegate install its own custom views and
        //    modules first. If the app's delegate happens to register a view
        //    or module with the same name as an A2UI one, the A2UI put below
        //    wins for the current page — we treat that as intentional (the
        //    A2UI-side registration is process-global and more recent).
        inner.registerExternalRenderView(kuiklyRenderExport)
        // 2) Attach so:
        //    - every currently-registered A2UI custom view is installed now,
        //    - any future `A2UIAndroidCustomViewRegistry.register(…)` also
        //      fans out to this export.
        A2UIAndroidCustomViewRegistry.attachExport(kuiklyRenderExport)
        // 3) Install the A2UI module endpoint on the host Kuikly page so the
        //    KMP core can route A2UI-specific requests (showDateTimePicker /
        //    executeFunctionCall / invokeCustomFunction / …) through the
        //    standard `BridgeManager.callModuleMethod` channel in Kuikly-
        //    page-embedded mode. See [A2UIBridgeModule] for the wire
        //    contract. A fresh instance is created per page via the
        //    creator lambda — module instances are owned by the host
        //    render view.
        kuiklyRenderExport.moduleExport(A2UIBridgeModule.MODULE_NAME) { A2UIBridgeModule() }
    }
}
