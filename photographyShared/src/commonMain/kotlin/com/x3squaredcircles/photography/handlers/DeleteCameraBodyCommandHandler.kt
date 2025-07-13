// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/DeleteCameraBodyCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.DeleteCameraBodyCommand
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ICameraBodyRepository
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensCameraCompatibilityRepository

class DeleteCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository
) : ICommandHandler<DeleteCameraBodyCommand, Result<Boolean>> {

    override suspend fun handle(request: DeleteCameraBodyCommand): Result<Boolean> {
        return try {
            val existsResult = cameraBodyRepository.getByIdAsync(request.id)
            if (!existsResult.isSuccess) {
                return Result.failure("Camera body not found")
            }

            val cameraBody = existsResult.getOrNull()
            if (cameraBody == null) {
                return Result.failure("Camera body not found")
            }

            compatibilityRepository.deleteByCameraIdAsync(request.id)

            val deleteResult = cameraBodyRepository.deleteAsync(request.id)
            if (!deleteResult.isSuccess) {
                return Result.failure("Error deleting camera body")
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure("Error deleting camera body: ${e.message}")
        }
    }
}