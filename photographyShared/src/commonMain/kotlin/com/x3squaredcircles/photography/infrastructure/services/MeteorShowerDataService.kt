// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/MeteorShowerDataService.kt
package com.x3squaredcircles.photography.infrastructure.services

import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.application.common.interfaces.IMeteorShowerDataService

import com.x3squaredcircles.photography.domain.entities.MeteorShower
import com.x3squaredcircles.photography.domain.entities.MeteorShowerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class MeteorShowerDataService(
    private val logger: Logger,
    private val parser: StellariumMeteorShowerParser
) : IMeteorShowerDataService {

    companion object {
        private const val RESOURCE_PATH = "meteor_showers.json"
        private var meteorShowerData: MeteorShowerData? = null
        private var staticLogger: Logger? = null
    }

    init {
        staticLogger = logger
    }

    override suspend fun getActiveShowersAsync(date: LocalDate): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val activeShowers = data.getActiveShowers(date)

            logger.d("Found ${activeShowers.size} active meteor showers for ${date}")

            activeShowers
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting active showers for date $date" }
            emptyList()
        }
    }

    override suspend fun getActiveShowersAsync(date: LocalDate, minZHR: Int): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val activeShowers = data.getActiveShowers(date, minZHR)

            logger.d("Found ${activeShowers.size} active meteor showers for $date with ZHR >= $minZHR")

            activeShowers
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting active showers for date $date with ZHR >= $minZHR" }
            emptyList()
        }
    }

    override suspend fun getShowerByCodeAsync(code: String): MeteorShower? {
        return try {
            if (code.isBlank()) {
                logger.w("getShowerByCodeAsync called with null or empty code")
                return null
            }

            val data = getMeteorShowerDataAsync()
            val shower = data.getShowerByCode(code)

            if (shower != null) {
                logger.d("Found meteor shower: $code - ${shower.designation}")
            } else {
                logger.d("Meteor shower not found for code: $code")
            }

            shower
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting shower by code $code" }
            null
        }
    }

    override suspend fun getAllShowersAsync(): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            logger.d("Retrieved ${data.showers.size} total meteor showers")
            data.showers
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting all showers" }
            emptyList()
        }
    }

    override suspend fun getPeakShowersAsync(date: LocalDate): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val peakShowers = data.getPeakShowers(date)

            logger.d("Found ${peakShowers.size} peak meteor showers for $date")

            peakShowers
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting peak showers for date $date" }
            emptyList()
        }
    }

    private suspend fun getMeteorShowerDataAsync(): MeteorShowerData {
        return withContext(Dispatchers.Default) {
            meteorShowerData ?: run {
                val newData = loadMeteorShowerData()
                meteorShowerData = newData
                newData
            }
        }
    }

    private fun loadMeteorShowerData(): MeteorShowerData {
        return try {
            val jsonContent = loadResourceAsString(RESOURCE_PATH)

            if (jsonContent.isBlank()) {
                staticLogger?.e("Meteor shower resource file is empty or contains only whitespace")
                return MeteorShowerData()
            }

            val data = parser.parseStellariumData(jsonContent)

            staticLogger?.i("Successfully loaded ${data.showers.size} meteor showers from embedded resource")

            data
        } catch (ex: Exception) {
            staticLogger?.e(ex) { "Critical error loading meteor shower data from embedded resource" }
            MeteorShowerData()
        }
    }

    private fun loadResourceAsString(resourcePath: String): String {
        return try {
            val classLoader = this::class.java.classLoader
            val inputStream = classLoader?.getResourceAsStream(resourcePath)
                ?: throw IllegalStateException("Resource not found: $resourcePath")

            inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            staticLogger?.e(ex) { "Failed to load resource: $resourcePath" }
            ""
        }
    }
}