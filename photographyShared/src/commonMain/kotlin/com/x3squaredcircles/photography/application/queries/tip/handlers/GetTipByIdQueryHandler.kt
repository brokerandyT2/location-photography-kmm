// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers
import com.x3squaredcircles.photography.application.queries.tip.GetTipByIdQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger
class GetTipByIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipByIdQuery, GetTipByIdQueryResult> {
    override suspend fun handle(query: GetTipByIdQuery): GetTipByIdQueryResult {
        return try {
            logger.d { "Handling GetTipByIdQuery with id: ${query.id}" }

            val tip = tipRepository.getByIdAsync(query.id)

            logger.i { "Retrieved tip with id: ${query.id}, found: ${tip != null}" }

            GetTipByIdQueryResult(
                tip = tip,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tip by id: ${query.id}" }
            GetTipByIdQueryResult(
                tip = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}