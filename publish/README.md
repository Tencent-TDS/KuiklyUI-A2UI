# KuiklyUI-A2UI 工程 · 发版脚本索引

本目录聚合 KuiklyUI-A2UI 工程（对 Google A2UI 协议的 Kuikly 实现）的发版工具与兼容性 patch 配置：

| 目标产物 | 通道 | 脚本 |
|---------|------|------|
| **Android AAR + KMP klib**（`com.tencent.kuikly-a2ui-open:a2ui` / `com.tencent.kuikly-a2ui-open:a2ui-render-android`，Kotlin 2.1.21 主线） | 本地 `~/.m2` / Sonatype Maven Central | [`2.1.21_publish.sh`](2.1.21_publish.sh) |
| **HarmonyOS KMP klib**（`com.tencent.kuikly-a2ui-open:a2ui`，Kotlin `2.0.21-KBA-010` / `-ohos` 坐标） | 本地 `~/.m2` / Sonatype Maven Central | [`2.0_ohos_publish.sh`](2.0_ohos_publish.sh) |
| **iOS Pod**（`a2ui` / `OpenKuiklyA2UIIOSRender`；`a2ui_demo` 属业务示例代码不发布） | pod trunk / 私有 spec repo | 无脚本，手工发布（见下文） |

> HarmonyOS ohpm 包（`@kuikly-a2ui-open/render`）与 iOS Pod 当前都不在本仓库脚本范围内发布，与 Kuikly 工程保持一致。iOS Pod 请在对应目录手工执行 `pod trunk push`，ohpm 包请在 `a2ui-render-ohos/` 目录下手工执行 `ohpm pack` / `ohpm publish`。

其中 [`FileReplacer.java`](FileReplacer.java) 是一个通用文本 patch 工具，[`compatible/`](compatible/) 目录下的 YAML 配置文件描述"切换到某个兼容构建（例如 HarmonyOS 2.0 + Gradle 8.0）时需要临时改哪些文件"，配合 `2.0_a2ui_ohos_build.sh` / `2.1.21_publish.sh` / `2.0_ohos_publish.sh` 使用。

## Android 端发版用法（`2.1.21_publish.sh`）

脚本结构完全对齐 Kuikly 工程（`KuiklyUI/publish/2.1.21_publish.sh`）：

```bash
# 发所有 Android 端模块到本地 ~/.m2（默认发布方式）
bash publish/2.1.21_publish.sh

# 只发 :a2ui 到本地
bash publish/2.1.21_publish.sh a2ui

# 发所有模块到 Maven Central
bash publish/2.1.21_publish.sh all publishAllPublicationsToMavenCentralRepository

# 发单个模块到 Maven Central Snapshot
bash publish/2.1.21_publish.sh a2ui publishAllPublicationsToMavenCentralSnapshotRepository
```

参数：

| 位置 | 名称 | 默认值 | 说明 |
|------|------|--------|------|
| `$1` | `MODULE` | `all` | 模块名；`all` 时依次发布 `:a2ui` 与 `:a2ui-render-android` |
| `$2` | `PUBLISH_TASK` | `publishToMavenLocal` | Gradle 发布 task 名；本地用 `publishToMavenLocal`，远端用 `publishAllPublicationsToMavenCentralRepository` 或其 Snapshot 变体 |

脚本会在 Gradle 执行前后分别调用 `java publish/FileReplacer.java replace/restore publish/compatible/2.1.21.yaml`，用于按需做跨 Kotlin 版本的文件兼容性替换与还原（当前 2.1.21 配置为空占位，与 Kuikly 保持结构一致）。

Maven Central / GPG 签名所需的凭据不再由脚本做环境变量硬性校验，统一由 Gradle 发布插件自身读取（例如 `~/.gradle/gradle.properties` 中配置 `mavenCentralUsername` / `mavenCentralPassword` / `signing.*`），用法与 Kuikly 工程一致。

## HarmonyOS 端发版用法（`2.0_ohos_publish.sh`）

脚本结构完全对齐 Kuikly 工程（`KuiklyUI/publish/2.0_ohos_publish.sh`）：

```bash
# 发所有 ohos 端模块到本地 ~/.m2（默认发布方式）
bash publish/2.0_ohos_publish.sh

# 只发 :a2ui 到本地
bash publish/2.0_ohos_publish.sh a2ui

# 发所有模块到 Maven Central
bash publish/2.0_ohos_publish.sh all publishAllPublicationsToMavenCentralRepository

# 发单个模块到 Maven Central Snapshot
bash publish/2.0_ohos_publish.sh a2ui publishAllPublicationsToMavenCentralSnapshotRepository
```

参数：

| 位置 | 名称 | 默认值 | 说明 |
|------|------|--------|------|
| `$1` | `MODULE` | `all` | 模块名；`all` 时发布 `:a2ui`（当前 ohos settings 中唯一需要发布的 A2UI 自家 KMP 模块） |
| `$2` | `PUBLISH_TASK` | `publishToMavenLocal` | Gradle 发布 task 名；本地用 `publishToMavenLocal`，远端用 `publishAllPublicationsToMavenCentralRepository` 或其 Snapshot 变体 |

脉本会使用 `-c settings.2.0.ohos.gradle.kts` 切换到 HarmonyOS 构建变体，并通过 `KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010` 环境变量驱动 Kotlin toolchain，最终产出带 `-ohos` 后缀的 Maven 坐标（例如 `com.tencent.kuikly-a2ui-open:a2ui:1.0.0-2.0.21-ohos`）。

配套的兼容性替换由 `publish/compatible/2.0_ohos.yaml` 定义（Gradle Wrapper 从 7.6.3 切到 8.0），执行前后由脚本自动 `replace` / `restore`。

## iOS 端发版用法（手工）

本仓库不提供 iOS Pod 发版脚本（与 Kuikly 工程保持一致）。两个 iOS pod（`a2ui`、`OpenKuiklyA2UIIOSRender`）的发布流程如下：

### 1) `OpenKuiklyA2UIIOSRender`（纯 ObjC/Swift 源码 pod）

```bash
cd a2ui-render-ios
pod trunk push OpenKuiklyA2UIIOSRender.podspec --allow-warnings
# 或私有 spec repo:
# pod repo push <repo-alias> OpenKuiklyA2UIIOSRender.podspec --allow-warnings
```

版本号由 [`OpenKuiklyA2UIIOSRender.podspec`](../a2ui-render-ios/OpenKuiklyA2UIIOSRender.podspec) 中的 `spec.version` 决定，默认与 `A2uiVersion.kt` 的 `DEFAULT_A2UI_SEMANTIC` 保持一致。如需临时覆盖，可用 `A2UI_VERSION=x.y.z pod trunk push ...`，但发版主路径仍推荐直接修改 `DEFAULT_A2UI_SEMANTIC` 后同步 podspec 默认值。

### 2) `a2ui`（KMP 产物 A2UICore.framework）

先在工程根目录生成 podspec 与 xcframework，再推送：

```bash
KUIKLY_AGP_VERSION=7.4.2 KUIKLY_KOTLIN_VERSION=2.1.21 \
  ./gradlew -c settings.2.1.21.gradle.kts \
            :a2ui:podspec :a2ui:podPublishXCFramework --stacktrace

cd a2ui
pod trunk push a2ui.podspec --allow-warnings
```

> `a2ui_demo` 属业务示例代码（`demo/a2ui_demo.podspec`），仅供下游 `:path` 本地消费，有意不发布。

## 通用前置

1. **依赖 KuiklyUI**：默认走 **产物模式**（`kuikly.useSource=false`），从 Maven 仓读取 `com.tencent.kuikly-open:core` 等坐标，无需 Kuikly 源码；需要和 Kuikly 一起源码调试时，参见 [`../SETUP-zh_CN.md`](../SETUP-zh_CN.md) 开启源码模式（`kuikly.useSource=true` + `kuikly.projectDir`）。
2. **JDK 1.8+**：`java -version` 需 ≥ 1.8。字节码目标与 KuiklyUI Core 对齐，固定为 1.8，本地或 CI runner 使用 JDK 8 / 11 / 17 均可——Gradle 会按 1.8 产出 class 文件（即流水线的 runner JDK 不必升级到 17）。
3. **Kotlin / AGP 工具链**：由各脚本内 `KUIKLY_AGP_VERSION` / `KUIKLY_KOTLIN_VERSION` 环境变量固定——`2.1.21_publish.sh` 使用 Kotlin 2.1.21，`2.0_ohos_publish.sh` 使用 Kotlin `2.0.21-KBA-010`。AGP 统一为 7.4.2。
4. **Kuikly 产物版本**：由 `kuikly.version` 决定（默认 `2.29.2`，拼上 Kotlin 后缀形如 `2.29.2-2.1.21`）。可通过 `-Pkuikly.version=` / `KUIKLY_VERSION` env / `local.properties` 覆盖，详见下文。

> `2.1.21_publish.sh` 强制 `KUIKLY_USE_SOURCE=false`：发布态必须用 Maven 坐标依赖已发布的 Kuikly 产物，这样 POM 中记录的也是坐标而非 `project(":core")` 引用。本地源码调试请直接用 `./gradlew publishToMavenLocal`，不要走此脚本。

## 版本号

版本号由 [`../buildSrc/src/main/java/A2uiVersion.kt`](../buildSrc/src/main/java/A2uiVersion.kt) 统一管理，提供两个 API：

- `A2uiVersion.getSemantic()` — 语义版本，形如 `1.0.0`。用于 iOS pod（CocoaPods 强 SemVer）、HarmonyOS ohpm 包（同上）、README 显示。
- `A2uiVersion.getArtifactVersion()` — Maven artifact 版本，形如 `1.0.0-2.1.21` / `1.0.0-2.0.21-ohos`。语义版本后拼当前 Kotlin 版本，规则完全对齐 Kuikly Core `getCoreVersion()`，避免不同 Kotlin toolchain 的 KMP 产物在 Maven 上互相覆盖。

版本号由 `buildSrc/A2uiVersion.kt` 内的 `DEFAULT_A2UI_SEMANTIC` 常量决定，发版时请直接修改源码。也支持通过环境变量临时覆盖（优先级高于默认值，对齐 Kuikly `KUIKLY_VERSION` 的用法）：

```bash
A2UI_VERSION=1.0.0 bash publish/2.1.21_publish.sh
A2UI_VERSION=1.0.0 bash publish/2.0_ohos_publish.sh
```

但发版主路径仍推荐直接修改 `DEFAULT_A2UI_SEMANTIC`，避免同一版本在不同环境产出不一致。

当前默认：

- 语义版本：`1.0.0`（iOS pod / ohpm 包直接使用）
- Maven artifact（Kotlin 2.1.21 主线，`2.1.21_publish.sh`）：`1.0.0-2.1.21`
- Maven artifact（HarmonyOS 2.0 兼容构建，Kotlin `2.0.21-KBA-010`，`2.0_ohos_publish.sh`）：`1.0.0-2.0.21-ohos`

## 上游 Kuikly 产物版本（`kuikly.version`）

A2UI 产物依赖的上游 Kuikly 坐标版本由 [`gradle.properties`](../gradle.properties) 中的 `kuikly.version` 决定（默认 `2.29.2`），实际拼接规则对齐 Kuikly Core 发布 core/compose/ksp 时所用的坐标约定（见 [`KuiklyDep.kuiklyCoordinateVersion`](../buildSrc/src/main/java/KuiklyDep.kt)）：

- **值被原样使用**：是否带 `-SNAPSHOT` 必须自己写明（不再自动追加）
- 在正确位置拼上当前 Kotlin 版本：`2.29.2` × Kotlin 2.1.21 → `com.tencent.kuikly-open:core:2.29.2-2.1.21`；`2.29.2-SNAPSHOT` × Kotlin 2.1.21 → `com.tencent.kuikly-open:core:2.29.2-2.1.21-SNAPSHOT`
- Kotlin 为 `2.0.21-KBA-010` 时自动把 `KBA-010` 替换为 `ohos`

覆盖优先级（高 → 低）：

```bash
# 1) Gradle -P
./gradlew ... -Pkuikly.version=2.29.2

# 2) env
KUIKLY_VERSION=2.30.0 bash publish/2.1.21_publish.sh

# 3) local.properties / gradle.properties
```

与 `A2UI_VERSION` 相互独立：前者控制"依赖哪个 Kuikly 产物"，后者控制"A2UI 自身发布什么版本"。
