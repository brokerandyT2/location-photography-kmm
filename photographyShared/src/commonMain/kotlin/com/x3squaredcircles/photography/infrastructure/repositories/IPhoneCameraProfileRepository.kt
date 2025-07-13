// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/IPhoneCameraProfileRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile

/**
 * Repository interface for PhoneCameraProfile entity operations.
 */
interface IPhoneCameraProfileRepository {

    suspend fun createAsync(profile: PhoneCameraProfile): Result<PhoneCameraProfile>
    suspend fun getActiveProfileAsync(): Result<PhoneCameraProfile>
    suspend fun getByIdAsync(id: Int): Result<PhoneCameraProfile>
    suspend fun getAllAsync(): Result<List<PhoneCameraProfile>>
    suspend fun updateAsync(profile: PhoneCameraProfile): Result<PhoneCameraProfile>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun setActiveProfileAsync(profileId: Int): Result<Boolean>
    suspend fun getByPhoneModelAsync(phoneModel: String): Result<List<PhoneCameraProfile>>
}