// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationsModifiedSinceQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationsModifiedSinceQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationsModifiedSinceQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLocationsModifiedSinceQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationsModifiedSinceQuery, GetLocationsModifiedSinceQueryResult> {

    override suspend fun handle(query: GetLocationsModifiedSinceQuery): Result<GetLocationsModifiedSinceQueryResult> {
        logger.d { "Handling GetLocationsModifiedSinceQuery with timestamp: ${query.timestamp}" }

        return when (val result = locationRepository.getModifiedSinceAsync(query.timestamp)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} locations modified since timestamp: ${query.timestamp}" }
                Result.success(
                    GetLocationsModifiedSinceQueryResult(
                        locations = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get locations modified since timestamp: ${query.timestamp} - ${result.error}" }
                Result.success(
                    GetLocationsModifiedSinceQueryResult(
                        locations = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}