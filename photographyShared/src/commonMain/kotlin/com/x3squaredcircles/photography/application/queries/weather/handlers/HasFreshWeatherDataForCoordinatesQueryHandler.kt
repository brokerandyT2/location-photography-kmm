// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/HasFreshWeatherDataForCoordinatesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataForCoordinatesQuery
import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataForCoordinatesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import co.touchlab.kermit.Logger

class HasFreshWeatherDataForCoordinatesQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<HasFreshWeatherDataForCoordinatesQuery, HasFreshWeatherDataForCoordinatesQueryResult> {

    override suspend fun handle(query: HasFreshWeatherDataForCoordinatesQuery): HasFreshWeatherDataForCoordinatesQueryResult {
        return try {
            logger.d { "Handling HasFreshWeatherDataForCoordinatesQuery with coordinates: (${query.latitude}, ${query.longitude}), maxAge: ${query.maxAge}" }

            val coordinate = Coordinate.create(query.latitude, query.longitude)
            val hasFreshData = weatherRepository.hasFreshDataForCoordinatesAsync(
                coordinate = coordinate,
                maxAge = query.maxAge
            )

            logger.i { "Fresh weather data check for coordinates (${query.latitude}, ${query.longitude}): $hasFreshData" }

            HasFreshWeatherDataForCoordinatesQueryResult(
                hasFreshData = hasFreshData,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check for fresh weather data for coordinates" }
            HasFreshWeatherDataForCoordinatesQueryResult(
                hasFreshData = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}