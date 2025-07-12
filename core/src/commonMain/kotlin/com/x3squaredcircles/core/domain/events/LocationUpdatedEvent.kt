// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/LocationUpdatedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.serialization.Serializable

/**
 * Domain event raised when a location is updated.
 * Can trigger weather data refresh if coordinates changed, cache invalidation, etc.
 */
@Serializable
data class LocationUpdatedEvent(
    val locationId: Int,
    val title: String,
    val oldLatitude: Double? = null,
    val oldLongitude: Double? = null,
    val newLatitude: Double,
    val newLongitude: Double,
    val coordinatesChanged: Boolean,
    val updatedBy: String? = null,
    override val dateOccurred: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
) : IDomainEvent {
    
    /**
     * Gets the old coordinate display string.
     */
    val oldCoordinateDisplay: String
        get() = if (oldLatitude != null && oldLongitude != null) {
            String.format("%.6f, %.6f", oldLatitude, oldLongitude)
        } else {
            "Unknown"
        }
    
    /**
     * Gets the new coordinate display string.
     */
    val newCoordinateDisplay: String
        get() = String.format("%.6f, %.6f", newLatitude, newLongitude)
    
    /**
     * Gets a description of the event for logging.
     */
    val eventDescription: String
        get() = if (coordinatesChanged) {
            "Location '$title' updated - coordinates changed from $oldCoordinateDisplay to $newCoordinateDisplay"
        } else {
            "Location '$title' updated - details changed"
        }
}