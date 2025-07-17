// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/EnhancedMoonData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class EnhancedMoonData(
    val dateTime: Instant,
    val phase: Double,
    val phaseName: String,
    val illumination: Double,
    val azimuth: Double,
    val altitude: Double,
    val distance: Double,
    val angularDiameter: Double,
    val rise: Instant?,
    val set: Instant?,
    val transit: Instant?,
    val librationLatitude: Double,
    val librationLongitude: Double,
    val positionAngle: Double,
    val isSupermoon: Boolean,
    val opticalLibration: Double,
    val optimalPhotographyPhase: String,
    val visibleFeatures: List<String>,
    val recommendedExposureSettings: String
)