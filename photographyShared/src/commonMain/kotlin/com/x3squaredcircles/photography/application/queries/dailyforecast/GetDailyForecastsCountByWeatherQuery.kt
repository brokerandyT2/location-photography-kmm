// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsCountByWeatherQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

data class GetDailyForecastsCountByWeatherQuery(
    val weatherId: Int
)

data class GetDailyForecastsCountByWeatherQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)