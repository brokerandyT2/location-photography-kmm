// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByLocationAndDateRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationAndDateRangeQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationAndDateRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastsByLocationAndDateRangeQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByLocationAndDateRangeQuery, GetDailyForecastsByLocationAndDateRangeQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByLocationAndDateRangeQuery): GetDailyForecastsByLocationAndDateRangeQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastsByLocationAndDateRangeQuery with locationId: ${query.locationId}, startDate: ${query.startDate}, endDate: ${query.endDate}" }

            val startDate = Instant.fromEpochMilliseconds(query.startDate)
            val endDate = Instant.fromEpochMilliseconds(query.endDate)
            val dailyForecasts = dailyForecastRepository.getByLocationAndDateRangeAsync(query.locationId, startDate, endDate)

            logger.i { "Retrieved ${dailyForecasts.size} daily forecasts for locationId: ${query.locationId} in date range" }

            GetDailyForecastsByLocationAndDateRangeQueryResult(
                dailyForecasts = dailyForecasts,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecasts by location and date range - locationId: ${query.locationId}" }
            GetDailyForecastsByLocationAndDateRangeQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}