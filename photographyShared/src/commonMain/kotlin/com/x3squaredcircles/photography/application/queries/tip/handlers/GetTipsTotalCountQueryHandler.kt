// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsTotalCountQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetTipsTotalCountQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsTotalCountQuery, GetTipsTotalCountQueryResult> {

    override suspend fun handle(query: GetTipsTotalCountQuery): GetTipsTotalCountQueryResult {
        return try {
            logger.d { "Handling GetTipsTotalCountQuery" }

            val count = tipRepository.getTotalCountAsync()

            logger.i { "Retrieved total tips count: $count" }

            GetTipsTotalCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tips total count" }
            GetTipsTotalCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}