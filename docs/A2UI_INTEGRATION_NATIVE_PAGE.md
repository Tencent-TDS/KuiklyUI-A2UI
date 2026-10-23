# A2UI 接入指南 · Native 页面

> 适用于宿主是**纯原生页面**（Android `Activity`、iOS `UIViewController`、HarmonyOS ArkTS `@Entry`）、不使用 Kuikly DSL Pager 的场景。A2UI 内容以 `A2UISurfaceView` 作为一块子 View 嵌入原生视图树。

参考实现：仓库中的 [`androidAppForNativePage`](../androidAppForNativePage) / [`iosAppForNativePage`](../iosAppForNativePage) / [`ohosAppForNativePage`](../ohosAppForNativePage) 三个示例工程。

---

## 目录

1. [选型速览](#选型速览)
2. [Android 接入](#android-接入)
3. [iOS 接入](#ios-接入)
4. [HarmonyOS (OHOS) 接入](#harmonyos-ohos-接入)
5. [Action 回调协议约定](#action-回调协议约定)
6. [JSON 消息流格式](#json-消息流格式)
7. [常见问题](#常见问题)

---

## 选型速览

| 场景 | 是否适用 |
|------|---------|
| 宿主是原生 App，UI 都是原生代码写的 | ✅ 推荐 |
| 宿主页本身是 Kuikly Pager，想内嵌 A2UI 子树 | ❌ 请看 [A2UI_INTEGRATION_KUIKLY_PAGE.md](A2UI_INTEGRATION_KUIKLY_PAGE.md) |
| 一个 App 里两种页面都要接入 | ⚠️ 依赖需合并，请看 [A2UI_INTEGRATION_BOTH_PAGES.md](A2UI_INTEGRATION_BOTH_PAGES.md) |

---

## Android 接入

### 1. 添加依赖

```kotlin
// build.gradle.kts (业务模块)
// 版本号格式：${kuiklyVersion}-${kotlinVersion}
val a2uiVersion = "1.0.0-2.1.21"

dependencies {
    // A2UI 核心（KMP）
    implementation("com.tencent.kuikly-a2ui-open:a2ui:${a2uiVersion}")
    // A2UI Android 渲染层（提供 A2UISurfaceView）
    implementation("com.tencent.kuikly-a2ui-open:a2ui-render-android:${a2uiVersion}")
    // Kuikly 渲染层
    implementation("com.tencent.kuikly:core-render-android:${a2uiVersion}")
    // 图片加载（推荐 Glide）
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

### 2. Application 初始化

在 `Application.onCreate()` 中注册 Kuikly Adapter：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 图片 Adapter（必须，否则 Image 组件无法显示）
        if (KuiklyRenderAdapterManager.krImageAdapter == null) {
            KuiklyRenderAdapterManager.krImageAdapter = MyImageAdapter(this)
        }
        // 视频 Adapter（可选，只有用到 Video 组件时需要）
        if (KuiklyRenderAdapterManager.krVideoViewAdapter == null) {
            KuiklyRenderAdapterManager.krVideoViewAdapter = MyVideoAdapter()
        }
        // 音频 Adapter（可选，只有用到 AudioPlayer 组件时需要）
        if (KuiklyRenderAdapterManager.krAudioViewAdapter == null) {
            KuiklyRenderAdapterManager.krAudioViewAdapter = MyAudioAdapter()
        }
    }
}
```

### 3. 在 Activity 中使用 `A2UISurfaceView`

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceViewListener

class MyActivity : AppCompatActivity() {

    private var surfaceView: A2UISurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this)
        val surface = A2UISurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT  // 或 MATCH_PARENT
            )
        }
        container.addView(surface)
        setContentView(container)

        // configJson 可以传 "{}"，也可以指定屏幕宽度等配置
        surface.init("{}")
        surface.setListener(object : A2UISurfaceViewListener {
            override fun onAction(surfaceId: String, tag: Int,
                                  action: String, params: String?) {
                // A2UI 组件点击回调。action 是完整的 JSON，形如：
                //   {"event":{"name":"toast","context":{"title":"...","message":"..."}}}
                //   {"functionCall":{"call":"openUrl","args":{"url":"..."}}}
                // 建议按 event.name 分派，例如：
                //   val evt = JSONObject(action).optJSONObject("event") ?: return
                //   when (evt.optString("name")) {
                //     "toast" -> AlertDialog.Builder(this).setMessage(...).show()
                //     ...
                //   }
            }
            override fun onDataModelChanged(dataModelJson: String, changedKey: String) = Unit
            override fun onOpenUrl(url: String) {
                // 处理 Markdown 链接点击
            }
        })

        // 写入协议流：每次 writeStream() 只能传 **一个** JSON envelope
        surface.writeStream(createSurfaceEnvelope)
        surface.writeStream(updateComponentsEnvelope)
        surface.finishStream()   // 标记结束，触发最终布局

        surfaceView = surface
    }

    override fun onDestroy() {
        surfaceView?.destroy()
        surfaceView = null
        super.onDestroy()
    }
}
```

### 4. 注册自定义组件 / 函数（可选）

```kotlin
// 自定义组件
// 注意：measurer 的输入/输出都是 Kuikly 的**逻辑单位 dp**，不要在这里
// 乘 `displayMetrics.density`——Kuikly native render handler 会在
// 布局到 Android View 时自己把 dp 转 px。
A2UISurfaceView.registerCustomRenderView(
    viewName = "A2UIWeb",
    creator = { context -> A2UIWebView(context) },
    measurer = A2UIAndroidCustomViewRegistry.A2UICustomViewMeasurer { cwDp, _ ->
        SizeF(cwDp, 240f)                       // 宽：父约束（dp）；高：240dp
    }                                           // 可选
)

// 自定义函数
A2UISurfaceView.registerCustomFunction(
    name = "formatPhone",
    returnType = A2UISurfaceView.RETURN_TYPE_STRING,
    invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
) { args -> args.optString("phone", "") }
```

详见 [A2UI_CUSTOM_COMPONENT_GUIDE.md](A2UI_CUSTOM_COMPONENT_GUIDE.md) / [A2UI_CUSTOM_FUNCTION_GUIDE.md](A2UI_CUSTOM_FUNCTION_GUIDE.md)。

---

## iOS 接入

### 1. Podfile

```ruby
platform :ios, '15.0'

target 'MyApp' do
  use_modular_headers!

  # Kuikly iOS 原生渲染层（已发布到 CocoaPods Trunk，正式接入时可去 :path）
  pod 'OpenKuiklyIOSRender', :path => '<path-to-repo-root>'

  # A2UI iOS 渲染层（尚未发布到 Trunk，暂时本地路径）
  pod 'OpenKuiklyA2UIIOSRender', :path => '<path-to-a2ui-render-ios>'

  # A2UI 核心（KMP）——由 Kotlin cocoapods 插件自动生成 podspec，
  # 每次 Xcode Build 会触发 Gradle 编译 A2UICore.framework
  pod 'a2ui', :path => '<path-to-a2ui-module>'
end
```

首次 `pod install` 前需要生成 dummy framework：

```bash
./gradlew :a2ui:generateDummyFramework
```

### 2. AppDelegate 初始化

Native 页宿主使用 `pod 'a2ui'`，运行时 Kotlin/Native framework 就是 `A2UICore`。因为这是默认值，通常不需要手动指定 `frameworkName`。

```swift
import UIKit
import OpenKuiklyA2UIIOSRender

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // 默认 frameworkName = "A2UICore"，与 pod 'a2ui' 对应，通常无需改。
        // 若使用 pod 'a2ui_demo'（把 A2UI 打进业务 KMP framework）则需指定：
        // A2UISurfaceView.frameworkName = "MyBizFramework"

        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = ViewController()
        window?.makeKeyAndVisible()
        return true
    }
}
```

> **说明**：`import OpenKuiklyA2UIIOSRender` 即可，不要 `import A2UICore`。渲染 pod 会用 `NSClassFromString` 在运行时按 `frameworkName` 解析 `A2UICoreEntry`，避免将 KMP 类型泄漏到业务代码。

### 3. UIViewController 使用 `A2UISurfaceView`

```swift
import UIKit
import OpenKuiklyA2UIIOSRender

class MyViewController: UIViewController {

    private var surfaceView: A2UISurfaceView?

    override func viewDidLoad() {
        super.viewDidLoad()

        let surface = A2UISurfaceView()
        surface.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(surface)
        NSLayoutConstraint.activate([
            surface.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            surface.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            surface.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            surface.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])

        surface.initSurface(configJson: "{}")
        surface.surfaceListener = self

        surface.writeStream(createSurfaceEnvelope)
        surface.writeStream(updateComponentsEnvelope)
        surface.finishStream()

        surfaceView = surface
    }

    deinit { surfaceView?.destroy() }
}

extension MyViewController: A2UISurfaceViewDelegate {
    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView,
                         didReceiveAction action: String,
                         fromTag tag: Int,
                         inSurface surfaceId: String) {
        // A2UI 组件点击回调。action 是完整的 JSON，建议按 event.name 分派：
        //   guard let data = action.data(using: .utf8),
        //         let obj = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
        //         let event = obj["event"] as? [String: Any],
        //         let name = event["name"] as? String else { return }
        //   switch name {
        //   case "toast": /* 弹 UIAlertController */ break
        //   default: break
        //   }
    }
    // 其余方法均为可选：didCreateSurface / didFinishStream /
    // didEncounterError / didChangeDataModel
}
```

### 4. 注册自定义组件 / 函数

```swift
A2UISurfaceView.registerCustomRenderView(viewName: "A2UIWeb") {
    A2UIWebView()   // UIView 子类 + KuiklyRenderViewExportProtocol
}

A2UISurfaceView.registerCustomFunction(
    name: "formatPhone",
    returnType: A2UISurfaceView.returnTypeString,
    invokeMode: A2UISurfaceView.invokeModeSync
) { args in args["phone"] as? String ?? "" }
```

---

## HarmonyOS (OHOS) 接入

### 1. 依赖

在 `entry/oh-package.json5` 中：

```json5
{
  "dependencies": {
    "@kuikly-open/render": "2.0.0",
    "@kuikly-a2ui-open/render": "file:../../a2ui-render-ohos"
  }
}
```

### 2. 构建并放置 KMP 产物

A2UI 核心 KMP 代码在 OHOS 上以 `libshared.so` 形式集成，需要手动构建：

```bash
# Debug（开发调试）
./a2ui/2.0_a2ui_ohos_build.sh
# 或 Release（正式发布）
./a2ui/2.0_a2ui_ohos_build_release.sh
```

产物会自动放入：
- `entry/libs/arm64-v8a/libshared.so`
- `entry/src/main/cpp/thirdparty/biz_entry/libshared_api.h`

同时需要放入 `entry/libs/arm64-v8a/libkuikly_entry.so`（Kuikly C++ 入口）。

### 3. Ability 初始化

```typescript
import { UIAbility } from '@kit.AbilityKit';
import { KuiklyRenderAdapterManager } from '@kuikly-open/render';

export default class MyAbility extends UIAbility {
  onCreate() {
    // 视频 / 音频 Adapter（仅当协议中用到 Video / AudioPlayer 时必需）
    if (KuiklyRenderAdapterManager.krVideoViewAdapter == null) {
      KuiklyRenderAdapterManager.krVideoViewAdapter = new MyVideoAdapter();
    }
    if (KuiklyRenderAdapterManager.krAudioViewAdapter == null) {
      KuiklyRenderAdapterManager.krAudioViewAdapter = new MyAudioAdapter();
    }
  }
  // ...
}
```

### 4. C++ 层 napi_init.cpp

```cpp
#include "napi/native_api.h"
#include "thirdparty/biz_entry/libshared_api.h"

static napi_value InitKuikly(napi_env env, napi_callback_info info) {
    auto api = libshared_symbols();
    int handler = api->kotlin.root.initKuikly_();
    napi_value result;
    napi_create_int32(env, handler, &result);
    return result;
}
```

> 自定义组件通过 ArkTS 侧的 `registerA2UICustomRenderView(...)` 注册即可（见 [A2UI_CUSTOM_COMPONENT_GUIDE.md](A2UI_CUSTOM_COMPONENT_GUIDE.md)），`napi_init.cpp` 不再需要注册 Shadow。Kuikly 会为未显式注册 C++ Shadow 的 viewName 使用内置默认 Shadow 兜底，彻底避免 `libkuikly.so` 与 `libkuikly_entry.so` 之间的 C++ ABI 耦合问题。

### 5. ArkTS 页面使用

`A2UISurfaceView` 直接暴露 controller 语义：

```typescript
import { A2UISurfaceView, A2UISurfaceViewController, A2UISurfaceViewListener }
  from '@kuikly-a2ui-open/render';

@Entry
@Component
struct MyPage {
  private controller: A2UISurfaceViewController | null = null;

  private listener: A2UISurfaceViewListener = {
    onAction: (surfaceId, tag, action, params) => {
      // A2UI 组件点击回调。action 是完整的 JSON，建议按 event.name 分派：
      //   const obj = JSON.parse(action);
      //   const event = obj.event;
      //   if (event?.name === 'toast') {
      //     AlertDialog.show({ title: event.context?.title, message: event.context?.message,
      //                        confirm: { value: 'OK', action: () => {} } });
      //   }
      console.info(`Action: ${action}`);
    }
    // 其余回调均为可选
  };

  build() {
    Column() {
      A2UISurfaceView({
        listener: this.listener,
        onReady: (ctrl: A2UISurfaceViewController) => {
          this.controller = ctrl;
          ctrl.writeStream(createSurfaceEnvelope);
          ctrl.writeStream(updateComponentsEnvelope);
          ctrl.finishStream();
        }
      })
        .width('100%')
        .height('100%')
    }
  }
}
```

如果协议流已经打包成 JSON 数组，可用便捷组件 `A2UISurface`（示例见 [`ohosAppForNativePage`](../ohosAppForNativePage) 中的 `A2UISurface.ets`）：

```typescript
A2UISurface({
  messages: '[{"version":"v0.9","createSurface":{...}},{"version":"v0.9","updateComponents":{...}}]',
  finished: true
})
```

### 6. 注册自定义组件 / 函数

```typescript
import {
  registerA2UICustomRenderView,
  registerA2UICustomFunction
} from '@kuikly-a2ui-open/render';

registerA2UICustomRenderView('A2UIWeb', () => new A2UIWebRenderView());

registerA2UICustomFunction('formatPhone', 'string', 'sync', (args) => {
  return (args['phone'] as string) ?? '';
});
```

---

## Action 回调协议约定

A2UI v0.9 协议里 `Button.action` 有两种形态，`onAction` 回调收到的 JSON 也对应两种结构：

| Action 类型 | JSON 结构 | 语义 | Kuikly a2ui 内置支持 |
|---|---|---|---|
| `action.event` | `{"event":{"name":"...","context":{...}}}` | 通知宿主"发生了什么"，由宿主决定后续行为 | ✅ **推荐**，宿主在 `onAction` 里自行处理 |
| `action.functionCall` | `{"functionCall":{"call":"...","args":{...},"returnType":"void"}}` | 直接调用一个客户端函数 | 仅 `openUrl` 是 spec 内置；其它需通过 `registerCustomFunction` 注册 |

**为什么是这样**：A2UI v0.9 basic catalog 里内置 `action.functionCall` 只保留了 `openUrl` 一个 action 型函数。诸如"弹 alert"、"跳转页面"、"埋点"这类业务行为，spec 明确要求走 `action.event` —— 服务端下发事件、由宿主用原生 UI 响应。

**Demo 里的约定**：本仓库 demo（`NativeCardDemoActivity` / `NativeCardDemoViewController` / `NativeCardDemoPage`）统一约定 `event.name == "toast"` 表示"弹一个原生 alert"，`event.context.title` / `event.context.message` 为标题和内容。宿主 App 可以按需自定义更多 `name`（如 `"track.play"`、`"cart.add"` 等），保持"服务端只描述事件、客户端决定 UI"的解耦。

**告警日志**：当 JSON 里的 `action.functionCall` 使用了既非注册的自定义函数、也非 spec 保留名（当前仅 `openUrl`）时，`A2UIBridgeManager` 会打印一条 `[A2UI] warn: …` 提示，帮助你快速发现拼写错误或非 spec 用法。

---

## JSON 消息流格式

A2UI 使用 **JSONL**（JSON Lines）。每次 `writeStream()` 只能传一个 JSON envelope。以一张最小示例卡片为例：

```jsonc
// envelope 1: 声明 surface
{
  "version": "v0.9",
  "createSurface": { "surfaceId": "hello" }
}

// envelope 2: 组件树
{
  "version": "v0.9",
  "updateComponents": {
    "surfaceId": "hello",
    "components": [
      { "id": "root", "component": "Column", "children": ["title"],
        "styles": { "padding": 16, "backgroundColor": "#FFFFFF" } },
      { "id": "title", "component": "Text", "text": "Hello A2UI!",
        "styles": { "fontSize": 20, "fontWeight": "bold", "color": "#333333" } }
    ]
  }
}
```

> `styles` 是 Kuikly 在 v0.9 标准之上的**可选扩展**。不写 `styles` 时协议仍然是纯 v0.9，多端行为一致。字段全集见 [`catalogs/kuikly_a2ui_catalog.json`](../catalogs/kuikly_a2ui_catalog.json)。

---

## 常见问题

**Q1：`writeStream()` 能一次传多条 envelope 吗？**
不能。协议要求每次调用只能是一个 JSON 对象。如果数据源是 JSON 数组（如批量测试文件），调用方需自行拆包。

**Q2：`A2UISurfaceView` 是 WRAP_CONTENT 好还是 MATCH_PARENT 好？**
两种都支持。A2UI 内部会根据外部宽度约束进行 FlexBox 布局，然后回报内容实际高度——因此**推荐把外部高度设成 WRAP_CONTENT**，把控制权交给协议。

**Q3：`onDestroy()` 一定要调 `surfaceView.destroy()` 吗？**
是。它会释放 Kuikly Pager 实例、清理 tag→view 映射、解注册 native bridge。iOS 侧对应 `deinit` 里 `surfaceView.destroy()`。

**Q4：iOS 上如果既想用 `A2UISurfaceView`，又想让业务 KMP framework 里也包含 A2UI 逻辑？**
请看 [A2UI_INTEGRATION_BOTH_PAGES.md](A2UI_INTEGRATION_BOTH_PAGES.md) —— 需要 `pod 'a2ui_demo'` + 手动指定 `frameworkName`，且不能同时链 `pod 'a2ui'`。
