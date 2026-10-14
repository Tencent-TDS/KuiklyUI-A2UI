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

/// Component Demo View Controller.
/// Displays a list of A2UI v0.9 protocol standard components (pure native UI, no A2UI rendering).
class ComponentDemoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private struct ComponentInfo {
        let name: String
        let description: String
        let icon: String
        let color: UIColor
    }

    private let components: [ComponentInfo] = [
        ComponentInfo(name: "Text", description: "文本组件 - 支持文本样式、variant、数据绑定", icon: "📝", color: UIColor(red: 0.91, green: 0.30, blue: 0.24, alpha: 1.0)),
        ComponentInfo(name: "Image", description: "图片组件 - 支持网络图片、fit模式", icon: "🖼️", color: UIColor(red: 0.15, green: 0.68, blue: 0.38, alpha: 1.0)),
        ComponentInfo(name: "Icon", description: "图标组件 - 支持标准图标名称和SVG路径", icon: "⭐", color: UIColor(red: 0.61, green: 0.35, blue: 0.71, alpha: 1.0)),
        ComponentInfo(name: "Video", description: "视频组件 - 视频播放能力", icon: "🎬", color: UIColor(red: 0.09, green: 0.63, blue: 0.52, alpha: 1.0)),
        ComponentInfo(name: "AudioPlayer", description: "音频播放组件 - 音频播放能力", icon: "🎵", color: UIColor(red: 0.17, green: 0.24, blue: 0.31, alpha: 1.0)),
        ComponentInfo(name: "Row", description: "水平布局 - 子组件水平排列，支持justify/align", icon: "↔️", color: UIColor(red: 0.29, green: 0.56, blue: 0.85, alpha: 1.0)),
        ComponentInfo(name: "Column", description: "垂直布局 - 子组件垂直排列，支持justify/align", icon: "↕️", color: UIColor(red: 0.20, green: 0.60, blue: 0.86, alpha: 1.0)),
        ComponentInfo(name: "List", description: "列表组件 - 可滚动列表，支持水平/垂直方向", icon: "📋", color: UIColor(red: 0.95, green: 0.61, blue: 0.07, alpha: 1.0)),
        ComponentInfo(name: "Card", description: "卡片组件 - 带圆角和阴影的容器", icon: "🃏", color: UIColor(red: 0.90, green: 0.49, blue: 0.13, alpha: 1.0)),
        ComponentInfo(name: "Tabs", description: "标签页组件 - 多标签切换", icon: "📑", color: UIColor(red: 0.20, green: 0.29, blue: 0.37, alpha: 1.0)),
        ComponentInfo(name: "Modal", description: "弹窗组件 - 模态弹窗展示", icon: "💬", color: UIColor(red: 0.17, green: 0.24, blue: 0.31, alpha: 1.0)),
        ComponentInfo(name: "Divider", description: "分割线组件 - 水平/垂直分割线", icon: "➖", color: UIColor(red: 0.58, green: 0.65, blue: 0.65, alpha: 1.0)),
        ComponentInfo(name: "Button", description: "按钮组件 - 可点击，支持action", icon: "🔘", color: UIColor(red: 0.42, green: 0.36, blue: 0.91, alpha: 1.0)),
        ComponentInfo(name: "TextField", description: "输入框组件 - 文本输入，支持label/value", icon: "⌨️", color: UIColor(red: 0.10, green: 0.74, blue: 0.61, alpha: 1.0)),
        ComponentInfo(name: "CheckBox", description: "复选框组件 - 布尔值选择", icon: "☑️", color: UIColor(red: 0.56, green: 0.27, blue: 0.68, alpha: 1.0)),
        ComponentInfo(name: "ChoicePicker", description: "选择器组件 - 单选/多选列表", icon: "🔽", color: UIColor(red: 0.83, green: 0.33, blue: 0.0, alpha: 1.0)),
        ComponentInfo(name: "Slider", description: "滑块组件 - 数值范围选择", icon: "🎚️", color: UIColor(red: 0.15, green: 0.68, blue: 0.38, alpha: 1.0)),
        ComponentInfo(name: "DateTimeInput", description: "日期时间组件 - 日期/时间选择", icon: "📅", color: UIColor(red: 0.75, green: 0.22, blue: 0.17, alpha: 1.0))
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "组件展示"
        view.backgroundColor = .systemGroupedBackground

        setupTableView()
    }

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
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 44))
        let headerLabel = UILabel()
        headerLabel.text = "A2UI v0.9 标准协议支持的 18 个组件"
        headerLabel.font = .systemFont(ofSize: 14)
        headerLabel.textColor = .secondaryLabel
        headerLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(headerLabel)
        NSLayoutConstraint.activate([
            headerLabel.leadingAnchor.constraint(equalTo: headerView.leadingAnchor, constant: 20),
            headerLabel.bottomAnchor.constraint(equalTo: headerView.bottomAnchor, constant: -8)
        ])
        tableView.tableHeaderView = headerView
    }

    // MARK: - UITableViewDataSource

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return components.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ComponentCell", for: indexPath) as! ComponentCell
        let comp = components[indexPath.row]
        cell.configure(icon: comp.icon, name: comp.name, description: comp.description, color: comp.color)
        return cell
    }

    // MARK: - UITableViewDelegate

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 72
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let comp = components[indexPath.row]
        let dir = componentDir(for: comp.name)
        if hasTestCases(in: dir) {
            let vc = A2UIComponentTestViewController(componentDir: dir, componentName: comp.name)
            navigationController?.pushViewController(vc, animated: true)
        } else {
            showToast(message: "\(comp.name) 暂未提供测试用例")
        }
    }

    // MARK: - Helpers

    /// Map a component display name (e.g. "Text", "AudioPlayer") to its bundle directory
/// under `Resources/a2ui_components/`. The mapping is the lower-cased component name.
    private func componentDir(for componentName: String) -> String {
        return componentName.lowercased()
    }

    /// Check whether at least one *.json test case exists for the given component directory.
    private func hasTestCases(in dir: String) -> Bool {
        guard let resourcePath = Bundle.main.resourcePath else { return false }
        let dirPath = (resourcePath as NSString)
.appendingPathComponent("a2ui_components")
            .appending("/\(dir)")
        var isDir: ObjCBool = false
        let fm = FileManager.default
        guard fm.fileExists(atPath: dirPath, isDirectory: &isDir), isDir.boolValue else {
            return false
        }
        guard let files = try? fm.contentsOfDirectory(atPath: dirPath) else { return false }
        return files.contains { $0.hasSuffix(".json") }
    }

    /// Lightweight toast-like notification using a transient label.
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
        // Add internal padding via attributedText spaces is fragile;
        // use a tiny container instead.
        toast.layoutMargins = UIEdgeInsets(top: 8, left: 12, bottom: 8, right: 12)
        UIView.animate(withDuration: 0.2, animations: { toast.alpha = 1 }) { _ in
            UIView.animate(withDuration: 0.25, delay: 1.4, options: [], animations: {
                toast.alpha = 0
            }, completion: { _ in toast.removeFromSuperview() })
        }
    }
}

// MARK: - ComponentCell

class ComponentCell: UITableViewCell {

    private let iconBackground = UIView()
    private let iconLabel = UILabel()
    private let nameLabel = UILabel()
    private let descLabel = UILabel()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupViews()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        selectionStyle = .none

        iconBackground.layer.cornerRadius = 18
        iconBackground.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(iconBackground)

        iconLabel.font = .systemFont(ofSize: 18)
        iconLabel.textAlignment = .center
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        iconBackground.addSubview(iconLabel)

        nameLabel.font = .boldSystemFont(ofSize: 15)
        nameLabel.textColor = .label
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(nameLabel)

        descLabel.font = .systemFont(ofSize: 12)
        descLabel.textColor = .secondaryLabel
        descLabel.numberOfLines = 2
        descLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(descLabel)

        NSLayoutConstraint.activate([
            iconBackground.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            iconBackground.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            iconBackground.widthAnchor.constraint(equalToConstant: 36),
            iconBackground.heightAnchor.constraint(equalToConstant: 36),

            iconLabel.centerXAnchor.constraint(equalTo: iconBackground.centerXAnchor),
            iconLabel.centerYAnchor.constraint(equalTo: iconBackground.centerYAnchor),

            nameLabel.leadingAnchor.constraint(equalTo: iconBackground.trailingAnchor, constant: 12),
            nameLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            nameLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 16),

            descLabel.leadingAnchor.constraint(equalTo: nameLabel.leadingAnchor),
            descLabel.trailingAnchor.constraint(equalTo: nameLabel.trailingAnchor),
            descLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4)
        ])
    }

    func configure(icon: String, name: String, description: String, color: UIColor) {
        iconLabel.text = icon
        nameLabel.text = name
        descLabel.text = description
        iconBackground.backgroundColor = color.withAlphaComponent(0.15)
    }
}
