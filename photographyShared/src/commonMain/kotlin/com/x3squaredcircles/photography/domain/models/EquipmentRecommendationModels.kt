// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/EquipmentRecommendationModels.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import kotlinx.datetime.Instant

    enum class AstroTarget {
        // General Categories
        Moon,
        Planets,
        MilkyWayCore,
        DeepSkyObjects,
        StarTrails,
        Comets,
        MeteorShowers,
        PolarAlignment,
        Constellations,
        NorthernLights,

        // Individual Planets
        Mercury,
        Venus,
        Mars,
        Jupiter,
        Saturn,
        Uranus,
        Neptune,
        Pluto,

        // Specific Meteor Showers
        Quadrantids,
        EtaAquariids,
        Leonids,
        Geminids,
        Perseids,

        // Individual Constellations (legacy naming)
        Leo,
        Scorpius,
        Cygnus,
        BigDipper,
        Cassiopeia,
        Orion,

        // Individual Constellations (prefixed naming)
        Constellation_Sagittarius,
        Constellation_Orion,
        Constellation_Cassiopeia,
        Constellation_UrsaMajor,
        Constellation_Cygnus,
        Constellation_Scorpius,
        Constellation_Leo,

        // Specific Deep Sky Objects - Messier Catalog
        M31_Andromeda,
        M42_Orion,
        M51_Whirlpool,
        M13_Hercules,
        M27_Dumbbell,
        M57_Ring,
        M81_Bodes,
        M104_Sombrero,

        // Other Deep Sky Objects (legacy naming)
        CrabNebula,
        LagoonNebula,
        EagleNebula,
        RingNebula,
        WhirlpoolGalaxy,
        Pleiades,
        OrionNebula,
        AndromedaGalaxy,

        // Special Targets
        ISS,
        DEEP_SKY_OBJECTS,
        STAR_TRAILS,


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