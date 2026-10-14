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

/// Data Binding Demo View Controller.
/// Displays the data-binding capabilities defined by the A2UI v0.9 protocol,
/// grouped by category. Selecting a capability pushes
/// `A2UIComponentTestViewController` bound to
/// `Resources/a2ui_databinding_tests/<capabilityDir>/`.
class DataBindingDemoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private static let rootDir = "a2ui_databinding_tests"
    private static let titleSuffix = "数据绑定"

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct BindingInfo {
        let dir: String
        let name: String
        let description: String
        let icon: String
        let color: UIColor
    }

    private struct BindingSection {
        let title: String
        let entries: [BindingInfo]
    }

    private let sections: [BindingSection] = [
        BindingSection(title: "路径绑定（Path Binding）", entries: [
            BindingInfo(
                dir: "01_absolute_path",
                name: "绝对路径",
                description: "{path:\"/x\"} 绑定 DynamicString / Number / Boolean 等各种类型",
                icon: "🎯",
                color: UIColor(red: 0.04, green: 0.52, blue: 0.89, alpha: 1.0)
            ),
            BindingInfo(
                dir: "02_data_model_upsert",
                name: "Data Model 增量同步",
                description: "updateDataModel 的 upsert / 删除 / 整体替换语义",
                icon: "🔄",
                color: UIColor(red: 0.16, green: 0.50, blue: 0.73, alpha: 1.0)
            )
        ]),
        BindingSection(title: "列表模板（ChildList Template）", entries: [
            BindingInfo(
                dir: "03_child_list_template",
                name: "ChildList 模板遍历",
                description: "children:{componentId,path} 基于数组渲染动态列表",
                icon: "📋",
                color: UIColor(red: 0.56, green: 0.27, blue: 0.68, alpha: 1.0)
            ),
            BindingInfo(
                dir: "04_relative_path",
                name: "模板内相对路径",
                description: "子作用域内使用 \"fieldName\" 绑定当前迭代项",
                icon: "🧭",
                color: UIColor(red: 0.61, green: 0.35, blue: 0.71, alpha: 1.0)
            ),
            BindingInfo(
                dir: "05_mixed_scope",
                name: "混合作用域",
                description: "模板子树内绝对路径与相对路径共存",
                icon: "🌐",
                color: UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)
            )
        ]),
        BindingSection(title: "双向绑定（Two-Way Binding）", entries: [
            BindingInfo(
                dir: "06_two_way_binding",
                name: "输入组件回写 + 反应式联动",
                description: "TextField / Slider / CheckBox 输入自动写回 Data Model 并触发同路径重渲染",
                icon: "↔️",
                color: UIColor(red: 0.0, green: 0.72, blue: 0.58, alpha: 1.0)
            )
        ]),
        BindingSection(title: "函数与插值（Function & Interpolation）", entries: [
            BindingInfo(
                dir: "07_function_binding",
                name: "FunctionCall 作为 Dynamic*",
                description: "以 FunctionCall 作为属性值（可嵌套 args）",
                icon: "ƒ",
                color: UIColor(red: 0.09, green: 0.63, blue: 0.52, alpha: 1.0)
            ),
            BindingInfo(
                dir: "08_string_interpolation",
                name: "formatString 字符串插值",
                description: "${path} / ${func(args...)} / 转义 / 类型转换",
                icon: "🧵",
                color: UIColor(red: 0.15, green: 0.68, blue: 0.38, alpha: 1.0)
            )
        ]),
        BindingSection(title: "校验绑定（Check Binding）", entries: [
            BindingInfo(
                dir: "09_check_binding",
                name: "checks 表达式",
                description: "条件基于路径与逻辑函数，未通过时 Button 自动 disable",
                icon: "✅",
                color: UIColor(red: 0.91, green: 0.30, blue: 0.24, alpha: 1.0)
            )
        ]),
        BindingSection(title: "动作与无障碍（Action & A11y）", entries: [
            BindingInfo(
                dir: "10_action_context",
                name: "Action.context 绑定",
                description: "点击事件把 Data Model 值随 context 一起下发",
                icon: "🖱️",
                color: UIColor(red: 0.90, green: 0.49, blue: 0.13, alpha: 1.0)
            ),
            BindingInfo(
                dir: "11_accessibility",
                name: "无障碍字段动态绑定",
                description: "accessibility.label / description 支持 DynamicString",
                icon: "♿",
                color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0)
            )
        ]),
        BindingSection(title: "渐进渲染（Progressive Rendering）", entries: [
            BindingInfo(
                dir: "12_progressive_rendering",
                name: "缺数据 / 数据后到达降级",
                description: "path 未定义时 UI 优雅降级，updateDataModel 到达后自动填充重渲染",
                icon: "⏳",
                color: UIColor(red: 0.50, green: 0.55, blue: 0.55, alpha: 1.0)
            )
        ])
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "数据绑定测试"
        view.backgroundColor = .systemGroupedBackground
        setupTableView()
    }

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(ComponentCell.self, forCellReuseIdentifier: "BindingCell")
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
        headerLabel.text = "A2UI v0.9 协议 12 项数据绑定能力"
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
        let cell = tableView.dequeueReusableCell(withIdentifier: "BindingCell", for: indexPath) as! ComponentCell
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
