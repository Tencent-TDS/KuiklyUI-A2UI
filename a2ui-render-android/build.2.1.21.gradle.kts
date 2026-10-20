plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    signing
}

group = MavenConfig.GROUP
// Android/KMP Maven coordinate — keeps the Kotlin suffix so that this
// AAR aligns with the coordinate scheme of `:a2ui` and Kuikly Core.
version = A2uiVersion.getArtifactVersion(project)

afterEvaluate {
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
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = MavenConfig.GROUP
                artifactId = "a2ui-render-android"
                version = A2uiVersion.getArtifactVersion(project)
                from(components["release"])
                pom.configureMavenCentralMetadata()
                signPublicationIfKeyPresent(project)
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui.render.android"

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        moduleName = "${project.group}.${project.name}"
    }

    publishing {
        singleVariant("release") {

        }
    }
}

dependencies {
    // A2UI core module (message parsing, dispatch, surface management, etc.)
    compileOnly(project(":a2ui"))
    // Kuikly native render layer (KuiklyRenderLayerHandler, scheduler, components, etc.)
    compileOnly(KuiklyDep.coreRenderAndroid(project))
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.4.2")
}
