// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByCameraIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByCameraIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByCameraIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByCameraIdQuery, GetLensCameraCompatibilityByCameraIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByCameraIdQuery): Result<GetLensCameraCompatibilityByCameraIdQueryResult> {
        logger.d { "Handling GetLensCameraCompatibilityByCameraIdQuery with cameraBodyId: ${query.cameraBodyId}" }

        return when (val result = lensCameraCompatibilityRepository.getByCameraIdAsync(query.cameraBodyId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} lens camera compatibilities for cameraBodyId: ${query.cameraBodyId}" }
                Result.success(
                    GetLensCameraCompatibilityByCameraIdQueryResult(
                        compatibilities = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get lens camera compatibilities by camera id: ${query.cameraBodyId} - ${result.error}" }
                Result.success(
                    GetLensCameraCompatibilityByCameraIdQueryResult(
                        compatibilities = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}