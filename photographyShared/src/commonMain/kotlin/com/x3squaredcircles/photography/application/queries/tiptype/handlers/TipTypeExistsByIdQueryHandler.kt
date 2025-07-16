// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/TipTypeExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class TipTypeExistsByIdQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<TipTypeExistsByIdQuery, TipTypeExistsByIdQueryResult> {

    override suspend fun handle(query: TipTypeExistsByIdQuery): TipTypeExistsByIdQueryResult {
        return try {
            logger.d { "Handling TipTypeExistsByIdQuery with id: ${query.id}" }

            val exists = tipTypeRepository.existsByIdAsync(query.id)

            logger.i { "TipType exists check for id ${query.id}: $exists" }

            TipTypeExistsByIdQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if tip type exists by id: ${query.id}" }
            TipTypeExistsByIdQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}