// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PlanetaryConjunction.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.PlanetType
import kotlinx.datetime.Instant

data class PlanetaryConjunction(
    val dateTime: Instant,
    val planet1: PlanetType,
    val planet2: PlanetType,
    val separation: Double,
    val altitude: Double,
    val azimuth: Double,
    val visibilityDescription: String,
    val photographyRecommendation: String
)