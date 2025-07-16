// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetBestConditionsDailyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetBestConditionsDailyForecastsQuery(
    val weatherId: Int,
    val startDate: Long,
    val endDate: Long,
    val limit: Int
)

data class GetBestConditionsDailyForecastsQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)