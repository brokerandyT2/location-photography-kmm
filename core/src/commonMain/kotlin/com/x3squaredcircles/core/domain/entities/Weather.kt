// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Weather.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.AggregateRoot
import com.x3squaredcircles.core.domain.events.WeatherUpdatedEvent
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
/**

Weather aggregate root containing weather data for a location
 */
class Weather private constructor(
    private var _id: Int = 0,
    val locationId: Int,
    private var _coordinate: Coordinate,
    private var _timezone: String,
    private var _timezoneOffset: Int,
    private var _lastUpdate: Instant = Clock.System.now()
) : AggregateRoot() {
    private val _forecasts = mutableListOf<WeatherForecast>()
    private val _hourlyForecasts = mutableListOf<HourlyForecast>()
    override val id: Int get() = _id
    val coordinate: Coordinate get() = _coordinate
    val lastUpdate: Instant get() = _lastUpdate
    val timezone: String get() = _timezone
    val timezoneOffset: Int get() = _timezoneOffset
    val forecasts: List<WeatherForecast> get() = _forecasts.toList()
    val hourlyForecasts: List<HourlyForecast> get() = _hourlyForecasts.toList()
    companion object {
        /**
         * Creates a new Weather instance
         */
        fun create(locationId: Int, coordinate: Coordinate, timezone: String, timezoneOffset: Int): Weather {
            return Weather(
                locationId = locationId,
                _coordinate = coordinate,
                _timezone = timezone,
                _timezoneOffset = timezoneOffset
            )
        }
        /**
         * Creates a Weather instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            locationId: Int,
            coordinate: Coordinate,
            timezone: String,
            timezoneOffset: Int,
            lastUpdate: Instant
        ): Weather {
            return Weather(
                _id = id,
                locationId = locationId,
                _coordinate = coordinate,
                _timezone = timezone,
                _timezoneOffset = timezoneOffset,
                _lastUpdate = lastUpdate
            )
        }
    }
    /**

    Updates weather forecasts
     */
    fun updateForecasts(forecasts: List<WeatherForecast>) {
        _forecasts.clear()
        _forecasts.addAll(forecasts.take(7)) // Limit to 7-day forecast
        _lastUpdate = Clock.System.now()
        addDomainEvent(WeatherUpdatedEvent(locationId, _lastUpdate))
    }

    /**

    Updates hourly forecasts
     */
    fun updateHourlyForecasts(hourlyForecasts: List<HourlyForecast>) {
        _hourlyForecasts.clear()
        _hourlyForecasts.addAll(hourlyForecasts.take(48)) // Limit to 48-hour forecast
        _lastUpdate = Clock.System.now()
        addDomainEvent(WeatherUpdatedEvent(locationId, _lastUpdate))
    }

    /**

    Gets forecast for a specific date
     */
    fun getForecastForDate(date: Instant): WeatherForecast? {
        return _forecasts.find { it.date == date }
    }

    /**

    Gets current forecast
     */
    fun getCurrentForecast(): WeatherForecast? {
        val now = Clock.System.now()
        return _forecasts.find { it.date >= now }
    }

    /**

    Gets hourly forecasts for a specific date range
     */
    fun getHourlyForecastsForRange(startTime: Instant, endTime: Instant): List<HourlyForecast> {
        return _hourlyForecasts.filter { it.dateTime >= startTime && it.dateTime <= endTime }
    }

    /**

    Gets current hourly forecast
     */
    fun getCurrentHourlyForecast(): HourlyForecast? {
        val now = Clock.System.now()
        return _hourlyForecasts.find { it.dateTime >= now }
    }

    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}