/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 */

// ─────────────────────────────────────────────────────────────────────────
// kuikly-path.settings.gradle.kts — resolve `kuikly.projectDir` for
// settings scripts.
//
// **Contract**: after `apply(from = …)`, the following `extra`
// properties are set on `Settings`:
//
//   * `kuiklyProjectDir` — `java.io.File`, canonical path to the
//     external Kuikly checkout that contains `core/`,
//     `core-render-android/`, `compose/`, etc.
//
// **Resolution order** (first non-blank wins):
//   1. `-Pkuikly.projectDir=…` on the Gradle command line
//   2. `KUIKLY_PROJECT_DIR` environment variable
//   3. `<root>/local.properties`   → key `kuikly.projectDir`
//   4. `<root>/gradle.properties`  → key `kuikly.projectDir` (default)
// ─────────────────────────────────────────────────────────────────────────

val KEY = "kuikly.projectDir"
val ENV_KEY = "KUIKLY_PROJECT_DIR"

fun readProperty(file: java.io.File, key: String): String? {
    if (!file.exists()) return null
    val props = java.util.Properties().apply {
        java.io.FileInputStream(file).use { load(it) }
    }
    return props.getProperty(key)?.takeIf { it.isNotBlank() }
}

val fromCmdOrEnv: String? =
    (settings.startParameter.projectProperties[KEY])?.takeIf { it.isNotBlank() }
        ?: System.getenv(ENV_KEY)?.takeIf { it.isNotBlank() }

val raw: String = fromCmdOrEnv
    ?: readProperty(java.io.File(settings.rootDir, "local.properties"), KEY)
    ?: readProperty(java.io.File(settings.rootDir, "gradle.properties"), KEY)
    ?: error(
        "A2UI standalone project requires the external KuiklyUI checkout " +
            "location to be configured via '$KEY' (in local.properties or " +
            "gradle.properties) or the '$ENV_KEY' environment variable."
    )

val resolvedKuiklyDir: java.io.File = java.io.File(raw)
    .let { if (it.isAbsolute) it else java.io.File(settings.rootDir, raw) }
    .canonicalFile

require(resolvedKuiklyDir.isDirectory && java.io.File(resolvedKuiklyDir, "core").isDirectory) {
    "Configured '$KEY' does not point to a valid KuiklyUI checkout: " +
        "$resolvedKuiklyDir (expected sub-directory 'core/' not found)."
}

settings.extra["kuiklyProjectDir"] = resolvedKuiklyDir

println("[A2UI] kuikly.projectDir → $resolvedKuiklyDir")

// ─────────────────────────────────────────────────────────────────────────
// Anti-pollution guard for source-linked Kuikly sub-projects.
//
// A2UI standalone `settings.gradle.kts` includes several Kuikly modules
// (`:core`, `:core-annotations`, `:compose`, …) by pointing `projectDir`
// at the external KuiklyUI checkout. Those upstream `build.*.gradle.kts`
// scripts apply the `org.jetbrains.kotlin.native.cocoapods` plugin, which
// registers `podspec` / `syncFramework…` / `podInstall…` tasks. When any
// of those tasks run in *this* rootProject context, the plugin rewrites
// the upstream `.podspec` files with a `$REPO_ROOT/../../KuiklyUI-A2UI…`
// path — silently corrupting the KuiklyUI checkout for its own iOS
// tooling.
//
// The Kuikly CocoaPods pipeline is **not** used by anything inside A2UI
// (A2UI ships its own `:a2ui` / `:demo` podspecs locally), so it is safe
// to disable those tasks whenever the owning project physically lives
// under the resolved Kuikly checkout directory.
//
// Guard is a no-op for A2UI-owned modules (their `projectDir` is under
// `settings.rootDir`, not under `resolvedKuiklyDir`).
// ─────────────────────────────────────────────────────────────────────────
gradle.beforeProject {
    val underKuikly = projectDir.canonicalFile.startsWith(resolvedKuiklyDir)
    if (!underKuikly) return@beforeProject
    afterEvaluate {
        val disabled = tasks.matching { t ->
            val n = t.name
            n == "podspec" ||
                n == "podspecCleanUp" ||
                n == "generateDummyFramework" ||
                n.startsWith("podInstall") ||
                n.startsWith("podPublish") ||
                n.startsWith("podImport") ||
                n.startsWith("podSetupBuild") ||
                n.startsWith("podBuild") ||
                n.startsWith("syncFramework") ||
                n.startsWith("syncPodFramework")
        }
        disabled.configureEach {
            enabled = false
            // Fail-fast if someone still tries to run it explicitly.
            doFirst {
                throw GradleException(
                    "[A2UI] CocoaPods task '${this@configureEach.path}' is disabled " +
                        "because it belongs to a source-linked Kuikly module " +
                        "($projectDir). Run Kuikly-side pod tasks from the KuiklyUI " +
                        "checkout root, not from the A2UI standalone project."
                )
            }
        }
    }
}
