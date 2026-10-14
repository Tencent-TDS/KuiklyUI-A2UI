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

/// Main list view controller for **iosAppForKuiklyPage**.
///
/// Mirrors `androidAppForKuiklyPage/A2UIDemoActivity` — every entry below
/// demonstrates hosting A2UI inside a Kuikly-Page (either a Kuikly DSL
/// page via `A2UISurface { ... }` or a Kuikly Compose page).
///
/// Phase-2 wiring is live: `A2UISurfaceDSLDemo` and `ComposePageDemo` now
/// really host their respective Kuikly pages (backed by the `A2UIDemo`
/// framework produced by `:demo`'s cocoapods block) via
/// `KuiklyRenderViewControllerBaseDelegator`, and go through
/// `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting()` so any
/// custom render view registered via `A2UISurfaceView.registerCustomRenderView`
/// reaches the KMP-side registry consumed by the A2UI DSL applier.
class A2UIDemoListViewController: A2UIDemoBaseListViewController {

    override var listTitle: String { "A2UI - KuiklyPage" }
    override var headerSubtitle: String { "A2UI 嵌入 Kuikly DSL / Compose 页面" }

    override var sections: [A2UIDemoSection] {
        [
            A2UIDemoSection(title: "Kuikly 页面接入示例", entries: [
                A2UIDemoEntry(
                    title: "卡片场景接入示例",
                    description: "在 Kuikly DSL 页面里嵌入 A2UISurface：单 Pager、单 FlexNode 树，点击按钮追加 A2UI 卡片",
                    icon: "🃏",
                    color: UIColor(red: 1.0, green: 0.42, blue: 0.42, alpha: 1.0),
                    build: { A2UISurfaceDSLDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义函数示例",
                    description: "注册 string/boolean/number/void 返回类型的自定义函数，验证表达式求值、check 条件、action 触发",
                    icon: "⚡",
                    color: UIColor(red: 0.0, green: 0.54, blue: 0.48, alpha: 1.0),
                    // KuiklyPage variant: pushes A2UIComponentTestKuiklyPageViewController
                    // which hosts @Page("A2UICaseList") — aligned with
                    // androidAppForKuiklyPage/CustomFunctionDemoActivity.
                    build: { CustomFunctionKuiklyDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义组件示例",
                    description: "注册 A2UIWeb (WebView) 和 MyA2UIView (简单视图) 自定义组件，覆盖 setProp/Shadow 完整链路",
                    icon: "🧩",
                    color: UIColor(red: 0.38, green: 0.0, blue: 0.93, alpha: 1.0),
                    // KuiklyPage variant — see CustomFunctionKuiklyDemoViewController
                    // comment for the rationale.
                    build: { CustomComponentKuiklyDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "自定义 Icon 图片",
                    description: "通过 A2UIIconRegistry 注册业务侧 Icon 图片，替换协议内建的 emoji / unicode 字形",
                    icon: "🎯",
                    color: UIColor(red: 0.85, green: 0.11, blue: 0.38, alpha: 1.0),
                    // KuiklyPage variant: registers icons + hosts A2UICaseList,
                    // mirrors androidAppForKuiklyPage/CustomIconDemoActivity.
                    build: { CustomIconKuiklyDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "Compose 页面接入示例",
                    description: "在 Kuikly Compose 页面（:compose 模块）里承载 A2UI，展示 Compose 世界的 A2UI 集成",
                    icon: "🎨",
                    color: UIColor(red: 0.34, green: 0.63, blue: 0.32, alpha: 1.0),
                    build: { ComposePageDemoViewController() }
                )
            ]),
            A2UIDemoSection(title: "组件展示", entries: [
                A2UIDemoEntry(
                    title: "组件展示",
                    description: "展示 A2UI v0.9 协议语义化组件：Text、Row、Column、Card 等 18 个组件",
                    icon: "🔧",
                    color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0),
                    build: { ComponentDemoViewController() }
                )
            ])
        ]
    }
}
