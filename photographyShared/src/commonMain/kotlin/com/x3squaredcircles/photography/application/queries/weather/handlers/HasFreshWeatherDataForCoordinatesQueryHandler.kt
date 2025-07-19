// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/HasFreshWeatherDataForCoordinatesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataForCoordinatesQuery
import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataForCoordinatesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class HasFreshWeatherDataForCoordinatesQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<HasFreshWeatherDataForCoordinatesQuery, HasFreshWeatherDataForCoordinatesQueryResult> {

    override suspend fun handle(query: HasFreshWeatherDataForCoordinatesQuery): Result<HasFreshWeatherDataForCoordinatesQueryResult> {
        logger.d { "Handling HasFreshWeatherDataForCoordinatesQuery with coordinates: (${query.latitude}, ${query.longitude}), maxAge: ${query.maxAge}" }

        val coordinate = Coordinate.create(query.latitude, query.longitude)

        return when (val result = weatherRepository.hasFreshDataForCoordinatesAsync(
            coordinate = coordinate,
            maxAge = query.maxAge
        )) {
            is Result.Success -> {
                logger.i { "Fresh weather data check for coordinates (${query.latitude}, ${query.longitude}): ${result.data}" }
                Result.success(
                    HasFreshWeatherDataForCoordinatesQueryResult(
                        hasFreshData = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check for fresh weather data for coordinates: ${result.error}" }
                Result.success(
                    HasFreshWeatherDataForCoordinatesQueryResult(
                        hasFreshData = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}