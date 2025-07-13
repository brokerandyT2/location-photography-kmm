// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/services/IExifService.kt
package com.x3squaredcircles.photographyshared.infrastructure.services

import com.x3squaredcircles.core.Result

interface IExifService {

    suspend fun extractExifDataAsync(imagePath: String): Result<ExifData>
    suspend fun hasRequiredExifDataAsync(imagePath: String): Result<Boolean>
}

data class ExifData(
    val focalLength: Double? = null,
    val cameraModel: String = "",
    val cameraMake: String = "",
    val dateTaken: Long? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val aperture: Double? = null,
    val lensModel: String = ""
) {
    val hasValidFocalLength: Boolean
        get() = focalLength != null && focalLength > 0

    val fullCameraModel: String
        get() = "$cameraMake $cameraModel".trim()
}