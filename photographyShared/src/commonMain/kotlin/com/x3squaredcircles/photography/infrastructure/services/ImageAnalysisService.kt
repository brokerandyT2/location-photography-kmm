// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ImageAnalysisService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.IImageAnalysisService
import com.x3squaredcircles.photography.domain.services.ImageAnalysisResult
import com.x3squaredcircles.photography.domain.services.HistogramColor
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ImageAnalysisService(
    private val logger: Logger
) : IImageAnalysisService {

    private val histogramCache = mutableMapOf<String, String>()
    private val platformImageProcessor = createPlatformImageProcessor()

    override suspend fun analyzeImageAsync(imagePath: String): Result<ImageAnalysisResult> {
        return try {
            logger.d { "Analyzing image: $imagePath" }

            if (!imageExists(imagePath)) {
                return Result.failure("Image file does not exist: $imagePath")
            }

            withContext(Dispatchers.Default) {
                val image = platformImageProcessor.loadImageFromPath(imagePath)
                    ?: return@withContext Result.failure("Failed to load image: $imagePath")

                val analysisResult = platformImageProcessor.extractHistogramData(image)
                logger.i { "Successfully analyzed image: $imagePath" }
                Result.success(analysisResult)
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error analyzing image: $imagePath" }
            Result.failure("Image analysis failed: ${ex.message}")
        }
    }

    override suspend fun generateHistogramImageAsync(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String
    ): Result<String> {
        return try {
            val cacheKey = "${fileName}_${color.name}_${histogram.contentHashCode()}"

            histogramCache[cacheKey]?.let { cachedPath ->
                if (fileExists(cachedPath)) {
                    logger.d { "Using cached histogram: $cachedPath" }
                    return Result.success(cachedPath)
                }
            }

            withContext(Dispatchers.Default) {
                val outputPath = platformImageProcessor.generateHistogramImage(
                    histogram, color, fileName, getOutputDirectory()
                )

                if (outputPath.isNotEmpty()) {
                    histogramCache[cacheKey] = outputPath
                    logger.d { "Generated histogram image: $outputPath" }
                    Result.success(outputPath)
                } else {
                    Result.failure("Failed to generate histogram image")
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error generating histogram image: $fileName" }
            Result.failure("Histogram generation failed: ${ex.message}")
        }
    }

    override suspend fun generateStackedHistogramImageAsync(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String
    ): Result<String> {
        return try {
            val cacheKey = "${fileName}_stacked_${redHistogram.contentHashCode()}_${greenHistogram.contentHashCode()}_${blueHistogram.contentHashCode()}_${luminanceHistogram.contentHashCode()}"

            histogramCache[cacheKey]?.let { cachedPath ->
                if (fileExists(cachedPath)) {
                    logger.d { "Using cached stacked histogram: $cachedPath" }
                    return Result.success(cachedPath)
                }
            }

            withContext(Dispatchers.Default) {
                val outputPath = platformImageProcessor.generateStackedHistogramImage(
                    redHistogram, greenHistogram, blueHistogram, luminanceHistogram, fileName, getOutputDirectory()
                )

                if (outputPath.isNotEmpty()) {
                    histogramCache[cacheKey] = outputPath
                    logger.d { "Generated stacked histogram image: $outputPath" }
                    Result.success(outputPath)
                } else {
                    Result.failure("Failed to generate stacked histogram image")
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error generating stacked histogram image: $fileName" }
            Result.failure("Stacked histogram generation failed: ${ex.message}")
        }
    }

    override fun clearHistogramCache() {
        logger.d { "Clearing histogram cache (${histogramCache.size} entries)" }
        histogramCache.clear()
    }

    private fun imageExists(imagePath: String): Boolean {
        return try {
            // Platform-specific file existence check
            // This would be implemented via expect/actual if needed
            imagePath.isNotEmpty()
        } catch (ex: Exception) {
            logger.w(ex) { "Error checking image existence: $imagePath" }
            false
        }
    }

    private fun fileExists(filePath: String): Boolean {
        return try {
            // Platform-specific file existence check
            filePath.isNotEmpty()
        } catch (ex: Exception) {
            logger.w(ex) { "Error checking file existence: $filePath" }
            false
        }
    }

    private fun getOutputDirectory(): String {
        return try {
            // Platform-specific output directory
            // This could be implemented via expect/actual or injected
            "histograms"
        } catch (ex: Exception) {
            logger.w(ex) { "Error getting output directory" }
            "."
        }
    }
}