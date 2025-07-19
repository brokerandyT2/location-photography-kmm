// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetNearbyLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetNearbyLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetNearbyLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetNearbyLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetNearbyLocationsQuery, GetNearbyLocationsQueryResult> {

    override suspend fun handle(query: GetNearbyLocationsQuery): Result<GetNearbyLocationsQueryResult> {
        logger.d { "Handling GetNearbyLocationsQuery - center: (${query.centerLatitude}, ${query.centerLongitude}), radius: ${query.radiusKm}km, limit: ${query.limit}" }

        return try {
            val centerCoordinate = Coordinate.create(query.centerLatitude, query.centerLongitude)

            when (val result = locationRepository.getNearbyAsync(
                centerCoordinate = centerCoordinate,
                radiusKm = query.radiusKm,
                limit = query.limit
            )) {
                is Result.Success -> {
                    logger.i { "Retrieved ${result.data.size} nearby locations within ${query.radiusKm}km" }
                    Result.success(
                        GetNearbyLocationsQueryResult(
                            locations = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get nearby locations - ${result.error}" }
                    Result.success(
                        GetNearbyLocationsQueryResult(
                            locations = emptyList(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to create coordinate or get nearby locations" }
            Result.success(
                GetNearbyLocationsQueryResult(
                    locations = emptyList(),
                    isSuccess = false,
                    errorMessage = ex.message ?: "Unknown error occurred"
                )
            )
        }
    }
}