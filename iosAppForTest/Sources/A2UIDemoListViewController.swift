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

/// Main list view controller for **iosAppForTest**.
///
/// Mirrors `androidAppForTest/A2UIDemoActivity` — the A2UI protocol
/// sandbox (databinding / theme / function tests + official v0.9 examples).
/// All entries route to concrete VCs already migrated to
/// `iosAppForTest/Sources`.
class A2UIDemoListViewController: A2UIDemoBaseListViewController {

    override var listTitle: String { "A2UI Test" }
    override var headerSubtitle: String { "A2UI 协议 / 主题 / 数据绑定 / 函数 / 官方示例 全量 sandbox" }

    override var sections: [A2UIDemoSection] {
        [
            A2UIDemoSection(title: "A2UI 协议测试", entries: [
                A2UIDemoEntry(
                    title: "函数测试",
                    description: "展示 A2UI v0.9 basic catalog 内建 14 个函数：required / regex / formatDate / pluralize / and / or 等",
                    icon: "λ",
                    color: UIColor(red: 0.30, green: 0.68, blue: 0.31, alpha: 1.0),
                    build: { FunctionDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "数据绑定测试",
                    description: "展示 A2UI v0.9 数据绑定 11 项能力：绝对/相对路径、ChildList 模板、双向绑定、formatString 插值、Action 上下文等",
                    icon: "🔗",
                    color: UIColor(red: 0.13, green: 0.59, blue: 0.95, alpha: 1.0),
                    build: { DataBindingDemoViewController() }
                ),
                A2UIDemoEntry(
                    title: "主题测试",
                    description: "展示 A2UI v0.9 主题字段能力：createSurface.theme.primaryColor / iconUrl / agentDisplayName、非法值 fallback、默认主题等",
                    icon: "🎨",
                    color: UIColor(red: 0.61, green: 0.15, blue: 0.69, alpha: 1.0),
                    build: { ThemeDemoViewController() }
                )
            ]),
            A2UIDemoSection(title: "A2UI 协议官方示例", entries: [
                A2UIDemoEntry(
                    title: "A2UI v0.9 协议官方示例",
                    description: "展示 A2UI v0.9 协议规范中的 36 个官方示例卡片（启动一次性加载了36个json文件渲染UI卡片，待优化）",
                    icon: "📖",
                    color: UIColor(red: 1.0, green: 0.60, blue: 0.0, alpha: 1.0),
                    build: { OfficialExampleViewController() }
                )
            ])
        ]
    }
}
