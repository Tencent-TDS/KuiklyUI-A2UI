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
import OpenKuiklyA2UIIOSRender

/// Component Test View Controller.
    /// Renders all test-case JSON files under `Resources/a2ui_components/<componentDir>/`
/// for a single A2UI component. Used to verify per-property rendering correctness.
///
/// Launched from `ComponentDemoViewController`. Pass:
    ///  - `componentDir` : directory name under a2ui_components/ (e.g. "text")
///  - `componentName`: display name shown in the title bar (e.g. "Text")
class A2UIComponentTestViewController: UIViewController, A2UISurfaceViewDelegate {

    /// Asset root directory inside the bundle. Overridable via init.
    static let defaultRootDir = "a2ui_components"
    static let defaultTitleSuffix = "组件测试"

    private let rootDir: String
    private let titleSuffix: String
    private let componentDir: String
    private let componentName: String

    private var surfaceViews: [A2UISurfaceView] = []
    private let scrollView = UIScrollView()
    private let contentStack = UIStackView()

    init(componentDir: String,
         componentName: String,
         rootDir: String = A2UIComponentTestViewController.defaultRootDir,
         titleSuffix: String = A2UIComponentTestViewController.defaultTitleSuffix) {
        self.componentDir = componentDir
        self.componentName = componentName.isEmpty ? componentDir : componentName
        self.rootDir = rootDir
        self.titleSuffix = titleSuffix
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "\(componentName) \(titleSuffix)"
        view.backgroundColor = UIColor(red: 0.96, green: 0.96, blue: 0.96, alpha: 1.0)

        setupLayout()
        loadTestCases()
    }

    private func setupLayout() {
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)

        contentStack.axis = .vertical
        contentStack.spacing = 4
        contentStack.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(contentStack)

        let safeArea = view.safeAreaLayoutGuide
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: safeArea.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            contentStack.topAnchor.constraint(equalTo: scrollView.topAnchor, constant: 12),
            contentStack.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor, constant: 12),
            contentStack.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor, constant: -12),
            contentStack.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor, constant: -16),
            contentStack.widthAnchor.constraint(equalTo: scrollView.widthAnchor, constant: -24)
        ])

        // Subtitle showing source directory
        let subtitle = UILabel()
        subtitle.text = "测试用例目录：\(rootDir)/\(componentDir)/"
        subtitle.font = .systemFont(ofSize: 12)
        subtitle.textColor = .secondaryLabel
        subtitle.numberOfLines = 0
        contentStack.addArrangedSubview(subtitle)
        contentStack.setCustomSpacing(12, after: subtitle)
    }

    private func loadTestCases() {
        guard let resourcePath = Bundle.main.resourcePath else {
            showEmptyState(reason: "无法读取资源路径")
            return
        }
        let dirPath = (resourcePath as NSString)
            .appendingPathComponent(rootDir)
            .appending("/\(componentDir)")
        let fileManager = FileManager.default

        var isDir: ObjCBool = false
        guard fileManager.fileExists(atPath: dirPath, isDirectory: &isDir), isDir.boolValue,
              let files = try? fileManager.contentsOfDirectory(atPath: dirPath) else {
            showEmptyState(reason: "目录不存在或为空：\(rootDir)/\(componentDir)/")
            return
        }
        let sortedFiles = files.filter { $0.hasSuffix(".json") }.sorted()
        if sortedFiles.isEmpty {
            showEmptyState(reason: "暂无 JSON 测试用例")
            return
        }

        var index = 0
        for fileName in sortedFiles {
            let filePath = (dirPath as NSString).appendingPathComponent(fileName)
            guard let data = fileManager.contents(atPath: filePath),
                  let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                NSLog("[ComponentTest] Skip \(fileName): invalid JSON")
                continue
            }
            guard let messages = json["messages"] as? [[String: Any]],
                  let messagesData = try? JSONSerialization.data(withJSONObject: messages),
                  let messagesStr = String(data: messagesData, encoding: .utf8) else {
                NSLog("[ComponentTest] Skip \(fileName): no messages array")
                continue
            }

            index += 1
            let name = json["name"] as? String ?? fileName
            let description = json["description"] as? String ?? ""

            // Title row: index + name + filename badge
            let titleRow = UIStackView()
            titleRow.axis = .horizontal
            titleRow.alignment = .center
            titleRow.spacing = 8

            let titleLabel = UILabel()
            titleLabel.text = "\(index). \(name)"
            titleLabel.font = .boldSystemFont(ofSize: 13)
            titleLabel.textColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 1.0)
            titleLabel.numberOfLines = 0
            titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
            titleRow.addArrangedSubview(titleLabel)

            let badge = PaddedLabel()
            badge.text = fileName
            badge.font = .systemFont(ofSize: 10)
            badge.textColor = UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)
            badge.backgroundColor = UIColor(red: 0.93, green: 0.91, blue: 1.0, alpha: 1.0)
            badge.layer.cornerRadius = 4
            badge.layer.masksToBounds = true
            badge.setContentCompressionResistancePriority(.required, for: .horizontal)
            titleRow.addArrangedSubview(badge)

            contentStack.addArrangedSubview(titleRow)

            if !description.isEmpty {
                let descLabel = UILabel()
                descLabel.text = description
                descLabel.font = .systemFont(ofSize: 11)
                descLabel.textColor = .secondaryLabel
                descLabel.numberOfLines = 0
                contentStack.addArrangedSubview(descLabel)
            }

            // Card container - transparent layout wrapper, no extra styling
            // (the inner Card component handles its own styling per v0.9 protocol)
            let cardContainer = UIView()
            cardContainer.backgroundColor = .clear
            cardContainer.clipsToBounds = false
            cardContainer.translatesAutoresizingMaskIntoConstraints = false
            cardContainer.heightAnchor.constraint(greaterThanOrEqualToConstant: 60).isActive = true
            contentStack.addArrangedSubview(cardContainer)

            let surfaceView = A2UISurfaceView(frame: .zero)
            surfaceView.translatesAutoresizingMaskIntoConstraints = false
            cardContainer.addSubview(surfaceView)
            NSLayoutConstraint.activate([
                surfaceView.topAnchor.constraint(equalTo: cardContainer.topAnchor),
                surfaceView.leadingAnchor.constraint(equalTo: cardContainer.leadingAnchor),
                surfaceView.trailingAnchor.constraint(equalTo: cardContainer.trailingAnchor),
                surfaceView.bottomAnchor.constraint(equalTo: cardContainer.bottomAnchor)
            ])

            surfaceView.initSurface()
            surfaceView.surfaceListener = self
            A2UIDemoStreamUtil.writeMessages(surfaceView, messagesStr)
            surfaceView.finishStream()
            surfaceViews.append(surfaceView)
        }
    }

    private func showEmptyState(reason: String) {
        let label = UILabel()
        label.text = "暂无测试用例\n\(reason)"
        label.font = .systemFont(ofSize: 14)
        label.textColor = .tertiaryLabel
        label.textAlignment = .center
        label.numberOfLines = 0
        contentStack.addArrangedSubview(label)
    }

    deinit {
        for sv in surfaceViews {
            sv.destroy()
        }
        surfaceViews.removeAll()
    }

    // MARK: - A2UISurfaceViewDelegate

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView,
                         didReceiveAction action: String,
                         fromTag tag: Int,
                         inSurface surfaceId: String) {
        NSLog("[ComponentTest][\(componentName)] Action: surfaceId=\(surfaceId), tag=\(tag), action=\(action)")
        // Lightweight toast-like alert
        let alert = UIAlertController(title: "Action", message: action, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

// MARK: - PaddedLabel (filename badge)

/// A UILabel with horizontal padding so the rounded background looks like a pill.
private class PaddedLabel: UILabel {
    var horizontalInset: CGFloat = 6
    var verticalInset: CGFloat = 2

    override var intrinsicContentSize: CGSize {
        let s = super.intrinsicContentSize
        return CGSize(width: s.width + horizontalInset * 2, height: s.height + verticalInset * 2)
    }

    override func drawText(in rect: CGRect) {
        let inset = UIEdgeInsets(top: verticalInset, left: horizontalInset,
                                 bottom: verticalInset, right: horizontalInset)
        super.drawText(in: rect.inset(by: inset))
    }
}
