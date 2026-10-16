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

/// Custom Component Demo View Controller — **KuiklyPage variant**.
///
/// iOS mirror of `androidAppForKuiklyPage/CustomComponentDemoActivity`.
/// Registers the same two custom render views as the native variant
/// (`iosShared/CustomComponentDemoViewController`) — `A2UIWeb` (WKWebView)
/// and `MyA2UIView` (simple UILabel) — but the per-case detail screen is a
/// **KuiklyPage** (`@Page("A2UICaseList")`) rather than a raw
/// `A2UISurfaceView` list.
///
/// The custom render view classes themselves (`A2UIWebView`,
/// `MyA2UIView`, and their shadows) live in
/// `iosShared/CustomComponentDemoViewController.swift` and are shared
/// verbatim with the NativePage app.
final class CustomComponentKuiklyDemoViewController: UIViewController,
    UITableViewDataSource, UITableViewDelegate {

    private static let rootDir = "a2ui_custom_components"
    private static let titleSuffix = "自定义组件"

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct ComponentInfo {
        let dir: String
        let name: String
        let description: String
        let icon: String
        let color: UIColor
    }

    private struct ComponentSection {
        let title: String
        let entries: [ComponentInfo]
    }

    private let sections: [ComponentSection] = [
        ComponentSection(title: "WebView 自定义组件", entries: [
            ComponentInfo(
                dir: "01_web_view",
                name: "A2UIWeb → WKWebView",
                description: "自定义 WebView 组件，支持 source 属性加载网页。演示通过 registerCustomRenderView 注册自定义组件。",
                icon: "🌐",
                color: UIColor(red: 0.91, green: 0.12, blue: 0.39, alpha: 1.0)
            )
        ]),
        ComponentSection(title: "简单自定义组件", entries: [
            ComponentInfo(
                dir: "02_my_view",
                name: "MyA2UIView → UILabel",
                description: "最简单的自定义组件，支持 text 和 textColor 属性。演示自定义属性处理。",
                icon: "🏷️",
                color: UIColor(red: 0.38, green: 0.0, blue: 0.93, alpha: 1.0)
            )
        ])
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "自定义组件示例"
        view.backgroundColor = .systemGroupedBackground

        registerCustomComponents()
        setupTableView()
    }

    deinit {
        unregisterCustomComponents()
    }

    // MARK: - Custom Component Registration

    private func registerCustomComponents() {
        // A2UIWeb and MyA2UIView are declared in
        // iosShared/CustomComponentDemoViewController.swift and shared
        // with the NativePage app; the classes are visible to this
        // target through the shared Sources compile group.
        A2UISurfaceView.registerCustomRenderView(
            viewName: "A2UIWeb",
            creator: { A2UIWebView() },
            measurer: { constraintWidth, _ in
                A2UISurfaceView.A2UICustomViewSize(width: constraintWidth, height: 240)
            }
        )
        A2UISurfaceView.registerCustomRenderView(
            viewName: "MyA2UIView",
            creator: { MyA2UIView() },
            measurer: { constraintWidth, _ in
                A2UISurfaceView.A2UICustomViewSize(width: constraintWidth, height: 200)
            }
        )
        print("✅ [CustomComponentKuikly] All custom components registered: A2UIWeb, MyA2UIView")
    }

    private func unregisterCustomComponents() {
        A2UISurfaceView.unregisterCustomRenderView(viewName: "A2UIWeb")
        A2UISurfaceView.unregisterCustomRenderView(viewName: "MyA2UIView")
        print("🗑️ [CustomComponentKuikly] All custom components unregistered")
    }

    // MARK: - Table View Setup

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(ComponentCell.self, forCellReuseIdentifier: "ComponentCell")
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
        headerLabel.text = "✅ 已注册 2 个自定义组件（宿主：Kuikly Page）\nA2UIWeb (WKWebView), MyA2UIView (UILabel)"
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
        let cell = tableView.dequeueReusableCell(withIdentifier: "ComponentCell", for: indexPath) as! ComponentCell
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
