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

/// Custom Icon Demo View Controller — **KuiklyPage variant**.
///
/// iOS mirror of `androidAppForKuiklyPage/CustomIconDemoActivity`. On create,
/// registers a curated set of icons through `A2UISurfaceView.registerA2UIIcons`
/// so every subsequent A2UI Icon component in this process renders via
/// `KRImageView` instead of the framework's built-in unicode/emoji glyph.
///
/// Unlike its Android sibling this VC hosts a `KuiklyRenderViewController-
/// BaseDelegator` bound to `@Page("A2UICaseList")` — same page used by
/// `A2UIComponentTestKuiklyPageViewController`. The `cases` array is
/// **assembled in-memory** here (no bundle assets involved) and passed to
/// the Kuikly page via `pageData`.
///
/// On dealloc, the registration is rolled back so other screens keep the
/// built-in glyphs. Same lifecycle contract as
/// `iosShared/CustomIconDemoViewController` (the NativePage variant).
final class CustomIconKuiklyDemoViewController: UIViewController {

    private static let pageName = "A2UICaseList"
    private static let frameworkName = "A2UIDemo"

    /// Same set as the Android CustomIconDemoActivity — 5 icons + fallback.
    private static let REGISTERED_ICONS: [String: String] = [
        "star":     "assets://a2ui_icons/star.png",
        "favorite": "assets://a2ui_icons/favorite.png",
        "home":     "assets://a2ui_icons/home.png",
        "settings": "assets://a2ui_icons/settings.png",
        "search":   "assets://a2ui_icons/search.png",
        A2UISurfaceView.iconFallbackKey: "assets://a2ui_icons/_fallback.png"
    ]

    private var delegator: KuiklyRenderViewControllerBaseDelegator?
    private let container = UIView()
    private let headerView = UIView()

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        // Register icons before the Kuikly page mounts so the very first
        // Icon layout in A2UICaseList resolves against them.
        A2UISurfaceView.registerA2UIIcons(Self.REGISTERED_ICONS)

        // KMP-registry fan-out + @Page warm-up for A2UICaseList in one
        // call. The icon registration above goes through the KMP bridge
        // and may flip `BridgeManager.isDidInit()` to `true`; the
        // two-argument overload makes sure Kuikly's generated entry
        // still runs `triggerRegisterPages()` so `createPager` succeeds.
        A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
            pageName: Self.pageName,
            frameworkName: Self.frameworkName
        )

        title = "自定义 Icon 图片"
        view.backgroundColor = UIColor(red: 245/255.0, green: 245/255.0, blue: 245/255.0, alpha: 1.0)

        setupHeader()
        setupContainer()
        setupDelegator()
    }

    deinit {
        // Roll back the global registration so other screens keep the
        // built-in unicode/emoji glyphs. Mirrors CustomIconDemoActivity.
        for name in Self.REGISTERED_ICONS.keys {
            A2UISurfaceView.unregisterA2UIIcon(name: name)
        }
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

    private func setupHeader() {
        headerView.translatesAutoresizingMaskIntoConstraints = false
        headerView.backgroundColor = UIColor(red: 0.85, green: 0.11, blue: 0.38, alpha: 1.0) // #D81B60
        view.addSubview(headerView)

        let title = UILabel()
        title.text = "自定义 Icon 图片"
        title.font = .boldSystemFont(ofSize: 20)
        title.textColor = .white
        title.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(title)

        let registeredCount = Self.REGISTERED_ICONS.count - 1
        let subtitle = UILabel()
        subtitle.text = "宿主：Kuikly Page（A2UICaseList）\n已注册 \(registeredCount) 个 Icon + _fallback，未注册的名称均落到 _fallback"
        subtitle.font = .systemFont(ofSize: 11)
        subtitle.textColor = UIColor(red: 0.99, green: 0.89, blue: 0.93, alpha: 1.0)
        subtitle.numberOfLines = 2
        subtitle.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(subtitle)

        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            headerView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            headerView.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            title.topAnchor.constraint(equalTo: headerView.topAnchor, constant: 12),
            title.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 24),

            subtitle.topAnchor.constraint(equalTo: title.bottomAnchor, constant: 4),
            subtitle.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 24),
            subtitle.trailingAnchor.constraint(equalTo: headerView.trailingAnchor, constant: -24),
            subtitle.bottomAnchor.constraint(equalTo: headerView.bottomAnchor, constant: -16),
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
                "title": "自定义 Icon 图片",
                "cases": casesJson
            ]
        )
        d.delegate = self
        delegator = d
        d.viewDidLoad(with: container)
    }

    // MARK: - Cases assembly (in-memory, no bundle assets)

    /// Produce the JSON array string consumed by
    /// `A2UICaseListPager.parseCases()`. Two cases:
    ///  1. 5 registered icons — all rendered via KRImageView through REGISTERED_ICONS.
    ///  2. 3 unregistered names — all fall back to `_fallback.png`.
    private func buildCasesJson() -> String {
        let cases: [[String: Any]] = [
            buildIconGridCase(
                name: "已注册 Icon（走 KRImageView）",
                description: "star / favorite / home / settings / search 均命中 REGISTERED_ICONS",
                iconNames: ["star", "favorite", "home", "settings", "search"]
            ),
            buildIconGridCase(
                name: "未注册 Icon（走 _fallback）",
                description: "camera / delete / edit 三个名称未注册，均渲染为 _fallback.png",
                iconNames: ["camera", "delete", "edit"]
            )
        ]
        guard let data = try? JSONSerialization.data(withJSONObject: cases),
              let str = String(data: data, encoding: .utf8) else {
            return "[]"
        }
        return str
    }

    /// Build one A2UICaseList case object whose surface contains a Row of
    /// Icon components — same envelope shape as
    /// `CustomIconDemoActivity.buildIconGridCase(...)` on Android.
    private func buildIconGridCase(name: String,
                                   description: String,
                                   iconNames: [String]) -> [String: Any] {
        let surfaceId = "icon-case-\(abs(name.hashValue))"
        var components: [[String: Any]] = []
        var iconChildren: [String] = []

        components.append([
            "id": "root",
            "component": "Card",
            "children": ["row"]
        ])

        for (index, iconName) in iconNames.enumerated() {
            let id = "icon-\(index)"
            iconChildren.append(id)
            components.append([
                "id": id,
                "component": "Icon",
                "name": iconName,
                "size": 48
            ])
        }

        components.append([
            "id": "row",
            "component": "Row",
            "children": iconChildren,
            "justifyContent": "spaceAround",
            "alignItems": "center",
            "padding": 16
        ])

        // Restore the {root, row, icons...} order to match Android's writer.
        // (Row was appended last only to reference the fully-built iconChildren
        // array; reorder before emitting so the JSON reads naturally.)
        let rootObj = components.removeFirst()
        let rowObj = components.removeLast()
        var ordered: [[String: Any]] = [rootObj, rowObj]
        ordered.append(contentsOf: components)

        let messages: [[String: Any]] = [
            [
                "version": "v0.9",
                "createSurface": ["surfaceId": surfaceId]
            ],
            [
                "version": "v0.9",
                "updateComponents": [
                    "surfaceId": surfaceId,
                    "components": ordered
                ]
            ]
        ]

        return [
            "name": name,
            "description": description,
            "messages": messages
        ]
    }
}

// MARK: - KuiklyRenderViewControllerBaseDelegatorDelegate

extension CustomIconKuiklyDemoViewController: KuiklyRenderViewControllerBaseDelegatorDelegate {

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
