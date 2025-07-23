// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/di/PhotographyModule.kt
package com.x3squaredcircles.photography.di

import com.x3squaredcircles.photography.viewmodels.*
import com.x3squaredcircles.photography.models.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val photographyModule = module {

    // ==== CONFIRMED VIEWMODELS ====

    // Main ViewModels (extend BaseViewModel)
    factoryOf(::LocationViewModel)
    factoryOf(::LocationsViewModel)
    factoryOf(::WeatherViewModel)
    factoryOf(::TipsViewModel)
    factoryOf(::SettingViewModel)
    factoryOf(::AboutViewModel)

    // Item ViewModels (don't extend BaseViewModel)
    factoryOf(::TipItemViewModel)
    factoryOf(::TipTypeItemViewModel)
    factoryOf(::DailyWeatherViewModel)
    factoryOf(::LocationListItemViewModel)

    // Models (simple data classes)
    factoryOf(::SimpleLocationViewModel)

    // ==== LIKELY MISSING VIEWMODELS ====
    // Based on photography app architecture and features

    // Main ViewModels
    factoryOf(::MainViewModel)
    factoryOf(::CameraViewModel)
    factoryOf(::SunMoonViewModel)
    factoryOf(::EquipmentViewModel)
    factoryOf(::SubscriptionViewModel)
    factoryOf(::SettingsViewModel)

    // Photography-specific ViewModels
    factoryOf(::ExposureViewModel)
    factoryOf(::ExposureCalculatorViewModel)
    factoryOf(::LensViewModel)
    factoryOf(::CameraBodyViewModel)
    factoryOf(::EquipmentRecommendationViewModel)
    factoryOf(::PhotoAnalysisViewModel)
    factoryOf(::ExifViewModel)
    factoryOf(::HistogramViewModel)
    factoryOf(::SceneEvaluationViewModel)

    // Astronomy/Solar ViewModels
    factoryOf(::SunCalculatorViewModel)
    factoryOf(::MoonPhaseViewModel)
    factoryOf(::AstronomyViewModel)
    factoryOf(::PlanetaryViewModel)
    factoryOf(::DeepSkyViewModel)
    factoryOf(::MeteorShowerViewModel)
    factoryOf(::OptimalShootingTimesViewModel)

    // Location/Weather ViewModels
    factoryOf(::LocationDetailViewModel)
    factoryOf(::WeatherForecastViewModel)
    factoryOf(::HourlyForecastViewModel)
    factoryOf(::WeatherImpactViewModel)

    // UI/Navigation ViewModels
    factoryOf(::OnboardingViewModel)
    factoryOf(::ProfileViewModel)
    factoryOf(::AboutViewModel)
    factoryOf(::HelpViewModel)
    factoryOf(::DiagnosticsViewModel)
    factoryOf(::ExportImportViewModel)
    factoryOf(::BackupRestoreViewModel)
    factoryOf(::GalleryViewModel)
    factoryOf(::MapViewModel)
    factoryOf(::SearchViewModel)
    factoryOf(::FavoritesViewModel)
    factoryOf(::HistoryViewModel)
    factoryOf(::CalendarViewModel)
    factoryOf(::PlannerViewModel)

    // Settings ViewModels
    factoryOf(::NotificationSettingsViewModel)
    factoryOf(::PrivacySettingsViewModel)
    factoryOf(::SecuritySettingsViewModel)
    factoryOf(::AppearanceSettingsViewModel)
    factoryOf(::AdvancedSettingsViewModel)
    factoryOf(::DeveloperSettingsViewModel)

    // Subscription/Purchase ViewModels
    factoryOf(::BillingViewModel)
    factoryOf(::PurchaseViewModel)
    factoryOf(::FeatureAccessViewModel)

    // List/Detail ViewModels
    factoryOf(::TipDetailViewModel)
    factoryOf(::TipListViewModel)
    factoryOf(::LocationEditViewModel)
    factoryOf(::CameraBodyListViewModel)
    factoryOf(::LensListViewModel)
    factoryOf(::EquipmentListViewModel)

    // Item ViewModels
    factoryOf(::CameraBodyItemViewModel)
    factoryOf(::LensItemViewModel)
    factoryOf(::EquipmentItemViewModel)
    factoryOf(::WeatherItemViewModel)
    factoryOf(::AstroEventItemViewModel)
    factoryOf(::HourlyForecastItemViewModel)
    factoryOf(::OptimalTimeItemViewModel)
    factoryOf(::SubscriptionItemViewModel)

    // Display Models
    factoryOf(::AstroHourlyPredictionDisplayModel)
    factoryOf(::AstroEventDisplayModel)
    factoryOf(::SolarEventDisplayModel)
    factoryOf(::WeatherDataResult)
    factoryOf(::CachedSunCalculation)
    factoryOf(::CachedFeatureAccessResult)
    factoryOf(::FeatureAccessResult)
    factoryOf(::WeatherImpactFactor)
    factoryOf(::ExposureArrays)

    // ==== PLATFORM SERVICES ====
    // Will be added as they are created during migration

    // Photography Services
    // singleOf(::AstronomyCalculationService) { bind<IAstronomyCalculationService>() }
    // singleOf(::ExposureCalculationService) { bind<IExposureCalculationService>() }
    // singleOf(::CameraControlService) { bind<ICameraControlService>() }
    // singleOf(::ImageProcessingService) { bind<IImageProcessingService>() }

    // Platform Services
    // singleOf(::MediaService) { bind<IMediaService>() }
    // singleOf(::GeolocationService) { bind<IGeolocationService>() }
    // singleOf(::PermissionService) { bind<IPermissionService>() }
    // singleOf(::NotificationService) { bind<INotificationService>() }
    // singleOf(::FileService) { bind<IFileService>() }
    // singleOf(::WeatherSyncService) { bind<IWeatherSyncService>() }
    // singleOf(::BackgroundTaskService) { bind<IBackgroundTaskService>() }
    // singleOf(::CacheService) { bind<ICacheService>() }
    // singleOf(::AnalyticsService) { bind<IAnalyticsService>() }
    // singleOf(::CrashReportingService) { bind<ICrashReportingService>() }
    // singleOf(::PreferenceService) { bind<IPreferenceService>() }
    // singleOf(::BiometricService) { bind<IBiometricService>() }
    // singleOf(::HapticFeedbackService) { bind<IHapticFeedbackService>() }
    // singleOf(::ThemeService) { bind<IThemeService>() }
    // singleOf(::LocalizationService) { bind<ILocalizationService>() }
    // singleOf(::AccessibilityService) { bind<IAccessibilityService>() }

    // Mediator
    // singleOf(::Mediator) { bind<IMediator>() }
}