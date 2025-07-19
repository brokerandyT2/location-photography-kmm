// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetAllTipTypesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetAllTipTypesQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetAllTipTypesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllTipTypesQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetAllTipTypesQuery, GetAllTipTypesQueryResult> {

    override suspend fun handle(query: GetAllTipTypesQuery): Result<GetAllTipTypesQueryResult> {
        logger.d { "Handling GetAllTipTypesQuery" }

        return when (val result = tipTypeRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tip types" }
                Result.success(
                    GetAllTipTypesQueryResult(
                        tipTypes = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all tip types: ${result.error}" }
                Result.success(
                    GetAllTipTypesQueryResult(
                        tipTypes = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}