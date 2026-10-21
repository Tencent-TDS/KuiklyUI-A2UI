plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui.demo"

    defaultConfig {
        applicationId = "com.tencent.kuikly.a2ui.demo.kuiklypage"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":a2ui"))
    implementation(project(":a2ui-render-android"))
    // KMP business module holding the @Page-annotated DSL example pages
    // (A2UISurfaceDemoPager for the "card scene", A2UIJsonHostPager for the
    // component / custom-function / custom-component / custom-icon details,
    // ComposeSamplePager for the Compose integration entry). Consumed via
    // KuiklyBaseView.onAttach(pageName=...).
    implementation(project(":demo"))
    // Kuikly render engine — required to host the DSL page inside a native
    // Android Activity (provides KuiklyBaseView / KuiklyRenderView).
    implementation(KuiklyDep.coreRenderAndroid(project))
    // Kuikly Compose module — used by the "Compose Page integration" entry to
    // showcase embedding A2UI inside a Kuikly Compose page.
    implementation(KuiklyDep.compose(project))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Video playback for the A2UI v0.9 "Video" component (KRVideoView adapter)
    implementation("com.google.android.exoplayer:exoplayer:2.16.1")
}
