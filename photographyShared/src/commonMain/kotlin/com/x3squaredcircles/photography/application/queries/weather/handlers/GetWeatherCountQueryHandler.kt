// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherCountQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetWeatherCountQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherCountQuery, GetWeatherCountQueryResult> {

    override suspend fun handle(query: GetWeatherCountQuery): Result<GetWeatherCountQueryResult> {
        logger.d { "Handling GetWeatherCountQuery" }

        return when (val result = weatherRepository.getCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved weather count: ${result.data}" }
                Result.success(
                    GetWeatherCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get weather count: ${result.error}" }
                Result.success(
                    GetWeatherCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}