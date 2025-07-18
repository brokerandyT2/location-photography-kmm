// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypesByLocalizationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesByLocalizationQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesByLocalizationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipTypesByLocalizationQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypesByLocalizationQuery, GetTipTypesByLocalizationQueryResult> {

    override suspend fun handle(query: GetTipTypesByLocalizationQuery): GetTipTypesByLocalizationQueryResult {
        logger.d { "Handling GetTipTypesByLocalizationQuery with localization: ${query.localization}" }

        return when (val result = tipTypeRepository.getTipTypesByLocalizationAsync(query.localization)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tip types for localization: ${query.localization}" }
                GetTipTypesByLocalizationQueryResult(
                    tipTypes = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tip types by localization: ${query.localization} - ${result.error}" }
                GetTipTypesByLocalizationQueryResult(
                    tipTypes = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}