# KuiklyUI-A2UI Project — Setup Guide

Running this project depends on KuiklyUI. A2UI supports **two dependency
modes** against KuiklyUI; the active mode is chosen by a single switch
`kuikly.useSource` (details below).

- **Artifact mode (default)** — consume published Maven / Pod / OHPM
  packages from `com.tencent.kuikly-open:*`. No local KuiklyUI checkout
  required.
- **Source mode (opt-in)** — consume the following modules as local
  source from a KuiklyUI checkout: `:core`, `:core-render-android`,
  `:compose`, `:core-annotations`, `:core-ksp`, `core-render-ohos`,
  `core-render-ios`, `OpenKuiklyIOSRender`. Useful when you need to
  co-debug A2UI with KuiklyUI changes.

> Scope: the `kuikly.useSource` switch only affects the Kotlin 2.1.21
> Android / KMP build (`settings.gradle.kts` / `settings.2.1.21.gradle.kts`).
> The HarmonyOS variant (`settings.2.0.ohos.gradle.kts`) is still
> source-only for now. iOS CocoaPods now supports both modes —
> `pod 'a2ui'` / `pod 'a2ui_demo'` can be consumed either via
> `:path =>` into this repo or via a published version (CocoaPods
> trunk). See the iOS section in §4 for the full rundown.

## 1. Choose dependency mode: `kuikly.useSource`

Default is `false` (artifact mode).

Resolution order (first non-blank wins):

1. `-Pkuikly.useSource=true|false` on the Gradle command line
2. `KUIKLY_USE_SOURCE=true|false` environment variable
3. `<project-root>/local.properties` → key `kuikly.useSource`
4. `<project-root>/gradle.properties` → key `kuikly.useSource`

```bash
# One-shot: open the IDE / run gradle under source mode
./gradlew -Pkuikly.useSource=true :androidAppForKuiklyPage:installDebug

# Per-machine persistent (recommended for Kuikly contributors):
echo "kuikly.useSource=true" >> local.properties
```

## 2. Configure the upstream Kuikly version — artifact mode

Under artifact mode, A2UI resolves Kuikly modules as
`com.tencent.kuikly-open:<module>:<coord-version>`. The coordinate
version is composed by combining `kuikly.version` with the current
Kotlin toolchain, following the convention Kuikly Core publishes with
(see [`KuiklyDep.kuiklyCoordinateVersion`](buildSrc/src/main/java/KuiklyDep.kt)):

- `kuikly.version=2.29.2` + Kotlin `2.1.21` → `2.29.2-2.1.21`
- `kuikly.version=2.29.2-SNAPSHOT` + Kotlin `2.1.21` → `2.29.2-2.1.21-SNAPSHOT`
- `kuikly.version=2.29.2-SNAPSHOT` + Kotlin `2.0.21-KBA-010` → `2.29.2-2.0.21-ohos-SNAPSHOT`

The value is used **verbatim** — any `-SNAPSHOT` tail must be spelled
out explicitly. The resolver only inserts the Kotlin toolchain segment
(and rewrites the `KBA-010` suffix to `ohos` for HarmonyOS builds).

Resolution order of `kuikly.version`:

1. `-Pkuikly.version=…`
2. `KUIKLY_VERSION` env (shared with Kuikly Core — co-building both
   repos only needs this single env var)
3. `local.properties` → `kuikly.version`
4. `gradle.properties` → `kuikly.version` (default: `2.29.2`)

> The upstream Kuikly artifact must already be published to a Maven repo
> that your build can resolve. For local co-development without
> publishing, use source mode (section 3) instead.

## 3. Configure the Kuikly checkout path — source mode

Only required when `kuikly.useSource=true`. Set `kuikly.projectDir` to
your local KuiklyUI repository path. This value is read by **all**
build tools (Gradle, Podfiles, iOS setup scripts, HarmonyOS DevEco
Studio (via the `.kuikly-external` symlink), OHOS build scripts).

Resolution order (first hit wins):

1. Gradle command-line argument `-Pkuikly.projectDir=/path`, or
   environment variable `KUIKLY_PROJECT_DIR=/path`
2. `kuikly.projectDir=/path` in `<project-root>/local.properties`
3. `kuikly.projectDir=<path>` in `<project-root>/gradle.properties`
   (default: `../KuiklyUI`)

Recommended: put an absolute path in `local.properties` (git-ignored).

## 4. Per-platform notes

**iOS** — this repo ships two kinds of A2UI pods:

| Pod | Framework | Source of the five `@ObjCName` entry classes | Typical consumer |
|---|---|---|---|
| `pod 'a2ui'` | `A2UICore.framework` (produced by `:a2ui`) | Originals in `:a2ui/iosMain` (`A2UICoreEntry` / `A2UICustomViewRegistryBridge` / `A2UIIconRegistryBridge` / `A2UICustomFunctionRegistryBridge` / `A2UIPickerResultBridge`) | `iosAppForNativePage`, `iosAppForTest` — Native-page embedding |
| `pod 'a2ui_demo'` | `A2UIDemo.framework` (produced by `:demo`) | Thin shells in `:demo/iosMain` (same five `@ObjCName` names, forwarding to `:a2ui`'s commonMain singletons) | `iosAppForKuiklyPage`, `iosAppForBothPages` — Kuikly-page embedding |

> **Pick-one-pod rule**: both pods register `@ObjCName` classes under
> the same ObjC names (e.g. `A2UICoreEntry`), so they cannot be linked
> into the same app. In practice each `iosAppFor*` demo's Podfile uses
> exactly one of them. Downstream products are expected to mimic the
> `a2ui_demo` pattern (own KMP business module + its own iosMain
> shells of the five `@ObjCName` entry classes), rather than depending
> on `a2ui_demo` itself.

**In-repo local development** — regardless of source/artifact mode,
`iosAppFor*/Podfile` consumes the A2UI-side pods (`a2ui`, `a2ui_demo`,
`OpenA2UIIOSRender`) via `:path => '../a2ui'` / `:path => '../demo'` /
`:path => '../a2ui-render-ios'`. This is a fixed convention of this
repo's Podfiles.

**Artifact-mode downstream integrators** — may pull published versions
from the CocoaPods trunk instead:

```ruby
pod 'a2ui', '~> 0.9.0'
pod 'OpenA2UIIOSRender', '~> 0.9.0'
```

This repository does not ship an automated iOS pod publish script
(aligned with the upstream Kuikly project); to publish, run
`pod trunk push <podspec> --allow-warnings` manually from each pod's
directory. See [publish/README.md](publish/README.md) for the exact
commands. `a2ui_demo` — while consumed inside this repo by
`iosAppForKuiklyPage` / `iosAppForBothPages` via `:path => '../demo'`
— is positioned as a **business-side sample** rather than a public
API; downstream integrators are expected to implement their own
equivalent KMP module and integrate it via `:path`, so this pod is
intentionally not published to the trunk.

**Source mode only** — additionally keep the upstream Kuikly pod
linked against the local checkout:

```ruby
pod 'OpenKuiklyIOSRender', :path => '../.kuikly-external'
```

Under artifact mode, `OpenKuiklyIOSRender` resolves to the published
version and `.kuikly-external` is not needed.

**HarmonyOS** — in `a2ui-render-ohos/oh-package.json5`:

```json5
"@kuikly-open/render": "file:../.kuikly-external/core-render-ohos"
```

The ohpm package `@kuikly-open/a2ui-render` is not published by any
script in this repository (aligned with the upstream Kuikly project).
To publish it, cd into `a2ui-render-ohos/` and run `ohpm pack` /
`ohpm publish` manually, after flipping the local `file:` dependency
to a published semver.

## 5. A2UI artefact versioning strategy

This repository includes four types of artefacts and uses **two related but
different** version schemes:

| Artefact | Scheme | Example | Source |
|---|---|---|---|
| Android AAR / KMP klib on Maven (`com.tencent.kuikly-open:a2ui`, `a2ui-render-android`) | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `0.9.0-2.1.21-SNAPSHOT`, `0.9.0-2.0.21-ohos-SNAPSHOT` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods (`a2ui`, `OpenA2UIIOSRender`) | `${semantic}` (plain SemVer, required by CocoaPods) | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm (`@kuikly-open/a2ui-render`) | `${semantic}` (plain SemVer, required by OHPM) | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic(project)` |
| README / display usage | `${semantic}` | `0.9.0-SNAPSHOT` | `A2uiVersion.getSemantic()` |

The Kotlin suffix in Maven coordinates follows the Kuikly Core convention
(`com.tencent.kuikly-open:core:2.29.2-2.1.21-SNAPSHOT`) — KMP `.klib` and
metadata binaries are not binary-compatible across Kotlin compiler
versions, so each supported toolchain must publish under its own
coordinate.

Both APIs for fetching A2UI artefact versions are located in
[`buildSrc/src/main/java/A2uiVersion.kt`](buildSrc/src/main/java/A2uiVersion.kt).

### Override A2UI semantic version

Priority order (`getSemantic` and `getArtifactVersion` share the same
semantic base; env-only, deliberately simpler than
[`KuiklyDep.kuiklyBaseVersion`](buildSrc/src/main/java/KuiklyDep.kt)):

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

The two Gradle-based publish scripts (`2.1.21_publish.sh`,
`2.0_ohos_publish.sh`) are aligned with the Kuikly project: they take
`$1 = MODULE` (default `all`) and `$2 = PUBLISH_TASK` (default
`publishToMavenLocal`) as positional arguments, read `KUIKLY_AGP_VERSION`
/ `KUIKLY_KOTLIN_VERSION` for toolchain, and leave Maven Central
credentials to the Gradle publish plugin itself. `2.1.21_publish.sh`
forces `KUIKLY_USE_SOURCE=false` so that the generated POMs always
record upstream Kuikly as Maven coordinates (not `project(":core")`
references). The A2UI semantic version and the Kuikly coordinate
version can be overridden on the fly via env vars:

```bash
# Override A2UI's own release version
A2UI_VERSION=1.0.0 bash publish/2.1.21_publish.sh

# Override which Kuikly coordinate A2UI depends on
KUIKLY_VERSION=2.30.0 bash publish/2.1.21_publish.sh
```

But the recommended release path is to edit `DEFAULT_A2UI_SEMANTIC` in
`A2uiVersion.kt` and `kuikly.version` in `gradle.properties` directly,
so that the version is reproducible across environments.

iOS pods have no publish script — run `pod trunk push` manually (see
[publish/README.md](publish/README.md)).

In the end, all artefacts share the same **semantic** version tag;
Maven coordinates additionally carry the Kotlin/ohos suffix.

Note: the **upstream Kuikly Core dependency version** is a separate
config item (`kuikly.version` in `gradle.properties`), and is not
affected by `A2UI_VERSION`.
