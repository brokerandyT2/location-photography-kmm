// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetHourlyForecastsByLocationIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsByLocationIdQuery, GetHourlyForecastsByLocationIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsByLocationIdQuery): Result<GetHourlyForecastsByLocationIdQueryResult> {
        logger.d { "Handling GetHourlyForecastsByLocationIdQuery with locationId: ${query.locationId}" }

        return when (val result = hourlyForecastRepository.getByLocationIdAsync(query.locationId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} hourly forecasts for locationId: ${query.locationId}" }
                Result.success(
                    GetHourlyForecastsByLocationIdQueryResult(
                        hourlyForecasts = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get hourly forecasts by location id: ${query.locationId} - ${result.error}" }
                Result.success(
                    GetHourlyForecastsByLocationIdQueryResult(
                        hourlyForecasts = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}