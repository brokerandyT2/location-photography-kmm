// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/LensCameraCompatibilityExistsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.LensCameraCompatibilityExistsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import co.touchlab.kermit.Logger

class LensCameraCompatibilityExistsQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<LensCameraCompatibilityExistsQuery, LensCameraCompatibilityExistsQueryResult> {

    override suspend fun handle(query: LensCameraCompatibilityExistsQuery): LensCameraCompatibilityExistsQueryResult {
        return try {
            logger.d { "Handling LensCameraCompatibilityExistsQuery with lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId}" }

            val exists = lensCameraCompatibilityRepository.existsAsync(query.lensId, query.cameraBodyId)

            logger.i { "Lens camera compatibility exists check for lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId} - exists: $exists" }

            LensCameraCompatibilityExistsQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check lens camera compatibility existence - lensId: ${query.lensId}, cameraBodyId: ${query.cameraBodyId}" }
            LensCameraCompatibilityExistsQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}