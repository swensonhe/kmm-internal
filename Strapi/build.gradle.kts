//import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

val currentVersion = "0.0.5"
val libName = "strapiKMM"

version = currentVersion

kotlin {
//    android {
//        publishLibraryVariants("debug", "release")
//        publishLibraryVariantsGroupedByFlavor = true
//    }

//    val xcf = XCFramework(libName)
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries.framework(libName) {
//            baseName = libName
//            xcf.add(this)
//        }
//    }

//    multiplatformSwiftPackage {
//        packageName(libName)
//        swiftToolsVersion("5.5")
//        targetPlatforms {
//            iOS { v("13") }
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Ktor.core)
                api(Ktor.clientSerialization)
                api(Ktor.kotlinXSerialization)
                api(Ktor.logback)
                api(Ktor.logging)
                api(Kotlin.kotlinxCoroutines) {
                    version {
                        strictly(Kotlin.kotlinxCoroutinesVersion)
                    }
                }
                api(ProjectDependencies.firebaseGitLive)
                api(ProjectDependencies.sharedPreferencesKVaultV)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Ktor.android)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(Ktor.ios)
            }
        }
    }
}

android {
    compileSdk = Application.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
}
