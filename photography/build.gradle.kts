// photography/build.gradle.kts

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
       // iOS targets only on macOS
    // Uncomment when building on macOS:
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Module dependencies
                api(project(":core"))
                api(project(":photographyShared"))
                
                // Kotlin essentials
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.extra["serialization_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${rootProject.extra["datetime_version"]}")
                
                // Dependency Injection
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
                
                // Math utilities for calculations
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.6")
                
                // UUID for entity generation
                implementation("com.benasher44:uuid:0.8.1")

                // TODO: Add astronomy library back once repository is configured
                // implementation("io.github.cosinekitty:astronomy:2.1.19")
                // TODO: Add atomicfu back with proper compiler plugin configuration if needed
                // implementation("org.jetbrains.kotlinx:kotlinx-atomicfu:0.27.0")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
                
                // Test astronomy calculations
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
                
                // Android-specific Koin
                implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
                implementation("io.insert-koin:koin-androidx-compose:${rootProject.extra["koin_version"]}")
                
                // Android EXIF interface
                implementation("androidx.exifinterface:exifinterface:1.3.6")
                
                // Android camera utilities
                implementation("androidx.camera:camera-core:1.3.1")
                implementation("androidx.camera:camera-camera2:1.3.1")
                
                // Color analysis libraries
                implementation("androidx.palette:palette-ktx:1.0.0")

                // Math/calculation libraries for exposure
                implementation("org.apache.commons:commons-math3:3.6.1")
            }
        }
        
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation("org.mockito:mockito-core:5.1.1")
                implementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
            }
        }
        

        
        // iOS sourcesets only on macOS
        // Uncomment when building on macOS:
        // val iosMain by getting {
        //     dependencies {
        //         // iOS-specific camera/image processing if needed
        //         // Most calculations will be in commonMain
        //     }
        // }
        // 
        // val iosTest by getting {
        //     dependencies {
        //         // iOS-specific test dependencies
        //     }
        // }
    }
}

android {
    namespace = "com.x3squaredcircles.photography"
    compileSdk = rootProject.extra["android_compile_sdk"] as Int
    
    defaultConfig {
        minSdk = rootProject.extra["android_min_sdk"] as Int
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
dependencies {
    implementation("androidx.exifinterface:exifinterface:1.4.1")
}
