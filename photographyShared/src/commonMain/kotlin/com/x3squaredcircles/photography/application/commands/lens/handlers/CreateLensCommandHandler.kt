// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/handlers/CreateLensCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.lens.handlers

import com.x3squaredcircles.photography.application.commands.lens.CreateLensCommand
import com.x3squaredcircles.photography.application.commands.lens.CreateLensCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityDto
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock

class CreateLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : ICommandHandler<CreateLensCommand, CreateLensCommandResult> {

    override suspend fun handle(command: CreateLensCommand): Result<CreateLensCommandResult> {
        logger.d { "Handling CreateLensCommand for lens: ${command.lensName}" }

        return try {
            // Validate that at least one camera is selected
            if (command.compatibleCameraIds.isEmpty()) {
                logger.w { "No compatible cameras provided for lens: ${command.lensName}" }
                return Result.success(
                    CreateLensCommandResult(
                        lens = LensDto(
                            id = 0,
                            minMM = 0.0,
                            maxMM = 0.0,
                            minFStop = 0.0,
                            maxFStop = 0.0,
                            isPrime = false,
                            isUserCreated = false,
                            nameForLens = "",
                            dateAdded = 0L
                        ),
                        compatibleCameraIds = emptyList(),
                        isSuccess = false,
                        errorMessage = "At least one compatible camera must be selected"
                    )
                )
            }

            // Check for similar lens (fuzzy search by focal length)
            val searchFocalLength = command.maxMM ?: command.minMM
            when (val existingResult = lensRepository.getByFocalLengthRangeAsync(searchFocalLength - 1.0, searchFocalLength + 1.0)) {
                is Result.Success -> {
                    if (existingResult.data.isNotEmpty()) {
                        logger.i { "Found similar lens for focal length: $searchFocalLength" }
                    }
                }
                is Result.Failure -> {
                    logger.w { "Failed to check for similar lenses: ${existingResult.error}" }
                }
            }

            // Create the lens DTO for repository
            val isPrime = command.maxMM == null || command.maxMM == command.minMM

            val lensDto = LensDto(
                id = 0, // Will be set by repository
                minMM = command.minMM,
                maxMM = command.maxMM ?: command.minMM,
                minFStop = command.minFStop ?: 0.0,
                maxFStop = command.maxFStop ?: 0.0,
                isPrime = isPrime,
                isUserCreated = command.isUserCreated,
                nameForLens = command.lensName,
                dateAdded = Clock.System.now().toEpochMilliseconds()
            )

            when (val createResult = lensRepository.createAsync(lensDto)) {
                is Result.Success -> {
                    val createdLens = createResult.data

                    // Create lens-camera compatibility entries
                    val compatibilities = command.compatibleCameraIds.map { cameraId ->
                        LensCameraCompatibilityDto(
                            id = 0,
                            lensId = createdLens.id,
                            cameraBodyId = cameraId,
                            dateAdded = Clock.System.now().toEpochMilliseconds()
                        )
                    }

                    when (val compatibilityResult = compatibilityRepository.createBatchAsync(compatibilities)) {
                        is Result.Success -> {
                            val displayName = if (isPrime) {
                                "${command.minMM.toInt()}mm f/${command.minFStop ?: "?"}"
                            } else {
                                "${command.minMM.toInt()}-${command.maxMM?.toInt() ?: "?"}mm f/${command.minFStop ?: "?"}-${command.maxFStop ?: "?"}"
                            }

                            logger.i { "Successfully created lens: ${command.lensName} with ${command.compatibleCameraIds.size} compatibility entries" }
                            Result.success(
                                CreateLensCommandResult(
                                    lens = createdLens,
                                    compatibleCameraIds = command.compatibleCameraIds,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to create lens-camera compatibilities: ${compatibilityResult.error}" }
                            Result.success(
                                CreateLensCommandResult(
                                    lens = createdLens,
                                    compatibleCameraIds = emptyList(),
                                    isSuccess = false,
                                    errorMessage = "Lens created but failed to create camera compatibilities"
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to create lens: ${createResult.error}" }
                    Result.success(
                        CreateLensCommandResult(
                            lens = LensDto(
                                id = 0,
                                minMM = 0.0,
                                maxMM = 0.0,
                                minFStop = 0.0,
                                maxFStop = 0.0,
                                isPrime = false,
                                isUserCreated = false,
                                nameForLens = "",
                                dateAdded = 0L
                            ),
                            compatibleCameraIds = emptyList(),
                            isSuccess = false,
                            errorMessage = createResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error creating lens: ${command.lensName}" }
            Result.success(
                CreateLensCommandResult(
                    lens = LensDto(
                        id = 0,
                        minMM = 0.0,
                        maxMM = 0.0,
                        minFStop = 0.0,
                        maxFStop = 0.0,
                        isPrime = false,
                        isUserCreated = false,
                        nameForLens = "",
                        dateAdded = 0L
                    ),
                    compatibleCameraIds = emptyList(),
                    isSuccess = false,
                    errorMessage = "Error creating lens"
                )
            )
        }
    }
}