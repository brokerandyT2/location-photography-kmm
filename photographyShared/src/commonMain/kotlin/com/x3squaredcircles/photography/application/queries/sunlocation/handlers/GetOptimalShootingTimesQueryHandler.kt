package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetOptimalShootingTimesQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetOptimalShootingTimesQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.OptimalShootingTimeDto
import com.x3squaredcircles.photography.application.queries.sunlocation.LightQuality
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class GetOptimalShootingTimesQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetOptimalShootingTimesQuery, GetOptimalShootingTimesQueryResult> {

    override suspend fun handle(query: GetOptimalShootingTimesQuery): Result<GetOptimalShootingTimesQueryResult> {
        logger.d { "Handling GetOptimalShootingTimesQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val optimalTimes = mutableListOf<OptimalShootingTimeDto>()

            val sunrise = sunCalculatorService.getSunrise(query.date, query.latitude, query.longitude, query.timezone)
            val sunset = sunCalculatorService.getSunset(query.date, query.latitude, query.longitude, query.timezone)
            val solarNoon = sunCalculatorService.getSolarNoon(query.date, query.latitude, query.longitude, query.timezone)
            val civilDawn = sunCalculatorService.getCivilDawn(query.date, query.latitude, query.longitude, query.timezone)
            val civilDusk = sunCalculatorService.getCivilDusk(query.date, query.latitude, query.longitude, query.timezone)
            val nauticalDawn = sunCalculatorService.getNauticalDawn(query.date, query.latitude, query.longitude, query.timezone)
            val nauticalDusk = sunCalculatorService.getNauticalDusk(query.date, query.latitude, query.longitude, query.timezone)

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunrise,
                endTime = sunrise.plus(30.minutes),
                lightQuality = LightQuality.GOLDEN_HOUR,
                qualityScore = 0.95,
                description = "Sunrise",
                idealFor = listOf("Sunrise Photography", "Landscapes", "Portraits")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunrise.plus(30.minutes),
                endTime = sunrise.plus(1.hours),
                lightQuality = LightQuality.GOLDEN_HOUR,
                qualityScore = 0.95,
                description = "Golden Hour Morning",
                idealFor = listOf("Portraits", "Landscapes", "Golden Hour")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = solarNoon,
                endTime = solarNoon.plus(30.minutes),
                lightQuality = LightQuality.HARSH,
                qualityScore = 0.4,
                description = "Solar Noon",
                idealFor = listOf("Architecture", "Minimal Shadows")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunset.minus(1.hours),
                endTime = sunset.minus(30.minutes),
                lightQuality = LightQuality.GOLDEN_HOUR,
                qualityScore = 0.95,
                description = "Golden Hour Evening",
                idealFor = listOf("Portraits", "Landscapes", "Golden Hour")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunset.minus(30.minutes),
                endTime = sunset,
                lightQuality = LightQuality.GOLDEN_HOUR,
                qualityScore = 0.95,
                description = "Sunset",
                idealFor = listOf("Sunset Photography", "Landscapes", "Silhouettes")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunset,
                endTime = sunset.plus(1.hours),
                lightQuality = LightQuality.BLUE_HOUR,
                qualityScore = 0.9,
                description = "Blue Hour Evening",
                idealFor = listOf("Blue Hour", "Cityscapes", "Architecture")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = civilDusk,
                endTime = nauticalDusk,
                lightQuality = LightQuality.BLUE_HOUR,
                qualityScore = 0.85,
                description = "Dusk",
                idealFor = listOf("Blue Hour", "Night Cityscapes")
            ))

            optimalTimes.add(OptimalShootingTimeDto(
                startTime = sunrise.minus(1.hours),
                endTime = sunrise,
                lightQuality = LightQuality.BLUE_HOUR,
                qualityScore = 0.9,
                description = "Blue Hour Morning",
                idealFor = listOf("Blue Hour", "Cityscapes", "Landscapes")
            ))

            val sortedTimes = optimalTimes.sortedBy { it.startTime }

            logger.i { "Retrieved ${sortedTimes.size} optimal shooting times" }
            Result.success(
                GetOptimalShootingTimesQueryResult(
                    optimalTimes = sortedTimes,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating optimal shooting times for coordinates ${query.latitude}, ${query.longitude} on ${query.date}" }
            Result.success(
                GetOptimalShootingTimesQueryResult(
                    optimalTimes = emptyList(),
                    isSuccess = false,
                    errorMessage = "Error calculating optimal times: ${ex.message}"
                )
            )
        }
    }
}