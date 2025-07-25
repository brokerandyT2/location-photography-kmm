// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/CameraService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.ICameraService
import com.x3squaredcircles.photography.domain.services.CameraConfiguration
import com.x3squaredcircles.photography.domain.services.CameraResolution
import com.x3squaredcircles.photography.domain.services.ImageFormat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

expect class PlatformCameraManager {
    suspend fun isCameraAvailable(): Boolean
    suspend fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
    suspend fun captureImage(): String?
    suspend fun getCameraConfiguration(): CameraConfiguration
}

class CameraService(
    private val platformCameraManager: PlatformCameraManager,
    private val logger: Logger
) : ICameraService {

    override suspend fun captureImageAsync(): Result<String> {
        return try {
            logger.d { "Starting image capture" }

            // Check camera availability
            val availabilityResult = isCameraAvailableAsync()
            when (availabilityResult) {
                is Result.Failure -> {
                    return Result.failure("Camera not available: ${availabilityResult.error}")
                }
                is Result.Success -> {
                    if (!availabilityResult.data) {
                        return Result.failure("Camera is not available on this device")
                    }
                }
            }

            // Check permissions
            val permissionResult = isCameraPermissionGrantedAsync()
            when (permissionResult) {
                is Result.Failure -> {
                    return Result.failure("Failed to check camera permission: ${permissionResult.error}")
                }
                is Result.Success -> {
                    if (!permissionResult.data) {
                        // Try to request permission
                        val requestResult = requestCameraPermissionAsync()
                        when (requestResult) {
                            is Result.Failure -> {
                                return Result.failure("Camera permission denied: ${requestResult.error}")
                            }
                            is Result.Success -> {
                                if (!requestResult.data) {
                                    return Result.failure("Camera permission denied by user")
                                }
                            }
                        }
                    }
                }
            }

            // Capture the image
            withContext(Dispatchers.Main) {
                val imagePath = platformCameraManager.captureImage()
                if (imagePath.isNullOrBlank()) {
                    logger.e { "Image capture returned null or empty path" }
                    Result.failure("Failed to capture image")
                } else {
                    logger.i { "Successfully captured image: $imagePath" }
                    Result.success(imagePath)
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error during image capture" }
            Result.failure("Image capture failed: ${ex.message}")
        }
    }

    override suspend fun isCameraPermissionGrantedAsync(): Result<Boolean> {
        return try {
            val hasPermission = platformCameraManager.hasPermission()
            logger.d { "Camera permission check: $hasPermission" }
            Result.success(hasPermission)
        } catch (ex: Exception) {
            logger.e(ex) { "Error checking camera permission" }
            Result.failure("Failed to check camera permission: ${ex.message}")
        }
    }

    override suspend fun requestCameraPermissionAsync(): Result<Boolean> {
        return try {
            logger.d { "Requesting camera permission" }
            val granted = platformCameraManager.requestPermission()
            logger.i { "Camera permission request result: $granted" }
            Result.success(granted)
        } catch (ex: Exception) {
            logger.e(ex) { "Error requesting camera permission" }
            Result.failure("Failed to request camera permission: ${ex.message}")
        }
    }

    override suspend fun isCameraAvailableAsync(): Result<Boolean> {
        return try {
            val available = platformCameraManager.isCameraAvailable()
            logger.d { "Camera availability check: $available" }
            Result.success(available)
        } catch (ex: Exception) {
            logger.e(ex) { "Error checking camera availability" }
            Result.failure("Failed to check camera availability: ${ex.message}")
        }
    }

    override suspend fun getCameraConfigurationAsync(): Result<CameraConfiguration> {
        return try {
            logger.d { "Getting camera configuration" }
            val config = platformCameraManager.getCameraConfiguration()
            logger.i { "Retrieved camera configuration: ${config.isAvailable}" }
            Result.success(config)
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting camera configuration" }

            // Return default configuration on error
            val defaultConfig = CameraConfiguration(
                isAvailable = false,
                hasFlash = false,
                hasAutoFocus = false,
                supportedResolutions = listOf(
                    CameraResolution(1920, 1080),
                    CameraResolution(1280, 720)
                ),
                defaultResolution = CameraResolution(1920, 1080),
                maxZoom = 1.0f,
                supportedFormats = listOf(ImageFormat.JPEG),
                defaultFormat = ImageFormat.JPEG
            )

            Result.success(defaultConfig)
        }
    }
}