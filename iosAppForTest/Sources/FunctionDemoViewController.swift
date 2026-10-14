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

/// Function Demo View Controller.
/// Displays the 14 built-in A2UI v0.9 catalog functions grouped by category.
/// Selecting a function pushes `A2UIComponentTestViewController` bound to
/// `Resources/a2ui_function_tests/<funcName>/`.
class FunctionDemoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private static let rootDir = "a2ui_function_tests"
    private static let titleSuffix = "函数测试"

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct FunctionInfo {
        let name: String
        let description: String
        let icon: String
        let color: UIColor
    }

    private struct FunctionSection {
        let title: String
        let entries: [FunctionInfo]
    }

    private let sections: [FunctionSection] = [
        FunctionSection(title: "校验函数（Validation, returns Boolean）", entries: [
            FunctionInfo(name: "required", description: "非空校验：值不为 null / undefined / 空串", icon: "✅",
                         color: UIColor(red: 0.91, green: 0.30, blue: 0.24, alpha: 1.0)),
            FunctionInfo(name: "regex", description: "正则匹配：value 必须匹配指定 pattern", icon: "🔤",
                         color: UIColor(red: 0.75, green: 0.22, blue: 0.17, alpha: 1.0)),
            FunctionInfo(name: "length", description: "长度约束：字符串长度落在 [min,max]", icon: "📏",
                         color: UIColor(red: 0.83, green: 0.33, blue: 0.0, alpha: 1.0)),
            FunctionInfo(name: "numeric", description: "数值范围：数字落在 [min,max]", icon: "🔢",
                         color: UIColor(red: 0.90, green: 0.49, blue: 0.13, alpha: 1.0)),
            FunctionInfo(name: "email", description: "邮箱格式：value 必须是合法 email", icon: "📧",
                         color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0))
        ]),
        FunctionSection(title: "格式化函数（Formatting, returns String）", entries: [
            FunctionInfo(name: "formatString", description: "字符串插值：${path} / ${fn(...)}", icon: "🧵",
                         color: UIColor(red: 0.29, green: 0.56, blue: 0.85, alpha: 1.0)),
            FunctionInfo(name: "formatNumber", description: "数值格式化：小数位、千位分隔", icon: "🧮",
                         color: UIColor(red: 0.20, green: 0.60, blue: 0.86, alpha: 1.0)),
            FunctionInfo(name: "formatCurrency", description: "货币格式化：ISO4217 currency", icon: "💵",
                         color: UIColor(red: 0.17, green: 0.24, blue: 0.31, alpha: 1.0)),
            FunctionInfo(name: "formatDate", description: "日期格式化：Unicode TR35 pattern", icon: "🗓️",
                         color: UIColor(red: 0.09, green: 0.63, blue: 0.52, alpha: 1.0)),
            FunctionInfo(name: "pluralize", description: "复数选择：CLDR zero/one/two/few/many/other", icon: "🌐",
                         color: UIColor(red: 0.15, green: 0.68, blue: 0.38, alpha: 1.0))
        ]),
        FunctionSection(title: "副作用函数（Side-effect, returns Void）", entries: [
            FunctionInfo(name: "openUrl", description: "打开外链：仅用于 Action.functionCall", icon: "🔗",
                         color: UIColor(red: 0.56, green: 0.27, blue: 0.68, alpha: 1.0))
        ]),
        FunctionSection(title: "逻辑函数（Logic, returns Boolean）", entries: [
            FunctionInfo(name: "and", description: "逻辑与：所有子条件全为 true", icon: "🅰️",
                         color: UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)),
            FunctionInfo(name: "or", description: "逻辑或：至少一个子条件为 true", icon: "🅾️",
                         color: UIColor(red: 0.61, green: 0.35, blue: 0.71, alpha: 1.0)),
            FunctionInfo(name: "not", description: "逻辑非：对 boolean 取反", icon: "❎",
                         color: UIColor(red: 0.20, green: 0.29, blue: 0.37, alpha: 1.0))
        ])
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "函数测试"
        view.backgroundColor = .systemGroupedBackground
        setupTableView()
    }

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(ComponentCell.self, forCellReuseIdentifier: "FunctionCell")
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
        headerLabel.text = "A2UI v0.9 basic catalog 内建 14 个函数"
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
        let cell = tableView.dequeueReusableCell(withIdentifier: "FunctionCell", for: indexPath) as! ComponentCell
        let entry = sections[indexPath.section].entries[indexPath.row]
        cell.configure(icon: entry.icon, name: entry.name, description: entry.description, color: entry.color)
        return cell
    }

    // MARK: - UITableViewDelegate

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat { 72 }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let entry = sections[indexPath.section].entries[indexPath.row]
        if hasTestCases(for: entry.name) {
            let vc = A2UIComponentTestViewController(
                componentDir: entry.name,
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

    private func hasTestCases(for funcName: String) -> Bool {
        guard let resourcePath = Bundle.main.resourcePath else { return false }
        let dirPath = (resourcePath as NSString)
            .appendingPathComponent(Self.rootDir)
            .appending("/\(funcName)")
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
