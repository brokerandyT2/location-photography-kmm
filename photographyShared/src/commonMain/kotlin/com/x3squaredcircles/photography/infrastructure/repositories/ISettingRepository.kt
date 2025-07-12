// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ISettingRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Setting

/**
 * Repository interface for photography Setting entity operations.
 */
interface ISettingRepository {
    
    suspend fun getAllAsync(): Result<List<Setting>>
    suspend fun getByIdAsync(id: Int): Result<Setting>
    suspend fun getByKeyAsync(key: String): Result<Setting>
    suspend fun getByKeysAsync(keys: List<String>): Result<List<Setting>>
    suspend fun getAllAsDictionaryAsync(): Result<Map<String, String>>
    suspend fun createAsync(setting: Setting): Result<Setting>
    suspend fun updateAsync(setting: Setting): Result<Setting>
    suspend fun updateByKeyAsync(key: String, value: String, description: String = ""): Result<Setting>
    suspend fun upsertAsync(key: String, value: String, description: String = ""): Result<Setting>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByKeyAsync(key: String): Result<Boolean>
    suspend fun existsByKeyAsync(key: String): Result<Boolean>
    suspend fun getCountAsync(): Result<Int>
}