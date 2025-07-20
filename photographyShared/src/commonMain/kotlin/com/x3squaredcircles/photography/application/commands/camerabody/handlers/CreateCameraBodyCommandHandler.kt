// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/handlers/CreateCameraBodyCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.camerabody.handlers

import com.x3squaredcircles.photography.application.commands.camerabody.CreateCameraBodyCommand
import com.x3squaredcircles.photography.application.commands.camerabody.CreateCameraBodyCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock

class CreateCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : ICommandHandler<CreateCameraBodyCommand, CreateCameraBodyCommandResult> {

    override suspend fun handle(command: CreateCameraBodyCommand): Result<CreateCameraBodyCommandResult> {
        logger.d { "Handling CreateCameraBodyCommand for camera: ${command.name}" }

        return try {
            // Check for fuzzy duplicate
            when (val existingResult = cameraBodyRepository.searchByNameAsync(command.name)) {
                is Result.Success -> {
                    if (existingResult.data.isNotEmpty()) {
                        logger.w { "Duplicate camera name found: ${command.name}" }
                        return Result.success(
                            CreateCameraBodyCommandResult(
                                cameraBody = CameraBodyDto(
                                    id = 0,
                                    name = "",
                                    sensorType = "",
                                    sensorWidth = 0.0,
                                    sensorHeight = 0.0,
                                    mountType = "",
                                    isUserCreated = false,
                                    dateAdded = 0L,
                                    displayName = ""
                                ),
                                isSuccess = false,
                                errorMessage = "Camera with similar name already exists"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.w { "Failed to check for duplicates: ${existingResult.error}" }
                }
            }

            // Create the camera body entity
            val cameraBody = CameraBody.create(
                name = command.name,
                sensorType = command.sensorType,
                sensorWidth = command.sensorWidth,
                sensorHeight = command.sensorHeight,
                mountType = command.mountType,
                isUserCreated = command.isUserCreated
            )

            // Convert to DTO for repository
            val cameraBodyDto = CameraBodyDto(
                id = 0, // Will be set by repository
                name = cameraBody.name,
                sensorType = cameraBody.sensorType,
                sensorWidth = cameraBody.sensorWidth,
                sensorHeight = cameraBody.sensorHeight,
                mountType = cameraBody.mountType.toString(),
                isUserCreated = cameraBody.isUserCreated,
                dateAdded = cameraBody.dateAdded.toEpochMilliseconds(),
                displayName = cameraBody.getDisplayName()
            )

            when (val createResult = cameraBodyRepository.createAsync(cameraBodyDto)) {
                is Result.Success -> {
                    logger.i { "Successfully created camera body: ${command.name}" }
                    Result.success(
                        CreateCameraBodyCommandResult(
                            cameraBody = createResult.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to create camera body: ${createResult.error}" }
                    Result.success(
                        CreateCameraBodyCommandResult(
                            cameraBody = CameraBodyDto(
                                id = 0,
                                name = "",
                                sensorType = "",
                                sensorWidth = 0.0,
                                sensorHeight = 0.0,
                                mountType = "",
                                isUserCreated = false,
                                dateAdded = 0L,
                                displayName = ""
                            ),
                            isSuccess = false,
                            errorMessage = createResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error creating camera body: ${command.name}" }
            Result.success(
                CreateCameraBodyCommandResult(
                    cameraBody = CameraBodyDto(
                        id = 0,
                        name = "",
                        sensorType = "",
                        sensorWidth = 0.0,
                        sensorHeight = 0.0,
                        mountType = "",
                        isUserCreated = false,
                        dateAdded = 0L,
                        displayName = ""
                    ),
                    isSuccess = false,
                    errorMessage = "Error creating camera body"
                )
            )
        }
    }
}