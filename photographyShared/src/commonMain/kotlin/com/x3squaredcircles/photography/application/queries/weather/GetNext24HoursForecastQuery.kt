// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetNext24HoursForecastQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.HourlyForecast
import kotlinx.datetime.Instant

data class GetNext24HoursForecastQuery(
    val weatherId: Int,
    val fromTime: Instant
)

data class GetNext24HoursForecastQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)