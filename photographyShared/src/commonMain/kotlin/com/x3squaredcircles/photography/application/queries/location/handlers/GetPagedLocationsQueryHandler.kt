// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetPagedLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetPagedLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetPagedLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class GetPagedLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedLocationsQuery, GetPagedLocationsQueryResult> {

    override suspend fun handle(query: GetPagedLocationsQuery): GetPagedLocationsQueryResult {
        return try {
            logger.d { "Handling GetPagedLocationsQuery - page: ${query.pageNumber}, size: ${query.pageSize}, includeDeleted: ${query.includeDeleted}, searchTerm: ${query.searchTerm}" }

            val locations = locationRepository.getPagedAsync(
                pageNumber = query.pageNumber,
                pageSize = query.pageSize,
                includeDeleted = query.includeDeleted,
                searchTerm = query.searchTerm
            )

            val totalCount = locationRepository.getTotalCountAsync(
                includeDeleted = query.includeDeleted,
                searchTerm = query.searchTerm
            )

            logger.i { "Retrieved ${locations.size} locations for page ${query.pageNumber}, total count: $totalCount" }

            GetPagedLocationsQueryResult(
                locations = locations,
                totalCount = totalCount,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get paged locations" }
            GetPagedLocationsQueryResult(
                locations = emptyList(),
                totalCount = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}