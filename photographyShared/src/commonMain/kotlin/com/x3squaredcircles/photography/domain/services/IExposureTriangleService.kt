// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IExposureTriangleService.kt
package com.x3squaredcircles.photography.domain.services

/**
 * Service for performing calculations on the exposure triangle (shutter speed, aperture, ISO)
 */
interface IExposureTriangleService {

    /**
     * Calculates the required shutter speed to maintain equivalent exposure
     * @param baseShutterSpeed Current shutter speed
     * @param baseAperture Current aperture (f-stop)
     * @param baseIso Current ISO
     * @param targetAperture Target aperture (f-stop)
     * @param targetIso Target ISO
     * @param scale Scale factor: 1 for full stops, 2 for half stops, 3 for third stops
     * @param evCompensation EV compensation value (positive = brighter, negative = darker)
     * @return The calculated shutter speed as a string
     */
    fun calculateShutterSpeed(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetAperture: String,
        targetIso: String,
        scale: Int,
        evCompensation: Double = 0.0
    ): String

    /**
     * Calculates the required aperture to maintain equivalent exposure
     * @param baseShutterSpeed Current shutter speed
     * @param baseAperture Current aperture (f-stop)
     * @param baseIso Current ISO
     * @param targetShutterSpeed Target shutter speed
     * @param targetIso Target ISO
     * @param scale Scale factor: 1 for full stops, 2 for half stops, 3 for third stops
     * @param evCompensation EV compensation value (positive = brighter, negative = darker)
     * @return The calculated aperture as a string
     */
    fun calculateAperture(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetShutterSpeed: String,
        targetIso: String,
        scale: Int,
        evCompensation: Double = 0.0
    ): String

    /**
     * Calculates the required ISO to maintain equivalent exposure
     * @param baseShutterSpeed Current shutter speed
     * @param baseAperture Current aperture (f-stop)
     * @param baseIso Current ISO
     * @param targetShutterSpeed Target shutter speed
     * @param targetAperture Target aperture (f-stop)
     * @param scale Scale factor: 1 for full stops, 2 for half stops, 3 for third stops
     * @param evCompensation EV compensation value (positive = brighter, negative = darker)
     * @return The calculated ISO as a string
     */
    fun calculateIso(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetShutterSpeed: String,
        targetAperture: String,
        scale: Int,
        evCompensation: Double = 0.0
    ): String
}