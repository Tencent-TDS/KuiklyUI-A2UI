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

/// Theme Demo View Controller.
/// Displays the A2UI v0.9 protocol theme capabilities defined in `createSurface.theme`,
/// grouped by category. Selecting a capability pushes
/// `A2UIComponentTestViewController` bound to
/// `Resources/a2ui_theme_tests/<capabilityDir>/`.
class ThemeDemoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private static let rootDir = "a2ui_theme_tests"
    private static let titleSuffix = "主题"

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct ThemeInfo {
        let dir: String
        let name: String
        let description: String
        let icon: String
        let color: UIColor
    }

    private struct ThemeSection {
        let title: String
        let entries: [ThemeInfo]
    }

    private let sections: [ThemeSection] = [
        ThemeSection(title: "主题色渲染（primaryColor）", entries: [
            ThemeInfo(
                dir: "01_primary_color_basic",
                name: "primaryColor 一次性染色多组件",
                description: "Button primary / Slider progressColor / Tabs 选中态 / CheckBox / ChoicePicker 等均应统一为主题色",
                icon: "🎨",
                color: UIColor(red: 0.91, green: 0.12, blue: 0.39, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "身份归因（Identity & Attribution）", entries: [
            ThemeInfo(
                dir: "02_agent_identity",
                name: "iconUrl + agentDisplayName",
                description: "theme.iconUrl (URI) 与 agentDisplayName (String) 用于向用户展示 agent 身份",
                icon: "🪪",
                color: UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "完整主题（All Fields）", entries: [
            ThemeInfo(
                dir: "03_theme_all_fields",
                name: "三字段全填 + 自定义扩展",
                description: "同时携带 primaryColor / iconUrl / agentDisplayName，并附加规范未定义的自定义键（additionalProperties=true 允许扩展）",
                icon: "🧩",
                color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "非法值 fallback（Invalid Fallback）", entries: [
            ThemeInfo(
                dir: "04_invalid_color_fallback",
                name: "primaryColor 非法值应回退默认",
                description: "非 hex / 3 位 / 8 位 / 缺 # / 非 hex 字符 / 空串 均应回退 #00BFFF，大写 6 位 hex 作为正向对照",
                icon: "🛑",
                color: UIColor(red: 0.91, green: 0.30, blue: 0.24, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "默认主题（Default）", entries: [
            ThemeInfo(
                dir: "05_default_no_theme",
                name: "createSurface 不带 theme",
                description: "theme 属可选。未携带时，primaryColor 走协议默认值 #00BFFF（浅蓝）",
                icon: "🔵",
                color: UIColor(red: 0.0, green: 0.75, blue: 1.0, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "极端色（Extreme Colors）", entries: [
            ThemeInfo(
                dir: "06_extreme_colors",
                name: "primaryColor 极端色可读性",
                description: "纯黑 / 纯白 primaryColor 目视验证，暴露主题色对比度不当时的观感（不代表实现缺陷）",
                icon: "⚫️",
                color: UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "Button variant 隔离（Regression）", entries: [
            ThemeInfo(
                dir: "07_button_variant_isolation",
                name: "仅 primary variant 应被染色",
                description: "同一主题下 primary / default / borderless 并列，验证仅 primary 背景应用主题色，其他 variant 不被误染",
                icon: "🧪",
                color: UIColor(red: 0.91, green: 0.12, blue: 0.39, alpha: 1.0)
            )
        ]),
        ThemeSection(title: "Agent 身份边界（Identity Edge）", entries: [
            ThemeInfo(
                dir: "08_agent_identity_edge",
                name: "超长名称 / emoji / data URI",
                description: "agentDisplayName 超长中英混排 + emoji，iconUrl 使用 data:image/png;base64,... 内联图片",
                icon: "🧾",
                color: UIColor(red: 0.0, green: 0.59, blue: 0.53, alpha: 1.0)
            )
        ])
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "主题测试"
        view.backgroundColor = .systemGroupedBackground
        setupTableView()
    }

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(ComponentCell.self, forCellReuseIdentifier: "ThemeCell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        // Header
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 44))
        let headerLabel = UILabel()
        headerLabel.text = "A2UI v0.9 协议 createSurface.theme 主题字段能力"
        headerLabel.font = .systemFont(ofSize: 14)
        headerLabel.textColor = .secondaryLabel
        headerLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(headerLabel)
        NSLayoutConstraint.activate([
            headerLabel.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 20),
            headerLabel.bottomAnchor.constraint(equalTo: headerView.bottomAnchor, constant: -8)
        ])
        tableView.tableHeaderView = headerView
    }

    // MARK: - UITableViewDataSource

    func numberOfSections(in tableView: UITableView) -> Int { sections.count }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sections[section].entries.count
    }

    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sections[section].title
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ThemeCell", for: indexPath) as! ComponentCell
        let entry = sections[indexPath.section].entries[indexPath.row]
        cell.configure(icon: entry.icon, name: entry.name, description: entry.description, color: entry.color)
        return cell
    }

    // MARK: - UITableViewDelegate

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat { 78 }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let entry = sections[indexPath.section].entries[indexPath.row]
        if hasTestCases(for: entry.dir) {
            let vc = A2UIComponentTestViewController(
                componentDir: entry.dir,
                componentName: entry.name,
                rootDir: Self.rootDir,
                titleSuffix: Self.titleSuffix
            )
            navigationController?.pushViewController(vc, animated: true)
        } else {
            showToast(message: "\(entry.name) 暂未提供测试用例")
        }
    }

    // MARK: - Helpers

    private func hasTestCases(for capabilityDir: String) -> Bool {
        guard let resourcePath = Bundle.main.resourcePath else { return false }
        let dirPath = (resourcePath as NSString)
            .appendingPathComponent(Self.rootDir)
            .appending("/\(capabilityDir)")
        var isDir: ObjCBool = false
        let fm = FileManager.default
        guard fm.fileExists(atPath: dirPath, isDirectory: &isDir), isDir.boolValue else { return false }
        guard let files = try? fm.contentsOfDirectory(atPath: dirPath) else { return false }
        return files.contains { $0.hasSuffix(".json") }
    }

    private func showToast(message: String) {
        let toast = UILabel()
        toast.text = message
        toast.textColor = .white
        toast.backgroundColor = UIColor.black.withAlphaComponent(0.75)
        toast.font = .systemFont(ofSize: 13)
        toast.textAlignment = .center
        toast.numberOfLines = 0
        toast.layer.cornerRadius = 8
        toast.clipsToBounds = true
        toast.alpha = 0
        toast.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(toast)
        NSLayoutConstraint.activate([
            toast.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            toast.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -40),
            toast.widthAnchor.constraint(lessThanOrEqualTo: view.widthAnchor, constant: -40),
            toast.heightAnchor.constraint(greaterThanOrEqualToConstant: 36)
        ])
        UIView.animate(withDuration: 0.2, animations: { toast.alpha = 1 }) { _ in
            UIView.animate(withDuration: 0.25, delay: 1.4, options: [], animations: {
                toast.alpha = 0
            }, completion: { _ in toast.removeFromSuperview() })
        }
    }
}
