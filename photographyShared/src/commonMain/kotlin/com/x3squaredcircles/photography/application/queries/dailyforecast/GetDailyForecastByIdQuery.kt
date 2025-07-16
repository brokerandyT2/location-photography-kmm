// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetDailyForecastByIdQuery(
    val id: Int
)

data class GetDailyForecastByIdQueryResult(
    val dailyForecast: WeatherForecast?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)