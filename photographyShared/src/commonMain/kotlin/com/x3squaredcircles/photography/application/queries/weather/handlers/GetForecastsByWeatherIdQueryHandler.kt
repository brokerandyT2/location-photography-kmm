// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetForecastsByWeatherIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetForecastsByWeatherIdQuery, GetForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetForecastsByWeatherIdQuery): GetForecastsByWeatherIdQueryResult {
        logger.d { "Handling GetForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

        return when (val result = weatherRepository.getForecastsByWeatherIdAsync(query.weatherId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} forecasts for weather id: ${query.weatherId}" }
                GetForecastsByWeatherIdQueryResult(
                    forecasts = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get forecasts by weather id: ${query.weatherId} - ${result.error}" }
                GetForecastsByWeatherIdQueryResult(
                    forecasts = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}