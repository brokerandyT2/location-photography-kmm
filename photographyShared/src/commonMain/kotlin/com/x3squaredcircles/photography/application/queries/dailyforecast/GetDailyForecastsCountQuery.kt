// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetDailyForecastsCountQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

data class GetDailyForecastsCountQuery(
    val dummy: Boolean = true
)

data class GetDailyForecastsCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)