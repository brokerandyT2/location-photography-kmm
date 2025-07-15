// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IWeatherRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import kotlinx.datetime.Instant

interface IWeatherRepository {
    suspend fun getByIdAsync(id: Int): Weather?
    suspend fun getAllAsync(): List<Weather>
    suspend fun getByLocationIdAsync(locationId: Int): Weather?
    suspend fun getByCoordinatesAsync(coordinate: Coordinate): Weather?
    suspend fun getByLocationAndTimeRangeAsync(
        locationId: Int,
        startTime: Instant,
        endTime: Instant
    ): List<Weather>
    suspend fun getRecentAsync(count: Int = 10): List<Weather>
    suspend fun getExpiredAsync(olderThan: Instant): List<Weather>
    suspend fun addAsync(weather: Weather): Weather
    suspend fun updateAsync(weather: Weather)
    suspend fun deleteAsync(weather: Weather)
    suspend fun softDeleteAsync(weather: Weather)
    suspend fun softDeleteByLocationIdAsync(locationId: Int)
    suspend fun hasFreshDataAsync(locationId: Int, maxAge: Instant): Boolean
    suspend fun hasFreshDataForCoordinatesAsync(coordinate: Coordinate, maxAge: Instant): Boolean
    suspend fun cleanupExpiredAsync(olderThan: Instant): Int
    suspend fun getCountAsync(): Long
    suspend fun addForecastAsync(forecast: WeatherForecast): WeatherForecast
    suspend fun addForecastsAsync(forecasts: List<WeatherForecast>): List<WeatherForecast>
    suspend fun updateForecastAsync(forecast: WeatherForecast)
    suspend fun deleteForecastAsync(forecast: WeatherForecast)
    suspend fun deleteForecastsByWeatherIdAsync(weatherId: Int)
    suspend fun getForecastsByWeatherIdAsync(weatherId: Int): List<WeatherForecast>
    suspend fun getForecastsByWeatherAndDateRangeAsync(
        weatherId: Int,
        startDate: Instant,
        endDate: Instant
    ): List<WeatherForecast>
    suspend fun getForecastByWeatherAndDateAsync(weatherId: Int, date: Instant): WeatherForecast?
    suspend fun addHourlyForecastAsync(hourlyForecast: HourlyForecast): HourlyForecast
    suspend fun addHourlyForecastsAsync(hourlyForecasts: List<HourlyForecast>): List<HourlyForecast>
    suspend fun updateHourlyForecastAsync(hourlyForecast: HourlyForecast)
    suspend fun deleteHourlyForecastAsync(hourlyForecast: HourlyForecast)
    suspend fun deleteHourlyForecastsByWeatherIdAsync(weatherId: Int)
    suspend fun getHourlyForecastsByWeatherIdAsync(weatherId: Int): List<HourlyForecast>
    suspend fun getHourlyForecastsByWeatherAndTimeRangeAsync(
        weatherId: Int,
        startTime: Instant,
        endTime: Instant
    ): List<HourlyForecast>
    suspend fun getNext24HoursForecastAsync(weatherId: Int, fromTime: Instant): List<HourlyForecast>
    suspend fun getHourlyForecastsForDayAsync(weatherId: Int, date: Instant): List<HourlyForecast>
    suspend fun cleanupOldForecastsAsync(olderThan: Instant): Int
    suspend fun cleanupOldHourlyForecastsAsync(olderThan: Instant): Int
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(locationId: Int, cacheType: WeatherCacheType)
}

enum class WeatherCacheType {
    WEATHER,
    DAILY_FORECAST,
    HOURLY_FORECAST,
    ALL
}