// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetCompatibleLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetCompatibleLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetCompatibleLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetCompatibleLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetCompatibleLensesQuery, GetCompatibleLensesQueryResult> {

    override suspend fun handle(query: GetCompatibleLensesQuery): GetCompatibleLensesQueryResult {
        return try {
            logger.d { "Handling GetCompatibleLensesQuery with cameraBodyId: ${query.cameraBodyId}" }

            val lenses = lensRepository.getCompatibleLensesAsync(query.cameraBodyId)

            logger.i { "Retrieved ${lenses.size} compatible lenses for cameraBodyId: ${query.cameraBodyId}" }

            GetCompatibleLensesQueryResult(
                lenses = lenses,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get compatible lenses for cameraBodyId: ${query.cameraBodyId}" }
            GetCompatibleLensesQueryResult(
                lenses = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}