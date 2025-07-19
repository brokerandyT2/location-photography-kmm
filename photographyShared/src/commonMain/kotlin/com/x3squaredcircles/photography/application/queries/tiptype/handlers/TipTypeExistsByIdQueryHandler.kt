// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/TipTypeExistsByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByIdQuery
import com.x3squaredcircles.photography.application.queries.tiptype.TipTypeExistsByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class TipTypeExistsByIdQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<TipTypeExistsByIdQuery, TipTypeExistsByIdQueryResult> {

    override suspend fun handle(query: TipTypeExistsByIdQuery): Result<TipTypeExistsByIdQueryResult> {
        logger.d { "Handling TipTypeExistsByIdQuery with id: ${query.id}" }

        return when (val result = tipTypeRepository.existsByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "TipType exists check for id ${query.id}: ${result.data}" }
                Result.success(
                    TipTypeExistsByIdQueryResult(
                        exists = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if tip type exists by id: ${query.id} - ${result.error}" }
                Result.success(
                    TipTypeExistsByIdQueryResult(
                        exists = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}