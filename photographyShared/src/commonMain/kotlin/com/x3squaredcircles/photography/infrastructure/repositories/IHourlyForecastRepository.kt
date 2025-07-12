// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/IHourlyForecastRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.HourlyForecast

/**
 * Repository interface for HourlyForecast entity operations.
 */
interface IHourlyForecastRepository {
    
    suspend fun getAllAsync(): Result<List<HourlyForecast>>
    suspend fun getByIdAsync(id: Int): Result<HourlyForecast>
    suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<HourlyForecast>>
    suspend fun getByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>>
    suspend fun getNext24HoursAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>>
    suspend fun getNext7DaysAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>>
    suspend fun getForDayAsync(weatherId: Int, startOfDay: Long, endOfDay: Long): Result<List<HourlyForecast>>
    suspend fun getBestConditionsInRangeAsync(weatherId: Int, startTime: Long, endTime: Long, count: Int): Result<List<HourlyForecast>>
    suspend fun getGoldenHoursAsync(weatherId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>>
    suspend fun getByLocationIdAsync(locationId: Int): Result<List<HourlyForecast>>
    suspend fun getByLocationAndTimeRangeAsync(locationId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>>
    suspend fun createAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast>
    suspend fun createBatchAsync(hourlyForecasts: List<HourlyForecast>): Result<List<HourlyForecast>>
    suspend fun updateAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean>
    suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int>
    suspend fun deleteByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Long, endTime: Long): Result<Int>
    suspend fun getCountAsync(): Result<Int>
    suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int>
    suspend fun existsForWeatherAndTimeAsync(weatherId: Int, forecastTime: Long): Result<Boolean>
}