// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastByDateQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastByDateQuery(
    val weatherId: Int,
    val forecastDate: Long
)

data class GetDailyForecastByDateQueryResult(
    val dailyForecast: WeatherForecast?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)