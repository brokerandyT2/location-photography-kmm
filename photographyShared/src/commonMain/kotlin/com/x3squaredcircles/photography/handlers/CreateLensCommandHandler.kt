// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/CreateLensCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler

import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import com.x3squaredcircles.photography.dtos.CreateLensResultDto

import com.x3squaredcircles.photography.dtos.LensDto
import com.x3squaredcircles.photographyshared.commands.CreateLensCommand
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensRepository
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensCameraCompatibilityRepository
import kotlinx.datetime.Clock
class CreateLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository
) : ICommandHandler<CreateLensCommand, Result<CreateLensResultDto>> {
    override suspend fun handle(request: CreateLensCommand): Result<CreateLensResultDto> {
        return try {
            val currentTime = Clock.System.now().epochSeconds
            val lens = Lens(
                id = 0,
                minMM = request.minMM,
                maxMM = request.maxMM ?: request.minMM,
                minFStop = request.minFStop ?: 1.4,
                maxFStop = request.maxFStop ?: request.minFStop ?: 22.0,
                isPrime = request.maxMM == null || request.maxMM == request.minMM,
                isUserCreated = request.isUserCreated,
                nameForLens = request.lensName,
                dateAdded = currentTime
            )

            val createResult = lensRepository.createAsync(lens)
            if (!createResult.isSuccess) {
                return Result.failure("Error creating lens")
            }

            val createdLens = createResult.getOrNull()!!

            if (request.compatibleCameraIds.isNotEmpty()) {
                val compatibilities = request.compatibleCameraIds.map { cameraId ->
                    LensCameraCompatibility(
                        id = 0,
                        lensId = createdLens.id,
                        cameraBodyId = cameraId,
                        dateAdded = currentTime
                    )
                }

                val compatibilityResult = compatibilityRepository.createBatchAsync(compatibilities)
                if (!compatibilityResult.isSuccess) {
                    return Result.failure("Lens created but failed to create compatibility relationships")
                }
            }

            val lensDto = LensDto(
                id = createdLens.id,
                minMM = createdLens.minMM,
                maxMM = createdLens.maxMM,
                minFStop = createdLens.minFStop,
                maxFStop = createdLens.maxFStop,
                isPrime = createdLens.isPrime,
                isUserCreated = createdLens.isUserCreated,
                dateAdded = createdLens.dateAdded,
                displayName = createdLens.displayName
            )

            val resultDto = CreateLensResultDto(
                lens = lensDto,
                compatibleCameraIds = request.compatibleCameraIds,
                isSuccessful = true,
                errorMessage = ""
            )

            Result.success(resultDto)
        } catch (e: Exception) {
            val failureDto = CreateLensResultDto(
                lens = null,
                compatibleCameraIds = emptyList(),
                isSuccessful = false,
                errorMessage = "Error creating lens: ${e.message}"
            )
            Result.success(failureDto)
        }
    }
}