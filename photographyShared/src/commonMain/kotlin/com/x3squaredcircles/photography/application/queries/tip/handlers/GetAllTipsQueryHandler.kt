// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetAllTipsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetAllTipsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetAllTipsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetAllTipsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetAllTipsQuery, GetAllTipsQueryResult> {

    override suspend fun handle(query: GetAllTipsQuery): GetAllTipsQueryResult {
        return try {
            logger.d { "Handling GetAllTipsQuery" }

            val tips = tipRepository.getAllAsync()

            logger.i { "Retrieved ${tips.size} tips" }

            GetAllTipsQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all tips" }
            GetAllTipsQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}