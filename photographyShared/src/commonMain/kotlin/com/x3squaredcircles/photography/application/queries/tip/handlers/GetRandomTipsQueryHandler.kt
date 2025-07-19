// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetRandomTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetRandomTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetRandomTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetRandomTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetRandomTipsQuery, GetRandomTipsQueryResult> {

    override suspend fun handle(query: GetRandomTipsQuery): Result<GetRandomTipsQueryResult> {
        logger.d { "Handling GetRandomTipsQuery with count: ${query.count}" }

        return when (val result = tipRepository.getRandomAsync(query.count)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} random tips" }
                Result.success(
                    GetRandomTipsQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get random tips with count: ${query.count} - ${result.error}" }
                Result.success(
                    GetRandomTipsQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}