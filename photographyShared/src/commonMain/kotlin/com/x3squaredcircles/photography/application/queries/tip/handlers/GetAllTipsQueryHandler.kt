// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetAllTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetAllTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetAllTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetAllTipsQuery, GetAllTipsQueryResult> {

    override suspend fun handle(query: GetAllTipsQuery): Result<GetAllTipsQueryResult> {
        logger.d { "Handling GetAllTipsQuery" }

        return when (val result = tipRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tips" }
                Result.success(
                    GetAllTipsQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all tips: ${result.error}" }
                Result.success(
                    GetAllTipsQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}