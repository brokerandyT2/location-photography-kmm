// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationStatsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationStatsQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationStatsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLocationStatsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationStatsQuery, GetLocationStatsQueryResult> {

    override suspend fun handle(query: GetLocationStatsQuery): Result<GetLocationStatsQueryResult> {
        logger.d { "Handling GetLocationStatsQuery" }

        return when (val result = locationRepository.getStatsAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved location stats - total: ${result.data.totalCount}, active: ${result.data.activeCount}, deleted: ${result.data.deletedCount}" }
                Result.success(
                    GetLocationStatsQueryResult(
                        stats = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get location stats: ${result.error}" }
                Result.success(
                    GetLocationStatsQueryResult(
                        stats = com.x3squaredcircles.photography.infrastructure.repositories.interfaces.LocationStats(
                            totalCount = 0L,
                            activeCount = 0L,
                            deletedCount = 0L,
                            withPhotosCount = 0L,
                            oldestTimestamp = null,
                            newestTimestamp = null
                        ),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}