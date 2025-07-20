// photographyShared/src/androidMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/PlatformCameraManager.android.kt
package com.x3squaredcircles.photography.infrastructure.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.x3squaredcircles.photography.domain.services.CameraConfiguration
import com.x3squaredcircles.photography.domain.services.CameraResolution
import com.x3squaredcircles.photography.domain.services.ImageFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class PlatformCameraManager(
    private val context: Context
) {
    actual suspend fun isCameraAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        }
    }

    actual suspend fun hasPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    actual suspend fun requestPermission(): Boolean {
        // Note: This is a simplified implementation
        // In practice, you'd need to integrate with your permission handling system
        return withContext(Dispatchers.Main) {
            // Platform-specific permission request logic would go here
            // For now, return false to indicate permission needs to be handled elsewhere
            false
        }
    }

    actual suspend fun captureImage(): String? {
        return withContext(Dispatchers.IO) {
            // Platform-specific image capture logic would go here
            // This would typically integrate with CameraX or Camera2 API
            // For now, return null to indicate capture is not implemented
            null
        }
    }

    actual suspend fun getCameraConfiguration(): CameraConfiguration {
        return withContext(Dispatchers.IO) {
            val hasCamera = isCameraAvailable()

            CameraConfiguration(
                isAvailable = hasCamera,
                hasFlash = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH),
                hasAutoFocus = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS),
                supportedResolutions = listOf(
                    CameraResolution(1920, 1080),
                    CameraResolution(1280, 720),
                    CameraResolution(640, 480)
                ),
                defaultResolution = CameraResolution(1920, 1080),
                maxZoom = 4.0f,
                supportedFormats = listOf(ImageFormat.JPEG, ImageFormat.PNG),
                defaultFormat = ImageFormat.JPEG
            )
        }
    }
}