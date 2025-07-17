// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ExifData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class ExifData(
    val cameraMake: String? = null,
    val cameraModel: String? = null,
    val dateTaken: Instant? = null,
    val focalLength: Double? = null,
    val aperture: Double? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val lensModel: String? = null
) {
    val hasValidFocalLength: Boolean
        get() = focalLength != null && focalLength!! > 0

    val fullCameraModel: String
        get() = when {
            !cameraMake.isNullOrBlank() && !cameraModel.isNullOrBlank() -> "$cameraMake $cameraModel"
            !cameraModel.isNullOrBlank() -> cameraModel!!
            !cameraMake.isNullOrBlank() -> cameraMake!!
            else -> ""
        }
}