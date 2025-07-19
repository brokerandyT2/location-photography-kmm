// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetAllLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetAllLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetAllLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLocationsQuery, GetAllLocationsQueryResult> {

    override suspend fun handle(query: GetAllLocationsQuery): Result<GetAllLocationsQueryResult> {
        logger.d { "Handling GetAllLocationsQuery with includeDeleted: ${query.includeDeleted}" }

        val result = if (query.includeDeleted) {
            locationRepository.getAllAsync()
        } else {
            locationRepository.getActiveAsync()
        }

        return when (result) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} locations" }
                Result.success(
                    GetAllLocationsQueryResult(
                        locations = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all locations: ${result.error}" }
                Result.success(
                    GetAllLocationsQueryResult(
                        locations = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}