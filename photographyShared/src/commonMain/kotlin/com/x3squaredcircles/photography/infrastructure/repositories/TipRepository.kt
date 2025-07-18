// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/TipRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Tip
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.TipCacheType
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class TipRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ITipRepository {

    private val tipsCache = mutableMapOf<Int, CachedTip>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 60.minutes

    override suspend fun getByIdAsync(id: Int): Result<Tip?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.tipQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): Result<List<Tip>> {
        return executeWithExceptionMapping("GetAll") {
            database.tipQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByTypeIdAsync(tipTypeId: Int): Result<List<Tip>> {
        return executeWithExceptionMapping("GetByTypeId") {
            database.tipQueries.selectByTypeId(tipTypeId.toLong())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getWithCameraSettingsAsync(): Result<List<Tip>> {
        return executeWithExceptionMapping("GetWithCameraSettings") {
            database.tipQueries.selectWithCameraSettings()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun searchByTextAsync(searchTerm: String): Result<List<Tip>> {
        return executeWithExceptionMapping("SearchByText") {
            database.tipQueries.selectBySearch(
                searchTerm,
                searchTerm,
                searchTerm,
                searchTerm
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRandomAsync(count: Int): Result<List<Tip>> {
        return executeWithExceptionMapping("GetRandom") {
            database.tipQueries.selectRandom(count.toLong())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        tipTypeId: Int?
    ): Result<List<Tip>> {
        return executeWithExceptionMapping("GetPaged") {
            val offset = (pageNumber - 1) * pageSize
            val tips = if (tipTypeId != null) {
                database.tipQueries.selectByTypeId(tipTypeId.toLong())
                    .executeAsList()
                    .drop(offset)
                    .take(pageSize)
            } else {
                database.tipQueries.selectAll()
                    .executeAsList()
                    .drop(offset)
                    .take(pageSize)
            }
            tips.map { mapToDomain(it) }
        }
    }

    override suspend fun getTotalCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetTotalCount") {
            database.tipQueries.getCount().executeAsOne()
        }
    }

    override suspend fun getCountByTypeAsync(tipTypeId: Int): Result<Long> {
        return executeWithExceptionMapping("GetCountByType") {
            database.tipQueries.getCountByType(tipTypeId.toLong()).executeAsOne()
        }
    }

    override suspend fun createAsync(tip: Tip): Result<Tip> {
        return executeWithExceptionMapping("Create") {
            database.tipQueries.insert(
                tipTypeId = tip.tipTypeId.toLong(),
                title = tip.title,
                content = tip.content,
                fstop = tip.fstop,
                shutterSpeed = tip.shutterSpeed,
                iso = tip.iso,
                i8n = tip.i8n
            )

            val id = database.tipQueries.lastInsertRowId().executeAsOne()
            val savedTip = Tip.fromPersistence(
                id = id.toInt(),
                tipTypeId = tip.tipTypeId,
                title = tip.title,
                content = tip.content,
                fstop = tip.fstop,
                shutterSpeed = tip.shutterSpeed,
                iso = tip.iso,
                i8n = tip.i8n
            )

            logger.i { "Created tip with ID ${savedTip.id}" }
            savedTip
        }
    }

    override suspend fun updateAsync(tip: Tip): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.tipQueries.update(
                tipTypeId = tip.tipTypeId.toLong(),
                title = tip.title,
                content = tip.content,
                fstop = tip.fstop,
                shutterSpeed = tip.shutterSpeed,
                iso = tip.iso,
                i8n = tip.i8n,
                id = tip.id.toLong()
            )
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Tip with ID ${tip.id} not found for update")
            }

            logger.i { "Updated tip with ID ${tip.id}" }
        }
    }

    override suspend fun deleteAsync(tip: Tip): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.tipQueries.deleteById(tip.id.toLong())
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Tip with ID ${tip.id} not found for deletion")
            }

            logger.i { "Deleted tip with ID ${tip.id}" }
        }
    }

    override suspend fun deleteByTypeIdAsync(tipTypeId: Int): Result<Int> {
        return executeWithExceptionMapping("DeleteByTypeId") {
            database.tipQueries.deleteByTypeId(tipTypeId.toLong())
            val rowsAffected = database.tipQueries.changes().executeAsOne().toInt()

            logger.i { "Deleted $rowsAffected tips for tip type ID $tipTypeId" }
            rowsAffected
        }
    }

    override suspend fun updateCameraSettingsAsync(
        id: Int,
        fstop: String,
        shutterSpeed: String,
        iso: String
    ): Result<Unit> {
        return executeWithExceptionMapping("UpdateCameraSettings") {
            database.tipQueries.updateCameraSettings(
                fstop = fstop,
                shutterSpeed = shutterSpeed,
                iso = iso,
                id = id.toLong()
            )
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Tip with ID $id not found for camera settings update")
            }

            logger.i { "Updated camera settings for tip with ID $id" }
        }
    }

    override suspend fun existsByIdAsync(id: Int): Result<Boolean> {
        return executeWithExceptionMapping("ExistsById") {
            database.tipQueries.selectById(id.toLong()).executeAsOneOrNull() != null
        }
    }

    override suspend fun createBulkAsync(tips: List<Tip>): Result<List<Tip>> {
        return executeWithExceptionMapping("CreateBulk") {
            if (tips.isEmpty()) return@executeWithExceptionMapping tips

            database.transactionWithResult {
                val result = mutableListOf<Tip>()

                for (tip in tips) {
                    database.tipQueries.insert(
                        tipTypeId = tip.tipTypeId.toLong(),
                        title = tip.title,
                        content = tip.content,
                        fstop = tip.fstop,
                        shutterSpeed = tip.shutterSpeed,
                        iso = tip.iso,
                        i8n = tip.i8n
                    )

                    val id = database.tipQueries.lastInsertRowId().executeAsOne()
                    val savedTip = Tip.fromPersistence(
                        id = id.toInt(),
                        tipTypeId = tip.tipTypeId,
                        title = tip.title,
                        content = tip.content,
                        fstop = tip.fstop,
                        shutterSpeed = tip.shutterSpeed,
                        iso = tip.iso,
                        i8n = tip.i8n
                    )
                    result.add(savedTip)
                }

                logger.i { "Created ${result.size} tips in bulk" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(tips: List<Tip>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            if (tips.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                for (tip in tips) {
                    database.tipQueries.update(
                        tipTypeId = tip.tipTypeId.toLong(),
                        title = tip.title,
                        content = tip.content,
                        fstop = tip.fstop,
                        shutterSpeed = tip.shutterSpeed,
                        iso = tip.iso,
                        i8n = tip.i8n,
                        id = tip.id.toLong()
                    )
                    val rowsAffected = database.tipQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        updatedCount++
                    }
                }

                logger.i { "Updated $updatedCount tips in bulk" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(tipIds: List<Int>): Result<Int> {
        return executeWithExceptionMapping("DeleteBulk") {
            if (tipIds.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                for (id in tipIds) {
                    database.tipQueries.deleteById(id.toLong())
                    val rowsAffected = database.tipQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        deletedCount++
                    }
                }

                logger.i { "Deleted $deletedCount tips in bulk" }
                deletedCount
            }
        }
    }

    override suspend fun getTipsByLocalizationAsync(localization: String): Result<List<Tip>> {
        return executeWithExceptionMapping("GetByLocalization") {
            database.tipQueries.selectAll()
                .executeAsList()
                .filter { it.i8n == localization }
                .map { mapToDomain(it) }
        }
    }

    override suspend fun updateLocalizationAsync(id: Int, localization: String): Result<Unit> {
        return executeWithExceptionMapping("UpdateLocalization") {
            val tip = database.tipQueries.selectById(id.toLong()).executeAsOneOrNull()
                ?: throw IllegalArgumentException("Tip with ID $id not found")

            database.tipQueries.update(
                tipTypeId = tip.tipTypeId,
                title = tip.title,
                content = tip.content,
                fstop = tip.fstop,
                shutterSpeed = tip.shutterSpeed,
                iso = tip.iso,
                i8n = localization,
                id = id.toLong()
            )
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Tip with ID $id not found for localization update")
            }

            logger.i { "Updated localization for tip with ID $id to $localization" }
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                tipsCache.clear()
                logger.i { "Tip cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                tipsCache.remove(id)
                logger.d { "Removed tip $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(tipTypeId: Int, cacheType: TipCacheType) {
        if (cacheMutex.tryLock()) {
            try {
                when (cacheType) {
                    TipCacheType.BY_TYPE -> {
                        tipsCache.entries.removeAll { it.value.tip.tipTypeId == tipTypeId }
                        logger.d { "Removed tips for tip type $tipTypeId from cache" }
                    }
                    TipCacheType.ALL -> {
                        tipsCache.clear()
                        logger.d { "Cleared all tips from cache" }
                    }
                    else -> {
                        logger.d { "Cache type $cacheType not implemented for clearing" }
                    }
                }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.Tip): Tip {
        return Tip.fromPersistence(
            id = entity.id.toInt(),
            tipTypeId = entity.tipTypeId.toInt(),
            title = entity.title,
            content = entity.content,
            fstop = entity.fstop,
            shutterSpeed = entity.shutterSpeed,
            iso = entity.iso,
            i8n = entity.i8n
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
            logger.e(ex) { "Repository operation $operationName failed for tip" }
            val mappedException = exceptionMapper.mapToTipDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedTip(
        val tip: Tip,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}