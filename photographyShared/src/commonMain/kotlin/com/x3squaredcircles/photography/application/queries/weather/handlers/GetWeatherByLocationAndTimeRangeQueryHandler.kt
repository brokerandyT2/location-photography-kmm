// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByLocationAndTimeRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationAndTimeRangeQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationAndTimeRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetWeatherByLocationAndTimeRangeQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByLocationAndTimeRangeQuery, GetWeatherByLocationAndTimeRangeQueryResult> {

    override suspend fun handle(query: GetWeatherByLocationAndTimeRangeQuery): Result<GetWeatherByLocationAndTimeRangeQueryResult> {
        logger.d { "Handling GetWeatherByLocationAndTimeRangeQuery with locationId: ${query.locationId}, startTime: ${query.startTime}, endTime: ${query.endTime}" }

        return when (val result = weatherRepository.getByLocationAndTimeRangeAsync(
            locationId = query.locationId,
            startTime = query.startTime,
            endTime = query.endTime
        )) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} weather records for location ${query.locationId} in time range" }
                Result.success(
                    GetWeatherByLocationAndTimeRangeQueryResult(
                        weather = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get weather by location and time range: ${result.error}" }
                Result.success(
                    GetWeatherByLocationAndTimeRangeQueryResult(
                        weather = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}