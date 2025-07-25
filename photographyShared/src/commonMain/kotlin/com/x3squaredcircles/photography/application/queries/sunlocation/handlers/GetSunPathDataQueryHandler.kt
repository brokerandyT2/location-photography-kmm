package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetSunPathDataQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetSunPathDataQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.SunPathDataResultDto
import com.x3squaredcircles.photography.application.queries.sunlocation.SunPathPointDto
import com.x3squaredcircles.photography.application.queries.sunlocation.SunPathMetricsDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.days
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class GetSunPathDataQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetSunPathDataQuery, GetSunPathDataQueryResult> {

    @OptIn(ExperimentalTime::class)
    override suspend fun handle(query: GetSunPathDataQuery): Result<GetSunPathDataQueryResult> {
        logger.d { "Handling GetSunPathDataQuery for coordinates: ${query.latitude}, ${query.longitude} on ${query.date}" }

        return try {
            val timezone = TimeZone.currentSystemDefault().toString()
            val pathPoints = mutableListOf<SunPathPointDto>()

            val startOfDay = query.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.atTime(0, 0).toInstant(TimeZone.currentSystemDefault())
            val endOfDay = startOfDay.plus(1.days)
            val interval = query.intervalMinutes.minutes

            var maxElevation = -90.0
            var maxElevationTime = startOfDay
            var sunriseAzimuth = 0.0
            var sunsetAzimuth = 0.0

            var currentTime = startOfDay
            while (currentTime < endOfDay) {
                val azimuth = sunCalculatorService.getSolarAzimuth(currentTime, query.latitude, query.longitude, timezone)
                val elevation = sunCalculatorService.getSolarElevation(currentTime, query.latitude, query.longitude, timezone)

                pathPoints.add(
                    SunPathPointDto(
                        time = currentTime,
                        azimuth = azimuth,
                        elevation = elevation,
                        isVisible = elevation > 0
                    )
                )

                if (elevation > maxElevation) {
                    maxElevation = elevation
                    maxElevationTime = currentTime
                }

                if (abs(elevation) < 0.5) {
                    val hour = currentTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour
                    if (hour < 12) {
                        sunriseAzimuth = azimuth
                    } else {
                        sunsetAzimuth = azimuth
                    }
                }

                currentTime = currentTime.plus(interval)
            }

            val currentPosition = SunPathPointDto(
                time = Clock.System.now(),
                azimuth = sunCalculatorService.getSolarAzimuth(Clock.System.now(), query.latitude, query.longitude, timezone),
                elevation = sunCalculatorService.getSolarElevation(Clock.System.now(), query.latitude, query.longitude, timezone),
                isVisible = sunCalculatorService.getSolarElevation(Clock.System.now(), query.latitude, query.longitude, timezone) > 0
            )

            val sunrise = sunCalculatorService.getSunrise(query.date, query.latitude, query.longitude, timezone)
            val sunset = sunCalculatorService.getSunset(query.date, query.latitude, query.longitude, timezone)
            val daylightDuration =
                (sunset.toEpochMilliseconds() - sunrise.toEpochMilliseconds()).milliseconds
            val metrics = SunPathMetricsDto(
                daylightDuration = daylightDuration,
                maxElevation = maxElevation,
                maxElevationTime = maxElevationTime,
                sunriseAzimuth = sunriseAzimuth,
                sunsetAzimuth = sunsetAzimuth,
                seasonalNote = generateSeasonalNote(query.date, query.latitude)
            )

            val result = SunPathDataResultDto(
                pathPoints = pathPoints,
                currentPosition = currentPosition,
                date = query.date,
                latitude = query.latitude,
                longitude = query.longitude,
                metrics = metrics
            )

            logger.i { "Generated ${pathPoints.size} sun path points with max elevation: $maxElevation°" }
            Result.success(
                GetSunPathDataQueryResult(
                    sunPathData = result,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating sun path data for coordinates ${query.latitude}, ${query.longitude} on ${query.date}" }
            Result.success(
                GetSunPathDataQueryResult(
                    sunPathData = SunPathDataResultDto(
                        pathPoints = emptyList(),
                        currentPosition = SunPathPointDto(
                            time = Clock.System.now(),
                            azimuth = 0.0,
                            elevation = 0.0,
                            isVisible = false
                        ),
                        date = query.date,
                        latitude = query.latitude,
                        longitude = query.longitude,
                        metrics = SunPathMetricsDto(
                            daylightDuration = kotlin.time.Duration.ZERO,
                            maxElevation = 0.0,
                            maxElevationTime = query.date,
                            sunriseAzimuth = 0.0,
                            sunsetAzimuth = 0.0,
                            seasonalNote = ""
                        )
                    ),
                    isSuccess = false,
                    errorMessage = "Error calculating sun path: ${ex.message}"
                )
            )
        }
    }

    private fun generateSeasonalNote(date: Instant, latitude: Double): String {
        val isNorthernHemisphere = latitude > 0
        val dayOfYear = date.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear

        return when {
            dayOfYear < 80 || dayOfYear > 355 -> {
                if (isNorthernHemisphere) "Winter: Short days, low sun angle"
                else "Summer: Long days, high sun angle"
            }
            dayOfYear in 80..171 -> {
                if (isNorthernHemisphere) "Spring: Days getting longer"
                else "Autumn: Days getting shorter"
            }
            dayOfYear in 172..265 -> {
                if (isNorthernHemisphere) "Summer: Long days, high sun angle"
                else "Winter: Short days, low sun angle"
            }
            else -> {
                if (isNorthernHemisphere) "Autumn: Days getting shorter"
                else "Spring: Days getting longer"
            }
        }
    }
}