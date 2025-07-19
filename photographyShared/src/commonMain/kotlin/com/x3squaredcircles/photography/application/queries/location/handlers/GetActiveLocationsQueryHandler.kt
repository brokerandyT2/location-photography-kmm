// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetActiveLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetActiveLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetActiveLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetActiveLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveLocationsQuery, GetActiveLocationsQueryResult> {

    override suspend fun handle(query: GetActiveLocationsQuery): Result<GetActiveLocationsQueryResult> {
        logger.d { "Handling GetActiveLocationsQuery" }

        return when (val result = locationRepository.getActiveAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} active locations" }
                Result.success(
                    GetActiveLocationsQueryResult(
                        locations = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get active locations: ${result.error}" }
                Result.success(
                    GetActiveLocationsQueryResult(
                        locations = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}