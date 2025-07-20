// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/handlers/DeleteLensCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.lens.handlers

import com.x3squaredcircles.photography.application.commands.lens.DeleteLensCommand
import com.x3squaredcircles.photography.application.commands.lens.DeleteLensCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class DeleteLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : ICommandHandler<DeleteLensCommand, DeleteLensCommandResult> {

    override suspend fun handle(command: DeleteLensCommand): Result<DeleteLensCommandResult> {
        logger.d { "Handling DeleteLensCommand for id: ${command.id}" }

        return try {
            if (command.id <= 0) {
                logger.w { "Invalid lens ID: ${command.id}" }
                return Result.success(
                    DeleteLensCommandResult(
                        isSuccess = false,
                        errorMessage = "Invalid lens ID"
                    )
                )
            }

            // First, delete lens-camera compatibility entries
            when (val compatibilityResult = compatibilityRepository.deleteByLensIdAsync(command.id)) {
                is Result.Success -> {
                    logger.d { "Successfully deleted compatibility entries for lens: ${command.id}" }
                }
                is Result.Failure -> {
                    logger.w { "Failed to delete compatibility entries for lens: ${command.id} - ${compatibilityResult.error}" }
                    // Continue with lens deletion even if compatibility deletion fails
                }
            }

            // Delete the lens
            when (val deleteResult = lensRepository.deleteAsync(command.id)) {
                is Result.Success -> {
                    logger.i { "Successfully deleted lens with id: ${command.id}" }
                    Result.success(
                        DeleteLensCommandResult(
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to delete lens with id: ${command.id} - ${deleteResult.error}" }
                    Result.success(
                        DeleteLensCommandResult(
                            isSuccess = false,
                            errorMessage = deleteResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error deleting lens with id: ${command.id}" }
            Result.success(
                DeleteLensCommandResult(
                    isSuccess = false,
                    errorMessage = "Error deleting lens"
                )
            )
        }
    }
}