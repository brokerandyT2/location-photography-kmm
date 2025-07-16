// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/SearchCameraBodiesByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.SearchCameraBodiesByNameQuery
import com.x3squaredcircles.photography.application.queries.camerabody.SearchCameraBodiesByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class SearchCameraBodiesByNameQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<SearchCameraBodiesByNameQuery, SearchCameraBodiesByNameQueryResult> {

    override suspend fun handle(query: SearchCameraBodiesByNameQuery): SearchCameraBodiesByNameQueryResult {
        return try {
            logger.d { "Handling SearchCameraBodiesByNameQuery with searchTerm: ${query.searchTerm}" }

            val cameraBodies = cameraBodyRepository.searchByNameAsync(query.searchTerm)

            logger.i { "Retrieved ${cameraBodies.size} camera bodies for search term: ${query.searchTerm}" }

            SearchCameraBodiesByNameQueryResult(
                cameraBodies = cameraBodies,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to search camera bodies by name: ${query.searchTerm}" }
            SearchCameraBodiesByNameQueryResult(
                cameraBodies = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}