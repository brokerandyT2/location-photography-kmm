// photographyShared/src/androidMain/kotlin/com/x3squaredcircles/photography/di/AndroidPlatformModule.kt
package com.x3squaredcircles.photography.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.services.PlatformCameraManager
import com.x3squaredcircles.photography.infrastructure.services.PlatformImageProcessor
import com.x3squaredcircles.photography.infrastructure.services.createPlatformImageProcessor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {

    // Android SQLDelight Driver
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = PhotographyDatabase.Schema,
            context = androidContext(),
            name = "photography.db"
        )
    }

    // Platform-specific services that exist
    single<PlatformCameraManager> {
        PlatformCameraManager(androidContext())
    }

    single<PlatformImageProcessor> {
        createPlatformImageProcessor()
    }

    // Android Context is automatically available via androidContext()
}