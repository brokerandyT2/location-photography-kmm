// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetWeatherByLocationAndTimeRangeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationAndTimeRangeQuery
import com.x3squaredcircles.photography.application.queries.weather.GetWeatherByLocationAndTimeRangeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetWeatherByLocationAndTimeRangeQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetWeatherByLocationAndTimeRangeQuery, GetWeatherByLocationAndTimeRangeQueryResult> {

    override suspend fun handle(query: GetWeatherByLocationAndTimeRangeQuery): GetWeatherByLocationAndTimeRangeQueryResult {
        return try {
            logger.d { "Handling GetWeatherByLocationAndTimeRangeQuery with locationId: ${query.locationId}, startTime: ${query.startTime}, endTime: ${query.endTime}" }

            val weather = weatherRepository.getByLocationAndTimeRangeAsync(
                locationId = query.locationId,
                startTime = query.startTime,
                endTime = query.endTime
            )

            logger.i { "Retrieved ${weather.size} weather records for location ${query.locationId} in time range" }

            GetWeatherByLocationAndTimeRangeQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get weather by location and time range" }
            GetWeatherByLocationAndTimeRangeQueryResult(
                weather = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}