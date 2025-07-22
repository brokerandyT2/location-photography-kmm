// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/CachedSunCalculation.kt
package com.x3squaredcircles.photography.models


import com.x3squaredcircles.photography.application.queries.sunlocation.SunTimesDto
import kotlinx.datetime.Instant

data class CachedSunCalculation(
    val sunTimes: SunTimesDto,
    val timestamp: Instant
)