// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetWeatherByIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByIdQuery, GetWeatherByIdQueryResult> {

    override suspend fun handle(query: GetWeatherByIdQuery): GetWeatherByIdQueryResult {
        logger.d { "Handling GetWeatherByIdQuery with id: ${query.id}" }

        return when (val result = weatherRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved weather with id: ${query.id}, found: ${result.data != null}" }
                GetWeatherByIdQueryResult(
                    weather = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get weather by id: ${query.id} - ${result.error}" }
                GetWeatherByIdQueryResult(
                    weather = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}