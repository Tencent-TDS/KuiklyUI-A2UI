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
    override var listRowHeight: CGFloat { 72 }
    override var cellDescriptionLines: Int { 1 }

    override var sections: [A2UIDemoSection] {
        [
            A2UIDemoSection(title: "按协议版本运行", entries: [
                A2UIDemoEntry(
                    title: "A2UI 0.9",
                    description: "原样归档的组件、绑定、函数、主题和官方示例，包含预期非法样本",
                    icon: "0.9",
                    color: .systemGray,
                    build: { A2UIAssetBrowserViewController(directory: "v0_9") }
                ),
                A2UIDemoEntry(
                    title: "A2UI 1.0",
                    description: "独立的 1.0 严格模式样本目录，不混入旧版语料",
                    icon: "1.0",
                    color: .systemOrange,
                    build: { A2UIAssetBrowserViewController(directory: "v1_0") }
                )
            ])
        ]
    }
}
