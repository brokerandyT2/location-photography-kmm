// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/WeatherDataUpdatedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.serialization.Serializable

/**
 * Domain event raised when weather data is updated for a location.
 * Can trigger UI refreshes, notifications for photography conditions, etc.
 */
@Serializable
data class WeatherDataUpdatedEvent(
    val weatherId: Int,
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double? = null,
    val condition: String? = null,
    val cloudCover: Double? = null,
    val windSpeed: Double? = null,
    val isGoodForPhotography: Boolean,
    val dataSource: String? = null,
    override val dateOccurred: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
) : IDomainEvent {
    
    /**
     * Gets a formatted coordinate string for logging/display.
     */
    val coordinateDisplay: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Gets a formatted temperature string.
     */
    val temperatureDisplay: String
        get() = temperature?.let { "${it.toInt()}°C" } ?: "Unknown"
    
    /**
     * Gets a description of the event for logging.
     */
    val eventDescription: String
        get() = "Weather updated for location $locationId at $coordinateDisplay - $temperatureDisplay, ${condition ?: "Unknown condition"}"
    
    /**
     * Gets a photography recommendation based on conditions.
     */
    val photographyRecommendation: String
        get() = if (isGoodForPhotography) {
            "Good conditions for photography"
        } else {
            "Challenging conditions for photography"
        }
}