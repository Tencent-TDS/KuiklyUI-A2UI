plugins {
    kotlin("multiplatform")
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

        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}
