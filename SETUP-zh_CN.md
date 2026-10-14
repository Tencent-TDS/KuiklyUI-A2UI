# KuiklyUI-A2UI 工程 — 环境配置指南

本工程的运行依赖 KuiklyUI。在 Maven / CocoaPods / OHPM
完整发布流程就绪之前，工程会从本地 KuiklyUI 仓库源码依赖以下模块：
`:core`、`:core-render-android`、`:compose`、`:core-annotations`、`:core-ksp`、
`core-render-ohos`、`core-render-ios`、`OpenKuiklyIOSRender`。

## 1. KuiklyUI 源码依赖模式：配置 KuiklyUI 本地仓路径

将 `kuikly.projectDir` 设置为你本地 KuiklyUI 仓库路径。
该值会被**所有**构建工具读取（Gradle、Podfile、iOS setup 脚本、
HarmonyOS DevEco Studio（通过 `.kuikly-external` 软链）、OHOS 构建脚本）。
因此只需改**一处**即可切换整个工程依赖指向。

解析优先级（命中即停止）：

1. Gradle 命令行参数 `-Pkuikly.projectDir=/path`，或
   环境变量 `KUIKLY_PROJECT_DIR=/path`
2. `<project-root>/local.properties` 中的 `kuikly.projectDir=/path`
3. `<project-root>/gradle.properties` 中的 `kuikly.projectDir=<path>`
   （默认：`../KuiklyUI`）

推荐：把绝对路径写在 `local.properties`（该文件已被 git ignore）。

## 2. KuiklyUI 产物依赖模式：已发布 Maven / Pod / OHPM

当前工程通过上面的 `kuikly.projectDir` 以**本地源码**方式依赖 KuiklyUI Core。
当对应的 Kuikly Core 产物已发布后（无论是你自己的 Maven 仓，还是
CocoaPods spec repo / ohpm registry），可按平台独立切换。

**Android / KMP** — 打开 [settings.gradle.kts](settings.gradle.kts)（以及
ohos 变体），移除四行 `include(":core", ...)` /
`project(":core").projectDir = ...`。然后在各模块
`build.*.gradle.kts` 中，将 `project(":core")` 替换成 Maven 坐标，例如：

```kotlin
// Before (local source):
api(project(":core"))
// After (published Maven artefact):
api("com.tencent.kuikly-open:core:${Version.getCoreVersion()}")
```

`:core-annotations`、`:core-ksp`、`:core-render-android`、`:compose`
同理替换。位于
[buildSrc/](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)
中的 `Version.getCoreVersion()` 已按规则计算上游版本。

**iOS** — 在每个 `iosAppFor*/Podfile` 中，将：

```ruby
pod 'OpenKuiklyIOSRender', :path => '../.kuikly-external'
```

替换为已发布 pod：

```ruby
pod 'OpenKuiklyIOSRender', '~> <upstream-kuikly-version>'
```

A2UI 侧 pods（`a2ui`、`OpenA2UIIOSRender`）在本地开发期间仍保持
`:path => 'path'`。本仓库没有自动发布 iOS Pod 的脚本
（与 Kuikly 工程保持一致），如需发布请在对应目录执行
`pod trunk push <podspec> --allow-warnings`，具体命令见
[publish/README.md](publish/README.md)。
其中 `a2ui_demo` pod 是**业务侧示例**，下游集成方会自行实现对应 KMP
模块并通过 `:path` 方式接入，因此该 pod 有意不发布。

**HarmonyOS** — 在 `a2ui-render-ohos/oh-package.json5` 中，将：

```json5
"@kuikly-open/render": "file:../.kuikly-external/core-render-ohos"
```

替换为：

```json5
"@kuikly-open/render": "^<upstream-render-version>"
```

本仓库当前没有自动发布 ohpm 包 `@kuikly-open/a2ui-render` 的脚本
（与 Kuikly 工程保持一致）。如需发布请先按上面的方式把本地
`file:` 依赖改回已发布的 semver，然后在 `a2ui-render-ohos/` 目录
手工执行 `ohpm pack` / `ohpm publish`。

## 3. A2UI 产物版本策略

本仓库包含四类产物，采用**两套相关但不同**的版本方案：

| 产物 | 方案 | 示例 | 来源 |
|---|---|---|---|
| Maven 上的 Android AAR / KMP klib（`com.tencent.kuikly:a2ui`、`a2ui-render-android`） | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `0.9.0-2.1.21-SNAPSHOT`、`0.9.0-2.0.21-ohos-SNAPSHOT` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods（`a2ui`、`OpenA2UIIOSRender`） | `${semantic}`（纯 SemVer，CocoaPods 要求） | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm（`@kuikly-open/a2ui-render`） | `${semantic}`（纯 SemVer，OHPM 要求） | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| README / 展示用途 | `${semantic}` | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic()` |

Maven 坐标中的 Kotlin 后缀遵循 Kuikly Core 约定
（`com.tencent.kuikly:core:2.0.0-2.1.21-SNAPSHOT`）——
KMP `.klib` 与 metadata 二进制在不同 Kotlin 编译器版本间并不兼容，
因此每个支持的 toolchain 都需要独立坐标发布。

两个获取 A2UI 产物版本的 API 都位于
[`buildSrc/src/main/java/A2uiVersion.kt`](buildSrc/src/main/java/A2uiVersion.kt)。

### 覆盖语义版本

优先级（`getSemantic` 与 `getArtifactVersion` 共享同一 semantic 基础；
对齐 Kuikly Core 的 `Version.getKuiklyVersion()`）：

1. `A2UI_VERSION=1.0.0 ./gradlew ...`（环境变量）
2. 否则回退到 `A2uiVersion.kt` 中的 `DEFAULT_A2UI_SEMANTIC`

### 覆盖 Kotlin 后缀

Maven 坐标中的 Kotlin 片段始终读取 `Version.getKotlinVersion()`
（位于
[`KuiklyKotlinBuildVar.kt`](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)），
其会响应 `KUIKLY_KOTLIN_VERSION`。
通过 `2.0_a2ui_ohos_build.sh` 触发的 HarmonyOS 构建会自动设置
`KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010`，从而在最终坐标中生成
`-ohos` 标记（`0.9.0-2.0.21-ohos-SNAPSHOT`）。

### CI 一行命令

两个基于 Gradle 的发布脚本（`2.1.21_publish.sh`、
`2.0_ohos_publish.sh`）已对齐 Kuikly 工程：接受 `$1 = MODULE`
（默认 `all`）和 `$2 = PUBLISH_TASK`（默认 `publishToMavenLocal`）两个
位置参数，通过 `KUIKLY_AGP_VERSION` / `KUIKLY_KOTLIN_VERSION` 固定
 toolchain，Maven Central 凭据统一交给 Gradle 发布插件自身读取。
语义版本可通过 `A2UI_VERSION` 环境变量临时覆盖（与 Kuikly
`KUIKLY_VERSION` 用法一致）：

```bash
A2UI_VERSION=1.0.0 bash publish/2.1.21_publish.sh
A2UI_VERSION=1.0.0 bash publish/2.0_ohos_publish.sh
```

但发版主路径仍推荐直接修改 `A2uiVersion.kt` 中的
`DEFAULT_A2UI_SEMANTIC`，以保证不同环境下版本一致。

iOS Pod 没有发布脚本，请手工执行 `pod trunk push`（见
[publish/README.md](publish/README.md)）。

最终所有产物共享同一个 **semantic** 版本标签；
Maven 侧坐标会额外带上 Kotlin/ohos 后缀。

注意：**上游 Kuikly Core 依赖版本**是另一条独立配置项
（见
[`KuiklyKotlinBuildVar.kt`](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)
中的 `Version.getKuiklyVersion()`），不受 `A2UI_VERSION` 影响。
