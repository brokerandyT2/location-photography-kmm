// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/LocationExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.LocationExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.location.LocationExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class LocationExistsByIdQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<LocationExistsByIdQuery, LocationExistsByIdQueryResult> {

    override suspend fun handle(query: LocationExistsByIdQuery): Result<LocationExistsByIdQueryResult> {
        logger.d { "Handling LocationExistsByIdQuery with id: ${query.id}" }

        return when (val result = locationRepository.existsByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Location exists check for id ${query.id}: ${result.data}" }
                Result.success(
                    LocationExistsByIdQueryResult(
                        exists = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if location exists by id: ${query.id} - ${result.error}" }
                Result.success(
                    LocationExistsByIdQueryResult(
                        exists = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}