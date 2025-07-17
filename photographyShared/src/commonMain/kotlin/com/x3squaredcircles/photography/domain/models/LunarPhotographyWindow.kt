// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/LunarPhotographyWindow.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class LunarPhotographyWindow(
    val startTime: Instant,
    val endTime: Instant,
    val phase: Double,
    val phaseName: String,
    val optimalAltitude: Double,
    val photographyType: String,
    val recommendedSettings: String,
    val visibleFeatures: List<String>,
    val qualityScore: Double
)