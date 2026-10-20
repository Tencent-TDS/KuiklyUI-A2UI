# KuiklyUI-A2UI 工程 — 环境配置指南

本工程的运行依赖 KuiklyUI。A2UI 对 KuiklyUI 提供**两种依赖模式**，由
`kuikly.useSource` 单一开关控制（详见下文）。

- **产物模式（默认）** —— 从 `com.tencent.kuikly-open:*` 的 Maven /
  Pod / OHPM 已发布包消费，不需要本地 KuiklyUI 源码。
- **源码模式（显式开启）** —— 从本地 KuiklyUI 仓库源码依赖以下模块：
  `:core`、`:core-render-android`、`:compose`、`:core-annotations`、
  `:core-ksp`、`core-render-ohos`、`core-render-ios`、
  `OpenKuiklyIOSRender`。用于与 KuiklyUI 一起源码联调。

> 作用范围：`kuikly.useSource` 开关只影响 Kotlin 2.1.21 的
> Android / KMP 构建（`settings.gradle.kts` /
> `settings.2.1.21.gradle.kts`）。HarmonyOS 变体
> （`settings.2.0.ohos.gradle.kts`）当前仍强制源码模式；iOS CocoaPods
> 也仍强制源码模式（`:a2ui/build.2.1.21.gradle.kts` 中有显式守卫，
> 当 `kuikly.useSource=false` 时任何 Pod 任务都会立即报错）。

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

## 4. 源码模式下的各平台说明

**iOS** — 在每个 `iosAppFor*/Podfile` 中保持：

```ruby
pod 'OpenKuiklyIOSRender', :path => '../.kuikly-external'
```

A2UI 侧 pods（`a2ui`、`OpenA2UIIOSRender`）在本地开发期间始终使用
`:path => 'path'`。本仓库没有自动发布 iOS Pod 的脚本（与 Kuikly
工程保持一致），如需发布请在对应目录执行 `pod trunk push <podspec>
--allow-warnings`，具体命令见 [publish/README.md](publish/README.md)。
其中 `a2ui_demo` pod 是**业务侧示例**，下游集成方会自行实现对应 KMP
模块并通过 `:path` 方式接入，因此该 pod 有意不发布。

**HarmonyOS** — 在 `a2ui-render-ohos/oh-package.json5` 中：

```json5
"@kuikly-open/render": "file:../.kuikly-external/core-render-ohos"
```

本仓库当前没有自动发布 ohpm 包 `@kuikly-open/a2ui-render` 的脚本
（与 Kuikly 工程保持一致）。如需发布请先把本地 `file:` 依赖改回已
发布的 semver，然后在 `a2ui-render-ohos/` 目录手工执行 `ohpm pack` /
`ohpm publish`。

## 5. A2UI 产物版本策略

本仓库包含四类产物，采用**两套相关但不同**的版本方案：

| 产物 | 方案 | 示例 | 来源 |
|---|---|---|---|
| Maven 上的 Android AAR / KMP klib（`com.tencent.kuikly-open:a2ui`、`a2ui-render-android`） | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `0.9.0-2.1.21-SNAPSHOT`、`0.9.0-2.0.21-ohos-SNAPSHOT` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods（`a2ui`、`OpenA2UIIOSRender`） | `${semantic}`（纯 SemVer，CocoaPods 要求） | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm（`@kuikly-open/a2ui-render`） | `${semantic}`（纯 SemVer，OHPM 要求） | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| README / 展示用途 | `${semantic}` | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic()` |

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
`-ohos` 标记（`0.9.0-2.0.21-ohos-SNAPSHOT`）。

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
