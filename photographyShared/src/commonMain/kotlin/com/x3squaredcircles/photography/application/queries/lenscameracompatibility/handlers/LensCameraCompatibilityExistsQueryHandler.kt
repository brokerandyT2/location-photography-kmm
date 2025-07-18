// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/LensCameraCompatibilityExistsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class LensCameraCompatibilityExistsQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<LensCameraCompatibilityExistsQuery, LensCameraCompatibilityExistsQueryResult> {

    override suspend fun handle(query: LensCameraCompatibilityExistsQuery): LensCameraCompatibilityExistsQueryResult {
        logger.d { "Handling LensCameraCompatibilityExistsQuery with lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId}" }

        return when (val result = lensCameraCompatibilityRepository.existsAsync(query.lensId, query.cameraBodyId)) {
            is Result.Success -> {
                logger.i { "Lens camera compatibility exists check for lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId} - exists: ${result.data}" }
                LensCameraCompatibilityExistsQueryResult(
                    exists = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check lens camera compatibility existence - lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId} - ${result.error}" }
                LensCameraCompatibilityExistsQueryResult(
                    exists = false,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}