// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/LensCameraCompatibility.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents the compatibility relationship between a lens and camera body.
 */
@Serializable
data class LensCameraCompatibility(
    override val id: Int = 0,
    val lensId: Int,
    val cameraBodyId: Int,
    val dateAdded: Long
) : Entity() {
    
    /**
     * Gets the date added as a LocalDateTime in the current system timezone.
     */
    val dateAddedDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(dateAdded)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets a unique compatibility key for this lens-camera combination.
     */
    val compatibilityKey: String
        get() = "${lensId}_${cameraBodyId}"
    
    /**
     * Validates that this compatibility record has valid IDs.
     */
    val isValid: Boolean
        get() = lensId > 0 && cameraBodyId > 0
    
    companion object {
        /**
         * Creates a new LensCameraCompatibility with the current timestamp.
         */
        fun create(lensId: Int, cameraBodyId: Int): LensCameraCompatibility {
            require(lensId > 0) { "Lens ID must be positive" }
            require(cameraBodyId > 0) { "Camera body ID must be positive" }
            
            return LensCameraCompatibility(
                lensId = lensId,
                cameraBodyId = cameraBodyId,
                dateAdded = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
        
        /**
         * Creates multiple compatibility records for a lens with multiple camera bodies.
         */
        fun createForLens(lensId: Int, cameraBodyIds: List<Int>): List<LensCameraCompatibility> {
            require(lensId > 0) { "Lens ID must be positive" }
            require(cameraBodyIds.isNotEmpty()) { "Camera body IDs cannot be empty" }
            require(cameraBodyIds.all { it > 0 }) { "All camera body IDs must be positive" }
            
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            return cameraBodyIds.map { cameraBodyId ->
                LensCameraCompatibility(
                    lensId = lensId,
                    cameraBodyId = cameraBodyId,
                    dateAdded = now
                )
            }
        }
        
        /**
         * Creates multiple compatibility records for a camera body with multiple lenses.
         */
        fun createForCameraBody(cameraBodyId: Int, lensIds: List<Int>): List<LensCameraCompatibility> {
            require(cameraBodyId > 0) { "Camera body ID must be positive" }
            require(lensIds.isNotEmpty()) { "Lens IDs cannot be empty" }
            require(lensIds.all { it > 0 }) { "All lens IDs must be positive" }
            
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            return lensIds.map { lensId ->
                LensCameraCompatibility(
                    lensId = lensId,
                    cameraBodyId = cameraBodyId,
                    dateAdded = now
                )
            }
        }
    }
}