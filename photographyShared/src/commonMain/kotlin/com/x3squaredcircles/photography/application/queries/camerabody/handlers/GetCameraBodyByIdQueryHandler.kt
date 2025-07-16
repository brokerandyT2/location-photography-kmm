// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodyByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodyByIdQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodyByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetCameraBodyByIdQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodyByIdQuery, GetCameraBodyByIdQueryResult> {

    override suspend fun handle(query: GetCameraBodyByIdQuery): GetCameraBodyByIdQueryResult {
        return try {
            logger.d { "Handling GetCameraBodyByIdQuery with id: ${query.id}" }

            val cameraBody = cameraBodyRepository.getByIdAsync(query.id)

            logger.i { "Retrieved camera body with id: ${query.id}, found: ${cameraBody != null}" }

            GetCameraBodyByIdQueryResult(
                cameraBody = cameraBody,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get camera body by id: ${query.id}" }
            GetCameraBodyByIdQueryResult(
                cameraBody = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}