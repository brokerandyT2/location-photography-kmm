// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastByIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetHourlyForecastByIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastByIdQuery, GetHourlyForecastByIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastByIdQuery): GetHourlyForecastByIdQueryResult {
        logger.d { "Handling GetHourlyForecastByIdQuery with id: ${query.id}" }

        return when (val result = hourlyForecastRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved hourly forecast with id: ${query.id}, found: ${result.data != null}" }
                GetHourlyForecastByIdQueryResult(
                    hourlyForecast = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get hourly forecast by id: ${query.id} - ${result.error}" }
                GetHourlyForecastByIdQueryResult(
                    hourlyForecast = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}