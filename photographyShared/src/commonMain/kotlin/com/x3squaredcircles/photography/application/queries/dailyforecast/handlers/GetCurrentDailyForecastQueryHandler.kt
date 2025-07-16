// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetCurrentDailyForecastQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetCurrentDailyForecastQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetCurrentDailyForecastQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetCurrentDailyForecastQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetCurrentDailyForecastQuery, GetCurrentDailyForecastQueryResult> {

    override suspend fun handle(query: GetCurrentDailyForecastQuery): GetCurrentDailyForecastQueryResult {
        return try {
            logger.d { "Handling GetCurrentDailyForecastQuery with weatherId: ${query.weatherId}, currentTime: ${query.currentTime}" }

            val currentTime = Instant.fromEpochMilliseconds(query.currentTime)
            val dailyForecast = dailyForecastRepository.getCurrentAsync(query.weatherId, currentTime)

            logger.i { "Retrieved current daily forecast for weatherId: ${query.weatherId}, time: ${query.currentTime}, found: ${dailyForecast != null}" }

            GetCurrentDailyForecastQueryResult(
                dailyForecast = dailyForecast,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get current daily forecast - weatherId: ${query.weatherId}, time: ${query.currentTime}" }
            GetCurrentDailyForecastQueryResult(
                dailyForecast = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}