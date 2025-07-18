// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/SettingRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Setting
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class SettingRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : ISettingRepository {

    private val settingsCache = mutableMapOf<String, CachedSetting>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 15.minutes

    override suspend fun getByIdAsync(id: Int): Result<Setting?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.settingQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getByKeyAsync(key: String): Result<Setting?> {
        return executeWithExceptionMapping("GetByKey") {
            cacheMutex.withLock {
                val cached = settingsCache[key]
                if (cached != null && !cached.isExpired) {
                    logger.d { "Cache hit for setting key: $key" }
                    return@executeWithExceptionMapping cached.setting
                }
            }

            val entity = database.settingQueries.selectByKey(key).executeAsOneOrNull()
            val setting = entity?.let { mapToDomain(it) }

            if (setting != null) {
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    settingsCache[key] = CachedSetting(setting, expiration)
                    logger.d { "Cached setting for key: $key" }
                }
            }

            setting
        }
    }

    override suspend fun getAllAsync(): Result<List<Setting>> {
        return executeWithExceptionMapping("GetAll") {
            database.settingQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByKeysAsync(keys: List<String>): Result<List<Setting>> {
        return executeWithExceptionMapping("GetByKeys") {
            if (keys.isEmpty()) return@executeWithExceptionMapping emptyList()

            val results = mutableListOf<Setting>()
            val uncachedKeys = mutableListOf<String>()

            cacheMutex.withLock {
                for (key in keys) {
                    val cached = settingsCache[key]
                    if (cached != null && !cached.isExpired) {
                        results.add(cached.setting)
                        logger.d { "Cache hit for setting key: $key" }
                    } else {
                        uncachedKeys.add(key)
                    }
                }
            }

            if (uncachedKeys.isNotEmpty()) {
                val entities = database.settingQueries.selectAll()
                    .executeAsList()
                    .filter { it.key in uncachedKeys }

                val settings = entities.map { mapToDomain(it) }
                results.addAll(settings)

                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    for (setting in settings) {
                        settingsCache[setting.key] = CachedSetting(setting, expiration)
                        logger.d { "Cached setting for key: ${setting.key}" }
                    }
                }
            }

            results
        }
    }

    override suspend fun createAsync(setting: Setting): Result<Setting> {
        return executeWithExceptionMapping("Create") {
            val exists = database.settingQueries.existsByKey(setting.key).executeAsOne()
            if (exists) {
                throw IllegalArgumentException("Setting with key '${setting.key}' already exists")
            }

            database.settingQueries.insert(
                key = setting.key,
                value_ = setting.value,
                description = setting.description,
                timestamp = setting.timestamp.toEpochMilliseconds()
            )

            val id = database.settingQueries.lastInsertRowId().executeAsOne()
            val savedSetting = Setting.fromPersistence(
                id = id.toInt(),
                key = setting.key,
                value = setting.value,
                description = setting.description,
                timestamp = setting.timestamp
            )

            cacheMutex.withLock {
                val expiration = Clock.System.now() + cacheExpiration
                settingsCache[setting.key] = CachedSetting(savedSetting, expiration)
            }

            logger.i { "Created setting with key ${setting.key}" }
            savedSetting
        }
    }

    override suspend fun updateAsync(setting: Setting): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.settingQueries.update(
                id = setting.id.toLong(),
                value_ = setting.value,
                description = setting.description,
                timestamp = setting.timestamp.toEpochMilliseconds()
            )
            val rowsAffected = database.settingQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Setting with ID ${setting.id} not found for update")
            }

            cacheMutex.withLock {
                settingsCache.remove(setting.key)
                logger.d { "Removed setting ${setting.key} from cache due to update" }
            }

            logger.i { "Updated setting with ID ${setting.id}" }
        }
    }

    override suspend fun deleteAsync(setting: Setting): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.settingQueries.deleteById(setting.id.toLong())
            val rowsAffected = database.settingQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Setting with ID ${setting.id} not found for deletion")
            }

            cacheMutex.withLock {
                settingsCache.remove(setting.key)
                logger.d { "Removed setting ${setting.key} from cache due to deletion" }
            }

            logger.i { "Deleted setting with ID ${setting.id}" }
        }
    }

    override suspend fun upsertAsync(key: String, value: String, description: String): Result<Setting> {
        return executeWithExceptionMapping("Upsert") {
            val now = Clock.System.now()
            val existing = database.settingQueries.selectByKey(key).executeAsOneOrNull()

            val setting = if (existing != null) {
                database.settingQueries.update(

                    value_ = value,
                    description = description,
                    timestamp = now.toEpochMilliseconds(),
                    id = existing.id
                )

                val updatedSetting = Setting.fromPersistence(
                    id = existing.id.toInt(),
                    key = key,
                    value = value,
                    description = description,
                    timestamp = now
                )

                logger.i { "Updated setting with key $key via upsert" }
                updatedSetting
            } else {
                database.settingQueries.insert(
                    key = key,
                    value_ = value,
                    description = description,
                    timestamp = now.toEpochMilliseconds()
                )

                val id = database.settingQueries.lastInsertRowId().executeAsOne()
                val createdSetting = Setting.fromPersistence(
                    id = id.toInt(),
                    key = key,
                    value = value,
                    description = description,
                    timestamp = now
                )

                logger.i { "Created setting with key $key via upsert" }
                createdSetting
            }

            cacheMutex.withLock {
                val expiration = Clock.System.now() + cacheExpiration
                settingsCache[key] = CachedSetting(setting, expiration)
            }

            setting
        }
    }

    override suspend fun getAllAsDictionaryAsync(): Result<Map<String, String>> {
        return executeWithExceptionMapping("GetAllAsDictionary") {
            database.settingQueries.selectAllAsDictionary()
                .executeAsList()
                .associate { it.key to it.value_ }
        }
    }

    override suspend fun getByPrefixAsync(keyPrefix: String): Result<List<Setting>> {
        return executeWithExceptionMapping("GetByPrefix") {
            database.settingQueries.selectAll()
                .executeAsList()
                .filter { it.key.startsWith(keyPrefix) }
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRecentlyModifiedAsync(count: Int): Result<List<Setting>> {
        return executeWithExceptionMapping("GetRecentlyModified") {
            database.settingQueries.selectAll()
                .executeAsList()
                .sortedByDescending { it.timestamp }
                .take(count)
                .map { mapToDomain(it) }
        }
    }

    override suspend fun existsAsync(key: String): Result<Boolean> {
        return executeWithExceptionMapping("Exists") {
            database.settingQueries.existsByKey(key).executeAsOne()
        }
    }

    override suspend fun createBulkAsync(settings: List<Setting>): Result<List<Setting>> {
        return executeWithExceptionMapping("CreateBulk") {
            if (settings.isEmpty()) return@executeWithExceptionMapping settings

            database.transactionWithResult {
                val result = mutableListOf<Setting>()

                for (setting in settings) {
                    val exists = database.settingQueries.existsByKey(setting.key).executeAsOne()
                    if (exists) {
                        throw IllegalArgumentException("Setting with key '${setting.key}' already exists")
                    }

                    database.settingQueries.insert(
                        key = setting.key,
                        value_ = setting.value,
                        description = setting.description,
                        timestamp = setting.timestamp.toEpochMilliseconds()
                    )

                    val id = database.settingQueries.lastInsertRowId().executeAsOne()
                    val savedSetting = Setting.fromPersistence(
                        id = id.toInt(),
                        key = setting.key,
                        value = setting.value,
                        description = setting.description,
                        timestamp = setting.timestamp
                    )
                    result.add(savedSetting)
                }

                cacheMutex.tryLock() {
                    val expiration = Clock.System.now() + cacheExpiration
                    for (setting in result) {
                        settingsCache[setting.key] = CachedSetting(setting, expiration)
                    }
                }

                logger.i { "Created ${result.size} settings in bulk" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(settings: List<Setting>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            if (settings.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var updatedCount = 0

                for (setting in settings) {
                    database.settingQueries.update(

                        value_ = setting.value,
                        description = setting.description,
                        timestamp = setting.timestamp.toEpochMilliseconds(),
                        id = setting.id.toLong()
                    )
                    val rowsAffected = database.settingQueries.changes().executeAsOne()
                    if (rowsAffected > 0L) {
                        updatedCount++
                    }
                }

                cacheMutex.tryLock {
                    for (setting in settings) {
                        settingsCache.remove(setting.key)
                    }
                }

                logger.i { "Updated $updatedCount settings in bulk" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(keys: List<String>): Result<Int> {
        return executeWithExceptionMapping("DeleteBulk") {
            if (keys.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                for (key in keys) {
                    val setting = database.settingQueries.selectByKey(key).executeAsOneOrNull()
                    if (setting != null) {
                        database.settingQueries.deleteById(setting.id)
                        val rowsAffected = database.settingQueries.changes().executeAsOne()
                        if (rowsAffected > 0L) {
                            deletedCount++
                        }
                    }
                }

                cacheMutex.tryLock {
                    for (key in keys) {
                        settingsCache.remove(key)
                    }
                }

                logger.i { "Deleted $deletedCount settings in bulk" }
                deletedCount
            }
        }
    }

    override suspend fun upsertBulkAsync(keyValuePairs: Map<String, String>): Result<Map<String, String>> {
        return executeWithExceptionMapping("UpsertBulk") {
            if (keyValuePairs.isEmpty()) return@executeWithExceptionMapping emptyMap()

            database.transactionWithResult {
                val result = mutableMapOf<String, String>()
                val now = Clock.System.now()

                for ((key, value) in keyValuePairs) {
                    val existing = database.settingQueries.selectByKey(key).executeAsOneOrNull()

                    if (existing != null) {
                        database.settingQueries.update(

                            value_ = value,
                            description = existing.description,
                            timestamp = now.toEpochMilliseconds(),
                            id = existing.id
                        )
                    } else {
                        database.settingQueries.insert(
                            key = key,
                            value_ = value,
                            description = "",
                            timestamp = now.toEpochMilliseconds()
                        )
                    }
                    result[key] = value
                }

                cacheMutex.tryLock {
                    for (key in keyValuePairs.keys) {
                        settingsCache.remove(key)
                    }
                }

                logger.i { "Upserted ${result.size} settings in bulk" }
                result
            }
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                settingsCache.clear()
                logger.i { "Setting cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(key: String) {
        if (cacheMutex.tryLock()) {
            try {
                settingsCache.remove(key)
                logger.d { "Removed setting $key from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.Setting): Setting {
        return Setting.fromPersistence(
            id = entity.id.toInt(),
            key = entity.key,
            value = entity.value_,
            description = entity.description,
            timestamp = Instant.fromEpochMilliseconds(entity.timestamp)
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
            logger.e(ex) { "Repository operation $operationName failed for setting" }
            val mappedException = exceptionMapper.mapToSettingDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedSetting(
        val setting: Setting,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}