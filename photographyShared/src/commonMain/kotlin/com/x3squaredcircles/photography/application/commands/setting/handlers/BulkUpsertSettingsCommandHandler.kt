// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/BulkUpsertSettingsCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.BulkUpsertSettingsCommand
import com.x3squaredcircles.photography.application.commands.setting.BulkUpsertSettingsCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class BulkUpsertSettingsCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<BulkUpsertSettingsCommand, BulkUpsertSettingsCommandResult> {

    override suspend fun handle(command: BulkUpsertSettingsCommand): Result<BulkUpsertSettingsCommandResult> {
        logger.d { "Handling BulkUpsertSettingsCommand for ${command.keyValuePairs.size} settings" }

        return try {
            if (command.keyValuePairs.isEmpty()) {
                logger.w { "No settings provided for bulk upsert" }
                return Result.success(
                    BulkUpsertSettingsCommandResult(
                        upsertedSettings = emptyMap(),
                        isSuccess = true
                    )
                )
            }

            // Validate all keys are non-empty
            val invalidKeys = command.keyValuePairs.keys.filter { it.isBlank() }
            if (invalidKeys.isNotEmpty()) {
                logger.w { "Found ${invalidKeys.size} invalid keys in bulk upsert" }
                return Result.success(
                    BulkUpsertSettingsCommandResult(
                        upsertedSettings = emptyMap(),
                        isSuccess = false,
                        errorMessage = "Invalid keys found: keys cannot be blank"
                    )
                )
            }

            // Perform bulk upsert
            when (val result = unitOfWork.settings.upsertBulkAsync(command.keyValuePairs)) {
                is Result.Success -> {
                    // Save changes
                    try {
                        unitOfWork.saveChangesAsync()
                        logger.i { "Successfully upserted ${result.data.size} settings" }

                        Result.success(
                            BulkUpsertSettingsCommandResult(
                                upsertedSettings = result.data,
                                isSuccess = true
                            )
                        )
                    } catch (ex: Exception) {
                        logger.e(ex) { "Failed to save bulk upsert changes" }
                        Result.success(
                            BulkUpsertSettingsCommandResult(
                                upsertedSettings = emptyMap(),
                                isSuccess = false,
                                errorMessage = "Failed to save settings changes: ${ex.message}"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to bulk upsert settings: ${result.error}" }
                    Result.success(
                        BulkUpsertSettingsCommandResult(
                            upsertedSettings = emptyMap(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error in bulk upsert settings" }
            Result.success(
                BulkUpsertSettingsCommandResult(
                    upsertedSettings = emptyMap(),
                    isSuccess = false,
                    errorMessage = "Error in bulk upsert settings: ${ex.message}"
                )
            )
        }
    }
}