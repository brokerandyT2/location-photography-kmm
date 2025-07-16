// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByWeatherAndDateRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherAndDateRangeQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherAndDateRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastsByWeatherAndDateRangeQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByWeatherAndDateRangeQuery, GetDailyForecastsByWeatherAndDateRangeQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByWeatherAndDateRangeQuery): GetDailyForecastsByWeatherAndDateRangeQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastsByWeatherAndDateRangeQuery with weatherId: ${query.weatherId}, startDate: ${query.startDate}, endDate: ${query.endDate}" }

            val startDate = Instant.fromEpochMilliseconds(query.startDate)
            val endDate = Instant.fromEpochMilliseconds(query.endDate)
            val dailyForecasts = dailyForecastRepository.getByWeatherAndDateRangeAsync(query.weatherId, startDate, endDate)

            logger.i { "Retrieved ${dailyForecasts.size} daily forecasts for weatherId: ${query.weatherId} in date range" }

            GetDailyForecastsByWeatherAndDateRangeQueryResult(
                dailyForecasts = dailyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecasts by weather and date range - weatherId: ${query.weatherId}" }
            GetDailyForecastsByWeatherAndDateRangeQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}