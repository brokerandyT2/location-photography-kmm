// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/LocationListDto.kt
package com.x3squaredcircles.core.dtos

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for location list display.
 * Contains only the essential information needed for list views.
 */
@Serializable
data class LocationListDto(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoPath: String?,
    val isDeleted: Boolean,
    val createdAt: Long
) {
    /**
     * Gets a formatted string representation of the coordinates.
     */
    val formattedCoordinates: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Gets a shorter description for list views (max 100 characters).
     */
    val shortDescription: String
        get() = if (description.length > 100) {
            "${description.take(97)}..."
        } else {
            description
        }
    
    /**
     * Checks if this location has a photo.
     */
    val hasPhoto: Boolean
        get() = !photoPath.isNullOrBlank()
}