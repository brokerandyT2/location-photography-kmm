// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsForDayQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsForDayQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsForDayQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetHourlyForecastsForDayQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsForDayQuery, GetHourlyForecastsForDayQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsForDayQuery): GetHourlyForecastsForDayQueryResult {
        return try {
            logger.d { "Handling GetHourlyForecastsForDayQuery with weatherId: ${query.weatherId}, startTime: ${query.startTime}, endTime: ${query.endTime}" }

            val startTime = Instant.fromEpochMilliseconds(query.startTime)
            val endTime = Instant.fromEpochMilliseconds(query.endTime)
            val hourlyForecasts = hourlyForecastRepository.getByWeatherAndTimeRangeAsync(query.weatherId, startTime, endTime)

            logger.i { "Retrieved ${hourlyForecasts.size} hourly forecasts for day - weatherId: ${query.weatherId}" }

            GetHourlyForecastsForDayQueryResult(
                hourlyForecasts = hourlyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get hourly forecasts for day - weatherId: ${query.weatherId}" }
            GetHourlyForecastsForDayQueryResult(
                hourlyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}