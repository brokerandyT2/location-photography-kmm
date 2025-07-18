// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ISettingRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Setting
import com.x3squaredcircles.core.domain.common.Result

interface ISettingRepository {
    suspend fun getByIdAsync(id: Int): Result<Setting?>
    suspend fun getByKeyAsync(key: String): Result<Setting?>
    suspend fun getAllAsync(): Result<List<Setting>>
    suspend fun getByKeysAsync(keys: List<String>): Result<List<Setting>>
    suspend fun createAsync(setting: Setting): Result<Setting>
    suspend fun updateAsync(setting: Setting): Result<Unit>
    suspend fun deleteAsync(setting: Setting): Result<Unit>
    suspend fun upsertAsync(key: String, value: String, description: String = ""): Result<Setting>
    suspend fun getAllAsDictionaryAsync(): Result<Map<String, String>>
    suspend fun getByPrefixAsync(keyPrefix: String): Result<List<Setting>>
    suspend fun getRecentlyModifiedAsync(count: Int = 10): Result<List<Setting>>
    suspend fun existsAsync(key: String): Result<Boolean>
    suspend fun createBulkAsync(settings: List<Setting>): Result<List<Setting>>
    suspend fun updateBulkAsync(settings: List<Setting>): Result<Int>
    suspend fun deleteBulkAsync(keys: List<String>): Result<Int>
    suspend fun upsertBulkAsync(keyValuePairs: Map<String, String>): Result<Map<String, String>>
    fun clearCache()
    fun clearCache(key: String)
}