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
// :demo ohos build script (Option-A minimal-dependency variant).
//
// Consumers on ohos:
//   ohosAppForKuiklyPage  &  ohosAppForBothPages
//
// Produces `libshared.so` that bundles:
//   * A2UI runtime (transitively via `api(:a2ui)`).
//   * Kuikly Pager classes annotated with `@Page`, including
//     `A2UISurfaceDemoPager` / `A2UICaseListPager` /
//     `ComposeSampleWithA2UIPager` / `A2UISurfaceDemoSample`.
//
// NativePage / Test apps do NOT consume this module — they only need
// `:a2ui`'s libshared.so (built by a2ui/build.2.0.ohos.gradle.kts)
// so their KMP payload stays minimal.
// ─────────────────────────────────────────────────────────────────────────

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
}

group = "com.tencent.kuikly.a2ui"
// Android/KMP-style Maven coordinate. When triggered via
// `2.0_a2ui_ohos_build.sh` the `KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010`
// export causes `getArtifactVersion` to emit e.g.
// `0.9.0-2.0.21-ohos-SNAPSHOT`.
version = A2uiVersion.getArtifactVersion(project)

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-Xinline-classes",
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlin.experimental.ExperimentalNativeApi",
                    "-opt-in=kotlin.contracts.ExperimentalContracts",
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
                    "-Xcontext-receivers"
                )
            }
        }
    }

    ohosArm64 {
        binaries.sharedLib("shared") {
            freeCompilerArgs += "-Xadd-light-debug=enable"
            linkerOpts += "--build-id=sha1"
            if (buildType == org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE) {
                val CLANG_OPT_FLAGS = "-Os   -ffunction-sections"
                val CLANG_FLAGS = "clangOptFlags.ios_arm64=$CLANG_OPT_FLAGS;clangDebugFlags.ios_arm64=$CLANG_OPT_FLAGS;clangOptFlags.ohos_arm64=$CLANG_OPT_FLAGS;clangDebugFlags.ohos_arm64=$CLANG_OPT_FLAGS"
                freeCompilerArgs += "-Xoverride-konan-properties=$CLANG_FLAGS"
                linkerOpts += "--pack-dyn-relocs=relr"
                linkerOpts += "--gc-sections"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(KuiklyDep.core(project))
                implementation(KuiklyDep.coreAnnotations(project))
                implementation(KuiklyDep.compose(project))
                // Re-export the A2UI runtime so the resulting libshared.so
                // contains both the runtime and the demo Pager classes.
                api(project(":a2ui"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}

dependencies {
    add("kspOhosArm64", KuiklyDep.coreKsp(project))
}

ksp {
    arg("moduleId", "a2ui_demo")
    arg("isMainModule", "true")
    arg("catchException", "false")
}
