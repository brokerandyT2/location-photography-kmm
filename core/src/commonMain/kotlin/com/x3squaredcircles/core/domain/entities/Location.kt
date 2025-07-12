// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Location.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.AggregateRoot
import kotlinx.serialization.Serializable
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a geographical location for photography planning.
 * This is an aggregate root that can raise domain events when important changes occur.
 */
@Serializable
data class Location(
    override val id: Int = 0,
    val title: String,
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val photoPath: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) : AggregateRoot() {
    
    /**
     * The coordinate pair for this location.
     */
    val coordinate: Pair<Double, Double>
        get() = Pair(latitude, longitude)
    
    /**
     * Returns a formatted string representation of the coordinates.
     */
    val formattedCoordinates: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Calculates the distance to another location in kilometers using the Haversine formula.
     */
    fun distanceTo(other: Location): Double {
        return distanceTo(other.latitude, other.longitude)
    }
    
    /**
     * Calculates the distance to the specified coordinates in kilometers using the Haversine formula.
     */
    fun distanceTo(lat: Double, lon: Double): Double {
        val earthRadiusKm = 6371.0
        
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(lat)
        val deltaLatRad = Math.toRadians(lat - latitude)
        val deltaLonRad = Math.toRadians(lon - longitude)
        
        val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
                kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                kotlin.math.sin(deltaLonRad / 2) * kotlin.math.sin(deltaLonRad / 2)
        
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return earthRadiusKm * c
    }
    
    /**
     * Determines if this location is within the specified distance of another location.
     */
    fun isWithinDistance(other: Location, distanceKm: Double): Boolean {
        return distanceTo(other) <= distanceKm
    }
    
    /**
     * Determines if this location is within the specified distance of the given coordinates.
     */
    fun isWithinDistance(lat: Double, lon: Double, distanceKm: Double): Boolean {
        return distanceTo(lat, lon) <= distanceKm
    }
    
    /**
     * Creates a copy of this location marked as deleted.
     */
    fun markAsDeleted(): Location {
        return copy(isDeleted = true, updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
    }
    
    /**
     * Creates a copy of this location with updated coordinates.
     */
    fun updateCoordinates(lat: Double, lon: Double): Location {
        return copy(
            latitude = lat, 
            longitude = lon, 
            updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
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
    
    companion object {
        /**
         * Creates a new Location with the current timestamp.
         */
        fun create(
            title: String,
            description: String = "",
            latitude: Double,
            longitude: Double,
            photoPath: String? = null
        ): Location {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            return Location(
                title = title,
                description = description,
                latitude = latitude,
                longitude = longitude,
                photoPath = photoPath,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}