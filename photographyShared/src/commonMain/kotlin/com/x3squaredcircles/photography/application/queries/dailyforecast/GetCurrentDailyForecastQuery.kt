// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetCurrentDailyForecastQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetCurrentDailyForecastQuery(
    val weatherId: Int,
    val currentTime: Long
)

data class GetCurrentDailyForecastQueryResult(
    val dailyForecast: WeatherForecast?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)