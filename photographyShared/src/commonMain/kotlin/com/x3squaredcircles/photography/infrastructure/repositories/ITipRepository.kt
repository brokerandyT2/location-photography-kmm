// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ITipRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Tip

/**
 * Repository interface for photography Tip entity operations.
 */
interface ITipRepository {
    
    suspend fun getAllAsync(): Result<List<Tip>>
    suspend fun getByIdAsync(id: Int): Result<Tip>
    suspend fun getByTypeAsync(tipTypeId: Int): Result<List<Tip>>
    suspend fun getWithCameraSettingsAsync(): Result<List<Tip>>
    suspend fun searchAsync(searchTerm: String): Result<List<Tip>>
    suspend fun getRandomAsync(count: Int): Result<List<Tip>>
    suspend fun createAsync(tip: Tip): Result<Tip>
    suspend fun updateAsync(tip: Tip): Result<Tip>
    suspend fun updateCameraSettingsAsync(id: Int, fstop: String, shutterSpeed: String, iso: String): Result<Boolean>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByTypeAsync(tipTypeId: Int): Result<Boolean>
    suspend fun getCountAsync(): Result<Int>
    suspend fun getCountByTypeAsync(tipTypeId: Int): Result<Int>
}