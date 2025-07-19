// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetZoomLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetZoomLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetZoomLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetZoomLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetZoomLensesQuery, GetZoomLensesQueryResult> {

    override suspend fun handle(query: GetZoomLensesQuery): Result<GetZoomLensesQueryResult> {
        logger.d { "Handling GetZoomLensesQuery" }

        return when (val result = lensRepository.getZoomLensesAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} zoom lenses" }
                Result.success(
                    GetZoomLensesQueryResult(
                        lenses = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get zoom lenses: ${result.error}" }
                Result.success(
                    GetZoomLensesQueryResult(
                        lenses = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}