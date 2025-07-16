// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastByIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import co.touchlab.kermit.Logger

class GetHourlyForecastByIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastByIdQuery, GetHourlyForecastByIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastByIdQuery): GetHourlyForecastByIdQueryResult {
        return try {
            logger.d { "Handling GetHourlyForecastByIdQuery with id: ${query.id}" }

            val hourlyForecast = hourlyForecastRepository.getByIdAsync(query.id)

            logger.i { "Retrieved hourly forecast with id: ${query.id}, found: ${hourlyForecast != null}" }

            GetHourlyForecastByIdQueryResult(
                hourlyForecast = hourlyForecast,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get hourly forecast by id: ${query.id}" }
            GetHourlyForecastByIdQueryResult(
                hourlyForecast = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}