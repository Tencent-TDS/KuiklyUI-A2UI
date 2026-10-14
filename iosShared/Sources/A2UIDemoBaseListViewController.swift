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

/// A single row on the A2UI demo main list.
public struct A2UIDemoEntry {
    public let title: String
    public let description: String
    public let icon: String
    public let color: UIColor
    public let build: () -> UIViewController

    public init(title: String,
                description: String,
                icon: String,
                color: UIColor,
                build: @escaping () -> UIViewController) {
        self.title = title
        self.description = description
        self.icon = icon
        self.color = color
        self.build = build
    }
}

/// A grouped section of demo entries on the main list.
public struct A2UIDemoSection {
    public let title: String
    public let entries: [A2UIDemoEntry]

    public init(title: String, entries: [A2UIDemoEntry]) {
        self.title = title
        self.entries = entries
    }
}

/// UITableViewCell used by every iosAppFor* main list. Kept in iosShared so
/// each app-specific `A2UIDemoListViewController` reuses the same look.
class A2UIDemoEntryCell: UITableViewCell {

    private let iconLabel = UILabel()
    private let titleLabel = UILabel()
    private let descLabel = UILabel()
    private let iconBackground = UIView()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupViews()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        accessoryType = .disclosureIndicator

        iconBackground.layer.cornerRadius = 20
        iconBackground.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(iconBackground)

        iconLabel.font = .systemFont(ofSize: 22)
        iconLabel.textAlignment = .center
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        iconBackground.addSubview(iconLabel)

        titleLabel.font = .boldSystemFont(ofSize: 16)
        titleLabel.textColor = .label
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(titleLabel)

        descLabel.font = .systemFont(ofSize: 13)
        descLabel.textColor = .secondaryLabel
        descLabel.numberOfLines = 2
        descLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(descLabel)

        NSLayoutConstraint.activate([
            iconBackground.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            iconBackground.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            iconBackground.widthAnchor.constraint(equalToConstant: 40),
            iconBackground.heightAnchor.constraint(equalToConstant: 40),

            iconLabel.centerXAnchor.constraint(equalTo: iconBackground.centerXAnchor),
            iconLabel.centerYAnchor.constraint(equalTo: iconBackground.centerYAnchor),

            titleLabel.leadingAnchor.constraint(equalTo: iconBackground.trailingAnchor, constant: 12),
            titleLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            titleLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 16),

            descLabel.leadingAnchor.constraint(equalTo: titleLabel.leadingAnchor),
            descLabel.trailingAnchor.constraint(equalTo: titleLabel.trailingAnchor),
            descLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 4)
        ])
    }

    func configure(icon: String, title: String, description: String, color: UIColor) {
        iconLabel.text = icon
        titleLabel.text = title
        descLabel.text = description
        iconBackground.backgroundColor = color.withAlphaComponent(0.15)
    }
}

/// Base list view controller reused by every iosAppFor* target. Concrete
/// subclasses only provide the `sections` and (optionally) the header
/// subtitle / navigation title strings.
open class A2UIDemoBaseListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)
    private let headerSubtitleLabel = UILabel()

    /// Concrete subclasses override to declare their sections.
    open var sections: [A2UIDemoSection] { [] }
    /// Concrete subclasses override to customise the subtitle shown at
    /// the top of the table view.
    open var headerSubtitle: String { "" }
    /// Concrete subclasses override to customise the navigation bar title.
    open var listTitle: String { "A2UI Demo" }

    override open func viewDidLoad() {
        super.viewDidLoad()
        title = listTitle
        view.backgroundColor = .systemGroupedBackground
        setupTableView()
    }

    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(A2UIDemoEntryCell.self, forCellReuseIdentifier: "A2UIDemoEntryCell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])

        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 44))
        headerSubtitleLabel.text = headerSubtitle
        headerSubtitleLabel.font = .systemFont(ofSize: 14)
        headerSubtitleLabel.textColor = .secondaryLabel
        headerSubtitleLabel.numberOfLines = 0
        headerSubtitleLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(headerSubtitleLabel)
        NSLayoutConstraint.activate([
            headerSubtitleLabel.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 20),
            headerSubtitleLabel.trailingAnchor.constraint(equalTo: headerView.trailingAnchor, constant: -20),
            headerSubtitleLabel.bottomAnchor.constraint(equalTo: headerView.bottomAnchor, constant: -8)
        ])
        tableView.tableHeaderView = headerView
    }

    // MARK: - UITableViewDataSource

    public func numberOfSections(in tableView: UITableView) -> Int { sections.count }
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { sections[section].entries.count }
    public func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? { sections[section].title }

    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "A2UIDemoEntryCell", for: indexPath) as! A2UIDemoEntryCell
        let entry = sections[indexPath.section].entries[indexPath.row]
        cell.configure(icon: entry.icon, title: entry.title, description: entry.description, color: entry.color)
        return cell
    }

    // MARK: - UITableViewDelegate

    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let vc = sections[indexPath.section].entries[indexPath.row].build()
        navigationController?.pushViewController(vc, animated: true)
    }

    // Give the two-line description enough breathing room so it never hugs the
    // bottom edge of the cell. Title ~20pt + gap 4pt + 2 lines desc (~31pt) +
    // top/bottom padding 16pt each ≈ 87pt, so 92pt leaves ~5pt of slack.
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat { 92 }
}
