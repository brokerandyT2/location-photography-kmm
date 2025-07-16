// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetNearbyLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetNearbyLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetNearbyLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import co.touchlab.kermit.Logger

class GetNearbyLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetNearbyLocationsQuery, GetNearbyLocationsQueryResult> {

    override suspend fun handle(query: GetNearbyLocationsQuery): GetNearbyLocationsQueryResult {
        return try {
            logger.d { "Handling GetNearbyLocationsQuery - center: (${query.centerLatitude}, ${query.centerLongitude}), radius: ${query.radiusKm}km, limit: ${query.limit}" }

            val centerCoordinate = Coordinate.create(query.centerLatitude, query.centerLongitude)

            val locations = locationRepository.getNearbyAsync(
                centerCoordinate = centerCoordinate,
                radiusKm = query.radiusKm,
                limit = query.limit
            )

            logger.i { "Retrieved ${locations.size} nearby locations within ${query.radiusKm}km" }

            GetNearbyLocationsQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get nearby locations" }
            GetNearbyLocationsQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}