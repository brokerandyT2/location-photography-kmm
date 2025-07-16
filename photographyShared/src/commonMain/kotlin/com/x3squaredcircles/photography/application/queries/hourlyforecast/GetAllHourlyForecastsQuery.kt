// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetAllHourlyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetAllHourlyForecastsQuery(
    val dummy: Boolean = true
)

data class GetAllHourlyForecastsQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)