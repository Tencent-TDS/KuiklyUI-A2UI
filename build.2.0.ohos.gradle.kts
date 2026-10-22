plugins {
    kotlin("multiplatform") version "2.0.21-KBA-010" apply false
    kotlin("plugin.compose") version "2.0.21-KBA-010" apply false
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4") apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/repository/maven-tencent/")
        }
    }
}

allprojects {
    // Project-level repositories mirror `build.2.1.21.gradle.kts`. On
    // some CI runners the `settings.*.gradle.kts` repository list is
    // transparently rewritten by an injected `init.gradle` to internal
    // proxies that do NOT cover `com.tencent.kuikly-open:*` (notably the
    // OHOS variants like `core:2.29.2-2.0.21-ohos`). Declaring the real
    // Tencent public Maven proxy here guarantees dependency resolution
    // reaches an upstream that proxies the kuikly-open repository,
    // regardless of what the CI init script injects at settings level.
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/repository/maven-tencent/")
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
    }
    // Under source mode, Kuikly `:compose` is included as a sub-project
    // (see settings.2.0.ohos.gradle.kts), so any transitive coordinate
    // that happens to resolve to `com.tencent.kuikly-open:compose` must
    // be substituted back to the local project. Under artifact mode the
    // sub-project does not exist and this substitution would fail
    // configuration — skip it in that case (the Maven coordinate
    // already does the right thing).
    if (KuiklyDep.useSource(project)) {
        configurations.all {
            resolutionStrategy.dependencySubstitution {
                substitute(module("${MavenConfig.GROUP}:compose")).using(project(":compose"))
            }
        }
    }
}
