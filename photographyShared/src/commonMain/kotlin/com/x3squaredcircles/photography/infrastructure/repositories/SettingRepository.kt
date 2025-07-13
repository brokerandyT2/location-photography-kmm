// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/SettingRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.domain.entities.Setting
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

class SettingRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ISettingRepository {

    override suspend fun getAllAsync(): Result<List<Setting>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.settingQueries.selectAll().executeAsList()
                val settings = entities.map { entity ->
                    Setting(
                        id = entity.id.toInt(),
                        key = entity.key,
                        value = entity.value,
                        description = entity.description,
                        timestamp = entity.timestamp
                    )
                }
                Result.success(settings)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all settings", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<Setting> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.settingQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val setting = Setting(
                        id = entity.id.toInt(),
                        key = entity.key,
                        value = entity.value,
                        description = entity.description,
                        timestamp = entity.timestamp
                    )
                    Result.success(setting)
                } else {
                    Result.failure(Exception("Setting not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting setting by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getByKeyAsync(key: String): Result<Setting> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.settingQueries.selectByKey(key).executeAsOneOrNull()
                if (entity != null) {
                    val setting = Setting(
                        id = entity.id.toInt(),
                        key = entity.key,
                        value = entity.value,
                        description = entity.description,
                        timestamp = entity.timestamp
                    )
                    Result.success(setting)
                } else {
                    Result.failure(Exception("Setting not found for key: $key"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting setting by key: $key", e)
            Result.failure(e)
        }
    }

    override suspend fun getByKeysAsync(keys: List<String>): Result<List<Setting>> {
        return try {
            withContext(Dispatchers.IO) {
                val settings = mutableListOf<Setting>()
                keys.forEach { key ->
                    val entity = database.settingQueries.selectByKey(key).executeAsOneOrNull()
                    entity?.let {
                        settings.add(
                            Setting(
                                id = it.id.toInt(),
                                key = it.key,
                                value = it.value,
                                description = it.description,
                                timestamp = it.timestamp
                            )
                        )
                    }
                }
                Result.success(settings)
            }
        } catch (e: Exception) {
            logger.logError("Error getting settings by keys", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllAsDictionaryAsync(): Result<Map<String, String>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.settingQueries.selectAll().executeAsList()
                val dictionary = entities.associate { entity ->
                    entity.key to entity.value
                }
                Result.success(dictionary)
            }
        } catch (e: Exception) {
            logger.logError("Error getting settings as dictionary", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(setting: Setting): Result<Setting> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.settingQueries.insert(
                    key = setting.key,
                    value = setting.value,
                    description = setting.description,
                    timestamp = currentTime
                )

                val insertedId = database.settingQueries.transactionWithResult {
                    database.settingQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newSetting = setting.copy(
                    id = insertedId,
                    timestamp = currentTime
                )

                logger.logInformation("Created setting with ID: $insertedId")
                Result.success(newSetting)
            }
        } catch (e: Exception) {
            logger.logError("Error creating setting", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAsync(setting: Setting): Result<Setting> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.settingQueries.update(
                    key = setting.key,
                    value = setting.value,
                    description = setting.description,
                    timestamp = currentTime,
                    id = setting.id.toLong()
                )

                val updatedSetting = setting.copy(timestamp = currentTime)

                logger.logInformation("Updated setting with ID: ${setting.id}")
                Result.success(updatedSetting)
            }
        } catch (e: Exception) {
            logger.logError("Error updating setting", e)
            Result.failure(e)
        }
    }

    override suspend fun updateByKeyAsync(key: String, value: String, description: String): Result<Setting> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.settingQueries.updateByKey(
                    value = value,
                    description = description,
                    timestamp = currentTime,
                    key = key
                )

                val entity = database.settingQueries.selectByKey(key).executeAsOneOrNull()
                if (entity != null) {
                    val setting = Setting(
                        id = entity.id.toInt(),
                        key = entity.key,
                        value = entity.value,
                        description = entity.description,
                        timestamp = entity.timestamp
                    )
                    logger.logInformation("Updated setting by key: $key")
                    Result.success(setting)
                } else {
                    Result.failure(Exception("Setting not found after update"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error updating setting by key: $key", e)
            Result.failure(e)
        }
    }

    override suspend fun upsertAsync(key: String, value: String, description: String): Result<Setting> {
        return try {
            val existingResult = getByKeyAsync(key)
            if (existingResult.isSuccess) {
                val existing = existingResult.getOrNull()
                if (existing != null) {
                    val updated = existing.copy(
                        value = value,
                        description = description
                    )
                    updateAsync(updated)
                } else {
                    val newSetting = Setting(
                        id = 0,
                        key = key,
                        value = value,
                        description = description,
                        timestamp = Clock.System.now().epochSeconds
                    )
                    createAsync(newSetting)
                }
            } else {
                val newSetting = Setting(
                    id = 0,
                    key = key,
                    value = value,
                    description = description,
                    timestamp = Clock.System.now().epochSeconds
                )
                createAsync(newSetting)
            }
        } catch (e: Exception) {
            logger.logError("Error upserting setting", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.settingQueries.deleteById(id.toLong())
                logger.logInformation("Deleted setting with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting setting", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByKeyAsync(key: String): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.settingQueries.deleteByKey(key)
                logger.logInformation("Deleted setting with key: $key")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting setting by key", e)
            Result.failure(e)
        }
    }

    override suspend fun existsByKeyAsync(key: String): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.settingQueries.existsByKey(key).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if setting exists by key", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.settingQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting setting count", e)
            Result.failure(e)
        }
    }
}








