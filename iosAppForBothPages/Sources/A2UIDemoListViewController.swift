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

/// Main list view controller for **iosAppForBothPages**.
///
/// Mirrors `androidAppForBothPages/A2UIDemoActivity` — two entries side by
/// side that render the SAME A2UI v0.9 weather-card DSL through two
/// different integration paths, so users can eyeball that both produce
/// identical output.
class A2UIDemoListViewController: A2UIDemoBaseListViewController {

    override var listTitle: String { "A2UI - Both Pages" }
    override var headerSubtitle: String { "" }

    override var sections: [A2UIDemoSection] {
        [
            A2UIDemoSection(title: "A2UI 集成路径对比", entries: [
                A2UIDemoEntry(
                    title: "Kuikly 页面接入示例",
                    description: "Kuikly Page（KuiklyBaseView + @Page）承载一张 A2UI v0.9 天气卡片",
                    icon: "🌐",
                    color: UIColor(red: 0.13, green: 0.59, blue: 0.95, alpha: 1.0),
                    build: { KuiklyPageCardViewController() }
                ),
                A2UIDemoEntry(
                    title: "Native 页面接入示例",
                    description: "Native Activity + A2UISurfaceView 直接承载一张 A2UI v0.9 天气卡片",
                    icon: "📱",
                    color: UIColor(red: 0.27, green: 0.35, blue: 0.39, alpha: 1.0),
                    build: { NativePageCardViewController() }
                )
            ])
        ]
    }
}
