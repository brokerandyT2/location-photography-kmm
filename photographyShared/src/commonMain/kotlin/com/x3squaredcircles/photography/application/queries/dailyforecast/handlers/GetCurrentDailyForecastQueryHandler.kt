// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetCurrentDailyForecastQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetCurrentDailyForecastQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetCurrentDailyForecastQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetCurrentDailyForecastQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetCurrentDailyForecastQuery, GetCurrentDailyForecastQueryResult> {

    override suspend fun handle(query: GetCurrentDailyForecastQuery): GetCurrentDailyForecastQueryResult {
        logger.d { "Handling GetCurrentDailyForecastQuery with weatherId: ${query.weatherId}, currentTime: ${query.currentTime}" }

        return try {
            val currentTime = Instant.fromEpochMilliseconds(query.currentTime)

            when (val result = dailyForecastRepository.getCurrentAsync(query.weatherId, currentTime)) {
                is Result.Success -> {
                    logger.i { "Retrieved current daily forecast for weatherId: ${query.weatherId}, time: ${query.currentTime}, found: ${result.data != null}" }
                    GetCurrentDailyForecastQueryResult(
                        dailyForecast = result.data,
                        isSuccess = true
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get current daily forecast - weatherId: ${query.weatherId}, time: ${query.currentTime} - ${result.error}" }
                    GetCurrentDailyForecastQueryResult(
                        dailyForecast = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create time or get current daily forecast - weatherId: ${query.weatherId}, time: ${query.currentTime}" }
            GetCurrentDailyForecastQueryResult(
                dailyForecast = null,
                isSuccess = false,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }
}