// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetAllDailyForecastsQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetAllDailyForecastsQuery(
    val dummy: Boolean = true
)

data class GetAllDailyForecastsQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)