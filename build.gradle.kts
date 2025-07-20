// build.gradle.kts (root)

plugins {
    // Kotlin Multiplatform
    kotlin("multiplatform") version "1.9.21" apply false
    kotlin("plugin.serialization") version "1.9.21" apply false
    
    // Android
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

// Common configuration for all projects
allprojects {
    group = "com.x3squaredcircles"
    version = "1.0.0"
    
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io") 
    }
}

// Common configuration for subprojects only
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }
}

// Root project tasks
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

// Version catalog for dependency management
ext {
    set("kotlin_version", "1.9.21")
    set("coroutines_version", "1.7.3")
    set("serialization_version", "1.6.0")
    set("datetime_version", "0.4.1")
    set("ktor_version", "2.3.5")
    set("koin_version", "3.5.0")
    set("android_compile_sdk", 34)
    set("android_min_sdk", 24)
    set("android_target_sdk", 34)
}