// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/GetHourlyForecastsCountQuery.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast

data class GetHourlyForecastsCountQuery(
    val dummy: Boolean = true
)

data class GetHourlyForecastsCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)