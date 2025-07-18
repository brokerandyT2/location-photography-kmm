// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ITipTypeRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.TipType
import com.x3squaredcircles.core.domain.common.Result

interface ITipTypeRepository {
    suspend fun getByIdAsync(id: Int): Result<TipType?>
    suspend fun getAllAsync(): Result<List<TipType>>
    suspend fun getByNameAsync(name: String): Result<TipType?>
    suspend fun getWithTipCountsAsync(): Result<List<TipTypeWithCount>>
    suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): Result<List<TipType>>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun createAsync(tipType: TipType): Result<TipType>
    suspend fun updateAsync(tipType: TipType): Result<Unit>
    suspend fun deleteAsync(tipType: TipType): Result<Unit>
    suspend fun existsByNameAsync(name: String, excludeId: Int = 0): Result<Boolean>
    suspend fun existsByIdAsync(id: Int): Result<Boolean>
    suspend fun createBulkAsync(tipTypes: List<TipType>): Result<List<TipType>>
    suspend fun updateBulkAsync(tipTypes: List<TipType>): Result<Int>
    suspend fun deleteBulkAsync(tipTypeIds: List<Int>): Result<Int>
    suspend fun getTipTypesByLocalizationAsync(localization: String): Result<List<TipType>>
    suspend fun updateLocalizationAsync(id: Int, localization: String): Result<Unit>
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(name: String)
}

data class TipTypeWithCount(
    val tipType: TipType,
    val tipCount: Long
)