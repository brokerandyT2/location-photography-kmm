// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ILocationRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.models.PagedList


/**
 * Repository interface for Location entity operations.
 * Defines the contract for data access without specifying implementation details.
 */
interface ILocationRepository {
    
    /**
     * Gets a location by its unique identifier.
     */
    suspend fun getByIdAsync(id: Int): Result<Location>
    
    /**
     * Gets all locations (including deleted ones).
     */
    suspend fun getAllAsync(): Result<List<Location>>
    
    /**
     * Gets only active (non-deleted) locations.
     */
    suspend fun getActiveAsync(): Result<List<Location>>
    
    /**
     * Creates a new location.
     */
    suspend fun createAsync(location: Location): Result<Location>
    
    /**
     * Updates an existing location.
     */
    suspend fun updateAsync(location: Location): Result<Location>
    
    /**
     * Soft deletes a location by marking it as deleted.
     */
    suspend fun deleteAsync(id: Int): Result<Boolean>
    
    /**
     * Gets a location by its title.
     */
    suspend fun getByTitleAsync(title: String): Result<Location>
    
    /**
     * Gets locations within a specified distance of the given coordinates.
     */
    suspend fun getNearbyAsync(
        latitude: Double,
        longitude: Double,
        distanceKm: Double
    ): Result<List<Location>>
    
    /**
     * Gets locations with paging support and optional filtering.
     */
    suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        searchTerm: String? = null,
        includeDeleted: Boolean = false
    ): Result<PagedList<Location>>
    
    /**
     * Checks if a location exists with the specified ID.
     */
    suspend fun existsByIdAsync(id: Int): Result<Boolean>
    
    /**
     * Gets the total count of locations (optionally including deleted).
     */
    suspend fun getCountAsync(includeDeleted: Boolean = false): Result<Int>
    
    /**
     * Searches locations by title or description.
     */
    suspend fun searchAsync(searchTerm: String, includeDeleted: Boolean = false): Result<List<Location>>
}