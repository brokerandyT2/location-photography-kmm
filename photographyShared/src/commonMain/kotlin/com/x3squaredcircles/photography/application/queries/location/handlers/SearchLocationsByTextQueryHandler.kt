// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/SearchLocationsByTextQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.SearchLocationsByTextQuery
import com.x3squaredcircles.photography.application.queries.location.SearchLocationsByTextQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class SearchLocationsByTextQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<SearchLocationsByTextQuery, SearchLocationsByTextQueryResult> {

    override suspend fun handle(query: SearchLocationsByTextQuery): SearchLocationsByTextQueryResult {
        logger.d { "Handling SearchLocationsByTextQuery with searchTerm: ${query.searchTerm}, includeDeleted: ${query.includeDeleted}" }

        return when (val result = locationRepository.searchByTextAsync(
            searchTerm = query.searchTerm,
            includeDeleted = query.includeDeleted
        )) {
            is Result.Success -> {
                logger.i { "Found ${result.data.size} locations matching search term: ${query.searchTerm}" }
                SearchLocationsByTextQueryResult(
                    locations = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to search locations by text: ${query.searchTerm} - ${result.error}" }
                SearchLocationsByTextQueryResult(
                    locations = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}