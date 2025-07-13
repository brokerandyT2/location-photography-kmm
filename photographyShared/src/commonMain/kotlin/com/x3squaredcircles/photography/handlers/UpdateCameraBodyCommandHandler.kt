// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/UpdateCameraBodyCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.UpdateCameraBodyCommand
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ICameraBodyRepository

class UpdateCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository
) : ICommandHandler<UpdateCameraBodyCommand, Result<CameraBodyDto>> {

    override suspend fun handle(request: UpdateCameraBodyCommand): Result<CameraBodyDto> {
        return try {
            val existingResult = cameraBodyRepository.getByIdAsync(request.id)
            if (!existingResult.isSuccess) {
                return Result.failure("Camera body not found")
            }

            val existingCamera = existingResult.getOrNull()
            if (existingCamera == null) {
                return Result.failure("Camera body not found")
            }

            val nameExistsResult = cameraBodyRepository.existsByNameAsync(request.name, request.id)
            if (nameExistsResult.isSuccess && nameExistsResult.getOrNull() == true) {
                return Result.failure("Camera with this name already exists")
            }

            val updatedCamera = existingCamera.copy(
                name = request.name,
                sensorType = request.sensorType,
                sensorWidth = request.sensorWidth,
                sensorHeight = request.sensorHeight,
                mountType = request.mountType.name
            )

            val updateResult = cameraBodyRepository.updateAsync(updatedCamera)
            if (!updateResult.isSuccess) {
                return Result.failure("Error updating camera body")
            }

            val savedCamera = updateResult.getOrNull()!!
            val dto = CameraBodyDto(
                id = savedCamera.id,
                name = savedCamera.name,
                sensorType = savedCamera.sensorType,
                sensorWidth = savedCamera.sensorWidth,
                sensorHeight = savedCamera.sensorHeight,
                mountType = savedCamera.mountType,
                isUserCreated = savedCamera.isUserCreated,
                dateAdded = savedCamera.dateAdded,
                displayName = savedCamera.name
            )

            Result.success(dto)
        } catch (e: Exception) {
            Result.failure("Error updating camera body: ${e.message}")
        }
    }
}