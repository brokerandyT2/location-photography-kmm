// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/CameraConfiguration.kt
package com.x3squaredcircles.photography.domain.services

data class CameraConfiguration(
    val isAvailable: Boolean = false,
    val hasFlash: Boolean = false,
    val hasAutoFocus: Boolean = false,
    val supportedResolutions: List<CameraResolution> = emptyList(),
    val defaultResolution: CameraResolution? = null,
    val maxZoom: Float = 1.0f,
    val supportedFormats: List<ImageFormat> = emptyList(),
    val defaultFormat: ImageFormat = ImageFormat.JPEG
)

data class CameraResolution(
    val width: Int,
    val height: Int,
    val aspectRatio: String = "${width}:${height}"
)

enum class ImageFormat {
    JPEG, PNG, RAW
}