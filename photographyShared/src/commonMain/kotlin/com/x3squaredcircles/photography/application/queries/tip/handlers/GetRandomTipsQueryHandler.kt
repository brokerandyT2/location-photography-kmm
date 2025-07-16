// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetRandomTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetRandomTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetRandomTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetRandomTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetRandomTipsQuery, GetRandomTipsQueryResult> {

    override suspend fun handle(query: GetRandomTipsQuery): GetRandomTipsQueryResult {
        return try {
            logger.d { "Handling GetRandomTipsQuery with count: ${query.count}" }

            val tips = tipRepository.getRandomAsync(query.count)

            logger.i { "Retrieved ${tips.size} random tips" }

            GetRandomTipsQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get random tips with count: ${query.count}" }
            GetRandomTipsQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}