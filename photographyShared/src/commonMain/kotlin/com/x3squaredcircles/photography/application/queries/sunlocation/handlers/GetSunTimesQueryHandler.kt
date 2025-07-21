package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetSunTimesQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetSunTimesQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.SunTimesDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.TimeZone

class GetSunTimesQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetSunTimesQuery, GetSunTimesQueryResult> {

    override suspend fun handle(query: GetSunTimesQuery): Result<GetSunTimesQueryResult> {
        logger.d { "Handling GetSunTimesQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val timezone = TimeZone.currentSystemDefault().toString()

            val sunTimes = SunTimesDto(
                date = query.date,
                latitude = query.latitude,
                longitude = query.longitude,
                sunrise = sunCalculatorService.getSunrise(query.date, query.latitude, query.longitude, timezone),
                sunset = sunCalculatorService.getSunset(query.date, query.latitude, query.longitude, timezone),
                solarNoon = sunCalculatorService.getSolarNoon(query.date, query.latitude, query.longitude, timezone),
                astronomicalDawn = sunCalculatorService.getAstronomicalDawn(query.date, query.latitude, query.longitude, timezone),
                astronomicalDusk = sunCalculatorService.getAstronomicalDusk(query.date, query.latitude, query.longitude, timezone),
                nauticalDawn = sunCalculatorService.getNauticalDawn(query.date, query.latitude, query.longitude, timezone),
                nauticalDusk = sunCalculatorService.getNauticalDusk(query.date, query.latitude, query.longitude, timezone),
                civilDawn = sunCalculatorService.getCivilDawn(query.date, query.latitude, query.longitude, timezone),
                civilDusk = sunCalculatorService.getCivilDusk(query.date, query.latitude, query.longitude, timezone),
                goldenHourMorningStart = sunCalculatorService.getGoldenHourStart(query.date, query.latitude, query.longitude, timezone),
                goldenHourMorningEnd = sunCalculatorService.getGoldenHourEnd(query.date, query.latitude, query.longitude, timezone),
                goldenHourEveningStart = sunCalculatorService.getGoldenHourStart(query.date, query.latitude, query.longitude, timezone),
                goldenHourEveningEnd = sunCalculatorService.getGoldenHourEnd(query.date, query.latitude, query.longitude, timezone)
            )

            logger.i { "Retrieved sun times for ${query.date}" }
            Result.success(
                GetSunTimesQueryResult(
                    sunTimes = sunTimes,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating sun times for coordinates ${query.latitude}, ${query.longitude} on ${query.date}" }
            Result.success(
                GetSunTimesQueryResult(
                    sunTimes = SunTimesDto(
                        date = query.date,
                        latitude = query.latitude,
                        longitude = query.longitude,
                        sunrise = query.date,
                        sunset = query.date,
                        solarNoon = query.date,
                        astronomicalDawn = query.date,
                        astronomicalDusk = query.date,
                        nauticalDawn = query.date,
                        nauticalDusk = query.date,
                        civilDawn = query.date,
                        civilDusk = query.date,
                        goldenHourMorningStart = query.date,
                        goldenHourMorningEnd = query.date,
                        goldenHourEveningStart = query.date,
                        goldenHourEveningEnd = query.date
                    ),
                    isSuccess = false,
                    errorMessage = "Error calculating sun times: ${ex.message}"
                )
            )
        }
    }
}