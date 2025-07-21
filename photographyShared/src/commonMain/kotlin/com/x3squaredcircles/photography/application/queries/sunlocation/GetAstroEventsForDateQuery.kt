package com.x3squaredcircles.photography.application.queries.astrolocation

import com.x3squaredcircles.photography.domain.enums.AstroTarget
import kotlinx.datetime.Instant

data class GetAstroEventsForDateQuery(
    val date: Instant,
    val latitude: Double,
    val longitude: Double,
    val minimumAltitude: Int = 10,
    val includeDayTimeEvents: Boolean = false
)

data class GetAstroEventsForDateQueryResult(
    val events: List<AstroEventDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class AstroEventDto(
    val name: String,
    val target: AstroTarget,
    val startTime: Instant,
    val endTime: Instant,
    val peakTime: Instant?,
    val azimuth: Double,
    val altitude: Double,
    val magnitude: Double,
    val description: String,
    val constellation: String,
    val isVisible: Boolean,
    val eventType: String,
    val angularSize: Double,
    val recommendedEquipment: String
)