// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ICameraBodyRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.CameraBody

/**
 * Repository interface for CameraBody entity operations.
 */
interface ICameraBodyRepository {
    
    suspend fun getAllAsync(): Result<List<CameraBody>>
    suspend fun getByIdAsync(id: Int): Result<CameraBody>
    suspend fun getPagedAsync(skip: Int, take: Int): Result<List<CameraBody>>
    suspend fun getUserCreatedAsync(): Result<List<CameraBody>>
    suspend fun getByMountTypeAsync(mountType: String): Result<List<CameraBody>>
    suspend fun searchByNameAsync(name: String): Result<List<CameraBody>>
    suspend fun createAsync(cameraBody: CameraBody): Result<CameraBody>
    suspend fun updateAsync(cameraBody: CameraBody): Result<CameraBody>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun existsByNameAsync(name: String, excludeId: Int = -1): Result<Boolean>
    suspend fun getTotalCountAsync(): Result<Int>
    suspend fun getCountByMountTypeAsync(): Result<Map<String, Int>>
}