package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetEnhancedSunTimesQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetEnhancedSunTimesQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.EnhancedSunTimesDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.TimeZone

class GetEnhancedSunTimesQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetEnhancedSunTimesQuery, GetEnhancedSunTimesQueryResult> {

    override suspend fun handle(query: GetEnhancedSunTimesQuery): Result<GetEnhancedSunTimesQueryResult> {
        logger.d { "Handling GetEnhancedSunTimesQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val timezone = TimeZone.currentSystemDefault().toString()

            val enhancedSunTimes = EnhancedSunTimesDto(
                sunrise = sunCalculatorService.getSunrise(query.date, query.latitude, query.longitude, timezone),
                sunset = sunCalculatorService.getSunset(query.date, query.latitude, query.longitude, timezone),
                solarNoon = sunCalculatorService.getSolarNoon(query.date, query.latitude, query.longitude, timezone),
                civilDawn = sunCalculatorService.getCivilDawn(query.date, query.latitude, query.longitude, timezone),
                civilDusk = sunCalculatorService.getCivilDusk(query.date, query.latitude, query.longitude, timezone),
                nauticalDawn = sunCalculatorService.getNauticalDawn(query.date, query.latitude, query.longitude, timezone),
                nauticalDusk = sunCalculatorService.getNauticalDusk(query.date, query.latitude, query.longitude, timezone),
                astronomicalDawn = sunCalculatorService.getAstronomicalDawn(query.date, query.latitude, query.longitude, timezone),
                astronomicalDusk = sunCalculatorService.getAstronomicalDusk(query.date, query.latitude, query.longitude, timezone),
                goldenHourStart = sunCalculatorService.getGoldenHourStart(query.date, query.latitude, query.longitude, timezone),
                goldenHourEnd = sunCalculatorService.getGoldenHourEnd(query.date, query.latitude, query.longitude, timezone),
                blueHourStart = sunCalculatorService.getBlueHourStart(query.date, query.latitude, query.longitude, timezone),
                blueHourEnd = sunCalculatorService.getBlueHourEnd(query.date, query.latitude, query.longitude, timezone)
            )

            logger.i { "Retrieved enhanced sun times for ${query.date}" }
            Result.success(
                GetEnhancedSunTimesQueryResult(
                    sunTimes = enhancedSunTimes,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating enhanced sun times for coordinates ${query.latitude}, ${query.longitude} on ${query.date}" }
            Result.success(
                GetEnhancedSunTimesQueryResult(
                    sunTimes = EnhancedSunTimesDto(
                        sunrise = query.date,
                        sunset = query.date,
                        solarNoon = query.date,
                        civilDawn = query.date,
                        civilDusk = query.date,
                        nauticalDawn = query.date,
                        nauticalDusk = query.date,
                        astronomicalDawn = query.date,
                        astronomicalDusk = query.date,
                        goldenHourStart = query.date,
                        goldenHourEnd = query.date,
                        blueHourStart = query.date,
                        blueHourEnd = query.date
                    ),
                    isSuccess = false,
                    errorMessage = "Error calculating enhanced sun times: ${ex.message}"
                )
            )
        }
    }
}