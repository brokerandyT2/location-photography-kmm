// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/PhoneCameraProfileDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PhoneCameraProfileDto(
    val id: Int = 0,
    val phoneModel: String = "",
    val mainLensFocalLength: Double = 0.0,
    val mainLensFOV: Double = 0.0,
    val ultraWideFocalLength: Double? = null,
    val telephotoFocalLength: Double? = null,
    val dateCalibrated: Long = 0L,
    val isActive: Boolean = true,
    val isCalibrationSuccessful: Boolean = false,
    val errorMessage: String = ""
)