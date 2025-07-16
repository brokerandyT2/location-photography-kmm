// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetExpiredWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetExpiredWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetExpiredWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetExpiredWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetExpiredWeatherQuery, GetExpiredWeatherQueryResult> {

    override suspend fun handle(query: GetExpiredWeatherQuery): GetExpiredWeatherQueryResult {
        return try {
            logger.d { "Handling GetExpiredWeatherQuery with olderThan: ${query.olderThan}" }

            val weather = weatherRepository.getExpiredAsync(query.olderThan)

            logger.i { "Retrieved ${weather.size} expired weather records" }

            GetExpiredWeatherQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get expired weather" }
            GetExpiredWeatherQueryResult(
                weather = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}