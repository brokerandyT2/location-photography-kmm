// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/BulkDeleteSettingsCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.BulkDeleteSettingsCommand
import com.x3squaredcircles.photography.application.commands.setting.BulkDeleteSettingsCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class BulkDeleteSettingsCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<BulkDeleteSettingsCommand, BulkDeleteSettingsCommandResult> {

    override suspend fun handle(command: BulkDeleteSettingsCommand): Result<BulkDeleteSettingsCommandResult> {
        logger.d { "Handling BulkDeleteSettingsCommand for ${command.keys.size} keys" }

        return try {
            if (command.keys.isEmpty()) {
                logger.w { "No keys provided for bulk delete" }
                return Result.success(
                    BulkDeleteSettingsCommandResult(
                        deletedCount = 0,
                        isSuccess = true
                    )
                )
            }

            val validKeys = command.keys.filter { it.isNotBlank() }
            if (validKeys.size != command.keys.size) {
                logger.w { "Found ${command.keys.size - validKeys.size} invalid keys in bulk delete" }
            }

            if (validKeys.isEmpty()) {
                return Result.success(
                    BulkDeleteSettingsCommandResult(
                        deletedCount = 0,
                        isSuccess = false,
                        errorMessage = "No valid keys provided for deletion"
                    )
                )
            }

            when (val result = unitOfWork.settings.deleteBulkAsync(validKeys)) {
                is Result.Success -> {
                    try {
                        unitOfWork.saveChangesAsync()
                        logger.i { "Successfully deleted ${result.data} settings" }

                        Result.success(
                            BulkDeleteSettingsCommandResult(
                                deletedCount = result.data,
                                isSuccess = true
                            )
                        )
                    } catch (ex: Exception) {
                        logger.e(ex) { "Failed to save bulk delete changes" }
                        Result.success(
                            BulkDeleteSettingsCommandResult(
                                deletedCount = 0,
                                isSuccess = false,
                                errorMessage = "Failed to save delete changes: ${ex.message}"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to bulk delete settings: ${result.error}" }
                    Result.success(
                        BulkDeleteSettingsCommandResult(
                            deletedCount = 0,
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error in bulk delete settings" }
            Result.success(
                BulkDeleteSettingsCommandResult(
                    deletedCount = 0,
                    isSuccess = false,
                    errorMessage = "Error in bulk delete settings: ${ex.message}"
                )
            )
        }
    }
}