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

/// Official Example View Controller.
/// Displays all 36 A2UI v0.9 official example cards from the specification.
/// Each card renders the JSON content from the examples directory without modification.
class OfficialExampleViewController: UIViewController, A2UISurfaceViewDelegate {

    private var surfaceViews: [A2UISurfaceView] = []
    private let scrollView = UIScrollView()
    private let contentStack = UIStackView()

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "A2UI v0.9 协议官方示例"
        view.backgroundColor = UIColor(red: 0.96, green: 0.96, blue: 0.96, alpha: 1.0)

        setupLayout()
        loadExamples()
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

        // Subtitle
        let subtitle = UILabel()
        subtitle.text = "展示 A2UI v0.9 协议规范中的 36 个官方示例，JSON 内容无任何修改"
        subtitle.font = .systemFont(ofSize: 13)
        subtitle.textColor = .secondaryLabel
        subtitle.numberOfLines = 0
        contentStack.addArrangedSubview(subtitle)
        contentStack.setCustomSpacing(12, after: subtitle)
    }

    private func loadExamples() {
        guard let resourcePath = Bundle.main.resourcePath else { return }
        let examplesPath = (resourcePath as NSString).appendingPathComponent("a2ui_examples")
        let fileManager = FileManager.default

        guard let files = try? fileManager.contentsOfDirectory(atPath: examplesPath) else { return }
        let sortedFiles = files.filter { $0.hasSuffix(".json") }.sorted()

        var exampleIndex = 0
        for fileName in sortedFiles {
            let filePath = (examplesPath as NSString).appendingPathComponent(fileName)
            guard let data = fileManager.contents(atPath: filePath),
                  let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                continue
            }

            exampleIndex += 1
            let name = json["name"] as? String ?? fileName
            let description = json["description"] as? String ?? ""
            guard let messages = json["messages"] as? [[String: Any]] else { continue }
            guard let messagesData = try? JSONSerialization.data(withJSONObject: messages),
                  let messagesStr = String(data: messagesData, encoding: .utf8) else { continue }

            // Card title label
            let titleLabel = UILabel()
            titleLabel.text = "\(exampleIndex). \(name)"
            titleLabel.font = .boldSystemFont(ofSize: 13)
            titleLabel.textColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 1.0)
            contentStack.addArrangedSubview(titleLabel)

            // Description label
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
            // Minimum height to avoid empty cards
            cardContainer.heightAnchor.constraint(greaterThanOrEqualToConstant: 60).isActive = true
            contentStack.addArrangedSubview(cardContainer)

            // A2UI surface view
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

    deinit {
        for sv in surfaceViews {
            sv.destroy()
        }
        surfaceViews.removeAll()
    }

    // MARK: - A2UISurfaceViewDelegate

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didReceiveAction action: String, fromTag tag: Int, inSurface surfaceId: String) {
        print("[OfficialExample] Action received: surfaceId=\(surfaceId), tag=\(tag), action=\(action)")
        // Show a toast-like alert to match Android behavior
        let alert = UIAlertController(title: "Action Received", message: action, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}
