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

/**
 * Maps Kuikly-native components (KR-prefixed) directly to the render layer.
 *
 * Unlike [A2UIComponentMapper] which translates A2UI v0.9 semantic components
 * (Text, Row, Column, etc.) into Kuikly internal names and properties,
 * this mapper handles components that are already expressed in Kuikly DSL format.
 *
 * Kuikly components use:
 * - Component names: KRView, KRRichTextView, KRImageView, KRScrollView, KRListView, etc.
 * - Property names: Directly aligned with Kuikly DSL (e.g., backgroundColor, borderRadius,
 *   flexDirection, alignItems, justifyContent, fontSize, color, text, src, etc.)
 * - Value types: Same as Kuikly DSL (strings for colors, numbers for dimensions, etc.)
 *
 * This separation ensures:
 * 1. Kuikly component logic is clearly separated from v0.9 protocol logic
 * 2. Kuikly properties are passed through without semantic translation
 * 3. Extended properties only take effect under Kuikly components
 */
object KuiklyComponentMapper {

    /**
     * Check if a component name is a Kuikly-native component (KR-prefixed).
     */
    fun isKuiklyComponent(componentName: String): Boolean {
        return componentName.startsWith("KR")
    }

    /**
     * For Kuikly components, the view name IS the component name (direct pass-through).
     */
    fun mapComponentName(componentName: String): String {
        return componentName
    }

    /**
     * Check if a Kuikly component needs shadow (text measurement).
     * Only KRRichTextView and KRGradientRichTextView need shadow.
     */
    fun needsShadow(componentName: String): Boolean {
        return componentName in SHADOW_COMPONENTS
    }

    /**
     * Map a property for a Kuikly component.
     * Kuikly properties are passed through directly - no translation needed.
     * Only skip structural keys that are handled by the adapter (id, component, children, child).
     *
     * @return Pair of (propKey, propValue) or null if the prop should be skipped
     */
    fun mapProp(componentName: String, propKey: String, propValue: Any?): Pair<String, Any?>? {
        if (propValue == null) return null

        // Skip structural keys handled by the adapter
        if (propKey in SKIP_KEYS) return null

        // All other properties pass through directly to the Kuikly render layer
        return propKey to propValue
    }

    /**
     * Build default props for a Kuikly component.
     * Unlike v0.9 components which need semantic defaults (Leaf-Margin Strategy, etc.),
     * Kuikly components have their properties explicitly specified in the JSON.
     * No implicit defaults are added.
     *
     * @return Empty map (no implicit defaults for Kuikly components)
     */
    fun buildDefaultProps(componentName: String): Map<String, Any?> {
        return emptyMap()
    }

    // ---- Private constants ----

    private val SHADOW_COMPONENTS = setOf(
        "KRRichTextView",
        "KRGradientRichTextView"
    )

    /**
     * Structural keys that are handled by the adapter layer, not passed as props.
     */
    private val SKIP_KEYS = setOf(
        "id",
        "component",
        "children",
        "child",
        "action"
    )

    /**
     * All supported Kuikly component names for reference.
     * These are the KR-prefixed view names registered in the Kuikly render layer.
     */
    val SUPPORTED_COMPONENTS = setOf(
        "KRView",
        "KRRichTextView",
        "KRGradientRichTextView",
        "KRImageView",
        "KRWrapperImageView",
        "KRListView",
        "KRScrollView",
        "KRScrollContentView",
        "KRTextFieldView",
        "KRTextAreaView",
        "KRCanvasView",
        "KRBlurView",
        "KRVideoView",
        "KRAudioView",
        "KRModalView",
        "KRHoverView",
        "KRActivityIndicatorView",
        "KRAPNGView",
        "KRMaskView",
        "KRPAGView"
    )
}
