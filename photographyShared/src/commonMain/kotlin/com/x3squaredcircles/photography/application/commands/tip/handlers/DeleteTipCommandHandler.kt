// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/handlers/DeleteTipCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.tip.handlers

import com.x3squaredcircles.photography.application.commands.tip.DeleteTipCommand
import com.x3squaredcircles.photography.application.commands.tip.DeleteTipCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class DeleteTipCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<DeleteTipCommand, DeleteTipCommandResult> {

    override suspend fun handle(command: DeleteTipCommand): Result<DeleteTipCommandResult> {
        logger.d { "Handling DeleteTipCommand for id: ${command.id}" }

        return try {
            if (command.id <= 0) {
                logger.w { "Invalid tip ID: ${command.id}" }
                return Result.success(
                    DeleteTipCommandResult(
                        isSuccess = false,
                        errorMessage = "Invalid tip ID"
                    )
                )
            }

            when (val getTipResult = unitOfWork.tips.getByIdAsync(command.id)) {
                is Result.Success -> {
                    val tip = getTipResult.data
                    if (tip == null) {
                        logger.w { "Tip not found with id: ${command.id}" }
                        return Result.success(
                            DeleteTipCommandResult(
                                isSuccess = false,
                                errorMessage = "Tip not found"
                            )
                        )
                    }

                    when (val deleteResult = unitOfWork.tips.deleteAsync(tip)) {
                        is Result.Success -> {
                            try {
                                unitOfWork.saveChangesAsync()
                                logger.i { "Successfully deleted tip with id: ${command.id}" }

                                Result.success(
                                    DeleteTipCommandResult(
                                        isSuccess = true
                                    )
                                )
                            } catch (ex: Exception) {
                                logger.e(ex) { "Failed to save tip deletion changes" }
                                Result.success(
                                    DeleteTipCommandResult(
                                        isSuccess = false,
                                        errorMessage = "Failed to save deletion: ${ex.message}"
                                    )
                                )
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to delete tip: ${deleteResult.error}" }
                            Result.success(
                                DeleteTipCommandResult(
                                    isSuccess = false,
                                    errorMessage = deleteResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to get tip for deletion: ${getTipResult.error}" }
                    Result.success(
                        DeleteTipCommandResult(
                            isSuccess = false,
                            errorMessage = getTipResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error deleting tip with id: ${command.id}" }
            Result.success(
                DeleteTipCommandResult(
                    isSuccess = false,
                    errorMessage = "Error deleting tip: ${ex.message}"
                )
            )
        }
    }
}