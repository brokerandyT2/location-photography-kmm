// core/src/commonMain/kotlin/com/x3squaredcircles/core/di/CoreModule.kt
package com.x3squaredcircles.core.di

import org.koin.dsl.module

val coreModule = module {
    // Core module is intentionally minimal
    // No dependencies since core is designed to be dependency-free
    // All core entities, value objects, and domain events are created directly
    // Repository interfaces are defined but not implemented in core

    // If any core-specific services are needed in the future, they would go here
    // For now, core remains pure domain with no infrastructure dependencies
}