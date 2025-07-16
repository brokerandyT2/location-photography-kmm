// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/handlers/GetDailyForecastByDateQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast.handlers

import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByDateQuery
import com.x3squaredcircles.photography.application.queries.dailyforecast.GetDailyForecastByDateQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IDailyForecastRepository
import kotlinx.datetime.Instant
import co.touchlab.kermit.Logger

class GetDailyForecastByDateQueryHandler(
    private val dailyForecastRepository: IDailyForecastRepository,
    private val logger: Logger
) : IQueryHandler<GetDailyForecastByDateQuery, GetDailyForecastByDateQueryResult> {

    override suspend fun handle(query: GetDailyForecastByDateQuery): GetDailyForecastByDateQueryResult {
        return try {
            logger.d { "Handling GetDailyForecastByDateQuery with weatherId: ${query.weatherId}, forecastDate: ${query.forecastDate}" }

            val forecastDate = Instant.fromEpochMilliseconds(query.forecastDate)
            val dailyForecast = dailyForecastRepository.getByDateAsync(query.weatherId, forecastDate)

            logger.i { "Retrieved daily forecast for weatherId: ${query.weatherId}, date: ${query.forecastDate}, found: ${dailyForecast != null}" }

            GetDailyForecastByDateQueryResult(
                dailyForecast = dailyForecast,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get daily forecast by date - weatherId: ${query.weatherId}, date: ${query.forecastDate}" }
            GetDailyForecastByDateQueryResult(
                dailyForecast = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}