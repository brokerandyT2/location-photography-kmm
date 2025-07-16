// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByCameraIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByCameraIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByCameraIdQuery, GetLensCameraCompatibilityByCameraIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByCameraIdQuery): GetLensCameraCompatibilityByCameraIdQueryResult {
        return try {
            logger.d { "Handling GetLensCameraCompatibilityByCameraIdQuery with cameraBodyId: ${query.cameraBodyId}" }

            val compatibilities = lensCameraCompatibilityRepository.getByCameraIdAsync(query.cameraBodyId)

            logger.i { "Retrieved ${compatibilities.size} lens camera compatibilities for cameraBodyId: ${query.cameraBodyId}" }

            GetLensCameraCompatibilityByCameraIdQueryResult(
                compatibilities = compatibilities,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get lens camera compatibilities by camera id: ${query.cameraBodyId}" }
            GetLensCameraCompatibilityByCameraIdQueryResult(
                compatibilities = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}