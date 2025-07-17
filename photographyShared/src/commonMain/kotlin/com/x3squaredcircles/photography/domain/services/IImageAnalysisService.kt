// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IImageAnalysisService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ImageAnalysisResult
import com.x3squaredcircles.photography.domain.models.ColorAnalysis
import com.x3squaredcircles.photography.domain.models.CompositionAnalysis

interface IImageAnalysisService {

    suspend fun analyzeImageAsync(imagePath: String): Result<ImageAnalysisResult>

    suspend fun analyzeColorAsync(imagePath: String): Result<ColorAnalysis>

    suspend fun analyzeCompositionAsync(imagePath: String): Result<CompositionAnalysis>

    suspend fun detectObjectsAsync(imagePath: String): Result<List<String>>

    suspend fun calculateImageQualityScoreAsync(imagePath: String): Result<Double>
}