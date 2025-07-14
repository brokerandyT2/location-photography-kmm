// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/SettingRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Setting
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

    // In-memory cache for frequently accessed settings
    private val settingsCache = mutableMapOf<String, CachedSetting>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 15.minutes

    override suspend fun getByIdAsync(id: Int): Setting? {
        return executeWithExceptionMapping("GetById") {
            val entity = database.settingQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getByKeyAsync(key: String): Setting? {
        return executeWithExceptionMapping("GetByKey") {
            // Check cache first
            cacheMutex.withLock {
                settingsCache[key]?.let { cached ->
                    if (!cached.isExpired) {
                        logger.d { "Retrieved setting $key from cache" }
                        return@executeWithExceptionMapping cached.setting
                    } else {
                        settingsCache.remove(key)
                    }
                }
            }

            // Query database
            val entity = database.settingQueries.selectByKey(key).executeAsOneOrNull()
            val setting = entity?.let { mapToDomain(it) }

            // Cache the result (including null)
            cacheMutex.withLock {
                val expiration = Clock.System.now() + cacheExpiration
                settingsCache[key] = CachedSetting(setting, expiration)
            }

            setting
        }
    }

    override suspend fun getAllAsync(): List<Setting> {
        return executeWithExceptionMapping("GetAll") {
            database.settingQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByKeysAsync(keys: List<String>): List<Setting> {
        return executeWithExceptionMapping("GetByKeys") {
            if (keys.isEmpty()) return@executeWithExceptionMapping emptyList()

            val settings = mutableListOf<Setting>()
            val uncachedKeys = mutableListOf<String>()

            // Check cache for each key
            cacheMutex.withLock {
                keys.forEach { key ->
                    settingsCache[key]?.let { cached ->
                        if (!cached.isExpired) {
                            cached.setting?.let { settings.add(it) }
                        } else {
                            settingsCache.remove(key)
                            uncachedKeys.add(key)
                        }
                    } ?: run {
                        uncachedKeys.add(key)
                    }
                }
            }

            // Query database for uncached keys
            if (uncachedKeys.isNotEmpty()) {
                val entities = database.settingQueries.selectByKeys(uncachedKeys).executeAsList()
                val uncachedSettings = entities.map { mapToDomain(it) }

                // Cache the results
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    val foundKeys = uncachedSettings.map { it.key }.toSet()

                    uncachedSettings.forEach { setting ->
                        settingsCache[setting.key] = CachedSetting(setting, expiration)
                    }

                    // Cache null results for keys not found
                    uncachedKeys.filter { it !in foundKeys }.forEach { key ->
                        settingsCache[key] = CachedSetting(null, expiration)
                    }
                }

                settings.addAll(uncachedSettings)
            }

            settings
        }
    }

    override suspend fun addAsync(setting: Setting): Setting {
        return executeWithExceptionMapping("Add") {
            // Check if key already exists
            val exists = database.settingQueries.existsByKey(setting.key).executeAsOne()
            if (exists) {
                throw IllegalArgumentException("Setting with key '${setting.key}' already exists")
            }

            val now = Clock.System.now()
            database.settingQueries.insert(
                key = setting.key,
                value_ = setting.value,
                description = setting.description,
                timestamp = now.toEpochMilliseconds()
            )

            val id = database.settingQueries.lastInsertRowId().executeAsOne()
            val updatedSetting = Setting.fromPersistence(
                id = id.toInt(),
                key = setting.key,
                value = setting.value,
                description = setting.description,
                timestamp = now
            )

            // Update cache
            cacheMutex.withLock {
                val expiration = Clock.System.now() + cacheExpiration
                settingsCache[setting.key] = CachedSetting(updatedSetting, expiration)
            }

            logger.i { "Created setting with key ${setting.key}" }
            updatedSetting
        }
    }

    override suspend fun updateAsync(setting: Setting) {
        executeWithExceptionMapping("Update") {
            val now = Clock.System.now()
            val rowsAffected = database.settingQueries.updateByKey(
                value_ = setting.value,
                description = setting.description,
                timestamp = now.toEpochMilliseconds(),
                key = setting.key
            ).executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Setting with key '${setting.key}' not found for update")
            }

            val updatedSetting = Setting.fromPersistence(
                id = setting.id,
                key = setting.key,
                value = setting.value,
                description = setting.description,
                timestamp = now
            )

            // Update cache
            cacheMutex.withLock {
                val expiration = Clock.System.now() + cacheExpiration
                settingsCache[setting.key] = CachedSetting(updatedSetting, expiration)
            }

            logger.i { "Updated setting with key ${setting.key}" }
        }
    }

    override suspend fun deleteAsync(setting: Setting) {
        executeWithExceptionMapping("Delete") {
            val rowsAffected = database.settingQueries.deleteByKey(setting.key).executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Setting with key '${setting.key}' not found for deletion")
            }

            // Remove from cache
            cacheMutex.withLock {
                settingsCache.remove(setting.key)
            }

            logger.i { "Deleted setting with key ${setting.key}" }
        }
    }

    override suspend fun upsertAsync(key: String, value: String, description: String): Setting {
        return executeWithExceptionMapping("Upsert") {
            database.transactionWithResult {
                val now = Clock.System.now()

                // Try to get existing setting
                val existing = database.settingQueries.selectByKey(key).executeAsOneOrNull()

                val setting = if (existing != null) {
                    // Update existing
                    database.settingQueries.updateByKey(
                        value_ = value,
                        description = description,
                        timestamp = now.toEpochMilliseconds(),
                        key = key
                    )

                    Setting.fromPersistence(
                        id = existing.id.toInt(),
                        key = key,
                        value = value,
                        description = description,
                        timestamp = now
                    ).also {
                        logger.i { "Updated setting with key $key via upsert" }
                    }
                } else {
                    // Create new
                    database.settingQueries.insert(
                        key = key,
                        value_ = value,
                        description = description,
                        timestamp = now.toEpochMilliseconds()
                    )

                    val id = database.settingQueries.lastInsertRowId().executeAsOne()
                    Setting.fromPersistence(
                        id = id.toInt(),
                        key = key,
                        value = value,
                        description = description,
                        timestamp = now
                    ).also {
                        logger.i { "Created setting with key $key via upsert" }
                    }
                }

                // Update cache
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    settingsCache[key] = CachedSetting(setting, expiration)
                }

                setting
            }
        }
    }

    override suspend fun getAllAsDictionaryAsync(): Map<String, String> {
        return executeWithExceptionMapping("GetAllAsDictionary") {
            database.settingQueries.selectAllAsDictionary()
                .executeAsList()
                .associate { it.key to it.value_ }
        }
    }

    override suspend fun getByPrefixAsync(keyPrefix: String): List<Setting> {
        return executeWithExceptionMapping("GetByPrefix") {
            database.settingQueries.selectByKeys(listOf("$keyPrefix%"))
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRecentlyModifiedAsync(count: Int): List<Setting> {
        return executeWithExceptionMapping("GetRecentlyModified") {
            database.settingQueries.selectAll()
                .executeAsList()
                .sortedByDescending { it.timestamp }
                .take(count)
                .map { mapToDomain(it) }
        }
    }

    override suspend fun existsAsync(key: String): Boolean {
        return executeWithExceptionMapping("Exists") {
            // Check cache first
            cacheMutex.withLock {
                settingsCache[key]?.let { cached ->
                    if (!cached.isExpired) {
                        return@executeWithExceptionMapping cached.setting != null
                    }
                }
            }

            // Check database
            database.settingQueries.existsByKey(key).executeAsOne()
        }
    }

    override suspend fun createBulkAsync(settings: List<Setting>): List<Setting> {
        return executeWithExceptionMapping("CreateBulk") {
            if (settings.isEmpty()) return@executeWithExceptionMapping settings

            database.transactionWithResult {
                val now = Clock.System.now()
                val result = mutableListOf<Setting>()

                // Check for duplicate keys in batch
                val keys = settings.map { it.key }
                val duplicateKeys = keys.groupBy { it }.filter { it.value.size > 1 }.keys
                if (duplicateKeys.isNotEmpty()) {
                    throw IllegalArgumentException("Duplicate keys in batch: ${duplicateKeys.joinToString()}")
                }

                // Check for existing keys in database
                val existingKeys = database.settingQueries.selectByKeys(keys)
                    .executeAsList()
                    .map { it.key }
                    .toSet()

                if (existingKeys.isNotEmpty()) {
                    throw IllegalArgumentException("Settings with these keys already exist: ${existingKeys.joinToString()}")
                }

                // Bulk insert
                settings.forEach { setting ->
                    database.settingQueries.insert(
                        key = setting.key,
                        value_ = setting.value,
                        description = setting.description,
                        timestamp = now.toEpochMilliseconds()
                    )

                    val id = database.settingQueries.lastInsertRowId().executeAsOne()
                    val createdSetting = Setting.fromPersistence(
                        id = id.toInt(),
                        key = setting.key,
                        value = setting.value,
                        description = setting.description,
                        timestamp = now
                    )
                    result.add(createdSetting)
                }

                // Update cache
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    result.forEach { setting ->
                        settingsCache[setting.key] = CachedSetting(setting, expiration)
                    }
                }

                logger.i { "Bulk created ${result.size} settings" }
                result
            }
        }
    }

    override suspend fun updateBulkAsync(settings: List<Setting>): Int {
        return executeWithExceptionMapping("UpdateBulk") {
            if (settings.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                val now = Clock.System.now()
                var updatedCount = 0

                settings.forEach { setting ->
                    val rowsAffected = database.settingQueries.updateByKey(
                        value_ = setting.value,
                        description = setting.description,
                        timestamp = now.toEpochMilliseconds(),
                        key = setting.key
                    ).executeAsOne()

                    if (rowsAffected > 0) {
                        updatedCount++

                        val updatedSetting = Setting.fromPersistence(
                            id = setting.id,
                            key = setting.key,
                            value = setting.value,
                            description = setting.description,
                            timestamp = now
                        )

                        // Update cache
                        cacheMutex.withLock {
                            val expiration = Clock.System.now() + cacheExpiration
                            settingsCache[setting.key] = CachedSetting(updatedSetting, expiration)
                        }
                    }
                }

                logger.i { "Bulk updated $updatedCount settings" }
                updatedCount
            }
        }
    }

    override suspend fun deleteBulkAsync(keys: List<String>): Int {
        return executeWithExceptionMapping("DeleteBulk") {
            if (keys.isEmpty()) return@executeWithExceptionMapping 0

            database.transactionWithResult {
                var deletedCount = 0

                keys.forEach { key ->
                    val rowsAffected = database.settingQueries.deleteByKey(key).executeAsOne()
                    if (rowsAffected > 0) {
                        deletedCount++
                    }
                }

                // Remove from cache
                cacheMutex.withLock {
                    keys.forEach { key ->
                        settingsCache.remove(key)
                    }
                }

                logger.i { "Bulk deleted $deletedCount settings" }
                deletedCount
            }
        }
    }

    override suspend fun upsertBulkAsync(keyValuePairs: Map<String, String>): Map<String, String> {
        return executeWithExceptionMapping("UpsertBulk") {
            if (keyValuePairs.isEmpty()) return@executeWithExceptionMapping emptyMap()

            database.transactionWithResult {
                val now = Clock.System.now()
                val result = mutableMapOf<String, String>()

                // Get existing settings
                val keys = keyValuePairs.keys.toList()
                val existingSettings = database.settingQueries.selectByKeys(keys)
                    .executeAsList()
                    .associateBy { it.key }

                keyValuePairs.forEach { (key, value) ->
                    if (existingSettings.containsKey(key)) {
                        // Update existing
                        database.settingQueries.updateByKey(
                            value_ = value,
                            description = "",
                            timestamp = now.toEpochMilliseconds(),
                            key = key
                        )
                    } else {
                        // Insert new
                        database.settingQueries.insert(
                            key = key,
                            value_ = value,
                            description = "",
                            timestamp = now.toEpochMilliseconds()
                        )
                    }
                    result[key] = value
                }

                // Update cache
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    keyValuePairs.forEach { (key, value) ->
                        val setting = Setting.create(key, value)
                        settingsCache[key] = CachedSetting(setting, expiration)
                    }
                }

                logger.i { "Bulk upserted ${keyValuePairs.size} settings" }
                result
            }
        }
    }

    override fun clearCache() {
        cacheMutex.tryLock {
            settingsCache.clear()
            logger.i { "Settings cache cleared" }
        }
    }

    override fun clearCache(key: String) {
        cacheMutex.tryLock {
            settingsCache.remove(key)
            logger.d { "Removed setting $key from cache" }
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
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for setting" }
            throw exceptionMapper.mapToSettingDomainException(ex, operationName)
        }
    }

    private data class CachedSetting(
        val setting: Setting?,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}