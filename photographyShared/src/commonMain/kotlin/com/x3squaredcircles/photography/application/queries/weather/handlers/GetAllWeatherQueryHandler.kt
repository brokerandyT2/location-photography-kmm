// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/handlers/GetAllWeatherQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.weather.handlers

import com.x3squaredcircles.photography.application.queries.weather.GetAllWeatherQuery
import com.x3squaredcircles.photography.application.queries.weather.GetAllWeatherQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllWeatherQueryHandler(
    private val weatherRepository: IWeatherRepository,
    private val logger: Logger
) : IQueryHandler<GetAllWeatherQuery, GetAllWeatherQueryResult> {

    override suspend fun handle(query: GetAllWeatherQuery): Result<GetAllWeatherQueryResult> {
        logger.d { "Handling GetAllWeatherQuery" }

        return when (val result = weatherRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} weather records" }
                Result.success(
                    GetAllWeatherQueryResult(
                        weather = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all weather: ${result.error}" }
                Result.success(
                    GetAllWeatherQueryResult(
                        weather = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}