// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsForDayQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsForDayQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsForDayQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetHourlyForecastsForDayQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsForDayQuery, GetHourlyForecastsForDayQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsForDayQuery): Result<GetHourlyForecastsForDayQueryResult> {
        logger.d { "Handling GetHourlyForecastsForDayQuery with weatherId: ${query.weatherId}, startTime: ${query.startTime}, endTime: ${query.endTime}" }

        return try {
            val startTime = Instant.fromEpochMilliseconds(query.startTime)
            val endTime = Instant.fromEpochMilliseconds(query.endTime)

            when (val result = hourlyForecastRepository.getByWeatherAndTimeRangeAsync(query.weatherId, startTime, endTime)) {
                is Result.Success -> {
                    logger.i { "Retrieved ${result.data.size} hourly forecasts for day - weatherId: ${query.weatherId}" }
                    Result.success(
                        GetHourlyForecastsForDayQueryResult(
                            hourlyForecasts = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get hourly forecasts for day - weatherId: ${query.weatherId} - ${result.error}" }
                    Result.success(
                        GetHourlyForecastsForDayQueryResult(
                            hourlyForecasts = emptyList(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create times or get hourly forecasts for day - weatherId: ${query.weatherId}" }
            Result.success(
                GetHourlyForecastsForDayQueryResult(
                    hourlyForecasts = emptyList(),
                    isSuccess = false,
                    errorMessage = ex.message ?: "Unknown error occurred"
                )
            )
        }
    }
}