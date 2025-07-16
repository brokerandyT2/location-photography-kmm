// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsByWeatherAndTimeRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetHourlyForecastsByWeatherAndTimeRangeQuery(
    val weatherId: Int,
    val startTime: Long,
    val endTime: Long
)

data class GetHourlyForecastsByWeatherAndTimeRangeQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)