// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/WeatherImpactFactor.kt
package com.x3squaredcircles.photography.models

data class WeatherImpactFactor(
    val cloudCoverReduction: Double = 0.0,
    val precipitationReduction: Double = 0.0,
    val humidityReduction: Double = 0.0,
    val visibilityReduction: Double = 0.0,
    val overallLightReductionFactor: Double = 1.0,
    val confidenceImpact: Double = 0.0
)