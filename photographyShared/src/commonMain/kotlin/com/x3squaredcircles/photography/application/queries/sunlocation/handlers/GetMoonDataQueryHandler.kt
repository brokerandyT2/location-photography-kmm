package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetMoonDataQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetMoonDataQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.MoonPhaseDataDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

class GetMoonDataQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetMoonDataQuery, GetMoonDataQueryResult> {

    override suspend fun handle(query: GetMoonDataQuery): Result<GetMoonDataQueryResult> {
        logger.d { "Handling GetMoonDataQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val timezone = TimeZone.currentSystemDefault().toString()

            val phase = calculateMoonPhase(query.date)
            val phaseName = getMoonPhaseName(phase)
            val illuminationPercentage = calculateMoonIllumination(phase)

            val moonData = MoonPhaseDataDto(
                date = query.date,
                phase = phase,
                phaseName = phaseName,
                illuminationPercentage = illuminationPercentage,
                moonRise = sunCalculatorService.getMoonrise(query.date, query.latitude, query.longitude, timezone),
                moonSet = sunCalculatorService.getMoonset(query.date, query.latitude, query.longitude, timezone),
                azimuth = sunCalculatorService.getMoonAzimuth(query.date, query.latitude, query.longitude, timezone),
                elevation = sunCalculatorService.getMoonElevation(query.date, query.latitude, query.longitude, timezone),
                distance = sunCalculatorService.getMoonDistance(query.date, query.latitude, query.longitude, timezone),
                brightness = calculateMoonBrightness(phase)
            )

            logger.i { "Retrieved moon data for ${query.date} - phase: $phaseName (${illuminationPercentage}%)" }
            Result.success(
                GetMoonDataQueryResult(
                    moonData = moonData,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating moon data for coordinates ${query.latitude}, ${query.longitude} on ${query.date}" }
            Result.success(
                GetMoonDataQueryResult(
                    moonData = MoonPhaseDataDto(
                        date = query.date,
                        phase = 0.0,
                        phaseName = "Unknown",
                        illuminationPercentage = 0.0,
                        moonRise = null,
                        moonSet = null,
                        azimuth = 0.0,
                        elevation = 0.0,
                        distance = 384400.0,
                        brightness = 0.0
                    ),
                    isSuccess = false,
                    errorMessage = "Error calculating moon data: ${ex.message}"
                )
            )
        }
    }

    private fun calculateMoonPhase(date: Instant): Double {
        val newMoonDate = Instant.fromEpochMilliseconds(1704931200000L) // 2024-01-11
        val daysSinceNewMoon = (date.toEpochMilliseconds() - newMoonDate.toEpochMilliseconds()) / (24 * 60 * 60 * 1000.0)
        val lunarCycle = 29.53058867
        val phase = (daysSinceNewMoon % lunarCycle) / lunarCycle
        return if (phase < 0) phase + 1 else phase
    }

    private fun getMoonPhaseName(phase: Double): String {
        return when {
            phase < 0.03 || phase > 0.97 -> "New Moon"
            phase < 0.22 -> "Waxing Crescent"
            phase < 0.28 -> "First Quarter"
            phase < 0.47 -> "Waxing Gibbous"
            phase < 0.53 -> "Full Moon"
            phase < 0.72 -> "Waning Gibbous"
            phase < 0.78 -> "Third Quarter"
            else -> "Waning Crescent"
        }
    }

    private fun calculateMoonIllumination(phase: Double): Double {
        return if (phase <= 0.5) phase * 2 * 100 else (1 - phase) * 2 * 100
    }

    private fun calculateMoonBrightness(phase: Double): Double {
        return -12.74 + 0.026 * abs(phase - 0.5) * 180
    }
}