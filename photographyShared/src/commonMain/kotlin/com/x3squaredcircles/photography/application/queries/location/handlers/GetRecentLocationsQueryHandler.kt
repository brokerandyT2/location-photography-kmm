// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetRecentLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetRecentLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetRecentLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetRecentLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetRecentLocationsQuery, GetRecentLocationsQueryResult> {

    override suspend fun handle(query: GetRecentLocationsQuery): Result<GetRecentLocationsQueryResult> {
        logger.d { "Handling GetRecentLocationsQuery with count: ${query.count}" }

        return when (val result = locationRepository.getRecentAsync(query.count)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} recent locations" }
                Result.success(
                    GetRecentLocationsQueryResult(
                        locations = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get recent locations: ${result.error}" }
                Result.success(
                    GetRecentLocationsQueryResult(
                        locations = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}