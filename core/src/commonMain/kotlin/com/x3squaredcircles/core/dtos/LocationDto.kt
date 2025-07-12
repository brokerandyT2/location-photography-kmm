// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/LocationDto.kt
package com.x3squaredcircles.core.dtos

import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Data Transfer Object for complete location information.
 * Used for detailed location views and API responses.
 */
@Serializable
data class LocationDto(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoPath: String?,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Gets a formatted string representation of the coordinates.
     */
    val formattedCoordinates: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Gets the coordinate pair.
     */
    val coordinates: Pair<Double, Double>
        get() = Pair(latitude, longitude)
    
    /**
     * Gets the created date as a LocalDateTime in the current system timezone.
     */
    val createdDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(createdAt)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets the updated date as a LocalDateTime in the current system timezone.
     */
    val updatedDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(updatedAt)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Checks if this location has a photo.
     */
    val hasPhoto: Boolean
        get() = !photoPath.isNullOrBlank()
    
    /**
     * Gets a display-friendly created date string.
     */
    val createdDateFormatted: String
        get() {
            val date = createdDateTime
            return "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
        }
    
    /**
     * Checks if the location was recently updated (within last 24 hours).
     */
    val isRecentlyUpdated: Boolean
        get() {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            val dayInMillis = 24 * 60 * 60 * 1000L
            return (now - updatedAt) < dayInMillis
        }
}