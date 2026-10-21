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

// ─────────────────────────────────────────────────────────────────────────
// :demo — KMP library holding business-side A2UI DSL example pages.
//
// **Positioning**: business sample / integration reference. This module is
// **not published** as a public artifact (no Maven publish, no `pod trunk
// push`, no ohpm publish). Downstream integrators author their own
// equivalent KMP business module and consume it via `:path` (iOS) /
// `implementation project(...)` (Android) / local dependency (OHOS).
//
// Mirrors the top-level `:demo` module of the Kuikly project:
//
//   :core                    ─┐
//   :a2ui       (KMP)         ├─►  :demo  (KMP, this module)  ──►  :androidAppForKuiklyPage
//   :core-annotations         │                                     (also consumed by AppForBothPages & Test)
//   :core-ksp   (KSP only)   ─┘
//
// What lives here:
//   * `@Page("A2UISurfaceDemo")` — Pager classes written in Kuikly DSL that
//      embed `A2UISurface { ... }` inside a hand-written page.
//   * Nothing platform-specific. All example pages are commonMain-only.
//
// What does NOT live here:
//   * The A2UI runtime (that stays in :a2ui).
//   * Native View integration examples (those stay in :androidAppForNativePage,
//      because they are plain Android Activities and do not need KMP).
//
// Consumers (all local, in-repo):
//   * Android — `:androidAppForKuiklyPage` & friends depend on this module
//      via `implementation(project(":demo"))`.
//   * iOS — the `cocoapods { framework { baseName = "A2UIDemo" } }` block
//      below produces `A2UIDemo.framework` (via `syncFramework`); the
//      generated `demo/a2ui_demo.podspec` is consumed by
//      `iosAppForKuiklyPage / iosAppForBothPages` via
//      `pod 'a2ui_demo', :path => '../demo'` — never through a pod repo.
// ─────────────────────────────────────────────────────────────────────────

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

group = MavenConfig.GROUP
// Android/KMP Maven coordinate — includes the Kotlin suffix
// (`1.0.0-2.1.21`) to match Kuikly Core's convention and to
// keep the two build files (2.1.21 vs 2.0.ohos) from overwriting each
// other in the local/remote Maven repo.
version = A2uiVersion.getArtifactVersion(project)

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
                moduleName = "${project.group}.${project.name}"
            }
        }
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("release")
    }

    // iOS targets kept so the module builds under KMP even before :iosApp
    // is wired up. Safe to keep — no iOS-only code lives here yet.
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    cocoapods {
        summary = "A2UI DSL demo pages (KMP business sample — :path consumption only, not published)"
        homepage = "https://github.com/Tencent-TDS/KuiklyUI-A2UI"
        // Plain SemVer for the pod — CocoaPods rejects the multi-segment
        // artifact form (`1.0.0-2.1.21`).
        version = A2uiVersion.getSemantic(project)
        license = "{ :type => 'KuiklyUI', :file => '../LICENSE' }"
        authors = "Kuikly Team"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "A2UIDemo"
            isStatic = true

        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(KuiklyDep.core(project))
                implementation(KuiklyDep.coreAnnotations(project))
                // `implementation` is sufficient now that the framework
                // block no longer `export`s `:a2ui`. The five
                // `@ObjCName` entry classes needed by the render pod
                // live as thin shells in `:demo/iosMain` and forward
                // to `:a2ui`'s commonMain singletons — none of
                // `:a2ui`'s symbols need to appear on `:demo`'s API
                // surface for downstream Kotlin consumers.
                implementation(project(":a2ui"))
                // For the "Compose Page integration" example page. Consumed
                // by `:androidAppForKuiklyPage` at runtime; unused apps
                // let R8 strip the Compose entry classes.
                implementation(KuiklyDep.compose(project))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
    }

    sourceSets.iosMain {
        dependsOn(sourceSets.getByName("commonMain"))
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        val mainSourceSets = this.compilations.getByName("main").defaultSourceSet
        if (konanTarget.family.isAppleFamily) {
            mainSourceSets.dependsOn(sourceSets.getByName("iosMain"))
        }
    }
}

// KSP configuration: mirrors the top-level `:demo` module. `moduleId` /
// `isMainModule` follow the multi-module page-routing convention used by
// core-ksp. Note: enableMultiModule is left off so this module still emits a
// self-contained page registry (there is no sub-module here).
ksp {
    arg("pageName", project.properties["pageName"] as? String ?: "")
    arg("pageNameList", project.properties["pageNameList"] as? String ?: "")
    arg("moduleId", "a2ui_demo")
    arg("isMainModule", "true")
}

dependencies {
    val coreKsp = KuiklyDep.coreKsp(project)
    compileOnly(coreKsp)
    add("kspAndroid", coreKsp)
    add("kspIosArm64", coreKsp)
    add("kspIosX64", coreKsp)
    add("kspIosSimulatorArm64", coreKsp)
}

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui.demo"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
