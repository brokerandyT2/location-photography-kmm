// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IDailyForecastRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant

interface IDailyForecastRepository {
    suspend fun getByIdAsync(id: Int): Result<WeatherForecast?>
    suspend fun getAllAsync(): Result<List<WeatherForecast>>
    suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<WeatherForecast>>
    suspend fun getByWeatherAndDateRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant): Result<List<WeatherForecast>>
    suspend fun getNext7DaysAsync(weatherId: Int, startDate: Instant): Result<List<WeatherForecast>>
    suspend fun getNext14DaysAsync(weatherId: Int, startDate: Instant): Result<List<WeatherForecast>>
    suspend fun getByDateAsync(weatherId: Int, forecastDate: Instant): Result<WeatherForecast?>
    suspend fun getCurrentAsync(weatherId: Int, currentTime: Instant): Result<WeatherForecast?>
    suspend fun getBestConditionsInRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant, limit: Int): Result<List<WeatherForecast>>
    suspend fun getBestPhotographyDaysAsync(weatherId: Int, startDate: Instant, endDate: Instant, maxCloudCover: Double, maxPrecipitationChance: Double, limit: Int): Result<List<WeatherForecast>>
    suspend fun getClearDaysAsync(weatherId: Int, startDate: Instant, endDate: Instant): Result<List<WeatherForecast>>
    suspend fun getSunriseSunsetAsync(weatherId: Int, forecastDate: Instant): Result<Pair<Instant?, Instant?>>
    suspend fun getMoonPhaseAsync(weatherId: Int, forecastDate: Instant): Result<Double?>
    suspend fun getByLocationIdAsync(locationId: Int): Result<List<WeatherForecast>>
    suspend fun getByLocationAndDateRangeAsync(locationId: Int, startDate: Instant, endDate: Instant): Result<List<WeatherForecast>>
    suspend fun createAsync(dailyForecast: WeatherForecast): Result<WeatherForecast>
    suspend fun createBatchAsync(dailyForecasts: List<WeatherForecast>): Result<List<WeatherForecast>>
    suspend fun updateAsync(dailyForecast: WeatherForecast): Result<Unit>
    suspend fun upsertByWeatherAndDateAsync(dailyForecast: WeatherForecast): Result<WeatherForecast>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Unit>
    suspend fun deleteOlderThanAsync(date: Instant): Result<Int>
    suspend fun deleteByWeatherAndDateRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant): Result<Int>
    suspend fun getCountAsync(): Result<Long>
    suspend fun getCountByWeatherAsync(weatherId: Int): Result<Long>
    suspend fun existsForWeatherAndDateAsync(weatherId: Int, forecastDate: Instant): Result<Boolean>
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(weatherId: Int, cacheType: DailyForecastCacheType)
}

enum class DailyForecastCacheType {
    BY_ID,
    BY_WEATHER_ID,
    BY_DATE_RANGE,
    BY_LOCATION_ID,
    ALL
}