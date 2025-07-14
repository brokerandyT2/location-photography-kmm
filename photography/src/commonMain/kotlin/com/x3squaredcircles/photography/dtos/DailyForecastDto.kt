// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/DailyForecastDto.kt
package com.x3squaredcircles.photography.dtos
data class DailyForecastDto(
    val date: Long = 0L,
    val sunrise: Long = 0L,
    val sunset: Long = 0L,
    val temperature: Double = 0.0,
    val minTemperature: Double = 0.0,
    val maxTemperature: Double = 0.0,
    val description: String = "",
    val icon: String = "",
    val windSpeed: Double = 0.0,
    val windDirection: Double = 0.0,
    val windGust: Double? = null,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val clouds: Int = 0,
    val uvIndex: Double = 0.0,
    val precipitation: Double? = null,
    val moonRise: Long? = null,
    val moonSet: Long? = null,
    val moonPhase: Double = 0.0
)