// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationsByBoundsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationsByBoundsQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationsByBoundsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLocationsByBoundsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationsByBoundsQuery, GetLocationsByBoundsQueryResult> {

    override suspend fun handle(query: GetLocationsByBoundsQuery): Result<GetLocationsByBoundsQueryResult> {
        logger.d { "Handling GetLocationsByBoundsQuery - bounds: (${query.southLatitude}, ${query.westLongitude}) to (${query.northLatitude}, ${query.eastLongitude})" }

        return when (val result = locationRepository.getByBoundsAsync(
            southLat = query.southLatitude,
            northLat = query.northLatitude,
            westLon = query.westLongitude,
            eastLon = query.eastLongitude
        )) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} locations within bounds" }
                Result.success(
                    GetLocationsByBoundsQueryResult(
                        locations = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get locations by bounds: ${result.error}" }
                Result.success(
                    GetLocationsByBoundsQueryResult(
                        locations = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}