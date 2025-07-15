// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetForecastsByWeatherIdQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetForecastsByWeatherIdQuery(
    val weatherId: Int
)

data class GetForecastsByWeatherIdQueryResult(
    val forecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)