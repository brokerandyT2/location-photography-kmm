// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/LocationExistsByTitleQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.LocationExistsByTitleQuery
import com.x3squaredcircles.photography.application.queries.location.LocationExistsByTitleQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class LocationExistsByTitleQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<LocationExistsByTitleQuery, LocationExistsByTitleQueryResult> {

    override suspend fun handle(query: LocationExistsByTitleQuery): LocationExistsByTitleQueryResult {
        return try {
            logger.d { "Handling LocationExistsByTitleQuery with title: ${query.title}, excludeId: ${query.excludeId}" }

            val exists = locationRepository.existsByTitleAsync(
                title = query.title,
                excludeId = query.excludeId
            )

            logger.i { "Location exists check for title '${query.title}' (excluding id ${query.excludeId}): $exists" }

            LocationExistsByTitleQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if location exists by title: ${query.title}" }
            LocationExistsByTitleQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}