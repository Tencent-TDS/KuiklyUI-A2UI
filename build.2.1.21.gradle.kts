plugins {
    kotlin("multiplatform") version "2.1.21" apply false
    kotlin("plugin.compose") version "2.1.21" apply false
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
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
    dependencies {
        classpath(BuildPlugin.kotlin)
        classpath(BuildPlugin.android)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/repository/maven-tencent/")
        }
    }
    // Under source mode, Kuikly `:compose` is included as a sub-project
    // (see settings.gradle.kts), so any transitive coordinate that
    // happens to resolve to `com.tencent.kuikly-open:compose` must be
    // substituted back to the local project. Under artifact mode the
    // sub-project does not exist and this substitution would fail
    // configuration \u2014 skip it in that case (the Maven coordinate
    // already does the right thing).
    if (KuiklyDep.useSource(project)) {
        configurations.all {
            resolutionStrategy.dependencySubstitution {
                substitute(module("${MavenConfig.GROUP}:compose")).using(project(":compose"))
            }
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
    }
}
