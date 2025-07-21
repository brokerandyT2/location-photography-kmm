package com.x3squaredcircles.photography.application.queries.sunlocation.handlers


import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.IAstroCalculationService
import com.x3squaredcircles.photography.domain.enums.AstroTarget
import com.x3squaredcircles.photography.domain.enums.PlanetType
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.application.queries.astrolocation.AstroEventDto
import com.x3squaredcircles.photography.application.queries.astrolocation.GetAstroEventsForDateQuery
import com.x3squaredcircles.photography.application.queries.astrolocation.GetAstroEventsForDateQueryResult
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.hours

class GetAstroEventsForDateQueryHandler(
    private val astroCalculationService: IAstroCalculationService,
    private val logger: Logger
) : IQueryHandler<GetAstroEventsForDateQuery, GetAstroEventsForDateQueryResult> {

    override suspend fun handle(query: GetAstroEventsForDateQuery): Result<GetAstroEventsForDateQueryResult> {
        logger.d { "Handling GetAstroEventsForDateQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val events = mutableListOf<AstroEventDto>()
            val eventDate = query.date.plus(20.hours) // Start at 8 PM local time

            // Get Moon data
            when (val moonResult = astroCalculationService.getEnhancedMoonDataAsync(eventDate, query.latitude, query.longitude)) {
                is Result.Success -> {
                    val moonData = moonResult.data
                    if (moonData.altitude > query.minimumAltitude || query.includeDayTimeEvents) {
                        events.add(createMoonEvent(moonData))
                    }
                }
                is Result.Failure -> {
                    logger.w { "Failed to get moon data: ${moonResult.error}" }
                }
            }

            // Get visible planets
            val planetTypes = listOf(PlanetType.Venus, PlanetType.Mars, PlanetType.Jupiter, PlanetType.Saturn)
            for (planetType in planetTypes) {
                when (val planetResult = astroCalculationService.getPlanetPositionAsync(planetType, eventDate, query.latitude, query.longitude)) {
                    is Result.Success -> {
                        val planetData = planetResult.data
                        if (planetData.isVisible && (planetData.altitude > query.minimumAltitude || query.includeDayTimeEvents)) {
                            events.add(createPlanetEvent(planetData))
                        }
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to get planet data for $planetType: ${planetResult.error}" }
                    }
                }
            }

            val sortedEvents = events.sortedWith(
                compareByDescending<AstroEventDto> { calculateVisibilityScore(it) }
                    .thenBy { getOptimalTime(it) }
            )

            logger.i { "Retrieved ${sortedEvents.size} astro events for ${query.date}" }
            Result.success(
                GetAstroEventsForDateQueryResult(
                    events = sortedEvents,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving astro events for ${query.date}" }
            Result.success(
                GetAstroEventsForDateQueryResult(
                    events = emptyList(),
                    isSuccess = false,
                    errorMessage = "Error retrieving astro events: ${ex.message}"
                )
            )
        }
    }

    private fun createMoonEvent(moonData: com.x3squaredcircles.photography.domain.models.EnhancedMoonData): AstroEventDto {
        return AstroEventDto(
            name = "Moon (${moonData.phaseName})",
            target = AstroTarget.Moon,
            startTime = moonData.rise ?: moonData.dateTime,
            endTime = moonData.set ?: moonData.dateTime.plus(12.hours),
            peakTime = moonData.transit,
            azimuth = moonData.azimuth,
            altitude = moonData.altitude,
            magnitude = -12.7, // Approximate moon magnitude
            description = "Moon phase: ${moonData.phaseName}, Illumination: ${moonData.illumination}%, Distance: ${moonData.distance}km",
            constellation = "",
            isVisible = moonData.altitude > 0,
            eventType = moonData.phaseName,
            angularSize = moonData.angularDiameter,
            recommendedEquipment = "Telephoto lens or telescope"
        )
    }

    private fun createPlanetEvent(planetData: com.x3squaredcircles.photography.domain.models.PlanetPositionData): AstroEventDto {
        return AstroEventDto(
            name = getPlanetDisplayName(planetData.planet),
            target = AstroTarget.Planets,
            startTime = planetData.rise ?: planetData.dateTime,
            endTime = planetData.set ?: planetData.dateTime.plus(12.hours),
            peakTime = planetData.transit,
            azimuth = planetData.azimuth,
            altitude = planetData.altitude,
            magnitude = planetData.apparentMagnitude,
            description = "Planet magnitude: ${planetData.apparentMagnitude}, Angular diameter: ${planetData.angularDiameter}\"",
            constellation = "",
            isVisible = planetData.isVisible,
            eventType = "Planet",
            angularSize = planetData.angularDiameter,
            recommendedEquipment = planetData.recommendedEquipment
        )
    }

    private fun calculateVisibilityScore(event: AstroEventDto): Double {
        if (!event.isVisible) return 0.0

        var score = 0.5 // Base score for being visible

        // Altitude bonus (higher is better)
        when {
            event.altitude > 60 -> score += 0.3
            event.altitude > 30 -> score += 0.2
            event.altitude > 15 -> score += 0.1
        }

        // Magnitude bonus (brighter is better, lower magnitude = brighter)
        when {
            event.magnitude < 0 -> score += 0.2
            event.magnitude < 3 -> score += 0.1
        }

        return minOf(1.0, score)
    }

    private fun getOptimalTime(event: AstroEventDto): Instant {
        return event.peakTime ?: run {
            val span = event.endTime.toEpochMilliseconds() - event.startTime.toEpochMilliseconds()
            Instant.fromEpochMilliseconds(event.startTime.toEpochMilliseconds() + span / 2)
        }
    }

    private fun getPlanetDisplayName(planet: PlanetType): String {
        return when (planet) {
            PlanetType.Mercury -> "Mercury"
            PlanetType.Venus -> "Venus"
            PlanetType.Mars -> "Mars"
            PlanetType.Jupiter -> "Jupiter"
            PlanetType.Saturn -> "Saturn"
            PlanetType.Uranus -> "Uranus"
            PlanetType.Neptune -> "Neptune"
            PlanetType.Pluto -> "Pluto"
        }
    }
}