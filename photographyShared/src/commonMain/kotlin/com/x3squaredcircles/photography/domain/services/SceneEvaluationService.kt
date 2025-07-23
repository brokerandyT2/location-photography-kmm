// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/SceneEvaluationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.SceneEvaluationResultDto
import com.x3squaredcircles.photography.domain.models.SceneEvaluationStatsDto
import com.x3squaredcircles.photography.domain.models.ImageAnalysisData
import com.x3squaredcircles.photography.domain.services.ISceneEvaluationService
import com.x3squaredcircles.photography.domain.services.ICameraService
import com.x3squaredcircles.photography.domain.services.IImageAnalysisService
import com.x3squaredcircles.photography.domain.services.HistogramColor
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SceneEvaluationService(
    private val cameraService: ICameraService,
    private val imageAnalysisService: IImageAnalysisService,
    private val logger: Logger
) : ISceneEvaluationService {

    override suspend fun evaluateSceneAsync(): Result<SceneEvaluationResultDto> {
        return try {
            logger.d { "Starting scene evaluation - capturing current scene" }

            val captureResult = cameraService.captureImageAsync()
            when (captureResult) {
                is Result.Success -> {
                    val imagePath = captureResult.data
                    logger.i { "Successfully captured scene image: $imagePath" }

                    analyzeImageAsync(imagePath)
                }
                is Result.Failure -> {
                    logger.e { "Failed to capture scene image: ${captureResult.error}" }
                    Result.failure("Failed to capture scene: ${captureResult.error}")
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error during scene evaluation" }
            Result.failure("Error evaluating scene: ${ex.message}")
        }
    }

    override suspend fun analyzeImageAsync(imagePath: String): Result<SceneEvaluationResultDto> {
        return try {
            logger.d { "Starting image analysis for: $imagePath" }

            if (imagePath.isBlank()) {
                return Result.failure("Image path cannot be empty")
            }

            if (!isValidImageFile(imagePath)) {
                return Result.failure("Invalid image file or unsupported format")
            }

            val analysisResult = withContext(Dispatchers.Default) {
                performImageAnalysis(imagePath)
            }

            when (analysisResult) {
                is Result.Success -> {
                    logger.i { "Successfully analyzed image: $imagePath" }
                    Result.success(analysisResult.data)
                }
                is Result.Failure -> {
                    logger.e { "Failed to analyze image: ${analysisResult.error}" }
                    Result.failure("Image analysis failed: ${analysisResult.error}")
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error during image analysis" }
            Result.failure("Error analyzing image: ${ex.message}")
        }
    }

    private suspend fun performImageAnalysis(imagePath: String): Result<SceneEvaluationResultDto> {
        return try {
            val imageAnalysisResult = imageAnalysisService.analyzeImageAsync(imagePath)
            when (imageAnalysisResult) {
                is Result.Failure -> {
                    return Result.failure("Failed to analyze image: ${imageAnalysisResult.error}")
                }
                is Result.Success -> {
                    val analysisData = imageAnalysisResult.data

                    val timestamp = Clock.System.now().toEpochMilliseconds()
                    val baseFileName = "histogram_${timestamp}"

                    val redHistogramPath = generateHistogramImage(
                        analysisData.redHistogram,
                        HistogramColor.RED,
                        "${baseFileName}_red"
                    )

                    val greenHistogramPath = generateHistogramImage(
                        analysisData.greenHistogram,
                        HistogramColor.GREEN,
                        "${baseFileName}_green"
                    )

                    val blueHistogramPath = generateHistogramImage(
                        analysisData.blueHistogram,
                        HistogramColor.BLUE,
                        "${baseFileName}_blue"
                    )

                    val contrastHistogramPath = generateHistogramImage(
                        analysisData.luminanceHistogram,
                        HistogramColor.LUMINANCE,
                        "${baseFileName}_contrast"
                    )

                    val stats = calculateStatistics(analysisData)

                    val result = SceneEvaluationResultDto(
                        redHistogramPath = redHistogramPath,
                        greenHistogramPath = greenHistogramPath,
                        blueHistogramPath = blueHistogramPath,
                        contrastHistogramPath = contrastHistogramPath,
                        imagePath = imagePath,
                        stats = stats
                    )

                    logger.i { "Image analysis completed successfully" }
                    Result.success(result)
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error performing image analysis" }
            Result.failure("Image analysis error: ${ex.message}")
        }
    }

    private suspend fun generateHistogramImage(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String
    ): String {
        return try {
            when (val result = imageAnalysisService.generateHistogramImageAsync(histogram, color, fileName)) {
                is Result.Success -> result.data
                is Result.Failure -> {
                    logger.w { "Failed to generate histogram image for $fileName: ${result.error}" }
                    ""
                }
            }
        } catch (ex: Exception) {
            logger.w(ex) { "Failed to generate histogram image for $fileName" }
            ""
        }
    }

    private fun calculateStatistics(analysisData: ImageAnalysisData): SceneEvaluationStatsDto {
        return SceneEvaluationStatsDto(
            meanRed = analysisData.redHistogram.calculateMean(),
            meanGreen = analysisData.greenHistogram.calculateMean(),
            meanBlue = analysisData.blueHistogram.calculateMean(),
            meanContrast = analysisData.luminanceHistogram.calculateMean(),
            stdDevRed = analysisData.redHistogram.calculateStandardDeviation(),
            stdDevGreen = analysisData.greenHistogram.calculateStandardDeviation(),
            stdDevBlue = analysisData.blueHistogram.calculateStandardDeviation(),
            stdDevContrast = analysisData.luminanceHistogram.calculateStandardDeviation(),
            totalPixels = analysisData.totalPixels
        )
    }

    private fun isValidImageFile(path: String): Boolean {
        val validExtensions = setOf("jpg", "jpeg", "png", "bmp", "gif")
        val extension = path.substringAfterLast('.', "").lowercase()
        return validExtensions.contains(extension)
    }

    private fun DoubleArray.calculateMean(): Double {
        return if (isEmpty()) 0.0 else sum() / size
    }

    private fun DoubleArray.calculateStandardDeviation(): Double {
        if (isEmpty()) return 0.0
        val mean = calculateMean()
        val variance = map { (it - mean) * (it - mean) }.sum() / size
        return kotlin.math.sqrt(variance)
    }
}