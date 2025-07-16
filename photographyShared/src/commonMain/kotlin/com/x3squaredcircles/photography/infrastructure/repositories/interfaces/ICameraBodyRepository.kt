// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ICameraBodyRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto

interface ICameraBodyRepository {
    suspend fun getByIdAsync(id: Int): CameraBodyDto?
    suspend fun getAllAsync(): List<CameraBodyDto>
    suspend fun getPagedAsync(pageSize: Int, offset: Int): List<CameraBodyDto>
    suspend fun getUserCreatedAsync(): List<CameraBodyDto>
    suspend fun getByMountTypeAsync(mountType: String): List<CameraBodyDto>
    suspend fun searchByNameAsync(searchTerm: String): List<CameraBodyDto>
    suspend fun addAsync(cameraBody: CameraBodyDto): CameraBodyDto
    suspend fun updateAsync(cameraBody: CameraBodyDto)
    suspend fun deleteAsync(id: Int)
    suspend fun getTotalCountAsync(): Long
    suspend fun existsByNameAsync(name: String, excludeId: Int = 0): Boolean
    suspend fun createBulkAsync(cameraBodies: List<CameraBodyDto>): List<CameraBodyDto>
    suspend fun updateBulkAsync(cameraBodies: List<CameraBodyDto>): Int
    suspend fun deleteBulkAsync(ids: List<Int>): Int
    fun clearCache()
    fun clearCache(id: Int)
}