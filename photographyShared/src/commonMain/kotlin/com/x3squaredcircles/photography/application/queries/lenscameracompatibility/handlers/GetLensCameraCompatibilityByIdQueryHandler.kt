// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByIdQuery, GetLensCameraCompatibilityByIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByIdQuery): GetLensCameraCompatibilityByIdQueryResult {
        return try {
            logger.d { "Handling GetLensCameraCompatibilityByIdQuery with id: ${query.id}" }

            val compatibility = lensCameraCompatibilityRepository.getByIdAsync(query.id)

            logger.i { "Retrieved lens camera compatibility with id: ${query.id}, found: ${compatibility != null}" }

            GetLensCameraCompatibilityByIdQueryResult(
                compatibility = compatibility,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get lens camera compatibility by id: ${query.id}" }
            GetLensCameraCompatibilityByIdQueryResult(
                compatibility = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}