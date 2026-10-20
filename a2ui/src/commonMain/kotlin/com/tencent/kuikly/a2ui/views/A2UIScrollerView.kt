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

import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexLayout
import com.tencent.kuikly.core.layout.undefined
import com.tencent.kuikly.core.pager.IPagerLayoutEventObserver
import com.tencent.kuikly.core.views.ScrollerAttr
import com.tencent.kuikly.core.views.ScrollerEvent
import com.tencent.kuikly.core.views.ScrollerView

/**
 * A2UIScrollerView - A ScrollerView subclass tailored for A2UI's content-driven layout.
 *
 * Problem: ScrollerView's contentView uses absolutePosition which means it does NOT
 * contribute to ScrollerView's intrinsic size calculation. In A2UI's content-driven
 * layout (where Card height is determined by children), this causes ScrollerView's
 * height to collapse to 0, making all content invisible.
 *
 * Solution: After contentView completes layout, backfill ScrollerView's cross-axis
 * dimension with contentView's actual size. This allows:
 * - Horizontal List: ScrollerView height = contentView height (determined by tallest child)
 * - Vertical List: ScrollerView height = contentView height (all items visible; scrolling
 *   kicks in only when an ancestor constrains the ScrollerView's height)
 *
 * The main-axis dimension is left unconstrained so contentView can grow beyond
 * ScrollerView's bounds, enabling native scrolling via KRScrollView's contentSize sync.
 */
internal open class A2UIScrollerView : ScrollerView<ScrollerAttr, ScrollerEvent>(), IPagerLayoutEventObserver {

    private var didInitContentViewConstraint = false

    override fun onPagerWillCalculateLayoutFinish() {}

    /**
     * Called after every full layout pass of the host Pager.
     *
     * Why we do everything here instead of using `layoutFrameDidChangedCallback`:
     * In DSL embed mode the timing of `ScrollerContentView.createFlexNode()` is
     * AFTER our `addChild` returns, so any callback we register on
     * `contentView.flexNode.layoutFrameDidChangedCallback` gets overwritten by the
     * default callback later. Using the pager-level observer avoids this race
     * entirely - it fires once per completed calculateLayout() and always sees the
     * up-to-date `contentView.flexNode.layoutFrame`.
     */
    override fun onPagerCalculateLayoutFinish() {
        // 1) First-time init: remove the cross-axis absolute constraint on contentView
        //    so that contentView.flexNode can grow to fit its children (content-driven).
        ensureContentViewConstraintInited()

        // 2) Backfill ScrollerView's cross-axis dimension from contentView's actual size.
        //    If styleHeight changes, this markDirty()s our node and the while-loop in
        //    Pager.layoutIfNeed() will run another pass before the observer fires again.
        backfillCrossAxisSize()
    }

    override fun onPagerDidLayout() {}

    override fun didInit() {
        super.didInit()
        getPager().addPagerLayoutEventObserver(this)
    }

    override fun willRemoveFromParentView() {
        getPager().removePagerLayoutEventObserver(this)
        super.willRemoveFromParentView()
    }

    /**
     * Remove contentView's cross-axis absolute-position pin exactly once.
     * initScrollerContentComponentIfNeed() sets:
     *   - Horizontal: absolutePosition(top=0, left=0, bottom=0) → bottom pins height to parent
     *   - Vertical:   absolutePosition(top=0, left=0, right=0)  → right pins width to parent
     * Since ScrollerView has no intrinsic size in content-driven layouts, this causes
     * contentView's cross-axis to collapse to 0. By removing the cross-axis pin,
     * contentView's cross-axis dimension is determined by its children.
     */
    private fun ensureContentViewConstraintInited() {
        if (didInitContentViewConstraint) return
        val cv = contentView ?: return
        didInitContentViewConstraint = true

        val isHorizontal = flexNode.flexDirection == FlexDirection.ROW
                || flexNode.flexDirection == FlexDirection.ROW_REVERSE
        if (isHorizontal) {
            cv.flexNode.setStylePosition(FlexLayout.PositionType.POSITION_BOTTOM, Float.undefined)
        } else {
            cv.flexNode.setStylePosition(FlexLayout.PositionType.POSITION_RIGHT, Float.undefined)
        }
    }

    /**
     * After contentView layout completes, set ScrollerView's cross-axis dimension
     * to match contentView's actual size. This makes the ScrollerView participate
     * correctly in the parent's layout calculation.
     *
     * For horizontal scroll (flexDirection=ROW):
     *   - Cross axis = height → set ScrollerView.styleHeight = contentView.layoutHeight
     *   - Main axis = width → left unconstrained (contentView grows freely)
     *
     * For vertical scroll (flexDirection=COLUMN):
     *   - Cross axis = width → contentView width is content-driven; we backfill
     *     contentView's styleWidth to match ScrollerView.layoutFrame.width so that
     *     align=center/end work against the full List width.
     *   - Main axis = height → set ScrollerView.styleHeight = contentView.layoutHeight
     *     (capped at 2 * listWidth to keep it a reasonable viewport size)
     */
    private fun backfillCrossAxisSize() {
        val cv = contentView ?: return
        val contentFrame = cv.flexNode.layoutFrame
        val contentHeight = contentFrame.height
        if (contentHeight <= 0f) return

        val isHorizontal = flexNode.flexDirection == FlexDirection.ROW
                || flexNode.flexDirection == FlexDirection.ROW_REVERSE

        var targetHeight = contentHeight

        if (!isHorizontal) {
            val listWidth = flexNode.layoutFrame.width
            if (listWidth > 0f) {
                val maxHeight = listWidth * 2f
                if (targetHeight > maxHeight) {
                    targetHeight = maxHeight
                }
                // Backfill contentView.styleWidth so alignment computes against the full List width.
                if (cv.flexNode.styleWidth != listWidth) {
                    cv.flexNode.styleWidth = listWidth
                }
            }
        }

        if (flexNode.styleHeight != targetHeight) {
            flexNode.styleHeight = targetHeight
        }
    }

    // Note: no override of addChild any more. Constraint init and backfill are both
    // done inside onPagerCalculateLayoutFinish, so we don't care about the ordering
    // between `initScrollerContentComponentIfNeed()` and `ScrollerContentView.createFlexNode()`.
}
