// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/sceneevaluation/handlers/AnalyzeImageCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.sceneevaluation.handlers

import com.x3squaredcircles.photography.application.commands.sceneevaluation.AnalyzeImageCommand
import com.x3squaredcircles.photography.application.commands.sceneevaluation.AnalyzeImageCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.ISceneEvaluationService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class AnalyzeImageCommandHandler(
    private val sceneEvaluationService: ISceneEvaluationService,
    private val logger: Logger
) : ICommandHandler<AnalyzeImageCommand, AnalyzeImageCommandResult> {

    override suspend fun handle(command: AnalyzeImageCommand): Result<AnalyzeImageCommandResult> {
        logger.d { "Handling AnalyzeImageCommand for image: ${command.imagePath}" }

        return try {
            if (command.imagePath.isBlank()) {
                logger.w { "Image path is empty or blank" }
                return Result.success(
                    AnalyzeImageCommandResult(
                        result = null,
                        isSuccess = false,
                        errorMessage = "Image path is required"
                    )
                )
            }

            if (!isValidImagePath(command.imagePath)) {
                logger.w { "Invalid image path or extension: ${command.imagePath}" }
                return Result.success(
                    AnalyzeImageCommandResult(
                        result = null,
                        isSuccess = false,
                        errorMessage = "Invalid image path or unsupported file format"
                    )
                )
            }

            when (val result = sceneEvaluationService.analyzeImageAsync(command.imagePath)) {
                is Result.Success -> {
                    logger.i { "Successfully analyzed image: ${command.imagePath}" }
                    Result.success(
                        AnalyzeImageCommandResult(
                            result = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to analyze image: ${command.imagePath} - ${result.error}" }
                    Result.success(
                        AnalyzeImageCommandResult(
                            result = null,
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error analyzing image: ${command.imagePath}" }
            Result.success(
                AnalyzeImageCommandResult(
                    result = null,
                    isSuccess = false,
                    errorMessage = "Error analyzing image"
                )
            )
        }
    }

    private fun isValidImagePath(path: String): Boolean {
        if (path.isBlank()) return false

        val validExtensions = setOf(".jpg", ".jpeg", ".png", ".bmp", ".gif")
        val extension = path.substringAfterLast('.', "").lowercase()
        return validExtensions.contains(".$extension")
    }
}