// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastByDateQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByDateQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByDateQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastByDateQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastByDateQuery, GetDailyForecastByDateQueryResult> {

    override suspend fun handle(query: GetDailyForecastByDateQuery): GetDailyForecastByDateQueryResult {
        logger.d { "Handling GetDailyForecastByDateQuery with weatherId: ${query.weatherId}, forecastDate: ${query.forecastDate}" }

        return try {
            val forecastDate = Instant.fromEpochMilliseconds(query.forecastDate)

            when (val result = dailyForecastRepository.getByDateAsync(query.weatherId, forecastDate)) {
                is Result.Success -> {
                    logger.i { "Retrieved daily forecast for weatherId: ${query.weatherId}, date: ${query.forecastDate}, found: ${result.data != null}" }
                    GetDailyForecastByDateQueryResult(
                        dailyForecast = result.data,
                        isSuccess = true
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get daily forecast by date - weatherId: ${query.weatherId}, date: ${query.forecastDate} - ${result.error}" }
                    GetDailyForecastByDateQueryResult(
                        dailyForecast = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create date or get daily forecast by date - weatherId: ${query.weatherId}, date: ${query.forecastDate}" }
            GetDailyForecastByDateQueryResult(
                dailyForecast = null,
                isSuccess = false,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }
}