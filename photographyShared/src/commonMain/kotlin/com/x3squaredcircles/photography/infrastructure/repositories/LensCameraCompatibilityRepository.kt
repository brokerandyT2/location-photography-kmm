// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityDto
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class LensCameraCompatibilityRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ILensCameraCompatibilityRepository {

    private val compatibilityCache = mutableMapOf<Int, CachedCompatibility>()
    private val cacheByLensId = mutableMapOf<Int, List<LensCameraCompatibilityDto>>()
    private val cacheByCameraId = mutableMapOf<Int, List<LensCameraCompatibilityDto>>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 30.minutes

    override suspend fun getByIdAsync(id: Int): Result<LensCameraCompatibilityDto?> {
        return executeWithExceptionMapping("GetById") {
            cacheMutex.withLock {
                val cached = compatibilityCache[id]
                if (cached != null && !cached.isExpired) {
                    logger.d { "Cache hit for lens camera compatibility ID: $id" }
                    return@executeWithExceptionMapping cached.compatibility
                }
            }

            val entity = database.lensCameraCompatibilityQueries.selectById(id.toLong()).executeAsOneOrNull()
            val compatibility = entity?.let { mapToDto(it) }

            if (compatibility != null) {
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    compatibilityCache[id] = CachedCompatibility(compatibility, expiration)
                    logger.d { "Cached lens camera compatibility for ID: $id" }
                }
            }

            compatibility
        }
    }

    override suspend fun getAllAsync(): Result<List<LensCameraCompatibilityDto>> {
        return executeWithExceptionMapping("GetAll") {
            database.lensCameraCompatibilityQueries.selectAll()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getByLensIdAsync(lensId: Int): Result<List<LensCameraCompatibilityDto>> {
        return executeWithExceptionMapping("GetByLensId") {
            cacheMutex.withLock {
                val cached = cacheByLensId[lensId]
                if (cached != null) {
                    logger.d { "Cache hit for lens camera compatibility by lens ID: $lensId" }
                    return@executeWithExceptionMapping cached
                }
            }

            val compatibilities = database.lensCameraCompatibilityQueries.selectByLensId(lensId.toLong())
                .executeAsList()
                .map { mapToDto(it) }

            cacheMutex.withLock {
                cacheByLensId[lensId] = compatibilities
                logger.d { "Cached ${compatibilities.size} lens camera compatibilities for lens ID: $lensId" }
            }

            compatibilities
        }
    }

    override suspend fun getByCameraIdAsync(cameraBodyId: Int): Result<List<LensCameraCompatibilityDto>> {
        return executeWithExceptionMapping("GetByCameraId") {
            cacheMutex.withLock {
                val cached = cacheByCameraId[cameraBodyId]
                if (cached != null) {
                    logger.d { "Cache hit for lens camera compatibility by camera ID: $cameraBodyId" }
                    return@executeWithExceptionMapping cached
                }
            }

            val compatibilities = database.lensCameraCompatibilityQueries.selectByCameraId(cameraBodyId.toLong())
                .executeAsList()
                .map { mapToDto(it) }

            cacheMutex.withLock {
                cacheByCameraId[cameraBodyId] = compatibilities
                logger.d { "Cached ${compatibilities.size} lens camera compatibilities for camera ID: $cameraBodyId" }
            }

            compatibilities
        }
    }

    override suspend fun createAsync(compatibility: LensCameraCompatibilityDto): Result<LensCameraCompatibilityDto> {
        return executeWithExceptionMapping("Create") {
            val now = Clock.System.now().toEpochMilliseconds()

            database.lensCameraCompatibilityQueries.insert(
                lensId = compatibility.lensId.toLong(),
                cameraBodyId = compatibility.cameraBodyId.toLong(),
                dateAdded = now
            )

            val newId = database.lensCameraCompatibilityQueries.lastInsertRowId().executeAsOne().toInt()
            val created = compatibility.copy(id = newId, dateAdded = now)

            clearCacheForIds(compatibility.lensId, compatibility.cameraBodyId)
            logger.i { "Created lens camera compatibility with ID: $newId" }

            created
        }
    }

    override suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibilityDto>): Result<List<LensCameraCompatibilityDto>> {
        return executeWithExceptionMapping("CreateBatch") {
            val now = Clock.System.now().toEpochMilliseconds()
            val created = mutableListOf<LensCameraCompatibilityDto>()

            database.transaction {
                for (compatibility in compatibilities) {
                    database.lensCameraCompatibilityQueries.insert(
                        lensId = compatibility.lensId.toLong(),
                        cameraBodyId = compatibility.cameraBodyId.toLong(),
                        dateAdded = now
                    )

                    val newId = database.lensCameraCompatibilityQueries.lastInsertRowId().executeAsOne().toInt()
                    created.add(compatibility.copy(id = newId, dateAdded = now))
                }
            }

            clearCache()
            logger.i { "Created ${created.size} lens camera compatibilities" }

            created
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            // Get the compatibility first to clear related caches
            val compatibility = database.lensCameraCompatibilityQueries.selectById(id.toLong()).executeAsOneOrNull()

            database.lensCameraCompatibilityQueries.deleteById(id.toLong())
            val rowsAffected = database.lensCameraCompatibilityQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Lens camera compatibility with ID $id not found")
            }

            compatibility?.let {
                clearCacheForIds(it.lensId.toInt(), it.cameraBodyId.toInt())
            }

            logger.i { "Deleted lens camera compatibility with ID: $id" }
        }
    }

    override suspend fun deleteByLensAndCameraAsync(lensId: Int, cameraBodyId: Int): Result<Unit> {
        return executeWithExceptionMapping("DeleteByLensAndCamera") {
            database.lensCameraCompatibilityQueries.deleteByLensAndCamera(
                lensId = lensId.toLong(),
                cameraBodyId = cameraBodyId.toLong()
            )
            val rowsAffected = database.lensCameraCompatibilityQueries.changes().executeAsOne()

            clearCacheForIds(lensId, cameraBodyId)
            logger.i { "Deleted lens camera compatibility for lens ID: $lensId, camera ID: $cameraBodyId (affected rows: $rowsAffected)" }
        }
    }

    override suspend fun deleteByLensIdAsync(lensId: Int): Result<Unit> {
        return executeWithExceptionMapping("DeleteByLensId") {
            database.lensCameraCompatibilityQueries.deleteByLensId(lensId.toLong())
            val rowsAffected = database.lensCameraCompatibilityQueries.changes().executeAsOne()

            clearCache()
            logger.i { "Deleted $rowsAffected lens camera compatibilities for lens ID: $lensId" }
        }
    }

    override suspend fun deleteByCameraIdAsync(cameraBodyId: Int): Result<Unit> {
        return executeWithExceptionMapping("DeleteByCameraId") {
            database.lensCameraCompatibilityQueries.deleteByCameraId(cameraBodyId.toLong())
            val rowsAffected = database.lensCameraCompatibilityQueries.changes().executeAsOne()

            clearCache()
            logger.i { "Deleted $rowsAffected lens camera compatibilities for camera ID: $cameraBodyId" }
        }
    }

    override suspend fun existsAsync(lensId: Int, cameraBodyId: Int): Result<Boolean> {
        return executeWithExceptionMapping("Exists") {
            val exists = database.lensCameraCompatibilityQueries.exists(
                lensId = lensId.toLong(),
                cameraBodyId = cameraBodyId.toLong()
            ).executeAsOne()

             exists
        }
    }

    override suspend fun getCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetCount") {
            database.lensCameraCompatibilityQueries.getCount().executeAsOne()
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                compatibilityCache.clear()
                cacheByLensId.clear()
                cacheByCameraId.clear()
                logger.i { "Lens camera compatibility cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                compatibilityCache.remove(id)
                logger.d { "Removed lens camera compatibility $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(lensId: Int, cameraBodyId: Int) {
        clearCacheForIds(lensId, cameraBodyId)
    }

    private fun clearCacheForIds(lensId: Int, cameraBodyId: Int) {
        if (cacheMutex.tryLock()) {
            try {
                cacheByLensId.remove(lensId)
                cacheByCameraId.remove(cameraBodyId)
                logger.d { "Cleared lens camera compatibility cache for lens ID: $lensId, camera ID: $cameraBodyId" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDto(entity: com.x3squaredcircles.photographyshared.db.LensCameraCompatibility): LensCameraCompatibilityDto {
        return LensCameraCompatibilityDto(
            id = entity.id.toInt(),
            lensId = entity.lensId.toInt(),
            cameraBodyId = entity.cameraBodyId.toInt(),
            dateAdded = entity.dateAdded
        )
    }

    private suspend fun <T> executeWithExceptionMapping(
        operationName: String,
        operation: suspend () -> T
    ): Result<T> {
        return try {
            val result = operation()
            Result.success(result)
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for lens camera compatibility" }
            val mappedException = exceptionMapper.mapToLocationDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedCompatibility(
        val compatibility: LensCameraCompatibilityDto,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}