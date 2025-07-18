// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByLensIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByLensIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByLensIdQuery, GetLensCameraCompatibilityByLensIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByLensIdQuery): GetLensCameraCompatibilityByLensIdQueryResult {
        logger.d { "Handling GetLensCameraCompatibilityByLensIdQuery with lensId: ${query.lensId}" }

        return when (val result = lensCameraCompatibilityRepository.getByLensIdAsync(query.lensId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} lens camera compatibilities for lensId: ${query.lensId}" }
                GetLensCameraCompatibilityByLensIdQueryResult(
                    compatibilities = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get lens camera compatibilities by lens id: ${query.lensId} - ${result.error}" }
                GetLensCameraCompatibilityByLensIdQueryResult(
                    compatibilities = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}