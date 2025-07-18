// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByCoordinatesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByCoordinatesQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByCoordinatesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetWeatherByCoordinatesQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByCoordinatesQuery, GetWeatherByCoordinatesQueryResult> {

    override suspend fun handle(query: GetWeatherByCoordinatesQuery): GetWeatherByCoordinatesQueryResult {
        logger.d { "Handling GetWeatherByCoordinatesQuery with coordinates: (${query.latitude}, ${query.longitude})" }

        return try {
            val coordinate = Coordinate.create(query.latitude, query.longitude)

            when (val result = weatherRepository.getByCoordinatesAsync(coordinate)) {
                is Result.Success -> {
                    logger.i { "Retrieved weather for coordinates: (${query.latitude}, ${query.longitude}), found: ${result.data != null}" }
                    GetWeatherByCoordinatesQueryResult(
                        weather = result.data,
                        isSuccess = true
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get weather by coordinates: (${query.latitude}, ${query.longitude}) - ${result.error}" }
                    GetWeatherByCoordinatesQueryResult(
                        weather = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create coordinate or get weather by coordinates: (${query.latitude}, ${query.longitude})" }
            GetWeatherByCoordinatesQueryResult(
                weather = null,
                isSuccess = false,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }
}