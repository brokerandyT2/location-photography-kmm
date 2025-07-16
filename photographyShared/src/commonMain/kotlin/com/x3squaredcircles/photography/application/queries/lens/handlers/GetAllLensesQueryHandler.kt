// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetAllLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetAllLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetAllLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetAllLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLensesQuery, GetAllLensesQueryResult> {

    override suspend fun handle(query: GetAllLensesQuery): GetAllLensesQueryResult {
        return try {
            logger.d { "Handling GetAllLensesQuery" }

            val lenses = lensRepository.getAllAsync()

            logger.i { "Retrieved ${lenses.size} lenses" }

            GetAllLensesQueryResult(
                lenses = lenses,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all lenses" }
            GetAllLensesQueryResult(
                lenses = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}