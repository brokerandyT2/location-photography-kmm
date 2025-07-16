// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsByLocationAndDateRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastsByLocationAndDateRangeQuery(
    val locationId: Int,
    val startDate: Long,
    val endDate: Long
)

data class GetDailyForecastsByLocationAndDateRangeQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)