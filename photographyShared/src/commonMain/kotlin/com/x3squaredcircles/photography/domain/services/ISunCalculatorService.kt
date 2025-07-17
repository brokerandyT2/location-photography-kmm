// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ISunCalculatorService.kt
package com.x3squaredcircles.photography.domain.services

import kotlinx.datetime.Instant

interface ISunCalculatorService {

    fun getSunrise(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getSunriseEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getSunsetStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getSunset(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getSolarNoon(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getNadir(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getCivilDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getCivilDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getNauticalDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getNauticalDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getAstronomicalDawn(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getAstronomicalDusk(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getGoldenHourStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getGoldenHourEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getBlueHourStart(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getBlueHourEnd(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant

    fun getSolarAzimuth(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getSolarElevation(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getSolarDistance(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getSunCondition(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): String

    fun getMoonrise(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant?

    fun getMoonset(date: Instant, latitude: Double, longitude: Double, timezone: String): Instant?

    fun getMoonAzimuth(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getMoonElevation(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getMoonDistance(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getMoonIllumination(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getMoonPhaseAngle(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): Double

    fun getMoonPhaseName(dateTime: Instant, latitude: Double, longitude: Double, timezone: String): String

    suspend fun getBatchAstronomicalDataAsync(
        date: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String,
        requestedData: List<String>
    ): Map<String, Any>

    fun cleanupExpiredCache()

    suspend fun preloadAstronomicalCalculationsAsync(
        startDate: Instant,
        endDate: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String
    )
}