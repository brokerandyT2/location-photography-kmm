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

// Command Handlers
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.commands.exposurecalculator.handlers.*
import com.x3squaredcircles.photography.application.commands.phonecameraprofile.handlers.*
import com.x3squaredcircles.photography.application.commands.sceneevaluation.handlers.*

// Query Handlers
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.application.queries.setting.handlers.*
import com.x3squaredcircles.photography.application.queries.camerabody.handlers.*
import com.x3squaredcircles.photography.application.queries.lens.handlers.*
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers.*
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers.*
import com.x3squaredcircles.photography.application.queries.sunlocation.handlers.*
import com.x3squaredcircles.photography.application.queries.exposurecalculator.handlers.*

import org.koin.dsl.module

val photographySharedModule = module {

    // =============== CORE INFRASTRUCTURE ===============

    // Database
    single<PhotographyDatabase> {
        PhotographyDatabase(driver = get<SqlDriver>())
    }

    // Logger
    single<Logger> {
        Logger.withTag("PhotographyApp")
    }

    // Exception Mapping Service
    single<IInfrastructureExceptionMappingService> {
        InfrastructureExceptionMappingService()
    }

    // Database Initializer
    single<DatabaseInitializer> {
        DatabaseInitializer(get(), get(), get())
    }

    // =============== REPOSITORIES ===============

    // Existing Repositories
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

    single<ISettingRepository> {
        SettingRepository(get(), get(), get())
    }

    single<ILensRepository> {
        LensRepository(get(), get(), get())
    }

    single<ILocationRepository> {
        LocationRepository(get(), get(), get())
    }

    single<ILensCameraCompatibilityRepository> {
        LensCameraCompatibilityRepository(get(), get(), get())
    }

    single<IPhoneCameraProfileRepository> {
        PhoneCameraProfileRepository(get(), get(), get())
    }

    // =============== DOMAIN SERVICES ===============

    single<IEquipmentRecommendationService> {
        EquipmentRecommendationService(get(), get(), get(), get())
    }

    single<ISubscriptionService> {
        SubscriptionService(get(), get())
    }

    single<ISunCalculatorService> {
        SunCalculatorService(get())
    }

    single<ICameraSensorProfileService> {
        CameraSensorProfileService(get())
    }

    single<IImageAnalysisService> {
        ImageAnalysisService(get())
    }

    single<IExifService> {
        ExifService(get())
    }

    single<IExposureCalculatorService> {
        ExposureCalculatorService(get(), get())
    }

    single<ISceneEvaluationService> {
        SceneEvaluationService(get(), get(), get())
    }

    single<IExposureTriangleService> {
        ExposureTriangleService()
    }

    single<IAstroCalculationService> {
        AstroCalculationService(get(), get())
    }

    single<ICameraService> {
        CameraService(get(), get())
    }

    // =============== COMMAND HANDLERS ===============

    factory<ICommandHandler<com.x3squaredcircles.photography.application.commands.exposurecalculator.CalculateExposureCommand, com.x3squaredcircles.photography.application.commands.exposurecalculator.CalculateExposureCommandResult>> {
        CalculateExposureCommandHandler(get(), get())
    }

    factory<ICommandHandler<com.x3squaredcircles.photography.application.commands.phonecameraprofile.CreatePhoneCameraProfileCommand, com.x3squaredcircles.photography.application.commands.phonecameraprofile.CreatePhoneCameraProfileCommandResult>> {
        CreatePhoneCameraProfileCommandHandler(get(), get(), get())
    }

    factory<ICommandHandler<com.x3squaredcircles.photography.application.commands.sceneevaluation.AnalyzeImageCommand, com.x3squaredcircles.photography.application.commands.sceneevaluation.AnalyzeImageCommandResult>> {
        AnalyzeImageCommandHandler(get(), get())
    }

    // =============== QUERY HANDLERS ===============

    // Setting Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQuery, com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQueryResult>> {
        GetAllSettingsQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQuery, com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQueryResult>> {
        GetAllSettingsAsDictionaryQueryHandler(get(), get())
    }

    // Camera Body Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesQuery, com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesQueryResult>> {
        GetCameraBodiesQueryHandler(get(), get(), get())
    }

    // Lens Camera Compatibility Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQuery, com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQueryResult>> {
        GetAllLensCameraCompatibilityQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQuery, com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQueryResult>> {
        GetLensCameraCompatibilityByIdQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQuery, com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQueryResult>> {
        GetLensCameraCompatibilityByLensIdQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQuery, com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQueryResult>> {
        GetLensCameraCompatibilityByCameraIdQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQuery, com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQueryResult>> {
        LensCameraCompatibilityExistsQueryHandler(get(), get())
    }

    // Phone Camera Profile Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQuery, com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQueryResult>> {
        GetAllPhoneCameraProfilesQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQuery, com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQueryResult>> {
        GetActivePhoneCameraProfileQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQuery, com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQueryResult>> {
        GetPhoneCameraProfilesByPhoneModelQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQuery, com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQueryResult>> {
        GetPhoneCameraProfilesCountQueryHandler(get(), get())
    }

    // Sun Location Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.sunlocation.GetShadowCalculationQuery, com.x3squaredcircles.photography.application.queries.sunlocation.GetShadowCalculationQueryResult>> {
        GetShadowCalculationQueryHandler(get(), get())
    }

    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.sunlocation.GetOptimalShootingTimesQuery, com.x3squaredcircles.photography.application.queries.sunlocation.GetOptimalShootingTimesQueryResult>> {
        GetOptimalShootingTimesQueryHandler(get(), get())
    }

    // Exposure Calculator Query Handlers
    factory<IQueryHandler<com.x3squaredcircles.photography.application.queries.exposurecalculator.GetExposureValuesQuery, com.x3squaredcircles.photography.application.queries.exposurecalculator.GetExposureValuesQueryResult>> {
        GetExposureValuesQueryHandler(get(), get())
    }
}