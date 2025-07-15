// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetHourlyForecastsForDayQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.HourlyForecast
import kotlinx.datetime.Instant

data class GetHourlyForecastsForDayQuery(
    val weatherId: Int,
    val date: Instant
)

data class GetHourlyForecastsForDayQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)