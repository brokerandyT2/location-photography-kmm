// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/TipTypeExistsByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByNameQuery
import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class TipTypeExistsByNameQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<TipTypeExistsByNameQuery, TipTypeExistsByNameQueryResult> {

    override suspend fun handle(query: TipTypeExistsByNameQuery): Result<TipTypeExistsByNameQueryResult> {
        logger.d { "Handling TipTypeExistsByNameQuery with name: ${query.name}, excludeId: ${query.excludeId}" }

        return when (val result = tipTypeRepository.existsByNameAsync(query.name, query.excludeId)) {
            is Result.Success -> {
                logger.i { "TipType exists check for name '${query.name}' (excludeId: ${query.excludeId}): ${result.data}" }
                Result.success(
                    TipTypeExistsByNameQueryResult(
                        exists = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if tip type exists by name: ${query.name} - ${result.error}" }
                Result.success(
                    TipTypeExistsByNameQueryResult(
                        exists = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}