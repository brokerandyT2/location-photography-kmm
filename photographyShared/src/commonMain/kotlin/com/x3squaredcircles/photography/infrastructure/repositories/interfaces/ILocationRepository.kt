// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILocationRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.common.Result

interface ILocationRepository {
    suspend fun getByIdAsync(id: Int): Result<Location?>
    suspend fun getAllAsync(): Result<List<Location>>
    suspend fun getActiveAsync(): Result<List<Location>>
    suspend fun getByTitleAsync(title: String): Result<Location?>
    suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        includeDeleted: Boolean = false,
        searchTerm: String? = null
    ): Result<List<Location>>
    suspend fun getTotalCountAsync(includeDeleted: Boolean = false, searchTerm: String? = null): Result<Long>
    suspend fun getNearbyAsync(
        centerCoordinate: Coordinate,
        radiusKm: Double,
        limit: Int = 50
    ): Result<List<Location>>
    suspend fun searchByTextAsync(searchTerm: String, includeDeleted: Boolean = false): Result<List<Location>>
    suspend fun getByBoundsAsync(
        southLat: Double,
        northLat: Double,
        westLon: Double,
        eastLon: Double
    ): Result<List<Location>>
    suspend fun getRecentAsync(count: Int = 10): Result<List<Location>>
    suspend fun getModifiedSinceAsync(timestamp: Long): Result<List<Location>>
    suspend fun getRandomAsync(): Result<Location?>
    suspend fun createAsync(location: Location): Result<Location>
    suspend fun updateAsync(location: Location): Result<Unit>
    suspend fun deleteAsync(location: Location): Result<Unit>
    suspend fun softDeleteAsync(location: Location): Result<Unit>
    suspend fun restoreAsync(location: Location): Result<Unit>
    suspend fun hardDeleteAsync(location: Location): Result<Unit>
    suspend fun updateCoordinatesAsync(id: Int, coordinate: Coordinate): Result<Unit>
    suspend fun updatePhotoPathAsync(id: Int, photoPath: String?): Result<Unit>
    suspend fun existsByIdAsync(id: Int): Result<Boolean>
    suspend fun existsByTitleAsync(title: String, excludeId: Int = 0): Result<Boolean>
    suspend fun createBulkAsync(locations: List<Location>): Result<List<Location>>
    suspend fun updateBulkAsync(locations: List<Location>): Result<Int>
    suspend fun deleteBulkAsync(locationIds: List<Int>): Result<Int>
    suspend fun cleanupDeletedAsync(olderThanTimestamp: Long): Result<Int>
    suspend fun getStatsAsync(): Result<LocationStats>
    fun clearCache()
    fun clearCache(id: Int)
}

data class LocationStats(
    val totalCount: Long,
    val activeCount: Long,
    val deletedCount: Long,
    val withPhotosCount: Long,
    val oldestTimestamp: Long?,
    val newestTimestamp: Long?
)