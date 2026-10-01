import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.kotlinSerialization)
}

fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

val qaBaseUrl = providers.gradleProperty("WMS_QA_BASE_URL")
    .orElse(providers.environmentVariable("WMS_QA_BASE_URL"))
    .orElse("https://pr-itenant.silky.systems/")

val stageBaseUrl = providers.gradleProperty("WMS_STAGE_BASE_URL")
    .orElse(providers.environmentVariable("WMS_STAGE_BASE_URL"))
    .orElse("https://itenant.silkysystems.com/")

val productionBaseUrl = providers.gradleProperty("WMS_PRODUCTION_BASE_URL")
    .orElse(providers.environmentVariable("WMS_PRODUCTION_BASE_URL"))
    .orElse("https://tenant.silkysystems.com/")

val recaptchaSiteKey = providers.gradleProperty("WMS_RECAPTCHA_SITE_KEY")
    .orElse(providers.environmentVariable("WMS_RECAPTCHA_SITE_KEY"))
    .orElse("")

val releaseStoreFile = providers.gradleProperty("WMS_KEYSTORE_FILE")
    .orElse(providers.environmentVariable("WMS_KEYSTORE_FILE"))

val releaseStorePassword = providers.gradleProperty("WMS_KEYSTORE_PASSWORD")
    .orElse(providers.environmentVariable("WMS_KEYSTORE_PASSWORD"))

val releaseKeyAlias = providers.gradleProperty("WMS_KEY_ALIAS")
    .orElse(providers.environmentVariable("WMS_KEY_ALIAS"))

val releaseKeyPassword = providers.gradleProperty("WMS_KEY_PASSWORD")
    .orElse(providers.environmentVariable("WMS_KEY_PASSWORD"))

val hasReleaseSigning = listOf(
    releaseStoreFile.orNull,
    releaseStorePassword.orNull,
    releaseKeyAlias.orNull,
    releaseKeyPassword.orNull,
).all { !it.isNullOrBlank() }


kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":sharedLogic"))
    implementation(projects.sharedUI)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.legacySupportV4)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.cardview)
    implementation(libs.google.material)
    implementation(libs.androidx.navigation.fragmentKtx)
    implementation(libs.androidx.navigation.uiKtx)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.playServices.maps)
    implementation(libs.shimmer)
    implementation(libs.firebase.messaging)
    implementation(libs.circleimageview)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.google.appUpdateKtx)
    implementation(libs.gifDrawable)
    implementation(libs.google.recaptcha)
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinxJson)
    implementation(libs.kotlinx.serializationJson)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)


    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidxCompose)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.coil)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.kotlinx.datetime)
    implementation(libs.barcode.scanning)
}

android {
    namespace = "com.silkysys.warehouse"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.silkysys.warehouse"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "1.4.0"

    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile.get())
                storePassword = releaseStorePassword.get()
                keyAlias = releaseKeyAlias.get()
                keyPassword = releaseKeyPassword.get()
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "BASE_URL", qaBaseUrl.get().asBuildConfigString())
            buildConfigField("String", "SITE_KEY", recaptchaSiteKey.get().asBuildConfigString())
        }

        create("stage") {
            initWith(getByName("debug"))
            versionNameSuffix = "-stage"
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "BASE_URL", stageBaseUrl.get().asBuildConfigString())
            buildConfigField("String", "SITE_KEY", recaptchaSiteKey.get().asBuildConfigString())
            matchingFallbacks += listOf("debug")
        }

        release {
            //isMinifyEnabled = false
            //isShrinkResources = true
            isDebuggable = false
            buildConfigField("String", "BASE_URL", productionBaseUrl.get().asBuildConfigString())
            buildConfigField("String", "SITE_KEY", recaptchaSiteKey.get().asBuildConfigString())

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }

            ndk {
                abiFilters += listOf("arm64-v8a")
                //abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "assets/dexopt/baseline.prof"
            excludes += "assets/dexopt/baseline.profm"
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


val verifyReleaseConfiguration = tasks.register("verifyReleaseConfiguration") {
    group = "verification"
    description =
        "Fails production builds when required security configuration is missing or unsafe."

    val resolvedProductionBaseUrl = productionBaseUrl.get()
    val resolvedRecaptchaSiteKey = recaptchaSiteKey.get()
    val resolvedHasReleaseSigning = hasReleaseSigning

    doLast {
        require(resolvedProductionBaseUrl.startsWith("https://")) {
            "WMS_PRODUCTION_BASE_URL must use HTTPS."
        }
        require(!resolvedProductionBaseUrl.contains("pr-", ignoreCase = true)) {
            "WMS_PRODUCTION_BASE_URL must not point to a pre-production tenant."
        }
        require(resolvedRecaptchaSiteKey.isNotBlank()) {
            "WMS_RECAPTCHA_SITE_KEY must be supplied for release builds."
        }
        require(resolvedHasReleaseSigning) {
            "Release signing is not configured. Supply WMS_KEYSTORE_FILE, WMS_KEYSTORE_PASSWORD, WMS_KEY_ALIAS and WMS_KEY_PASSWORD."
        }
    }
}

tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    dependsOn(verifyReleaseConfiguration)
}

