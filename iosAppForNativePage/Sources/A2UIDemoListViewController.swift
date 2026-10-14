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

import UIKit

/// Main list view controller for **iosAppForNativePage**.
///
/// Mirrors `androidAppForNativePage/A2UIDemoActivity` — every entry below hosts
/// A2UI through the classic *Native View* integration (see `A2UISurfaceView`
/// in a2ui-render-ios), i.e. A2UI runs inside its own private
/// `IKuiklyRenderExport` and does NOT sit inside a hosting Kuikly page.
///
/// See sibling apps for the other integration paths:
///   * `iosAppForKuiklyPage` — DSL/Compose embedding inside a Kuikly page.
///   * `iosAppForBothPages`  — side-by-side comparison (Kuikly-Page vs Native-Page cards).
///   * `iosAppForTest`       — the full protocol / theme / databinding sandbox.
class A2UIDemoListViewController: A2UIDemoBaseListViewController {

    override var listTitle: String { "A2UI - Native Page" }
    override var headerSubtitle: String { "A2UI 以独立 UIView 形式嵌入原生页面" }

    override var sections: [A2UIDemoSection] {
        [
            // Group 1 — Native 页面接入示例
            // Order & wording mirrors androidAppForNativePage/A2UIDemoActivity.kt so
            // both platforms present the same demo taxonomy to reviewers.
            A2UIDemoSection(title: "Native 页面接入示例", entries: [
                A2UIDemoEntry(
                    title: "卡片场景接入示例",
                    description: "在原生 UIViewController 中直接嵌入 A2UISurfaceView，通过 writeStream() 追加 A2UI 卡片",
                    icon: "🧬",
                    color: UIColor(red: 0.27, green: 0.35, blue: 0.39, alpha: 1.0), // #455A64
                    build: { NativeCardDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义函数示例",
                    description: "注册 string/boolean/number/void 返回类型的自定义函数，验证表达式求值、check 条件、action 触发",
                    icon: "⚡",
                    color: UIColor(red: 0.0, green: 0.54, blue: 0.48, alpha: 1.0),  // #00897B
                    build: { CustomFunctionDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义组件示例",
                    description: "注册 A2UIWeb (WKWebView) 和 MyA2UIView (简单视图) 自定义组件，覆盖 setProp/Shadow 完整链路",
                    icon: "🧩",
                    color: UIColor(red: 0.38, green: 0.0, blue: 0.93, alpha: 1.0),  // #6200EE
                    build: { CustomComponentDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义 Icon 图片",
                    description: "通过 A2UIIconRegistry 注册业务侧 Icon 图片，替换协议内建的 emoji / unicode 字形",
                    icon: "🖼️",
                    color: UIColor(red: 0.85, green: 0.11, blue: 0.38, alpha: 1.0), // #D81B60
                    build: { CustomIconDemoViewController() }
                )
            ]),
            // Group 2 — 组件展示
            A2UIDemoSection(title: "组件展示", entries: [
                A2UIDemoEntry(
                    title: "组件展示",
                    description: "展示 A2UI v0.9 协议语义化组件：Text、Row、Column、Card 等 18 个组件",
                    icon: "🔧",
                    color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0), // #F39C12
                    build: { ComponentDemoViewController() }
                )
            ])
        ]
    }
}
