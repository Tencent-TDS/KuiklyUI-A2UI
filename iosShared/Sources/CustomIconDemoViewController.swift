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

/// Custom Icon Demo View Controller (NativePage integration).
///
/// iOS mirror of `androidAppForNativePage/CustomIconDemoActivity`. On create,
/// registers a small set of icons through `A2UISurfaceView.registerA2UIIcons`,
/// renders a pair of A2UI cards displaying both registered and unregistered
/// icon names, then unregisters on dealloc so other screens keep the
/// built-in unicode/emoji glyphs.
///
/// The rendering path here is *Native View* (an [A2UISurfaceView] per card)
/// rather than the KuiklyPage's KuiklyBaseView + Kuikly Compose/DSL page.
///
/// The two A2UI cards are hand-written as literal JSON strings (no runtime
/// JSON assembly), one pair of create/update envelopes each.
class CustomIconDemoViewController: UIViewController {

    // MARK: - JSON templates (mirrors CustomIconDemoActivity)

    private static let REGISTERED_ICONS: [String: String] = [
        "star":     "assets://a2ui_icons/star.png",
        "favorite": "assets://a2ui_icons/favorite.png",
        "home":     "assets://a2ui_icons/home.png",
        "settings": "assets://a2ui_icons/settings.png",
        "search":   "assets://a2ui_icons/search.png",
        A2UISurfaceView.iconFallbackKey: "assets://a2ui_icons/_fallback.png"
    ]

    // ==== Card A: 5 registered icons — hit KRImageView through REGISTERED_ICONS ====
    private static let REGISTERED_CREATE_SURFACE_JSON = """
    {
      "version": "v0.9",
      "createSurface": { "surfaceId": "native-icon-card-registered" }
    }
    """
    private static let REGISTERED_UPDATE_COMPONENTS_JSON = """
    {
      "version": "v0.9",
      "updateComponents": {
        "surfaceId": "native-icon-card-registered",
        "components": [
          { "id": "root",    "component": "Card", "child": "row" },
          { "id": "row",     "component": "Row",  "justify": "spaceAround", "align": "center",
            "children": ["icon-0", "icon-1", "icon-2", "icon-3", "icon-4"] },
          { "id": "icon-0",  "component": "Icon", "name": "star" },
          { "id": "icon-1",  "component": "Icon", "name": "favorite" },
          { "id": "icon-2",  "component": "Icon", "name": "home" },
          { "id": "icon-3",  "component": "Icon", "name": "settings" },
          { "id": "icon-4",  "component": "Icon", "name": "search" }
        ]
      }
    }
    """

    // ==== Card B: 3 unregistered names — all fall back to _fallback.png ====
    private static let UNREGISTERED_CREATE_SURFACE_JSON = """
    {
      "version": "v0.9",
      "createSurface": { "surfaceId": "native-icon-card-unregistered" }
    }
    """
    private static let UNREGISTERED_UPDATE_COMPONENTS_JSON = """
    {
      "version": "v0.9",
      "updateComponents": {
        "surfaceId": "native-icon-card-unregistered",
        "components": [
          { "id": "root",    "component": "Card", "child": "row" },
          { "id": "row",     "component": "Row",  "justify": "spaceAround", "align": "center",
            "children": ["icon-0", "icon-1", "icon-2"] },
          { "id": "icon-0",  "component": "Icon", "name": "camera" },
          { "id": "icon-1",  "component": "Icon", "name": "delete" },
          { "id": "icon-2",  "component": "Icon", "name": "edit" }
        ]
      }
    }
    """

    // MARK: - State

    /// One A2UISurfaceView per card; kept for `destroy()` at dealloc time.
    private var surfaceViews: [A2UISurfaceView] = []

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        // Register the custom icons *before* the surfaces initialise.
        // A2UIIconRegistry is a process-global singleton on the Kotlin side,
        // so `registerA2UIIcons` here matches the Android variant one-for-one.
        A2UISurfaceView.registerA2UIIcons(Self.REGISTERED_ICONS)

        view.backgroundColor = UIColor(red: 0.96, green: 0.96, blue: 0.96, alpha: 1.0)
        title = "自定义 Icon 图片"
        buildLayout()
    }

    deinit {
        surfaceViews.forEach { $0.destroy() }
        surfaceViews.removeAll()
        // Roll back the global registration so other screens keep the built-in
        // unicode/emoji glyphs. Mirrors the Android CustomIconDemoActivity.
        for name in Self.REGISTERED_ICONS.keys {
            A2UISurfaceView.unregisterA2UIIcon(name: name)
        }
    }

    // MARK: - Layout

    private func buildLayout() {
        // Header banner (mirrors #D81B60 header in Android variant).
        let header = UIView()
        header.backgroundColor = UIColor(red: 0.85, green: 0.11, blue: 0.38, alpha: 1.0)
        header.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(header)

        let headerTitle = UILabel()
        headerTitle.text = "自定义 Icon 图片"
        headerTitle.font = .boldSystemFont(ofSize: 20)
        headerTitle.textColor = .white
        headerTitle.translatesAutoresizingMaskIntoConstraints = false
        header.addSubview(headerTitle)

        // count-of-icons-minus-fallback, exactly like the Kotlin variant.
        let registeredCount = Self.REGISTERED_ICONS.count - 1
        let headerSubtitle = UILabel()
        headerSubtitle.text = "宿主：Native UIViewController + A2UISurfaceView\n已注册 \(registeredCount) 个 Icon + _fallback，未注册的名称均落到 _fallback"
        headerSubtitle.font = .systemFont(ofSize: 11)
        headerSubtitle.textColor = UIColor(red: 0.99, green: 0.89, blue: 0.93, alpha: 1.0)
        headerSubtitle.numberOfLines = 2
        headerSubtitle.translatesAutoresizingMaskIntoConstraints = false
        header.addSubview(headerSubtitle)

        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.alwaysBounceVertical = true
        view.addSubview(scrollView)

        let cardsContainer = UIStackView()
        cardsContainer.axis = .vertical
        cardsContainer.spacing = 0
        cardsContainer.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(cardsContainer)

        NSLayoutConstraint.activate([
            header.topAnchor.constraint(equalTo: view.topAnchor),
            header.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            header.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            headerTitle.topAnchor.constraint(equalTo: header.safeAreaLayoutGuide.topAnchor, constant: 12),
            headerTitle.leadingAnchor.constraint(equalTo: header.leadingAnchor, constant: 24),

            headerSubtitle.topAnchor.constraint(equalTo: headerTitle.bottomAnchor, constant: 4),
            headerSubtitle.leadingAnchor.constraint(equalTo: header.leadingAnchor, constant: 24),
            headerSubtitle.trailingAnchor.constraint(equalTo: header.trailingAnchor, constant: -24),
            headerSubtitle.bottomAnchor.constraint(equalTo: header.bottomAnchor, constant: -16),

            scrollView.topAnchor.constraint(equalTo: header.bottomAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),

            cardsContainer.topAnchor.constraint(equalTo: scrollView.topAnchor, constant: 8),
            cardsContainer.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor, constant: 12),
            cardsContainer.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor, constant: -12),
            cardsContainer.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor, constant: -12),
            cardsContainer.widthAnchor.constraint(equalTo: scrollView.widthAnchor, constant: -24)
        ])

        addIconCard(
            into: cardsContainer,
            title: "已注册 Icon（走 KRImageView）",
            description: "star / favorite / home / settings / search 均命中 REGISTERED_ICONS",
            createJson: Self.REGISTERED_CREATE_SURFACE_JSON,
            updateJson: Self.REGISTERED_UPDATE_COMPONENTS_JSON
        )
        addIconCard(
            into: cardsContainer,
            title: "未注册 Icon（走 _fallback）",
            description: "camera / delete / edit 三个名称未注册，均渲染为 _fallback.png",
            createJson: Self.UNREGISTERED_CREATE_SURFACE_JSON,
            updateJson: Self.UNREGISTERED_UPDATE_COMPONENTS_JSON
        )
    }

    private func addIconCard(into parent: UIStackView,
                             title: String,
                             description: String,
                             createJson: String,
                             updateJson: String) {
        let titleView = UILabel()
        titleView.text = title
        titleView.font = .boldSystemFont(ofSize: 14)
        titleView.textColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 1.0)
        parent.addArrangedSubview(titleView)
        parent.setCustomSpacing(2, after: titleView)

        let descView = UILabel()
        descView.text = description
        descView.font = .systemFont(ofSize: 12)
        descView.textColor = UIColor(red: 0.53, green: 0.53, blue: 0.53, alpha: 1.0)
        descView.numberOfLines = 0
        parent.addArrangedSubview(descView)
        parent.setCustomSpacing(6, after: descView)

        let cardWrap = UIView()
        cardWrap.backgroundColor = .white
        cardWrap.layer.cornerRadius = 8
        cardWrap.layer.masksToBounds = true
        cardWrap.translatesAutoresizingMaskIntoConstraints = false

        let surface = A2UISurfaceView()
        surface.translatesAutoresizingMaskIntoConstraints = false
        cardWrap.addSubview(surface)
        NSLayoutConstraint.activate([
            surface.topAnchor.constraint(equalTo: cardWrap.topAnchor),
            surface.leadingAnchor.constraint(equalTo: cardWrap.leadingAnchor),
            surface.trailingAnchor.constraint(equalTo: cardWrap.trailingAnchor),
            surface.bottomAnchor.constraint(equalTo: cardWrap.bottomAnchor)
        ])
        parent.addArrangedSubview(cardWrap)
        parent.setCustomSpacing(10, after: cardWrap)

        surface.initSurface(configJson: "{}")
        surface.surfaceListener = self
        surface.writeStream(createJson)
        surface.writeStream(updateJson)
        surface.finishStream()
        surfaceViews.append(surface)
    }
}

// MARK: - A2UISurfaceViewDelegate

extension CustomIconDemoViewController: A2UISurfaceViewDelegate {

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didReceiveAction action: String, fromTag tag: Int, inSurface surfaceId: String) {
        print("[CustomIconDemo] didReceiveAction surfaceId=\(surfaceId) tag=\(tag) action=\(action)")
    }

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didEncounterError message: String) {
        print("[CustomIconDemo] didEncounterError message=\(message)")
    }
}
