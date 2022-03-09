import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    id(SwiftPackage.swiftPackage) version SwiftPackage.swiftPackageVersion
    id(Plugins.mavenPublish)
    signing
}

val currentVersion = "0.0.1"
val libName = "StrapiKMM"

version = currentVersion

kotlin {
    android()

    val xcf = XCFramework(libName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework(libName) {
            baseName = libName
            xcf.add(this)
        }
    }

    android {
        publishLibraryVariants("release", "debug")
        publishLibraryVariantsGroupedByFlavor = true
    }

    multiplatformSwiftPackage {
        packageName(libName)
        swiftToolsVersion("5.5")
        targetPlatforms {
            iOS { v("13") }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Ktor.core)
                api(Ktor.clientSerialization)
                api(Ktor.kotlinXSerialization)
                api(Ktor.logback)
                api(Ktor.logging)
                implementation(Kotlin.kotlinxCoroutines) {
                    version {
                        strictly(Kotlin.kotlinxCoroutinesVersion)
                    }
                }
                implementation(ProjectDependencies.firebaseGitLive)
                implementation(ProjectDependencies.sharedPreferencesKVaultV)
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

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    groupId = "com.swensonhe"
                    artifactId = libName
                    version = currentVersion

                    from(components.getByName("release"))
                }
                create<MavenPublication>("debug") {
                    groupId = "com.swensonhe"
                    artifactId = "$libName-debug"
                    version = currentVersion

                    from(components.getByName("debug"))
                }
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
