// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import co.touchlab.kermit.Logger

class GetHourlyForecastsByLocationIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsByLocationIdQuery, GetHourlyForecastsByLocationIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsByLocationIdQuery): GetHourlyForecastsByLocationIdQueryResult {
        return try {
            logger.d { "Handling GetHourlyForecastsByLocationIdQuery with locationId: ${query.locationId}" }

            val hourlyForecasts = hourlyForecastRepository.getByLocationIdAsync(query.locationId)

            logger.i { "Retrieved ${hourlyForecasts.size} hourly forecasts for locationId: ${query.locationId}" }

            GetHourlyForecastsByLocationIdQueryResult(
                hourlyForecasts = hourlyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get hourly forecasts by location id: ${query.locationId}" }
            GetHourlyForecastsByLocationIdQueryResult(
                hourlyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}