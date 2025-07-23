// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ImageAnalysisService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.IImageAnalysisService

import com.x3squaredcircles.photography.domain.services.HistogramData
import com.x3squaredcircles.photography.domain.services.HistogramStatistics
import com.x3squaredcircles.photography.domain.services.ColorTemperatureData
import com.x3squaredcircles.photography.domain.services.ContrastMetrics
import com.x3squaredcircles.photography.domain.services.ExposureAnalysis
import com.x3squaredcircles.photography.domain.services.HistogramColor
import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.domain.models.ImageAnalysisResult


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

                val analysisData = platformImageProcessor.extractHistogramData(image)

                val imageAnalysisResult = convertToImageAnalysisResult(analysisData, imagePath)

                logger.i { "Successfully analyzed image: $imagePath" }
                Result.success(imageAnalysisResult)
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

    private fun convertToImageAnalysisResult(
        analysisData: com.x3squaredcircles.photography.domain.models.ImageAnalysisData,
        imagePath: String
    ): ImageAnalysisResult {
        return ImageAnalysisResult(
            redHistogram = HistogramData(
                values = analysisData.redHistogram,
                statistics = calculateHistogramStatistics(analysisData.redHistogram),
                imagePath = imagePath
            ),
            greenHistogram = HistogramData(
                values = analysisData.greenHistogram,
                statistics = calculateHistogramStatistics(analysisData.greenHistogram),
                imagePath = imagePath
            ),
            blueHistogram = HistogramData(
                values = analysisData.blueHistogram,
                statistics = calculateHistogramStatistics(analysisData.blueHistogram),
                imagePath = imagePath
            ),
            luminanceHistogram = HistogramData(
                values = analysisData.luminanceHistogram,
                statistics = calculateHistogramStatistics(analysisData.luminanceHistogram),
                imagePath = imagePath
            ),
            whiteBalance = ColorTemperatureData(),
            contrast = ContrastMetrics(),
            exposure = ExposureAnalysis()
        )
    }

    private fun calculateHistogramStatistics(histogram: DoubleArray): HistogramStatistics {
        if (histogram.isEmpty()) {
            return HistogramStatistics()
        }

        val mean = histogram.sum() / histogram.size
        val nonZeroValues = histogram.filter { it > 0.0 }
        val median = if (nonZeroValues.isNotEmpty()) {
            val sorted = nonZeroValues.sorted()
            sorted[sorted.size / 2]
        } else 0.0

        val variance = histogram.map { (it - mean) * (it - mean) }.sum() / histogram.size
        val standardDeviation = kotlin.math.sqrt(variance)

        val minValue = histogram.minOrNull() ?: 0.0
        val maxValue = histogram.maxOrNull() ?: 0.0
        val dynamicRange = maxValue - minValue

        val shadowClipping = histogram.take(10).sum() > 0.1
        val highlightClipping = histogram.takeLast(10).sum() > 0.1

        return HistogramStatistics(
            mean = mean,
            median = median,
            standardDeviation = standardDeviation,
            shadowClipping = shadowClipping,
            highlightClipping = highlightClipping,
            dynamicRange = dynamicRange,
            mode = histogram.withIndex().maxByOrNull { it.value }?.index?.toDouble() ?: 0.0,
            skewness = 0.0
        )
    }

    private fun imageExists(imagePath: String): Boolean {
        return try {
            imagePath.isNotEmpty()
        } catch (ex: Exception) {
            logger.w(ex) { "Error checking image existence: $imagePath" }
            false
        }
    }

    private fun fileExists(filePath: String): Boolean {
        return try {
            filePath.isNotEmpty()
        } catch (ex: Exception) {
            logger.w(ex) { "Error checking file existence: $filePath" }
            false
        }
    }

    private fun getOutputDirectory(): String {
        return try {
            "histograms"
        } catch (ex: Exception) {
            logger.w(ex) { "Error getting output directory" }
            "."
        }
    }
}