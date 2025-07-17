// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/WeatherImpactAnalysis.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class WeatherImpactAnalysis(
    val currentConditions: WeatherConditions? = null,
    val hourlyImpacts: List<HourlyWeatherImpact> = emptyList(),
    val overallLightReductionFactor: Double = 1.0,
    val summary: String = "",
    val alerts: List<String> = emptyList()
)

data class WeatherConditions(
    val cloudCover: Double = 0.0,
    val visibility: Double = 0.0,
    val humidity: Double = 0.0,
    val uvIndex: Double = 0.0,
    val temperature: Double = 0.0,
    val windSpeed: Double = 0.0,
    val precipitation: Double = 0.0,
    val description: String = ""
)

data class HourlyWeatherImpact(
    val dateTime: Instant,
    val lightReductionFactor: Double,
    val conditions: WeatherConditions,
    val reasoning: String = ""
)