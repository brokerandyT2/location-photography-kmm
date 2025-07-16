// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsByWeatherIdQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetHourlyForecastsByWeatherIdQuery(
    val weatherId: Int
)

data class GetHourlyForecastsByWeatherIdQueryResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)