// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsByWeatherIdQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastsByWeatherIdQuery(
    val weatherId: Int
)

data class GetDailyForecastsByWeatherIdQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)