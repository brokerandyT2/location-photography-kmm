// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/sceneevaluation/EvaluateSceneCommand.kt
package com.x3squaredcircles.photography.application.commands.sceneevaluation

import com.x3squaredcircles.photography.domain.models.SceneEvaluationResultDto

data class EvaluateSceneCommand(
    val dummy: Boolean = true // No parameters needed for scene evaluation - captures current scene
)

data class EvaluateSceneCommandResult(
    val result: SceneEvaluationResultDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)