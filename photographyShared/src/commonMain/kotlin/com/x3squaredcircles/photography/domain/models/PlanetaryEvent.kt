// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PlanetaryEvent.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.PlanetType
import kotlinx.datetime.Instant

data class PlanetaryEvent(
    val dateTime: Instant,
    val planet: PlanetType,
    val eventType: String,
    val apparentMagnitude: Double,
    val angularDiameter: Double,
    val optimalViewingConditions: String,
    val equipmentRecommendations: String
)