// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/datapopulation/DatabaseInitializer.kt
package com.x3squaredcircles.photography.infrastructure.datapopulation

import co.touchlab.kermit.Logger


import com.x3squaredcircles.core.domain.entities.Setting
import com.x3squaredcircles.core.domain.entities.TipType
import com.x3squaredcircles.core.domain.entities.Tip
import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.services.IAlertService
import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

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
            if (markerResult != null) {
                isInitialized = true
                logger.d("Database initialization marker found")
                return true
            }

            val locationsCount = unitOfWork.locations.getTotalCountAsync()
            val hasData = locationsCount > 0

            if (hasData) {
                createInitializationMarkerAsync()
                isInitialized = true
                logger.i("Database has data but missing marker - marker added")
                return true
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

            val result = unitOfWork.settings.addAsync(marker)

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
                val result = unitOfWork.settings.addAsync(setting)

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
                "Landscape", "Silhouette", "Building", "Person", "Baby", "Animals",
                "BlurryWater", "Night", "BlueHour", "GoldenHour", "Sunset"
            )
            //TODO: fix this to use JSON
            val batchSize = 3
            for (i in tipTypeNames.indices step batchSize) {
                val batch = tipTypeNames.sliceArray(i until minOf(i + batchSize, tipTypeNames.size))

                for (name in batch) {
                    val tipType = TipType.create(name)
                    tipType.setLocalization("en-US")

                    val typeResult = unitOfWork.tipTypes.addAsync(tipType)


                    val tip = Tip.create(
                        tipTypeId = typeResult.id,
                        title = "How to take great $name photos",
                        content = "Placeholder content for $name photography tips"
                    )
                    tip.updatePhotographySettings("f/1", "1/125", "50")
                    tip.setLocalization("en-US")

                    val tipResult = unitOfWork.tips.addAsync(tip)

                }
            }

            logger.i("Created ${tipTypeNames.size} tip types with sample tips")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating tip types" }
            throw ex
        }
    }

    private suspend fun createSampleLocationsAsync() {
        try {
            val sampleLocations = listOf(
                LocationData(
                    "Soldiers and Sailors Monument",
                    "Located in the heart of downtown in Monument Circle, it was originally designed to honor Indiana's Civil War veterans.",
                    39.7685, -86.1580, "s_and_sm_new.jpg"
                ),
                LocationData(
                    "The Bean",
                    "What is The Bean? The Bean is a work of public art in the heart of Chicago. The sculpture, which is officially titled Cloud Gate, is one of the world's largest permanent outdoor art installations.",
                    41.8827, -87.6233, "chicagobean.jpg"
                ),
                LocationData(
                    "Golden Gate Bridge",
                    "The Golden Gate Bridge is a suspension bridge spanning the Golden Gate strait, the one-mile-wide (1.6 km) channel between San Francisco Bay and the Pacific Ocean.",
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

                val result = unitOfWork.locations.addAsync(location)

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
                    val result = unitOfWork.settings.addAsync(setting)

                }
            }

            logger.i("Created ${baseSettings.size} base settings")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating base settings" }
            throw ex
        }
    }

    private suspend fun createCameraSensorProfilesAsync() {
        try {
            val cameraProfiles = listOf(
                CameraProfileData("Canon EOS 550D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D3100", "Nikon", "Crop", 23.1, 15.4),
                CameraProfileData("Canon EOS 7D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D7000", "Nikon", "Crop", 23.6, 15.6),
                CameraProfileData("Sony Alpha A500", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-x", "Pentax", "Crop", 23.6, 15.8),
                CameraProfileData("Canon EOS 5D Mark II", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D3s", "Nikon", "Full Frame", 36.0, 23.9),
                CameraProfileData("Sony Alpha A850", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Pentax K-7", "Pentax", "Crop", 23.4, 15.6),
                CameraProfileData("Canon EOS 1000D", "Canon", "Crop", 22.2, 14.8),
                CameraProfileData("Nikon D90", "Nikon", "Crop", 23.6, 15.8),
                CameraProfileData("Sony Alpha A230", "Sony", "Crop", 23.5, 15.7),
                CameraProfileData("Pentax K20D", "Pentax", "Crop", 23.4, 15.6),
                CameraProfileData("Canon EOS 50D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS 600D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D5100", "Nikon", "Crop", 23.6, 15.6),
                CameraProfileData("Sony Alpha A35", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-5", "Pentax", "Crop", 23.7, 15.7),
                CameraProfileData("Canon EOS 1100D", "Canon", "Crop", 22.2, 14.7),
                CameraProfileData("Sony Alpha A55", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-r", "Pentax", "Crop", 23.6, 15.8),
                CameraProfileData("Sony Alpha A900", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Pentax 645D", "Pentax", "Medium Format", 44.0, 33.0),
                CameraProfileData("Canon EOS 60D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Sony Alpha A290", "Sony", "Crop", 23.5, 15.7),
                CameraProfileData("Canon EOS 650D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D5200", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 6D", "Canon", "Full Frame", 35.8, 23.9),
                CameraProfileData("Nikon D600", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon D800", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 5D Mark III", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D3200", "Nikon", "Crop", 23.2, 15.4),
                CameraProfileData("Pentax K-30", "Pentax", "Crop", 23.7, 15.7),
                CameraProfileData("Sony Alpha A57", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A65", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-01", "Pentax", "Crop", 23.7, 15.7),
                CameraProfileData("Canon EOS 1D X", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sony Alpha A99", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 700D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D7100", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 100D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D5300", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A58", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-3", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon D610", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 70D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D800E", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A77 II", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 1200D", "Canon", "Crop", 22.2, 14.8),
                CameraProfileData("Pentax K-50", "Pentax", "Crop", 23.7, 15.7),
                CameraProfileData("Nikon D3300", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A7", "Sony", "Full Frame", 35.8, 23.9),
                CameraProfileData("Sony Alpha A7R", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 750D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D750", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 760D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D810", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 7D Mark II", "Canon", "Crop", 22.4, 15.0),
                CameraProfileData("Sony Alpha A7S", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Sony Alpha A7 II", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 5DS", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D4S", "Nikon", "Full Frame", 36.0, 23.9),
                CameraProfileData("Nikon D5500", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A3000", "Sony", "Crop", 23.2, 15.4),
                CameraProfileData("Pentax K-S1", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-T1", "Fujifilm", "Crop", 23.6, 15.6),
                CameraProfileData("Panasonic Lumix DMC-GH4", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Olympus OM-D E-M1", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Canon EOS 5DS R", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D7200", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-3 II", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A68", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 1300D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Pentax K-S2", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon D5", "Nikon", "Full Frame", 35.9, 23.9),
                CameraProfileData("Sony Alpha A7R II", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7S II", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Fujifilm X-T10", "Fujifilm", "Crop", 23.6, 15.6),
                CameraProfileData("Panasonic Lumix DMC-G7", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Olympus OM-D E-M5 Mark II", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Canon EOS M3", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Samsung NX1", "Samsung", "Crop", 23.5, 15.7),
                CameraProfileData("Leica Q", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS 80D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS 5D Mark IV", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D500", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-1", "Pentax", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS 1D X Mark II", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon D3400", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Pentax K-70", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A6300", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-Pro2", "Fujifilm", "Crop", 23.6, 15.6),
                CameraProfileData("Fujifilm X-T2", "Fujifilm", "Crop", 23.6, 15.6),
                CameraProfileData("Panasonic Lumix DMC-GX85", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Olympus OM-D E-M1 Mark II", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Canon EOS M5", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Sony Alpha A99 II", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Hasselblad X1D", "Hasselblad", "Medium Format", 43.8, 32.9),
                CameraProfileData("Nikon D850", "Nikon", "Full Frame", 35.9, 23.9),
                CameraProfileData("Canon EOS 77D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS 800D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D7500", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 6D Mark II", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Pentax KP", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon D5600", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 200D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Sony Alpha A9", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Sony Alpha A7R III", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A6500", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-T20", "Fujifilm", "Crop", 23.6, 15.6),
                CameraProfileData("Panasonic Lumix DC-GH5", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Canon EOS M6", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Leica SL", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS 2000D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS 4000D", "Canon", "Crop", 22.2, 14.8),
                CameraProfileData("Nikon D3500", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS 1500D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Pentax K-1 Mark II", "Pentax", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7 III", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Fujifilm X-T3", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-H1", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A6400", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS R", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon Z7", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z6", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Panasonic Lumix DC-S1R", "Panasonic", "Full Frame", 36.0, 24.0),
                CameraProfileData("Panasonic Lumix DC-S1", "Panasonic", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS M50", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS 90D", "Canon", "Crop", 22.3, 14.8),
                CameraProfileData("Canon EOS 250D", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Nikon D780", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7R IV", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A6600", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A6100", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-T30", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS RP", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z50", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("Panasonic Lumix DC-G90", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Olympus OM-D E-M1X", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Sigma fp", "Sigma", "Full Frame", 35.9, 23.9),
                CameraProfileData("Canon EOS M6 Mark II", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Fujifilm X-Pro3", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Leica SL2", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS R5", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Canon EOS R6", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7S III", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Fujifilm X-T4", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-S10", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon Z5", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Panasonic Lumix DC-S5", "Panasonic", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sony Alpha A7C", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS M50 Mark II", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Panasonic Lumix DC-GH5S", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Olympus OM-D E-M5 Mark III", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Fujifilm X100V", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Leica Q2", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sigma fp L", "Sigma", "Full Frame", 35.9, 23.9),
                CameraProfileData("Pentax K-3 Mark III", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A7 IV", "Sony", "Full Frame", 35.9, 23.9),
                CameraProfileData("Sony Alpha A1", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS R3", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Nikon Z9", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Fujifilm X-E4", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Panasonic Lumix DC-GH6", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Canon EOS R7", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Canon EOS R10", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Olympus OM-D E-M1 Mark III", "Olympus", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Fujifilm X-T30 II", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha FX3", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Leica M11", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Hasselblad X1D II 50C", "Hasselblad", "Medium Format", 43.8, 32.9),
                CameraProfileData("Phase One XF IQ4", "Phase One", "Medium Format", 53.4, 40.0),
                CameraProfileData("Canon EOS R5 C", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sony Alpha A7R V", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Fujifilm X-H2S", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Fujifilm X-H2", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS R6 Mark II", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z30", "Nikon", "Crop", 23.5, 15.6),
                CameraProfileData("OM System OM-1", "OM System", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Fujifilm X-T5", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon Z6 II", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z7 II", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Panasonic Lumix DC-S5 II", "Panasonic", "Full Frame", 36.0, 24.0),
                CameraProfileData("Leica M11 Monochrom", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Pentax K-3 Mark III Monochrome", "Pentax", "Crop", 23.5, 15.6),
                CameraProfileData("Sony Alpha A7C II", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7C R", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS R8", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Canon EOS R50", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Fujifilm X-S20", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon Z8", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("OM System OM-5", "OM System", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Panasonic Lumix DC-G9 II", "Panasonic", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Sony Alpha A6700", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS R100", "Canon", "Crop", 22.3, 14.9),
                CameraProfileData("Fujifilm X100VI", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Nikon Zf", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Leica Q3", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Hasselblad X2D 100C", "Hasselblad", "Medium Format", 43.8, 32.9),
                CameraProfileData("Phase One XT", "Phase One", "Medium Format", 53.4, 40.0),
                CameraProfileData("Canon EOS R5 Mark II", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sony Alpha A9 III", "Sony", "Full Frame", 35.6, 23.8),
                CameraProfileData("Nikon Z6 III", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Fujifilm X-T50", "Fujifilm", "Crop", 23.5, 15.6),
                CameraProfileData("Canon EOS R1", "Canon", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sony Alpha FX30", "Sony", "Crop", 23.5, 15.6),
                CameraProfileData("Panasonic Lumix DC-S9", "Panasonic", "Full Frame", 36.0, 24.0),
                CameraProfileData("OM System OM-1 Mark II", "OM System", "Micro Four Thirds", 17.3, 13.0),
                CameraProfileData("Fujifilm GFX100S II", "Fujifilm", "Medium Format", 43.8, 32.9),
                CameraProfileData("Canon EOS R6 Mark III", "Canon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Nikon Z5 II", "Nikon", "Full Frame", 35.9, 24.0),
                CameraProfileData("Sony Alpha A7 V", "Sony", "Full Frame", 35.9, 24.0),
                CameraProfileData("Leica SL3", "Leica", "Full Frame", 36.0, 24.0),
                CameraProfileData("Sigma fp II", "Sigma", "Full Frame", 35.9, 23.9),
                CameraProfileData("Pentax KF", "Pentax", "Crop", 23.5, 15.6)
            )

            val batchSize = 10
            for (i in cameraProfiles.indices step batchSize) {
                val batch = cameraProfiles.subList(i, minOf(i + batchSize, cameraProfiles.size))

                for (cameraData in batch) {
                    val mountType = determineMountType(cameraData.brand, cameraData.name)
                    val cameraBody = CameraBodyDto(
                        name = cameraData.name,
                        sensorType = cameraData.sensorType,
                        sensorWidth = cameraData.sensorWidth,
                        sensorHeight = cameraData.sensorHeight,
                        mountType = mountType.toString(),
                        id=0,
                        dateAdded = Clock.System.now().toEpochMilliseconds(),
                        isUserCreated = false
                    )

                    val result = unitOfWork.cameraBodies.addAsync(cameraBody)

                }
            }

            logger.i("Created ${cameraProfiles.size} camera sensor profiles")
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating camera sensor profiles" }
            throw ex
        }
    }

    private fun determineMountType(brand: String, cameraName: String): MountType {
        val brandLower = brand.lowercase()
        val cameraNameLower = cameraName.lowercase()

        return when (brandLower) {
            "canon" -> when {
                cameraNameLower.contains("eos r") -> MountType.CANON_RF
                cameraNameLower.contains("eos m") -> MountType.CANON_EFM
                else -> MountType.CANON_EF
            }
            "nikon" -> when {
                cameraNameLower.contains(" z") -> MountType.NIKON_Z
                else -> MountType.NIKON_F
            }
            "sony" -> when {
                cameraNameLower.contains("fx") || cameraNameLower.contains("a7") -> MountType.SONY_FE
                else -> MountType.SONY_E
            }
            "pentax" -> MountType.PENTAX_K
            else -> MountType.OTHER
        }
    }



    private data class LocationData(
        val title: String,
        val description: String,
        val latitude: Double,
        val longitude: Double,
        val photo: String
    )

    private data class CameraProfileData(
        val name: String,
        val brand: String,
        val sensorType: String,
        val sensorWidth: Double,
        val sensorHeight: Double
    )
}