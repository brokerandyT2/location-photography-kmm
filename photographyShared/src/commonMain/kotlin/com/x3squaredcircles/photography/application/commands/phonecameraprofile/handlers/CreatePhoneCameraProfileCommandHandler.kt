// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/phonecameraprofile/handlers/CreatePhoneCameraProfileCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.commands.phonecameraprofile.CreatePhoneCameraProfileCommand
import com.x3squaredcircles.photography.application.commands.phonecameraprofile.CreatePhoneCameraProfileCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto
import com.x3squaredcircles.photography.domain.services.IExifService
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock

class CreatePhoneCameraProfileCommandHandler(
    private val exifService: IExifService,
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : ICommandHandler<CreatePhoneCameraProfileCommand, CreatePhoneCameraProfileCommandResult> {

    override suspend fun handle(command: CreatePhoneCameraProfileCommand): Result<CreatePhoneCameraProfileCommandResult> {
        logger.d { "Handling CreatePhoneCameraProfileCommand for image: ${command.imagePath}" }

        return try {
            if (command.imagePath.isBlank()) {
                logger.w { "Image path is blank" }
                return Result.success(createFailureResult("Image path is required"))
            }

            // Step 1: Extract EXIF data
            when (val exifResult = exifService.extractExifDataAsync(command.imagePath)) {
                is Result.Success -> {
                    val exifData = exifResult.data

                    // Step 2: Validate required EXIF data
                    if (!exifData.hasValidFocalLength) {
                        logger.w { "Invalid focal length in EXIF data" }
                        return Result.success(createFailureResult("Invalid focal length in image"))
                    }

                    if (exifData.fullCameraModel.isBlank()) {
                        logger.w { "Missing camera model in EXIF data" }
                        return Result.success(createFailureResult("Missing camera model in image"))
                    }

                    // Step 3: Deactivate existing profiles
                    deactivateExistingProfiles()

                    // Step 4: Create phone camera profile
                    val profile = PhoneCameraProfileDto(
                        id = 0, // Will be set by repository
                        phoneModel = exifData.fullCameraModel,
                        mainLensFocalLength = exifData.focalLength ?: 0.0,
                        mainLensFOV = calculateFOV(exifData.focalLength ?: 0.0),
                        ultraWideFocalLength = null,
                        telephotoFocalLength = null,
                        dateCalibrated = Clock.System.now().toEpochMilliseconds(),
                        isActive = true
                    )

                    when (val saveResult = phoneCameraProfileRepository.createAsync(profile)) {
                        is Result.Success -> {
                            logger.i { "Successfully created phone camera profile for: ${exifData.fullCameraModel}" }
                            Result.success(
                                CreatePhoneCameraProfileCommandResult(
                                    profile = saveResult.data,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to save phone camera profile: ${saveResult.error}" }
                            Result.success(createFailureResult("Failed to save camera profile"))
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to extract EXIF data: ${exifResult.error}" }
                    Result.success(createFailureResult("Failed to extract camera information from image"))
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error creating phone camera profile from image: ${command.imagePath}" }
            Result.success(createFailureResult("Error processing image"))
        }
    }

    private suspend fun deactivateExistingProfiles() {
        try {
            when (val existingProfilesResult = phoneCameraProfileRepository.getAllAsync()) {
                is Result.Success -> {
                    existingProfilesResult.data.forEach { profile ->
                        if (profile.isActive) {
                            val deactivatedProfile = profile.copy(isActive = false)
                            phoneCameraProfileRepository.updateAsync(deactivatedProfile)
                        }
                    }
                }
                is Result.Failure -> {
                    logger.w { "Failed to get existing profiles for deactivation: ${existingProfilesResult.error}" }
                }
            }
        } catch (ex: Exception) {
            logger.w(ex) { "Failed to deactivate existing profiles" }
        }
    }

    private fun calculateFOV(focalLength: Double): Double {
        // Simplified FOV calculation for phone cameras
        // This would typically use sensor dimensions, but for phones we can estimate
        val estimatedSensorWidth = 5.76 // Typical phone sensor width in mm
        return 2 * kotlin.math.atan(estimatedSensorWidth / (2 * focalLength)) * (180.0 / kotlin.math.PI)
    }

    private fun createFailureResult(errorMessage: String): CreatePhoneCameraProfileCommandResult {
        return CreatePhoneCameraProfileCommandResult(
            profile = PhoneCameraProfileDto(
                id = 0,
                phoneModel = "",
                mainLensFocalLength = 0.0,
                mainLensFOV = 0.0,
                ultraWideFocalLength = null,
                telephotoFocalLength = null,
                dateCalibrated = 0L,
                isActive = false
            ),
            isSuccess = false,
            errorMessage = errorMessage
        )
    }
}