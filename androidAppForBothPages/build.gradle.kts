plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui.demo"

    defaultConfig {
        applicationId = "com.tencent.kuikly.a2ui.demo.both"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":a2ui"))
    implementation(project(":a2ui-render-android"))
    // KMP business module holding the @Page-annotated DSL example pages
    // (e.g. A2UISurfaceDemoPager). Consumed at runtime by
    // A2UISurfaceDSLDemoActivity via KuiklyBaseView.onAttach(pageName=...).
    implementation(project(":demo"))
    // Kuikly render engine — required to host the DSL page inside a native
    // Android Activity (provides KuiklyBaseView / KuiklyRenderView).
    implementation(project(":core-render-android"))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Video playback for the A2UI v0.9 "Video" component (KRVideoView adapter)
    implementation("com.google.android.exoplayer:exoplayer:2.16.1")
}
