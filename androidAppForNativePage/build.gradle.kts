plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34
    namespace = "com.tencent.kuikly.a2ui.demo"

    defaultConfig {
        applicationId = "com.tencent.kuikly.a2ui.demo.nativepage"
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
    // Kuikly render engine — required for KRImage adapter (Native View path
    // still uses core-render Kuikly adapters for image/audio/video).
    implementation(KuiklyDep.coreRenderAndroid(project))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Video playback for the A2UI v0.9 "Video" component (KRVideoView adapter)
    implementation("com.google.android.exoplayer:exoplayer:2.16.1")
}

// -----------------------------------------------------------------------------
// Sync assets from androidAppForKuiklyPage (source of truth) to this module.
//
// Both androidAppForKuiklyPage and androidAppForNativePage need the exact same
// a2ui_* JSON demo assets and a2ui_icons. To avoid duplicating them in the
// repository, we treat androidAppForKuiklyPage/src/main/assets as the single
// source of truth and copy it into this module before every build.
//
// The destination directory is intentionally ignored by git — see the entry
// in a2ui/.gitignore for `androidAppForNativePage/src/main/assets/`.
// -----------------------------------------------------------------------------
val syncAssetsFromKuiklyPage by tasks.registering(Copy::class) {
    // Standalone A2UI project layout: rootProject IS the A2UI repo root,
    // so the source-of-truth assets live at
    // `<root>/androidAppForKuiklyPage/src/main/assets` (no `a2ui/` prefix).
    val sourceDir = rootProject.file("androidAppForKuiklyPage/src/main/assets")
    val destDir = projectDir.resolve("src/main/assets")

    from(sourceDir)
    into(destDir)
    // Prevent copying macOS metadata files.
    exclude("**/.DS_Store")
    doFirst {
        destDir.mkdirs()
    }
}

tasks.named("preBuild") {
    dependsOn(syncAssetsFromKuiklyPage)
}

