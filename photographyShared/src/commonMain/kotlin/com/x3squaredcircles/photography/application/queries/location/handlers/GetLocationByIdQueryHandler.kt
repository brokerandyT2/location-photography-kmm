// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationByIdQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLocationByIdQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationByIdQuery, GetLocationByIdQueryResult> {

    override suspend fun handle(query: GetLocationByIdQuery): Result<GetLocationByIdQueryResult> {
        logger.d { "Handling GetLocationByIdQuery with id: ${query.id}" }

        return when (val result = locationRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved location with id: ${query.id}, found: ${result.data != null}" }
                Result.success(
                    GetLocationByIdQueryResult(
                        location = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get location by id: ${query.id} - ${result.error}" }
                Result.success(
                    GetLocationByIdQueryResult(
                        location = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}