// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesTotalCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesTotalCountQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesTotalCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetCameraBodiesTotalCountQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesTotalCountQuery, GetCameraBodiesTotalCountQueryResult> {

    override suspend fun handle(query: GetCameraBodiesTotalCountQuery): GetCameraBodiesTotalCountQueryResult {
        return try {
            logger.d { "Handling GetCameraBodiesTotalCountQuery" }

            val count = cameraBodyRepository.getTotalCountAsync()

            logger.i { "Retrieved total camera bodies count: $count" }

            GetCameraBodiesTotalCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get camera bodies total count" }
            GetCameraBodiesTotalCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}