// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetMoonDataQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetMoonDataQuery(
    val latitude: Double,
    val longitude: Double,
    val date: Instant
)

data class GetMoonDataQueryResult(
    val moonData: MoonPhaseDataDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class MoonPhaseDataDto(
    val date: Instant,
    val phase: Double,
    val phaseName: String,
    val illuminationPercentage: Double,
    val moonRise: Instant?,
    val moonSet: Instant?,
    val azimuth: Double,
    val elevation: Double,
    val distance: Double,
    val brightness: Double
)