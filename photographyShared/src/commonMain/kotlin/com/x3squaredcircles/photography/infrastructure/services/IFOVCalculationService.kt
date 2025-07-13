// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/services/IFOVCalculationService.kt
package com.x3squaredcircles.photographyshared.infrastructure.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile

interface IFOVCalculationService {

    fun calculateHorizontalFOV(focalLength: Double, sensorWidth: Double): Double
    fun calculateVerticalFOV(focalLength: Double, sensorHeight: Double): Double
    suspend fun estimateSensorDimensionsAsync(phoneModel: String): Result<SensorDimensions>
    suspend fun createPhoneCameraProfileAsync(phoneModel: String, focalLength: Double): Result<PhoneCameraProfile>
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