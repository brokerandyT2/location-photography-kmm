// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetEnhancedSunTimesQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetEnhancedSunTimesQuery(
    val latitude: Double,
    val longitude: Double,
    val date: Instant,
    val useHighPrecision: Boolean = true
)

data class GetEnhancedSunTimesQueryResult(
    val sunTimes: EnhancedSunTimesDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class EnhancedSunTimesDto(
    val sunrise: Instant,
    val sunset: Instant,
    val solarNoon: Instant,
    val civilDawn: Instant,
    val civilDusk: Instant,
    val nauticalDawn: Instant,
    val nauticalDusk: Instant,
    val astronomicalDawn: Instant,
    val astronomicalDusk: Instant,
    val goldenHourStart: Instant,
    val goldenHourEnd: Instant,
    val blueHourStart: Instant,
    val blueHourEnd: Instant
)