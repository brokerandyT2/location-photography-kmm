// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IImageAnalysisService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result

interface IImageAnalysisService {
    suspend fun analyzeImageAsync(imagePath: String): Result<ImageAnalysisResult>
    suspend fun generateHistogramImageAsync(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String
    ): Result<String>
    suspend fun generateStackedHistogramImageAsync(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String
    ): Result<String>
    fun clearHistogramCache()
}

data class ImageAnalysisResult(
    val redHistogram: HistogramData,
    val greenHistogram: HistogramData,
    val blueHistogram: HistogramData,
    val luminanceHistogram: HistogramData,
    val whiteBalance: ColorTemperatureData,
    val contrast: ContrastMetrics,
    val exposure: ExposureAnalysis
)

data class HistogramData(
    val values: DoubleArray = DoubleArray(256),
    val statistics: HistogramStatistics,
    val imagePath: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HistogramData
        return values.contentEquals(other.values) &&
                statistics == other.statistics &&
                imagePath == other.imagePath
    }

    override fun hashCode(): Int {
        var result = values.contentHashCode()
        result = 31 * result + statistics.hashCode()
        result = 31 * result + imagePath.hashCode()
        return result
    }
}

data class HistogramStatistics(
    val mean: Double = 0.0,
    val median: Double = 0.0,
    val standardDeviation: Double = 0.0,
    val shadowClipping: Boolean = false,
    val highlightClipping: Boolean = false,
    val dynamicRange: Double = 0.0,
    val mode: Double = 0.0,
    val skewness: Double = 0.0
)

data class ColorTemperatureData(
    val temperature: Double = 5500.0,
    val tint: Double = 0.0,
    val redRatio: Double = 0.0,
    val greenRatio: Double = 0.0,
    val blueRatio: Double = 0.0
)

data class ContrastMetrics(
    val rmsContrast: Double = 0.0,
    val michelsonContrast: Double = 0.0,
    val weberContrast: Double = 0.0,
    val dynamicRange: Double = 0.0,
    val globalContrast: Double = 0.0
)

data class ExposureAnalysis(
    val averageEv: Double = 0.0,
    val suggestedEv: Double = 0.0,
    val isUnderexposed: Boolean = false,
    val isOverexposed: Boolean = false,
    val recommendedSettings: String = "",
    val histogramBalance: Double = 0.5,
    val shadowDetail: Double = 0.0,
    val highlightDetail: Double = 0.0
)

enum class HistogramColor {
    RED, GREEN, BLUE, LUMINANCE
}

// Platform-specific bitmap processing
expect class PlatformImageProcessor {
    suspend fun loadImage(imagePath: String): PlatformBitmap?
    suspend fun generateHistogramImage(
        histogram: DoubleArray,
        color: HistogramColor,
        width: Int,
        height: Int
    ): ByteArray
    suspend fun generateStackedHistogramImage(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        width: Int,
        height: Int
    ): ByteArray
}

// Platform-specific bitmap representation
expect class PlatformBitmap {
    val width: Int
    val height: Int
    fun getPixel(x: Int, y: Int): PixelColor
}

data class PixelColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255
)