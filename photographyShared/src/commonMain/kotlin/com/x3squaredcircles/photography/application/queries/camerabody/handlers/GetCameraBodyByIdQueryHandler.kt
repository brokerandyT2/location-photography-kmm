// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodyByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodyByIdQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodyByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetCameraBodyByIdQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodyByIdQuery, GetCameraBodyByIdQueryResult> {

    override suspend fun handle(query: GetCameraBodyByIdQuery): GetCameraBodyByIdQueryResult {
        logger.d { "Handling GetCameraBodyByIdQuery with id: ${query.id}" }

        return when (val result = cameraBodyRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved camera body with id: ${query.id}, found: ${result.data != null}" }
                GetCameraBodyByIdQueryResult(
                    cameraBody = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get camera body by id: ${query.id} - ${result.error}" }
                GetCameraBodyByIdQueryResult(
                    cameraBody = null,
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}