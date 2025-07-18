// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetRecentWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetRecentWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetRecentWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetRecentWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetRecentWeatherQuery, GetRecentWeatherQueryResult> {

    override suspend fun handle(query: GetRecentWeatherQuery): GetRecentWeatherQueryResult {
        logger.d { "Handling GetRecentWeatherQuery with count: ${query.count}" }

        return when (val result = weatherRepository.getRecentAsync(query.count)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} recent weather records" }
                GetRecentWeatherQueryResult(
                    weather = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get recent weather: ${result.error}" }
                GetRecentWeatherQueryResult(
                    weather = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}