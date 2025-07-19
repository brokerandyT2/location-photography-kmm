// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetExpiredWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetExpiredWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetExpiredWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetExpiredWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetExpiredWeatherQuery, GetExpiredWeatherQueryResult> {

    override suspend fun handle(query: GetExpiredWeatherQuery): Result<GetExpiredWeatherQueryResult> {
        logger.d { "Handling GetExpiredWeatherQuery with olderThan: ${query.olderThan}" }

        return when (val result = weatherRepository.getExpiredAsync(query.olderThan)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} expired weather records" }
                Result.success(
                    GetExpiredWeatherQueryResult(
                        weather = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get expired weather: ${result.error}" }
                Result.success(
                    GetExpiredWeatherQueryResult(
                        weather = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}