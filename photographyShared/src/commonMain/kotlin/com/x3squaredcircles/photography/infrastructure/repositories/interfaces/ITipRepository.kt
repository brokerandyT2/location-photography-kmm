// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ITipRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Tip

interface ITipRepository {
    suspend fun getByIdAsync(id: Int): Tip?
    suspend fun getAllAsync(): List<Tip>
    suspend fun getByTypeIdAsync(tipTypeId: Int): List<Tip>
    suspend fun getWithCameraSettingsAsync(): List<Tip>
    suspend fun searchByTextAsync(searchTerm: String): List<Tip>
    suspend fun getRandomAsync(count: Int = 1): List<Tip>
    suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        tipTypeId: Int? = null
    ): List<Tip>
    suspend fun getTotalCountAsync(): Long
    suspend fun getCountByTypeAsync(tipTypeId: Int): Long
    suspend fun addAsync(tip: Tip): Tip
    suspend fun updateAsync(tip: Tip)
    suspend fun deleteAsync(tip: Tip)
    suspend fun deleteByTypeIdAsync(tipTypeId: Int): Int
    suspend fun updateCameraSettingsAsync(
        id: Int,
        fstop: String,
        shutterSpeed: String,
        iso: String
    )
    suspend fun existsByIdAsync(id: Int): Boolean
    suspend fun createBulkAsync(tips: List<Tip>): List<Tip>
    suspend fun updateBulkAsync(tips: List<Tip>): Int
    suspend fun deleteBulkAsync(tipIds: List<Int>): Int
    suspend fun getTipsByLocalizationAsync(localization: String): List<Tip>
    suspend fun updateLocalizationAsync(id: Int, localization: String)
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(tipTypeId: Int, cacheType: TipCacheType)
}

enum class TipCacheType {
    BY_ID,
    BY_TYPE,
    WITH_CAMERA_SETTINGS,
    SEARCH_RESULTS,
    ALL
}