// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetForecastsByWeatherIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetForecastsByWeatherIdQuery, GetForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetForecastsByWeatherIdQuery): GetForecastsByWeatherIdQueryResult {
        return try {
            logger.d { "Handling GetForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

            val forecasts = weatherRepository.getForecastsByWeatherIdAsync(query.weatherId)

            logger.i { "Retrieved ${forecasts.size} forecasts for weather id: ${query.weatherId}" }

            GetForecastsByWeatherIdQueryResult(
                forecasts = forecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get forecasts by weather id: ${query.weatherId}" }
            GetForecastsByWeatherIdQueryResult(
                forecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}