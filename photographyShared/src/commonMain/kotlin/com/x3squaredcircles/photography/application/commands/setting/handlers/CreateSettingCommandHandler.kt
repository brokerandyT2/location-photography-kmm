// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/handlers/CreateSettingCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.setting.handlers

import com.x3squaredcircles.photography.application.commands.setting.CreateSettingCommand
import com.x3squaredcircles.photography.application.commands.setting.CreateSettingCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.entities.Setting
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class CreateSettingCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<CreateSettingCommand, CreateSettingCommandResult> {

    override suspend fun handle(command: CreateSettingCommand): Result<CreateSettingCommandResult> {
        logger.d { "Handling CreateSettingCommand for key: ${command.key}" }

        return try {
            if (command.key.isBlank()) {
                logger.w { "Setting key is blank" }
                return Result.success(
                    CreateSettingCommandResult(
                        setting = Setting.create("", "", ""),
                        isSuccess = false,
                        errorMessage = "Setting key cannot be blank"
                    )
                )
            }

            when (val existsResult = unitOfWork.settings.existsAsync(command.key)) {
                is Result.Success -> {
                    if (existsResult.data) {
                        logger.w { "Setting already exists for key: ${command.key}" }
                        return Result.success(
                            CreateSettingCommandResult(
                                setting = Setting.create("", "", ""),
                                isSuccess = false,
                                errorMessage = "Setting with this key already exists"
                            )
                        )
                    }

                    val newSetting = Setting.create(command.key, command.value, command.description)

                    when (val createResult = unitOfWork.settings.createAsync(newSetting)) {
                        is Result.Success -> {
                            try {
                                unitOfWork.saveChangesAsync()
                                logger.i { "Successfully created setting: ${command.key}" }

                                Result.success(
                                    CreateSettingCommandResult(
                                        setting = createResult.data,
                                        isSuccess = true
                                    )
                                )
                            } catch (ex: Exception) {
                                logger.e(ex) { "Failed to save create changes for key: ${command.key}" }
                                Result.success(
                                    CreateSettingCommandResult(
                                        setting = Setting.create("", "", ""),
                                        isSuccess = false,
                                        errorMessage = "Failed to save setting: ${ex.message}"
                                    )
                                )
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to create setting: ${command.key} - ${createResult.error}" }
                            Result.success(
                                CreateSettingCommandResult(
                                    setting = Setting.create("", "", ""),
                                    isSuccess = false,
                                    errorMessage = createResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to check if setting exists: ${command.key} - ${existsResult.error}" }
                    Result.success(
                        CreateSettingCommandResult(
                            setting = Setting.create("", "", ""),
                            isSuccess = false,
                            errorMessage = existsResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error creating setting: ${command.key}" }
            Result.success(
                CreateSettingCommandResult(
                    setting = Setting.create("", "", ""),
                    isSuccess = false,
                    errorMessage = "Error creating setting: ${ex.message}"
                )
            )
        }
    }
}