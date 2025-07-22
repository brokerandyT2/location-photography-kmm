// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/HourlyPredictionDisplayModel.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone

data class HourlyPredictionDisplayModel(
    val time: Instant,
    val deviceTimeDisplay: String = "",
    val locationTimeDisplay: String = "",
    val predictedEV: Double = 0.0,
    val evConfidenceMargin: Double = 0.0,
    val suggestedAperture: String = "",
    val suggestedShutterSpeed: String = "",
    val suggestedISO: String = "",
    val confidenceLevel: Double = 0.0,
    val lightQuality: String = "",
    val colorTemperature: Double = 0.0,
    val recommendations: String = "",
    val isOptimalTime: Boolean = false,
    val timeFormat: String = "HH:mm",
    val shootingQualityScore: Double = 0.0,

    // Weather integration properties
    val weatherDescription: String = "",
    val cloudCover: Int = 0,
    val precipitationProbability: Double = 0.0,
    val windInfo: String = "",
    val uvIndex: Double = 0.0,
    val humidity: Int = 0,

    // Equipment recommendation properties
    val equipmentRecommendation: HourlyEquipmentRecommendation? = null,
    val equipmentRecommendationText: String = "",
    val userCameraLensRecommendation: String = "",
    val equipmentStrengths: List<String> = emptyList(),
    val timePeriod: String = "",
    val weatherImpactLevel: String = ""
) {
    val hasUserEquipment: Boolean
        get() = equipmentRecommendation?.hasUserEquipment ?: false

    val equipmentMatchScore: String
        get() {
            val score = equipmentRecommendation?.recommendedCombination?.matchScore ?: 0.0
            return when {
                score >= 85 -> "Excellent Match"
                score >= 70 -> "Good Match"
                score >= 50 -> "Fair Match"
                else -> "Poor Match"
            }
        }

    val formattedPrediction: String
        get() = "EV ${predictedEV.format(1)} • ${lightQuality}"

    val confidenceDisplay: String
        get() = "${(confidenceLevel * 100).toInt()}% confidence"

    val compactSummary: String
        get() = "$formattedPrediction • $confidenceDisplay • $lightQuality"

    val detailedEquipmentRecommendation: String
        get() {
            var recommendation = "Best for: $lightQuality"

            if (hasUserEquipment) {
                recommendation += "\n📷 Equipment: $userCameraLensRecommendation"
                if (equipmentStrengths.isNotEmpty()) {
                    recommendation += "\n✓ ${equipmentStrengths.take(2).joinToString(", ")}"
                }
            } else {
                recommendation += "\n📷 Recommended: $equipmentRecommendationText"
            }

            if (recommendations.isNotEmpty()) {
                recommendation += "\nTips: $recommendations"
            }
            if (colorTemperature > 0) {
                recommendation += "\nColor: ${colorTemperature.toInt()}K"
            }
            return recommendation
        }

    fun getTipMatchingCriteria(): CameraTipCriteria {
        return CameraTipCriteria(
            aperture = suggestedAperture,
            shutterSpeed = suggestedShutterSpeed,
            iso = suggestedISO,
            lightCondition = lightQuality,
            timePeriod = timePeriod,
            optimalForPortraits = lightQuality.contains("soft", ignoreCase = true) ||
                    lightQuality.contains("golden", ignoreCase = true),
            optimalForLandscapes = isOptimalTime && (lightQuality.contains("golden", ignoreCase = true) ||
                    lightQuality.contains("blue", ignoreCase = true)),
            weatherCondition = weatherImpactLevel,
            equipmentRecommendation = detailedEquipmentRecommendation
        )
    }

    fun getFormattedTime(format: String): String {
        val localTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
        return formatTime(localTime.hour, localTime.minute, format)
    }

    fun getFormattedDate(format: String): String {
        val localTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localTime.year}-${localTime.monthNumber.toString().padStart(2, '0')}-${localTime.dayOfMonth.toString().padStart(2, '0')}"
    }

    private fun formatTime(hour: Int, minute: Int, format: String): String {
        return when (format) {
            "HH:mm" -> "%02d:%02d".format(hour, minute)
            "h:mm tt" -> {
                val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                val amPm = if (hour < 12) "AM" else "PM"
                "%d:%02d %s".format(h, minute, amPm)
            }
            else -> "%02d:%02d".format(hour, minute)
        }
    }

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }
}

data class HourlyEquipmentRecommendation(
    val hasUserEquipment: Boolean = false,
    val recommendation: String = "",
    val recommendedCombination: CameraLensCombination? = null
)

data class CameraLensCombination(
    val camera: CameraBodyInfo,
    val lens: LensInfo,
    val matchScore: Double = 0.0
)

data class CameraBodyInfo(
    val name: String = ""
)

data class LensInfo(
    val nameForLens: String = ""
)