# a2ui_v10_tests · A2UI v1.0 协议回归样本集

本目录集中收录 A2UI **v1.0 严格模式**（`version: "1.0"`）的手工目视 / 解析回归样本，用于验证从 `commonMain` 的 `A2UIStreamParser` 一路到三端渲染层的端到端兼容性。所有样本都是**自包含**的 `{ "name", "description", "messages": [...] }` 结构，与现有 `a2ui_examples/` 一致。

- 加载方式：由 `A2UIDemoActivity` 的"A2UI v1.0 回归样本"卡片组入口统一列出，点击即可通过 `A2UISurfaceDSLView.writeStream(...)` 注入到 Kuikly DSL 组件树。
- 协议版本：所有样本的消息顶层都是 `"version": "1.0"`。
- 仅 Android：iOS / OHOS 测试宿主采用不同 payload 加载机制，对应用例分别内嵌在 `A2UISurfaceDemoSample.kt`（KMP demo）和 `Index.ets`（OHOS demo）里，本目录不对称复制。

---

## 样本一览

| 文件 | 覆盖 PR | 关键字段 / 场景 |
|---|---|---|
| `01_pr2_mixin_keys.json` | PR-2 Mixin keys | `version:"1.0"` · `@path` · `@call` · `@accessibility`（挂到组件顶层） |
| `02_pr3_createsurface_inline_datamodel.json` | PR-3 / PR-4 | `createSurface.dataModel` 内联初始数据 · surface 级 `catalogId` · List 内 `@index` |
| `03_pr6_rpc_bidirectional.json` | PR-6 双向 RPC | `callRendererFunction` 入站包 · `agentFunctionResponse` 出站回包 · 组件内 `action.callAgentFunction` |
| `04_pr8_multi_catalog.json` | PR-8 多 Catalog | surface 级 `catalogId` = basic + 组件级 `@catalogId` 切 custom_components · `@call.catalogId` 切 custom_functions |
| `05_pr9_small_fields_video_poster.json` | PR-9 小字段 | `Video.posterUrl` · `TextField.placeholder` · `Slider.steps` · `action.event.userMessage` |

## 命名规范

- 文件：`NN_prX_feature.json`，`NN` 两位数字 + `prX` 对应协议升级 PR 编号 + 简述。
- JSON 顶层：
  - `name`：Demo 入口卡片使用。
  - `description`：说明"该样本应看到 / 可验证什么"，供人肉目视断言。
  - `messages`：消息数组，每条 `{"version":"1.0", <envelope>}`。

## 回归方式

- **解析侧回归**：commonTest `A2UIV10SamplesParseTest` 会以字符串形式直接复用这 5 个样本的核心片段，经 `A2UIStreamParser` / `A2UIProtocolAdapter` 跑一遍，断言不崩溃、`onError` 不触发；其中 `createSurface_readsOfficialV10FieldNames` 正向断言 `catalogId` 与 `dataModel` 这两个**官方字段名**确实落到了 surface 配置与数据模型上。
- **目视回归**：真机跑 Demo App，依次打开 5 个入口，按 `description` 字段预期做人眼断言（PR-9 视频 cover 首帧退场、TextField placeholder 显示、Slider 吸附到四等分位、Button 按下触发 agent 事件 + userMessage）。

## 迁移速查

对应的协议迁移要点见 [`docs/A2UI_V10_MIGRATION.md`](../../../../../../docs/A2UI_V10_MIGRATION.md)。
