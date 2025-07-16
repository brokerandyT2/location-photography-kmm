// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/HasFreshWeatherDataQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataQuery
import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class HasFreshWeatherDataQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<HasFreshWeatherDataQuery, HasFreshWeatherDataQueryResult> {

    override suspend fun handle(query: HasFreshWeatherDataQuery): HasFreshWeatherDataQueryResult {
        return try {
            logger.d { "Handling HasFreshWeatherDataQuery with locationId: ${query.locationId}, maxAge: ${query.maxAge}" }

            val hasFreshData = weatherRepository.hasFreshDataAsync(
                locationId = query.locationId,
                maxAge = query.maxAge
            )

            logger.i { "Fresh weather data check for location ${query.locationId}: $hasFreshData" }

            HasFreshWeatherDataQueryResult(
                hasFreshData = hasFreshData,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check for fresh weather data" }
            HasFreshWeatherDataQueryResult(
                hasFreshData = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}