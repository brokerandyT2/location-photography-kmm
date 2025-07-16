// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class GetHourlyForecastByIdQuery(
    val id: Int
)

data class GetHourlyForecastByIdQueryResult(
    val hourlyForecast: HourlyForecast?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)