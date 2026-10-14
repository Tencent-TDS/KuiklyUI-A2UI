# a2ui_theme_tests · A2UI v0.9 主题（createSurface.theme）测试用例集

本目录集中收录 A2UI v0.9 协议 `createSurface.theme` 相关能力的**手工目视测试用例**，同时作为主题字段消费点的**回归护栏**。用例以纯 JSON 形式存放，双端（Android / iOS）通过 Demo App 的"主题测试"入口自动加载并渲染。

- Android 入口：`ThemeDemoActivity`（`androidApp/src/main/java/com/tencent/kuikly/a2ui/demo/ThemeDemoActivity.kt`）
- iOS 入口：`ThemeDemoViewController`（`iosApp/Sources/ThemeDemoViewController.swift`）
- 用例目录必须双端同步（`androidApp/src/main/assets/a2ui_theme_tests/` ↔ `iosApp/Resources/a2ui_theme_tests/`）

---

## 目录结构

```
a2ui_theme_tests/
├── 01_primary_color_basic/        主题色一次性染色多组件
├── 02_agent_identity/             iconUrl + agentDisplayName（镜像目视）
├── 03_theme_all_fields/           三字段全填 + 自定义扩展（additionalProperties=true）
├── 04_invalid_color_fallback/     primaryColor 非法值回退默认 + 正向对照
├── 05_default_no_theme/           createSurface 不带 theme（默认 #00BFFF）
├── 06_extreme_colors/             极端色（纯黑 / 纯白）可读性目视
├── 07_button_variant_isolation/   仅 primary variant 应被染色（防误染回归）
└── 08_agent_identity_edge/        超长名称 / emoji / data URI icon 边界
```

## 命名与编号规范

- 目录：`NN_snake_case_capability_name/`，`NN` 为两位数字，与 Demo 入口清单顺序一致。
- 文件：`M_lower_snake_case.json`，`M` 为该目录内序号（从 1 开始）。
- JSON 顶层：
  - `name`：入口卡片和详情标题使用，简洁一句话。
  - `description`：说明"应看到什么"以及"为什么"，便于目视断言。
  - `messages`：一至多条 `createSurface` / `updateComponents` / `updateDataModel` 消息，模拟真实 orchestrator 流。

---

## 引擎实际生效点清单（v0.9）

以下是 KuiklyUI 引擎当前**实际消费** `theme.primaryColor` 的所有位置。用例设计围绕这些点展开。

| 组件 / 属性 | 生效路径 | 备注 |
| :-- | :-- | :-- |
| `Button` 背景 | `variant=primary` 时通过 `A2UIProtocolAdapter` 注入 backgroundColor | `default` / `borderless` 不被染色 |
| `Slider progressColor` | `A2UIProtocolAdapter` 未显式指定 `progressColor` 时注入主色 | trackColor / thumbColor 不受主色影响 |
| `Tabs` 选中态 | 选中 tab 的下划线 / 前景色使用主色 | 未选中态保持中性 |
| `ChoicePicker` 选中态 | chip 背景 / checkbox 勾选态使用主色 | — |
| `CheckBox` 勾色 | `A2UIApplier` 通过 `__primaryColor` 注入到 `checkedViewCreator` 内的 "☑" 字符 `color(...)` | 未选中态使用固定灰色 |

**未内置消费**（当前版本）：
- `theme.iconUrl`
- `theme.agentDisplayName`

这两个字段在协议中定义为"agent 身份归因"用途。引擎不自动展示，需业务侧通过 orchestrator 将其写入 Data Model，再由 UI 组件（`Image` / `Text`）绑定路径显示。02 / 08 目录的用例均采用此方式做目视验证。

---

## 非法值回退契约（04 目录背景）

`A2UIProtocolAdapter` 在处理 `createSurface.theme.primaryColor` 时对字符串做正则校验：

```regex
^#[0-9a-fA-F]{6}$
```

不匹配则**保留 adapter 内初值** `#00BFFF`，不会写入 surface。因此 04 目录用例的目视断言为：**Primary Button 背景应为浅蓝 #00BFFF**，而非用户传入的非法字符串或黑色/透明色。

已覆盖的非法输入：
- 非 hex 字符串（`invalid-color`）
- 3 位缩写 hex（`#F0F`）
- 8 位 hex 含 alpha（`#FF00FFAA`）
- 缺失 `#` 前缀（`E91E63`）
- 非 hex 字符（`#GGGGGG`）
- 空字符串（`""`）

作为对照，`7_uppercase_hex_accepted.json` 传入合法大写 hex `#E91E63`，应正常生效为品红。

---

## 使用方式

### 编写新用例
1. 在合适编号的目录下新建 `M_short_name.json`。
2. `messages` 至少包含一条 `createSurface`。若涉及数据绑定，追加 `updateComponents` + `updateDataModel`。
3. **双端同步**：将文件复制到 `iosApp/Resources/a2ui_theme_tests/` 对应目录（iOS 侧通过 folder reference 打包，不需要改 xcodeproj）。可用：
   ```sh
   rsync -a --delete a2ui/androidApp/src/main/assets/a2ui_theme_tests/ \
                      a2ui/iosApp/Resources/a2ui_theme_tests/
   ```
4. 若目录是**新增**的（如未来的 `09_xxx/`），需要同时更新两处入口清单：
   - Android：`ThemeDemoActivity.kt` 的 `groups` 列表
   - iOS：`ThemeDemoViewController.swift` 的 `sections` 数组

### 目视验证
- Android：运行 `androidApp`，主页点击"主题测试"进入本目录列表。
- iOS：运行 `iosApp`，主页点击"主题测试"进入本目录列表。
- 每个用例的 `description` 描述了预期观感，逐条点击目视比对。

---

## 未来可扩展方向（暂未实现）

- Golden screenshot 视觉基线 + CI 差分：目前依赖人工目视，尚未接入快照测试框架。
- 多 surface 隔离用例：需先在测试运行器中支持同一测试文件内 `createSurface` 多个 surface 并同时展示。
- Orchestrator 身份归因端到端示例：让 `iconUrl` / `agentDisplayName` 通过 Data Model 或组件属性绑定被业务侧真实消费。
