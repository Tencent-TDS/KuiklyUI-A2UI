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

/// Placeholder view controller for demo entries whose hosting infrastructure
/// hasn't landed yet. Displays a titled banner explaining what will eventually
/// live at this route.
///
/// As of the Phase-2 completion of the iOS 4-app split, no shipping entry uses
/// this class — every route is now backed by a real implementation. The class
/// is kept in `iosShared` so future in-progress features can temporarily wire
/// a stub without adding another UI helper.
open class A2UIComingSoonViewController: UIViewController {

    private let bannerTitle: String
    private let bannerBody: String

    public init(title: String, body: String) {
        self.bannerTitle = title
        self.bannerBody = body
        super.init(nibName: nil, bundle: nil)
        self.title = title
    }

    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemGroupedBackground

        let iconLabel = UILabel()
        iconLabel.text = "🚧"
        iconLabel.font = .systemFont(ofSize: 72)
        iconLabel.textAlignment = .center
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(iconLabel)

        let titleLabel = UILabel()
        titleLabel.text = bannerTitle
        titleLabel.font = .boldSystemFont(ofSize: 22)
        titleLabel.textAlignment = .center
        titleLabel.numberOfLines = 0
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(titleLabel)

        let phaseLabel = UILabel()
        phaseLabel.text = "Coming Soon"
        phaseLabel.font = .systemFont(ofSize: 15, weight: .medium)
        phaseLabel.textAlignment = .center
        phaseLabel.textColor = UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0)
        phaseLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(phaseLabel)

        let bodyLabel = UILabel()
        bodyLabel.text = bannerBody
        bodyLabel.font = .systemFont(ofSize: 14)
        bodyLabel.textColor = .secondaryLabel
        bodyLabel.textAlignment = .center
        bodyLabel.numberOfLines = 0
        bodyLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(bodyLabel)

        NSLayoutConstraint.activate([
            iconLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            iconLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor, constant: -100),

            titleLabel.topAnchor.constraint(equalTo: iconLabel.bottomAnchor, constant: 16),
            titleLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 24),
            titleLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -24),

            phaseLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 8),
            phaseLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 24),
            phaseLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -24),

            bodyLabel.topAnchor.constraint(equalTo: phaseLabel.bottomAnchor, constant: 24),
            bodyLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 32),
            bodyLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -32)
        ])
    }
}
