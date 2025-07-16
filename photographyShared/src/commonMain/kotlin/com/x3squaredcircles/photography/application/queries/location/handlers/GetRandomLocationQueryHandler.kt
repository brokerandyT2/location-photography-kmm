// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetRandomLocationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetRandomLocationQuery
import com.x3squaredcircles.photography.application.queries.location.GetRandomLocationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetRandomLocationQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetRandomLocationQuery, GetRandomLocationQueryResult> {

    override suspend fun handle(query: GetRandomLocationQuery): GetRandomLocationQueryResult {
        return try {
            logger.d { "Handling GetRandomLocationQuery" }

            val location = locationRepository.getRandomAsync()

            logger.i { "Retrieved random location: ${location != null}" }

            GetRandomLocationQueryResult(
                location = location,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get random location" }
            GetRandomLocationQueryResult(
                location = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}