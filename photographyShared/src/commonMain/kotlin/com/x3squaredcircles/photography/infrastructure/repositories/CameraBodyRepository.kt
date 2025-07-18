// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/CameraBodyRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class CameraBodyRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ICameraBodyRepository {

    private val cameraBodyCache = mutableMapOf<Int, CachedCameraBody>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 60.minutes

    override suspend fun getByIdAsync(id: Int): Result<CameraBodyDto?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.cameraBodyQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDto(it) }
        }
    }

    override suspend fun getAllAsync(): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("GetAll") {
            database.cameraBodyQueries.selectAll()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getPagedAsync(pageSize: Int, offset: Int): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("GetPaged") {
            database.cameraBodyQueries.selectAll()
                .executeAsList()
                .drop(offset)
                .take(pageSize)
                .map { mapToDto(it) }
        }
    }

    override suspend fun getUserCreatedAsync(): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("GetUserCreated") {
            database.cameraBodyQueries.selectUserCreated()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getByMountTypeAsync(mountType: String): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("GetByMountType") {
            database.cameraBodyQueries.selectByMountType(mountType)
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun searchByNameAsync(searchTerm: String): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("SearchByName") {
            database.cameraBodyQueries.selectAll()
                .executeAsList()
                .filter { it.name.contains(searchTerm, ignoreCase = true) }
                .map { mapToDto(it) }
        }
    }

    override suspend fun createAsync(cameraBody: CameraBodyDto): Result<CameraBodyDto> {
        return executeWithExceptionMapping("Create") {
            database.cameraBodyQueries.insert(
                name = cameraBody.name,
                brand = cameraBody.brand,
                mountType = cameraBody.mountType,
                sensorFormat = cameraBody.sensorFormat,
                megapixels = cameraBody.megapixels,
                isoRange = cameraBody.isoRange,
                shutterSpeedRange = cameraBody.shutterSpeedRange,
                isUserCreated = if (cameraBody.isUserCreated) 1L else 0L
            )

            val id = database.cameraBodyQueries.lastInsertRowId().executeAsOne()
            val savedCameraBody = cameraBody.copy(id = id.toInt())

            logger.i { "Created camera body with ID ${savedCameraBody.id}" }
            savedCameraBody
        }
    }

    override suspend fun updateAsync(cameraBody: CameraBodyDto): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.cameraBodyQueries.update(
                name = cameraBody.name,
                brand = cameraBody.brand,
                mountType = cameraBody.mountType,
                sensorFormat = cameraBody.sensorFormat,
                megapixels = cameraBody.megapixels,
                isoRange = cameraBody.isoRange,
                shutterSpeedRange = cameraBody.shutterSpeedRange,
                isUserCreated = if (cameraBody.isUserCreated) 1L else 0L,
                id = cameraBody.id.toLong()
            )
            val rowsAffected = database.cameraBodyQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Camera body with ID ${cameraBody.id} not found for update")
            }

            logger.i { "Updated camera body with ID ${cameraBody.id}" }
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.cameraBodyQueries.deleteById(id.toLong())
            val rowsAffected = database.cameraBodyQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Camera body with ID $id not found for deletion")
            }

            logger.i { "Deleted camera body with ID $id" }
        }
    }

    override suspend fun getTotalCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetTotalCount") {
            database.cameraBodyQueries.getCount().executeAsOne()
        }
    }

    override suspend fun existsByNameAsync(name: String, excludeId: Int): Result<Boolean> {
        return executeWithExceptionMapping("ExistsByName") {
            database.cameraBodyQueries.selectAll()
                .executeAsList()
                .any { it.name == name && it.id.toInt() != excludeId }
        }
    }

    override suspend fun createBulkAsync(cameraBodies: List<CameraBodyDto>): Result<List<CameraBodyDto>> {
        return executeWithExceptionMapping("CreateBulk") {
            if (cameraBodies.isEmpty()) return@executeWithExceptionMapping cameraBodies

            database.transactionWithResult {
                val result = mutableListOf<CameraBodyDto>()

                for (cameraBody in cameraBodies) {
                    database.cameraBodyQueries.insert(
                        name = cameraBody.name,
                        brand = cameraBody.brand,
                        mountType = cameraBody.mountType,
                        sensorFormat = cameraBody.sensorFormat,
                        megapixels = cameraBody.megapixels,
                        isoRange = cameraBody.isoRange,
                        shutterSpeedRange = cameraBody.shutterSpeedRange,
                        isUserCreated = if (cameraBody.isUserCreated) 1L else 0L
                    )

                    val id = database.cameraBodyQueries.lastInsertRowId().executeAsOne()
                    val savedCameraBody = cameraBody.copy(id = id.toInt())
                    result.add(savedCameraBody)
                }

                logger.i { "Created ${result.size} camera bodies in bulk" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(cameraBodies: List<CameraBodyDto>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            if (cameraBodies.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                for (cameraBody in cameraBodies) {
                    database.cameraBodyQueries.update(
                        name = cameraBody.name,
                        brand = cameraBody.brand,
                        mountType = cameraBody.mountType,
                        sensorFormat = cameraBody.sensorFormat,
                        megapixels = cameraBody.megapixels,
                        isoRange = cameraBody.isoRange,
                        shutterSpeedRange = cameraBody.shutterSpeedRange,
                        isUserCreated = if (cameraBody.isUserCreated) 1L else 0L,
                        id = cameraBody.id.toLong()
                    )
                    val rowsAffected = database.cameraBodyQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        updatedCount++
                    }
                }

                logger.i { "Updated $updatedCount camera bodies in bulk" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(ids: List<Int>): Result<Int> {
        return executeWithExceptionMapping("DeleteBulk") {
            if (ids.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                for (id in ids) {
                    database.cameraBodyQueries.deleteById(id.toLong())
                    val rowsAffected = database.cameraBodyQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        deletedCount++
                    }
                }

                logger.i { "Deleted $deletedCount camera bodies in bulk" }
                deletedCount
            }
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                cameraBodyCache.clear()
                logger.i { "Camera body cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                cameraBodyCache.remove(id)
                logger.d { "Removed camera body $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDto(entity: com.x3squaredcircles.photographyshared.db.CameraBody): CameraBodyDto {
        return CameraBodyDto(
            id = entity.id.toInt(),
            name = entity.name,
            brand = entity.brand,
            mountType = entity.mountType,
            sensorFormat = entity.sensorFormat,
            megapixels = entity.megapixels,
            isoRange = entity.isoRange,
            shutterSpeedRange = entity.shutterSpeedRange,
            isUserCreated = entity.isUserCreated == 1L
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
            logger.e(ex) { "Repository operation $operationName failed for camera body" }
            val mappedException = exceptionMapper.mapToCameraBodyDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedCameraBody(
        val cameraBody: CameraBodyDto,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}