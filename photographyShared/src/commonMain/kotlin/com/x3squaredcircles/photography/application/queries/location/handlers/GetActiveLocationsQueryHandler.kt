// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetActiveLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetActiveLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetActiveLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetActiveLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveLocationsQuery, GetActiveLocationsQueryResult> {

    override suspend fun handle(query: GetActiveLocationsQuery): GetActiveLocationsQueryResult {
        return try {
            logger.d { "Handling GetActiveLocationsQuery" }

            val locations = locationRepository.getActiveAsync()

            logger.i { "Retrieved ${locations.size} active locations" }

            GetActiveLocationsQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get active locations" }
            GetActiveLocationsQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}