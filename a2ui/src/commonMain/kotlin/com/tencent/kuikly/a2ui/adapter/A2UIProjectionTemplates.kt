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

internal fun A2UIComponentProjection.getChildrenIds(compMap: Map<String, Any?>): List<String> {
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
internal fun A2UIComponentProjection.expandChildListBindings(surfaceId: String): List<A2UIMessage> {
        return expandChildListBindings(surfaceId, onlyParents = null)
    }

internal fun A2UIComponentProjection.hasCompleteTemplate(templateId: String): Boolean {
    val visiting = mutableSetOf<String>()
    val complete = mutableSetOf<String>()
    fun visit(id: String): Boolean {
        if (id in complete) return true
        if (!visiting.add(id)) return false
        val definition = idToOriginalDef[id] ?: return false
        if (definition["component"] !is String) return false
        val children = getChildrenIds(definition).toMutableList()
        getInlineChildListBinding(definition)?.second?.let(children::add)
        if (!children.all(::visit)) return false
        visiting.remove(id)
        complete.add(id)
        return true
    }
    return visit(templateId)
}

internal fun A2UIComponentProjection.reconcileChildLists(surfaceId: String, output: List<A2UIMessage>): List<A2UIMessage> {
    val pendingTags = output.filter { it.type == A2UIMessageType.CREATE_COMPONENT }.mapTo(mutableSetOf()) { it.tag }
    return expandChildListBindings(surfaceId, onlyParents = null, pendingParentTags = pendingTags)
}

    /**
     * Whether [componentId] belongs to a Modal content subtree whose overlay is not
     * currently open. Such components have a protocol tag but no view yet.
     */
internal fun A2UIComponentProjection.templateInputSnapshot(templateId: String): String {
    val definitions = linkedMapOf<String, Map<String, Any?>>()
    val rootPaths = linkedSetOf<String>()
    fun visit(id: String) {
        if (id in definitions) return
        val definition = idToOriginalDef[id] ?: return
        definitions[id] = definition
        rootPaths.addAll(extractDataPaths(definition).filter { it.startsWith("/") })
        getChildrenIds(definition).forEach(::visit)
        getInlineChildListBinding(definition)?.let { (path, child) ->
            if (path.startsWith("/")) rootPaths.add(path)
            visit(child)
        }
    }
    visit(templateId)
    return A2UIJsonValue.buildJsonString(listOf(definitions, rootPaths.associateWith { model.read(it.removePrefix("/")) }))
}

internal fun A2UIComponentProjection.isInClosedModalContent(componentId: String): Boolean {
        if (componentId !in modalDeferredIds) return false
        val tag = idToTag[componentId] ?: return true
        return modalOverlayTags.values.none { tags -> tags.any { it.first == tag } }
    }

    /**
     * @param onlyParents when non-null, only bindings whose parent id is in this set are
     *   expanded (used by [openModal] to materialize lists inside the new overlay).
     */
internal fun A2UIComponentProjection.expandChildListBindings(
    surfaceId: String,
    onlyParents: Set<String>?,
    pendingParentTags: Set<Int> = emptySet(),
): List<A2UIMessage> {
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
            if (_protocolVersion == A2UIProtocolVersion.V1_0) {
                if (!materialization.contains(parentTag) && parentTag !in pendingParentTags) continue
                if (!hasCompleteTemplate(binding.templateId)) continue
            }
            val templateDef = idToOriginalDef[binding.templateId]
            if (templateDef == null) {
                continue
            }
            val templateComponentType = templateDef["component"] as? String ?: continue
            val inputSnapshot = if (_protocolVersion == A2UIProtocolVersion.V1_0) templateInputSnapshot(binding.templateId) else null
            val templateUnchanged = inputSnapshot == binding.templateInputSnapshot

            // ------------------------------------------------------------------
            // Diff-based update (see KDoc on `ChildListBinding.lastItemSnapshots`).
            //
            // Before this change, every call to expandChildListBindings for the
            // same parent REMOVE'd all existing instances and INSTANTIATE'd the
            // full new array. During streaming updates (e.g. the /reviews array
            // grows one item at a time) this caused every existing review row
            // to be destroyed and re-created on every tick, producing a highly
            // visible flicker where unchanged review bodies toggled between
            // single-line (just attached, still empty) and wrapped multi-line
            // (after text props arrive and width is applied) states 5-10 times
            // over the course of a streaming session.
            //
            // The new logic compares each array index with its snapshot from
            // the previous expansion:
            //   - equal        : leave the subtree untouched (no REMOVE, no
            //                    INSTANTIATE, no UPDATE_COMPONENT). This is the
            //                    fast path and the main source of flicker
            //                    elimination for append-only streaming.
            //   - changed      : REMOVE the index's existing subtree, then
            //                    INSTANTIATE with the new data at the same
            //                    sibling position.
            //   - new (i >= oldCount): INSTANTIATE at the next sibling index.
            //   - removed (i in oldCount until newCount's tail): REMOVE.
            //
            // The expanded template may itself contain nested ChildLists,
            // which are handled recursively by instantiateSubtree; we rely on
            // `binding.generatedInstanceIds` as the authoritative set of ids
            // to clean up for a given top-level index (captured via a size
            // delta when instantiateSubtree is called below).
            // ------------------------------------------------------------------

            val oldInstancesByIndex = binding.lastInstancesByIndex.toMap()
            val oldSnapshots = binding.lastItemSnapshots.toMap()
            val oldCount = oldSnapshots.size

            val newInstancesByIndex = mutableMapOf<Int, MutableList<String>>()
            val newSnapshots = mutableMapOf<Int, Any?>()
            // Rebuild `generatedInstanceIds` as we decide index-by-index which
            // old entries are preserved and which new entries are produced.
            binding.generatedInstanceIds.clear()

            for ((index, item) in arrayData.withIndex()) {
                if (item !is Map<*, *>) continue
                @Suppress("UNCHECKED_CAST")
                val itemData = item as Map<String, Any?>
                val oldItem = oldSnapshots[index]
                val hadOld = oldInstancesByIndex.containsKey(index)

                val oldRootTag = idToTag["${binding.templateId}__$index"]
                val reusable = _protocolVersion != A2UIProtocolVersion.V1_0 ||
                    (templateUnchanged && oldRootTag != null &&
                        (materialization.contains(oldRootTag) || oldRootTag in pendingParentTags))
                if (hadOld && oldItem == itemData && reusable) {
                    // Unchanged: preserve the entire subtree for this index.
                    val preservedIds = oldInstancesByIndex[index] ?: mutableListOf()
                    newInstancesByIndex[index] = preservedIds.toMutableList()
                    newSnapshots[index] = itemData
                    binding.generatedInstanceIds.addAll(preservedIds)
                    continue
                }

                // Changed or added: if there was an old subtree at this index,
                // tear it down first to make room for the new one at the same
                // sibling position.
                if (hadOld) {
                    val oldIds = oldInstancesByIndex[index] ?: emptyList()
                    for (oldInstanceId in oldIds) {
                        val oldInstanceTag = idToTag[oldInstanceId] ?: continue
                        // Only REMOVE the top-level template root; nested
                        // children are torn down via removeInstanceTree so we
                        // don't double-REMOVE already-detached children.
                        if (oldInstanceId == "${binding.templateId}__$index") {
                            if (_protocolVersion == A2UIProtocolVersion.V1_0 && !materialization.contains(oldInstanceTag)) {
                                removeInstanceTree(oldInstanceId, surfaceId, parentTag, mutableListOf())
                                break
                            }
                            messages.add(
                                A2UIMessage(
                                    type = A2UIMessageType.REMOVE_COMPONENT,
                                    surfaceId = surfaceId,
                                    tag = oldInstanceTag,
                                    parentTag = parentTag
                                )
                            )
                            removeInstanceTree(oldInstanceId, surfaceId, parentTag, messages)
                            break
                        }
                    }
                }

                val scopePath = "/${dataKey}/$index"
                val sizeBefore = binding.generatedInstanceIds.size
                // v1.0 `@index` support: publish the current data-array
                // index to [templateIndexStack] for the duration of this
                // item's instantiation so FunctionCalls evaluated inside
                // the template subtree see it via [currentTemplateIndex].
                // try/finally guarantees the stack stays balanced even if
                // instantiation throws.
                templateIndexStack.addLast(index)
                try {
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
                } finally {
                    templateIndexStack.removeLast()
                }
                val produced = binding.generatedInstanceIds.subList(
                    sizeBefore,
                    binding.generatedInstanceIds.size
                ).toMutableList()
                newInstancesByIndex[index] = produced
                newSnapshots[index] = itemData
            }

            // Tail removal: indices that existed previously but are not in the
            // new array (array shrank).
            val newCount = arrayData.size
            if (oldCount > newCount) {
                for (removedIndex in newCount until oldCount) {
                    val removedIds = oldInstancesByIndex[removedIndex] ?: continue
                    for (removedInstanceId in removedIds) {
                        if (removedInstanceId == "${binding.templateId}__$removedIndex") {
                            val removedTag = idToTag[removedInstanceId] ?: continue
                            messages.add(
                                A2UIMessage(
                                    type = A2UIMessageType.REMOVE_COMPONENT,
                                    surfaceId = surfaceId,
                                    tag = removedTag,
                                    parentTag = parentTag
                                )
                            )
                            removeInstanceTree(removedInstanceId, surfaceId, parentTag, messages)
                            break
                        }
                    }
                }
            }

            // Commit the new maps atomically so a subsequent expansion in the
            // same tick sees a consistent view.
            binding.lastInstancesByIndex.clear()
            binding.lastInstancesByIndex.putAll(newInstancesByIndex)
            binding.lastItemSnapshots.clear()
            binding.lastItemSnapshots.putAll(newSnapshots)
            binding.templateInputSnapshot = inputSnapshot
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
internal fun A2UIComponentProjection.getInlineChildListBinding(compDef: Map<String, Any?>): Pair<String, String>? {
        val children = compDef["children"] as? Map<*, *> ?: return null
        // NOTE(v1.0): ChildList template objects keep bare `path` and
        //   `componentId` keys in v1.0 (see handleUpdateComponents for the
        //   reference to evolution_guide.md). Do not route through readPathKey.
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
internal fun A2UIComponentProjection.resolveScopePathValue(path: String, itemData: Map<String, Any?>): Any? {
        return if (path.startsWith("/")) {
            val key = path.removePrefix("/")
            val absolute = resolveNestedDataPath(key)
            if (_protocolVersion == A2UIProtocolVersion.V1_0 && strictV10) absolute
            else absolute ?: if (itemData.isNotEmpty()) resolveRelativePath(key, itemData) else null
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
internal fun A2UIComponentProjection.instantiateSubtree(
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
        val propertyPlan = planProperties(componentType, compDef, instanceId = instanceId, item = itemData, scopePath = scopePath)
        val props = commitProperties(propertyPlan)

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
                    // Nested ChildList: push the inner index on top of
                    // the outer one. `@index` deepest-scope semantics
                    // means the inner template sees `innerIdx`; when we
                    // return control to the outer template, the pop
                    // restores the outer index.
                    templateIndexStack.addLast(innerIdx)
                    try {
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
                    } finally {
                        templateIndexStack.removeLast()
                    }
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

internal fun A2UIComponentProjection.getOriginalComponentId(instanceId: String): String {
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
internal fun A2UIComponentProjection.getOriginalParentId(instanceId: String): String? {
        // First try direct lookup in childToParentCache
        val directParent = childToParentCache[instanceId]
        if (directParent != null) return directParent

        // For template instances, look up the original parent relationship.
        // Fall back to the persistent originalChildToParent map because the batch-
        // scoped cache only contains relationships for the current updateComponents
        // batch and may have evicted the template parent link already.
        val originalId = getOriginalComponentId(instanceId)
        return childToParentCache[originalId] ?: originalChildToParent[originalId]
    }

    /**
     * Get the parent component type for an instance (works for both regular and template instances).
     */
internal fun A2UIComponentProjection.getParentComponentType(instanceId: String): String? {
        // First try direct lookup in the batch-scoped cache (covers live components
        // present in the latest updateComponents batch).
        val parentId = childToParentCache[instanceId]
        if (parentId != null) {
            return idToTag[parentId]?.let { tagToComponentName[it] }
        }

        // For template instances, strip the suffix to find the original id and
        // look up the original parent. Prefer the batch cache, but fall back to
        // the persistent originalChildToParent map because template components
        // are declared once and later batches evict them from the batch cache.
        val originalId = getOriginalComponentId(instanceId)
        val originalParentId = childToParentCache[originalId] ?: originalChildToParent[originalId]
        if (originalParentId != null) {
            val parentDef = idToOriginalDef[originalParentId]
            return parentDef?.get("component") as? String
        }
        return null
    }

    /**
     * Check if the parent of an instance is a Row component.
     */
internal fun A2UIComponentProjection.isParentRow(instanceId: String): Boolean {
        return getParentComponentType(instanceId) == "Row"
    }

    /**
     * Remove all generated instances in a template instance tree.
     */
internal fun A2UIComponentProjection.removeInstanceTree(instanceId: String, surfaceId: String, parentTag: Int, messages: MutableList<A2UIMessage>) {
        // Clean up scope path and tag mappings
        instanceScopePath.remove(instanceId)
        idToTag.remove(instanceId)
    }

    /**
     * Recursively collect a template component ID and all its descendant IDs.
     * These components should not be created as regular components — they are only
     * instantiated via ChildList expansion.
     */
internal fun A2UIComponentProjection.collectTemplateTree(
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
internal fun A2UIComponentProjection.getParentButtonVariant(parentId: String): String {
        return componentVariantCache[parentId] ?: "default"
    }
