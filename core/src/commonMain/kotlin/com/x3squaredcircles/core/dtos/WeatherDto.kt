// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/WeatherDto.kt
package com.x3squaredcircles.core.dtos

import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Data Transfer Object for weather information.
 * Used for transferring weather data between layers and external APIs.
 */
@Serializable
data class WeatherDto(
    val id: Int = 0,
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
    val sunset: Long? = null
) {
    /**
     * Gets the last update time as a formatted string.
     */
    val lastUpdateFormatted: String
        get() {
            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(lastUpdate)
            val dateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            return "${dateTime.date} ${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
        }
    
    /**
     * Gets a formatted temperature string with unit.
     */
    fun getTemperatureFormatted(unit: String = "°C"): String {
        return temperature?.let { "${it.toInt()}$unit" } ?: "N/A"
    }
    
    /**
     * Gets a formatted wind speed with direction.
     */
    val windInfo: String
        get() {
            val speed = windSpeed?.let { "${it.toInt()} km/h" } ?: "N/A"
            val direction = windDirection?.let {
                val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", 
                                       "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
                val index = ((it + 11.25) / 22.5).toInt() % 16
                directions[index]
            } ?: ""
            
            return if (direction.isNotEmpty()) "$speed $direction" else speed
        }
    
    /**
     * Checks if the weather data is considered fresh (less than 1 hour old).
     */
    val isFresh: Boolean
        get() {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            return (now - lastUpdate) < (60 * 60 * 1000) // 1 hour
        }
    
    /**
     * Gets a photography suitability rating based on conditions.
     */
    val photographySuitability: String
        get() = when {
            cloudCover == null -> "Unknown"
            cloudCover < 20 -> "Excellent"
            cloudCover < 50 -> "Good"
            cloudCover < 80 -> "Fair"
            else -> "Poor"
        }
}