// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/AstroHourlyPredictionDisplayModel.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.Instant

data class AstroHourlyPredictionDisplayModel(
    // Header Properties
    val timeDisplay: String = "",
    val solarEventsDisplay: String = "",
    val qualityScore: Double = 0.0,
    val isOptimalTime: Boolean = false,

    // Event Collections
    val astroEvents: List<AstroEventDisplayModel> = emptyList(),
    val solarEvents: List<SolarEventDisplayModel> = emptyList(),
    val weatherConfidence: String = "",

    // Overall Assessment
    val overallQuality: String = "",
    val confidenceDisplay: String = "",
    val recommendations: String = "",

    // Original Domain Data
    val hour: Instant,
    val domainModel: AstroHourlyPrediction? = null,
    val solarEvent: String = "",
    val qualityDisplay: String = "",
    val qualityDescription: String = "",
    val weatherCloudCover: Double = 0.0,
    val weatherHumidity: Double = 0.0,
    val weatherWindSpeed: Double = 0.0,
    val weatherDescription: String = "",
    val weatherVisibility: Double = 0.0,
    val weatherDisplay: String = "",
    val weatherSuitability: String = ""
)

data class AstroEventDisplayModel(
    // Event Identity
    val targetName: String = "",
    val eventType: String = "",
    val qualityRank: Double = 0.0,

    // Position Information
    val azimuth: Double = 0.0,
    val altitude: Double = 0.0,

    // Timing Information
    val riseTime: Instant? = null,
    val setTime: Instant? = null,
    val optimalTime: Instant? = null,

    // Equipment Recommendations
    val recommendedLens: String = "",
    val recommendedCamera: String = "",
    val isUserEquipment: Boolean = false,
    val equipmentNote: String = "",

    // Photography Settings
    val suggestedAperture: String = "",
    val suggestedShutterSpeed: String = "",
    val suggestedISO: String = "",
    val focalLengthRecommendation: String = "",

    // Event-Specific Notes
    val photographyNotes: String = "",
    val difficultyLevel: String = "",
    val visibility: String = "",
    val cameraSettings: String = "",
    val recommendedEquipment: String = "",
    val notes: String = ""
) {
    val azimuthDisplay: String
        get() = "${azimuth.format(1)}°"

    val altitudeDisplay: String
        get() = "${altitude.format(1)}°"

    val isVisible: Boolean
        get() = altitude > 0

    val riseTimeDisplay: String
        get() = riseTime?.let { formatInstantToTime(it) } ?: "N/A"

    val setTimeDisplay: String
        get() = setTime?.let { formatInstantToTime(it) } ?: "N/A"

    val optimalTimeDisplay: String
        get() = optimalTime?.let { formatInstantToTime(it) } ?: "N/A"

    private fun formatInstantToTime(instant: Instant): String {
        val localTime = instant.toString() // Simplified - would need proper timezone conversion
        return localTime.substring(11, 16) // Extract HH:mm
    }

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }
}

data class SolarEventDisplayModel(
    // Event Identity
    val eventName: String = "",
    val eventType: String = "",
    val eventTime: Instant,

    // Position Information
    val sunAzimuth: Double = 0.0,
    val sunAltitude: Double = 0.0,

    // Light Quality
    val lightQuality: String = "",
    val colorTemperature: String = "",

    // Photography Impact
    val impactOnAstro: String = "",
    val conflictsWithAstro: Boolean = false
) {
    val sunAzimuthDisplay: String
        get() = "${sunAzimuth.format(1)}°"

    val sunAltitudeDisplay: String
        get() = "${sunAltitude.format(1)}°"

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }
}

data class AstroHourlyPrediction(
    val hour: Instant,
    val qualityScore: Double = 0.0,
    val isOptimalTime: Boolean = false,
    val solarEvents: String = "",
    val astroEvents: List<String> = emptyList(),
    val weatherConditions: String = "",
    val recommendations: String = ""
)