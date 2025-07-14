// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/interfaces/ISettingRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Setting

interface ISettingRepository {
    suspend fun getByIdAsync(id: Int): Setting?
    suspend fun getByKeyAsync(key: String): Setting?
    suspend fun getAllAsync(): List<Setting>
    suspend fun getByKeysAsync(keys: List<String>): List<Setting>
    suspend fun addAsync(setting: Setting): Setting
    suspend fun updateAsync(setting: Setting)
    suspend fun deleteAsync(setting: Setting)
    suspend fun upsertAsync(key: String, value: String, description: String = ""): Setting
    suspend fun getAllAsDictionaryAsync(): Map<String, String>
    suspend fun getByPrefixAsync(keyPrefix: String): List<Setting>
    suspend fun getRecentlyModifiedAsync(count: Int = 10): List<Setting>
    suspend fun existsAsync(key: String): Boolean
    suspend fun createBulkAsync(settings: List<Setting>): List<Setting>
    suspend fun updateBulkAsync(settings: List<Setting>): Int
    suspend fun deleteBulkAsync(keys: List<String>): Int
    suspend fun upsertBulkAsync(keyValuePairs: Map<String, String>): Map<String, String>
    fun clearCache()
    fun clearCache(key: String)
}