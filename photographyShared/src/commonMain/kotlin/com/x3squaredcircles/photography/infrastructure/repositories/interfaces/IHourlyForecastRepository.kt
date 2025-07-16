// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IHourlyForecastRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.HourlyForecast
import kotlinx.datetime.Instant

interface IHourlyForecastRepository {
    suspend fun getByIdAsync(id: Int): HourlyForecast?
    suspend fun getAllAsync(): List<HourlyForecast>
    suspend fun getByWeatherIdAsync(weatherId: Int): List<HourlyForecast>
    suspend fun getByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant): List<HourlyForecast>
    suspend fun getNext24HoursAsync(weatherId: Int, fromTime: Instant): List<HourlyForecast>
    suspend fun getNext7DaysAsync(weatherId: Int, startTime: Instant, endTime: Instant): List<HourlyForecast>
    suspend fun getForDayAsync(weatherId: Int, date: Instant): List<HourlyForecast>
    suspend fun getBestConditionsInRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant, limit: Int): List<HourlyForecast>
    suspend fun getGoldenHoursAsync(weatherId: Int, startTime: Instant, endTime: Instant): List<HourlyForecast>
    suspend fun getByLocationIdAsync(locationId: Int): List<HourlyForecast>
    suspend fun getByLocationAndTimeRangeAsync(locationId: Int, startTime: Instant, endTime: Instant): List<HourlyForecast>
    suspend fun addAsync(hourlyForecast: HourlyForecast): HourlyForecast
    suspend fun addBatchAsync(hourlyForecasts: List<HourlyForecast>): List<HourlyForecast>
    suspend fun updateAsync(hourlyForecast: HourlyForecast)
    suspend fun deleteAsync(id: Int)
    suspend fun deleteByWeatherIdAsync(weatherId: Int)
    suspend fun deleteOlderThanAsync(date: Instant): Int
    suspend fun deleteByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant): Int
    suspend fun getCountAsync(): Long
    suspend fun getCountByWeatherAsync(weatherId: Int): Long
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(weatherId: Int, cacheType: HourlyForecastCacheType)
}

enum class HourlyForecastCacheType {
    BY_ID,
    BY_WEATHER_ID,
    BY_TIME_RANGE,
    BY_LOCATION_ID,
    ALL
}