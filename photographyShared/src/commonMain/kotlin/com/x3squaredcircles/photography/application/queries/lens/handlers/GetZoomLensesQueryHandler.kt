// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetZoomLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetZoomLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetZoomLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetZoomLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetZoomLensesQuery, GetZoomLensesQueryResult> {

    override suspend fun handle(query: GetZoomLensesQuery): GetZoomLensesQueryResult {
        return try {
            logger.d { "Handling GetZoomLensesQuery" }

            val lenses = lensRepository.getZoomLensesAsync()

            logger.i { "Retrieved ${lenses.size} zoom lenses" }

            GetZoomLensesQueryResult(
                lenses = lenses,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get zoom lenses" }
            GetZoomLensesQueryResult(
                lenses = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}