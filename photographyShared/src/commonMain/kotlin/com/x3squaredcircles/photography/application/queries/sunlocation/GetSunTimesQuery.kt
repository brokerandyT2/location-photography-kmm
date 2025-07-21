// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetSunTimesQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetSunTimesQuery(
    val latitude: Double,
    val longitude: Double,
    val date: Instant
)

data class GetSunTimesQueryResult(
    val sunTimes: SunTimesDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class SunTimesDto(
    val date: Instant,
    val latitude: Double,
    val longitude: Double,
    val sunrise: Instant,
    val sunset: Instant,
    val solarNoon: Instant,
    val astronomicalDawn: Instant,
    val astronomicalDusk: Instant,
    val nauticalDawn: Instant,
    val nauticalDusk: Instant,
    val civilDawn: Instant,
    val civilDusk: Instant,
    val goldenHourMorningStart: Instant,
    val goldenHourMorningEnd: Instant,
    val goldenHourEveningStart: Instant,
    val goldenHourEveningEnd: Instant
)