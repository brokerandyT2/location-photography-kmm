// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/TipTypeExistsByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByNameQuery
import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class TipTypeExistsByNameQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<TipTypeExistsByNameQuery, TipTypeExistsByNameQueryResult> {

    override suspend fun handle(query: TipTypeExistsByNameQuery): TipTypeExistsByNameQueryResult {
        return try {
            logger.d { "Handling TipTypeExistsByNameQuery with name: ${query.name}, excludeId: ${query.excludeId}" }

            val exists = tipTypeRepository.existsByNameAsync(query.name, query.excludeId)

            logger.i { "TipType exists check for name '${query.name}' (excludeId: ${query.excludeId}): $exists" }

            TipTypeExistsByNameQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if tip type exists by name: ${query.name}" }
            TipTypeExistsByNameQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}