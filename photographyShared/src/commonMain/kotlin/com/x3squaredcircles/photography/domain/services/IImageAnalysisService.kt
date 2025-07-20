// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IImageAnalysisService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result

interface IImageAnalysisService {

    suspend fun analyzeImageAsync(imagePath: String): Result<ImageAnalysisData>

    suspend fun generateHistogramImageAsync(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String
    ): String

    suspend fun generateStackedHistogramImageAsync(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String
    ): String

    fun clearHistogramCache()
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

        if (!redHistogram.contentEquals(other.redHistogram)) return false
        if (!greenHistogram.contentEquals(other.greenHistogram)) return false
        if (!blueHistogram.contentEquals(other.blueHistogram)) return false
        if (!luminanceHistogram.contentEquals(other.luminanceHistogram)) return false
        if (totalPixels != other.totalPixels) return false

        return true
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

enum class HistogramColor {
    RED, GREEN, BLUE, LUMINANCE
}