// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ImageAnalysisModels.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.services.ColorTemperatureData
import com.x3squaredcircles.photography.domain.services.ContrastMetrics
import com.x3squaredcircles.photography.domain.services.ExposureAnalysis
import com.x3squaredcircles.photography.domain.services.HistogramStatistics

data class ImageAnalysisResult(
    val redHistogram: HistogramData,
    val greenHistogram: HistogramData,
    val blueHistogram: HistogramData,
    val luminanceHistogram: HistogramData,
    val whiteBalance: ColorTemperatureData,
    val contrast: ContrastMetrics,
    val exposure: ExposureAnalysis,
    val totalPixels: Long
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

data class ImageAnalysisData(
    val redHistogram: DoubleArray,
    val greenHistogram: DoubleArray,
    val blueHistogram: DoubleArray,
    val luminanceHistogram: DoubleArray,
    val totalPixels: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ImageAnalysisData
        return redHistogram.contentEquals(other.redHistogram) &&
                greenHistogram.contentEquals(other.greenHistogram) &&
                blueHistogram.contentEquals(other.blueHistogram) &&
                luminanceHistogram.contentEquals(other.luminanceHistogram) &&
                totalPixels == other.totalPixels
    }

    override fun hashCode(): Int {
        var result = redHistogram.contentHashCode()
        result = 31 * result + greenHistogram.contentHashCode()
        result = 31 * result + blueHistogram.contentHashCode()
        result = 31 * result + luminanceHistogram.contentHashCode()
        result = 31 * result + totalPixels.hashCode()
        return result
    }
}

data class ColorAnalysis(
    val dominantColors: List<ColorInfo> = emptyList(),
    val colorHarmony: ColorHarmony = ColorHarmony.UNKNOWN,
    val brightness: Double = 0.0,
    val contrast: Double = 0.0,
    val saturation: Double = 0.0,
    val temperature: ColorTemperature = ColorTemperature.NEUTRAL
)

data class ColorInfo(
    val red: Int,
    val green: Int,
    val blue: Int,
    val percentage: Double,
    val name: String = ""
)

data class CompositionAnalysis(
    val ruleOfThirds: Double = 0.0,
    val leadingLines: Int = 0,
    val symmetry: Double = 0.0,
    val balance: Double = 0.0,
    val focusArea: FocusArea? = null,
    val recommendations: List<String> = emptyList()
)

data class FocusArea(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val confidence: Double
)

enum class ColorHarmony {
    UNKNOWN,
    MONOCHROMATIC,
    ANALOGOUS,
    COMPLEMENTARY,
    TRIADIC,
    SPLIT_COMPLEMENTARY,
    TETRADIC
}

enum class ColorTemperature {
    VERY_WARM,
    WARM,
    NEUTRAL,
    COOL,
    VERY_COOL
}
