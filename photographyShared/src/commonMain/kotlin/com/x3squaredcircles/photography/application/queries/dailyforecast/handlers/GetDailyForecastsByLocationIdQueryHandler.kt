// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetDailyForecastsByLocationIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByLocationIdQuery, GetDailyForecastsByLocationIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByLocationIdQuery): GetDailyForecastsByLocationIdQueryResult {
        logger.d { "Handling GetDailyForecastsByLocationIdQuery with locationId: ${query.locationId}" }

        return when (val result = dailyForecastRepository.getByLocationIdAsync(query.locationId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} daily forecasts for locationId: ${query.locationId}" }
                GetDailyForecastsByLocationIdQueryResult(
                    dailyForecasts = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get daily forecasts by location id: ${query.locationId} - ${result.error}" }
                GetDailyForecastsByLocationIdQueryResult(
                    dailyForecasts = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}