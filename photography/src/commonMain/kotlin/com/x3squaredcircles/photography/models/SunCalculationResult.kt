// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/SunCalculationResult.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.Instant

data class SunCalculationResult(
    val sunrise: Instant,
    val sunset: Instant,
    val solarNoon: Instant,
    val astronomicalDawn: Instant,
    val astronomicalDusk: Instant,
    val nauticalDawn: Instant,
    val nauticalDusk: Instant,
    val civilDawn: Instant,
    val civilDusk: Instant
)