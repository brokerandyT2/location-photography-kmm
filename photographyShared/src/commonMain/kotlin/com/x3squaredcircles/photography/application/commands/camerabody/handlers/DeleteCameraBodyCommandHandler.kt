// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/handlers/DeleteCameraBodyCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.camerabody.handlers

import com.x3squaredcircles.photography.application.commands.camerabody.DeleteCameraBodyCommand
import com.x3squaredcircles.photography.application.commands.camerabody.DeleteCameraBodyCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class DeleteCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : ICommandHandler<DeleteCameraBodyCommand, DeleteCameraBodyCommandResult> {

    override suspend fun handle(command: DeleteCameraBodyCommand): Result<DeleteCameraBodyCommandResult> {
        logger.d { "Handling DeleteCameraBodyCommand for id: ${command.id}" }

        return try {
            if (command.id <= 0) {
                logger.w { "Invalid camera body ID: ${command.id}" }
                return Result.success(
                    DeleteCameraBodyCommandResult(
                        isSuccess = false,
                        errorMessage = "Invalid camera body ID"
                    )
                )
            }

            when (val deleteResult = cameraBodyRepository.deleteAsync(command.id)) {
                is Result.Success -> {
                    logger.i { "Successfully deleted camera body with id: ${command.id}" }
                    Result.success(
                        DeleteCameraBodyCommandResult(
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to delete camera body with id: ${command.id} - ${deleteResult.error}" }
                    Result.success(
                        DeleteCameraBodyCommandResult(
                            isSuccess = false,
                            errorMessage = deleteResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error deleting camera body with id: ${command.id}" }
            Result.success(
                DeleteCameraBodyCommandResult(
                    isSuccess = false,
                    errorMessage = "Error deleting camera body"
                )
            )
        }
    }
}