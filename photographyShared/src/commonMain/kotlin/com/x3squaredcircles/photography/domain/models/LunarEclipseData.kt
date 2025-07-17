// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/LunarEclipseData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class LunarEclipseData(
    val dateTime: Instant,
    val eclipseType: String,
    val penumbralBegin: Instant,
    val partialBegin: Instant?,
    val totalityBegin: Instant?,
    val maximum: Instant?,
    val totalityEnd: Instant?,
    val partialEnd: Instant?,
    val penumbralEnd: Instant,
    val magnitude: Double,
    val isVisible: Boolean,
    val photographyPlanning: String,
    val exposureRecommendations: List<String>
)