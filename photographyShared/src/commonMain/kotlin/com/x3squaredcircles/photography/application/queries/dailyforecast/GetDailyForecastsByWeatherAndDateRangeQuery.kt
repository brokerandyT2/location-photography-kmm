// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsByWeatherAndDateRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastsByWeatherAndDateRangeQuery(
    val weatherId: Int,
    val startDate: Long,
    val endDate: Long
)

data class GetDailyForecastsByWeatherAndDateRangeQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)