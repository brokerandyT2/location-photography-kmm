// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByWeatherAndDateRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherAndDateRangeQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByWeatherAndDateRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastsByWeatherAndDateRangeQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByWeatherAndDateRangeQuery, GetDailyForecastsByWeatherAndDateRangeQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByWeatherAndDateRangeQuery): GetDailyForecastsByWeatherAndDateRangeQueryResult {
        logger.d { "Handling GetDailyForecastsByWeatherAndDateRangeQuery with weatherId: ${query.weatherId}, startDate: ${query.startDate}, endDate: ${query.endDate}" }

        return try {
            val startDate = Instant.fromEpochMilliseconds(query.startDate)
            val endDate = Instant.fromEpochMilliseconds(query.endDate)

            when (val result = dailyForecastRepository.getByWeatherAndDateRangeAsync(query.weatherId, startDate, endDate)) {
                is Result.Success -> {
                    logger.i { "Retrieved ${result.data.size} daily forecasts for weatherId: ${query.weatherId} in date range" }
                    GetDailyForecastsByWeatherAndDateRangeQueryResult(
                        dailyForecasts = result.data,
                        isSuccess = true
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get daily forecasts by weather and date range - weatherId: ${query.weatherId} - ${result.error}" }
                    GetDailyForecastsByWeatherAndDateRangeQueryResult(
                        dailyForecasts = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create dates or get daily forecasts by weather and date range - weatherId: ${query.weatherId}" }
            GetDailyForecastsByWeatherAndDateRangeQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }
}