# A2UI v0.9 → v1.0 迁移指南（Agent 下发侧）

> 面向**业务 Agent / 后端**同学的协议迁移速查。Kuikly-A2UI renderer 已经**同时支持 v0.9 与 v1.0**（按消息顶层 `version` 字段分流），本文档只关注 **Agent 侧要如何改下发报文**。
>
> 对应的 renderer 侧实施记录见 [`upgrade_to_a2ui1.0_progress.md`](../upgrade_to_a2ui1.0_progress.md) 和 [`upgrade_to_a2ui1.0.md`](../upgrade_to_a2ui1.0.md)。

---

## 1. 版本号

| v0.9 | v1.0 |
|---|---|
| `"version": "v0.9"` | `"version": "1.0"` |

- renderer 的 `A2UIStreamParser` 以**每条消息**为粒度判断版本，支持单条流里 v0.9 / v1.0 混用（PR-1 版本协商）。
- `v1.0` 严格模式对未识别 `@`-前缀 key、缺字段等直接报错；v0.9 仍保持宽容。
- 迁移推荐路径：**每次只迁一条消息**（先 `updateDataModel`、再 `updateComponents`、最后 `createSurface`），renderer 侧透明。

## 2. 顶层 `createSurface` 变化

### 2.1 删除 `theme` 字段

v1.0 移除 `createSurface.theme`（含 `primaryColor` / `iconUrl` / `agentDisplayName`）。宿主要染色请走 `styles` 或内嵌的 host-driven 主题管道，protocol 层不再承载。

### 2.2 内联 `dataModel`（替代 `sendDataModel: true` + 后续 `updateDataModel`）

**v0.9：**
```json
{"version":"v0.9", "createSurface": {"surfaceId":"s1", "sendDataModel": true}}
{"version":"v0.9", "updateDataModel": {"surfaceId":"s1", "dataModel": {"user":{"name":"Alice"}}}}
```

**v1.0：**
```json
{"version":"1.0", "createSurface": {
  "surfaceId": "s1",
  "catalogId": "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json",
  "dataModel": {"user": {"name": "Alice"}}
}}
```

> 字段名是 **`dataModel`**。当同一条 `createSurface` 同时带 `dataModel` 与 `components` 时，renderer **先应用 `dataModel`**，组件首次求值即可绑定到初始数据。

### 2.3 新增 surface 级 `catalogId`

v1.0 的 surface 级 catalog 字段名是 **`catalogId`**（不是 `defaultCatalogId`）：

- 类型是**纯字符串标识符**，不是"必须可下载的 URL"。惯例上用 URI 形式避免跨组织重名，但**不需要**真的能解析到某个部署资源；renderer 只做字符串比对。
- `createSurface` 的完整属性集合并**只有**：`surfaceId`（必填）、`catalogId`、`sendDataModel`、`components`、`dataModel`、`metadata`。**协议里没有 `catalogs` 字段**。
- 组件级覆盖用 `@catalogId`，函数调用点覆盖用 `@call.catalogId`；未显式指定的走 surface 级默认值。

> **本 renderer 只认官方字段名，不提供别名。** 早期实现曾读 `defaultCatalogId` / `initialDataModel`，并自行引入 `catalogs` allow-list；这三个都不是 v1.0 schema 的一部分，已全部删除（该功能无线上业务接入，无需兼容旧报文）。发错字段名的报文会被当作"未提供"处理。

v0.9 的短 ID（`"catalogId": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"`）仍可被 renderer 识别，但新报文请只用 v1.0 URL。

## 3. 动态值：`@`-前缀 Mixin Keys（**重点**）

v1.0 把原本的内联 `{"path": "..."}` / `{"call": ...}` 等**改为 `@`-前缀形式**，并允许和同层静态字段共存（component-level mixin）。

### 3.1 字段级 Dynamic String

| v0.9 | v1.0 | 语义 |
|---|---|---|
| `{"path": "/user/name"}` | `{"@path": "/user/name"}` | 从 dataModel 读取值 |
| `{"call": {"name":"formatGrade","args":{...}}}` | `{"@call": {"name":"formatGrade","args":{...}}}` | 调用 custom function |
| （list 模板内）`{"path": "@index"}` / `"@index"` | `{"@index": {}}` | 当前 list 项的下标 |

### 3.2 组件级 Mixin（新能力）

v1.0 允许把下列字段作为 `@`-前缀 key **直接挂到组件对象**（不用外层包一层）：

```json
{
  "id": "greeting",
  "component": "Text",
  "text": {"@path": "/user/name"},
  "@accessibility": {
    "description": "Greeting line",
    "live": "polite"
  },
  "@checks": [{"name":"minLength","args":{"min":1}}],
  "@metadata": {"source":"llm-reply-v3"},
  "@catalogId": "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
}
```

**保留 key 全集**（PR-2 落地）：
`@path` · `@call` · `@index` · `@accessibility` · `@checks` · `@metadata` · `@catalogId`

> **严格模式**：出现**除上述之外**的 `@`-前缀 key（如 `@paht` typo）会被 renderer **整条消息拒绝**。需要字面 `@` 开头的字符串值请使用双 `@`（`"@@foo"`）转义。

## 4. `updateDataModel` 必填 `value`

**v0.9：**
```json
{"version":"v0.9", "updateDataModel": {"surfaceId":"s1", "dataModel":{...}}}
```

**v1.0：**
```json
{"version":"1.0", "updateDataModel": {
  "surfaceId": "s1",
  "path": "/user/name",
  "value": "Bob"
}}
```

v1.0 要求 **`path` + `value` 对**作为一次原子更新；批量更新请发多条消息，renderer 按序应用。

## 5. 双向 RPC：`callFunction` → `callRendererFunction`

### 5.1 入站（Agent → Renderer）

**v0.9：**
```json
{"version":"v0.9", "callFunction": {"surfaceId":"s1", "requestId":"r-1", "name":"openPage", "args":{...}}}
```

**v1.0：**
```json
{"version":"1.0", "callRendererFunction": {
  "surfaceId": "s1",
  "requestId": "r-1",
  "name": "openPage",
  "args": {"route": "/profile"},
  "timeoutMs": 3000
}}
```

Renderer 执行完回包：
```json
{"version":"1.0", "rendererFunctionResponse": {
  "surfaceId": "s1",
  "requestId": "r-1",
  "result": {...}       // 或 "error": {"code":"...", "message":"..."}
}}
```

### 5.2 出站（Renderer → Agent，新能力）

Button 按下时可以不经 `event` 直接发起对 Agent 的函数调用：

```json
{
  "id": "fetch-btn",
  "component": "Button",
  "child": "fetch-btn-text",
  "action": {
    "callAgentFunction": {
      "name": "fetchUser",
      "args": {"id": 42},
      "timeoutMs": 5000
    }
  }
}
```

Renderer 会 mint 一个 `requestId`，以 wire 形式发：
```json
{"version":"1.0", "callAgentFunction": {
  "surfaceId": "s1", "requestId": "mint-...", "name": "fetchUser", "args": {"id":42}, "timeoutMs":5000
}}
```

Agent 回包：
```json
{"version":"1.0", "agentFunctionResponse": {
  "surfaceId": "s1",
  "requestId": "mint-...",
  "result": {"name":"Alice","level":"gold"}
}}
```

## 6. 小字段新增（PR-9）

| 字段 | 目标组件 | 类型 | 说明 |
|---|---|---|---|
| `posterUrl` | `Video` | DynamicString | 播放前的 cover 图，首帧渲染后自动隐藏 |
| `placeholder` | `TextField` | DynamicString | 输入为空时的灰色提示 |
| `steps` | `Slider` | Number | 吸附到的**间隔数**（`steps=4 → 5 个 stops`）；`<=0 / 缺省`不吸附 |
| `action.event.userMessage` | 任意可触发组件的 `action` | DynamicString | 事件回传时附带的"人类可读 prompt" |

## 7. 多 Catalog 混用（PR-8）

v1.0 允许同一 surface 在不同组件 / 不同 `@call` 调用点切换 catalog：

```json
// createSurface.catalogId = basic
{
  "id": "custom-badge",
  "component": "A2UIBadge",
  "@catalogId": "https://a2ui.org/specification/v1_0/catalogs/custom_components/catalog.json",
  "label": {
    "@call": {
      "catalogId": "https://a2ui.org/specification/v1_0/catalogs/custom_functions/catalog.json",
      "name": "describeTier",
      "args": {"score": {"@path": "/user/totalScore"}}
    }
  }
}
```

- **lookup 规则**：优先 `catalogId + name` 精确命中，再 fallback 到 `name only` global 注册表。
- **组合约束**：若宿主在 `A2UICatalogDescriptorRegistry` 注册过 `allowedParents` / `allowedChildren`，renderer 会在 CREATE 时做结构校验；违反的单组件 skip，其余照常 render。

## 8. 迁移 checklist（下发侧）

- [ ] `"version": "v0.9"` → `"version": "1.0"`
- [ ] `createSurface.theme` 字段**整段删除**
- [ ] `createSurface.sendDataModel: true` + 分离的 `updateDataModel` → `createSurface.dataModel` 内联
- [ ] surface 级 catalog 字段用 `catalogId`（不要发 `defaultCatalogId`，也不要发 `catalogs`，两者都不被识别）
- [ ] 所有 `{"path": ...}` → `{"@path": ...}`
- [ ] 所有 `{"call": ...}` → `{"@call": ...}`
- [ ] `{"path": "@index"}` → `{"@index": {}}`
- [ ] 组件上的 `accessibility` 字段若原为嵌套对象，可改用 `@accessibility`（**可选**，v1.0 两种都识别）
- [ ] `updateDataModel` 增补 `value` 字段
- [ ] `callFunction` → `callRendererFunction`；相应 `functionResponse` → `rendererFunctionResponse`
- [ ] `Video` / `TextField` / `Slider` / `Button.action` 按需补 PR-9 新字段
- [ ] 跨 catalog 调用加 `@catalogId` / `@call.catalogId`
- [ ] 避免使用 v1.0 保留 `@`-前缀集合之外的 key，否则严格模式整条消息被拒

## 9. 参考样本

真机可跑的 v1.0 样本见：

- [`androidAppForTest/.../a2ui_v10_tests/01_pr2_mixin_keys.json`](../androidAppForTest/src/main/assets/a2ui_v10_tests/01_pr2_mixin_keys.json)
- [`02_pr3_createsurface_inline_datamodel.json`](../androidAppForTest/src/main/assets/a2ui_v10_tests/02_pr3_createsurface_inline_datamodel.json)
- [`03_pr6_rpc_bidirectional.json`](../androidAppForTest/src/main/assets/a2ui_v10_tests/03_pr6_rpc_bidirectional.json)
- [`04_pr8_multi_catalog.json`](../androidAppForTest/src/main/assets/a2ui_v10_tests/04_pr8_multi_catalog.json)
- [`05_pr9_small_fields_video_poster.json`](../androidAppForTest/src/main/assets/a2ui_v10_tests/05_pr9_small_fields_video_poster.json)

对应的 renderer 实现细节与设计决策见 [`upgrade_to_a2ui1.0_progress.md`](../upgrade_to_a2ui1.0_progress.md)。
