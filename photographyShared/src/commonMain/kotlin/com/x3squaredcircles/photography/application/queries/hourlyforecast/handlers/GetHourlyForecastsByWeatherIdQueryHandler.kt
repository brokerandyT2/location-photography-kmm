// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import co.touchlab.kermit.Logger

class GetHourlyForecastsByWeatherIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsByWeatherIdQuery, GetHourlyForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsByWeatherIdQuery): GetHourlyForecastsByWeatherIdQueryResult {
        return try {
            logger.d { "Handling GetHourlyForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

            val hourlyForecasts = hourlyForecastRepository.getByWeatherIdAsync(query.weatherId)

            logger.i { "Retrieved ${hourlyForecasts.size} hourly forecasts for weatherId: ${query.weatherId}" }

            GetHourlyForecastsByWeatherIdQueryResult(
                hourlyForecasts = hourlyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get hourly forecasts by weather id: ${query.weatherId}" }
            GetHourlyForecastsByWeatherIdQueryResult(
                hourlyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}