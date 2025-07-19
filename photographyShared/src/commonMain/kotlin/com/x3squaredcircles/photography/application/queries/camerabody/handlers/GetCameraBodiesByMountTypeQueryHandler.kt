// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesByMountTypeQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesByMountTypeQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesByMountTypeQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetCameraBodiesByMountTypeQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesByMountTypeQuery, GetCameraBodiesByMountTypeQueryResult> {

    override suspend fun handle(query: GetCameraBodiesByMountTypeQuery): Result<GetCameraBodiesByMountTypeQueryResult> {
        logger.d { "Handling GetCameraBodiesByMountTypeQuery with mountType: ${query.mountType}" }

        return when (val result = cameraBodyRepository.getByMountTypeAsync(query.mountType)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} camera bodies for mountType: ${query.mountType}" }
                Result.success(
                    GetCameraBodiesByMountTypeQueryResult(
                        cameraBodies = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get camera bodies by mount type: ${query.mountType} - ${result.error}" }
                Result.success(
                    GetCameraBodiesByMountTypeQueryResult(
                        cameraBodies = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}