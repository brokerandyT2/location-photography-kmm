// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetForecastsByWeatherAndDateRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.WeatherForecast
import kotlinx.datetime.Instant

data class GetForecastsByWeatherAndDateRangeQuery(
    val weatherId: Int,
    val startDate: Instant,
    val endDate: Instant
)

data class GetForecastsByWeatherAndDateRangeQueryResult(
    val forecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)