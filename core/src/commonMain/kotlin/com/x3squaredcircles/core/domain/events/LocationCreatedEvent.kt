// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/LocationCreatedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.serialization.Serializable

/**
 * Domain event raised when a new location is created.
 * This event can trigger side effects like weather data fetching, photo optimization, etc.
 */
@Serializable
data class LocationCreatedEvent(
    val locationId: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val createdBy: String? = null,
    override val dateOccurred: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
) : IDomainEvent {
    
    /**
     * Gets a formatted coordinate string for logging/display.
     */
    val coordinateDisplay: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Gets a description of the event for logging.
     */
    val eventDescription: String
        get() = "Location '$title' created at $coordinateDisplay"
}