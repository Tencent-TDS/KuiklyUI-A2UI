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

internal fun A2UIComponentProjection.handleUpdateComponents(data: Map<String, Any?>): List<A2UIMessage> {
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

        // ---- PR-8: pre-pass \u2014 resolve each component's catalogId -----
        // Reads the `@catalogId` mixin key (bare `catalogId` also tolerated
        // under lenient v1.0 just like other `@`-prefixed keys) and falls
        // back to the surface-level catalogId captured by
        // [handleCreateSurfaceV10]. Under v1.0 strict mode a component that
        // ends up with no resolved catalogId is skipped and reported via
        // the standard `[A2UI] error:` channel \u2014 the rest of the batch
        // still renders (matching how other v1.0 strict checks degrade).
        //
        // The resolved catalogId is cached in [componentCatalogIds] AND
        // injected as `__catalogId` on each compMap's view of the world
        // via a sidecar read (we don't mutate the input Map, see
        // readResolvedCatalogId). The `@catalogId` key is listed in
        // [com.tencent.kuikly.a2ui.schema.A2UIReservedKeys] so the lenient
        // path doesn't flag it as an unknown reserved key.
        val catalogSkipIds = mutableSetOf<String>()
        val isStrict = strictV10 && _protocolVersion == A2UIProtocolVersion.V1_0
        for (comp in components) {
            if (comp !is Map<*, *>) continue
            val id = comp["id"] as? String ?: continue
            val resolved = resolveComponentCatalogId(comp)
            if (resolved != null) {
                model.rememberCatalog(id, resolved)
            } else if (isStrict) {
                // v1.0 strict: no `@catalogId`, no surface `catalogId` —
                // we cannot know which catalog's schema to validate this
                // component against. Skip it instead of rendering with
                // ambiguous semantics.
                println(
                    "[A2UI] error: v1.0 strict: UNRESOLVED_CATALOG: " +
                        "component '$id' has no @catalogId and the surface " +
                        "has no catalogId; component skipped."
                )
                catalogSkipIds.add(id)
            }
            // Lenient mode (or v0.9): silently leave componentCatalogIds[id]
            // unset \u2014 downstream lookups fall back to the global/name-only
            // descriptor exactly like pre-PR-8.
        }

        // Second pass: build parent-child relationships and create messages
        // We need to determine parent from "children"/"child" references
        val childToParent = mutableMapOf<String, String>()
        val childOrder = mutableMapOf<String, Int>()
        // Update the class-level cache for use in buildProps
        model.beginComponentBatch()

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
                            // Also record in the persistent map so template parent
                            // lookups keep working after later batches clear the
                            // batch-scoped childToParentCache.
                            model.rememberParent(childId, id)
                        }
                    }
                } else if (children is Map<*, *>) {
                    // ChildList template binding: {"path":"/events", "componentId":"event-template"}
                    // NOTE(v1.0): per `evolution_guide.md` §"Reserved @ prefix",
                    //   ChildList template objects explicitly KEEP the bare
                    //   `path` / `componentId` keys — the `@`-prefix rename
                    //   ONLY applies to DataBinding / FunctionCall positions,
                    //   NOT to ChildList template shapes. Do not route this
                    //   through readPathKey().
                    val dataPath = children["path"] as? String
                    val templateId = children["componentId"] as? String
                    if (dataPath != null && templateId != null) {
                        val previous = childListBindings[id]
                        if (_protocolVersion != A2UIProtocolVersion.V1_0 || previous == null ||
                            previous.dataPath != dataPath || previous.templateId != templateId
                        ) {
                            childListBindings[id] = ChildListBinding(dataPath, templateId)
                        }
                        // Register dependency so we get notified when this data changes
                        val dataKey = dataPath.removePrefix("/")
                        reactive.registerPath("__childlist__$id", dataKey)
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
                        interactions.initializeTab(id)
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
        model.replaceBatchParents(childToParent)

        // Cache variant values for all components
        for (comp in components) {
            if (comp is Map<*, *>) {
                val compId = comp["id"] as? String ?: continue
                val variant = comp["variant"] as? String
                if (variant != null) {
                    model.rememberVariant(compId, variant)
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
                model.rememberDefinition(id, compMap)
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
            if (_protocolVersion == A2UIProtocolVersion.V1_0) {
                val deferred = mutableSetOf<String>()
                collectTemplateTree(modalDef.contentId, idToOriginalDef, childToParent, deferred)
                modalDeferredIds.addAll(deferred)
            } else {
                collectTemplateTree(modalDef.contentId, idToComp, childToParent, modalDeferredIds)
            }
        }
        model.replaceBatchParents(childToParent)

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
        val templateDefinitions = if (_protocolVersion == A2UIProtocolVersion.V1_0) idToOriginalDef else idToComp
        for ((_, binding) in childListBindings) {
            collectTemplateTree(binding.templateId, templateDefinitions, childToParent, templateIds)
        }

        // ---- PR-8: validate `allowedParents` / `allowedChildren` -------
        // Only runs under v1.0 strict mode because v0.9 carries no such
        // schema facet and lenient v1.0 historically renders as much as
        // possible.
        //
        // Lookup is delegated to [A2UICatalogDescriptorRegistry] so the
        // commonMain module has ZERO dependency on a JSON-schema parser
        // (host apps opt-in by calling
        // [A2UICatalogDescriptorRegistry.register] at startup).
        //
        // On a violation we DO NOT short-circuit the whole batch; the
        // offending component id is added to [catalogSkipIds] which the
        // CREATE loop below honours, mirroring how UNRESOLVED_CATALOG is
        // handled in the pre-pass above. Error channel is the standard
        // `[A2UI] error:` log line consumed by the Bridge diagnostics
        // pipe.
        if (isStrict) {
            for (comp in components) {
                if (comp !is Map<*, *>) continue
                val id = comp["id"] as? String ?: continue
                if (id in catalogSkipIds) continue
                val componentType = comp["component"] as? String ?: continue
                val catalogId = componentCatalogIds[id]
                val constraint = com.tencent.kuikly.a2ui.function
                    .A2UICatalogDescriptorRegistry.lookup(componentType, catalogId)
                    ?: continue // Nothing registered \u2192 unconstrained.

                // allowedParents check
                val parentId = childToParent[id]
                val parentType = parentId?.let {
                    (idToComp[it]?.get("component") as? String)
                }
                val allowedParents = constraint.allowedParents
                if (allowedParents != null &&
                    com.tencent.kuikly.a2ui.function.A2UIComponentConstraint.WILDCARD !in allowedParents
                ) {
                    val parentMatch = when {
                        // Component has no in-batch parent \u2192 treat as
                        // "mounted at surface root"; accept only if the
                        // catalog allows `Surface` as a parent.
                        parentType == null ->
                            com.tencent.kuikly.a2ui.function.A2UIComponentConstraint.ROOT_MARKER in allowedParents
                        else -> parentType in allowedParents
                    }
                    if (!parentMatch) {
                        val parentLabel = parentType
                            ?: com.tencent.kuikly.a2ui.function.A2UIComponentConstraint.ROOT_MARKER
                        println(
                            "[A2UI] error: v1.0 strict: UNALLOWED_PARENT: " +
                                "component '$id' (type=$componentType, " +
                                "catalogId=$catalogId) placed under '$parentLabel'; " +
                                "allowedParents=$allowedParents; component skipped."
                        )
                        catalogSkipIds.add(id)
                        continue
                    }
                }

                // allowedChildren check \u2014 applied from the parent's view:
                // for every child of this component listed in idToComp,
                // verify the child's type is allowed. We evaluate here so a
                // violation can point at the specific offending child id.
                val allowedChildren = constraint.allowedChildren
                if (allowedChildren != null &&
                    com.tencent.kuikly.a2ui.function.A2UIComponentConstraint.WILDCARD !in allowedChildren
                ) {
                    for ((childId, pId) in childToParent) {
                        if (pId != id) continue
                        val childType = idToComp[childId]?.get("component") as? String ?: continue
                        if (childType !in allowedChildren) {
                            println(
                                "[A2UI] error: v1.0 strict: UNALLOWED_CHILD: " +
                                    "component '$childId' (type=$childType) under " +
                                    "parent '$id' (type=$componentType, " +
                                    "catalogId=$catalogId); " +
                                    "allowedChildren=$allowedChildren; child skipped."
                            )
                            catalogSkipIds.add(childId)
                        }
                    }
                }
            }
        }

        for (id in sortedIds) {
            // Skip template components - they are only instantiated via ChildList expansion
            if (id in templateIds) continue
            // Skip Modal content subtrees - created lazily when the modal opens
            if (id in modalDeferredIds) continue
            // PR-8: skip components whose catalog resolution failed under
            // strict v1.0, or that violated allowedParents / allowedChildren.
            if (id in catalogSkipIds) continue

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
            val propertyPlan = planProperties(componentType, compMap, kuikly = isKuiklyComponent)
            val builtProps = commitProperties(propertyPlan)
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
                val hasChecks = (readChecksKey(compMap) != null || compMap["validationRegexp"] != null) &&
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
                        reactive.registerPath(id, rootKey)
                    }
                } else if (componentType == "Tabs" && tabsDefinitions.containsKey(id)) {
                    // Tabs: expand into a Column with tab header row + content panels
                    val tabDefs = tabsDefinitions[id]!!
                    val selectedIndex = interactions.selectedTab(id)

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

        if (_protocolVersion == A2UIProtocolVersion.V1_0) {
            messages.addAll(reconcileChildLists(surfaceId, messages))
        }
        return messages
    }
