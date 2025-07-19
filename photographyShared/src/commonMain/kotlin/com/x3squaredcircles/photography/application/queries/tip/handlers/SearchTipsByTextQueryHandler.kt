// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/SearchTipsByTextQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.SearchTipsByTextQuery
import com.x3squaredcircles.photography.application.queries.tip.SearchTipsByTextQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class SearchTipsByTextQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<SearchTipsByTextQuery, SearchTipsByTextQueryResult> {

    override suspend fun handle(query: SearchTipsByTextQuery): Result<SearchTipsByTextQueryResult> {
        logger.d { "Handling SearchTipsByTextQuery with searchTerm: ${query.searchTerm}" }

        return when (val result = tipRepository.searchByTextAsync(query.searchTerm)) {
            is Result.Success -> {
                logger.i { "Found ${result.data.size} tips matching search term: ${query.searchTerm}" }
                Result.success(
                    SearchTipsByTextQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to search tips by text: ${query.searchTerm} - ${result.error}" }
                Result.success(
                    SearchTipsByTextQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}