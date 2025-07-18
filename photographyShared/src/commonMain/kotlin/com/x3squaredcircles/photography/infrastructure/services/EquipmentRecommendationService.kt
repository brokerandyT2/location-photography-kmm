// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/EquipmentRecommendationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.application.services.IEquipmentRecommendationService
import com.x3squaredcircles.photography.domain.models.AstroTarget
import com.x3squaredcircles.photography.domain.models.UserEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.GenericEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.HourlyEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.CameraLensCombination
import com.x3squaredcircles.photography.domain.models.OptimalEquipmentSpecs
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.math.*

class EquipmentRecommendationService(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IEquipmentRecommendationService {

    override suspend fun getUserEquipmentRecommendationAsync(
        target: AstroTarget
    ): Result<UserEquipmentRecommendation> {
        return try {
            val specs = getOptimalEquipmentSpecs(target)

            val userCameras = cameraBodyRepository.getUserCreatedAsync()
            val userLenses = lensRepository.getUserCreatedAsync()

            if (userCameras.isEmpty() || userLenses.isEmpty()) {
                return Result.failure("Failed to load user equipment")
            }

            val matchingLenses = withContext(Dispatchers.Default) {
                findMatchingLenses(userLenses, specs)
            }

            val combinations = mutableListOf<CameraLensCombination>()

            for (lens in matchingLenses) {
                val compatibleCameras = getCompatibleUserCameras(lens, userCameras)

                for (camera in compatibleCameras) {
                    val combination = createCameraLensCombination(camera, lens, specs)
                    combinations.add(combination)
                }
            }

            val orderedCombinations = combinations.sortedByDescending { it.matchScore }

            val recommendation = UserEquipmentRecommendation(
                target = target,
                targetSpecs = specs,
                recommendedCombinations = orderedCombinations.filter { it.matchScore >= 70 },
                alternativeCombinations = orderedCombinations.filter { it.matchScore in 40.0..69.9 },
                hasOptimalEquipment = orderedCombinations.any { it.isOptimal },
                summary = generateRecommendationSummary(orderedCombinations, specs, target)
            )

            Result.success(recommendation)
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting user equipment recommendation for $target" }
            Result.failure("Error getting user equipment recommendation: ${ex.message}")
        }
    }

    override suspend fun getHourlyEquipmentRecommendationsAsync(
        target: AstroTarget,
        predictionTimes: List<Instant>
    ): Result<List<HourlyEquipmentRecommendation>> {
        return try {
            val hourlyRecommendations = mutableListOf<HourlyEquipmentRecommendation>()
            val specs = getOptimalEquipmentSpecs(target)

            for (time in predictionTimes) {
                val hourlySpec = adjustSpecsForTime(specs, time)
                val userRecommendation = getUserEquipmentRecommendationAsync(target)

                val hourlyRecommendation = if (userRecommendation.isSuccess) {
                    val recommendation = userRecommendation.getOrThrow()
                    HourlyEquipmentRecommendation(
                        time = time,
                        target = target,
                        bestCombination = recommendation.recommendedCombinations.firstOrNull(),
                        alternativeCombinations = recommendation.alternativeCombinations.take(2),
                        weatherAdjustments = getWeatherAdjustments(time),
                        settingsRecommendation = hourlySpec.recommendedSettings,
                        notes = generateHourlyNotes(time, target)
                    )
                } else {
                    HourlyEquipmentRecommendation(
                        time = time,
                        target = target,
                        bestCombination = null,
                        alternativeCombinations = emptyList(),
                        weatherAdjustments = getWeatherAdjustments(time),
                        settingsRecommendation = hourlySpec.recommendedSettings,
                        notes = "No suitable user equipment found"
                    )
                }

                hourlyRecommendations.add(hourlyRecommendation)
            }

            Result.success(hourlyRecommendations)
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting hourly equipment recommendations" }
            Result.failure("Error getting hourly equipment recommendations: ${ex.message}")
        }
    }

    override suspend fun getGenericRecommendationAsync(
        target: AstroTarget
    ): Result<GenericEquipmentRecommendation> {
        return try {
            val specs = getOptimalEquipmentSpecs(target)

            val recommendation = GenericEquipmentRecommendation(
                target = target,
                targetSpecs = specs,
                lensRecommendation = generateGenericLensRecommendation(specs),
                cameraRecommendation = generateGenericCameraRecommendation(specs),
                shoppingList = generateShoppingList(specs, target)
            )

            Result.success(recommendation)
        } catch (ex: Exception) {
            logger.e(ex) { "Error generating generic recommendation for $target" }
            Result.failure("Error generating generic recommendation: ${ex.message}")
        }
    }

    private fun getOptimalEquipmentSpecs(target: AstroTarget): OptimalEquipmentSpecs {
        return when (target) {
            AstroTarget.DEEP_SKY -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 400.0,
                maxAperture = 4.0,
                recommendedSettings = "ISO 1600, f/4, 60-120 seconds",
                notes = "Deep sky objects require long focal lengths for detail"
            )
            AstroTarget.MILKY_WAY -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                recommendedSettings = "ISO 3200, f/2.8, 15-30 seconds",
                notes = "Wide angle lenses capture the full arc of the Milky Way"
            )
            AstroTarget.PLANETS -> OptimalEquipmentSpecs(
                minFocalLength = 600.0,
                maxFocalLength = 2000.0,
                optimalFocalLength = 1000.0,
                maxAperture = 8.0,
                recommendedSettings = "ISO 800, f/8, 1/60 - 1/250 second",
                notes = "Long telephoto lenses reveal planetary details"
            )
            AstroTarget.MOON -> OptimalEquipmentSpecs(
                minFocalLength = 300.0,
                maxFocalLength = 1000.0,
                optimalFocalLength = 600.0,
                maxAperture = 8.0,
                recommendedSettings = "ISO 200, f/8, 1/125 - 1/500 second",
                notes = "Medium telephoto captures lunar surface details"
            )
            AstroTarget.STAR_TRAILS -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 50.0,
                optimalFocalLength = 24.0,
                maxAperture = 4.0,
                recommendedSettings = "ISO 400, f/4, 30 minutes - 2 hours",
                notes = "Wide angles capture circular star trail patterns"
            )
            AstroTarget.CONJUNCTIONS -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 300.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                recommendedSettings = "ISO 800, f/4, 1-10 seconds",
                notes = "Medium telephoto balances field of view with detail"
            )
            else -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 300.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                recommendedSettings = "ISO 1600, f/2.8, 15-30 seconds",
                notes = "General astrophotography setup"
            )
        }
    }

    private fun findMatchingLenses(userLenses: List<LensDto>, specs: OptimalEquipmentSpecs): List<LensDto> {
        return userLenses.filter { lens ->
            isLensMatchingSpecs(lens, specs)
        }
    }

    private fun isLensMatchingSpecs(lens: LensDto, specs: OptimalEquipmentSpecs): Boolean {
        val focalLengthMatch = if (lens.isPrime) {
            lens.minMM >= specs.minFocalLength && lens.minMM <= specs.maxFocalLength
        } else {
            val lensMaxMM = lens.maxMM ?: lens.minMM
            !(lensMaxMM < specs.minFocalLength || lens.minMM > specs.maxFocalLength)
        }

        val apertureMatch = lens.maxFStop <= specs.maxAperture

        return focalLengthMatch && apertureMatch
    }

    private suspend fun getCompatibleUserCameras(
        lens: LensDto,
        userCameras: List<CameraBodyDto>
    ): List<CameraBodyDto> {
        return try {
            val compatibilities = compatibilityRepository.getByLensIdAsync(lens.id)

            if (compatibilities.isEmpty()) {
                return emptyList()
            }

            val compatibleCameraIds = compatibilities.map { it.cameraBodyId }.toSet()
            userCameras.filter { camera -> camera.id in compatibleCameraIds }
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting compatible cameras for lens ${lens.id}" }
            emptyList()
        }
    }

    private fun createCameraLensCombination(
        camera: CameraBodyDto,
        lens: LensDto,
        specs: OptimalEquipmentSpecs
    ): CameraLensCombination {
        val matchScore = calculateMatchScore(camera, lens, specs)
        val strengths = identifyStrengths(camera, lens, specs)
        val limitations = identifyLimitations(camera, lens, specs)

        return CameraLensCombination(
            camera = camera,
            lens = lens,
            matchScore = matchScore,
            isOptimal = matchScore >= 85,
            recommendationReason = generateRecommendationText(camera, lens, specs, matchScore),
            strengths = strengths,
            limitations = limitations,
            detailedRecommendation = generateRecommendationText(camera, lens, specs, matchScore)
        )
    }

    private fun calculateMatchScore(camera: CameraBodyDto, lens: LensDto, specs: OptimalEquipmentSpecs): Double {
        val focalScore = calculateFocalLengthScore(lens, specs) * 0.4
        val apertureScore = calculateApertureScore(lens, specs) * 0.3
        val sensorScore = calculateSensorScore(camera, specs) * 0.2
        val userBonus = if (camera.isUserCreated) 0.05 else 0.0

        val score = (focalScore + apertureScore + sensorScore + userBonus) * 100
        return score.coerceIn(0.0, 100.0)
    }

    private fun calculateFocalLengthScore(lens: LensDto, specs: OptimalEquipmentSpecs): Double {
        return if (lens.isPrime) {
            val distance = abs(lens.minMM - specs.optimalFocalLength)
            val tolerance = (specs.maxFocalLength - specs.minFocalLength) / 2
            maxOf(0.0, 1 - (distance / tolerance))
        } else {
            val lensMaxMM = lens.maxMM ?: lens.minMM
            val overlap = minOf(lensMaxMM, specs.maxFocalLength) - maxOf(lens.minMM, specs.minFocalLength)
            val targetRange = specs.maxFocalLength - specs.minFocalLength
            maxOf(0.0, overlap / targetRange)
        }
    }

    private fun calculateApertureScore(lens: LensDto, specs: OptimalEquipmentSpecs): Double {
        return if (lens.maxFStop <= specs.maxAperture) {
            1.0
        } else {
            val apertureDeficit = lens.maxFStop - specs.maxAperture
            maxOf(0.0, 1 - (apertureDeficit / 2.0))
        }
    }

    private fun calculateSensorScore(camera: CameraBodyDto, specs: OptimalEquipmentSpecs): Double {
        val sensorArea = camera.sensorWidth * camera.sensorHeight
        val fullFrameArea = 36.0 * 24.0
        return minOf(1.0, sensorArea / fullFrameArea)
    }

    private fun identifyStrengths(camera: CameraBodyDto, lens: LensDto, specs: OptimalEquipmentSpecs): List<String> {
        val strengths = mutableListOf<String>()

        if (lens.maxFStop <= specs.maxAperture) {
            strengths.add("Fast aperture of f/${lens.maxFStop} excellent for light gathering")
        }

        val userFocalLength = if (lens.isPrime) lens.minMM else (lens.minMM + (lens.maxMM ?: lens.minMM)) / 2
        if (userFocalLength >= specs.minFocalLength && userFocalLength <= specs.maxFocalLength) {
            strengths.add("Optimal focal length of ${userFocalLength}mm for target")
        }

        if (camera.sensorWidth * camera.sensorHeight >= 800) {
            strengths.add("Large sensor provides excellent light gathering")
        }

        return strengths
    }

    private fun identifyLimitations(camera: CameraBodyDto, lens: LensDto, specs: OptimalEquipmentSpecs): List<String> {
        val limitations = mutableListOf<String>()

        if (lens.maxFStop > specs.maxAperture) {
            limitations.add("Slower aperture f/${lens.maxFStop} vs recommended f/${specs.maxAperture}")
        }

        val userFocalLength = if (lens.isPrime) lens.minMM else (lens.minMM + (lens.maxMM ?: lens.minMM)) / 2
        if (userFocalLength < specs.minFocalLength) {
            limitations.add("Too wide at ${userFocalLength}mm, recommended minimum ${specs.minFocalLength}mm")
        } else if (userFocalLength > specs.maxFocalLength) {
            limitations.add("Too long at ${userFocalLength}mm, recommended maximum ${specs.maxFocalLength}mm")
        }

        return limitations
    }

    private fun generateRecommendationText(camera: CameraBodyDto, lens: LensDto, specs: OptimalEquipmentSpecs, matchScore: Double): String {
        val recommendation = when {
            matchScore >= 85 -> "Excellent equipment combination for this target"
            matchScore >= 70 -> "Very good equipment combination"
            else -> "This combination can work for ${getTargetDescription(specs)} with some compromises"
        }

        return "$recommendation ${specs.recommendedSettings}"
    }

    private fun getTargetDescription(specs: OptimalEquipmentSpecs): String {
        return specs.notes.split('.').firstOrNull() ?: "astrophotography"
    }

    private fun generateRecommendationSummary(combinations: List<CameraLensCombination>, specs: OptimalEquipmentSpecs, target: AstroTarget): String {
        return when {
            combinations.isEmpty() -> "No compatible equipment found for $target. Consider equipment that meets the specifications."
            combinations.any { it.isOptimal } -> "You have excellent equipment for $target! Use your best combination for optimal results."
            combinations.any { it.matchScore >= 70 } -> "You have good equipment for $target. Your setup will work well with minor adjustments."
            else -> "Your current equipment can work for $target with some limitations. Consider upgrading for better results."
        }
    }

    private fun adjustSpecsForTime(specs: OptimalEquipmentSpecs, time: Instant): OptimalEquipmentSpecs {
        return specs
    }

    private fun getWeatherAdjustments(time: Instant): List<String> {
        return listOf("Monitor atmospheric conditions", "Adjust ISO based on sky transparency")
    }

    private fun generateHourlyNotes(time: Instant, target: AstroTarget): String {
        return "Check atmospheric conditions and adjust settings accordingly"
    }

    private fun generateGenericLensRecommendation(specs: OptimalEquipmentSpecs): String {
        val apertureDesc = when {
            specs.maxAperture <= 2.0 -> "fast"
            specs.maxAperture <= 4.0 -> "moderate"
            else -> "standard"
        }

        return "${specs.minFocalLength.toInt()}-${specs.maxFocalLength.toInt()}mm f/${specs.maxAperture} $apertureDesc lens"
    }

    private fun generateGenericCameraRecommendation(specs: OptimalEquipmentSpecs): String {
        val minIso = when (specs.maxAperture) {
            in 0.0..2.0 -> 800
            in 2.1..4.0 -> 1600
            else -> 3200
        }
        val maxIso = minIso * 4

        return "Camera with good high ISO performance (ISO $minIso-$maxIso)"
    }

    private fun generateShoppingList(specs: OptimalEquipmentSpecs, target: AstroTarget): List<String> {
        return listOf(
            "Lens: ${generateGenericLensRecommendation(specs)}",
            "Camera: ${generateGenericCameraRecommendation(specs)}",
            "Sturdy tripod for stability",
            "Remote shutter release or intervalometer",
            "Red flashlight for night vision preservation"
        )
    }
}