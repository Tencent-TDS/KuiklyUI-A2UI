/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * KuiklyUI-A2UI standalone project — version constants.
 *
 * This is the single source of truth for the version of every artifact
 * shipped by *this* repository (a2ui KMP core, a2ui-render-android,
 * a2ui-render-ohos, OpenA2UIIOSRender pod).
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Two version flavours
 * ─────────────────────────────────────────────────────────────────────────
 * A2UI artifacts fall into two categories with *different* version schemes:
 *
 *   1. Android / KMP Maven artifacts  →  [getArtifactVersion]
 *      Format: `${semantic}-${kotlin}[-<ohosSuffix>][-SNAPSHOT]`
 *      Examples:
 *        - `0.9.0-2.1.21-SNAPSHOT`         (Kotlin 2.1.21, snapshot)
 *        - `0.9.0-2.0.21-SNAPSHOT`         (Kotlin 2.0.21, snapshot)
 *        - `0.9.0-2.0.21-ohos-SNAPSHOT`    (HarmonyOS Kotlin 2.0.21-KBA-010)
 *        - `0.9.0-2.1.21`                  (release)
 *
 *      Why the Kotlin suffix? KMP `.klib` / metadata binaries are NOT
 *      compatible across Kotlin compiler versions, so every supported
 *      Kotlin toolchain must publish its own Maven coordinate — the
 *      exact same convention Kuikly Core uses (`2.0.0-2.1.21-SNAPSHOT`,
 *      etc.). Without this, the two build files
 *      (`build.2.1.21.gradle.kts` vs `build.2.0.ohos.gradle.kts`) would
 *      overwrite each other in the local/remote Maven repo.
 *
 *   2. iOS pod / HarmonyOS ohpm / display  →  [getSemantic]
 *      Format: `${semantic}` (e.g. `0.9.0-SNAPSHOT`, `0.9.0`).
 *      CocoaPods and OHPM both enforce strict SemVer, so we deliberately
 *      keep those artifacts on the plain semantic version. Each toolchain
 *      ships a single pod / ohpm package whose contents are built by
 *      whichever Kotlin version the release pipeline chooses.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Override precedence (both APIs, for the semantic component)
 * ─────────────────────────────────────────────────────────────────────────
 *   1. Environment var   `A2UI_VERSION=<x.y.z>`
 *   2. Default          [DEFAULT_A2UI_SEMANTIC] below.
 *
 * This mirrors Kuikly Core's `Version.getKuiklyVersion()` which likewise
 * reads a single `KUIKLY_VERSION` env var (no Gradle property fallback).
 *
 * The Kotlin component consumed by [getArtifactVersion] is *always* read
 * from `Version.getKotlinVersion()` (which honours `KUIKLY_KOTLIN_VERSION`),
 * so switching Kotlin toolchains only requires flipping that one env var —
 * artifact coordinates then follow automatically.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Consumers
 * ─────────────────────────────────────────────────────────────────────────
 *   getArtifactVersion(project):
 *     - a2ui/build.2.1.21.gradle.kts             `version = ...`
 *     - a2ui/build.2.0.ohos.gradle.kts           `version = ...`
 *     - demo/build.2.1.21.gradle.kts             `version = ...`
 *     - demo/build.2.0.ohos.gradle.kts           `version = ...`
 *     - a2ui-render-android/build.2.1.21.gradle.kts (top-level + publication)
 *
 *   getSemantic(project):
 *     - a2ui/build.2.1.21.gradle.kts       cocoapods { version = ... }
 *     - demo/build.2.1.21.gradle.kts       cocoapods { version = ... }
 *     - a2ui-render-ios/OpenA2UIIOSRender.podspec   (edited by hand when
 *                                                    publishing the iOS pod;
 *                                                    `A2UI_VERSION` env is
 *                                                    also honoured at parse
 *                                                    time for CI parity)
 *     - a2ui-render-ohos/oh-package.json5           (edited by hand when
 *                                                    publishing the ohpm pkg)
 */
object A2uiVersion {

    /** Semantic version of this repository's user-visible release line. */
    private const val DEFAULT_A2UI_SEMANTIC = "0.9.0-SNAPSHOT"

    private const val SNAPSHOT_SUFFIX = "-SNAPSHOT"

    /**
     * Kotlin version identifier that signals a HarmonyOS-only Kuikly build
     * (mirrors `Version.OHOS_KOTLIN_SUFFIX`). When present in the resolved
     * Kotlin version, we replace it with a shorter, human-friendly `ohos`
     * marker in the final artifact coordinate — same convention as
     * `Version.getCoreVersion()` in Kuikly Core.
     */
    private const val OHOS_KOTLIN_MARKER = "KBA-010"
    private const val OHOS_ARTIFACT_SUFFIX = "ohos"

    private const val KEY_A2UI_VERSION_ENV = "A2UI_VERSION"

    /**
     * Resolve the plain **semantic** A2UI version — used by iOS pods,
     * HarmonyOS ohpm packages, and any user-facing display (README,
     * publish logs).
     *
     * Resolution order (mirrors Kuikly Core's `Version.getKuiklyVersion()`):
     *   1. `A2UI_VERSION` environment variable
     *   2. [DEFAULT_A2UI_SEMANTIC] fallback
     *
     * @param project optional Gradle [org.gradle.api.Project] — accepted for
     *                API symmetry with [getArtifactVersion] but currently
     *                unused (no Gradle-property override, by design).
     */
    @Suppress("UNUSED_PARAMETER")
    fun getSemantic(project: org.gradle.api.Project? = null): String {
        System.getenv(KEY_A2UI_VERSION_ENV)?.takeIf { it.isNotBlank() }?.let { return it }
        return DEFAULT_A2UI_SEMANTIC
    }

    /**
     * Resolve the **Maven artifact** version for Android / KMP publications.
     * Composes [getSemantic] with the Kotlin version resolved by
     * `Version.getKotlinVersion()`.
     *
     * Layout rules — kept identical to Kuikly Core's `getCoreVersion()`
     * so that upstream and downstream artifact coordinates stay visually
     * aligned:
     *
     *   - Snapshot semantic (`0.9.0-SNAPSHOT`) → insert `-${kotlin}` **before**
     *     the `-SNAPSHOT` suffix   →  `0.9.0-2.1.21-SNAPSHOT`
     *   - Release semantic (`0.9.0`)          → append `-${kotlin}`
     *                                          →  `0.9.0-2.1.21`
     *   - If kotlin version contains `KBA-010`, that marker is replaced
     *     with `ohos`                          →  `0.9.0-2.0.21-ohos-SNAPSHOT`
     */
    fun getArtifactVersion(project: org.gradle.api.Project? = null): String {
        val semantic = getSemantic(project)
        val kotlin = Version.getKotlinVersion()
        val isSnapshot = semantic.endsWith(SNAPSHOT_SUFFIX)
        val artifact = if (isSnapshot) {
            // Insert `-${kotlin}` right before the `-SNAPSHOT` tail so the
            // resulting coordinate keeps SNAPSHOT ordering semantics.
            StringBuilder(semantic)
                .insert(semantic.indexOf(SNAPSHOT_SUFFIX), "-$kotlin")
                .toString()
        } else {
            "$semantic-$kotlin"
        }
        return if (artifact.contains(OHOS_KOTLIN_MARKER)) {
            artifact.replace(OHOS_KOTLIN_MARKER, OHOS_ARTIFACT_SUFFIX)
        } else {
            artifact
        }
    }
}
