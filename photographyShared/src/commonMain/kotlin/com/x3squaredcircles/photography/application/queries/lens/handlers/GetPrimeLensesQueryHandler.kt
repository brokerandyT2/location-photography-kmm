// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetPrimeLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetPrimeLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetPrimeLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetPrimeLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetPrimeLensesQuery, GetPrimeLensesQueryResult> {

    override suspend fun handle(query: GetPrimeLensesQuery): GetPrimeLensesQueryResult {
        return try {
            logger.d { "Handling GetPrimeLensesQuery" }

            val lenses = lensRepository.getPrimeLensesAsync()

            logger.i { "Retrieved ${lenses.size} prime lenses" }

            GetPrimeLensesQueryResult(
                lenses = lenses,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get prime lenses" }
            GetPrimeLensesQueryResult(
                lenses = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}