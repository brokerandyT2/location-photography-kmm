// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetUserCreatedCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetUserCreatedCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetUserCreatedCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetUserCreatedCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetUserCreatedCameraBodiesQuery, GetUserCreatedCameraBodiesQueryResult> {

    override suspend fun handle(query: GetUserCreatedCameraBodiesQuery): GetUserCreatedCameraBodiesQueryResult {
        return try {
            logger.d { "Handling GetUserCreatedCameraBodiesQuery" }

            val cameraBodies = cameraBodyRepository.getUserCreatedAsync()

            logger.i { "Retrieved ${cameraBodies.size} user created camera bodies" }

            GetUserCreatedCameraBodiesQueryResult(
                cameraBodies = cameraBodies,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get user created camera bodies" }
            GetUserCreatedCameraBodiesQueryResult(
                cameraBodies = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}