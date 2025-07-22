// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/DeleteSettingCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.DeleteSettingCommand
import com.x3squaredcircles.photography.application.commands.setting.DeleteSettingCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class DeleteSettingCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<DeleteSettingCommand, DeleteSettingCommandResult> {

    override suspend fun handle(command: DeleteSettingCommand): Result<DeleteSettingCommandResult> {
        logger.d { "Handling DeleteSettingCommand for key: ${command.key}" }

        return try {
            if (command.key.isBlank()) {
                logger.w { "Setting key is blank" }
                return Result.success(
                    DeleteSettingCommandResult(
                        isSuccess = false,
                        errorMessage = "Setting key cannot be blank"
                    )
                )
            }

            // First check if the setting exists
            when (val existsResult = unitOfWork.settings.existsAsync(command.key)) {
                is Result.Success -> {
                    if (!existsResult.data) {
                        logger.w { "Setting not found for key: ${command.key}" }
                        return Result.success(
                            DeleteSettingCommandResult(
                                isSuccess = false,
                                errorMessage = "Setting not found"
                            )
                        )
                    }

                    // Get the setting to delete it
                    when (val getResult = unitOfWork.settings.getByKeyAsync(command.key)) {
                        is Result.Success -> {
                            val setting = getResult.data
                            if (setting == null) {
                                logger.w { "Setting not found for key: ${command.key}" }
                                return Result.success(
                                    DeleteSettingCommandResult(
                                        isSuccess = false,
                                        errorMessage = "Setting not found"
                                    )
                                )
                            }

                            // Delete the setting
                            when (val deleteResult = unitOfWork.settings.deleteAsync(setting)) {
                                is Result.Success -> {
                                    try {
                                        unitOfWork.saveChangesAsync()
                                        logger.i { "Successfully deleted setting: ${command.key}" }

                                        Result.success(
                                            DeleteSettingCommandResult(
                                                isSuccess = true
                                            )
                                        )
                                    } catch (ex: Exception) {
                                        logger.e(ex) { "Failed to save delete changes for key: ${command.key}" }
                                        Result.success(
                                            DeleteSettingCommandResult(
                                                isSuccess = false,
                                                errorMessage = "Failed to save deletion: ${ex.message}"
                                            )
                                        )
                                    }
                                }
                                is Result.Failure -> {
                                    logger.e { "Failed to delete setting: ${command.key} - ${deleteResult.error}" }
                                    Result.success(
                                        DeleteSettingCommandResult(
                                            isSuccess = false,
                                            errorMessage = deleteResult.error
                                        )
                                    )
                                }
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to get setting for deletion: ${command.key} - ${getResult.error}" }
                            Result.success(
                                DeleteSettingCommandResult(
                                    isSuccess = false,
                                    errorMessage = getResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to check if setting exists: ${command.key} - ${existsResult.error}" }
                    Result.success(
                        DeleteSettingCommandResult(
                            isSuccess = false,
                            errorMessage = existsResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error deleting setting: ${command.key}" }
            Result.success(
                DeleteSettingCommandResult(
                    isSuccess = false,
                    errorMessage = "Error deleting setting: ${ex.message}"
                )
            )
        }
    }
}