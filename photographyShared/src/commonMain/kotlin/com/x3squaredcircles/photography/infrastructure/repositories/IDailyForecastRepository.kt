// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/IDailyForecastRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.WeatherForecast

/**
 * Repository interface for WeatherForecast (daily forecast) entity operations.
 */
interface IDailyForecastRepository {
    
    suspend fun getAllAsync(): Result<List<WeatherForecast>>
    suspend fun getByIdAsync(id: Int): Result<WeatherForecast>
    suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<WeatherForecast>>
    suspend fun getByWeatherAndDateRangeAsync(weatherId: Int, startDate: Long, endDate: Long): Result<List<WeatherForecast>>
    suspend fun getNext7DaysAsync(weatherId: Int, fromDate: Long): Result<List<WeatherForecast>>
    suspend fun getForDateAsync(weatherId: Int, date: Long): Result<WeatherForecast>
    suspend fun getCurrentForecastAsync(weatherId: Int): Result<WeatherForecast>
    suspend fun getBestPhotographyDaysAsync(weatherId: Int, startDate: Long, endDate: Long, count: Int): Result<List<WeatherForecast>>
    suspend fun getByLocationIdAsync(locationId: Int): Result<List<WeatherForecast>>
    suspend fun getByLocationAndDateRangeAsync(locationId: Int, startDate: Long, endDate: Long): Result<List<WeatherForecast>>
    suspend fun getSunriseSunsetAsync(weatherId: Int, date: Long): Result<Pair<Long?, Long?>>
    suspend fun getMoonPhaseAsync(weatherId: Int, date: Long): Result<Double?>
    suspend fun createAsync(dailyForecast: WeatherForecast): Result<WeatherForecast>
    suspend fun createBatchAsync(dailyForecasts: List<WeatherForecast>): Result<List<WeatherForecast>>
    suspend fun updateAsync(dailyForecast: WeatherForecast): Result<WeatherForecast>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean>
    suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int>
    suspend fun deleteByWeatherAndDateRangeAsync(weatherId: Int, startDate: Long, endDate: Long): Result<Int>
    suspend fun getCountAsync(): Result<Int>
    suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int>
    suspend fun existsForWeatherAndDateAsync(weatherId: Int, date: Long): Result<Boolean>
}