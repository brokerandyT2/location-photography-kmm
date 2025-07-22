// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetSunPositionQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetSunPositionQuery(
    val latitude: Double,
    val longitude: Double,
    val dateTime: Instant
)

data class GetSunPositionQueryResult(
    val sunPosition: SunPositionDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class SunPositionDto(
    val dateTime: Instant,
    val latitude: Double,
    val longitude: Double,
    val azimuth: Double,
    val elevation: Double,
    val distance: Double,
    val isVisible: Boolean
)