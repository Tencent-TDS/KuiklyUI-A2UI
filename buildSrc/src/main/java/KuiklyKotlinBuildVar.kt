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

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * Build-script helpers for the KuiklyUI-A2UI repository.
 *
 * Scope (kept intentionally narrow):
 *   - [BuildPlugin]        — Gradle plugin classpath coordinates (Kotlin / AGP).
 *   - [MavenConfig]        — Maven Central publication group + credential helpers.
 *   - [Version]            — Kotlin / AGP toolchain versions consumed by [BuildPlugin].
 *   - [configureMavenCentralMetadata] / [signPublicationIfKeyPresent] /
 *     [getSensitiveProperty] — publication POM + signing helpers.
 *
 * Note: Kuikly artifact coordinates (group / module / version) are **not**
 * declared here. They are owned entirely by [KuiklyDep] (see
 * `buildSrc/src/main/java/KuiklyDep.kt`), which is the single source of
 * truth for `kuikly.version` resolution and source/artifact mode switching.
 */

object BuildPlugin {
    val kotlin by lazy {
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.getKotlinVersion()}"
    }

    val android by lazy {
        "com.android.tools.build:gradle:${Version.getAGPVersion()}"
    }
}

object MavenConfig {
    const val GROUP = "com.tencent.kuikly-open"
    const val GROUP_WEB = "com.tencent.kuikly-open.core-render-web"
    const val REPO_URL = ""
    const val SNAPSHOT_REPO_URL = "https://central.sonatype.com/repository/maven-snapshots/"
    const val RENDER_ANDROID_ARTIFACT_ID = "core-render-android"
    const val RENDER_WEB_BASE_ARTIFACT_ID = "core-render-web-base"
    const val RENDER_WEB_H5_ARTIFACT_ID = "core-render-web-h5"
    const val RENDER_WEB_MINI_APP_ARTIFACT_ID = "core-render-web-miniapp"

    private const val KEY_USER_NAME = "username"
    private const val KEY_USER_PASSWORD = "password"

    fun getUsername(project: Project) : String {
        var username = ""
        if (project.hasProperty(KEY_USER_NAME)) {
            return project.property(KEY_USER_NAME) as String
        }
        if (username.isEmpty()) {
            val propertiesFile = File(project.rootDir, "local.properties")
            if (!propertiesFile.exists()) {
                return ""
            }
            val prop = Properties().apply {
                load(FileInputStream(propertiesFile))
            }
            username = prop.getProperty(KEY_USER_NAME) ?: ""
        }
        println("kuikly username is $username")
        return username
    }

    fun getPassword(project: Project) : String {
        var password = ""
        if (project.hasProperty(KEY_USER_PASSWORD)) {
            return project.property(KEY_USER_PASSWORD) as String
        }
        if (password.isEmpty()) {
            val propertiesFile = File(project.rootDir, "local.properties")
            if (!propertiesFile.exists()) {
                return ""
            }
            val prop = Properties().apply {
                load(FileInputStream(propertiesFile))
            }
            password = prop.getProperty(KEY_USER_PASSWORD) ?: ""
        }
        return password
    }

    fun getRepoUrl(version: String) : String {
        if (version.endsWith("-SNAPSHOT")) {
            return SNAPSHOT_REPO_URL
        }
        return REPO_URL
    }
}

object Version {

    private const val DEFAULT_KOTLIN_VERSION = "2.1.21"
    private const val DEFAULT_AGP_VERSION = "7.4.2"

    private const val KEY_KOTLIN_VERSION = "KUIKLY_KOTLIN_VERSION"
    private const val KEY_AGP_VERSION = "KUIKLY_AGP_VERSION"

    /**
     * Resolve the Kotlin toolchain version.
     * Honours `KUIKLY_KOTLIN_VERSION` (same env var name as Kuikly Core)
     * and falls back to [DEFAULT_KOTLIN_VERSION].
     */
    fun getKotlinVersion() : String {
        var kotlinVersion = System.getenv(KEY_KOTLIN_VERSION)
        if (kotlinVersion == null || kotlinVersion.isEmpty()) {
            kotlinVersion = DEFAULT_KOTLIN_VERSION
        }
        return kotlinVersion
    }

    /**
     * Resolve the Android Gradle Plugin version.
     * Honours `KUIKLY_AGP_VERSION` and falls back to [DEFAULT_AGP_VERSION].
     */
    fun getAGPVersion() : String {
        var agpVersion = System.getenv(KEY_AGP_VERSION)
        if (agpVersion == null || agpVersion.isEmpty()) {
            agpVersion = DEFAULT_AGP_VERSION
        }
        return agpVersion
    }
}

fun MavenPom.configureMavenCentralMetadata() {
    name.set("KuiklyUI-A2UI")
    description.set("`KuiklyUI-A2UI` is the standalone A2UI protocol (v0.9) implementation for Kuikly — a rendering engine that lets a server-side LLM stream JSON messages and drive real native UI on Android / iOS / HarmonyOS via Kuikly Core.")
    url.set("https://github.com/Tencent-TDS/KuiklyUI-A2UI")

    licenses {
        license {
            name.set("KuiklyUI")
            url.set("https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE")
        }
    }

    developers {
        developer {
            id.set("tds-Kuikly")
            name.set("tds-Kuikly Team")
        }
    }
    scm {
        url.set("https://github.com/Tencent-TDS/KuiklyUI-A2UI")
    }
}

fun MavenPublication.signPublicationIfKeyPresent(project: Project) {
    val keyId = getSensitiveProperty(project, "signing.keyId")
    val secretKey = getSensitiveProperty(project, "signing.secretKey")
    val password = getSensitiveProperty(project, "signing.password")
    if (!secretKey.isNullOrBlank()) {
        project.extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(keyId, secretKey, password)
            sign(this@signPublicationIfKeyPresent)
        }
    }
}

fun getSensitiveProperty(project: Project, name: String): String? {
    return project.findProperty(name) as? String ?: System.getenv(name)
}
