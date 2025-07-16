// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetMoonPhaseQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

data class GetMoonPhaseQuery(
    val weatherId: Int,
    val forecastDate: Long
)

data class GetMoonPhaseQueryResult(
    val moonPhase: Double?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)