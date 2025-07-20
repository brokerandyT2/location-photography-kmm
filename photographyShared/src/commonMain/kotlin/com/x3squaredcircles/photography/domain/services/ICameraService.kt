// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ICameraService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result

interface ICameraService {

    /**
     * Captures an image from the camera and returns the file path
     */
    suspend fun captureImageAsync(): Result<String>

    /**
     * Checks if camera permission is granted
     */
    suspend fun isCameraPermissionGrantedAsync(): Result<Boolean>

    /**
     * Requests camera permission from the user
     */
    suspend fun requestCameraPermissionAsync(): Result<Boolean>

    /**
     * Checks if the camera is available on the device
     */
    suspend fun isCameraAvailableAsync(): Result<Boolean>

    /**
     * Gets the default camera configuration
     */
    suspend fun getCameraConfigurationAsync(): Result<CameraConfiguration>
}