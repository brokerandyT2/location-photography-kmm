// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/HasFreshWeatherDataQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataQuery
import com.x3squaredcircles.photography.application.queries.weather.HasFreshWeatherDataQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class HasFreshWeatherDataQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<HasFreshWeatherDataQuery, HasFreshWeatherDataQueryResult> {

    override suspend fun handle(query: HasFreshWeatherDataQuery): Result<HasFreshWeatherDataQueryResult> {
        logger.d { "Handling HasFreshWeatherDataQuery with locationId: ${query.locationId}, maxAge: ${query.maxAge}" }

        return when (val result = weatherRepository.hasFreshDataAsync(
            locationId = query.locationId,
            maxAge = query.maxAge
        )) {
            is Result.Success -> {
                logger.i { "Fresh weather data check for location ${query.locationId}: ${result.data}" }
                Result.success(
                    HasFreshWeatherDataQueryResult(
                        hasFreshData = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check for fresh weather data: ${result.error}" }
                Result.success(
                    HasFreshWeatherDataQueryResult(
                        hasFreshData = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}