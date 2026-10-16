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

// No package declaration — root package on purpose so that the C++ layer can
// reach these symbols through `api->kotlin.root.*` (mirrors how KSP-generated
// `initKuikly` is authored).

import com.tencent.kuikly.a2ui.adapter.A2UIIconRegistry
import com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
import com.tencent.kuikly.a2ui.bridge.A2UIKotlinMethod
import com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry
import com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import ohos.com_tencent_kuikly_ScheduleContextTask

/**
 * A2UI Kuikly-page bridge: unidirectional writers that ArkTS calls through
 * NAPI to keep the three A2UI global registries populated when the host
 * runs in Kuikly-page mode.
 *
 * ## Why this file exists
 *
 * In Kuikly-page mode the business Pager (`A2UICaseListPager` etc.) extends
 * the plain `Pager` class, so it does not override `onReceivePagerEvent`
 * to route A2UI registration events (`a2ui_registerCustomFunction`, …)
 * into the KMP registries. Registrations issued from the ArkTS side
 * therefore never reach `A2UICustomFunctionRegistry.register(...)`, and
 * `A2UIProtocolAdapter.evaluateCustomFunctionAsString` short-circuits
 * with `descriptor == null`.
 *
 * The three writers below let ArkTS `registerA2UICustomFunction / View /
 * Icon` push straight into the same singletons `A2UIPager.onReceivePagerEvent`
 * would use on the native-surface path, so both hosting modes end up with
 * identical KMP state.
 *
 * ## Header-emission notes
 *
 * These are declared as plain `public` top-level functions (no `@CName`).
 * Kotlin/Native's `libshared_api.h` generator treats every reachable
 * public top-level function as an exported symbol, mirroring how the
 * KSP-generated `initKuikly()` reaches the header without a `@CName`
 * annotation. The C++ NAPI layer accesses them via
 * `api->kotlin.root.a2uiRegisterCustomFunction(...)` etc.
 *
 * We deliberately avoid `@CName` because Kotlin/Native's release-mode
 * DCE strips `@CName` top-level functions unless something else in the
 * compiled module marks them reachable — a caveat proven repeatedly on
 * the `unregister*` variants when we first tried the `@CName` route.
 *
 * ## Why they live in `:demo`
 *
 * The header generator only surfaces top-level public functions authored
 * inside the module currently being linked. Placing these writers in the
 * upstream `:a2ui` module would keep them out of `:demo`'s
 * generated header even though the underlying `libshared.so` bytecode
 * would still contain them.
 */

public fun a2uiRegisterCustomFunction(name: String, spec: String) {
    A2UICustomFunctionRegistry.register(name, spec)
}

public fun a2uiUnregisterCustomFunction(name: String) {
    A2UICustomFunctionRegistry.unregister(name)
}

public fun a2uiRegisterCustomView(viewName: String) {
    A2UICustomViewRegistry.register(viewName)
}

public fun a2uiUnregisterCustomView(viewName: String) {
    A2UICustomViewRegistry.unregister(viewName)
}

public fun a2uiRegisterIcon(name: String, uri: String) {
    A2UIIconRegistry.registerIcon(name, uri)
}

public fun a2uiUnregisterIcon(name: String) {
    A2UIIconRegistry.unregisterIcon(name)
}

/**
 * Deliver a DateTimeInput picker result from ArkTS back into the KMP layer.
 *
 * ## Why this is a top-level bridge
 *
 * In Kuikly-page mode there is no `A2UISurfaceViewController` on the ArkTS
 * side, so the standard `sendEvent("a2ui_pickerResult", …)` path is
 * unavailable — that event is only dispatched by
 * `A2UISurfaceViewController.notifyPickerResult(...)`. Moreover the outer
 * business Pager (e.g. `A2UICaseListPager`) extends the plain `Pager`
 * class and does not override `onReceivePagerEvent` to route
 * `a2ui_pickerResult` to `A2UIBridgeManager.PICKER_RESULT`, so even a
 * manually issued event would be dropped.
 *
 * Instead we let ArkTS's `A2UIBridgeModule.call('showDateTimePicker')`
 * pop its own ArkUI DatePicker/TimePicker dialog and then reach back into
 * the KMP layer directly through this NAPI-exported writer, targeting the
 * dsl-side `instanceId` that was previously baked into the `configJson`
 * request (see `A2UIDSLBridge.showDateTimePicker`). The routing then
 * lands in `A2UIBridgeManager.callKotlinMethod(dslInstanceId,
 * PICKER_RESULT, …)` and finally in the correct
 * `A2UIDSLBridge.handlePickerResult(tag, iso)`.
 *
 * ## Thread-safety: why we hop onto the Kuikly Context Thread
 *
 * NAPI callbacks run on the ArkTS main thread. Directly invoking
 * `callKotlinMethod(PICKER_RESULT, …)` from that thread lands us straight
 * inside `A2UIDSLBridge.handlePickerResult`, which mutates the DSL Flex
 * tree (`applier.updateDateTimeInputValue` / `applier.performLayout` /
 * further protocol-adapter cascades). Those Flex/DSL operations are only
 * safe to perform on the Kuikly Context Thread — running them on ArkTS
 * main races with the host page's own layout pass and visibly corrupts
 * the outer Kuikly page layout (see Android
 * `A2UIAndroidNativeMethodHandler.sendPickerResult` which does the same
 * hop via `KuiklyRenderCoreContextScheduler`).
 *
 * We therefore stash the request under the *outer host* pager id and ask
 * Kuikly Core (`com_tencent_kuikly_ScheduleContextTask`) to invoke us
 * back on the context thread of that pager. The C callback (which cannot
 * capture a closure — see `staticCFunction` restrictions) then drains any
 * pending picker results for the pager and finally dispatches them into
 * `A2UIBridgeManager.callKotlinMethod` — this time on the correct thread.
 *
 * If the host pager id can't be resolved (legacy native-page mode, or
 * malformed configJson), we fall back to a synchronous dispatch: the
 * legacy path already runs on the correct thread anyway.
 *
 * @param dslInstanceId The internal A2UI DSL child pager id (baked into the
 *                      original picker config so ArkTS can echo it back).
 * @param tag           A2UI protocol tag of the DateTimeInput component.
 * @param isoString     Selected date/time in ISO 8601 format.
 */
@OptIn(ExperimentalForeignApi::class)
public fun a2uiNotifyPickerResult(dslInstanceId: String, tag: Int, isoString: String) {
    val hostPagerId = A2UIBridgeManager.getHostPagerIdForDsl(dslInstanceId)
    if (hostPagerId == null) {
        // No enclosing Kuikly host — assume caller is already on the correct
        // thread (legacy native-page mode).
        A2UIBridgeManager.callKotlinMethod(
            A2UIKotlinMethod.PICKER_RESULT,
            dslInstanceId,
            "$tag|$isoString",
            null
        )
        return
    }
    A2UIPickerResultRelay.enqueue(hostPagerId, dslInstanceId, tag, isoString)
    com_tencent_kuikly_ScheduleContextTask(hostPagerId, staticCFunction { pagerIdBytes: CPointer<ByteVar>? ->
        val id = pagerIdBytes?.toKString() ?: return@staticCFunction
        A2UIPickerResultRelay.drainAndDispatch(id)
    })
}

/**
 * Process-wide queue of picker results waiting to be dispatched on the
 * Kuikly Context Thread of a specific pager id.
 *
 * Producer: `a2uiNotifyPickerResult` (called from the ArkTS main thread
 *           via NAPI).
 * Consumer: `drainAndDispatch` (called from the Kuikly Context Thread of
 *           the target pager via `com_tencent_kuikly_ScheduleContextTask`
 *           → `staticCFunction` trampoline).
 *
 * Kotlin/Native's `staticCFunction` cannot capture a closure, so we can't
 * simply pass a lambda holding `dslInstanceId` / `tag` / `isoString` to
 * `com_tencent_kuikly_ScheduleContextTask`. This relay keeps the arguments
 * alive between the schedule call and the callback.
 *
 * All operations are guarded so producer/consumer running on different
 * threads (ArkTS main vs Kuikly Context) never see torn state; simple
 * synchronization is enough because the payload lifetime is measured in
 * milliseconds and picker events are one-shot per user tap.
 */
private object A2UIPickerResultRelay : SynchronizedObject() {

    private data class Entry(
        val dslInstanceId: String,
        val tag: Int,
        val isoString: String
    )

    /** hostPagerId → pending picker results waiting to hit the Kuikly thread. */
    private val queues = mutableMapOf<String, MutableList<Entry>>()

    fun enqueue(hostPagerId: String, dslInstanceId: String, tag: Int, isoString: String) {
        synchronized(this) {
            val bucket = queues.getOrPut(hostPagerId) { mutableListOf() }
            bucket.add(Entry(dslInstanceId, tag, isoString))
        }
    }

    fun drainAndDispatch(hostPagerId: String) {
        val snapshot: List<Entry> = synchronized(this) {
            val bucket = queues[hostPagerId] ?: return
            if (bucket.isEmpty()) return
            val copy = bucket.toList()
            bucket.clear()
            copy
        }
        for (entry in snapshot) {
            A2UIBridgeManager.callKotlinMethod(
                A2UIKotlinMethod.PICKER_RESULT,
                entry.dslInstanceId,
                "${entry.tag}|${entry.isoString}",
                null
            )
        }
    }
}

/**
 * Push an async "custom view size changed" notification from ArkTS into
 * the KMP layer. Companion to [notifyA2UICustomViewSizeChanged] on the
 * ArkTS side.
 *
 * ## Why this is a top-level bridge
 *
 * Same reason as [a2uiNotifyPickerResult]: the Kuikly-page business pager
 * extends plain `Pager`, so the usual `sendEvent` route cannot carry a
 * message into A2UI's dispatcher. Instead ArkTS calls this NAPI writer
 * directly and we land in [A2UIBridgeManager.notifyCustomViewResize]
 * which looks up the [A2UICustomComponentView] registered under
 * `(pagerId, nativeRef)` and updates its Flex node.
 *
 * ## Thread-safety: Kuikly Context Thread hop
 *
 * The actual size update eventually writes `flexNode.styleWidth` /
 * `styleHeight`, which internally marks the node dirty and expects to
 * run on the owning pager's Kuikly Context Thread. NAPI callbacks arrive
 * on the ArkTS main thread, so we schedule onto the Kuikly thread via
 * `com_tencent_kuikly_ScheduleContextTask` before touching the Flex
 * tree — identical to the picker-result path.
 *
 * @param pagerId   Pager id that owns the target custom view (either the
 *                  A2UI DSL instance id in native-page mode or the host
 *                  Kuikly pager id in Kuikly-page-embedded mode).
 * @param nativeRef Kuikly Core render-view tag of the custom view.
 * @param width     New intrinsic width in vp, or `0` to keep width.
 * @param height    New intrinsic height in vp, or `0` to keep height.
 */
@OptIn(ExperimentalForeignApi::class)
public fun a2uiNotifyCustomViewResize(
    pagerId: String,
    nativeRef: Int,
    width: Float,
    height: Float
) {
    A2UICustomViewResizeRelay.enqueue(pagerId, nativeRef, width, height)
    com_tencent_kuikly_ScheduleContextTask(pagerId, staticCFunction { pagerIdBytes: CPointer<ByteVar>? ->
        val id = pagerIdBytes?.toKString() ?: return@staticCFunction
        A2UICustomViewResizeRelay.drainAndDispatch(id)
    })
}

/**
 * Companion relay for [a2uiNotifyCustomViewResize]. Same contract as
 * [A2UIPickerResultRelay]: the ArkTS main thread enqueues, the Kuikly
 * Context Thread drains. `staticCFunction` cannot capture a closure, so
 * the payload lives here between the schedule call and the callback.
 */
private object A2UICustomViewResizeRelay : SynchronizedObject() {

    private data class Entry(
        val nativeRef: Int,
        val width: Float,
        val height: Float
    )

    /** pagerId → pending resize events waiting to hit the Kuikly thread. */
    private val queues = mutableMapOf<String, MutableList<Entry>>()

    fun enqueue(pagerId: String, nativeRef: Int, width: Float, height: Float) {
        synchronized(this) {
            val bucket = queues.getOrPut(pagerId) { mutableListOf() }
            bucket.add(Entry(nativeRef, width, height))
        }
    }

    fun drainAndDispatch(pagerId: String) {
        val snapshot: List<Entry> = synchronized(this) {
            val bucket = queues[pagerId] ?: return
            if (bucket.isEmpty()) return
            val copy = bucket.toList()
            bucket.clear()
            copy
        }
        for (entry in snapshot) {
            A2UIBridgeManager.notifyCustomViewResize(
                pagerId,
                entry.nativeRef,
                entry.width,
                entry.height
            )
        }
    }
}