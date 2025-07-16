// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationsByBoundsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationsByBoundsQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationsByBoundsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetLocationsByBoundsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationsByBoundsQuery, GetLocationsByBoundsQueryResult> {

    override suspend fun handle(query: GetLocationsByBoundsQuery): GetLocationsByBoundsQueryResult {
        return try {
            logger.d { "Handling GetLocationsByBoundsQuery - bounds: (${query.southLatitude}, ${query.westLongitude}) to (${query.northLatitude}, ${query.eastLongitude})" }

            val locations = locationRepository.getByBoundsAsync(
                southLat = query.southLatitude,
                northLat = query.northLatitude,
                westLon = query.westLongitude,
                eastLon = query.eastLongitude
            )

            logger.i { "Retrieved ${locations.size} locations within bounds" }

            GetLocationsByBoundsQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get locations by bounds" }
            GetLocationsByBoundsQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}