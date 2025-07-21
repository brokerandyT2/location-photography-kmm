// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetSunPathDataQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class GetSunPathDataQuery(
    val latitude: Double,
    val longitude: Double,
    val date: Instant,
    val intervalMinutes: Int = 15
)

data class GetSunPathDataQueryResult(
    val sunPathData: SunPathDataResultDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class SunPathDataResultDto(
    val pathPoints: List<SunPathPointDto>,
    val currentPosition: SunPathPointDto,
    val date: Instant,
    val latitude: Double,
    val longitude: Double,
    val metrics: SunPathMetricsDto
)

data class SunPathMetricsDto(
    val daylightDuration: Duration,
    val maxElevation: Double,
    val maxElevationTime: Instant,
    val sunriseAzimuth: Double,
    val sunsetAzimuth: Double,
    val seasonalNote: String
)

data class SunPathPointDto(
    val time: Instant,
    val azimuth: Double,
    val elevation: Double,
    val isVisible: Boolean
)