// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetDailyForecastsByWeatherIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByWeatherIdQuery, GetDailyForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByWeatherIdQuery): GetDailyForecastsByWeatherIdQueryResult {
        logger.d { "Handling GetDailyForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

        return when (val result = dailyForecastRepository.getByWeatherIdAsync(query.weatherId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} daily forecasts for weatherId: ${query.weatherId}" }
                GetDailyForecastsByWeatherIdQueryResult(
                    dailyForecasts = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get daily forecasts by weather id: ${query.weatherId} - ${result.error}" }
                GetDailyForecastsByWeatherIdQueryResult(
                    dailyForecasts = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}