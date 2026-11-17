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

import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BACKGROUND_COLOR
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BORDER_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BOX_SHADOW_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.clearDefaultsIfGradient
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.hexToKuiklyColor
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeColorFieldsInPlace
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeHexInShorthand
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.withNormalizedColors
import com.tencent.kuikly.a2ui.adapter.A2UIDataPath.parsePathSegments
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.addThousandsSeparator
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToBool
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToInt
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.doubleToPlainString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.findMatchingBrace
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDateString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDouble
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.splitArguments
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolDefaults
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.protocol.A2UISurfaceConfig
import com.tencent.kuikly.a2ui.protocol.ProtocolKeys
import com.tencent.kuikly.a2ui.protocol.ValidationResult
import com.tencent.kuikly.a2ui.rpc.A2UIRpcDispatcher
import com.tencent.kuikly.a2ui.rpc.A2UIRpcError
import com.tencent.kuikly.a2ui.rpc.A2UIRpcRequest
import com.tencent.kuikly.a2ui.rpc.A2UIRpcResponse


internal class A2UIComponentProjection(
    internal val model: A2UISurfaceModel,
    internal val decoder: A2UIProtocolDecoder,
    internal val values: A2UIValueEngine,
    internal val reactive: A2UIReactiveEngine,
    internal val interactions: A2UIInteractionState,
) {
    internal val idToOriginalDef get() = model.definitions
    internal val originalChildToParent get() = model.originalParents
    internal val childToParentCache get() = model.currentParents
    internal val componentVariantCache get() = model.variants
    internal val componentCatalogIds get() = model.componentCatalogs
    internal val dataModel get() = model.data
    internal val _protocolVersion get() = decoder.protocolVersion
    internal val strictV10 get() = decoder.strictV10
    internal var currentSurfaceId: String
        get() = model.surfaceId
        set(value) { model.surfaceId = value }
    internal var currentCatalogId: String?
        get() = model.catalogId
        set(value) { model.catalogId = value }
    internal var primaryColor: String
        get() = model.primaryColor
        set(value) { model.primaryColor = value }
    internal var sendDataModel: Boolean
        get() = model.sendDataModel
        set(value) { model.sendDataModel = value }

    internal var pendingProperties: A2UIProjectionDraft? = null
    internal val materialization = A2UIMaterializationState()

    internal fun tabCount(id: String): Int? = tabsDefinitions[id]?.size
    internal fun modalContentId(id: String): String? = modalDefinitions[id]?.contentId
    internal fun isMaterialized(id: String): Boolean = idToTag[id]?.let(materialization::contains) == true

    internal fun context(item: Map<String, Any?>? = null): A2UIEvaluationContext {
        val initial = pendingProperties?.initialValues.orEmpty()
        val data = if (initial.isEmpty()) model.data else model.data + initial
        return A2UIEvaluationContext(data, item, currentTemplateIndex, model.catalogId)
    }

    internal fun resolveValue(value: Any?, componentId: String? = null): Any? {
        val result = values.evaluate(value, context(), componentId != null)
        if (componentId != null) {
            val draft = pendingProperties
            if (draft == null) {
                result.paths.forEach { reactive.registerPath(componentId, it) }
                result.functions.forEach { reactive.registerFunction(componentId, it) }
            } else {
                draft.pathDependencies.getOrPut(componentId) { linkedSetOf() }.addAll(result.paths)
                draft.functionDependencies.getOrPut(componentId) { linkedSetOf() }.addAll(result.functions)
            }
        }
        return result.value
    }

    internal fun resolveValueWithScope(value: Any?, item: Map<String, Any?>, instanceId: String?): Any? =
        values.evaluate(value, context(item), false).value

    internal fun resolveRelativePath(path: String, item: Map<String, Any?>): Any? =
        A2UIEvaluationWorker(context(item), decoder, values.customFunctionInvoker).resolveRelativePath(path, item)

    internal fun evaluateFunctionCall(call: Map<*, *>): String? = values.function(call, context())

    internal fun resolveNestedDataPath(key: String): Any? = model.read(key)
    internal fun setNestedDataPath(key: String, value: Any?) {
        model.write(key, value, A2UIUpdateOrigin.WIRE)
    }
    internal fun getRootDataKey(key: String) = A2UIDataPath.getRootDataKey(key)
    internal fun readPathKey(map: Map<*, *>) = decoder.readPathKey(map)
    internal fun readCallKey(map: Map<*, *>) = decoder.readCallKey(map)
    internal fun readChecksKey(map: Map<*, *>) = decoder.readChecksKey(map)
    internal fun readAccessibilityKey(map: Map<*, *>) = decoder.readAccessibilityKey(map)
    internal fun readMixinKey(map: Map<*, *>, key: String) = decoder.readMixinKey(map, key)
    internal fun bindingSkipKeys(extra: Set<String> = emptySet()) = decoder.bindingSkipKeys(extra)
    internal fun unescapeAtKeysDeep(value: Any?) = decoder.unescapeAtKeysDeep(value)
    internal fun extractDataPaths(value: Any?) = values.paths(value)
    internal fun resolveComponentCatalogId(map: Map<*, *>): String? {
        if (_protocolVersion != A2UIProtocolVersion.V1_0) return null
        return readMixinKey(map, "catalogId")?.takeIf { it.isNotEmpty() } ?: model.catalogId
    }
    internal fun registerChecks(id: String, checks: List<*>) {
        val paths = checks.filterIsInstance<Map<*, *>>().flatMap { extractDataPaths(it["condition"]) }.toSet()
        val draft = pendingProperties
        if (draft == null) {
            reactive.registerChecks(id, checks, paths)
        } else {
            @Suppress("UNCHECKED_CAST")
            val rules = checks.filterIsInstance<Map<*, *>>().map { it as Map<String, Any?> }
            if (rules.isNotEmpty()) draft.checks[id] = A2UICheckRegistration(rules, paths)
        }
    }
    internal fun evaluateChecksForComponent(id: String): ValidationResult {
        val rules = pendingProperties?.checks?.get(id)?.rules ?: reactive.checksFor(id)
        for (rule in rules) {
            val rawMessage = rule["message"] as? String
            val message = rawMessage ?: run {
                if (_protocolVersion == A2UIProtocolVersion.V1_0) decoder.reportBareKeyUnderV10("checks.message[missing]")
                "Validation failed"
            }
            if (!values.check(rule["condition"], context())) {
                val name = (rule["condition"] as? Map<*, *>)?.let { readCallKey(it) }
                return ValidationResult.failure(message, name)
            }
        }
        return ValidationResult.VALID
    }

    fun reset() {
        idToTag.clear()
        materialization.reset()
        tagToComponentName.clear()
        childListBindings.clear()
        instanceScopePath.clear()
        nextTag = 1
        templateIndexStack.clear()
    }

    /** Map from string component ID to integer tag */
    internal val idToTag = mutableMapOf<String, Int>()

    /** Map from tag to component standard name */
    internal val tagToComponentName = mutableMapOf<Int, String>()

    /** Map from component ID to its current children IDs (for incremental updates) */
    internal val idToChildren = mutableMapOf<String, List<String>>()

    /** Map from component ID to its check error text view tag */
    internal val idToCheckErrorTag = mutableMapOf<String, Int>()

    /** Map from component ID to its check wrapper Column tag */
    internal val idToCheckWrapperTag = mutableMapOf<String, Int>()

    /**
     * Map from component ID to the tag of the persistent label/title Text rendered ABOVE
     * the component (TextField/DateTimeInput `label`, AudioPlayer `description`). Used to
     * refresh the label when its bound data path arrives/changes (the label is not a
     * normal component with its own id, so it is updated explicitly here).
     */
    internal val idToFieldLabelTag = mutableMapOf<String, Int>()

    /** Map from component ID to the original label/title spec (literal or data binding) for [idToFieldLabelTag]. */
    internal val idToFieldLabelSpec = mutableMapOf<String, Any?>()

    /** ChoicePicker: map from component ID to list of chip container tags */
    internal val choicePickerChipTags = mutableMapOf<String, List<Int>>()

    /** ChoicePicker: map from component ID to list of chip option values (parallel to chipTags) */
    internal val choicePickerChipValues = mutableMapOf<String, List<String>>()

    /** ChoicePicker: map from component ID to list of chip option labels (parallel to chipTags) */
    internal val choicePickerChipLabels = mutableMapOf<String, List<String>>()

    /** ChoicePicker: map from component ID to displayStyle ("chips" or "checkbox") */
    internal val choicePickerDisplayStyles = mutableMapOf<String, String>()

    /** ChoicePicker: map from component ID to variant ("mutuallyExclusive" or "multipleSelection") */
    internal val choicePickerVariants = mutableMapOf<String, String>()

    /** ChoicePicker: map from component ID to the filter input tag (when filterable=true) */
    internal val choicePickerFilterInputTag = mutableMapOf<String, Int>()

    /** Tabs: map from Tabs component ID to its tab definitions (title + child ID) */
    internal val tabsDefinitions = mutableMapOf<String, List<TabDefinition>>()

    /** Tabs: map from Tabs component ID to list of tab header button tags */
    internal val tabsHeaderTags = mutableMapOf<String, List<Int>>()

    /** Tabs: map from Tabs component ID to list of content wrapper tags (one per tab) */
    internal val tabsContentWrapperTags = mutableMapOf<String, List<Int>>()

    /** Modal: map from Modal component ID to its definition (trigger + content IDs) */
    internal val modalDefinitions = mutableMapOf<String, ModalDefinition>()

    /** Modal: map from trigger component ID to the Modal component ID it opens */
    internal val modalTriggerToId = mutableMapOf<String, String>()

    /** Modal: set of component IDs (the content subtree) deferred until the modal opens */
    internal val modalDeferredIds = mutableSetOf<String>()

    /**
     * Modal: map from Modal ID to the list of (tag, parentTag) created for its open
     * overlay, in creation order. Used to tear the overlay down (children first) on close.
     */
    internal val modalOverlayTags = mutableMapOf<String, MutableList<Pair<Int, Int>>>()

    /** ChildList template bindings: parentId -> ChildListBinding(dataPath, templateId) */
    internal val childListBindings = mutableMapOf<String, ChildListBinding>()

    /** Map from generated instance ID to its relative data scope path (e.g., "/events/0") */
    internal val instanceScopePath = mutableMapOf<String, String>()

    /** Next available tag */
    internal var nextTag = 1
    internal val templateIndexStack: ArrayDeque<Int> = ArrayDeque()

    /** Top of [templateIndexStack], or `null` outside any template scope. */
    internal val currentTemplateIndex: Int?
        get() = templateIndexStack.lastOrNull()
    fun refreshBindingValue(change: A2UIDataChange): List<A2UIMessage> {
        val key = change.paths.first()

        // Re-evaluate bindings that depend on this key. Path dependents are registered
        // under the ROOT data key (getRootDataKey), e.g. a Text bound to "/event/name" is
        // tracked under "event", so a change to the nested key "event/name" must also look
        // up the root key. Without this, sibling components bound to the same object (e.g.
        // a preview Text mirroring a TextField's "/event/name") never re-render.
        val messages = mutableListOf<A2UIMessage>()
        val impact = reactive.impact(change)
        val affectedComponentIds = impact.components

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
                val selectedIndex = interactions.selectedTab(compId)
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
            val skipKeys = bindingSkipKeys()
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
        val checksAffectedIds = impact.checks

        for (compId in checksAffectedIds) {
            val originalDef = idToOriginalDef[compId] ?: continue
            val componentType = originalDef["component"] as? String ?: continue
            // PR-5: evaluateChecksForComponent now returns a structured
            // ValidationResult; existing downstream code only needs the
            // user-facing message, which keeps the `String?` null-means-pass
            // contract the Button / error-text props expect.
            val errorMessage = evaluateChecksForComponent(compId).errorMessage

            if (componentType == "Button") {
                // Button: toggle the dispatch guard, dim the button, and show/hide
                // the inline hint text. Per A2UI spec a failing button is disabled.
                val btnTag = idToTag[compId]
                if (btnTag != null) interactions.setDisabled(btnTag, errorMessage != null)
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
    internal fun handleCreateSurface(data: Map<String, Any?>): List<A2UIMessage> {
        return when (_protocolVersion) {
            A2UIProtocolVersion.V0_9, A2UIProtocolVersion.V0_9_1 ->
                handleCreateSurfaceV09(data)
            A2UIProtocolVersion.V1_0 ->
                handleCreateSurfaceV10(data)
        }
    }

    /**
     * v0.9 / v0.9.1 implementation. Unchanged from pre-PR-3 behaviour so
     * existing payloads render identically.
     */
    internal fun handleCreateSurfaceV09(data: Map<String, Any?>): List<A2UIMessage> {
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

    /**
     * v1.0 implementation.
     *
     * Changes relative to v0.9:
     *   1. `theme` is no longer accepted — strict-mode rejection happens
     *      upstream in [convertMessageV10]; lenient mode silently drops
     *      it here (NO primaryColor update).
     *   2. `createSurface` may carry inline `components` and `dataModel`
     *      fields that are expanded into equivalent updateComponents /
     *      updateDataModel messages inside the SAME batch, so the surface
     *      is renderable from a single envelope (per v1.0 spec
     *      §"Single-envelope surface creation").
     *   3. `catalogId` is plumbed through [A2UISurfaceConfig] for use by
     *      the component mapper when resolving per-component `@catalogId`
     *      overrides (full support lands in PR-8 — this PR just wires the
     *      data).
     */
    internal fun handleCreateSurfaceV10(data: Map<String, Any?>): List<A2UIMessage> {
        val surfaceId = data["surfaceId"] as? String ?: return emptyList()
        currentSurfaceId = surfaceId

        // sendDataModel semantics unchanged from v0.9.
        sendDataModel = data["sendDataModel"] as? Boolean ?: false

        // ---- catalogId -----------------------------------------------
        // Per A2UI v1.0 spec (§createSurface), the surface-level catalog
        // field is `catalogId` — a plain string identifier, not a
        // resolvable URI. No aliases, no `catalogs` allow-list: the field
        // set is exactly `surfaceId, catalogId, sendDataModel,
        // components, dataModel, metadata`.
        val catalogId = data["catalogId"] as? String

        // ---- PR-8: expose the resolved catalogId to downstream handlers
        //      (handleUpdateComponents / evaluateFunctionCall /
        //      evaluateFunctionCallWithScope). Any catalogId the server
        //      attaches to a component / FunctionCall site is trusted as
        //      sent.
        currentCatalogId = catalogId
        model.clearCatalogs()

        // Emit the CREATE_SURFACE message first so downstream consumers
        // see the surface before any updateComponents/updateDataModel.
        val messages = mutableListOf(
            A2UIMessage(
                type = A2UIMessageType.CREATE_SURFACE,
                surfaceId = surfaceId,
                config = A2UISurfaceConfig(
                    width = 0f, // Will be set by the platform layer
                    height = 0f,
                    incrementalRender = true,
                    catalogId = catalogId
                )
            )
        )

        val inlineDataModel = data["dataModel"]
        if (inlineDataModel != null) {
            val wrapped = mapOf<String, Any?>(
                "surfaceId" to surfaceId,
                "path" to "/",
                "value" to inlineDataModel
            )
            messages.addAll(handleUpdateDataModel(wrapped))
        }

        val inlineComponents = data["components"]
        if (inlineComponents is List<*>) {
            val wrapped = mapOf<String, Any?>(
                "surfaceId" to surfaceId,
                "components" to inlineComponents
            )
            messages.addAll(handleUpdateComponents(wrapped))
        }

        return messages
    }
    internal fun handleUpdateDataModel(data: Map<String, Any?>, pendingOutput: List<A2UIMessage> = emptyList()): List<A2UIMessage> {
        val surfaceId = data["surfaceId"] as? String ?: currentSurfaceId
        // NOTE(v1.0): `updateDataModel` envelope keeps bare `path` / `value` /
        //   `surfaceId` field names in v1.0 — the `@`-prefix rename only
        //   applies to in-document DataBinding / FunctionCall mixin keys,
        //   NOT to top-level envelope fields. Do not route this through
        //   readPathKey().
        val path = data["path"] as? String

        // --- v1.0 strict-mode `value`-presence validation -------------
        // Per `evolution_guide.md` §"updateDataModel tightened" the `value`
        // field is MANDATORY in v1.0. v0.9 payloads often omitted it and
        // were silently tolerated; v1.0 strict mode must reject such
        // envelopes. We check for KEY PRESENCE here, not non-null, because
        // `value: null` is the explicit v1.0 delete signal and must be
        // allowed through.
        if (_protocolVersion == A2UIProtocolVersion.V1_0 && strictV10 &&
            !data.containsKey("value")
        ) {
            println(
                "[A2UI] error: v1.0 strict: updateDataModel is missing the " +
                    "required `value` field (path=$path). Reject envelope."
            )
            return emptyList()
        }

        // Per A2UI protocol, `value` can be any JSON type (string / number / boolean /
        // array / object). Keep it as Any? here and only narrow to Map where the
        // full-replace semantics require it.
        // Under v1.0 we additionally apply the `@@xxx` → `@xxx` key-unescape
        // rule across the entire structure so user-authored dictionaries
        // that intentionally carry a literal `@`-prefixed key land in the
        // data model with that literal name intact (per
        // evolution_guide.md §"Reserved @ prefix"). For v0.9 this is a no-op.
        val value: Any? = unescapeAtKeysDeep(data["value"])
        @Suppress("UNCHECKED_CAST")
        val valueAsMap = value as? Map<String, Any?>

        // v1.0 delete signal: `value: null` at a specific path means
        // "remove this leaf and notify downstream". We detect it here so
        // the UPDATE_DATA emit branch below can propagate the deletion.
        val isV10DeleteSignal = _protocolVersion == A2UIProtocolVersion.V1_0 &&
            path != null && data.containsKey("value") && value == null

        if (value == null && path == null && !isV10DeleteSignal) return emptyList()

        // Per protocol: if path is omitted or "/", replace entire data model
        // If path is specified, update at that path
        if (path == null || path == "/") {
            // Replace entire data model — value must be a Map here.
            if (valueAsMap != null) {
                model.clearData()
                // A2UI extends flatten paths (e.g. "chart.items[0].label") in value
                // keys. If any key contains such syntax, unflatten it into nested
                // Map/List structure so downstream binding resolution works.
                val hasFlattenKey = valueAsMap.keys.any { k -> k.contains('.') || k.contains('[') }
                if (hasFlattenKey) {
                    for ((k, v) in valueAsMap) {
                        setNestedDataPath(k, v)
                    }
                } else {
                    model.replaceData(valueAsMap, A2UIUpdateOrigin.WIRE)
                }
            }
        } else {
            // Update at specific path (e.g., "/user/firstName" -> nested update).
            // value may be any JSON type; setNestedDataPath supports arbitrary leaf values.
            // A null value removes the leaf (see setNestedDataPath KDoc) — this is
            // also how the v1.0 delete signal is realised.
            val key = path.removePrefix("/")
            // Write into the nested Map structure so resolveNestedDataPath() can
            // read the value back (handles both single-segment and nested keys).
            setNestedDataPath(key, value)
        }

        val messages = mutableListOf<A2UIMessage>()

        // Emit UPDATE_DATA message for the dispatcher's binding mechanism.
        // For full-replace, forward the original Map. For partial upsert, wrap the
        // written value under its path key so downstream consumers see a Map too.
        // For v1.0 delete (value == null at a specific path) we still emit an
        // UPDATE_DATA carrying `{pathKey -> null}` so dependents re-resolve
        // bindings and ChildLists shrink correctly.
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
        } else if (isV10DeleteSignal) {
            val pathKey = path!!.removePrefix("/")
            messages.add(A2UIMessage(
                type = A2UIMessageType.UPDATE_DATA,
                surfaceId = surfaceId,
                data = mapOf(pathKey to null)
            ))
        }

        // Expand ChildList template bindings that depend on changed data
        if (_protocolVersion == A2UIProtocolVersion.V1_0) {
            messages.addAll(reconcileChildLists(surfaceId, pendingOutput))
        } else {
            messages.addAll(expandChildListBindings(surfaceId))
        }

        // Re-evaluate bindings that depend on changed keys
        val changedKeys = valueAsMap?.keys ?: setOf(path?.removePrefix("/") ?: "")
        // Path dependents are registered under the ROOT data key (getRootDataKey), so a
        // change to a nested key like "event/name" must also resolve the root key "event".
        val impact = reactive.impact(A2UIDataChange(changedKeys.toSet(), A2UIUpdateOrigin.WIRE))
        val affectedComponentIds = impact.components

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
                val selectedIndex = interactions.selectedTab(compId)
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
            val skipKeys = bindingSkipKeys()
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
        val checksAffectedIds = impact.checks

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

            // PR-5: see note above — only .errorMessage is consumed here.
            val errorMessage = evaluateChecksForComponent(compId).errorMessage

            if (componentType == "Button") {
                // Button: toggle the dispatch guard, dim the button, and show/hide
                // the inline hint text. Per A2UI spec a failing button is disabled.
                interactions.setDisabled(tag, errorMessage != null)
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
}
