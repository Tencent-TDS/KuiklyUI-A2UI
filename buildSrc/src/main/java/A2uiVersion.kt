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
 * a2ui-render-ohos, OpenKuiklyA2UIIOSRender pod).
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Two version flavours
 * ─────────────────────────────────────────────────────────────────────────
 * A2UI artifacts fall into two categories with *different* version schemes:
 *
 *   1. Android / KMP Maven artifacts  →  [getArtifactVersion]
 *      Format: `${semantic}-${kotlin}[-<ohosSuffix>][-SNAPSHOT]`
 *      Examples:
 *        - `1.0.0-2.1.21`                  (Kotlin 2.1.21, release)
 *        - `1.0.0-2.0.21`                  (Kotlin 2.0.21, release)
 *        - `1.0.0-2.0.21-ohos`             (HarmonyOS Kotlin 2.0.21-KBA-010)
 *        - `1.0.0-2.1.21-SNAPSHOT`         (snapshot, when semantic ends with -SNAPSHOT)
 *
 *      Why the Kotlin suffix? KMP `.klib` / metadata binaries are NOT
 *      compatible across Kotlin compiler versions, so every supported
 *      Kotlin toolchain must publish its own Maven coordinate — the
 *      exact same convention Kuikly Core uses (see
 *      `KuiklyDep.kuiklyCoordinateVersion`, e.g. `2.29.2-2.1.21-SNAPSHOT`).
 *      Without this, the two build files
 *      (`build.2.1.21.gradle.kts` vs `build.2.0.ohos.gradle.kts`) would
 *      overwrite each other in the local/remote Maven repo.
 *
 *   2. iOS pod / HarmonyOS ohpm / display  →  [getSemantic]
 *      Format: `${semantic}` (e.g. `1.0.0`, `1.0.0-SNAPSHOT`).
 *      CocoaPods and OHPM both enforce strict SemVer, so we deliberately
 *      keep those artifacts on the plain semantic version. Each toolchain
 *      ships a single pod / ohpm package whose contents are built by
 *      whichever Kotlin version the release pipeline chooses.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Override precedence (both APIs, for the semantic component)
 * ─────────────────────────────────────────────────────────────────────────
 *   1. Gradle property   `-Pa2ui.version=<x.y.z>`
 *   2. Environment var   `A2UI_VERSION=<x.y.z>`
 *   3. `<root>/local.properties`   key `a2ui.version`
 *   4. `<root>/gradle.properties`  key `a2ui.version`
 *   5. Default           [DEFAULT_A2UI_SEMANTIC] below.
 *
 * This mirrors how `KuiklyDep.kuiklyBaseVersion` resolves the upstream
 * Kuikly coordinate and matches [A2uiDep.useSource]'s 5-tier resolution,
 * so IDE sync (which cannot inherit the shell's env vars) and CI (which
 * typically exports `A2UI_VERSION`) stay in lock-step.
 *
 * The Kotlin component consumed by [getArtifactVersion] is *always* read
 * from [Version.getKotlinVersion] (which honours `KUIKLY_KOTLIN_VERSION`),
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
 *     - OpenKuiklyA2UIIOSRender.podspec              (edited by hand when
 *                                                    publishing the iOS pod;
 *                                                    `A2UI_VERSION` env is
 *                                                    also honoured at parse
 *                                                    time for CI parity)
 *     - a2ui-render-ohos/oh-package.json5           (edited by hand when
 *                                                    publishing the ohpm pkg)
 */
object A2uiVersion {

    /** Semantic version of this repository's user-visible release line. */
    private const val DEFAULT_A2UI_SEMANTIC = "1.0.0"

    private const val SNAPSHOT_SUFFIX = "-SNAPSHOT"

    /**
     * Kotlin version identifier that signals a HarmonyOS-only Kuikly build.
     * When present in the resolved Kotlin version, we replace it with a
     * shorter, human-friendly `ohos` marker in the final artifact
     * coordinate — same convention used by
     * [KuiklyDep.kuiklyCoordinateVersion].
     */
    private const val OHOS_KOTLIN_MARKER = "KBA-010"
    private const val OHOS_ARTIFACT_SUFFIX = "ohos"

    private const val KEY_A2UI_VERSION_PROP = "a2ui.version"
    private const val KEY_A2UI_VERSION_ENV = "A2UI_VERSION"

    /**
     * Resolve the plain **semantic** A2UI version — used by iOS pods,
     * HarmonyOS ohpm packages, and any user-facing display (README,
     * publish logs).
     *
     * Resolution order (mirrors [A2uiDep.useSource] for symmetry):
     *   1. `-Pa2ui.version=…` Gradle property
     *   2. `A2UI_VERSION` environment variable
     *   3. `<root>/local.properties`   key `a2ui.version`
     *   4. `<root>/gradle.properties`  key `a2ui.version`
     *   5. [DEFAULT_A2UI_SEMANTIC] fallback
     *
     * @param project optional Gradle [org.gradle.api.Project] — when `null`
     *                only the env-var and default tiers are consulted
     *                (used by rare call-sites that lack a Project handle,
     *                e.g. settings-time code).
     */
    fun getSemantic(project: org.gradle.api.Project? = null): String {
        // 1) -Pa2ui.version=…
        if (project != null) {
            (project.findProperty(KEY_A2UI_VERSION_PROP) as? String)
                ?.takeIf { it.isNotBlank() }
                ?.let { return it }
        }
        // 2) A2UI_VERSION env var
        System.getenv(KEY_A2UI_VERSION_ENV)?.takeIf { it.isNotBlank() }?.let { return it }
        // 3) <root>/local.properties, then 4) <root>/gradle.properties
        if (project != null) {
            readFromPropertiesFile(java.io.File(project.rootDir, "local.properties"), KEY_A2UI_VERSION_PROP)
                ?.let { return it }
            readFromPropertiesFile(java.io.File(project.rootDir, "gradle.properties"), KEY_A2UI_VERSION_PROP)
                ?.let { return it }
        }
        // 5) fallback
        return DEFAULT_A2UI_SEMANTIC
    }

    /**
     * Resolve the **Maven artifact** version for Android / KMP publications.
     * Composes [getSemantic] with the Kotlin version resolved by
     * [Version.getKotlinVersion].
     *
     * Layout rules — kept identical to [KuiklyDep.kuiklyCoordinateVersion]
     * so that upstream and downstream artifact coordinates stay visually
     * aligned:
     *
     *   - Release semantic (`1.0.0`)          → append `-${kotlin}`
     *                                          →  `1.0.0-2.1.21`
     *   - Snapshot semantic (`1.0.0-SNAPSHOT`) → insert `-${kotlin}` **before**
     *     the `-SNAPSHOT` suffix   →  `1.0.0-2.1.21-SNAPSHOT`
     *   - If kotlin version contains `KBA-010`, that marker is replaced
     *     with `ohos`                          →  `1.0.0-2.0.21-ohos`
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

    // ── Internal: properties-file lookup ────────────────────────────────

    /**
     * Read [key] from a `.properties` file on disk, returning `null` if the
     * file does not exist or the key is absent / blank.
     *
     * Kept inline (instead of extracted to a shared helper) to avoid a
     * cross-cutting dependency on any other `buildSrc` util — this file is
     * loaded very early during Gradle's settings phase.
     */
    private fun readFromPropertiesFile(file: java.io.File, key: String): String? {
        if (!file.exists()) return null
        val props = java.util.Properties().apply {
            java.io.FileInputStream(file).use { load(it) }
        }
        return props.getProperty(key)?.takeIf { it.isNotBlank() }
    }
}
