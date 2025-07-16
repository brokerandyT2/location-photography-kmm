// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetAllWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetAllWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetAllWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import co.touchlab.kermit.Logger

class GetAllWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetAllWeatherQuery, GetAllWeatherQueryResult> {

    override suspend fun handle(query: GetAllWeatherQuery): GetAllWeatherQueryResult {
        return try {
            logger.d { "Handling GetAllWeatherQuery" }

            val weather = weatherRepository.getAllAsync()

            logger.i { "Retrieved ${weather.size} weather records" }

            GetAllWeatherQueryResult(
                weather = weather,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all weather" }
            GetAllWeatherQueryResult(
                weather = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}