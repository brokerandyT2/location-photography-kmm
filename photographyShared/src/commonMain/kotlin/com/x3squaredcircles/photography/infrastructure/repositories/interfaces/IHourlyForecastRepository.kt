// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IHourlyForecastRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant

interface IHourlyForecastRepository {
    suspend fun getByIdAsync(id: Int): Result<HourlyForecast?>
    suspend fun getAllAsync(): Result<List<HourlyForecast>>
    suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<HourlyForecast>>
    suspend fun getByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant): Result<List<HourlyForecast>>
    suspend fun getNext24HoursAsync(weatherId: Int, fromTime: Instant): Result<List<HourlyForecast>>
    suspend fun getNext7DaysAsync(weatherId: Int, startTime: Instant, endTime: Instant): Result<List<HourlyForecast>>
    suspend fun getForDayAsync(weatherId: Int, date: Instant): Result<List<HourlyForecast>>
    suspend fun getBestConditionsInRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant, limit: Int): Result<List<HourlyForecast>>
    suspend fun getGoldenHoursAsync(weatherId: Int, startTime: Instant, endTime: Instant): Result<List<HourlyForecast>>
    suspend fun getByLocationIdAsync(locationId: Int): Result<List<HourlyForecast>>
    suspend fun getByLocationAndTimeRangeAsync(locationId: Int, startTime: Instant, endTime: Instant): Result<List<HourlyForecast>>
    suspend fun createAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast>
    suspend fun createBatchAsync(hourlyForecasts: List<HourlyForecast>): Result<List<HourlyForecast>>
    suspend fun updateAsync(hourlyForecast: HourlyForecast): Result<Unit>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Unit>
    suspend fun deleteOlderThanAsync(date: Instant): Result<Int>
    suspend fun deleteByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Instant, endTime: Instant): Result<Int>
    suspend fun getCountAsync(): Result<Long>
    suspend fun getCountByWeatherAsync(weatherId: Int): Result<Long>
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