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

package com.tencent.kuikly.a2ui.adapter

import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.protocol.A2UISurfaceConfig

/**
 * Adapter that converts standard A2UI v0.9 protocol messages into internal A2UIMessage format.
 *
 * Standard A2UI 0.9 protocol uses:
 * - Top-level message keys: "createSurface", "updateComponents", "updateDataModel"
 * - Components identified by string "id" (not integer tag)
 * - Component type specified by "component" field
 * - Tree structure via "children" arrays and "child" references
 * - Data binding via {"path": "/key"} syntax
 *
 * This adapter translates these into the internal format used by A2UIApplier:
 * - Integer tags for component identification
 * - "viewName" for component type (mapped to Kuikly internal names)
 * - Explicit parentTag/index for tree operations
 * - Kuikly-compatible property names
 */
class A2UIProtocolAdapter {

    companion object {
        // ---------------------------------------------------------------
        // Card semantic defaults
        //
        // Card's `border` / `boxShadow` / `backgroundColor` defaults are
        // installed in TWO places that MUST stay in lock-step:
        //   1. buildProps("Card") — seeds these keys before the caller's
        //      `styles` merge.
        //   2. clearDefaultsIfGradient() — removes them again when the
        //      caller supplies `backgroundLinearGradient`, but ONLY when
        //      the values still match the defaults (so user-supplied
        //      styles.border / styles.boxShadow are preserved).
        //
        // Historically both sites carried literal strings ("1 solid
        // #E0E0E0", plus their post-hex-normalization decimal cousins
        // "1 solid 4292927712"), which drifted the moment Card's default
        // palette changed. Centralize the source of truth here.
        // ---------------------------------------------------------------
        private const val CARD_DEFAULT_BORDER_COLOR = "#E0E0E0"
        private const val CARD_DEFAULT_SHADOW_COLOR = "#1A000000"
        private const val CARD_DEFAULT_BACKGROUND_COLOR = "#FFFFFF"

        /** Raw (pre-normalization) `border` shorthand seeded on Card. */
        private const val CARD_DEFAULT_BORDER_RAW = "1 solid $CARD_DEFAULT_BORDER_COLOR"

        /** Raw (pre-normalization) `boxShadow` shorthand seeded on Card. */
        private const val CARD_DEFAULT_BOX_SHADOW_RAW = "0 1 2 $CARD_DEFAULT_SHADOW_COLOR 0"
    }

    /** Map from string component ID to integer tag */
    private val idToTag = mutableMapOf<String, Int>()

    /** Map from tag to component standard name */
    private val tagToComponentName = mutableMapOf<Int, String>()

    /** Map from component ID to its current children IDs (for incremental updates) */
    private val idToChildren = mutableMapOf<String, List<String>>()

    /** Map from tag to action definition JSON string */
    private val tagToAction = mutableMapOf<Int, Any?>()

    /**
     * Tags whose action is currently suppressed because the component's checks
     * fail. Per A2UI v0.9: "Buttons can also define checks. If any check fails,
     * the button is automatically disabled." A disabled button must not dispatch
     * its action, which we enforce here at dispatch time (path-independent).
     */
    private val tagToActionDisabled = mutableMapOf<Int, Boolean>()

    /** Cache of child-to-parent relationships for the current updateComponents batch */
    private var childToParentCache = mutableMapOf<String, String>()

    /** Cache of component variant values for the current updateComponents batch */
    private var componentVariantCache = mutableMapOf<String, String>()

    /** Map from component ID to its original component definition (for re-evaluation on data update) */
    private val idToOriginalDef = mutableMapOf<String, Map<String, Any?>>()

    /** Map from data key to set of component IDs that have function calls depending on that key */
    private val dataKeyToFuncDependents = mutableMapOf<String, MutableSet<String>>()

    /** Map from data key to set of component IDs that have path references depending on that key */
    private val dataKeyToPathDependents = mutableMapOf<String, MutableSet<String>>()

    /** Map from component ID to its checks definitions (raw JSON objects) */
    private val checksRegistry = mutableMapOf<String, List<Map<String, Any?>>>()

    /** Map from data key to set of component IDs that have checks depending on that key */
    private val dataKeyToChecksDependents = mutableMapOf<String, MutableSet<String>>()

    /** Map from component ID to its check error text view tag */
    private val idToCheckErrorTag = mutableMapOf<String, Int>()

    /** Map from component ID to its check wrapper Column tag */
    private val idToCheckWrapperTag = mutableMapOf<String, Int>()

    /**
     * Map from component ID to the tag of the persistent label/title Text rendered ABOVE
     * the component (TextField/DateTimeInput `label`, AudioPlayer `description`). Used to
     * refresh the label when its bound data path arrives/changes (the label is not a
     * normal component with its own id, so it is updated explicitly here).
     */
    private val idToFieldLabelTag = mutableMapOf<String, Int>()

    /** Map from component ID to the original label/title spec (literal or data binding) for [idToFieldLabelTag]. */
    private val idToFieldLabelSpec = mutableMapOf<String, Any?>()

    /** ChoicePicker: map from component ID to list of chip container tags */
    private val choicePickerChipTags = mutableMapOf<String, List<Int>>()

    /** ChoicePicker: map from component ID to list of chip option values (parallel to chipTags) */
    private val choicePickerChipValues = mutableMapOf<String, List<String>>()

    /** ChoicePicker: map from component ID to list of chip option labels (parallel to chipTags) */
    private val choicePickerChipLabels = mutableMapOf<String, List<String>>()

    /** ChoicePicker: map from component ID to displayStyle ("chips" or "checkbox") */
    private val choicePickerDisplayStyles = mutableMapOf<String, String>()

    /** ChoicePicker: map from component ID to variant ("mutuallyExclusive" or "multipleSelection") */
    private val choicePickerVariants = mutableMapOf<String, String>()

    /** ChoicePicker: map from component ID to the filter input tag (when filterable=true) */
    private val choicePickerFilterInputTag = mutableMapOf<String, Int>()

    /** Tabs: map from Tabs component ID to its tab definitions (title + child ID) */
    private val tabsDefinitions = mutableMapOf<String, List<TabDefinition>>()

    /** Tabs: map from Tabs component ID to the currently selected tab index */
    private val tabsSelectedIndex = mutableMapOf<String, Int>()

    /** Tabs: map from Tabs component ID to list of tab header button tags */
    private val tabsHeaderTags = mutableMapOf<String, List<Int>>()

    /** Tabs: map from Tabs component ID to list of content wrapper tags (one per tab) */
    private val tabsContentWrapperTags = mutableMapOf<String, List<Int>>()

    /** Modal: map from Modal component ID to its definition (trigger + content IDs) */
    private val modalDefinitions = mutableMapOf<String, ModalDefinition>()

    /** Modal: map from trigger component ID to the Modal component ID it opens */
    private val modalTriggerToId = mutableMapOf<String, String>()

    /** Modal: set of component IDs (the content subtree) deferred until the modal opens */
    private val modalDeferredIds = mutableSetOf<String>()

    /** Modal: currently-open Modal component IDs */
    private val modalOpenState = mutableSetOf<String>()

    /**
     * Modal: map from Modal ID to the list of (tag, parentTag) created for its open
     * overlay, in creation order. Used to tear the overlay down (children first) on close.
     */
    private val modalOverlayTags = mutableMapOf<String, MutableList<Pair<Int, Int>>>()

    /** ChildList template bindings: parentId -> ChildListBinding(dataPath, templateId) */
    private val childListBindings = mutableMapOf<String, ChildListBinding>()

    /** Map from generated instance ID to its relative data scope path (e.g., "/events/0") */
    private val instanceScopePath = mutableMapOf<String, String>()

    /** Next available tag */
    private var nextTag = 1

    /** Data model for resolving data bindings */
    private val dataModel = mutableMapOf<String, Any?>()

    /** Current surface ID */
    private var currentSurfaceId: String = ""

    /** Theme primary color (default per protocol spec: #00BFFF) */
    private var primaryColor: String = "#00BFFF"

    /** Whether to send data model changes back to the host (from createSurface.sendDataModel) */
    var sendDataModel: Boolean = false
        private set

    /**
     * Bridge callback used to invoke user-registered custom A2UI functions on the
     * native (platform) side. Set by [com.tencent.kuikly.a2ui.dsl.A2UIDSLBridge].
     *
     * Contract:
     *   - `name`     : the function name (already looked up in the registry by the caller)
     *   - `argsJson` : JSON string of the (resolved) arguments
     *   - `spec`     : wire-format descriptor "returnType|invokeMode"
     *
     * The invoker must return synchronously for non-void return types. For void
     * functions the return value is ignored. When the invoker is `null` (e.g. no
     * platform hooked up) any custom function evaluates to `null` and callers
     * fall back to a safe default.
     */
    internal var customFunctionInvoker: ((name: String, argsJson: String, spec: String) -> Any?)? = null

    /**
     * Split a data-model path expression into its atomic segments.
     *
     * A2UI JSON Pointer paths use `/` as the segment separator, but this project also
     * accepts an extended flatten form where `.` acts as a field separator and `[N]`
     * denotes a numeric array index (e.g. `chart.items[0].label`). Both forms may be
     * mixed. Empty segments (produced by leading slashes such as `/foo/bar`) are
     * dropped so callers can pass the raw path with or without a leading `/`.
     *
     * Examples:
     *   "stat1/value"           -> ["stat1", "value"]
     *   "chart.items[0].label"  -> ["chart", "items", "0", "label"]
     *   "/event/location/0"     -> ["event", "location", "0"]
     */
    private fun parsePathSegments(path: String): List<String> {
        if (path.isEmpty()) return emptyList()
        val out = mutableListOf<String>()
        val buf = StringBuilder()
        val flush = {
            if (buf.isNotEmpty()) {
                out.add(buf.toString())
                buf.clear()
            }
        }
        var i = 0
        while (i < path.length) {
            when (val c = path[i]) {
                '/', '.' -> {
                    flush()
                    i++
                }
                '[' -> {
                    flush()
                    val end = path.indexOf(']', i + 1)
                    if (end < 0) {
                        // Malformed - treat rest as literal to avoid data loss
                        buf.append(path.substring(i))
                        i = path.length
                    } else {
                        val idx = path.substring(i + 1, end).trim()
                        if (idx.isNotEmpty()) out.add(idx)
                        i = end + 1
                    }
                }
                else -> {
                    buf.append(c)
                    i++
                }
            }
        }
        flush()
        return out
    }

    /**
     * Resolve a nested data path from the data model.
     * Supports multi-segment paths like "stat1/value" or "chart.items[0].label" by
     * traversing nested Maps and Lists. Numeric segments index into Lists (per A2UI
     * path syntax, e.g. "/event/location/0" or "chart.items[0]").
     *
     * As a fallback for backward compatibility, if the raw [key] exists as-is in the
     * top-level data model (e.g. legacy flatten storage), it is returned directly.
     */
    @Suppress("UNCHECKED_CAST")
    private fun resolveNestedDataPath(key: String): Any? {
        // Fast path & backward compatibility: exact top-level key hit (e.g. flatten
        // key "chart.items[0].label" that was stored verbatim before normalization).
        if (dataModel.containsKey(key)) return dataModel[key]

        val segments = parsePathSegments(key)
        if (segments.isEmpty()) return null
        var current: Any? = dataModel[segments[0]]
        for (i in 1 until segments.size) {
            when (current) {
                null -> return null
                is Map<*, *> -> current = current[segments[i]]
                is List<*> -> {
                    val index = segments[i].toIntOrNull() ?: return null
                    current = current.getOrNull(index)
                }
                else -> return null
            }
        }
        return current
    }

    /**
     * Write [value] into the data model at [key], creating/traversing nested Maps
     * and Lists as needed. Supports both slash-separated JSON Pointer paths (e.g.
     * "formData/email") and the extended flatten form with `.`/`[N]` (e.g.
     * "chart.items[0].label"). This is the inverse of [resolveNestedDataPath]: a
     * value written here can be read back through it.
     *
     * Intermediate Maps parsed from JSON may be read-only, so they are copied into
     * MutableMaps along the path. A null [value] removes the leaf key.
     */
    private fun setNestedDataPath(key: String, value: Any?) {
        val segments = parsePathSegments(key)
        if (segments.isEmpty()) return
        if (segments.size == 1) {
            val seg = segments[0]
            if (value != null) dataModel[seg] = value else dataModel.remove(seg)
            return
        }

        // Walk/create the path down to the parent of the leaf segment. At each
        // step the container's shape is decided by the *next* segment: a numeric
        // segment implies a List, otherwise a Map.
        var parentContainer: Any = dataModel
        var parentKey: Any = segments[0] // Either String (Map key) or Int (List index)
        for (i in 0 until segments.size - 1) {
            val currentSeg = segments[i]
            val nextSeg = segments[i + 1]
            val nextIsIndex = nextSeg.toIntOrNull() != null
            val currentIndex = currentSeg.toIntOrNull()

            @Suppress("UNCHECKED_CAST")
            val existing: Any? = when (parentContainer) {
                is MutableMap<*, *> -> (parentContainer as MutableMap<String, Any?>)[currentSeg]
                is MutableList<*> -> {
                    val idx = currentIndex ?: return
                    (parentContainer as MutableList<Any?>).getOrNull(idx)
                }
                else -> return
            }

            val child: Any = if (nextIsIndex) {
                // Ensure child is a MutableList
                when (existing) {
                    is MutableList<*> -> existing
                    is List<*> -> existing.toMutableList()
                    else -> mutableListOf<Any?>()
                }
            } else {
                // Ensure child is a MutableMap
                @Suppress("UNCHECKED_CAST")
                when (existing) {
                    is MutableMap<*, *> -> existing as MutableMap<String, Any?>
                    is Map<*, *> -> (existing as Map<String, Any?>).toMutableMap()
                    else -> mutableMapOf<String, Any?>()
                }
            }

            // Store child back into parent (may replace read-only or absent value)
            @Suppress("UNCHECKED_CAST")
            when (parentContainer) {
                is MutableMap<*, *> -> (parentContainer as MutableMap<String, Any?>)[currentSeg] = child
                is MutableList<*> -> {
                    val list = parentContainer as MutableList<Any?>
                    val idx = currentIndex ?: return
                    while (list.size <= idx) list.add(null)
                    list[idx] = child
                }
            }

            parentContainer = child
            parentKey = if (nextIsIndex) nextSeg.toInt() else nextSeg
        }

        // Assign the leaf
        @Suppress("UNCHECKED_CAST")
        when (parentContainer) {
            is MutableMap<*, *> -> {
                val m = parentContainer as MutableMap<String, Any?>
                val leafKey = parentKey as String
                if (value != null) m[leafKey] = value else m.remove(leafKey)
            }
            is MutableList<*> -> {
                val list = parentContainer as MutableList<Any?>
                val idx = parentKey as Int
                while (list.size <= idx) list.add(null)
                list[idx] = value
            }
        }
    }

    /**
     * Get the root key (first segment) from a potentially nested path.
     * Used for dependency tracking - when "stat1" changes, "stat1/value" also changes.
     * Recognises `/`, `.` and `[` as segment boundaries so flatten paths like
     * `chart.items[0].label` still map back to the root key `chart`.
     */
    private fun getRootDataKey(key: String): String {
        var end = key.length
        for (i in key.indices) {
            val c = key[i]
            if (c == '/' || c == '.' || c == '[') {
                end = i
                break
            }
        }
        return if (end == 0) key else key.substring(0, end)
    }

    /**
     * Convert a standard A2UI 0.9 protocol message (as parsed JSON map) into
     * a list of internal A2UIMessage objects.
     */
    fun convertMessage(rawMessage: Map<String, Any?>): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()

        // Check for createSurface
        val createSurface = rawMessage["createSurface"]
        if (createSurface is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            messages.addAll(handleCreateSurface(createSurface as Map<String, Any?>))
        }

        // Check for updateComponents
        val updateComponents = rawMessage["updateComponents"]
        if (updateComponents is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            messages.addAll(handleUpdateComponents(updateComponents as Map<String, Any?>))
        }

        // Check for updateDataModel
        val updateDataModel = rawMessage["updateDataModel"]
        if (updateDataModel is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            messages.addAll(handleUpdateDataModel(updateDataModel as Map<String, Any?>))
        }

        return messages
    }

    /**
     * Convert a list of standard A2UI 0.9 protocol messages.
     * Automatically appends a streamEnd message at the end.
     */
    fun convertMessages(rawMessages: List<Map<String, Any?>>): List<A2UIMessage> {
        val allMessages = mutableListOf<A2UIMessage>()
        for (rawMessage in rawMessages) {
            allMessages.addAll(convertMessage(rawMessage))
        }
        // Append stream end
        if (currentSurfaceId.isNotEmpty()) {
            allMessages.add(A2UIMessage(
                type = A2UIMessageType.STREAM_END,
                surfaceId = currentSurfaceId
            ))
        }
        return allMessages
    }

    /**
     * Reset adapter state.
     */
    fun reset() {
        idToTag.clear()
        tagToComponentName.clear()
        tagToAction.clear()
        tagToActionDisabled.clear()
        dataModel.clear()
        childListBindings.clear()
        instanceScopePath.clear()
        checksRegistry.clear()
        dataKeyToChecksDependents.clear()
        nextTag = 1
        currentSurfaceId = ""
        sendDataModel = false
    }

    /**
     * Get the action definition JSON for a given tag.
     * Returns null if no action is defined for this tag.
     */
    fun getActionForTag(tag: Int): String? {
        // A failing-checks button is disabled per the A2UI spec: swallow the tap so
        // no action is dispatched (the native render path can't rely on touchEnable).
        if (tagToActionDisabled[tag] == true) return null
        val action = tagToAction[tag] ?: return null
        // Resolve any {"path": "/..."} references (e.g. context.data) against the
        // current data model before handing the action JSON to the host, so the
        // host receives the live form values rather than the raw path reference.
        return serializeToJson(resolveActionValue(action))
    }

    /**
     * Recursively resolve DynamicValue references within an action payload before
     * the action JSON is handed to the host, so the host receives concrete values
     * (per A2UI protocol §Action: context values are DynamicValues, i.e. literal /
     * path reference / FunctionCall).
     *
     * - `{"path": "/..."}`               → value read from the current data model
     * - `{"call": "...", "args": {...}}` → evaluated function result (formatDate,
     *                                      formatNumber, formatCurrency, formatString,
     *                                      pluralize). Falls back to the original map
     *                                      when the call is unsupported so we don't
     *                                      silently drop non-formatter FunctionCalls.
     * - other Maps / Lists               → walked recursively in place
     */
    private fun resolveActionValue(obj: Any?): Any? {
        return when (obj) {
            is Map<*, *> -> {
                val path = obj["path"] as? String
                if (path != null && obj.size == 1) {
                    return resolveNestedDataPath(path.removePrefix("/"))
                }
                val call = obj["call"] as? String
                if (call != null) {
                    val evaluated = evaluateFunctionCall(obj)
                    if (evaluated != null) {
                        return evaluated
                    }
                    // Unsupported call: keep original map so host can decide.
                }
                obj.entries.associate { (k, v) -> k.toString() to resolveActionValue(v) }
            }
            is List<*> -> obj.map { resolveActionValue(it) }
            else -> obj
        }
    }

    /**
     * Props for an inline check-message Text: visible (auto height) when there is
     * an error message, collapsed (zero height / transparent) when checks pass.
     */
    private fun checkErrorTextProps(errorMessage: String?): Map<String, Any?> {
        return if (errorMessage != null) {
            mapOf("text" to errorMessage, "height" to Float.NaN, "opacity" to 1f)
        } else {
            mapOf("text" to "", "height" to 0f, "opacity" to 0f)
        }
    }

    /**
     * Get all tags that have actions defined (for debugging).
     */
    fun getActionTags(): Set<Int> {
        return tagToAction.keys
    }

    /**
     * Get a JSON snapshot of the current data model.
     * Used by sendDataModel to notify the host of data model changes.
     *
     * @return JSON string representing the current data model
     */
    fun getDataModelSnapshot(): String {
        return buildJsonString(dataModel)
    }

    /**
     * Update a single data model value (for two-way binding).
     * Updates the local data model and returns UPDATE_COMPONENT messages
     * for all components that depend on the changed key.
     *
     * @param key The data model key (without leading "/")
     * @param value The new value
     * @return List of messages to apply to the component tree
     */
    fun updateDataModelValue(key: String, value: Any?): List<A2UIMessage> {
        // Update local data model. For nested keys (e.g. "formData/email") write
        // into the nested Map structure so resolveNestedDataPath() reads the new
        // value back. Writing a flat "formData/email" key instead would leave the
        // nested dataModel["formData"]["email"] stale, which breaks checks like the
        // email validator (it would keep reading the initial empty string).
        setNestedDataPath(key, value)

        // Re-evaluate bindings that depend on this key. Path dependents are registered
        // under the ROOT data key (getRootDataKey), e.g. a Text bound to "/event/name" is
        // tracked under "event", so a change to the nested key "event/name" must also look
        // up the root key. Without this, sibling components bound to the same object (e.g.
        // a preview Text mirroring a TextField's "/event/name") never re-render.
        val messages = mutableListOf<A2UIMessage>()
        val affectedComponentIds = mutableSetOf<String>()
        val lookupKeys = setOf(key, getRootDataKey(key))
        for (lookupKey in lookupKeys) {
            dataKeyToFuncDependents[lookupKey]?.let { affectedComponentIds.addAll(it) }
            dataKeyToPathDependents[lookupKey]?.let { ids ->
                affectedComponentIds.addAll(ids.filter { !it.startsWith("__childlist__") })
            }
        }

        for (compId in affectedComponentIds) {
            val originalDef = idToOriginalDef[compId] ?: continue
            val tag = idToTag[compId] ?: continue
            val componentType = originalDef["component"] as? String ?: continue

            // Special handling for ChoicePicker: re-render options with new selection state
            if (componentType == "ChoicePicker" && choicePickerChipTags.containsKey(compId)) {
                val bindingPath = getChoicePickerBindingPath(compId)
                if (bindingPath != null) {
                    val selectedValues = when (val raw = resolveNestedDataPath(bindingPath)) {
                        is List<*> -> raw.map { it.toString() }.toSet()
                        is String -> setOf(raw)
                        else -> emptySet()
                    }
                    val chipTags = choicePickerChipTags[compId] ?: continue
                    val chipValues = choicePickerChipValues[compId] ?: continue
                    val cpDisplayStyle = choicePickerDisplayStyles[compId] ?: "chips"
                    val cpVariant = choicePickerVariants[compId] ?: "mutuallyExclusive"
                    for (i in chipTags.indices) {
                        val chipTag = chipTags[i]
                        val chipValue = chipValues[i]
                        val isSelected = chipValue in selectedValues
                        if (cpDisplayStyle == "checkbox") {
                            // Update icon
                            val icon = getCheckboxIcon(isSelected, cpVariant)
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = currentSurfaceId,
                                tag = chipTag + 1,
                                viewName = "KRRichTextView",
                                props = mapOf(
                                    "text" to icon,
                                    "color" to if (isSelected) primaryColor else "#999999"
                                )
                            ))
                        } else {
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = currentSurfaceId,
                                tag = chipTag,
                                viewName = "KRView",
                                props = if (isSelected) mapOf(
                                    "backgroundColor" to primaryColor,
                                    "border" to normalizeHexInShorthand("0 solid #00000000")
                                ) else mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "border" to normalizeHexInShorthand("1 solid #CCCCCC")
                                )
                            ))
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = currentSurfaceId,
                                tag = chipTag + 1,
                                viewName = "KRRichTextView",
                                props = mapOf("color" to if (isSelected) "#FFFFFF" else "#333333")
                            ))
                        }
                    }
                }
                continue
            }

            // Special handling for Tabs: re-resolve bound tab titles and update header text
            if (componentType == "Tabs" && tabsDefinitions.containsKey(compId)) {
                val tabDefs = tabsDefinitions[compId]!!
                val headerTags = tabsHeaderTags[compId]
                val selectedIndex = tabsSelectedIndex[compId] ?: 0
                if (headerTags != null) {
                    for ((tabIndex, tabDef) in tabDefs.withIndex()) {
                        // Only re-resolve if the title has a binding spec (Map with path)
                        if (tabDef.titleSpec is Map<*, *>) {
                            val newTitle = resolveLabelString(tabDef.titleSpec, compId).ifEmpty { "Tab ${tabIndex + 1}" }
                            val isSelected = tabIndex == selectedIndex
                            val headerTag = headerTags[tabIndex]
                            // Text tag is always headerTag + 1 (see switchTab / creation logic)
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = currentSurfaceId,
                                tag = headerTag + 1,
                                viewName = "KRRichTextView",
                                props = buildTabHeaderTextProps(newTitle, isSelected)
                            ))
                        }
                    }
                }
                continue
            }

            val viewName = if (componentType == "TextField") {
                val variant = originalDef["variant"] as? String
                A2UIComponentMapper.mapTextFieldViewName(variant)
            } else {
                A2UIComponentMapper.mapComponentName(componentType)
            }

            val updatedProps = mutableMapOf<String, Any?>()
            val skipKeys = setOf("id", "component", "children", "child", "accessibility", "action", "checks", "tabs")
            for ((propKey, propValue) in originalDef) {
                if (propKey in skipKeys) continue
                // Kuikly `styles` extension: re-expand on data-model change so
                // path-bound style values (e.g. `styles.color = {path: "/theme/color"}`)
                // refresh in place.
                if (propKey == "styles" && propValue is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    for ((sk, sv) in A2UIComponentMapper.expandStyles(componentType, propValue as Map<String, Any?>)) {
                        updatedProps[sk] = resolveValue(sv, compId)
                    }
                    clearDefaultsIfGradient(updatedProps)
                    continue
                }
                val mapped = A2UIComponentMapper.mapProp(componentType, propKey, propValue)
                if (mapped != null) {
                    val (mappedKey, mappedValue) = mapped
                    if (mappedKey.isNotEmpty()) {
                        updatedProps[mappedKey] = resolveValue(mappedValue, compId)
                    }
                }
            }

            if (updatedProps.isNotEmpty()) {
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = currentSurfaceId,
                    tag = tag,
                    viewName = viewName,
                    props = updatedProps
                ))
            }
        }

        // Re-evaluate checks for components that depend on this data key
        val checksAffectedIds = mutableSetOf<String>()
        dataKeyToChecksDependents[key]?.let { checksAffectedIds.addAll(it) }
        // Also re-evaluate checks for components already in affectedComponentIds
        // (their data changed, so their checks may also need re-evaluation)
        for (compId in affectedComponentIds) {
            if (checksRegistry.containsKey(compId)) {
                checksAffectedIds.add(compId)
            }
        }

        for (compId in checksAffectedIds) {
            val originalDef = idToOriginalDef[compId] ?: continue
            val componentType = originalDef["component"] as? String ?: continue
            val errorMessage = evaluateChecksForComponent(compId)

            if (componentType == "Button") {
                // Button: toggle the dispatch guard, dim the button, and show/hide
                // the inline hint text. Per A2UI spec a failing button is disabled.
                val btnTag = idToTag[compId]
                if (btnTag != null) tagToActionDisabled[btnTag] = errorMessage != null
                idToCheckErrorTag[compId]?.let { errorTag ->
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = currentSurfaceId,
                        tag = errorTag,
                        viewName = "KRRichTextView",
                        props = checkErrorTextProps(errorMessage)
                    ))
                }
                if (btnTag != null) {
                    val disabled = errorMessage != null
                    val visualProps = mapOf<String, Any?>(
                        "opacity" to if (disabled) 0.4f else 1.0f,
                        "touchEnable" to if (disabled) 0 else 1
                    )
                    val existingMsg = messages.find { it.tag == btnTag && it.type == A2UIMessageType.UPDATE_COMPONENT }
                    if (existingMsg != null) {
                        val mutableProps = existingMsg.props?.toMutableMap() ?: mutableMapOf()
                        mutableProps.putAll(visualProps)
                        val idx = messages.indexOf(existingMsg)
                        messages[idx] = existingMsg.copy(props = mutableProps)
                    } else {
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.UPDATE_COMPONENT,
                            surfaceId = currentSurfaceId,
                            tag = btnTag,
                            viewName = A2UIComponentMapper.mapComponentName(componentType),
                            props = visualProps
                        ))
                    }
                }
            } else {
                // TextField/Input: update the error text component and border
                val errorTag = idToCheckErrorTag[compId]
                val compTag = idToTag[compId]
                if (errorTag != null) {
                    // Update error text component
                    val errorProps = mutableMapOf<String, Any?>()
                    if (errorMessage != null) {
                        errorProps["text"] = errorMessage
                        errorProps["height"] = Float.NaN // auto height
                        errorProps["opacity"] = 1f
                    } else {
                        errorProps["text"] = ""
                        errorProps["height"] = 0f
                        errorProps["opacity"] = 0f
                    }
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = currentSurfaceId,
                        tag = errorTag,
                        viewName = "KRRichTextView",
                        props = errorProps
                    ))
                }
                // Update TextField border
                if (compTag != null) {
                    if (compId in affectedComponentIds) {
                        val existingMsg = messages.find { it.tag == compTag && it.type == A2UIMessageType.UPDATE_COMPONENT }
                        if (existingMsg != null) {
                            val mutableProps = existingMsg.props?.toMutableMap() ?: mutableMapOf()
                mutableProps["border"] = if (errorMessage != null) normalizeHexInShorthand("1 solid #D32F2F") else normalizeHexInShorthand("1 solid #CCCCCC")
                            mutableProps.remove("__checkError") // No longer needed
                            val idx = messages.indexOf(existingMsg)
                            messages[idx] = existingMsg.copy(props = mutableProps)
                        }
                    } else {
                        val variant = originalDef["variant"] as? String
                        val viewName = if (componentType == "TextField") {
                            A2UIComponentMapper.mapTextFieldViewName(variant)
                        } else {
                            A2UIComponentMapper.mapComponentName(componentType)
                        }
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.UPDATE_COMPONENT,
                            surfaceId = currentSurfaceId,
                            tag = compTag,
                            viewName = viewName,
                            props = mapOf("border" to if (errorMessage != null) normalizeHexInShorthand("1 solid #D32F2F") else normalizeHexInShorthand("1 solid #CCCCCC"))
                        ))
                    }
                }
            }
        }

        return messages
    }
    // ---- Private handlers ----

    private fun handleCreateSurface(data: Map<String, Any?>): List<A2UIMessage> {
        val surfaceId = data["surfaceId"] as? String ?: return emptyList()
        currentSurfaceId = surfaceId

        // Parse sendDataModel flag (default false)
        sendDataModel = data["sendDataModel"] as? Boolean ?: false

        // Parse theme if provided
        @Suppress("UNCHECKED_CAST")
        val theme = data["theme"] as? Map<String, Any?>
        if (theme != null) {
            val themeColor = theme["primaryColor"] as? String
            if (themeColor != null && themeColor.matches(Regex("^#[0-9a-fA-F]{6}$"))) {
                primaryColor = themeColor
            }
        }

        return listOf(A2UIMessage(
            type = A2UIMessageType.CREATE_SURFACE,
            surfaceId = surfaceId,
            config = A2UISurfaceConfig(
                width = 0f, // Will be set by the platform layer
                height = 0f,
                incrementalRender = true
            )
        ))
    }

    private fun handleUpdateComponents(data: Map<String, Any?>): List<A2UIMessage> {
        val surfaceId = data["surfaceId"] as? String ?: currentSurfaceId
        val components = data["components"] as? List<*> ?: return emptyList()

        val messages = mutableListOf<A2UIMessage>()

        // First pass: assign tags to all components, track which are new
        val newIds = mutableSetOf<String>()
        for (comp in components) {
            if (comp is Map<*, *>) {
                val id = comp["id"] as? String ?: continue
                if (!idToTag.containsKey(id)) {
                    idToTag[id] = nextTag++
                    newIds.add(id)
                }
            }
        }

        // Second pass: build parent-child relationships and create messages
        // We need to determine parent from "children"/"child" references
        val childToParent = mutableMapOf<String, String>()
        val childOrder = mutableMapOf<String, Int>()
        // Update the class-level cache for use in buildProps
        childToParentCache.clear()
        componentVariantCache.clear()

        for (comp in components) {
            if (comp is Map<*, *>) {
                val id = comp["id"] as? String ?: continue
                val children = comp["children"]
                val child = comp["child"] as? String

                if (children is List<*>) {
                    children.forEachIndexed { index, childId ->
                        if (childId is String) {
                            childToParent[childId] = id
                            childOrder[childId] = index
                        }
                    }
                } else if (children is Map<*, *>) {
                    // ChildList template binding: {"path": "/events", "componentId": "event-template"}
                    val dataPath = children["path"] as? String
                    val templateId = children["componentId"] as? String
                    if (dataPath != null && templateId != null) {
                        childListBindings[id] = ChildListBinding(dataPath, templateId)
                        // Register dependency so we get notified when this data changes
                        val dataKey = dataPath.removePrefix("/")
                        dataKeyToPathDependents.getOrPut(dataKey) { mutableSetOf() }.add("__childlist__$id")
                    }
                }
                if (child != null) {
                    childToParent[child] = id
                    childOrder[child] = 0
                }

                // Handle Tabs component: extract child IDs from "tabs" array
                val tabsArray = comp["tabs"] as? List<*>
                if (tabsArray != null && comp["component"] == "Tabs") {
                    val tabDefs = mutableListOf<TabDefinition>()
                    tabsArray.forEachIndexed { index, tab ->
                        if (tab is Map<*, *>) {
                            val titleSpec = tab["title"]
                            // Resolve title: supports plain String or data binding {"path": "/key"}
                            val title = resolveLabelString(titleSpec, id).ifEmpty { "Tab ${index + 1}" }
                            val tabChild = tab["child"] as? String
                            if (tabChild != null) {
                                childToParent[tabChild] = id
                                childOrder[tabChild] = index
                                tabDefs.add(TabDefinition(title, tabChild, titleSpec))
                            }
                        }
                    }
                    if (tabDefs.isNotEmpty()) {
                        tabsDefinitions[id] = tabDefs
                        if (!tabsSelectedIndex.containsKey(id)) {
                            tabsSelectedIndex[id] = 0
                        }
                    }
                }

                // Handle Modal component: record trigger + content references.
                // Per spec the Modal renders nothing itself; only the trigger is shown
                // inline and tapping it opens a dialog with the content. Trigger adoption
                // and content deferral happen after childToParent is fully built (below).
                if (comp["component"] == "Modal") {
                    val triggerId = comp["trigger"] as? String
                    val contentId = comp["content"] as? String
                    if (triggerId != null && contentId != null) {
                        modalDefinitions[id] = ModalDefinition(triggerId, contentId)
                        modalTriggerToId[triggerId] = id
                    }
                }
            }
        }
        // Sync to class-level cache for use in buildProps
        childToParentCache = childToParent.toMutableMap()

        // Cache variant values for all components
        for (comp in components) {
            if (comp is Map<*, *>) {
                val compId = comp["id"] as? String ?: continue
                val variant = comp["variant"] as? String
                if (variant != null) {
                    componentVariantCache[compId] = variant
                }
            }
        }

        // Third pass: topological sort to ensure parents are processed before children
        // Build a map from id to component data for quick lookup
        val idToComp = mutableMapOf<String, Map<String, Any?>>()
        for (comp in components) {
            if (comp is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val compMap = comp as Map<String, Any?>
                val id = compMap["id"] as? String ?: continue
                idToComp[id] = compMap
                // Store original definition for ALL components (including templates)
                // so they are available for ChildList expansion and data re-evaluation
                idToOriginalDef[id] = compMap
            }
        }

        // Modal: finalize trigger/content wiring now that childToParent is complete.
        for ((modalId, modalDef) in modalDefinitions) {
            // If the trigger is not placed anywhere else in the tree (e.g. it is only
            // referenced by the Modal, as in the canonical modal example), the Modal
            // adopts it into its own slot so the trigger is rendered inline.
            // If the trigger is already a child of another container (as in the movie
            // card, where it lives inside a Column), we leave it in place.
            if (childToParent[modalDef.triggerId] == null && modalDef.triggerId in idToComp) {
                val modalParent = childToParent[modalId]
                if (modalParent != null) {
                    childToParent[modalDef.triggerId] = modalParent
                    childOrder[modalDef.triggerId] = childOrder[modalId] ?: 0
                }
            }
            // The content subtree is created lazily when the modal opens, so exclude it
            // (and its descendants) from the initial component creation.
            collectTemplateTree(modalDef.contentId, idToComp, childToParent, modalDeferredIds)
        }
        childToParentCache = childToParent.toMutableMap()

        // Topological sort: process parents before children, AND emit siblings strictly in
        // their `children`-array order (childOrder). The order in which components are listed
        // in the `components` array must NOT affect layout order (per A2UI spec, child order
        // is defined solely by the parent's `children`/`child` reference). A preorder DFS that
        // follows children sorted by childOrder guarantees each CREATE message is emitted with
        // a sibling index that is contiguous at insertion time, avoiding out-of-range inserts.
        val sortedIds = mutableListOf<String>()
        val visited = mutableSetOf<String>()

        // Group direct children by parent, ordered by their childOrder (children-array index).
        val childrenByParent = mutableMapOf<String, MutableList<String>>()
        for (comp in components) {
            if (comp is Map<*, *>) {
                val id = comp["id"] as? String ?: continue
                val parentId = childToParent[id]
                if (parentId != null && parentId in idToComp) {
                    childrenByParent.getOrPut(parentId) { mutableListOf() }.add(id)
                }
            }
        }
        for ((_, kids) in childrenByParent) {
            kids.sortBy { childOrder[it] ?: 0 }
        }

        fun visit(id: String) {
            if (id in visited) return
            if (id !in idToComp) return
            visited.add(id)
            sortedIds.add(id) // parent before children (preorder)
            childrenByParent[id]?.forEach { visit(it) }
        }

        // Start from roots (components with no parent within this batch), in definition order.
        for (comp in components) {
            if (comp is Map<*, *>) {
                val id = comp["id"] as? String ?: continue
                val parentId = childToParent[id]
                if (parentId == null || parentId !in idToComp) {
                    visit(id)
                }
            }
        }
        // Catch any components not reachable from a root (orphans / cycles), in definition order.
        for (comp in components) {
            if (comp is Map<*, *>) {
                val id = comp["id"] as? String ?: continue
                visit(id)
            }
        }

        // Generate messages in topological order
        // First, collect template component IDs and their descendants (they should NOT be created as regular components)
        val templateIds = mutableSetOf<String>()
        for ((_, binding) in childListBindings) {
            collectTemplateTree(binding.templateId, idToComp, childToParent, templateIds)
        }

        for (id in sortedIds) {
            // Skip template components - they are only instantiated via ChildList expansion
            if (id in templateIds) continue
            // Skip Modal content subtrees - created lazily when the modal opens
            if (id in modalDeferredIds) continue

            val compMap = idToComp[id] ?: continue
            val componentType = compMap["component"] as? String ?: continue
            val tag = idToTag[id] ?: continue

            // The Modal component itself renders nothing inline (only its trigger is
            // shown, which is created as a normal component). Skip creating a view for
            // it so it doesn't leave an empty placeholder in the layout.
            if (componentType == "Modal") continue

            tagToComponentName[tag] = componentType

            val parentId = childToParent[id]
            var parentTag = if (parentId != null) idToTag[parentId] ?: -1 else -1
            var index = childOrder[id] ?: 0

            // For Tabs content children, redirect to the content wrapper tag
            if (parentId != null && tabsDefinitions.containsKey(parentId)) {
                val tabDefs = tabsDefinitions[parentId]!!
                val tabIndex = tabDefs.indexOfFirst { it.childId == id }
                if (tabIndex >= 0) {
                    val wrapperTags = tabsContentWrapperTags[parentId]
                    if (wrapperTags != null && tabIndex < wrapperTags.size) {
                        parentTag = wrapperTags[tabIndex]
                        index = 0 // Each content wrapper has only one child
                    }
                }
            }

            // Route to appropriate mapper based on component type
            val isKuiklyComponent = KuiklyComponentMapper.isKuiklyComponent(componentType)
            val viewName = if (isKuiklyComponent) {
                KuiklyComponentMapper.mapComponentName(componentType)
            } else if (componentType == "TextField") {
                // TextField viewName depends on variant: longText -> KRTextAreaView (multi-line)
                val variant = compMap["variant"] as? String
                A2UIComponentMapper.mapTextFieldViewName(variant)
            } else {
                A2UIComponentMapper.mapComponentName(componentType)
            }

            // Build props using the appropriate strategy
            val builtProps = if (isKuiklyComponent) {
                buildKuiklyProps(componentType, compMap)
            } else {
                buildProps(componentType, compMap)
            }
            // If this component is a Modal trigger, make it tappable and tag it so the
            // applier wires a click that opens the modal.
            val props = if (modalTriggerToId.containsKey(id)) {
                builtProps.toMutableMap().also {
                    it["__modalTriggerId"] = modalTriggerToId[id]
                    it["touchEnable"] = 1
                }
            } else {
                builtProps
            }

            if (id in newIds) {
                // A TextField/DateTimeInput renders its `label` as a persistent Text
                // ABOVE the input (per the A2UI reference renderer), and TextField/Button
                // checks render an error/hint Text BELOW. Either case wraps the component
                // in a Column: [label?] [input/button] [error?].
                val labelSpec: Any? = when (componentType) {
                    "TextField", "DateTimeInput", "Slider" -> compMap["label"]
                    // AudioPlayer renders its `description` as a persistent title above the player.
                    "AudioPlayer" -> compMap["description"]
                    else -> null
                }
                // Decide based on spec PRESENCE, not its resolved value: a data-bound label
                // (e.g. {"path": "/episodeTitle"}) often resolves to "" at build time because
                // the updateDataModel message arrives AFTER updateComponents. We still build
                // the wrapper now and refresh the label text once the data arrives.
                val hasLabel = when (labelSpec) {
                    null -> false
                    is String -> labelSpec.isNotEmpty()
                    else -> true
                }
                val fieldLabel = if (hasLabel) resolveLabelString(labelSpec, id) else ""
                val hasChecks = (compMap["checks"] != null || compMap["validationRegexp"] != null) &&
                    (componentType == "TextField" || componentType == "Button")
                val needsWrapper = hasChecks || hasLabel
                if (needsWrapper) {
                    // Create wrapper Column
                    val wrapperTag = nextTag++
                    idToCheckWrapperTag[id] = wrapperTag
                    tagToComponentName[wrapperTag] = "Column"

                    // Extract props that belong on the wrapper rather than the input.
                    val mutableProps = props.toMutableMap()
                    val errorMessage = mutableProps.remove("__checkError") as? String
                    mutableProps.remove("placeholder") // label is rendered as a separate Text
                    val flexValue = mutableProps.remove("flex")
                    val outerMargin = mutableProps.remove("margin")

                    // 1. Wrapper Column in place of the component
                    val wrapperProps = mutableMapOf<String, Any?>(
                        "alignSelf" to "stretch",
                        "flexDirection" to "column"
                    )
                    if (flexValue != null) wrapperProps["flex"] = flexValue
                    if (outerMargin != null) wrapperProps["margin"] = outerMargin
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = wrapperTag,
                        viewName = "KRView",
                        parentTag = parentTag,
                        index = index,
                        props = wrapperProps
                    ))

                    var childIndex = 0

                    // 2. Label/title Text above the input/player (TextField/DateTimeInput
                    //    `label`, AudioPlayer `description`). Created whenever a label spec
                    //    exists; its text may be empty now and is refreshed once the bound
                    //    data arrives (see handleUpdateDataModel + idToFieldLabelTag).
                    if (hasLabel) {
                        val labelTag = nextTag++
                        tagToComponentName[labelTag] = "Text"
                        idToFieldLabelTag[id] = labelTag
                        idToFieldLabelSpec[id] = labelSpec
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = labelTag,
                            viewName = "KRRichTextView",
                            parentTag = wrapperTag,
                            index = childIndex++,
                            props = mapOf(
                                "text" to fieldLabel,
                                "fontSize" to 14f,
                                "fontWeight" to "bold",
                                "color" to "#333333",
                                "marginBottom" to 4f,
                                "alignSelf" to "stretch"
                            )
                        ))
                    }

                    // 3. The component (input / button) itself
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = viewName,
                        parentTag = wrapperTag,
                        index = childIndex++,
                        props = mutableProps
                    ))

                    // 4. Error/hint Text below the component (only when there are checks)
                    if (hasChecks) {
                        val errorTag = nextTag++
                        idToCheckErrorTag[id] = errorTag
                        tagToComponentName[errorTag] = "Text"
                        val errorProps = mutableMapOf<String, Any?>(
                            "text" to (errorMessage ?: ""),
                            "fontSize" to 12f,
                            "color" to "#D32F2F",
                            "marginLeft" to 4f,
                            "marginTop" to 4f,
                            "alignSelf" to "stretch"
                        )
                        // Hide if no error
                        if (errorMessage == null) {
                            errorProps["height"] = 0f
                            errorProps["opacity"] = 0f
                        }
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = errorTag,
                            viewName = "KRRichTextView",
                            parentTag = wrapperTag,
                            index = childIndex++,
                            props = errorProps
                        ))
                    }
                } else if (componentType == "ChoicePicker") {
                    // ChoicePicker: expand into a container with chip or checkbox sub-components
                    val mutableProps = props.toMutableMap()
                    val options = mutableProps.remove("__choicePickerOptions") as? List<*> ?: emptyList<Any>()
                    val variant = mutableProps.remove("__choicePickerVariant") as? String ?: "mutuallyExclusive"
                    val displayStyle = mutableProps.remove("__choicePickerDisplayStyle") as? String ?: "chips"
                    val filterable = mutableProps.remove("__choicePickerFilterable") == true
                    val pickerLabel = resolveLabelString(mutableProps.remove("__choicePickerLabel"), id)
                    val bindingPath = mutableProps["__choicePickerBindingPath"] as? String
                    val directValue = mutableProps.remove("__choicePickerDirectValue") as? List<*>

                    // Resolve current selected values from data model or direct value
                    val selectedValues = if (bindingPath != null) {
                        when (val raw = resolveNestedDataPath(bindingPath)) {
                            is List<*> -> raw.map { it.toString() }.toSet()
                            is String -> setOf(raw)
                            else -> emptySet()
                        }
                    } else if (directValue != null) {
                        directValue.map { it.toString() }.toSet()
                    } else emptySet()

                    // When a top-level label is present (or filterable), wrap in a Column
                    // and render the label as a persistent Text above it.
                    var rowParentTag = parentTag
                    var rowIndex = index
                    var nextChildIndex = 0
                    val needsWrapper = pickerLabel.isNotEmpty() || filterable
                    var wrapperTag: Int? = null
                    if (needsWrapper) {
                        wrapperTag = nextTag++
                        tagToComponentName[wrapperTag!!] = "Column"
                        val outerMargin = mutableProps.remove("margin")
                        val wrapperProps = mutableMapOf<String, Any?>(
                            "alignSelf" to "stretch",
                            "flexDirection" to "column"
                        )
                        if (outerMargin != null) wrapperProps["margin"] = outerMargin
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = wrapperTag!!,
                            viewName = "KRView",
                            parentTag = parentTag,
                            index = index,
                            props = wrapperProps
                        ))

                        if (pickerLabel.isNotEmpty()) {
                            val labelTag = nextTag++
                            tagToComponentName[labelTag] = "Text"
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = labelTag,
                                viewName = "KRRichTextView",
                                parentTag = wrapperTag!!,
                                index = nextChildIndex++,
                                props = mapOf(
                                    "text" to pickerLabel,
                                    "fontSize" to 14f,
                                    "fontWeight" to "bold",
                                    "color" to "#333333",
                                    "marginBottom" to 4f,
                                    "alignSelf" to "stretch"
                                )
                            ))
                        }

                        // Filterable: add a search input above the options
                        if (filterable) {
                            val searchTag = nextTag++
                            tagToComponentName[searchTag] = "__ChoicePickerSearch"
                            choicePickerFilterInputTag[id] = searchTag
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = searchTag,
                                viewName = "KRTextFieldView",
                                parentTag = wrapperTag!!,
                                index = nextChildIndex++,
                                props = mapOf(
                                    "placeholder" to "",
                                    "fontSize" to 14f,
                                    "height" to 36f,
                                    "borderRadius" to 8f,
                                    "border" to normalizeHexInShorthand("1 solid #CCCCCC"),
                                    "paddingLeft" to 10f,
                                    "paddingRight" to 10f,
                                    "marginBottom" to 8f,
                                    "alignSelf" to "stretch",
                                    "__choicePickerFilterId" to id
                                )
                            ))
                        }

                        rowParentTag = wrapperTag!!
                        rowIndex = nextChildIndex
                    }

                    // Store chip/option tags for this ChoicePicker (for re-rendering on data change)
                    val chipTags = mutableListOf<Int>()
                    val chipOptionValues = mutableListOf<String>()
                    val chipOptionLabels = mutableListOf<String>()

                    // 1. Create the ChoicePicker container
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = "KRView",
                        parentTag = rowParentTag,
                        index = rowIndex,
                        props = mutableProps.filterKeys { !it.startsWith("__") }
                    ))

                    // 2. Create sub-components for each option
                    if (displayStyle == "checkbox") {
                        // Checkbox/Radio display: each option is a Row with icon + label
                        for ((optIndex, option) in options.withIndex()) {
                            if (option !is Map<*, *>) continue
                            val label = option["label"] as? String ?: continue
                            val optionValue = option["value"] as? String ?: continue
                            val isSelected = optionValue in selectedValues

                            // Create option row container
                            val chipTag = nextTag++
                            chipTags.add(chipTag)
                            chipOptionValues.add(optionValue)
                            chipOptionLabels.add(label)
                            tagToComponentName[chipTag] = "__ChoiceChip"

                            val rowProps = buildCheckboxOptionRowProps(isSelected)
                            rowProps["__choicePickerParentId"] = id
                            rowProps["__choiceOptionValue"] = optionValue
                            rowProps["__choicePickerVariant"] = variant

                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTag,
                                viewName = "KRView",
                                parentTag = tag,
                                index = optIndex,
                                props = rowProps
                            ))

                            // Create icon text (checkbox/radio symbol)
                            val iconTag = nextTag++
                            tagToComponentName[iconTag] = "__ChoiceCheckboxIcon"
                            val icon = getCheckboxIcon(isSelected, variant)
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = iconTag,
                                viewName = "KRRichTextView",
                                parentTag = chipTag,
                                index = 0,
                                props = mapOf(
                                    "text" to icon,
                                    "fontSize" to 18f,
                                    "color" to if (isSelected) primaryColor else "#999999",
                                    "marginRight" to 8f
                                )
                            ))

                            // Create label text
                            val chipTextTag = nextTag++
                            tagToComponentName[chipTextTag] = "__ChoiceChipText"
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTextTag,
                                viewName = "KRRichTextView",
                                parentTag = chipTag,
                                index = 1,
                                props = mapOf(
                                    "text" to label,
                                    "fontSize" to 14f,
                                    "color" to "#333333"
                                )
                            ))
                        }
                    } else {
                        // Chips display (default): pill-shaped chips
                        for ((chipIndex, option) in options.withIndex()) {
                            if (option !is Map<*, *>) continue
                            val label = option["label"] as? String ?: continue
                            val optionValue = option["value"] as? String ?: continue
                            val isSelected = optionValue in selectedValues

                            // Create chip container (KRView with border/background)
                            val chipTag = nextTag++
                            chipTags.add(chipTag)
                            chipOptionValues.add(optionValue)
                            chipOptionLabels.add(label)
                            tagToComponentName[chipTag] = "__ChoiceChip"

                            val chipProps = buildChoiceChipProps(isSelected)
                            chipProps["__choicePickerParentId"] = id
                            chipProps["__choiceOptionValue"] = optionValue
                            chipProps["__choicePickerVariant"] = variant

                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTag,
                                viewName = "KRView",
                                parentTag = tag,
                                index = chipIndex,
                                props = chipProps
                            ))

                            // Create chip text (KRRichTextView inside the chip)
                            val chipTextTag = nextTag++
                            tagToComponentName[chipTextTag] = "__ChoiceChipText"
                            val chipTextProps = buildChoiceChipTextProps(label, isSelected)

                            messages.add(A2UIMessage(
                                type = A2UIMessageType.CREATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTextTag,
                                viewName = "KRRichTextView",
                                parentTag = chipTag,
                                index = 0,
                                props = chipTextProps
                            ))
                        }
                    }

                    // Store chip info for re-rendering on data change
                    choicePickerChipTags[id] = chipTags
                    choicePickerChipValues[id] = chipOptionValues
                    choicePickerChipLabels[id] = chipOptionLabels
                    choicePickerDisplayStyles[id] = displayStyle
                    choicePickerVariants[id] = variant

                    // Register dependency tracking for the binding path
                    if (bindingPath != null) {
                        val rootKey = getRootDataKey(bindingPath)
                        dataKeyToPathDependents.getOrPut(rootKey) { mutableSetOf() }.add(id)
                    }
                } else if (componentType == "Tabs" && tabsDefinitions.containsKey(id)) {
                    // Tabs: expand into a Column with tab header row + content panels
                    val tabDefs = tabsDefinitions[id]!!
                    val selectedIndex = tabsSelectedIndex[id] ?: 0

                    // 1. Create the Tabs Column container
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = "KRView",
                        parentTag = parentTag,
                        index = index,
                        props = props
                    ))

                    // 2. Create tab header Row
                    val headerRowTag = nextTag++
                    tagToComponentName[headerRowTag] = "__TabsHeaderRow"
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = headerRowTag,
                        viewName = "KRView",
                        parentTag = tag,
                        index = 0,
                        props = mapOf(
                            "flexDirection" to "row",
                            "alignSelf" to "stretch",
                            "alignItems" to "center",
                            "paddingLeft" to 4f,
                            "paddingRight" to 4f
                        )
                    ))

                    // 3. Create tab header buttons
                    val headerTags = mutableListOf<Int>()
                    for ((tabIndex, tabDef) in tabDefs.withIndex()) {
                        val isSelected = tabIndex == selectedIndex
                        val tabHeaderTag = nextTag++
                        headerTags.add(tabHeaderTag)
                        tagToComponentName[tabHeaderTag] = "__TabHeader"

                        val headerProps = buildTabHeaderProps().toMutableMap()
                        headerProps["__tabsParentId"] = id
                        headerProps["__tabIndex"] = tabIndex

                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = tabHeaderTag,
                            viewName = "KRView",
                            parentTag = headerRowTag,
                            index = tabIndex,
                            props = headerProps
                        ))

                        // Tab header text (tag is always tabHeaderTag + 1, see switchTab)
                        val tabTextTag = nextTag++
                        tagToComponentName[tabTextTag] = "__TabHeaderText"
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = tabTextTag,
                            viewName = "KRRichTextView",
                            parentTag = tabHeaderTag,
                            index = 0,
                            props = buildTabHeaderTextProps(tabDef.title, isSelected)
                        ))

                        // Tab header underline indicator (tag is always tabHeaderTag + 2,
                        // see switchTab). Used in place of an unsupported `borderBottom`.
                        val tabUnderlineTag = nextTag++
                        tagToComponentName[tabUnderlineTag] = "__TabHeaderUnderline"
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = tabUnderlineTag,
                            viewName = "KRView",
                            parentTag = tabHeaderTag,
                            index = 1,
                            props = buildTabUnderlineProps(isSelected)
                        ))
                    }
                    tabsHeaderTags[id] = headerTags

                    // 4. Create content wrappers for each tab (only selected one visible)
                    val contentWrapperTags = mutableListOf<Int>()
                    for ((tabIndex, _) in tabDefs.withIndex()) {
                        val isSelected = tabIndex == selectedIndex
                        val wrapperTag = nextTag++
                        contentWrapperTags.add(wrapperTag)
                        tagToComponentName[wrapperTag] = "__TabContentWrapper"

                        val wrapperProps = mutableMapOf<String, Any?>(
                            "flexDirection" to "column",
                            "alignSelf" to "stretch",
                            "overflow" to "hidden"
                        )
                        if (isSelected) {
                            wrapperProps["flex"] = 1
                        } else {
                            wrapperProps["height"] = 0f
                        }

                        messages.add(A2UIMessage(
                            type = A2UIMessageType.CREATE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = wrapperTag,
                            viewName = "KRView",
                            parentTag = tag,
                            index = tabIndex + 1, // +1 because header row is at index 0
                            props = wrapperProps
                        ))
                    }
                    tabsContentWrapperTags[id] = contentWrapperTags
                } else if (componentType == "CheckBox" &&
                    resolveLabelString(compMap["label"], id).isNotEmpty()) {
                    // Per A2UI spec: CheckBox renders a native checkbox alongside a text
                    // label. Wrap the box + label in a Row so the label is visible. The
                    // checkbox keeps tag = idToTag[id] so click + two-way data binding work.
                    val labelText = resolveLabelString(compMap["label"], id)
                    val mutableProps = props.toMutableMap()
                    // Move the outer margin to the Row; keep the box itself tight.
                    val outerMargin = mutableProps.remove("margin")

                    // 1. Row wrapper in place of the checkbox
                    val rowTag = nextTag++
                    tagToComponentName[rowTag] = "Row"
                    val rowProps = mutableMapOf<String, Any?>(
                        "flexDirection" to "row",
                        "alignItems" to "center",
                        "alignSelf" to "stretch"
                    )
                    if (outerMargin != null) rowProps["margin"] = outerMargin
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = rowTag,
                        viewName = "KRView",
                        parentTag = parentTag,
                        index = index,
                        props = rowProps
                    ))

                    // 2. The checkbox itself (index 0)
                    mutableProps["marginRight"] = 8
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = viewName,
                        parentTag = rowTag,
                        index = 0,
                        props = mutableProps
                    ))

                    // 3. The label text (index 1)
                    val labelTag = nextTag++
                    tagToComponentName[labelTag] = "Text"
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = labelTag,
                        viewName = "KRRichTextView",
                        parentTag = rowTag,
                        index = 1,
                        props = mapOf(
                            "text" to labelText,
                            "fontSize" to 16f,
                            "color" to "#1A1A1A",
                            "flex" to 1
                        )
                    ))
                } else {
                    // Normal component: generate CREATE_COMPONENT
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.CREATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = viewName,
                        parentTag = parentTag,
                        index = index,
                        props = props
                    ))
                }
            } else {
                // Existing component: handle children changes first
                val newChildren = getChildrenIds(compMap)
                val oldChildren = idToChildren[id] ?: emptyList()

                // Remove old children that are no longer present
                val newChildrenSet = newChildren.toSet()
                for (oldChildId in oldChildren) {
                    if (oldChildId !in newChildrenSet) {
                        val oldChildTag = idToTag[oldChildId] ?: continue
                        messages.add(A2UIMessage(
                            type = A2UIMessageType.REMOVE_COMPONENT,
                            surfaceId = surfaceId,
                            tag = oldChildTag
                        ))
                    }
                }

                // Generate UPDATE_COMPONENT to update props
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = tag,
                    viewName = viewName,
                    parentTag = parentTag,
                    index = index,
                    props = props
                ))
            }

            // Track current children for this component
            val currentChildren = getChildrenIds(compMap)
            if (currentChildren.isNotEmpty()) {
                idToChildren[id] = currentChildren
            }
        }

        return messages
    }

    private fun handleUpdateDataModel(data: Map<String, Any?>): List<A2UIMessage> {
        val surfaceId = data["surfaceId"] as? String ?: currentSurfaceId
        val path = data["path"] as? String
        // Per A2UI protocol, `value` can be any JSON type (string / number / boolean /
        // array / object). Keep it as Any? here and only narrow to Map where the
        // full-replace semantics require it.
        val value: Any? = data["value"]
        @Suppress("UNCHECKED_CAST")
        val valueAsMap = value as? Map<String, Any?>

        if (value == null && path == null) return emptyList()

        // Per protocol: if path is omitted or "/", replace entire data model
        // If path is specified, update at that path
        if (path == null || path == "/") {
            // Replace entire data model — value must be a Map here.
            if (valueAsMap != null) {
                dataModel.clear()
                // A2UI extends flatten paths (e.g. "chart.items[0].label") in value
                // keys. If any key contains such syntax, unflatten it into nested
                // Map/List structure so downstream binding resolution works.
                val hasFlattenKey = valueAsMap.keys.any { k -> k.contains('.') || k.contains('[') }
                if (hasFlattenKey) {
                    for ((k, v) in valueAsMap) {
                        setNestedDataPath(k, v)
                    }
                } else {
                    dataModel.putAll(valueAsMap)
                }
            }
        } else {
            // Update at specific path (e.g., "/user/firstName" -> nested update).
            // value may be any JSON type; setNestedDataPath supports arbitrary leaf values.
            val key = path.removePrefix("/")
            // Write into the nested Map structure so resolveNestedDataPath() can
            // read the value back (handles both single-segment and nested keys).
            setNestedDataPath(key, value)
        }

        val messages = mutableListOf<A2UIMessage>()

        // Emit UPDATE_DATA message for the dispatcher's binding mechanism.
        // For full-replace, forward the original Map. For partial upsert, wrap the
        // written value under its path key so downstream consumers see a Map too.
        if (valueAsMap != null) {
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_DATA,
                surfaceId = surfaceId,
                data = valueAsMap
            ))
        } else if (path != null && value != null) {
            val pathKey = path.removePrefix("/")
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_DATA,
                surfaceId = surfaceId,
                data = mapOf(pathKey to value)
            ))
        }

        // Expand ChildList template bindings that depend on changed data
        messages.addAll(expandChildListBindings(surfaceId))

        // Re-evaluate bindings that depend on changed keys
        val changedKeys = valueAsMap?.keys ?: setOf(path?.removePrefix("/") ?: "")
        // Path dependents are registered under the ROOT data key (getRootDataKey), so a
        // change to a nested key like "event/name" must also resolve the root key "event".
        val lookupKeys = changedKeys.flatMap { setOf(it, getRootDataKey(it)) }.toSet()
        val affectedComponentIds = mutableSetOf<String>()
        for (key in lookupKeys) {
            dataKeyToFuncDependents[key]?.let { affectedComponentIds.addAll(it) }
            dataKeyToPathDependents[key]?.let { ids ->
                // Filter out __childlist__ markers (handled separately above)
                affectedComponentIds.addAll(ids.filter { !it.startsWith("__childlist__") })
            }
        }

        for (compId in affectedComponentIds) {
            val originalDef = idToOriginalDef[compId] ?: continue
            val tag = idToTag[compId] ?: continue
            val componentType = originalDef["component"] as? String ?: continue

            // Special handling for ChoicePicker: re-render options with new selection state
            if (componentType == "ChoicePicker" && choicePickerChipTags.containsKey(compId)) {
                val bindingPath = getChoicePickerBindingPath(compId)
                if (bindingPath != null) {
                    val selectedValues = when (val raw = resolveNestedDataPath(bindingPath)) {
                        is List<*> -> raw.map { it.toString() }.toSet()
                        is String -> setOf(raw)
                        else -> emptySet()
                    }
                    val chipTags = choicePickerChipTags[compId] ?: continue
                    val chipValues = choicePickerChipValues[compId] ?: continue
                    val cpDisplayStyle = choicePickerDisplayStyles[compId] ?: "chips"
                    val cpVariant = choicePickerVariants[compId] ?: "mutuallyExclusive"
                    for (i in chipTags.indices) {
                        val chipTag = chipTags[i]
                        val chipValue = chipValues[i]
                        val isSelected = chipValue in selectedValues
                        if (cpDisplayStyle == "checkbox") {
                            val icon = getCheckboxIcon(isSelected, cpVariant)
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTag + 1,
                                viewName = "KRRichTextView",
                                props = mapOf(
                                    "text" to icon,
                                    "color" to if (isSelected) primaryColor else "#999999"
                                )
                            ))
                        } else {
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTag,
                                viewName = "KRView",
                                props = if (isSelected) mapOf(
                                    "backgroundColor" to primaryColor,
                                    "border" to normalizeHexInShorthand("0 solid #00000000")
                                ) else mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "border" to normalizeHexInShorthand("1 solid #CCCCCC")
                                )
                            ))
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = chipTag + 1,
                                viewName = "KRRichTextView",
                                props = mapOf("color" to if (isSelected) "#FFFFFF" else "#333333")
                            ))
                        }
                    }
                }
                continue
            }

            // Special handling for Tabs: re-resolve bound tab titles and update header text
            if (componentType == "Tabs" && tabsDefinitions.containsKey(compId)) {
                val tabDefs = tabsDefinitions[compId]!!
                val headerTags = tabsHeaderTags[compId]
                val selectedIndex = tabsSelectedIndex[compId] ?: 0
                if (headerTags != null) {
                    for ((tabIndex, tabDef) in tabDefs.withIndex()) {
                        // Only re-resolve if the title has a binding spec (Map with path)
                        if (tabDef.titleSpec is Map<*, *>) {
                            val newTitle = resolveLabelString(tabDef.titleSpec, compId).ifEmpty { "Tab ${tabIndex + 1}" }
                            val isSelected = tabIndex == selectedIndex
                            val headerTag = headerTags[tabIndex]
                            // Text tag is always headerTag + 1 (see switchTab / creation logic)
                            messages.add(A2UIMessage(
                                type = A2UIMessageType.UPDATE_COMPONENT,
                                surfaceId = surfaceId,
                                tag = headerTag + 1,
                                viewName = "KRRichTextView",
                                props = buildTabHeaderTextProps(newTitle, isSelected)
                            ))
                        }
                    }
                }
                continue
            }

            val viewName = A2UIComponentMapper.mapComponentName(componentType)

            // Re-evaluate properties that use data bindings (path references and function calls)
            val updatedProps = mutableMapOf<String, Any?>()
            val skipKeys = setOf("id", "component", "children", "child", "accessibility", "action", "tabs")
            for ((propKey, propValue) in originalDef) {
                if (propKey in skipKeys) continue
                // Kuikly `styles` extension: re-expand on data-model change.
                if (propKey == "styles" && propValue is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    for ((sk, sv) in A2UIComponentMapper.expandStyles(componentType, propValue as Map<String, Any?>)) {
                        updatedProps[sk] = resolveValue(sv, compId)
                    }
                    clearDefaultsIfGradient(updatedProps)
                    continue
                }
                val mapped = A2UIComponentMapper.mapProp(componentType, propKey, propValue)
                if (mapped != null) {
                    val (mappedKey, mappedValue) = mapped
                    if (mappedKey.isNotEmpty()) {
                        when (mappedValue) {
                            is A2UIValue.PathBinding -> {
                                // Type-safe path binding - resolve from data model
                                val resolved = resolveNestedDataPath(mappedValue.key)
                                if (resolved != null) {
                                    // For Icon component, map resolved name to unicode char
                                    if (componentType == "Icon" && mappedKey == "text") {
                                        updatedProps[mappedKey] = A2UIComponentMapper.resolveIconNameToChar(resolved.toString())
                                    } else {
                                        updatedProps[mappedKey] = resolved
                                    }
                                }
                            }
                            is A2UIValue.IconPathBinding -> {
                                // Data-bound Icon name in image mode: resolve the underlying
                                // path to the raw icon name, then translate to a URI via the
                                // registry (with fallback). Always write the result so the
                                // fallback image is applied even when the source name
                                // resolves to null / an empty string.
                                val name = resolveNestedDataPath(mappedValue.binding.key)?.toString()
                                updatedProps[mappedKey] = A2UIIconRegistry.resolveIconUriOrFallback(name)
                            }
                            is A2UIValue.FunctionCall -> {
                                // Type-safe function call - re-evaluate
                                val evaluated = evaluateFunctionCall(mappedValue.definition)
                                if (evaluated != null) {
                                    updatedProps[mappedKey] = evaluated
                                }
                            }
                            is A2UIValue.Literal -> {
                                // Literal values don't need re-evaluation
                            }
                            is Map<*, *> -> {
                                // Legacy Map form (not wrapped in A2UIValue) - handle for backward compatibility
                                val pathRef = (mappedValue as? Map<*, *>)?.get("path") as? String
                                if (pathRef != null) {
                                    val dataKey = pathRef.removePrefix("/")
                                    val resolved = resolveNestedDataPath(dataKey)
                                    if (resolved != null) {
                                        if (componentType == "Icon" && mappedKey == "text") {
                                            updatedProps[mappedKey] = A2UIComponentMapper.resolveIconNameToChar(resolved.toString())
                                        } else {
                                            updatedProps[mappedKey] = resolved
                                        }
                                    }
                                } else {
                                    val evaluated = evaluateFunctionCall(mappedValue)
                                    if (evaluated != null) {
                                        updatedProps[mappedKey] = evaluated
                                    }
                                }
                            }
                            else -> {
                                // Other types don't need re-evaluation
                            }
                        }
                    }
                }
            }

            if (updatedProps.isNotEmpty()) {
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = tag,
                    viewName = viewName,
                    parentTag = -1,
                    index = -1,
                    props = updatedProps
                ))
            }

            // Refresh the persistent label/title Text rendered ABOVE this component when its
            // bound data path changes (e.g. AudioPlayer `description` -> /episodeTitle).
            val labelTag = idToFieldLabelTag[compId]
            if (labelTag != null) {
                val labelText = resolveLabelString(idToFieldLabelSpec[compId], compId)
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = labelTag,
                    viewName = "KRRichTextView",
                    parentTag = -1,
                    index = -1,
                    props = mapOf("text" to labelText)
                ))
            }
        }

        // Re-evaluate checks for components that depend on changed data keys
        val checksAffectedIds = mutableSetOf<String>()
        for (key in changedKeys) {
            dataKeyToChecksDependents[key]?.let { checksAffectedIds.addAll(it) }
        }
        // Also re-evaluate checks for components already affected (their data changed)
        for (compId in affectedComponentIds) {
            if (checksRegistry.containsKey(compId)) {
                checksAffectedIds.add(compId)
            }
        }

        for (compId in checksAffectedIds) {
            val originalDef = idToOriginalDef[compId] ?: continue
            val tag = idToTag[compId] ?: continue
            val componentType = originalDef["component"] as? String ?: continue
            val viewName = if (componentType == "TextField") {
                val variant = originalDef["variant"] as? String
                A2UIComponentMapper.mapTextFieldViewName(variant)
            } else {
                A2UIComponentMapper.mapComponentName(componentType)
            }

            val errorMessage = evaluateChecksForComponent(compId)

            if (componentType == "Button") {
                // Button: toggle the dispatch guard, dim the button, and show/hide
                // the inline hint text. Per A2UI spec a failing button is disabled.
                tagToActionDisabled[tag] = errorMessage != null
                idToCheckErrorTag[compId]?.let { errorTag ->
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = errorTag,
                        viewName = "KRRichTextView",
                        parentTag = -1,
                        index = -1,
                        props = checkErrorTextProps(errorMessage)
                    ))
                }
                val disabled = errorMessage != null
                val visualProps = mapOf<String, Any?>(
                    "opacity" to if (disabled) 0.4f else 1.0f,
                    "touchEnable" to if (disabled) 0 else 1
                )
                val existingMsg = messages.find { it.tag == tag && it.type == A2UIMessageType.UPDATE_COMPONENT }
                if (existingMsg != null) {
                    val mutableProps = existingMsg.props?.toMutableMap() ?: mutableMapOf()
                    mutableProps.putAll(visualProps)
                    val idx = messages.indexOf(existingMsg)
                    messages[idx] = existingMsg.copy(props = mutableProps)
                } else {
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = viewName,
                        parentTag = -1,
                        index = -1,
                        props = visualProps
                    ))
                }
            } else {
                // TextField/Input: update the error text component and border
                val errorTag = idToCheckErrorTag[compId]
                if (errorTag != null) {
                    // Update error text component
                    val errorProps = mutableMapOf<String, Any?>()
                    if (errorMessage != null) {
                        errorProps["text"] = errorMessage
                        errorProps["height"] = Float.NaN // auto height
                        errorProps["opacity"] = 1f
                    } else {
                        errorProps["text"] = ""
                        errorProps["height"] = 0f
                        errorProps["opacity"] = 0f
                    }
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = errorTag,
                        viewName = "KRRichTextView",
                        parentTag = -1,
                        index = -1,
                        props = errorProps
                    ))
                }
                // Update TextField border
                val existingMsg = messages.find { it.tag == tag && it.type == A2UIMessageType.UPDATE_COMPONENT }
                if (existingMsg != null) {
                    val mutableProps = existingMsg.props?.toMutableMap() ?: mutableMapOf()
                mutableProps["border"] = if (errorMessage != null) normalizeHexInShorthand("1 solid #D32F2F") else normalizeHexInShorthand("1 solid #CCCCCC")
                    mutableProps.remove("__checkError")
                    val idx = messages.indexOf(existingMsg)
                    messages[idx] = existingMsg.copy(props = mutableProps)
                } else {
                    messages.add(A2UIMessage(
                        type = A2UIMessageType.UPDATE_COMPONENT,
                        surfaceId = surfaceId,
                        tag = tag,
                        viewName = viewName,
                        parentTag = -1,
                        index = -1,
                props = mapOf("border" to if (errorMessage != null) normalizeHexInShorthand("1 solid #D32F2F") else normalizeHexInShorthand("1 solid #CCCCCC"))
                    ))
                }
            }
        }

        return messages
    }

    /**
     * Build props for a Kuikly-native component (KR-prefixed).
     *
     * Unlike [buildProps] which applies semantic defaults (Leaf-Margin Strategy, variant expansion, etc.),
     * this method passes properties through directly without any implicit defaults.
     * Kuikly components have their properties explicitly specified in the JSON,
     * aligned 1:1 with Kuikly DSL property names and value types.
     */
    private fun buildKuiklyProps(componentType: String, compMap: Map<String, Any?>): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()
        val id = compMap["id"] as? String

        // Apply default props (currently empty for Kuikly components)
        props.putAll(KuiklyComponentMapper.buildDefaultProps(componentType))

        // Pass through all non-structural properties directly
        for ((key, value) in compMap) {
            val mapped = KuiklyComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                props[propKey] = resolveValue(propValue, id)
            }
        }

        // Merge borderWidth + borderColor into "border" property (Kuikly DSL format: "width style color")
        val borderWidth = props.remove("borderWidth")
        val borderColor = props.remove("borderColor")
        if (borderWidth != null || borderColor != null) {
            val width = borderWidth?.toString() ?: "1"
            val rawColor = borderColor?.toString() ?: "#000000"
            // iOS renderer parses the shorthand color via [NSString longLongValue]
            // (decimal only) - hex literals collapse to transparent. Normalize here.
            val color = hexToKuiklyColor(rawColor)
            props["border"] = "$width solid $color"
        }

        // Ensure borderRadius is String (Kuikly native expects String)
        val borderRadius = props["borderRadius"]
        if (borderRadius is Number) {
            props["borderRadius"] = borderRadius.toString()
        }

        // Store original definition for re-evaluation on data model updates
        if (id != null) {
            idToOriginalDef[id] = compMap
        }

        // Extract and store action definition, set touchEnable for clickable Kuikly components
        val action = compMap["action"]
        if (action != null && id != null) {
            val tag = idToTag[id]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                tagToAction[tag] = action
                props["touchEnable"] = 1
            }
        }

        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Build Kuikly-compatible props from a standard A2UI 0.9 component definition.
     *
     * Follows the official "Leaf-Margin Strategy" from basic_catalog_implementation_guide.md:
     * 1. Invisible containers (Row, Column, List) have ZERO spacing
     * 2. Leaf components (Text, Image, Icon, Video, AudioPlayer, Slider) carry 8dp margin
     * 3. Visually outlined containers (Card, Button, TextField, CheckBox, ChoicePicker) carry 8dp margin
     */
    private fun buildProps(componentType: String, compMap: Map<String, Any?>): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()

        // Determine if this is the root component (no parent in childToParent)
        val id = compMap["id"] as? String
        val isRoot = id != null && !childToParentCache.containsKey(id)

        // Apply component-specific default layout props per implementation guide
        when (componentType) {
            "Row" -> {
                // Row: horizontal flex container, fills available width
                // Per guide: Row/Column have ZERO internal padding and ZERO external margins
                props["flexDirection"] = "row"
                // Per catalog: default align is "stretch"
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
                // Auto-wrap children onto new lines when they don't fit on one line.
                // The engine has no flex-shrink, so without wrapping a row whose children
                // exceed its width simply overflows the parent. Wrapping keeps the content
                // inside the parent's bounds (note: a single child that is itself wider than
                // the whole row still can't be split by the engine and stays on its own line).
                props["flexWrap"] = "wrap"
                // Stack wrapped lines cleanly from the top so they don't overlap on the
                // cross axis (same fix applied to ChoicePicker's chip row).
                props["alignContent"] = "flex-start"
            }
            "Column" -> {
                // Column: vertical flex container
                // Per guide: Row/Column have ZERO internal padding and ZERO external margins
                props["flexDirection"] = "column"
                // Per catalog: default align is "stretch" — make it explicit so that the
                // engine default cannot silently drift away from the protocol contract.
                // If the source JSON provides an `align`, the mapProp loop later overwrites
                // this with mapAlign(compMap["align"]).
                if (compMap["align"] == null) {
                    props["alignItems"] = "stretch"
                }
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
                // When Column is inside a Row and has no explicit weight, it should
                // flex to fill remaining space. Without this, Column's width is determined
                // by content (e.g. text natural width), causing text overflow without wrapping.
                if (id != null && compMap["weight"] == null) {
                    val parentId = childToParentCache[id]
                    if (parentId != null) {
                        val parentComp = idToTag[parentId]?.let { tagToComponentName[it] }
                        if (parentComp == "Row") {
                            props["flex"] = 1
                        }
                    }
                }
            }
            "Card" -> {
                // Per basic_catalog_implementation_guide.md (Card): rounded corners
                // (8-12dp), inner padding (16dp), a background distinct from the surface,
                // and a subtle shadow/elevation, plus a 1dp outline for nesting clarity.
                // Card carries the standard 8dp external margin (Leaf-Margin Strategy).
                //
                // NOTE: buildProps (unlike buildKuiklyProps) does NOT merge
                // borderWidth/borderColor into the `border` shorthand, and the renderer
                // only honors `border` + `boxShadow`. The previous borderWidth/borderColor
                // props were therefore silently dropped (no outline, no shadow).
                props["flexDirection"] = "column"
                props["borderRadius"] = "12"
                props["border"] = normalizeHexInShorthand(CARD_DEFAULT_BORDER_RAW)
                if (props["backgroundColor"] == null) {
                    props["backgroundColor"] = CARD_DEFAULT_BACKGROUND_COLOR
                }
                // boxShadow format: "offsetX offsetY radius color [fill]" (dp). A non-zero
                // offset and a positive radius are required for the shadow to render;
                // fill=0 draws a clean outer drop shadow (view region clipped out).
                // Keep blur small (2dp) so the shadow doesn't extend far beyond the
                // rounded corners — avoids grey residue at corners on Android.
                props["boxShadow"] = normalizeHexInShorthand(CARD_DEFAULT_BOX_SHADOW_RAW)
                // iOS-only escape hatch: when a view has both `borderRadius` and
                // `boxShadow`, the rounded-corner mask on CALayer also clips the
                // drop shadow, making it invisible. Setting `wrapperBoxShadowView=1`
                // tells the iOS renderer to wrap this view in a KRBoxShadowView so
                // the shadow is drawn on an outer layer that isn't masked.
                // Android/Web/HarmonyOS ignore this prop; Core auto-sets it inside
                // View.boxShadow(...) but A2UI writes props directly, so we opt in
                // here explicitly. See core/base/Props.kt WRAPPER_BOX_SHADOW_VIEW.
                props["wrapperBoxShadowView"] = 1
                props["padding"] = 16
                props["margin"] = 8
                // Per v0.9 protocol: Card fills width, height is content-driven
                props["alignSelf"] = "stretch"
                if (isRoot) {
                    props["margin"] = 0
                }
            }
            "Divider" -> {
                // Per A2UI catalog: 1dp line spanning 100% of the parent's cross axis,
                // with a subtle border color. Divider is a leaf, carries 8dp margin.
                //
                // Height/width strategy:
                //   - horizontal: fixed height=1, alignSelf=stretch to fill parent width.
                //   - vertical:   fixed width=1 + explicit visible height fallback.
                //
                // Why vertical Divider does NOT rely on alignSelf=stretch alone:
                // The Kuikly flex engine cannot stretch a node whose intrinsic cross-axis
                // size is undefined when the parent's cross-axis is content-driven (Row
                // height derived from siblings). In that scenario the Divider's undefined
                // layoutDim propagates through max()/boundAxis() as NaN, which makes both
                // the Row's height AND the divider's stretched height collapse to 0.
                //
                // Solution: give the vertical divider a concrete fallback height so the
                // rule is always visible AND does not poison the Row's cross-axis
                // measurement. The value 40 matches the natural height of a body Text
                // (~24 line-height + 8+8 margin), which is the most common sibling in a
                // Row that uses a vertical divider ("Left | Right"). The applier layer
                // can further stretch this to the actual Row height after layout via the
                // __verticalDividerFollowParent hook.
                val axis = compMap["axis"] as? String ?: "horizontal"
                props["backgroundColor"] = "#E0E0E0"
                if (axis == "horizontal") {
                    props["height"] = 1
                    props["alignSelf"] = "stretch"
                    props["marginTop"] = 8
                    props["marginBottom"] = 8
                } else {
                    props["width"] = 1
                    props["height"] = 40
                    props["marginLeft"] = 8
                    props["marginRight"] = 8
                    // Marker consumed by A2UIApplier: after the initial layout, resize the
                    // divider's height to match the parent Row's actual height so it truly
                    // "spans the height of the container" for tall siblings (images, etc.).
                    props["__verticalDividerFollowParent"] = true
                }
            }
            "Button" -> {
                // Per guide: Button is a visually outlined container, carries 8dp margin
                // variant handling: default=subtle bg+border, primary=primaryColor bg, borderless=no bg/border
                val variant = compMap["variant"] as? String ?: "default"
                props["flexDirection"] = "row"
                props["alignItems"] = "center"
                props["justifyContent"] = "center"
                props["borderRadius"] = "6"
                props["padding"] = 10
                props["paddingLeft"] = 16
                props["paddingRight"] = 16
                props["margin"] = 8
                when (variant) {
                    "primary" -> {
                        props["backgroundColor"] = primaryColor
                    }
                    "borderless" -> {
                        // No background, no border - looks like clickable text
                    }
                    else -> {
                        // "default": subtle background + border
                        props["backgroundColor"] = "#F5F5F5"
                        props["borderWidth"] = "1"
                        // Raw hex kept here; buildKuiklyProps() collapses borderWidth+
                        // borderColor into the `border` shorthand and converts the hex
                        // to a decimal AARRGGBB value the iOS renderer can parse.
                        props["borderColor"] = "#CCCCCC"
                    }
                }
            }
            "List" -> {
                // Per A2UI v0.9 protocol: List is a scrollable, invisible container with
                // ZERO spacing. It fills the cross axis of its parent; the main-axis size
                // is bounded by the parent, and content exceeding the main axis MUST be
                // scrollable (not clipped).
                //
                // The DSL side (A2UIApplier.createDSLView) maps "KRScrollView" to
                // A2UIScrollerView, which extends ScrollerView with cross-axis size
                // backfill. This guarantees:
                //   * A KRScrollContentView is auto-inserted so iOS KRScrollView.contentSize
                //     stays in sync with children;
                //   * ScrollerAttr.flexDirection override auto-emits `directionRow` prop;
                //   * ScrollerView.willInit defaults overflow=true — do NOT forward
                //     `overflow` here (applyCommonProps maps non-"hidden" to overflow(false),
                //     which would disable scrolling);
                //   * A2UIScrollerView.backfillCrossAxisSize() sets ScrollerView's height
                //     after contentView layout, so parent Card can correctly size itself.
                val direction = compMap["direction"] as? String ?: "vertical"
                val isHorizontal = direction == "horizontal"
                props["flexDirection"] = if (isHorizontal) "row" else "column"
                // Fill the cross-axis of the parent
                props["alignSelf"] = "stretch"
            }
            "Image" -> {
                // Per guide: Image is a leaf component, carries 8dp margin
                // Per guide: defaults to flexible width to fill container
                val variant = compMap["variant"] as? String ?: "mediumFeature"
                props["margin"] = 8

                // Check if parent is a Row (horizontal layout) to adjust sizing strategy
                val parentId = if (id != null) childToParentCache[id] else null
                val parentDef = if (parentId != null) idToOriginalDef[parentId] else null
                val parentIsRow = parentDef?.get("component") == "Row"

                when (variant) {
                    "icon" -> {
                        props["width"] = 24
                        props["height"] = 24
                        props["margin"] = 0 // Icon-sized images don't need margin
                    }
                    "avatar" -> {
                        props["width"] = 40
                        props["height"] = 40
                        props["borderRadius"] = "20" // Fully rounded
                    }
                    "smallFeature" -> {
                        props["width"] = 100
                        props["height"] = 100
                    }
                    "mediumFeature" -> {
                        if (parentIsRow) {
                            // In a Row, use fixed width since alignSelf=stretch only affects cross-axis
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Sizing rule (spec):
                            //   width  = min(image.intrinsicWidth, 300dp)  — fixed cap
                            //   height = width * (imgH / imgW), but capped at 200dp
                            //            (excess is CROPPED via cover, not letter-boxed).
                            //   The image is centered horizontally in the parent when the
                            //   parent's content width is greater than the image width.
                            //
                            // We can't know the intrinsic size at build time, so we set a
                            // safe pre-load placeholder here (stretch-to-container, 200dp
                            // tall, cover) and mark the node with __featureAutoSizeMax(Width|Height)Dp
                            // so A2UIApplier wires up a loadResolution callback that
                            // rewrites width/height/alignSelf once the real image size
                            // is known (clamped against these caps, centered on cross axis).
                            props["alignSelf"] = "stretch"
                            props["height"] = 200
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                    "largeFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 150
                            props["height"] = 150
                        } else {
                            // Same auto-size mechanism as mediumFeature but with:
                            //   - NO width cap: image renders at its intrinsic width.
                            //   - Taller max height (400dp).
                            //   - No horizontal centering (alignSelf=AUTO after load),
                            //     since without a width cap the image just occupies its
                            //     own natural width at the leading edge.
                            // We intentionally do NOT emit __featureAutoSizeMaxWidthDp
                            // here; the applier treats its absence as "no width cap +
                            // no centering". Placeholder height matches the cap so the
                            // pre-load layout doesn't shift when the real image resolves
                            // smaller. See A2UIApplier's applyFeatureAutoSize.
                            props["alignSelf"] = "stretch"
                            props["height"] = 400
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxHeightDp"] = 400
                        }
                    }
                    "header" -> {
                        // Banner style: 100% width (respecting margins), fixed height 200dp, cover.
                        // alignSelf=stretch lets the image fill parent's available width
                        // (parent content width minus this image's own margins).
                        props["alignSelf"] = "stretch"
                        props["height"] = 200
                        props["resizeMode"] = "cover"
                    }
                    else -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Same behavior as mediumFeature (the default variant).
                            props["alignSelf"] = "stretch"
                            props["height"] = 200
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                }
            }
            "Icon" -> {
                // All Icons share the same 24dp x 24dp footprint plus an 8dp margin.
                // Which prop family we emit here depends on the global routing decided by
                // A2UIComponentMapper.isIconRenderedAsImage() (backed by A2UIIconRegistry):
                // - image mode  → KRImageView layout props (size + resizeMode)
                // - text mode   → KRRichTextView typography props (fontSize + textAlign)
                // The `src` / `text` prop itself is produced by mapIconProp in the
                // generic prop loop; here we only supply layout defaults.
                props["width"] = 24
                props["margin"] = 8
                if (A2UIComponentMapper.isIconRenderedAsImage()) {
                    // Match the default Icon footprint (24dp x 24dp) so registered images
                    // occupy the same slot as the unicode glyph they replace.
                    props["height"] = 24
                    props["resizeMode"] = "contain"
                } else {
                    // Per guide: Icon is a leaf component, 24dp width, no fixed height
                    // so text naturally sizes vertically for proper centering in Button
                    props["fontSize"] = 20f
                    props["textAlign"] = "center"
                }
            }
            "Text" -> {
                // Per guide: Text is a leaf component, carries 8dp margin
                props["margin"] = 8
                // Per A2UI catalog v0.9: Text.variant defaults to "body" (16dp base font,
                // normal weight, standard foreground color). When the JSON omits the
                // `variant` field we must still apply body's visual defaults, otherwise
                // the native Text falls back to platform default sizing which does not
                // match the protocol contract. If a variant IS provided, the __variant
                // expansion further below (mapProp result) will overwrite these values.
                if (compMap["variant"] == null) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize("body")
                    props["color"] = A2UIComponentMapper.getVariantColor("body")
                }
                // If parent is a Button, inherit button's text color
                if (id != null) {
                    val parentId = childToParentCache[id]
                    if (parentId != null) {
                        val parentComp = idToTag[parentId]?.let { tagToComponentName[it] }
                        if (parentComp == "Button") {
                            // Per guide section 4: text inside primary button should contrast
                            val buttonVariant = getParentButtonVariant(parentId)
                            if (buttonVariant == "primary") {
                                props["color"] = "#FFFFFF"
                            }
                            // Text inside button doesn't need its own margin
                            props["margin"] = 0
                        } else if (parentComp == "Row") {
                            // Inside a horizontal container, Text has no width constraint
                            // from the engine (parentMaxWidth is undefined for Row children).
                            // When flexWrap=wrap causes the Text to move to a new line, its
                            // natural width may exceed the Row's width, causing clipping.
                            // Mark for post-layout correction: set maxWidth = Row's width
                            // so the text wraps within bounds.
                            props["__textInRowPostLayout"] = "true"
                        } else if (parentComp == "Column" || parentComp == "Card") {
                            // Inside a vertical container, stretch the text so the Flexbox
                            // engine's Loop A pre-fill provides a width constraint via
                            // node.getLayoutDimension(crossAxis) — this is critical when
                            // parentMaxWidth is undefined (Row → flex Column chain).
                            //
                            // NOTE: For parent Column with align=center/end, the end-of-
                            // function logic keeps this stretch but adds a
                            // __textAlignPostLayout hint. The applier uses a post-layout
                            // callback to set maxWidth and remove stretch, enabling
                            // alignItems to position the text correctly on re-layout.
                            props["alignSelf"] = "stretch"
                        }
                    }
                }
            }
            "Video" -> {
                // Per guide: Video is a leaf, spans full width, carries 8dp margin
                props["alignSelf"] = "stretch"
                props["height"] = 200
                props["margin"] = 8
            }
            "AudioPlayer" -> {
                // Per guide: AudioPlayer is a leaf, spans full width, carries 8dp margin.
                // Height accommodates the native transport bar (play/pause + scrubber + time).
                props["alignSelf"] = "stretch"
                props["height"] = 64
                props["margin"] = 8
            }
            "TextField" -> {
                // Per guide: TextField is a visually outlined container, carries 8dp margin
                props["alignSelf"] = "stretch"
                props["margin"] = 8
                val variant = compMap["variant"] as? String ?: "shortText"
                if (variant == "longText") {
                    // Multi-line text area: use minHeight instead of fixed height
                    props["minHeight"] = 88
                } else {
                    // Single-line input: fixed height
                    props["height"] = 44
                }
                props["border"] = normalizeHexInShorthand("1 solid #CCCCCC")
                props["borderRadius"] = "4"
                props["padding"] = 8
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = valueSpec["path"] as? String
                    if (path != null) {
                        props["__textFieldBindingPath"] = path.removePrefix("/")
                    }
                } else if (compMap["validationRegexp"] != null && id != null) {
                    // validationRegexp requires a binding path to trigger re-validation
                    // on text change. Create an internal binding path if none exists.
                    val internalPath = "__vr_$id"
                    props["__textFieldBindingPath"] = internalPath
                    // Initialize the internal data model entry
                    setNestedDataPath(internalPath, valueSpec?.toString() ?: "")
                }
            }
            "CheckBox" -> {
                // Per A2UI protocol: CheckBox is a leaf interactive component
                // CheckBoxView requires explicit width and height (checked in didInit())
                props["width"] = 24
                props["height"] = 24
                props["margin"] = 8
                // Pass the current theme primaryColor so the applier can tint the checked glyph.
                // Aligned with Button variant=primary / Slider progressColor / ChoicePicker selection.
                props["__primaryColor"] = primaryColor
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = valueSpec["path"] as? String
                    if (path != null) {
                        props["__checkBoxBindingPath"] = path.removePrefix("/")
                    }
                }
            }
            "ChoicePicker" -> {
                // Per guide: ChoicePicker is a visually outlined container, carries 8dp margin
                val displayStyle = compMap["displayStyle"] as? String ?: "chips"
                props["margin"] = 8
                // Fill the available width so the container has a defined main-axis size.
                props["alignSelf"] = "stretch"

                if (displayStyle == "checkbox") {
                    // Checkbox display: vertical list of rows, each row has icon + label
                    props["flexDirection"] = "column"
                    props["alignItems"] = "stretch"
                } else {
                    // Chips display (default): wrapping Row of pill-shaped chips
                    props["flexDirection"] = "row"
                    props["flexWrap"] = "wrap"
                    // Use flex-start (not center) on the cross axis. With flexWrap the row's
                    // cross size (height) is content-driven/undefined while lines are laid out,
                    // and the center calculation depends on that height; it mis-positions the
                    // wrapped lines so they overlap. flex-start stacks the lines cleanly.
                    props["alignItems"] = "flex-start"
                    props["alignContent"] = "flex-start"
                }

                // Extract binding path for two-way binding, or direct initial selection
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = valueSpec["path"] as? String
                    if (path != null) {
                        props["__choicePickerBindingPath"] = path.removePrefix("/")
                    }
                } else if (valueSpec is List<*>) {
                    // Direct array value: pass as initial selection
                    props["__choicePickerDirectValue"] = valueSpec
                } else if (valueSpec is String) {
                    props["__choicePickerDirectValue"] = listOf(valueSpec)
                }

                // Store options, variant, displayStyle, filterable for chip/checkbox generation
                val options = compMap["options"] as? List<*>
                val variant = compMap["variant"] as? String ?: "mutuallyExclusive"
                props["__choicePickerOptions"] = options
                props["__choicePickerVariant"] = variant
                props["__choicePickerDisplayStyle"] = displayStyle
                props["__choicePickerFilterable"] = compMap["filterable"] == true
                // Top-level `label` is rendered as a persistent Text ABOVE the options
                // (resolved + rendered during expansion in buildComponentTree).
                compMap["label"]?.let { props["__choicePickerLabel"] = it }
            }
            "Slider" -> {
                // Per guide: Slider is a leaf component, carries 8dp margin
                // Per A2UI protocol: Slider should span full width of parent (like Video/AudioPlayer)
                // No explicit width - alignSelf=stretch handles it in Column context.
                // A2UIApplier handles the SliderView.didInit() width requirement specially.
                props["alignSelf"] = "stretch"
                props["height"] = 40
                props["margin"] = 8
                // Set default progressColor to theme primary color (overridable by explicit prop)
                if (!compMap.containsKey("progressColor")) {
                    props["progressColor"] = primaryColor
                }
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = valueSpec["path"] as? String
                    if (path != null) {
                        props["__sliderBindingPath"] = path.removePrefix("/")
                    }
                }
                // Note: A2UIValue.PathBinding is handled via resolveValue in the generic prop loop
                // Pass min/max directly for normalization
                compMap["max"]?.let { props["max"] = it }
                compMap["min"]?.let { props["min"] = it }
            }
            "DateTimeInput" -> {
                // Per guide: DateTimeInput is a visually outlined container, carries 8dp margin
                // DateTimeInputView is a VirtualView that handles its own rendering
                props["margin"] = 8
                props["alignSelf"] = "stretch"
                // Pass enableDate/enableTime/label to the view
                compMap["enableDate"]?.let { props["enableDate"] = it }
                compMap["enableTime"]?.let { props["enableTime"] = it }
                compMap["label"]?.let { props["label"] = it }
                // Pass min/max constraints to the view
                compMap["min"]?.let { props["min"] = it }
                compMap["max"]?.let { props["max"] = it }
                // Extract binding path for two-way binding support
                val valueSpec = compMap["value"]
                if (valueSpec is Map<*, *>) {
                    val path = valueSpec["path"] as? String
                    if (path != null) {
                        props["__dateTimeBindingPath"] = path.removePrefix("/")
                    }
                }
            }
            "Tabs" -> {
                // Tabs: invisible container with tab headers
                props["flexDirection"] = "column"
                // Per v0.9 protocol: width fills parent, height is content-driven
                props["alignSelf"] = "stretch"
            }
            "Modal" -> {
                // Modal: acts as a trigger entry point
                props["flexDirection"] = "column"
            }
        }

        // Process component-specific properties
val skipKeys = setOf("id", "component", "children", "child", "accessibility", "action", "tabs")
        for ((key, value) in compMap) {
            if (key in skipKeys) continue

            // Kuikly `styles` extension (kuikly_a2ui_catalog.json):
            // expand the styles map into individual whitelisted Kuikly props.
            // Placed after semantic defaults (applyDefaultLayoutProps and the
            // per-component blocks above) so that user-specified styles win.
            if (key == "styles" && value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                for ((sk, sv) in A2UIComponentMapper.expandStyles(componentType, value as Map<String, Any?>)) {
                    props[sk] = resolveValue(sv, id)
                }
                clearDefaultsIfGradient(props)
                continue
            }

            val mapped = A2UIComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                if (propKey == "__variant" && propValue is String) {
                    // Expand variant into multiple props
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize(propValue)
                    val fontWeight = A2UIComponentMapper.getVariantFontWeight(propValue)
                    if (fontWeight != null) {
                        props["fontWeight"] = fontWeight
                    }
                    props["color"] = A2UIComponentMapper.getVariantColor(propValue)
                } else if (propKey.isNotEmpty()) {
                    val resolved = resolveValue(propValue, id)
                    // For Icon component with data-bound name (PathBinding),
                    // map the resolved name to unicode char.
                    // Static icon names are already mapped by mapIconProp (wrapped in Literal).
                    if (componentType == "Icon" && propKey == "text" && propValue is A2UIValue.PathBinding && resolved is String && resolved.isNotEmpty()) {
                        props[propKey] = A2UIComponentMapper.resolveIconNameToChar(resolved)
                    } else {
                        props[propKey] = resolved
                    }
                }
            }
        }

        // Store original definition for re-evaluation on data model updates
        if (id != null) {
            idToOriginalDef[id] = compMap
        }

        // Extract and store action definition, set touchEnable for clickable components
        val action = compMap["action"]
        if (action != null && id != null) {
            val tag = idToTag[id]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                tagToAction[tag] = action
                props["touchEnable"] = 1
            }
        }

        // Register and evaluate checks for this component
        val checks = compMap["checks"] as? List<*>
        if (checks != null && id != null) {
            registerChecks(id, checks)
            val errorMessage = evaluateChecksForComponent(id)
            if (componentType == "Button") {
                // Button: disabled when any check fails. We make the disabled state
                // effective on every render path: (1) tagToActionDisabled guards the
                // dispatch, (2) touchEnable=0 makes the native view truly inert (no
                // tap/ripple), (3) opacity dims it. The message is stashed so
                // buildComponentTree can render an inline hint below the button.
                val disabled = errorMessage != null
                idToTag[id]?.let { tagToActionDisabled[it] = disabled }
                props["opacity"] = if (disabled) 0.4f else 1.0f
                props["touchEnable"] = if (disabled) 0 else 1
                props["__checkError"] = errorMessage
            } else {
                // TextField/CheckBox: store error for wrapper error text component
                // (handled by buildComponentTree which creates a separate error text view)
                props["__checkError"] = errorMessage
                // Set border color based on check result
                if (errorMessage != null) {
                    props["border"] = normalizeHexInShorthand("1 solid #D32F2F")
                }
            }
        }

        // Handle validationRegexp for TextField (legacy validation, converted to checks)
        val validationRegexp = compMap["validationRegexp"] as? String
        if (validationRegexp != null && id != null && componentType == "TextField" && checks == null) {
            // Determine the binding path for the regex check's value reference
            val valueSpec = compMap["value"]
            val bindingPath = if (valueSpec is Map<*, *>) {
                (valueSpec["path"] as? String) ?: "/__vr_$id"
            } else {
                "/__vr_$id"
            }
            // Convert validationRegexp to an equivalent regex check
            val syntheticChecks = listOf(
                mapOf(
                    "condition" to mapOf(
                        "call" to "regex",
                        "args" to mapOf(
                            "value" to mapOf("path" to bindingPath),
                            "pattern" to validationRegexp
                        ),
                        "returnType" to "boolean"
                    ),
                    "message" to "Input does not match the required format"
                )
            )
            registerChecks(id, syntheticChecks)
            val errorMessage = evaluateChecksForComponent(id)
            // TextField: store error for wrapper error text component
            props["__checkError"] = errorMessage
            // Set border color based on check result
            if (errorMessage != null) {
                props["border"] = normalizeHexInShorthand("1 solid #D32F2F")
            }
        }

        // Handle "weight" property (flex-grow in Row/Column children)
        val weight = compMap["weight"]
        if (weight != null) {
            props["flex"] = weight
        }

        // Parent-align-aware alignSelf override.
        //
        // Many leaf/container branches above set `alignSelf=stretch` so the child fills
        // the parent's cross axis (needed for Text re-measure/wrap, Image banner width,
        // Divider full-width, TextField/Slider/etc. full-width look).
        //
        // However, A2UI's Row/Column `align` (start/center/end) is expressed on the
        // parent as `alignItems`, and in Flexbox a child's `alignSelf` OVERRIDES the
        // parent's `alignItems`. So an unconditional `alignSelf=stretch` on the child
        // silently defeats `align:center/end/start` — every case renders as a stretched
        // block anchored at the cross-axis start, regardless of the parent's align.
        //
        // Fix: when the parent has an explicit non-stretch `align`, DROP the child's
        // `alignSelf=stretch` so the parent's `alignItems` can position the child's box
        // by its content size on the cross axis. The `stretch` semantics are preserved
        // when the parent's align is absent or explicitly "stretch" (the protocol
        // default), which is where the wrap/full-cross-axis behaviors matter.
        //
        // Cross-axis mapping (Yoga):
        //  - Parent Column (main=vertical)  → cross axis is horizontal → `width`  is the explicit cross-axis size
        //  - Parent Row    (main=horizontal) → cross axis is vertical   → `height` is the explicit cross-axis size
        //
        // Text handling asymmetry:
        //  - In a Column parent with center/end align, Text needs first-pass stretch so
        //    it can wrap correctly (parentMaxWidth is often undefined), then a
        //    post-layout correction (`__textAlignPostLayout`) fixes horizontal alignment.
        //  - In a Row parent, cross-axis alignment (vertical) is handled directly by
        //    Yoga's alignItems on a content-sized cross axis. No post-layout hint
        //    is needed; simply keep stretch (Row Text alignSelf=stretch is compatible
        //    with alignItems=center because Text height is content-driven).
        if (id != null) {
            val parentId = childToParentCache[id]
            val parentDef = if (parentId != null) idToOriginalDef[parentId] else null
            val parentComp = parentDef?.get("component") as? String
            if (parentComp == "Column" || parentComp == "Row") {
                val parentAlign = parentDef["align"] as? String
                if (parentAlign == "start" || parentAlign == "center" || parentAlign == "end") {
                    // Explicit cross-axis size key differs by parent orientation.
                    val explicitCrossSizeKey = if (parentComp == "Column") "width" else "height"
                    val removeStretch = when {
                        // Text is special-cased FIRST: even though a Text may carry an
                        // explicit width (unusual but legal), the Column-parent case
                        // needs the post-layout hint. Handling Text here avoids the
                        // explicit-size branch below stealing this case.
                        componentType == "Text" -> {
                            // Only Column parent needs post-layout correction for horizontal
                            // alignment. In a Row parent, Text keeps stretch and Yoga's
                            // alignItems handles vertical positioning cleanly.
                            if (parentComp == "Column" &&
                                (parentAlign == "center" || parentAlign == "end")
                            ) {
                                props["__textAlignPostLayout"] = parentAlign
                            }
                            false  // keep stretch
                        }
                        // Explicit cross-axis size present → user pinned the cross-axis
                        // dimension, stretch is redundant and would defeat alignItems.
                        // This branch INTENTIONALLY precedes the Row/Column container
                        // branch below so that decorative Row/Column nodes with explicit
                        // cross-axis size (e.g. an 8×8 dot Column, a fixed-height Row
                        // badge) get their stretch removed and the parent's alignItems
                        // can position them.
                        props.containsKey(explicitCrossSizeKey) -> true
                        // Row/Column containers WITHOUT explicit cross-axis size are
                        // content-driven nested layouts and need stretch for internal
                        // re-measure / child stretch propagation.
                        componentType == "Row" || componentType == "Column" -> false
                        // Button/Icon are content-sized on both axes; safe to remove.
                        componentType == "Button" || componentType == "Icon" -> true
                        // Slider/Image/Video/etc. depend on stretch for their main-axis
                        // width in Column context. In Row context this branch rarely
                        // fires (those components aren't typical Row siblings), but
                        // keeping stretch is the conservative default either way.
                        else -> false
                    }
                    if (removeStretch && props["alignSelf"] == "stretch") {
                        props.remove("alignSelf")
                    }
                }
            }
        }

        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Resolve a component's `label` (DynamicString) into a display string.
     * Supports literal strings and data-binding maps ({"path": "/key"}).
     */
    private fun resolveLabelString(labelSpec: Any?, componentId: String? = null): String {
        return when (labelSpec) {
            null -> ""
            is String -> labelSpec
            is Map<*, *> -> resolveValue(A2UIComponentMapper.resolveTextValue(labelSpec), componentId)?.toString() ?: ""
            else -> labelSpec.toString()
        }
    }

    /**
     * Resolve a value that might be a data binding, function call, or plain value.
     * - Strings starting with "/" are data path references
     * - Map objects with "call" key are function calls to be evaluated
     * - Other values are returned as-is
     *
     * @param componentId optional component ID for registering function call dependencies
     */
    private fun resolveValue(value: Any?, componentId: String? = null): Any? {
        if (value == null) return null

        // Handle A2UIValue sealed class (type-safe path/function references)
        if (value is A2UIValue) {
            return when (value) {
                is A2UIValue.Literal -> value.value
                is A2UIValue.PathBinding -> {
                    val key = value.key
                    // Register path dependency for re-evaluation on data update
                    // Use root key so nested paths like "stat1/value" trigger on "stat1" changes
                    if (componentId != null) {
                        dataKeyToPathDependents.getOrPut(getRootDataKey(key)) { mutableSetOf() }.add(componentId)
                    }
                    // Return empty string if not yet resolved (not the path string itself,
                    // which would be rendered as text content and cause incorrect measurement)
                    resolveNestedDataPath(key) ?: ""
                }
                is A2UIValue.IconPathBinding -> {
                    // Data-bound Icon `name` in image mode: register a dependency on the
                    // underlying key so a data update re-renders this Icon, then translate
                    // the resolved name through A2UIIconRegistry (with fallback URI).
                    val key = value.binding.key
                    if (componentId != null) {
                        dataKeyToPathDependents.getOrPut(getRootDataKey(key)) { mutableSetOf() }.add(componentId)
                    }
                    val name = resolveNestedDataPath(key)?.toString()
                    A2UIIconRegistry.resolveIconUriOrFallback(name)
                }
                is A2UIValue.FunctionCall -> {
                    // Register dependencies for this function call
                    if (componentId != null) {
                        registerFunctionDependencies(componentId, value.definition)
                    }
                    // Try to evaluate the function
                    evaluateFunctionCall(value.definition) ?: ""
                }
            }
        }

        // Legacy fallback for raw values not wrapped in A2UIValue
        if (value is String) return value
        if (value is Map<*, *>) {
            // Data binding path reference: {"path": "/key"}
            val path = value["path"] as? String
            if (path != null) {
                val key = path.removePrefix("/")
                if (componentId != null) {
                    dataKeyToPathDependents.getOrPut(getRootDataKey(key)) { mutableSetOf() }.add(componentId)
                }
                return resolveNestedDataPath(key) ?: ""
            }
            // Function call: {"call": "formatDate", "args": {...}}
            val call = value["call"] as? String
            if (call != null) {
                if (componentId != null) {
                    registerFunctionDependencies(componentId, value)
                }
                return evaluateFunctionCall(value) ?: ""
            }
        }
        return value
    }

    /**
     * Register data model dependencies for a function call.
     * When any of the referenced data keys change, the component will be re-evaluated.
     */
    private fun registerFunctionDependencies(componentId: String, funcCall: Map<*, *>) {
        val paths = extractDataPaths(funcCall)
        for (path in paths) {
            val key = path.removePrefix("/")
            dataKeyToFuncDependents.getOrPut(key) { mutableSetOf() }.add(componentId)
            // Also register under the ROOT key so that updates keyed on the root object
            // (e.g. a ChoicePicker writing "event/location", re-evaluated via getRootDataKey
            // = "event") still re-evaluate functions that read a deeper path such as
            // "event/location/0". Without this, a formatString over an array element would
            // not refresh when the array changes.
            val rootKey = getRootDataKey(key)
            if (rootKey != key) {
                dataKeyToFuncDependents.getOrPut(rootKey) { mutableSetOf() }.add(componentId)
            }
        }
    }

    /**
     * Extract all data path references from a function call (recursively).
     */
    private fun extractDataPaths(obj: Any?): Set<String> {
        val paths = mutableSetOf<String>()
        when (obj) {
            is String -> {
                // Extract all ${...} interpolation references from strings, handling nesting
                extractPathsFromTemplate(obj, paths)
            }
            is Map<*, *> -> {
                // Check if this map is a path reference
                val path = obj["path"] as? String
                if (path != null) {
                    paths.add(path)
                }
                // Recurse into all values
                for ((_, v) in obj) {
                    paths.addAll(extractDataPaths(v))
                }
            }
            is List<*> -> {
                for (item in obj) {
                    paths.addAll(extractDataPaths(item))
                }
            }
        }
        return paths
    }

    /**
     * Extract all data path references from a template string with nested ${...} support.
     */
    private fun extractPathsFromTemplate(template: String, paths: MutableSet<String>) {
        var i = 0
        while (i < template.length) {
            if (i < template.length - 1 && template[i] == '$' && template[i + 1] == '{') {
                val exprStart = i + 2
                val exprEnd = findMatchingBrace(template, exprStart)
                if (exprEnd == -1) {
                    i++
                } else {
                    val expr = template.substring(exprStart, exprEnd)
                    extractPathsFromExpr(expr, paths)
                    i = exprEnd + 1
                }
            } else {
                i++
            }
        }
    }

    /**
     * Extract data paths from a single interpolation expression.
     * Recursively handles nested ${...} within function call arguments.
     */
    private fun extractPathsFromExpr(expr: String, paths: MutableSet<String>) {
        // Check if it's a function call: funcName(...)
        val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(expr)
        if (funcMatch != null) {
            // Recursively extract paths from the function arguments
            val argsStr = funcMatch.groupValues[2]
            extractPathsFromTemplate(argsStr, paths)
        } else {
            // It's a path reference
            if (expr.startsWith("/")) {
                paths.add(expr)
            } else {
                paths.add("/$expr")
            }
        }
    }

    /**
     * Evaluate a function call against the current data model.
     * Supports: formatDate, formatNumber, formatCurrency, formatString, pluralize.
     */
    private fun evaluateFunctionCall(funcCall: Map<*, *>): String? {
        val call = funcCall["call"] as? String ?: return null
        @Suppress("UNCHECKED_CAST")
        val args = funcCall["args"] as? Map<String, Any?> ?: return null

        return when (call) {
            "formatDate" -> evaluateFormatDate(args)
            "formatNumber" -> evaluateFormatNumber(args)
            "formatCurrency" -> evaluateFormatCurrency(args)
            "formatString" -> evaluateFormatString(args)
            "pluralize" -> evaluatePluralize(args)
            else -> evaluateCustomFunctionAsString(call, args)
        }
    }

    /**
     * Resolve a dynamic value argument (could be literal, path reference, or nested function call).
     */
    private fun resolveDynamicArg(arg: Any?): Any? {
        if (arg == null) return null
        if (arg is String) return arg
        if (arg is Number) return arg
        if (arg is Boolean) return arg
        if (arg is Map<*, *>) {
            // Path reference: {"path": "/key"}
            val path = arg["path"] as? String
            if (path != null) {
                val key = path.removePrefix("/")
                return resolveNestedDataPath(key)
            }
            // Nested function call: {"call": "...", "args": {...}}
            val call = arg["call"] as? String
            if (call != null) {
                return evaluateFunctionCall(arg)
            }
        }
        return arg.toString()
    }

    /**
     * Evaluate formatDate function.
     * Per catalog: formats a timestamp into a string using a Unicode TR35 pattern.
     * Args: value (date string), format (pattern string)
     */
    private fun evaluateFormatDate(args: Map<String, Any?>): String? {
        val valueArg = resolveDynamicArg(args["value"]) ?: return null
        val formatArg = resolveDynamicArg(args["format"]) as? String ?: return null
        val dateStr = valueArg.toString()

        if (dateStr.isEmpty()) return null

        // Parse ISO 8601 date/time string and format according to pattern
        return formatDateString(dateStr, formatArg)
    }

    /**
     * Simple date formatter that handles common TR35 patterns.
     * Parses ISO 8601 dates and applies format patterns.
     */
    private fun formatDateString(isoDate: String, pattern: String): String {
        // Parse ISO 8601: "2025-12-15" or "2025-12-15T10:15:00Z"
        val datePart: String
        val timePart: String?

        if (isoDate.contains("T")) {
            val parts = isoDate.split("T")
            datePart = parts[0]
            timePart = parts.getOrNull(1)?.removeSuffix("Z")?.removeSuffix("+00:00")
        } else {
            datePart = isoDate
            timePart = null
        }

        val dateParts = datePart.split("-")
        if (dateParts.size < 3) return isoDate

        val year = dateParts[0].toIntOrNull() ?: return isoDate
        val month = dateParts[1].toIntOrNull() ?: return isoDate
        val day = dateParts[2].toIntOrNull() ?: return isoDate

        var hour = 0
        var minute = 0
        var second = 0
        if (timePart != null) {
            val timeParts = timePart.split(":")
            hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            second = timeParts.getOrNull(2)?.split(".")?.get(0)?.toIntOrNull() ?: 0
        }

        val monthNames = listOf("", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val monthShort = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayShort = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Calculate day of week
        val dow = calculateDayOfWeek(year, month, day)

        // Calculate ISO 8601 week-year & week-of-year (used by TR-35 tokens Y/w).
        // These may differ from the calendar year at year boundaries: e.g. 2027-01-01
        // is a Friday, so its ISO week is 2026-W53 (week-year=2026), while the calendar
        // year is 2027.
        val (weekYear, weekOfYear) = calculateIsoWeek(year, month, day)

        // Time formatting
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val amPm = if (hour < 12) "AM" else "PM"

        // Token-based replacement to avoid conflicts
        // Parse the pattern into tokens and replace each token with its value
        val result = StringBuilder()
        var i = 0
        while (i < pattern.length) {
            val remaining = pattern.substring(i)
            val consumed = when {
                remaining.startsWith("EEEE") -> { result.append(dayNames[dow]); 4 }
                remaining.startsWith("EEE") -> { result.append(dayShort[dow]); 3 }
                remaining.startsWith("E") -> { result.append(dayShort[dow]); 1 }
                remaining.startsWith("MMMM") -> { result.append(monthNames[month]); 4 }
                remaining.startsWith("MMM") -> { result.append(monthShort[month]); 3 }
                remaining.startsWith("MM") -> { result.append(month.toString().padStart(2, '0')); 2 }
                remaining.startsWith("M") -> { result.append(month.toString()); 1 }
                remaining.startsWith("yyyy") -> { result.append(year.toString()); 4 }
                remaining.startsWith("yy") -> { result.append((year % 100).toString().padStart(2, '0')); 2 }
                // TR-35 `Y` = week-based year (ISO 8601). Different from `y` on
                // year-boundary weeks. YYYY is 4-digit, YY is last-2-digit, Y is unpadded.
                remaining.startsWith("YYYY") -> { result.append(weekYear.toString().padStart(4, '0')); 4 }
                remaining.startsWith("YY") -> { result.append((weekYear % 100).toString().padStart(2, '0')); 2 }
                remaining.startsWith("Y") -> { result.append(weekYear.toString()); 1 }
                remaining.startsWith("dd") -> { result.append(day.toString().padStart(2, '0')); 2 }
                remaining.startsWith("d") -> { result.append(day.toString()); 1 }
                remaining.startsWith("HH") -> { result.append(hour.toString().padStart(2, '0')); 2 }
                remaining.startsWith("H") -> { result.append(hour.toString()); 1 }
                remaining.startsWith("hh") -> { result.append(hour12.toString().padStart(2, '0')); 2 }
                remaining.startsWith("h") -> { result.append(hour12.toString()); 1 }
                remaining.startsWith("mm") -> { result.append(minute.toString().padStart(2, '0')); 2 }
                remaining.startsWith("ss") -> { result.append(second.toString().padStart(2, '0')); 2 }
                // TR-35 `w` = ISO 8601 week-of-year (1..53). ww is 2-digit, w is unpadded.
                remaining.startsWith("ww") -> { result.append(weekOfYear.toString().padStart(2, '0')); 2 }
                remaining.startsWith("w") -> { result.append(weekOfYear.toString()); 1 }
                remaining.startsWith("a") -> { result.append(amPm); 1 }
                remaining.startsWith("'") -> {
                    // Quoted literal text
                    val endQuote = remaining.indexOf("'", 1)
                    if (endQuote > 0) {
                        result.append(remaining.substring(1, endQuote))
                        endQuote + 1
                    } else {
                        result.append("'")
                        1
                    }
                }
                else -> { result.append(pattern[i]); 1 }
            }
            i += consumed
        }

        return result.toString()
    }

    /**
     * Calculate day of week (0=Sunday, 1=Monday, ..., 6=Saturday)
     * using Tomohiko Sakamoto's algorithm.
     */
    private fun calculateDayOfWeek(year: Int, month: Int, day: Int): Int {
        val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        var y = year
        if (month < 3) y -= 1
        return (y + y / 4 - y / 100 + y / 400 + t[month - 1] + day) % 7
    }

    /**
     * ISO 8601 week-year and week-of-year.
     *
     * Rules (per ISO 8601 / TR-35):
     * - Weeks start on Monday (weekday 1..7 with Sunday = 7).
     * - Week 1 of a year is the week containing that year's first Thursday.
     * - As a consequence, dates in early January may belong to the last week of the
     *   previous year (week-year = year - 1), and dates in late December may belong
     *   to week 1 of the following year (week-year = year + 1).
     *
     * Returns Pair(weekYear, weekOfYear).
     */
    private fun calculateIsoWeek(year: Int, month: Int, day: Int): Pair<Int, Int> {
        val ordinal = dayOfYear(year, month, day)
        val isoDow = isoWeekday(year, month, day) // 1=Mon..7=Sun
        // Week number formula (from ISO 8601):
        //   week = (ordinal - isoDow + 10) / 7
        val week = (ordinal - isoDow + 10) / 7
        return when {
            week < 1 -> Pair(year - 1, lastIsoWeekOfYear(year - 1))
            week > lastIsoWeekOfYear(year) -> Pair(year + 1, 1)
            else -> Pair(year, week)
        }
    }

    /** ISO weekday: Monday=1 .. Sunday=7. */
    private fun isoWeekday(year: Int, month: Int, day: Int): Int {
        // calculateDayOfWeek returns 0=Sun..6=Sat; convert to 1=Mon..7=Sun.
        val dow = calculateDayOfWeek(year, month, day)
        return if (dow == 0) 7 else dow
    }

    /** Day-of-year (1-based). */
    private fun dayOfYear(year: Int, month: Int, day: Int): Int {
        val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var d = day
        for (m in 1 until month) d += daysInMonth[m - 1]
        if (month > 2 && isLeapYear(year)) d += 1
        return d
    }

    private fun isLeapYear(y: Int): Boolean =
        (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

    /**
     * Number of ISO weeks in a given year (52 or 53).
     * A year has 53 weeks iff Jan 1 is Thursday, or Dec 31 is Thursday
     * (equivalently: Jan 1 is Wed AND leap year).
     */
    private fun lastIsoWeekOfYear(year: Int): Int {
        val jan1 = isoWeekday(year, 1, 1)
        val dec31 = isoWeekday(year, 12, 31)
        return if (jan1 == 4 || dec31 == 4) 53 else 52
    }

    /**
     * Format a double with specified decimal places (Kotlin Multiplatform compatible).
     */
    private fun formatDouble(value: Double, decimals: Int): String {
        // Split the integer and fractional parts via Long arithmetic instead of
        // Double.toString(). Double.toString() emits scientific notation for large
        // magnitudes (e.g. 850000000000.0 -> "8.5E11"), which the previous substring
        // logic mangled into "8.5E". Long.toString() never uses scientific notation.
        val negative = value < 0
        val abs = kotlin.math.abs(value)
        var factor = 1L
        repeat(decimals) { factor *= 10L }
        val scaled = kotlin.math.round(abs * factor).toLong()
        val intPart = scaled / factor
        val fracPart = scaled % factor
        val sign = if (negative) "-" else ""
        return if (decimals > 0) {
            "$sign$intPart.${fracPart.toString().padStart(decimals, '0')}"
        } else {
            "$sign$intPart"
        }
    }

    /**
     * Convert a Double to a plain decimal string without scientific notation.
     * Kotlin's Double.toString() emits scientific notation for large magnitudes
     * (>= 1e7) and very small ones (< 1e-3), e.g. 0.0001 -> "1.0E-4". This expands
     * such forms into plain decimals and strips redundant trailing zeros.
     */
    private fun doubleToPlainString(num: Double): String {
        val s = num.toString()
        if (!s.contains('E') && !s.contains('e')) return s

        val negative = s.startsWith("-")
        val body = if (negative) s.substring(1) else s
        val eIndex = body.indexOfFirst { it == 'E' || it == 'e' }
        val mantissa = body.substring(0, eIndex)
        val exp = body.substring(eIndex + 1).toInt()
        val dot = mantissa.indexOf('.')
        val digits = mantissa.replace(".", "")
        // Decimal point position within `digits` after applying the exponent.
        val pointPos = (if (dot < 0) mantissa.length else dot) + exp

        val sb = StringBuilder()
        when {
            pointPos <= 0 -> {
                sb.append("0.")
                repeat(-pointPos) { sb.append('0') }
                sb.append(digits)
            }
            pointPos >= digits.length -> {
                sb.append(digits)
                repeat(pointPos - digits.length) { sb.append('0') }
            }
            else -> {
                sb.append(digits.substring(0, pointPos))
                sb.append('.')
                sb.append(digits.substring(pointPos))
            }
        }

        var result = sb.toString()
        if (result.contains('.')) {
            result = result.trimEnd('0').trimEnd('.')
        }
        return (if (negative) "-" else "") + result
    }

    /**
     * Evaluate formatNumber function.
     *
     * Per A2UI v0.9 basic catalog, `formatNumber` accepts:
     * - value (DynamicNumber, required)
     * - decimals (DynamicNumber, optional): digits after decimal point (with rounding).
     * - grouping (DynamicBoolean, optional, default true): whether to add thousands separator.
     *
     * Notes:
     * - Inline function calls (from formatString) may pass args as strings, so we accept
     *   "3" -> Int and "true"/"false" -> Boolean here.
     * - `precision` is kept as a legacy alias for backward compatibility.
     */
    private fun evaluateFormatNumber(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val num = when (value) {
            is Number -> value.toDouble()
            // Robust parse: nested calls may pass a formatted string with thousands
            // separators like "1,234.50". Strip commas before parsing.
            is String -> value.replace(",", "").toDoubleOrNull() ?: return value
            else -> return value.toString()
        }
        val decimals = coerceToInt(resolveDynamicArg(args["decimals"]))
            ?: coerceToInt(resolveDynamicArg(args["precision"])) // legacy alias
        val grouping = coerceToBool(resolveDynamicArg(args["grouping"])) ?: true

        val formatted = if (decimals != null) {
            // Explicit decimals: always round to that number of digits (including 0.00 for zero).
            formatDouble(num, decimals)
        } else {
            // No decimals specified: keep integers as int, non-integers in plain form.
            if (num == num.toLong().toDouble()) {
                num.toLong().toString()
            } else {
                doubleToPlainString(num)
            }
        }
        return if (grouping) addThousandsSeparator(formatted) else formatted
    }

    /** Coerce Number/String to Int (accepts "3", 3.0, etc.); returns null on failure. */
    private fun coerceToInt(v: Any?): Int? = when (v) {
        null -> null
        is Number -> v.toInt()
        is String -> v.trim().toDoubleOrNull()?.toInt()
        else -> null
    }

    /** Coerce Boolean/String ("true"/"false") to Boolean; returns null on failure. */
    private fun coerceToBool(v: Any?): Boolean? = when (v) {
        null -> null
        is Boolean -> v
        is String -> when (v.trim().lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }
        else -> null
    }

    /**
     * Add thousands separator (comma) to a formatted number string.
     */
    private fun addThousandsSeparator(numStr: String): String {
        val parts = numStr.split(".")
        val intPart = parts[0]
        val isNegative = intPart.startsWith("-")
        val digits = if (isNegative) intPart.substring(1) else intPart
        val result = StringBuilder()
        for ((i, c) in digits.reversed().withIndex()) {
            if (i > 0 && i % 3 == 0) {
                result.append(',')
            }
            result.append(c)
        }
        val intFormatted = (if (isNegative) "-" else "") + result.reverse().toString()
        return if (parts.size > 1) "$intFormatted.${parts[1]}" else intFormatted
    }

    /**
     * Evaluate formatCurrency function.
     */
    private fun evaluateFormatCurrency(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val currency = resolveDynamicArg(args["currency"]) as? String ?: "USD"
        val num = when (value) {
            is Number -> value.toDouble()
            // Robust parse: nested calls (e.g. formatNumber) may pass a formatted string
            // with thousands separators like "1,234.50". Strip commas before parsing.
            is String -> value.replace(",", "").toDoubleOrNull() ?: return value
            else -> return value.toString()
        }
        val symbol = when (currency.uppercase()) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "CNY" -> "¥"
            else -> currency
        }
        val formatted = formatDouble(num, 2)
        // Add thousands separator using existing helper (handles decimal part internally)
        val withCommas = addThousandsSeparator(formatted)
        return "$symbol$withCommas"
    }

    /**
     * Evaluate formatString function (string interpolation).
     * @param itemData optional scope data for resolving relative paths in ChildList templates.
     */
    private fun evaluateFormatString(args: Map<String, Any?>, itemData: Map<String, Any?>? = null): String? {
        val template = resolveDynamicArg(args["value"]) as? String ?: return null
        return resolveFormatStringTemplate(template, itemData)
    }

    /**
     * Resolve a formatString template with support for nested ${...} expressions.
     * Handles: ${/path}, ${relativePath}, ${funcName(arg: value)}, ${func(arg: ${/nested})}
     */
    private fun resolveFormatStringTemplate(template: String, itemData: Map<String, Any?>? = null): String {
        val result = StringBuilder()
        var i = 0
        while (i < template.length) {
            // Check for escaped \${
            if (i < template.length - 1 && template[i] == '\\' && template[i + 1] == '$') {
                result.append('$')
                i += 2
                continue
            }
            // Check for ${ start
            if (i < template.length - 1 && template[i] == '$' && template[i + 1] == '{') {
                // Find the matching closing } considering nesting
                val exprStart = i + 2
                val exprEnd = findMatchingBrace(template, exprStart)
                if (exprEnd == -1) {
                    // No matching brace found, output as-is
                    result.append(template[i])
                    i++
                } else {
                    val expr = template.substring(exprStart, exprEnd)
                    result.append(evaluateInterpolationExpr(expr, itemData))
                    i = exprEnd + 1 // skip past the closing }
                }
            } else {
                result.append(template[i])
                i++
            }
        }
        return result.toString()
    }

    /**
     * Find the matching closing brace '}' starting from position `start`,
     * accounting for nested ${...} pairs.
     * Returns the index of the matching '}', or -1 if not found.
     */
    private fun findMatchingBrace(s: String, start: Int): Int {
        var depth = 1
        var i = start
        while (i < s.length) {
            if (i < s.length - 1 && s[i] == '$' && s[i + 1] == '{') {
                depth++
                i += 2
            } else if (s[i] == '}') {
                depth--
                if (depth == 0) return i
                i++
            } else {
                i++
            }
        }
        return -1
    }

    /**
     * Evaluate a single interpolation expression (the content between ${ and }).
     * Could be: a path like "/reviewCount", a relative path like "temp",
     * or a function call like "formatNumber(value: ${/reviewCount})".
     */
    private fun evaluateInterpolationExpr(expr: String, itemData: Map<String, Any?>? = null): String {
        // Check if it's a function call: funcName(...)
        val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(expr)
        if (funcMatch != null) {
            val funcName = funcMatch.groupValues[1]
            val argsStr = funcMatch.groupValues[2]
            return evaluateInlineFunctionCall(funcName, argsStr, itemData)
        }

        // Otherwise it's a path reference
        val resolved = if (expr.startsWith("/")) {
            // Absolute path
            resolveNestedDataPath(expr.removePrefix("/"))
        } else {
            // Relative path - resolve from itemData first, then fallback to dataModel
            val fromItem = itemData?.let { resolveRelativePath(expr, it) }
            if (fromItem != null && fromItem != "") fromItem else resolveNestedDataPath(expr)
        }
        return stringifyForInterpolation(resolved)
    }

    /**
     * Convert a resolved value to its interpolation string form.
     * Per A2UI v0.9 protocol (docs/a2ui_protocol.md):
     *   Objects/Arrays: Stringified as JSON to ensure consistency across different client implementations.
     *   Primitives are stringified using their natural representation.
     */
    private fun stringifyForInterpolation(value: Any?): String = when (value) {
        null -> ""
        is Map<*, *>, is List<*> -> toJsonString(value)
        else -> value.toString()
    }

    /**
     * Minimal JSON stringifier for Map/List/String/Number/Boolean/null.
     * Kept private to this adapter to avoid taking on a serialization dependency; the output
     * conforms to RFC 8259 for the value types actually seen in a2ui data models.
     */
    private fun toJsonString(value: Any?): String {
        val sb = StringBuilder()
        appendJson(sb, value)
        return sb.toString()
    }

    private fun appendJson(sb: StringBuilder, value: Any?) {
        when (value) {
            null -> sb.append("null")
            is Boolean -> sb.append(value.toString())
            is Number -> {
                // Represent whole doubles without ".0" tail; keep others plain (no scientific).
                val d = value.toDouble()
                if (value is Double || value is Float) {
                    if (!d.isFinite()) {
                        sb.append("null") // JSON has no Infinity/NaN
                    } else if (d == d.toLong().toDouble()) {
                        sb.append(d.toLong().toString())
                    } else {
                        sb.append(doubleToPlainString(d))
                    }
                } else {
                    sb.append(value.toString())
                }
            }
            is String -> appendJsonString(sb, value)
            is Map<*, *> -> {
                sb.append('{')
                var first = true
                for ((k, v) in value) {
                    if (!first) sb.append(',')
                    first = false
                    appendJsonString(sb, k?.toString() ?: "")
                    sb.append(':')
                    appendJson(sb, v)
                }
                sb.append('}')
            }
            is List<*> -> {
                sb.append('[')
                var first = true
                for (item in value) {
                    if (!first) sb.append(',')
                    first = false
                    appendJson(sb, item)
                }
                sb.append(']')
            }
            else -> appendJsonString(sb, value.toString())
        }
    }

    private fun appendJsonString(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    sb.append("\\u")
                    val hex = c.code.toString(16)
                    repeat(4 - hex.length) { sb.append('0') }
                    sb.append(hex)
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append('"')
    }

    /**
     * Evaluate an inline function call parsed from a formatString interpolation.
     * e.g. funcName = "formatNumber", argsStr = "value: ${/reviewCount}"
     */
    private fun evaluateInlineFunctionCall(funcName: String, argsStr: String, itemData: Map<String, Any?>? = null): String {
        val args = parseInlineFunctionArgs(argsStr, itemData)
        return when (funcName) {
            "formatDate" -> evaluateFormatDate(args) ?: ""
            "formatNumber" -> evaluateFormatNumber(args) ?: ""
            "formatCurrency" -> evaluateFormatCurrency(args) ?: ""
            "formatString" -> evaluateFormatString(args, itemData) ?: ""
            "pluralize" -> evaluatePluralize(args) ?: ""
            else -> evaluateCustomFunctionAsString(funcName, args) ?: ""
        }
    }

    /**
     * Parse inline function arguments from a string like "value: ${/reviewCount}, one: 'review', other: 'reviews'"
     * Returns a map of argument name to resolved value.
     */
    private fun parseInlineFunctionArgs(argsStr: String, itemData: Map<String, Any?>? = null): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        // Split by commas, but respect nested ${...} and quoted strings
        val argPairs = splitArguments(argsStr)
        for (pair in argPairs) {
            val colonIdx = pair.indexOf(':')
            if (colonIdx == -1) continue
            val key = pair.substring(0, colonIdx).trim()
            val valueStr = pair.substring(colonIdx + 1).trim()
            result[key] = resolveInlineArgValue(valueStr, itemData)
        }
        return result
    }

    /**
     * Split argument string by top-level commas, respecting nested ${...} and quotes.
     */
    private fun splitArguments(argsStr: String): List<String> {
        val parts = mutableListOf<String>()
        var depth = 0
        var inSingleQuote = false
        var inDoubleQuote = false
        var start = 0
        var i = 0
        while (i < argsStr.length) {
            val c = argsStr[i]
            when {
                c == '\'' && !inDoubleQuote -> inSingleQuote = !inSingleQuote
                c == '"' && !inSingleQuote -> inDoubleQuote = !inDoubleQuote
                !inSingleQuote && !inDoubleQuote -> {
                    if (i < argsStr.length - 1 && c == '$' && argsStr[i + 1] == '{') {
                        depth++
                        i += 2
                        continue
                    } else if (c == '}' && depth > 0) {
                        depth--
                    } else if (c == ',' && depth == 0) {
                        parts.add(argsStr.substring(start, i))
                        start = i + 1
                    }
                }
            }
            i++
        }
        if (start < argsStr.length) {
            parts.add(argsStr.substring(start))
        }
        return parts
    }

    /**
     * Resolve an inline argument value. Could be:
     * - A quoted string: 'review' or "review"
     * - A ${...} expression: ${/reviewCount}
     * - A plain number or identifier
     */
    private fun resolveInlineArgValue(valueStr: String, itemData: Map<String, Any?>? = null): Any? {
        val trimmed = valueStr.trim()
        // Quoted string (single or double quotes)
        if ((trimmed.startsWith("'") && trimmed.endsWith("'")) ||
            (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        // ${...} expression — resolve while preserving the original type.
        // For simple path references (e.g. ${/originalPrice}), return the raw
        // dataModel value (Number, Boolean, String, etc.) so downstream
        // serialization keeps the correct JSON type. For nested function calls
        // (e.g. ${funcName(...)}), delegate to evaluateInlineFunctionCall which
        // returns String (acceptable since the outer context is interpolation).
        if (trimmed.startsWith("\${") && trimmed.endsWith("}")) {
            val innerExpr = trimmed.substring(2, trimmed.length - 1)
            // Check if it's a function call: funcName(...)
            val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(innerExpr)
            if (funcMatch != null) {
                // Nested function call — returns String
                val funcName = funcMatch.groupValues[1]
                val argsStr = funcMatch.groupValues[2]
                return evaluateInlineFunctionCall(funcName, argsStr, itemData)
            }
            // Simple path reference — return the raw value preserving its type
            return if (innerExpr.startsWith("/")) {
                resolveNestedDataPath(innerExpr.removePrefix("/"))
            } else {
                val fromItem = itemData?.let { resolveRelativePath(innerExpr, it) }
                if (fromItem != null && fromItem != "") fromItem else resolveNestedDataPath(innerExpr)
            }
        }
        // Number
        trimmed.toDoubleOrNull()?.let { return it }
        trimmed.toIntOrNull()?.let { return it }
        // Plain string (could be a path reference)
        return trimmed
    }

    /**
     * Evaluate pluralize function.
     */
    private fun evaluatePluralize(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val count = when (value) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
        // CLDR plural categories for English: one, other
        val one = resolveDynamicArg(args["one"]) as? String
        val other = resolveDynamicArg(args["other"]) as? String
        val zero = resolveDynamicArg(args["zero"]) as? String

        return when {
            count == 0 && zero != null -> zero
            count == 1 && one != null -> one
            other != null -> other
            else -> ""
        }
    }

    /**
     * Extract children IDs from a component map.
     * Handles both "children" (list) and "child" (single) references.
     * Note: ChildList template bindings (children as Map) are NOT included here
     * because they are expanded dynamically via expandChildListBindings().
     */
    private fun getChildrenIds(compMap: Map<String, Any?>): List<String> {
        val result = mutableListOf<String>()
        val children = compMap["children"]
        val child = compMap["child"] as? String

        if (children is List<*>) {
            for (childId in children) {
                if (childId is String) {
                    result.add(childId)
                }
            }
        }
        // Note: children as Map (ChildList template binding) is handled separately
        if (child != null) {
            result.add(child)
        }
        return result
    }

    // region ChildList Template Binding

    /**
     * Expand all ChildList template bindings based on current data model.
     * For each binding, reads the array from the data model and instantiates
     * the template component for each array item.
     */
    private fun expandChildListBindings(surfaceId: String): List<A2UIMessage> {
        return expandChildListBindings(surfaceId, onlyParents = null)
    }

    /**
     * Whether [componentId] belongs to a Modal content subtree whose overlay is not
     * currently open. Such components have a protocol tag but no view yet.
     */
    private fun isInClosedModalContent(componentId: String): Boolean {
        if (componentId !in modalDeferredIds) return false
        val tag = idToTag[componentId] ?: return true
        return modalOverlayTags.values.none { tags -> tags.any { it.first == tag } }
    }

    /**
     * @param onlyParents when non-null, only bindings whose parent id is in this set are
     *   expanded (used by [openModal] to materialize lists inside the new overlay).
     */
    private fun expandChildListBindings(surfaceId: String, onlyParents: Set<String>?): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()

        for ((parentId, binding) in childListBindings) {
            if (onlyParents != null && parentId !in onlyParents) continue
            // Modal content is created lazily when the modal opens. Its parent already
            // owns a tag, but there is no view for it yet, so expanding now would make
            // the applier fall back to the surface root and render the list outside
            // the card. openModal() expands these bindings once the overlay exists.
            if (isInClosedModalContent(parentId)) continue

            // Inline ChildList bindings whose `path` is relative (no leading
            // `/`) only make sense inside a template scope. They are expanded
            // in-place by `instantiateSubtree` when the enclosing template is
            // being materialized. Skip them here to avoid noisy failures
            // against the root data model.
            if (!binding.dataPath.startsWith("/")) {
                continue
            }

            val dataKey = binding.dataPath.removePrefix("/")
            val arrayData = resolveNestedDataPath(dataKey)
            if (arrayData !is List<*>) {
                continue
            }

            val parentTag = idToTag[parentId]
            if (parentTag == null) {
                continue
            }
            val templateDef = idToOriginalDef[binding.templateId]
            if (templateDef == null) {
                continue
            }
            val templateComponentType = templateDef["component"] as? String ?: continue

            // Remove previously generated instances for this parent
            val previousInstances = binding.generatedInstanceIds.toList()
            for (instanceId in previousInstances) {
                val instanceTag = idToTag[instanceId] ?: continue
                messages.add(A2UIMessage(
                    type = A2UIMessageType.REMOVE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = instanceTag,
                    parentTag = parentTag
                ))
                // Also remove children of this instance
                removeInstanceTree(instanceId, surfaceId, parentTag, messages)
            }
            binding.generatedInstanceIds.clear()

            // Generate new instances for each array item
            for ((index, item) in arrayData.withIndex()) {
                if (item !is Map<*, *>) continue
                @Suppress("UNCHECKED_CAST")
                val itemData = item as Map<String, Any?>
                val scopePath = "/${dataKey}/$index"

                // Instantiate the outer template subtree; nested inline
                // ChildLists (relative or absolute) are handled recursively
                // inside instantiateSubtree so arbitrary nesting depths work.
                messages.addAll(
                    instantiateSubtree(
                        originalId = binding.templateId,
                        parentInstanceTag = parentTag,
                        indexInSiblings = index,
                        indexPath = "$index",
                        itemData = itemData,
                        scopePath = scopePath,
                        surfaceId = surfaceId,
                        binding = binding
                    )
                )
            }
        }

        return messages
    }

    /**
     * If a component definition declares its `children` as a ChildList template
     * binding (i.e. `children: { componentId, path }`), return the pair
     * `(path, componentId)`. Returns null for regular children lists or single
     * child references.
     *
     * Supports both absolute paths (e.g. `/sections`) and relative paths (e.g.
     * `items`). Relative paths are resolved against the enclosing template scope
     * at expansion time.
     */
    private fun getInlineChildListBinding(compDef: Map<String, Any?>): Pair<String, String>? {
        val children = compDef["children"] as? Map<*, *> ?: return null
        val path = children["path"] as? String ?: return null
        val templateId = children["componentId"] as? String ?: return null
        return path to templateId
    }

    /**
     * Resolve a `path` (absolute or relative) into a value using the current
     * scope's `itemData`. Absolute paths (leading `/`) are resolved from the
     * root data model with fallback to the relative form for spec-lenient
     * cards; relative paths are resolved against `itemData` only.
     */
    private fun resolveScopePathValue(path: String, itemData: Map<String, Any?>): Any? {
        return if (path.startsWith("/")) {
            val key = path.removePrefix("/")
            resolveNestedDataPath(key)
                ?: if (itemData.isNotEmpty()) resolveRelativePath(key, itemData) else null
        } else {
            resolveRelativePath(path, itemData)
        }
    }

    /**
     * Instantiate an entire template subtree rooted at `originalId` under
     * `parentInstanceTag`, using the given scope. Handles:
     *
     *  - regular `children` (list of ids) — recurse into each child
     *  - single `child` reference — recurse
     *  - inline ChildList binding (`children: { componentId, path }`) — resolve
     *    the array from the current scope and recurse the inner template for
     *    each array item, forming a NEW deeper scope. Supports arbitrary
     *    nesting depth.
     *
     * `indexPath` is a stable string built from the outer→inner array indices
     * (joined with `_`, e.g. `0_2_1`) so instance IDs remain unique across
     * nesting levels while still being decodable by `getOriginalComponentId`
     * (which splits at the last `__`).
     *
     * All generated instance IDs (including those produced by deeper nested
     * ChildList expansions) are appended to the outermost `binding`'s
     * `generatedInstanceIds`, ensuring wholesale cleanup on data re-expansion.
     */
    private fun instantiateSubtree(
        originalId: String,
        parentInstanceTag: Int,
        indexInSiblings: Int,
        indexPath: String,
        itemData: Map<String, Any?>,
        scopePath: String,
        surfaceId: String,
        binding: ChildListBinding
    ): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        val compDef = idToOriginalDef[originalId] ?: return messages
        val componentType = compDef["component"] as? String ?: return messages

        val instanceId = "${originalId}__${indexPath}"
        binding.generatedInstanceIds.add(instanceId)

        if (!idToTag.containsKey(instanceId)) {
            idToTag[instanceId] = nextTag++
        }
        val instanceTag = idToTag[instanceId]!!
        instanceScopePath[instanceId] = scopePath
        tagToComponentName[instanceTag] = componentType

        val viewName = A2UIComponentMapper.mapComponentName(componentType)
        val props = buildPropsWithScope(componentType, compDef, instanceId, itemData, scopePath)

        messages.add(
            A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = instanceTag,
                viewName = viewName,
                parentTag = parentInstanceTag,
                index = indexInSiblings,
                props = props
            )
        )

        // Branch 1: inline ChildList binding — expand the inner template per
        // array item with a deeper scope. This is what enables arbitrary-depth
        // nested ChildLists (e.g. sections → items → sub-items → ...).
        val inline = getInlineChildListBinding(compDef)
        if (inline != null) {
            val (innerPath, innerTemplateId) = inline
            val innerArray = resolveScopePathValue(innerPath, itemData)
            if (innerArray is List<*>) {
                // Compose a readable scope path for downstream absolute-path
                // fallback behavior. For an absolute inner path we use it as-is;
                // for a relative inner path we append it under the current scope.
                val innerScopeBase = if (innerPath.startsWith("/")) {
                    innerPath
                } else {
                    "${scopePath}/${innerPath}"
                }
                for ((innerIdx, innerItem) in innerArray.withIndex()) {
                    if (innerItem !is Map<*, *>) continue
                    @Suppress("UNCHECKED_CAST")
                    val innerItemData = innerItem as Map<String, Any?>
                    val innerScopePath = "${innerScopeBase}/${innerIdx}"
                    val innerIndexPath = "${indexPath}_${innerIdx}"
                    messages.addAll(
                        instantiateSubtree(
                            originalId = innerTemplateId,
                            parentInstanceTag = instanceTag,
                            indexInSiblings = innerIdx,
                            indexPath = innerIndexPath,
                            itemData = innerItemData,
                            scopePath = innerScopePath,
                            surfaceId = surfaceId,
                            binding = binding
                        )
                    )
                }
            }
            return messages
        }

        // Branch 2: regular children — recurse preserving current scope.
        val childrenIds = getChildrenIds(compDef)
        for ((childIndex, childId) in childrenIds.withIndex()) {
            messages.addAll(
                instantiateSubtree(
                    originalId = childId,
                    parentInstanceTag = instanceTag,
                    indexInSiblings = childIndex,
                    indexPath = "${indexPath}_${childIndex}",
                    itemData = itemData,
                    scopePath = scopePath,
                    surfaceId = surfaceId,
                    binding = binding
                )
            )
        }

        return messages
    }

    /**
     * Build props for a component within a ChildList template scope.
     * Relative paths (not starting with "/") are resolved against the current item data.
     */
    private fun buildPropsWithScope(
        componentType: String,
        compMap: Map<String, Any?>,
        instanceId: String,
        itemData: Map<String, Any?>,
        scopePath: String
    ): Map<String, Any?> {
        val props = mutableMapOf<String, Any?>()
        val id = compMap["id"] as? String

        // Apply component-specific default layout props (same as buildProps)
        applyDefaultLayoutProps(componentType, props, instanceId)

        // Process component-specific properties with scope-aware resolution
        val skipKeys = setOf("id", "component", "children", "child", "accessibility", "action", "tabs")
        for ((key, value) in compMap) {
            if (key in skipKeys) continue

            // Kuikly `styles` extension: expand per-scope so binding values inside
            // styles (e.g. {"color": {"path": "textColor"}}) resolve against itemData.
            // We walk the original styles map (`value`) directly, filter via the
            // catalog whitelist, and hand each raw sub-value to resolveValueWithScope
            // so both absolute `/…` paths and relative item-scoped paths work.
            if (key == "styles" && value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val stylesMap = value as Map<String, Any?>
                for ((sk, _) in A2UIComponentMapper.expandStyles(componentType, stylesMap)) {
                    val rawSub = stylesMap[sk]
                    props[sk] = resolveValueWithScope(rawSub, itemData, instanceId)
                }
                clearDefaultsIfGradient(props)
                continue
            }

            val mapped = A2UIComponentMapper.mapProp(componentType, key, value)
            if (mapped != null) {
                val (propKey, propValue) = mapped
                if (propKey == "__variant" && propValue is String) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize(propValue)
                    val fontWeight = A2UIComponentMapper.getVariantFontWeight(propValue)
                    if (fontWeight != null) {
                        props["fontWeight"] = fontWeight
                    }
                    props["color"] = A2UIComponentMapper.getVariantColor(propValue)
                } else if (propKey.isNotEmpty()) {
                    // For data bindings, resolve using the ORIGINAL value (before mapProp processing)
                    // because mapProp's resolveTextValue may have extracted the path string,
                    // losing the Map structure needed for scope-aware resolution.
                    val resolved = resolveValueWithScope(value, itemData, instanceId)
                    // For Icon component, map resolved name to unicode char
                    if (componentType == "Icon" && propKey == "text" && resolved is String && resolved.isNotEmpty()) {
                        props[propKey] = A2UIComponentMapper.resolveIconNameToChar(resolved)
                    } else {
                        props[propKey] = resolved
                    }
                }
            }
        }

        // Handle action
        val action = compMap["action"]
        if (action != null) {
            val tag = idToTag[instanceId]
            if (tag != null) {
                // Store the raw action object; path references inside the action's
                // context (e.g. {"data": {"path": "/formData"}}) are resolved against
                // the live data model at dispatch time in getActionForTag().
                tagToAction[tag] = action
                props["touchEnable"] = 1
            }
        }

        // Handle weight
        val weight = compMap["weight"]
        if (weight != null) {
            props["flex"] = weight
        }

        // Parent-align-aware alignSelf override (template path). See the matching block
        // in buildProps for the rationale and cross-axis mapping. Handles both Column
        // (cross=width) and Row (cross=height) parents symmetrically.
        val parentIdForAlign = getOriginalParentId(instanceId)
        val parentDefForAlign = if (parentIdForAlign != null) idToOriginalDef[parentIdForAlign] else null
        val parentCompForAlign = parentDefForAlign?.get("component") as? String
        if (parentCompForAlign == "Column" || parentCompForAlign == "Row") {
            val parentAlign = parentDefForAlign["align"] as? String
            if (parentAlign == "start" || parentAlign == "center" || parentAlign == "end") {
                val originalId = getOriginalComponentId(instanceId)
                val originalComp = originalId?.let { idToOriginalDef[it]?.get("component") as? String }
                val explicitCrossSizeKey = if (parentCompForAlign == "Column") "width" else "height"
                val removeStretch = when {
                    // Text special-cased first (see buildProps for rationale).
                    originalComp == "Text" -> {
                        if (parentCompForAlign == "Column" &&
                            (parentAlign == "center" || parentAlign == "end")
                        ) {
                            props["__textAlignPostLayout"] = parentAlign
                        }
                        false  // keep stretch
                    }
                    // Explicit cross-axis size wins over Row/Column container heuristic
                    // (see buildProps for the decorative-Row/Column rationale).
                    props.containsKey(explicitCrossSizeKey) -> true
                    originalComp == "Row" || originalComp == "Column" -> false
                    originalComp == "Button" || originalComp == "Icon" -> true
                    else -> false
                }
                if (removeStretch && props["alignSelf"] == "stretch") {
                    props.remove("alignSelf")
                }
            }
        }

        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Resolve a value with scope-aware path resolution.
     * Relative paths (not starting with "/") are resolved against itemData.
     * Absolute paths (starting with "/") are resolved against the root data model.
     */
    private fun resolveValueWithScope(value: Any?, itemData: Map<String, Any?>, instanceId: String?): Any? {
        if (value == null) return null

        // Handle A2UIValue sealed class (type-safe path/function references)
        if (value is A2UIValue) {
            return when (value) {
                is A2UIValue.Literal -> value.value
                is A2UIValue.PathBinding -> {
                    if (value.isAbsolute) {
                        // Absolute path - resolve from root data model.
                        // Fallback: some cards use "/xxx" inside a list-template to
                        // reference the item's field (technically a spec violation
                        // where "xxx" would be the correct relative form). If the
                        // global lookup misses and we are inside a template scope,
                        // fall back to relative resolution against itemData so the
                        // card still renders. Per spec, unresolved paths render as
                        // empty strings, so this fallback never breaks correct data.
                        val absolute = resolveNestedDataPath(value.key)
                        if (absolute != null) absolute
                        else if (itemData.isNotEmpty()) resolveRelativePath(value.key, itemData)
                        else ""
                    } else {
                        // Relative path - resolve from current item data
                        resolveRelativePath(value.path, itemData)
                    }
                }
                is A2UIValue.IconPathBinding -> {
                    // Data-bound Icon `name` in image mode inside a template scope.
                    // Resolve the underlying path using the same absolute/relative rules
                    // as PathBinding, then translate the name through A2UIIconRegistry.
                    val inner = value.binding
                    val nameRaw: Any? = if (inner.isAbsolute) {
                        resolveNestedDataPath(inner.key)
                            ?: (if (itemData.isNotEmpty()) resolveRelativePath(inner.key, itemData) else null)
                    } else {
                        resolveRelativePath(inner.path, itemData)
                    }
                    A2UIIconRegistry.resolveIconUriOrFallback(nameRaw?.toString())
                }
                is A2UIValue.FunctionCall -> {
                    evaluateFunctionCallWithScope(value.definition, itemData)
                }
            }
        }

        // Legacy fallback for raw values not wrapped in A2UIValue
        if (value is String) return value
        if (value is Map<*, *>) {
            val pathRef = value["path"] as? String
            if (pathRef != null) {
                if (pathRef.startsWith("/")) {
                    // Absolute path - resolve from root data model, with template
                    // scope fallback (see A2UIValue.PathBinding branch above for
                    // rationale).
                    val key = pathRef.removePrefix("/")
                    val absolute = resolveNestedDataPath(key)
                    return absolute
                        ?: if (itemData.isNotEmpty()) resolveRelativePath(key, itemData) else ""
                } else {
                    // Relative path - resolve from current item data
                    return resolveRelativePath(pathRef, itemData)
                }
            }
            // Function call
            val call = value["call"] as? String
            if (call != null) {
                return evaluateFunctionCallWithScope(value, itemData)
            }
        }
        return value
    }

    /**
     * Resolve a relative path against item data.
     * Supports nested paths like "address/city", or the extended flatten form
     * with `.`/`[N]` (e.g. `items[0].name`). Returns "" when any segment misses
     * so the caller can render an empty placeholder gracefully.
     */
    private fun resolveRelativePath(path: String, itemData: Map<String, Any?>): Any? {
        val segments = parsePathSegments(path)
        if (segments.isEmpty()) return ""
        var current: Any? = itemData
        for (part in segments) {
            current = when (current) {
                is Map<*, *> -> current[part]
                is List<*> -> {
                    val idx = part.toIntOrNull()
                    if (idx != null && idx < current.size) current[idx] else null
                }
                else -> null
            }
            if (current == null) return ""
        }
        return current ?: ""
    }

    /**
     * Evaluate a function call with scope-aware argument resolution.
     */
    private fun evaluateFunctionCallWithScope(funcCall: Map<*, *>, itemData: Map<String, Any?>): String? {
        val call = funcCall["call"] as? String ?: return null
        @Suppress("UNCHECKED_CAST")
        val args = funcCall["args"] as? Map<String, Any?> ?: return null

        // Resolve args with scope
        val resolvedArgs = mutableMapOf<String, Any?>()
        for ((key, argValue) in args) {
            resolvedArgs[key] = resolveValueWithScope(argValue, itemData, null)
        }

        return when (call) {
            "formatDate" -> evaluateFormatDate(resolvedArgs)
            "formatNumber" -> evaluateFormatNumber(resolvedArgs)
            "formatCurrency" -> evaluateFormatCurrency(resolvedArgs)
            "formatString" -> evaluateFormatString(resolvedArgs, itemData)
            "pluralize" -> evaluatePluralize(resolvedArgs)
            else -> evaluateCustomFunctionAsString(call, resolvedArgs)
        }
    }

    /**
     * Apply default layout props for a component type (extracted from buildProps for reuse).
     */
    private fun applyDefaultLayoutProps(componentType: String, props: MutableMap<String, Any?>, instanceId: String) {
        when (componentType) {
            "Row" -> {
                props["flexDirection"] = "row"
                props["alignSelf"] = "stretch"
                // Mirror buildProps: auto-wrap overflowing children onto new lines.
                props["flexWrap"] = "wrap"
                props["alignContent"] = "flex-start"
            }
            "Column" -> {
                props["flexDirection"] = "column"
                props["alignSelf"] = "stretch"
                // Per catalog: default align is \"stretch\" — explicit for template path too.
                // The subsequent mapProp loop in buildPropsWithScope will overwrite this if
                // the original definition declares `align`.
                val originalId = getOriginalComponentId(instanceId)
                val originalHasAlign = idToOriginalDef[originalId]?.containsKey("align") == true
                if (!originalHasAlign) {
                    props["alignItems"] = "stretch"
                }                // When Column is inside a Row, flex to fill remaining space
                val parentIsRow = isParentRow(instanceId)
                if (parentIsRow) {
                    props["flex"] = 1
                }
            }
            "Icon" -> {
                // Mirror buildProps("Icon"): leaf component, 24dp width, 8dp margin.
                // The image-vs-text branch is a global routing decision that never changes
                // once the host app has finished registering icons, so we consult the same
                // helper the initial-create path uses — no per-instance state needed.
                props["width"] = 24
                props["margin"] = 8
                if (A2UIComponentMapper.isIconRenderedAsImage()) {
                    props["height"] = 24
                    props["resizeMode"] = "contain"
                } else {
                    props["fontSize"] = 20f
                    props["textAlign"] = "center"
                }
            }
            "Text" -> {
                props["margin"] = 8
                // Mirror buildProps: apply body-variant defaults when the source JSON
                // did not specify a variant, so template-instantiated Text nodes match
                // the A2UI catalog spec (variant="body" is the documented default).
                val originalId = getOriginalComponentId(instanceId)
                val originalVariant = originalId?.let { idToOriginalDef[it]?.get("variant") as? String }
                if (originalVariant == null) {
                    props["fontSize"] = A2UIComponentMapper.getVariantFontSize("body")
                    props["color"] = A2UIComponentMapper.getVariantColor("body")
                }
                // Check if parent is a Button
                val parentComp = getParentComponentType(instanceId)
                if (parentComp == "Button") {
                    val parentId = childToParentCache[instanceId] ?: getOriginalParentId(instanceId)
                    if (parentId != null) {
                        val buttonVariant = getParentButtonVariant(parentId)
                        if (buttonVariant == "primary") {
                            props["color"] = "#FFFFFF"
                        }
                    }
                    props["margin"] = 0
                } else if (parentComp == "Row") {
                    // Mirror buildProps: Text in Row needs post-layout maxWidth fix
                    props["__textInRowPostLayout"] = "true"
                } else if (parentComp == "Column" || parentComp == "Card") {
                    // Mirror buildProps: stretch text so the engine's Loop A pre-fill
                    // provides a width constraint via node.getLayoutDimension(crossAxis).
                    // For parent Column with align=center/end, the end-of-function logic
                    // adds __textAlignPostLayout hint; the applier fixes alignment post-layout.
                    props["alignSelf"] = "stretch"
                }
            }
            "Image" -> {
                props["margin"] = 8
                // Look up variant from original component definition
                val originalId = getOriginalComponentId(instanceId)
                val originalDef = if (originalId != null) idToOriginalDef[originalId] else null
                val variant = originalDef?.get("variant") as? String ?: "mediumFeature"
                val parentIsRow = isParentRow(instanceId)

                when (variant) {
                    "icon" -> {
                        props["width"] = 24
                        props["height"] = 24
                        props["margin"] = 0
                    }
                    "avatar" -> {
                        props["width"] = 40
                        props["height"] = 40
                        props["borderRadius"] = "20"
                    }
                    "smallFeature" -> {
                        props["width"] = 100
                        props["height"] = 100
                    }
                    "mediumFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Mirror buildProps("Image", mediumFeature, non-Row):
                            // safe pre-load placeholder; A2UIApplier will refine
                            // via loadResolution callback (see __featureAutoSizeMax*Dp).
                            props["alignSelf"] = "stretch"
                            props["height"] = 200
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                    "largeFeature" -> {
                        if (parentIsRow) {
                            props["width"] = 150
                            props["height"] = 150
                        } else {
                            // Mirror buildProps("Image", largeFeature, non-Row):
                            // no width cap (image renders at intrinsic width, leading-
                            // aligned), max height 400dp. Placeholder height matches the
                            // cap so pre-load layout doesn't shift.
                            props["alignSelf"] = "stretch"
                            props["height"] = 400
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxHeightDp"] = 400
                        }
                    }
                    "header" -> {
                        // Banner style: 100% width (respecting margins), fixed height 200dp, cover.
                        props["alignSelf"] = "stretch"
                        props["height"] = 200
                        props["resizeMode"] = "cover"
                    }
                    else -> {
                        if (parentIsRow) {
                            props["width"] = 100
                            props["height"] = 100
                        } else {
                            // Same as mediumFeature (default variant).
                            props["alignSelf"] = "stretch"
                            props["height"] = 200
                            props["resizeMode"] = "cover"
                            props["__featureAutoSizeMaxWidthDp"] = 300
                            props["__featureAutoSizeMaxHeightDp"] = 200
                        }
                    }
                }
            }
            "Divider" -> {
                // Mirror buildProps("Divider"): vertical divider uses an explicit height
                // fallback (40dp) rather than alignSelf=stretch, because Kuikly's flex
                // engine cannot stretch a NaN cross-axis dim when the parent Row's height
                // is content-driven. The applier layer will resize this divider to match
                // the parent Row's actual height after layout via the marker prop.
                val originalId = getOriginalComponentId(instanceId)
                val axis = idToOriginalDef[originalId]?.get("axis") as? String ?: "horizontal"
                props["backgroundColor"] = "#E0E0E0"
                if (axis == "horizontal") {
                    props["height"] = 1
                    props["alignSelf"] = "stretch"
                    props["marginTop"] = 8
                    props["marginBottom"] = 8
                } else {
                    props["width"] = 1
                    props["height"] = 40
                    props["marginLeft"] = 8
                    props["marginRight"] = 8
                    props["__verticalDividerFollowParent"] = true
                }
            }
            "CheckBox" -> {
                // Mirror buildProps("CheckBox"): Kuikly's CheckBoxView.didInit() throws a
                // fatal RuntimeException when width/height are unset, so template instances
                // need the same fixed 24x24 size as directly declared CheckBoxes.
                props["width"] = 24
                props["height"] = 24
                props["margin"] = 8
                props["__primaryColor"] = primaryColor
            }
            "Slider" -> {
                // Mirror buildProps("Slider"): SliderView.didInit() also requires a height.
                props["alignSelf"] = "stretch"
                props["height"] = 40
                props["margin"] = 8
                props["progressColor"] = primaryColor
            }
        }
    }

    /**
     * Extract the original component ID from an instance ID.
     * Instance IDs have format "originalId__arrayIndex" for template instances,
     * or just "originalId" for regular components.
     */
    private fun getOriginalComponentId(instanceId: String): String {
        val separatorIndex = instanceId.lastIndexOf("__")
        return if (separatorIndex > 0) {
            instanceId.substring(0, separatorIndex)
        } else {
            instanceId
        }
    }

    /**
     * Get the parent component ID for an instance (works for both regular and template instances).
     */
    private fun getOriginalParentId(instanceId: String): String? {
        // First try direct lookup in childToParentCache
        val directParent = childToParentCache[instanceId]
        if (directParent != null) return directParent

        // For template instances, look up the original parent relationship
        val originalId = getOriginalComponentId(instanceId)
        return childToParentCache[originalId]
    }

    /**
     * Get the parent component type for an instance (works for both regular and template instances).
     */
    private fun getParentComponentType(instanceId: String): String? {
        // First try direct lookup
        val parentId = childToParentCache[instanceId]
        if (parentId != null) {
            return idToTag[parentId]?.let { tagToComponentName[it] }
        }

        // For template instances, look up original parent's component type
        val originalId = getOriginalComponentId(instanceId)
        val originalParentId = childToParentCache[originalId]
        if (originalParentId != null) {
            val parentDef = idToOriginalDef[originalParentId]
            return parentDef?.get("component") as? String
        }
        return null
    }

    /**
     * Check if the parent of an instance is a Row component.
     */
    private fun isParentRow(instanceId: String): Boolean {
        return getParentComponentType(instanceId) == "Row"
    }

    /**
     * Remove all generated instances in a template instance tree.
     */
    private fun removeInstanceTree(instanceId: String, surfaceId: String, parentTag: Int, messages: MutableList<A2UIMessage>) {
        // Clean up scope path and tag mappings
        instanceScopePath.remove(instanceId)
        idToTag.remove(instanceId)
    }

    /**
     * Recursively collect a template component ID and all its descendant IDs.
     * These components should not be created as regular components — they are only
     * instantiated via ChildList expansion.
     */
    private fun collectTemplateTree(
        templateId: String,
        idToComp: Map<String, Map<String, Any?>>,
        childToParent: Map<String, String>,
        result: MutableSet<String>
    ) {
        if (templateId in result) return
        val compMap = idToComp[templateId] ?: return
        result.add(templateId)

        // Collect children of this template
        val children = compMap["children"]
        if (children is List<*>) {
            for (childId in children) {
                if (childId is String) {
                    collectTemplateTree(childId, idToComp, childToParent, result)
                }
            }
        } else if (children is Map<*, *>) {
            // Inline ChildList binding inside a template body — its `componentId`
            // is itself a nested template that must NOT be created as a regular
            // component. Recurse to also cover deeper inline ChildLists.
            val innerTemplateId = children["componentId"] as? String
            if (innerTemplateId != null) {
                collectTemplateTree(innerTemplateId, idToComp, childToParent, result)
            }
        }
        val child = compMap["child"] as? String
        if (child != null) {
            collectTemplateTree(child, idToComp, childToParent, result)
        }
    }

    // endregion

    /**
     * Get the variant of a parent Button component.
     */
    private fun getParentButtonVariant(parentId: String): String {
        return componentVariantCache[parentId] ?: "default"
    }

    /**
     * Resolve all data bindings in the component tree using the current data model.
     * Call this after updateDataModel to update component props.
     */
    fun resolveDataBindings(): List<A2UIMessage> {
        // This would generate UPDATE_COMPONENT messages for bound props
        // For now, data binding resolution happens at render time
        return emptyList()
    }

    /**
     * Serialize an object (Map, List, String, Number, Boolean) to a JSON string.
     * Simple implementation for Kotlin Multiplatform (no external JSON library dependency).
     */
    private fun serializeToJson(obj: Any?): String? {
        if (obj == null) return null
        return try {
            buildJsonString(obj)
        } catch (e: Exception) {
            null
        }
    }

    private fun buildJsonString(obj: Any?): String {
        return when (obj) {
            null -> "null"
            is String -> "\"${escapeJsonString(obj)}\""
            is Number -> obj.toString()
            is Boolean -> obj.toString()
            is Map<*, *> -> {
                val entries = obj.entries.joinToString(",") { (k, v) ->
                    "\"${escapeJsonString(k.toString())}\":${buildJsonString(v)}"
                }
                "{$entries}"
            }
            is List<*> -> {
                val items = obj.joinToString(",") { buildJsonString(it) }
                "[$items]"
            }
            else -> "\"${escapeJsonString(obj.toString())}\""
        }
    }

    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    // ---- Checks Evaluation ----

    /**
     * Register checks for a component and set up data dependencies.
     * Called during buildProps when a component has a "checks" array.
     *
     * @param componentId The component ID
     * @param checks The raw checks array from the protocol JSON
     */
    private fun registerChecks(componentId: String, checks: List<*>) {
        val checkRules = mutableListOf<Map<String, Any?>>()
        for (check in checks) {
            if (check is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                checkRules.add(check as Map<String, Any?>)
            }
        }
        if (checkRules.isEmpty()) return

        checksRegistry[componentId] = checkRules

        // Extract data paths from all check conditions and register dependencies
        for (rule in checkRules) {
            val condition = rule["condition"]
            if (condition != null) {
                val paths = extractDataPaths(condition)
                for (path in paths) {
                    val key = path.removePrefix("/")
                    dataKeyToChecksDependents.getOrPut(key) { mutableSetOf() }.add(componentId)
                }
            }
        }
    }

    /**
     * Evaluate all checks for a component and return the first failed check's message.
     * Returns null if all checks pass.
     *
     * @param componentId The component ID
     * @return The error message of the first failed check, or null if all pass
     */
    fun evaluateChecksForComponent(componentId: String): String? {
        val checkRules = checksRegistry[componentId] ?: return null

        for (rule in checkRules) {
            val condition = rule["condition"]
            val message = rule["message"] as? String ?: "Validation failed"

            val result = evaluateDynamicBoolean(condition)
            if (!result) {
                return message
            }
        }
        return null // All checks passed
    }

    /**
     * Evaluate a DynamicBoolean value (literal, path reference, or function call).
     * Returns true if the condition passes, false if it fails.
     *
     * @param condition The condition value (Boolean, Map with "path", or Map with "call")
     * @return The boolean result of evaluating the condition
     */
    private fun evaluateDynamicBoolean(condition: Any?): Boolean {
        if (condition == null) return true

        // Literal boolean
        if (condition is Boolean) return condition

        // Number treated as boolean (0 = false, non-zero = true)
        if (condition is Number) return condition.toInt() != 0

        // Map: either path reference or function call
        if (condition is Map<*, *>) {
            // Path reference: {"path": "/someFlag"}
            val path = condition["path"] as? String
            if (path != null) {
                val key = path.removePrefix("/")
                val value = resolveNestedDataPath(key)
                return toBooleanValue(value)
            }

            // Function call: {"call": "required", "args": {...}}
            val call = condition["call"] as? String
            if (call != null) {
                @Suppress("UNCHECKED_CAST")
                val args = condition["args"] as? Map<String, Any?> ?: emptyMap()
                return evaluateCheckFunction(call, args)
            }
        }

        // String "true"/"false"
        if (condition is String) {
            return condition.lowercase() == "true"
        }

        return true // Default: pass
    }

    /**
     * Convert any value to a boolean for checks evaluation.
     */
    private fun toBooleanValue(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            is Number -> value.toInt() != 0
            is String -> value.isNotEmpty() && value.lowercase() != "false"
            is List<*> -> value.isNotEmpty()
            is Map<*, *> -> value.isNotEmpty()
            else -> true
        }
    }

    /**
     * Evaluate a built-in check function.
     * Supports: required, email, length, regex, numeric, and, or, not.
     */
    private fun evaluateCheckFunction(call: String, args: Map<String, Any?>): Boolean {
        return when (call) {
            "required" -> evaluateRequired(args)
            "email" -> evaluateEmail(args)
            "length" -> evaluateLength(args)
            "regex" -> evaluateRegex(args)
            "numeric" -> evaluateNumeric(args)
            "and" -> evaluateAnd(args)
            "or" -> evaluateOr(args)
            "not" -> evaluateNot(args)
            else -> evaluateCustomFunctionAsBoolean(call, args)
        }
    }

    /**
     * Evaluate "required" check: value must not be null, undefined, or empty.
     *
     * Per A2UI v0.9, "required" means the user must have provided a value.
     * We interpret truthiness consistently with [toBooleanValue] so that the
     * check remains meaningful across all bindable types:
     *   - Boolean `false` counts as "not provided" (e.g. an unchecked CheckBox).
     *   - Number `0` counts as "not provided" (matches JS `Boolean(0)` semantics
     *     used across A2UI reference implementations).
     *   - Empty String / List / Map count as "not provided".
     * Otherwise the value is considered present and the check passes.
     */
    private fun evaluateRequired(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])
        return when (value) {
            null -> false
            is Boolean -> value
            is Number -> value.toDouble() != 0.0
            is String -> value.isNotEmpty()
            is List<*> -> value.isNotEmpty()
            is Map<*, *> -> value.isNotEmpty()
            else -> true
        }
    }

    /**
     * Evaluate "email" check: value must be a valid email format.
     * Empty value passes email validation (emptiness should be checked by "required").
     */
    private fun evaluateEmail(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return true
        // Empty value passes email validation (emptiness should be checked by "required")
        if (value.isEmpty()) return true
        // Simple email regex validation
        return value.matches(Regex("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"))
    }

    /**
     * Evaluate "length" check: string length must be within min/max bounds.
     */
    private fun evaluateLength(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return false
        val min = (args["min"] as? Number)?.toInt()
        val max = (args["max"] as? Number)?.toInt()

        if (min != null && value.length < min) return false
        if (max != null && value.length > max) return false
        return true
    }

    /**
     * Evaluate "regex" check: value must match the given pattern.
     */
    private fun evaluateRegex(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return false
        // Empty value passes regex validation (emptiness should be checked by "required")
        if (value.isEmpty()) return true
        val pattern = args["pattern"] as? String ?: return true
        return try {
            value.matches(Regex(pattern))
        } catch (e: Exception) {
            true // Invalid regex: pass by default
        }
    }

    /**
     * Evaluate "numeric" check: value must be a number within min/max bounds.
     */
    private fun evaluateNumeric(args: Map<String, Any?>): Boolean {
        val rawValue = resolveCheckArg(args["value"])
        val numValue = when (rawValue) {
            is Number -> rawValue.toDouble()
            is String -> rawValue.toDoubleOrNull() ?: return false
            else -> return false
        }
        val min = (args["min"] as? Number)?.toDouble()
        val max = (args["max"] as? Number)?.toDouble()

        if (min != null && numValue < min) return false
        if (max != null && numValue > max) return false
        return true
    }

    /**
     * Evaluate "and" check: all values must be true.
     */
    private fun evaluateAnd(args: Map<String, Any?>): Boolean {
        val values = args["values"] as? List<*> ?: return true
        for (value in values) {
            if (!evaluateDynamicBoolean(value)) return false
        }
        return true
    }

    /**
     * Evaluate "or" check: at least one value must be true.
     */
    private fun evaluateOr(args: Map<String, Any?>): Boolean {
        val values = args["values"] as? List<*> ?: return true
        for (value in values) {
            if (evaluateDynamicBoolean(value)) return true
        }
        return values.isEmpty() // Empty list: pass
    }

    /**
     * Evaluate "not" check: the value must be false.
     */
    private fun evaluateNot(args: Map<String, Any?>): Boolean {
        val value = args["value"] ?: args["values"]
        if (value is List<*> && value.isNotEmpty()) {
            return !evaluateDynamicBoolean(value[0])
        }
        return !evaluateDynamicBoolean(value)
    }

    /**
     * Resolve a check function argument value.
     * Handles path references, nested function calls, and literal values.
     */
    private fun resolveCheckArg(arg: Any?): Any? {
        if (arg == null) return null
        if (arg is String) return arg
        if (arg is Number) return arg
        if (arg is Boolean) return arg
        if (arg is Map<*, *>) {
            // Path reference: {"path": "/key"}
            val path = arg["path"] as? String
            if (path != null) {
                val key = path.removePrefix("/")
                return resolveNestedDataPath(key)
            }
            // Nested function call: {"call": "...", "args": {...}}
            val call = arg["call"] as? String
            if (call != null) {
                @Suppress("UNCHECKED_CAST")
                val nestedArgs = arg["args"] as? Map<String, Any?> ?: emptyMap()
                // For boolean-returning functions, return the boolean result
                return evaluateCheckFunction(call, nestedArgs)
            }
        }
        return arg
    }

    // region ChoicePicker helpers

    /**
     * Build props for a choice chip container (KRView).
     * Selected chips have primary color background; unselected have border only.
     */
    private fun buildChoiceChipProps(isSelected: Boolean): MutableMap<String, Any?> {
        val props = mutableMapOf<String, Any?>(
            "borderRadius" to 16f,
            "paddingLeft" to 12f,
            "paddingRight" to 12f,
            "paddingTop" to 6f,
            "paddingBottom" to 6f,
            "alignItems" to "center",
            "justifyContent" to "center",
            // Don't let the chip stretch on the row's cross axis; keep it sized to its
            // text content so the chips read as compact pills.
            "alignSelf" to "flex-start",
            // Make every chip an interactive render view so taps are reliably received
            // (matches the working Tabs header chips).
            "touchEnable" to 1,
            // Spacing lives on the trailing/bottom edges so all chips share the same
            // left edge (no per-index marginLeft that misaligns the row).
            "marginRight" to 8f,
            "marginBottom" to 8f
        )

        if (isSelected) {
            props["backgroundColor"] = primaryColor
        } else {
            props["border"] = normalizeHexInShorthand("1 solid #CCCCCC")
            props["backgroundColor"] = "#FFFFFF"
        }
        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Build props for a choice chip text (KRRichTextView).
     * Selected chips have white text; unselected have dark text.
     */
    private fun buildChoiceChipTextProps(label: String, isSelected: Boolean): MutableMap<String, Any?> {
        val props = mutableMapOf<String, Any?>(
            "text" to label,
            "fontSize" to 14f,
            "color" to if (isSelected) "#FFFFFF" else "#333333"
        )
        normalizeColorFieldsInPlace(props)
        return props
    }

    /**
     * Build props for a checkbox/radio option row container (KRView).
     * Each option is a horizontal row with icon + label.
     */
    private fun buildCheckboxOptionRowProps(isSelected: Boolean): MutableMap<String, Any?> {
        return mutableMapOf(
            "flexDirection" to "row",
            "alignItems" to "center",
            "paddingTop" to 8f,
            "paddingBottom" to 8f,
            "paddingLeft" to 4f,
            "paddingRight" to 4f,
            "alignSelf" to "stretch",
            "touchEnable" to 1
        )
    }

    /**
     * Get the checkbox/radio icon for the given selection state and variant.
     */
    private fun getCheckboxIcon(isSelected: Boolean, variant: String): String {
        return if (variant == "mutuallyExclusive") {
            // Radio style
            if (isSelected) "●" else "○"
        } else {
            // Checkbox style
            if (isSelected) "☑" else "□"
        }
    }

    /**
     * Filter ChoicePicker options by search text. Shows/hides options by setting height.
     * Returns update messages to apply.
     */
    fun filterChoicePickerOptions(componentId: String, filterText: String): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        val chipTags = choicePickerChipTags[componentId] ?: return messages
        val chipLabels = choicePickerChipLabels[componentId] ?: return messages
        val displayStyle = choicePickerDisplayStyles[componentId] ?: "chips"
        val lowerFilter = filterText.lowercase()

        for (i in chipTags.indices) {
            val chipTag = chipTags[i]
            val label = chipLabels.getOrNull(i) ?: ""
            val visible = lowerFilter.isEmpty() || label.lowercase().contains(lowerFilter)

            // Show/hide by setting height and overflow
            val props: MutableMap<String, Any?> = if (visible) {
                // Restore: remove fixed height constraint (Float.NaN clears styleHeight),
                // restore padding/margin and overflow so content is visible again.
                if (displayStyle == "checkbox") {
                    mutableMapOf(
                        "height" to Float.NaN,
                        "paddingTop" to 8f,
                        "paddingBottom" to 8f,
                        "opacity" to 1f,
                        "overflow" to "visible"
                    )
                } else {
                    mutableMapOf(
                        "height" to Float.NaN,
                        "paddingTop" to 6f,
                        "paddingBottom" to 6f,
                        "marginBottom" to 8f,
                        "marginRight" to 8f,
                        "opacity" to 1f,
                        "overflow" to "visible"
                    )
                }
            } else {
                // Hide: collapse height, remove padding/margin, hide overflow
                mutableMapOf(
                    "height" to 0f,
                    "paddingTop" to 0f,
                    "paddingBottom" to 0f,
                    "marginBottom" to 0f,
                    "marginRight" to 0f,
                    "opacity" to 0f,
                    "overflow" to "hidden"
                )
            }
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_COMPONENT,
                surfaceId = currentSurfaceId,
                tag = chipTag,
                viewName = "KRView",
                props = props
            ))
        }
        return messages
    }

    /**
     * Handle ChoicePicker value change: update data model and return update messages
     * for re-rendering the chips with new selection state.
     *
     * @param componentId The ChoicePicker component ID
     * @param selectedValue The value of the chip that was tapped
     * @return List of update messages to apply
     */
    fun updateChoicePickerSelection(componentId: String, selectedValue: String): List<A2UIMessage> {
        val originalDef = idToOriginalDef[componentId] ?: return emptyList()
        val variant = originalDef["variant"] as? String ?: "mutuallyExclusive"
        val valueSpec = originalDef["value"]
        val bindingPath = if (valueSpec is Map<*, *>) {
            (valueSpec["path"] as? String)?.removePrefix("/")
        } else null

        // For ChoicePickers without a binding path, use an internal tracking path
        val effectivePath = bindingPath ?: "__cp_internal_${componentId}"

        // Initialize internal path with direct value if first time
        if (bindingPath == null && resolveNestedDataPath(effectivePath) == null) {
            val directValue = when (valueSpec) {
                is List<*> -> valueSpec.map { it.toString() }
                is String -> listOf(valueSpec)
                else -> emptyList<String>()
            }
            setNestedDataPath(effectivePath, directValue)
        }

        // Get current selection
        val currentSelection = when (val raw = resolveNestedDataPath(effectivePath)) {
            is List<*> -> raw.map { it.toString() }.toMutableList()
            is String -> mutableListOf(raw)
            else -> mutableListOf()
        }

        // Update selection based on variant
        val newSelection: List<String> = when (variant) {
            "mutuallyExclusive" -> {
                // Single select: toggle or replace
                if (currentSelection.contains(selectedValue)) {
                    emptyList() // Deselect
                } else {
                    listOf(selectedValue) // Select new
                }
            }
            "multipleSelection" -> {
                // Multi select: toggle
                if (currentSelection.contains(selectedValue)) {
                    currentSelection.filter { it != selectedValue }
                } else {
                    currentSelection + selectedValue
                }
            }
            else -> listOf(selectedValue)
        }

        // Update data model. Write through the nested path (e.g. event.location) so that
        // the next read via resolveNestedDataPath sees the new value. Writing a flat
        // "event/location" key here instead would leave the nested value stale, so toggling
        // an already-(initially-)selected option would always read the old selection and
        // never reflect the new one (the reported "middle chip never highlights").
        setNestedDataPath(effectivePath, newSelection)

        // Re-render options with new selection state
        val messages = mutableListOf<A2UIMessage>()
        val chipTags = choicePickerChipTags[componentId] ?: return messages
        val chipValues = choicePickerChipValues[componentId] ?: return messages
        val displayStyle = choicePickerDisplayStyles[componentId] ?: "chips"

        for (i in chipTags.indices) {
            val chipTag = chipTags[i]
            val chipValue = chipValues[i]
            val isSelected = chipValue in newSelection

            if (displayStyle == "checkbox") {
                // Checkbox/Radio display: update icon text and color
                val iconTag = chipTag + 1 // Icon tag is always chipTag + 1
                val icon = getCheckboxIcon(isSelected, variant)
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = currentSurfaceId,
                    tag = iconTag,
                    viewName = "KRRichTextView",
                    props = mapOf(
                        "text" to icon,
                        "color" to if (isSelected) primaryColor else "#999999"
                    )
                ))
            } else {
                // Chips display: update chip container style
                val chipProps = mutableMapOf<String, Any?>()
                if (isSelected) {
                    chipProps["backgroundColor"] = primaryColor
                    chipProps["border"] = normalizeHexInShorthand("0 solid #00000000")
                } else {
                    chipProps["backgroundColor"] = "#FFFFFF"
                    chipProps["border"] = normalizeHexInShorthand("1 solid #CCCCCC")
                }
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = currentSurfaceId,
                    tag = chipTag,
                    viewName = "KRView",
                    props = chipProps
                ))

                // Update chip text color
                val chipTextTag = chipTag + 1 // Text tag is always chipTag + 1
                val textProps = mutableMapOf<String, Any?>(
                    "color" to if (isSelected) "#FFFFFF" else "#333333"
                )
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = currentSurfaceId,
                    tag = chipTextTag,
                    viewName = "KRRichTextView",
                    props = textProps
                ))
            }
        }

        // Also re-evaluate other components that depend on this data key
        // (only relevant when there's a real binding path, not internal tracking)
        if (bindingPath != null) {
            val rootKey = getRootDataKey(bindingPath)
        val affectedComponentIds = mutableSetOf<String>()
        dataKeyToFuncDependents[rootKey]?.let { affectedComponentIds.addAll(it) }
        dataKeyToPathDependents[rootKey]?.let { ids ->
            affectedComponentIds.addAll(ids.filter { it != componentId && !it.startsWith("__childlist__") })
        }

        for (compId in affectedComponentIds) {
            val compDef = idToOriginalDef[compId] ?: continue
            val compTag = idToTag[compId] ?: continue
            val compType = compDef["component"] as? String ?: continue
            val compViewName = A2UIComponentMapper.mapComponentName(compType)

            val updatedProps = mutableMapOf<String, Any?>()
            val skipKeys = setOf("id", "component", "children", "child", "accessibility", "action", "checks", "tabs")
            for ((propKey, propValue) in compDef) {
                if (propKey in skipKeys) continue
                // Kuikly `styles` extension: re-expand on data-model change so
                // path-bound style values refresh together with other bound props.
                if (propKey == "styles" && propValue is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    for ((sk, sv) in A2UIComponentMapper.expandStyles(compType, propValue as Map<String, Any?>)) {
                        updatedProps[sk] = resolveValue(sv, compId)
                    }
                    clearDefaultsIfGradient(updatedProps)
                    continue
                }
                val mapped = A2UIComponentMapper.mapProp(compType, propKey, propValue)
                if (mapped != null) {
                    val (mappedKey, mappedValue) = mapped
                    if (mappedKey.isNotEmpty()) {
                        updatedProps[mappedKey] = resolveValue(mappedValue, compId)
                    }
                }
            }

            if (updatedProps.isNotEmpty()) {
                messages.add(A2UIMessage(
                    type = A2UIMessageType.UPDATE_COMPONENT,
                    surfaceId = currentSurfaceId,
                    tag = compTag,
                    viewName = compViewName,
                    props = updatedProps
                ))
            }
        }
        } // end if (bindingPath != null)

        return messages
    }

    /**
     * Get the ChoicePicker component ID for a given chip tag.
     * Returns null if the tag is not a chip.
     */
    fun getChoicePickerIdForChipTag(chipTag: Int): String? {
        for ((pickerId, tags) in choicePickerChipTags) {
            if (chipTag in tags) return pickerId
        }
        return null
    }

    /**
     * Get the option value for a given chip tag.
     */
    fun getChoiceOptionValueForChipTag(chipTag: Int): String? {
        for ((pickerId, tags) in choicePickerChipTags) {
            val index = tags.indexOf(chipTag)
            if (index >= 0) {
                return choicePickerChipValues[pickerId]?.getOrNull(index)
            }
        }
        return null
    }

    /**
     * Get the binding path for a ChoicePicker component.
     */
    fun getChoicePickerBindingPath(componentId: String): String? {
        val originalDef = idToOriginalDef[componentId] ?: return null
        val valueSpec = originalDef["value"]
        if (valueSpec is Map<*, *>) {
            return (valueSpec["path"] as? String)?.removePrefix("/")
        }
        return null
    }

    // endregion

    // region Tabs helpers

    /**
     * Build props for a tab header button container.
     *
     * Each tab takes an equal share of the header row width via `flex: 1`. This is
     * required for the selected indicator: Kuikly's native render layer only
     * supports a full 4-side `border` (single-side `borderBottom` is ignored), so
     * the indicator is drawn as a child bar (see [buildTabUnderlineProps]). That
     * bar uses `alignSelf: stretch`, which only resolves a width when the header
     * has a *defined* width — a content-sized (auto-width) header collapses the bar
     * to 0px wide. `flex: 1` gives the header a defined width.
     */
    private fun buildTabHeaderProps(): Map<String, Any?> {
        return mapOf(
            "flex" to 1,
            "alignItems" to "center",
            "justifyContent" to "center",
            "paddingLeft" to 12f,
            "paddingRight" to 12f,
            "paddingTop" to 10f,
            "paddingBottom" to 8f,
            "touchEnable" to 1
        )
    }

    /**
     * Build props for the bottom underline indicator of a tab header.
     *
     * Rendered as a child bar (native Kuikly has no `borderBottom`). It always
     * occupies the same space and only toggles its background color, so [switchTab]
     * just needs to update `backgroundColor`.
     */
    private fun buildTabUnderlineProps(isSelected: Boolean): Map<String, Any?> {
        return withNormalizedColors(mapOf(
            "alignSelf" to "stretch",
            "marginTop" to 8f,
            "height" to 2.5f,
            "backgroundColor" to if (isSelected) primaryColor else "#00000000"
        ))
    }

    /**
     * Build props for a tab header text.
     */
    private fun buildTabHeaderTextProps(title: String, isSelected: Boolean): Map<String, Any?> {
        return withNormalizedColors(mapOf(
            "text" to title,
            "fontSize" to 14f,
            "fontWeight" to if (isSelected) "bold" else "normal",
            "color" to if (isSelected) primaryColor else "#666666"
        ))
    }

    /**
     * Switch the selected tab for a Tabs component.
     * Returns update messages to show/hide content panels and update header styles.
     *
     * @param tabsId The Tabs component ID
     * @param newIndex The index of the tab to select
     * @return List of update messages to apply
     */
    fun switchTab(tabsId: String, newIndex: Int): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        val tabDefs = tabsDefinitions[tabsId] ?: return messages
        val oldIndex = tabsSelectedIndex[tabsId] ?: 0

        if (newIndex == oldIndex || newIndex < 0 || newIndex >= tabDefs.size) {
            return messages
        }

        tabsSelectedIndex[tabsId] = newIndex

        // Update content wrapper visibility
        val contentWrapperTags = tabsContentWrapperTags[tabsId] ?: return messages
        for ((tabIndex, wrapperTag) in contentWrapperTags.withIndex()) {
            val isSelected = tabIndex == newIndex
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_COMPONENT,
                surfaceId = currentSurfaceId,
                tag = wrapperTag,
                viewName = "KRView",
                props = if (isSelected) mapOf(
                    "height" to Float.NaN,
                    "flex" to 1
                ) else mapOf(
                    "height" to 0f,
                    "flex" to 0
                )
            ))
        }

        // Update tab header styles
        val headerTags = tabsHeaderTags[tabsId] ?: return messages
        for ((tabIndex, headerTag) in headerTags.withIndex()) {
            val isSelected = tabIndex == newIndex
            // Update header text (color + weight). Text tag is always headerTag + 1.
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_COMPONENT,
                surfaceId = currentSurfaceId,
                tag = headerTag + 1,
                viewName = "KRRichTextView",
                props = mapOf(
                    "fontWeight" to if (isSelected) "bold" else "normal",
                    "color" to if (isSelected) primaryColor else "#666666"
                )
            ))
            // Update the bottom underline indicator (child of the header).
            // Underline tag is always headerTag + 2 (text is headerTag + 1).
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_COMPONENT,
                surfaceId = currentSurfaceId,
                tag = headerTag + 2,
                viewName = "KRView",
                props = mapOf(
                    "backgroundColor" to if (isSelected) primaryColor else "#00000000"
                )
            ))
        }

        return messages
    }

    /**
     * Get the Tabs component ID for a given tab header tag.
     * Returns null if the tag is not a tab header.
     */
    fun getTabsIdForHeaderTag(headerTag: Int): String? {
        for ((tabsId, tags) in tabsHeaderTags) {
            if (headerTag in tags) return tabsId
        }
        return null
    }

    // region Modal

    /**
     * Open the modal identified by [modalId].
     *
     * Builds the modal overlay on demand: a full-screen [ViewConst.TYPE_MODAL_VIEW]
     * window containing a dimmed backdrop, a centered dialog card with a close button,
     * and the modal's lazily-created content subtree.
     *
     * @return the messages that create the overlay, or empty if the modal is unknown
     *   or already open.
     */
    fun openModal(modalId: String): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        val modalDef = modalDefinitions[modalId] ?: return messages
        if (modalId in modalOpenState) return messages
        if (idToOriginalDef[modalDef.contentId] == null) return messages
        modalOpenState.add(modalId)

        val surfaceId = currentSurfaceId
        val createdTags = mutableListOf<Pair<Int, Int>>()

        // 1. Full-screen modal window (top-level, above all card content).
        val modalTag = nextTag++
        tagToComponentName[modalTag] = "Modal"
        createdTags.add(modalTag to -1)
        messages.add(A2UIMessage(
            type = A2UIMessageType.CREATE_COMPONENT,
            surfaceId = surfaceId,
            tag = modalTag,
            viewName = "KRModalView",
            parentTag = -1,
            index = -1,
            props = mapOf("__modalId" to modalId)
        ))

        // 2. Dimmed backdrop that fills the window and centers the dialog.
        val backdropTag = nextTag++
        tagToComponentName[backdropTag] = "Column"
        createdTags.add(backdropTag to modalTag)
        messages.add(A2UIMessage(
            type = A2UIMessageType.CREATE_COMPONENT,
            surfaceId = surfaceId,
            tag = backdropTag,
            viewName = "KRView",
            parentTag = modalTag,
            index = 0,
            props = mapOf(
                "flex" to 1,
                "flexDirection" to "column",
                "justifyContent" to "center",
                "alignItems" to "center",
                "paddingLeft" to 24f,
                "paddingRight" to 24f,
                "backgroundColor" to "#99000000",
                // Tapping the backdrop dismisses the modal.
                "touchEnable" to 1,
                "__modalClose" to modalId
            )
        ))

        // 3. Centered dialog card. Absorbs taps so they don't reach the backdrop.
        val dialogTag = nextTag++
        tagToComponentName[dialogTag] = "Column"
        createdTags.add(dialogTag to backdropTag)
        messages.add(A2UIMessage(
            type = A2UIMessageType.CREATE_COMPONENT,
            surfaceId = surfaceId,
            tag = dialogTag,
            viewName = "KRView",
            parentTag = backdropTag,
            index = 0,
            props = mapOf(
                "alignSelf" to "stretch",
                "flexDirection" to "column",
                "backgroundColor" to "#FFFFFF",
                "borderRadius" to 12f,
                "paddingLeft" to 16f,
                "paddingRight" to 16f,
                "paddingTop" to 8f,
                "paddingBottom" to 16f,
                "minHeight" to 120f,
                // Absorb taps so the backdrop's close handler doesn't fire.
                "touchEnable" to 1,
                "__modalAbsorb" to 1
            )
        ))

        // 4. Header row with a close button aligned to the end.
        val closeRowTag = nextTag++
        tagToComponentName[closeRowTag] = "Row"
        createdTags.add(closeRowTag to dialogTag)
        messages.add(A2UIMessage(
            type = A2UIMessageType.CREATE_COMPONENT,
            surfaceId = surfaceId,
            tag = closeRowTag,
            viewName = "KRView",
            parentTag = dialogTag,
            index = 0,
            props = mapOf(
                "flexDirection" to "row",
                "justifyContent" to "flex-end",
                "alignItems" to "center",
                // The whole close row is the tap target (a container view) so the click
                // handler is wired on a DivView.
                "touchEnable" to 1,
                "__modalClose" to modalId
            )
        ))

        val closeBtnTag = nextTag++
        tagToComponentName[closeBtnTag] = "Text"
        createdTags.add(closeBtnTag to closeRowTag)
        messages.add(A2UIMessage(
            type = A2UIMessageType.CREATE_COMPONENT,
            surfaceId = surfaceId,
            tag = closeBtnTag,
            viewName = "KRRichTextView",
            parentTag = closeRowTag,
            index = 0,
            props = mapOf(
                "text" to "✕",
                "fontSize" to 20f,
                "color" to "#333333",
                "paddingLeft" to 12f,
                "paddingRight" to 4f,
                "paddingTop" to 4f,
                "paddingBottom" to 4f
            )
        ))

        // 5. The modal content subtree (created lazily here).
        buildModalContentSubtree(modalDef.contentId, dialogTag, 1, surfaceId, createdTags, messages)

        modalOverlayTags[modalId] = createdTags

        // 6. ChildList templates inside the content were skipped while the modal was
        //    closed. Expand them now and record the instances so closeModal() removes them.
        val contentTags = createdTags.mapTo(mutableSetOf()) { it.first }
        val contentIds = idToTag.filterValues { it in contentTags }.keys
        for (message in expandChildListBindings(surfaceId, onlyParents = contentIds)) {
            if (message.type == A2UIMessageType.CREATE_COMPONENT) {
                createdTags.add(message.tag to message.parentTag)
            }
            messages.add(message)
        }
        return messages
    }

    /**
     * Close the modal identified by [modalId], removing its overlay subtree.
     *
     * @return the removal messages, or empty if the modal is not open.
     */
    fun closeModal(modalId: String): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        if (modalId !in modalOpenState) return messages
        modalOpenState.remove(modalId)
        val createdTags = modalOverlayTags.remove(modalId) ?: return messages

        // Remove children before their parents (reverse creation order).
        for ((tag, parentTag) in createdTags.reversed()) {
            messages.add(A2UIMessage(
                type = A2UIMessageType.REMOVE_COMPONENT,
                surfaceId = currentSurfaceId,
                tag = tag,
                parentTag = parentTag
            ))
        }
        return messages
    }

    /**
     * Resolve the Modal component ID that the given trigger tag opens, if any.
     */
    fun getModalIdForTriggerTag(triggerTag: Int): String? {
        for ((triggerId, modalId) in modalTriggerToId) {
            if (idToTag[triggerId] == triggerTag) return modalId
        }
        return null
    }

    /**
     * Recursively create CREATE_COMPONENT messages for a component subtree, reusing the
     * standard prop builders. Used to materialize a Modal's content when it opens.
     */
    private fun buildModalContentSubtree(
        compId: String,
        parentTag: Int,
        index: Int,
        surfaceId: String,
        createdTags: MutableList<Pair<Int, Int>>,
        messages: MutableList<A2UIMessage>
    ) {
        val def = idToOriginalDef[compId] ?: return
        val componentType = def["component"] as? String ?: return

        if (!idToTag.containsKey(compId)) {
            idToTag[compId] = nextTag++
        }
        val tag = idToTag[compId]!!
        tagToComponentName[tag] = componentType

        val isKuiklyComponent = KuiklyComponentMapper.isKuiklyComponent(componentType)
        val viewName = if (isKuiklyComponent) {
            KuiklyComponentMapper.mapComponentName(componentType)
        } else if (componentType == "TextField") {
            // TextField viewName depends on variant: longText -> KRTextAreaView (multi-line)
            val variant = def["variant"] as? String
            A2UIComponentMapper.mapTextFieldViewName(variant)
        } else {
            A2UIComponentMapper.mapComponentName(componentType)
        }
        val props = if (isKuiklyComponent) {
            buildKuiklyProps(componentType, def)
        } else {
            buildProps(componentType, def)
        }

        // TextField/DateTimeInput/Slider label wrapper: same logic as handleUpdateComponents
        val labelSpec: Any? = when (componentType) {
            "TextField", "DateTimeInput", "Slider" -> def["label"]
            else -> null
        }
        val hasLabel = when (labelSpec) {
            null -> false
            is String -> labelSpec.isNotEmpty()
            else -> true
        }
        val fieldLabel = if (hasLabel) resolveLabelString(labelSpec, compId) else ""
        val hasChecks = (def["checks"] != null || def["validationRegexp"] != null) &&
            (componentType == "TextField" || componentType == "Button")
        val needsWrapper = (hasChecks || hasLabel) &&
            (componentType == "TextField" || componentType == "DateTimeInput" || componentType == "Slider")

        if (needsWrapper) {
            // Wrap in Column: [label?] [input] [error?]
            val wrapperTag = nextTag++
            idToCheckWrapperTag[compId] = wrapperTag
            tagToComponentName[wrapperTag] = "Column"

            val mutableProps = props.toMutableMap()
            val errorMessage = mutableProps.remove("__checkError") as? String
            mutableProps.remove("placeholder")
            val flexValue = mutableProps.remove("flex")
            val outerMargin = mutableProps.remove("margin")

            val wrapperProps = mutableMapOf<String, Any?>(
                "alignSelf" to "stretch",
                "flexDirection" to "column"
            )
            if (flexValue != null) wrapperProps["flex"] = flexValue
            if (outerMargin != null) wrapperProps["margin"] = outerMargin
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = wrapperTag,
                viewName = "KRView",
                parentTag = parentTag,
                index = index,
                props = wrapperProps
            ))
            createdTags.add(wrapperTag to parentTag)

            var childIndex = 0

            // Label Text (if present)
            if (hasLabel) {
                val labelTag = nextTag++
                tagToComponentName[labelTag] = "Text"
                idToFieldLabelTag[compId] = labelTag
                idToFieldLabelSpec[compId] = labelSpec
                messages.add(A2UIMessage(
                    type = A2UIMessageType.CREATE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = labelTag,
                    viewName = "KRRichTextView",
                    parentTag = wrapperTag,
                    index = childIndex++,
                    props = mapOf(
                        "text" to fieldLabel,
                        "fontSize" to 14f,
                        "fontWeight" to "bold",
                        "color" to "#333333",
                        "marginBottom" to 4f,
                        "alignSelf" to "stretch"
                    )
                ))
                createdTags.add(labelTag to wrapperTag)
            }

            // The input itself
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = tag,
                viewName = viewName,
                parentTag = wrapperTag,
                index = childIndex++,
                props = mutableProps
            ))
            createdTags.add(tag to wrapperTag)

            // Error/hint Text below the component (only when there are checks)
            if (hasChecks) {
                val errorTag = nextTag++
                idToCheckErrorTag[compId] = errorTag
                tagToComponentName[errorTag] = "Text"
                val errorProps = mutableMapOf<String, Any?>(
                    "text" to (errorMessage ?: ""),
                    "fontSize" to 12f,
                    "color" to "#D32F2F",
                    "marginLeft" to 4f,
                    "marginTop" to 4f,
                    "alignSelf" to "stretch"
                )
                if (errorMessage == null) {
                    errorProps["height"] = 0f
                    errorProps["opacity"] = 0f
                }
                messages.add(A2UIMessage(
                    type = A2UIMessageType.CREATE_COMPONENT,
                    surfaceId = surfaceId,
                    tag = errorTag,
                    viewName = "KRRichTextView",
                    parentTag = wrapperTag,
                    index = childIndex++,
                    props = errorProps
                ))
                createdTags.add(errorTag to wrapperTag)
            }
        } else if (componentType == "CheckBox" &&
            resolveLabelString(def["label"], compId).isNotEmpty()) {
            // Wrap in Row: [checkbox] [label text]
            val labelText = resolveLabelString(def["label"], compId)
            val mutableProps = props.toMutableMap()
            val outerMargin = mutableProps.remove("margin")

            val rowTag = nextTag++
            tagToComponentName[rowTag] = "Row"
            val rowProps = mutableMapOf<String, Any?>(
                "flexDirection" to "row",
                "alignItems" to "center",
                "alignSelf" to "stretch"
            )
            if (outerMargin != null) rowProps["margin"] = outerMargin
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = rowTag,
                viewName = "KRView",
                parentTag = parentTag,
                index = index,
                props = rowProps
            ))
            createdTags.add(rowTag to parentTag)

            // The checkbox itself
            mutableProps["marginRight"] = 8
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = tag,
                viewName = viewName,
                parentTag = rowTag,
                index = 0,
                props = mutableProps
            ))
            createdTags.add(tag to rowTag)

            // The label text
            val labelTag = nextTag++
            tagToComponentName[labelTag] = "Text"
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = labelTag,
                viewName = "KRRichTextView",
                parentTag = rowTag,
                index = 1,
                props = mapOf(
                    "text" to labelText,
                    "fontSize" to 16f,
                    "color" to "#1A1A1A",
                    "flex" to 1
                )
            ))
            createdTags.add(labelTag to rowTag)
        } else {
            // Normal component: single CREATE_COMPONENT
            messages.add(A2UIMessage(
                type = A2UIMessageType.CREATE_COMPONENT,
                surfaceId = surfaceId,
                tag = tag,
                viewName = viewName,
                parentTag = parentTag,
                index = index,
                props = props
            ))
            createdTags.add(tag to parentTag)
        }

        val children = getChildrenIds(def)
        for ((childIndex, childId) in children.withIndex()) {
            buildModalContentSubtree(childId, tag, childIndex, surfaceId, createdTags, messages)
        }
    }

    // endregion

    /**
     * Get the tab index for a given tab header tag.
     */
    fun getTabIndexForHeaderTag(headerTag: Int): Int {
        for ((_, tags) in tabsHeaderTags) {
            val index = tags.indexOf(headerTag)
            if (index >= 0) return index
        }
        return -1
    }

    // endregion

    // region Color conversion helpers
    //
    // Kuikly's iOS renderer parses CSS shorthand values such as `border` and
    // `boxShadow` by splitting the string on spaces and running each token
    // through `[UIView css_color:]`, which internally calls
    // `[NSString longLongValue]`. That accepts DECIMAL numeric strings only:
    // "#E0E0E0".longLongValue == 0, so any hex color embedded in these
    // shorthands renders as fully-transparent black (invisible borders /
    // shadows). Convert hex → the decimal AARRGGBB representation the
    // renderer expects, before writing these props.

    /**
     * Convert a CSS hex color literal into a decimal AARRGGBB string that
     * Kuikly's shorthand parsers (`border`, `boxShadow`, ...) can consume.
     *
     * Accepted inputs: "#RGB", "#RRGGBB", "#RRGGBBAA", "#AARRGGBB".
     * A missing alpha channel defaults to fully opaque (0xFF).
     * Non-hex inputs are returned unchanged (safe pass-through).
     */
    private fun hexToKuiklyColor(hex: String): String {
        // CSS keyword: fully-transparent. KR's `toColor` uses
        // `Long.parseLong` which crashes on any non-decimal token, so we
        // pre-translate the one keyword A2UI JSON authors reasonably
        // expect to work. Additional named colors could join this list,
        // but "transparent" is the only one with no compact hex alias.
        if (hex.equals("transparent", ignoreCase = true)) return "0"
        // Defensive fast-path: an already-decimal string (e.g. seeded by
        // earlier passes of this same helper, or emitted directly by
        // A2UIComponentMapper.normalizeColorToArgbDecimal) is safe to hand
        // straight to KR. Skip the fall-through crash-shield below.
        if (isDecimalColorLiteral(hex)) return hex
        if (!hex.startsWith("#")) {
            // Any non-hex, non-decimal, non-keyword literal (e.g. a typo
            // like "reed", or a legacy CSS keyword like "black" that
            // slipped past schema validation) is a **defect** in the
            // source JSON — but the catalog's `pattern` is not enforced
            // at runtime, so we cannot rely on schema rejection. Rather
            // than let the token crash KR's `Long.parseLong`, downgrade
            // to fully-transparent black ("0") and log once for triage.
            reportInvalidColorLiteral(hex)
            return "0"
        }
        val body = hex.substring(1)
        // Expand to 8-digit AARRGGBB
        val aarrggbb: String = when (body.length) {
            3 -> {
                // #RGB -> #FFRRGGBB (each nibble doubled)
                val r = body[0]; val g = body[1]; val b = body[2]
                "FF$r$r$g$g$b$b"
            }
            6 -> "FF$body"                             // #RRGGBB
            8 -> {
                // Ambiguity: "#RRGGBBAA" (web/CSS) vs "#AARRGGBB" (Android).
                // The A2UI codebase uses the Android convention (#33000000
                // = 20% opaque black shadow), so treat 8-digit as AARRGGBB.
                body
            }
            else -> {
                // Malformed hex length (#12, #1234, #123456789, ...). Same
                // downgrade as above — never let it reach KR unparsed.
                reportInvalidColorLiteral(hex)
                return "0"
            }
        }
        val value = aarrggbb.toLongOrNull(16)
        if (value == null) {
            // Hex body has non-hex characters (e.g. "#GG0000"). Downgrade.
            reportInvalidColorLiteral(hex)
            return "0"
        }
        return value.toString()
    }

    /**
     * True iff [s] is a non-empty run of decimal digits (optionally with a
     * single leading minus). Kuikly Render's color parsers accept exactly
     * this shape, so anything matching here is safe to hand through the
     * crash-shield unchanged.
     */
    private fun isDecimalColorLiteral(s: String): Boolean {
        if (s.isEmpty()) return false
        val start = if (s[0] == '-') 1 else 0
        if (start >= s.length) return false
        for (i in start until s.length) {
            if (s[i] !in '0'..'9') return false
        }
        return true
    }

    /**
     * Emit a one-line warning for a color literal that cannot be normalized
     * to a KR-parseable decimal. This should be treated as a schema
     * violation — the offending JSON is expected to be authored with hex
     * colors per `kuikly_a2ui_catalog.json#/definitions/styles/backgroundColor`.
     */
    private fun reportInvalidColorLiteral(raw: String) {
        // Match A2UIDSLBridge's `[A2UI-DSL] ...` prefix convention so log
        // scrapers can pick this up alongside other adapter diagnostics.
        println("[A2UI-Adapter] Invalid color literal '$raw' — downgraded to transparent. Use CSS hex ('#RRGGBB' / '#AARRGGBB') or 'transparent'.")
    }

    /**
     * When a component's `styles` block introduces a
     * `backgroundLinearGradient`, the semantic defaults previously seeded
     * for that component type (typically a solid `backgroundColor`, a light
     * 1dp `border`, and a small `boxShadow` with iOS's
     * `wrapperBoxShadowView`) become visual noise:
     *
     *  - The solid `backgroundColor` and the gradient share KR's
     *    `KRCSSBackgroundDrawable` paint slot; whichever is applied last
     *    wins and can silently overpaint the gradient.
     *  - The default 1dp `#E0E0E0` `border` and `#1A000000` shadow were
     *    designed to sit against a white surface. On a saturated gradient
     *    card they read as a **grey halo at the four rounded corners**
     *    (the shadow leaks outside the clipPath; the border reads as a
     *    hairline against the vivid fill).
     *  - `wrapperBoxShadowView=1` is meaningless without a real shadow.
     *
     * Strip them so the user's gradient is rendered cleanly. Explicit
     * per-component `styles.boxShadow` / `styles.border` (which the caller
     * will have written into `props` on the very next line inside the
     * `styles`-expansion loop) survive because we only remove the keys
     * whose values still match the semantic defaults installed above.
     */
    private fun clearDefaultsIfGradient(props: MutableMap<String, Any?>) {
        val g = props["backgroundLinearGradient"]
        if (g !is String || g.isEmpty()) return
        // Always drop the solid color paint — it wipes the gradient.
        props.remove("backgroundColor")
        // Drop only the *default* light border/shadow. A caller who wants a
        // border/shadow on a gradient card can set `styles.border` /
        // `styles.boxShadow` explicitly; expandStyles runs before this
        // helper is invoked, so those overrides are already in `props` and
        // won't match the sentinel values below.
        //
        // Derive both the pre- and post-normalization sentinel from the
        // same source constants used by buildProps("Card"). props may hold
        // either form depending on whether hex→decimal normalization has
        // already run, so we compare against both.
        val borderNormalized = normalizeHexInShorthand(CARD_DEFAULT_BORDER_RAW)
        if (props["border"] == borderNormalized ||
            props["border"] == CARD_DEFAULT_BORDER_RAW) {
            props.remove("border")
        }
        val shadowNormalized = normalizeHexInShorthand(CARD_DEFAULT_BOX_SHADOW_RAW)
        if (props["boxShadow"] == shadowNormalized ||
            props["boxShadow"] == CARD_DEFAULT_BOX_SHADOW_RAW) {
            props.remove("boxShadow")
        }
        props.remove("wrapperBoxShadowView")
    }

    /**
     * Replace any hex color tokens inside a space-separated CSS shorthand
     * (e.g. "1 solid #E0E0E0" or "0 2 8 #33000000 0") with the decimal
     * form Kuikly's iOS renderer understands. Non-hex tokens are kept as-is.
     */
    private fun normalizeHexInShorthand(shorthand: String): String {
        if (!shorthand.contains('#')) return shorthand
        return shorthand.split(' ').joinToString(" ") { token ->
            if (token.startsWith("#")) hexToKuiklyColor(token) else token
        }
    }

    /**
     * Heuristic: does this props key semantically carry a color value?
     *
     * Matches the exact key `"color"` (Text foreground) and any key ending
     * with `Color` (case-insensitive): `backgroundColor`, `borderColor`,
     * `progressColor`, `shadowColor`, `placeholderColor`, `activeColor`,
     * `iconColor`, and the internal `__primaryColor` sentinel consumed by
     * [A2UIApplier] for CheckBox / Button / Slider tinting.
     *
     * Keeping this a keyname predicate (rather than an explicit allowlist)
     * means new theming props added to the catalog are automatically
     * normalized without touching this file.
     */
    private fun isColorPropKey(key: String): Boolean {
        if (key == "color") return true
        return key.length > "Color".length && key.endsWith("Color", ignoreCase = true)
    }

    /**
     * Walk [props] and rewrite every color-valued field whose value is a
     * hex literal (`#RGB` / `#RRGGBB` / `#AARRGGBB`) into the decimal
     * AARRGGBB string that Kuikly's per-platform renderers (iOS
     * `[NSString longLongValue]`, ohos `std::stol`, Android
     * `String.toLong`) all agree on.
     *
     * Rationale: Kuikly does NOT register a shared hex-parsing color
     * adapter by default — the raw color-string path in the render layer
     * only accepts decimal ARGB. Any hex color that leaks through as a
     * naked value (i.e. NOT part of `border` / `boxShadow` / gradient
     * shorthand, which are already normalized token-by-token) becomes
     * fully-transparent black on iOS/ohos. That was the root cause of the
     * "3 shadow-only text glyphs visible, gradient/background missing"
     * regression on ohos and the "3 bubbles visible but card body white"
     * regression on iOS.
     *
     * This sweep happens at the last mile — every `buildProps*` /
     * `buildChoiceChipProps*` path funnels through here before its return
     * — so:
     *  1. seeded defaults (both string literals here and constants from
     *     the companion object) don't need to be hand-converted at every
     *     site.
     *  2. user-supplied `styles.*Color` values in the source JSON are
     *     covered without touching the styles-expansion loops.
     *  3. non-hex forms (already-decimal, `rgb(...)`, named colors) fall
     *     through untouched because [hexToKuiklyColor] short-circuits on
     *     any input that doesn't start with `#`.
     */
    private fun normalizeColorFieldsInPlace(props: MutableMap<String, Any?>) {
        // Snapshot the key set so we can safely rewrite values in-place even
        // on JVM/Android/Native where MutableMap iterators may not tolerate
        // put() on the *same* key during iteration on all impls.
        val keys = props.keys.toList()
        for (key in keys) {
            if (!isColorPropKey(key)) continue
            val v = props[key]
            // Pipe every String color value through hexToKuiklyColor so
            // its crash-shield can catch invalid literals ("red",
            // "rgb(...)", typos, ...) and downgrade them to "0". The
            // helper's own decimal fast-path keeps already-normalized
            // values (e.g. seeded defaults like "4294967295") cheap.
            if (v is String) {
                props[key] = hexToKuiklyColor(v)
            }
        }
    }

    /**
     * Immutable-flavor of [normalizeColorFieldsInPlace] for helpers that
     * return `Map<String, Any?>` rather than the mutable variant. Returns
     * the input untouched when it has no color fields (fast path) so we
     * don't churn allocations for shape-only prop maps like
     * [buildTabHeaderProps] or [buildCheckboxOptionRowProps].
     */
    private fun withNormalizedColors(props: Map<String, Any?>): Map<String, Any?> {
        // Fast path: no color-shaped key at all.
        var needs = false
        for (key in props.keys) {
            if (isColorPropKey(key)) {
                val v = props[key]
                // Any String color value must be piped through the
                // helper so invalid literals get downgraded. Fast-path
                // preservation for already-decimal values lives inside
                // hexToKuiklyColor itself.
                if (v is String) {
                    needs = true
                    break
                }
            }
        }
        if (!needs) return props
        val mutable = props.toMutableMap()
        normalizeColorFieldsInPlace(mutable)
        return mutable
    }

    // region Custom function evaluation

    /**
     * Fallback for [evaluateFunctionCall]: try to evaluate `call(args)` as a
     * user-registered custom A2UI function returning a scalar/array value.
     *
     * Returns the value stringified (this is how A2UI's data-binding pipeline
     * consumes function results in expression contexts). NUMBER results are
     * formatted with A2UI's normal numeric-to-string conversion; ARRAY results
     * are returned as JSON. Returns `null` if the function is unknown or the
     * platform-side invoker is unavailable.
     */
    private fun evaluateCustomFunctionAsString(call: String, args: Map<String, Any?>): String? {
        val descriptor = com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry.lookup(call)
        if (descriptor == null) {
            return null
        }
        // VOID functions are only valid inside `action.functionCall`, never as
        // expressions — evaluating them here would be a semantic error but we
        // simply return null to keep the UI from crashing on malformed inputs.
        if (descriptor.returnType == com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.VOID) {
            return null
        }

        val invoker = customFunctionInvoker
        if (invoker == null) {
            return null
        }
        // Resolve dynamic arguments (path references, nested function calls)
        // before serializing to JSON — same as built-in functions do via
        // resolveDynamicArg.
        val resolvedArgs = resolveCustomFunctionArgs(args)
        val argsJson = customArgsToJsonString(resolvedArgs)
        val raw = try {
            invoker(call, argsJson, descriptor.toSpec())
        } catch (_: Exception) {
            null
        }
        if (raw == null) {
            return null
        }

        val coerced = coerceCustomReturnToString(raw, descriptor.returnType)
        return coerced
    }

    /**
     * Fallback for [evaluateCheckFunction]: try to evaluate `call(args)` as a
     * user-registered custom check (BOOLEAN-returning) function.
     *
     * Unknown / mistyped custom functions default to `true` to preserve the
     * existing A2UI protocol behaviour ("unknown check ⇒ pass"), matching
     * how the built-in path handles unrecognized names.
     */
    private fun evaluateCustomFunctionAsBoolean(call: String, args: Map<String, Any?>): Boolean {
        val descriptor = com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry.lookup(call)
            ?: return true // Unknown function: pass by default (protocol behaviour)
        if (descriptor.returnType != com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.BOOLEAN) return true

        val invoker = customFunctionInvoker ?: return true
        // Resolve dynamic arguments (path references, nested function calls)
        val resolvedArgs = resolveCustomFunctionArgs(args)
        val argsJson = customArgsToJsonString(resolvedArgs)
        val raw = try {
            invoker(call, argsJson, descriptor.toSpec())
        } catch (e: Exception) {
            null
        } ?: return true

        return coerceCustomReturnToBoolean(raw)
    }

    /**
     * Resolve all dynamic arguments in a custom function's args map.
     * Each value may be:
     *   - A literal (String, Number, Boolean) → kept as-is
     *   - A path reference: {"path": "/key"} → resolved from dataModel
     *   - A nested function call: {"call": "...", "args": {...}} → recursively evaluated
     *
     * This mirrors what built-in functions do via [resolveDynamicArg] on each
     * individual argument.
     */
    private fun resolveCustomFunctionArgs(args: Map<String, Any?>): Map<String, Any?> {
        val resolved = mutableMapOf<String, Any?>()
        for ((key, value) in args) {
            resolved[key] = resolveDynamicArg(value)
        }
        return resolved
    }

    /**
     * Encode a resolved argument map into a JSON string that is fed to the
     * native invoker. Values are already resolved (path references and nested
     * function calls have been reduced to concrete values by the caller), so
     * a straight JSON encoding is enough. We reuse Kuikly's JSONObject.
     */
    private fun customArgsToJsonString(args: Map<String, Any?>): String {
        val obj = com.tencent.kuikly.core.nvi.serialization.json.JSONObject()
        for ((k, v) in args) {
            when (v) {
                null -> { /* JSONObject drops null keys — leave key absent */ }
                is Boolean -> obj.put(k, v)
                is Int -> obj.put(k, v)
                is Long -> obj.put(k, v)
                is Float -> obj.put(k, v.toDouble())
                is Double -> obj.put(k, v)
                is Number -> obj.put(k, v.toDouble())
                is String -> obj.put(k, v)
                is Map<*, *> -> obj.put(k, mapToJsonObject(v))
                is List<*> -> obj.put(k, listToJsonArray(v))
                else -> obj.put(k, v.toString())
            }
        }
        return obj.toString()
    }

    private fun mapToJsonObject(m: Map<*, *>): com.tencent.kuikly.core.nvi.serialization.json.JSONObject {
        val o = com.tencent.kuikly.core.nvi.serialization.json.JSONObject()
        for ((k, v) in m) {
            val key = k?.toString() ?: continue
            when (v) {
                null -> {}
                is Boolean -> o.put(key, v)
                is Number -> o.put(key, v.toDouble())
                is String -> o.put(key, v)
                is Map<*, *> -> o.put(key, mapToJsonObject(v))
                is List<*> -> o.put(key, listToJsonArray(v))
                else -> o.put(key, v.toString())
            }
        }
        return o
    }

    private fun listToJsonArray(l: List<*>): com.tencent.kuikly.core.nvi.serialization.json.JSONArray {
        val a = com.tencent.kuikly.core.nvi.serialization.json.JSONArray()
        for (v in l) {
            when (v) {
                null -> a.put(null)
                is Boolean -> a.put(v)
                is Number -> a.put(v.toDouble())
                is String -> a.put(v)
                is Map<*, *> -> a.put(mapToJsonObject(v))
                is List<*> -> a.put(listToJsonArray(v))
                else -> a.put(v.toString())
            }
        }
        return a
    }

    /**
     * Coerce the raw value returned by the native invoker into a String, based
     * on the declared A2UI return type. The native side is free to hand us
     * back the closest native scalar (Boolean/Number/String) or a JSON string
     * for ARRAY.
     */
    private fun coerceCustomReturnToString(
        raw: Any,
        returnType: com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType
    ): String {
        return when (returnType) {
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.STRING -> raw.toString()
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.BOOLEAN ->
                if (coerceCustomReturnToBoolean(raw)) "true" else "false"
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.NUMBER -> when (raw) {
                is Number -> {
                    val d = raw.toDouble()
                    // Preserve integer look for whole numbers (e.g. 3 -> "3"), match
                    // formatNumber's default style so downstream text bindings render
                    // consistently.
                    if (d == d.toLong().toDouble()) d.toLong().toString() else d.toString()
                }
                // On Kotlin/Native (iOS), NSNumber(value: 0.0) may be bridged as
                // Boolean(false) and NSNumber(value: 1.0) as Boolean(true). When the
                // declared return type is NUMBER, coerce booleans back to numeric strings.
                is Boolean -> if (raw) "1" else "0"
                is String -> {
                    // Try to normalize numeric strings (e.g. "139.3" stays as-is,
                    // but "true"/"false" from mis-bridging gets caught above).
                    raw
                }
                else -> raw.toString()
            }
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.ARRAY -> raw.toString()
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.VOID -> ""
        }
    }

    /**
     * Coerce the raw value returned by the native invoker into a Boolean.
     * Accepts native Booleans directly and treats the strings "true"/"false"
     * (case-insensitive) as their obvious values; everything else follows a
     * relaxed "truthy" rule consistent with [toBooleanValue] elsewhere in this
     * file.
     */
    private fun coerceCustomReturnToBoolean(raw: Any): Boolean {
        return when (raw) {
            is Boolean -> raw
            is Number -> raw.toDouble() != 0.0
            is String -> when (raw.lowercase()) {
                "true", "1", "yes" -> true
                "false", "0", "no", "" -> false
                else -> true
            }
            else -> true
        }
    }

    // endregion
}

/**
 * Represents a ChildList template binding.
 * When a container's "children" is {"path": "/items", "componentId": "template-id"},
 * this binding is stored and expanded when the data at the path becomes available.
 */
data class ChildListBinding(
    /** The data path pointing to an array in the data model (e.g., "/events") */
    val dataPath: String,
    /** The component ID of the template to instantiate for each array item */
    val templateId: String,
    /** IDs of currently generated instances (for cleanup on re-expansion) */
    val generatedInstanceIds: MutableList<String> = mutableListOf()
)

/**
 * Represents a single tab definition within a Tabs component.
 */
data class TabDefinition(
    /** The display title of the tab (resolved at creation time) */
    val title: String,
    /** The component ID of the tab's content */
    val childId: String,
    /** The original title spec from the protocol (String or Map with path binding) for re-resolution on data update */
    val titleSpec: Any? = null
)

/**
 * Represents a Modal component definition.
 *
 * Per the A2UI spec, a Modal is a "Modal Entry Point": only the [triggerId]
 * component is shown inline; tapping it opens a dialog displaying [contentId].
 */
data class ModalDefinition(
    /** Component ID of the trigger (rendered inline; opens the modal when tapped) */
    val triggerId: String,
    /** Component ID of the content shown inside the modal when open */
    val contentId: String
)
