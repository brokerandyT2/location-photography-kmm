// core/build.gradle.kts

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    // Target platforms
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    // JVM target for desktop development
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    // iOS targets only on macOS
    // Uncomment when building on macOS:
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin essentials
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.extra["serialization_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${rootProject.extra["datetime_version"]}")
                
                // Networking (for weather/location services)
                implementation("io.ktor:ktor-client-core:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-content-negotiation:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.extra["ktor_version"]}")
                
                // Dependency Injection
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
                
                // NO database dependencies - core stays database agnostic
                // NO repository implementations - only interfaces
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
            }
        }
        
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        
        val desktopMain by getting {
            dependencies {
                // Desktop-specific dependencies if needed
            }
        }
        
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        
        // iOS sourcesets only on macOS
        // Uncomment when building on macOS:
        // val iosMain by getting {
        //     dependencies {
        //         // iOS-specific dependencies if needed
        //     }
        // }
    }
}

android {
    namespace = "com.x3squaredcircles.core"
    compileSdk = rootProject.extra["android_compile_sdk"] as Int
    
    defaultConfig {
        minSdk = rootProject.extra["android_min_sdk"] as Int
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}