// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetAllLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetAllLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetAllLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLensesQuery, GetAllLensesQueryResult> {

    override suspend fun handle(query: GetAllLensesQuery): Result<GetAllLensesQueryResult> {
        logger.d { "Handling GetAllLensesQuery" }

        return when (val result = lensRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} lenses" }
                Result.success(
                    GetAllLensesQueryResult(
                        lenses = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all lenses: ${result.error}" }
                Result.success(
                    GetAllLensesQueryResult(
                        lenses = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}