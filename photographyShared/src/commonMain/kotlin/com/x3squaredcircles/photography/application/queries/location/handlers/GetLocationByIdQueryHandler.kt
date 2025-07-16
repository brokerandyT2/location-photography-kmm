// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationByIdQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetLocationByIdQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationByIdQuery, GetLocationByIdQueryResult> {

    override suspend fun handle(query: GetLocationByIdQuery): GetLocationByIdQueryResult {
        return try {
            logger.d { "Handling GetLocationByIdQuery with id: ${query.id}" }

            val location = locationRepository.getByIdAsync(query.id)

            logger.i { "Retrieved location with id: ${query.id}, found: ${location != null}" }

            GetLocationByIdQueryResult(
                location = location,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get location by id: ${query.id}" }
            GetLocationByIdQueryResult(
                location = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}