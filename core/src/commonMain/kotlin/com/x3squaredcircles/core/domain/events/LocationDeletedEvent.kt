// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/LocationDeletedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.serialization.Serializable

/**
 * Domain event raised when a location is deleted (soft delete).
 * Can trigger cleanup of related data like weather cache, photos, etc.
 */
@Serializable
data class LocationDeletedEvent(
    val locationId: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val deletedBy: String? = null,
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
        get() = "Location '$title' deleted at $coordinateDisplay"
}