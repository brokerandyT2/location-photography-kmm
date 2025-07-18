// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/TipExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.TipExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.tip.TipExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class TipExistsByIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<TipExistsByIdQuery, TipExistsByIdQueryResult> {

    override suspend fun handle(query: TipExistsByIdQuery): TipExistsByIdQueryResult {
        logger.d { "Handling TipExistsByIdQuery with id: ${query.id}" }

        return when (val result = tipRepository.existsByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Tip exists check for id ${query.id}: ${result.data}" }
                TipExistsByIdQueryResult(
                    exists = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if tip exists by id: ${query.id} - ${result.error}" }
                TipExistsByIdQueryResult(
                    exists = false,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}