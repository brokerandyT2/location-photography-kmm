// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypeByNameQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByNameQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByNameQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetTipTypeByNameQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypeByNameQuery, GetTipTypeByNameQueryResult> {

    override suspend fun handle(query: GetTipTypeByNameQuery): GetTipTypeByNameQueryResult {
        return try {
            logger.d { "Handling GetTipTypeByNameQuery with name: ${query.name}" }

            val tipType = tipTypeRepository.getByNameAsync(query.name)

            logger.i { "Retrieved tip type with name: ${query.name}, found: ${tipType != null}" }

            GetTipTypeByNameQueryResult(
                tipType = tipType,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tip type by name: ${query.name}" }
            GetTipTypeByNameQueryResult(
                tipType = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}