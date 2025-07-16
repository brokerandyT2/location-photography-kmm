// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetPagedTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetPagedTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetPagedTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetPagedTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedTipsQuery, GetPagedTipsQueryResult> {

    override suspend fun handle(query: GetPagedTipsQuery): GetPagedTipsQueryResult {
        return try {
            logger.d { "Handling GetPagedTipsQuery - page: ${query.pageNumber}, size: ${query.pageSize}, tipTypeId: ${query.tipTypeId}" }

            val tips = tipRepository.getPagedAsync(
                pageNumber = query.pageNumber,
                pageSize = query.pageSize,
                tipTypeId = query.tipTypeId
            )

            logger.i { "Retrieved ${tips.size} tips for page ${query.pageNumber}" }

            GetPagedTipsQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get paged tips - page: ${query.pageNumber}, size: ${query.pageSize}, tipTypeId: ${query.tipTypeId}" }
            GetPagedTipsQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}