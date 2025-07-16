// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/handlers/SearchLocationsByTextQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.location.handlers

import com.x3squaredcircles.photography.application.queries.location.SearchLocationsByTextQuery
import com.x3squaredcircles.photography.application.queries.location.SearchLocationsByTextQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import co.touchlab.kermit.Logger

class SearchLocationsByTextQueryHandler(
    private val locationRepository: ILocationRepository,
    private val logger: Logger
) : IQueryHandler<SearchLocationsByTextQuery, SearchLocationsByTextQueryResult> {

    override suspend fun handle(query: SearchLocationsByTextQuery): SearchLocationsByTextQueryResult {
        return try {
            logger.d { "Handling SearchLocationsByTextQuery with searchTerm: ${query.searchTerm}, includeDeleted: ${query.includeDeleted}" }

            val locations = locationRepository.searchByTextAsync(
                searchTerm = query.searchTerm,
                includeDeleted = query.includeDeleted
            )

            logger.i { "Found ${locations.size} locations matching search term: ${query.searchTerm}" }

            SearchLocationsByTextQueryResult(
                locations = locations,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to search locations by text: ${query.searchTerm}" }
            SearchLocationsByTextQueryResult(
                locations = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}