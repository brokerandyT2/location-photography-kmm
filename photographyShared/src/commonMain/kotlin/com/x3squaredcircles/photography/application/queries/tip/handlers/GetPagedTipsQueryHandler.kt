// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetPagedTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetPagedTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetPagedTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPagedTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedTipsQuery, GetPagedTipsQueryResult> {

    override suspend fun handle(query: GetPagedTipsQuery): Result<GetPagedTipsQueryResult> {
        logger.d { "Handling GetPagedTipsQuery - page: ${query.pageNumber}, size: ${query.pageSize}, tipTypeId: ${query.tipTypeId}" }

        return when (val result = tipRepository.getPagedAsync(
            pageNumber = query.pageNumber,
            pageSize = query.pageSize,
            tipTypeId = query.tipTypeId
        )) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tips for page ${query.pageNumber}" }
                Result.success(
                    GetPagedTipsQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get paged tips - page: ${query.pageNumber}, size: ${query.pageSize}, tipTypeId: ${query.tipTypeId} - ${result.error}" }
                Result.success(
                    GetPagedTipsQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}