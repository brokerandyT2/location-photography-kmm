// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetWeatherByIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByIdQuery, GetWeatherByIdQueryResult> {

    override suspend fun handle(query: GetWeatherByIdQuery): GetWeatherByIdQueryResult {
        return try {
            logger.d { "Handling GetWeatherByIdQuery with id: ${query.id}" }

            val weather = weatherRepository.getByIdAsync(query.id)

            logger.i { "Retrieved weather with id: ${query.id}, found: ${weather != null}" }

            GetWeatherByIdQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get weather by id: ${query.id}" }
            GetWeatherByIdQueryResult(
                weather = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}