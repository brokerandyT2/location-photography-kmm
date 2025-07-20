// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetNext24HoursHourlyForecastsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetNext24HoursHourlyForecastsQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetNext24HoursHourlyForecastsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetNext24HoursHourlyForecastsQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetNext24HoursHourlyForecastsQuery, GetNext24HoursHourlyForecastsQueryResult> {

    override suspend fun handle(query: GetNext24HoursHourlyForecastsQuery): Result<GetNext24HoursHourlyForecastsQueryResult> {
        logger.d { "Handling GetNext24HoursHourlyForecastsQuery with weatherId: ${query.weatherId}, startTime: ${query.startTime}" }

        return try {
            val startTime = Instant.fromEpochMilliseconds(query.startTime)

            when (val result = hourlyForecastRepository.getNext24HoursAsync(query.weatherId, startTime)) {
                is Result.Success -> {
                    logger.i { "Retrieved ${result.data.size} hourly forecasts for next 24 hours - weatherId: ${query.weatherId}" }
                    Result.success(
                        GetNext24HoursHourlyForecastsQueryResult(
                            hourlyForecasts = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get next 24 hours hourly forecasts - weatherId: ${query.weatherId} - ${result.error}" }
                    Result.success(
                        GetNext24HoursHourlyForecastsQueryResult(
                            hourlyForecasts = emptyList(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create start time or get next 24 hours hourly forecasts - weatherId: ${query.weatherId}" }
            Result.success(
                GetNext24HoursHourlyForecastsQueryResult(
                    hourlyForecasts = emptyList(),
                    isSuccess = false,
                    errorMessage = ex.message ?: "Unknown error occurred"
                )
            )
        }
    }
}