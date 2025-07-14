// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/HourlyForecast.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.core.domain.valueobjects.WindInfo
import kotlinx.datetime.Instant
/**

Individual weather forecast for a single hour
 */
class HourlyForecast private constructor(
    private var _id: Int = 0,
    val weatherId: Int,
    val dateTime: Instant,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val wind: WindInfo,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val uvIndex: Double,
    val probabilityOfPrecipitation: Double,
    val visibility: Int,
    val dewPoint: Double
) : Entity() {
    override val id: Int get() = _id
    companion object {
        /**
         * Creates a new HourlyForecast instance
         */
        fun create(
            weatherId: Int,
            dateTime: Instant,
            temperature: Double,
            feelsLike: Double,
            description: String,
            icon: String,
            wind: WindInfo,
            humidity: Int,
            pressure: Int,
            clouds: Int,
            uvIndex: Double,
            probabilityOfPrecipitation: Double,
            visibility: Int,
            dewPoint: Double
        ): HourlyForecast {
            require(humidity in 0..100) { "Humidity must be between 0 and 100" }
            require(clouds in 0..100) { "Clouds must be between 0 and 100" }
            require(probabilityOfPrecipitation in 0.0..1.0) { "Probability of precipitation must be between 0 and 1" }
            require(visibility >= 0) { "Visibility cannot be negative" }
            require(uvIndex >= 0) { "UV index cannot be negative" }
            return HourlyForecast(
                weatherId = weatherId,
                dateTime = dateTime,
                temperature = temperature,
                feelsLike = feelsLike,
                description = description,
                icon = icon,
                wind = wind,
                humidity = humidity,
                pressure = pressure,
                clouds = clouds,
                uvIndex = uvIndex,
                probabilityOfPrecipitation = probabilityOfPrecipitation,
                visibility = visibility,
                dewPoint = dewPoint
            )
        }

        /**
         * Creates a HourlyForecast instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            weatherId: Int,
            dateTime: Instant,
            temperature: Double,
            feelsLike: Double,
            description: String,
            icon: String,
            wind: WindInfo,
            humidity: Int,
            pressure: Int,
            clouds: Int,
            uvIndex: Double,
            probabilityOfPrecipitation: Double,
            visibility: Int,
            dewPoint: Double
        ): HourlyForecast {
            return HourlyForecast(
                _id = id,
                weatherId = weatherId,
                dateTime = dateTime,
                temperature = temperature,
                feelsLike = feelsLike,
                description = description,
                icon = icon,
                wind = wind,
                humidity = humidity,
                pressure = pressure,
                clouds = clouds,
                uvIndex = uvIndex,
                probabilityOfPrecipitation = probabilityOfPrecipitation,
                visibility = visibility,
                dewPoint = dewPoint
            )
        }
    }
    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}