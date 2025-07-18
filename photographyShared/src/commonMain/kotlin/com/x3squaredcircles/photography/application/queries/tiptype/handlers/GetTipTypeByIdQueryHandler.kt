// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypeByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByIdQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipTypeByIdQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypeByIdQuery, GetTipTypeByIdQueryResult> {

    override suspend fun handle(query: GetTipTypeByIdQuery): GetTipTypeByIdQueryResult {
        logger.d { "Handling GetTipTypeByIdQuery with id: ${query.id}" }

        return when (val result = tipTypeRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved tip type with id: ${query.id}, found: ${result.data != null}" }
                GetTipTypeByIdQueryResult(
                    tipType = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tip type by id: ${query.id} - ${result.error}" }
                GetTipTypeByIdQueryResult(
                    tipType = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}