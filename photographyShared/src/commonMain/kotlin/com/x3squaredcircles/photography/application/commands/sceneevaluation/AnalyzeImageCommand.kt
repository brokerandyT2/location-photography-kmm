// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/sceneevaluation/AnalyzeImageCommand.kt
package com.x3squaredcircles.photography.application.commands.sceneevaluation

import com.x3squaredcircles.photography.domain.models.SceneEvaluationResultDto

data class AnalyzeImageCommand(
    val imagePath: String
)

data class AnalyzeImageCommandResult(
    val result: SceneEvaluationResultDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)