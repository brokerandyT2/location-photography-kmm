// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetLensCameraCompatibilityByLensIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetLensCameraCompatibilityByLensIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import co.touchlab.kermit.Logger

class GetLensCameraCompatibilityByLensIdQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetLensCameraCompatibilityByLensIdQuery, GetLensCameraCompatibilityByLensIdQueryResult> {

    override suspend fun handle(query: GetLensCameraCompatibilityByLensIdQuery): GetLensCameraCompatibilityByLensIdQueryResult {
        return try {
            logger.d { "Handling GetLensCameraCompatibilityByLensIdQuery with lensId: ${query.lensId}" }

            val compatibilities = lensCameraCompatibilityRepository.getByLensIdAsync(query.lensId)

            logger.i { "Retrieved ${compatibilities.size} lens camera compatibilities for lensId: ${query.lensId}" }

            GetLensCameraCompatibilityByLensIdQueryResult(
                compatibilities = compatibilities,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get lens camera compatibilities by lens id: ${query.lensId}" }
            GetLensCameraCompatibilityByLensIdQueryResult(
                compatibilities = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}