import UIKit

final class A2UIAssetBrowserViewController: A2UIDemoBaseListViewController {
    private let directory: String

    init(directory: String) {
        precondition(["v0_9", "v1_0"].contains(directory.components(separatedBy: "/").first ?? ""))
        precondition(!directory.components(separatedBy: "/").contains(where: { $0 == ".." || $0.isEmpty }))
        self.directory = directory
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var listTitle: String { directory.hasPrefix("v0_9") ? "0.9 兼容回归" : "1.0 回归样本" }
    override var headerSubtitle: String { directory }
    override var listRowHeight: CGFloat { 72 }
    override var cellDescriptionLines: Int { 1 }

    override var sections: [A2UIDemoSection] {
        guard let resourcePath = Bundle.main.resourcePath else { return [] }
        let path = (resourcePath as NSString).appendingPathComponent(directory)
        let manager = FileManager.default
        let children = (try? manager.contentsOfDirectory(atPath: path))?.sorted() ?? []
        var entries: [A2UIDemoEntry] = []
        for child in children {
            var isDirectory: ObjCBool = false
            let childPath = (path as NSString).appendingPathComponent(child)
            guard manager.fileExists(atPath: childPath, isDirectory: &isDirectory), isDirectory.boolValue else { continue }
            let nextDirectory = "\(directory)/\(child)"
            let hasCases = ((try? manager.contentsOfDirectory(atPath: childPath)) ?? []).contains { $0.hasSuffix(".json") }
            entries.append(A2UIDemoEntry(title: child, description: nextDirectory, icon: "›", color: .systemGray,
                                         build: {
                if hasCases {
                    return A2UIComponentTestViewController(componentDir: "", componentName: child,
                                                          rootDir: nextDirectory, titleSuffix: "回归样本")
                }
                return A2UIAssetBrowserViewController(directory: nextDirectory)
            }))
        }
        return [A2UIDemoSection(title: entries.isEmpty ? "暂无样本" : "样本目录", entries: entries)]
    }
}
