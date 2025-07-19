// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/hourlyforecast/handlers/GetHourlyForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.hourlyforecast.handlers

import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.hourlyforecast.GetHourlyForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IHourlyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetHourlyForecastsByWeatherIdQueryHandler(
    private val hourlyForecastRepository: IHourlyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetHourlyForecastsByWeatherIdQuery, GetHourlyForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetHourlyForecastsByWeatherIdQuery): Result<GetHourlyForecastsByWeatherIdQueryResult> {
        logger.d { "Handling GetHourlyForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

        return when (val result = hourlyForecastRepository.getByWeatherIdAsync(query.weatherId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} hourly forecasts for weatherId: ${query.weatherId}" }
                Result.success(
                    GetHourlyForecastsByWeatherIdQueryResult(
                        hourlyForecasts = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get hourly forecasts by weather id: ${query.weatherId} - ${result.error}" }
                Result.success(
                    GetHourlyForecastsByWeatherIdQueryResult(
                        hourlyForecasts = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}