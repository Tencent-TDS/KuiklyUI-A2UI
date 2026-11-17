# A2UI v1.0 协议缺口审计（渲染器侧）

> **性质**：只读分析报告，未改动任何实现代码（遵循 `KuiklyUI-A2UI` 冻结约定）。
>
> **基准**：官方仓库 `/Users/etkm/git_a2ui/a2ui`（Apache-2.0）的
> `conformance/features.yaml`（146 个 feature ID）与 `conformance/core/*.yaml`
> （867 个一致性用例）。**官方用例优先于本仓库自有 fixture**——自有用例只能证明"不崩溃"，
> 不能证明"符合协议"。
>
> **口径**：本渲染器是 adapter/renderer 层，不是 agent/processor 层。因此
> `F-AGT-*`（agent 侧）与 `F-CORE-*` 中属于 MessageProcessor 的严格校验语义，
> 只按"渲染器是否受影响"评级，不按"是否实现 agent 功能"评级。
>
> **审计日期**：2026-11-19

---

## 0. 结论摘要

| 评级 | 数量 | 说明 |
|---|---|---|
| **P0 阻断** | 3 | 协议要求渲染器主动产生报文 / 主动清理资源，当前**完全缺失**，agent 侧无法与之闭环 |
| **P1 语义不符** | 3 | 报文能收发，但参数或校验语义与官方 schema 不一致 |
| **P2 弱化** | 3 | 协议允许"可选"，缺失不导致错渲染，但会削弱互操作性 |
| **已清理** | 2 | 非 schema 字段名 `defaultCatalogId` / `initialDataModel` 与自造的 `catalogs`，均已删除 |
| **无缺口** | — | 18 个 Basic Catalog 组件、14 个内置函数均覆盖 |

**一句话**：组件与函数的"渲染广度"是齐的，缺的是**协议闭环能力**——`deleteSurface`、
`error` 报文、合规的 `action` 报文、以及 v1.0 严格校验骨架。

---

## 1. P0 · 阻断项

### P0-1 `deleteSurface` 完全未实现

- **协议要求**：`deleteSurface` 是 v1.0 envelope 的六种必选类型之一
  （`createSurface / updateComponents / updateDataModel / deleteSurface /
  callRendererFunction / agentFunctionResponse`）。渲染器必须销毁 surface 的组件树、
  释放资源，并允许同一 `surfaceId` 之后被重新创建。
- **现状**：`A2UIProtocolAdapter` 只识别前三种 + 两个 RPC envelope；
  `commonMain` 中 `deleteSurface` / `DELETE_SURFACE` 出现次数为 **0**。
  没有任何 JSON 消息类型、没有任何资源回收路径。
- **影响的官方用例**（部分）：
  - `message_processor_v1_0.yaml` → `test_update_components_on_deleted_surface_error`、
    `test_update_data_model_on_deleted_surface_error`、
    `test_delete_non_existent_surface_is_safe_noop`
  - `F-CORE-04`（deleteSurface handling and lifecycle cleanup）
  - `F-AGT-56`（agent 侧 deleteSurface + 同 id 重建的多轮生命周期）
- **后果**：agent 发出 `deleteSurface` 时被静默忽略，旧 surface 继续存活；
  同一 `surfaceId` 再次 `createSurface` 会与既有状态冲突（协议此处的正确行为是
  `INTEGRITY_ERROR`），而当前既不报错也不清理。长时间会话必然泄漏 surface
  和视图树。
- **建议**：在 `handleCreateSurfaceV10` 旁边新增 `handleDeleteSurface`，
  无条件清理 `idToTag` / `dataModel` / `childListBindings` / `tagToAction*` /
  `componentCatalogIds` 等全部 per-surface 状态，并 emit 一个 `DESTROY_SURFACE`
  类型给 DSL 层做视图回收。

### P0-2 出站 `error` 报文完全未实现

- **协议要求**：渲染器用顶层 `error` 报文向 agent 报告 surface 级校验/完整性错误，
  字段为 `code`（`VALIDATION_FAILED` / `INTEGRITY_ERROR` / `INVALID_FUNCTION_CALL` …，
  必填）、`message`（必填）、`surfaceId` 或 `functionCallId`（二者互斥）。
  这是 agent 侧"自我纠错"的唯一入口。
- **现状**：`VALIDATION_FAILED` / `INTEGRITY_ERROR` / `EXPRESSION_ERROR` 在
  `commonMain` 中出现次数均为 **0**。当前所有协议错误只走
  `println("[A2UI] error: ...")` 日志，对 agent 不可见。
- **影响的官方用例**：`error_responses.yaml` 全部 24 条、`F-CORE-26`、
  `F-AGT-58`（入站 error 报文的读取与自纠错格式化）。
- **后果**：agent 无法得知自己构造了非法 UI，只能靠"界面看起来不对"。
  这是与官方 processor 行为差距最大的一项。
- **建议**：定义一个统一的 `emitProtocolError(code, message, surfaceId?, functionCallId?)`
  出口，把现有散落的 `println("[A2UI] error: ...")` 逐处替换；`strictV10`
  下的拒绝分支应全部改为 emit `error` 后丢弃，而不是仅打日志。

### P0-3 出站 `action` 报文缺必填字段，无 v1.0 报文构造

- **协议要求**：v1.0 `action` 报文必填 `name`、`surfaceId`、**`sourceComponentId`**、
  **`timestamp`**（ISO 8601）、`context`；可选 `userMessage`、`metadata`。
- **现状**：`sourceComponentId` 出现次数 **0**；`userMessage` 出现次数 **0**；
  渲染器根本不构造 v1.0 `action` 报文——`getActionForTag(tag)` 把组件的 `action`
  子对象（含 `action.event.context`）原样序列化成字符串交给宿主，
  `A2UIMessageType.ACTION` 虽有枚举值但 `commonMain` 中**从未被构造**。
- **影响的官方用例**：`actions.yaml`（18 条）、`F-CORE-23`、`F-AGT-57`
  （agent 侧解析 v1.0 action 并格式化为模型输入）。
- **后果**：宿主拿到的是一段不带 `sourceComponentId` / `timestamp` 的自定义 JSON，
  agent 侧无法按官方 schema 解析，也无法区分事件来源组件。
- **建议**：新增 `buildActionEnvelope(componentId, actionMap): Map<String, Any?>`
  产出完整 v1.0 报文；`getActionForTag` 保留为兼容层，但新增
  `getActionEnvelopeForTag` 走新路径。`userMessage` 的 DynamicString 解析
  `resolveActionValue` 已经能做，只差把它提升到报文顶层。

---

## 2. P1 · 语义不符

### P1-1 `@index` 忽略 `offset` / `step` 参数

- **协议要求**：`@call: "@index"` 支持 `args: {offset, step}`，用于
  `${index(offset:1)}` 式的 1-based 编号。
- **现状**：`evaluateFunctionCall` 中 `@index` 分支直接返回
  `currentTemplateIndex.toString()`，**在读取 `args` 之前就 return 了**，
  `offset` / `step` 被静默丢弃。
- **官方用例**：`index_function.yaml` → `test_index_function_with_offset`、
  `test_index_function_nested_path`（期望 `1,2` 而当前会给 `0,1`）；
  `message_processor_v1_0.yaml` → `test_v10_call_in_index_args_is_checked`。
- **修复**：`@index` 分支移到 `args` 解析之后，按 `offset + index * step`
  计算（缺省 offset=0、step=1）。

### P1-2 `createSurface` 字段名（已彻底修正）

- 官方属性集合**只有** `surfaceId, catalogId, sendDataModel, components,
  dataModel, metadata`。此前代码读非 schema 的 `defaultCatalogId` /
  `initialDataModel`，因未知字段被静默忽略，样本仍然"通过"。
- **已修**：不保留兼容分支，直接只认官方名。
  - `A2UISurfaceConfig.defaultCatalogId` → 重命名为 `catalogId`，内部字段名与
    wire 名一致，不再有两套名字。
  - `A2UISurfaceConfig.catalogs`（allow-list，见 §4）**整个字段删除**。
  - `A2UIStreamParser.parseSurfaceConfig()` /
    `A2UIProtocolAdapter.handleCreateSurfaceV10()` 去掉全部 `?:` 回退读取。
  - 新增 `createSurface_readsSchemaFieldNames`（正向断言字段值真的落库）与
    `createSurface_ignoresNonSchemaAliases`（反向断言别名被忽略）两条测试。
- **依据**：该模块尚无线上业务接入，不存在需要兼容的已发布报文。

### P1-3 v1.0 严格校验骨架缺失（拓扑 / 命名 / 深度 / 参数 schema）

官方 `validator_v1_0.yaml`（50 条）与 `message_processor_v1_0.yaml` 要求的
以下校验在渲染器中**均无实现**：

| 校验类 | 官方用例示例 | 现状 |
|---|---|---|
| 循环引用 / 自引用 | `test_v10_topology_circular_reference_error` | 仅做"可达性遍历"，不报错 |
| 悬空 child 引用 | `test_v10_topology_dangling_child_reference_error` | 静默跳过 |
| 缺失 `root` | `test_v10_topology_missing_root_component_error` | 无 `root` 概念，任意无父组件即当根 |
| 孤儿组件 | `test_v10_topology_orphaned_component_error` | 按定义顺序兜底渲染，不报错 |
| 组件嵌套深度上限 | `test_v10_component_nesting_depth_limit_exceeded_error` | 无 |
| dataModel 嵌套深度上限 | `test_v10_data_model_nesting_depth_limit_exceeded_error` | 无 |
| 函数调用递归深度上限 | `test_v10_recursion_limit_function_call_depth_exceeded_error` | 无 |
| UAX 31 标识符校验 | `test_v10_uax31_invalid_identifier_error` | 无 |
| 未知组件属性 / 未知嵌套函数 | `test_v10_unknown_component_property`、`test_v10_unknown_nested_function` | 静默忽略 |
| 函数参数 schema 校验 | `test_v10_function_argument_schema_mismatch_error` | 无（customMain 不引 JSON Schema 解析器，是刻意的架构选择） |
| `@foo` 与 `foo` 同名共存 | `test_v10_validate_component_envelope_conflict_at_and_bare_error` | 有 `findUnknownAtKey`，不检测同名冲突 |

- **说明**：这是"渲染器 vs MessageProcessor"的定位差异，不是纯粹的实现缺陷。
  但其中**拓扑三项（循环/悬空/缺失 root）**会直接导致死循环或静默丢 UI，
  建议至少补齐这三项，并统一通过 P0-2 的 `error` 通道上报 `INTEGRITY_ERROR`。

### P1-4 `openUrl` 缺少 user-activation 传播

- **协议要求**：`openUrl` 是"需要用户手势激活"的函数；异步或非用户触发的调用
  应被拒绝（`F-BP-06`、`F-CORE-38`：`timeoutMs` / `maxPendingCalls` /
  `userActivation` 生命周期保障）。
- **现状**：`userActivation` 出现次数 **0**；`maxPendingCalls` **0**。
  `openUrl` 有实现（1 处引用），但无激活态校验。
- **后果**：agent 通过 `updateDataModel` 间接触发的 `openUrl` 会被直接执行，
  平台侧可能拦截（iOS/Android 均要求手势上下文），表现为"点了没反应"。

---

## 3. P2 · 弱化项

### P2-1 无 renderer capabilities 宣告

`getRendererCapabilities` / `inlineCatalogs` 均未实现（出现次数 0）。
官方 `F-CORE-30` 要求 v1.0 下渲染器以 `renderer_capabilities.json` schema
宣告自己支持的 catalog / 组件 / 函数，agent 侧据此裁剪 prompt。
缺失时 agent 只能盲发，容易下发不支持的组件。属"可选但强烈建议"。

### P2-2 `sendDataModel` 只存不发

`sendDataModel` 有 4 处引用（读取并记录），但渲染器**不产出**
`a2uiRendererDataModel` 传输层元数据（出现次数 0）。协议允许把它放进
transport metadata 而非 envelope，但当前连这条路径也没有实现，
agent 侧拿不到 UI 全量状态。官方用例：
`test_v10_get_renderer_data_model_includes_surface_without_default` 等 3 条。

### P2-3 `metadata.extensions` 基本未处理

`createSurface.metadata` / 组件级 `@metadata` 在协议中是 `{extensions: {id: {...}}}`
结构，扩展键必须是 UAX 31 标识符。当前 `metadata` 有 7 处引用但
`extensions` 仅 1 处，未做键名校验。低风险，但自定义扩展语义会丢失。

---

## 4. 自造概念 · 已清理

### `catalogs`（surface 级 allow-list）— **已删除**

- 官方 v1.0 schema 中 **`catalogs` 不存在**（`grep '"catalogs"' specification/v1_0`
  零命中）。协议的立场是"catalog 广告由宿主决定，不在 envelope 里"。
- 本渲染器曾在 `A2UISurfaceConfig` 中引入 `catalogs: List<String>?`，并在
  `strictV10` 下用它校验 `catalogId` 是否在白名单内，否则**中止 createSurface**。
  这是个只在自家 fixture 里成立的约定，会误杀"`catalogs` 与 `catalogId` 不同步"
  的报文。
- **处置**：字段、白名单校验分支、`currentCatalogsSet` 状态、以及
  `convertMessageV10` 中对 `createSurface.catalogs` 的类型校验一并删除。
  该模块无线上业务，不需要保留任何过渡期。

---

## 5. 无缺口（可对外声明的能力）

| 维度 | 官方数量 | 覆盖 |
|---|---|---|
| Basic Catalog v1.0 组件 | 18（`AudioPlayer, Button, Card, CheckBox, ChoicePicker, Column, DateTimeInput, Divider, Icon, Image, List, Modal, Row, Slider, Tabs, Text, TextField, Video`） | 18/18 有实现 |
| Basic Catalog v1.0 函数 | 14（`and, email, formatCurrency, formatDate, formatNumber, formatString, length, not, numeric, openUrl, or, pluralize, regex, required`） | 14/14 有实现 |
| 邻接表模型 + `child`/`children` 双形态 | — | 已支持，含 `{path, componentId}` 模板形态 |
| List 模板作用域 + `@index`（无参形态） | — | 已支持，含越界访问降级 |
| v0.9 / v0.9.1 / v1.0 版本分流 | — | 已支持（按 message 粒度） |
| `@path` / `@call` / `@accessibility` / `@checks` mixin | — | 已支持，含 `@@` 转义 |
| `callRendererFunction` / `agentFunctionResponse` 双向 RPC | — | 已支持 |
| `formatString` 的 `${...}` 嵌套插值 | — | 已支持 |
| Markdown 子集 | — | 有渲染，**未做 HTML sanitization**（`sanitiz*` 零命中），需在安全评审中确认 |
| v0.8 envelope | 27 条用例 | 不实现（`v0.8` 零命中）——v0.8 已 deprecated，符合预期，但应在 README 明示不支持 |

---

## 6. 建议的执行顺序（非本次改动）

1. **P0-2 `error` 报文** — 成本最低、收益最高：它让后面每一项校验从"静默"变成"可观测"。
2. **P0-1 `deleteSurface`** — 会话长跑的内存正确性问题。
3. **P1-1 `@index` 参数** — 十行内的修改，直接影响 1-based 编号这类常见场景。
4. **P0-3 `action` 报文** — 需要与 `ka2ui-server` 侧约定报文形状，属跨仓契约，建议在 harness 中先落 spec。
5. **P1-3 拓扑三项** — 补齐循环/悬空/缺失 root，配合 1 的 error 通道。
6. 其余 P2 在有能力宣告（capabilities）时一并处理。

> 以上 1–4 均在 `a2ui/src/commonMain` 内，**不触碰公开 API 的语义**，
> 但会新增报文类型与字段，属于契约变更——按仓库发布顺序应在
> `ka2ui-harness` 中先写进 OpenSpec，再由 server / web 跟进。
