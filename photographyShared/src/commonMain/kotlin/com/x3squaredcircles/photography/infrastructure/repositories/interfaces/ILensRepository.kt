// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ILensRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.core.domain.common.Result

interface ILensRepository {
    suspend fun getByIdAsync(id: Int): Result<LensDto?>
    suspend fun getAllAsync(): Result<List<LensDto>>
    suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): Result<List<LensDto>>
    suspend fun getUserCreatedAsync(): Result<List<LensDto>>
    suspend fun getByFocalLengthRangeAsync(minFocalLength: Double, maxFocalLength: Double): Result<List<LensDto>>
    suspend fun getCompatibleLensesAsync(cameraBodyId: Int): Result<List<LensDto>>
    suspend fun getPrimeLensesAsync(): Result<List<LensDto>>
    suspend fun getZoomLensesAsync(): Result<List<LensDto>>
    suspend fun createAsync(lens: LensDto): Result<LensDto>
    suspend fun updateAsync(lens: LensDto): Result<Unit>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun getCountByTypeAsync(): Result<Pair<Long, Long>>
    suspend fun createBulkAsync(lenses: List<LensDto>): Result<List<LensDto>>
    suspend fun updateBulkAsync(lenses: List<LensDto>): Result<Int>
    suspend fun deleteBulkAsync(ids: List<Int>): Result<Int>
    fun clearCache()
    fun clearCache(id: Int)
}