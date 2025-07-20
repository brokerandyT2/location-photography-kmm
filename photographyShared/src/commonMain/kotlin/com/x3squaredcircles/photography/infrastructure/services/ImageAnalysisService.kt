// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ImageAnalysisService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.IImageAnalysisService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ImageAnalysisService(
    private val logger: Logger
) : IImageAnalysisService {

    private val histogramCache = mutableMapOf<String, String>()
    private val outputDirectory = getOutputDirectory()
    private val platformImageProcessor = createPlatformImageProcessor()

    override suspend fun analyzeImageAsync(imagePath: String): Result<ImageAnalysisData> {
        return try {
            logger.d { "Analyzing image: $imagePath" }

            if (!imageExists(imagePath)) {
                return Result.failure("Image file does not exist: $imagePath")
            }

            withContext(Dispatchers.Default) {
                val image = platformImageProcessor.loadImageFromPath(imagePath)
                    ?: return@withContext Result.failure("Failed to load image: $imagePath")

                val analysisData = platformImageProcessor.extractHistogramData(image)
                logger.i { "Successfully analyzed image: $imagePath" }
                Result.success(analysisData)
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
    ): String {
        return try {
            val cacheKey = "${fileName}_${color.name}_${histogram.contentHashCode()}"

            histogramCache[cacheKey]?.let { cachedPath ->
                if (fileExists(cachedPath)) {
                    logger.d { "Using cached histogram: $cachedPath" }
                    return cachedPath
                }
            }

            withContext(Dispatchers.Default) {
                val outputPath = platformImageProcessor.generateHistogramImage(
                    histogram, color, fileName, outputDirectory
                )

                if (outputPath.isNotEmpty()) {
                    histogramCache[cacheKey] = outputPath
                    logger.d { "Generated histogram image: $outputPath" }
                }

                outputPath
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error generating histogram image: $fileName" }
            ""
        }
    }

    override suspend fun generateStackedHistogramImageAsync(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String
    ): String {
        return try {
            val cacheKey = "${fileName}_stacked_${generateStackedHashCode(redHistogram, greenHistogram, blueHistogram, luminanceHistogram)}"

            histogramCache[cacheKey]?.let { cachedPath ->
                if (fileExists(cachedPath)) {
                    logger.d { "Using cached stacked histogram: $cachedPath" }
                    return cachedPath
                }
            }

            withContext(Dispatchers.Default) {
                val outputPath = platformImageProcessor.generateStackedHistogramImage(
                    redHistogram, greenHistogram, blueHistogram, luminanceHistogram, fileName, outputDirectory
                )

                if (outputPath.isNotEmpty()) {
                    histogramCache[cacheKey] = outputPath
                    logger.d { "Generated stacked histogram image: $outputPath" }
                }

                outputPath
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error generating stacked histogram image: $fileName" }
            ""
        }
    }

    override fun clearHistogramCache() {
        try {
            histogramCache.clear()
            logger.i { "Histogram cache cleared" }
        } catch (ex: Exception) {
            logger.w(ex) { "Error clearing histogram cache" }
        }
    }

    private fun generateStackedHashCode(
        red: DoubleArray,
        green: DoubleArray,
        blue: DoubleArray,
        luminance: DoubleArray
    ): Int {
        var result = red.contentHashCode()
        result = 31 * result + green.contentHashCode()
        result = 31 * result + blue.contentHashCode()
        result = 31 * result + luminance.contentHashCode()
        return result
    }

    private fun getOutputDirectory(): String {
        // Platform-specific implementation needed
        return "histograms/${Clock.System.now().toEpochMilliseconds()}"
    }

    private fun imageExists(path: String): Boolean {
        // Platform-specific implementation needed
        return true
    }

    private fun fileExists(path: String): Boolean {
        // Platform-specific implementation needed
        return true
    }
}