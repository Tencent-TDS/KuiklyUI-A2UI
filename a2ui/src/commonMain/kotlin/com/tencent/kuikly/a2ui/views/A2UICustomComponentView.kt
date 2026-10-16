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

package com.tencent.kuikly.a2ui.views

import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.bridge.isCustomComponentShadowRequired
import com.tencent.kuikly.core.base.Shadow
import com.tencent.kuikly.core.layout.FlexNode
import com.tencent.kuikly.core.layout.MeasureFunction
import com.tencent.kuikly.core.layout.MeasureOutput
import com.tencent.kuikly.core.layout.isUndefined
import com.tencent.kuikly.core.views.DivView

/**
 * A2UICustomComponentView - A DivView subclass for custom components in A2UI.
 *
 * This class:
 * 1. Overrides [viewName] to return the actual custom component name (e.g., "A2UIWeb", "MyA2UIView"),
 *    ensuring the native render layer creates the correct registered custom view.
 * 2. Implements [MeasureFunction]. On platforms where
 *    [com.tencent.kuikly.a2ui.bridge.isCustomComponentShadowRequired] is `true`
 *    (Android, iOS) it creates a Kuikly [Shadow] that delegates measurement to
 *    the native `IKuiklyRenderShadowExport` / `+hrv_createShadow` implementation,
 *    so the Flex engine can discover the component's intrinsic size (e.g.
 *    height=240 for WebView, height=200 for MyA2UIView).
 *
 *    On platforms where the flag is `false` (currently OHOS) we instead ask the
 *    business side through the standard Kuikly Module channel
 *    (`A2UIBridgeModule.calculateCustomViewSize`) whether it has registered a
 *    `measurer` for this component. If so, its return value drives measurement
 *    exactly the way a native Shadow would on Android/iOS — this keeps the
 *    "intrinsic size is the component's own responsibility" authoring contract
 *    uniform across all three platforms, with the physical mechanism being the
 *    only difference. If no measurer is registered (or the Module call returns
 *    null/empty), we fall back to `width = parent cross-axis constraint`,
 *    `height = 0`; authors can still override via JSON `width` / `height` as a
 *    hard cap at any time.
 *
 *    On JVM / unknown targets this branch is a compile-time no-op (see
 *    [isModuleBasedPlatform] and [isA2UIBridgeModuleAvailable]).
 * 3. Supports a *second-pass* resize path:
 *    [handleNativeResize] can be invoked from the host-platform side (ArkTS
 *    for OHOS) after the business component determines its final size
 *    asynchronously — e.g. a WebView whose content height arrives after the
 *    page finishes loading. The call updates `flexNode.styleWidth` /
 *    `flexNode.styleHeight` ONLY when the new size genuinely differs from the
 *    current layout size, so no spurious second layout pass is triggered.
 * 4. Forces [isRenderView] to return true so the native view is always created.
 *
 * See `docs/A2UI_CUSTOM_COMPONENT_GUIDE.md` for the per-platform contract.
 *
 * @param customViewName The registered custom component name (e.g., "A2UIWeb", "MyA2UIView")
 */
class A2UICustomComponentView(private val customViewName: String) : DivView(), MeasureFunction {

    private var shadow: Shadow? = null

    /**
     * Snapshot of the most recently applied cross/main-axis dimensions. Used
     * by [handleNativeResize] to short-circuit when the business side reports
     * a size identical to the one already in the Flex tree, so we never kick
     * off a redundant layout pass. Reset to `-1f` after
     * [didRemoveFromParentView] so a view instance re-attached later cannot
     * accidentally compare against stale values.
     */
    private var lastAppliedWidth: Float = -1f
    private var lastAppliedHeight: Float = -1f

    override fun viewName(): String = customViewName

    override fun isRenderView(): Boolean = true

    override fun willInit() {
        super.willInit()
        // Only create the native Shadow on platforms that actually ship a
        // Shadow registry reachable from this view name. On OHOS the Shadow
        // registry lives in a C++ static map that ArkTS cannot populate
        // without a C++ ABI patch, so we deliberately leave `shadow` null and
        // let the Module-measurer path (see [measure]) take over. See
        // Platform.kt for the rationale.
        if (isCustomComponentShadowRequired) {
            shadow = Shadow(pagerId, nativeRef, customViewName)
        }
    }

    override fun createFlexNode() {
        super.createFlexNode()
        // Register this view as the measure function for its FlexNode.
        // This tells the Flex engine to call measure() during layout calculation.
        flexNode.measureFunction = this
    }

    override fun createRenderView() {
        super.createRenderView()
        // After layout, sync the shadow to the native render view. Skipped
        // on platforms without a Shadow (OHOS, JVM) — there is nothing to sync.
        if (shadow != null) {
            renderView?.setShadow()
        }
        // Register ourselves into the process-wide table so platform-side
        // code (ArkTS A2UIBridgeModule) can locate this view instance by
        // (pagerId, nativeRef) and push second-pass resize updates back into
        // the Flex tree. Keyed by nativeRef because that is the identifier
        // the native renderer uses for this view; `pagerId` is baked into
        // the lookup to disambiguate across concurrent A2UI pages.
        A2UICustomComponentViewRegistry.register(pagerId, nativeRef, this)
    }

    override fun didRemoveFromParentView() {
        super.didRemoveFromParentView()
        flexNode.measureFunction = null
        shadow?.removeFromParentComponent()
        shadow = null
        A2UICustomComponentViewRegistry.unregister(pagerId, nativeRef)
        lastAppliedWidth = -1f
        lastAppliedHeight = -1f
    }

    override fun measure(
        node: FlexNode,
        width: Float,
        height: Float,
        measureOutput: MeasureOutput
    ) {
        val cWidth = if (width.isUndefined()) 100000f else width
        val cHeight = if (height.isUndefined()) -1f else height

        // Primary path on Android / iOS: delegate to the native Shadow.
        val shadowSize = shadow?.calculateRenderViewSize(cWidth, cHeight)

        // Secondary path on OHOS (and any future module-based target):
        // ask the business side via the standard Kuikly Module channel.
        // Compile-time no-op on JVM / platforms without the module.
        val moduleSize: Pair<Float, Float>? = if (shadowSize == null) {
            A2UIBridgeManager.calculateCustomViewSize(
                pagerId,
                customViewName,
                nativeRef,
                cWidth,
                cHeight
            )
        } else null

        // Fallback intrinsic size for platforms without a Shadow and without a
        // business-side measurer:
        //
        // Kuikly's Flex engine (`LayoutImpl.performMeasureFunction`) treats
        // whatever `measureOutput.width` we write here as the node's final
        // cross-axis layout width. Unlike standard Yoga, it does NOT apply the
        // parent's `alignItems=stretch` after a measure function returns — the
        // measured value wins. Returning 0 would therefore leave the component
        // collapsed on the cross axis regardless of the parent Column's
        // `alignItems=stretch`.
        //
        // The `width` parameter the engine passed in already represents
        // "the parent's available cross-axis width minus this node's
        // horizontal margin" (see `LayoutImpl.performMeasureFunction`), which
        // is exactly what a stretched child should occupy. So we use it as
        // the fallback width. Height has no equivalent constraint here — the
        // only credible fallback is 0, which pushes JSON authors to specify
        // an explicit `height` when they neither register a measurer nor rely
        // on the second-pass resize path (see [handleNativeResize]).
        val fallbackWidth = if (width.isUndefined()) 0f else width
        var outWidth = shadowSize?.width ?: moduleSize?.first ?: fallbackWidth
        var outHeight = shadowSize?.height ?: moduleSize?.second ?: 0f

        // Explicit style dimensions from the DSL (`width` / `height` in JSON)
        // always win over both the measurer result and the fallback above,
        // giving authors a hard override when needed.
        if (!node.styleWidth.isUndefined()) {
            outWidth = node.styleWidth
        }
        if (!node.styleHeight.isUndefined()) {
            outHeight = node.styleHeight
        }

        measureOutput.width = outWidth
        measureOutput.height = outHeight

        lastAppliedWidth = outWidth
        lastAppliedHeight = outHeight
    }

    /**
     * Second-pass resize entry — invoked when the host-platform side (ArkTS
     * for OHOS, via the `A2UIBridgeModule.notifyCustomViewSizeChanged` path)
     * reports that this custom component's intrinsic size has changed, e.g.
     * a `Web` component finished loading its content and now knows its own
     * height.
     *
     * The call is a **no-op if neither dimension changed meaningfully** —
     * negative values indicate "keep current", and any dimension that
     * matches the last-applied value within a 0.5f tolerance is skipped so
     * no redundant layout pass is triggered.
     *
     * Only when at least one dimension actually changes do we write into
     * `flexNode.styleWidth` / `flexNode.styleHeight`, which internally marks
     * the node dirty; the host Pager's layout loop then picks up the change
     * on the next layout tick.
     *
     * All callers must already be on the Kuikly Context Thread (ArkTS entry
     * points go through `com_tencent_kuikly_ScheduleContextTask` before
     * reaching here — see the OHOS registry bridge).
     */
    fun handleNativeResize(newWidth: Float, newHeight: Float) {
        var changed = false
        if (newWidth > 0f && kotlin.math.abs(newWidth - lastAppliedWidth) > 0.5f) {
            flexNode.styleWidth = newWidth
            lastAppliedWidth = newWidth
            changed = true
        }
        if (newHeight > 0f && kotlin.math.abs(newHeight - lastAppliedHeight) > 0.5f) {
            flexNode.styleHeight = newHeight
            lastAppliedHeight = newHeight
            changed = true
        }
        // Nothing to do — the business side reported the same size we already
        // laid out. Returning early here is the key to the "only trigger a
        // second update when something actually changed" contract.
        if (!changed) return
    }
}

/**
 * Process-wide `(pagerId, nativeRef) → A2UICustomComponentView` registry so
 * platform-side code (ArkTS `A2UIBridgeModule` on OHOS) can locate the KMP
 * view instance that owns a given native render view and push back a
 * second-pass size update.
 *
 * Threading: `register` and `unregister` run on the Kuikly Context Thread
 * (invoked from `createRenderView` / `didRemoveFromParentView`); `lookup`
 * may be called from any thread but the caller is responsible for hopping
 * to the Kuikly Context Thread *before* mutating the returned view's Flex
 * tree. On OHOS this is done inside `A2UIRegistryBridge.a2uiNotifyCustomViewResize`
 * via `com_tencent_kuikly_ScheduleContextTask`, mirroring how
 * `a2uiNotifyPickerResult` already handles the ArkTS → Kuikly thread hop.
 *
 * Entries are keyed by the pager-scoped composite `"$pagerId|$nativeRef"`:
 * `nativeRef` alone is only unique within a single pager, so the pager id is
 * baked in to make lookups safe across multiple concurrent A2UI surfaces.
 */
internal object A2UICustomComponentViewRegistry {

    private val views = mutableMapOf<String, A2UICustomComponentView>()

    private fun keyOf(pagerId: String, nativeRef: Int): String = "$pagerId|$nativeRef"

    fun register(pagerId: String, nativeRef: Int, view: A2UICustomComponentView) {
        views[keyOf(pagerId, nativeRef)] = view
    }

    fun unregister(pagerId: String, nativeRef: Int) {
        views.remove(keyOf(pagerId, nativeRef))
    }

    fun lookup(pagerId: String, nativeRef: Int): A2UICustomComponentView? {
        return views[keyOf(pagerId, nativeRef)]
    }
}
