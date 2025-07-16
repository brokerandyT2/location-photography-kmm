// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetLensByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetLensByIdQuery
import com.x3squaredcircles.photography.application.queries.lens.GetLensByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import co.touchlab.kermit.Logger

class GetLensByIdQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetLensByIdQuery, GetLensByIdQueryResult> {

    override suspend fun handle(query: GetLensByIdQuery): GetLensByIdQueryResult {
        return try {
            logger.d { "Handling GetLensByIdQuery with id: ${query.id}" }

            val lens = lensRepository.getByIdAsync(query.id)

            logger.i { "Retrieved lens with id: ${query.id}, found: ${lens != null}" }

            GetLensByIdQueryResult(
                lens = lens,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get lens by id: ${query.id}" }
            GetLensByIdQueryResult(
                lens = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}