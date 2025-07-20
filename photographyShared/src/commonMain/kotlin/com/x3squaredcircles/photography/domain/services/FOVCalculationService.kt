// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/FOVCalculationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto
import com.x3squaredcircles.photography.domain.services.IFOVCalculationService
import com.x3squaredcircles.photography.domain.services.SensorDimensions
import com.x3squaredcircles.photography.domain.services.OverlayBox
import com.x3squaredcircles.photography.domain.services.Size
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.math.*

class FOVCalculationService(
    private val logger: Logger
) : IFOVCalculationService {

    override fun calculateHorizontalFOV(focalLength: Double, sensorWidth: Double): Double {
        if (focalLength <= 0.0 || sensorWidth <= 0.0) {
            logger.w { "Invalid parameters for FOV calculation: focalLength=$focalLength, sensorWidth=$sensorWidth" }
            return 0.0
        }

        // FOV = 2 * arctan(sensorWidth / (2 * focalLength))
        val fovRadians = 2.0 * atan(sensorWidth / (2.0 * focalLength))
        val fovDegrees = fovRadians * (180.0 / PI)

        logger.d { "Calculated horizontal FOV: ${fovDegrees}° (focal length: ${focalLength}mm, sensor width: ${sensorWidth}mm)" }
        return fovDegrees
    }

    override fun calculateVerticalFOV(focalLength: Double, sensorHeight: Double): Double {
        if (focalLength <= 0.0 || sensorHeight <= 0.0) {
            logger.w { "Invalid parameters for vertical FOV calculation: focalLength=$focalLength, sensorHeight=$sensorHeight" }
            return 0.0
        }

        // FOV = 2 * arctan(sensorHeight / (2 * focalLength))
        val fovRadians = 2.0 * atan(sensorHeight / (2.0 * focalLength))
        val fovDegrees = fovRadians * (180.0 / PI)

        logger.d { "Calculated vertical FOV: ${fovDegrees}° (focal length: ${focalLength}mm, sensor height: ${sensorHeight}mm)" }
        return fovDegrees
    }

    override suspend fun estimateSensorDimensionsAsync(phoneModel: String): Result<SensorDimensions> {
        return try {
            withContext(Dispatchers.Default) {
                val phoneModelLower = phoneModel.lowercase().trim()

                // Database of common phone sensor dimensions (width x height in mm)
                val sensorDimensions = when {
                    // iPhone models
                    phoneModelLower.contains("iphone 15") -> SensorDimensions(7.39, 5.56, "iPhone 15 Series")
                    phoneModelLower.contains("iphone 14") -> SensorDimensions(7.39, 5.56, "iPhone 14 Series")
                    phoneModelLower.contains("iphone 13") -> SensorDimensions(7.39, 5.56, "iPhone 13 Series")
                    phoneModelLower.contains("iphone 12") -> SensorDimensions(7.39, 5.56, "iPhone 12 Series")
                    phoneModelLower.contains("iphone 11") -> SensorDimensions(6.90, 5.18, "iPhone 11 Series")
                    phoneModelLower.contains("iphone x") -> SensorDimensions(6.90, 5.18, "iPhone X Series")
                    phoneModelLower.contains("iphone 8") -> SensorDimensions(6.17, 4.55, "iPhone 8 Series")
                    phoneModelLower.contains("iphone 7") -> SensorDimensions(6.17, 4.55, "iPhone 7 Series")
                    phoneModelLower.contains("iphone 6") -> SensorDimensions(5.76, 4.29, "iPhone 6 Series")
                    phoneModelLower.contains("iphone") -> SensorDimensions(5.76, 4.29, "iPhone Generic")

                    // Samsung Galaxy models
                    phoneModelLower.contains("galaxy s24") -> SensorDimensions(7.04, 5.28, "Galaxy S24 Series")
                    phoneModelLower.contains("galaxy s23") -> SensorDimensions(7.04, 5.28, "Galaxy S23 Series")
                    phoneModelLower.contains("galaxy s22") -> SensorDimensions(7.04, 5.28, "Galaxy S22 Series")
                    phoneModelLower.contains("galaxy s21") -> SensorDimensions(7.04, 5.28, "Galaxy S21 Series")
                    phoneModelLower.contains("galaxy s20") -> SensorDimensions(7.04, 5.28, "Galaxy S20 Series")
                    phoneModelLower.contains("galaxy note") -> SensorDimensions(7.04, 5.28, "Galaxy Note Series")
                    phoneModelLower.contains("galaxy") -> SensorDimensions(6.40, 4.80, "Galaxy Generic")

                    // Google Pixel models
                    phoneModelLower.contains("pixel 8") -> SensorDimensions(7.39, 5.56, "Pixel 8 Series")
                    phoneModelLower.contains("pixel 7") -> SensorDimensions(7.39, 5.56, "Pixel 7 Series")
                    phoneModelLower.contains("pixel 6") -> SensorDimensions(7.39, 5.56, "Pixel 6 Series")
                    phoneModelLower.contains("pixel 5") -> SensorDimensions(6.90, 5.18, "Pixel 5 Series")
                    phoneModelLower.contains("pixel 4") -> SensorDimensions(6.90, 5.18, "Pixel 4 Series")
                    phoneModelLower.contains("pixel") -> SensorDimensions(6.40, 4.80, "Pixel Generic")

                    // OnePlus models
                    phoneModelLower.contains("oneplus 11") -> SensorDimensions(7.22, 5.42, "OnePlus 11")
                    phoneModelLower.contains("oneplus 10") -> SensorDimensions(7.22, 5.42, "OnePlus 10 Series")
                    phoneModelLower.contains("oneplus 9") -> SensorDimensions(7.22, 5.42, "OnePlus 9 Series")
                    phoneModelLower.contains("oneplus") -> SensorDimensions(6.40, 4.80, "OnePlus Generic")

                    // Xiaomi models
                    phoneModelLower.contains("mi 13") -> SensorDimensions(7.04, 5.28, "Xiaomi Mi 13 Series")
                    phoneModelLower.contains("mi 12") -> SensorDimensions(7.04, 5.28, "Xiaomi Mi 12 Series")
                    phoneModelLower.contains("mi 11") -> SensorDimensions(7.04, 5.28, "Xiaomi Mi 11 Series")
                    phoneModelLower.contains("redmi") || phoneModelLower.contains("mi") -> SensorDimensions(6.40, 4.80, "Xiaomi Generic")

                    // Huawei models
                    phoneModelLower.contains("p50") -> SensorDimensions(7.39, 5.56, "Huawei P50 Series")
                    phoneModelLower.contains("p40") -> SensorDimensions(7.39, 5.56, "Huawei P40 Series")
                    phoneModelLower.contains("mate 50") -> SensorDimensions(7.39, 5.56, "Huawei Mate 50 Series")
                    phoneModelLower.contains("mate 40") -> SensorDimensions(7.39, 5.56, "Huawei Mate 40 Series")
                    phoneModelLower.contains("huawei") -> SensorDimensions(6.40, 4.80, "Huawei Generic")

                    // Default fallback for unknown phones
                    else -> SensorDimensions(5.76, 4.29, "Unknown Phone")
                }

                logger.i { "Estimated sensor dimensions for '$phoneModel': ${sensorDimensions.width}x${sensorDimensions.height}mm (${sensorDimensions.sensorType})" }
                Result.success(sensorDimensions)
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error estimating sensor dimensions for phone model: $phoneModel" }
            Result.failure("Failed to estimate sensor dimensions")
        }
    }

    override suspend fun createPhoneCameraProfileAsync(
        phoneModel: String,
        focalLength: Double
    ): Result<PhoneCameraProfileDto> {
        return try {
            withContext(Dispatchers.Default) {
                logger.d { "Creating phone camera profile for: $phoneModel with focal length: ${focalLength}mm" }

                if (focalLength <= 0.0) {
                    return@withContext Result.failure("Invalid focal length: $focalLength")
                }

                // Get sensor dimensions for the phone model
                when (val sensorResult = estimateSensorDimensionsAsync(phoneModel)) {
                    is Result.Success -> {
                        val sensorDimensions = sensorResult.data

                        // Calculate FOV using the main camera sensor
                        val horizontalFOV = calculateHorizontalFOV(focalLength, sensorDimensions.width)

                        // Estimate other lenses (these are approximations)
                        val ultraWideFocalLength = estimateUltraWideFocalLength(phoneModel)
                        val telephotoFocalLength = estimateTelephotoFocalLength(phoneModel)

                        val profile = PhoneCameraProfileDto(
                            id = 0, // Will be set by repository
                            phoneModel = phoneModel.trim(),
                            mainLensFocalLength = focalLength,
                            mainLensFOV = horizontalFOV,
                            ultraWideFocalLength = ultraWideFocalLength,
                            telephotoFocalLength = telephotoFocalLength,
                            dateCalibrated = Clock.System.now().toEpochMilliseconds(),
                            isActive = true
                        )

                        logger.i { "Created phone camera profile: $phoneModel, focal length: ${focalLength}mm, FOV: ${horizontalFOV}°" }
                        Result.success(profile)
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to get sensor dimensions for $phoneModel: ${sensorResult.error}" }
                        Result.failure("Failed to create camera profile: ${sensorResult.error}")
                    }
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error creating phone camera profile for: $phoneModel" }
            Result.failure("Error creating camera profile: ${ex.message}")
        }
    }

    override fun calculateOverlayBox(phoneFOV: Double, cameraFOV: Double, screenSize: Size): OverlayBox {
        if (phoneFOV <= 0.0 || cameraFOV <= 0.0 || screenSize.width <= 0 || screenSize.height <= 0) {
            logger.w { "Invalid parameters for overlay box calculation" }
            return OverlayBox(0, 0, screenSize.width, screenSize.height)
        }

        // Calculate the ratio of camera FOV to phone FOV
        val fovRatio = cameraFOV / phoneFOV

        // Calculate overlay dimensions (assuming landscape orientation)
        val overlayWidth = (screenSize.width * fovRatio).toInt().coerceAtMost(screenSize.width)
        val overlayHeight = (screenSize.height * fovRatio).toInt().coerceAtMost(screenSize.height)

        // Center the overlay
        val overlayX = (screenSize.width - overlayWidth) / 2
        val overlayY = (screenSize.height - overlayHeight) / 2

        val overlayBox = OverlayBox(overlayX, overlayY, overlayWidth, overlayHeight)

        logger.d { "Calculated overlay box: ${overlayBox.width}x${overlayBox.height} at (${overlayBox.x}, ${overlayBox.y}) for FOV ratio: $fovRatio" }
        return overlayBox
    }

    private fun estimateUltraWideFocalLength(phoneModel: String): Double? {
        val phoneModelLower = phoneModel.lowercase()

        return when {
            phoneModelLower.contains("iphone 15") || phoneModelLower.contains("iphone 14") -> 13.0
            phoneModelLower.contains("iphone 13") || phoneModelLower.contains("iphone 12") -> 13.0
            phoneModelLower.contains("iphone 11") -> 13.0
            phoneModelLower.contains("galaxy s24") || phoneModelLower.contains("galaxy s23") -> 13.0
            phoneModelLower.contains("galaxy s22") || phoneModelLower.contains("galaxy s21") -> 13.0
            phoneModelLower.contains("pixel 8") || phoneModelLower.contains("pixel 7") -> 14.0
            phoneModelLower.contains("pixel 6") -> 14.0
            phoneModelLower.contains("oneplus") -> 14.0
            phoneModelLower.contains("mi") || phoneModelLower.contains("redmi") -> 13.0
            else -> null // Not all phones have ultra-wide cameras
        }
    }

    private fun estimateTelephotoFocalLength(phoneModel: String): Double? {
        val phoneModelLower = phoneModel.lowercase()

        return when {
            phoneModelLower.contains("iphone 15 pro") -> 77.0
            phoneModelLower.contains("iphone 14 pro") -> 77.0
            phoneModelLower.contains("iphone 13 pro") -> 77.0
            phoneModelLower.contains("iphone 12 pro") -> 65.0
            phoneModelLower.contains("galaxy s24 ultra") -> 200.0 // 10x zoom
            phoneModelLower.contains("galaxy s23 ultra") -> 200.0
            phoneModelLower.contains("galaxy s22 ultra") -> 200.0
            phoneModelLower.contains("galaxy s24") || phoneModelLower.contains("galaxy s23") -> 70.0
            phoneModelLower.contains("pixel 8 pro") -> 120.0
            phoneModelLower.contains("pixel 7 pro") -> 104.0
            phoneModelLower.contains("oneplus 11") -> 70.0
            else -> null // Not all phones have telephoto cameras
        }
    }
}