// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILocationRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate

interface ILocationRepository {
    suspend fun getByIdAsync(id: Int): Location?
    suspend fun getAllAsync(): List<Location>
    suspend fun getActiveAsync(): List<Location>
    suspend fun getByTitleAsync(title: String): Location?
    suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        includeDeleted: Boolean = false,
        searchTerm: String? = null
    ): List<Location>
    suspend fun getTotalCountAsync(includeDeleted: Boolean = false, searchTerm: String? = null): Long
    suspend fun getNearbyAsync(
        centerCoordinate: Coordinate,
        radiusKm: Double,
        limit: Int = 50
    ): List<Location>
    suspend fun searchByTextAsync(searchTerm: String, includeDeleted: Boolean = false): List<Location>
    suspend fun getByBoundsAsync(
        southLat: Double,
        northLat: Double,
        westLon: Double,
        eastLon: Double
    ): List<Location>
    suspend fun getRecentAsync(count: Int = 10): List<Location>
    suspend fun getModifiedSinceAsync(timestamp: Long): List<Location>
    suspend fun getRandomAsync(): Location?
    suspend fun addAsync(location: Location): Location
    suspend fun updateAsync(location: Location)
    suspend fun deleteAsync(location: Location)
    suspend fun softDeleteAsync(location: Location)
    suspend fun restoreAsync(location: Location)
    suspend fun hardDeleteAsync(location: Location)
    suspend fun updateCoordinatesAsync(id: Int, coordinate: Coordinate)
    suspend fun updatePhotoPathAsync(id: Int, photoPath: String?)
    suspend fun existsByIdAsync(id: Int): Boolean
    suspend fun existsByTitleAsync(title: String, excludeId: Int = 0): Boolean
    suspend fun createBulkAsync(locations: List<Location>): List<Location>
    suspend fun updateBulkAsync(locations: List<Location>): Int
    suspend fun deleteBulkAsync(locationIds: List<Int>): Int
    suspend fun cleanupDeletedAsync(olderThanTimestamp: Long): Int
    suspend fun getStatsAsync(): LocationStats
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