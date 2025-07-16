// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityDto

interface ILensCameraCompatibilityRepository {
    suspend fun getByIdAsync(id: Int): LensCameraCompatibilityDto?
    suspend fun getAllAsync(): List<LensCameraCompatibilityDto>
    suspend fun getByLensIdAsync(lensId: Int): List<LensCameraCompatibilityDto>
    suspend fun getByCameraIdAsync(cameraBodyId: Int): List<LensCameraCompatibilityDto>
    suspend fun addAsync(compatibility: LensCameraCompatibilityDto): LensCameraCompatibilityDto
    suspend fun addBatchAsync(compatibilities: List<LensCameraCompatibilityDto>): List<LensCameraCompatibilityDto>
    suspend fun deleteAsync(id: Int)
    suspend fun deleteByLensAndCameraAsync(lensId: Int, cameraBodyId: Int)
    suspend fun deleteByLensIdAsync(lensId: Int)
    suspend fun deleteByCameraIdAsync(cameraBodyId: Int)
    suspend fun existsAsync(lensId: Int, cameraBodyId: Int): Boolean
    suspend fun getCountAsync(): Long
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(lensId: Int, cameraBodyId: Int)
}