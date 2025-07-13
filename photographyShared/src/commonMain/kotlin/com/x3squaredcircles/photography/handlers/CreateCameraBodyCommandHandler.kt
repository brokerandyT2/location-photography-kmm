// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/CreateCameraBodyCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreateCameraBodyCommand

import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ICameraBodyRepository
import kotlinx.datetime.Clock
class CreateCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository
) : ICommandHandler<CreateCameraBodyCommand, Result<CameraBodyDto>> {
    override suspend fun handle(request: CreateCameraBodyCommand): Result<CameraBodyDto> {
        return try {
            val existsByNameResult = cameraBodyRepository.existsByNameAsync(request.name)
            if (existsByNameResult.isSuccess && existsByNameResult.getOrNull() == true) {
                return Result.failure("Camera with this name already exists")
            }

            val currentTime = Clock.System.now().epochSeconds
            val cameraBody = CameraBody(
                id = 0,
                name = request.name,
                sensorType = request.sensorType,
                sensorWidth = request.sensorWidth,
                sensorHeight = request.sensorHeight,
                mountType = request.mountType.name,
                isUserCreated = request.isUserCreated,
                dateAdded = currentTime
            )

            val createResult = cameraBodyRepository.createAsync(cameraBody)
            if (!createResult.isSuccess) {
                return Result.failure("Error creating camera body")
            }

            val createdCamera = createResult.getOrNull()!!
            val dto = CameraBodyDto(
                id = createdCamera.id,
                name = createdCamera.name,
                sensorType = createdCamera.sensorType,
                sensorWidth = createdCamera.sensorWidth,
                sensorHeight = createdCamera.sensorHeight,
                mountType = createdCamera.mountType,
                isUserCreated = createdCamera.isUserCreated,
                dateAdded = createdCamera.dateAdded,
                displayName = createdCamera.name
            )

            Result.success(dto)
        } catch (e: Exception) {
            Result.failure("Error creating camera body: ${e.message}")
        }
    }
}