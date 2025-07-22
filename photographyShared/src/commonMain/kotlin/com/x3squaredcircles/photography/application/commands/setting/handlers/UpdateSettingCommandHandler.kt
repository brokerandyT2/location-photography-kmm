// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/UpdateSettingCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.UpdateSettingCommand
import com.x3squaredcircles.photography.application.commands.setting.UpdateSettingCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class UpdateSettingCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<UpdateSettingCommand, UpdateSettingCommandResult> {

    override suspend fun handle(command: UpdateSettingCommand): Result<UpdateSettingCommandResult> {
        logger.d { "Handling UpdateSettingCommand for key: ${command.key}" }

        return try {
            if (command.key.isBlank()) {
                logger.w { "Setting key is blank" }
                return Result.success(
                    UpdateSettingCommandResult(
                        setting = null,
                        isSuccess = false,
                        errorMessage = "Setting key cannot be blank"
                    )
                )
            }

            when (val getResult = unitOfWork.settings.getByKeyAsync(command.key)) {
                is Result.Success -> {
                    val setting = getResult.data
                    if (setting == null) {
                        logger.w { "Setting not found for key: ${command.key}" }
                        return Result.success(
                            UpdateSettingCommandResult(
                                setting = null,
                                isSuccess = false,
                                errorMessage = "Setting not found"
                            )
                        )
                    }

                    // Note: Setting entity only allows updating value, not description
                    setting.updateValue(command.value)

                    when (val updateResult = unitOfWork.settings.updateAsync(setting)) {
                        is Result.Success -> {
                            try {
                                unitOfWork.saveChangesAsync()
                                logger.i { "Successfully updated setting: ${command.key}" }

                                Result.success(
                                    UpdateSettingCommandResult(
                                        setting = setting,
                                        isSuccess = true
                                    )
                                )
                            } catch (ex: Exception) {
                                logger.e(ex) { "Failed to save update changes for key: ${command.key}" }
                                Result.success(
                                    UpdateSettingCommandResult(
                                        setting = null,
                                        isSuccess = false,
                                        errorMessage = "Failed to save setting update: ${ex.message}"
                                    )
                                )
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to update setting: ${command.key} - ${updateResult.error}" }
                            Result.success(
                                UpdateSettingCommandResult(
                                    setting = null,
                                    isSuccess = false,
                                    errorMessage = updateResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to get setting for update: ${command.key} - ${getResult.error}" }
                    Result.success(
                        UpdateSettingCommandResult(
                            setting = null,
                            isSuccess = false,
                            errorMessage = getResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error updating setting: ${command.key}" }
            Result.success(
                UpdateSettingCommandResult(
                    setting = null,
                    isSuccess = false,
                    errorMessage = "Error updating setting: ${ex.message}"
                )
            )
        }
    }
}