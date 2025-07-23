// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ImageAnalysisData.kt
package com.x3squaredcircles.photography.domain.services

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