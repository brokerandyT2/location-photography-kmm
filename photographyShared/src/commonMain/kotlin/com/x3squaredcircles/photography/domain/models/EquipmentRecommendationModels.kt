// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/EquipmentRecommendationModels.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

enum class AstroTarget {
    MOON,
    PLANETS,
    MILKY_WAY_CORE,
    DEEP_SKY_OBJECTS,
    STAR_TRAILS,
    METEOR_SHOWERS,
    SOLAR_ECLIPSE,
    LUNAR_ECLIPSE
}

data class UserEquipmentRecommendation(
    val target: AstroTarget,
    val hasUserEquipment: Boolean,
    val recommendedCombinations: List<EquipmentCombination> = emptyList(),
    val alternativeCombinations: List<EquipmentCombination> = emptyList(),
    val missingEquipment: List<String> = emptyList(),
    val upgradeRecommendations: List<String> = emptyList()
)

data class HourlyEquipmentRecommendation(
    val predictionTime: Instant,
    val target: AstroTarget,
    val hasUserEquipment: Boolean,
    val recommendedCombination: EquipmentCombination? = null,
    val recommendation: String = "",
    val genericRecommendation: String = ""
)

data class GenericEquipmentRecommendation(
    val target: AstroTarget,
    val specs: OptimalEquipmentSpecs,
    val lensRecommendation: String = "",
    val cameraRecommendation: String = "",
    val shoppingList: List<String> = emptyList()
)

data class EquipmentCombination(
    val camera: String,
    val lens: String,
    val displayText: String,
    val score: Double,
    val notes: String = ""
)

data class OptimalEquipmentSpecs(
    val minFocalLength: Double,
    val maxFocalLength: Double,
    val maxAperture: Double,
    val minIsoCapability: Int,
    val tracking: Boolean,
    val tripod: Boolean,
    val notes: String = ""
)