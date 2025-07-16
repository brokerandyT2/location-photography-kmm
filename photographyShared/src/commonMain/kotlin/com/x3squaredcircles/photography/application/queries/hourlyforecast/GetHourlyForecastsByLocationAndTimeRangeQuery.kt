// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsByLocationAndTimeRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetHourlyForecastsByLocationAndTimeRangeQuery(
    val locationId: Int,
    val startTime: Long,
    val endTime: Long
)

data class GetHourlyForecastsByLocationAndTimeRangeQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)