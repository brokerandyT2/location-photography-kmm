// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/WeatherForecast.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*

/**
 * Represents daily weather forecast data for photography planning.
 */
@Serializable
data class WeatherForecast(
    override val id: Int = 0,
    val weatherId: Int,
    val forecastDate: Long,
    val minTemperature: Double? = null,
    val maxTemperature: Double? = null,
    val humidity: Double? = null,
    val pressure: Double? = null,
    val uvIndex: Double? = null,
    val windSpeed: Double? = null,
    val windDirection: Double? = null,
    val windGust: Double? = null,
    val cloudCover: Double? = null,
    val precipitationChance: Double? = null,
    val precipitationAmount: Double? = null,
    val condition: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val moonPhase: Double? = null,
    val moonrise: Long? = null,
    val moonset: Long? = null
) : Entity() {
    
    /**
     * Gets the forecast date as a LocalDateTime in the current system timezone.
     */
    val forecastDateTime: LocalDateTime
        get() = Instant.fromEpochMilliseconds(forecastDate)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    
    /**
     * Gets the sunrise time as a LocalDateTime in the current system timezone.
     */
    val sunriseDateTime: LocalDateTime?
        get() = sunrise?.let { 
            Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    
    /**
     * Gets the sunset time as a LocalDateTime in the current system timezone.
     */
    val sunsetDateTime: LocalDateTime?
        get() = sunset?.let { 
            Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    
    /**
     * Gets the moonrise time as a LocalDateTime in the current system timezone.
     */
    val moonriseDateTime: LocalDateTime?
        get() = moonrise?.let { 
            Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    
    /**
     * Gets the moonset time as a LocalDateTime in the current system timezone.
     */
    val moonsetDateTime: LocalDateTime?
        get() = moonset?.let { 
            Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    
    /**
     * Gets the average temperature.
     */
    val averageTemperature: Double?
        get() = if (minTemperature != null && maxTemperature != null) {
            (minTemperature + maxTemperature) / 2.0
        } else null
    
    /**
     * Gets the temperature range.
     */
    val temperatureRange: Double?
        get() = if (minTemperature != null && maxTemperature != null) {
            maxTemperature - minTemperature
        } else null
    
    /**
     * Determines if conditions are good for photography.
     */
    val isGoodForPhotography: Boolean
        get() = (cloudCover ?: 50.0) < 70.0 && 
                (precipitationChance ?: 0.5) < 0.3 && 
                (windSpeed ?: 0.0) < 30.0
    
    /**
     * Determines if this is a clear day.
     */
    val isClearDay: Boolean
        get() = (cloudCover ?: 100.0) < 30.0 && (precipitationChance ?: 1.0) < 0.2
    
    /**
     * Gets a photography suitability score (0-100).
     */
    val photographySuitabilityScore: Int
        get() {
            var score = 100
            
            // Cloud cover impact (0-40 points)
            cloudCover?.let { score -= (it * 0.4).toInt() }
            
            // Precipitation impact (0-30 points)
            precipitationChance?.let { score -= (it * 30).toInt() }
            
            // Wind impact (0-20 points)
            windSpeed?.let { 
                if (it > 20.0) {
                    score -= ((it - 20.0) * 0.5).toInt()
                }
            }
            
            // UV index bonus for good lighting (0-10 points)
            uvIndex?.let {
                if (it in 3.0..8.0) {
                    score += 10
                }
            }
            
            return maxOf(0, minOf(100, score))
        }
    
    /**
     * Gets the wind direction as a compass direction.
     */
    val windDirectionCompass: String
        get() = windDirection?.let { dir ->
            when ((dir + 11.25) / 22.5) {
                0.0, 16.0 -> "N"
                1.0 -> "NNE"
                2.0 -> "NE"
                3.0 -> "ENE"
                4.0 -> "E"
                5.0 -> "ESE"
                6.0 -> "SE"
                7.0 -> "SSE"
                8.0 -> "S"
                9.0 -> "SSW"
                10.0 -> "SW"
                11.0 -> "WSW"
                12.0 -> "W"
                13.0 -> "WNW"
                14.0 -> "NW"
                15.0 -> "NNW"
                else -> "N"
            }
        } ?: "Unknown"
    
    /**
     * Gets a temperature display string.
     */
    val temperatureDisplay: String
        get() = when {
            minTemperature != null && maxTemperature != null -> 
                "${minTemperature.toInt()}°/${maxTemperature.toInt()}°C"
            averageTemperature != null -> 
                "${averageTemperature?.toInt()}°C"
            else -> "Unknown"
        }
    
    /**
     * Gets a wind display string.
     */
    val windDisplay: String
        get() = windSpeed?.let { 
            "${it.toInt()} km/h $windDirectionCompass"
        } ?: "Unknown"
    
    /**
     * Gets the moon phase as a descriptive string.
     */
    val moonPhaseDescription: String
        get() = moonPhase?.let { phase ->
            when {
                phase < 0.125 -> "New Moon"
                phase < 0.25 -> "Waxing Crescent"
                phase < 0.375 -> "First Quarter"
                phase < 0.5 -> "Waxing Gibbous"
                phase < 0.625 -> "Full Moon"
                phase < 0.75 -> "Waning Gibbous"
                phase < 0.875 -> "Last Quarter"
                else -> "Waning Crescent"
            }
        } ?: "Unknown"
    
    companion object {
        /**
         * Creates a new WeatherForecast.
         */
        fun create(
            weatherId: Int,
            forecastDate: Long,
            minTemperature: Double? = null,
            maxTemperature: Double? = null,
            condition: String? = null,
            description: String? = null
        ): WeatherForecast {
            require(weatherId > 0) { "Weather ID must be positive" }
            require(forecastDate > 0) { "Forecast date must be positive" }
            
            return WeatherForecast(
                weatherId = weatherId,
                forecastDate = forecastDate,
                minTemperature = minTemperature,
                maxTemperature = maxTemperature,
                condition = condition?.trim(),
                description = description?.trim()
            )
        }
    }
}