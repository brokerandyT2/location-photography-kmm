// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByCoordinatesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByCoordinatesQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByCoordinatesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import co.touchlab.kermit.Logger

class GetWeatherByCoordinatesQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByCoordinatesQuery, GetWeatherByCoordinatesQueryResult> {

    override suspend fun handle(query: GetWeatherByCoordinatesQuery): GetWeatherByCoordinatesQueryResult {
        return try {
            logger.d { "Handling GetWeatherByCoordinatesQuery with coordinates: (${query.latitude}, ${query.longitude})" }

            val coordinate = Coordinate.create(query.latitude, query.longitude)
            val weather = weatherRepository.getByCoordinatesAsync(coordinate)

            logger.i { "Retrieved weather for coordinates: (${query.latitude}, ${query.longitude}), found: ${weather != null}" }

            GetWeatherByCoordinatesQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get weather by coordinates: (${query.latitude}, ${query.longitude})" }
            GetWeatherByCoordinatesQueryResult(
                weather = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}