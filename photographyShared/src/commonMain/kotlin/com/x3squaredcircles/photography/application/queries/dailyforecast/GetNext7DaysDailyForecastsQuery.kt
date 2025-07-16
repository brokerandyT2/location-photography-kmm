// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetNext7DaysDailyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetNext7DaysDailyForecastsQuery(
    val weatherId: Int,
    val startDate: Long,
    val endDate: Long
)

data class GetNext7DaysDailyForecastsQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)