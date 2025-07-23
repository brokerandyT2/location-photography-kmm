// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/di/PhotographySharedModule.kt
package com.x3squaredcircles.photography.di

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger

import com.x3squaredcircles.photography.domain.services.*
import com.x3squaredcircles.photography.infrastructure.datapopulation.DatabaseInitializer
import com.x3squaredcircles.photography.infrastructure.repositories.*
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.*
import com.x3squaredcircles.photography.infrastructure.services.*
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import org.koin.dsl.module

val photographySharedModule = module {

    // Database
    single<PhotographyDatabase> {
        PhotographyDatabase(driver = get<SqlDriver>())
    }

    // Logger
    single<Logger> {
        Logger.withTag("PhotographyApp")
    }

    // Exception Mapping Service (will need to be created)
    single<IInfrastructureExceptionMappingService> {
        InfrastructureExceptionMappingService()
    }

    // Repositories that exist
    single<ITipTypeRepository> {
        TipTypeRepository(get(), get(), get())
    }

    single<ITipRepository> {
        TipRepository(get(), get(), get())
    }

    single<ICameraBodyRepository> {
        CameraBodyRepository(get(), get(), get())
    }

    single<IWeatherRepository> {
        WeatherRepository(get(), get(), get())
    }

    // Services that exist
    single<IEquipmentRecommendationService> {
        EquipmentRecommendationService(get(), get(), get(), get(), get())
    }

    single<ISubscriptionService> {
        SubscriptionService(get(), get())
    }

    single<ISunCalculatorService> {
        SunCalculatorService(get())
    }

    // Database Initializer
    single<DatabaseInitializer> {
        DatabaseInitializer(get(), get(), get())
    }

    // Note: Additional repositories, services, handlers, and validators
    // will be added as they are created during the migration process
}