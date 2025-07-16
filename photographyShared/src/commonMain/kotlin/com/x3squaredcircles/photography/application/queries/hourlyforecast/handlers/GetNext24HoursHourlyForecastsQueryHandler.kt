// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetNext24HoursHourlyForecastsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetNext24HoursHourlyForecastsQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetNext24HoursHourlyForecastsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetNext24HoursHourlyForecastsQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetNext24HoursHourlyForecastsQuery, GetNext24HoursHourlyForecastsQueryResult> {

    override suspend fun handle(query: GetNext24HoursHourlyForecastsQuery): GetNext24HoursHourlyForecastsQueryResult {
        return try {
            logger.d { "Handling GetNext24HoursHourlyForecastsQuery with weatherId: ${query.weatherId}, startTime: ${query.startTime}, endTime: ${query.endTime}" }

            val startTime = Instant.fromEpochMilliseconds(query.startTime)
            val hourlyForecasts = hourlyForecastRepository.getNext24HoursAsync(query.weatherId, startTime)

            logger.i { "Retrieved ${hourlyForecasts.size} hourly forecasts for next 24 hours - weatherId: ${query.weatherId}" }

            GetNext24HoursHourlyForecastsQueryResult(
                hourlyForecasts = hourlyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get next 24 hours hourly forecasts - weatherId: ${query.weatherId}" }
            GetNext24HoursHourlyForecastsQueryResult(
                hourlyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}