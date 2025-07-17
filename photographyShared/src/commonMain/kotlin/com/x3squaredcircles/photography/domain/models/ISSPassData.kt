// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ISSPassData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class ISSPassData(
    val startTime: Instant,
    val maxTime: Instant,
    val endTime: Instant,
    val startAzimuth: Double,
    val maxAltitude: Double,
    val endAzimuth: Double,
    val magnitude: Double,
    val duration: Duration,
    val passType: String
)