plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

subprojects {
    afterEvaluate {
        if (pluginManager.hasPlugin("com.android.library")) {
            extensions.getByType<com.android.build.gradle.LibraryExtension>().apply {
                namespace = namespace ?: "com.example.flutter.plugins"
            }
        }
    }
}

android {
    namespace = "com.example.zero_touch_car_diagnostics"
    compileSdk = 36
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    defaultConfig {
        applicationId = "com.example.zero_touch_car_diagnostics"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        // Explicitly set versionCode/versionName for this release
        versionCode = 5
        versionName = "ZTCDv1.32.12BETA"
    }

    val releaseKeystoreFile = file("${project.projectDir}/release.keystore")

    signingConfigs {
        create("release") {
            if (releaseKeystoreFile.exists()) {
                storeFile = releaseKeystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Only apply signing config if keystore exists
            if (releaseKeystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    // Enable CMake for native C++ code (backend bridge)
    // Temporarily disabled for release build
    // externalNativeBuild {
    //     cmake {
    //         path = file("CMakeLists.txt")
    //         version = "3.18.1"
    //     }
    // }
}

flutter {
    source = "../.."
}

tasks.register("renameReleaseApk") {
    dependsOn("assembleRelease")
    doLast {
        val outputDir = file("${project.projectDir}/build/outputs/apk/release")
        val targetName = "ZTCDv1.32.12BETA.apk"
        val targetFile = file("${outputDir}/${targetName}")
        
        // Find the release APK (signed or unsigned)
        val releaseApk = outputDir.listFiles()?.firstOrNull { 
            it.name.matches(Regex("app-release(-unsigned)?\\.apk"))
        }
        
        if (releaseApk != null && releaseApk.exists()) {
            releaseApk.copyTo(targetFile, overwrite = true)
            println("Renamed ${releaseApk.name} to ${targetName}")
        } else {
            throw GradleException("Release APK not found in ${outputDir}")
        }
    }
}
