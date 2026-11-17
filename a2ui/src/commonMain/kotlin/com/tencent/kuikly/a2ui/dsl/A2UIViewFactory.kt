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

import com.tencent.kuikly.core.base.DeclarativeBaseView
import com.tencent.kuikly.core.views.CheckBoxView
import com.tencent.kuikly.core.views.DivView
import com.tencent.kuikly.core.views.ImageView
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.TextAreaView
import com.tencent.kuikly.core.views.ModalView
import com.tencent.kuikly.core.views.RichTextView
import com.tencent.kuikly.core.views.AudioView
import com.tencent.kuikly.core.views.BlurView
import com.tencent.kuikly.core.views.CanvasView
import com.tencent.kuikly.core.views.HoverView
import com.tencent.kuikly.core.views.PAGView
import com.tencent.kuikly.core.views.APNGVView
import com.tencent.kuikly.core.views.ActivityIndicatorView
import com.tencent.kuikly.core.views.VideoView
import com.tencent.kuikly.a2ui.views.A2UIScrollerView
import com.tencent.kuikly.a2ui.views.A2UICustomComponentView
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.a2ui.views.DateTimeInputView

internal object A2UIViewFactory {
    fun create(viewName: String): DeclarativeBaseView<*, *> {
        return when (viewName) {
            "KRView", "View" -> DivView()
            "KRRichTextView", "KRGradientRichTextView", "RichText", "Text" -> RichTextView()
            "KRImageView", "KRWrapperImageView", "Image" -> ImageView()
            "KRVideoView", "Video" -> VideoView()
            "KRAudioView", "AudioPlayer" -> AudioView()
            "KRTextFieldView", "TextField" -> InputView()
            "KRTextAreaView" -> TextAreaView()
            "KRSliderView", "Slider" -> A2UISliderView()
            "KRCheckBoxView", "CheckBox" -> CheckBoxView()
            "KRDateTimeInputView", "DateTimeInput" -> DateTimeInputView()
            "KRModalView", "Modal" -> ModalView().apply { inWindow = true }
            // A2UI List -> A2UIScrollerView (extends ScrollerView with cross-axis
            // size backfill to fix content-driven layout height collapse).
            "KRScrollView", "KRListView", "Scroller", "List" -> A2UIScrollerView()
            "KRBlurView" -> BlurView()
            "KRActivityIndicatorView" -> ActivityIndicatorView()
            "KRHoverView" -> HoverView()
            "KRPAGView" -> PAGView()
            "KRAPNGView" -> APNGVView()
            "KRCanvasView" -> CanvasView()
            // Custom component branch:
            //  - If the host app has registered `viewName` via
            //    `A2UISurfaceView.registerCustomRenderView(...)`, route it to
            //    A2UICustomComponentView so the native render layer creates the
            //    correct registered custom view and the flex engine can call the
            //    native Shadow to determine intrinsic size.
            //  - Otherwise (viewName is unknown / malformed protocol data), fall
            //    back to a plain DivView. This avoids the fatal native asserts
            //    that would otherwise fire on iOS/Android when the runtime cannot
            //    resolve a corresponding view class (`KRClassFromString(...)`
            //    returns nil / `createRenderShadow` throws).
            else -> if (A2UICustomViewRegistry.isRegistered(viewName)) {
                A2UICustomComponentView(viewName)
            } else {
                DivView()
            }
        }
    }
}
