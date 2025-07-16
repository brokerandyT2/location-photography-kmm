// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/SearchTipsByTextQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.SearchTipsByTextQuery
import com.x3squaredcircles.photography.application.queries.tip.SearchTipsByTextQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class SearchTipsByTextQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<SearchTipsByTextQuery, SearchTipsByTextQueryResult> {

    override suspend fun handle(query: SearchTipsByTextQuery): SearchTipsByTextQueryResult {
        return try {
            logger.d { "Handling SearchTipsByTextQuery with searchTerm: ${query.searchTerm}" }

            val tips = tipRepository.searchByTextAsync(query.searchTerm)

            logger.i { "Found ${tips.size} tips matching search term: ${query.searchTerm}" }

            SearchTipsByTextQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to search tips by text: ${query.searchTerm}" }
            SearchTipsByTextQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}