// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SupermoonEvent.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class SupermoonEvent(
    val dateTime: Instant,
    val distance: Double,
    val angularDiameter: Double,
    val percentLarger: Double,
    val eventName: String,
    val photographyOpportunity: String
)