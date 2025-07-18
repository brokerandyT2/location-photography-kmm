// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/LocationExistsByTitleQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.LocationExistsByTitleQuery
import com.x3squaredcircles.photography.application.queries.location.LocationExistsByTitleQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class LocationExistsByTitleQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<LocationExistsByTitleQuery, LocationExistsByTitleQueryResult> {

    override suspend fun handle(query: LocationExistsByTitleQuery): LocationExistsByTitleQueryResult {
        logger.d { "Handling LocationExistsByTitleQuery with title: ${query.title}, excludeId: ${query.excludeId}" }

        return when (val result = locationRepository.existsByTitleAsync(
            title = query.title,
            excludeId = query.excludeId
        )) {
            is Result.Success -> {
                logger.i { "Location exists check for title '${query.title}' (excluding id ${query.excludeId}): ${result.data}" }
                LocationExistsByTitleQueryResult(
                    exists = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if location exists by title: ${query.title} - ${result.error}" }
                LocationExistsByTitleQueryResult(
                    exists = false,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}