// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PolarAlignmentData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class PolarAlignmentData(
    val dateTime: Instant,
    val polarisAzimuth: Double,
    val polarisAltitude: Double,
    val polarisOffsetAngle: Double,
    val polarisOffsetDistance: Double,
    val alignmentInstructions: String,
    val referenceStars: List<String>
)