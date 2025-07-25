// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IWeatherRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant

interface IWeatherRepository {
    suspend fun getByIdAsync(id: Int): Result<Weather?>
    suspend fun getAllAsync(): Result<List<Weather>>
    suspend fun getByLocationIdAsync(locationId: Int): Result<Weather?>
    suspend fun getByCoordinatesAsync(coordinate: Coordinate): Result<Weather?>
    suspend fun getByLocationAndTimeRangeAsync(
        locationId: Int,
        startTime: Instant,
        endTime: Instant
    ): Result<List<Weather>>
    suspend fun getRecentAsync(count: Int = 10): Result<List<Weather>>
    suspend fun getExpiredAsync(olderThan: Instant): Result<List<Weather>>
    suspend fun createAsync(weather: Weather): Result<Weather>
    suspend fun updateAsync(weather: Weather): Result<Unit>
    suspend fun deleteAsync(weather: Weather): Result<Unit>
    suspend fun softDeleteAsync(weather: Weather): Result<Unit>
    suspend fun softDeleteByLocationIdAsync(locationId: Int): Result<Unit>
    suspend fun hasFreshDataAsync(locationId: Int, maxAge: Instant): Result<Boolean>
    suspend fun hasFreshDataForCoordinatesAsync(coordinate: Coordinate, maxAge: Instant): Result<Boolean>
    suspend fun cleanupExpiredAsync(olderThan: Instant): Result<Int>
    suspend fun getCountAsync(): Result<Long>
    suspend fun addForecastAsync(forecast: WeatherForecast): Result<WeatherForecast>
    suspend fun addForecastsAsync(forecasts: List<WeatherForecast>): Result<List<WeatherForecast>>
    suspend fun updateForecastAsync(forecast: WeatherForecast): Result<Unit>
    suspend fun deleteForecastAsync(forecast: WeatherForecast): Result<Unit>
    suspend fun deleteForecastsByWeatherIdAsync(weatherId: Int): Result<Unit>
    suspend fun getForecastsByWeatherIdAsync(weatherId: Int): Result<List<WeatherForecast>>
    suspend fun getForecastsByWeatherAndDateRangeAsync(
        weatherId: Int,
        startDate: Instant,
        endDate: Instant
    ): Result<List<WeatherForecast>>
    suspend fun getForecastByWeatherAndDateAsync(weatherId: Int, date: Instant): Result<WeatherForecast?>
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(locationId: Int, cacheType: WeatherCacheType)
}

enum class WeatherCacheType {
    BY_ID,
    BY_LOCATION_ID,
    BY_COORDINATES,
    BY_DATE_RANGE,
    ALL
}