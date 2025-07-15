// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetHourlyForecastsByWeatherAndTimeRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.HourlyForecast
import kotlinx.datetime.Instant

data class GetHourlyForecastsByWeatherAndTimeRangeQuery(
    val weatherId: Int,
    val startTime: Instant,
    val endTime: Instant
)

data class GetHourlyForecastsByWeatherAndTimeRangeQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)