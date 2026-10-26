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

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * KuiklyUI dependency resolver — dual-mode support (source vs. published artifact).
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Mode selection — `kuikly.useSource`
 * ─────────────────────────────────────────────────────────────────────────
 * Resolution order (first non-blank wins):
 *   1. `-Pkuikly.useSource=true|false` on the Gradle command line
 *   2. `KUIKLY_USE_SOURCE` environment variable
 *   3. `<root>/local.properties`  → key `kuikly.useSource`
 *   4. `<root>/gradle.properties` → key `kuikly.useSource`
 *   5. Default: `false` (artifact mode)
 *
 *  - `true`  → consume `:core` / `:core-annotations` / `:core-ksp` /
 *              `:core-render-android` / `:compose` as source-linked
 *              Gradle sub-projects (requires `kuikly.projectDir`).
 *  - `false` → consume them as published Maven artifacts under
 *              `com.tencent.kuikly-open:<module>:<coord-version>`.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Kuikly artifact version — `kuikly.version`
 * ─────────────────────────────────────────────────────────────────────────
 * Resolution order:
 *   1. `-Pkuikly.version=…`
 *   2. `KUIKLY_VERSION` environment variable (same name used by Kuikly
 *      Core, so a single env var covers both repos when they are built
 *      together)
 *   3. `<root>/local.properties`  → key `kuikly.version`
 *   4. `<root>/gradle.properties` → key `kuikly.version`
 *   5. [DEFAULT_KUIKLY_VERSION] fallback (`2.30.0`)
 *
 * The resolved value is used *verbatim* — the caller must spell out
 * whether a `-SNAPSHOT` tail is wanted. Pick the value that matches the
 * coordinate Kuikly actually published:
 *
 *   - released artifact → `kuikly.version=2.30.0`
 *   - snapshot artifact → `kuikly.version=2.30.0-SNAPSHOT`
 *
 * [kuiklyCoordinateVersion] then composes the final Maven coordinate
 * version by inserting the Kotlin toolchain segment *before* any
 * trailing `-SNAPSHOT`, following the exact same convention Kuikly Core
 * publishes with:
 *
 *   `2.30.0`           × Kotlin `2.1.21`         → `2.30.0-2.1.21`
 *   `2.30.0-SNAPSHOT`  × Kotlin `2.1.21`         → `2.30.0-2.1.21-SNAPSHOT`
 *   `2.30.0-SNAPSHOT`  × Kotlin `2.0.21-KBA-010` → `2.30.0-2.0.21-ohos-SNAPSHOT`
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Why this exists
 * ─────────────────────────────────────────────────────────────────────────
 * Each consumer (settings script, module `build.*.gradle.kts`) picks the
 * right form via [core] / [coreAnnotations] / [coreKsp] /
 * [coreRenderAndroid] / [compose], which return either `project(":xxx")`
 * (source mode) or a Maven coordinate string (artifact mode). This keeps
 * every call-site a one-liner, with no inline `if (useSource)` branches.
 */
object KuiklyDep {

    private const val KEY_USE_SOURCE = "kuikly.useSource"
    private const val ENV_USE_SOURCE = "KUIKLY_USE_SOURCE"

    private const val KEY_KUIKLY_VERSION = "kuikly.version"
    private const val ENV_KUIKLY_VERSION = "KUIKLY_VERSION"
    private const val DEFAULT_KUIKLY_VERSION = "2.30.0"

    private const val SNAPSHOT_SUFFIX = "-SNAPSHOT"
    private const val OHOS_KOTLIN_MARKER = "KBA-010"
    private const val OHOS_ARTIFACT_SUFFIX = "ohos"

    private const val KUIKLY_GROUP = "com.tencent.kuikly-open"

    // ── Mode ────────────────────────────────────────────────────────────

    /** Resolve whether to consume KuiklyUI modules as local source. */
    fun useSource(project: Project): Boolean {
        val raw = readProperty(project, KEY_USE_SOURCE, ENV_USE_SOURCE) ?: return false
        return raw.trim().equals("true", ignoreCase = true)
    }

    // ── Version ─────────────────────────────────────────────────────────

    /**
     * Base version of the upstream Kuikly artifact — used verbatim from
     * `kuikly.version` / `KUIKLY_VERSION`, so the caller controls whether
     * a `-SNAPSHOT` tail is present. Falls back to
     * [DEFAULT_KUIKLY_VERSION] when nothing is configured.
     */
    fun kuiklyBaseVersion(project: Project): String {
        val raw = readProperty(project, KEY_KUIKLY_VERSION, ENV_KUIKLY_VERSION)
            ?: DEFAULT_KUIKLY_VERSION
        return raw.trim()
    }

    /**
     * Maven coordinate version for Kuikly Core / Compose / KSP, composed
     * from [kuiklyBaseVersion] and [Version.getKotlinVersion].
     * Formula is identical to the convention Kuikly Core uses when
     * publishing its own core/compose/ksp artifacts.
     */
    fun kuiklyCoordinateVersion(project: Project): String {
        val base = kuiklyBaseVersion(project)
        val kotlin = Version.getKotlinVersion()
        val isSnapshot = base.endsWith(SNAPSHOT_SUFFIX)
        val composed = if (isSnapshot) {
            StringBuilder(base)
                .insert(base.indexOf(SNAPSHOT_SUFFIX), "-$kotlin")
                .toString()
        } else {
            "$base-$kotlin"
        }
        return if (composed.contains(OHOS_KOTLIN_MARKER)) {
            composed.replace(OHOS_KOTLIN_MARKER, OHOS_ARTIFACT_SUFFIX)
        } else {
            composed
        }
    }

    // ── Dependency notation helpers ─────────────────────────────────────
    //
    // Each returns `Any` so that Gradle's `DependencyHandler.add(...)` /
    // `api(...)` / `implementation(...)` dispatch to the right overload:
    //   - ProjectDependency (source mode)
    //   - String coordinate (artifact mode)

    fun core(project: Project): Any = resolve(project, "core")
    fun coreAnnotations(project: Project): Any = resolve(project, "core-annotations")
    fun coreKsp(project: Project): Any = resolve(project, "core-ksp")
    fun coreRenderAndroid(project: Project): Any = resolve(project, "core-render-android")
    fun compose(project: Project): Any = resolve(project, "compose")

    private fun resolve(project: Project, moduleName: String): Any {
        return if (useSource(project)) {
            project.project(":$moduleName")
        } else {
            "$KUIKLY_GROUP:$moduleName:${kuiklyCoordinateVersion(project)}"
        }
    }

    // ── Internal: unified property/env/local/gradle lookup ──────────────

    private fun readProperty(project: Project, propKey: String, envKey: String): String? {
        (project.findProperty(propKey) as? String)?.takeIf { it.isNotBlank() }?.let { return it }
        System.getenv(envKey)?.takeIf { it.isNotBlank() }?.let { return it }
        readFromPropertiesFile(File(project.rootDir, "local.properties"), propKey)
            ?.let { return it }
        readFromPropertiesFile(File(project.rootDir, "gradle.properties"), propKey)
            ?.let { return it }
        return null
    }

    private fun readFromPropertiesFile(file: File, key: String): String? {
        if (!file.exists()) return null
        val props = Properties().apply {
            FileInputStream(file).use { load(it) }
        }
        return props.getProperty(key)?.takeIf { it.isNotBlank() }
    }
}
