# A2UI 接入指南 · 同一 App 中同时接入 Native 页 & Kuikly 页

> 适用于**一个 App 里同时存在两类页面**：一部分是原生页（`Activity` / `UIViewController` / ArkTS `@Entry`），一部分是 Kuikly DSL Pager 页。二者都要能接入 A2UI。

本文档**只讲**依赖合并要点和平台陷阱。**接入代码**请查阅：

- [A2UI 接入指南 · Native 页面](A2UI_INTEGRATION_NATIVE_PAGE.md)
- [A2UI 接入指南 · Kuikly 页面](A2UI_INTEGRATION_KUIKLY_PAGE.md)

参考实现：仓库中的 [`androidAppForBothPages`](../androidAppForBothPages) / [`iosAppForBothPages`](../iosAppForBothPages) / [`ohosAppForBothPages`](../ohosAppForBothPages) 三个示例工程。

---

## 目录

1. [依赖合并总览](#依赖合并总览)
2. [Android 平台](#android-平台)
3. [iOS 平台](#ios-平台)
4. [HarmonyOS (OHOS) 平台](#harmonyos-ohos-平台)
5. [Action 回调协议约定](#action-回调协议约定)
6. [常见陷阱汇总](#常见陷阱汇总)

---

## 依赖合并总览

| 关注点 | Android | iOS | HarmonyOS |
|--------|---------|-----|-----------|
| A2UI 核心 KMP | 一份 `:a2ui` 依赖 | 一个 K/N framework（`a2ui_demo`） | 一份 `libshared.so` |
| Kuikly 渲染层 | `core-render-android` | `OpenKuiklyIOSRender` | `@kuikly-open/render` |
| A2UI 渲染层 | `a2ui-render-android` | `OpenKuiklyA2UIIOSRender` | `@kuikly-a2ui-open/render` |
| 业务 KMP 模块 | 必须（用于 Kuikly 页承载） | 必须（用于 Kuikly 页承载） | 必须（用于 Kuikly 页承载） |
| 全局初始化 | Application 里做 A + K 两侧初始化 | AppDelegate 一次设置 frameworkName | Ability 里做 A + K 两侧初始化 |

**核心原则**：Both 模式下**不要**同时链两份 A2UI KMP（例如 iOS `pod 'a2ui' + pod 'a2ui_demo'`）—— 静态 K/N framework 会产生重复的 stdlib-cache 符号或 runtime bridge 混乱。始终**只保留一份 A2UI KMP 产物**。

---

## 版本号速查（产物模式）

下文所有示例里出现的 `${a2uiVersion}` / `${kuiklyVersion}` 占位符，按平台替换为下表的值（当前默认版本）：

| 变量 | Android / KMP Maven 坐标 | iOS CocoaPods / OHOS ohpm | 说明 |
|------|--------------------------|---------------------------|------|
| `${a2uiVersion}` | `1.0.0-2.1.21` | `1.0.0` | 本仓库发布的 A2UI 产物版本 |
| `${kuiklyVersion}` | `2.30.0-2.1.21` | `2.30.0` | 底层 Kuikly 产物版本 |
| HarmonyOS 的 Android/KMP Maven 坐标 | `1.0.0-2.0.21-ohos` / `2.30.0-2.0.21-ohos` | 同上 | 仅 HarmonyOS 2.0 兼容构建时需要 |

> **源码模式**：本仓示例工程 `*AppForBothPages` 使用 `project(":a2ui")` 风格的工程依赖，由 `A2uiDep` / `KuiklyDep` 根据 `a2ui.useSource` / `kuikly.useSource` 开关自动切换，详见 [`SETUP-zh_CN.md`](../SETUP-zh_CN.md)。

---

## Android 平台

### 依赖合并

Kuikly 页宿主要求业务 KMP 模块（用于承载 `@Page` Pager）：

**产物模式（下游业务接入推荐）**：

```kotlin
// 业务 KMP 模块（承载 @Page 页面）——配置同 Kuikly 页宿主模式
//   详见 A2UI_INTEGRATION_KUIKLY_PAGE.md

// 宿主 App 模块：
dependencies {
    implementation(project(":mybiz"))                                                 // 业务 KMP
    implementation("com.tencent.kuikly-a2ui-open:a2ui:${a2uiVersion}")                 // A2UI 核心
    implementation("com.tencent.kuikly-a2ui-open:a2ui-render-android:${a2uiVersion}")
    implementation("com.tencent.kuikly-open:core-render-android:${kuiklyVersion}")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

**源码模式（仅本仓内示例工程使用）**：

```kotlin
// androidAppForBothPages/build.gradle.kts（本仓示例）
dependencies {
    implementation(project(":demo"))
    implementation(A2uiDep.a2ui(project))
    implementation(A2uiDep.a2uiRenderAndroid(project))
    implementation(KuiklyDep.coreRenderAndroid(project))
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

> 相较单独接入，Both 模式的 App 依赖 = Native 页 App 依赖 **∪** Kuikly 页 App 依赖，本质上是取并集。**`:compose` 不需要**（Both 模式的示例不涉及 Compose 集成）。

### Application 初始化

Both 模式**必须**把 Kuikly Adapter + `A2UIAndroidNativeMethodHandler.install()` 都装上：

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UIAndroidNativeMethodHandler
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // ①【Native 页 & Kuikly 页共用】Kuikly Adapter
        KuiklyRenderAdapterManager.krImageAdapter = MyImageAdapter(this)
        KuiklyRenderAdapterManager.krVideoViewAdapter = MyVideoAdapter()
        KuiklyRenderAdapterManager.krAudioViewAdapter = MyAudioAdapter()

        // ②【仅 Kuikly 页宿主需要，但 Both 模式必须装】
        // 让 Kuikly 页里的 A2UI 组件能弹 DatePicker / 跑 functionCall
        // / 调用同步自定义函数。
        A2UIAndroidNativeMethodHandler.install(this)
    }
}
```

参考实现：[`androidAppForBothPages/A2UIDemoApplication.kt`](../androidAppForBothPages/src/main/java/com/tencent/kuikly/a2ui/demo/A2UIDemoApplication.kt)。

### Activity 侧

两类页面的 Activity 代码与单独接入完全一致，直接沿用即可。

---

## iOS 平台

### Podfile 合并（**最重要**）

**产物模式（下游业务接入推荐）**：

```ruby
platform :ios, '15.0'

target 'MyApp' do
  use_modular_headers!

  # ⚠️ 关键：Both 模式只能引 `pod 'a2ui_demo'`。
  # A2UIDemo.framework 已经把 :a2ui 的所有 Kotlin 符号打进去了
  #（见业务 KMP 模块的 build.gradle.kts），一个 K/N framework 就够两类
  # 页面共用了。同时链 `pod 'a2ui'` 会产生 duplicate stdlib-cache 符号。
  pod 'a2ui_demo', :path => '<path-to-biz-kmp-module>'

  pod 'OpenKuiklyIOSRender',     '2.30.0'
  pod 'OpenKuiklyA2UIIOSRender', '1.0.0'
end
```

**源码模式（仅本仓内示例工程使用）**：

```ruby
platform :ios, '15.0'

target 'MyApp' do
  use_modular_headers!

  pod 'a2ui_demo',               :path => '<path-to-biz-kmp-module>'
  pod 'OpenKuiklyIOSRender',     :path => '<path-to-repo-root>'
  pod 'OpenKuiklyA2UIIOSRender', :path => '<path-to-repo-root>'
end
```

参考实现：[`iosAppForBothPages/Podfile`](../iosAppForBothPages/Podfile)（含关键注释原文）。

### AppDelegate 初始化

Both 模式下**必须**显式指定 `frameworkName` 为业务 KMP framework（例如 `A2UIDemo`）——因为渲染 pod 会用它反射解析 `A2UICoreEntry`：

```swift
import OpenKuiklyA2UIIOSRender

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // 关键：必须在任何 A2UI 静态入口之前设置。
        A2UISurfaceView.frameworkName = "A2UIDemo"
        // ...
        return true
    }
}
```

### Both 模式 iOS 特有陷阱 —— Kuikly 页宿主 `viewDidLoad` 里的 warm-up

这是 Both 模式**最容易踩的坑**：

> **场景**：用户在 App 里先访问了 Native 页（走 `A2UISurfaceView` + `writeStream`），再切换到 Kuikly 页。Kuikly 页 `createPager("MyBizPage")` 抛出 `PagerNotFoundException`。

**原因**：`A2UIDSLBridge.initialize()` 会调 `BridgeManager.init(true)`，把 `isDidInit()` 直接翻转成 `true` —— 但它**不触发** `triggerRegisterPages()`。之后 KSP 生成的 `KuiklyCoreEntry.callKotlinMethod(...)` 走进 `if (!BridgeManager.isDidInit()) { ... }` 分支就会被跳过，注册 @Page 的时机彻底错过，`createPager` 找不到目标页。

**修复**：Kuikly 页宿主 VC 的 `viewDidLoad` 里直接调用一次带 `pageName:frameworkName:` 的两参重载：`A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(pageName:frameworkName:)`。它在无参版的基础上会额外走一次 `KuiklyBaseView.isPageExist(withPageName:frameworkName:)` warm-up —— 后者一定会跑 `triggerRegisterPages()`（不受 `isDidInit()` guard 限制），且是幂等操作：

```swift
override func viewDidLoad() {
    super.viewDidLoad()

    // 建议所有 Kuikly 页宿主 VC 都调用两参重载 —— 不只 Both 模式。
    // 单纯接入 Kuikly 页看似"没有 Native 页抢先调 BridgeManager.init(true)"，
    // 但一旦同进程里存在 A2UI 相关的注册路径（customFunction / customRenderView /
    // iconRegistry 等），这些路径可能在 Kuikly Render 的 context 线程进入
    // callKotlinMethod 之前先一步翻转 isDidInit()，造成偶发 PagerNotFoundException。
    A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
        pageName: "MyBizPage",
        frameworkName: "A2UIDemo"
    )
    // ... 后续 KuiklyRenderViewControllerBaseDelegator 初始化
}
```

完整参考：[`iosAppForBothPages/KuiklyPageCardViewController.swift`](../iosAppForBothPages/Sources/KuiklyPageCardViewController.swift) 以及 [`iosAppForKuiklyPage/A2UISurfaceDSLDemoViewController.swift`](../iosAppForKuiklyPage/Sources/A2UISurfaceDSLDemoViewController.swift)（含详细注释原文）。

---

## HarmonyOS (OHOS) 平台

### 依赖合并

**产物模式（下游业务接入推荐）**：

```json5
// entry/oh-package.json5
{
  "dependencies": {
    "@kuikly-open/render": "2.30.0",
    "@kuikly-a2ui-open/render": "1.0.0"
  }
}
```

**源码/`file:` 模式（仅本仓内示例工程使用）**：

```json5
// entry/oh-package.json5
{
  "dependencies": {
    "@kuikly-open/render": "file:../../core-render-ohos",
    "@kuikly-a2ui-open/render": "file:../../a2ui-render-ohos"
  }
}
```

**只需要一份 `libshared.so`**（业务 KMP 产物），两类页面共用。构建脚本与单独接入一致：

```bash
./a2ui/2.0_a2ui_ohos_build.sh          # Debug
./a2ui/2.0_a2ui_ohos_build_release.sh  # Release
```

### Ability 初始化

```typescript
import { KuiklyRenderAdapterManager } from '@kuikly-open/render';

export default class MyAbility extends UIAbility {
  onCreate() {
    // 视频 / 音频 Adapter（两类页面共用）
    KuiklyRenderAdapterManager.krVideoViewAdapter = new MyVideoAdapter();
    KuiklyRenderAdapterManager.krAudioViewAdapter = new MyAudioAdapter();
  }
}
```

参考实现：[`ohosAppForBothPages/entry/src/main/ets/entryability/A2UIDemoAbility.ets`](../ohosAppForBothPages/entry/src/main/ets/entryability/A2UIDemoAbility.ets)。

### 页面侧

两类页面 ArkTS 代码与单独接入完全一致：

- **Native 页**：直接用 `A2UISurfaceView({ ... onReady })` 或便捷组件 `A2UISurface({ messages, finished })`。示例：[`NativeCardDemoPage.ets`](../ohosAppForBothPages/entry/src/main/ets/pages/NativeCardDemoPage.ets)
- **Kuikly 页**：用 `Kuikly({ delegate: A2UIKuiklyRenderViewSupport.wrap(), nativeManager: a2uiNativeManager, onControllerReadyCallback })`，callback 里必须调用 `A2UIKuiklyRenderViewSupport.onControllerReadyReplay(ctrl)` 回放注册项。示例：[`KuiklyPageCardDemoPage.ets`](../ohosAppForBothPages/entry/src/main/ets/pages/KuiklyPageCardDemoPage.ets)

### OHOS 特有说明

- OHOS 上 A2UI 的 Native 页和 Kuikly 页共享同一份 `libshared.so` 和 `A2UIDemoAbility`，**不存在** iOS 那种 framework 名字选择问题
- OHOS 上没有 iOS Both 模式的 `PagerNotFoundException` 陷阱：`Kuikly({...})` 组件启动流程中一定会走完 `triggerRegisterPages()`，不受 A2UI Native 页是否先启动影响
- 如果需要 Kuikly 页里的 A2UI 支持自定义函数 / 组件 / Icon 注册，`napi_init.cpp` 需要暴露 `a2uiRegisterCustomFunction / a2uiRegisterCustomView / a2uiRegisterIcon / a2uiNotifyPickerResult` 等 NAPI 方法。参考实现：[`ohosAppForBothPages/entry/src/main/cpp/napi_init.cpp`](../ohosAppForBothPages/entry/src/main/cpp/napi_init.cpp)

---

## Action 回调协议约定

`A2UISurfaceView.onAction`（Native 页宿主）和 `A2UISurface { }` 的 `event.action { … }`（Kuikly 页宿主）收到的都是同一份 A2UI action JSON，且两种页面里协议行为完全一致：

- `action.event` → 服务端下发事件，宿主自己响应（推荐路径）
- `action.functionCall` → spec 内置仅 `openUrl`，其它需 `registerCustomFunction`

Both 模式下要注意：**如果只在 Native 页宿主里注册了 alert / event 处理逻辑，Kuikly 页里的 A2UI 组件不会自动继承**。两种宿主要各自实现自己的 event 分发，或者抽出一个共享的 handler 让两端调用。

详细规则与示例见：

- [Native 页文档 · Action 回调协议约定](./A2UI_INTEGRATION_NATIVE_PAGE.md#action-回调协议约定)
- [Kuikly 页文档 · Action 回调协议约定](./A2UI_INTEGRATION_KUIKLY_PAGE.md#action-回调协议约定)

---

## 常见陷阱汇总

| # | 平台 | 陷阱 | 解决 |
|---|------|------|------|
| 1 | iOS | 同时 `pod 'a2ui'` + `pod 'a2ui_demo'` | 只保留 `pod 'a2ui_demo'` |
| 2 | iOS | 忘记 `A2UISurfaceView.frameworkName = "A2UIDemo"` | AppDelegate 首行设置，早于任何 A2UI 入口 |
| 3 | iOS | Native 页先访问后再进 Kuikly 页，`PagerNotFoundException` | Kuikly 页 VC 的 `viewDidLoad` 里调用 `A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(pageName:frameworkName:)` 两参重载 |
| 4 | Android | Kuikly 页里的 A2UI 弹不出 DatePicker、functionCall 不响应 | Application 里 `A2UIAndroidNativeMethodHandler.install(this)` |
| 5 | Android | Kuikly 页宿主 Activity 无法识别 `A2UISurface` 组件 | `KuiklyBaseView(this, A2UIKuiklyBaseViewSupport.wrap())` |
| 6 | OHOS | Kuikly 页 A2UI 自定义函数不生效 | `napi_init.cpp` 暴露对应 NAPI 方法 + Kuikly 页 delegate 用 `A2UIKuiklyRenderViewSupport.wrap()` + `onControllerReadyReplay` |
| 7 | 三端 | 图片 / 视频 / 音频不显示 | Application/Ability 里注册对应 Adapter |
| 8 | 三端 | 单次 `writeStream()` 传多条 envelope | 拆包，每次只传一个 JSON 对象 |
