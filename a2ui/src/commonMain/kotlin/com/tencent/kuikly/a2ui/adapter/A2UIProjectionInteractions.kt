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

internal fun A2UIComponentProjection.buildChoiceChipProps(isSelected: Boolean): MutableMap<String, Any?> {
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
internal fun A2UIComponentProjection.buildChoiceChipTextProps(label: String, isSelected: Boolean): MutableMap<String, Any?> {
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
internal fun A2UIComponentProjection.buildCheckboxOptionRowProps(isSelected: Boolean): MutableMap<String, Any?> {
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
internal fun A2UIComponentProjection.getCheckboxIcon(isSelected: Boolean, variant: String): String {
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
internal fun A2UIComponentProjection.filterChoicePickerOptions(componentId: String, filterText: String): List<A2UIMessage> {
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
internal fun A2UIComponentProjection.renderChoiceSelection(
    componentId: String,
    newSelection: List<String>,
    bindingPath: String?,
    variant: String,
): List<A2UIMessage> {

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
        val affectedComponentIds = reactive.impact(
            A2UIDataChange(setOf(bindingPath), A2UIUpdateOrigin.CHOICE),
            excludedPathComponents = setOf(componentId),
        ).components

        for (compId in affectedComponentIds) {
            val compDef = idToOriginalDef[compId] ?: continue
            val compTag = idToTag[compId] ?: continue
            val compType = compDef["component"] as? String ?: continue
            val compViewName = A2UIComponentMapper.mapComponentName(compType)

            val updatedProps = mutableMapOf<String, Any?>()
            val skipKeys = bindingSkipKeys()
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
internal fun A2UIComponentProjection.getChoicePickerIdForChipTag(chipTag: Int): String? {
        for ((pickerId, tags) in choicePickerChipTags) {
            if (chipTag in tags) return pickerId
        }
        return null
    }

    /**
     * Get the option value for a given chip tag.
     */
internal fun A2UIComponentProjection.getChoiceOptionValueForChipTag(chipTag: Int): String? {
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
internal fun A2UIComponentProjection.getChoicePickerBindingPath(componentId: String): String? {
        val originalDef = idToOriginalDef[componentId] ?: return null
        val valueSpec = originalDef["value"]
        if (valueSpec is Map<*, *>) {
            return readPathKey(valueSpec)?.removePrefix("/")
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
internal fun A2UIComponentProjection.buildTabHeaderProps(): Map<String, Any?> {
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
internal fun A2UIComponentProjection.buildTabUnderlineProps(isSelected: Boolean): Map<String, Any?> {
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
internal fun A2UIComponentProjection.buildTabHeaderTextProps(title: String, isSelected: Boolean): Map<String, Any?> {
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
internal fun A2UIComponentProjection.renderTabSelection(tabsId: String, newIndex: Int): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()

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
internal fun A2UIComponentProjection.getTabsIdForHeaderTag(headerTag: Int): String? {
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
internal fun A2UIComponentProjection.renderModalOpen(modalId: String): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        val modalDef = modalDefinitions[modalId] ?: return messages

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
        for (message in expandChildListBindings(surfaceId, onlyParents = contentIds, pendingParentTags = contentTags)) {
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
internal fun A2UIComponentProjection.renderModalClose(modalId: String): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
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
internal fun A2UIComponentProjection.getModalIdForTriggerTag(triggerTag: Int): String? {
        for ((triggerId, modalId) in modalTriggerToId) {
            if (idToTag[triggerId] == triggerTag) return modalId
        }
        return null
    }

    /**
     * Recursively create CREATE_COMPONENT messages for a component subtree, reusing the
     * standard prop builders. Used to materialize a Modal's content when it opens.
     */
internal fun A2UIComponentProjection.buildModalContentSubtree(
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
        val propertyPlan = planProperties(componentType, def, kuikly = isKuiklyComponent)
        val props = commitProperties(propertyPlan)

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
            val hasChecks = (readChecksKey(def) != null || def["validationRegexp"] != null) &&
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
internal fun A2UIComponentProjection.getTabIndexForHeaderTag(headerTag: Int): Int {
        for ((_, tags) in tabsHeaderTags) {
            val index = tags.indexOf(headerTag)
            if (index >= 0) return index
        }
        return -1
    }
