// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationsModifiedSinceQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationsModifiedSinceQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationsModifiedSinceQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetLocationsModifiedSinceQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationsModifiedSinceQuery, GetLocationsModifiedSinceQueryResult> {

    override suspend fun handle(query: GetLocationsModifiedSinceQuery): GetLocationsModifiedSinceQueryResult {
        return try {
            logger.d { "Handling GetLocationsModifiedSinceQuery with timestamp: ${query.timestamp}" }

            val locations = locationRepository.getModifiedSinceAsync(query.timestamp)

            logger.i { "Retrieved ${locations.size} locations modified since timestamp: ${query.timestamp}" }

            GetLocationsModifiedSinceQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get locations modified since timestamp: ${query.timestamp}" }
            GetLocationsModifiedSinceQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}