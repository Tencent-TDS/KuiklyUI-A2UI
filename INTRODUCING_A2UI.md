# Kuikly A2UI：让 AI 直接生成原生性能的 UI 界面

> 导语：[Kuikly](https://github.com/Tencent-TDS/KuiklyUI) 是由 PCG 端框架团队、QQ 团队、腾讯视频团队合作共建的 Kotlin Multiplatform 跨端 UI 框架，也是 [腾讯端服务联盟](https://framework.tds.qq.com/)（TDS）的重要成员。[Kuikly A2UI](https://github.com/Tencent-TDS/KuiklyUI-A2UI) 是腾讯 Kuikly 团队推出的"AI-to-UI"渲染引擎——对齐 A2UI 开源协议，让服务端大模型只需下发一段 JSON 消息流，客户端便能在 Android、iOS、HarmonyOS 上实时渲染出 100% 原生性能的 UI 界面。

---

## 简介

随着 AI 大模型的广泛使用，应用侧各种聊天对话场景里展示的内容都由大模型生成。为了将大模型生成的静态文本替换为更图形化和可交互的 UI 界面，[A2UI 协议](https://a2ui.org/) 让服务端大模型生成一段结构化 JSON，动态描述 UI 卡片，然后客户端根据 JSON 描述的 UI 树结构来渲染卡片界面。

**Kuikly A2UI 使用 Kuikly 本身优秀并经过验证的渲染能力和性能，实现了 A2UI 协议在 Android、iOS、HarmonyOS 三端的原生渲染体验，并扩展了 UI 表现效果和自定义组件等方面的能力。**

Kuikly A2UI 核心 SDK 提供了从 JSON 流到渲染 UI 卡片的能力，以及操作 UI 卡片产生 Action 回调给业务的能力。业务接入后可连接服务端 AI 智能体、业务客户端和 Kuikly A2UI SDK 三者。服务端 AI 智能体生成 JSON 流给到业务客户端，进而给到 Kuikly A2UI SDK 渲染 UI 卡片；用户操作 UI 卡片，产生 Action 回调给业务客户端，业务客户端根据 Action 类型，判断出本地处理 Action，还是传给服务端 AI 智能体；服务端 AI 智能体收到 Action 可以进一步生成新的 JSON 流给客户端渲染新的 UI 卡片。

Kuikly A2UI 核心 SDK 内部直接对接 Kuikly DSL 组件树，将服务端下发的 JSON 协议转换为 Kuikly Core 管理的原生视图树，实现真正的“服务端驱动 UI”能力。内部整个流程即： 协议 → 内部消息 → DSL 操作 → 原生视图。

<div style="display: inline-block; text-align: center; margin-left: 20px;">
  <img src="docs/a2ui_and_llm.png" alt="Kuikly A2UI Architecture" width="800">
</div>

## 使用场景

Kuikly A2UI 的目标是让 AI 大模型生成交互体验更好的 UI 卡片，所以在应用聊天对话场景，或者其它 AI 大模型实时生成 UI 内容的场景，AI 大模型的回复内容里，下面情况都适合使用。

- 给出机票状态，可以生成描述机票卡片的 JSON 给客户端渲染。
- 让用户填写一个带校验的表单，可以生成描述表单卡片的 JSON 给客户端渲染，并且用户提交表单后可以产生 Action 内容回复给 AI 大模型。
- 在对话里插入一张可点击的商品卡片，可以生成描述商品信息卡片的 JSON 给客户端渲染，并且用户点击购买按钮后产生 Action，进而可以跳转商品页面进行购买，或者直接支付购买。

## 特点和优势

Kuikly A2UI 除了 A2UI 协议本身的特点，如：UI 表达丰富、支持交互、无需编译等，还有以下特点和优势：

- 性能方面：使用 Kuikly 保持了原生 UI 的性能和体验，同时内存占用小
- 表现能力：Kuikly A2UI 使用的协议基于 A2UI 协议之上做了 **UI 表现能力增强**——保持标准协议向后兼容的同时，为每个组件补充了可选的 `styles` 属性包，让服务端能更精细地定制组件的原生视觉表现（渐变、阴影、圆角、变换、文本装饰等）
- 扩展能力：Kuikly A2UI 支持扩展自定义组件、自定义函数、自定义 Icon 图片
- 接入能力：Kuikly A2UI 即支持 App 原生界面接入 AI 生成的卡片，也支持在已经是 Kuikly 页面的情况下，无缝嵌入 AI 生成的卡片

总之，使用 Kuikly A2UI 可以提高 AI 智能体和用户之间的交互体验，可以在原生页面直接集成 A2UI 卡片，也可以在已有 Kuikly 页面无缝集成 A2UI 卡片。

---

## 示例展示

### Demo 应用

Android / iOS / HarmonyOS 三端 Demo 已内置：

- **卡片接入示例**：加载 JSON 字符串生成 A2UI 卡片，以及通过点击添加按钮模拟卡片动态生成的过程。有机票状态卡片、日程安排卡片、天气卡片、商品卡片等。

<div style="display: inline-block; text-align: center; margin-left: 30px;">
  <img src="docs/a2ui_demo.gif" alt="Kuikly A2UI demo" width="200">
</div>

- **组件展示**：按照 A2UI 协议定义，18 个标准组件全覆盖（Text / Image / Button / Card / Column / Row / List / Tabs / Slider / CheckBox / ChoicePicker / DateTimeInput / TextField / Video / AudioPlayer / Modal / Divider / Icon）

<div style="display: inline-block; text-align: center; margin-left: 30px;">
  <img src="docs/a2ui_demo2.gif" alt="Kuikly A2UI component" width="200">
</div>

- **自定义组件示例**：展示了自定义 Web 组件和自定义简单 View 组件的示例。
- **自定义函数示例**：展示了 A2UI JSON 串里调用自定义函数的多个示例。
- **自定义 Icon 图片示例**：展示了 App 如果使用自定义 Icon 图片替换所有 Icon 组件默认图标的示例。

### Demo 快速体验

以 Native 页面接入 Kuikly A2UI 卡片的场景举例，Android 手机可扫码快速体验。

<div style="display: inline-block; text-align: center; margin-left: 30px;">
  <img src="docs/a2ui_demo_apk.png" alt="Kuikly A2UI demo apk" width="200">
</div>

iPhone 和 HarmonyOS 手机请按如下步骤本地编译 Demo 体验。

**先决条件**：**无需**本地 clone KuiklyUI 源码。Android / iOS / HarmonyOS 三端默认都从已发布的 **Maven / CocoaPods trunk / ohpm registry** 消费 Kuikly（默认 `kuikly.version=2.29.2`）。只有和 KuiklyUI 本地源码联调时才需要开启源码模式，详见 [`SETUP-zh_CN.md`](https://github.com/Tencent-TDS/KuiklyUI-A2UI/SETUP-zh_CN.md)。

```bash
# iOS
cd iosAppForNativePage && bash setup.sh && open A2UIDemoApp.xcworkspace

# HarmonyOS：先在【本工程根目录】执行构建脚本，产出 libshared.so 到各 ohosAppFor* 模块
./2.0_a2ui_ohos_build.sh # Debug 构建
# 然后用 DevEco Studio 打开对应目录并运行：
# ohosAppForNativePage
```

更多场景的 Demo，可按项目 [README](https://github.com/Tencent-TDS/KuiklyUI-A2UI/README-zh_CN.md) 文档里步骤本地编译体验。

---

## 立即开始

以 Native 页面接入 Kuikly A2UI 卡片的场景举例，Android 平台接入方式如下。

1 添加依赖：

```kotlin
// build.gradle.kts (业务模块)
// 版本号格式：${kuiklyVersion}-${kotlinVersion}
val a2uiVersion = "1.0.0-2.1.21"

dependencies {
    // A2UI 核心（KMP）
    implementation("com.tencent.kuikly:a2ui:${a2uiVersion}")
    // A2UI Android 渲染层（提供 A2UISurfaceView）
    implementation("com.tencent.kuikly:a2ui-render-android:${a2uiVersion}")
    // Kuikly 渲染层
    implementation("com.tencent.kuikly:core-render-android:${a2uiVersion}")
    // 图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

2 Application 初始化：

在 `Application.onCreate()` 中注册 Kuikly Adapter：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 图片 Adapter
        if (KuiklyRenderAdapterManager.krImageAdapter == null) {
            KuiklyRenderAdapterManager.krImageAdapter = MyImageAdapter(this)
        }
        // 视频/音频 Adapter
        // ...
    }
}
```

3 在 Activity 中使用 `A2UISurfaceView`：

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
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        container.addView(surface)
        setContentView(container)

        // configJson 可以传 "{}"，也可以指定屏幕宽度等配置
        surface.init("{}")
        surface.setListener(object : A2UISurfaceViewListener {
            override fun onAction(surfaceId: String, tag: Int,
                                  action: String, params: String?) {}
            override fun onDataModelChanged(dataModelJson: String, changedKey: String) = Unit
            override fun onOpenUrl(url: String) {}
        })

        // 写入协议流：每次 writeStream() 只能传一个 JSON envelope
        surface.writeStream(createSurfaceEnvelope)
        surface.writeStream(updateComponentsEnvelope)
        surface.finishStream()

        surfaceView = surface
    }

    override fun onDestroy() {
        surfaceView?.destroy()
        surfaceView = null
        super.onDestroy()
    }
}
```

[Kuikly A2UI](https://github.com/Tencent-TDS/KuiklyUI-A2UI) 更多使用文档参考：

- **工程目录、架构概述、Demo 构建等参考**：[README.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/README-zh_CN.md)
- **接入指南**：
  - **Native 页面接入指南**：[docs/A2UI_INTEGRATION_NATIVE_PAGE.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_INTEGRATION_NATIVE_PAGE.md)
  - **Kuikly 页面接入指南**：[docs/A2UI_INTEGRATION_KUIKLY_PAGE.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_INTEGRATION_KUIKLY_PAGE.md)
  - **两种页面同时接入指南**：[docs/A2UI_INTEGRATION_BOTH_PAGES.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_INTEGRATION_BOTH_PAGES.md)
- **架构与实现设计**：[docs/A2UI_KUIKLY_IMPLEMENTATION_DESIGN.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_KUIKLY_IMPLEMENTATION_DESIGN.md)
- **自定义组件指南**：[docs/A2UI_CUSTOM_COMPONENT_GUIDE.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_CUSTOM_COMPONENT_GUIDE.md)
- **自定义函数指南**：[docs/A2UI_CUSTOM_FUNCTION_GUIDE.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_CUSTOM_FUNCTION_GUIDE.md)
- **自定义 Icon 指南**：[docs/A2UI_CUSTOM_ICON_GUIDE.md](https://github.com/Tencent-TDS/KuiklyUI-A2UI/docs/A2UI_CUSTOM_ICON_GUIDE.md)
- **协议 Catalog（Schema）**：[catalogs/kuikly_a2ui_catalog.json](https://github.com/Tencent-TDS/KuiklyUI-A2UI/catalogs/kuikly_a2ui_catalog.json)


---

## 支持

随着 AI 的广泛应用，对于客户端 App，从产品形态、交互体验到技术框架上都需要面向 AI 进行各种重构，Kuikly A2UI 在 Kuikly 为多端 App 开发 UI 的基础上，提供了面向用户和 AI 智能体交互的重要能力。[Kuikly A2UI](https://github.com/Tencent-TDS/KuiklyUI-A2UI) 开源，欢迎提交 Issue 和 Pull Request。
