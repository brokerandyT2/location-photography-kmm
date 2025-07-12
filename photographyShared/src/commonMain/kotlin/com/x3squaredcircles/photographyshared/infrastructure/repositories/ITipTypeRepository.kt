// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ITipTypeRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.TipType

/**
 * Repository interface for photography TipType entity operations.
 */
interface ITipTypeRepository {
    
    suspend fun getAllAsync(): Result<List<TipType>>
    suspend fun getByIdAsync(id: Int): Result<TipType>
    suspend fun getByNameAsync(name: String): Result<TipType>
    suspend fun getWithTipCountAsync(): Result<List<Pair<TipType, Int>>>
    suspend fun createAsync(tipType: TipType): Result<TipType>
    suspend fun updateAsync(tipType: TipType): Result<TipType>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun existsByNameAsync(name: String, excludeId: Int = -1): Result<Boolean>
    suspend fun getCountAsync(): Result<Int>
}