// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/handlers/BulkCreateTipsCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.tip.handlers

import com.x3squaredcircles.photography.application.commands.tip.BulkCreateTipsCommand
import com.x3squaredcircles.photography.application.commands.tip.BulkCreateTipsCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.core.domain.entities.Tip
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class BulkCreateTipsCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<BulkCreateTipsCommand, BulkCreateTipsCommandResult> {

    override suspend fun handle(command: BulkCreateTipsCommand): Result<BulkCreateTipsCommandResult> {
        logger.d { "Handling BulkCreateTipsCommand for ${command.tips.size} tips" }

        return try {
            if (command.tips.isEmpty()) {
                logger.w { "No tips provided for bulk creation" }
                return Result.success(
                    BulkCreateTipsCommandResult(
                        createdTips = emptyList(),
                        isSuccess = true
                    )
                )
            }

            // Validate all tips
            val invalidTips = command.tips.filter { tipData ->
                tipData.tipTypeId <= 0 || tipData.title.isBlank() || tipData.content.isBlank()
            }

            if (invalidTips.isNotEmpty()) {
                logger.w { "Found ${invalidTips.size} invalid tips in bulk create" }
                return Result.success(
                    BulkCreateTipsCommandResult(
                        createdTips = emptyList(),
                        isSuccess = false,
                        errorMessage = "Invalid tip data found: tips must have valid tipTypeId, title, and content"
                    )
                )
            }

            // Create Tip entities
            val tips = command.tips.map { tipData ->
                Tip.create(
                    tipTypeId = tipData.tipTypeId,
                    title = tipData.title,
                    content = tipData.content
                )
            }

            when (val createResult = unitOfWork.tips.createBulkAsync(tips)) {
                is Result.Success -> {
                    try {
                        unitOfWork.saveChangesAsync()
                        logger.i { "Successfully created ${createResult.data.size} tips in bulk" }

                        Result.success(
                            BulkCreateTipsCommandResult(
                                createdTips = createResult.data,
                                isSuccess = true
                            )
                        )
                    } catch (ex: Exception) {
                        logger.e(ex) { "Failed to save bulk tip creation changes" }
                        Result.success(
                            BulkCreateTipsCommandResult(
                                createdTips = emptyList(),
                                isSuccess = false,
                                errorMessage = "Failed to save tips: ${ex.message}"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to bulk create tips: ${createResult.error}" }
                    Result.success(
                        BulkCreateTipsCommandResult(
                            createdTips = emptyList(),
                            isSuccess = false,
                            errorMessage = createResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error in bulk create tips" }
            Result.success(
                BulkCreateTipsCommandResult(
                    createdTips = emptyList(),
                    isSuccess = false,
                    errorMessage = "Error creating tips: ${ex.message}"
                )
            )
        }
    }
}