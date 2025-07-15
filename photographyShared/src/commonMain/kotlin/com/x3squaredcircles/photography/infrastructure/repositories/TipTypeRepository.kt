// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/TipTypeRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.TipType
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.TipTypeWithCount
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class TipTypeRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ITipTypeRepository {

    private val tipTypesCache = mutableMapOf<Int, CachedTipType>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 60.minutes

    override suspend fun getByIdAsync(id: Int): TipType? {
        return executeWithExceptionMapping("GetById") {
            val entity = database.tipTypeQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): List<TipType> {
        return executeWithExceptionMapping("GetAll") {
            database.tipTypeQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByNameAsync(name: String): TipType? {
        return executeWithExceptionMapping("GetByName") {
            val entity = database.tipTypeQueries.selectByName(name).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getWithTipCountsAsync(): List<TipTypeWithCount> {
        return executeWithExceptionMapping("GetWithTipCounts") {
            database.tipTypeQueries.selectWithTips()
                .executeAsList()
                .map { entity ->
                    val tipType = TipType.fromPersistence(
                        id = entity.id.toInt(),
                        name = entity.name,
                        i8n = entity.i8n
                    )
                    TipTypeWithCount(
                        tipType = tipType,
                        tipCount = entity.COUNT ?: 0L
                    )
                }
        }
    }

    override suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): List<TipType> {
        return executeWithExceptionMapping("GetPaged") {
            database.tipTypeQueries.selectAll()
                .executeAsList()
                .drop((pageNumber - 1) * pageSize)
                .take(pageSize)
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getTotalCountAsync(): Long {
        return executeWithExceptionMapping("GetTotalCount") {
            database.tipTypeQueries.getCount().executeAsOne()
        }
    }

    override suspend fun addAsync(tipType: TipType): TipType {
        return executeWithExceptionMapping("Add") {
            val exists = database.tipTypeQueries.existsByName(tipType.name, 0L).executeAsOne()
            if (exists) {
                throw IllegalArgumentException("TipType with name '${tipType.name}' already exists")
            }

            database.tipTypeQueries.insert(
                name = tipType.name,
                i8n = tipType.i8n
            )

            val id = database.tipTypeQueries.lastInsertRowId().executeAsOne()
            val savedTipType = TipType.fromPersistence(
                id = id.toInt(),
                name = tipType.name,
                i8n = tipType.i8n
            )

            logger.i { "Created tip type with ID ${savedTipType.id}" }
            savedTipType
        }
    }

    override suspend fun updateAsync(tipType: TipType) {
        executeWithExceptionMapping("Update") {
            val exists = database.tipTypeQueries.existsByName(tipType.name, tipType.id.toLong()).executeAsOne()
            if (exists) {
                throw IllegalArgumentException("TipType with name '${tipType.name}' already exists")
            }

            database.tipTypeQueries.update(
                name = tipType.name,
                i8n = tipType.i8n,
                id = tipType.id.toLong()
            )
            val rowsAffected = database.tipTypeQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("TipType with ID ${tipType.id} not found for update")
            }

            logger.i { "Updated tip type with ID ${tipType.id}" }
        }
    }

    override suspend fun deleteAsync(tipType: TipType) {
        executeWithExceptionMapping("Delete") {
            database.tipTypeQueries.deleteById(tipType.id.toLong())
            val rowsAffected = database.tipTypeQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("TipType with ID ${tipType.id} not found for deletion")
            }

            logger.i { "Deleted tip type with ID ${tipType.id}" }
        }
    }

    override suspend fun existsByNameAsync(name: String, excludeId: Int): Boolean {
        return executeWithExceptionMapping("ExistsByName") {
            database.tipTypeQueries.existsByName(name, excludeId.toLong()).executeAsOne()
        }
    }

    override suspend fun existsByIdAsync(id: Int): Boolean {
        return executeWithExceptionMapping("ExistsById") {
            database.tipTypeQueries.selectById(id.toLong()).executeAsOneOrNull() != null
        }
    }

    override suspend fun createBulkAsync(tipTypes: List<TipType>): List<TipType> {
        return executeWithExceptionMapping("CreateBulk") {
            if (tipTypes.isEmpty()) return@executeWithExceptionMapping tipTypes

            database.transactionWithResult {
                val result = mutableListOf<TipType>()

                val names = tipTypes.map { it.name }
                val duplicateNames = names.groupBy { it }.filter { it.value.size > 1 }.keys
                if (duplicateNames.isNotEmpty()) {
                    throw IllegalArgumentException("Duplicate names in batch: ${duplicateNames.joinToString()}")
                }

                val existingNames = database.tipTypeQueries.selectAll()
                    .executeAsList()
                    .map { it.name }
                    .toSet()

                val conflictingNames = names.filter { it in existingNames }
                if (conflictingNames.isNotEmpty()) {
                    throw IllegalArgumentException("TipTypes with these names already exist: ${conflictingNames.joinToString()}")
                }

                tipTypes.forEach { tipType ->
                    database.tipTypeQueries.insert(
                        name = tipType.name,
                        i8n = tipType.i8n
                    )

                    val id = database.tipTypeQueries.lastInsertRowId().executeAsOne()
                    val savedTipType = TipType.fromPersistence(
                        id = id.toInt(),
                        name = tipType.name,
                        i8n = tipType.i8n
                    )
                    result.add(savedTipType)
                }

                logger.i { "Bulk created ${result.size} tip types" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(tipTypes: List<TipType>): Int {
        return executeWithExceptionMapping("UpdateBulk") {
            if (tipTypes.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                tipTypes.forEach { tipType ->
                    val exists = database.tipTypeQueries.existsByName(tipType.name, tipType.id.toLong()).executeAsOne()
                    if (!exists) {
                        database.tipTypeQueries.update(
                            name = tipType.name,
                            i8n = tipType.i8n,
                            id = tipType.id.toLong()
                        )
                        val rowsAffected = database.tipTypeQueries.changes().executeAsOne()

                        if (rowsAffected > 0) {
                            updatedCount++
                        }
                    }
                }

                logger.i { "Bulk updated $updatedCount tip types" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(tipTypeIds: List<Int>): Int {
        return executeWithExceptionMapping("DeleteBulk") {
            if (tipTypeIds.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                tipTypeIds.forEach { id ->
                    database.tipTypeQueries.deleteById(id.toLong())
                    val rowsAffected = database.tipTypeQueries.changes().executeAsOne()
                    if (rowsAffected > 0) {
                        deletedCount++
                    }
                }

                logger.i { "Bulk deleted $deletedCount tip types" }
                deletedCount
            }
        }
    }

    override suspend fun getTipTypesByLocalizationAsync(localization: String): List<TipType> {
        return executeWithExceptionMapping("GetTipTypesByLocalization") {
            database.tipTypeQueries.selectAll()
                .executeAsList()
                .filter { it.i8n == localization }
                .map { mapToDomain(it) }
        }
    }

    override suspend fun updateLocalizationAsync(id: Int, localization: String) {
        executeWithExceptionMapping("UpdateLocalization") {
            val tipType = database.tipTypeQueries.selectById(id.toLong()).executeAsOneOrNull()
                ?: throw IllegalArgumentException("TipType with ID $id not found")

            database.tipTypeQueries.update(
                name = tipType.name,
                i8n = localization,
                id = id.toLong()
            )
            val rowsAffected = database.tipTypeQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("TipType with ID $id not found for localization update")
            }

            logger.i { "Updated localization for tip type with ID $id to $localization" }
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                tipTypesCache.clear()
                logger.i { "TipType cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                tipTypesCache.remove(id)
                logger.d { "Removed tip type $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(name: String) {
        if (cacheMutex.tryLock()) {
            try {
                tipTypesCache.entries.removeAll { it.value.tipType.name == name }
                logger.d { "Removed tip type with name '$name' from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.TipType): TipType {
        return TipType.fromPersistence(
            id = entity.id.toInt(),
            name = entity.name,
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
            logger.e(ex) { "Repository operation $operationName failed for tip type" }
            throw exceptionMapper.mapToTipTypeDomainException(ex, operationName)
        }
    }

    private data class CachedTipType(
        val tipType: TipType,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}