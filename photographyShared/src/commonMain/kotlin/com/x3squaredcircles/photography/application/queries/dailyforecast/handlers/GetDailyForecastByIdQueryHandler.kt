// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetDailyForecastByIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastByIdQuery, GetDailyForecastByIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastByIdQuery): Result<GetDailyForecastByIdQueryResult> {
        logger.d { "Handling GetDailyForecastByIdQuery with id: ${query.id}" }

        return when (val result = dailyForecastRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved daily forecast with id: ${query.id}, found: ${result.data != null}" }
                Result.success(
                    GetDailyForecastByIdQueryResult(
                        dailyForecast = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get daily forecast by id: ${query.id} - ${result.error}" }
                Result.success(
                    GetDailyForecastByIdQueryResult(
                        dailyForecast = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}