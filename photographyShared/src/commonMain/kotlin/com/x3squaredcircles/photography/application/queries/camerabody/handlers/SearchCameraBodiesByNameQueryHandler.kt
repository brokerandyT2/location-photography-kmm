// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/SearchCameraBodiesByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.SearchCameraBodiesByNameQuery
import com.x3squaredcircles.photography.application.queries.camerabody.SearchCameraBodiesByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class SearchCameraBodiesByNameQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<SearchCameraBodiesByNameQuery, SearchCameraBodiesByNameQueryResult> {

    override suspend fun handle(query: SearchCameraBodiesByNameQuery): SearchCameraBodiesByNameQueryResult {
        logger.d { "Handling SearchCameraBodiesByNameQuery with searchTerm: ${query.searchTerm}" }

        return when (val result = cameraBodyRepository.searchByNameAsync(query.searchTerm)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} camera bodies for search term: ${query.searchTerm}" }
                SearchCameraBodiesByNameQueryResult(
                    cameraBodies = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to search camera bodies by name: ${query.searchTerm} - ${result.error}" }
                SearchCameraBodiesByNameQueryResult(
                    cameraBodies = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}