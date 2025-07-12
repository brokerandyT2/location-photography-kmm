// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Weather.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents weather data for a specific location.
 * Contains current conditions and forecast information for photography planning.
 */
@Serializable
data class Weather(
    override val id: Int = 0,
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val lastUpdate: Long,
    val temperature: Double? = null,
    val feelsLike: Double? = null,
    val humidity: Double? = null,
    val pressure: Double? = null,
    val visibility: Double? = null,
    val uvIndex: Double? = null,
    val windSpeed: Double? = null,
    val windDirection: Double? = null,
    val windGust: Double? = null,
    val cloudCover: Double? = null,
    val condition: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val isDeleted: Boolean = false
) : Entity() {
    
    /**
     * Gets the last update time as a LocalDateTime in the current system timezone.
     */
    val lastUpdateDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(lastUpdate)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets the sunrise time as a LocalDateTime in the current system timezone.
     */
    val sunriseDateTime: kotlinx.datetime.LocalDateTime?
        get() = sunrise?.let { 
            kotlinx.datetime.Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        }
    
    /**
     * Gets the sunset time as a LocalDateTime in the current system timezone.
     */
    val sunsetDateTime: kotlinx.datetime.LocalDateTime?
        get() = sunset?.let { 
            kotlinx.datetime.Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        }
    
    /**
     * Checks if the weather data is stale (older than the specified age in milliseconds).
     */
    fun isStale(maxAgeMillis: Long): Boolean {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return (now - lastUpdate) > maxAgeMillis
    }
    
    /**
     * Checks if the weather data is fresh (within the specified age in milliseconds).
     */
    fun isFresh(maxAgeMillis: Long): Boolean = !isStale(maxAgeMillis)
    
    /**
     * Gets the coordinate pair for this weather data.
     */
    val coordinate: Pair<Double, Double>
        get() = Pair(latitude, longitude)
    
    /**
     * Determines if conditions are suitable for photography based on basic criteria.
     */
    val isGoodForPhotography: Boolean
        get() {
            return when {
                cloudCover != null && cloudCover > 90.0 -> false  // Too cloudy
                visibility != null && visibility < 5.0 -> false   // Poor visibility
                windSpeed != null && windSpeed > 50.0 -> false    // Too windy
                else -> true
            }
        }
    
    /**
     * Gets a photography recommendation based on current conditions.
     */
    val photographyRecommendation: String
        get() = when {
            cloudCover == null -> "Weather data incomplete"
            cloudCover < 20.0 -> "Excellent - Clear skies"
            cloudCover < 50.0 -> "Good - Partly cloudy"
            cloudCover < 80.0 -> "Fair - Mostly cloudy"
            else -> "Poor - Overcast"
        }
    
    /**
     * Creates a copy of this weather data marked as deleted.
     */
    fun markAsDeleted(): Weather {
        return copy(isDeleted = true)
    }
    
    /**
     * Creates a copy of this weather data with updated timestamp.
     */
    fun updateLastUpdate(): Weather {
        return copy(lastUpdate = kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
    }
    
    companion object {
        /**
         * Creates a new Weather record with the current timestamp.
         */
        fun create(
            locationId: Int,
            latitude: Double,
            longitude: Double,
            timezone: String,
            timezoneOffset: Int
        ): Weather {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            return Weather(
                locationId = locationId,
                latitude = latitude,
                longitude = longitude,
                timezone = timezone,
                timezoneOffset = timezoneOffset,
                lastUpdate = now
            )
        }
        
        /**
         * Default staleness threshold for weather data (1 hour).
         */
        const val DEFAULT_STALE_THRESHOLD_MILLIS = 60 * 60 * 1000L
        
        /**
         * Maximum staleness threshold for weather data (6 hours).
         */
        const val MAX_STALE_THRESHOLD_MILLIS = 6 * 60 * 60 * 1000L
    }
}