plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    signing
}

group = MavenConfig.A2UI_GROUP
// Android/KMP-style Maven coordinate. When triggered via
// `2.0_a2ui_ohos_build.sh` the `KUIKLY_KOTLIN_VERSION=2.0.21-KBA-010`
// export causes `getArtifactVersion` to emit e.g.
// `1.0.0-2.0.21-ohos`.
version = A2uiVersion.getArtifactVersion(project)

publishing {
    repositories {
        val username = MavenConfig.getUsername(project)
        val password = MavenConfig.getPassword(project)
        if (username.isNotEmpty() && password.isNotEmpty()) {
            maven {
                credentials {
                    setUsername(username)
                    setPassword(password)
                }
                url = uri(MavenConfig.getRepoUrl(version as String))
            }
        } else {
            mavenLocal()
        }

        publications.withType<MavenPublication>().configureEach {
            pom.configureMavenCentralMetadata()
            signPublicationIfKeyPresent(project)
        }
    }
}

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

    // Android target is kept here (mirroring Kuikly's
    // `core/build.2.0.ohos.gradle.kts`) so that consumers compiling under
    // the OHOS channel — notably `:a2ui-render-android` which has
    // `compileOnly(project(":a2ui"))` — can resolve an `androidJvm`
    // variant. Without this target the project only exposes `ohosArm64`
    // and `common` variants, breaking Android-library consumers.
    androidTarget {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                moduleName = "${project.group}.${project.name}"
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
                api(KuiklyDep.core(project))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }

        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}

// Android library configuration required by the `androidTarget` above.
// Values mirror `a2ui/build.2.1.21.gradle.kts` to keep the Android AAR
// consistent across build channels.
android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
