plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "2.1.21"
    id("maven-publish")
    signing
}

group = MavenConfig.GROUP
// Android/KMP Maven coordinate — includes the Kotlin suffix
// (`0.9.0-2.1.21-SNAPSHOT`) to match Kuikly Core's convention and to
// keep the two build files (2.1.21 vs 2.0.ohos) from overwriting each
// other in the local/remote Maven repo.
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

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    iosSimulatorArm64()
    iosX64()
    iosArm64()

    cocoapods {
        summary = "A2UI Core Module"
        homepage = "https://github.com/Tencent-TDS/KuiklyUI-A2UI"
        // Plain SemVer for the pod — CocoaPods rejects the multi-segment
        // artifact form (`0.9.0-2.1.21-SNAPSHOT`).
        version = A2uiVersion.getSemantic(project)
        license = "{ :type => 'KuiklyUI', :file => '../LICENSE' }"
        authors = "Kuikly Team"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "A2UICore"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api(KuiklyDep.core(project))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val jvmMain by getting
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

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui"
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
