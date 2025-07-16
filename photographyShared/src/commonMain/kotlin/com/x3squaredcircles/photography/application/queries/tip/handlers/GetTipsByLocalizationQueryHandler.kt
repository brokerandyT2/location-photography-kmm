// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsByLocalizationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsByLocalizationQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsByLocalizationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetTipsByLocalizationQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsByLocalizationQuery, GetTipsByLocalizationQueryResult> {

    override suspend fun handle(query: GetTipsByLocalizationQuery): GetTipsByLocalizationQueryResult {
        return try {
            logger.d { "Handling GetTipsByLocalizationQuery with localization: ${query.localization}" }

            val tips = tipRepository.getTipsByLocalizationAsync(query.localization)

            logger.i { "Retrieved ${tips.size} tips for localization: ${query.localization}" }

            GetTipsByLocalizationQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tips by localization: ${query.localization}" }
            GetTipsByLocalizationQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}