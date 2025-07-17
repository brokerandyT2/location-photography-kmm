// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ExifData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class ExifData(
    var cameraMake: String? = null,
    var cameraModel: String? = null,
    var dateTaken: Instant? = null,
    var focalLength: Double? = null,
    var aperture: Double? = null,
    var imageWidth: Int? = null,
    var imageHeight: Int? = null,
    var lensModel: String? = null
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