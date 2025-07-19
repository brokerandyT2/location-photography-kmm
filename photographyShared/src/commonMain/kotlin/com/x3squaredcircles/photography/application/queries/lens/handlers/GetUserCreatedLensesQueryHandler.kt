// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetUserCreatedLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetUserCreatedLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetUserCreatedLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetUserCreatedLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetUserCreatedLensesQuery, GetUserCreatedLensesQueryResult> {

    override suspend fun handle(query: GetUserCreatedLensesQuery): Result<GetUserCreatedLensesQueryResult> {
        logger.d { "Handling GetUserCreatedLensesQuery" }

        return when (val result = lensRepository.getUserCreatedAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} user created lenses" }
                Result.success(
                    GetUserCreatedLensesQueryResult(
                        lenses = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get user created lenses: ${result.error}" }
                Result.success(
                    GetUserCreatedLensesQueryResult(
                        lenses = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}