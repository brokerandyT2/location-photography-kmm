// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypesTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesTotalCountQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetTipTypesTotalCountQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypesTotalCountQuery, GetTipTypesTotalCountQueryResult> {

    override suspend fun handle(query: GetTipTypesTotalCountQuery): GetTipTypesTotalCountQueryResult {
        return try {
            logger.d { "Handling GetTipTypesTotalCountQuery" }

            val count = tipTypeRepository.getTotalCountAsync()

            logger.i { "Retrieved total tip types count: $count" }

            GetTipTypesTotalCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tip types total count" }
            GetTipTypesTotalCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}