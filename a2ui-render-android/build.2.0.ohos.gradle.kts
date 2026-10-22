plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    signing
}

group = MavenConfig.A2UI_GROUP
// Android/KMP Maven coordinate — keeps the Kotlin suffix so that this
// AAR aligns with the coordinate scheme of `:a2ui` and Kuikly Core.
// Under the OHOS channel the suffix resolves to `-ohos` (see
// `A2uiVersion.getArtifactVersion`), producing an AAR coordinate like
// `com.tencent.kuikly-a2ui-open:a2ui-render-android:<ver>-2.0.21-ohos`.
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
                groupId = MavenConfig.A2UI_GROUP
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
