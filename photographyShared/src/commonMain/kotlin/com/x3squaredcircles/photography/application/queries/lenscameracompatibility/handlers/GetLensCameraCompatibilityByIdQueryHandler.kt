// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByIdQuery, GetLensCameraCompatibilityByIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByIdQuery): GetLensCameraCompatibilityByIdQueryResult {
        logger.d { "Handling GetLensCameraCompatibilityByIdQuery with id: ${query.id}" }

        return when (val result = lensCameraCompatibilityRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved lens camera compatibility with id: ${query.id}, found: ${result.data != null}" }
                GetLensCameraCompatibilityByIdQueryResult(
                    compatibility = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get lens camera compatibility by id: ${query.id} - ${result.error}" }
                GetLensCameraCompatibilityByIdQueryResult(
                    compatibility = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}