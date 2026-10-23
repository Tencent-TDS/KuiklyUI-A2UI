# KuiklyUI-A2UI 工程 — 环境配置指南

本工程的运行依赖 KuiklyUI。A2UI 对 KuiklyUI 提供**两种依赖模式**，由
`kuikly.useSource` 单一开关控制（详见下文）。

- **产物模式（默认）** —— 从 `com.tencent.kuikly-open:*` 的 Maven /
  Pod / OHPM 已发布包消费，不需要本地 KuiklyUI 源码。
- **源码模式（显式开启）** —— 从本地 KuiklyUI 仓库源码依赖以下模块：
  `:core`、`:core-render-android`、`:compose`、`:core-annotations`、
  `:core-ksp`、`core-render-ohos`、`core-render-ios`、
  `OpenKuiklyIOSRender`。用于与 KuiklyUI 一起源码联调。

> 作用范围：`kuikly.useSource` 开关同时影响三端的 Kuikly 依赖消费
> 方式：
>
> - **Android / KMP**（Kotlin 2.1.21，`settings.gradle.kts` /
>   `settings.2.1.21.gradle.kts`）—— 产物模式用
>   `com.tencent.kuikly-open:*` Maven 坐标；源码模式用
>   `includeBuild(<kuikly.projectDir>)`。
> - **iOS CocoaPods** —— 产物模式用 CocoaPods trunk 的
>   `'<kuikly.version>'`；源码模式用
>   `:path => <kuikly.projectDir>`。
> - **HarmonyOS**（`settings.2.0.ohos.gradle.kts` + 各
>   `ohosAppFor*/entry/oh-package.json5` + `a2ui-render-ohos/oh-package.json5`
>   + `ohosAppFor*/build-profile.json5`）—— 产物模式从 ohpm registry
>   消费 `"@kuikly-open/render": "<kuikly.version>"`；源码模式由
>   [`scripts/apply-ohos-mode.sh`](scripts/apply-ohos-mode.sh) 自动把
>   `oh-package.json5` 改写为 `"file:../..?/core-render-ohos"` 并在
>   `build-profile.json5` 的 `modules` 数组中补回 `{"name":"render",
>   "srcPath":"../core-render-ohos"}` 块，由
>   [`scripts/link-kuikly.sh`](scripts/link-kuikly.sh) 统一驱动（详见
>   §4 HarmonyOS 段）。
>
> A2UI 自身的 iOS Pod（`pod 'a2ui'` / `pod 'a2ui_demo'` /
> `pod 'OpenKuiklyA2UIIOSRender'`）在仓库内 `iosAppFor*` 示例中始终以
> `:path =>` 接入，详见 §4 iOS 段。

## 1. 选择依赖模式：`kuikly.useSource`

默认 `false`（产物模式）。

解析优先级（命中即停止）：

1. Gradle 命令行 `-Pkuikly.useSource=true|false`
2. `KUIKLY_USE_SOURCE=true|false` 环境变量
3. `<project-root>/local.properties` → key `kuikly.useSource`
4. `<project-root>/gradle.properties` → key `kuikly.useSource`

```bash
# 单次：以源码模式打开 IDE / 跑 Gradle
./gradlew -Pkuikly.useSource=true :androidAppForKuiklyPage:installDebug

# 持久（推荐 Kuikly 贡献者）：
echo "kuikly.useSource=true" >> local.properties
```

## 2. 产物模式下配置上游 Kuikly 版本

产物模式下，A2UI 把 Kuikly 模块解析成
`com.tencent.kuikly-open:<module>:<coord-version>`。坐标版本由
`kuikly.version` 和当前 Kotlin 工具链拼接而成，规则与 Kuikly Core
发布 core/compose/ksp 时所用的坐标约定一致（见
[`KuiklyDep.kuiklyCoordinateVersion`](buildSrc/src/main/java/KuiklyDep.kt)）：

- `kuikly.version=2.29.2` × Kotlin `2.1.21` → `2.29.2-2.1.21`
- `kuikly.version=2.29.2-SNAPSHOT` × Kotlin `2.1.21` → `2.29.2-2.1.21-SNAPSHOT`
- `kuikly.version=2.29.2-SNAPSHOT` × Kotlin `2.0.21-KBA-010` → `2.29.2-2.0.21-ohos-SNAPSHOT`

该值**原样使用**——是否带 `-SNAPSHOT` 必须自己写明。解析器只会插入
Kotlin 工具链段（HarmonyOS 构建时会把 `KBA-010` 后缀替换为 `ohos`）。

`kuikly.version` 解析顺序：

1. `-Pkuikly.version=…`
2. `KUIKLY_VERSION` 环境变量（和 Kuikly Core 共用同一个 env，联编两个
   仓库时只需这一个环境变量）
3. `local.properties` → `kuikly.version`
4. `gradle.properties` → `kuikly.version`（默认 `2.29.2`）

> 要求上游 Kuikly 产物已发布到你的构建可以解析的 Maven 仓。本地未发布
> 的 Kuikly 联调请改用源码模式（见第 3 节）。

## 3. 源码模式下配置 KuiklyUI 本地仓路径

仅当 `kuikly.useSource=true` 时需要。将 `kuikly.projectDir` 设置为你
本地 KuiklyUI 仓库路径。该值会被**所有**构建工具读取（Gradle、
Podfile、iOS setup 脚本、HarmonyOS DevEco Studio（通过
`.kuikly-external` 软链）、OHOS 构建脚本）。

解析优先级（命中即停止）：

1. Gradle 命令行参数 `-Pkuikly.projectDir=/path`，或
   环境变量 `KUIKLY_PROJECT_DIR=/path`
2. `<project-root>/local.properties` 中的 `kuikly.projectDir=/path`
3. `<project-root>/gradle.properties` 中的 `kuikly.projectDir=<path>`
   （默认：`../KuiklyUI`）

推荐：把绝对路径写在 `local.properties`（该文件已被 git ignore）。

## 4. 各平台说明

**iOS** — 本仓库中 iOS 侧使用两类 A2UI Pod：

| Pod | 产物 Framework | 内含的 `@ObjCName` 入口类来源 | 典型消费者 |
|---|---|---|---|
| `pod 'a2ui'` | `A2UICore.framework`（`:a2ui` 模块产物） | `:a2ui/iosMain` 原件（5 个入口类：`A2UICoreEntry` / `A2UICustomViewRegistryBridge` / `A2UIIconRegistryBridge` / `A2UICustomFunctionRegistryBridge` / `A2UIPickerResultBridge`） | `iosAppForNativePage`、`iosAppForTest` — Native 页接入场景 |
| `pod 'a2ui_demo'` | `A2UIDemo.framework`（`:demo` 模块产物） | `:demo/iosMain` 自家薄壳（同 5 个 `@ObjCName` 名，内部转调 `:a2ui` 的 commonMain 单例） | `iosAppForKuiklyPage`、`iosAppForBothPages` — Kuikly 页接入场景 |

> **二选一原则**：这两个 Pod 由于都注册同名的 `@ObjCName` 类（如
> `A2UICoreEntry`），不能同时链接进同一个 App。实际使用上，每个
> `iosAppFor*` demo 的 Podfile 要么用 `a2ui` 要么用 `a2ui_demo`，不会
> 两者并存。下游业务通常复刻 `a2ui_demo` 的模式（自建 KMP 业务模块、
> 自家 iosMain 持有 5 个 `@ObjCName` 薄壳），不直接依赖 `a2ui_demo`。

**在仓库内（本地开发期）** — 不论源码还是产物模式，`iosAppFor*/Podfile`
中 **A2UI 侧** Pod（`a2ui`、`a2ui_demo`、`OpenKuiklyA2UIIOSRender`）**始终**以
`:path => '../a2ui'` / `:path => '../demo'` / `:path => '../a2ui-render-ios'`
方式接入，这是本仓库 Podfile 的既定约定——A2UI 自身的 Kotlin
`.framework` 必须随 Xcode 构建触发 Gradle `syncFramework` 实时生成，
无法通过 trunk 消费。

**上游 Kuikly 侧** Pod（`OpenKuiklyIOSRender`）则由 `kuikly.useSource`
开关决定，与 Gradle 侧 [`KuiklyDep`](buildSrc/src/main/java/KuiklyDep.kt)
的行为完全对齐，具体映射规则：

| 模式 | `iosAppFor*/Podfile` 中 `OpenKuiklyIOSRender` 的声明 |
|---|---|
| 产物模式（默认，`kuikly.useSource=false`） | `pod 'OpenKuiklyIOSRender', '2.29.2'`（从 CocoaPods trunk 拉已发布版本） |
| 源码模式（`kuikly.useSource=true`） | `pod 'OpenKuiklyIOSRender', :path => <kuikly.projectDir>`（本地源码，用于与 Kuikly 一起联调） |

版本号解析顺序与 Gradle 侧 `kuikly.version` 完全一致（env /
`local.properties` / `gradle.properties` / 默认 `2.29.2`，详见
[`scripts/lib/kuikly_cocoapods.rb`](scripts/lib/kuikly_cocoapods.rb)）。
`OpenKuiklyIOSRender` 作为纯 ObjC/C++ Pod，trunk 上的 tag 使用纯
SemVer（无 Kotlin 后缀），所以 Podfile 侧只取 `kuikly.version` 的
SemVer 段，有 `-SNAPSHOT` 后缀会被剥去。

**产物模式下集成方（下游 App）** — 可以通过 CocoaPods trunk 拉已发布版本：

```ruby
pod 'a2ui', '~> 1.0.0'
pod 'OpenKuiklyA2UIIOSRender', '~> 1.0.0'
# OpenKuiklyIOSRender 通过 OpenKuiklyA2UIIOSRender 的 spec.dependency 自动引入
# （版本号已在 OpenKuiklyA2UIIOSRender.podspec 中锚定为 Kuikly 2.29.2）
```

本仓库没有自动发布 iOS Pod 的脚本（与 Kuikly 工程保持一致），如需
发布请在对应目录执行 `pod trunk push <podspec> --allow-warnings`，
具体命令见 [publish/README.md](publish/README.md)。其中 `a2ui_demo`
pod 虽然在仓库内被 `iosAppForKuiklyPage` / `iosAppForBothPages` 以
`:path => '../demo'` 消费，但它定位是**业务侧示例**而非对外公共 API，
下游集成方会自行实现对应 KMP 模块并通过 `:path` 方式接入，因此该
pod 有意不发布到 trunk。

**HarmonyOS** — OHOS 侧**两种模式都支持**，由同一个 `kuikly.useSource`
开关驱动，与 Android / iOS 完全对齐。切换由
[`scripts/link-kuikly.sh`](scripts/link-kuikly.sh)（内部调用
[`scripts/apply-ohos-mode.sh`](scripts/apply-ohos-mode.sh)）统一驱动：

| 模式 | `a2ui-render-ohos/oh-package.json5` 中 `@kuikly-open/render` 的声明 | `ohosAppFor*/entry/oh-package.json5` 中 `@kuikly-open/render` 的声明 | `ohosAppFor*/build-profile.json5` 的 `modules` 数组 | 本地文件系统 |
|---|---|---|---|---|
| 产物模式（默认，`kuikly.useSource=false`） | `"@kuikly-open/render": "2.29.2"`（从 ohpm registry 拉已发布版本） | 同左（版本一致） | 仅 `entry` + `a2ui_render`，无 `render` 模块块 | 无 `.kuikly-external`，无 `core-render-ohos/` |
| 源码模式（`kuikly.useSource=true`） | `"@kuikly-open/render": "file:../core-render-ohos"` | `"@kuikly-open/render": "file:../../core-render-ohos"` | 额外包含 `{"name":"render", "srcPath":"../core-render-ohos", …}` 模块块 | `.kuikly-external` 软链指向 `kuikly.projectDir`；`core-render-ohos/` 是 `kuikly.projectDir/core-render-ohos` 的 rsync 本地镜像（git-ignored） |

> 版本号解析顺序与 Gradle / CocoaPods 侧 `kuikly.version` 完全一致
> （env / `local.properties` / `gradle.properties` / 默认 `2.29.2`，
> 任何 `-SNAPSHOT` 后缀会被剥去——ohpm registry 和 CocoaPods trunk 一样
> 只收录正式发布版）。

**C/C++ 侧统一入口**：`ohosAppFor*/entry/src/main/cpp/CMakeLists.txt`
对齐 Kuikly 工程写法，用
`find_package(render REQUIRED CONFIG)` + `add_library(kuikly_render
ALIAS render::kuikly)` 消费 Kuikly 的头文件与 `libkuikly.so`。两种模式
共用同一份 CMakeLists——无论是源码镜像还是 ohpm 产物，装到
`oh_modules/@kuikly-open/render/` 之后 ohpm 生成的 CMake Config 导出
路径一致，`PACKAGE_FIND_FILE` 由 hvigor 注入。

**源码模式工作流**（本仓库内 Kuikly 联调）：

```bash
# local.properties 中设置：
#   kuikly.useSource=true
#   kuikly.projectDir=/absolute/path/to/KuiklyUI
bash scripts/link-kuikly.sh
# ↑ 会把 kuikly.projectDir/core-render-ohos 整个 rsync 到
#   <A2UI-root>/core-render-ohos（排除 build/ oh_modules/ .hvigor/
#   .cxx/ 等生成目录），同时把 .kuikly-external 软链指向
#   kuikly.projectDir，然后把 5 份 oh-package.json5 与 4 份
#   build-profile.json5 改回源码版。镜像幂等，`rsync -a --delete`
#   每次重新同步；真实源头仍是 kuikly.projectDir/core-render-ohos。
```

**从源码模式切回产物模式**：

```bash
# 把 local.properties 中的 kuikly.useSource 改回 false（或删除此行）
bash scripts/link-kuikly.sh
# ↑ 会自动清理 .kuikly-external 软链与 core-render-ohos/ 镜像目录，
#   并把 5 份 oh-package.json5 / 4 份 build-profile.json5 改回产物版。
```

> 为什么需要 `core-render-ohos/` 本地镜像而不是软链？hvigor 的
> `build-profile.json5` 的 `modules[].srcPath` 校验器会拒绝任何
> 真实目标在 workspace 之外的路径（无论绝对路径还是软链）——因此
> 源码模式下必须以"真实在 workspace 内的目录"方式提供给 hvigor。镜像
> 已在 `.gitignore` 中过滤。

**ohpm 包 `@kuikly-a2ui-open/render` 的发布** — 本仓库当前没有自动发布
脚本（与 Kuikly 工程保持一致）。发布前请确保仓库处于**产物模式**
（即 `a2ui-render-ohos/oh-package.json5` 中 `@kuikly-open/render` 是
`"<kuikly.version>"` 形式，而不是 `"file:../core-render-ohos"`），然后
在 `a2ui-render-ohos/` 目录执行 `ohpm pack` / `ohpm publish`。

## 5. A2UI 产物版本策略

本仓库包含四类产物，采用**两套相关但不同**的版本方案：

| 产物 | 方案 | 示例 | 来源 |
|---|---|---|---|
| Maven 上的 Android AAR / KMP klib（`com.tencent.kuikly-open:a2ui`、`a2ui-render-android`） | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `1.0.0-2.1.21`、`1.0.0-2.0.21-ohos` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods（`a2ui`、`OpenKuiklyA2UIIOSRender`） | `${semantic}`（纯 SemVer，CocoaPods 要求） | `1.0.0` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm（`@kuikly-a2ui-open/render`） | `${semantic}`（纯 SemVer，OHPM 要求） | `1.0.0` | `A2uiVersion.getSemantic(project)` |
| README / 展示用途 | `${semantic}` | `1.0.0` | `A2uiVersion.getSemantic()` |

Maven 坐标中的 Kotlin 后缀遵循 Kuikly Core 约定
（`com.tencent.kuikly-open:core:2.29.2-2.1.21-SNAPSHOT`）—— KMP `.klib`
与 metadata 二进制在不同 Kotlin 编译器版本间并不兼容，因此每个支持
的 toolchain 都需要独立坐标发布。

两个获取 A2UI 产物版本的 API 都位于
[`buildSrc/src/main/java/A2uiVersion.kt`](buildSrc/src/main/java/A2uiVersion.kt)。

### 覆盖 A2UI 语义版本

优先级（`getSemantic` 与 `getArtifactVersion` 共享同一 semantic 基础；
仅支持 env 覆盖，比
[`KuiklyDep.kuiklyBaseVersion`](buildSrc/src/main/java/KuiklyDep.kt) 更简
化）：

1. `A2UI_VERSION=1.0.0 ./gradlew ...`（环境变量）
2. 否则回退到 `A2uiVersion.kt` 中的 `DEFAULT_A2UI_SEMANTIC`

### 覆盖 Kotlin 后缀

Maven 坐标中的 Kotlin 片段始终读取 `Version.getKotlinVersion()`
（位于
[`KuiklyKotlinBuildVar.kt`](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)），
其会响应 `KUIKLY_KOTLIN_VERSION`。
通过 `2.0_a2ui_ohos_build.sh` 触发的 HarmonyOS 构建会自动设置
`KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010`，从而在最终坐标中生成
`-ohos` 标记（`1.0.0-2.0.21-ohos`）。

### CI 一行命令

两个基于 Gradle 的发布脚本（`2.1.21_publish.sh`、
`2.0_ohos_publish.sh`）已对齐 Kuikly 工程：接受 `$1 = MODULE`
（默认 `all`）和 `$2 = PUBLISH_TASK`（默认 `publishToMavenLocal`）两个
位置参数，通过 `KUIKLY_AGP_VERSION` / `KUIKLY_KOTLIN_VERSION` 固定
toolchain，Maven Central 凭据统一交给 Gradle 发布插件自身读取。
`2.1.21_publish.sh` 强制 `KUIKLY_USE_SOURCE=false`，保证生成的 POM
以 Maven 坐标记录上游 Kuikly 依赖（而非 `project(":core")` 引用）。
A2UI 语义版本和 Kuikly 坐标版本可通过环境变量临时覆盖：

```bash
# 覆盖 A2UI 自身发布版本
A2UI_VERSION=1.0.0 bash publish/2.1.21_publish.sh

# 覆盖 A2UI 依赖的 Kuikly 坐标版本
KUIKLY_VERSION=2.30.0 bash publish/2.1.21_publish.sh
```

但发版主路径仍推荐直接修改 `A2uiVersion.kt` 中的
`DEFAULT_A2UI_SEMANTIC` 和 `gradle.properties` 中的 `kuikly.version`，
以保证不同环境下版本一致。

iOS Pod 没有发布脚本，请手工执行 `pod trunk push`（见
[publish/README.md](publish/README.md)）。

最终所有产物共享同一个 **semantic** 版本标签；
Maven 侧坐标会额外带上 Kotlin/ohos 后缀。

注意：**上游 Kuikly Core 依赖版本**由本仓库的 `kuikly.version`
（`gradle.properties`）单独控制，不受 `A2UI_VERSION` 影响。
