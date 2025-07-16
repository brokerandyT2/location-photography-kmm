// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsByLocationIdQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastsByLocationIdQuery(
    val locationId: Int
)

data class GetDailyForecastsByLocationIdQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)