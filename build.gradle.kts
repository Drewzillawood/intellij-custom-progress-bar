import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.intellij.platform") version "2.0.1"
    kotlin("plugin.serialization") version "1.9.20"
}

val ideaVersion: String by project
val ideaType: String by project

dependencies {
    intellijPlatform {
        create(ideaType, ideaVersion)

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

group = "com.drewzillawood.CustomProgressBar"
version = "2024.3.1-1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    pluginConfiguration {
        name = "Custom Progress Bar"
        ideaVersion {
            sinceBuild = "243"
            untilBuild.set(provider { null })
        }
    }

    publishing {
        hidden = true
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_21.toString().toInt())
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}

tasks {
    // Set the JVM compatibility versions
//    withType<JavaCompile> {
//        options.encoding = "UTF-8"
//        sourceCompatibility = "21"
//        targetCompatibility = "21"
//    }
//    withType<KotlinJvmCompile> {
//        compilerOptions {
//            jvmTarget = JvmTarget.JVM_21
//        }
//    }
//
//    patchPluginXml {
//        sinceBuild.set("242")
//        untilBuild.set("242.*")
//    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
