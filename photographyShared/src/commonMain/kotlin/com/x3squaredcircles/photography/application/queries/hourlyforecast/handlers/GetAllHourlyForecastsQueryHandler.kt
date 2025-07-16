// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetAllHourlyForecastsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetAllHourlyForecastsQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetAllHourlyForecastsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import co.touchlab.kermit.Logger

class GetAllHourlyForecastsQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetAllHourlyForecastsQuery, GetAllHourlyForecastsQueryResult> {

    override suspend fun handle(query: GetAllHourlyForecastsQuery): GetAllHourlyForecastsQueryResult {
        return try {
            logger.d { "Handling GetAllHourlyForecastsQuery" }

            val hourlyForecasts = hourlyForecastRepository.getAllAsync()

            logger.i { "Retrieved ${hourlyForecasts.size} hourly forecasts" }

            GetAllHourlyForecastsQueryResult(
                hourlyForecasts = hourlyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all hourly forecasts" }
            GetAllHourlyForecastsQueryResult(
                hourlyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}