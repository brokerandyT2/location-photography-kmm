// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsCountByTypeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsCountByTypeQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsCountByTypeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipsCountByTypeQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsCountByTypeQuery, GetTipsCountByTypeQueryResult> {

    override suspend fun handle(query: GetTipsCountByTypeQuery): Result<GetTipsCountByTypeQueryResult> {
        logger.d { "Handling GetTipsCountByTypeQuery with tipTypeId: ${query.tipTypeId}" }

        return when (val result = tipRepository.getCountByTypeAsync(query.tipTypeId)) {
            is Result.Success -> {
                logger.i { "Retrieved tips count for tipTypeId ${query.tipTypeId}: ${result.data}" }
                Result.success(
                    GetTipsCountByTypeQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tips count by type: ${query.tipTypeId} - ${result.error}" }
                Result.success(
                    GetTipsCountByTypeQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}