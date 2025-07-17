// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/MeteorShowerData.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.MeteorShowerType
import kotlinx.datetime.Instant

data class MeteorShowerData(
    val showerType: MeteorShowerType,
    val name: String,
    val peakDate: Instant,
    val activityStart: Instant,
    val activityEnd: Instant,
    val radiantRightAscension: Double,
    val radiantDeclination: Double,
    val radiantAzimuth: Double,
    val radiantAltitude: Double,
    val zenithHourlyRate: Int,
    val moonIllumination: Double,
    val optimalConditions: Boolean,
    val photographyStrategy: String
)