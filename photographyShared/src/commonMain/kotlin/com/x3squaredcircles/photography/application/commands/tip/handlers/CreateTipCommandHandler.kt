// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/handlers/CreateTipCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.tip.handlers

import com.x3squaredcircles.photography.application.commands.tip.CreateTipCommand
import com.x3squaredcircles.photography.application.commands.tip.CreateTipCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.entities.Tip
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class CreateTipCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<CreateTipCommand, CreateTipCommandResult> {

    override suspend fun handle(command: CreateTipCommand): Result<CreateTipCommandResult> {
        logger.d { "Handling CreateTipCommand for tipTypeId: ${command.tipTypeId}" }

        return try {
            if (command.tipTypeId <= 0) {
                logger.w { "Invalid tipTypeId: ${command.tipTypeId}" }
                return Result.success(
                    CreateTipCommandResult(
                        tip = createEmptyTip(),
                        isSuccess = false,
                        errorMessage = "Invalid tip type ID"
                    )
                )
            }

            if (command.title.isBlank()) {
                logger.w { "Title is required for tip creation" }
                return Result.success(
                    CreateTipCommandResult(
                        tip = createEmptyTip(),
                        isSuccess = false,
                        errorMessage = "Title is required"
                    )
                )
            }

            if (command.content.isBlank()) {
                logger.w { "Content is required for tip creation" }
                return Result.success(
                    CreateTipCommandResult(
                        tip = createEmptyTip(),
                        isSuccess = false,
                        errorMessage = "Content is required"
                    )
                )
            }

            val newTip = Tip.create(
                tipTypeId = command.tipTypeId,
                title = command.title,
                content = command.content

            )

            when (val createResult = unitOfWork.tips.createAsync(newTip)) {
                is Result.Success -> {
                    try {
                        unitOfWork.saveChangesAsync()
                        logger.i { "Successfully created tip: ${command.title}" }

                        Result.success(
                            CreateTipCommandResult(
                                tip = createResult.data,
                                isSuccess = true
                            )
                        )
                    } catch (ex: Exception) {
                        logger.e(ex) { "Failed to save tip creation changes" }
                        Result.success(
                            CreateTipCommandResult(
                                tip = createEmptyTip(),
                                isSuccess = false,
                                errorMessage = "Failed to save tip: ${ex.message}"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to create tip: ${createResult.error}" }
                    Result.success(
                        CreateTipCommandResult(
                            tip = createEmptyTip(),
                            isSuccess = false,
                            errorMessage = createResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error creating tip: ${command.title}" }
            Result.success(
                CreateTipCommandResult(
                    tip = createEmptyTip(),
                    isSuccess = false,
                    errorMessage = "Error creating tip: ${ex.message}"
                )
            )
        }
    }

    private fun createEmptyTip(): Tip {
        return Tip.create(0, "", "")
    }
}