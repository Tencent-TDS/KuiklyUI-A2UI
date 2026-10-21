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

import com.tencent.kuikly.a2ui.adapter.A2UIProtocolAdapter
import com.tencent.kuikly.a2ui.bridge.A2UINativeMethod
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.core.base.ContainerAttr
import com.tencent.kuikly.core.base.CrossPlatFeature
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

/**
 * A2UIDSLBridge - Integration layer that connects A2UI JSON protocol to the Kuikly DSL rendering path.
 *
 * This is the new rendering path that replaces the old flow:
 *   JSON → Dispatcher → FlexNode → NativeBridge → KuiklyRenderLayerHandler
 *
 * With the new flow:
 *   JSON → A2UIDSLBridge → A2UIApplier → Kuikly DSL Component Tree → Kuikly Core (auto layout + render)
 *
 * The key difference is that Kuikly Core handles:
 * - FlexNode creation and layout calculation automatically
 * - RenderView creation and frame synchronization
 * - Text measurement via RichTextView's built-in shadow
 *
 * Usage:
 * ```kotlin
 * // Native-page mode:
 * val bridge = A2UIDSLBridge(instanceId, nativeBridge)
 * // Kuikly-page-embedded mode (no per-surface NativeBridge needed):
 * val bridge = A2UIDSLBridge(instanceId, nativeBridge = null, externalHost = host)
 * bridge.initialize(width = 375f)
 * bridge.writeStream(jsonChunk)
 * bridge.finishStream()
 * // Content height is reported via onContentHeightChanged callback
 * bridge.destroy()
 * ```
 *
 * Transport channels
 * ------------------
 * This bridge has to reach the platform (native) layer for a few things:
 * showing the date/time picker, running `executeFunctionCall(openUrl)`,
 * invoking a registered custom function synchronously, …
 *
 * There are two possible wires:
 *
 * - **NativeBridge** (`toNative(methodId, …)`): the per-pager Kuikly
 *   transport. Used *only* in Native-page mode, where this class owns a
 *   dedicated `NativeBridge` (coming in through the constructor) that is
 *   handled directly by `A2UISurfaceView.callNative(…)` on all three
 *   platforms. In Kuikly-page-embedded mode this is `null` and we skip
 *   the methodId path entirely — the enclosing page's render core would
 *   have no receiver for those ids anyway.
 * - **Module channel** (`A2UIBridgeManager.dispatchToA2UIBridgeModule`):
 *   the standard Kuikly module bus. Used in Kuikly-page-embedded mode on
 *   all three target platforms (Android / OHOS / iOS); the module class
 *   `A2UIBridgeModule` is installed on the host Kuikly page's delegate.
 *
 * Both wires carry identical payload shapes so Native and Module handlers
 * are symmetric. Native-page mode normally fires only the NativeBridge
 * path (the Module path is a cheap compile-time no-op on non-module
 * targets and a redundant extra call on the others).
 */
@Suppress("DEPRECATION")
class A2UIDSLBridge(
    private val instanceId: String,
    /**
     * The per-pager NativeBridge used in **Native-page mode only**. In
     * **Kuikly-page-embedded mode** this is `null`: the enclosing Kuikly
     * page owns its own NativeBridge and the enclosing page's render core
     * has no receiver for A2UI's methodId=103/106/107, so all native
     * dispatch goes through the Module channel
     * (`A2UIBridgeManager.dispatchToA2UIBridgeModule`).
     */
    private val nativeBridge: NativeBridge?,
    /**
     * The A2UIHost that hosts A2UI-generated components.
     *
     * - If `null` (default / legacy path): [initialize] creates its own [A2UIPager]
     *   via [PagerManager.createPager], and this bridge fully owns the pager lifecycle
     *   (register NativeBridge, destroy pager on [destroy]). This matches the existing
     *   native A2UISurfaceView integration. [nativeBridge] MUST be non-null in this
     *   mode.
     *
     * - If non-null (embedded / DSL path): the bridge attaches [A2UIApplier] to the
     *   provided host directly, without creating or destroying any Pager. The caller
     *   (e.g. `A2UISurfaceDSLView`) owns the pager lifecycle. All platform dispatch
     *   happens through the Module channel in this mode.
     */
    private val externalHost: A2UIHost? = null
) {
    /** The A2UIPager instance created internally (legacy path only) */
    private var pager: A2UIPager? = null

    /** The host that A2UIApplier attaches to — either [externalHost] or [pager]. */
    private var host: A2UIHost? = null

    /** The applier that translates messages to DSL tree operations */
    private var applier: A2UIApplier? = null

    /** Stream parser for JSON protocol */
    private var streamParser: A2UIStreamParser? = null

    /** Protocol adapter reference (for action lookup) */
    internal var protocolAdapter: A2UIProtocolAdapter? = null

    /** Callback when content height changes after layout */
    var onContentHeightChanged: ((Float) -> Unit)? = null

    /** Callback for action dispatch */
    var onAction: ((surfaceId: String, tag: Int, action: String) -> Unit)? = null

    /** Callback when a Markdown link inside a Text is tapped (the link URL). */
    var onOpenUrl: ((String) -> Unit)? = null

    /** Callback when data model changes due to user interaction (two-way binding).
     *  Parameters: dataModelJson (full snapshot), changedKey (the key that changed) */
    var onDataModelChanged: ((dataModelJson: String, changedKey: String) -> Unit)? = null

    /** Whether the bridge has been initialized */
    private var isInitialized = false

    /** Stored surface width for deferred initialization */
    private var pendingWidth: Float = 0f

    /**
     * Initialize the DSL bridge.
     *
     * Two modes:
     *
     * - **Legacy mode** (externalHost == null): creates an [A2UIPager] via PagerManager,
     *   registers the NativeBridge, and manages the pager lifecycle. Used by the
     *   native A2UISurfaceView entry point.
     * - **Embedded mode** (externalHost != null): reuses the caller-provided host
     *   (typically an `A2UISurfaceDSLView` living inside the enclosing Kuikly page).
     *   The bridge does NOT create/destroy any Pager and does NOT register the
     *   NativeBridge — those are already owned by the enclosing Kuikly page.
     *
     * @param width Initial width constraint in dp (legacy mode only; 0 means deferred)
     * @param deviceWidth Real screen width in dp (legacy mode only)
     * @param deviceHeight Real screen height in dp (legacy mode only)
     */
    fun initialize(width: Float = 0f, deviceWidth: Float = 0f, deviceHeight: Float = 0f) {
        if (isInitialized) return

        // Initialize BridgeManager if needed
        if (!BridgeManager.isDidInit()) {
            BridgeManager.init(true)
        }

        // Disable flat-layer optimization for A2UI so every dynamically-added
        // container becomes a RenderView (flat-layer would otherwise skip creating
        // native views for containers that only have layout properties).
        //
        // - Embedded mode: we set the per-subtree flag on the host's rootContainer
        //   ATTR (see [A2UIHost.rootContainer] / [ContainerAttr.forceRenderView]).
        //   The attr setter is monotonic (SUB_TREE wins) and the SUB_TREE mode is
        //   propagated to every child container at insert time by
        //   `ViewContainer.internalAddChild`, so only the A2UI subtree is affected
        //   — the enclosing Kuikly page keeps its flat-layer optimization intact.
        // - Legacy mode: we toggle the process-wide switch, matching pre-existing
        //   behavior (the whole A2UIPager tree is A2UI-owned anyway).
        if (externalHost != null) {
            (externalHost.rootContainer.getViewAttr() as? ContainerAttr)
                ?.forceRenderViewSubtree()
        } else {
            CrossPlatFeature.isIgnoreRenderViewForFlatLayer = true
        }

        val activeHost: A2UIHost
        if (externalHost != null) {
            // Embedded mode: reuse the host provided by the caller (e.g. A2UISurfaceDSLView).
            // The host's Pager (and its NativeBridge) already exist and are owned by the
            // enclosing Kuikly page; this bridge only wires up A2UIApplier and the parser.
            activeHost = externalHost

            // Ensure currentPageId is set for ReactiveObserver lookup in attr{}.
            BridgeManager.currentPageId = activeHost.hostPagerId
        } else {
            // Legacy mode: own the Pager lifecycle.
            // In this mode [nativeBridge] is contractually non-null.
            val transport = nativeBridge
                ?: error("A2UIDSLBridge: nativeBridge must be non-null in Native-page mode")

            // Register NativeBridge with BridgeManager
            BridgeManager.registerNativeBridge(instanceId, transport)

            // Register the A2UIPager as a page
            PagerManager.registerPageRouter("a2ui_dsl_$instanceId") { A2UIPager() }

            // Set currentPageId BEFORE creating pager, because body() → attr{} needs
            // ReactiveObserver lookup via BridgeManager.currentPageId
            BridgeManager.currentPageId = instanceId

            // Create the pager via PagerManager (standard Kuikly lifecycle)
            val pageData = JSONObject().apply {
                put("rootViewWidth", if (width > 0f) width.toDouble() else 375.0)
                put("rootViewHeight", 10000.0) // Large value = content-driven height
                // Required so Modal (ModalView.inWindow) renders as a top-level, full-screen
                // window above the embedded card surface. Without this, the modal would be
                // clipped to the (self-sizing) card bounds.
                put("nativeBuild", 2)
                // Real screen size (dp) so full-screen overlays (Modal) lay out against the
                // whole screen instead of the self-sizing card width/height.
                if (deviceWidth > 0f) put("deviceWidth", deviceWidth.toDouble())
                if (deviceHeight > 0f) put("deviceHeight", deviceHeight.toDouble())
            }
            PagerManager.createPager(instanceId, "a2ui_dsl_$instanceId", pageData.toString())

            // Get the created pager
            val createdPager = PagerManager.getPager(instanceId) as? A2UIPager
            if (createdPager == null) {
                return
            }
            pager = createdPager

            // Set up content height reporting (legacy mode only \u2014 embedded mode's height
            // is driven by the host Kuikly Flex engine automatically).
            createdPager.onContentHeightChanged = { height ->
                onContentHeightChanged?.invoke(height)
            }

            activeHost = createdPager
        }

        host = activeHost

        // Create applier bound to the active host
        applier = A2UIApplier(activeHost)

        // Wire up two-way binding: slider value changes update the data model
        applier!!.onSliderValueChanged = { tag, dataKey, newValue ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val updateMessages = adapter.updateDataModelValue(dataKey, newValue)
                // Apply the resulting update messages (re-evaluate dependent components)
                // But skip the slider itself to avoid feedback loops
                for (msg in updateMessages) {
                    if (msg.tag != tag) {
                        handleMessage(msg)
                    }
                }
                // Trigger layout after updates
                this.applier?.performLayout()
                // Notify host if sendDataModel is enabled
                if (adapter.sendDataModel) {
                    onDataModelChanged?.invoke(adapter.getDataModelSnapshot(), dataKey)
                }
            }
        }

        // Wire up two-way binding: checkbox value changes update the data model
        applier!!.onCheckBoxValueChanged = { tag, dataKey, newValue ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val updateMessages = adapter.updateDataModelValue(dataKey, newValue)
                // Apply the resulting update messages (re-evaluate dependent components)
                // But skip the checkbox itself to avoid feedback loops
                for (msg in updateMessages) {
                    if (msg.tag != tag) {
                        handleMessage(msg)
                    }
                }
                // Trigger layout after updates
                this.applier?.performLayout()
                // Notify host if sendDataModel is enabled
                if (adapter.sendDataModel) {
                    onDataModelChanged?.invoke(adapter.getDataModelSnapshot(), dataKey)
                }
            }
        }

        // Wire up two-way binding: textField value changes update the data model
        applier!!.onTextFieldValueChanged = { tag, dataKey, newValue ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val updateMessages = adapter.updateDataModelValue(dataKey, newValue)
                // Apply the resulting update messages (re-evaluate dependent components)
                for (msg in updateMessages) {
                    if (msg.tag != tag) {
                        // Apply all updates for other components (error text, button, etc.)
                        handleMessage(msg)
                    } else {
                        // For the text field itself, only apply non-text props (e.g., border)
                        // to avoid feedback loops from setting text programmatically
                        val nonTextProps = msg.props?.filterKeys { it != "text" && it != "__checkError" }
                        if (!nonTextProps.isNullOrEmpty()) {
                            val filteredMsg = msg.copy(props = nonTextProps)
                            handleMessage(filteredMsg)
                        }
                    }
                }
                // Trigger layout after updates
                this.applier?.performLayout()
                // Notify host if sendDataModel is enabled
                if (adapter.sendDataModel) {
                    onDataModelChanged?.invoke(adapter.getDataModelSnapshot(), dataKey)
                }
            }
        }

        // Wire up two-way binding: dateTimeInput requests native picker dialog
        applier!!.onShowDateTimePicker = { tag, enableDate, enableTime, currentValue, label, min, max ->
            showDateTimePicker(tag, enableDate, enableTime, currentValue, label, min, max)
        }

        // Wire up two-way binding: choicePicker chip tapped
        applier!!.onChoicePickerValueChanged = { chipTag, parentComponentId, optionValue ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val updateMessages = adapter.updateChoicePickerSelection(parentComponentId, optionValue)
                // Apply the resulting update messages (re-render chips and dependent components)
                for (msg in updateMessages) {
                    handleMessage(msg)
                }
                // Trigger layout after updates
                this.applier?.performLayout()
                // Notify host if sendDataModel is enabled
                if (adapter.sendDataModel) {
                    val bindingPath = adapter.getChoicePickerBindingPath(parentComponentId)
                    if (bindingPath != null) {
                        onDataModelChanged?.invoke(adapter.getDataModelSnapshot(), bindingPath)
                    }
                }
            }
        }

        // Wire up choicePicker filter input text change
        applier!!.onChoicePickerFilter = { componentId, filterText ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val filterMessages = adapter.filterChoicePickerOptions(componentId, filterText)
                for (msg in filterMessages) {
                    handleMessage(msg)
                }
                this.applier?.performLayout()
            }
        }

        // Wire up tab selection: tab header tapped
        applier!!.onTabSelected = { tabsId, tabIndex ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val updateMessages = adapter.switchTab(tabsId, tabIndex)
                for (msg in updateMessages) {
                    handleMessage(msg)
                }
                this.applier?.performLayout()
            }
        }

        // Wire up Modal open: trigger tapped -> build & show the modal overlay
        applier!!.onModalOpen = { modalId ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val openMessages = adapter.openModal(modalId)
                for (msg in openMessages) {
                    handleMessage(msg)
                }
                this.applier?.performLayout()
            }
        }

        // Wire up Modal close: close button / backdrop / back pressed -> tear down overlay.
        // The teardown removes the overlay views synchronously; the native removal is queued
        // and applied by the syncFlushUI() that the bridge runs right after the click handler
        // returns (see A2UIBridgeManager.handleViewEvent), so it is effectively deferred past
        // the active touch dispatch. (Note: setTimeout is not viable here — the embedded A2UI
        // surface does not wire up the standard timer bridge, so its callback never fires.)
        applier!!.onModalClose = { modalId ->
            val adapter = protocolAdapter
            if (adapter != null) {
                val closeMessages = adapter.closeModal(modalId)
                for (msg in closeMessages) {
                    handleMessage(msg)
                }
                this.applier?.performLayout()
            }
        }

        // Wire up Markdown link taps -> forward the URL to the host for opening.
        applier!!.onMarkdownLinkClick = { url ->
            onOpenUrl?.invoke(url)
        }

        // Wire up view click (touchEnable=1) -> dispatch action via handleViewClick.
        // This is needed because on OHOS, the C++ render layer does NOT register
        // a click callback when touchEnable=1 is set (unlike Android which registers
        // OnClickListener and sends VIEW_CLICK). Instead, we use Kuikly Core's standard
        // click event mechanism which works cross-platform.
        applier!!.onViewClick = { tag ->
            handleViewClick(tag)
        }

        // Create stream parser
        streamParser = A2UIStreamParser()
        protocolAdapter = streamParser?.protocolAdapter

        // Wire the protocol adapter's expression-time custom function invoker.
        // This calls back into the native (platform) layer via NativeBridge
        // (Native-page mode on all platforms) and via callModuleMethod on
        // module-based platforms (Android & OHOS in Kuikly-page-embedded mode).
        // See A2UINativeMethod.INVOKE_CUSTOM_FUNCTION for the wire contract.
        protocolAdapter?.customFunctionInvoker = { name, argsJson, spec ->
            invokeCustomFunctionOnNative(name, argsJson, spec)
        }

        // Set up stream parser listener
        streamParser?.setListener(object : A2UIStreamParser.A2UIStreamListener {
            override fun onMessageParsed(message: A2UIMessage) {
                handleMessage(message)
            }

            override fun onError(error: com.tencent.kuikly.a2ui.parser.A2UIParseError) {
                println("[A2UI-DSL] Parse error: ${error.message}")
            }

            override fun onStreamEnd() {
                // Trigger layout when stream ends
                applier?.performLayout()
            }
        })

        // Set initial width if provided.
        // Only meaningful in legacy mode — embedded mode's surface width is driven
        // by the enclosing Kuikly page's Flex layout via A2UISurfaceDSLView.
        if (width > 0f) {
            pager?.setSurfaceWidth(width)
        }

        isInitialized = true
    }

    /**
     * Set the surface size constraint.
     *
     * @param width Width in dp
     * @param height Height in dp (>= 10000 means content-driven)
     */
    fun setSurfaceSize(width: Float, height: Float) {
        if (!isInitialized) {
            pendingWidth = width
            return
        }
        // Only the legacy-mode pager supports explicit setSurfaceSize; in embedded
        // mode the surface dimensions are driven by the host page's Flex layout.
        pager?.setSurfaceSize(width, height)
    }

    /**
     * Update the device (screen) dimensions used for full-screen overlays (Modal).
     * Called when the platform layer provides real screen size information.
     *
     * @param deviceWidth Real screen width in dp
     * @param deviceHeight Real screen height in dp
     */
    fun setDeviceSize(deviceWidth: Float, deviceHeight: Float) {
        // Legacy mode: forward device size to the internal pager. Embedded mode
        // reads device dimensions from the enclosing Kuikly page's PageData.
        val p = pager ?: return
        p.updateDeviceSize(deviceWidth, deviceHeight)
    }

    /**
     * Write a chunk of A2UI JSON protocol stream.
     */
    fun writeStream(chunk: String) {
        if (!isInitialized) {
            // Deferred initialization with pending width
            if (pendingWidth > 0f) {
                initialize(pendingWidth)
            } else {
                initialize()
            }
        }
        streamParser?.feed(chunk)
    }

    /**
     * Mark the stream as finished.
     * Triggers final layout calculation.
     */
    fun finishStream() {
        streamParser?.finish()
    }

    /**
     * Handle a parsed A2UI message by translating it to DSL tree operations.
     */
    private fun handleMessage(message: A2UIMessage) {
        val applier = this.applier ?: return

        when (message.type) {
            A2UIMessageType.CREATE_SURFACE -> {
                // Surface creation is handled by the pager initialization
                // Nothing to do here for DSL path
            }

            A2UIMessageType.CREATE_COMPONENT -> {
                val tag = message.tag
                val viewName = message.viewName ?: "KRView"
                val parentTag = message.parentTag
                val index = message.index
                val props = message.props

                applier.createComponent(tag, viewName, parentTag, index, props)
            }

            A2UIMessageType.UPDATE_COMPONENT -> {
                val tag = message.tag
                val viewName = message.viewName ?: "KRView"
                val props = message.props ?: return

                applier.updateComponent(tag, viewName, props)
            }

            A2UIMessageType.REMOVE_COMPONENT -> {
                val tag = message.tag
                val parentTag = message.parentTag

                applier.removeComponent(tag, parentTag)
            }

            A2UIMessageType.SET_LAYOUT -> {
                // In DSL path, layout is handled automatically by Kuikly Core
                // No manual frame setting needed
            }

            A2UIMessageType.BATCH_UPDATE -> {
                // Process batch messages recursively
                message.messages?.forEach { subMessage ->
                    handleMessage(subMessage)
                }
            }

            A2UIMessageType.STREAM_END -> {
                applier.performLayout()
            }

            else -> {
                // Other message types (ACTION, EVENT, etc.) handled by existing mechanism
            }
        }
    }

    /**
     * Handle a view click event (for action dispatch).
     */
    fun handleViewClick(tag: Int) {
        val adapter = protocolAdapter ?: return
        val actionJson = adapter.getActionForTag(tag) ?: return
        onAction?.invoke("dsl-surface", tag, actionJson)
    }

    /**
     * In embedded mode (Kuikly page hosts an A2UISurface DSL), the enclosing
     * Kuikly page's pagerId — used to route `callModuleMethod` calls to the
     * host-side `A2UIBridgeModule`, which is installed on the *host*
     * Kuikly page's delegate rather than on the internal A2UI child pager.
     * Returns `null` in legacy Native-page mode; callers should then fall
     * back to `instanceId` (which equals the child pager's own pagerId).
     */
    internal fun getHostPagerId(): String? = externalHost?.hostPagerId

    /**
     * Reach back into the native (platform) layer to run a user-registered
     * custom A2UI function synchronously, then return its raw value to the
     * protocol adapter for coercion.
     *
     * Two symmetric channels (both carry the same `name / spec / argsJson`
     * payload shape):
     *
     * 1. **Native-page mode** (this bridge owns a per-surface NativeBridge):
     *    dispatch via [NativeBridge.toNative] with methodId
     *    [A2UINativeMethod.INVOKE_CUSTOM_FUNCTION]. The platform's
     *    `A2UISurfaceView.callNative(…)` handles the id directly and runs
     *    the registered handler synchronously on the Kuikly Context Thread.
     * 2. **Kuikly-page-embedded mode** (nativeBridge is null, [externalHost]
     *    is set): dispatch via [A2UIBridgeManager.dispatchToA2UIBridgeModule]
     *    with method `"invokeCustomFunction"`, syncCall=1. The host-page's
     *    `A2UIBridgeModule` executes the handler and returns the raw value.
     *    This path is active on all three target platforms (Android / OHOS
     *    / iOS) — see `A2UIBridgeModule.kt` / `.ets` / `.m`.
     *
     * The embedded path routes by `externalHost.hostPagerId` because the
     * `A2UIBridgeModule` lives on the enclosing Kuikly page's delegate,
     * not on this bridge's own (child) pager.
     *
     * NOTE: The caller (adapter) is responsible for guarding this against
     *       VOID descriptors; VOID functions never reach expression paths.
     */
    private fun invokeCustomFunctionOnNative(name: String, argsJson: String, spec: String): Any? {
        return if (externalHost == null) {
            // Native-page: direct per-surface NativeBridge round-trip.
            // `nativeBridge` is contractually non-null in this mode (see
            // the `error(...)` guard in `initialize`); use `!!` so a bug
            // that regresses that invariant fails loud.
            try {
                nativeBridge!!.toNative(
                    A2UINativeMethod.INVOKE_CUSTOM_FUNCTION,
                    instanceId,
                    name,
                    argsJson,
                    spec,
                    null,
                    null
                )
            } catch (_: Exception) {
                null
            }
        } else {
            // Kuikly-page-embedded: standard Module channel. Payload shape
            // mirrors the Native-page NativeBridge call so the module side
            // can parse name/spec/argsJson by splitting on '\n'.
            com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager
                .dispatchToA2UIBridgeModule(
                    externalHost.hostPagerId,
                    "invokeCustomFunction",
                    "$name\n$spec\n$argsJson",
                    1
                )
        }
    }

    /**
     * Get the A2UIPager instance (for nativeRef → protocol tag lookup).
     */
    internal fun getPager(): A2UIPager? = pager

    /**
     * Show a native date/time picker dialog via the native layer.
     * Called by DateTimeInputView when the user taps the display area.
     *
     * @param tag The A2UI protocol tag of the DateTimeInput component
     * @param enableDate Whether date selection is enabled
     * @param enableTime Whether time selection is enabled
     * @param currentValue Current ISO 8601 value
     * @param label Display label for the picker title
     */
    fun showDateTimePicker(tag: Int, enableDate: Boolean, enableTime: Boolean, currentValue: String, label: String, min: String = "", max: String = "") {
        val minPart = if (min.isNotEmpty()) ",\"min\":\"$min\"" else ""
        val maxPart = if (max.isNotEmpty()) ",\"max\":\"$max\"" else ""
        // Include our own DSL instanceId so the Module-side handler
        // (A2UIBridgeModule on Android / iOS / OHOS) can route the picker
        // result back through `A2UIBridgeManager.callKotlinMethod(PICKER_RESULT, …)`
        // — in Kuikly-page-embedded mode there is no `A2UISurfaceView` to
        // dispatch the result through. Native-page handlers ignore this field.
        val configJson = "{\"tag\":$tag,\"enableDate\":$enableDate,\"enableTime\":$enableTime,\"value\":\"$currentValue\",\"label\":\"$label\",\"dslInstanceId\":\"$instanceId\"$minPart$maxPart}"
        if (externalHost == null) {
            // Native-page: send through the per-surface NativeBridge.
            // `A2UISurfaceView.callNative(SHOW_DATE_TIME_PICKER, …)` on each
            // platform pops the picker locally and sends the result back
            // via `callKotlinMethod(PICKER_RESULT, …)`.
            val transport = nativeBridge
                ?: error("A2UIDSLBridge: nativeBridge must be non-null in Native-page mode")
            transport.toNative(
                A2UINativeMethod.SHOW_DATE_TIME_PICKER,
                instanceId,
                tag,
                configJson,
                null,
                null,
                null
            )
            transport.toNative(A2UINativeMethod.FLUSH_UI_TASKS, instanceId, null, null, null, null, null)
        } else {
            // Kuikly-page-embedded: route through the standard Module channel.
            // The `A2UIBridgeModule` installed on the host Kuikly page's
            // delegate pops the native picker and sends the result back
            // via `A2UIBridgeManager.callKotlinMethod(PICKER_RESULT, …)`.
            com.tencent.kuikly.a2ui.bridge.A2UIBridgeManager.dispatchToA2UIBridgeModule(
                externalHost.hostPagerId,
                "showDateTimePicker",
                configJson,
                1
            )
            // Host Kuikly page's own render loop flushes UI tasks after we
            // return; no need to send a per-surface FLUSH_UI_TASKS here.
        }
    }

    /**
     * Handle the picker result from native layer.
     * Updates the DateTimeInputView and triggers two-way binding.
     *
     * @param tag The A2UI protocol tag of the DateTimeInput component
     * @param isoString The selected ISO 8601 date/time string
     */
    fun handlePickerResult(tag: Int, isoString: String) {
        val applier = this.applier ?: return
        val adapter = this.protocolAdapter ?: return

        // Update the DateTimeInputView's displayed value
        applier.updateDateTimeInputValue(tag, isoString)

        // Trigger two-way binding: update data model and re-evaluate dependent components
        val bindingPath = applier.getDateTimeBindingPath(tag)
        if (bindingPath != null) {
            val updateMessages = adapter.updateDataModelValue(bindingPath, isoString)
            for (msg in updateMessages) {
                if (msg.tag != tag) {
                    handleMessage(msg)
                }
            }
            applier.performLayout()
            // Notify host if sendDataModel is enabled
            if (adapter.sendDataModel) {
                onDataModelChanged?.invoke(adapter.getDataModelSnapshot(), bindingPath)
            }
        }
    }

    /**
     * Destroy the bridge and release all resources.
     */
    fun destroy() {
        if (!isInitialized) return

        applier?.destroy()
        streamParser?.reset()

        if (externalHost != null) {
            // Embedded mode: we do NOT own the pager or the NativeBridge.
            // Clean up only our own state and let the enclosing Kuikly page
            // manage the pager / NativeBridge lifecycle.
            //
            // Tag ↔ view mappings live in externalHost.registry; clearing them
            // avoids stale entries if the surface view is re-attached later
            // with new content. The DSL views themselves are removed from the
            // host's rootContainer either by the applier (via removeComponent
            // messages) or by A2UISurfaceDSLView.willRemoveFromParentView().
            externalHost.registry.clear()
        } else {
            // Legacy mode: own the pager lifecycle.
            // Set currentPageId before destroying pager, because onDestroyPager -> didRemoveFromParentView
            // -> ReactiveObserver.removeObserver needs to find the correct ReactiveObserver via currentPageId
            BridgeManager.currentPageId = instanceId

            // Destroy pager via PagerManager
            PagerManager.destroyPager(instanceId)
        }

        pager = null
        host = null
        applier = null
        streamParser = null
        protocolAdapter = null
        onContentHeightChanged = null
        onAction = null
        onDataModelChanged = null
        isInitialized = false
    }
}
