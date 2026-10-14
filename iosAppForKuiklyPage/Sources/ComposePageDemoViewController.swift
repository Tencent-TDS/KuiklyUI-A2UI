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
import OpenA2UIIOSRender

/// iOS mirror of `androidAppForKuiklyPage/ComposePageDemoActivity`.
///
/// Hosts the Kuikly Compose page `ComposeSampleWithA2UI` (see
/// `:demo/…/ComposeSampleWithA2UIPager.kt`) inside a plain
/// `UIViewController`. The Compose-declared Kuikly page internally embeds
/// an `A2UISurfaceDSLView` via `MakeKuiklyComposeNode`, giving a proper
/// *Compose page → A2UI* integration example that runs identically on
/// Android and iOS.
///
/// Wiring is identical to `A2UISurfaceDSLDemoViewController`; only the
/// pageName differs. See that file's KDoc for the framework-name / KMP
/// registry preparation rationale.
final class ComposePageDemoViewController: UIViewController {

    private static let pageName = "ComposeSampleWithA2UI"

    private var delegator: KuiklyRenderViewControllerBaseDelegator?
    private let container = UIView()

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        // KMP-registry fan-out + @Page warm-up in one shot. See
        // `A2UIKuiklyRenderViewSupport` KDoc for the full rationale
        // behind the two-argument overload.
        A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
            pageName: Self.pageName,
            frameworkName: "A2UIDemo"
        )

        title = "[Compose] A2UI in Kuikly Page"
        view.backgroundColor = UIColor(red: 245/255.0, green: 245/255.0, blue: 245/255.0, alpha: 1.0)

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

    private func setupContainer() {
        container.translatesAutoresizingMaskIntoConstraints = false
        container.backgroundColor = UIColor(white: 0.96, alpha: 1.0)
        view.addSubview(container)
        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            container.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }

    private func setupDelegator() {
        let d = KuiklyRenderViewControllerBaseDelegator(
            pageName: Self.pageName,
            pageData: [:]
        )
        d.delegate = self
        delegator = d
        d.viewDidLoad(with: container)
    }
}

// MARK: - KuiklyRenderViewControllerBaseDelegatorDelegate

extension ComposePageDemoViewController: KuiklyRenderViewControllerBaseDelegatorDelegate {

    /// The Compose page lives in the same `:demo` module, so the
    /// framework name is the same as the DSL variant.
    func fetchContextCode(withPageName pageName: String,
                          resultCallback callback: @escaping KuiklyContextCodeCallback) {
        callback("A2UIDemo", nil)
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
