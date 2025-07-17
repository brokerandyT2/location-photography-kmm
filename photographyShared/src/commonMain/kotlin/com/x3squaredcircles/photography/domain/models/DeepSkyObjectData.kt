// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/DeepSkyObjectData.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.ConstellationType
import kotlinx.datetime.Instant

data class DeepSkyObjectData(
    val catalogId: String,
    val commonName: String,
    val objectType: String,
    val dateTime: Instant,
    val rightAscension: Double,
    val declination: Double,
    val azimuth: Double,
    val altitude: Double,
    val magnitude: Double,
    val angularSize: Double,
    val isVisible: Boolean,
    val optimalViewingTime: Instant?,
    val recommendedEquipment: String,
    val exposureGuidance: String,
    val parentConstellation: ConstellationType
)