// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/handlers/UpdateCameraBodyCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.camerabody.handlers

import com.x3squaredcircles.photography.application.commands.camerabody.UpdateCameraBodyCommand

import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class UpdateCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : ICommandHandler<UpdateCameraBodyCommand, UpdateCameraBodyCommandResult> {

    override suspend fun handle(command: UpdateCameraBodyCommand): Result<UpdateCameraBodyCommandResult> {
        logger.d { "Handling UpdateCameraBodyCommand for id: ${command.id}" }

        return try {
            if (command.id <= 0) {
                logger.w { "Invalid camera body ID: ${command.id}" }
                return Result.success(
                    UpdateCameraBodyCommandResult(
                        cameraBody = createEmptyDto(),
                        isSuccess = false,
                        errorMessage = "Invalid camera body ID"
                    )
                )
            }

            // Get existing camera body
            when (val existingResult = cameraBodyRepository.getByIdAsync(command.id)) {
                is Result.Success -> {
                    val existingCamera = existingResult.data
                    if (existingCamera == null) {
                        logger.w { "Camera body not found with id: ${command.id}" }
                        return Result.success(
                            UpdateCameraBodyCommandResult(
                                cameraBody = createEmptyDto(),
                                isSuccess = false,
                                errorMessage = "Camera body not found"
                            )
                        )
                    }

                    // Create updated camera body DTO
                    val updatedCamera = existingCamera.copy(
                        name = command.name,
                        sensorType = command.sensorType,
                        sensorWidth = command.sensorWidth,
                        sensorHeight = command.sensorHeight,
                        mountType = command.mountType.toString(),
                        displayName = if (existingCamera.isUserCreated) "${command.name}*" else command.name
                    )

                    when (val updateResult = cameraBodyRepository.updateAsync(updatedCamera)) {
                        is Result.Success -> {
                            logger.i { "Successfully updated camera body: ${command.name}" }
                            Result.success(
                                UpdateCameraBodyCommandResult(
                                    cameraBody = updatedCamera,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to update camera body: ${updateResult.error}" }
                            Result.success(
                                UpdateCameraBodyCommandResult(
                                    cameraBody = createEmptyDto(),
                                    isSuccess = false,
                                    errorMessage = updateResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to get camera body for update: ${existingResult.error}" }
                    Result.success(
                        UpdateCameraBodyCommandResult(
                            cameraBody = createEmptyDto(),
                            isSuccess = false,
                            errorMessage = existingResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error updating camera body: ${command.name}" }
            Result.success(
                UpdateCameraBodyCommandResult(
                    cameraBody = createEmptyDto(),
                    isSuccess = false,
                    errorMessage = "Error updating camera body"
                )
            )
        }
    }

    private fun createEmptyDto(): CameraBodyDto {
        return CameraBodyDto(
            id = 0,
            name = "",
            sensorType = "",
            sensorWidth = 0.0,
            sensorHeight = 0.0,
            mountType = "",
            isUserCreated = false,
            dateAdded = 0L,
            displayName = ""
        )
    }
}