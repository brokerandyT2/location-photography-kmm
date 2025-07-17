// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SceneEvaluationResultDto.kt
package com.x3squaredcircles.photography.domain.models

data class SceneEvaluationResultDto(
    val redHistogramPath: String = "",
    val greenHistogramPath: String = "",
    val blueHistogramPath: String = "",
    val contrastHistogramPath: String = "",
    val imagePath: String = "",
    val stats: SceneEvaluationStatsDto = SceneEvaluationStatsDto()
)

data class SceneEvaluationStatsDto(
    val meanRed: Double = 0.0,
    val meanGreen: Double = 0.0,
    val meanBlue: Double = 0.0,
    val meanContrast: Double = 0.0,
    val stdDevRed: Double = 0.0,
    val stdDevGreen: Double = 0.0,
    val stdDevBlue: Double = 0.0,
    val stdDevContrast: Double = 0.0,
    val totalPixels: Long = 0
)