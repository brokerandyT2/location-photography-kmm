// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/GetPagedLocationsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.GetPagedLocationsQuery
import com.x3squaredcircles.photography.application.queries.location.GetPagedLocationsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPagedLocationsQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedLocationsQuery, GetPagedLocationsQueryResult> {

    override suspend fun handle(query: GetPagedLocationsQuery): Result<GetPagedLocationsQueryResult> {
        logger.d { "Handling GetPagedLocationsQuery - page: ${query.pageNumber}, size: ${query.pageSize}, includeDeleted: ${query.includeDeleted}, searchTerm: ${query.searchTerm}" }

        val locationsResult = locationRepository.getPagedAsync(
            pageNumber = query.pageNumber,
            pageSize = query.pageSize,
            includeDeleted = query.includeDeleted,
            searchTerm = query.searchTerm
        )

        return when (locationsResult) {
            is Result.Success -> {
                when (val totalCountResult = locationRepository.getTotalCountAsync(
                    includeDeleted = query.includeDeleted,
                    searchTerm = query.searchTerm
                )) {
                    is Result.Success -> {
                        logger.i { "Retrieved ${locationsResult.data.size} locations for page ${query.pageNumber}, total count: ${totalCountResult.data}" }
                        Result.success(
                            GetPagedLocationsQueryResult(
                                locations = locationsResult.data,
                                totalCount = totalCountResult.data,
                                isSuccess = true
                            )
                        )
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to get total count: ${totalCountResult.error}" }
                        Result.success(
                            GetPagedLocationsQueryResult(
                                locations = emptyList(),
                                totalCount = 0L,
                                isSuccess = false,
                                errorMessage = totalCountResult.error
                            )
                        )
                    }
                }
            }
            is Result.Failure -> {
                logger.e { "Failed to get paged locations: ${locationsResult.error}" }
                Result.success(
                    GetPagedLocationsQueryResult(
                        locations = emptyList(),
                        totalCount = 0L,
                        isSuccess = false,
                        errorMessage = locationsResult.error
                    )
                )
            }
        }
    }
}