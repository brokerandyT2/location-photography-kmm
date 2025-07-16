// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILensRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.lens.LensDto

interface ILensRepository {
    suspend fun getByIdAsync(id: Int): LensDto?
    suspend fun getAllAsync(): List<LensDto>
    suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): List<LensDto>
    suspend fun getUserCreatedAsync(): List<LensDto>
    suspend fun getByFocalLengthRangeAsync(minFocalLength: Double, maxFocalLength: Double): List<LensDto>
    suspend fun getCompatibleLensesAsync(cameraBodyId: Int): List<LensDto>
    suspend fun getPrimeLensesAsync(): List<LensDto>
    suspend fun getZoomLensesAsync(): List<LensDto>
    suspend fun addAsync(lens: LensDto): LensDto
    suspend fun updateAsync(lens: LensDto)
    suspend fun deleteAsync(id: Int)
    suspend fun getTotalCountAsync(): Long
    suspend fun getCountByTypeAsync(): Pair<Long, Long> // (primeCount, zoomCount)
    suspend fun createBulkAsync(lenses: List<LensDto>): List<LensDto>
    suspend fun updateBulkAsync(lenses: List<LensDto>): Int
    suspend fun deleteBulkAsync(ids: List<Int>): Int
    fun clearCache()
    fun clearCache(id: Int)
}