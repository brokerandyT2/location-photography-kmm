// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ITipTypeRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.TipType

interface ITipTypeRepository {
    suspend fun getByIdAsync(id: Int): TipType?
    suspend fun getAllAsync(): List<TipType>
    suspend fun getByNameAsync(name: String): TipType?
    suspend fun getWithTipCountsAsync(): List<TipTypeWithCount>
    suspend fun getPagedAsync(pageNumber: Int, pageSize: Int): List<TipType>
    suspend fun getTotalCountAsync(): Long
    suspend fun addAsync(tipType: TipType): TipType
    suspend fun updateAsync(tipType: TipType)
    suspend fun deleteAsync(tipType: TipType)
    suspend fun existsByNameAsync(name: String, excludeId: Int = 0): Boolean
    suspend fun existsByIdAsync(id: Int): Boolean
    suspend fun createBulkAsync(tipTypes: List<TipType>): List<TipType>
    suspend fun updateBulkAsync(tipTypes: List<TipType>): Int
    suspend fun deleteBulkAsync(tipTypeIds: List<Int>): Int
    suspend fun getTipTypesByLocalizationAsync(localization: String): List<TipType>
    suspend fun updateLocalizationAsync(id: Int, localization: String)
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(name: String)
}

data class TipTypeWithCount(
    val tipType: TipType,
    val tipCount: Long
)