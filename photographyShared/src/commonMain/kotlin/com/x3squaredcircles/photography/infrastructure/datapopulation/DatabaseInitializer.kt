// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/datapopulation/DatabaseInitializer.kt
package com.x3squaredcircles.photography.infrastructure.datapopulation

import co.touchlab.kermit.Logger
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.core.domain.entities.Setting
import com.x3squaredcircles.core.domain.entities.TipType
import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.services.IAlertService
import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class DatabaseInitializer(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger,
    private val alertService: IAlertService
) {
    companion object {
        private val initializationLock = Mutex()
        private var isInitialized = false
        private var isInitializing = false
        private var initializationTimestamp: Instant? = null
    }

    suspend fun isDatabaseInitializedAsync(): Boolean {
        return try {
            if (isInitialized) {
                logger.d("Database already initialized (static flag)")
                return true
            }

            if (!databaseFileExists()) {
                logger.d("Database file does not exist")
                return false
            }

            val markerResult = unitOfWork.settings.getByKeyAsync("DatabaseInitialized")
            when (markerResult) {
                is Result.Success -> {
                    if (markerResult.data != null) {
                        isInitialized = true
                        logger.d("Database initialization marker found")
                        return true
                    }
                }
                is Result.Failure -> {
                    logger.e { "Error checking initialization marker: ${markerResult.error}" }
                }
            }

            when (val locationsCountResult = unitOfWork.locations.getTotalCountAsync()) {
                is Result.Success -> {
                    val hasData = locationsCountResult.data > 0

                    if (hasData) {
                        createInitializationMarkerAsync()
                        isInitialized = true
                        logger.i("Database has data but missing marker - marker added")
                        return true
                    }
                }
                is Result.Failure -> {
                    logger.e { "Error checking locations count: ${locationsCountResult.error}" }
                }
            }

            logger.d("Database exists but is not initialized")
            false
        } catch (ex: Exception) {
            logger.e(ex) { "Error checking database initialization status" }
            false
        }
    }

    suspend fun initializeDatabaseWithStaticDataAsync() {
        if (isInitialized) {
            logger.d("Database already initialized, skipping static data initialization")
            return
        }

        if (isInitializing) {
            logger.d("Database initialization already in progress, waiting...")
            return
        }

        initializationLock.withLock {
            if (isInitialized) {
                logger.d("Database was initialized while waiting for lock")
                return
            }

            if (isInitializing) {
                logger.d("Database initialization already in progress")
                return
            }

            try {
                isInitializing = true
                initializationTimestamp = Clock.System.now()

                logger.i("Starting database initialization with static data...")

                createTipTypesAsync()
                createSampleLocationsAsync()
                createBaseSettingsAsync()
                createCameraSensorProfilesAsync()
                createInitializationMarkerAsync()

                isInitialized = true
                isInitializing = false

                val duration = Clock.System.now() - (initializationTimestamp ?: Clock.System.now())
                logger.i("Database initialization with static data completed successfully in ${duration.inWholeMilliseconds}ms")
            } catch (ex: Exception) {
                isInitializing = false
                logger.e(ex) { "Error during database initialization with static data" }
                throw ex
            }
        }
    }

    private suspend fun createInitializationMarkerAsync() {
        try {
            val marker = Setting.create(
                key = "DatabaseInitialized",
                value = Clock.System.now().toString(),
                description = "Timestamp when database initialization was completed"
            )

            val result = unitOfWork.settings.createAsync(marker)
            when (result) {
                is Result.Success -> {
                    logger.d("Database initialization marker created successfully")
                }
                is Result.Failure -> {
                    logger.w { "Failed to create initialization marker: ${result.error}" }
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating database initialization marker" }
        }
    }

    suspend fun createUserSettingsAsync(
        hemisphere: String,
        tempFormat: String,
        dateFormat: String,
        timeFormat: String,
        windDirection: String,
        email: String,
        guid: String
    ) {
        try {
            logger.i("Creating user-specific settings...")

            val userSettings = listOf(
                Setting.create("Hemisphere", hemisphere, "User's hemisphere (north/south)"),
                Setting.create("WindDirection", windDirection, "Wind direction setting (towardsWind/withWind)"),
                Setting.create("TimeFormat", timeFormat, "Time format (12h/24h)"),
                Setting.create("DateFormat", dateFormat, "Date format (US/International)"),
                Setting.create("TemperatureType", tempFormat, "Temperature format (F/C)"),
                Setting.create("Email", email, "User's email address"),
                Setting.create("UniqueID", guid, "Unique identifier for the installation")
            )

            for (setting in userSettings) {
                val result = unitOfWork.settings.createAsync(setting)
                when (result) {
                    is Result.Success -> {
                        logger.d { "Created user setting: ${setting.key}" }
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to create user setting ${setting.key}: ${result.error}" }
                    }
                }
            }

            logger.i("User settings created successfully")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating user-specific settings" }
            throw ex
        }
    }

    suspend fun initializeDatabaseAsync(
        hemisphere: String = "north",
        tempFormat: String = "F",
        dateFormat: String = "MMM/dd/yyyy",
        timeFormat: String = "hh:mm tt",
        windDirection: String = "towardsWind",
        email: String = "",
        guid: String = ""
    ) {
        try {
            initializeDatabaseWithStaticDataAsync()
            createUserSettingsAsync(hemisphere, tempFormat, dateFormat, timeFormat, windDirection, email, guid)
            logger.i("Complete database initialization completed successfully")
        } catch (ex: Exception) {
            logger.e(ex) { "Error during complete database initialization" }
            alertService.showErrorAlertAsync("Failed to initialize database: ${ex.message}", "Error")
            throw ex
        }
    }

    private fun databaseFileExists(): Boolean {
        return true
    }

    private suspend fun createTipTypesAsync() {
        try {
            val tipTypeNames = arrayOf(
                "Landscape", "Silhouette", "Portrait", "Macro", "Architecture", "Street", "Wildlife", "Astrophotography", "Sports", "Event"
            )

            for (tipTypeName in tipTypeNames) {
                val tipType = TipType.create(tipTypeName)
                val result = unitOfWork.tipTypes.createAsync(tipType)
                when (result) {
                    is Result.Success -> {
                        logger.d { "Created tip type: $tipTypeName" }
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to create tip type $tipTypeName: ${result.error}" }
                    }
                }
            }

            logger.i("Created ${tipTypeNames.size} tip types")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating tip types" }
            throw ex
        }
    }

    private data class LocationData(
        val title: String,
        val description: String,
        val latitude: Double,
        val longitude: Double,
        val photo: String = ""
    )

    private suspend fun createSampleLocationsAsync() {
        try {
            val sampleLocations = listOf(
                LocationData(
                    "Golden Gate Bridge",
                    "The Golden Gate Bridge is a suspension bridge spanning the Golden Gate, the one-mile-wide strait connecting San Francisco Bay and the Pacific Ocean.",
                    37.8199, -122.4783, "ggbridge.jpg"
                ),
                LocationData(
                    "Gateway Arch",
                    "The Gateway Arch is a 630-foot (192 m) monument in St. Louis, Missouri, that commemorates Thomas Jefferson and the westward expansion of the United States.",
                    38.6247, -90.1848, "stlarch.jpg"
                )
            )

            for (locationData in sampleLocations) {
                val coordinate = Coordinate.create(locationData.latitude, locationData.longitude)
                val address = Address("", "")
                val location = Location.create(
                    title = locationData.title,
                    description = locationData.description,
                    coordinate = coordinate,
                    address = address
                )

                if (locationData.photo.isNotEmpty()) {
                    location.attachPhoto(locationData.photo)
                }

                val result = unitOfWork.locations.createAsync(location)
                when (result) {
                    is Result.Success -> {
                        logger.d { "Created location: ${locationData.title}" }
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to create location ${locationData.title}: ${result.error}" }
                    }
                }
            }

            logger.i("Created ${sampleLocations.size} sample locations")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating sample locations" }
            throw ex
        }
    }

    private suspend fun createBaseSettingsAsync() {
        try {
            val now = Clock.System.now().toString()
            val yesterdayStr = Clock.System.now().toString() // Simplified for now

            val baseSettings = listOf(
                Setting.create("LastBulkWeatherUpdate", yesterdayStr, "Timestamp of last bulk weather update"),
                Setting.create("DefaultLanguage", "en-US", "Default language setting"),
                Setting.create("CameraRefresh", "500", "Camera refresh rate in milliseconds"),
                Setting.create("AppOpenCounter", "1", "Number of times the app has been opened"),
                Setting.create("WeatherURL", "https://api.openweathermap.org/data/3.0/onecall", "Weather API URL"),
                Setting.create("Weather_API_Key", "aa24f449cced50c0491032b2f955d610", "Weather API key"),
                Setting.create("FreePremiumAdSupported", "false", "Whether the app is running in ad-supported mode"),
                Setting.create("SettingsViewed", "false", "Whether the settings page has been viewed"),
                Setting.create("HomePageViewed", "false", "Whether the home page has been viewed"),
                Setting.create("LocationListViewed", "false", "Whether the location list has been viewed"),
                Setting.create("TipsViewed", "false", "Whether the tips page has been viewed"),
                Setting.create("ExposureCalcViewed", "false", "Whether the exposure calculator has been viewed"),
                Setting.create("LightMeterViewed", "false", "Whether the light meter has been viewed"),
                Setting.create("SceneEvaluationViewed", "false", "Whether the scene evaluation has been viewed"),
                Setting.create("AddLocationViewed", "false", "Whether the add location page has been viewed"),
                Setting.create("WeatherDisplayViewed", "false", "Whether the weather display has been viewed"),
                Setting.create("SunCalculatorViewed", "false", "Whether the sun calculator has been viewed"),
                Setting.create("SunLocationViewed", "false", "Whether the SunLocation Page has been viewed"),
                Setting.create("ExposureCalcAdViewed_TimeStamp", yesterdayStr, "Timestamp of last exposure calculator ad view"),
                Setting.create("LightMeterAdViewed_TimeStamp", yesterdayStr, "Timestamp of last light meter ad view"),
                Setting.create("SceneEvaluationAdViewed_TimeStamp", yesterdayStr, "Timestamp of last scene evaluation ad view"),
                Setting.create("SunCalculatorViewed_TimeStamp", yesterdayStr, "Timestamp of last sun calculator ad view"),
                Setting.create("SunLocationAdViewed_TimeStamp", yesterdayStr, "Timestamp of last sun location ad view"),
                Setting.create("WeatherDisplayAdViewed_TimeStamp", yesterdayStr, "Timestamp of last weather display ad view"),
                Setting.create("SubscriptionType", "Free", "Subscription type (Free/Premium)"),
                Setting.create("SubscriptionExpiration", yesterdayStr, "Subscription expiration date"),
                Setting.create("SubscriptionProductId", "", "Subscription product ID"),
                Setting.create("SubscriptionPurchaseDate", "", "Subscription purchase date"),
                Setting.create("SubscriptionTransactionId", "", "Subscription transaction ID"),
                Setting.create("AdGivesHours", "12", "Hours of premium access granted per ad view"),
                Setting.create("LastUploadTimeStamp", yesterdayStr, "Last Time that data was backed up to cloud")
            )

            val batchSize = 10
            for (i in baseSettings.indices step batchSize) {
                val batch = baseSettings.subList(i, minOf(i + batchSize, baseSettings.size))

                for (setting in batch) {
                    val result = unitOfWork.settings.createAsync(setting)
                    when (result) {
                        is Result.Success -> {
                            logger.d { "Created base setting: ${setting.key}" }
                        }
                        is Result.Failure -> {
                            logger.w { "Failed to create base setting ${setting.key}: ${result.error}" }
                        }
                    }
                }
            }

            logger.i("Created ${baseSettings.size} base settings")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating base settings" }
            throw ex
        }
    }

    private data class CameraProfileData(
        val name: String,
        val brand: String,
        val sensorType: String,
        val sensorWidth: Double,
        val sensorHeight: Double
    )

    private suspend fun createCameraSensorProfilesAsync() {
        try {
            val cameraProfiles = listOf(
                CameraProfileData("Canon EOS 5D Mark IV", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS R", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 90D", "Canon", "Crop", 22.3, 14.8),
                CameraProfileData("Nikon D850", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z7", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7R IV", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A6600", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-T4", "Fujifilm", "Crop", 23.5, 15.6)
            )

            for (cameraData in cameraProfiles) {
                val cameraBodyDto = CameraBodyDto(
                    id = 0,
                    name = cameraData.name,
                    sensorType = cameraData.sensorType,
                    sensorWidth = cameraData.sensorWidth,
                    sensorHeight = cameraData.sensorHeight,
                    mountType = MountType.NIKON_F.toString(),
                    isUserCreated = false,
                    dateAdded = Clock.System.now().toEpochMilliseconds(),
                    displayName = cameraData.name
                )

                val result = unitOfWork.cameraBodies.createAsync(cameraBodyDto)
                when (result) {
                    is Result.Success -> {
                        logger.d { "Created camera profile: ${cameraData.name}" }
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to create camera profile ${cameraData.name}: ${result.error}" }
                    }
                }
            }

            logger.i("Created ${cameraProfiles.size} camera sensor profiles")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating camera sensor profiles" }
            throw ex
        }
    }
}