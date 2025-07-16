// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesByMountTypeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesByMountTypeQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesByMountTypeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetCameraBodiesByMountTypeQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesByMountTypeQuery, GetCameraBodiesByMountTypeQueryResult> {

    override suspend fun handle(query: GetCameraBodiesByMountTypeQuery): GetCameraBodiesByMountTypeQueryResult {
        return try {
            logger.d { "Handling GetCameraBodiesByMountTypeQuery with mountType: ${query.mountType}" }

            val cameraBodies = cameraBodyRepository.getByMountTypeAsync(query.mountType)

            logger.i { "Retrieved ${cameraBodies.size} camera bodies for mountType: ${query.mountType}" }

            GetCameraBodiesByMountTypeQueryResult(
                cameraBodies = cameraBodies,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get camera bodies by mount type: ${query.mountType}" }
            GetCameraBodiesByMountTypeQueryResult(
                cameraBodies = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}