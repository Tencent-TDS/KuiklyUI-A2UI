pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/repository/maven-tencent/")
        }
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/gradle-plugins/")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// A2UI Standalone Project — Gradle Settings (default: Kotlin 2.1.21).
//
// This is the DEFAULT settings file used by `./gradlew` when no `-c` flag
// is passed. It sources both the A2UI-owned modules (under this repo root)
// AND the Kuikly modules from the external `kuikly.projectDir` checkout,
// so `./gradlew :androidAppForKuiklyPage:installDebug` etc. work
// end-to-end without any published artifact.
//
// Module path convention (no `:a2ui:` prefix — top-level modules only):
//
//   Physical dir (this repo)         Logical Gradle path
//   ─────────────────────────       ────────────────────────
//   a2ui/                            :a2ui
//   a2ui-render-android/             :a2ui-render-android
//   demo/                            :demo
//   androidAppForKuiklyPage/         :androidAppForKuiklyPage
//   androidAppForNativePage/         :androidAppForNativePage
//   androidAppForBothPages/          :androidAppForBothPages
//   androidAppForTest/               :androidAppForTest
//
// External Kuikly modules are included by pointing `projectDir` at the
// corresponding sub-directory of the resolved `kuikly.projectDir`
// (see `gradle/kuikly-path.settings.gradle.kts`).
// ─────────────────────────────────────────────────────────────────────────

val buildFileName = "build.2.1.21.gradle.kts"
rootProject.buildFileName = buildFileName

// ── Resolve `kuikly.useSource` (settings scripts run *before* buildSrc, so
// we can't `import KuiklyDep` here — keep the lookup inline). Resolution
// order mirrors KuiklyDep.useSource: -P → env → local.properties →
// gradle.properties → default `false`. ──────────────────────────────────
val useKuiklySource: Boolean = run {
    val KEY = "kuikly.useSource"
    val ENV_KEY = "KUIKLY_USE_SOURCE"
    fun readFromProps(file: java.io.File): String? {
        if (!file.exists()) return null
        val props = java.util.Properties().apply {
            java.io.FileInputStream(file).use { load(it) }
        }
        return props.getProperty(KEY)?.takeIf { it.isNotBlank() }
    }
    val raw: String? =
        (settings.startParameter.projectProperties[KEY])?.takeIf { it.isNotBlank() }
            ?: System.getenv(ENV_KEY)?.takeIf { it.isNotBlank() }
            ?: readFromProps(java.io.File(settings.rootDir, "local.properties"))
            ?: readFromProps(java.io.File(settings.rootDir, "gradle.properties"))
    raw?.trim().equals("true", ignoreCase = true)
}

println("[A2UI] kuikly.useSource → $useKuiklySource")

// ── Resolve `a2ui.useSource` — DEFAULT TRUE (source mode). A2UI lives
// in this very repo so the same-repo dev loop is the common case; set to
// `false` to validate that demos compile against the published
// `com.tencent.kuikly-a2ui-open:*` artefacts only. ──────────────────────
val useA2uiSource: Boolean = run {
    val KEY = "a2ui.useSource"
    val ENV_KEY = "A2UI_USE_SOURCE"
    fun readFromProps(file: java.io.File): String? {
        if (!file.exists()) return null
        val props = java.util.Properties().apply {
            java.io.FileInputStream(file).use { load(it) }
        }
        return props.getProperty(KEY)?.takeIf { it.isNotBlank() }
    }
    val raw: String? =
        (settings.startParameter.projectProperties[KEY])?.takeIf { it.isNotBlank() }
            ?: System.getenv(ENV_KEY)?.takeIf { it.isNotBlank() }
            ?: readFromProps(java.io.File(settings.rootDir, "local.properties"))
            ?: readFromProps(java.io.File(settings.rootDir, "gradle.properties"))
    // Default TRUE when nothing is set, inverse of `kuikly.useSource`.
    (raw?.trim() ?: "true").equals("true", ignoreCase = true)
}

println("[A2UI] a2ui.useSource → $useA2uiSource")

if (useKuiklySource) {
    // Resolve external KuiklyUI checkout. Reads `kuikly.projectDir` from
    // -P / env / local.properties / gradle.properties (first non-blank wins).
    // Populates `settings.extra["kuiklyProjectDir"]` as a canonical java.io.File.
    apply(from = "gradle/kuikly-path.settings.gradle.kts")
    val kuiklyDir: java.io.File = settings.extra["kuiklyProjectDir"] as java.io.File

    // ── External Kuikly modules (source-linked, from `kuikly.projectDir`) ────
    include(":core-annotations")
    project(":core-annotations").projectDir = java.io.File(kuiklyDir, "core-annotations")
    project(":core-annotations").buildFileName = buildFileName

    include(":core-ksp")
    project(":core-ksp").projectDir = java.io.File(kuiklyDir, "core-ksp")
    project(":core-ksp").buildFileName = buildFileName

    include(":core")
    project(":core").projectDir = java.io.File(kuiklyDir, "core")
    project(":core").buildFileName = buildFileName

    include(":core-render-android")
    project(":core-render-android").projectDir = java.io.File(kuiklyDir, "core-render-android")
    project(":core-render-android").buildFileName = buildFileName

    include(":compose")
    project(":compose").projectDir = java.io.File(kuiklyDir, "compose")
    project(":compose").buildFileName = buildFileName
}

// ── A2UI-owned modules (this repo, top-level paths) ──────────────────────
// `:a2ui` + `:a2ui-render-android` are gated by `a2ui.useSource`: in
// artifact mode we DO NOT include them, so Gradle doesn't try to compile
// them from source — demos pull the published Maven coordinates instead
// (resolved via `A2uiDep`). `:demo` is NOT publishable (business sample)
// so it stays unconditionally source-linked, and its own `A2uiDep.a2ui`
// call-site then decides whether to transitively hit the local project
// or the Maven artefact.
if (useA2uiSource) {
    include(":a2ui")
    project(":a2ui").projectDir = file("a2ui")
    project(":a2ui").buildFileName = buildFileName

    include(":a2ui-render-android")
    project(":a2ui-render-android").projectDir = file("a2ui-render-android")
    project(":a2ui-render-android").buildFileName = buildFileName
}

include(":demo")
project(":demo").projectDir = file("demo")
project(":demo").buildFileName = buildFileName

include(":androidAppForKuiklyPage")
project(":androidAppForKuiklyPage").projectDir = file("androidAppForKuiklyPage")

include(":androidAppForNativePage")
project(":androidAppForNativePage").projectDir = file("androidAppForNativePage")

include(":androidAppForBothPages")
project(":androidAppForBothPages").projectDir = file("androidAppForBothPages")

include(":androidAppForTest")
project(":androidAppForTest").projectDir = file("androidAppForTest")
