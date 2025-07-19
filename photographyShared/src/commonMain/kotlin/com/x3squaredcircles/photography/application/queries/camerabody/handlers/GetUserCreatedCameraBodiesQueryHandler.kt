// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetUserCreatedCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetUserCreatedCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetUserCreatedCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetUserCreatedCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetUserCreatedCameraBodiesQuery, GetUserCreatedCameraBodiesQueryResult> {

    override suspend fun handle(query: GetUserCreatedCameraBodiesQuery): Result<GetUserCreatedCameraBodiesQueryResult> {
        logger.d { "Handling GetUserCreatedCameraBodiesQuery" }

        return when (val result = cameraBodyRepository.getUserCreatedAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} user created camera bodies" }
                Result.success(
                    GetUserCreatedCameraBodiesQueryResult(
                        cameraBodies = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get user created camera bodies: ${result.error}" }
                Result.success(
                    GetUserCreatedCameraBodiesQueryResult(
                        cameraBodies = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}