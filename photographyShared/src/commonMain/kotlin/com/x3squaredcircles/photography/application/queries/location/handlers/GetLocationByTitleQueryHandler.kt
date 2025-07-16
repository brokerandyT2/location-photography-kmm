// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationByTitleQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationByTitleQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationByTitleQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetLocationByTitleQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationByTitleQuery, GetLocationByTitleQueryResult> {

    override suspend fun handle(query: GetLocationByTitleQuery): GetLocationByTitleQueryResult {
        return try {
            logger.d { "Handling GetLocationByTitleQuery with title: ${query.title}" }

            val location = locationRepository.getByTitleAsync(query.title)

            logger.i { "Retrieved location with title: ${query.title}, found: ${location != null}" }

            GetLocationByTitleQueryResult(
                location = location,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get location by title: ${query.title}" }
            GetLocationByTitleQueryResult(
                location = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}