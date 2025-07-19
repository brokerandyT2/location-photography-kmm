// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsTotalCountQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipsTotalCountQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsTotalCountQuery, GetTipsTotalCountQueryResult> {

    override suspend fun handle(query: GetTipsTotalCountQuery): Result<GetTipsTotalCountQueryResult> {
        logger.d { "Handling GetTipsTotalCountQuery" }

        return when (val result = tipRepository.getTotalCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved total tips count: ${result.data}" }
                Result.success(
                    GetTipsTotalCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tips total count: ${result.error}" }
                Result.success(
                    GetTipsTotalCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}