// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetPagedCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetPagedCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetPagedCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPagedCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedCameraBodiesQuery, GetPagedCameraBodiesQueryResult> {

    override suspend fun handle(query: GetPagedCameraBodiesQuery): GetPagedCameraBodiesQueryResult {
        logger.d { "Handling GetPagedCameraBodiesQuery with pageSize: ${query.pageSize}, offset: ${query.offset}" }

        return when (val result = cameraBodyRepository.getPagedAsync(query.pageSize, query.offset)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} camera bodies for page (pageSize: ${query.pageSize}, offset: ${query.offset})" }
                GetPagedCameraBodiesQueryResult(
                    cameraBodies = result.data,
                    isSuccess = true
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get paged camera bodies (pageSize: ${query.pageSize}, offset: ${query.offset}) - ${result.error}" }
                GetPagedCameraBodiesQueryResult(
                    cameraBodies = emptyList(),
                    isSuccess = false,
                    errorMessage = result.error
                )
            }
        }
    }
}