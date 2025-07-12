// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/HourlyForecast.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents hourly weather forecast data for photography planning.
 */
@Serializable
data class HourlyForecast(
    override val id: Int = 0,
    val weatherId: Int,
    val forecastTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val visibility: Double,
    val uvIndex: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val windGust: Double? = null,
    val cloudCover: Int,
    val precipitationChance: Double,
    val precipitationAmount: Double? = null,
    val condition: String,
    val description: String,
    val icon: String
) : Entity() {
    
    /**
     * Gets the forecast time as a LocalDateTime in the current system timezone.
     */
    val forecastDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(forecastTime)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Determines if conditions are good for photography.
     */
    val isGoodForPhotography: Boolean
        get() = cloudCover < 80 && precipitationChance < 0.3 && visibility > 5.0 && windSpeed < 25.0
    
    /**
     * Determines if this is during golden hour (soft lighting conditions).
     */
    val isGoldenHour: Boolean
        get() {
            val hour = forecastDateTime.hour
            return (hour >= 6 && hour <= 8) || (hour >= 17 && hour <= 19)
        }
    
    /**
     * Gets a photography suitability score (0-100).
     */
    val photographySuitabilityScore: Int
        get() {
            var score = 100
            
            // Cloud cover impact (0-40 points)
            score -= (cloudCover * 0.4).toInt()
            
            // Precipitation impact (0-30 points)
            score -= (precipitationChance * 30).toInt()
            
            // Visibility impact (0-20 points)
            if (visibility < 10.0) {
                score -= ((10.0 - visibility) * 2).toInt()
            }
            
            // Wind impact (0-10 points)
            if (windSpeed > 15.0) {
                score -= ((windSpeed - 15.0) * 0.5).toInt()
            }
            
            return maxOf(0, minOf(100, score))
        }
    
    /**
     * Gets the wind direction as a compass direction.
     */
    val windDirectionCompass: String
        get() = when ((windDirection + 11.25) / 22.5) {
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
    
    /**
     * Gets a temperature display string.
     */
    val temperatureDisplay: String
        get() = "${temperature.toInt()}°C"
    
    /**
     * Gets a wind display string.
     */
    val windDisplay: String
        get() = "${windSpeed.toInt()} km/h $windDirectionCompass"
    
    companion object {
        /**
         * Creates a new HourlyForecast.
         */
        fun create(
            weatherId: Int,
            forecastTime: Long,
            temperature: Double,
            feelsLike: Double,
            humidity: Int,
            pressure: Int,
            visibility: Double,
            uvIndex: Double,
            windSpeed: Double,
            windDirection: Double,
            windGust: Double? = null,
            cloudCover: Int,
            precipitationChance: Double,
            precipitationAmount: Double? = null,
            condition: String,
            description: String,
            icon: String
        ): HourlyForecast {
            require(weatherId > 0) { "Weather ID must be positive" }
            require(humidity in 0..100) { "Humidity must be between 0 and 100" }
            require(cloudCover in 0..100) { "Cloud cover must be between 0 and 100" }
            require(precipitationChance in 0.0..1.0) { "Precipitation chance must be between 0 and 1" }
            require(condition.isNotBlank()) { "Condition cannot be blank" }
            
            return HourlyForecast(
                weatherId = weatherId,
                forecastTime = forecastTime,
                temperature = temperature,
                feelsLike = feelsLike,
                humidity = humidity,
                pressure = pressure,
                visibility = visibility,
                uvIndex = maxOf(0.0, uvIndex),
                windSpeed = maxOf(0.0, windSpeed),
                windDirection = windDirection,
                windGust = windGust,
                cloudCover = cloudCover,
                precipitationChance = precipitationChance,
                precipitationAmount = precipitationAmount,
                condition = condition.trim(),
                description = description.trim(),
                icon = icon.trim()
            )
        }
    }
}