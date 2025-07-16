// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetAllLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetAllLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetAllLocationsQueryResult
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.application.queries.IQueryHandler

class GetAllLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLocationsQuery, GetAllLocationsQueryResult> {

    override suspend fun handle(query: GetAllLocationsQuery): GetAllLocationsQueryResult {
        return try {
            logger.d { "Handling GetAllLocationsQuery with includeDeleted: ${query.includeDeleted}" }

            val locations = if (query.includeDeleted) {
                locationRepository.getAllAsync()
            } else {
                locationRepository.getActiveAsync()
            }

            logger.i { "Retrieved ${locations.size} locations" }

            GetAllLocationsQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all locations" }
            GetAllLocationsQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}