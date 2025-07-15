// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/TipRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Tip
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

    override suspend fun getByIdAsync(id: Int): Tip? {
        return executeWithExceptionMapping("GetById") {
            val entity = database.tipQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): List<Tip> {
        return executeWithExceptionMapping("GetAll") {
            database.tipQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByTypeIdAsync(tipTypeId: Int): List<Tip> {
        return executeWithExceptionMapping("GetByTypeId") {
            database.tipQueries.selectByTypeId(tipTypeId.toLong())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getWithCameraSettingsAsync(): List<Tip> {
        return executeWithExceptionMapping("GetWithCameraSettings") {
            database.tipQueries.selectWithCameraSettings()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun searchByTextAsync(searchTerm: String): List<Tip> {
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

    override suspend fun getRandomAsync(count: Int): List<Tip> {
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
    ): List<Tip> {
        return executeWithExceptionMapping("GetPaged") {
            if (tipTypeId != null) {
                database.tipQueries.selectByTypeId(tipTypeId.toLong())
                    .executeAsList()
                    .drop((pageNumber - 1) * pageSize)
                    .take(pageSize)
                    .map { mapToDomain(it) }
            } else {
                database.tipQueries.selectAll()
                    .executeAsList()
                    .drop((pageNumber - 1) * pageSize)
                    .take(pageSize)
                    .map { mapToDomain(it) }
            }
        }
    }

    override suspend fun getTotalCountAsync(): Long {
        return executeWithExceptionMapping("GetTotalCount") {
            database.tipQueries.getCount().executeAsOne()
        }
    }

    override suspend fun getCountByTypeAsync(tipTypeId: Int): Long {
        return executeWithExceptionMapping("GetCountByType") {
            database.tipQueries.getCountByType(tipTypeId.toLong()).executeAsOne()
        }
    }

    override suspend fun addAsync(tip: Tip): Tip {
        return executeWithExceptionMapping("Add") {
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

    override suspend fun updateAsync(tip: Tip) {
        executeWithExceptionMapping("Update") {
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

    override suspend fun deleteAsync(tip: Tip) {
        executeWithExceptionMapping("Delete") {
            database.tipQueries.deleteById(tip.id.toLong())
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Tip with ID ${tip.id} not found for deletion")
            }

            logger.i { "Deleted tip with ID ${tip.id}" }
        }
    }

    override suspend fun deleteByTypeIdAsync(tipTypeId: Int): Int {
        return executeWithExceptionMapping("DeleteByTypeId") {
            database.tipQueries.deleteByTypeId(tipTypeId.toLong())
            val rowsAffected = database.tipQueries.changes().executeAsOne()

            logger.i { "Deleted $rowsAffected tips for tip type ID $tipTypeId" }
            rowsAffected.toInt()
        }
    }

    override suspend fun updateCameraSettingsAsync(
        id: Int,
        fstop: String,
        shutterSpeed: String,
        iso: String
    ) {
        executeWithExceptionMapping("UpdateCameraSettings") {
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

    override suspend fun existsByIdAsync(id: Int): Boolean {
        return executeWithExceptionMapping("ExistsById") {
            database.tipQueries.selectById(id.toLong()).executeAsOneOrNull() != null
        }
    }

    override suspend fun createBulkAsync(tips: List<Tip>): List<Tip> {
        return executeWithExceptionMapping("CreateBulk") {
            if (tips.isEmpty()) return@executeWithExceptionMapping tips

            database.transactionWithResult {
                val result = mutableListOf<Tip>()

                tips.forEach { tip ->
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

                logger.i { "Bulk created ${result.size} tips" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(tips: List<Tip>): Int {
        return executeWithExceptionMapping("UpdateBulk") {
            if (tips.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                tips.forEach { tip ->
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

                    if (rowsAffected > 0) {
                        updatedCount++
                    }
                }

                logger.i { "Bulk updated $updatedCount tips" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(tipIds: List<Int>): Int {
        return executeWithExceptionMapping("DeleteBulk") {
            if (tipIds.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                tipIds.forEach { id ->
                    database.tipQueries.deleteById(id.toLong())
                    val rowsAffected = database.tipQueries.changes().executeAsOne()
                    if (rowsAffected > 0) {
                        deletedCount++
                    }
                }

                logger.i { "Bulk deleted $deletedCount tips" }
                deletedCount
            }
        }
    }

    override suspend fun getTipsByLocalizationAsync(localization: String): List<Tip> {
        return executeWithExceptionMapping("GetTipsByLocalization") {
            database.tipQueries.selectAll()
                .executeAsList()
                .filter { it.i8n == localization }
                .map { mapToDomain(it) }
        }
    }

    override suspend fun updateLocalizationAsync(id: Int, localization: String) {
        executeWithExceptionMapping("UpdateLocalization") {
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
                    }
                    TipCacheType.ALL -> {
                        tipsCache.clear()
                    }
                    else -> {
                        logger.d { "Cache type $cacheType not implemented for type-specific clearing" }
                    }
                }
                logger.d { "Cleared tip cache for type $tipTypeId, cache type $cacheType" }
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
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for tip" }
            throw exceptionMapper.mapToTipDomainException(ex, operationName)
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