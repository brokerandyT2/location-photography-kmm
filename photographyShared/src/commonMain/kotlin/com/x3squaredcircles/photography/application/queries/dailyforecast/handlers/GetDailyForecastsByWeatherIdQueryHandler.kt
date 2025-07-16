// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByWeatherIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherIdQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import co.touchlab.kermit.Logger

class GetDailyForecastsByWeatherIdQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByWeatherIdQuery, GetDailyForecastsByWeatherIdQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByWeatherIdQuery): GetDailyForecastsByWeatherIdQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastsByWeatherIdQuery with weatherId: ${query.weatherId}" }

            val dailyForecasts = dailyForecastRepository.getByWeatherIdAsync(query.weatherId)

            logger.i { "Retrieved ${dailyForecasts.size} daily forecasts for weatherId: ${query.weatherId}" }

            GetDailyForecastsByWeatherIdQueryResult(
                dailyForecasts = dailyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecasts by weather id: ${query.weatherId}" }
            GetDailyForecastsByWeatherIdQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}