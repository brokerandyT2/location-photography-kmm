// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/HasFreshWeatherDataForCoordinatesQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import kotlinx.datetime.Instant

data class HasFreshWeatherDataForCoordinatesQuery(
    val latitude: Double,
    val longitude: Double,
    val maxAge: Instant
)

data class HasFreshWeatherDataForCoordinatesQueryResult(
    val hasFreshData: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)