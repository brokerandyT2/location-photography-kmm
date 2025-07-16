// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetNext7DaysHourlyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetNext7DaysHourlyForecastsQuery(
    val weatherId: Int,
    val startTime: Long,
    val endTime: Long
)

data class GetNext7DaysHourlyForecastsQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)