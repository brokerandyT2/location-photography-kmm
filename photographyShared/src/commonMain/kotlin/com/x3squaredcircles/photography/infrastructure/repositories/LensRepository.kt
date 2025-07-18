// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LensRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class LensRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ILensRepository {

    private val lensCache = mutableMapOf<Int, CachedLens>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 60.minutes

    override suspend fun getByIdAsync(id: Int): Result<LensDto?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.lensQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDto(it) }
        }
    }

    override suspend fun getAllAsync(): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetAll") {
            database.lensQueries.selectAll()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetPaged") {
            val offset = (pageNumber - 1) * pageSize
            database.lensQueries.selectAll()
                .executeAsList()
                .sortedBy { if (it.isUserCreated == 1L) 0 else 1 }
                .drop(offset)
                .take(pageSize)
                .map { mapToDto(it) }
        }
    }

    override suspend fun getUserCreatedAsync(): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetUserCreated") {
            database.lensQueries.selectUserCreated()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getByFocalLengthRangeAsync(minFocalLength: Double, maxFocalLength: Double): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetByFocalLengthRange") {
            database.lensQueries.selectAll()
                .executeAsList()
                .filter { lens ->
                    val minMM = lens.minMM ?: 0.0
                    val maxMM = lens.maxMM ?: minMM
                    (minMM >= minFocalLength && minMM <= maxFocalLength) ||
                            (maxMM >= minFocalLength && maxMM <= maxFocalLength) ||
                            (minMM <= minFocalLength && maxMM >= maxFocalLength)
                }
                .map { mapToDto(it) }
        }
    }

    override suspend fun getCompatibleLensesAsync(cameraBodyId: Int): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetCompatibleLenses") {
            database.lensQueries.selectCompatibleLenses(cameraBodyId.toLong())
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getPrimeLensesAsync(): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetPrimeLenses") {
            database.lensQueries.selectPrimes()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getZoomLensesAsync(): Result<List<LensDto>> {
        return executeWithExceptionMapping("GetZoomLenses") {
            database.lensQueries.selectZooms()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun createAsync(lens: LensDto): Result<LensDto> {
        return executeWithExceptionMapping("Create") {
            database.lensQueries.insert(
                nameForLens =  lens.nameForLens,
                minMM = lens.minMM,
                maxMM = lens.maxMM,
                maxFStop =  lens.maxFStop,
                minFStop = lens.minFStop,
                isUserCreated = if (lens.isUserCreated) 1L else 0L,
                isPrime = if (lens.isPrime) 1L else 0L,
                dateAdded = lens.dateAdded
            )

            val id = database.lensQueries.lastInsertRowId().executeAsOne()
            val savedLens = lens.copy(id = id.toInt())

            logger.i { "Created lens with ID ${savedLens.id}" }
            savedLens
        }
    }

    override suspend fun updateAsync(lens: LensDto): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.lensQueries.update(
                nameForLens = lens.nameForLens,
                minMM = lens.minMM,
                maxMM = lens.maxMM,
                id = lens.id.toLong(),
                minFStop =  lens.minFStop,
                maxFStop =  lens.maxFStop,
                isPrime = if (lens.isPrime) 1L else 0L
            )
            val rowsAffected = database.lensQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Lens with ID ${lens.id} not found for update")
            }

            logger.i { "Updated lens with ID ${lens.id}" }
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.lensQueries.deleteById(id.toLong())
            val rowsAffected = database.lensQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Lens with ID $id not found for deletion")
            }

            logger.i { "Deleted lens with ID $id" }
        }
    }

    override suspend fun getTotalCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetTotalCount") {
            database.lensQueries.selectAll().executeAsList().size.toLong()
        }
    }

    override suspend fun getCountByTypeAsync(): Result<Pair<Long, Long>> {
        return executeWithExceptionMapping("GetCountByType") {
            val allLenses = database.lensQueries.selectAll().executeAsList()
            val primeCount = allLenses.count { it.minMM == it.maxMM }.toLong()
            val zoomCount = allLenses.count { it.minMM != it.maxMM }.toLong()
            Pair(primeCount, zoomCount)
        }

    }

    override suspend fun createBulkAsync(lenses: List<LensDto>): Result<List<LensDto>> {
        return executeWithExceptionMapping("CreateBulk") {
            if (lenses.isEmpty()) return@executeWithExceptionMapping lenses

            database.transactionWithResult {
                val result = mutableListOf<LensDto>()

                for (lens in lenses) {
                    database.lensQueries.insert(
                        minMM = lens.minMM,
                        maxMM = lens.maxMM,
                        isUserCreated = if (lens.isUserCreated) 1L else 0L,
                        maxFStop = lens.maxFStop,
                        minFStop = lens.minFStop,
                        dateAdded = lens.dateAdded,
                        nameForLens = lens.nameForLens,
                        isPrime = if (lens.isPrime) 1L else 0L
                    )

                    val id = database.lensQueries.lastInsertRowId().executeAsOne()
                    val savedLens = lens.copy(id = id.toInt())
                    result.add(savedLens)
                }

                logger.i { "Created ${result.size} lenses in bulk" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(lenses: List<LensDto>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            if (lenses.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                for (lens in lenses) {
                    database.lensQueries.update(

                        minMM = lens.minMM,
                        maxMM = lens.maxMM,
                       minFStop = lens.minFStop,
                        maxFStop = lens.maxFStop,
                        isPrime = if (lens.isPrime) 1L else 0L,
                        nameForLens = lens.nameForLens,
                        id = lens.id.toLong()
                    )
                    val rowsAffected = database.lensQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        updatedCount++
                    }
                }

                logger.i { "Updated $updatedCount lenses in bulk" }
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
                    database.lensQueries.deleteById(id.toLong())
                    val rowsAffected = database.lensQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        deletedCount++
                    }
                }

                logger.i { "Deleted $deletedCount lenses in bulk" }
                deletedCount
            }
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                lensCache.clear()
                logger.i { "Lens cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                lensCache.remove(id)
                logger.d { "Removed lens $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDto(entity: com.x3squaredcircles.photographyshared.db.Lens): LensDto {
        return LensDto(
            id = entity.id.toInt(),
            minMM = entity.minMM,
            maxMM = entity.maxMM,
            maxFStop = entity.maxFStop,
            minFStop = entity.minFStop,
            nameForLens = entity.nameForLens,
            isPrime = entity.isPrime == 1L,
            isUserCreated = entity.isUserCreated == 1L,
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
            logger.e(ex) { "Repository operation $operationName failed for lens" }
            Result.failure(ex.message ?: "Unknown error", ex)
        }
    }
    private data class CachedLens(
        val lens: LensDto,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}