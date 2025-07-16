// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypesByLocalizationQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesByLocalizationQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypesByLocalizationQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetTipTypesByLocalizationQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypesByLocalizationQuery, GetTipTypesByLocalizationQueryResult> {

    override suspend fun handle(query: GetTipTypesByLocalizationQuery): GetTipTypesByLocalizationQueryResult {
        return try {
            logger.d { "Handling GetTipTypesByLocalizationQuery with localization: ${query.localization}" }

            val tipTypes = tipTypeRepository.getTipTypesByLocalizationAsync(query.localization)

            logger.i { "Retrieved ${tipTypes.size} tip types for localization: ${query.localization}" }

            GetTipTypesByLocalizationQueryResult(
                tipTypes = tipTypes,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tip types by localization: ${query.localization}" }
            GetTipTypesByLocalizationQueryResult(
                tipTypes = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}