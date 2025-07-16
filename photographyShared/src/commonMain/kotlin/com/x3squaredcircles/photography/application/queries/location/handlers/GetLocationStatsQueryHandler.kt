// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetLocationStatsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetLocationStatsQuery
import com.x3squaredcircles.photography.application.queries.location.GetLocationStatsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetLocationStatsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetLocationStatsQuery, GetLocationStatsQueryResult> {

    override suspend fun handle(query: GetLocationStatsQuery): GetLocationStatsQueryResult {
        return try {
            logger.d { "Handling GetLocationStatsQuery" }

            val stats = locationRepository.getStatsAsync()

            logger.i { "Retrieved location stats - total: ${stats.totalCount}, active: ${stats.activeCount}, deleted: ${stats.deletedCount}" }

            GetLocationStatsQueryResult(
                stats = stats,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get location stats" }
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
                errorMessage = ex.message
            )
        }
    }
}