// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypesTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesTotalCountQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipTypesTotalCountQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypesTotalCountQuery, GetTipTypesTotalCountQueryResult> {

    override suspend fun handle(query: GetTipTypesTotalCountQuery): Result<GetTipTypesTotalCountQueryResult> {
        logger.d { "Handling GetTipTypesTotalCountQuery" }

        return when (val result = tipTypeRepository.getTotalCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved total tip types count: ${result.data}" }
                Result.success(
                    GetTipTypesTotalCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tip types total count: ${result.error}" }
                Result.success(
                    GetTipTypesTotalCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}