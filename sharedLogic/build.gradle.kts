import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "com.silkysys.warehouse.sharedLogic"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)           // use core, not android-specific
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)  // use kotlinx, not gson
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.ktor.client.auth)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)        // Android engine here
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.legacySupportV4)
            implementation(libs.androidx.annotation)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)         // iOS engine
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
