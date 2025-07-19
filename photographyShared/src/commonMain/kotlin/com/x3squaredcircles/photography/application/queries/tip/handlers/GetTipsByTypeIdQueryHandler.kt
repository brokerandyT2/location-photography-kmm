// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsByTypeIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsByTypeIdQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsByTypeIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipsByTypeIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsByTypeIdQuery, GetTipsByTypeIdQueryResult> {

    override suspend fun handle(query: GetTipsByTypeIdQuery): Result<GetTipsByTypeIdQueryResult> {
        logger.d { "Handling GetTipsByTypeIdQuery with tipTypeId: ${query.tipTypeId}" }

        return when (val result = tipRepository.getByTypeIdAsync(query.tipTypeId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tips for tipTypeId: ${query.tipTypeId}" }
                Result.success(
                    GetTipsByTypeIdQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tips by tipTypeId: ${query.tipTypeId} - ${result.error}" }
                Result.success(
                    GetTipsByTypeIdQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}