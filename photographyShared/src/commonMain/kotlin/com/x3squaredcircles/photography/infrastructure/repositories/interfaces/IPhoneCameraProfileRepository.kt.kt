// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/IPhoneCameraProfileRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto

interface IPhoneCameraProfileRepository {
    suspend fun getByIdAsync(id: Int): PhoneCameraProfileDto?
    suspend fun getAllAsync(): List<PhoneCameraProfileDto>
    suspend fun getActiveAsync(): PhoneCameraProfileDto?
    suspend fun getByPhoneModelAsync(phoneModel: String): List<PhoneCameraProfileDto>
    suspend fun addAsync(profile: PhoneCameraProfileDto): PhoneCameraProfileDto
    suspend fun updateAsync(profile: PhoneCameraProfileDto)
    suspend fun deleteAsync(id: Int)
    suspend fun setActiveAsync(id: Int)
    suspend fun deactivateAllAsync()
    suspend fun getTotalCountAsync(): Long
    suspend fun getActiveCountAsync(): Long
    suspend fun createBulkAsync(profiles: List<PhoneCameraProfileDto>): List<PhoneCameraProfileDto>
    suspend fun updateBulkAsync(profiles: List<PhoneCameraProfileDto>): Int
    suspend fun deleteBulkAsync(ids: List<Int>): Int
    fun clearCache()
    fun clearCache(id: Int)
}