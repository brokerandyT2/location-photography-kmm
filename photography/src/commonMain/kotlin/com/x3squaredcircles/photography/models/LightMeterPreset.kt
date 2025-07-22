// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/LightMeterPreset.kt
package com.x3squaredcircles.photography.models

data class LightMeterPreset(
    val predictedEV: Double = 0.0,
    val suggestedAperture: String = "",
    val suggestedShutterSpeed: String = "",
    val suggestedISO: String = "",
    val expectedLightLevel: Double = 0.0,
    val optimalForPhotography: Boolean = false,
    val confidenceLevel: Double = 0.0,
    val equipmentRecommendation: String = ""
)