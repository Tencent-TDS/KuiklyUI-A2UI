pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/gradle-plugins/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// A2UI Standalone Project — Gradle Settings for the OHOS build path
// (Kotlin 2.0.21-KBA-010 + Gradle 8.0). Selected via:
//   ./gradlew -c settings.2.0.ohos.gradle.kts <task>
//
// Only modules that actually target `ohosArm64` are included here.
// ─────────────────────────────────────────────────────────────────────────

val buildFileName = "build.2.0.ohos.gradle.kts"
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
include(":a2ui")
project(":a2ui").projectDir = file("a2ui")
project(":a2ui").buildFileName = buildFileName

include(":demo")
project(":demo").projectDir = file("demo")
project(":demo").buildFileName = buildFileName
