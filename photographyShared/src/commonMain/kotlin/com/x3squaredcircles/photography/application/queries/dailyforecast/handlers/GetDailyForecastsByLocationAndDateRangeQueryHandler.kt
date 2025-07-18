// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastsByLocationAndDateRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationAndDateRangeQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastsByLocationAndDateRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastsByLocationAndDateRangeQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastsByLocationAndDateRangeQuery, GetDailyForecastsByLocationAndDateRangeQueryResult> {

    override suspend fun handle(query: GetDailyForecastsByLocationAndDateRangeQuery): GetDailyForecastsByLocationAndDateRangeQueryResult {
        logger.d { "Handling GetDailyForecastsByLocationAndDateRangeQuery with locationId: ${query.locationId}, startDate: ${query.startDate}, endDate: ${query.endDate}" }

        return try {
            val startDate = Instant.fromEpochMilliseconds(query.startDate)
            val endDate = Instant.fromEpochMilliseconds(query.endDate)

            when (val result = dailyForecastRepository.getByLocationAndDateRangeAsync(query.locationId, startDate, endDate)) {
                is Result.Success -> {
                    logger.i { "Retrieved ${result.data.size} daily forecasts for locationId: ${query.locationId} in date range" }
                    GetDailyForecastsByLocationAndDateRangeQueryResult(
                        dailyForecasts = result.data,
                        isSuccess = true
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get daily forecasts by location and date range - locationId: ${query.locationId} - ${result.error}" }
                    GetDailyForecastsByLocationAndDateRangeQueryResult(
                        dailyForecasts = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create dates or get daily forecasts by location and date range - locationId: ${query.locationId}" }
            GetDailyForecastsByLocationAndDateRangeQueryResult(
                dailyForecasts = emptyList(),
                isSuccess = false,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }
}