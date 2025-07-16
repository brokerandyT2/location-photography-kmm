// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetRecentLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetRecentLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetRecentLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetRecentLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetRecentLocationsQuery, GetRecentLocationsQueryResult> {

    override suspend fun handle(query: GetRecentLocationsQuery): GetRecentLocationsQueryResult {
        return try {
            logger.d { "Handling GetRecentLocationsQuery with count: ${query.count}" }

            val locations = locationRepository.getRecentAsync(query.count)

            logger.i { "Retrieved ${locations.size} recent locations" }

            GetRecentLocationsQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get recent locations" }
            GetRecentLocationsQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}