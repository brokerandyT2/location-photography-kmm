// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import co.touchlab.kermit.Logger

class GetDailyForecastsByLocationIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByLocationIdQuery, GetDailyForecastsByLocationIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByLocationIdQuery): GetDailyForecastsByLocationIdQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastsByLocationIdQuery with locationId: ${query.locationId}" }

            val dailyForecasts = dailyForecastRepository.getByLocationIdAsync(query.locationId)

            logger.i { "Retrieved ${dailyForecasts.size} daily forecasts for locationId: ${query.locationId}" }

            GetDailyForecastsByLocationIdQueryResult(
                dailyForecasts = dailyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecasts by location id: ${query.locationId}" }
            GetDailyForecastsByLocationIdQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}