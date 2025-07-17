// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/WeatherImpactAnalysisRequest.kt
package com.x3squaredcircles.photography.domain.models

data class WeatherImpactAnalysisRequest(
    val weatherForecast: WeatherForecast? = null,
    val sunTimes: EnhancedSunTimes? = null,
    val locationId: Int = 0
)

data class WeatherForecast(
    val dailyForecasts: List<DailyForecast> = emptyList(),
    val hourlyForecasts: List<HourlyForecast> = emptyList()
)

data class DailyForecast(
    val date: String = "",
    val highTemperature: Double = 0.0,
    val lowTemperature: Double = 0.0,
    val cloudCover: Double = 0.0,
    val precipitationChance: Double = 0.0,
    val windSpeed: Double = 0.0,
    val humidity: Double = 0.0,
    val uvIndex: Double = 0.0,
    val visibility: Double = 0.0,
    val description: String = ""
)

data class HourlyForecast(
    val dateTime: String = "",
    val temperature: Double = 0.0,
    val cloudCover: Double = 0.0,
    val precipitationChance: Double = 0.0,
    val windSpeed: Double = 0.0,
    val humidity: Double = 0.0,
    val uvIndex: Double = 0.0,
    val visibility: Double = 0.0,
    val description: String = ""
)