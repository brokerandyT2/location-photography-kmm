// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/SunCalculatorService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class SunCalculatorService(
    private val logger: Logger
) : ISunCalculatorService {

    private val calculationCache = mutableMapOf<String, Pair<Any, Instant>>()
    private val cacheMutex = Mutex()
    private val cacheTimeout = 30.minutes

    override fun getSunrise(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "sunrise_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateSunrise(date, latitude, longitude)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getSunriseEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "sunriseend_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val sunrise = getSunrise(date, latitude, longitude, timezone)
        val result = Instant.fromEpochMilliseconds(sunrise.toEpochMilliseconds() + 180000) // Add 3 minutes

        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getSunsetStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "sunsetstart_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val sunset = getSunset(date, latitude, longitude, timezone)
        val result = Instant.fromEpochMilliseconds(sunset.toEpochMilliseconds() - 180000) // Subtract 3 minutes

        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getSunset(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "sunset_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateSunset(date, latitude, longitude)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getSolarNoon(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "solarnoon_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateSolarNoon(date, latitude, longitude)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getNadir(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "nadir_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val solarNoon = getSolarNoon(date, latitude, longitude, timezone)
        val result = Instant.fromEpochMilliseconds(solarNoon.toEpochMilliseconds() + 12 * 60 * 60 * 1000) // Add 12 hours

        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getCivilDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "civildawn_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateCivilTwilight(date, latitude, longitude, true)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getCivilDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "civildusk_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateCivilTwilight(date, latitude, longitude, false)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getNauticalDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "nauticaldawn_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateNauticalTwilight(date, latitude, longitude, true)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getNauticalDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "nauticaldusk_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateNauticalTwilight(date, latitude, longitude, false)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getAstronomicalDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "astronomicaldawn_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateAstronomicalTwilight(date, latitude, longitude, true)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getAstronomicalDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val cacheKey = "astronomicaldusk_${date}_${latitude}_${longitude}"

        val cached = calculationCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return cached.first as Instant
        }

        val result = calculateAstronomicalTwilight(date, latitude, longitude, false)
        calculationCache[cacheKey] = Pair(result, Clock.System.now() + cacheTimeout)
        return result
    }

    override fun getGoldenHourStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val sunset = getSunset(date, latitude, longitude, timezone)
        return Instant.fromEpochMilliseconds(sunset.toEpochMilliseconds() - 60 * 60 * 1000) // 1 hour before sunset
    }

    override fun getGoldenHourEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val sunrise = getSunrise(date, latitude, longitude, timezone)
        return Instant.fromEpochMilliseconds(sunrise.toEpochMilliseconds() + 60 * 60 * 1000) // 1 hour after sunrise
    }

    override fun getBlueHourStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val sunset = getSunset(date, latitude, longitude, timezone)
        return Instant.fromEpochMilliseconds(sunset.toEpochMilliseconds() + 30 * 60 * 1000) // 30 minutes after sunset
    }

    override fun getBlueHourEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant {
        val sunrise = getSunrise(date, latitude, longitude, timezone)
        return Instant.fromEpochMilliseconds(sunrise.toEpochMilliseconds() - 30 * 60 * 1000) // 30 minutes before sunrise
    }

    override fun getSolarAzimuth(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateSolarPosition(dateTime, latitude, longitude).azimuth
    }

    override fun getSolarElevation(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateSolarPosition(dateTime, latitude, longitude).elevation
    }

    override fun getSolarDistance(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return 1.0 // 1 AU (Astronomical Unit) - average Earth-Sun distance
    }

    override fun getSunCondition(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): String {
        val elevation = getSolarElevation(dateTime, latitude, longitude, timezone)
        return when {
            elevation > 6.0 -> "Day"
            elevation > -0.83 -> "Sunset"
            elevation > -6.0 -> "Civil Twilight"
            elevation > -12.0 -> "Nautical Twilight"
            elevation > -18.0 -> "Astronomical Twilight"
            else -> "Night"
        }
    }

    override fun getMoonrise(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant? {
        return calculateMoonrise(date, latitude, longitude)
    }

    override fun getMoonset(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant? {
        return calculateMoonset(date, latitude, longitude)
    }

    override fun getMoonAzimuth(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateMoonPosition(dateTime, latitude, longitude).azimuth
    }

    override fun getMoonElevation(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateMoonPosition(dateTime, latitude, longitude).elevation
    }

    override fun getMoonDistance(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateMoonDistance(dateTime, latitude, longitude)
    }

    override fun getMoonIllumination(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateMoonIllumination(dateTime, latitude, longitude)
    }

    override fun getMoonPhaseAngle(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double {
        return calculateMoonPhaseAngle(dateTime, latitude, longitude)
    }

    override fun getMoonPhaseName(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): String {
        val phaseAngle = getMoonPhaseAngle(dateTime, latitude, longitude, timezone)
        return when {
            phaseAngle < 22.5 -> "New Moon"
            phaseAngle < 67.5 -> "Waxing Crescent"
            phaseAngle < 112.5 -> "First Quarter"
            phaseAngle < 157.5 -> "Waxing Gibbous"
            phaseAngle < 202.5 -> "Full Moon"
            phaseAngle < 247.5 -> "Waning Gibbous"
            phaseAngle < 292.5 -> "Third Quarter"
            phaseAngle < 337.5 -> "Waning Crescent"
            else -> "New Moon"
        }
    }

    override suspend fun getBatchAstronomicalDataAsync(
        date: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String,
        requestedData: List<String>
    ): Map<String, Any> {
        return withContext(Dispatchers.Default) {
            val results = mutableMapOf<String, Any>()

            for (dataType in requestedData) {
                when (dataType.lowercase()) {
                    "sunrise" -> results[dataType] = getSunrise(date, latitude, longitude, timezone)
                    "sunset" -> results[dataType] = getSunset(date, latitude, longitude, timezone)
                    "solarnoon" -> results[dataType] = getSolarNoon(date, latitude, longitude, timezone)
                    "nadir" -> results[dataType] = getNadir(date, latitude, longitude, timezone)
                    "civildawn" -> results[dataType] = getCivilDawn(date, latitude, longitude, timezone)
                    "civildusk" -> results[dataType] = getCivilDusk(date, latitude, longitude, timezone)
                    "nauticaldawn" -> results[dataType] = getNauticalDawn(date, latitude, longitude, timezone)
                    "nauticaldusk" -> results[dataType] = getNauticalDusk(date, latitude, longitude, timezone)
                    "astronomicaldawn" -> results[dataType] = getAstronomicalDawn(date, latitude, longitude, timezone)
                    "astronomicaldusk" -> results[dataType] = getAstronomicalDusk(date, latitude, longitude, timezone)
                    "goldenhourstart" -> results[dataType] = getGoldenHourStart(date, latitude, longitude, timezone)
                    "goldenhourend" -> results[dataType] = getGoldenHourEnd(date, latitude, longitude, timezone)
                    "bluehourstart" -> results[dataType] = getBlueHourStart(date, latitude, longitude, timezone)
                    "bluehourend" -> results[dataType] = getBlueHourEnd(date, latitude, longitude, timezone)
                    "solarazimuth" -> results[dataType] = getSolarAzimuth(date, latitude, longitude, timezone)
                    "solarelevation" -> results[dataType] = getSolarElevation(date, latitude, longitude, timezone)
                    "solardistance" -> results[dataType] = getSolarDistance(date, latitude, longitude, timezone)
                    "suncondition" -> results[dataType] = getSunCondition(date, latitude, longitude, timezone)
                    "moonrise" -> getMoonrise(date, latitude, longitude, timezone)?.let { results[dataType] = it }
                    "moonset" -> getMoonset(date, latitude, longitude, timezone)?.let { results[dataType] = it }
                    "moonazimuth" -> results[dataType] = getMoonAzimuth(date, latitude, longitude, timezone)
                    "moonelevation" -> results[dataType] = getMoonElevation(date, latitude, longitude, timezone)
                    "moondistance" -> results[dataType] = getMoonDistance(date, latitude, longitude, timezone)
                    "moonillumination" -> results[dataType] = getMoonIllumination(date, latitude, longitude, timezone)
                    "moonphaseangle" -> results[dataType] = getMoonPhaseAngle(date, latitude, longitude, timezone)
                    "moonphasename" -> results[dataType] = getMoonPhaseName(date, latitude, longitude, timezone)
                }
            }

            results
        }
    }

    override fun cleanupExpiredCache() {
        val now = Clock.System.now()
        val expiredKeys = calculationCache.filterValues { (_, expiry) -> now >= expiry }.keys
        expiredKeys.forEach { calculationCache.remove(it) }
    }

    override suspend fun preloadAstronomicalCalculationsAsync(
        startDate: Instant,
        endDate: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String
    ) {
        withContext(Dispatchers.Default) {
            var currentDate = startDate
            while (currentDate <= endDate) {
                getSunrise(currentDate, latitude, longitude, timezone)
                getSunset(currentDate, latitude, longitude, timezone)
                getSolarNoon(currentDate, latitude, longitude, timezone)
                currentDate = Instant.fromEpochMilliseconds(currentDate.toEpochMilliseconds() + 24 * 60 * 60 * 1000)
            }
        }
    }

    // Platform-specific calculation methods to be implemented via expect/actual
    private fun calculateSunrise(date: Instant, latitude: Double, longitude: Double): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateSunset(date: Instant, latitude: Double, longitude: Double): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateSolarNoon(date: Instant, latitude: Double, longitude: Double): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateCivilTwilight(date: Instant, latitude: Double, longitude: Double, isDawn: Boolean): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateNauticalTwilight(date: Instant, latitude: Double, longitude: Double, isDawn: Boolean): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateAstronomicalTwilight(date: Instant, latitude: Double, longitude: Double, isDawn: Boolean): Instant {
        // Platform-specific implementation required
        return date
    }

    private fun calculateSolarPosition(dateTime: Instant, latitude: Double, longitude: Double): Position {
        // Platform-specific implementation required
        return Position(0.0, 0.0)
    }

    private fun calculateMoonrise(date: Instant, latitude: Double, longitude: Double): Instant? {
        // Platform-specific implementation required
        return null
    }

    private fun calculateMoonset(date: Instant, latitude: Double, longitude: Double): Instant? {
        // Platform-specific implementation required
        return null
    }

    private fun calculateMoonPosition(dateTime: Instant, latitude: Double, longitude: Double): Position {
        // Platform-specific implementation required
        return Position(0.0, 0.0)
    }

    private fun calculateMoonDistance(dateTime: Instant, latitude: Double, longitude: Double): Double {
        // Platform-specific implementation required
        return 384400.0 // Average Earth-Moon distance in km
    }

    private fun calculateMoonIllumination(dateTime: Instant, latitude: Double, longitude: Double): Double {
        // Platform-specific implementation required
        return 0.5
    }

    private fun calculateMoonPhaseAngle(dateTime: Instant, latitude: Double, longitude: Double): Double {
        // Platform-specific implementation required
        return 0.0
    }

    private data class Position(val azimuth: Double, val elevation: Double)
}