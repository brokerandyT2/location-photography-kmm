// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/TipExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.TipExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.tip.TipExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class TipExistsByIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<TipExistsByIdQuery, TipExistsByIdQueryResult> {

    override suspend fun handle(query: TipExistsByIdQuery): TipExistsByIdQueryResult {
        return try {
            logger.d { "Handling TipExistsByIdQuery with id: ${query.id}" }

            val exists = tipRepository.existsByIdAsync(query.id)

            logger.i { "Tip exists check for id ${query.id}: $exists" }

            TipExistsByIdQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if tip exists by id: ${query.id}" }
            TipExistsByIdQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}