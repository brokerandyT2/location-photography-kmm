// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesTotalCountQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetCameraBodiesTotalCountQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesTotalCountQuery, GetCameraBodiesTotalCountQueryResult> {

    override suspend fun handle(query: GetCameraBodiesTotalCountQuery): GetCameraBodiesTotalCountQueryResult {
        logger.d { "Handling GetCameraBodiesTotalCountQuery" }

        return when (val result = cameraBodyRepository.getTotalCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved total camera bodies count: ${result.data}" }
                GetCameraBodiesTotalCountQueryResult(
                    count = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get camera bodies total count: ${result.error}" }
                GetCameraBodiesTotalCountQueryResult(
                    count = 0L,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}