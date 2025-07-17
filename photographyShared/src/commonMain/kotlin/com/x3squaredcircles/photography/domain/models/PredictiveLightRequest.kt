// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PredictiveLightRequest.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class PredictiveLightRequest(
    val locationId: Int,
    val targetDate: Instant,
    val latitude: Double,
    val longitude: Double,
    val predictionWindowHours: Int,
    val weatherImpact: WeatherImpactAnalysis? = null,
    val sunTimes: EnhancedSunTimes? = null,
    val lastCalibrationReading: Double? = null
)

data class EnhancedSunTimes(
    val sunrise: Instant,
    val sunset: Instant,
    val solarNoon: Instant,
    val civilDawn: Instant,
    val civilDusk: Instant,
    val nauticalDawn: Instant,
    val nauticalDusk: Instant,
    val astronomicalDawn: Instant,
    val astronomicalDusk: Instant,
    val goldenHourStart: Instant,
    val goldenHourEnd: Instant,
    val blueHourStart: Instant,
    val blueHourEnd: Instant
)