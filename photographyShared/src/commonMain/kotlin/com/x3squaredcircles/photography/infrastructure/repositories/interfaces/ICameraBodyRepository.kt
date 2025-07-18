// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ICameraBodyRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.core.domain.common.Result

interface ICameraBodyRepository {
    suspend fun getByIdAsync(id: Int): Result<CameraBodyDto?>
    suspend fun getAllAsync(): Result<List<CameraBodyDto>>
    suspend fun getPagedAsync(pageSize: Int, offset: Int): Result<List<CameraBodyDto>>
    suspend fun getUserCreatedAsync(): Result<List<CameraBodyDto>>
    suspend fun getByMountTypeAsync(mountType: String): Result<List<CameraBodyDto>>
    suspend fun searchByNameAsync(searchTerm: String): Result<List<CameraBodyDto>>
    suspend fun createAsync(cameraBody: CameraBodyDto): Result<CameraBodyDto>
    suspend fun updateAsync(cameraBody: CameraBodyDto): Result<Unit>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun existsByNameAsync(name: String, excludeId: Int = 0): Result<Boolean>
    suspend fun createBulkAsync(cameraBodies: List<CameraBodyDto>): Result<List<CameraBodyDto>>
    suspend fun updateBulkAsync(cameraBodies: List<CameraBodyDto>): Result<Int>
    suspend fun deleteBulkAsync(ids: List<Int>): Result<Int>
    fun clearCache()
    fun clearCache(id: Int)
}