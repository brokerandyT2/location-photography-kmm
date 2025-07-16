// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetTipTypeByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByIdQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetTipTypeByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetTipTypeByIdQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetTipTypeByIdQuery, GetTipTypeByIdQueryResult> {

    override suspend fun handle(query: GetTipTypeByIdQuery): GetTipTypeByIdQueryResult {
        return try {
            logger.d { "Handling GetTipTypeByIdQuery with id: ${query.id}" }

            val tipType = tipTypeRepository.getByIdAsync(query.id)

            logger.i { "Retrieved tip type with id: ${query.id}, found: ${tipType != null}" }

            GetTipTypeByIdQueryResult(
                tipType = tipType,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tip type by id: ${query.id}" }
            GetTipTypeByIdQueryResult(
                tipType = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}