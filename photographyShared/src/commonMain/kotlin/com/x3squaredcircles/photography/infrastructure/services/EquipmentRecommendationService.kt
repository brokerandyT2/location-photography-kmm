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
            AstroTarget.Moon -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 2000.0,
                optimalFocalLength = 600.0,
                maxAperture = 8.0,
                minIsoCapability = 100,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 100-400, f/8-f/11, 1/60s-1/250s",
                notes = "Moon photography benefits from longer focal lengths and stopped-down apertures for sharpness."
            )

            AstroTarget.Planets -> OptimalEquipmentSpecs(
                minFocalLength = 500.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 1200.0,
                maxAperture = 10.0,
                minIsoCapability = 400,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 800-1600, f/8-f/10, 1/30s-1/125s",
                notes = "Planetary photography requires very long focal lengths and high magnification."
            )

            AstroTarget.MilkyWayCore -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 1600,
                maxIsoCapability = 12800,
                recommendedSettings = "ISO 3200-6400, f/1.4-f/2.8, 15s-25s",
                notes = "Milky Way photography requires wide angles and fast apertures for light gathering."
            )

            AstroTarget.DeepSkyObjects -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 1600-3200, f/2.8-f/4, 2-5 minutes with tracking",
                notes = "Deep sky objects benefit from longer focal lengths and tracking mounts for extended exposures.",
                tracking = true
            )

            AstroTarget.StarTrails -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 85.0,
                optimalFocalLength = 35.0,
                maxAperture = 4.0,
                minIsoCapability = 100,
                maxIsoCapability = 800,
                recommendedSettings = "ISO 200-400, f/4-f/5.6, 30s intervals for 1-4 hours",
                notes = "Star trails require wide angles for composition and moderate ISO for noise control."
            )

            AstroTarget.MeteorShowers -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 1600,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 3200, f/2.8, 15-30 seconds",
                notes = "Wide field to capture meteors. Point 45-60° away from radiant for longer trails."
            )

            AstroTarget.Comets -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 300.0,
                optimalFocalLength = 135.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 60-120 seconds with tracking",
                notes = "Comets require medium telephoto lenses and tracking for tail details.",
                tracking = true
            )

            AstroTarget.PolarAlignment -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 200.0,
                optimalFocalLength = 135.0,
                maxAperture = 5.6,
                minIsoCapability = 400,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 800, f/4-f/5.6, 10-30 seconds",
                notes = "Polar alignment requires moderate telephoto to see Polaris and surrounding stars clearly."
            )

            AstroTarget.Constellations -> OptimalEquipmentSpecs(
                minFocalLength = 24.0,
                maxFocalLength = 85.0,
                optimalFocalLength = 50.0,
                maxAperture = 2.8,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 15-30 seconds",
                notes = "Constellation photography benefits from standard to short telephoto lenses."
            )

            AstroTarget.NorthernLights -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 800,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 1600-3200, f/1.4-f/2.8, 3-15 seconds",
                notes = "Aurora photography requires fast wide-angle lenses and quick exposure times to freeze movement."
            )

            // Individual Planets
            AstroTarget.Mercury -> OptimalEquipmentSpecs(
                minFocalLength = 800.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 1500.0,
                maxAperture = 10.0,
                minIsoCapability = 400,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 400-800, f/8-f/10, 1/125s-1/500s",
                notes = "Mercury requires extreme magnification and is best photographed during twilight."
            )

            AstroTarget.Venus -> OptimalEquipmentSpecs(
                minFocalLength = 600.0,
                maxFocalLength = 2000.0,
                optimalFocalLength = 1000.0,
                maxAperture = 8.0,
                minIsoCapability = 200,
                maxIsoCapability = 800,
                recommendedSettings = "ISO 200-400, f/8-f/10, 1/250s-1/500s",
                notes = "Venus shows phases and requires high magnification. Use UV filters to enhance contrast."
            )

            AstroTarget.Mars -> OptimalEquipmentSpecs(
                minFocalLength = 800.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 1500.0,
                maxAperture = 8.0,
                minIsoCapability = 400,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 400-800, f/8-f/10, 1/60s-1/250s",
                notes = "Mars surface features require high magnification and steady seeing conditions."
            )

            AstroTarget.Jupiter -> OptimalEquipmentSpecs(
                minFocalLength = 800.0,
                maxFocalLength = 2500.0,
                optimalFocalLength = 1200.0,
                maxAperture = 8.0,
                minIsoCapability = 400,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 400-800, f/8-f/10, 1/60s-1/125s",
                notes = "Jupiter and its moons require high magnification. Great Red Spot details need excellent seeing."
            )

            AstroTarget.Saturn -> OptimalEquipmentSpecs(
                minFocalLength = 1000.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 1500.0,
                maxAperture = 8.0,
                minIsoCapability = 400,
                maxIsoCapability = 1600,
                recommendedSettings = "ISO 400-800, f/8-f/10, 1/60s-1/125s",
                notes = "Saturn's rings require the highest magnification and excellent atmospheric conditions."
            )

            AstroTarget.Uranus -> OptimalEquipmentSpecs(
                minFocalLength = 600.0,
                maxFocalLength = 2000.0,
                optimalFocalLength = 1000.0,
                maxAperture = 6.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 800-1600, f/6-f/8, 1/30s-1/60s",
                notes = "Uranus appears as a small blue-green disk requiring high magnification."
            )

            AstroTarget.Neptune -> OptimalEquipmentSpecs(
                minFocalLength = 800.0,
                maxFocalLength = 2500.0,
                optimalFocalLength = 1500.0,
                maxAperture = 6.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 800-1600, f/6-f/8, 1/30s-1/60s",
                notes = "Neptune is very small and faint, requiring maximum magnification and longer exposures."
            )

            AstroTarget.Pluto -> OptimalEquipmentSpecs(
                minFocalLength = 1000.0,
                maxFocalLength = 3000.0,
                optimalFocalLength = 2000.0,
                maxAperture = 4.0,
                minIsoCapability = 1600,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 3200-6400, f/4-f/6, 60-120 seconds with tracking",
                notes = "Pluto appears stellar and requires star charts for identification. Long exposures essential.",
                tracking = true
            )

            // Meteor Showers (specific)
            AstroTarget.Quadrantids, AstroTarget.EtaAquariids, AstroTarget.Leonids,
            AstroTarget.Geminids, AstroTarget.Perseids -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 35.0,
                optimalFocalLength = 24.0,
                maxAperture = 2.8,
                minIsoCapability = 1600,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 3200, f/2.8, 15-30 seconds",
                notes = "Specific meteor showers require wide field coverage and fast apertures."
            )

            // Constellations (specific)
            AstroTarget.Leo, AstroTarget.Scorpius, AstroTarget.Cygnus, AstroTarget.BigDipper,
            AstroTarget.Cassiopeia, AstroTarget.Orion, AstroTarget.Constellation_Sagittarius,
            AstroTarget.Constellation_Orion, AstroTarget.Constellation_Cassiopeia,
            AstroTarget.Constellation_UrsaMajor, AstroTarget.Constellation_Cygnus,
            AstroTarget.Constellation_Scorpius, AstroTarget.Constellation_Leo -> OptimalEquipmentSpecs(
                minFocalLength = 24.0,
                maxFocalLength = 85.0,
                optimalFocalLength = 50.0,
                maxAperture = 2.8,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 15-30 seconds",
                notes = "Individual constellations benefit from standard lenses to frame the star pattern."
            )

            // Deep Sky Objects (specific)
            AstroTarget.M31_Andromeda -> OptimalEquipmentSpecs(
                minFocalLength = 85.0,
                maxFocalLength = 300.0,
                optimalFocalLength = 135.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 2-5 minutes with tracking",
                notes = "M31 Andromeda Galaxy requires medium telephoto and tracking for spiral arm details.",
                tracking = true
            )

            AstroTarget.M42_Orion -> OptimalEquipmentSpecs(
                minFocalLength = 135.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 2-3 minutes with tracking",
                notes = "M42 Orion Nebula shows excellent detail with telephoto lenses and tracking.",
                tracking = true
            )

            AstroTarget.M51_Whirlpool -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 800.0,
                optimalFocalLength = 400.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 3-5 minutes with tracking",
                notes = "M51 Whirlpool Galaxy requires long focal lengths to show spiral structure.",
                tracking = true
            )

            AstroTarget.M13_Hercules -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 300.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 2-4 minutes with tracking",
                notes = "M13 Hercules Cluster benefits from telephoto lenses to resolve individual stars.",
                tracking = true
            )

            AstroTarget.M27_Dumbbell -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 300.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 3-5 minutes with tracking",
                notes = "M27 Dumbbell Nebula shows excellent color with medium telephoto lenses.",
                tracking = true
            )

            AstroTarget.M57_Ring -> OptimalEquipmentSpecs(
                minFocalLength = 300.0,
                maxFocalLength = 1000.0,
                optimalFocalLength = 600.0,
                maxAperture = 5.6,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/4-f/5.6, 3-5 minutes with tracking",
                notes = "M57 Ring Nebula is small and requires long focal lengths for detailed structure.",
                tracking = true
            )

            AstroTarget.M81_Bodes -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 600.0,
                optimalFocalLength = 300.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 3-5 minutes with tracking",
                notes = "M81 Bode's Galaxy pairs well with M82 in the same field of view.",
                tracking = true
            )

            AstroTarget.M104_Sombrero -> OptimalEquipmentSpecs(
                minFocalLength = 300.0,
                maxFocalLength = 800.0,
                optimalFocalLength = 500.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 4-6 minutes with tracking",
                notes = "M104 Sombrero Galaxy requires long focal lengths to show the distinctive dust lane.",
                tracking = true
            )

            // Nebulae (specific)
            AstroTarget.CrabNebula, AstroTarget.LagoonNebula, AstroTarget.EagleNebula,
            AstroTarget.RingNebula -> OptimalEquipmentSpecs(
                minFocalLength = 200.0,
                maxFocalLength = 800.0,
                optimalFocalLength = 400.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 3-5 minutes with tracking",
                notes = "Nebulae require telephoto lenses and tracking for detailed structure and color.",
                tracking = true
            )

            // Star Clusters and Galaxies
            AstroTarget.WhirlpoolGalaxy, AstroTarget.Pleiades, AstroTarget.OrionNebula,
            AstroTarget.AndromedaGalaxy -> OptimalEquipmentSpecs(
                minFocalLength = 135.0,
                maxFocalLength = 400.0,
                optimalFocalLength = 200.0,
                maxAperture = 4.0,
                minIsoCapability = 800,
                maxIsoCapability = 3200,
                recommendedSettings = "ISO 1600, f/2.8-f/4, 2-5 minutes with tracking",
                notes = "Large deep sky objects benefit from medium telephoto lenses and tracking.",
                tracking = true
            )

            // Special Targets
            AstroTarget.ISS -> OptimalEquipmentSpecs(
                minFocalLength = 14.0,
                maxFocalLength = 85.0,
                optimalFocalLength = 35.0,
                maxAperture = 2.8,
                minIsoCapability = 800,
                maxIsoCapability = 6400,
                recommendedSettings = "ISO 1600-3200, f/2.8-f/4, 1-5 seconds",
                notes = "ISS photography requires wide to standard lenses and precise timing for passes."
            )
            else -> {
                logger.w { "Using default equipment specs for unhandled target: $target" }
                OptimalEquipmentSpecs(
                    minFocalLength = 50.0,
                    maxFocalLength = 200.0,
                    optimalFocalLength = 85.0,
                    maxAperture = 4.0,
                    minIsoCapability = 800,
                    maxIsoCapability = 3200,
                    recommendedSettings = "ISO 1600, f/2.8-f/4, 30 seconds",
                    notes = "Default astrophotography settings. Specific recommendations not yet implemented for this target."
                )
            }

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