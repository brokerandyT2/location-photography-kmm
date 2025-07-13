// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetPhoneCameraProfileQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetPhoneCameraProfileQuery
import com.x3squaredcircles.photography.dtos.PhoneCameraProfileDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.IPhoneCameraProfileRepository

class GetPhoneCameraProfileQueryHandler(
    private val repository: IPhoneCameraProfileRepository
) : IQueryHandler<GetPhoneCameraProfileQuery, Result<PhoneCameraProfileDto>> {

    override suspend fun handle(request: GetPhoneCameraProfileQuery): Result<PhoneCameraProfileDto> {
        return try {
            val result = repository.getActiveProfileAsync()

            if (!result.isSuccess) {
                return Result.failure("Error retrieving phone camera profile")
            }

            val profile = result.getOrNull()
            if (profile == null) {
                return Result.failure("No active phone camera profile found")
            }

            val dto = PhoneCameraProfileDto(
                id = profile.id,
                phoneModel = profile.phoneModel,
                mainLensFocalLength = profile.mainLensFocalLength,
                mainLensFOV = profile.mainLensFOV,
                ultraWideFocalLength = profile.ultraWideFocalLength,
                telephotoFocalLength = profile.telephotoFocalLength,
                dateCalibrated = profile.dateCalibrated,
                isActive = profile.isActive,
                isCalibrationSuccessful = true,
                errorMessage = ""
            )

            Result.success(dto)
        } catch (e: Exception) {
            Result.failure("Error retrieving phone camera profile: ${e.message}")
        }
    }
}