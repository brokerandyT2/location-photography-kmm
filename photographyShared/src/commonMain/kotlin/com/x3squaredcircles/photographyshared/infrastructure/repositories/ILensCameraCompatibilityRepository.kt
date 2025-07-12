// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ILensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility

/**
 * Repository interface for LensCameraCompatibility entity operations.
 */
interface ILensCameraCompatibilityRepository {
    
    suspend fun getAllAsync(): Result<List<LensCameraCompatibility>>
    suspend fun getByIdAsync(id: Int): Result<LensCameraCompatibility>
    suspend fun getByLensIdAsync(lensId: Int): Result<List<LensCameraCompatibility>>
    suspend fun getByCameraIdAsync(cameraBodyId: Int): Result<List<LensCameraCompatibility>>
    suspend fun createAsync(compatibility: LensCameraCompatibility): Result<LensCameraCompatibility>
    suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibility>): Result<List<LensCameraCompatibility>>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun deleteByLensAndCameraAsync(lensId: Int, cameraBodyId: Int): Result<Boolean>
    suspend fun deleteByLensIdAsync(lensId: Int): Result<Boolean>
    suspend fun deleteByCameraIdAsync(cameraBodyId: Int): Result<Boolean>
    suspend fun existsAsync(lensId: Int, cameraBodyId: Int): Result<Boolean>
    suspend fun getCountAsync(): Result<Int>
}