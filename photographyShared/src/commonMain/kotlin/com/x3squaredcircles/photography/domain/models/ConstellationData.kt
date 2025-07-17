// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ConstellationData.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.ConstellationType
import kotlinx.datetime.Instant

data class ConstellationData(
    val constellation: ConstellationType,
    val dateTime: Instant,
    val centerRightAscension: Double,
    val centerDeclination: Double,
    val centerAzimuth: Double,
    val centerAltitude: Double,
    val rise: Instant?,
    val set: Instant?,
    val optimalViewingTime: Instant?,
    val isCircumpolar: Boolean,
    val notableObjects: List<DeepSkyObjectData>,
    val photographyNotes: String
)