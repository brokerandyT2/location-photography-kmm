// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ISceneEvaluationService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.SceneEvaluationResultDto

interface ISceneEvaluationService {

    suspend fun evaluateSceneAsync(): Result<SceneEvaluationResultDto>

    suspend fun analyzeImageAsync(imagePath: String): Result<SceneEvaluationResultDto>
}