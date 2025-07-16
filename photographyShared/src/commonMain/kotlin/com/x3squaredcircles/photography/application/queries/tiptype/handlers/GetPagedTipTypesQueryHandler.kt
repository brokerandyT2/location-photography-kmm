// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetPagedTipTypesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetPagedTipTypesQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetPagedTipTypesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetPagedTipTypesQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedTipTypesQuery, GetPagedTipTypesQueryResult> {

    override suspend fun handle(query: GetPagedTipTypesQuery): GetPagedTipTypesQueryResult {
        return try {
            logger.d { "Handling GetPagedTipTypesQuery - page: ${query.pageNumber}, size: ${query.pageSize}" }

            val tipTypes = tipTypeRepository.getPagedAsync(
                pageNumber = query.pageNumber,
                pageSize = query.pageSize
            )

            logger.i { "Retrieved ${tipTypes.size} tip types for page ${query.pageNumber}" }

            GetPagedTipTypesQueryResult(
                tipTypes = tipTypes,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get paged tip types - page: ${query.pageNumber}, size: ${query.pageSize}" }
            GetPagedTipTypesQueryResult(
                tipTypes = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}