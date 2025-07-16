// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsCountByWeatherQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

data class GetHourlyForecastsCountByWeatherQuery(
    val weatherId: Int
)

data class GetHourlyForecastsCountByWeatherQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)