// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/EquipmentRecommendationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.IEquipmentRecommendationService
import com.x3squaredcircles.photography.domain.models.AstroTarget
import com.x3squaredcircles.photography.domain.models.UserEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.GenericEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.HourlyEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.CameraLensCombination
import com.x3squaredcircles.photography.domain.models.OptimalEquipmentSpecs
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
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
        return withContext(Dispatchers.Default) {
            try {
                val specs = getOptimalEquipmentSpecs(target)

                // Get user equipment
                val userCamerasResult = cameraBodyRepository.getUserCreatedAsync()
                val userLensesResult = lensRepository.getUserCreatedAsync()

                when {
                    userCamerasResult.isFailure -> return@withContext Result.failure("Failed to load user cameras: ${(userCamerasResult as Result.Failure).error}")
                    userLensesResult.isFailure -> return@withContext Result.failure("Failed to load user lenses: ${(userLensesResult as Result.Failure).error}")
                }

                val userCameras = (userCamerasResult as Result.Success).data
                val userLenses = (userLensesResult as Result.Success).data

                if (userCameras.isEmpty() && userLenses.isEmpty()) {
                    return@withContext Result.success(
                        UserEquipmentRecommendation(
                            target = target,
                            specs = specs,
                            hasUserEquipment = false,
                            summary = "No user equipment found. Consider getting equipment that meets the recommended specifications."
                        )
                    )
                }

                val combinations = mutableListOf<CameraLensCombination>()

                for (lens in userLenses) {
                    if (isLensCompatibleWithTarget(lens, specs)) {
                        val compatibleCameras = getCompatibleUserCameras(lens, userCameras)

                        for (camera in compatibleCameras) {
                            val combination = createCameraLensCombination(camera, lens, specs)
                            combinations.add(combination)
                        }
                    }
                }

                combinations.sortByDescending { it.matchScore }

                val hasOptimal = combinations.any { it.isOptimal }
                val recommended = combinations.take(3)
                val alternatives = combinations.drop(3)

                Result.success(
                    UserEquipmentRecommendation(
                        target = target,
                        specs = specs,
                        hasUserEquipment = true,
                        recommendedCombinations = recommended,
                        alternativeCombinations = alternatives,
                        hasOptimalEquipment = hasOptimal,
                        summary = generateRecommendationSummary(combinations, specs, target)
                    )
                )

            } catch (ex: Exception) {
                logger.e(ex) { "Failed to get user equipment recommendation" }
                Result.failure("Failed to analyze user equipment: ${ex.message}")
            }
        }
    }

    override suspend fun getHourlyEquipmentRecommendationsAsync(
        target: AstroTarget,
        predictionTimes: List<Instant>
    ): Result<List<HourlyEquipmentRecommendation>> {
        return withContext(Dispatchers.Default) {
            try {
                val userRecommendationResult = getUserEquipmentRecommendationAsync(target)

                when (userRecommendationResult) {
                    is Result.Failure -> return@withContext Result.failure(userRecommendationResult.error)
                    is Result.Success -> {
                        val userRecommendation = userRecommendationResult.data
                        val hourlyRecommendations = predictionTimes.map { time ->
                            HourlyEquipmentRecommendation(
                                predictionTime = time,
                                target = target,
                                hasUserEquipment = userRecommendation.hasUserEquipment,
                                bestCombination = userRecommendation.recommendedCombinations.firstOrNull(),
                                alternativeCombinations = userRecommendation.alternativeCombinations.take(2),
                                weatherAdjustments = "Check weather conditions for optimal settings",
                                settingsRecommendation = userRecommendation.specs.recommendedSettings,
                                notes = if (userRecommendation.hasOptimalEquipment) "Your equipment is excellent for this target" else "Consider equipment upgrades for better results"
                            )
                        }

                        Result.success(hourlyRecommendations)
                    }
                }
            } catch (ex: Exception) {
                logger.e(ex) { "Failed to get hourly equipment recommendations" }
                Result.failure("Failed to generate hourly recommendations: ${ex.message}")
            }
        }
    }

    override suspend fun getGenericRecommendationAsync(target: AstroTarget): Result<GenericEquipmentRecommendation> {
        return withContext(Dispatchers.Default) {
            try {
                val specs = getOptimalEquipmentSpecs(target)

                Result.success(
                    GenericEquipmentRecommendation(
                        target = target,
                        specs = specs,
                        lensRecommendation = generateGenericLensRecommendation(specs),
                        cameraRecommendation = generateGenericCameraRecommendation(specs),
                        shoppingList = generateShoppingList(specs, target)
                    )
                )
            } catch (ex: Exception) {
                logger.e(ex) { "Failed to get generic recommendation" }
                Result.failure("Failed to generate generic recommendation: ${ex.message}")
            }
        }
    }

    private fun getOptimalEquipmentSpecs(target: AstroTarget): OptimalEquipmentSpecs {
        return when (target) {
            AstroTarget.MOON -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 2000.0,
                optimalFocalLength = 600.0,
                maxAperture = 8.0,
                minIsoCapability = 100,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 100-400, f/8-f/11, 1/60s-1/250s",
                notes = "Moon photography benefits from longer focal lengths and stopped-down apertures for sharpness."
            )
            AstroTarget.PLANETS -> OptimalEquipmentSpecs(
                minFocalLength = 500.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 1200.0,
                maxAperture = 10.0,
                minIsoCapability = 400,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 800-1600, f/8-f/10, 1/30s-1/125s",
                notes = "Planetary photography requires very long focal lengths and high magnification."
            )
            AstroTarget.MILKY_WAY_CORE, AstroTarget.MILKY_WAY -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 1600,
                maxIsoCapability = 12800,
                recommendedSettings = "ISO 3200-6400, f/1.4-f/2.8, 15s-25s",
                notes = "Milky Way photography requires wide angles and fast apertures for light gathering."
            )
            AstroTarget.DEEP_SKY_OBJECTS, AstroTarget.DEEP_SKY -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 1600-3200, f/2.8-f/4, 30s-300s with tracking",
                notes = "Deep sky objects benefit from moderate telephoto lenses and tracking mounts.",
                tracking = true
            )
            AstroTarget.STAR_TRAILS -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 50.0,
                optimalFocalLength = 28.0,
                maxAperture = 5.6,
                minIsoCapability = 200,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 400-800, f/4-f/5.6, 15min-4hr total exposure",
                notes = "Star trails work well with wide to normal lenses and lower ISO for cleaner images."
            )
            AstroTarget.METEOR_SHOWERS -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 1600,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 3200-6400, f/2.8-f/4, 15s-30s",
                notes = "Meteor photography requires wide coverage and fast settings to capture brief events."
            )
            AstroTarget.SOLAR_ECLIPSE -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 1000.0,
                optimalFocalLength = 400.0,
                maxAperture = 8.0,
                minIsoCapability = 100,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 100-1600, f/8-f/11, 1/1000s-2s (requires solar filter)",
                notes = "Solar eclipse photography requires telephoto lenses and proper solar filtration for safety."
            )
            AstroTarget.LUNAR_ECLIPSE -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 800.0,
                optimalFocalLength = 400.0,
                maxAperture = 5.6,
                minIsoCapability = 400,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 800-3200, f/4-f/8, 1s-15s",
                notes = "Lunar eclipses require moderate telephoto lenses and adaptable settings for changing light."
            )
            AstroTarget.CONJUNCTIONS -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 400.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600-3200, f/2.8-f/5.6, 1s-30s",
                notes = "Planetary conjunctions benefit from moderate telephoto lenses to show both objects clearly."
            )
        }
    }

    private fun isLensCompatibleWithTarget(lens: LensDto, specs: OptimalEquipmentSpecs): Boolean {
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
            val compatibilitiesResult = compatibilityRepository.getByLensIdAsync(lens.id)

            when (compatibilitiesResult) {
                is Result.Success -> {
                    val compatibleCameraIds = compatibilitiesResult.data.map { it.cameraBodyId }.toSet()
                    userCameras.filter { camera -> camera.id in compatibleCameraIds }
                }
                is Result.Failure -> {
                    logger.w { "Failed to get compatibility for lens ${lens.id}: ${compatibilitiesResult.error}" }
                    emptyList()
                }
            }
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

        return "$recommendation. ${specs.recommendedSettings}"
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

    private fun generateGenericLensRecommendation(specs: OptimalEquipmentSpecs): String {
        val focalLengthDesc = if (specs.minFocalLength == specs.maxFocalLength) {
            "${specs.optimalFocalLength.toInt()}mm"
        } else {
            "${specs.minFocalLength.toInt()}-${specs.maxFocalLength.toInt()}mm"
        }

        val apertureDesc = when {
            specs.maxAperture <= 2.8 -> "fast aperture"
            specs.maxAperture <= 4.0 -> "moderate aperture"
            else -> "standard aperture"
        }

        return "Lens: $focalLengthDesc with f/${specs.maxAperture} $apertureDesc for optimal results"
    }

    private fun generateGenericCameraRecommendation(specs: OptimalEquipmentSpecs): String {
        return "Camera: Capable of ISO ${specs.minIsoCapability}-${specs.maxIsoCapability} with good noise performance"
    }

    private fun generateShoppingList(specs: OptimalEquipmentSpecs, target: AstroTarget): List<String> {
        val list = mutableListOf<String>()

        list.add(generateGenericLensRecommendation(specs))
        list.add(generateGenericCameraRecommendation(specs))
        list.add("Sturdy tripod for stability")
        list.add("Remote shutter release or intervalometer")

        if (target == AstroTarget.DEEP_SKY_OBJECTS || target == AstroTarget.STAR_TRAILS) {
            list.add("Star tracker mount for long exposures")
        }

        return list
    }
}