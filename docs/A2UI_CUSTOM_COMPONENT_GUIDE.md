# A2UI 自定义组件使用指南

## 概述

A2UI 自定义组件（Custom Component）是 A2UI 协议的扩展能力，允许业务层注册新的原生 UI 组件，并在 JSON 协议中直接引用。任何不在 A2UI v0.9 标准组件列表中的 `"component"` 名称，都会自动被识别为自定义组件。

### 核心价值

- **组件可扩展**：不修改 A2UI 协议本身，即可添加全新的原生 UI 组件（如 WebView、地图、图表等）
- **原生渲染**：自定义组件由平台原生视图承载，与 A2UI 标准组件共享同一套渲染管道
- **自动布局**：通过 Shadow 机制参与 Kuikly FlexBox 布局计算，自动获得正确的宽高
- **跨平台一致**：三端（Android / iOS / HarmonyOS）使用统一的注册 API 和 JSON 引用语法

### 工作原理

```
┌─────────────────────────────────────────────────────────┐
│                  JSON 消息流 (Server/AI)                   │
│                                                          │
│  {                                                       │
│    "component": "A2UIWeb",         ← 未知组件名           │
│    "source": "https://..."                               │
│  }                                                       │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              A2UI Core (Kotlin Multiplatform)             │
│                                                          │
│  1. A2UIApplier.createDSLView("A2UIWeb")                  │
│     → 未命中已知组件 → 创建 A2UICustomComponentView        │
│  2. A2UICustomComponentView.willInit()                    │
│     → 创建 Kotlin Shadow 对象                              │
│     → BridgeManager.createShadow(tag, "A2UIWeb")          │
│        → Native 层注册的 Shadow Creator 创建 C++ 实例      │
│  3. FlexBox 布局时调用 measure()                           │
│     → Kotlin Shadow.calculateRenderViewSize(cw, ch)      │
│        → Native Shadow.CalculateRenderViewSize(cw, ch)   │
│        → 返回 intrinsic size (如 343×240)                 │
│  4. 布局完成后 sync frame 到原生视图                       │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│            Native Render Layer (平台渲染层)                │
│                                                          │
│  ┌─────────────────┐  ┌─────────────────┐               │
│  │  Shadow (测量)    │  │  RenderView     │               │
│  │  • 参与 FlexBox  │  │  (原生视图)      │               │
│  │  • 返回内在尺寸   │  │  • 显示 UI 内容   │               │
│  │  • 接收属性更新   │  │  • 处理 setProp  │               │
│  └─────────────────┘  └─────────────────┘               │
│           ↑ 同一个 viewName (e.g. "A2UIWeb")              │
└─────────────────────────────────────────────────────────┘
```

### 宽高确定机制

自定义组件的最终宽高由 **三层优先级** 决定；三端用不同的物理机制承载同一
语义——"组件自身决定 intrinsic size"：

```
Flex 布局约束 (父容器/Surface 宽度)
     │
     ▼
┌─ Android / iOS ──────────────────────────────────────┐
│  Shadow.CalculateRenderViewSize(cw, ch)              │
│  → 返回 intrinsic size (如 width=cw, height=240)      │
└─────────────────────────────────────────────────────┘
┌─ HarmonyOS ──────────────────────────────────────────┐
│  A2UIBridgeModule.calculateCustomViewSize            │
│  → ArkTS 注册时传入的 measurer(cw, ch)                │
│  → 返回 { width, height }                             │
│  → 异步 resize: notifyA2UICustomViewSizeChanged      │
└─────────────────────────────────────────────────────┘
     │
     ▼
检查 DSL 显式样式 styleWidth / styleHeight
→ 如果设置了 → 覆盖上一层返回的值（最高优先级）
→ 如果未设置 → 使用上一层返回值；没有 measurer / Shadow 时三端均 fallback
   到 (width = 父 cross-axis, height = 0)
     │
     ▼
最终 frame = (finalWidth, finalHeight)
```

**优先级：DSL 显式宽高 > Shadow 测量结果 > 父约束**

> **HarmonyOS 例外**：Kuikly OHOS 的 Shadow 创建器注册在 C++ 静态 map 中，ArkTS
> 不能向其注册；A2UI 选择不引入跨 `.so` 的 C++ ABI 补丁，因此
> `A2UICustomComponentView` 在 OHOS 上**跳过 Shadow 一步**，`measure()` 的 fallback
> 直接把父节点传入的 cross-axis 约束作为宽度（相当于"自动撑满父可用宽度"），高度
> 则需要 JSON 显式指定。实践中只需在 JSON 中为自定义组件写 `height`，不用写
> `width`。

---

## 整体架构

```
libkuikly.so (SDK 公共产物)               libkuikly_entry.so (业务模块)
┌──────────────────────────────┐       ┌─────────────────────────────┐
│ KRNativeRender (ArkTS)        │       │ napi_init.cpp               │
│ ├─ 自定义 View creator 注册表  │<──────│ initKuikly()                 │
│ │   (ArkTS Map)               │       │ (无需 C++ 侧 Shadow 注册)   │
│ └─ 无 ArkTS 侧 Shadow 机制    │       │                             │
├──────────────────────────────┤       │ ArkTS 侧：                  │
│ IKRRenderShadowExport         │       │ registerA2UICustomRenderView│
│ (C++ 侧，A2UI 不启用)         │       │   ("A2UIWeb", () => ...)    │
├──────────────────────────────┤       └─────────────────────────────┘
│ KRRenderLayerHandler.cpp     │
│ CreateShadow(tag, name) ←未命中直接 return "0|0" │
└──────────────────────────────┘
```

> **关键设计**：业务自定义组件**只需在 ArkTS 侧**通过
> `registerA2UICustomRenderView(...)` 注册即可；A2UI 的
> `A2UICustomComponentView` 在 OHOS 上**不创建 Kuikly Shadow**
> （`isCustomComponentShadowRequired=false`），彻底避免 `libkuikly.so`（SDK）
> 与 `libkuikly_entry.so`（业务）之间的 C++ ABI 耦合。代价是：OHOS JSON
> 作者必须为自定义组件显式写 `height`。

---

## JSON 协议格式

### 基本用法

在 JSON 协议中，将 `"component"` 字段设置为已注册的自定义组件名称即可：

```json
{
  "id": "myWebView",
  "component": "A2UIWeb",
  "source": "https://framework.tds.qq.com/"
}
```

### 完整示例：A2UIWeb（WebView 组件）

```json
{
  "version": "v0.9",
  "createSurface": {
    "surfaceId": "demo-web-1",
    "catalogId": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"
  }
}
{
  "version": "v0.9",
  "updateComponents": {
    "surfaceId": "demo-web-1",
    "components": [
      { "id": "root", "component": "Card", "child": "main-col" },
      { "id": "main-col", "component": "Column", "children": ["title", "desc", "webContainer"], "align": "stretch" },
      { "id": "title", "component": "Text", "text": "A2UIWeb 组件", "variant": "h3" },
      { "id": "desc", "component": "Text", "text": "通过 source 属性加载网页内容", "variant": "caption" },
      {
        "id": "webContainer",
        "component": "A2UIWeb",
        "source": "https://framework.tds.qq.com/"
      }
    ]
  }
}
```

### 完整示例：MyA2UIView（简单自定义视图）

```json
{
  "version": "v0.9",
  "updateComponents": {
    "surfaceId": "demo-view-1",
    "components": [
      { "id": "root", "component": "Card", "child": "main-col" },
      { "id": "main-col", "component": "Column", "children": ["title", "desc", "myView"], "align": "stretch" },
      { "id": "title", "component": "Text", "text": "MyA2UIView 组件", "variant": "h3" },
      { "id": "desc", "component": "Text", "text": "自定义简单视图", "variant": "caption" },
      {
        "id": "myView",
        "component": "MyA2UIView",
        "text": "Hello from MyA2UIView! 🎉",
        "textColor": "#6200EE"
      }
    ]
  }
}
```

### 属性传递机制

JSON 中的自定义属性（如 `source`、`text`、`textColor`）会经过以下链路传递到原生视图：

```
JSON props
  → A2UIApplier.createComponent() → applyProps()
    → view.didSetProp("source", "https://...")
      → RenderView.setProp("source", "https://...")
        → Native RenderView.setProp(propKey, propValue)
          → A2UIWebRenderView.setProp("source", ...)
```

**注意事项**：

| 规则 | 说明 |
|------|------|
| 组件名唯一 | 自定义组件名不能与 v0.9 标准组件或 Kuikly 组件重名 |
| 属性名自由 | 自定义属性名无限制，由业务层自行处理 |
| 属性类型 | 目前支持 `String`、`Number`、`Boolean` 等基本类型 |
| 布局属性 | `width`、`height` 等布局属性与标准组件一致，走 FlexBox 计算 |
| 宽高默认值 | 未指定宽高时，由 Shadow 返回 intrinsic size（如 A2UIWeb 高度默认 240） |

---

## 为 AI/LLM 生成 Catalog 文件

### 背景

在 A2UI 协议中，**Catalog** 是 Renderer 提供给 Agent（AI/LLM）的能力清单。标准的 basic catalog 定义了 18 个内置组件（Text、Image、Button 等）。当你注册了自定义组件后，如果希望 AI/LLM 能够正确生成包含自定义组件的 UI JSON，就需要提供一个**扩展的 catalog 文件**，让 AI 知道有哪些自定义组件可用、每个组件支持哪些属性。

### 何时需要 Catalog 文件

| 场景 | 是否需要 |
|------|---------|
| 客户端与服务端约定好组件协议，服务端手动编写 JSON | 不需要（双方约定即可） |
| AI/LLM 自动生成包含自定义组件的 UI JSON | **需要**（AI 需要 catalog 来了解可用组件和属性 schema） |
| 需要对 JSON 进行 schema 校验 | **需要**（catalog 提供 JSON Schema 校验规则） |

### Catalog 文件格式

自定义组件的 catalog 文件遵循标准 A2UI catalog JSON Schema 格式。核心结构如下：

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "custom_components_catalog.json",
  "title": "My Custom Components Catalog",
  "description": "Extended catalog with custom components.",
  "catalogId": "custom_components_catalog.json",
  "components": {
    "MyComponent": {
      "type": "object",
      "allOf": [
        { "$ref": "https://a2ui.org/specification/v0_9/common_types.json#/$defs/ComponentCommon" },
        { "$ref": "#/$defs/CatalogComponentCommon" },
        {
          "type": "object",
          "description": "Description of the component for AI/LLM.",
          "properties": {
            "component": { "const": "MyComponent" },
            "myProp": {
              "type": "string",
              "description": "Description of this property."
            }
          },
          "required": ["component", "myProp"]
        }
      ],
      "unevaluatedProperties": false
    }
  },
  "functions": {},
  "$defs": {
    "CatalogComponentCommon": {
      "type": "object",
      "properties": {
        "weight": {
          "type": "number",
          "description": "The relative weight of this component within a Row or Column."
        }
      }
    },
    "anyComponent": {
      "oneOf": [
        { "$ref": "#/components/MyComponent" }
      ],
      "discriminator": { "propertyName": "component" }
    }
  },
  "extends": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"
}
```

### 关键字段说明

| 字段 | 说明 |
|------|------|
| `catalogId` | Catalog 的唯一标识，在 `createSurface` 消息中引用 |
| `components` | 自定义组件定义，每个 key 是组件名 |
| `components.*.allOf[0]` | 引用 `ComponentCommon`，提供 `id`、`children` 等通用属性 |
| `components.*.allOf[1]` | 引用 `CatalogComponentCommon`，提供 `weight` 等布局属性 |
| `components.*.allOf[2]` | 组件特有属性定义（`component` const + 自定义 props） |
| `extends` | 声明继承自 basic catalog，表示同时支持所有标准组件 |
| `$defs.anyComponent` | 列出所有自定义组件的引用，供 schema 校验使用 |

### 如何编写组件属性定义

根据你在客户端 `setProp` 中处理的属性，在 catalog 中定义对应的 JSON Schema：

```kotlin
// Android RenderView 中处理的属性
override fun setProp(propKey: String, propValue: Any): Boolean {
    return when (propKey) {
        "source" -> { loadUrl(propValue as String); true }  // → type: string, format: uri
        "text" -> { setText(propValue as String); true }     // → type: string
        "textColor" -> { setColor(propValue as String); true } // → type: string, pattern: hex
        else -> false
    }
}
```

对应的 catalog 属性定义：

```json
"properties": {
  "component": { "const": "A2UIWeb" },
  "source": {
    "type": "string",
    "format": "uri",
    "description": "The URL of the web page to load."
  }
}
```

### 在 JSON 消息中引用自定义 Catalog

生成 catalog 文件后，在 `createSurface` 消息中将 `catalogId` 指向你的自定义 catalog：

```json
{
  "version": "v0.9",
  "createSurface": {
    "surfaceId": "my-surface",
    "catalogId": "custom_components_catalog.json"
  }
}
```

### 参考示例

项目中提供了完整的自定义组件 catalog 示例文件，包含 `A2UIWeb` 和 `MyA2UIView` 两个组件的定义：

```
a2ui/catalogs/
└── custom_components_catalog.json    # ← 自定义组件 Catalog 文件（AI/LLM 参考）

a2ui/androidAppForNativePage/src/main/assets/a2ui_custom_components/
├── 01_web_view/
│   └── 1_web_source.json            # 引用了 custom_components_catalog.json
└── 02_my_view/
    └── 1_my_view.json               # 引用了 custom_components_catalog.json
```

> **提示**：可以直接参考 [`a2ui/catalogs/custom_components_catalog.json`](../catalogs/custom_components_catalog.json) 的格式，为你的业务自定义组件编写对应的 catalog 文件。标准 basic catalog 的完整定义参见 A2UI 官方规范：<https://a2ui.org/>。


## 平台接入指南

### Android

#### 注册自定义组件

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView

// 1. 注册 RenderView（承载 UI 显示）
// 2. 注册 Shadow（参与布局测量，返回 intrinsic size）
A2UISurfaceView.registerCustomRenderView(
    viewName = "A2UIWeb",
    creator = { context -> A2UIWebView(context) },
    shadowCreator = { A2UIWebShadow() }
)

A2UISurfaceView.registerCustomRenderView(
    viewName = "MyA2UIView",
    creator = { context -> MyA2UIView(context) },
    shadowCreator = { MyA2UIViewShadow() }
)
```

#### 注销自定义组件

```kotlin
A2UISurfaceView.unregisterCustomRenderView("A2UIWeb")
A2UISurfaceView.unregisterCustomRenderView("MyA2UIView")
```

#### API 签名

```kotlin
companion object {
    @JvmStatic
    fun registerCustomRenderView(
        viewName: String,
        creator: (Context) -> View,
        shadowCreator: () -> IKuiklyRenderShadowExport
    )

    @JvmStatic
    fun unregisterCustomRenderView(viewName: String)
}
```

#### Shadow 实现

Shadow 负责返回组件的内在尺寸（intrinsic size），供 FlexBox 布局引擎使用：

```kotlin
import android.util.SizeF
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderShadowExport

class A2UIWebShadow : IKuiklyRenderShadowExport {
    override fun setProp(propKey: String, propValue: Any) {
        // 可根据属性动态调整尺寸
    }

    override fun calculateRenderViewSize(constraintSize: SizeF): SizeF {
        // Width: 使用约束宽度（填充父容器）
        // Height: 固定 240dp（WebView 内在内容高度）
        return SizeF(constraintSize.width, 240f)
    }
}

class MyA2UIViewShadow : IKuiklyRenderShadowExport {
    override fun setProp(propKey: String, propValue: Any) {}

    override fun calculateRenderViewSize(constraintSize: SizeF): SizeF {
        return SizeF(constraintSize.width, 200f)
    }
}
```

#### RenderView 实现

```kotlin
import android.content.Context
import android.webkit.WebView
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport

class A2UIWebView(context: Context) : WebView(context), IKuiklyRenderViewExport {
    override fun setProp(propKey: String, propValue: Any): Boolean {
        return when (propKey) {
            "source" -> {
                loadUrl(propValue as String)
                true
            }
            else -> false
        }
    }

    override fun call(method: String, params: Any?, callback: Any?) {}
}
```

#### 完整示例

```kotlin
class CustomComponentDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 必须在 A2UISurfaceView 加载 JSON 之前注册
        registerCustomComponents()

        setContentView(buildLayout())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterCustomComponents()
    }

    private fun registerCustomComponents() {
        // A2UIWeb: WebView 组件
        A2UISurfaceView.registerCustomRenderView(
            viewName = "A2UIWeb",
            creator = { context -> A2UIWebView(context) },
            shadowCreator = { A2UIWebShadow() }
        )

        // MyA2UIView: 简单自定义视图
        A2UISurfaceView.registerCustomRenderView(
            viewName = "MyA2UIView",
            creator = { context -> MyA2UIView(context) },
            shadowCreator = { MyA2UIViewShadow() }
        )

        Log.i(TAG, "✅ All custom components registered")
    }

    private fun unregisterCustomComponents() {
        A2UISurfaceView.unregisterCustomRenderView("A2UIWeb")
        A2UISurfaceView.unregisterCustomRenderView("MyA2UIView")
    }
}
```

---

### iOS

#### 注册自定义组件

```swift
import UIKit

// 注册 A2UIWeb 自定义组件
A2UISurfaceView.registerCustomRenderView(viewName: "A2UIWeb") {
    A2UIWebView()
}

// 注册 MyA2UIView 自定义组件
A2UISurfaceView.registerCustomRenderView(viewName: "MyA2UIView") {
    MyA2UIView()
}
```

#### 注销自定义组件

```swift
A2UISurfaceView.unregisterCustomRenderView(viewName: "A2UIWeb")
A2UISurfaceView.unregisterCustomRenderView(viewName: "MyA2UIView")
```

#### Shadow 实现

iOS 端的 Shadow 是一个 `NSObject` 子类，通过 `@objc` 暴露测量方法给渲染层调用（协议约定，不需要显式 conform 具体 protocol）：

```swift
import UIKit

/// Shadow for A2UIWebView.
/// Reports intrinsic size: width = constraint width, height = 240pt.
class A2UIWebShadow: NSObject {

    @objc func hrv_setPropWithKey(_ propKey: String, propValue: Any) {
        // 可根据属性动态调整尺寸；本示例不依赖属性
    }

    @objc func hrv_calculateRenderViewSize(withConstraintSize constraintSize: CGSize) -> CGSize {
        // Width: 使用约束宽度（填充父容器）
        // Height: 固定 240pt（WebView 内在内容高度）
        return CGSize(width: constraintSize.width, height: 240)
    }

    @objc func hrv_call(withMethod method: String, params: String?) -> String? {
        return nil
    }
}
```

#### RenderView 实现

`RenderView` 是一个 `UIView` 子类，实现 `KuiklyRenderViewExportProtocol`，并通过类方法 `hrv_createShadow` 返回上面定义的 Shadow：

```swift
import UIKit
import WebKit

@objc(A2UIWeb)
class A2UIWebView: UIView, KuiklyRenderViewExportProtocol {

    private let webView = WKWebView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        // 布局 webView 填满 self ...
        addSubview(webView)
    }
    required init?(coder: NSCoder) { fatalError() }

    // 处理 setProp
    @objc func hrv_setProp(withKey propKey: String, propValue: Any) {
        // 通用 CSS 属性走标准处理
        if css_setProp(withKey: propKey, value: propValue) { return }
        // 业务自定义属性
        if propKey == "source", let url = propValue as? String, let u = URL(string: url) {
            webView.load(URLRequest(url: u))
        }
    }

    @objc func hrv_resetPropWithKey(_ propKey: String) { /* ... */ }
    @objc func hrv_setShadow(_ shadow: Any) { /* Shadow 由渲染层管理，通常无需持有 */ }

    // 关键：注册 Shadow 工厂
    @objc class func hrv_createShadow() -> NSObject {
        return A2UIWebShadow()
    }
}
```

#### 完整示例

```swift
class CustomComponentDemoViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // 必须在 A2UISurfaceView 加载 JSON 之前注册
        registerCustomComponents()

        // ... 创建和使用 A2UISurfaceView ...
    }

    deinit {
        unregisterCustomComponents()
    }

    private func registerCustomComponents() {
        // A2UIWeb: WKWebView 组件
        A2UISurfaceView.registerCustomRenderView(viewName: "A2UIWeb") {
            A2UIWebView()
        }

        // MyA2UIView: 简单自定义视图
        A2UISurfaceView.registerCustomRenderView(viewName: "MyA2UIView") {
            MyA2UIView()
        }

        print("✅ All custom components registered")
    }

    private func unregisterCustomComponents() {
        A2UISurfaceView.unregisterCustomRenderView(viewName: "A2UIWeb")
        A2UISurfaceView.unregisterCustomRenderView(viewName: "MyA2UIView")
    }
}
```

---

### HarmonyOS (OHOS)

HarmonyOS 的自定义组件注册**只需在 ArkTS 层**完成：组件通过
`registerA2UICustomRenderView(viewName, creator, measurer?)` 注册到全局表，
`napi_init.cpp` **不需要**任何 Shadow 注册代码。

> **与 Android / iOS 的差异**：A2UI 的 `A2UICustomComponentView` 在 OHOS 上
> **不走 Shadow 测量路径**（`isCustomComponentShadowRequired=false`，见
> `a2ui/src/ohosArm64Main/.../Platform.ohos.kt`）。原因是 Kuikly 的 Shadow
> 创建器注册在 C++ 静态 map 中，ArkTS 无法向其写入，A2UI 不希望引入跨
> `.so` 的 C++ ABI 补丁。
>
> 替代方案：注册时传入可选的 `measurer`——A2UI 会在布局线程同步调用它来
> 决定自定义组件的 intrinsic size，等价于 Android 的
> `IKuiklyRenderShadowExport` 和 iOS 的 `+hrv_createShadow`。
>
> 对于**异步才知道尺寸**的组件（例如 WebView 内容加载完才知道高度），
> 业务侧可以在异步回调里调用
> `notifyA2UICustomViewSizeChanged(view, w, h)` 触发一次二次布局；A2UI
> 内部会判断新旧尺寸是否真有变化（0.5 vp 容差），只有变了才会 markDirty
> 重排，避免抖动。
>
> 如果既没有注册 `measurer` 也没有调用二次 resize，JSON 中还可以用
> `width` / `height` 作为硬覆盖（优先级最高）。

#### 架构概览

```
ArkTS 层 (libkuikly_entry.so)                 KMP 层 (libshared.so)
┌──────────────────────────┐                 ┌─────────────────────────────┐
│ registerA2UICustomRender │                 │ A2UICustomComponentView      │
│   View(name, creator,    │                 │   measure(cw, ch) →          │
│         measurer?)        │  ◄──── sync ─── │   A2UIBridgeModule          │
│                          │   Module call   │     .calculateCustomViewSize │
│ measurer(cw, ch) =>      │  ────────────►  │                             │
│   { width, height }      │                 │ Fallback (无 measurer):      │
│                          │                 │   width = parent cross,      │
│ notifyA2UICustomViewSize │                 │   height = 0                 │
│   Changed(view, w, h)    │  ──── async ──► │                             │
│   (异步 resize)           │   NAPI writer   │ Hard override:               │
│                          │                 │   JSON width / height always │
│ KuiklyRenderBaseView      │                 │   wins                       │
│   setProp / createArkUI   │                 └─────────────────────────────┘
└──────────────────────────┘
```

#### ArkTS 层：注册 RenderView（承载 UI 显示）

```typescript
import { KuiklyRenderBaseView } from '@kuikly-open/render';
import { registerA2UICustomRenderView, unregisterA2UICustomRenderView } from '@kuikly-open/a2ui-render';
import { webview } from '@kit.ArkWeb';

// ============================================================
// A2UIWebRenderView: Web 组件封装
// ============================================================

@Builder
function createA2UIWebView(view: KuiklyRenderBaseView) {
  Web({
    src: (view as A2UIWebRenderView).source,
    controller: (view as A2UIWebRenderView).webController
  })
    .width('100%')
    .height('100%')
}

export class A2UIWebRenderView extends KuiklyRenderBaseView {
  source: string = '';
  webController: webview.WebviewController = new webview.WebviewController();

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'source':
        this.source = propValue as string;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {}

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIWebView),
      this
    );
  }
}

// ============================================================
// MyA2UIRenderView: 简单文本视图封装
// ============================================================

@Builder
function createMyA2UIView(view: KuiklyRenderBaseView) {
  Column() {
    Text((view as MyA2UIRenderView).text)
      .fontSize(16)
      .fontColor((view as MyA2UIRenderView).textColor)
      .textAlign(TextAlign.Center)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#E8F5E9')
  .justifyContent(FlexAlign.Center)
  .alignItems(HorizontalAlign.Center)
}

export class MyA2UIRenderView extends KuiklyRenderBaseView {
  text: string = '';
  textColor: string = '#333333';

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'text':
        this.text = propValue as string;
        this.updateArkUI();
        return true;
      case 'textColor':
        this.textColor = propValue as string;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {}

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createMyA2UIView),
      this
    );
  }
}
```

#### 注册 RenderView

```typescript
import {
  registerA2UICustomRenderView,
  unregisterA2UICustomRenderView,
  notifyA2UICustomViewSizeChanged,
  A2UICustomViewSize
} from '@kuikly-open/a2ui-render';

@Entry
@Component
struct CustomComponentDemoPage {

  aboutToAppear(): void {
    this.registerCustomComponents();
  }

  aboutToDisappear(): void {
    this.unregisterCustomComponents();
  }

  private registerCustomComponents(): void {
    // 注册 A2UIWeb（Web 组件）
    //   measurer: 父 cross-axis 宽 + 固定 240vp 高。等价于 Android 的
    //   IKuiklyRenderShadowExport 和 iOS 的 +hrv_createShadow。
    registerA2UICustomRenderView(
      'A2UIWeb',
      (): KuiklyRenderBaseView => new A2UIWebRenderView(),
      (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 240 })
    );

    // 注册 MyA2UIView（简单文本视图）
    registerA2UICustomRenderView(
      'MyA2UIView',
      (): KuiklyRenderBaseView => new MyA2UIRenderView(),
      (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 200 })
    );

    console.info('✅ All custom components registered: A2UIWeb, MyA2UIView');
  }

  private unregisterCustomComponents(): void {
    unregisterA2UICustomRenderView('A2UIWeb');
    unregisterA2UICustomRenderView('MyA2UIView');
    console.info('🗑️ All custom components unregistered');
  }

  build() {
    // ... 页面 UI，包含 A2UISurfaceView ...
  }
}
```

> **异步尺寸（二次 resize）**：如果组件尺寸要等异步数据才能确定（例如
> WebView 加载完 HTML 后高度才稳定下来），可以先用 `measurer` 返回一个
> 占位尺寸，然后在异步回调里调用 `notifyA2UICustomViewSizeChanged`：
>
> ```typescript
> export class A2UIWebRenderView extends KuiklyRenderBaseView {
>   // ... 省略 setProp / createArkUIView ...
>
>   // 由 Web 组件的 onPageEnd 调用
>   onContentHeightReady(contentHeightVp: number): void {
>     // 只有新高度和当前布局高度相差 > 0.5vp 时才真正触发 relayout
>     notifyA2UICustomViewSizeChanged(this, 0, contentHeightVp);
>   }
> }
> ```
>
> 其中 `width=0` 表示保持当前宽度不变；`height=0` 同理。KMP 侧的
> `A2UICustomComponentView.handleNativeResize` 会严格比对新旧值，变化
> 不显著时直接 no-op，避免抖动。

---

## 宽高控制策略

### 常见场景

| 场景 | width | height | 说明 |
|------|-------|--------|------|
| 填充父容器宽度 + 固定内容高度 | `constraint_width` | 固定值（如 240） | 典型：WebView、Banner |
| 填充父容器宽度 + 自适应高度 | `constraint_width` | `-1`（由原生自行计算） | 典型：TextView、图片 |
| 固定宽高 | 固定值 | 固定值 | 典型：图标、头像 |
| 响应父布局约束 | 由 FlexBox 决定 | 由 FlexBox 决定 | 设置 `flex(1f)` 等 |

### 动态调整内在尺寸

如果组件的内在尺寸依赖某个属性（如图片尺寸依赖 URL），可在 `setProp` 中更新状态，`calculateRenderViewSize` 中读取：

#### Android

```kotlin
class MyDynamicShadow : IKuiklyRenderShadowExport {
    private var displayMode: String = "normal"

    override fun setProp(propKey: String, propValue: Any) {
        if (propKey == "displayMode") {
            displayMode = propValue as String
        }
    }

    override fun calculateRenderViewSize(constraintSize: SizeF): SizeF {
        val height = when (displayMode) {
            "compact" -> 80f
            "expanded" -> 320f
            else -> 200f
        }
        return SizeF(constraintSize.width, height)
    }
}
```

#### HarmonyOS (ArkTS)

HarmonyOS 没有 ArkTS 侧的 Shadow 机制，A2UI 也刻意不走 C++ Shadow 通路（见本
章节上方的 "HarmonyOS (OHOS)" 说明）。因此**组件的宽高完全由 JSON 的
`width` / `height` 决定**：

- 内在尺寸固定的组件 → 直接在 JSON 里写死（最常见做法）。
- 内在尺寸依赖某个属性（例如图片按 URL 决定高度）→ 推荐宿主业务侧在
  收到数据后更新 JSON 并重发消息流，由 A2UI 的 `styleHeight` 变更触发
  重新布局；或者在 JSON 中给一个保守的占位 `height`，让 ArkUI 内部
  用 `.width('100%').height('100%')` 填满（但组件在父容器中的
  **边界**仍由该占位 `height` 决定）。

---

## 注意事项

### 注册时机

- 自定义组件必须在 `A2UISurfaceView` 加载 JSON 消息流**之前**注册
- 推荐在页面 `onCreate` / `viewDidLoad` / `aboutToAppear` 中注册
- 组件注册是**全局**的：一旦注册，所有 A2UI 实例都可以使用

### 组件命名

| 规则 | 说明 |
|------|------|
| 不可与标准组件重名 | `Text`、`Image`、`Button` 等 18 个 v0.9 标准组件名被保留 |
| 不可与 Kuikly 组件重名 | `KRView`、`KRRichTextView` 等 KR 前缀组件名被保留 |
| 推荐命名风格 | 使用有意义的前缀，如 `A2UIWeb`、`A2UIChart`、`A2UIMap` |

### 宽高未设置时返回 0×0

如果自定义组件显示为 0×0，检查以下几点：

1. **Shadow 是否注册成功**（Android/iOS）：确认 `registerShadow` 调用的组件名与 JSON 中一致
2. **Shadow 是否返回正确尺寸**（Android/iOS）：`calculateRenderViewSize` 的返回宽/高必须 > 0
3. **FlexBox 是否触发布局**：确认父容器有正确的布局属性（如 `align: "stretch"`）
4. **是否显式设置了宽高为 0**：检查 JSON 中是否有 `"width": 0` 或 `"height": 0`
5. **HarmonyOS 上**：A2UI 不走 Shadow 测量路径，所以**JSON 必须为自定义组件显式写
   `height`**（父 Column 的 `alignItems=stretch` 已经负责宽度，可省略 `width`）。
   同时确认 ArkTS 侧 `registerA2UICustomRenderView` 调用的 viewName 与 JSON 中
   完全一致（区分大小写），且 `createArkUIView` 返回的 ArkUI 组件填满了容器
   （例如 `.width('100%').height('100%')`）。

### 平台特性

- **HarmonyOS 零 C++ ABI 耦合**：自定义组件通过 ArkTS 侧的
  `registerA2UICustomRenderView(...)` 注册即可；A2UI 的 `A2UICustomComponentView`
  在 OHOS 上跳过 Kuikly Shadow 通路（`isCustomComponentShadowRequired=false`），
  业务 `libkuikly_entry.so` 不需要 `#include` 任何 SDK 的 C++ 头文件，彻底避
  免了跨 `.so` 的 C++ ABI 脆弱性问题。代价是：**OHOS JSON 必须为自定义组件
  显式写 `height`**。

### 同步调用限制

- `calculateRenderViewSize` 在 Kuikly Context Thread（布局线程）同步调用
- 必须快速返回，不能执行耗时操作（如网络请求、文件 IO）
- 如需异步获取尺寸，先返回默认值，后续再触发重新布局

---

## Demo 参考

三端 Demo 应用中提供了完整的自定义组件示例页面：

| 测试分组 | 组件 | 说明 |
|---------|------|------|
| WebView 自定义组件 | `A2UIWeb` | 自定义 WebView 组件，支持 `source` 属性加载网页 |
| 简单自定义组件 | `MyA2UIView` | 最简单的自定义视图，支持 `text` 和 `textColor` 属性 |

### Demo 源码位置

| 平台 | 文件 |
|------|------|
| Android | `a2ui/androidAppForNativePage/src/main/java/com/tencent/kuikly/a2ui/demo/CustomComponentDemoActivity.kt` |
| iOS | `a2ui/iosShared/Sources/CustomComponentDemoViewController.swift`（`iosAppForNativePage` 与 `iosAppForKuiklyPage` 通过 `iosShared` 共用） |
| HarmonyOS ArkTS | `a2ui/ohosAppForNativePage/entry/src/main/ets/pages/CustomComponentDemoPage.ets`（Kuikly 页同位于 `ohosAppForKuiklyPage/entry/src/main/ets/pages/KuiklyPageCustomComponentDemoPage.ets`） |

### 测试 JSON 文件位置

```
a2ui/ohosAppForNativePage/entry/src/main/resources/rawfile/a2ui_custom_components/
├── 01_web_view/
│   └── 1_web_source.json         # A2UIWeb 组件测试
└── 02_my_view/
    └── 1_my_view.json            # MyA2UIView 组件测试
```

---

## 常见问题

### Q: 自定义组件显示为空白？

检查以下几点：
1. 组件是否在 `writeStream` 之前注册
2. RenderView 的 `setProp` 是否正确处理了属性
3. 组件名拼写是否与 JSON 和注册代码完全一致（区分大小写）

### Q: 自定义组件宽高为 0？

参考上方 "宽高未设置时返回 0×0" 检查清单。最常见的原因是 Shadow 未注册成功。

### Q: 能否在 Shadow 中访问网络或执行耗时操作？

不可以。`calculateRenderViewSize` 在布局线程同步执行，必须快速返回。如需异步数据，建议在 RenderView 的 `setProp` 中发起异步请求，完成后通知重新布局。

### Q: 自定义组件如何参与 FlexBox 布局？

- **Android / iOS**：组件的宽高由 Shadow 的 `calculateRenderViewSize` 返回，JSON
  的 `flex` / `width` / `height` 为可选覆盖值。
- **HarmonyOS**：A2UI 跳过了 Shadow 通路，宽高来源优先级为：
  1. 注册时传入的 `measurer`（intrinsic size，等价于 Android / iOS 的 Shadow）
  2. `notifyA2UICustomViewSizeChanged(view, w, h)` 的异步二次 resize
  3. 父容器 `alignItems=stretch` 分配给自定义组件的 cross-axis 宽度 + 零高度
  4. JSON 的 `width` / `height`（硬覆盖，优先级最高）

### Q: HarmonyOS 上自定义组件为什么是 0×0 ？

HarmonyOS 的自定义组件**不**在 C++ 层注册 Shadow，A2UI 也**不**走 Kuikly 的
Shadow 测量通路（见 `Platform.ohos.kt` 的 `isCustomComponentShadowRequired=false`）。
如果布局结果看起来是 0×0，依次排查：

1. 注册时是否传入了 `measurer`——这是 Android / iOS 的 Shadow 在 OHOS 的对
   应物。例如：

   ```typescript
   registerA2UICustomRenderView(
     'A2UIWeb',
     () => new A2UIWebRenderView(),
     (cw, _ch) => ({ width: cw, height: 240 })
   );
   ```

2. 若组件尺寸要等异步数据才能确定（WebView 内容高度等），在异步回调里调
   用 `notifyA2UICustomViewSizeChanged(view, w, h)` 触发二次布局。
3. 或者直接在 JSON 条目里写 `"width"` / `"height"` 作为硬覆盖（优先级最高）。

### Q: 一个自定义组件名可以注册多次吗？

不可以。后注册的同名组件会覆盖之前的注册。如果需要重置，先调用 `unregister` 再重新 `register`。
