// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetWeatherByLocationIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByLocationIdQuery, GetWeatherByLocationIdQueryResult> {

    override suspend fun handle(query: GetWeatherByLocationIdQuery): GetWeatherByLocationIdQueryResult {
        logger.d { "Handling GetWeatherByLocationIdQuery with locationId: ${query.locationId}" }

        return when (val result = weatherRepository.getByLocationIdAsync(query.locationId)) {
            is Result.Success -> {
                logger.i { "Retrieved weather for location id: ${query.locationId}, found: ${result.data != null}" }
                GetWeatherByLocationIdQueryResult(
                    weather = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get weather by location id: ${query.locationId} - ${result.error}" }
                GetWeatherByLocationIdQueryResult(
                    weather = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}