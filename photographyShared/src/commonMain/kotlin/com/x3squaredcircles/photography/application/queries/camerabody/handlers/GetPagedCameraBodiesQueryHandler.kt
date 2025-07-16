// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetPagedCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetPagedCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetPagedCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import co.touchlab.kermit.Logger

class GetPagedCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val logger: Logger
) : IQueryHandler<GetPagedCameraBodiesQuery, GetPagedCameraBodiesQueryResult> {

    override suspend fun handle(query: GetPagedCameraBodiesQuery): GetPagedCameraBodiesQueryResult {
        return try {
            logger.d { "Handling GetPagedCameraBodiesQuery with pageSize: ${query.pageSize}, offset: ${query.offset}" }

            val cameraBodies = cameraBodyRepository.getPagedAsync(query.pageSize, query.offset)

            logger.i { "Retrieved ${cameraBodies.size} camera bodies for page (pageSize: ${query.pageSize}, offset: ${query.offset})" }

            GetPagedCameraBodiesQueryResult(
                cameraBodies = cameraBodies,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get paged camera bodies (pageSize: ${query.pageSize}, offset: ${query.offset})" }
            GetPagedCameraBodiesQueryResult(
                cameraBodies = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}