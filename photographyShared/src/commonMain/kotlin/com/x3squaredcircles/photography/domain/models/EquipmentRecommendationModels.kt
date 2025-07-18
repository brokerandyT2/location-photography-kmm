// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/EquipmentRecommendationModels.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import kotlinx.datetime.Instant

enum class AstroTarget {
    MOON,
    PLANETS,
    MILKY_WAY_CORE,
    DEEP_SKY_OBJECTS,
    STAR_TRAILS,
    METEOR_SHOWERS,
    SOLAR_ECLIPSE,
    LUNAR_ECLIPSE,
    CONJUNCTIONS,
    DEEP_SKY,
    MILKY_WAY
}

data class OptimalEquipmentSpecs(
    val minFocalLength: Double,
    val maxFocalLength: Double,
    val optimalFocalLength: Double,
    val maxAperture: Double,
    val minIsoCapability: Int,
    val maxIsoCapability: Int,
    val recommendedSettings: String,
    val notes: String,
    val tracking: Boolean = false,
    val tripod: Boolean = true
)

data class CameraLensCombination(
    val camera: CameraBodyDto,
    val lens: LensDto,
    val matchScore: Double,
    val isOptimal: Boolean,
    val recommendationReason: String,
    val strengths: List<String>,
    val limitations: List<String>,
    val detailedRecommendation: String,
    val displayText: String = "${camera.name} + ${lens.nameForLens}"
)

data class UserEquipmentRecommendation(
    val target: AstroTarget,
    val specs: OptimalEquipmentSpecs,
    val hasUserEquipment: Boolean,
    val recommendedCombinations: List<CameraLensCombination> = emptyList(),
    val alternativeCombinations: List<CameraLensCombination> = emptyList(),
    val hasOptimalEquipment: Boolean = false,
    val summary: String = ""
)

data class HourlyEquipmentRecommendation(
    val predictionTime: Instant,
    val target: AstroTarget,
    val hasUserEquipment: Boolean,
    val bestCombination: CameraLensCombination? = null,
    val alternativeCombinations: List<CameraLensCombination> = emptyList(),
    val weatherAdjustments: String = "",
    val settingsRecommendation: String = "",
    val notes: String = ""
)

data class GenericEquipmentRecommendation(
    val target: AstroTarget,
    val specs: OptimalEquipmentSpecs,
    val lensRecommendation: String = "",
    val cameraRecommendation: String = "",
    val shoppingList: List<String> = emptyList()
)

// Data transfer objects referenced in combinations
data class CameraBodyDto(
    val id: Int,
    val model: String,
    val manufacturer: String,
    val sensorWidth: Double,
    val sensorHeight: Double,
    val isUserCreated: Boolean = false
)

data class LensDto(
    val id: Int,
    val model: String,
    val manufacturer: String,
    val minMM: Double,
    val maxMM: Double? = null,
    val minFStop: Double? = null,
    val maxFStop: Double,
    val isPrime: Boolean = maxMM == null || kotlin.math.abs((maxMM ?: minMM) - minMM) < 0.1,
    val isUserCreated: Boolean = false
)