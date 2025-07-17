// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PlanetPositionData.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.PlanetType
import kotlinx.datetime.Instant

data class PlanetPositionData(
    val planet: PlanetType,
    val dateTime: Instant,
    val rightAscension: Double,
    val declination: Double,
    val azimuth: Double,
    val altitude: Double,
    val distance: Double,
    val apparentMagnitude: Double,
    val angularDiameter: Double,
    val isVisible: Boolean,
    val rise: Instant?,
    val set: Instant?,
    val transit: Instant?,
    val recommendedEquipment: String,
    val photographyNotes: String
)