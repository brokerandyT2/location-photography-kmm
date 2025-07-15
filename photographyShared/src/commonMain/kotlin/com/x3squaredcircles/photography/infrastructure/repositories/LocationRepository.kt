// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LocationRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address
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

    override suspend fun getByIdAsync(id: Int): Location? {
        return executeWithExceptionMapping("GetById") {
            val entity = database.locationQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): List<Location> {
        return executeWithExceptionMapping("GetAll") {
            database.locationQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getActiveAsync(): List<Location> {
        return executeWithExceptionMapping("GetActive") {
            database.locationQueries.selectActive()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByTitleAsync(title: String): Location? {
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
    ): List<Location> {
        return executeWithExceptionMapping("GetPaged") {
            val offset = (pageNumber - 1) * pageSize
            database.locationQueries.selectPaged(
                includeDeleted = if (includeDeleted) 1L else 0L,
                searchTerm = searchTerm,
                limit = pageSize.toLong(),
                offset = offset.toLong()
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getTotalCountAsync(includeDeleted: Boolean, searchTerm: String?): Long {
        return executeWithExceptionMapping("GetTotalCount") {
            database.locationQueries.selectCount(
                includeDeleted = if (includeDeleted) 1L else 0L,
                searchTerm = searchTerm
            ).executeAsOne()
        }
    }

    override suspend fun getNearbyAsync(
        centerCoordinate: Coordinate,
        radiusKm: Double,
        limit: Int
    ): List<Location> {
        return executeWithExceptionMapping("GetNearby") {
            val boundingBox = calculateBoundingBox(centerCoordinate, radiusKm)
            val candidates = database.locationQueries.selectNearby(
                minLat = boundingBox.minLat,
                maxLat = boundingBox.maxLat,
                minLon = boundingBox.minLon,
                maxLon = boundingBox.maxLon,
                centerLat = centerCoordinate.latitude,
                centerLon = centerCoordinate.longitude
            ).executeAsList()
                .map { mapToDomain(it) }

            candidates.filter { location ->
                centerCoordinate.distanceTo(location.coordinate) <= radiusKm
            }.take(limit)
        }
    }

    override suspend fun searchByTextAsync(searchTerm: String, includeDeleted: Boolean): List<Location> {
        return executeWithExceptionMapping("SearchByText") {
            database.locationQueries.searchByText(
                searchTerm = searchTerm,
                includeDeleted = if (includeDeleted) 1L else 0L
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByBoundsAsync(
        southLat: Double,
        northLat: Double,
        westLon: Double,
        eastLon: Double
    ): List<Location> {
        return executeWithExceptionMapping("GetByBounds") {
            database.locationQueries.selectByBounds(
                southLat = southLat,
                northLat = northLat,
                westLon = westLon,
                eastLon = eastLon
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRecentAsync(count: Int): List<Location> {
        return executeWithExceptionMapping("GetRecent") {
            val sinceTimestamp = Clock.System.now().minus(30.minutes).toEpochMilliseconds()
            database.locationQueries.selectRecent(
                createdAt = sinceTimestamp,
                value_ = count.toLong()
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getModifiedSinceAsync(timestamp: Long): List<Location> {
        return executeWithExceptionMapping("GetModifiedSince") {
            database.locationQueries.selectModifiedSince(timestamp)
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRandomAsync(): Location? {
        return executeWithExceptionMapping("GetRandom") {
            val entity = database.locationQueries.selectRandom().executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun addAsync(location: Location): Location {
        return executeWithExceptionMapping("Add") {
            val now = Clock.System.now()
            database.locationQueries.insert(
                title = location.title,
                description = location.description,
                latitude = location.coordinate.latitude,
                longitude = location.coordinate.longitude,
                photoPath = location.photoPath,
                createdAt = now.toEpochMilliseconds(),
                updatedAt = now.toEpochMilliseconds()
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
                timestamp = now
            )

            logger.i { "Created location with ID ${savedLocation.id}" }
            savedLocation
        }
    }

    override suspend fun updateAsync(location: Location) {
        executeWithExceptionMapping("Update") {
            val now = Clock.System.now()
            database.locationQueries.update(
                title = location.title,
                description = location.description,
                latitude = location.coordinate.latitude,
                longitude = location.coordinate.longitude,
                photoPath = location.photoPath,
                updatedAt = now.toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for update")
            }

            logger.i { "Updated location with ID ${location.id}" }
        }
    }

    override suspend fun deleteAsync(location: Location) {
        softDeleteAsync(location)
    }

    override suspend fun softDeleteAsync(location: Location) {
        executeWithExceptionMapping("SoftDelete") {
            val now = Clock.System.now()
            database.locationQueries.softDelete(
                updatedAt = now.toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for soft delete")
            }

            logger.i { "Soft deleted location with ID ${location.id}" }
        }
    }

    override suspend fun restoreAsync(location: Location) {
        executeWithExceptionMapping("Restore") {
            val now = Clock.System.now()
            database.locationQueries.restore(
                updatedAt = now.toEpochMilliseconds(),
                id = location.id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for restore")
            }

            logger.i { "Restored location with ID ${location.id}" }
        }
    }

    override suspend fun hardDeleteAsync(location: Location) {
        executeWithExceptionMapping("HardDelete") {
            database.locationQueries.hardDelete(location.id.toLong())
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID ${location.id} not found for hard delete")
            }

            logger.i { "Hard deleted location with ID ${location.id}" }
        }
    }

    override suspend fun updateCoordinatesAsync(id: Int, coordinate: Coordinate) {
        executeWithExceptionMapping("UpdateCoordinates") {
            val now = Clock.System.now()
            database.locationQueries.updateCoordinates(
                latitude = coordinate.latitude,
                longitude = coordinate.longitude,
                updatedAt = now.toEpochMilliseconds(),
                id = id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID $id not found for coordinate update")
            }

            logger.i { "Updated coordinates for location with ID $id" }
        }
    }

    override suspend fun updatePhotoPathAsync(id: Int, photoPath: String?) {
        executeWithExceptionMapping("UpdatePhotoPath") {
            val now = Clock.System.now()
            database.locationQueries.updatePhotoPath(
                photoPath = photoPath,
                updatedAt = now.toEpochMilliseconds(),
                id = id.toLong()
            )
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Location with ID $id not found for photo path update")
            }

            logger.i { "Updated photo path for location with ID $id" }
        }
    }

    override suspend fun existsByIdAsync(id: Int): Boolean {
        return executeWithExceptionMapping("ExistsById") {
            database.locationQueries.existsById(id.toLong()).executeAsOne()
        }
    }

    override suspend fun existsByTitleAsync(title: String, excludeId: Int): Boolean {
        return executeWithExceptionMapping("ExistsByTitle") {
            database.locationQueries.existsByTitle(title, excludeId.toLong()).executeAsOne()
        }
    }

    override suspend fun createBulkAsync(locations: List<Location>): List<Location> {
        return executeWithExceptionMapping("CreateBulk") {
            if (locations.isEmpty()) return@executeWithExceptionMapping locations

            database.transactionWithResult {
                val now = Clock.System.now()
                val result = mutableListOf<Location>()

                locations.forEach { location ->
                    database.locationQueries.insert(
                        title = location.title,
                        description = location.description,
                        latitude = location.coordinate.latitude,
                        longitude = location.coordinate.longitude,
                        photoPath = location.photoPath,
                        createdAt = now.toEpochMilliseconds(),
                        updatedAt = now.toEpochMilliseconds()
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
                        timestamp = now
                    )
                    result.add(savedLocation)
                }

                logger.i { "Bulk created ${result.size} locations" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(locations: List<Location>): Int {
        return executeWithExceptionMapping("UpdateBulk") {
            if (locations.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                val now = Clock.System.now()
                var updatedCount = 0

                locations.forEach { location ->
                    database.locationQueries.update(
                        title = location.title,
                        description = location.description,
                        latitude = location.coordinate.latitude,
                        longitude = location.coordinate.longitude,
                        photoPath = location.photoPath,
                        updatedAt = now.toEpochMilliseconds(),
                        id = location.id.toLong()
                    )
                    val rowsAffected = database.locationQueries.changes().executeAsOne()

                    if (rowsAffected > 0) {
                        updatedCount++
                    }
                }

                logger.i { "Bulk updated $updatedCount locations" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(locationIds: List<Int>): Int {
        return executeWithExceptionMapping("DeleteBulk") {
            if (locationIds.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                val now = Clock.System.now()
                var deletedCount = 0

                locationIds.forEach { id ->
                    database.locationQueries.softDelete(
                        updatedAt = now.toEpochMilliseconds(),
                        id = id.toLong()
                    )
                    val rowsAffected = database.locationQueries.changes().executeAsOne()
                    if (rowsAffected > 0) {
                        deletedCount++
                    }
                }

                logger.i { "Bulk deleted $deletedCount locations" }
                deletedCount
            }
        }
    }

    override suspend fun cleanupDeletedAsync(olderThanTimestamp: Long): Int {
        return executeWithExceptionMapping("CleanupDeleted") {
            database.locationQueries.cleanupDeleted(olderThanTimestamp)
            val rowsAffected = database.locationQueries.changes().executeAsOne()

            logger.i { "Cleaned up $rowsAffected old deleted locations" }
            rowsAffected.toInt()
        }
    }

    override suspend fun getStatsAsync(): LocationStats {
        return executeWithExceptionMapping("GetStats") {
            val statsResult = database.locationQueries.selectStats().executeAsOne()
            LocationStats(
                totalCount = statsResult.COUNT ?: 0L,
                activeCount = statsResult.COUNT_ ?: 0L,
                deletedCount = statsResult.COUNT__ ?: 0L,
                withPhotosCount = statsResult.COUNT___ ?: 0L,
                oldestTimestamp = statsResult.MIN,
                newestTimestamp = statsResult.MAX
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
        return Location.fromPersistence(
            id = entity.id.toInt(),
            title = entity.title,
            description = entity.description,
            coordinate = Coordinate.create(entity.latitude, entity.longitude),
            address = Address("", ""),
            photoPath = entity.photoPath,
            isDeleted = entity.isDeleted == 1L,
            timestamp = Instant.fromEpochMilliseconds(entity.createdAt)
        )
    }

    private fun calculateBoundingBox(center: Coordinate, radiusKm: Double): BoundingBox {
        val deltaLat = radiusKm / 111.32
        val deltaLon = radiusKm / (111.32 * cos(center.latitude * PI / 180))

        return BoundingBox(
            minLat = maxOf(-90.0, center.latitude - deltaLat),
            maxLat = minOf(90.0, center.latitude + deltaLat),
            minLon = maxOf(-180.0, center.longitude - deltaLon),
            maxLon = minOf(180.0, center.longitude + deltaLon)
        )
    }

    private suspend fun <T> executeWithExceptionMapping(
        operationName: String,
        operation: suspend () -> T
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for location" }
            throw exceptionMapper.mapToLocationDomainException(ex, operationName)
        }
    }

    private data class CachedLocation(
        val location: Location,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }

    private data class BoundingBox(
        val minLat: Double,
        val maxLat: Double,
        val minLon: Double,
        val maxLon: Double
    )
}