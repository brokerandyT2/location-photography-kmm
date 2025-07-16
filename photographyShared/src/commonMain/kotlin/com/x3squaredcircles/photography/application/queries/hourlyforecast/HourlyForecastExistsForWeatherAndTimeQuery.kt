// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/HourlyForecastExistsForWeatherAndTimeQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

data class HourlyForecastExistsForWeatherAndTimeQuery(
    val weatherId: Int,
    val forecastTime: Long
)

data class HourlyForecastExistsForWeatherAndTimeQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)