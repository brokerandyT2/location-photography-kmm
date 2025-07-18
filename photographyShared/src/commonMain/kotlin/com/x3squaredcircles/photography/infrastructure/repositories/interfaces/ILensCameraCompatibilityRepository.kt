// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityDto
import com.x3squaredcircles.core.domain.common.Result

interface ILensCameraCompatibilityRepository {
    suspend fun getByIdAsync(id: Int): Result<LensCameraCompatibilityDto?>
    suspend fun getAllAsync(): Result<List<LensCameraCompatibilityDto>>
    suspend fun getByLensIdAsync(lensId: Int): Result<List<LensCameraCompatibilityDto>>
    suspend fun getByCameraIdAsync(cameraBodyId: Int): Result<List<LensCameraCompatibilityDto>>
    suspend fun createAsync(compatibility: LensCameraCompatibilityDto): Result<LensCameraCompatibilityDto>
    suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibilityDto>): Result<List<LensCameraCompatibilityDto>>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun deleteByLensAndCameraAsync(lensId: Int, cameraBodyId: Int): Result<Unit>
    suspend fun deleteByLensIdAsync(lensId: Int): Result<Unit>
    suspend fun deleteByCameraIdAsync(cameraBodyId: Int): Result<Unit>
    suspend fun existsAsync(lensId: Int, cameraBodyId: Int): Result<Boolean>
    suspend fun getCountAsync(): Result<Long>
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(lensId: Int, cameraBodyId: Int)
}