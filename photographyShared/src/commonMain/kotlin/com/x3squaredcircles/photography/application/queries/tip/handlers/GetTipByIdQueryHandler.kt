// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipByIdQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipByIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipByIdQuery, GetTipByIdQueryResult> {

    override suspend fun handle(query: GetTipByIdQuery): GetTipByIdQueryResult {
        logger.d { "Handling GetTipByIdQuery with id: ${query.id}" }

        return when (val result = tipRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved tip with id: ${query.id}, found: ${result.data != null}" }
                GetTipByIdQueryResult(
                    tip = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tip by id: ${query.id} - ${result.error}" }
                GetTipByIdQueryResult(
                    tip = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}