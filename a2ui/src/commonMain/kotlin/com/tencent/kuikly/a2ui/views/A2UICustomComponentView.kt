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

import com.tencent.kuikly.core.base.Shadow
import com.tencent.kuikly.core.base.Size
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
 * 2. Implements [MeasureFunction] and creates a [Shadow] to delegate measurement to the native
 *    [IKuiklyRenderShadowExport] implementation. This allows the Flex engine to determine the
 *    component's intrinsic size (e.g., height=240 for WebView, height=200 for MyA2UIView).
 * 3. Forces [isRenderView] to return true so the native view is always created.
 *
 * @param customViewName The registered custom component name (e.g., "A2UIWeb", "MyA2UIView")
 */
class A2UICustomComponentView(private val customViewName: String) : DivView(), MeasureFunction {

    private var shadow: Shadow? = null

    override fun viewName(): String = customViewName

    override fun isRenderView(): Boolean = true

    override fun willInit() {
        super.willInit()
        // Create a Shadow object that communicates with the native IKuiklyRenderShadowExport.
        // The native layer will create the shadow via the registered shadowCreator for this viewName.
        shadow = Shadow(pagerId, nativeRef, customViewName)
    }

    override fun createFlexNode() {
        super.createFlexNode()
        // Register this view as the measure function for its FlexNode.
        // This tells the Flex engine to call measure() during layout calculation.
        flexNode.measureFunction = this
    }

    override fun createRenderView() {
        super.createRenderView()
        // After layout, sync the shadow to the native render view
        renderView?.setShadow()
    }

    override fun didRemoveFromParentView() {
        super.didRemoveFromParentView()
        flexNode.measureFunction = null
        shadow?.removeFromParentComponent()
        shadow = null
    }

    override fun measure(
        node: FlexNode,
        width: Float,
        height: Float,
        measureOutput: MeasureOutput
    ) {
        val cWidth = if (width.isUndefined()) 100000f else width
        val cHeight = if (height.isUndefined()) -1f else height

        // Delegate measurement to the native Shadow (IKuiklyRenderShadowExport.calculateRenderViewSize)
        val size = shadow?.calculateRenderViewSize(cWidth, cHeight) ?: Size(0f, 0f)

        var outWidth = size.width
        var outHeight = size.height

        // Respect explicit style dimensions if set
        if (!node.styleWidth.isUndefined()) {
            outWidth = node.styleWidth
        }
        if (!node.styleHeight.isUndefined()) {
            outHeight = node.styleHeight
        }

        measureOutput.width = outWidth
        measureOutput.height = outHeight
    }


}
