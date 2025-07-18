// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ITipRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.core.domain.entities.Tip
import com.x3squaredcircles.core.domain.common.Result

interface ITipRepository {
    suspend fun getByIdAsync(id: Int): Result<Tip?>
    suspend fun getAllAsync(): Result<List<Tip>>
    suspend fun getByTypeIdAsync(tipTypeId: Int): Result<List<Tip>>
    suspend fun getWithCameraSettingsAsync(): Result<List<Tip>>
    suspend fun searchByTextAsync(searchTerm: String): Result<List<Tip>>
    suspend fun getRandomAsync(count: Int = 1): Result<List<Tip>>
    suspend fun getPagedAsync(
        pageNumber: Int,
        pageSize: Int,
        tipTypeId: Int? = null
    ): Result<List<Tip>>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun getCountByTypeAsync(tipTypeId: Int): Result<Long>
    suspend fun createAsync(tip: Tip): Result<Tip>
    suspend fun updateAsync(tip: Tip): Result<Unit>
    suspend fun deleteAsync(tip: Tip): Result<Unit>
    suspend fun deleteByTypeIdAsync(tipTypeId: Int): Result<Int>
    suspend fun updateCameraSettingsAsync(
        id: Int,
        fstop: String,
        shutterSpeed: String,
        iso: String
    ): Result<Unit>
    suspend fun existsByIdAsync(id: Int): Result<Boolean>
    suspend fun createBulkAsync(tips: List<Tip>): Result<List<Tip>>
    suspend fun updateBulkAsync(tips: List<Tip>): Result<Int>
    suspend fun deleteBulkAsync(tipIds: List<Int>): Result<Int>
    suspend fun getTipsByLocalizationAsync(localization: String): Result<List<Tip>>
    suspend fun updateLocalizationAsync(id: Int, localization: String): Result<Unit>
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