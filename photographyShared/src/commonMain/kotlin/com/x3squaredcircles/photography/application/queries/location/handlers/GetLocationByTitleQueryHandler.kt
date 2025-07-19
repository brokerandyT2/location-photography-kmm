// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationByTitleQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationByTitleQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationByTitleQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLocationByTitleQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationByTitleQuery, GetLocationByTitleQueryResult> {

    override suspend fun handle(query: GetLocationByTitleQuery): Result<GetLocationByTitleQueryResult> {
        logger.d { "Handling GetLocationByTitleQuery with title: ${query.title}" }

        return when (val result = locationRepository.getByTitleAsync(query.title)) {
            is Result.Success -> {
                logger.i { "Retrieved location with title: ${query.title}, found: ${result.data != null}" }
                Result.success(
                    GetLocationByTitleQueryResult(
                        location = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get location by title: ${query.title} - ${result.error}" }
                Result.success(
                    GetLocationByTitleQueryResult(
                        location = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}