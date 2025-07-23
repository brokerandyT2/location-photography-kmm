// photographyShared/build.gradle.kts

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("app.cash.sqldelight") version "2.0.0"
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
    // iOS targets only on macOS
    // Uncomment when building on macOS:
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Core module dependency
                api(project(":core"))
                
                // Kotlin essentials
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.extra["serialization_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${rootProject.extra["datetime_version"]}")
                
                // Database - SQLDelight
                implementation("app.cash.sqldelight:runtime:2.0.0")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")
                
                // Networking (for repository implementations)
                implementation("io.ktor:ktor-client-core:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-content-negotiation:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.extra["ktor_version"]}")
                
                // Dependency Injection
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
                implementation("io.github.cosinekitty:astronomy:2.1.19")
                // Logging
                implementation("co.touchlab:kermit:1.2.2")
                implementation("com.drewnoakes:metadata-extractor:2.19.0")
                // UUID generation for entities
                implementation("com.benasher44:uuid:0.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
                
                // Android SQLDelight driver
                implementation("app.cash.sqldelight:android-driver:2.0.0")
                
                // Android-specific Koin
                implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
            }
        }
        
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            }
        }
        
        
        
        // iOS sourcesets only on macOS
        // Uncomment when building on macOS:
        // val iosMain by getting {
        //     dependencies {
        //         // iOS SQLDelight driver
        //         implementation("app.cash.sqldelight:native-driver:2.0.0")
        //     }
        // }
    }
}

android {
    namespace = "com.x3squaredcircles.photographyshared"
    compileSdk = rootProject.extra["android_compile_sdk"] as Int
    
    defaultConfig {
        minSdk = rootProject.extra["android_min_sdk"] as Int
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// SQLDelight configuration
sqldelight {
    databases {
        create("PhotographyDatabase") {
            packageName.set("com.x3squaredcircles.photographyshared.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(false)
            
        }
    }
}