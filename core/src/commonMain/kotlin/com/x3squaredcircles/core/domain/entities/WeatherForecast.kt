// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/WeatherForecast.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.core.domain.valueobjects.WindInfo
import kotlinx.datetime.Instant
/**

Individual weather forecast for a single day
 */
class WeatherForecast private constructor(
    private var _id: Int = 0,
    val weatherId: Int,
    val date: Instant,
    val sunrise: Instant,
    val sunset: Instant,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val description: String,
    val icon: String,
    val wind: WindInfo,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val uvIndex: Double,
    private var _precipitation: Double? = null,
    private var _moonRise: Instant? = null,
    private var _moonSet: Instant? = null,
    private var _moonPhase: Double = 0.0
) : Entity() {
    override val id: Int get() = _id
    val precipitation: Double? get() = _precipitation
    val moonRise: Instant? get() = _moonRise
    val moonSet: Instant? get() = _moonSet
    val moonPhase: Double get() = _moonPhase
    companion object {
        /**
         * Creates a new WeatherForecast instance
         */
        fun create(
            weatherId: Int,
            date: Instant,
            sunrise: Instant,
            sunset: Instant,
            temperature: Double,
            minTemperature: Double,
            maxTemperature: Double,
            description: String,
            icon: String,
            wind: WindInfo,
            humidity: Int,
            pressure: Int,
            clouds: Int,
            uvIndex: Double
        ): WeatherForecast {
            require(humidity in 0..100) { "Humidity must be between 0 and 100" }
            require(clouds in 0..100) { "Clouds must be between 0 and 100" }
            return WeatherForecast(
                weatherId = weatherId,
                date = date,
                sunrise = sunrise,
                sunset = sunset,
                temperature = temperature,
                minTemperature = minTemperature,
                maxTemperature = maxTemperature,
                description = description,
                icon = icon,
                wind = wind,
                humidity = humidity,
                pressure = pressure,
                clouds = clouds,
                uvIndex = uvIndex
            )
        }

        /**
         * Creates a WeatherForecast instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            weatherId: Int,
            date: Instant,
            sunrise: Instant,
            sunset: Instant,
            temperature: Double,
            minTemperature: Double,
            maxTemperature: Double,
            description: String,
            icon: String,
            wind: WindInfo,
            humidity: Int,
            pressure: Int,
            clouds: Int,
            uvIndex: Double,
            precipitation: Double? = null,
            moonRise: Instant? = null,
            moonSet: Instant? = null,
            moonPhase: Double = 0.0
        ): WeatherForecast {
            return WeatherForecast(
                _id = id,
                weatherId = weatherId,
                date = date,
                sunrise = sunrise,
                sunset = sunset,
                temperature = temperature,
                minTemperature = minTemperature,
                maxTemperature = maxTemperature,
                description = description,
                icon = icon,
                wind = wind,
                humidity = humidity,
                pressure = pressure,
                clouds = clouds,
                uvIndex = uvIndex,
                _precipitation = precipitation,
                _moonRise = moonRise,
                _moonSet = moonSet,
                _moonPhase = moonPhase
            )
        }
    }
    /**

    Sets moon phase data
     */
    fun setMoonData(moonRise: Instant?, moonSet: Instant?, moonPhase: Double) {
        _moonRise = moonRise
        _moonSet = moonSet
        _moonPhase = maxOf(0.0, minOf(1.0, moonPhase)) // Clamp between 0 and 1
    }

    /**

    Sets precipitation amount
     */
    fun setPrecipitation(precipitation: Double) {
        _precipitation = maxOf(0.0, precipitation)
    }

    /**

    Gets moon phase description
     */
    fun getMoonPhaseDescription(): String {
        return when (_moonPhase) {
            in 0.0..0.03 -> "New Moon"
            in 0.03..0.22 -> "Waxing Crescent"
            in 0.22..0.28 -> "First Quarter"
            in 0.28..0.47 -> "Waxing Gibbous"
            in 0.47..0.53 -> "Full Moon"
            in 0.53..0.72 -> "Waning Gibbous"
            in 0.72..0.78 -> "Last Quarter"
            in 0.78..0.97 -> "Waning Crescent"
            else -> "New Moon"
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