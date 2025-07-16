// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetSunriseSunsetQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

data class GetSunriseSunsetQuery(
    val weatherId: Int,
    val forecastDate: Long
)

data class GetSunriseSunsetQueryResult(
    val sunrise: Long?,
    val sunset: Long?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)