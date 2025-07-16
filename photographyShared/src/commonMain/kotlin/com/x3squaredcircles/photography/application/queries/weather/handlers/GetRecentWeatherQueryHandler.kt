// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetRecentWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetRecentWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetRecentWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetRecentWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetRecentWeatherQuery, GetRecentWeatherQueryResult> {

    override suspend fun handle(query: GetRecentWeatherQuery): GetRecentWeatherQueryResult {
        return try {
            logger.d { "Handling GetRecentWeatherQuery with count: ${query.count}" }

            val weather = weatherRepository.getRecentAsync(query.count)

            logger.i { "Retrieved ${weather.size} recent weather records" }

            GetRecentWeatherQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get recent weather" }
            GetRecentWeatherQueryResult(
                weather = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}