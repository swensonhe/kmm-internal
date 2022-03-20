import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version

    id(Plugins.mavenPublish)
    id(Plugins.Signing)
}

val publishKey: String = gradleLocalProperties(rootDir).getProperty("publishKey")
val publishSecret: String = gradleLocalProperties(rootDir).getProperty("publishSecret")
val publishUsername: String = gradleLocalProperties(rootDir).getProperty("publishUsername")
val publishPassword: String = gradleLocalProperties(rootDir).getProperty("publishPassword")
val publishGroupId: String = gradleLocalProperties(rootDir).getProperty("publishGroupId")
val publishEmail: String = gradleLocalProperties(rootDir).getProperty("publishEmail")
val publishRepository: String = gradleLocalProperties(rootDir).getProperty("publishRepository")
val publishDeveloper: String = gradleLocalProperties(rootDir).getProperty("publishDeveloper")

val currentVersion = "0.0.8"
val libName = "strapiKMM"

version = currentVersion

kotlin {

    android {
        publishLibraryVariants("debug", "release")
        publishLibraryVariantsGroupedByFlavor = true
    }

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

    metadata {
        compilations.matching { it.name == "iosMain" }.all {
            compileKotlinTaskProvider.configure { enabled = false }
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
                    groupId = publishGroupId
                    artifactId = libName.toLowerCase()
                    version = currentVersion

                    from(components.getByName("release"))
                }
                create<MavenPublication>("debug") {
                    groupId = publishGroupId
                    artifactId = "${libName.toLowerCase()}-debug"
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


group = publishGroupId
version = "0.8"

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = project.group.toString()
    }
}

publishing {
    repositories {
        maven {
            name = "oss"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = publishUsername
                password = publishPassword
            }
        }
    }

    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }
    publications.withType<MavenPublication> {

        artifact(javadocJar)

        pom {
            name.set("Strapi-Kmm")
            description.set("Shared KMM Module")
            url.set(publishRepository)

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            scm {
                connection.set(publishRepository)
                url.set(publishRepository)
            }
            developers {
                developer {
                    name.set(publishDeveloper)
                    email.set(publishEmail)
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(publishKey,publishSecret, publishUsername)
    sign(publishing.publications)
}

afterEvaluate {
    val compilation = kotlin.targets["metadata"].compilations["iosMain"]
    compilation.compileKotlinTask.doFirst {
        compilation.compileDependencyFiles = files(
            compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
        )
    }
}
