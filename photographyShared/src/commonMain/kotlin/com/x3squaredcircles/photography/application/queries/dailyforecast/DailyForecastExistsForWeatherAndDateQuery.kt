// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/DailyForecastExistsForWeatherAndDateQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

data class DailyForecastExistsForWeatherAndDateQuery(
    val weatherId: Int,
    val forecastDate: Long
)

data class DailyForecastExistsForWeatherAndDateQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)