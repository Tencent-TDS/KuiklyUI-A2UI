# A2UI 自定义 Icon 图片使用指南

## 概述

A2UI 内置的 `Icon` 组件默认通过一套 Unicode / Emoji 字符表（`ICON_MAP`）渲染系统图标，能够满足通用场景，但在业务需要**统一视觉风格**、**品牌化图标**或**替换特定图标**时表现有限。自定义 Icon 图片能力允许业务在应用启动时注册一套图片资源，让 A2UI 协议中所有 `Icon` 组件自动改用注册的图片渲染。

### 核心价值

- **视觉统一**：业务可提供整套 Material / iOS / HarmonyOS 风格图标，取代默认 Emoji 字符
- **零协议改动**：服务端下发的 A2UI JSON 保持原样（仍然使用 `{"name": "home"}`），客户端注册即生效
- **跨平台一致**：三端（Android / iOS / HarmonyOS）使用统一的注册 API 和资源命名约定
- **数据绑定友好**：支持 `Icon.name` 通过 `path` / `functionCall` 数据绑定，绑定值变化时自动切换图片

### 工作原理

```
┌─────────────────────────────────────────────────────────┐
│                  JSON 消息流 (Server/AI)                   │
│                                                          │
│  {                                                       │
│    "component": "Icon",                                  │
│    "name": "home"    // 或 { "path": "/currentIcon" }    │
│  }                                                       │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              A2UI Core (Kotlin Multiplatform)             │
│                                                          │
│  1. A2UIComponentMapper 处理 "Icon" 组件时全局判定：       │
│     A2UIIconRegistry.isEmpty()                           │
│       ├─ true  → 路由到 KRRichTextView（Emoji 文本模式）  │
│       └─ false → 路由到 KRImageView（图片模式）           │
│                                                          │
│  2. 图片模式下解析 name（静态字符串 / path 绑定 / 函数调用）│
│     - 命中 registry：使用注册的 URI 渲染                    │
│     - 未命中：使用 A2UI_ICON_FALLBACK_KEY 对应的兜底图      │
│                                                          │
│  3. 默认样式：width=24, height=24, margin=8,              │
│     resizeMode="contain"                                 │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│            平台图片加载器                                   │
│                                                          │
│  Android : assets://a2ui_icons/home.png                  │
│    → assets/a2ui_icons/home.png                          │
│  iOS     : assets://a2ui_icons/home.png                  │
│    → Bundle.main / a2ui_icons/home.png                   │
│  OHOS    : assets://a2ui_icons/home.png                  │
│    → resources/resfile/a2ui_icons/home.png              │
└─────────────────────────────────────────────────────────┘
```

---

## JSON 协议格式

自定义 Icon **不改变 A2UI 协议**。服务端下发的 Icon 组件保持原有语义，客户端在渲染时自动决定使用 Emoji 还是图片。

### 1. 静态 name（最常见）

```json
{
  "id": "homeIcon",
  "component": "Icon",
  "name": "home"
}
```

- 未注册 icon 时：显示默认 Emoji（🏠）
- 已注册 `home` 时：显示注册的 `home.png`

### 2. 数据绑定 name（path 引用）

```json
{
  "id": "dynamicIcon",
  "component": "Icon",
  "name": { "path": "/currentIcon" }
}
```

结合 `updateDataModel` 更新 `/currentIcon` 为 `"home"` / `"search"` / `"settings"` 时，图片会自动切换。

### 3. 函数调用 name

```json
{
  "id": "computedIcon",
  "component": "Icon",
  "name": {
    "call": "pickIcon",
    "args": { "type": { "path": "/status" } },
    "returnType": "string"
  }
}
```

自定义函数 `pickIcon` 返回的字符串会被用作图标 name，参与 registry 查询。

---

## Icon 命名约定

内置 `ICON_MAP` 使用 **camelCase** 命名（如 `arrowBack`、`accountCircle`、`shoppingCart`）。业务侧的图片资源建议**保持一致**，并在注册时使用相同 key。

### 内置图标名一览（63 项）

```
accountCircle   add             arrowBack       arrowForward
arrowUpward     attachFile      calendarToday   call
camera          check           close           delete
directionsRun   download        edit            event
error           fastForward     favorite        favoriteOff
folder          help            home            info
locationOn      lock            lockOpen        mail
menu            moreHoriz       moreVert        notifications
notificationsOff  pause         payment         person
phone           photo           play            print
priorityHigh    refresh         rewind          search
send            settings        share           shoppingCart
skipNext        skipPrevious    star            starHalf
starOff         stop            trendingUp      upload
visibility      visibilityOff   volumeDown      volumeMute
volumeOff       volumeUp        warning
```

> **注意**：注册**未在 ICON_MAP 中出现的 name** 也被允许（例如业务扩展新图标），只要服务端 JSON 使用对应 name，客户端也能正确渲染。

### 兜底 Fallback

`A2UI_ICON_FALLBACK_KEY` 是一个特殊的注册键，用于**图片模式下未命中时的降级渲染**：

- 只要 `A2UIIconRegistry` 非空（有任意一个注册项），所有 Icon 都走图片模式
- 若 `Icon.name` 在 registry 中未命中（如服务端下发 `"unknownIcon"`），会尝试查找 `_fallback` 键对应的图片
- 建议**始终注册 `_fallback` 项**，为未知图标提供统一占位图（如灰底问号）

---

## 平台接入指南

### Android

#### 1. 放置图片资源

将图片资源放到 `androidAppForNativePage/src/main/assets/a2ui_icons/` 目录（若同时需要在 Kuikly 页接入形态下使用，另一份资源放在 `androidAppForKuiklyPage/src/main/assets/a2ui_icons/`）：

```
androidAppForNativePage/src/main/assets/a2ui_icons/
├── _fallback.png
├── home.png
├── search.png
├── settings.png
└── ...
```

建议规格：

- **格式**：PNG（推荐支持透明背景）
- **尺寸**：72×72 px（3x）或 96×96 px（4x），与 Icon 默认 24dp 尺寸匹配
- **颜色**：单色（推荐纯黑 `#000000` 或品牌色），便于跨主题使用

#### 2. 注册图片

在 `Application.onCreate()` 中注册：

```kotlin
import com.tencent.kuikly.a2ui.registry.A2UIIconRegistry

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerA2UIIcons()
    }

    private fun registerA2UIIcons() {
        A2UIIconRegistry.registerIcons(
            mapOf(
                "home"     to "assets://a2ui_icons/home.png",
                "search"   to "assets://a2ui_icons/search.png",
                "settings" to "assets://a2ui_icons/settings.png",
                // ... 其余图标 ...
                A2UIIconRegistry.FALLBACK_KEY to "assets://a2ui_icons/_fallback.png"
            )
        )
    }
}
```

#### API

| API | 说明 |
|-----|------|
| `A2UIIconRegistry.registerIcons(icons: Map<String, String>)` | 批量注册 name → URI |
| `A2UIIconRegistry.registerIcon(name: String, uri: String)` | 注册单个图标 |
| `A2UIIconRegistry.unregisterIcon(name: String)` | 注销单个图标 |
| `A2UIIconRegistry.clear()` | 清空所有注册项（回到 Emoji 模式） |
| `A2UIIconRegistry.FALLBACK_KEY` | 常量 `"_fallback"`，用于注册未命中兜底图 |

---

### iOS

#### 1. 放置图片资源

将图片放到 iOS Bundle 中，例如 `iosAppForNativePage/Resources/a2ui_icons/`（若同时需要在 Kuikly 页接入形态下使用，另一份资源放在 `iosAppForKuiklyPage/Resources/a2ui_icons/`）：

```
iosAppForNativePage/Resources/a2ui_icons/
├── _fallback.png
├── home.png
├── search.png
├── settings.png
└── ...
```

并在 `project.yml`（或 Xcode 工程配置）中把 `Resources/a2ui_icons` 加入 Bundle Resources。

#### 2. 注册图片

在 `AppDelegate` 或第一个 `A2UISurfaceView` 展示前注册：

```swift
import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        registerA2UIIcons()
        return true
    }

    private func registerA2UIIcons() {
        A2UISurfaceView.registerA2UIIcons([
            "home":     "assets://a2ui_icons/home.png",
            "search":   "assets://a2ui_icons/search.png",
            "settings": "assets://a2ui_icons/settings.png",
            // ... 其余图标 ...
            A2UISurfaceView.iconFallbackKey: "assets://a2ui_icons/_fallback.png",
        ])
    }
}
```

#### API

| API | 说明 |
|-----|------|
| `A2UISurfaceView.registerA2UIIcons(_ icons: [String: String])` | 批量注册 |
| `A2UISurfaceView.registerA2UIIcon(name:uri:)` | 注册单个 |
| `A2UISurfaceView.unregisterA2UIIcon(_ name: String)` | 注销单个 |
| `A2UISurfaceView.iconFallbackKey` | 常量 `"_fallback"` |

> **注意**：iOS 端的 `assets://` scheme 由 `A2UIComponentExpandHandler` 处理，会自动映射到 `Bundle.main` 路径，无需业务手动拼接绝对路径。

---

### HarmonyOS (OHOS)

#### 1. 放置图片资源

将图片放到 **`resources/resfile/a2ui_icons/`** 目录（若同时需要在 Kuikly 页接入形态下使用，另一份资源放在 `ohosAppForKuiklyPage/entry/src/main/resources/resfile/a2ui_icons/`）：

```
ohosAppForNativePage/entry/src/main/resources/resfile/a2ui_icons/
├── _fallback.png
├── home.png
├── search.png
├── settings.png
└── ...
```

> **重要**：必须是 `resfile/`（不是 `rawfile/`）。OHOS 侧 `assets://` scheme 通过 `KRURIHelper::URIForResFile` 映射到 `getContext().resourceDir`，即 `resfile` 目录。

#### 2. 注册图片

在 `UIAbility.onCreate()` 中注册：

```typescript
import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { registerA2UIIcons, A2UI_ICON_FALLBACK_KEY } from '@kuikly-a2ui-open/a2ui-render';

export default class MyAbility extends UIAbility {

  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    this.registerA2UIIconImages();
  }

  private registerA2UIIconImages(): void {
    // 使用 Map（不是对象字面量），ArkTS 严格模式禁止无类型对象字面量
    // 和作为计算属性名的常量（如 [A2UI_ICON_FALLBACK_KEY]）。
    const icons = new Map<string, string>();
    icons.set('home',     'assets://a2ui_icons/home.png');
    icons.set('search',   'assets://a2ui_icons/search.png');
    icons.set('settings', 'assets://a2ui_icons/settings.png');
    // ... 其余图标 ...
    icons.set(A2UI_ICON_FALLBACK_KEY, 'assets://a2ui_icons/_fallback.png');
    registerA2UIIcons(icons);
  }
}
```

#### API

```typescript
// 从 @kuikly-a2ui-open/a2ui-render 导出
function registerA2UIIcons(icons: Map<string, string>): void
function registerA2UIIcon(name: string, uri: string): void
function unregisterA2UIIcon(name: string): void

const A2UI_ICON_FALLBACK_KEY: string  // '_fallback'
```

#### 桥接说明

OHOS 端 ArkTS 无法直接调用 KMP 单例 `A2UIIconRegistry`，因此走**事件通道桥接**：

1. ArkTS 侧 `registerA2UIIcons` 将注册项写入模块级 `a2uiIconRegistry: Map<string, string>`
2. 对每个已存在的 `A2UISurfaceView` 通过 `sendEvent("a2ui_registerIcon", "name\nuri")` 广播到 Kotlin 侧
3. Kotlin 侧 `A2UIBridgeManager` 收到后调用 `A2UIIconRegistry.registerIcon(name, uri)` 写入 KMP 单例
4. 后续创建的 `A2UISurfaceView` 在 `onRenderControllerReady` 中会 **replay** 所有已注册 icon，然后再 flush pending A2UI 消息流——**这个顺序至关重要**，确保 Kotlin 侧 `A2UIComponentMapper` 处理 `createSurface` 时 registry 已就绪，Icon 组件才会被正确路由到 `KRImageView`

---

## 注册时机

- **必须在 `A2UISurfaceView` 加载 JSON 消息流之前完成注册**
- 推荐位置：
    - Android: `Application.onCreate()`
    - iOS: `AppDelegate.application(_:didFinishLaunchingWithOptions:)`
    - OHOS: `UIAbility.onCreate()`
- 注册是**全局的、进程级别**的：一次注册对所有 A2UI 实例生效
- 支持**运行时增量注册**（`registerA2UIIcon(name, uri)`），三端都会广播到已存在的 SurfaceView

---

## View 类型决策（重要）

A2UI 采用 **"注册即全局图片模式"** 策略：

| `A2UIIconRegistry` 状态 | 所有 Icon 的渲染方式 |
|------------------------|--------------------|
| **空** | `KRRichTextView`（Emoji / Unicode 字符） |
| **非空**（哪怕只注册 1 项） | `KRImageView`（图片） |

### 为什么是全局二选一？

Kuikly 节点模型的硬约束：**同一个节点的 view 类型在整个生命周期内不可切换**。如果按"每个 Icon 独立判定"（有图片就用图片、没图片回退到 Emoji），则数据绑定场景下 name 变化跨越 registry 边界时会需要重建节点，实现复杂且易出错。因此我们选择：

- **只要注册了任意图片，视觉风格就完全切换到业务图片模式**
- 未命中的 icon 使用统一的 `_fallback` 占位图，而非回退到 Emoji

这个策略把决策收敛到"应用启动一次性配置"层面，运行时零切换成本。

---

## Demo 参考

三端 Demo 已提供完整示例，注册了全部 63 个 `ICON_MAP` 图标 + 1 个 `_fallback` 占位图（共 64 张 PNG，均为纯黑 Material 风格）：

| 平台 | 注册代码位置 | 图片资源位置 |
|------|------------|-----------|
| Android | `a2ui/androidAppForNativePage/src/main/java/.../demo/CustomIconDemoActivity.kt`（内部直接调用 `A2UIIconRegistry.registerIcons(...)`） | `a2ui/androidAppForNativePage/src/main/assets/a2ui_icons/` |
| iOS | `a2ui/iosShared/Sources/CustomIconDemoViewController.swift` 与 `A2UIDemoIconRegistration.swift`（两个 iOS App 共享，调用 `A2UISurfaceView.registerA2UIIcons(...)`） | `a2ui/iosAppForNativePage/Resources/a2ui_icons/` |
| HarmonyOS | `a2ui/ohosAppForNativePage/entry/src/main/ets/entryability/A2UIDemoAbility.ets`（调用 top-level `registerA2UIIcons(...)`） | `a2ui/ohosAppForNativePage/entry/src/main/resources/resfile/a2ui_icons/` |

Demo 中所有 A2UI 测试 JSON 里出现的 `Icon` 组件（无论 name 是静态还是数据绑定）都会被替换为业务图标；服务端可无感知地下发原始 A2UI 协议。

---

## 常见问题

### Q: 注册后仍然显示 Emoji / 系统图标？

按以下顺序排查：

1. **注册时机**：确认 `registerA2UIIcons` 在**任何 `A2UISurfaceView` 加载 JSON 之前**已执行。可以在注册后立即打印 `A2UIIconRegistry.isEmpty()`（Kotlin 侧）确认。
2. **OHOS 时序**：确认 `A2UISurfaceView.onRenderControllerReady` 中 replay Icon 的动作**先于** flush pending stream chunks。如果顺序颠倒，首帧的 Icon 会因为 registry 尚未同步到 Kotlin 侧而落回文本模式。
3. **图片路径**：确认 URI 格式为 `assets://a2ui_icons/xxx.png`，且文件确实存在于对应平台的 assets / Bundle / resfile 目录下。

### Q: 注册后所有 Icon 都变成了 fallback 占位图？

说明 registry 非空判定生效了（Icon 都走 KRImageView），但每个具体的 name 都没有命中：

1. 检查注册时的 key 拼写是否与 A2UI JSON 中的 `name` 完全一致（区分大小写；建议全用 camelCase）
2. 检查 Kotlin 侧 `A2UIIconRegistry` 的实际内容是否与 ArkTS 侧一致（OHOS 桥接场景）
3. 服务端下发的图标名是否在你注册的清单里——如果是新图标，需要**同步扩充注册**

### Q: 数据绑定的 Icon 图片不切换？

`name` 通过 `path` / `functionCall` 绑定时，绑定值变化会触发 `Icon.src` 属性的 update：

1. 确认 `updateDataModel` 消息确实到达了 A2UI Core
2. 确认绑定值解析后的字符串在 registry 中能命中；如果解析结果是 `null` / 空字符串，会回退到 fallback
3. 视图类型（KRImageView vs KRRichTextView）在**首次创建**时就决定好了；如果首次创建时 registry 为空，之后再注册也不会切换到图片模式——所以务必在应用启动阶段完成注册

### Q: 想临时关闭自定义图片、恢复系统 Emoji？

调用 `A2UIIconRegistry.clear()`（Kotlin 侧）。**注意**：已经创建的 Icon 节点因为 view 类型不可变，仍会显示为图片模式；只有之后新创建的 Icon 才会回到 Emoji。真正做到"完全恢复"需要重建 `A2UISurfaceView`。

### Q: 图片默认尺寸能改吗？

当前默认渲染样式为 `width=24, height=24, margin=8, resizeMode="contain"`（与 Icon 的 24dp 尺寸对齐）。如果需要不同尺寸：

- **服务端方案**：在 A2UI JSON 中给 Icon 组件显式设置 `width` / `height` 覆盖默认值
- **客户端方案**：目前不开放全局尺寸配置，如需支持请提 issue

### Q: `_fallback` 一定要注册吗？

**强烈建议**。如果不注册 `_fallback`：

- 未命中的图标 `src` 会被设置为空字符串
- `KRImageView` 加载空 URI 时通常渲染为空白，UX 上表现为"图标消失"

注册 `_fallback` 后即使漏配某个图标，用户至少能看到一个统一的占位图，便于快速发现问题。

---

## 相关文档

- [自定义函数使用指南](A2UI_CUSTOM_FUNCTION_GUIDE.md)：JSON 表达式绑定 / Action 触发的自定义函数扩展
- [自定义组件使用指南](A2UI_CUSTOM_COMPONENT_GUIDE.md)：注册原生自定义 UI 组件（WebView、地图、图表等）
- [实现设计文档](A2UI_KUIKLY_IMPLEMENTATION_DESIGN.md)：A2UI 核心架构与实现细节
