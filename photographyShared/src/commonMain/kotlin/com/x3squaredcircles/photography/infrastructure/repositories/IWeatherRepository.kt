// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/IWeatherRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Weather

/**
 * Repository interface for Weather entity operations.
 */
interface IWeatherRepository {
    
    suspend fun getAllAsync(): Result<List<Weather>>
    suspend fun getByIdAsync(id: Int): Result<Weather>
    suspend fun getByLocationIdAsync(locationId: Int): Result<Weather>
    suspend fun getByCoordinatesAsync(latitude: Double, longitude: Double): Result<Weather>
    suspend fun getByLocationAndTimeRangeAsync(locationId: Int, startTime: Long, endTime: Long): Result<List<Weather>>
    suspend fun getNearbyWeatherAsync(latitude: Double, longitude: Double, radiusKm: Double): Result<List<Weather>>
    suspend fun getRecentAsync(count: Int = 10): Result<List<Weather>>
    suspend fun getExpiredAsync(maxAgeMillis: Long): Result<List<Weather>>
    suspend fun createAsync(weather: Weather): Result<Weather>
    suspend fun updateAsync(weather: Weather): Result<Weather>
    suspend fun upsertAsync(weather: Weather): Result<Weather>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByLocationIdAsync(locationId: Int): Result<Boolean>
    suspend fun hasFreshDataAsync(locationId: Int, maxAgeMillis: Long): Result<Boolean>
    suspend fun hasFreshDataForCoordinatesAsync(latitude: Double, longitude: Double, maxAgeMillis: Long): Result<Boolean>
    suspend fun cleanupExpiredDataAsync(maxAgeMillis: Long): Result<Int>
    suspend fun getCountAsync(): Result<Int>
    suspend fun getGroupedByLocationAsync(): Result<Map<Int, List<Weather>>>
}