// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsByLocalizationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsByLocalizationQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsByLocalizationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipsByLocalizationQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsByLocalizationQuery, GetTipsByLocalizationQueryResult> {

    override suspend fun handle(query: GetTipsByLocalizationQuery): GetTipsByLocalizationQueryResult {
        logger.d { "Handling GetTipsByLocalizationQuery with localization: ${query.localization}" }

        return when (val result = tipRepository.getTipsByLocalizationAsync(query.localization)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tips for localization: ${query.localization}" }
                GetTipsByLocalizationQueryResult(
                    tips = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tips by localization: ${query.localization} - ${result.error}" }
                GetTipsByLocalizationQueryResult(
                    tips = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}