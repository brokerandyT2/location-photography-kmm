// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetAllHourlyForecastsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetAllHourlyForecastsQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetAllHourlyForecastsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllHourlyForecastsQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetAllHourlyForecastsQuery, GetAllHourlyForecastsQueryResult> {

    override suspend fun handle(query: GetAllHourlyForecastsQuery): Result<GetAllHourlyForecastsQueryResult> {
        logger.d { "Handling GetAllHourlyForecastsQuery" }

        return when (val result = hourlyForecastRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} hourly forecasts" }
                Result.success(
                    GetAllHourlyForecastsQueryResult(
                        hourlyForecasts = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all hourly forecasts: ${result.error}" }
                Result.success(
                    GetAllHourlyForecastsQueryResult(
                        hourlyForecasts = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}