// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IPhoneCameraProfileRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto
import com.x3squaredcircles.core.domain.common.Result

interface IPhoneCameraProfileRepository {
    suspend fun getByIdAsync(id: Int): Result<PhoneCameraProfileDto?>
    suspend fun getAllAsync(): Result<List<PhoneCameraProfileDto>>
    suspend fun getActiveAsync(): Result<PhoneCameraProfileDto?>
    suspend fun getByPhoneModelAsync(phoneModel: String): Result<List<PhoneCameraProfileDto>>
    suspend fun createAsync(profile: PhoneCameraProfileDto): Result<PhoneCameraProfileDto>
    suspend fun updateAsync(profile: PhoneCameraProfileDto): Result<Unit>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun setActiveAsync(id: Int): Result<Unit>
    suspend fun deactivateAllAsync(): Result<Unit>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun getActiveCountAsync(): Result<Long>
    suspend fun createBulkAsync(profiles: List<PhoneCameraProfileDto>): Result<List<PhoneCameraProfileDto>>
    suspend fun updateBulkAsync(profiles: List<PhoneCameraProfileDto>): Result<Int>
    suspend fun deleteBulkAsync(ids: List<Int>): Result<Int>
    fun clearCache()
    fun clearCache(id: Int)
}