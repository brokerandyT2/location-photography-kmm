// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/HourlyLightPrediction.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class HourlyLightPrediction(
    val dateTime: Instant,
    val predictedEV: Double,
    val evConfidenceMargin: Double,
    val confidenceLevel: Double,
    val confidenceReason: String = "",
    val suggestedSettings: ExposureTriangle = ExposureTriangle(),
    val lightQuality: LightCharacteristics = LightCharacteristics(),
    val recommendations: List<String> = emptyList(),
    val isOptimalForPhotography: Boolean = false,
    val sunPosition: SunPositionDto = SunPositionDto(),
    val isMoonVisible: Boolean = false
)

data class ExposureTriangle(
    val aperture: String = "",
    val shutterSpeed: String = "",
    val iso: String = ""
) {
    val formattedSettings: String get() = "$aperture, $shutterSpeed, $iso"
}

data class LightCharacteristics(
    val colorTemperature: Double = 0.0,
    val softnessFactor: Double = 0.0,
    val shadowHarshness: ShadowIntensity = ShadowIntensity.MEDIUM,
    val optimalFor: String = "",
    val directionalityFactor: Double = 0.0
)

data class SunPositionDto(
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    val dateTime: Instant? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: Double = 1.0
) {
    val isAboveHorizon: Boolean get() = elevation > 0
}

enum class ShadowIntensity {
    NONE,
    SOFT,
    MEDIUM,
    HARD,
    VERY_HARD
}