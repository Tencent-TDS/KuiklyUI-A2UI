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
import OpenKuiklyIOSRender
import OpenKuiklyA2UIIOSRender

/// iOS mirror of `androidAppForKuiklyPage/A2UIComponentTestActivity` (KuiklyPage variant).
///
/// Reads every `<caseName>.json` under `Bundle.main` at
/// `<rootDir>/<componentDir>/`, aggregates them into a single `cases` JSON
/// array string, and hands the array off to the KMP page
/// `@Page("A2UICaseList")` (see `:demo/…/A2UICaseListPager`) via
/// `KuiklyRenderViewControllerBaseDelegator.pageData`. The Kuikly page then
/// renders every case as a stand-alone A2UISurface — giving us the
/// "many-cards-per-screen" UX with a single KMP page instance.
///
/// This is the KuiklyPage counterpart of iosShared's
/// `A2UIComponentTestViewController` (the native-view variant). The
/// KuiklyPage app uses this VC so custom function / component / icon
/// detail screens go through Kuikly rendering rather than raw
/// `A2UISurfaceView` cards.
final class A2UIComponentTestKuiklyPageViewController: UIViewController {

    /// Must match the `@Page` annotation on `A2UICaseListPager` in `:demo`.
    private static let pageName = "A2UICaseList"

    /// Framework baseName produced by `:demo`'s cocoapods block. Must be
    /// resolvable by Kuikly's `entryClassWithFrameworkName:`; keep in sync
    /// with `A2UISurfaceDSLDemoViewController.fetchContextCode(...)`.
    private static let frameworkName = "A2UIDemo"

    private static let defaultRootDir = "a2ui_components"
    private static let defaultTitleSuffix = "组件测试"

    private let componentDir: String
    private let componentName: String
    private let rootDir: String
    private let titleSuffix: String

    private var delegator: KuiklyRenderViewControllerBaseDelegator?
    private let container = UIView()

    // MARK: - Init

    init(componentDir: String,
         componentName: String,
         rootDir: String = A2UIComponentTestKuiklyPageViewController.defaultRootDir,
         titleSuffix: String = A2UIComponentTestKuiklyPageViewController.defaultTitleSuffix) {
        self.componentDir = componentDir
        self.componentName = componentName.isEmpty ? componentDir : componentName
        self.rootDir = rootDir
        self.titleSuffix = titleSuffix
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        // Ensure A2UI custom render view / function / icon registrations
        // reach the KMP-side registry consumed by A2UICaseList's
        // A2UISurface { … } children, *and* force-run the @Page registry
        // warm-up so `createPager("A2UICaseList")` cannot race against an
        // earlier `BridgeManager.init(true)` triggered by any A2UI code
        // path on this process (see A2UIKuiklyRenderViewSupport KDoc).
        A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
            pageName: Self.pageName,
            frameworkName: Self.frameworkName
        )

        title = "\(componentName) \(titleSuffix)"
        view.backgroundColor = UIColor(red: 245/255.0, green: 245/255.0, blue: 245/255.0, alpha: 1.0)

        setupHeader()
        setupContainer()
        setupDelegator()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        delegator?.viewDidLayoutSubviews()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        delegator?.viewWillAppear()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        delegator?.viewDidAppear()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        delegator?.viewWillDisappear()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        delegator?.viewDidDisappear()
    }

    // MARK: - Setup

    private let headerView = UIView()

    private func setupHeader() {
        headerView.translatesAutoresizingMaskIntoConstraints = false
        headerView.backgroundColor = UIColor(red: 63/255.0, green: 81/255.0, blue: 181/255.0, alpha: 1.0)
        view.addSubview(headerView)

        let subtitle = UILabel()
        subtitle.text = "宿主：Kuikly Page (KuiklyRenderViewControllerBaseDelegator + @Page A2UICaseList)\n资源：\(rootDir)/\(componentDir)/"
        subtitle.font = .systemFont(ofSize: 11)
        subtitle.textColor = UIColor(red: 0.87, green: 0.87, blue: 1.0, alpha: 1.0)
        subtitle.numberOfLines = 0
        subtitle.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(subtitle)

        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            headerView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            headerView.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            subtitle.topAnchor.constraint(equalTo: headerView.topAnchor, constant: 8),
            subtitle.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 20),
            subtitle.trailingAnchor.constraint(equalTo: headerView.trailingAnchor, constant: -20),
            subtitle.bottomAnchor.constraint(equalTo: headerView.bottomAnchor, constant: -12),
        ])
    }

    private func setupContainer() {
        container.translatesAutoresizingMaskIntoConstraints = false
        container.backgroundColor = UIColor(red: 0.96, green: 0.96, blue: 0.96, alpha: 1.0)
        view.addSubview(container)
        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: headerView.bottomAnchor),
            container.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }

    private func setupDelegator() {
        let casesJson = buildCasesJson()
        let d = KuiklyRenderViewControllerBaseDelegator(
            pageName: Self.pageName,
            pageData: [
                "title": "\(componentName) \(titleSuffix)",
                "cases": casesJson
            ]
        )
        d.delegate = self
        delegator = d
        d.viewDidLoad(with: container)
    }

    // MARK: - Cases assembly

    /// Aggregate every `<caseName>.json` under `Bundle.main/<rootDir>/<componentDir>/`
    /// into a single JSON array string with entries of shape
    /// `{ name, description, messages }` — the contract required by
    /// `A2UICaseListPager.parseCases()`.
    ///
    /// Skips files whose top-level JSON is malformed or missing a
    /// `messages` array; those cases are silently dropped so an isolated
    /// bad fixture doesn't break the whole page.
    private func buildCasesJson() -> String {
        guard let resourcePath = Bundle.main.resourcePath else { return "[]" }
        let dirPath = (resourcePath as NSString)
            .appendingPathComponent(rootDir)
            .appending("/\(componentDir)")

        let fm = FileManager.default
        var isDir: ObjCBool = false
        guard fm.fileExists(atPath: dirPath, isDirectory: &isDir), isDir.boolValue,
              let files = try? fm.contentsOfDirectory(atPath: dirPath) else {
            return "[]"
        }
        let sortedFiles = files.filter { $0.hasSuffix(".json") }.sorted()
        if sortedFiles.isEmpty { return "[]" }

        var cases: [[String: Any]] = []
        for fileName in sortedFiles {
            let filePath = (dirPath as NSString).appendingPathComponent(fileName)
            guard let data = fm.contents(atPath: filePath),
                  let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                NSLog("[A2UIComponentTestKuiklyPage] Skip \(fileName): invalid JSON")
                continue
            }
            guard let messages = json["messages"] as? [[String: Any]] else {
                NSLog("[A2UIComponentTestKuiklyPage] Skip \(fileName): no messages array")
                continue
            }
            let name = (json["name"] as? String) ?? fileName
            let desc = (json["description"] as? String) ?? ""
            cases.append([
                "name": name,
                "description": desc,
                "messages": messages
            ])
        }

        guard !cases.isEmpty,
              let data = try? JSONSerialization.data(withJSONObject: cases),
              let str = String(data: data, encoding: .utf8) else {
            return "[]"
        }
        return str
    }
}

// MARK: - KuiklyRenderViewControllerBaseDelegatorDelegate

extension A2UIComponentTestKuiklyPageViewController: KuiklyRenderViewControllerBaseDelegatorDelegate {

    /// See `A2UISurfaceDSLDemoViewController.fetchContextCode(...)` for
    /// the full explanation of why we hand back `A2UIDemo` here.
    func fetchContextCode(withPageName pageName: String,
                          resultCallback callback: @escaping KuiklyContextCodeCallback) {
        callback(Self.frameworkName, nil)
    }

    func createLoadingView() -> UIView {
        let v = UIView()
        v.backgroundColor = .white
        return v
    }

    func createErrorView() -> UIView {
        let v = UIView()
        v.backgroundColor = .white
        return v
    }
}
