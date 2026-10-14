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
import WebKit

/// Custom Component Demo View Controller.
///
/// Demonstrates the A2UI custom component extension capability:
///  1. Registers two custom render views globally via `A2UISurfaceView.registerCustomRenderView`
///  2. Provides navigation to test cases for each custom component
///
/// The registered custom components are:
///  - A2UIWeb: A WKWebView component that supports a "source" property to load URLs
///  - MyA2UIView: A simple custom view that supports "text" and "textColor" properties
class CustomComponentDemoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

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

        // Register custom components
        registerCustomComponents()

        setupTableView()
    }

    deinit {
        unregisterCustomComponents()
    }

    // MARK: - Custom Component Registration

    private func registerCustomComponents() {
        // 1. A2UIWeb: A WKWebView component with "source" property
        A2UISurfaceView.registerCustomRenderView(viewName: "A2UIWeb") {
            A2UIWebView()
        }

        // 2. MyA2UIView: A simple custom view with "text" and "textColor" properties
        A2UISurfaceView.registerCustomRenderView(viewName: "MyA2UIView") {
            MyA2UIView()
        }

        print("✅ [CustomComponent] All custom components registered: A2UIWeb, MyA2UIView")
    }

    private func unregisterCustomComponents() {
        A2UISurfaceView.unregisterCustomRenderView(viewName: "A2UIWeb")
        A2UISurfaceView.unregisterCustomRenderView(viewName: "MyA2UIView")
        print("🗑️ [CustomComponent] All custom components unregistered")
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

        // Header
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 60))
        let headerLabel = UILabel()
        headerLabel.text = "✅ 已注册 2 个自定义组件\nA2UIWeb (WKWebView), MyA2UIView (UILabel)"
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

// MARK: - Custom Component Implementations

/// A2UIWeb: A custom WKWebView component for A2UI.
/// Supports the "source" property to load a URL.
///
/// This class implements `KuiklyRenderViewExportProtocol` by extending UIView
/// and embedding a WKWebView inside.
/// It also provides a shadow (via `hrv_createShadow`) that reports an intrinsic
/// height of 240pt, so the flex engine can correctly size the card.
@objc(A2UIWeb)
class A2UIWebView: UIView, KuiklyRenderViewExportProtocol {

    private let webView: WKWebView = {
        let config = WKWebViewConfiguration()
        let wv = WKWebView(frame: .zero, configuration: config)
        wv.translatesAutoresizingMaskIntoConstraints = false
        return wv
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(webView)
        NSLayoutConstraint.activate([
            webView.topAnchor.constraint(equalTo: topAnchor),
            webView.leadingAnchor.constraint(equalTo: leadingAnchor),
            webView.trailingAnchor.constraint(equalTo: trailingAnchor),
            webView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // KuiklyRenderViewExportProtocol - setProp
    @objc func hrv_setProp(withKey propKey: String, propValue: Any) {
        // Handle standard CSS properties (frame, backgroundColor, opacity, etc.)
        if css_setProp(withKey: propKey, value: propValue) {
            return
        }
        // Handle custom properties
        if propKey == "source", let url = propValue as? String, !url.isEmpty {
            if let requestURL = URL(string: url) {
                webView.load(URLRequest(url: requestURL))
            }
        }
    }

    // KuiklyRenderViewExportProtocol - resetProp
    @objc func hrv_resetPropWithKey(_ propKey: String) {
        if propKey == "source" {
            webView.load(URLRequest(url: URL(string: "about:blank")!))
        }
    }

    // KuiklyRenderViewExportProtocol - setShadow
    @objc func hrv_setShadow(_ shadow: Any) {
        // Shadow is managed by the render layer; no local reference needed.
    }

    // KuiklyRenderViewExportProtocol - createShadow
    @objc class func hrv_createShadow() -> NSObject {
        return A2UIWebShadow()
    }
}

/// MyA2UIView: A simple custom view for A2UI.
/// Supports "text" and "textColor" properties.
///
/// This class implements `KuiklyRenderViewExportProtocol` by extending UIView
/// and embedding a UILabel inside.
/// It also provides a shadow (via `hrv_createShadow`) that reports an intrinsic
/// height of 200pt, so the flex engine can correctly size the card.
@objc(MyA2UIView)
class MyA2UIView: UIView, KuiklyRenderViewExportProtocol {

    private let label: UILabel = {
        let lbl = UILabel()
        lbl.font = .systemFont(ofSize: 16)
        lbl.textAlignment = .center
        lbl.textColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 1.0)
        lbl.translatesAutoresizingMaskIntoConstraints = false
        return lbl
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = UIColor(red: 232/255.0, green: 245/255.0, blue: 233/255.0, alpha: 1.0) // #E8F5E9
        addSubview(label)
        NSLayoutConstraint.activate([
            label.topAnchor.constraint(equalTo: topAnchor),
            label.leadingAnchor.constraint(equalTo: leadingAnchor),
            label.trailingAnchor.constraint(equalTo: trailingAnchor),
            label.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // KuiklyRenderViewExportProtocol - setProp
    @objc func hrv_setProp(withKey propKey: String, propValue: Any) {
        // Handle standard CSS properties (frame, backgroundColor, opacity, etc.)
        if css_setProp(withKey: propKey, value: propValue) {
            return
        }
        // Handle custom properties
        switch propKey {
        case "text":
            label.text = "\(propValue)"
        case "textColor":
            if let colorStr = propValue as? String {
                label.textColor = UIColor.fromHex(colorStr)
            }
        default:
            break
        }
    }

    // KuiklyRenderViewExportProtocol - resetProp
    @objc func hrv_resetPropWithKey(_ propKey: String) {
        switch propKey {
        case "text":
            label.text = ""
        case "textColor":
            label.textColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 1.0)
        default:
            break
        }
    }

    // KuiklyRenderViewExportProtocol - setShadow
    @objc func hrv_setShadow(_ shadow: Any) {
        // Shadow is managed by the render layer; no local reference needed.
    }

    // KuiklyRenderViewExportProtocol - createShadow
    @objc class func hrv_createShadow() -> NSObject {
        return MyA2UIViewShadow()
    }
}

// MARK: - Shadow Implementations

/// Shadow for A2UIWebView.
/// Reports intrinsic size: width = constraint width (fill parent), height = 240pt.
class A2UIWebShadow: NSObject {

    @objc func hrv_setPropWithKey(_ propKey: String, propValue: Any) {
        // No props affect measurement for this simple demo
    }

    @objc func hrv_calculateRenderViewSize(withConstraintSize constraintSize: CGSize) -> CGSize {
        // Width: use constraint width (fill parent)
        // Height: fixed 240pt (the intrinsic content height of this WebView component)
        return CGSize(width: constraintSize.width, height: 240)
    }

    @objc func hrv_call(withMethod method: String, params: String?) -> String? {
        return nil
    }
}

/// Shadow for MyA2UIView.
/// Reports intrinsic size: width = constraint width (fill parent), height = 200pt.
class MyA2UIViewShadow: NSObject {

    @objc func hrv_setPropWithKey(_ propKey: String, propValue: Any) {
        // No props affect measurement for this simple demo
    }

    @objc func hrv_calculateRenderViewSize(withConstraintSize constraintSize: CGSize) -> CGSize {
        // Width: use constraint width (fill parent)
        // Height: fixed 200pt (the intrinsic content height of this custom view)
        return CGSize(width: constraintSize.width, height: 200)
    }

    @objc func hrv_call(withMethod method: String, params: String?) -> String? {
        return nil
    }
}

// MARK: - UIColor Hex Extension (for MyA2UIView)

private extension UIColor {
    static func fromHex(_ hex: String) -> UIColor {
        var hexStr = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        if hexStr.hasPrefix("#") { hexStr.removeFirst() }
        guard hexStr.count == 6, let rgb = UInt64(hexStr, radix: 16) else {
            return .darkText
        }
        return UIColor(
            red: CGFloat((rgb >> 16) & 0xFF) / 255.0,
            green: CGFloat((rgb >> 8) & 0xFF) / 255.0,
            blue: CGFloat(rgb & 0xFF) / 255.0,
            alpha: 1.0
        )
    }
}
