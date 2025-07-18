// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypeByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByNameQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipTypeByNameQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypeByNameQuery, GetTipTypeByNameQueryResult> {

    override suspend fun handle(query: GetTipTypeByNameQuery): GetTipTypeByNameQueryResult {
        logger.d { "Handling GetTipTypeByNameQuery with name: ${query.name}" }

        return when (val result = tipTypeRepository.getByNameAsync(query.name)) {
            is Result.Success -> {
                logger.i { "Retrieved tip type with name: ${query.name}, found: ${result.data != null}" }
                GetTipTypeByNameQueryResult(
                    tipType = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tip type by name: ${query.name} - ${result.error}" }
                GetTipTypeByNameQueryResult(
                    tipType = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}