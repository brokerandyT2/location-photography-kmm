// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ImageAnalysisModels.kt
package com.x3squaredcircles.photography.domain.models

data class ImageAnalysisResult(
    val colorAnalysis: ColorAnalysis,
    val compositionAnalysis: CompositionAnalysis,
    val detectedObjects: List<String> = emptyList(),
    val qualityScore: Double = 0.0,
    val recommendations: List<String> = emptyList()
)

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