// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/AstroCalculationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.enums.ConstellationType
import com.x3squaredcircles.photography.domain.enums.CoordinateType
import com.x3squaredcircles.photography.domain.enums.PlanetType
import com.x3squaredcircles.photography.domain.models.AtmosphericCorrectionData
import com.x3squaredcircles.photography.domain.models.ConstellationData
import com.x3squaredcircles.photography.domain.models.CoordinateTransformResult
import com.x3squaredcircles.photography.domain.models.DeepSkyObjectData
import com.x3squaredcircles.photography.domain.models.EnhancedMoonData
import com.x3squaredcircles.photography.domain.models.PlanetPositionData
import com.x3squaredcircles.photography.domain.models.PlanetaryConjunction
import com.x3squaredcircles.photography.domain.models.PlanetaryEvent
import com.x3squaredcircles.photography.domain.services.IAstroCalculationService
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import co.touchlab.kermit.Logger
import io.github.cosinekitty.astronomy.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.*

class AstroCalculationService(
    private val logger: Logger,
    private val sunCalculatorService: ISunCalculatorService
) : IAstroCalculationService {

    companion object {
        private val planetBodies = mapOf(
            PlanetType.Mercury to Body.Mercury,
            PlanetType.Venus to Body.Venus,
            PlanetType.Mars to Body.Mars,
            PlanetType.Jupiter to Body.Jupiter,
            PlanetType.Saturn to Body.Saturn,
            PlanetType.Uranus to Body.Uranus,
            PlanetType.Neptune to Body.Neptune,
            PlanetType.Pluto to Body.Pluto
        )
    }

    override suspend fun getPlanetPositionAsync(
        planet: PlanetType,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<PlanetPositionData> {
        return withContext(Dispatchers.Default) {
            try {
                val body = planetBodies[planet]
                    ?: return@withContext Result.failure("Unknown planet: $planet")

                val time = Time(dateTime.toLocalDateTime(TimeZone.UTC).year, dateTime.toLocalDateTime(TimeZone.UTC).monthNumber, dateTime.toLocalDateTime(TimeZone.UTC).dayOfMonth, dateTime.toLocalDateTime(TimeZone.UTC).hour, dateTime.toLocalDateTime(TimeZone.UTC).minute, dateTime.toLocalDateTime(TimeZone.UTC).second.toDouble())
                val observer = Observer(latitude, longitude, 0.0)

                val equatorial =  equator(body, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
                val horizontal = horizon(time, observer, equatorial.ra, equatorial.dec, Refraction.Normal)
                val illumination = illumination(body, time)

                val riseSetTimes = calculatePlanetRiseSetTimes(body, dateTime, observer)

                val planetData = PlanetPositionData(
                    planet = planet,
                    dateTime = dateTime,
                    rightAscension = equatorial.ra,
                    declination = equatorial.dec,
                    azimuth = horizontal.azimuth,
                    altitude = horizontal.altitude,
                    distance = equatorial.dist,
                    apparentMagnitude = illumination.mag,
                    angularDiameter = calculatePlanetAngularDiameter(planet, equatorial.dist),
                    isVisible = horizontal.altitude > 0,
                    rise = riseSetTimes.rise,
                    set = riseSetTimes.set,
                    transit = riseSetTimes.transit,
                    recommendedEquipment = getPlanetEquipmentRecommendation(planet, equatorial.dist),
                    photographyNotes = getPlanetPhotographyNotes(planet, illumination.phaseFraction)
                )

                Result.success(planetData)
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating planet position for $planet" }
                Result.failure("Error calculating planet position: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getVisiblePlanetsAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<List<PlanetPositionData>> {
        return withContext(Dispatchers.Default) {
            try {
                val visiblePlanets = mutableListOf<PlanetPositionData>()

                for (planet in PlanetType.values()) {
                    val planetResult = getPlanetPositionAsync(planet, dateTime, latitude, longitude)
                    when (planetResult) {
                        is Result.Success -> {
                            if (planetResult.data.isVisible) {
                                visiblePlanets.add(planetResult.data)
                            }
                        }
                        is Result.Failure -> {
                            logger.w { "Failed to get position for $planet: ${planetResult.error}" }
                        }
                    }
                }

                Result.success(visiblePlanets)
            } catch (ex: Exception) {
                logger.e(ex) { "Error getting visible planets" }
                Result.failure("Error getting visible planets: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getPlanetaryConjunctionsAsync(
        startDate: Instant,
        endDate: Instant,
        latitude: Double,
        longitude: Double
    ): Result<List<PlanetaryConjunction>> {
        return withContext(Dispatchers.Default) {
            try {
                val conjunctions = mutableListOf<PlanetaryConjunction>()
                val planets = PlanetType.values()

                for (i in planets.indices) {
                    for (j in i + 1 until planets.size) {
                        val planet1 = planets[i]
                        val planet2 = planets[j]

                        val body1 = planetBodies[planet1] ?: continue
                        val body2 = planetBodies[planet2] ?: continue

                        val conjunctionEvents = findConjunctionEvents(body1, body2, startDate, endDate, latitude, longitude)
                        conjunctions.addAll(conjunctionEvents.map { event ->
                            PlanetaryConjunction(
                                planet1 = planet1,
                                planet2 = planet2,
                                dateTime = event.dateTime,
                                separation = event.separation,
                                altitude = 0.0,
                                azimuth = 0.0,
                                visibilityDescription = event.isVisible.toString(),
                                photographyRecommendation = getConjunctionPhotographyNotes(planet1, planet2, event.separation)
                            )
                        })
                    }
                }

                Result.success(conjunctions.sortedBy { it.dateTime })
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating planetary conjunctions" }
                Result.failure("Error calculating planetary conjunctions: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getPlanetOppositionsAsync(
        startDate: Instant,
        endDate: Instant
    ): Result<List<PlanetaryEvent>> {
        return withContext(Dispatchers.Default) {
            try {
                val oppositions = mutableListOf<PlanetaryEvent>()
                val outerPlanets = listOf(PlanetType.Mars, PlanetType.Jupiter, PlanetType.Saturn, PlanetType.Uranus, PlanetType.Neptune)

                for (planet in outerPlanets) {
                    val body = planetBodies[planet] ?: continue
                    val oppositionEvents = findOppositionEvents(body, startDate, endDate)

                    oppositions.addAll(oppositionEvents.map { event ->
                        PlanetaryEvent(
                            planet = planet,
                            eventType = "Opposition",
                            dateTime = event.dateTime,
                            optimalViewingConditions = event.toString(),
                            apparentMagnitude = 0.0,
                            angularDiameter = 0.0,
                            equipmentRecommendations = ""
                                    //getOppositionPhotographyNotes(planet)
                        )
                    })
                }

                Result.success(oppositions.sortedBy { it.dateTime })
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating planet oppositions" }
                Result.failure("Error calculating planet oppositions: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getEnhancedMoonDataAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<EnhancedMoonData> {
        return withContext(Dispatchers.Default) {
            try {
                val time = Time(dateTime.toEpochMilliseconds().toDouble())//, dateTime.toLocalDateTime(TimeZone.UTC).minute,dateTime.toLocalDateTime(TimeZone.UTC).second)
                val observer = Observer(latitude, longitude, 0.0)

                val equatorial = equator(Body.Moon, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
                val horizontal = horizon(time, observer, equatorial.ra, equatorial.dec, Refraction.Normal)
                val illumination = illumination(Body.Moon, time)
                val riseSetTimes = calculateMoonRiseSetTimes(dateTime, observer)

                val moonData = EnhancedMoonData(
                    dateTime = dateTime,
                    phase = illumination.phaseFraction,
                    phaseName = calculateMoonPhaseName(illumination.phaseAngle),
                    illumination = illumination.phaseFraction,
                    azimuth = horizontal.azimuth,
                    altitude = horizontal.altitude,
                    distance = equatorial.dist * 149597870.7, // Convert AU to km
                    angularDiameter = calculateMoonAngularDiameter(equatorial.dist),
                    rise = riseSetTimes.rise,
                    set = riseSetTimes.set,
                    transit = riseSetTimes.transit,
                    librationLatitude = calculateLunarLibration(dateTime).first,
                    librationLongitude = calculateLunarLibration(dateTime).second,
                    positionAngle = illumination.phaseAngle,
                    isSupermoon = isSupermoon(equatorial.dist * 149597870.7),
                    opticalLibration = 0.0,
                    optimalPhotographyPhase = getOptimalPhotographyPhase(illumination.phaseFraction),
                    visibleFeatures = getVisibleLunarFeatures(illumination.phaseFraction),
                    recommendedExposureSettings = getMoonPhotographyRecommendations(illumination.phaseFraction, horizontal.altitude)
                )

                Result.success(moonData)
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating enhanced moon data" }
                Result.failure("Error calculating enhanced moon data: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getConstellationDataAsync(
        constellation: ConstellationType,
        date: Instant,
        latitude: Double,
        longitude: Double
    ): Result<ConstellationData> {
        return withContext(Dispatchers.Default) {
            try {
                val time =  Time(date.toEpochMilliseconds().toDouble())
                val observer = Observer(latitude, longitude, 0.0)

                val constellationCoords = getConstellationCoordinates(constellation)
                val horizontal = horizon(time, observer, constellationCoords.ra, constellationCoords.dec, Refraction.Normal)
                val riseSetTimes = calculateObjectRiseSetTimes(constellationCoords.ra, constellationCoords.dec, date, observer)

                val constellationData = ConstellationData(
                    constellation = constellation,
                    dateTime = date,
                    centerRightAscension = constellationCoords.ra,
                    centerDeclination = constellationCoords.dec,
                    centerAzimuth = horizontal.azimuth,
                    centerAltitude = horizontal.altitude,
                    rise = riseSetTimes.rise,
                    set = riseSetTimes.set,
                    optimalViewingTime = getOptimalViewingTime(riseSetTimes.rise, riseSetTimes.set),
                    isCircumpolar = isCircumpolar(constellationCoords.dec, latitude),
                    notableObjects = getConstellationDeepSkyObjects(constellation),
                    photographyNotes = getConstellationPhotographyNotes(constellation)
                )

                Result.success(constellationData)
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating constellation data for $constellation" }
                Result.failure("Error calculating constellation data: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getDeepSkyObjectDataAsync(
        catalogId: String,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<DeepSkyObjectData> {
        return withContext(Dispatchers.Default) {
            try {
                val objectInfo = getDeepSkyObjectInfo(catalogId)
                    ?: return@withContext Result.failure("Unknown deep sky object: $catalogId")

                val time = Time(dateTime.toEpochMilliseconds().toDouble())
                val observer = Observer(latitude, longitude, 0.0)

                val horizontal = horizon(time, observer, objectInfo.rightAscension, objectInfo.declination, Refraction.Normal)
                val riseSetTimes = calculateObjectRiseSetTimes(objectInfo.rightAscension, objectInfo.declination, dateTime, observer)

                val deepSkyData = DeepSkyObjectData(
                    catalogId = catalogId,
                    commonName = objectInfo.commonName,
                    objectType = objectInfo.objectType,
                    dateTime = dateTime,
                    rightAscension = objectInfo.rightAscension,
                    declination = objectInfo.declination,
                    azimuth = horizontal.azimuth,
                    altitude = horizontal.altitude,
                    magnitude = objectInfo.magnitude,
                    angularSize = objectInfo.angularSize,
                    isVisible = horizontal.altitude > 0,
                    optimalViewingTime = getOptimalViewingTime(riseSetTimes.rise, riseSetTimes.set),
                    parentConstellation = objectInfo.parentConstellation,
                    recommendedEquipment = getDeepSkyEquipmentRecommendation(objectInfo.objectType, objectInfo.magnitude),
                    exposureGuidance = getDeepSkyPhotographyNotes(objectInfo.objectType, objectInfo.magnitude, horizontal.altitude)
                )

                Result.success(deepSkyData)
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating deep sky object data for $catalogId" }
                Result.failure("Error calculating deep sky object data: ${ex.message}", ex)
            }
        }
    }

    override suspend fun transformCoordinatesAsync(
        fromType: CoordinateType,
        toType: CoordinateType,
        coordinate1: Double,
        coordinate2: Double,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<CoordinateTransformResult> {
        return withContext(Dispatchers.Default) {
            try {
                val time = Time(dateTime.toEpochMilliseconds().toDouble())
                val observer = Observer(latitude, longitude, 0.0)

                val result = performCoordinateTransformation(fromType, toType, coordinate1, coordinate2, time, observer)

                val transformResult = CoordinateTransformResult(
                    fromType = fromType,
                    toType = toType,
                    inputCoordinate1 = coordinate1,
                    inputCoordinate2 = coordinate2,
                    outputCoordinate1 = result.first,
                    outputCoordinate2 = result.second,
                    dateTime = dateTime,
                    latitude = latitude,
                    longitude = longitude
                )

                Result.success(transformResult)
            } catch (ex: Exception) {
                logger.e(ex) { "Error transforming coordinates from $fromType to $toType" }
                Result.failure("Error transforming coordinates: ${ex.message}", ex)
            }
        }
    }

    override suspend fun getAtmosphericCorrectionAsync(
        altitude: Double,
        azimuth: Double,
        temperature: Double,
        pressure: Double,
        humidity: Double
    ): Result<AtmosphericCorrectionData> {
        return withContext(Dispatchers.Default) {
            try {
                val refraction = calculateAtmosphericRefraction(altitude, temperature, pressure, humidity)
                val extinction = calculateAtmosphericExtinction(altitude, humidity)
                val trueAltitude = altitude - (refraction / 60.0) // Convert arc minutes to degrees
                val apparentAltitude = altitude

                val correctionData = AtmosphericCorrectionData(
                    trueAltitude = trueAltitude,
                    apparentAltitude = apparentAltitude,
                    refractionCorrection = refraction,
                    atmosphericExtinction = extinction,
                    correctionNotes = generateAtmosphericCorrectionNotes(altitude, refraction, extinction)
                )

                Result.success(correctionData)
            } catch (ex: Exception) {
                logger.e(ex) { "Error calculating atmospheric correction" }
                Result.failure("Error calculating atmospheric correction: ${ex.message}", ex)
            }
        }
    }

    // Private calculation methods
    private fun calculatePlanetRiseSetTimes(body: Body, dateTime: Instant, observer: Observer): RiseSetTransitTimes {
        return try {
            val searchTime = Time(dateTime.toEpochMilliseconds().toDouble())
            val riseEvent = searchRiseSet(body, observer, Direction.Rise, searchTime, 1.0)
            val setEvent = searchRiseSet(body, observer, Direction.Set, searchTime, 1.0)
            val transitEvent = searchHourAngle(body, observer, 0.0, searchTime)

            RiseSetTransitTimes(
                rise = riseEvent?.toInstant(),
                set = setEvent?.toInstant(),
                transit = transitEvent.time.toInstant()
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating rise/set times" }
            RiseSetTransitTimes(null, null, null)
        }
    }

    private fun calculateMoonRiseSetTimes(dateTime: Instant, observer: Observer): RiseSetTransitTimes {
        return calculatePlanetRiseSetTimes(Body.Moon, dateTime, observer)
    }

    private fun calculateObjectRiseSetTimes(ra: Double, dec: Double, dateTime: Instant, observer: Observer): RiseSetTransitTimes {
        return try {

            val searchTime = Time(dateTime.toEpochMilliseconds().toDouble())
            // Define star coordinates using Body.Star1
            defineStar(Body.Star1, ra, dec, 1000.0)

            val riseEvent = searchRiseSet(Body.Star1, observer, Direction.Rise, searchTime, 1.0)
            val setEvent = searchRiseSet(Body.Star1, observer, Direction.Set, searchTime, 1.0)

            RiseSetTransitTimes(
                rise = riseEvent?.toInstant(),
                set = setEvent?.toInstant(),
                transit = null
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating object rise/set times" }
            RiseSetTransitTimes(null, null, null)
        }
    }

    private fun calculatePlanetAngularDiameter(planet: PlanetType, distanceAU: Double): Double {
        val baseDiameters = mapOf(
            PlanetType.Mercury to 6.74,
            PlanetType.Venus to 16.92,
            PlanetType.Mars to 9.36,
            PlanetType.Jupiter to 196.94,
            PlanetType.Saturn to 165.60,
            PlanetType.Uranus to 65.14,
            PlanetType.Neptune to 62.20,
            PlanetType.Pluto to 8.20
        )

        return baseDiameters[planet]?.div(distanceAU) ?: 0.0
    }

    private fun calculateMoonAngularDiameter(distanceAU: Double): Double {
        val meanAngularDiameter = 31.1 // Arc minutes
        val meanDistance = 0.00257 // AU
        return meanAngularDiameter * (meanDistance / distanceAU)
    }

    private fun getPlanetEquipmentRecommendation(planet: PlanetType, distance: Double): String {
        return when (planet) {
            PlanetType.Venus -> if (distance < 0.3) "200-600mm telephoto lens" else "400mm+ telephoto lens"
            PlanetType.Mars -> if (distance < 0.5) "200-400mm telephoto sufficient for surface features" else "400mm+ telephoto lens minimum"
            PlanetType.Jupiter -> "200mm+ telephoto lens, telescope for detail"
            PlanetType.Saturn -> "300mm+ telephoto lens, telescope for rings"
            PlanetType.Uranus, PlanetType.Neptune -> "Telescope required for detection"
            PlanetType.Pluto -> "Large telescope and long exposure required"
            else -> "Medium telephoto lens"
        }
    }

    private fun getPlanetPhotographyNotes(planet: PlanetType, phase: Double): String {
        return when (planet) {
            PlanetType.Venus -> "Phase: ${(phase * 100).toInt()}%. Best viewed during crescent phases for surface detail."
            PlanetType.Mars -> "Look for polar ice caps and surface features. Best during opposition."
            PlanetType.Jupiter -> "Capture the Great Red Spot and Galilean moons. Short exposures recommended."
            PlanetType.Saturn -> "Focus on ring system detail. Use moderate magnification for best results."
            else -> "Use appropriate focal length for planet size and atmospheric conditions."
        }
    }

    private fun calculateMoonPhaseName(phaseAngle: Double): String {
        return when {
            phaseAngle < 22.5 -> "New Moon"
            phaseAngle < 67.5 -> "Waxing Crescent"
            phaseAngle < 112.5 -> "First Quarter"
            phaseAngle < 157.5 -> "Waxing Gibbous"
            phaseAngle < 202.5 -> "Full Moon"
            phaseAngle < 247.5 -> "Waning Gibbous"
            phaseAngle < 292.5 -> "Third Quarter"
            phaseAngle < 337.5 -> "Waning Crescent"
            else -> "New Moon"
        }
    }

    private fun calculateLunarLibration(dateTime: Instant): Pair<Double, Double> {
        // Calculate lunar libration - simplified implementation
        val time = Time(dateTime.toEpochMilliseconds().toDouble())
        // This would require complex lunar mechanics calculation
        // For now, return approximate values
        return Pair(0.0, 0.0)
    }

    private fun isSupermoon(distanceKm: Double): Boolean {
        val averageDistance = 384400.0 // km
        return distanceKm < averageDistance * 0.9
    }

    private fun getOptimalPhotographyPhase(illumination: Double): String {
        return when {
            illumination < 0.1 -> "New Moon - not visible"
            illumination < 0.3 -> "Crescent - good for earthshine"
            illumination < 0.7 -> "Quarter phases - best for crater detail"
            illumination < 0.9 -> "Gibbous - excellent for surface features"
            else -> "Full Moon - best for overall landscape"
        }
    }

    private fun getVisibleLunarFeatures(illumination: Double): List<String> {
        return when {
            illumination < 0.1 -> emptyList()
            illumination < 0.3 -> listOf("Terminator craters", "Earthshine")
            illumination < 0.7 -> listOf("Major craters", "Mare borders", "Mountain ranges")
            illumination < 0.9 -> listOf("Ray systems", "Detailed craters", "Mare features")
            else -> listOf("Full lunar disk", "Ray systems", "All major features")
        }
    }

    private fun getMoonPhotographyRecommendations(illumination: Double, altitude: Double): String {
        val baseRecommendation = when {
            illumination < 0.3 -> "Long exposure for earthshine, use tripod"
            illumination < 0.7 -> "Medium exposure, focus on terminator detail"
            else -> "Short exposure to avoid overexposure, use fast shutter"
        }

        val altitudeNote = when {
            altitude < 30 -> " Account for atmospheric distortion at low altitude."
            altitude > 60 -> " Excellent viewing conditions at high altitude."
            else -> " Good viewing conditions."
        }

        return baseRecommendation + altitudeNote
    }

    private fun getConstellationCoordinates(constellation: ConstellationType): ConstellationCoords {
        return when (constellation) {
            ConstellationType.Orion -> ConstellationCoords(5.58, -5.39)
            ConstellationType.Andromeda -> ConstellationCoords(0.71, 41.27)
            ConstellationType.Sagittarius -> ConstellationCoords(18.06, -24.38)
            ConstellationType.Cygnus -> ConstellationCoords(20.22, 40.26)
            ConstellationType.Cassiopeia -> ConstellationCoords(1.0, 60.0)
            ConstellationType.UrsaMajor -> ConstellationCoords(11.0, 55.0)
            ConstellationType.Leo -> ConstellationCoords(10.5, 15.0)
            ConstellationType.Scorpius -> ConstellationCoords(16.5, -26.0)
            ConstellationType.Perseus -> ConstellationCoords(2.33, 45.0)
            ConstellationType.Auriga -> ConstellationCoords(5.9, 42.0)
            ConstellationType.Lyra -> ConstellationCoords(18.6, 38.8)
            ConstellationType.Aquila -> ConstellationCoords(19.7, 8.9)
            ConstellationType.Centaurus -> ConstellationCoords(13.4, -47.3)
            ConstellationType.Crux -> ConstellationCoords(12.4, -60.2)
            ConstellationType.UrsaMinor -> ConstellationCoords(15.0, 77.8)
            ConstellationType.Draco -> ConstellationCoords(17.5, 65.0)
            ConstellationType.Gemini -> ConstellationCoords(6.75, 22.5)
            ConstellationType.Cancer -> ConstellationCoords(8.6, 19.8)
            ConstellationType.Virgo -> ConstellationCoords(13.4, -4.0)
            ConstellationType.Libra -> ConstellationCoords(15.2, -15.2)
            ConstellationType.Capricornus -> ConstellationCoords(21.0, -20.0)
            ConstellationType.Aquarius -> ConstellationCoords(22.5, -10.0)
            ConstellationType.Pisces -> ConstellationCoords(0.75, 15.0)
            ConstellationType.Aries -> ConstellationCoords(2.6, 20.8)
            ConstellationType.Taurus -> ConstellationCoords(4.6, 15.8)
            ConstellationType.Ursa_Major -> TODO()
        }
    }

    private fun getOptimalViewingTime(rise: Instant?, set: Instant?): Instant? {
        if (rise == null || set == null) return null
        val midTime = (rise.toEpochMilliseconds() + set.toEpochMilliseconds()) / 2
        return Instant.fromEpochMilliseconds(midTime)
    }

    private fun isCircumpolar(declination: Double, latitude: Double): Boolean {
        return abs(declination) > (90.0 - abs(latitude))
    }

    private fun getConstellationDeepSkyObjects(constellation: ConstellationType): List<DeepSkyObjectData> {
        return when (constellation) {
            ConstellationType.Orion -> listOf(
                createDeepSkyObject("M42", "Orion Nebula", "Nebula", 5.58, -5.39, 4.0, 85.0, constellation),
                createDeepSkyObject("M43", "De Mairan's Nebula", "Nebula", 5.58, -5.27, 9.0, 20.0, constellation),
                createDeepSkyObject("NGC 2024", "Flame Nebula", "Nebula", 5.68, -1.9, 2.0, 30.0, constellation)
            )
            ConstellationType.Andromeda -> listOf(
                createDeepSkyObject("M31", "Andromeda Galaxy", "Galaxy", 0.71, 41.27, 3.4, 190.0, constellation),
                createDeepSkyObject("M32", "Le Gentil", "Galaxy", 0.71, 40.87, 8.1, 8.0, constellation),
                createDeepSkyObject("M110", "NGC 205", "Galaxy", 0.67, 41.68, 8.5, 19.0, constellation)
            )
            ConstellationType.Sagittarius -> listOf(
                createDeepSkyObject("M8", "Lagoon Nebula", "Nebula", 18.06, -24.38, 6.0, 90.0, constellation),
                createDeepSkyObject("M20", "Trifid Nebula", "Nebula", 18.03, -23.03, 9.0, 20.0, constellation),
                createDeepSkyObject("M22", "Great Sagittarius Cluster", "Globular Cluster", 18.61, -23.9, 5.1, 32.0, constellation)
            )
            ConstellationType.Cygnus -> listOf(
                createDeepSkyObject("NGC 7000", "North America Nebula", "Nebula", 20.98, 44.22, 4.0, 120.0, constellation),
                createDeepSkyObject("M27", "Dumbbell Nebula", "Planetary Nebula", 19.99, 22.72, 7.5, 8.0, constellation),
                createDeepSkyObject("NGC 6960", "Western Veil Nebula", "Supernova Remnant", 20.75, 30.72, 7.0, 70.0, constellation)
            )
            ConstellationType.Leo -> listOf(
                createDeepSkyObject("M65", "Leo Triplet", "Galaxy", 11.31, 13.1, 9.3, 8.0, constellation),
                createDeepSkyObject("M66", "Leo Triplet", "Galaxy", 11.34, 12.99, 8.9, 8.0, constellation),
                createDeepSkyObject("M95", "Barred Spiral Galaxy", "Galaxy", 10.74, 11.7, 9.7, 4.0, constellation)
            )
            ConstellationType.Virgo -> listOf(
                createDeepSkyObject("M87", "Virgo A", "Galaxy", 12.51, 12.39, 8.6, 7.0, constellation),
                createDeepSkyObject("M104", "Sombrero Galaxy", "Galaxy", 12.67, -11.62, 8.0, 9.0, constellation),
                createDeepSkyObject("M49", "Elliptical Galaxy", "Galaxy", 12.50, 8.0, 8.4, 9.0, constellation)
            )
            ConstellationType.Cassiopeia -> listOf(
                createDeepSkyObject("M52", "Open Cluster", "Open Cluster", 23.4, 61.6, 7.3, 13.0, constellation),
                createDeepSkyObject("NGC 457", "Owl Cluster", "Open Cluster", 1.32, 58.3, 6.4, 13.0, constellation)
            )
            ConstellationType.Perseus -> listOf(
                createDeepSkyObject("M34", "Perseus Cluster", "Open Cluster", 2.7, 42.8, 5.2, 35.0, constellation),
                createDeepSkyObject("NGC 869", "Double Cluster", "Open Cluster", 2.32, 57.1, 4.3, 30.0, constellation)
            )
            ConstellationType.Auriga -> listOf(
                createDeepSkyObject("M36", "Pinwheel Cluster", "Open Cluster", 5.6, 34.1, 6.0, 12.0, constellation),
                createDeepSkyObject("M37", "Salt and Pepper Cluster", "Open Cluster", 5.9, 32.5, 5.6, 24.0, constellation)
            )
            ConstellationType.Lyra -> listOf(
                createDeepSkyObject("M57", "Ring Nebula", "Planetary Nebula", 18.89, 33.03, 8.8, 1.4, constellation)
            )
            ConstellationType.Aquila -> listOf(
                createDeepSkyObject("NGC 6709", "Eagle Cluster", "Open Cluster", 18.85, 10.3, 6.7, 13.0, constellation)
            )
            ConstellationType.UrsaMajor -> listOf(
                createDeepSkyObject("M81", "Bode's Galaxy", "Galaxy", 9.93, 69.1, 6.9, 21.0, constellation),
                createDeepSkyObject("M82", "Cigar Galaxy", "Galaxy", 9.93, 69.7, 8.4, 9.0, constellation)
            )
            ConstellationType.Gemini -> listOf(
                createDeepSkyObject("M35", "Gemini Cluster", "Open Cluster", 6.15, 24.3, 5.3, 28.0, constellation),
                createDeepSkyObject("NGC 2392", "Eskimo Nebula", "Planetary Nebula", 7.48, 20.9, 9.2, 0.7, constellation)
            )
            ConstellationType.Cancer -> listOf(
                createDeepSkyObject("M44", "Beehive Cluster", "Open Cluster", 8.67, 19.7, 3.7, 95.0, constellation),
                createDeepSkyObject("M67", "King Cobra Cluster", "Open Cluster", 8.85, 11.8, 6.1, 30.0, constellation)
            )
            ConstellationType.Taurus -> listOf(
                createDeepSkyObject("M45", "Pleiades", "Open Cluster", 3.79, 24.1, 1.6, 110.0, constellation),
                createDeepSkyObject("M1", "Crab Nebula", "Supernova Remnant", 5.58, 22.0, 8.4, 6.0, constellation)
            )
            else -> emptyList()
        }
    }

    private fun createDeepSkyObject(
        catalogId: String,
        commonName: String,
        objectType: String,
        ra: Double,
        dec: Double,
        magnitude: Double,
        angularSize: Double,
        constellation: ConstellationType
    ): DeepSkyObjectData {
        return DeepSkyObjectData(
            catalogId = catalogId,
            commonName = commonName,
            objectType = objectType,
            dateTime = Instant.fromEpochMilliseconds(0),
            rightAscension = ra,
            declination = dec,
            azimuth = 0.0,
            altitude = 0.0,
            magnitude = magnitude,
            angularSize = angularSize,
            isVisible = false,
            parentConstellation = constellation,
            optimalViewingTime = getOptimalViewingTime(Instant.fromEpochMilliseconds(0),Instant.fromEpochMilliseconds(0)),
            exposureGuidance = getDeepSkyPhotographyNotes(objectType.toString(), magnitude, dec),//  expo(objectType, magnitude),
            recommendedEquipment = getDeepSkyEquipmentRecommendation(objectType, magnitude)
        )
    }

    private fun getConstellationPhotographyNotes(constellation: ConstellationType): String {
        return when (constellation) {
            ConstellationType.Orion -> "Winter constellation, excellent for nebula photography. M42 Orion Nebula is ideal beginner target. Use 85-200mm lens for detail."
            ConstellationType.Andromeda -> "Autumn constellation featuring M31 Andromeda Galaxy. Use 135-200mm lens. Galaxy spans 3+ degrees, plan wide compositions."
            ConstellationType.Sagittarius -> "Summer constellation in Milky Way core region. Rich nebula fields, excellent for wide-field astrophotography. Dark skies essential."
            ConstellationType.Cygnus -> "Summer constellation along Milky Way. North America Nebula excellent widefield target. Veil Nebula complex requires H-alpha filter."
            ConstellationType.Cassiopeia -> "Circumpolar constellation, visible year-round from northern latitudes. Heart and Soul nebulae excellent autumn targets."
            ConstellationType.UrsaMajor -> "Spring constellation featuring the Big Dipper asterism. M81 and M82 galaxy pair excellent telescopic targets."
            ConstellationType.Leo -> "Spring constellation with Leo Triplet galaxy group. Excellent southern hemisphere target."
            ConstellationType.Scorpius -> "Summer constellation. Rich in nebulae and star clusters. Distinctive scorpion shape with bright red star Antares."
            ConstellationType.Perseus -> "Autumn constellation containing Double Cluster and several nebulae. Excellent for wide-field photography."
            ConstellationType.Auriga -> "Winter constellation with bright star Capella. Contains several open clusters ideal for photography."
            ConstellationType.Lyra -> "Summer constellation featuring bright star Vega. Ring Nebula M57 excellent planetary nebula target."
            ConstellationType.Aquila -> "Summer constellation in Milky Way. Contains several star clusters and nebulae."
            ConstellationType.Centaurus -> "Southern constellation with bright stars Alpha and Beta Centauri. Excellent southern hemisphere target."
            ConstellationType.Crux -> "Southern Cross constellation with Coal Sack Nebula. Iconic southern hemisphere target for wide-field compositions."
            ConstellationType.UrsaMinor -> "Circumpolar constellation containing Polaris. Essential for polar alignment and star trail photography."
            ConstellationType.Draco -> "Large circumpolar constellation curving around the north celestial pole. Excellent for star trail compositions."
            ConstellationType.Gemini -> "Winter constellation with bright stars Castor and Pollux. Contains open cluster M35 and Eskimo Nebula."
            ConstellationType.Cancer -> "Faint zodiacal constellation containing Beehive Cluster M44. Requires dark skies for optimal photography."
            ConstellationType.Virgo -> "Spring constellation containing Virgo Galaxy Cluster. Excellent for galaxy photography with longer focal lengths."
            ConstellationType.Libra -> "Autumn constellation with modest deep sky targets. Good for constellation pattern photography."
            ConstellationType.Capricornus -> "Autumn constellation in southern sky. Globular cluster M30 is the main deep sky target."
            ConstellationType.Aquarius -> "Autumn constellation containing Helix Nebula and several globular clusters. Large constellation requires wide-field approach."
            ConstellationType.Pisces -> "Large but faint autumn constellation. Challenging target requiring dark skies for constellation photography."
            ConstellationType.Aries -> "Small autumn constellation with few deep sky objects. Good for constellation pattern and wide-field photography."
            ConstellationType.Taurus -> "Winter constellation featuring Pleiades M45 and Hyades clusters. Excellent targets for wide-field photography."
            ConstellationType.Ursa_Major -> TODO()
        }
    }

    private fun getDeepSkyObjectInfo(catalogId: String): DeepSkyObjectInfo? {
        val catalogMap = mapOf(
            "M31" to DeepSkyObjectInfo("Andromeda Galaxy", "Galaxy", catalogId, 0.71, 41.27, 3.4, 190.0, ConstellationType.Andromeda),
            "M32" to DeepSkyObjectInfo("Le Gentil", "Galaxy", catalogId, 0.71, 40.87, 8.1, 8.0, ConstellationType.Andromeda),
            "M42" to DeepSkyObjectInfo("Orion Nebula", "Nebula", catalogId, 5.58, -5.39, 4.0, 85.0, ConstellationType.Orion),
            "M43" to DeepSkyObjectInfo("De Mairan's Nebula", "Nebula", catalogId, 5.58, -5.27, 9.0, 20.0, ConstellationType.Orion),
            "M45" to DeepSkyObjectInfo("Pleiades", "Open Cluster", catalogId, 3.79, 24.1, 1.6, 110.0, ConstellationType.Taurus),
            "M8" to DeepSkyObjectInfo("Lagoon Nebula", "Nebula", catalogId, 18.06, -24.38, 6.0, 90.0, ConstellationType.Sagittarius),
            "M20" to DeepSkyObjectInfo("Trifid Nebula", "Nebula", catalogId, 18.03, -23.03, 9.0, 20.0, ConstellationType.Sagittarius),
            "M22" to DeepSkyObjectInfo("Great Sagittarius Cluster", "Globular Cluster", catalogId, 18.61, -23.9, 5.1, 32.0, ConstellationType.Sagittarius),
            "M57" to DeepSkyObjectInfo("Ring Nebula", "Planetary Nebula", catalogId, 18.89, 33.03, 8.8, 1.4, ConstellationType.Lyra),
            "M81" to DeepSkyObjectInfo("Bode's Galaxy", "Galaxy", catalogId, 9.93, 69.1, 6.9, 21.0, ConstellationType.UrsaMajor),
            "M82" to DeepSkyObjectInfo("Cigar Galaxy", "Galaxy", catalogId, 9.93, 69.7, 8.4, 9.0, ConstellationType.UrsaMajor),
            "NGC 7000" to DeepSkyObjectInfo("North America Nebula", "Nebula", catalogId, 20.98, 44.22, 4.0, 120.0, ConstellationType.Cygnus),
            "M27" to DeepSkyObjectInfo("Dumbbell Nebula", "Planetary Nebula", catalogId, 19.99, 22.72, 7.5, 8.0, ConstellationType.Cygnus),
            "M65" to DeepSkyObjectInfo("Leo Triplet", "Galaxy", catalogId, 11.31, 13.1, 9.3, 8.0, ConstellationType.Leo),
            "M66" to DeepSkyObjectInfo("Leo Triplet", "Galaxy", catalogId, 11.34, 12.99, 8.9, 8.0, ConstellationType.Leo),
            "M104" to DeepSkyObjectInfo("Sombrero Galaxy", "Galaxy", catalogId, 12.67, -11.62, 8.0, 9.0, ConstellationType.Virgo),
            "M44" to DeepSkyObjectInfo("Beehive Cluster", "Open Cluster", catalogId, 8.67, 19.7, 3.7, 95.0, ConstellationType.Cancer),
            "M35" to DeepSkyObjectInfo("Gemini Cluster", "Open Cluster", catalogId, 6.15, 24.3, 5.3, 28.0, ConstellationType.Gemini),
            "M1" to DeepSkyObjectInfo("Crab Nebula", "Supernova Remnant", catalogId, 5.58, 22.0, 8.4, 6.0, ConstellationType.Taurus)
        )

        return catalogMap[catalogId.uppercase()]
    }

    private fun getDeepSkyEquipmentRecommendation(objectType: String, magnitude: Double): String {
        return when (objectType.lowercase()) {
            "galaxy" -> if (magnitude < 8.0) "200-400mm telephoto, dark skies essential" else "Telescope required for faint galaxies"
            "nebula" -> "Wide-field telescope 135-200mm, consider narrowband filters for emission nebulae"
            "planetary nebula" -> "Telescope 200mm+, OIII filter recommended for contrast"
            "globular cluster" -> "Medium telephoto 200-300mm, resolves well in telescopes"
            "open cluster" -> "Wide-field lens 85-135mm, excellent for beginners"
            "supernova remnant" -> "Telescope with H-alpha filter, requires dark skies"
            else -> "Medium telephoto lens 135-200mm recommended"
        }
    }

    private fun getDeepSkyPhotographyNotes(objectType: String, magnitude: Double, altitude: Double): String {
        val baseNote = when (objectType.lowercase()) {
            "galaxy" -> "ISO 1600-6400, f/2.8-4, 2-5 minute exposures with tracking"
            "nebula" -> "ISO 800-3200, f/2.8-4, 3-8 minute exposures, consider narrowband filters"
            "planetary nebula" -> "ISO 800-1600, f/4-5.6, 2-5 minute exposures with OIII filter"
            "globular cluster" -> "ISO 400-1600, f/4-5.6, 1-3 minute exposures"
            "open cluster" -> "ISO 400-1600, f/2.8-4, 1-3 minute exposures"
            "supernova remnant" -> "ISO 1600-3200, f/2.8-4, 5-10 minute exposures with H-alpha filter"
            else -> "ISO 800-3200, f/2.8-4, 2-5 minute exposures with star tracker"
        }

        val altitudeNote = when {
            altitude < 30 -> " Low altitude - atmospheric effects will reduce contrast."
            altitude > 60 -> " Excellent altitude for optimal imaging quality."
            else -> ""
        }

        return baseNote + altitudeNote
    }

    private fun performCoordinateTransformation(
        fromType: CoordinateType,
        toType: CoordinateType,
        coordinate1: Double,
        coordinate2: Double,
        time: Time,
        observer: Observer
    ): Pair<Double, Double> {
        // Convert to equatorial first
        val (ra, dec) = convertToEquatorial(fromType, coordinate1, coordinate2, time, observer)

        // Then convert from equatorial to target type
        return convertFromEquatorial(toType, ra, dec, time, observer)
    }

    private fun convertToEquatorial(
        fromType: CoordinateType,
        coord1: Double,
        coord2: Double,
        time: Time,
        observer: Observer
    ): Pair<Double, Double> {
        return when (fromType) {
            CoordinateType.Equatorial -> Pair(coord1, coord2)
            CoordinateType.AltitudeAzimuth -> convertHorizontalToEquatorial(coord1, coord2, time, observer)
            CoordinateType.Galactic -> convertGalacticToEquatorial(coord1, coord2)
            CoordinateType.Ecliptic -> convertEclipticToEquatorial(coord1, coord2, time)
        }
    }

    private fun convertFromEquatorial(
        toType: CoordinateType,
        ra: Double,
        dec: Double,
        time: Time,
        observer: Observer
    ): Pair<Double, Double> {
        return when (toType) {
            CoordinateType.Equatorial -> Pair(ra, dec)
            CoordinateType.AltitudeAzimuth -> convertEquatorialToHorizontal(ra, dec, time, observer)
            CoordinateType.Galactic -> convertEquatorialToGalactic(ra, dec)
            CoordinateType.Ecliptic -> convertEquatorialToEcliptic(ra, dec, time)
        }
    }

    // Coordinate conversion helper methods
    private fun convertHorizontalToEquatorial(azimuth: Double, altitude: Double, time: Time, observer: Observer): Pair<Double, Double> {
        // Use spherical trigonometry to convert horizontal to equatorial coordinates
        val azRad = azimuth * PI / 180.0
        val altRad = altitude * PI / 180.0
        val latRad = observer.latitude * PI / 180.0

        // Calculate declination
        val decRad = asin(sin(altRad) * sin(latRad) + cos(altRad) * cos(latRad) * cos(azRad))

        // Calculate hour angle
        val hourAngleRad = atan2(-sin(azRad) * cos(altRad), cos(latRad) * sin(altRad) - sin(latRad) * cos(altRad) * cos(azRad))

        // Convert to right ascension using local sidereal time
        val lst = siderealTime(time)
        val ra = (lst - hourAngleRad * 12.0 / PI) % 24.0
        val dec = decRad * 180.0 / PI

        return Pair(ra, dec)
    }

    private fun convertEquatorialToHorizontal(ra: Double, dec: Double, time: Time, observer: Observer): Pair<Double, Double> {
        val horizontal = horizon(time, observer, ra, dec, Refraction.Normal)
        return Pair(horizontal.azimuth, horizontal.altitude)
    }

    private fun convertGalacticToEquatorial(galLon: Double, galLat: Double): Pair<Double, Double> {
        // Convert galactic coordinates to J2000 equatorial
        val galLonRad = galLon * PI / 180.0
        val galLatRad = galLat * PI / 180.0

        // Galactic pole coordinates (J2000)
        val ngpRa = 192.8594813 * PI / 180.0  // 12h 51m 26.28s
        val ngpDec = 27.1283436 * PI / 180.0  // 27° 07' 42.0"
        val galCenter = 122.9319185 * PI / 180.0  // Galactic center longitude

        // Spherical trigonometry conversion
        val decRad = asin(sin(galLatRad) * sin(ngpDec) + cos(galLatRad) * cos(ngpDec) * cos(galLonRad - galCenter))
        val raRad = atan2(cos(galLatRad) * sin(galLonRad - galCenter),
            sin(galLatRad) * cos(ngpDec) - cos(galLatRad) * sin(ngpDec) * cos(galLonRad - galCenter)) + ngpRa

        val ra = (raRad * 180.0 / PI) / 15.0  // Convert to hours
        val dec = decRad * 180.0 / PI

        return Pair(ra, dec)
    }

    private fun convertEquatorialToGalactic(ra: Double, dec: Double): Pair<Double, Double> {
        // Convert J2000 equatorial to galactic coordinates
        val raRad = ra * 15.0 * PI / 180.0  // Convert hours to radians
        val decRad = dec * PI / 180.0

        // Galactic pole coordinates (J2000)
        val ngpRa = 192.8594813 * PI / 180.0
        val ngpDec = 27.1283436 * PI / 180.0
        val galCenter = 122.9319185 * PI / 180.0

        // Spherical trigonometry conversion
        val galLatRad = asin(sin(decRad) * sin(ngpDec) + cos(decRad) * cos(ngpDec) * cos(raRad - ngpRa))
        val galLonRad = atan2(cos(decRad) * sin(raRad - ngpRa),
            sin(decRad) * cos(ngpDec) - cos(decRad) * sin(ngpDec) * cos(raRad - ngpRa)) + galCenter

        val galLon = (galLonRad * 180.0 / PI) % 360.0
        val galLat = galLatRad * 180.0 / PI

        return Pair(galLon, galLat)
    }

    private fun convertEclipticToEquatorial(eclLon: Double, eclLat: Double, time: Time): Pair<Double, Double> {
        // Convert ecliptic coordinates to equatorial using obliquity of the ecliptic
        val eclLonRad = eclLon * PI / 180.0
        val eclLatRad = eclLat * PI / 180.0

        // Calculate obliquity of the ecliptic for the given time
        val obliquity = 23.43929111 * PI / 180.0  // Approximate obliquity

        // Spherical trigonometry conversion
        val decRad = asin(sin(eclLatRad) * cos(obliquity) + cos(eclLatRad) * sin(obliquity) * sin(eclLonRad))
        val raRad = atan2(cos(eclLatRad) * cos(eclLonRad), cos(eclLatRad) * sin(eclLonRad) * cos(obliquity) - sin(eclLatRad) * sin(obliquity))

        val ra = (raRad * 180.0 / PI) / 15.0  // Convert to hours
        val dec = decRad * 180.0 / PI

        return Pair(ra, dec)
    }

    private fun convertEquatorialToEcliptic(ra: Double, dec: Double, time: Time): Pair<Double, Double> {
        // Convert equatorial coordinates to ecliptic using obliquity of the ecliptic
        val raRad = ra * 15.0 * PI / 180.0  // Convert hours to radians
        val decRad = dec * PI / 180.0

        // Calculate obliquity of the ecliptic for the given time
        val obliquity = 23.43929111 * PI / 180.0  // Approximate obliquity

        // Spherical trigonometry conversion
        val eclLatRad = asin(sin(decRad) * cos(obliquity) - cos(decRad) * sin(obliquity) * sin(raRad))
        val eclLonRad = atan2(cos(decRad) * sin(raRad) * cos(obliquity) + sin(decRad) * sin(obliquity), cos(decRad) * cos(raRad))

        val eclLon = (eclLonRad * 180.0 / PI) % 360.0
        val eclLat = eclLatRad * 180.0 / PI

        return Pair(eclLon, eclLat)
    }

    private fun calculateAtmosphericRefraction(altitude: Double, temperature: Double, pressure: Double, humidity: Double): Double {
        if (altitude <= 0) return 0.0

        val altitudeRad = altitude * PI / 180.0
        val refraction = (pressure / (1013.25 * (283.0 / temperature) * 1.02 / tan(altitudeRad + 10.3 / (altitude + 5.11))))
        return refraction * 60.0 // Convert to arc minutes
    }

    private fun calculateAtmosphericExtinction(altitude: Double, humidity: Double): Double {
        if (altitude <= 0) return 5.0 // Very high extinction below horizon

        val airMass = 1.0 / sin(altitude * PI / 180.0)
        val extinction = 0.2 * airMass * (1.0 + humidity / 100.0)
        return extinction.coerceAtMost(5.0)
    }

    private fun generateAtmosphericCorrectionNotes(altitude: Double, refraction: Double, extinction: Double): String {
        return when {
            altitude < 10 -> "Very high atmospheric effects. Refraction: ${refraction.toInt()}'. Consider observing when target is higher."
            altitude < 30 -> "Moderate atmospheric refraction and extinction. Some image quality degradation expected."
            else -> "Minimal atmospheric effects at this altitude."
        }
    }

    // Helper methods for conjunction and opposition calculations
    private fun findConjunctionEvents(body1: Body, body2: Body, startDate: Instant, endDate: Instant, latitude: Double, longitude: Double): List<ConjunctionEvent> {
        val events = mutableListOf<ConjunctionEvent>()
        val observer = Observer(latitude, longitude, 0.0)

        // Search for conjunctions by checking angular separation over time
        var currentDate = startDate
        val endTime = endDate.toEpochMilliseconds()
        val stepSize = 24 * 60 * 60 * 1000L // 1 day in milliseconds

        while (currentDate.toEpochMilliseconds() < endTime) {
            try {
                val time = Time.fromInstant(currentDate)
                val separation = calculateAngularSeparation(body1, body2, time, observer)

                if (separation < 10.0) { // Within 10 degrees
                    val altitude1 = equator(body1, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
                    val horizontal1 = horizon(time, observer, altitude1.ra, altitude1.dec, Refraction.Normal)

                    events.add(ConjunctionEvent(
                        dateTime = currentDate,
                        separation = separation,
                        isVisible = horizontal1.altitude > 0
                    ))
                }

                currentDate = Instant.fromEpochMilliseconds(currentDate.toEpochMilliseconds() + stepSize)
            } catch (ex: Exception) {
                logger.w(ex) { "Error calculating conjunction for date $currentDate" }
                currentDate = Instant.fromEpochMilliseconds(currentDate.toEpochMilliseconds() + stepSize)
            }
        }

        return events
    }

    private fun calculateAngularSeparation(body1: Body, body2: Body, time: Time, observer: Observer): Double {
        val pos1 = equator(body1, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
        val pos2 = equator(body2, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)

        // Convert to radians
        val ra1 = pos1.ra * PI / 12.0 // Hours to radians
        val dec1 = pos1.dec * PI / 180.0 // Degrees to radians
        val ra2 = pos2.ra * PI / 12.0
        val dec2 = pos2.dec * PI / 180.0

        // Calculate angular separation using spherical law of cosines
        val cosTheta = sin(dec1) * sin(dec2) + cos(dec1) * cos(dec2) * cos(ra1 - ra2)
        val theta = acos(cosTheta.coerceIn(-1.0, 1.0))

        return theta * 180.0 / PI // Convert to degrees
    }

    private fun findOppositionEvents(body: Body, startDate: Instant, endDate: Instant): List<OppositionEvent> {
        val events = mutableListOf<OppositionEvent>()

        // Search for oppositions by checking heliocentric longitude difference with Earth
        var currentDate = startDate
        val endTime = endDate.toEpochMilliseconds()
        val stepSize = 7 * 24 * 60 * 60 * 1000L // 1 week in milliseconds

        while (currentDate.toEpochMilliseconds() < endTime) {
            try {
                val time = Time.fromInstant(currentDate)

                // Get heliocentric positions
                val earthPos = helioVector(Body.Earth, time)
                val planetPos = helioVector(body, time)

                // Calculate longitude difference
                val earthLon = atan2(earthPos.y, earthPos.x)
                val planetLon = atan2(planetPos.y, planetPos.x)
                val diff = abs(earthLon - planetLon) * 180.0 / PI

                // Opposition occurs when difference is near 180 degrees
                if (abs(diff - 180.0) < 5.0) {
                    val illumination = illumination(body, time)

                    events.add(OppositionEvent(
                        dateTime = currentDate,
                        magnitude = illumination.mag
                    ))
                }

                currentDate = Instant.fromEpochMilliseconds(currentDate.toEpochMilliseconds() + stepSize)
            } catch (ex: Exception) {
                logger.w(ex) { "Error calculating opposition for date $currentDate" }
                currentDate = Instant.fromEpochMilliseconds(currentDate.toEpochMilliseconds() + stepSize)
            }
        }

        return events
    }

    private fun getConjunctionPhotographyNotes(planet1: PlanetType, planet2: PlanetType, separation: Double): String {
        return "Conjunction of $planet1 and $planet2. Separation: ${separation.toInt()}°. Use wide-angle lens to capture both objects."
    }

    private fun getOppositionPhotographyNotes(planet: PlanetType): String {
        return "Opposition of $planet. Best viewing and photography opportunity. Planet is closest to Earth."
    }

    // Data classes for internal calculations
    private data class ConstellationCoords(val ra: Double, val dec: Double)
    private data class RiseSetTransitTimes(val rise: Instant?, val set: Instant?, val transit: Instant?)
    private data class DeepSkyObjectInfo(
        val commonName: String,
        val objectType: String,
        val catalogId: String,
        val rightAscension: Double,
        val declination: Double,
        val magnitude: Double,
        val angularSize: Double,
        val parentConstellation: ConstellationType
    )
    private data class ConjunctionEvent(val dateTime: Instant, val separation: Double, val isVisible: Boolean)
    private data class OppositionEvent(val dateTime: Instant, val magnitude: Double)

    // Extension functions for CosineKitty types
    private fun Time.toInstant(): Instant {
        // Convert Time to Instant - J2000 epoch is January 1, 2000, 12:00 UTC
        return Instant.fromEpochMilliseconds((this.ut * 86400000.0).toLong() + 946728000000L)
    }

    private fun Time.Companion.fromInstant(instant: Instant): Time {
        // Convert Instant to Time - J2000 epoch is January 1, 2000, 12:00 UTC
        val j2000Millis = instant.toEpochMilliseconds() - 946728000000L
        return Time(j2000Millis / 86400000.0) // Convert to days
    }
}