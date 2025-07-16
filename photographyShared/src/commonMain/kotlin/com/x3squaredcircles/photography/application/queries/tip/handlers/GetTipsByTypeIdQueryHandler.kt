// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsByTypeIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers
import com.x3squaredcircles.photography.application.queries.tip.GetTipsByTypeIdQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsByTypeIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger
class GetTipsByTypeIdQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsByTypeIdQuery, GetTipsByTypeIdQueryResult> {
    override suspend fun handle(query: GetTipsByTypeIdQuery): GetTipsByTypeIdQueryResult {
        return try {
            logger.d { "Handling GetTipsByTypeIdQuery with tipTypeId: ${query.tipTypeId}" }

            val tips = tipRepository.getByTypeIdAsync(query.tipTypeId)

            logger.i { "Retrieved ${tips.size} tips for tipTypeId: ${query.tipTypeId}" }

            GetTipsByTypeIdQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tips by tipTypeId: ${query.tipTypeId}" }
            GetTipsByTypeIdQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}