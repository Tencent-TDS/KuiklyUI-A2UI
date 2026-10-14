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
import OpenA2UIIOSRender

/// Custom Function Demo View Controller — **KuiklyPage variant**.
///
/// iOS mirror of `androidAppForKuiklyPage/CustomFunctionDemoActivity`.
/// Registers a set of custom functions globally via
/// `A2UISurfaceView.registerCustomFunction` and, on row selection, pushes a
/// `A2UIComponentTestKuiklyPageViewController` so the per-case screen renders
/// via **KuiklyPage** (`@Page("A2UICaseList")`) rather than the native
/// `A2UISurfaceView` list used by `iosShared/CustomFunctionDemoViewController`.
///
/// The registration logic is intentionally identical to the native-variant
/// VC in `iosShared` — the difference lives entirely in the child screen.
final class CustomFunctionKuiklyDemoViewController: UIViewController,
    UITableViewDataSource, UITableViewDelegate {

    private static let rootDir = "a2ui_custom_functions"
    private static let titleSuffix = "自定义函数"

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct FunctionInfo {
        let dir: String
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
        FunctionSection(title: "返回 String 的自定义函数", entries: [
            FunctionInfo(
                dir: "01_string_return",
                name: "getGreeting / formatPhone → String",
                description: "自定义函数返回字符串，绑定到 Text.text 属性。验证表达式求值路径的同步调用和返回值传递。",
                icon: "📝",
                color: UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)
            )
        ]),
        FunctionSection(title: "返回 Boolean 的自定义函数", entries: [
            FunctionInfo(
                dir: "02_boolean_return",
                name: "isVip(level) → Boolean",
                description: "自定义函数返回布尔值，用于 check 条件控制组件显隐。验证 evaluateCheckFunction 兜底路径。",
                icon: "✓",
                color: UIColor(red: 0.0, green: 0.72, blue: 0.58, alpha: 1.0)
            )
        ]),
        FunctionSection(title: "返回 Number 的自定义函数", entries: [
            FunctionInfo(
                dir: "03_number_return",
                name: "calculateDiscount(price, rate) → Number",
                description: "自定义函数返回数值，绑定到 Text.text。验证数值类型的同步返回和 coercion。",
                icon: "#",
                color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0)
            )
        ]),
        FunctionSection(title: "Void 函数作为 Action", entries: [
            FunctionInfo(
                dir: "04_void_action",
                name: "logEvent(event, data) → Void",
                description: "自定义 void 函数作为 Button 的 action.functionCall。点击按钮触发。",
                icon: "⚡",
                color: UIColor(red: 0.91, green: 0.30, blue: 0.24, alpha: 1.0)
            )
        ]),
        FunctionSection(title: "多参数 + 数据绑定", entries: [
            FunctionInfo(
                dir: "05_args_passing",
                name: "buildSummary(name, count, status) → String",
                description: "多参数自定义函数，参数来自 dataModel 深层路径绑定。",
                icon: "🔗",
                color: UIColor(red: 0.04, green: 0.52, blue: 0.89, alpha: 1.0)
            )
        ])
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "自定义函数示例"
        view.backgroundColor = .systemGroupedBackground

        registerCustomFunctions()
        setupTableView()
    }

    deinit {
        unregisterCustomFunctions()
    }

    // MARK: - Custom Function Registration

    private func registerCustomFunctions() {
        // Same 6 functions as the native-variant VC in iosShared —
        // the KuiklyPage rendering path exercises them through
        // KRA2UICustomFunctionRegistry.
        A2UISurfaceView.registerCustomFunction(
            name: "getGreeting",
            returnType: A2UISurfaceView.returnTypeString,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let name = args["name"] as? String ?? "World"
            return "Hello, \(name)!"
        }

        A2UISurfaceView.registerCustomFunction(
            name: "formatPhone",
            returnType: A2UISurfaceView.returnTypeString,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let phone = args["phone"] as? String ?? ""
            if phone.count == 11 {
                let start = phone.prefix(3)
                let mid = phone.dropFirst(3).prefix(4)
                let end = phone.suffix(4)
                return "\(start)-\(mid)-\(end)"
            }
            return phone
        }

        A2UISurfaceView.registerCustomFunction(
            name: "isVip",
            returnType: A2UISurfaceView.returnTypeBoolean,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let level = (args["level"] as? NSNumber)?.intValue ?? 0
            return level >= 3
        }

        A2UISurfaceView.registerCustomFunction(
            name: "calculateDiscount",
            returnType: A2UISurfaceView.returnTypeNumber,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let price = (args["price"] as? NSNumber)?.doubleValue ?? 0.0
            let rate = (args["rate"] as? NSNumber)?.doubleValue ?? 1.0
            let result = (price * rate * 100).rounded() / 100
            return NSNumber(value: result)
        }

        A2UISurfaceView.registerCustomFunction(
            name: "logEvent",
            returnType: A2UISurfaceView.returnTypeVoid,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let event = args["event"] as? String ?? ""
            let data = args["data"] as? String ?? ""
            print("📊 [CustomFunctionKuikly] logEvent: event=\(event), data=\(data)")
            return nil
        }

        A2UISurfaceView.registerCustomFunction(
            name: "buildSummary",
            returnType: A2UISurfaceView.returnTypeString,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let name = args["name"] as? String ?? "Unknown"
            let count = (args["count"] as? NSNumber)?.intValue ?? 0
            let status = args["status"] as? String ?? "unknown"
            return "项目「\(name)」当前状态: \(status)，共 \(count) 个任务"
        }

        print("✅ [CustomFunctionKuikly] All custom functions registered")
    }

    private func unregisterCustomFunctions() {
        A2UISurfaceView.unregisterCustomFunction(name: "getGreeting")
        A2UISurfaceView.unregisterCustomFunction(name: "formatPhone")
        A2UISurfaceView.unregisterCustomFunction(name: "isVip")
        A2UISurfaceView.unregisterCustomFunction(name: "calculateDiscount")
        A2UISurfaceView.unregisterCustomFunction(name: "logEvent")
        A2UISurfaceView.unregisterCustomFunction(name: "buildSummary")
        print("🗑️ [CustomFunctionKuikly] All custom functions unregistered")
    }

    // MARK: - Table View Setup

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        // Reuses ComponentCell from iosShared/ComponentDemoViewController.swift.
        tableView.register(ComponentCell.self, forCellReuseIdentifier: "FunctionCell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 70))
        let headerLabel = UILabel()
        headerLabel.text = "✅ 已注册 6 个自定义函数（宿主：Kuikly Page）\ngetGreeting, formatPhone, isVip, calculateDiscount, logEvent, buildSummary"
        headerLabel.font = .systemFont(ofSize: 12)
        headerLabel.textColor = .secondaryLabel
        headerLabel.numberOfLines = 0
        headerLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(headerLabel)
        NSLayoutConstraint.activate([
            headerLabel.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 20),
            headerLabel.trailingAnchor.constraint(equalTo: headerView.trailingAnchor, constant: -20),
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

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat { 78 }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let entry = sections[indexPath.section].entries[indexPath.row]
        if hasTestCases(for: entry.dir) {
            // Push the *KuiklyPage* test VC — that's the whole point of this
            // variant. `A2UIComponentTestKuiklyPageViewController` wires up a
            // `KuiklyRenderViewControllerBaseDelegator` for @Page("A2UICaseList").
            let vc = A2UIComponentTestKuiklyPageViewController(
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

    private func hasTestCases(for dir: String) -> Bool {
        guard let resourcePath = Bundle.main.resourcePath else { return false }
        let dirPath = (resourcePath as NSString)
            .appendingPathComponent(Self.rootDir)
            .appending("/\(dir)")
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
