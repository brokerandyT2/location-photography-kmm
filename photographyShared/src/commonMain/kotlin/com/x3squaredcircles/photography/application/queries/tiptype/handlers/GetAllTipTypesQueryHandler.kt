// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/handlers/GetAllTipTypesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tiptype.handlers

import com.x3squaredcircles.photography.application.queries.tiptype.GetAllTipTypesQuery
import com.x3squaredcircles.photography.application.queries.tiptype.GetAllTipTypesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import co.touchlab.kermit.Logger

class GetAllTipTypesQueryHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val logger: Logger
) : IQueryHandler<GetAllTipTypesQuery, GetAllTipTypesQueryResult> {

    override suspend fun handle(query: GetAllTipTypesQuery): GetAllTipTypesQueryResult {
        return try {
            logger.d { "Handling GetAllTipTypesQuery" }

            val tipTypes = tipTypeRepository.getAllAsync()

            logger.i { "Retrieved ${tipTypes.size} tip types" }

            GetAllTipTypesQueryResult(
                tipTypes = tipTypes,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all tip types" }
            GetAllTipTypesQueryResult(
                tipTypes = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}