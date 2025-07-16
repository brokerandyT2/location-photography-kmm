// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/LocationExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.LocationExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.location.LocationExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class LocationExistsByIdQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<LocationExistsByIdQuery, LocationExistsByIdQueryResult> {

    override suspend fun handle(query: LocationExistsByIdQuery): LocationExistsByIdQueryResult {
        return try {
            logger.d { "Handling LocationExistsByIdQuery with id: ${query.id}" }

            val exists = locationRepository.existsByIdAsync(query.id)

            logger.i { "Location exists check for id ${query.id}: $exists" }

            LocationExistsByIdQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if location exists by id: ${query.id}" }
            LocationExistsByIdQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}