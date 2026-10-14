# A2UI 接入指南 · Kuikly 页面

> 适用于宿主页本身就是**Kuikly Pager（`@Page` 注解的 Kotlin 类 + Kuikly DSL）**，希望在 DSL 组件树里直接嵌入 A2UI 子树。渲染路径为：Kuikly Pager 的 Flex 树 → `A2UISurface { }` DSL 组件 → A2UI 子树，全程只有一颗组件树、一次布局。

参考实现：仓库中的 [`androidAppForKuiklyPage`](../androidAppForKuiklyPage) / [`iosAppForKuiklyPage`](../iosAppForKuiklyPage) / [`ohosAppForKuiklyPage`](../ohosAppForKuiklyPage) 三个示例工程，以及承载业务 Pager 的 [`:demo`](../demo) KMP 模块。

---

## 目录

1. [选型速览](#选型速览)
2. [业务集成模式](#业务集成模式)
3. [Android 接入](#android-接入)
4. [iOS 接入](#ios-接入)
5. [HarmonyOS (OHOS) 接入](#harmonyos-ohos-接入)
6. [`A2UISurface { }` DSL 用法](#a2uisurface---dsl-用法)
7. [事件回调与命令式 API](#事件回调与命令式-api)
8. [Action 回调协议约定](#action-回调协议约定)
9. [参考实现](#参考实现)

---

## 选型速览

| 场景 | 是否适用 |
|------|---------|
| 宿主是 Kuikly Pager（Kotlin DSL 页），要嵌 A2UI 子树 | ✅ 推荐 |
| 需要 A2UI 与宿主 DSL 组件共享同一颗 Flex 布局树 | ✅ 只有本模式支持 |
| 宿主是纯原生页面 | ❌ 请看 [A2UI_INTEGRATION_NATIVE_PAGE.md](A2UI_INTEGRATION_NATIVE_PAGE.md) |
| 一个 App 里两种页面都要接入 | ⚠️ 依赖需合并，请看 [A2UI_INTEGRATION_BOTH_PAGES.md](A2UI_INTEGRATION_BOTH_PAGES.md) |

**为什么选 Kuikly 页接入**：

- **单 Pager 单布局树**：A2UI 子树是宿主 Kuikly Pager Flex 树的一部分，Flex 联动、约束贯通，无二次布局
- **共享 NativeBridge / Adapter / Modal**：宿主 App 已注册的 `KRImageAdapter` / `KRVideoAdapter` / Modal 等能力 A2UI 自动复用
- **触摸事件与响应式属性天然对齐**：走 Kuikly Core 标准派发链
- **per-subtree flat-layer 开关**：A2UI 子树自动关闭扁平化，仅局部生效，不影响宿主

---

## 业务集成模式

DSL 接入的业务代码 = **Kuikly DSL Kotlin 页面**，因此业务方需要**维护一个自己的 KMP 模块**（就像本仓库的 `:demo`），把带 `@Page` 注解的 Pager 类编进该模块，宿主 App 再依赖它。仓库模块结构参考如下：

```
:core                    ─┐
:a2ui (KMP 库)       ├─►  :demo (业务 KMP 库)  ──►  :androidAppForKuiklyPage
:core-annotations         │    - 业务 @Page 页面示例         (Android 应用壳)
:core-ksp (KSP)           │    - core-ksp 生成 pageName     - 加载 pageName 渲染
:compose                  ─┘    - 里面的 A2UISurface { }
```

宿主 App 侧只负责通过 `KuiklyBaseView.onAttach(pageName=…)`（Android）/ `KuiklyRenderViewControllerBaseDelegator`（iOS）/ `Kuikly({...})`（OHOS）加载页面。

---

## Android 接入

### 1. 依赖

```kotlin
// 业务 KMP 模块（:mybiz）依赖 A2UI 核心 + Kuikly DSL 相关：
dependencies {
    commonMainImplementation("com.tencent.kuikly:core:${kuiklyVersion}")
    commonMainImplementation("com.tencent.kuikly:core-annotations:${kuiklyVersion}")
    commonMainImplementation("com.tencent.kuikly:a2ui:${a2uiVersion}")
    add("kspCommonMainMetadata", "com.tencent.kuikly:core-ksp:${kuiklyVersion}")
    // 如需 Compose DSL：
    commonMainImplementation("com.tencent.kuikly:compose:${kuiklyVersion}")
}

// 宿主 App 模块（:myapp）：
dependencies {
    implementation(project(":mybiz"))                                      // 业务 KMP 页面
    implementation("com.tencent.kuikly:a2ui:${a2uiVersion}")
    implementation("com.tencent.kuikly:a2ui-render-android:${a2uiVersion}")
    implementation("com.tencent.kuikly:core-render-android:${a2uiVersion}")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

### 2. Application 初始化

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UIAndroidNativeMethodHandler
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kuikly 图片/视频/音频 Adapter（同 Native 页）
        KuiklyRenderAdapterManager.krImageAdapter = MyImageAdapter(this)
        KuiklyRenderAdapterManager.krVideoViewAdapter = MyVideoAdapter()
        KuiklyRenderAdapterManager.krAudioViewAdapter = MyAudioAdapter()

        // Kuikly 页面里的 A2UI 组件如需：
        //   - 弹 DateTimePicker（DateTimeInput）
        //   - 执行 functionCall action
        //   - 调用用户注册的 sync 自定义函数
        // 必须调用 install()，把 Android 特定的 native method handler 挂进
        // KMP 的桥接层。仅 Kuikly 页宿主需要，Native 页宿主不需要。
        A2UIAndroidNativeMethodHandler.install(this)
    }
}
```

### 3. Activity 中加载 Kuikly 页面

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UIKuiklyBaseViewSupport
import com.tencent.kuikly.core.render.android.expand.KuiklyBaseView

class MyKuiklyPageActivity : AppCompatActivity() {

    private var renderView: KuiklyBaseView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this)
        setContentView(container)

        container.post {
            // 关键：把 A2UIKuiklyBaseViewSupport.wrap() 作为 delegate 传入，
            // 让 KuiklyBaseView 认识 A2UISurface 这个自定义 DSL 组件。
            val view = KuiklyBaseView(this, A2UIKuiklyBaseViewSupport.wrap())
            container.addView(view, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ))
            view.onAttach(
                contextCode = "",
                pageName = "MyBizPage",              // @Page 注解值
                pageData = emptyMap(),
                size = Size(container.width, container.height),
                assetsPath = null
            )
            renderView = view
        }
    }

    override fun onResume() { super.onResume(); renderView?.onResume() }
    override fun onPause() { super.onPause(); renderView?.onPause() }
    override fun onDestroy() {
        renderView?.onDetach(); renderView = null
        super.onDestroy()
    }
}
```

### 4. Kuikly Pager 里嵌入 `A2UISurface { }`

在业务 KMP 模块里写：

```kotlin
import com.tencent.kuikly.a2ui.dsl.embed.A2UISurface
import com.tencent.kuikly.a2ui.dsl.embed.A2UISurfaceDSLView
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.View

@Page("MyBizPage")
class MyBizPage : Pager() {

    private var surface: A2UISurfaceDSLView? = null

    override fun body() = ViewBuilder {
        View {
            attr { flexDirectionColumn(); padding(16f) }

            RichText { attr { text("宿主 Kuikly DSL 组件") } }

            A2UISurface {
                surface = this                       // 拿到句柄给命令式流控用
                attr { flex(1f) }
                event {
                    action { surfaceId, tag, actionJson ->
                        // A2UI 组件点击回调。actionJson 是完整的 A2UI action JSON，
                        // 根据业务类型建议按以下路线处理：
                        //   - action.event   → 服务端发送事件，客户端自行决定 UI。
                        //                       例如约定 event.name == "toast" 时弹 Kuikly ModalView。
                        //   - action.functionCall → spec 内置只有 openUrl，其余为你注册的自定义函数。
                        // 参见下方“Action 回调协议约定”小节。
                    }
                    openUrl { url ->
                        // 处理 Markdown 链接
                    }
                    dataModelChanged { snapshotJson, changedKey ->
                        // 双向绑定回流（需协议启用 sendDataModel）
                    }
                }
            }
        }
    }

    // 例：网络回调 / SSE 分片调用
    fun onChunkReceived(chunk: String) { surface?.writeStream(chunk) }
    fun onStreamDone() { surface?.finishStream() }
}
```

完整可运行示例：[`A2UISurfaceDemoPager.kt`](../demo/src/commonMain/kotlin/com/tencent/kuikly/a2ui/demo/pages/A2UISurfaceDemoPager.kt)。

---

## iOS 接入

### 1. Podfile

Kuikly 页宿主推荐使用 `pod 'a2ui_demo'`——它把 A2UI KMP 逻辑和业务 KMP 逻辑一并编入同一个 K/N framework（默认名 `A2UIDemo`），运行时只有一个 KMP framework，无 duplicate-symbol 风险。

```ruby
platform :ios, '14.0'

target 'MyApp' do
  use_modular_headers!

  # 承载业务 KMP Pager 的 framework（demo/ 目录的示例）
  pod 'a2ui_demo', :path => '<path-to-biz-kmp-module>'

  # Kuikly 原生渲染层
  pod 'OpenKuiklyIOSRender', :path => '<path-to-repo-root>'

  # A2UI 原生渲染层
  pod 'OpenA2UIIOSRender', :path => '<path-to-a2ui-render-ios>'

  # ⚠️ 不要再单独引 `pod 'a2ui'`——K/N 静态 framework 会产生
  #    duplicate stdlib-cache symbols
end
```

### 2. AppDelegate 初始化

```swift
import UIKit
import OpenA2UIIOSRender

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // 用 pod 'a2ui_demo' 时，K/N framework 名字是业务 KMP 模块生成的
        // framework，比如 `A2UIDemo`。渲染 pod 会用它来反射解析
        // `A2UICoreEntry`，因此必须在任何 A2UI 静态入口之前设置。
        A2UISurfaceView.frameworkName = "A2UIDemo"

        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = MyKuiklyPageViewController()
        window?.makeKeyAndVisible()
        return true
    }
}
```

### 3. ViewController 加载 Kuikly 页

```swift
import UIKit
import OpenKuiklyIOSRender
import OpenA2UIIOSRender

final class MyKuiklyPageViewController: UIViewController {

    private static let pageName = "MyBizPage"

    private var delegator: KuiklyRenderViewControllerBaseDelegator?
    private let container = UIView()

    override func viewDidLoad() {
        super.viewDidLoad()

        // 关键：一次完成两件事 ——
        //  (1) 让 A2UI 侧 customRenderView / customFunction / iconRegistry 的
        //      注册项同步到 KMP-side 的 A2UICustomViewRegistry；
        //  (2) 强制跑一次 Kuikly 的 triggerRegisterPages()，把 @Page 注册到
        //      PagerManager —— 不受 BridgeManager.isDidInit() guard 限制。
        //
        // 为什么 (2) 必须做：任何提前走到的 A2UI 路径
        // （A2UISurfaceView.registerCustomFunction / registerCustomRenderView /
        //  registerA2UIIcons，或 Both 模式下的 Native A2UISurfaceView）都可能
        // 通过 A2UIDSLBridge.initialize() 把 BridgeManager.isDidInit() 翻成
        // true 而不调 triggerRegisterPages()，之后 KSP 生成的
        // KuiklyCoreEntry.callKotlinMethod 会跳过页面注册，createPager 就会
        // 抛 PagerNotFoundException。两参重载幂等，多次调用安全。
        A2UIKuiklyRenderViewSupport.prepareForKuiklyPageHosting(
            pageName: Self.pageName,
            frameworkName: "A2UIDemo"
        )

        setupContainer()

        let d = KuiklyRenderViewControllerBaseDelegator(
            pageName: Self.pageName,
            pageData: [:]
        )
        d.delegate = self
        delegator = d
        d.viewDidLoad(with: container)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        delegator?.viewDidLayoutSubviews()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated); delegator?.viewWillAppear()
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated); delegator?.viewDidAppear()
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated); delegator?.viewWillDisappear()
    }
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated); delegator?.viewDidDisappear()
    }

    private func setupContainer() {
        container.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(container)
        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            container.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }
}

extension MyKuiklyPageViewController: KuiklyRenderViewControllerBaseDelegatorDelegate {
    // 告诉 Kuikly 到哪个 framework 找 @Page 页面（与 frameworkName 一致）
    func fetchContextCode(withPageName pageName: String,
                          resultCallback callback: @escaping KuiklyContextCodeCallback) {
        callback("A2UIDemo", nil)
    }
    func createLoadingView() -> UIView { UIView() }
    func createErrorView() -> UIView   { UIView() }
}
```

---

## HarmonyOS (OHOS) 接入

### 1. 依赖 & Ability 初始化

依赖配置和 KMP 产物构建与 Native 页一致（见 [A2UI_INTEGRATION_NATIVE_PAGE.md](A2UI_INTEGRATION_NATIVE_PAGE.md#harmonyos-ohos-接入)）。Ability 里注册 Video / Audio Adapter：

```typescript
import { KuiklyRenderAdapterManager } from '@kuikly-open/render';

export default class MyAbility extends UIAbility {
  onCreate() {
    KuiklyRenderAdapterManager.krVideoViewAdapter = new MyVideoAdapter();
    KuiklyRenderAdapterManager.krAudioViewAdapter = new MyAudioAdapter();
  }
}
```

### 2. Kuikly 页宿主

```typescript
import { KRNativeRenderController, Kuikly } from '@kuikly-open/render';
import {
  A2UIKuiklyRenderViewSupport,
  a2uiNativeManager
} from '@kuikly-open/a2ui-render';

@Entry
@Component
struct MyKuiklyPage {
  @State ready: boolean = true;
  private controller: KRNativeRenderController | null = null;

  /**
   * 关键：把 A2UIKuiklyRenderViewSupport 的 delegate 挂到 Kuikly，
   * 让宿主 Kuikly Pager 认识 A2UISurface 自定义组件 + A2UIBridgeModule。
   */
  private delegate = A2UIKuiklyRenderViewSupport.wrap();

  onBackPress(): boolean | void {
    return this.controller?.onBackPress() === true;
  }

  build() {
    Stack() {
      if (this.ready) {
        Kuikly({
          pageName: 'MyBizPage',
          pageData: {},
          delegate: this.delegate,
          nativeManager: a2uiNativeManager,           // 关键：A2UI 专属 native manager
          onControllerReadyCallback: (ctrl) => {
            this.controller = ctrl;
            // 关键：把 prepareForKuiklyPageHosting() 期间收集的注册项
            //        回放到刚创建的 pager 上。
            A2UIKuiklyRenderViewSupport.onControllerReadyReplay(ctrl);
          }
        })
      }
    }
    .width('100%')
    .height('100%')
  }
}
```

### 3. C++ 层与 `napi_init.cpp`

与 Native 页一致（详见 [A2UI_INTEGRATION_NATIVE_PAGE.md](A2UI_INTEGRATION_NATIVE_PAGE.md#4-c-层-napi_initcpp)）。

如果需要 Kuikly 页里的 A2UI 支持自定义函数 / 组件 / Icon 注册，`napi_init.cpp` 需要额外暴露 `a2uiRegisterCustomFunction / a2uiRegisterCustomView / a2uiRegisterIcon / a2uiNotifyPickerResult` 等 NAPI 方法。参考实现：[`ohosAppForBothPages/entry/src/main/cpp/napi_init.cpp`](../ohosAppForBothPages/entry/src/main/cpp/napi_init.cpp)。

---

## `A2UISurface { }` DSL 用法

Kuikly DSL 组件 `A2UISurface` 是本模式下 A2UI 子树的入口。它是**普通 DSL 组件**，可以作为任何父容器的子节点，参与父级 Flex 布局。

```kotlin
A2UISurface {
    attr {
        // 常规 View 属性都可用：flex / width / height / margin / padding …
        flex(1f)
        marginTop(8f)
    }
    event {
        action { surfaceId, tag, actionJson -> /* … */ }
        openUrl { url -> /* … */ }
        dataModelChanged { snapshotJson, changedKey -> /* … */ }
    }
}
```

---

## 事件回调与命令式 API

### 事件

| 回调 | 说明 |
|------|------|
| `action { surfaceId, tag, actionJson -> ... }` | A2UI 组件触发 Action。`actionJson` 为完整 JSON，形如 `{"event":{"name":"toast",...}}` 或 `{"functionCall":{"call":"openUrl",...}}`（见下方 [Action 回调协议约定](#action-回调协议约定)） |
| `openUrl { url -> ... }` | Markdown 链接被点击 |
| `dataModelChanged { snapshotJson, changedKey -> ... }` | 双向绑定数据变化（需协议启用 `sendDataModel`） |

### 命令式方法（用 `A2UISurfaceDSLView` 句柄调用）

| 方法 | 说明 |
|------|------|
| `surface.writeStream(chunk)` | 追加一段 A2UI 协议流；`didInit` 之前调用会自动缓冲 |
| `surface.finishStream()` | 通知流结束，触发最终布局 |
| `surface.resetStream()` | 清空当前 A2UI 子树、重置 parser，用于**替换**内容而非追加 |

---

## Action 回调协议约定

A2UI v0.9 协议里 `Button.action` 有两种形态，`action { surfaceId, tag, actionJson -> … }` 回调收到的 `actionJson` 也对应两种结构：

| Action 类型 | JSON 结构 | 语义 | Kuikly a2ui 内置支持 |
|---|---|---|---|
| `action.event` | `{"event":{"name":"...","context":{...}}}` | 通知宿主"发生了什么"，由宿主决定后续行为 | ✅ **推荐**，宿主在 `action { }` 里自行处理 |
| `action.functionCall` | `{"functionCall":{"call":"...","args":{...},"returnType":"void"}}` | 直接调用一个客户端函数 | 仅 `openUrl` 是 spec 内置；其它需通过 `registerCustomFunction` 注册 |

**为什么是这样**：A2UI v0.9 basic catalog 里内置 `action.functionCall` 只保留了 `openUrl` 一个 action 型函数。诸如"弹 alert"、"跳转页面"、"埋点"这类业务行为，spec 明确要求走 `action.event` —— 服务端下发事件、由宿主用原生 / Kuikly UI 响应。

**Demo 里的约定**：本仓库 KMP demo（`A2UISurfaceDemoPager`）统一约定 `event.name == "toast"` 表示"弹一个提示"，`event.context.title` / `event.context.message` 为标题和内容。宿主 Pager 可以在 `action { }` 里解析 `event` JSON 并弹 Kuikly `ModalView` / 派发路由 / 跳转 URL 等。

**Kotlin 侧最小示例**：

```kotlin
event {
    action { surfaceId, tag, actionJson ->
        val obj = JSONObject(actionJson)
        val event = obj.optJSONObject("event")
        if (event?.optString("name") == "toast") {
            val ctx = event.optJSONObject("context")
            val title = ctx?.optString("title").orEmpty()
            val message = ctx?.optString("message").orEmpty()
            // 展示 Kuikly ModalView / RichText header 等原生反馈
        }
    }
}
```

**告警日志**：当 JSON 里的 `action.functionCall` 使用了既非注册的自定义函数、也非 spec 保留名（当前仅 `openUrl`）时，`A2UIBridgeManager` 会打印一条 `[A2UI] warn: …` 提示，帮助你快速发现拼写错误或非 spec 用法。

---

## 参考实现

| 平台 | 参考路径 |
|------|---------|
| Android Kuikly 页宿主 | [`androidAppForKuiklyPage`](../androidAppForKuiklyPage) |
| iOS Kuikly 页宿主 | [`iosAppForKuiklyPage`](../iosAppForKuiklyPage) |
| OHOS Kuikly 页宿主 | [`ohosAppForKuiklyPage`](../ohosAppForKuiklyPage) |
| 业务 KMP 模块示例 | [`:demo`](../demo) — 内含 `A2UISurfaceDemoPager` 等 `@Page` 示例 |
