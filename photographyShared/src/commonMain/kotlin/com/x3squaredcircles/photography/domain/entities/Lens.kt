// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Lens.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents a camera lens for photography equipment management.
 */
@Serializable
data class Lens(
    override val id: Int = 0,
    val minMM: Double,
    val maxMM: Double,
    val minFStop: Double,
    val maxFStop: Double,
    val isPrime: Boolean,
    val isUserCreated: Boolean = false,
    val nameForLens: String = "",
    val dateAdded: Long
) : Entity() {
    
    /**
     * Gets the focal length range as a string.
     */
    val focalLengthRange: String
        get() = if (isPrime) "${minMM.toInt()}mm" else "${minMM.toInt()}-${maxMM.toInt()}mm"
    
    /**
     * Gets the aperture range as a string.
     */
    val apertureRange: String
        get() = if (minFStop == maxFStop) "f/${minFStop}" else "f/${minFStop}-${maxFStop}"
    
    /**
     * Gets the display name for the lens.
     */
    val displayName: String
        get() = if (nameForLens.isNotBlank()) nameForLens else "$focalLengthRange $apertureRange"
    
    /**
     * Gets the zoom ratio (maxMM / minMM).
     */
    val zoomRatio: Double
        get() = if (isPrime) 1.0 else maxMM / minMM
    
    /**
     * Determines if this lens covers a specific focal length.
     */
    fun coversFocalLength(focalLength: Double): Boolean {
        return focalLength >= minMM && focalLength <= maxMM
    }
    
    /**
     * Determines if this lens has a wide angle capability (< 35mm).
     */
    val isWideAngle: Boolean
        get() = minMM < 35.0
    
    /**
     * Determines if this lens has telephoto capability (> 85mm).
     */
    val isTelephoto: Boolean
        get() = maxMM > 85.0
    
    /**
     * Determines if this lens is suitable for macro photography (based on naming).
     */
    val isMacro: Boolean
        get() = nameForLens.contains("macro", ignoreCase = true)
    
    /**
     * Gets the date added as a LocalDateTime in the current system timezone.
     */
    val dateAddedDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(dateAdded)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    companion object {
        /**
         * Creates a new prime lens with the current timestamp.
         */
        fun createPrime(
            focalLength: Double,
            maxAperture: Double,
            name: String = "",
            isUserCreated: Boolean = false
        ): Lens {
            require(focalLength > 0) { "Focal length must be positive" }
            require(maxAperture > 0) { "Max aperture must be positive" }
            
            return Lens(
                minMM = focalLength,
                maxMM = focalLength,
                minFStop = maxAperture,
                maxFStop = maxAperture,
                isPrime = true,
                isUserCreated = isUserCreated,
                nameForLens = name.trim(),
                dateAdded = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
        
        /**
         * Creates a new zoom lens with the current timestamp.
         */
        fun createZoom(
            minFocalLength: Double,
            maxFocalLength: Double,
            maxApertureWide: Double,
            maxApertureTele: Double,
            name: String = "",
            isUserCreated: Boolean = false
        ): Lens {
            require(minFocalLength > 0) { "Min focal length must be positive" }
            require(maxFocalLength > minFocalLength) { "Max focal length must be greater than min" }
            require(maxApertureWide > 0) { "Max aperture (wide) must be positive" }
            require(maxApertureTele > 0) { "Max aperture (tele) must be positive" }
            
            return Lens(
                minMM = minFocalLength,
                maxMM = maxFocalLength,
                minFStop = maxApertureWide,
                maxFStop = maxApertureTele,
                isPrime = false,
                isUserCreated = isUserCreated,
                nameForLens = name.trim(),
                dateAdded = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
    }
}