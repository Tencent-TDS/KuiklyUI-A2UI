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

> In addition to `kuikly.useSource` (which governs the upstream Kuikly
> dependency), this repository has a **separate switch `a2ui.useSource`**
> that controls whether A2UI's **own modules** (`:a2ui` /
> `:a2ui-render-android`) are consumed from source or from published
> artefacts on the iOS / HarmonyOS side. The two switches are
> independent, and the per-platform constraints differ — see §2.

> Scope: the `kuikly.useSource` switch drives Kuikly consumption on all
> three platforms:
>
> - **Android / KMP** (Kotlin 2.1.21, `settings.gradle.kts` /
>   `settings.2.1.21.gradle.kts`) — artifact mode uses
>   `com.tencent.kuikly-open:*` Maven coordinates; source mode uses
>   `includeBuild(<kuikly.projectDir>)`.
> - **iOS CocoaPods** — artifact mode uses
>   `'<kuikly.version>'` from the CocoaPods trunk; source mode uses
>   `:path => <kuikly.projectDir>`.
> - **HarmonyOS** (`settings.2.0.ohos.gradle.kts` + each
>   `ohosAppFor*/entry/oh-package.json5` + `a2ui-render-ohos/oh-package.json5`
>   + `ohosAppFor*/build-profile.json5`) — artifact mode consumes
>   `"@kuikly-open/render": "<kuikly.version>"` from the ohpm registry;
>   source mode is set up by
>   [`scripts/apply-ohos-mode.sh`](scripts/apply-ohos-mode.sh), which
>   rewrites `oh-package.json5` to `"file:../..?/core-render-ohos"`
>   and re-inserts the `{"name":"render",
>   "srcPath":"../core-render-ohos"}` entry into
>   `build-profile.json5`'s `modules` array. The whole flow is driven
>   by [`scripts/link-kuikly.sh`](scripts/link-kuikly.sh) (see the
>   HarmonyOS section in §4).
>
> The A2UI-side iOS pods themselves (`pod 'a2ui'` / `pod 'a2ui_demo'` /
> `pod 'OpenKuiklyA2UIIOSRender'`) are always consumed via `:path =>` inside
> the in-repo `iosAppFor*` samples. See the iOS section in §4 for the
> full rundown.

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

## 2. Choose A2UI's own dependency mode: `a2ui.useSource`

**Default `true` (source mode).** This switch is fully independent of
`kuikly.useSource` and only governs whether A2UI's own modules
(`:a2ui` / `:a2ui-render-android`) are `include(...)`-ed in
`settings.*.gradle.kts`. That in turn decides whether the iOS pod
`pod 'a2ui'` can be resolved and whether the Gradle task
`:a2ui:linkShared…` exists on the HarmonyOS side.

Resolution order (fully symmetric with `kuikly.useSource`):

1. `-Pa2ui.useSource=true|false` on the Gradle command line
2. `A2UI_USE_SOURCE=true|false` environment variable
3. `<project-root>/local.properties` → key `a2ui.useSource`
4. `<project-root>/gradle.properties` → key `a2ui.useSource`

### 2.1 Per-platform constraints

A2UI's Native entry classes must be baked into the Kotlin/Native link
output (iOS's `A2UICore.framework`, OHOS's `libshared.so`). These
`.framework` / `.so` outputs are **link products**, not downloadable
artefacts on Maven / CocoaPods / ohpm, so some scenarios **require**
`a2ui.useSource=true`. Full breakdown:

| Scenario | How A2UI is consumed | `a2ui.useSource=false` (artifact mode) supported? |
|---|---|---|
| **Android / KMP**: all `androidAppFor*` | Maven coordinates `com.tencent.kuikly-a2ui-open:a2ui:<ver>` / `a2ui-render-android:<ver>` | ✅ Yes — Android side is 100% Maven-artifact driven |
| **iOS**: `iosAppForKuiklyPage` / `iosAppForBothPages` | `pod 'a2ui_demo', :path => '../demo'` → `:demo` consumes A2UI via `api(A2uiDep.a2ui(…))` | ✅ Yes — under artifact mode `:demo` pulls the `a2ui:<ver>` KMP coordinate from Maven |
| **iOS**: `iosAppForNativePage` / `iosAppForTest` | `pod 'a2ui', :path => '../a2ui'` → directly consumes `:a2ui/iosMain` originals | ❌ **Must be `true`** — the local `a2ui.podspec` requires the `:a2ui:generateDummyFramework` task, which only exists while `:a2ui` is included as a local subproject |
| **HarmonyOS**: `ohosAppForKuiklyPage` / `ohosAppForBothPages` | `libshared.so` from `:demo:linkSharedDebugSharedOhosArm64` (`:demo` consumes A2UI via `api(A2uiDep.a2ui(…))`) | Technically yes — under artifact mode `:demo` pulls `a2ui:<ver>-<kotlin>-ohos` from Maven. But the unified entrypoint script `2.0_a2ui_ohos_build.sh` **unconditionally `export`s `A2UI_USE_SOURCE=true`** for simplicity — see §2.3 |
| **HarmonyOS**: `ohosAppForNativePage` / `ohosAppForTest` | `libshared.so` from `:a2ui:linkSharedDebugSharedOhosArm64` | ❌ **Must be `true`** — this Gradle task only exists while `:a2ui` is included as a local subproject; a Kotlin/Native `.so` is a link product and published `.klib`s cannot substitute |

### 2.2 Behaviour of the four iOS `setup.sh` scripts

Each `iosAppFor*/setup.sh` handles `a2ui.useSource=false` differently,
in line with the per-scenario constraint above:

- `iosAppForKuiklyPage/setup.sh`, `iosAppForBothPages/setup.sh`:
  under artifact mode, **skip `:a2ui:generateDummyFramework`** (the
  task does not exist), run only `:demo:generateDummyFramework`, then
  let `pod install` pull `OpenKuiklyA2UIIOSRender` from the trunk.
- `iosAppForNativePage/setup.sh`, `iosAppForTest/setup.sh`:
  under artifact mode, **`exit 1` with a friendly error** instructing
  the user to flip `a2ui.useSource=true`. The Podfiles here use
  `pod 'a2ui', :path => '../a2ui'`; without `:a2ui` included the
  Gradle dummy-framework task is missing and `pod install` would fail
  later with an obscure error.

### 2.3 Why `2.0_a2ui_ohos_build.sh` unconditionally forces source mode

[`2.0_a2ui_ohos_build.sh`](2.0_a2ui_ohos_build.sh) produces two
`libshared.so` files in a single run:

| Gradle task | Artefact | Consumer apps |
|---|---|---|
| `:a2ui:linkSharedDebugSharedOhosArm64` | A2UI-runtime-only `libshared.so` | `ohosAppForNativePage`, `ohosAppForTest` |
| `:demo:linkSharedDebugSharedOhosArm64` | A2UI runtime + demo-Pager full `libshared.so` | `ohosAppForKuiklyPage`, `ohosAppForBothPages` |

The first task has a **hard requirement** on source mode (a
Kotlin/Native `.so` must be linked by the module that declares
`binaries.sharedLib(...)`; published `.klib`s cannot stand in). So the
script sets `export A2UI_USE_SOURCE=true` at the top, which takes
**precedence over** any `gradle.properties` / `local.properties`
setting — so the script still works even when the repo is tuned for
App-side artifact-mode testing.

> Strictly speaking, only the NativePage/Test production line requires
> source mode; the `:demo:linkShared…` line for KuiklyPage/BothPages
> could run under artifact mode on its own. The script just forces
> source mode uniformly for simplicity.

## 3. Configure the upstream Kuikly version — artifact mode

Under artifact mode, A2UI resolves Kuikly modules as
`com.tencent.kuikly-open:<module>:<coord-version>`. The coordinate
version is composed by combining `kuikly.version` with the current
Kotlin toolchain, following the convention Kuikly Core publishes with
(see [`KuiklyDep.kuiklyCoordinateVersion`](buildSrc/src/main/java/KuiklyDep.kt)):

- `kuikly.version=2.30.0` + Kotlin `2.1.21` → `2.30.0-2.1.21`
- `kuikly.version=2.30.0-SNAPSHOT` + Kotlin `2.1.21` → `2.30.0-2.1.21-SNAPSHOT`
- `kuikly.version=2.30.0-SNAPSHOT` + Kotlin `2.0.21-KBA-010` → `2.30.0-2.0.21-ohos-SNAPSHOT`

The value is used **verbatim** — any `-SNAPSHOT` tail must be spelled
out explicitly. The resolver only inserts the Kotlin toolchain segment
(and rewrites the `KBA-010` suffix to `ohos` for HarmonyOS builds).

Resolution order of `kuikly.version`:

1. `-Pkuikly.version=…`
2. `KUIKLY_VERSION` env (shared with Kuikly Core — co-building both
   repos only needs this single env var)
3. `local.properties` → `kuikly.version`
4. `gradle.properties` → `kuikly.version` (default: `2.30.0`)

> The upstream Kuikly artifact must already be published to a Maven repo
> that your build can resolve. For local co-development without
> publishing, use source mode (section 4) instead.

### 3.1 Upgrade Kuikly version — one-shot checklist

`kuikly.version` in `gradle.properties` is the **single source of truth
shared across all three platforms** at build time:

- Android / KMP (`KuiklyDep.kt`), iOS CocoaPods
  (`scripts/lib/kuikly_cocoapods.rb`), and HarmonyOS build scripts
  (`scripts/lib/kuikly-mode.sh`) all follow the same resolution order
  (`-P` → env → `local.properties` → `gradle.properties`) and fall back
  to `gradle.properties` as the final step.

However, a few places hold **frozen literal version strings** that are
not auto-synced from `gradle.properties` and must be edited by hand when
rolling the Kuikly dependency forward. The following checklist covers
everything:

| # | File | Required? | Why |
|---|---|---|---|
| 1 | [`gradle.properties`](gradle.properties) → `kuikly.version=` | **Required** | The real source consulted by all three platforms at build time. |
| 2 | [`a2ui-render-ohos/oh-package.json5`](a2ui-render-ohos/oh-package.json5) → `"@kuikly-open/render": "<ver>"` | **Required (artifact mode)** | Written literally in artifact-mode steady state. `scripts/apply-ohos-mode.sh` only rewrites it on **mode switch**, not on version bump. |
| 3 | [`ohosAppForKuiklyPage/entry/oh-package.json5`](ohosAppForKuiklyPage/entry/oh-package.json5) → same field | **Required (artifact mode)** | Same as above. |
| 4 | [`ohosAppForNativePage/entry/oh-package.json5`](ohosAppForNativePage/entry/oh-package.json5) → same field | **Required (artifact mode)** | Same as above. |
| 5 | [`ohosAppForBothPages/entry/oh-package.json5`](ohosAppForBothPages/entry/oh-package.json5) → same field | **Required (artifact mode)** | Same as above. |
| 6 | [`ohosAppForTest/entry/oh-package.json5`](ohosAppForTest/entry/oh-package.json5) → same field | **Required (artifact mode)** | Same as above. |
| 7 | [`OpenKuiklyA2UIIOSRender.podspec`](OpenKuiklyA2UIIOSRender.podspec) → `_kuikly_version` fallback (`'<ver>'`) | **Required before `pod trunk push`** | Anchors the `spec.dependency 'OpenKuiklyIOSRender', <ver>` carried by this pod when downstreams `pod install` it from CocoaPods trunk. |
| 8 | [`buildSrc/src/main/java/KuiklyDep.kt`](buildSrc/src/main/java/KuiklyDep.kt) → `DEFAULT_KUIKLY_VERSION` | Optional | Code-side final fallback; only hit when `gradle.properties` has no `kuikly.version` key. |
| 9 | [`scripts/lib/kuikly-mode.sh`](scripts/lib/kuikly-mode.sh) → `raw="<ver>"` fallback | Optional | Shell-side final fallback; same semantics as #8. |
| 10 | [`scripts/lib/kuikly_cocoapods.rb`](scripts/lib/kuikly_cocoapods.rb) → `DEFAULT_KUIKLY_VERSION` | Optional | Ruby-side final fallback for iOS Podfile; same semantics as #8. |
| 11 | `README.md` / `README-zh_CN.md` / `SETUP.md` / `SETUP-zh_CN.md` / `INTRODUCING_A2UI.md` / `publish/README.md` version snippets | Optional | Prose/examples only — does not affect the build. |

> **OHOS gotcha**: #2–#6 are the subtlest trap — in artifact-mode steady
> state the five `oh-package.json5` files already contain a hard-coded
> `"@kuikly-open/render": "<old-version>"` and are **not** touched by
> any build-time version resolver. If you bump `gradle.properties` and
> forget them, Android/KMP and iOS will pick up the new version while
> HarmonyOS silently stays on the old one. The cheapest cross-check is
> `grep -rn 'kuikly-open/render' .` after the bump.
>
> A quick workaround if you prefer not to hand-edit: with
> `kuikly.useSource=false` in `local.properties`, run
> `bash scripts/link-kuikly.sh` once in source mode and once back in
> artifact mode — the second run rewrites the five `oh-package.json5`
> files from the current `kuikly.version`. This is overkill for a plain
> version bump; hand-editing is usually simpler.

## 4. Configure the Kuikly checkout path — source mode

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

## 5. Per-platform notes

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
`iosAppFor*/Podfile` consumes the **A2UI-side** pods (`a2ui`,
`a2ui_demo`, `OpenKuiklyA2UIIOSRender`) via `:path => '../a2ui'` /
`:path => '../demo'` / `:path => '../a2ui-render-ios'`. This is a fixed
convention of this repo's Podfiles — A2UI's own Kotlin `.framework`
must be regenerated by Gradle `syncFramework` on each Xcode build and
cannot be consumed from trunk.

**Upstream Kuikly-side** pod (`OpenKuiklyIOSRender`) is governed by
`kuikly.useSource`, aligned with the Gradle-side
[`KuiklyDep`](buildSrc/src/main/java/KuiklyDep.kt):

| Mode | `OpenKuiklyIOSRender` declaration in `iosAppFor*/Podfile` |
|---|---|
| Artifact (default, `kuikly.useSource=false`) | `pod 'OpenKuiklyIOSRender', '2.30.0'` (from CocoaPods trunk) |
| Source (`kuikly.useSource=true`) | `pod 'OpenKuiklyIOSRender', :path => <kuikly.projectDir>` (local Kuikly checkout, for co-debugging) |

Resolution of the version follows the exact same order as Gradle-side
`kuikly.version` (env / `local.properties` / `gradle.properties` /
default `2.30.0`), see
[`scripts/lib/kuikly_cocoapods.rb`](scripts/lib/kuikly_cocoapods.rb).
`OpenKuiklyIOSRender` is a pure ObjC/C++ pod and ships to the trunk
with plain SemVer tags (no Kotlin suffix), so the Podfile-side logic
keeps only the SemVer segment of `kuikly.version` and strips any
`-SNAPSHOT` tail.

**Artifact-mode downstream integrators** — may pull published versions
from the CocoaPods trunk instead:

```ruby
pod 'a2ui', '~> 1.0.0'
pod 'OpenKuiklyA2UIIOSRender', '~> 1.0.0'
# OpenKuiklyIOSRender is pulled in transitively via
# OpenKuiklyA2UIIOSRender's spec.dependency (pinned to Kuikly 2.30.0 in
# OpenKuiklyA2UIIOSRender.podspec).
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

**HarmonyOS** — the OHOS side supports **both modes**, driven by the
same `kuikly.useSource` switch and fully aligned with Android / iOS.
Mode switching is orchestrated by
[`scripts/link-kuikly.sh`](scripts/link-kuikly.sh) (which internally
invokes [`scripts/apply-ohos-mode.sh`](scripts/apply-ohos-mode.sh)):

| Mode | `@kuikly-open/render` in `a2ui-render-ohos/oh-package.json5` | `@kuikly-open/render` in `ohosAppFor*/entry/oh-package.json5` | `modules` array in `ohosAppFor*/build-profile.json5` | On-disk layout |
|---|---|---|---|---|
| Artifact (default, `kuikly.useSource=false`) | `"@kuikly-open/render": "2.30.0"` (pulled from the ohpm registry) | Same (version matches) | Just `entry` + `a2ui_render`, no `render` block | No `.kuikly-external`, no `core-render-ohos/` |
| Source (`kuikly.useSource=true`) | `"@kuikly-open/render": "file:../core-render-ohos"` | `"@kuikly-open/render": "file:../../core-render-ohos"` | Adds `{"name":"render", "srcPath":"../core-render-ohos", …}` block | `.kuikly-external` symlinks to `kuikly.projectDir`; `core-render-ohos/` is an rsync mirror of `kuikly.projectDir/core-render-ohos` (git-ignored) |

> The version resolution follows the exact same order as Gradle- /
> CocoaPods-side `kuikly.version` (env / `local.properties` /
> `gradle.properties` / default `2.30.0`), and any `-SNAPSHOT` tail is
> stripped — the ohpm registry, like the CocoaPods trunk, only hosts
> released versions.

**Unified C/C++ entry point**: each
`ohosAppFor*/entry/src/main/cpp/CMakeLists.txt` is aligned with the
upstream Kuikly project — it uses
`find_package(render REQUIRED CONFIG)` +
`add_library(kuikly_render ALIAS render::kuikly)` to consume Kuikly's
headers and `libkuikly.so`. The same CMakeLists works in both modes:
whether `oh_modules/@kuikly-open/render/` is populated by the source
mirror or by an ohpm-installed HAR, ohpm emits a CMake Config at the
same location and `PACKAGE_FIND_FILE` is injected by hvigor.

**Source-mode workflow** (in-repo Kuikly co-debugging):

```bash
# In local.properties:
#   kuikly.useSource=true
#   kuikly.projectDir=/absolute/path/to/KuiklyUI
bash scripts/link-kuikly.sh
# ↑ rsyncs kuikly.projectDir/core-render-ohos into
#   <A2UI-root>/core-render-ohos (excluding build/ oh_modules/
#   .hvigor/ .cxx/ etc. generated dirs), symlinks .kuikly-external to
#   kuikly.projectDir, then rewrites the 5 oh-package.json5 files and
#   4 build-profile.json5 files to source-mode form. Idempotent —
#   `rsync -a --delete` resynchronises on every run; the source of
#   truth stays in kuikly.projectDir/core-render-ohos.
```

**Switching back to artifact mode from source mode**:

```bash
# Flip kuikly.useSource back to false in local.properties (or delete it)
bash scripts/link-kuikly.sh
# ↑ removes the .kuikly-external symlink and the core-render-ohos/
#   mirror directory, and rewrites the 5 oh-package.json5 / 4
#   build-profile.json5 files back to artifact form.
```

> Why a local `core-render-ohos/` mirror rather than a symlink? hvigor's
> `build-profile.json5` validator for `modules[].srcPath` rejects any
> path whose real target lies outside the workspace — this rules out
> both an absolute path and a symlink pointing at the external Kuikly
> checkout. So in source mode we must present hvigor with a real,
> in-workspace directory. The mirror is `.gitignore`d.

**Publishing the ohpm package `@kuikly-a2ui-open/render`** — this
repository ships no automated publish script (aligned with the upstream
Kuikly project). Before publishing, make sure the repository is in
**artifact mode** (i.e. `@kuikly-open/render` in
`a2ui-render-ohos/oh-package.json5` is in `"<kuikly.version>"` form
rather than `"file:../core-render-ohos"`); then run `ohpm pack` /
`ohpm publish` manually from the `a2ui-render-ohos/` directory.

## 6. A2UI artefact versioning strategy

This repository includes four types of artefacts and uses **two related but
different** version schemes:

| Artefact | Scheme | Example | Source |
|---|---|---|---|
| Android AAR / KMP klib on Maven (`com.tencent.kuikly-open:a2ui`, `a2ui-render-android`) | `${semantic}-${kotlin}[-ohos][-SNAPSHOT]` | `1.0.0-2.1.21`, `1.0.0-2.0.21-ohos` | `A2uiVersion.getArtifactVersion(project)` |
| iOS pods (`a2ui`, `OpenKuiklyA2UIIOSRender`) | `${semantic}` (plain SemVer, required by CocoaPods) | `1.0.0` | `A2uiVersion.getSemantic(project)` |
| HarmonyOS ohpm (`@kuikly-a2ui-open/render`) | `${semantic}` (plain SemVer, required by OHPM) | `1.0.0` | `A2uiVersion.getSemantic(project)` |
| README / display usage | `${semantic}` | `1.0.0` | `A2uiVersion.getSemantic()` |

The Kotlin suffix in Maven coordinates follows the Kuikly Core convention
(`com.tencent.kuikly-open:core:2.30.0-2.1.21-SNAPSHOT`) — KMP `.klib` and
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
`-ohos` marker in the final coordinate (`1.0.0-2.0.21-ohos`).

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
