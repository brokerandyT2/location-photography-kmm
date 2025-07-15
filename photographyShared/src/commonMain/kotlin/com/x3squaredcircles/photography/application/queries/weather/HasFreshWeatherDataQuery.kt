// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/HasFreshWeatherDataQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import kotlinx.datetime.Instant

data class HasFreshWeatherDataQuery(
    val locationId: Int,
    val maxAge: Instant
)

data class HasFreshWeatherDataQueryResult(
    val hasFreshData: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)