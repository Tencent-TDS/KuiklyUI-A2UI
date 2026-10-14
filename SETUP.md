# KuiklyUI-A2UI Project — Setup Guide

Running this project depends on KuiklyUI. Before the complete publishing
pipeline for Maven / CocoaPods / OHPM is ready, this project consumes the
following modules as source dependencies from a local KuiklyUI checkout:
`:core`, `:core-render-android`, `:compose`, `:core-annotations`, `:core-ksp`,
`core-render-ohos`, `core-render-ios`, `OpenKuiklyIOSRender`.

## 1. KuiklyUI source-dependency mode: configure the local KuiklyUI path

Set `kuikly.projectDir` to your local KuiklyUI repository path.
This value is read by **all** build tools (Gradle, Podfiles, iOS setup
scripts, HarmonyOS DevEco Studio (via the `.kuikly-external` symlink),
OHOS build scripts).
So you only need to change **one place** to redirect dependencies for the
entire project.

Resolution order (first hit wins):

1. Gradle command-line argument `-Pkuikly.projectDir=/path`, or
   environment variable `KUIKLY_PROJECT_DIR=/path`
2. `kuikly.projectDir=/path` in `<project-root>/local.properties`
3. `kuikly.projectDir=<path>` in `<project-root>/gradle.properties`
   (default: `../KuiklyUI`)

Recommended: put an absolute path in `local.properties` (this file is git-ignored).

## 2. KuiklyUI artefact-dependency mode: published Maven / Pod / OHPM

The project currently depends on KuiklyUI Core as **local source** via the
`kuikly.projectDir` setting above.
Once the corresponding Kuikly Core artefacts are published (either in your
own Maven repo, or a CocoaPods spec repo / ohpm registry), you can switch
per platform.

**Android / KMP** — open [settings.gradle.kts](settings.gradle.kts) (and
its ohos variant), remove the four `include(":core", ...)` /
`project(":core").projectDir = ...` lines. Then in each module’s
`build.*.gradle.kts`, replace `project(":core")` with a Maven coordinate,
for example:

```kotlin
// Before (local source):
api(project(":core"))
// After (published Maven artefact):
api("com.tencent.kuikly-open:core:${Version.getCoreVersion()}")
```

Apply the same replacement for `:core-annotations`, `:core-ksp`,
`:core-render-android`, `:compose`.
`Version.getCoreVersion()` in
[buildSrc/](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)
already computes the upstream version according to the rules.

**iOS** — in each `iosAppFor*/Podfile`, replace:

```ruby
pod 'OpenKuiklyIOSRender', :path => '../.kuikly-external'
```

with the published pod:

```ruby
pod 'OpenKuiklyIOSRender', '~> <upstream-kuikly-version>'
```

A2UI-side pods (`a2ui`, `OpenA2UIIOSRender`) should still use
`:path => 'path'` during local development. This repository does not
ship an automated iOS pod publish script (aligned with the upstream
Kuikly project); to publish, run `pod trunk push <podspec>
--allow-warnings` manually from each pod's directory. See
[publish/README.md](publish/README.md) for the exact commands.
Among them, `a2ui_demo` is a **business-side sample** pod. Downstream
integrators are expected to implement their own equivalent KMP module and
integrate it via `:path`, so this pod is intentionally not published.

**HarmonyOS** — in `a2ui-render-ohos/oh-package.json5`, replace:

```json5
"@kuikly-open/render": "file:../.kuikly-external/core-render-ohos"
```

with:

```json5
"@kuikly-open/render": "^<upstream-render-version>"
```

The ohpm package `@kuikly-open/a2ui-render` is not published by any script
in this repository (aligned with the upstream Kuikly project). To publish
it, cd into `a2ui-render-ohos/` and run `ohpm pack` / `ohpm publish`
manually, after flipping the local `file:` dependency to a published
semver as shown above.

## 3. A2UI artefact versioning strategy

This repository includes four types of artefacts and uses **two related but
different** version schemes:

| Artefact | Scheme | Example | Source |
|---|---|---|---|
| Android AAR / KMP klib on Maven (`com.tencent.kuikly:a2ui`, `a2ui-render-android`) | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `0.9.0-2.1.21-SNAPSHOT`, `0.9.0-2.0.21-ohos-SNAPSHOT` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods (`a2ui`, `OpenA2UIIOSRender`) | `${semantic}` (plain SemVer, required by CocoaPods) | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm (`@kuikly-open/a2ui-render`) | `${semantic}` (plain SemVer, required by OHPM) | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| README / display usage | `${semantic}` | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic()` |

The Kotlin suffix in Maven coordinates follows the Kuikly Core convention
(`com.tencent.kuikly:core:2.0.0-2.1.21-SNAPSHOT`) — KMP `.klib` and metadata
binaries are not binary-compatible across Kotlin compiler versions,
so each supported toolchain must publish under its own coordinate.

Both APIs for fetching A2UI artefact versions are located in
[`buildSrc/src/main/java/A2uiVersion.kt`](buildSrc/src/main/java/A2uiVersion.kt).

### Override semantic version

Priority order (`getSemantic` and `getArtifactVersion` share the same
semantic base; mirrors Kuikly Core's `Version.getKuiklyVersion()`):

1. `A2UI_VERSION=1.0.0 ./gradlew ...` (environment variable)
2. Otherwise fallback to `DEFAULT_A2UI_SEMANTIC` in `A2uiVersion.kt`

### Override Kotlin suffix

The Kotlin segment in Maven coordinates always reads
`Version.getKotlinVersion()`
(from
[`KuiklyKotlinBuildVar.kt`](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)),
which responds to `KUIKLY_KOTLIN_VERSION`.
HarmonyOS builds triggered via `2.0_a2ui_ohos_build.sh` automatically set
`KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010`, which produces the
`-ohos` marker in the final coordinate (`0.9.0-2.0.21-ohos-SNAPSHOT`).

### CI one-liners

The three Gradle-based publish scripts (`2.1.21_publish.sh`,
`2.0_ohos_publish.sh`) are aligned with the Kuikly project: they take
`$1 = MODULE` (default `all`) and `$2 = PUBLISH_TASK` (default
`publishToMavenLocal`) as positional arguments, read `KUIKLY_AGP_VERSION`
/ `KUIKLY_KOTLIN_VERSION` for toolchain, and leave Maven Central
credentials to the Gradle publish plugin itself. The semantic version
can be overridden on the fly via the `A2UI_VERSION` env var (same
convention as Kuikly `KUIKLY_VERSION`):

```bash
A2UI_VERSION=1.0.0 bash publish/2.1.21_publish.sh
A2UI_VERSION=1.0.0 bash publish/2.0_ohos_publish.sh
```

But the recommended release path is to edit `DEFAULT_A2UI_SEMANTIC` in
`A2uiVersion.kt` directly, so that the version is reproducible across
environments.

iOS pods have no publish script — run `pod trunk push` manually (see
[publish/README.md](publish/README.md)).

In the end, all artefacts share the same **semantic** version tag;
Maven coordinates additionally carry the Kotlin/ohos suffix.

Note: the **upstream Kuikly Core dependency version** is a separate config
item (see `Version.getKuiklyVersion()` in
[`KuiklyKotlinBuildVar.kt`](buildSrc/src/main/java/KuiklyKotlinBuildVar.kt)),
and is not affected by `A2UI_VERSION`.
