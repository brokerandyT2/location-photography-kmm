// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/HourlyForecastDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.datetime.Instant

data class HourlyForecastDto(
    val dateTime: Instant,
    val temperature: Double = 0.0,
    val feelsLike: Double = 0.0,
    val description: String = "",
    val icon: String = "",
    val windSpeed: Double = 0.0,
    val windDirection: Double = 0.0,
    val windGust: Double? = null,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val clouds: Int = 0,
    val uvIndex: Double = 0.0,
    val probabilityOfPrecipitation: Double = 0.0,
    val visibility: Double = 0.0,
    val dewPoint: Double = 0.0
)