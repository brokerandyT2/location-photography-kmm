// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetPrimeLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetPrimeLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetPrimeLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPrimeLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetPrimeLensesQuery, GetPrimeLensesQueryResult> {

    override suspend fun handle(query: GetPrimeLensesQuery): GetPrimeLensesQueryResult {
        logger.d { "Handling GetPrimeLensesQuery" }

        return when (val result = lensRepository.getPrimeLensesAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} prime lenses" }
                GetPrimeLensesQueryResult(
                    lenses = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get prime lenses: ${result.error}" }
                GetPrimeLensesQueryResult(
                    lenses = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}