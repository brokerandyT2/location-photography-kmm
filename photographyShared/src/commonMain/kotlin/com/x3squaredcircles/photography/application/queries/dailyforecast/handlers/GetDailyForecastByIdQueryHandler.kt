// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import co.touchlab.kermit.Logger

class GetDailyForecastByIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastByIdQuery, GetDailyForecastByIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastByIdQuery): GetDailyForecastByIdQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastByIdQuery with id: ${query.id}" }

            val dailyForecast = dailyForecastRepository.getByIdAsync(query.id)

            logger.i { "Retrieved daily forecast with id: ${query.id}, found: ${dailyForecast != null}" }

            GetDailyForecastByIdQueryResult(
                dailyForecast = dailyForecast,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecast by id: ${query.id}" }
            GetDailyForecastByIdQueryResult(
                dailyForecast = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}