// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsForDayQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetHourlyForecastsForDayQuery(
    val weatherId: Int,
    val startTime: Long,
    val endTime: Long
)

data class GetHourlyForecastsForDayQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)