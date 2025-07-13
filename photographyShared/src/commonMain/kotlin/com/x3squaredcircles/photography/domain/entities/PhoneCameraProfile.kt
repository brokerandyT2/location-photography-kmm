// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/PhoneCameraProfile.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.Clock

/**
 * Represents a phone camera profile with calibrated focal length and field of view data.
 */
@Serializable
data class PhoneCameraProfile(
    override val id: Int = 0,
    val phoneModel: String,
    val mainLensFocalLength: Double,
    val mainLensFOV: Double,
    val ultraWideFocalLength: Double? = null,
    val telephotoFocalLength: Double? = null,
    val dateCalibrated: Long = Clock.System.now().toEpochMilliseconds(),
    val isActive: Boolean = true
) : Entity() {

    init {
        require(phoneModel.isNotBlank()) { "Phone model cannot be blank" }
        require(mainLensFocalLength > 0) { "Main lens focal length must be positive" }
        require(mainLensFOV > 0 && mainLensFOV < 180) { "Main lens FOV must be between 0 and 180 degrees" }
        ultraWideFocalLength?.let { require(it > 0) { "Ultra wide focal length must be positive" } }
        telephotoFocalLength?.let { require(it > 0) { "Telephoto focal length must be positive" } }
    }

    /**
     * Deactivates this profile.
     */
    fun deactivate(): PhoneCameraProfile {
        return copy(isActive = false)
    }

    /**
     * Activates this profile.
     */
    fun activate(): PhoneCameraProfile {
        return copy(isActive = true)
    }

    /**
     * Checks if the calibration is stale based on the provided maximum age.
     */
    fun isCalibrationStale(maxAgeMillis: Long): Boolean {
        return Clock.System.now().toEpochMilliseconds() - dateCalibrated > maxAgeMillis
    }

    /**
     * Updates the lens data for this profile.
     */
    fun updateLensData(
        newMainLensFocalLength: Double,
        newMainLensFOV: Double,
        newUltraWideFocalLength: Double? = ultraWideFocalLength,
        newTelephotoFocalLength: Double? = telephotoFocalLength
    ): PhoneCameraProfile {
        return copy(
            mainLensFocalLength = newMainLensFocalLength,
            mainLensFOV = newMainLensFOV,
            ultraWideFocalLength = newUltraWideFocalLength,
            telephotoFocalLength = newTelephotoFocalLength,
            dateCalibrated = Clock.System.now().toEpochMilliseconds()
        )
    }

    /**
     * Gets all available focal lengths for this phone.
     */
    fun getAvailableFocalLengths(): List<Double> {
        val lengths = mutableListOf(mainLensFocalLength)
        ultraWideFocalLength?.let { lengths.add(it) }
        telephotoFocalLength?.let { lengths.add(it) }
        return lengths.sorted()
    }

    /**
     * Checks if this phone has multiple lenses.
     */
    fun hasMultipleLenses(): Boolean {
        return ultraWideFocalLength != null || telephotoFocalLength != null
    }

    /**
     * Gets a display name for this profile.
     */
    fun getDisplayName(): String {
        val status = if (isActive) "" else " (Inactive)"
        val lensCount = getAvailableFocalLengths().size
        val lensDescription = when (lensCount) {
            1 -> "${mainLensFocalLength.toInt()}mm"
            2 -> "Dual lens"
            3 -> "Triple lens"
            else -> "$lensCount lenses"
        }
        return "$phoneModel - $lensDescription$status"
    }

    companion object {
        /**
         * Creates a new phone camera profile with validation.
         */
        fun create(
            phoneModel: String,
            mainLensFocalLength: Double,
            mainLensFOV: Double,
            ultraWideFocalLength: Double? = null,
            telephotoFocalLength: Double? = null
        ): PhoneCameraProfile {
            return PhoneCameraProfile(
                phoneModel = phoneModel.trim(),
                mainLensFocalLength = mainLensFocalLength,
                mainLensFOV = mainLensFOV,
                ultraWideFocalLength = ultraWideFocalLength,
                telephotoFocalLength = telephotoFocalLength
            )
        }
    }
}