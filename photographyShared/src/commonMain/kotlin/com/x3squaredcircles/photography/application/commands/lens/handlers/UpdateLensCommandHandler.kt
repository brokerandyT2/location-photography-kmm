// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/handlers/UpdateLensCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.lens.handlers

import com.x3squaredcircles.photography.application.commands.lens.UpdateLensCommand
import com.x3squaredcircles.photography.application.commands.lens.UpdateLensCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityDto
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock

class UpdateLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : ICommandHandler<UpdateLensCommand, UpdateLensCommandResult> {

    override suspend fun handle(command: UpdateLensCommand): Result<UpdateLensCommandResult> {
        logger.d { "Handling UpdateLensCommand for id: ${command.id}" }

        return try {
            if (command.id <= 0) {
                logger.w { "Invalid lens ID: ${command.id}" }
                return Result.success(
                    UpdateLensCommandResult(
                        lens = createEmptyLensDto(),
                        compatibleCameraIds = emptyList(),
                        isSuccess = false,
                        errorMessage = "Invalid lens ID"
                    )
                )
            }

            if (command.compatibleCameraIds.isEmpty()) {
                logger.w { "No compatible cameras provided for lens: ${command.id}" }
                return Result.success(
                    UpdateLensCommandResult(
                        lens = createEmptyLensDto(),
                        compatibleCameraIds = emptyList(),
                        isSuccess = false,
                        errorMessage = "At least one compatible camera must be selected"
                    )
                )
            }

            // Get existing lens
            when (val existingResult = lensRepository.getByIdAsync(command.id)) {
                is Result.Success -> {
                    val existingLens = existingResult.data
                    if (existingLens == null) {
                        logger.w { "Lens not found with id: ${command.id}" }
                        return Result.success(
                            UpdateLensCommandResult(
                                lens = createEmptyLensDto(),
                                compatibleCameraIds = emptyList(),
                                isSuccess = false,
                                errorMessage = "Lens not found"
                            )
                        )
                    }

                    // Create updated lens DTO
                    val isPrime = command.maxMM == null || command.maxMM == command.minMM
                    val updatedLens = existingLens.copy(
                        minMM = command.minMM,
                        maxMM = command.maxMM ?: command.minMM,
                        minFStop = command.minFStop ?: 0.0,
                        maxFStop = command.maxFStop ?: 0.0,
                        isPrime = isPrime,
                        nameForLens = command.lensName
                    )

                    when (val updateResult = lensRepository.updateAsync(updatedLens)) {
                        is Result.Success -> {
                            // Update lens-camera compatibility entries
                            // First delete existing compatibilities
                            when (compatibilityRepository.deleteByLensIdAsync(command.id)) {
                                is Result.Success -> {
                                    // Create new compatibility entries
                                    val compatibilities = command.compatibleCameraIds.map { cameraId ->
                                        LensCameraCompatibilityDto(
                                            id = 0,
                                            lensId = command.id,
                                            cameraBodyId = cameraId,
                                            dateAdded = Clock.System.now().toEpochMilliseconds()
                                        )
                                    }

                                    when (val compatibilityResult = compatibilityRepository.createBatchAsync(compatibilities)) {
                                        is Result.Success -> {
                                            logger.i { "Successfully updated lens: ${command.lensName} with ${command.compatibleCameraIds.size} compatibility entries" }
                                            Result.success(
                                                UpdateLensCommandResult(
                                                    lens = updatedLens,
                                                    compatibleCameraIds = command.compatibleCameraIds,
                                                    isSuccess = true
                                                )
                                            )
                                        }
                                        is Result.Failure -> {
                                            logger.e { "Failed to update lens-camera compatibilities: ${compatibilityResult.error}" }
                                            Result.success(
                                                UpdateLensCommandResult(
                                                    lens = updatedLens,
                                                    compatibleCameraIds = emptyList(),
                                                    isSuccess = false,
                                                    errorMessage = "Lens updated but failed to update camera compatibilities"
                                                )
                                            )
                                        }
                                    }
                                }
                                is Result.Failure -> {
                                    logger.e { "Failed to delete existing lens compatibilities" }
                                    Result.success(
                                        UpdateLensCommandResult(
                                            lens = updatedLens,
                                            compatibleCameraIds = emptyList(),
                                            isSuccess = false,
                                            errorMessage = "Lens updated but failed to update camera compatibilities"
                                        )
                                    )
                                }
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to update lens: ${updateResult.error}" }
                            Result.success(
                                UpdateLensCommandResult(
                                    lens = createEmptyLensDto(),
                                    compatibleCameraIds = emptyList(),
                                    isSuccess = false,
                                    errorMessage = updateResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to get lens for update: ${existingResult.error}" }
                    Result.success(
                        UpdateLensCommandResult(
                            lens = createEmptyLensDto(),
                            compatibleCameraIds = emptyList(),
                            isSuccess = false,
                            errorMessage = existingResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error updating lens: ${command.lensName}" }
            Result.success(
                UpdateLensCommandResult(
                    lens = createEmptyLensDto(),
                    compatibleCameraIds = emptyList(),
                    isSuccess = false,
                    errorMessage = "Error updating lens"
                )
            )
        }
    }

    private fun createEmptyLensDto(): LensDto {
        return LensDto(
            id = 0,
            minMM = 0.0,
            maxMM = 0.0,
            minFStop = 0.0,
            maxFStop = 0.0,
            isPrime = false,
            isUserCreated = false,
            nameForLens = "",
            dateAdded = 0L
        )
    }
}