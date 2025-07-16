// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsCountByTypeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsCountByTypeQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsCountByTypeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetTipsCountByTypeQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsCountByTypeQuery, GetTipsCountByTypeQueryResult> {

    override suspend fun handle(query: GetTipsCountByTypeQuery): GetTipsCountByTypeQueryResult {
        return try {
            logger.d { "Handling GetTipsCountByTypeQuery with tipTypeId: ${query.tipTypeId}" }

            val count = tipRepository.getCountByTypeAsync(query.tipTypeId)

            logger.i { "Retrieved tips count for tipTypeId ${query.tipTypeId}: $count" }

            GetTipsCountByTypeQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tips count by type: ${query.tipTypeId}" }
            GetTipsCountByTypeQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}