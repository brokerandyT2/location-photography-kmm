// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetForecastByWeatherAndDateQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.WeatherForecast
import kotlinx.datetime.Instant

data class GetForecastByWeatherAndDateQuery(
    val weatherId: Int,
    val date: Instant
)

data class GetForecastByWeatherAndDateQueryResult(
    val forecast: WeatherForecast?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)