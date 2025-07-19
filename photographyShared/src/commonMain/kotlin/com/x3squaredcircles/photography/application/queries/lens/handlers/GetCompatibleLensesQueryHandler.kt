// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetCompatibleLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetCompatibleLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetCompatibleLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetCompatibleLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetCompatibleLensesQuery, GetCompatibleLensesQueryResult> {

    override suspend fun handle(query: GetCompatibleLensesQuery): Result<GetCompatibleLensesQueryResult> {
        logger.d { "Handling GetCompatibleLensesQuery with cameraBodyId: ${query.cameraBodyId}" }

        return when (val result = lensRepository.getCompatibleLensesAsync(query.cameraBodyId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} compatible lenses for cameraBodyId: ${query.cameraBodyId}" }
                Result.success(
                    GetCompatibleLensesQueryResult(
                        lenses = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get compatible lenses for cameraBodyId: ${query.cameraBodyId} - ${result.error}" }
                Result.success(
                    GetCompatibleLensesQueryResult(
                        lenses = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}