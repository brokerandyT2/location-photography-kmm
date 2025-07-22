// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/UpsertSettingCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.UpsertSettingCommand
import com.x3squaredcircles.photography.application.commands.setting.UpsertSettingCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class UpsertSettingCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<UpsertSettingCommand, UpsertSettingCommandResult> {

    override suspend fun handle(command: UpsertSettingCommand): Result<UpsertSettingCommandResult> {
        logger.d { "Handling UpsertSettingCommand for key: ${command.key}" }

        return try {
            if (command.key.isBlank()) {
                logger.w { "Setting key is blank" }
                return Result.success(
                    UpsertSettingCommandResult(
                        setting = createEmptySetting(),
                        isSuccess = false,
                        errorMessage = "Setting key cannot be blank"
                    )
                )
            }

            when (val result = unitOfWork.settings.upsertAsync(command.key, command.value, command.description)) {
                is Result.Success -> {
                    try {
                        unitOfWork.saveChangesAsync()
                        logger.i { "Successfully upserted setting: ${command.key}" }

                        Result.success(
                            UpsertSettingCommandResult(
                                setting = result.data,
                                isSuccess = true
                            )
                        )
                    } catch (ex: Exception) {
                        logger.e(ex) { "Failed to save upsert changes for key: ${command.key}" }
                        Result.success(
                            UpsertSettingCommandResult(
                                setting = createEmptySetting(),
                                isSuccess = false,
                                errorMessage = "Failed to save setting: ${ex.message}"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to upsert setting: ${command.key} - ${result.error}" }
                    Result.success(
                        UpsertSettingCommandResult(
                            setting = createEmptySetting(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error upserting setting: ${command.key}" }
            Result.success(
                UpsertSettingCommandResult(
                    setting = createEmptySetting(),
                    isSuccess = false,
                    errorMessage = "Error upserting setting: ${ex.message}"
                )
            )
        }
    }

    private fun createEmptySetting(): com.x3squaredcircles.core.domain.entities.Setting {
        return com.x3squaredcircles.core.domain.entities.Setting.create("", "", "")
    }
}