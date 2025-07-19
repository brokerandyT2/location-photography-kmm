// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetLensByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetLensByIdQuery
import com.x3squaredcircles.photography.application.queries.lens.GetLensByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLensByIdQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetLensByIdQuery, GetLensByIdQueryResult> {

    override suspend fun handle(query: GetLensByIdQuery): Result<GetLensByIdQueryResult> {
        logger.d { "Handling GetLensByIdQuery with id: ${query.id}" }

        return when (val result = lensRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved lens with id: ${query.id}, found: ${result.data != null}" }
                Result.success(
                    GetLensByIdQueryResult(
                        lens = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get lens by id: ${query.id} - ${result.error}" }
                Result.success(
                    GetLensByIdQueryResult(
                        lens = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}