// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetAllCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetAllCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetAllCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetAllCameraBodiesQuery, GetAllCameraBodiesQueryResult> {

    override suspend fun handle(query: GetAllCameraBodiesQuery): GetAllCameraBodiesQueryResult {
        logger.d { "Handling GetAllCameraBodiesQuery" }

        return when (val result = cameraBodyRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} camera bodies" }
                GetAllCameraBodiesQueryResult(
                    cameraBodies = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all camera bodies: ${result.error}" }
                GetAllCameraBodiesQueryResult(
                    cameraBodies = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}