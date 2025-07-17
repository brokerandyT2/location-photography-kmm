// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/StarTrailData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class StarTrailData(
    val startTime: Instant,
    val exposureDuration: Duration,
    val celestialPoleAzimuth: Double,
    val celestialPoleAltitude: Double,
    val starTrailLength: Double,
    val rotation: Double,
    val optimalComposition: String,
    val exposureStrategy: List<String>
)