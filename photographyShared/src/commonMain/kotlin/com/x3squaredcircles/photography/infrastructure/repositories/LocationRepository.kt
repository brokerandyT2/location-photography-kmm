// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LocationRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.LocationStats
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.*
import kotlin.time.Duration.Companion.minutes

class LocationRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ILocationRepository {

    private val locationsCache = mutableMapOf<Int, CachedLocation>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 30.minutes

    override suspend fun getByIdAsync(id: Int): Result<Location?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.locationQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): Result<List<Location>> {
        return executeWithExceptionMapping("GetAll") {
            database.locationQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getActiveAsync(): Result<List<Location>> {
        return executeWithExceptionMapping("GetActive") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByTitleAsync(title: String): Result<Location?> {
        return executeWithExceptionMapping("GetByTitle") {
            val entity = database.locationQueries.selectByTitle(title).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        includeDeleted: Boolean,
        searchTerm: String?
    ): Result<List<Location>> {
        return executeWithExceptionMapping("GetPaged") {
            val offset = (pageNumber - 1) * pageSize
            val entities = if (includeDeleted) {
                database.locationQueries.selectAll()
            } else {
                database.locationQueries.selectActive()
            }.executeAsList()

            val filteredEntities = if (searchTerm != null) {
                entities.filter {
                    it.title.contains(searchTerm, ignoreCase = true) ||
                            it.description?.contains(searchTerm, ignoreCase = true) == true
                }
            } else {
                entities
            }

            filteredEntities
                .drop(offset)
                .take(pageSize)
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getTotalCountAsync(includeDeleted: Boolean, searchTerm: String?): Result<Long> {
        return executeWithExceptionMapping("GetTotalCount") {
            val entities = if (includeDeleted) {
                database.locationQueries.selectAll()
            } else {
                database.locationQueries.selectActive()
            }.executeAsList()

            if (searchTerm != null) {
                entities.count {
                    it.title.contains(searchTerm, ignoreCase = true) ||
                            it.description?.contains(searchTerm, ignoreCase = true) == true
                }.toLong()
            } else {
                entities.size.toLong()
            }
        }
    }

    override suspend fun getNearbyAsync(
        centerCoordinate: Coordinate,
        radiusKm: Double,
        limit: Int
    ): Result<List<Location>> {
        return executeWithExceptionMapping("GetNearby") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
                .filter { location ->
                    val distance = calculateDistance(centerCoordinate, location.coordinate)
                    distance <= radiusKm
                }
                .sortedBy { location ->
                    calculateDistance(centerCoordinate, location.coordinate)
                }
                .take(limit)
        }
    }

    override suspend fun searchByTextAsync(searchTerm: String, includeDeleted: Boolean): Result<List<Location>> {
        return executeWithExceptionMapping("SearchByText") {
            val entities = if (includeDeleted) {
                database.locationQueries.selectAll()
            } else {
                database.locationQueries.selectActive()
            }.executeAsList()

            entities
                .filter {
                    it.title.contains(searchTerm, ignoreCase = true) ||
                            it.description?.contains(searchTerm, ignoreCase = true) == true
                }
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByBoundsAsync(
        southLat: Double,
        northLat: Double,
        westLon: Double,
        eastLon: Double
    ): Result<List<Location>> {
        return executeWithExceptionMapping("GetByBounds") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
                .filter { location ->
                    location.coordinate.latitude >= southLat &&
                            location.coordinate.latitude <= northLat &&
                            location.coordinate.longitude >= westLon &&
                            location.coordinate.longitude <= eastLon
                }
        }
    }

    override suspend fun getRecentAsync(count: Int): Result<List<Location>> {
        return executeWithExceptionMapping("GetRecent") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
                .sortedByDescending { it.timestamp }
                .take(count)
        }
    }

    override suspend fun getModifiedSinceAsync(timestamp: Long): Result<List<Location>> {
        return executeWithExceptionMapping("GetModifiedSince") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
                .filter { it.timestamp.toEpochMilliseconds() > timestamp }
        }
    }

    override suspend fun getRandomAsync(): Result<Location?> {
        return executeWithExceptionMapping("GetRandom") {
            val entities = database.locationQueries.selectActive().executeAsList()
            if (entities.isNotEmpty()) {
                val randomEntity = entities.random()
                mapToDomain(randomEntity)
            } else {
                null
            }
        }
    }

    override suspend fun createAsync(location: Location): Result<Location> {
        return executeWithExceptionMapping("Create") {
            val now = Clock.System.now().toEpochMilliseconds()
            database.locationQueries.insert(
                title = location.title,
                description = location.description ?: "",
                latitude = location.coordinate.latitude,
                longitude = location.coordinate.longitude,
                photoPath = location.photoPath,
                createdAt = now,
                updatedAt = now
            )

            val id = database.locationQueries.lastInsertRowId().executeAsOne()
            val savedLocation = Location.fromPersistence(
                id = id.toInt(),
                title = location.title,
                description = location.description,
                coordinate = location.coordinate,
                address = location.address,
                photoPath = location.photoPath,
                isDeleted = false,
                timestamp = Clock.System.now()
            )

            logger.i { "Created location with ID ${savedLocation.id}" }
            savedLocation
        }
    }

    override suspend fun updateAsync(location: Location): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.locationQueries.update(
                title = location.title,
                description = location.description ?: "",
                latitude = location.coordinate.latitude,
                longitude = location.coordinate.longitude,
                photoPath = location.photoPath,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for update")
            }

            logger.i { "Updated location with ID ${location.id}" }
        }
    }

    override suspend fun deleteAsync(location: Location): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.locationQueries.hardDelete(location.id.toLong())
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for deletion")
            }

            logger.i { "Deleted location with ID ${location.id}" }
        }
    }

    override suspend fun softDeleteAsync(location: Location): Result<Unit> {
        return executeWithExceptionMapping("SoftDelete") {
            database.locationQueries.softDelete(
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for soft deletion")
            }

            logger.i { "Soft deleted location with ID ${location.id}" }
        }
    }

    override suspend fun restoreAsync(location: Location): Result<Unit> {
        return executeWithExceptionMapping("Restore") {
            database.locationQueries.restore(
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for restoration")
            }

            logger.i { "Restored location with ID ${location.id}" }
        }
    }

    override suspend fun hardDeleteAsync(location: Location): Result<Unit> {
        return deleteAsync(location)
    }

    override suspend fun updateCoordinatesAsync(id: Int, coordinate: Coordinate): Result<Unit> {
        return executeWithExceptionMapping("UpdateCoordinates") {
            database.locationQueries.updateCoordinates(
                latitude = coordinate.latitude,
                longitude = coordinate.longitude,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID $id not found for coordinate update")
            }

            logger.i { "Updated coordinates for location with ID $id" }
        }
    }

    override suspend fun updatePhotoPathAsync(id: Int, photoPath: String?): Result<Unit> {
        return executeWithExceptionMapping("UpdatePhotoPath") {
            database.locationQueries.updatePhotoPath(
                photoPath = photoPath,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID $id not found for photo path update")
            }

            logger.i { "Updated photo path for location with ID $id" }
        }
    }

    override suspend fun existsByIdAsync(id: Int): Result<Boolean> {
        return executeWithExceptionMapping("ExistsById") {
            database.locationQueries.selectById(id.toLong()).executeAsOneOrNull() != null
        }
    }

    override suspend fun existsByTitleAsync(title: String, excludeId: Int): Result<Boolean> {
        return executeWithExceptionMapping("ExistsByTitle") {
            database.locationQueries.selectAll()
                .executeAsList()
                .any { it.title == title && it.id.toInt() != excludeId }
        }
    }

    override suspend fun createBulkAsync(locations: List<Location>): Result<List<Location>> {
        return executeWithExceptionMapping("CreateBulk") {
            if (locations.isEmpty()) return@executeWithExceptionMapping locations

            database.transactionWithResult {
                val result = mutableListOf<Location>()

                for (location in locations) {
                    val now = Clock.System.now().toEpochMilliseconds()
                    database.locationQueries.insert(
                        title = location.title,
                        description = location.description ?: "",
                        latitude = location.coordinate.latitude,
                        longitude = location.coordinate.longitude,
                        photoPath = location.photoPath,
                        createdAt = now,
                        updatedAt = now
                    )

                    val id = database.locationQueries.lastInsertRowId().executeAsOne()
                    val savedLocation = Location.fromPersistence(
                        id = id.toInt(),
                        title = location.title,
                        description = location.description,
                        coordinate = location.coordinate,
                        address = location.address,
                        photoPath = location.photoPath,
                        isDeleted = false,
                        timestamp = Clock.System.now()
                    )
                    result.add(savedLocation)
                }

                logger.i { "Created ${result.size} locations in bulk" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(locations: List<Location>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            if (locations.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                for (location in locations) {
                    database.locationQueries.update(
                        title = location.title,
                        description = location.description ?: "",
                        latitude = location.coordinate.latitude,
                        longitude = location.coordinate.longitude,
                        photoPath = location.photoPath,
                        updatedAt = Clock.System.now().toEpochMilliseconds(),
                        id = location.id.toLong()
                    )
                    val rowsAffected = database.locationQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        updatedCount++
                    }
                }

                logger.i { "Updated $updatedCount locations in bulk" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(locationIds: List<Int>): Result<Int> {
        return executeWithExceptionMapping("DeleteBulk") {
            if (locationIds.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                for (id in locationIds) {
                    database.locationQueries.hardDelete(id.toLong())
                    val rowsAffected = database.locationQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        deletedCount++
                    }
                }

                logger.i { "Deleted $deletedCount locations in bulk" }
                deletedCount
            }
        }
    }

    override suspend fun cleanupDeletedAsync(olderThanTimestamp: Long): Result<Int> {
        return executeWithExceptionMapping("CleanupDeleted") {
            // Since we don't have a deleteOlderThan query, we'll delete by isDeleted status
            // This is a simplified implementation
            val deletedLocations = database.locationQueries.selectAll()
                .executeAsList()
                .filter { it.isDeleted == 1L && it.updatedAt < olderThanTimestamp }

            var deletedCount = 0
            for (location in deletedLocations) {
                database.locationQueries.hardDelete(location.id)
                deletedCount++
            }

            logger.i { "Cleaned up $deletedCount deleted locations older than $olderThanTimestamp" }
            deletedCount
        }
    }

    override suspend fun getStatsAsync(): Result<LocationStats> {
        return executeWithExceptionMapping("GetStats") {
            val allLocations = database.locationQueries.selectAll().executeAsList()
            val activeLocations = allLocations.filter { it.isDeleted == 0L }
            val deletedLocations = allLocations.filter { it.isDeleted == 1L }
            val withPhotos = allLocations.filter { !it.photoPath.isNullOrBlank() }

            val oldestTimestamp = allLocations.minOfOrNull { it.createdAt }
            val newestTimestamp = allLocations.maxOfOrNull { it.createdAt }

            LocationStats(
                totalCount = allLocations.size.toLong(),
                activeCount = activeLocations.size.toLong(),
                deletedCount = deletedLocations.size.toLong(),
                withPhotosCount = withPhotos.size.toLong(),
                oldestTimestamp = oldestTimestamp,
                newestTimestamp = newestTimestamp
            )
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                locationsCache.clear()
                logger.i { "Location cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                locationsCache.remove(id)
                logger.d { "Removed location $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.Location): Location {
        val coordinate = Coordinate.create(entity.latitude, entity.longitude)
        // Address information is not stored in the database schema
        val address: Address? = null

        return Location.fromPersistence(
            id = entity.id.toInt(),
            title = entity.title,
            description = entity.description,
            coordinate = coordinate,
            address = address!!,
            photoPath = entity.photoPath,
            isDeleted = entity.isDeleted == 1L,
            timestamp = Instant.fromEpochMilliseconds(entity.createdAt)
        )
    }

    private fun calculateDistance(coord1: Coordinate, coord2: Coordinate): Double {
        val earthRadius = 6371.0 // Earth radius in kilometers
        val lat1Rad = Math.toRadians(coord1.latitude)
        val lat2Rad = Math.toRadians(coord2.latitude)
        val deltaLat = Math.toRadians(coord2.latitude - coord1.latitude)
        val deltaLon = Math.toRadians(coord2.longitude - coord1.longitude)

        val a = sin(deltaLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private suspend fun <T> executeWithExceptionMapping(
        operationName: String,
        operation: suspend () -> T
    ): Result<T> {
        return try {
            val result = operation()
            Result.success(result)
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for location" }
            val mappedException = exceptionMapper.mapToLocationDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedLocation(
        val location: Location,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}