// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IFOVCalculationService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto

interface IFOVCalculationService {

    /**
     * Calculates horizontal field of view from focal length and sensor width
     */
    fun calculateHorizontalFOV(focalLength: Double, sensorWidth: Double): Double

    /**
     * Calculates vertical field of view from focal length and sensor height
     */
    fun calculateVerticalFOV(focalLength: Double, sensorHeight: Double): Double

    /**
     * Estimates sensor dimensions based on phone model if available
     */
    suspend fun estimateSensorDimensionsAsync(phoneModel: String): Result<SensorDimensions>

    /**
     * Creates a phone camera profile from EXIF data
     */
    suspend fun createPhoneCameraProfileAsync(
        phoneModel: String,
        focalLength: Double
    ): Result<PhoneCameraProfileDto>

    /**
     * Calculates overlay box dimensions for FOV preview
     */
    fun calculateOverlayBox(phoneFOV: Double, cameraFOV: Double, screenSize: Size): OverlayBox
}

data class SensorDimensions(
    val width: Double,
    val height: Double,
    val sensorType: String = "Unknown"
)

data class OverlayBox(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

data class Size(
    val width: Int,
    val height: Int
)