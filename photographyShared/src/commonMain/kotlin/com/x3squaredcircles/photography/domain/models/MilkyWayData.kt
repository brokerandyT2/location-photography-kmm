// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/MilkyWayData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class MilkyWayData(
    val dateTime: Instant,
    val galacticCenterAzimuth: Double,
    val galacticCenterAltitude: Double,
    val isVisible: Boolean,
    val rise: Instant?,
    val set: Instant?,
    val optimalViewingTime: Instant?,
    val season: String,
    val darkSkyQuality: Double,
    val photographyRecommendations: String,
    val compositionSuggestions: List<String>
)