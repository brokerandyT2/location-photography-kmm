// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetBestConditionsHourlyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetBestConditionsHourlyForecastsQuery(
    val weatherId: Int,
    val startTime: Long,
    val endTime: Long,
    val limit: Int
)

data class GetBestConditionsHourlyForecastsQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)