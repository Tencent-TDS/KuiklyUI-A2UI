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
 * A2UI dependency resolver — dual-mode support for this repository's own
 * publishable Android/KMP artefacts (`:a2ui` and `:a2ui-render-android`).
 *
 * Mirrors [KuiklyDep] on the Kuikly side with two deliberate inversions:
 *   1. The default mode is `source` (not `artifact`): A2UI lives in this
 *      very repository, so running `./gradlew :androidAppForKuiklyPage:...`
 *      against local source is by far the most common dev loop.
 *   2. The resolved coordinate version is read from the local
 *      [A2uiVersion.getArtifactVersion] (`${semantic}-${kotlin}[-ohos][-SNAPSHOT]`),
 *      not from a `kuikly.version` style property — so switching A2UI
 *      versions only requires `A2UI_VERSION=…` and / or
 *      `KUIKLY_KOTLIN_VERSION=…`, matching the vars the publish scripts already
 *      honour.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Mode selection — `a2ui.useSource`
 * ─────────────────────────────────────────────────────────────────────────
 * Resolution order (first non-blank wins):
 *   1. `-Pa2ui.useSource=true|false` on the Gradle command line
 *   2. `A2UI_USE_SOURCE` environment variable
 *   3. `<root>/local.properties`  → key `a2ui.useSource`
 *   4. `<root>/gradle.properties` → key `a2ui.useSource`
 *   5. Default: `true` (source mode — same-repo dev loop)
 *
 *  - `true`  → consume `:a2ui` / `:a2ui-render-android` as source-linked
 *              Gradle sub-projects.
 *  - `false` → consume them as published Maven artefacts under
 *              `com.tencent.kuikly-a2ui-open:<module>:<coord-version>`.
 *
 * `a2ui.useSource` is orthogonal to `kuikly.useSource` — any of the four
 * combinations is valid.
 *
 * ─────────────────────────────────────────────────────────────────────────
 *  Why this exists
 * ─────────────────────────────────────────────────────────────────────────
 * Each consumer (demo `build.gradle.kts`, `:demo` KMP module) picks the
 * right form via [a2ui] / [a2uiRenderAndroid], which return either
 * `project(":xxx")` (source mode) or a Maven coordinate string
 * (artifact mode). This keeps every call-site a one-liner, with no inline
 * `if (useSource)` branches.
 */
object A2uiDep {

    private const val KEY_USE_SOURCE = "a2ui.useSource"
    private const val ENV_USE_SOURCE = "A2UI_USE_SOURCE"

    // ── Mode ────────────────────────────────────────────────────────────

    /**
     * Resolve whether to consume A2UI modules as local source. Default is
     * `true` (source mode) — see the class-level doc for rationale.
     */
    fun useSource(project: Project): Boolean {
        val raw = readProperty(project, KEY_USE_SOURCE, ENV_USE_SOURCE) ?: return true
        return raw.trim().equals("true", ignoreCase = true)
    }

    // ── Version ─────────────────────────────────────────────────────────

    /**
     * Maven coordinate version for A2UI's own publications — composed by
     * [A2uiVersion.getArtifactVersion] from `A2UI_VERSION` (semantic) and
     * `KUIKLY_KOTLIN_VERSION` (Kotlin toolchain). Example outputs:
     *   - `1.0.0-2.1.21`
     *   - `1.0.0-2.0.21-ohos`
     *   - `1.0.0-2.1.21-SNAPSHOT`
     */
    fun a2uiCoordinateVersion(project: Project): String =
        A2uiVersion.getArtifactVersion(project)

    // ── Dependency notation helpers ─────────────────────────────────────
    //
    // Each returns `Any` so that Gradle's `DependencyHandler.add(...)` /
    // `api(...)` / `implementation(...)` dispatch to the right overload:
    //   - ProjectDependency (source mode)
    //   - String coordinate (artifact mode)

    fun a2ui(project: Project): Any = resolve(project, "a2ui")
    fun a2uiRenderAndroid(project: Project): Any = resolve(project, "a2ui-render-android")

    private fun resolve(project: Project, moduleName: String): Any {
        return if (useSource(project)) {
            project.project(":$moduleName")
        } else {
            "${MavenConfig.A2UI_GROUP}:$moduleName:${a2uiCoordinateVersion(project)}"
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
