// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByLocationIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationIdQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetWeatherByLocationIdQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByLocationIdQuery, GetWeatherByLocationIdQueryResult> {

    override suspend fun handle(query: GetWeatherByLocationIdQuery): GetWeatherByLocationIdQueryResult {
        return try {
            logger.d { "Handling GetWeatherByLocationIdQuery with locationId: ${query.locationId}" }

            val weather = weatherRepository.getByLocationIdAsync(query.locationId)

            logger.i { "Retrieved weather for location id: ${query.locationId}, found: ${weather != null}" }

            GetWeatherByLocationIdQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get weather by location id: ${query.locationId}" }
            GetWeatherByLocationIdQueryResult(
                weather = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}