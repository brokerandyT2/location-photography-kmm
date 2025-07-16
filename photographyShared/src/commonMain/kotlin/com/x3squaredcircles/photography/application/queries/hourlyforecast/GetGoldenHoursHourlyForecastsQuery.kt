// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetGoldenHoursHourlyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetGoldenHoursHourlyForecastsQuery(
    val weatherId: Int,
    val startTime: Long,
    val endTime: Long
)

data class GetGoldenHoursHourlyForecastsQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)