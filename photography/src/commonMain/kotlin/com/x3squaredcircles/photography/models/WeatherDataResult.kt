// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/WeatherDataResult.kt
package com.x3squaredcircles.photography.models

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.photography.dtos.WeatherForecastDto
import com.x3squaredcircles.photography.domain.models.WeatherImpactAnalysis
import kotlinx.datetime.Instant

data class WeatherDataResult(
    val isSuccess: Boolean = false,
    val weather: Weather? = null,
    val weatherForecast: WeatherForecastDto? = null,
    val source: String = "",
    val errorMessage: String? = null,
    val weatherImpact: WeatherImpactAnalysis = WeatherImpactAnalysis(),
    val timestamp: Instant
)