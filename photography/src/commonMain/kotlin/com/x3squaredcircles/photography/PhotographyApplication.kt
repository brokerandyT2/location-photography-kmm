// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/PhotographyApplication.kt
package com.x3squaredcircles.photography

import com.x3squaredcircles.core.di.coreModule
import com.x3squaredcircles.photography.di.photographyModule
import com.x3squaredcircles.photography.di.photographySharedModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

object PhotographyApplication {

    fun initializeKoin(platformModule: Module) {
        startKoin {
            modules(
                coreModule,
                photographySharedModule,
                platformModule,
                photographyModule
            )
        }
    }

    fun initializeKoinWithModules(modules: List<Module>) {
        startKoin {
            modules(modules)
        }
    }
}