import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(Plugins.androidLibrary)
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    kotlin(Plugins.cocoapods)
    id(Plugins.mavenPublish)
    id(Plugins.signing)
}

val publishKey: String = gradleLocalProperties(rootDir).getProperty("publishKey")
val publishSecret: String = gradleLocalProperties(rootDir).getProperty("publishSecret")
val publishUsername: String = gradleLocalProperties(rootDir).getProperty("publishUsername")
val publishPassword: String = gradleLocalProperties(rootDir).getProperty("publishPassword")
val publishGroupId: String = gradleLocalProperties(rootDir).getProperty("publishGroupId")
val publishEmail: String = gradleLocalProperties(rootDir).getProperty("publishEmail")
val publishRepository: String = gradleLocalProperties(rootDir).getProperty("publishRepository")
val publishDeveloper: String = gradleLocalProperties(rootDir).getProperty("publishDeveloper")

val currentVersion = "1.0.27"
val libName = "strapiKMM"

version = currentVersion

kotlin {

    android {
        publishLibraryVariants("debug", "release")
//        publishLibraryVariantsGroupedByFlavor = true
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

    cocoapods {
        framework {
            export("io.github.kuuuurt:multiplatform-paging:0.4.7")
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
                api(Kotlin.kotlinxCoroutines) {
                    version {
                        strictly(Kotlin.kotlinxCoroutinesVersion)
                    }
                }
                api(ProjectDependencies.sharedPreferencesKVaultV)
                api(ProjectDependencies.paging)
            }
        }
        val androidMain by getting {
            dependencies {
                api(Ktor.android)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(Ktor.ios)
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
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
}


group = publishGroupId
version = currentVersion

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
