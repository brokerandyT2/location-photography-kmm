// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/sceneevaluation/handlers/EvaluateSceneCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.sceneevaluation.handlers

import com.x3squaredcircles.photography.application.commands.sceneevaluation.EvaluateSceneCommand
import com.x3squaredcircles.photography.application.commands.sceneevaluation.EvaluateSceneCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.ISceneEvaluationService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class EvaluateSceneCommandHandler(
    private val sceneEvaluationService: ISceneEvaluationService,
    private val logger: Logger
) : ICommandHandler<EvaluateSceneCommand, EvaluateSceneCommandResult> {

    override suspend fun handle(command: EvaluateSceneCommand): Result<EvaluateSceneCommandResult> {
        logger.d { "Handling EvaluateSceneCommand - capturing current scene" }

        return try {
            when (val result = sceneEvaluationService.evaluateSceneAsync()) {
                is Result.Success -> {
                    logger.i { "Successfully evaluated current scene" }
                    Result.success(
                        EvaluateSceneCommandResult(
                            result = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to evaluate scene: ${result.error}" }
                    Result.success(
                        EvaluateSceneCommandResult(
                            result = null,
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error evaluating scene: ${ex.message}" }
            Result.success(
                EvaluateSceneCommandResult(
                    result = null,
                    isSuccess = false,
                    errorMessage = "Error evaluating scene: ${ex.message}"
                )
            )
        }
    }
}