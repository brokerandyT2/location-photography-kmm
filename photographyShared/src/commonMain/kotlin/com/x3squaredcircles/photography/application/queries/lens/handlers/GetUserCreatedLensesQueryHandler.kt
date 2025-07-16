// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetUserCreatedLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetUserCreatedLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetUserCreatedLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetUserCreatedLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetUserCreatedLensesQuery, GetUserCreatedLensesQueryResult> {

    override suspend fun handle(query: GetUserCreatedLensesQuery): GetUserCreatedLensesQueryResult {
        return try {
            logger.d { "Handling GetUserCreatedLensesQuery" }

            val lenses = lensRepository.getUserCreatedAsync()

            logger.i { "Retrieved ${lenses.size} user created lenses" }

            GetUserCreatedLensesQueryResult(
                lenses = lenses,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get user created lenses" }
            GetUserCreatedLensesQueryResult(
                lenses = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}