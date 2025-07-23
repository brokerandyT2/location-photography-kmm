// photography/src/androidMain/kotlin/com/x3squaredcircles/photography/AndroidPhotographyApplication.kt
package com.x3squaredcircles.photography

import android.app.Application
import com.x3squaredcircles.photography.di.androidPlatformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AndroidPhotographyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {
            androidLogger()
            androidContext(this@AndroidPhotographyApplication)
            modules(
                com.x3squaredcircles.core.di.coreModule,
                com.x3squaredcircles.photography.di.photographySharedModule,
                androidPlatformModule,
                com.x3squaredcircles.photography.di.photographyModule
            )
        }
    }
}