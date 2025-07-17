// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ImageAnalysisModels.kt
package com.x3squaredcircles.photography.domain.models

data class ImageAnalysisResult(
    val colorAnalysis: ColorAnalysis,
    val compositionAnalysis: CompositionAnalysis,
    val detectedObjects: List<String> = emptyList(),
    val qualityScore: Double = 0.0,
    val recommendations: List<String> = emptyList()
)

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