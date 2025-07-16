// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IDailyForecastRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.WeatherForecast
import kotlinx.datetime.Instant

interface IDailyForecastRepository {
    suspend fun getByIdAsync(id: Int): WeatherForecast?
    suspend fun getAllAsync(): List<WeatherForecast>
    suspend fun getByWeatherIdAsync(weatherId: Int): List<WeatherForecast>
    suspend fun getByWeatherAndDateRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant): List<WeatherForecast>
    suspend fun getNext7DaysAsync(weatherId: Int, startDate: Instant): List<WeatherForecast>
    suspend fun getNext14DaysAsync(weatherId: Int, startDate: Instant): List<WeatherForecast>
    suspend fun getByDateAsync(weatherId: Int, forecastDate: Instant): WeatherForecast?
    suspend fun getCurrentAsync(weatherId: Int, currentTime: Instant): WeatherForecast?
    suspend fun getBestConditionsInRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant, limit: Int): List<WeatherForecast>
    suspend fun getBestPhotographyDaysAsync(weatherId: Int, startDate: Instant, endDate: Instant, maxCloudCover: Double, maxPrecipitationChance: Double, limit: Int): List<WeatherForecast>
    suspend fun getClearDaysAsync(weatherId: Int, startDate: Instant, endDate: Instant): List<WeatherForecast>
    suspend fun getSunriseSunsetAsync(weatherId: Int, forecastDate: Instant): Pair<Instant?, Instant?>
    suspend fun getMoonPhaseAsync(weatherId: Int, forecastDate: Instant): Double?
    suspend fun getByLocationIdAsync(locationId: Int): List<WeatherForecast>
    suspend fun getByLocationAndDateRangeAsync(locationId: Int, startDate: Instant, endDate: Instant): List<WeatherForecast>
    suspend fun addAsync(dailyForecast: WeatherForecast): WeatherForecast
    suspend fun addBatchAsync(dailyForecasts: List<WeatherForecast>): List<WeatherForecast>
    suspend fun updateAsync(dailyForecast: WeatherForecast)
    suspend fun upsertByWeatherAndDateAsync(dailyForecast: WeatherForecast): WeatherForecast
    suspend fun deleteAsync(id: Int)
    suspend fun deleteByWeatherIdAsync(weatherId: Int)
    suspend fun deleteOlderThanAsync(date: Instant): Int
    suspend fun deleteByWeatherAndDateRangeAsync(weatherId: Int, startDate: Instant, endDate: Instant): Int
    suspend fun getCountAsync(): Long
    suspend fun getCountByWeatherAsync(weatherId: Int): Long
    suspend fun existsForWeatherAndDateAsync(weatherId: Int, forecastDate: Instant): Boolean
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