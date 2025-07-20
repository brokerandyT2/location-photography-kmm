// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/PlatformImageProcessing.kt
package com.x3squaredcircles.photography.infrastructure.services

expect fun createPlatformImageProcessor(): PlatformImageProcessor

expect class PlatformImageProcessor {
    suspend fun loadImageFromPath(imagePath: String): PlatformImage?
    suspend fun extractHistogramData(image: PlatformImage): ImageAnalysisData
    suspend fun generateHistogramImage(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String,
        outputDir: String
    ): String
    suspend fun generateStackedHistogramImage(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String,
        outputDir: String
    ): String
}

expect class PlatformImage