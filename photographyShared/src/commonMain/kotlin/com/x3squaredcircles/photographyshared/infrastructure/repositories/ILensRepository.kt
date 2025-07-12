// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ILensRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Lens

/**
 * Repository interface for Lens entity operations.
 */
interface ILensRepository {
    
    suspend fun getAllAsync(): Result<List<Lens>>
    suspend fun getByIdAsync(id: Int): Result<Lens>
    suspend fun getPagedAsync(skip: Int, take: Int): Result<List<Lens>>
    suspend fun getUserCreatedAsync(): Result<List<Lens>>
    suspend fun getByFocalLengthRangeAsync(focalLength: Double): Result<List<Lens>>
    suspend fun getCompatibleLensesAsync(cameraBodyId: Int): Result<List<Lens>>
    suspend fun getPrimesAsync(): Result<List<Lens>>
    suspend fun getZoomsAsync(): Result<List<Lens>>
    suspend fun createAsync(lens: Lens): Result<Lens>
    suspend fun updateAsync(lens: Lens): Result<Lens>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun getTotalCountAsync(): Result<Int>
    suspend fun getCountByTypeAsync(): Result<Pair<Int, Int>> // (primeCount, zoomCount)
}