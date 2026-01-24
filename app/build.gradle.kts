import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

// Configuration
val productApkName = "ComposePager"
val productNamespace = "com.rwmobi.composepager"
val isRunningOnCI = System.getenv("CI") == "true"

android {
    namespace = productNamespace

    setupSdkVersionsFromVersionCatalog()
    setupSigningAndBuildTypes()
    setupPackagingResourcesDeduplication()

    defaultConfig {
        applicationId = "com.rwmobi.composepager"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        managedDevices {
            allDevices {
                create<ManagedVirtualDevice>("pixel2Api35") {
                    device = "Pixel 2"
                    apiLevel = 35
                    systemImageSource = "aosp-atd"
                    // testedAbi = "arm64-v8a" // better performance on CI and Macs
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
    jvmToolchain(21)
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui.util)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // For instrumented tests - with Kotlin
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.rules)
}

tasks {
    check { dependsOn("detekt") }
    preBuild { dependsOn("formatKotlin") }
}

detekt { parallel = true }

// Gradle Build Utilities - Revision 2026.01.22.01
private fun ApplicationExtension.setupSdkVersionsFromVersionCatalog() {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
}

private fun ApplicationExtension.setupPackagingResourcesDeduplication() {
    packaging.resources {
        excludes.addAll(
            listOf(
                "META-INF/*.md",
                "META-INF/proguard/*",
                "META-INF/*.kotlin_module",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/LICENSE-notice.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.properties",
                "/*.properties",
            ),
        )
    }
}

private fun ApplicationExtension.setupSigningAndBuildTypes() {
    val isReleaseSigningEnabled =
        providers.gradleProperty("releaseSigning")
            .map { it.toBoolean() }
            .orElse(false)
            .get()

    val releaseSigningConfigName = "releaseSigningConfig"
    val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
    val baseName = "$productApkName-${libs.versions.versionName.get()}-$timestamp"
    val isReleaseBuild = gradle.startParameter.taskNames.any {
        it.contains("Release", ignoreCase = true) ||
            it.contains("Bundle", ignoreCase = true)
    }

    project.extensions.configure<BasePluginExtension> { archivesName.set(baseName) }

    signingConfigs.create(releaseSigningConfigName) {
        // Only initialise the signing config when a Release or Bundle task is being executed.
        // This prevents Gradle sync or debug builds from attempting to load the keystore,
        // which could fail if the keystore or environment variables are not available.
        // SigningConfig itself is only wired to the 'release' build type, so this guard avoids unnecessary setup.
        if (isReleaseBuild && isReleaseSigningEnabled) {
            val keystorePropertiesFile = file("../../keystore.properties")
            println("🔑 Searching for keystore at ${keystorePropertiesFile.absolutePath}: exist? ${keystorePropertiesFile.exists()}")

            if (isRunningOnCI || !keystorePropertiesFile.exists()) {
                println("⚠\uFE0F Signing Config: using environment variables")
                keyAlias = System.getenv("CI_ANDROID_KEYSTORE_ALIAS")
                keyPassword = System.getenv("CI_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
                storeFile = file(System.getenv("KEYSTORE_LOCATION"))
                storePassword = System.getenv("CI_ANDROID_KEYSTORE_PASSWORD")
            } else {
                println("⚠\uFE0F Signing Config: using keystore properties")
                val properties = Properties()
                InputStreamReader(
                    FileInputStream(keystorePropertiesFile),
                    Charsets.UTF_8,
                ).use { reader ->
                    properties.load(reader)
                }

                keyAlias = properties.getProperty("alias")
                keyPassword = properties.getProperty("pass")
                storeFile = file(properties.getProperty("store"))
                storePassword = properties.getProperty("storePass")
            }
        } else {
            println("⚠️ Signing Config: skipped (no release signing intent)")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isDebuggable = true
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                ),
            )
            if (isReleaseSigningEnabled) {
                signingConfig = signingConfigs.getByName(name = releaseSigningConfigName)
            }
        }
    }
}
