// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetCurrentSunPositionQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetCurrentSunPositionQuery(
    val latitude: Double,
    val longitude: Double,
    val dateTime: Instant
)

data class GetCurrentSunPositionQueryResult(
    val currentSunPosition: SunPositionDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)