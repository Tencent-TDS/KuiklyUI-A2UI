# Kuikly A2UI 跨平台框架

## 简介

Kuikly A2UI 是基于 Kuikly 渲染的 A2UI 协议实现，由腾讯大前端领域Oteam（公司级）推出，旨在提供一套跨端、易用、灵活的面向用户和 AI 智能体交互的 UI 开发框架。

随着 AI 大模型的广泛使用，应用侧各种聊天对话场景里展示的内容都由大模型生成。为了将大模型生成的静态文本替换为更图形化和可交互的 UI 界面，A2UI 协议 让服务端大模型生成一段结构化 JSON，动态描述 UI 卡片，然后客户端根据 JSON 描述的 UI 树结构来渲染卡片界面。

Kuikly A2UI 使用 Kuikly 本身优秀并经过验证的渲染能力和性能，实现了 A2UI 协议在多端的原生渲染体验，并扩展了 UI 表现效果和自定义组件等方面的能力。

Kuikly A2UI 核心 SDK 提供了从 JSON 流到渲染 UI 卡片的能力，以及操作 UI 卡片产生 Action 回调给业务的能力。业务接入后可连接服务端 AI 智能体、业务客户端和 Kuikly A2UI SDK 三者。服务端 AI 智能体生成 JSON 流给到业务客户端，进而给到 Kuikly A2UI SDK 渲染 UI 卡片；用户操作 UI 卡片，产生 Action 回调给业务客户端，业务客户端根据 Action 类型，判断出本地处理 Action，还是传给服务端 AI 智能体；服务端 AI 智能体收到 Action 可以进一步生成新的 JSON 流给客户端渲染新的 UI 卡片。

Kuikly A2UI 核心 SDK 内部直接对接 Kuikly DSL 组件树，将服务端下发的 JSON 协议转换为 Kuikly Core 管理的原生视图树，实现真正的“服务端驱动 UI”能力。内部整个流程即： 协议 → 内部消息 → DSL 操作 → 原生视图。

**[主页](https://framework.tds.qq.com/)**

**[github](https://github.com/Tencent-TDS/KuiklyUI-A2UI)**

## 快速开始

### 环境准备

- [DevEco Studio 5.1.0 or newer](https://developer.huawei.com/consumer/cn/download/deveco-studio)
- Device or Emulator with HarmonyOS Next 5.0.0(12) or newer

### 集成 Kuikly A2UI

1. 你可以通过 ohpm 将 Kuikly A2UI 添加到你的项目中：

  `ohpm install @kuikly-a2ui-open/render`

2. 或者你可以手动添加到你的项目中：

在应用程序模块的 `oh-package.json5` 中添加 Kuikly A2UI 依赖

```json5
  "dependencies": {

    "@kuikly-a2ui-open/render": 'latest'

  }
```

并执行

`ohpm install`

更多接入信息请参考: [Kuikly A2UI](https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/README-zh_CN.md)
