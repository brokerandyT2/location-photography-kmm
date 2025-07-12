// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/CameraBody.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents a camera body for photography equipment management.
 */
@Serializable
data class CameraBody(
    override val id: Int = 0,
    val name: String,
    val sensorType: String,
    val sensorWidth: Double,
    val sensorHeight: Double,
    val mountType: String,
    val isUserCreated: Boolean = false,
    val dateAdded: Long
) : Entity() {
    
    /**
     * Gets the sensor diagonal in millimeters.
     */
    val sensorDiagonal: Double
        get() = kotlin.math.sqrt(sensorWidth * sensorWidth + sensorHeight * sensorHeight)
    
    /**
     * Gets the aspect ratio of the sensor.
     */
    val aspectRatio: Double
        get() = sensorWidth / sensorHeight
    
    /**
     * Gets the sensor area in square millimeters.
     */
    val sensorArea: Double
        get() = sensorWidth * sensorHeight
    
    /**
     * Gets the crop factor compared to full frame (36x24mm).
     */
    val cropFactor: Double
        get() = 43.27 / sensorDiagonal // 43.27mm is full frame diagonal
    
    /**
     * Determines if this is a full frame sensor.
     */
    val isFullFrame: Boolean
        get() = sensorWidth >= 35.0 && sensorHeight >= 23.0
    
    /**
     * Gets the date added as a LocalDateTime in the current system timezone.
     */
    val dateAddedDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(dateAdded)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    companion object {
        /**
         * Creates a new CameraBody with the current timestamp.
         */
        fun create(
            name: String,
            sensorType: String,
            sensorWidth: Double,
            sensorHeight: Double,
            mountType: String,
            isUserCreated: Boolean = false
        ): CameraBody {
            require(name.isNotBlank()) { "Camera body name cannot be blank" }
            require(sensorWidth > 0) { "Sensor width must be positive" }
            require(sensorHeight > 0) { "Sensor height must be positive" }
            require(mountType.isNotBlank()) { "Mount type cannot be blank" }
            
            return CameraBody(
                name = name.trim(),
                sensorType = sensorType.trim(),
                sensorWidth = sensorWidth,
                sensorHeight = sensorHeight,
                mountType = mountType.trim(),
                isUserCreated = isUserCreated,
                dateAdded = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
    }
}