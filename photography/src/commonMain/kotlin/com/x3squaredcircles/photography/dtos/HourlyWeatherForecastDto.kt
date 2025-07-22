// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/HourlyWeatherForecastDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.datetime.Instant

data class HourlyWeatherForecastDto(
    val weatherId: Int = 0,
    val lastUpdate: Instant,
    val timezone: String = "",
    val timezoneOffset: Int = 0,
    val hourlyForecasts: List<HourlyForecastDto> = emptyList()
)