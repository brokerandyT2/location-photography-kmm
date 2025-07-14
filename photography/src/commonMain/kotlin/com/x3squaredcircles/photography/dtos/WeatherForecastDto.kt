// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/WeatherForecastDto.kt
package com.x3squaredcircles.photography.dtos
data class WeatherForecastDto(
    val timezone: String = "",
    val timezoneOffset: Int = 0,
    val lastUpdate: Long = 0L,
    val dailyForecasts: List<DailyForecastDto> = emptyList()
)