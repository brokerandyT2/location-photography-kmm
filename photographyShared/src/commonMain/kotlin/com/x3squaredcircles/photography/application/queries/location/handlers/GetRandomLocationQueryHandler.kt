// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetRandomLocationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetRandomLocationQuery
import com.x3squaredcircles.photography.application.queries.location.GetRandomLocationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetRandomLocationQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetRandomLocationQuery, GetRandomLocationQueryResult> {

    override suspend fun handle(query: GetRandomLocationQuery): Result<GetRandomLocationQueryResult> {
        logger.d { "Handling GetRandomLocationQuery" }

        return when (val result = locationRepository.getRandomAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved random location: ${result.data != null}" }
                Result.success(
                    GetRandomLocationQueryResult(
                        location = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get random location: ${result.error}" }
                Result.success(
                    GetRandomLocationQueryResult(
                        location = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}