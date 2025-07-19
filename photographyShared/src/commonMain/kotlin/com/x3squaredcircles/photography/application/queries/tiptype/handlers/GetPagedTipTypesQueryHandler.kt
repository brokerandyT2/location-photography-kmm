// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetPagedTipTypesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetPagedTipTypesQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetPagedTipTypesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPagedTipTypesQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedTipTypesQuery, GetPagedTipTypesQueryResult> {

    override suspend fun handle(query: GetPagedTipTypesQuery): Result<GetPagedTipTypesQueryResult> {
        logger.d { "Handling GetPagedTipTypesQuery - page: ${query.pageNumber}, size: ${query.pageSize}" }

        return when (val result = tipTypeRepository.getPagedAsync(
            pageNumber = query.pageNumber,
            pageSize = query.pageSize
        )) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tip types for page ${query.pageNumber}" }
                Result.success(
                    GetPagedTipTypesQueryResult(
                        tipTypes = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get paged tip types - page: ${query.pageNumber}, size: ${query.pageSize} - ${result.error}" }
                Result.success(
                    GetPagedTipTypesQueryResult(
                        tipTypes = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}