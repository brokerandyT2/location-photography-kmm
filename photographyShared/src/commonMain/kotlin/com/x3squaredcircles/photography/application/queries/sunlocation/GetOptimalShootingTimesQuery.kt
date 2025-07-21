// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetOptimalShootingTimesQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetOptimalShootingTimesQuery(
    val latitude: Double,
    val longitude: Double,
    val date: Instant,
    val includeWeatherForecast: Boolean = false,
    val timezone: String
)

data class GetOptimalShootingTimesQueryResult(
    val optimalTimes: List<OptimalShootingTimeDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class OptimalShootingTimeDto(
    val startTime: Instant,
    val endTime: Instant,
    val lightQuality: LightQuality,
    val qualityScore: Double,
    val description: String,
    val idealFor: List<String>
)

enum class LightQuality {
    GOLDEN_HOUR,
    BLUE_HOUR,
    HARSH,
    NIGHT,
    OVERCAST,
    UNKNOWN
}