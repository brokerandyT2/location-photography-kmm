// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/PredictiveLightRecommendation.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class PredictiveLightRecommendation(
    val generatedAt: Instant,
    val bestTimeWindow: TimeWindow? = null,
    val alternativeWindows: List<TimeWindow> = emptyList(),
    val overallRecommendation: String = "",
    val keyInsights: List<String> = emptyList()
)

data class TimeWindow(
    val startTime: Instant,
    val endTime: Instant,
    val qualityScore: Double,
    val description: String = "",
    val conditions: String = ""
)