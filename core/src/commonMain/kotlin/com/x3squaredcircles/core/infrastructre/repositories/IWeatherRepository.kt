// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/IWeatherRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Weather

/**
 * Repository interface for Weather entity operations.
 * Defines the contract for weather data access without specifying implementation details.
 */
interface IWeatherRepository {
    
    /**
     * Gets weather data by its unique identifier.
     */
    suspend fun getByIdAsync(id: Int): Result<Weather>
    
    /**
     * Gets the most recent weather data for a specific location.
     */
    suspend fun getByLocationIdAsync(locationId: Int): Result<Weather>
    
    /**
     * Gets weather data by coordinates (latitude, longitude).
     */
    suspend fun getByCoordinatesAsync(latitude: Double, longitude: Double): Result<Weather>
    
    /**
     * Creates new weather data.
     */
    suspend fun createAsync(weather: Weather): Result<Weather>
    
    /**
     * Updates existing weather data.
     */
    suspend fun updateAsync(weather: Weather): Result<Weather>
    
    /**
     * Soft deletes weather data by marking it as deleted.
     */
    suspend fun deleteAsync(weather: Weather): Result<Boolean>
    
    /**
     * Deletes weather data by ID.
     */
    suspend fun deleteByIdAsync(id: Int): Result<Boolean>
    
    /**
     * Gets the most recent weather data entries (useful for caching/display).
     */
    suspend fun getRecentAsync(count: Int = 10): Result<List<Weather>>
    
    /**
     * Gets weather data that is older than the specified age in milliseconds.
     * Useful for cleanup operations.
     */
    suspend fun getExpiredAsync(maxAgeMillis: Long): Result<List<Weather>>
    
    /**
     * Gets all weather data for a location within a time range.
     */
    suspend fun getByLocationAndTimeRangeAsync(
        locationId: Int,
        startTime: Long,
        endTime: Long
    ): Result<List<Weather>>
    
    /**
     * Gets weather data within a geographic radius of specified coordinates.
     */
    suspend fun getNearbyWeatherAsync(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<Weather>>
    
    /**
     * Upserts weather data - updates if exists, creates if new.
     * Useful for caching weather API responses.
     */
    suspend fun upsertAsync(weather: Weather): Result<Weather>
    
    /**
     * Checks if fresh weather data exists for a location (within specified age).
     */
    suspend fun hasFreshDataAsync(
        locationId: Int,
        maxAgeMillis: Long = Weather.DEFAULT_STALE_THRESHOLD_MILLIS
    ): Result<Boolean>
    
    /**
     * Checks if fresh weather data exists for coordinates (within specified age).
     */
    suspend fun hasFreshDataForCoordinatesAsync(
        latitude: Double,
        longitude: Double,
        maxAgeMillis: Long = Weather.DEFAULT_STALE_THRESHOLD_MILLIS
    ): Result<Boolean>
    
    /**
     * Deletes all expired weather data older than the specified threshold.
     * Returns the number of records deleted.
     */
    suspend fun cleanupExpiredDataAsync(maxAgeMillis: Long): Result<Int>
    
    /**
     * Gets the total count of weather records.
     */
    suspend fun getCountAsync(): Result<Int>
    
    /**
     * Gets weather data grouped by location ID.
     */
    suspend fun getGroupedByLocationAsync(): Result<Map<Int, List<Weather>>>
}