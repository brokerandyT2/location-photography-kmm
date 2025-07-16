// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetAllCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetAllCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetAllCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetAllCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetAllCameraBodiesQuery, GetAllCameraBodiesQueryResult> {

    override suspend fun handle(query: GetAllCameraBodiesQuery): GetAllCameraBodiesQueryResult {
        return try {
            logger.d { "Handling GetAllCameraBodiesQuery" }

            val cameraBodies = cameraBodyRepository.getAllAsync()

            logger.i { "Retrieved ${cameraBodies.size} camera bodies" }

            GetAllCameraBodiesQueryResult(
                cameraBodies = cameraBodies,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all camera bodies" }
            GetAllCameraBodiesQueryResult(
                cameraBodies = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}