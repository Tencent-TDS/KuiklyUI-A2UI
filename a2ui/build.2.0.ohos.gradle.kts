plugins {
    kotlin("multiplatform")
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
                    "-opt-in=kotlin.contracts.ExperimentalContracts"
                )
            }
        }
    }

    ohosArm64 {
        binaries.sharedLib("shared") {
            freeCompilerArgs += "-Xadd-light-debug=enable"
            linkerOpts += "--build-id=sha1"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
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
